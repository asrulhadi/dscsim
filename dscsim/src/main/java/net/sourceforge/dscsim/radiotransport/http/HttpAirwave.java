/*
 * The contents of this file are subject to the Mozilla Public License Version 1.0
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'dscsim'.
 *
 * The Initial Developer of the Original Code is Oliver Hecker. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.radiotransport.http;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.radiotransport.Airwave;
import net.sourceforge.dscsim.radiotransport.AirwaveStatusInterface;
import net.sourceforge.dscsim.radiotransport.Antenna;
import net.sourceforge.dscsim.radiotransport.impl.AbstractAirwave;
import net.sourceforge.dscsim.radiotransport.impl.TransmissionPacket;
import net.sourceforge.dscsim.util.ByteArrayUtil;
import net.sourceforge.dscsim.util.ByteConverter;

/**
 * @author oliver
 *
 */
public class HttpAirwave extends AbstractAirwave implements AirwaveStatusInterface {

	private class StatusTracker {
		
		/**
		 * Limit how many requests might fail before the status goes to red
		 */
		private static final int FAIL_LIMIT = 5;
		
		/**
		 * Textual representation of the status
		 */
		private String _statusString;

		/**
		 * The status of the network
		 */
		private int _networkStatus;
		
		/**
		 * Counter which count how often the HTTP request failed subsequently
		 */
		private int _failCounter;
		
		/** 
		 * Constructor
		 */
		private StatusTracker() {
			_statusString = "---";
			_networkStatus = STATUS_YELLOW;
			_failCounter = FAIL_LIMIT;
		}

		/**
		 * Sets the new values of _statusString and _networkStatus
		 */
		private void setMembers( String statusString, int networkStatus ) {
			if( !statusString.equals(_statusString) ||
				networkStatus != _networkStatus	) {
				_statusString = statusString;
				_networkStatus = networkStatus;
				notifyStatusListeners();
			}
		}
		
		/**
		 * @return the _statusString
		 */
		protected String getStatusString() {
			return _statusString;
		}

		/**
		 * @return the _networkStatus
		 */
		protected int getNetworkStatus() {
			return _networkStatus;
		}
		
		/**
		 * Method to be called when HTTP request was successfull and fast
		 */
		protected synchronized void requestGood() {
			setMembers( "Good", STATUS_GREEN);
			_failCounter = 0;
		}
		
		/**
		 * Method to be called when HTTP request was successfull but slow
		 */
		protected synchronized void requestSlow() {
			setMembers( "Slow", STATUS_YELLOW );
			_failCounter = 0;
		}

		/**
		 * Method to be called when HTTP request failed
		 */
		protected synchronized void requestFailed() {
			_failCounter++;
			if(_failCounter >= FAIL_LIMIT ) {
				_failCounter = FAIL_LIMIT;
				setMembers( "Bad", STATUS_RED );
			} else {
				setMembers( "Drops", STATUS_YELLOW );
			}
		}

	}
	
	/**
	 * The logger for this class
	 */
	private static Logger _logger = Logger.getLogger(HttpAirwave.class);
	
	/**
	 * The logger for performance statistics
	 */
	private static Logger _statisticsLogger = Logger.getLogger("statistics." + HttpAirwave.class.getName());

	/**
	 * Prefix of systems properties to be used by to configure this object
	 */
	private static final String PROPERTY_PREFIX = Airwave.PROPERTY_NAME+".http.";
		
	/**
	 * The maximum allowed length of the UPD packets (buffer size)
	 */
	private static final int MAX_PACKET_SIZE = 16384;

	/**
	 * Default for Magic number which identifies all UDP packets used by this airwave
	 */
	private static final int MAGIC_NUMBER_DEFAULT = 369876138;

	/**
	 * Default value of the URL which is used as airwave hub
	 */
	private static final String URL_DEFAULT = "http://localhost:80/DscsimHttpServer";

	/**
	 * The name of the servlet relative to the server_url
	 */
	private static final String SERVLET_NAME = "AirwaveHubServlet";
	/**
	 * Default value of the HTTP proxy port to use
	 */
	private static final int PROXY_PORT_DEFAULT = 8080;
	
	/**
	 * Version of the protocol
	 */
	private static final byte PROTOCOL_VERSION = 1;

   /**
    * The length of the prefix of the encoded downlink data packets (server->client)
    */
   private static final int DOWNLINK_PREFIX_LENGTH = 6;
   
   /**
    * The length of the prefix of the encoded uplink data packets (client->server) 
    */
   private static final int UPLINK_PREFIX_LENGTH = 2;

	
	/**
	 * Duration when a request is categorized as "slow" (Milliseconds)
	 */
	private static final int SLOW_REQUEST_LIMIT = 200;
	
	/**
	 * Retry delay for requests in case of bad network status in milliseconds (to avoid high speed retries)
	 */
	private static final int RED_STATUS_RETRY_DELAY = 10000;
	
	/**
	 * Magic number which identifies all UDP packets used by this airwave
	 */
	private int _magicNumber;
	
	/**
	 * Thread which processes the incoming data
	 */
	private Thread _incomingThread;
	
	/**
	 * Thread which processes the outgoing data
	 */
	private Thread _outgoingThread;
	
	/**
	 * Queue of data to be send
	 */
	private Queue<byte[]> _outQueue;

	/**
	 * List of runnables which should be processed asynchronously
	 */
	private List _asynchronousCommands;
	
	/**
	 * String representation of the URL of the HTTP server
	 */
	private String _servletURL;
	
	/**
	 * The hostname of the HTTP proxy to use (if any)
	 */
	private String _proxyHost;
	
	/**
	 * The port of the HTTP proxy
	 */
	private int _proxyPort;
	
	/**
	 * The HTTP client object which is used for sending data to the 
	 * HTTP server.
	 */
	private HttpClient _httpClient;


	/**
	 * Sequence number for transmit requests (to avoid caching in any proxies)
	 */
//	int _transmitSequence;
	
//	/**
//	 * The status of the network
//	 */
//	protected int _networkStatus;

	/**
	 * The object which tracks the status of this Airwave
	 */
	private StatusTracker _statusTracker;
	
	/**
	 * Flag which indicates if the Airwave is already shut down
	 */
	private boolean _shutDown;
	
	
	/**
	 * Thread for handling the incoming data packets
	 */
	private class IncomingThread implements Runnable {
		/**
		 * Run method of the thread which receives incoming UDP packets
		 */
	    public void run() {
	    	byte contentSequence = -1;
	    	int sequenceMismatchCount = 0;
	        while (_incomingThread != null) {
	        	if( _statusTracker.getNetworkStatus() == STATUS_RED ) {
	        		try {
						Thread.sleep(RED_STATUS_RETRY_DELAY);
					} catch (InterruptedException e) {
						continue;
					}
	        	}

	        	GetMethod httpget = new GetMethod(_servletURL);
	    		httpget.addRequestHeader("magicNumber", ""+_magicNumber);
	    		httpget.addRequestHeader("airwaveUID", ""+_uid);
	    		httpget.addRequestHeader("seq", ""+System.currentTimeMillis());
	        	byte[] inData = null;
	    		try {
	    		  long startTime = System.currentTimeMillis();
	    		  _httpClient.executeMethod(httpget);
	    		  inData = httpget.getResponseBody();
	    
	    		  if( inData[0] != PROTOCOL_VERSION ) {
	    			  _logger.warn("Wrong protocol version found in downlink data "+inData[0]);
  	    			  _statusTracker.requestFailed();
	    			  continue;
	    		  }

	  			  contentSequence++;
	  			  if( inData[1] != contentSequence ) {
	  				sequenceMismatchCount++;  
					_logger.info("Mismatch in downlink sequence byte - Expected: "+contentSequence+" but was: "+inData[1] );
	  			  }
	  			  contentSequence = inData[1];
	    		  
	    		  long endTime = System.currentTimeMillis();
	    		  int serverBlockDelay = ByteConverter.byteArrayToInt(inData, 2);
	    		  long duration = endTime-startTime-serverBlockDelay;
	    		  _logger.debug( "Request roundtrip for Receiver: "+duration+" ms (plus "+serverBlockDelay+" ms server blocking delay)" );
	    		  if( duration > SLOW_REQUEST_LIMIT ) {
	    			  _statusTracker.requestSlow();
	    		  } else {
	    			  _statusTracker.requestGood();
	    		  }
	    		  List<byte[]> bAList = ByteArrayUtil.decode(inData, DOWNLINK_PREFIX_LENGTH);
	    		  for( byte[] packet : bAList ) {
	    			  TransmissionPacket antennaSignal = TransmissionPacket.fromByteArray(packet, 0);
	    			  pushSignalToLocalAntennas(antennaSignal);
	    		  }
	    		  if( _statisticsLogger.isDebugEnabled() ) {
	    			  StringBuilder message = new StringBuilder();
	    			  message.append(System.currentTimeMillis()).append(',');   // system time in milliseconds
	    			  message.append(_uid).append(',');							// uid of the airwave
	    			  message.append("downlink").append(',');					// this is "downlink" data
	    			  message.append(contentSequence).append(',');				// sequence byte as delivered by server
	    			  message.append(sequenceMismatchCount).append(',');		// count how often sequence mismatch occured
	    			  message.append(duration).append(',');						// net duration of request (excluding server blocking)
	    			  message.append(serverBlockDelay).append(',');				// server blocking time due to missing avaliable data
	    			  message.append(inData.length).append(',');				// size of the transmitted byte array
	    			  message.append(bAList.size());							// number of packets which were encoded in the byte array
		    		  _statisticsLogger.debug(message);
	    		  }
	    		} catch( Exception e ) {
	    			_logger.error("Problem while receiving from HTTP-Server", e);
	    			_statusTracker.requestFailed();
	    			continue;
	    		} finally {
		 		   	httpget.releaseConnection();
	    		}
	        }
	    }
	}
	
	/**
	 * Thread for handling the incoming data packets
	 */
	private class OutgoingThread implements Runnable {
		/**
		 * Run method of the thread send packets via HTTP
		 */
	    public void run() {
	    	byte contentSequence = 0;
    		List<byte[]> bAList = new ArrayList<byte[]>(10); 
    	  outer:	
	        while (_outgoingThread != null) {
	        	if( _statusTracker.getNetworkStatus() == STATUS_RED ) {
	        		try {
						Thread.sleep(RED_STATUS_RETRY_DELAY);
					} catch (InterruptedException e) {
						continue;
					}
	        	}
	        	synchronized(_outQueue) {
	        		while(_outQueue.peek()==null) {
	        			try {
							_outQueue.wait();
						} catch (InterruptedException e) {
							continue outer;
						}
	        		}
	        		bAList.clear();
	        		while(true) {
	        			byte[] packet = _outQueue.poll();
	        			if( packet == null ) {
	        				break;
	        			}
	        			bAList.add(packet);
	        		}
	        	}
	        	byte[] data = ByteArrayUtil.encode(bAList, UPLINK_PREFIX_LENGTH);
	        	data[0] = PROTOCOL_VERSION;
	        	data[1] = contentSequence++;

	        	PostMethod postMethod = new PostMethod(_servletURL);
	    		postMethod.addRequestHeader("magicNumber", ""+_magicNumber);
	    		postMethod.addRequestHeader("airwaveUID", ""+_uid);
//	    		_transmitSequence++;
	    		postMethod.addRequestHeader("seq", ""+System.currentTimeMillis());
	    		postMethod.setRequestEntity( new ByteArrayRequestEntity(data) );
	    		try {
	    		  long startTime = System.currentTimeMillis();
	    		  _httpClient.executeMethod(postMethod);
	    		  long endTime = System.currentTimeMillis();
	    		  long duration = endTime-startTime;
	    		  _logger.debug( "Request roundtrip for Sender: "+duration+" ms" );
	    		  if( duration > SLOW_REQUEST_LIMIT ) {
	    			  _statusTracker.requestSlow();
	    		  } else {
	    			  _statusTracker.requestGood();
	    		  }
	    		  
	    		  if( _statisticsLogger.isDebugEnabled() ) {
	    			  StringBuilder message = new StringBuilder();
	    			  message.append(System.currentTimeMillis()).append(',');   // system time in milliseconds
	    			  message.append(_uid).append(',');							// uid of the airwave
	    			  message.append("uplink").append(',');						// this is "uplink" data
	    			  message.append(contentSequence).append(',');				// sequence byte as delivered by server
	    			  message.append(',');										// -empty- 
	    			  message.append(duration).append(',');						// duration of request
	    			  message.append(data.length).append(',');					// size of the transmitted byte array
	    			  message.append(',');										// -empty-
	    			  message.append(bAList.size());							// number of packets which were encoded in the byte array
		    		  _statisticsLogger.debug(message);
	    		  }
	    		} catch( Exception e ) {
	    			_logger.error("Problem while sending to HTTP-Server", e);
	    			_statusTracker.requestFailed();
	    		} finally {
	    			postMethod.releaseConnection();
	    		}
	        }
	    }
	}

	/**
	 * Standard constructor
	 */
	public HttpAirwave() {
		super();
		readParameters();
		_statusTracker = new StatusTracker();
		_shutDown = false;
//		_transmitSequence = 0;
		_networkStatusListeners = new HashSet();
		_asynchronousCommands = new LinkedList();
		_outQueue = new LinkedList<byte[]>();

        // prepare the HttpClient object
		MultiThreadedHttpConnectionManager connectionManager = 
      		new MultiThreadedHttpConnectionManager();
        _httpClient = new HttpClient(connectionManager);
        if( _proxyHost != null ) {
        	_httpClient.getHostConfiguration().setProxy(_proxyHost, _proxyPort);
        }

        // prepare and start the receiver thread
        _incomingThread = new Thread(new IncomingThread());
        _incomingThread.setName("HTTPIncomingDataThread");
        _incomingThread.start();

        // prepare and start the sender thread
        _outgoingThread = new Thread(new OutgoingThread());
        _outgoingThread.setName("HTTPOutgoingDataThread");
        _outgoingThread.start();
		
	}

	/**
	 * Gets the network parameters either from the default or from the
	 * system properties if defined
	 */
	private void readParameters() {
		String propValue = System.getProperty(PROPERTY_PREFIX+"server_url");
		if( propValue != null ) {
			_servletURL = propValue;
		} else {
			_servletURL = URL_DEFAULT;
		}
		_servletURL = _servletURL + "/" + SERVLET_NAME;
		_logger.info("URL of the HTTP servlet is : "+_servletURL);

		propValue = System.getProperty(PROPERTY_PREFIX+"proxy_host");
		if( propValue != null ) {
			_proxyHost = propValue;
		} else {
			_proxyHost = null;
		}
		propValue = System.getProperty(PROPERTY_PREFIX+"proxy_port");
		if( propValue != null ) {
			_proxyPort = Integer.parseInt(propValue);
		} else {
			_proxyPort = PROXY_PORT_DEFAULT;
		}
		if( _proxyHost != null ) {
			_logger.info("HTTP-Proxy set to "+_proxyHost+":"+_proxyPort);
		}

		propValue = System.getProperty(Airwave.PROPERTY_NAME+".magicnumber");
		if( propValue != null ) {
			_magicNumber = Integer.parseInt(propValue);
		} else {
			_magicNumber = MAGIC_NUMBER_DEFAULT;
		}
		_logger.info("Fingerprint of magic number is : "+new String("F"+_magicNumber).hashCode());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Airwave#createAntenna()
	 */
	public Antenna createAntenna() {
		HttpAntenna antenna = new HttpAntenna(this);
		synchronized(_antennas) {
			_antennas.add(antenna);
		}
		return antenna;
	}


	/**
     * Sends a given signal to all antennas (local and remote)
     * @param antennaSignal the signal packet to send
     */
    @Override
    public synchronized void sendSignal(TransmissionPacket antennaSignal) {
    	pushSignalToLocalAntennas(antennaSignal);
    	byte[] data = antennaSignal.getByteArray();
    	synchronized(_outQueue) {
    		_outQueue.add(data);
    		_outQueue.notifyAll();
    	}

//
//    	PostMethod postMethod = new PostMethod(_servletURL);
//		postMethod.addRequestHeader("magicNumber", ""+_magicNumber);
//		postMethod.addRequestHeader("airwaveUID", ""+_uid);
//		_transmitSequence++;
//		postMethod.addRequestHeader("seq", ""+_transmitSequence);
//		postMethod.setRequestEntity( new ByteArrayRequestEntity(data) );
//		try {
//		  long startTime = System.currentTimeMillis();
//		  _httpClient.executeMethod(postMethod);
//		  long endTime = System.currentTimeMillis();
//		  long duration = endTime-startTime;
//		  _logger.debug( "Request duration for Sender: "+duration+" ms" );
//		  if( duration > SLOW_REQUEST_LIMIT ) {
//			  _statusTracker.requestSlow();
//		  } else {
//			  _statusTracker.requestGood();
//		  }
//		} catch( Exception e ) {
//			_logger.error("Problem while sending to HTTP-Server", e);
//		} finally {
//			postMethod.releaseConnection();
//		}
    }
    
    /**
     * Adds a runnable to the list of commands to be executed
     * asynchronously
     * @param command the Runnable to be executed
     */
    public synchronized void registerCommand(Runnable command) {
    	_asynchronousCommands.add(command);
    }

	/**
	 * Returns a brief status stringwith 3 numbers:
	 * <ul>
	 *  <li>number of totally known airwaves</li>
	 *  <li>number of airwaves from where packages are received</li>
	 *  <li>number of established connections</li>
	 * </ul> 
	 * @see net.sourceforge.dscsim.radiotransport.AirwaveStatusInterface#getStatusString()
	 */
	public String getStatusString() {
		return _statusTracker.getStatusString();
	}

	public int getNetworkStatus() {
		return _statusTracker.getNetworkStatus();
	}

	@Override
	public void shutdown() {
		_shutDown = true;
		Thread t1;
		t1 = _incomingThread;
		_incomingThread = null;
		t1.interrupt();
		Thread t2;
		t2 = _outgoingThread;
		_outgoingThread = null;
		t2.interrupt();
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			_logger.warn("Interrupted while shutting down HTTP Airwave");
		}
		_logger.info("HTTP Airwave shut down");
		
	}
}
