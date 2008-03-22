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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import net.sourceforge.dscsim.radiotransport.AirwaveStatusInterface;
import net.sourceforge.dscsim.radiotransport.Antenna;
import net.sourceforge.dscsim.radiotransport.impl.AbstractAirwave;
import net.sourceforge.dscsim.radiotransport.impl.TransmissionPacket;
import net.sourceforge.dscsim.util.ByteConverter;

/**
 * @author oliver
 *
 */
public class HttpAirwave extends AbstractAirwave implements AirwaveStatusInterface {
	/**
	 * The logger for this class
	 */
	private static Logger _logger = Logger.getLogger(HttpAirwave.class);
	
	/**
	 * Prefix of systems properties to be used by to configure this object
	 */
	private static final String PROPERTY_PREFIX = "parameter.dscsim.http_airwave.";
		
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
	private static final String URL_DEFAULT = "http://localhost:80/DscsimHttpServer/AirwaveHubServlet";
	
	/**
	 * Version of the protocol
	 */
	private static final byte PROTOCOL_VERSION = 1;
	
	/**
	 * Magic number which identifies all UDP packets used by this airwave
	 */
	private int _magicNumber;
	
	/**
	 * Thread which processes the incoming data
	 */
	private Thread _incomingThread;

	/**
	 * List of runnables which should be processed asynchronously
	 */
	private List _asynchronousCommands;
	
	/**
	 * String representation of the URL of the HTTP server
	 */
	private String _serverURL;
	
	
	/**
	 * The HTTP client object which is used for sending data to the 
	 * HTTP server.
	 */
	private HttpClient _httpClient;

	/**
	 * A brief status string with 3 numbers:
	 * <ul>
	 *  <li>number of totally known airwaves</li>
	 *  <li>number of airwaves from where packages are received</li>
	 *  <li>number of established connections</li>
	 * </ul> 
	 */
	private String _statusString;

//	/**
//	 * The status of the network
//	 */
//	protected int _networkStatus;

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
	        while (_incomingThread != null) {
	    		GetMethod httpget = new GetMethod(_serverURL);
	    		httpget.addRequestHeader("magicNumber", ""+_magicNumber);
	    		httpget.addRequestHeader("airwaveUID", ""+_uid);
	        	byte[] inData = null;
	    		try {
	    		  _httpClient.executeMethod(httpget);
	    		  inData = httpget.getResponseBody();
	    		} catch( Exception e ) {
	    			_logger.error("Problem while receiving from HTTP-Server", e);
	    		} finally {
		 		   	httpget.releaseConnection();
	    		}
	        	
	        	if( inData.length == 0 ) {
	        		try {
						Thread.sleep(100);  // nothing on the server: sleep some time
					} catch (InterruptedException e) {
						_logger.warn("incomingThread was interrupted");
					}
	        	} else {
		        	TransmissionPacket antennaSignal = TransmissionPacket.fromByteArray(inData, 0);
	    			pushSignalToLocalAntennas(antennaSignal);
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
//		_networkStatus = STATUS_RED;
		_statusString = "(X/X/X)";
		_shutDown = false;
		_networkStatusListeners = new HashSet();
		_asynchronousCommands = new LinkedList();

        // prepare the HttpClient object
		MultiThreadedHttpConnectionManager connectionManager = 
      		new MultiThreadedHttpConnectionManager();
        _httpClient = new HttpClient(connectionManager);

        // prepare and start the receiver thread
        _incomingThread = new Thread(new IncomingThread());
        _incomingThread.setName("HTTPIncomingDataThread");
        _incomingThread.start();

		
	}

	/**
	 * Gets the network parameters either from the default or from the
	 * system properties if defined
	 */
	private void readParameters() {
		String propValue = System.getProperty(PROPERTY_PREFIX+"server_url");
		if( propValue != null ) {
			_serverURL = propValue;
		} else {
			_serverURL = URL_DEFAULT;
		}
		_logger.info("URL of the HTTP server is : "+_serverURL);
		propValue = System.getProperty(PROPERTY_PREFIX+"magicnumber");
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

    	PostMethod postMethod = new PostMethod(_serverURL);
		postMethod.addRequestHeader("magicNumber", ""+_magicNumber);
		postMethod.addRequestHeader("airwaveUID", ""+_uid);
		postMethod.setRequestEntity( new ByteArrayRequestEntity(data) );
		try {
		  _httpClient.executeMethod(postMethod);
		} catch( Exception e ) {
			_logger.error("Problem while receiving from HTTP-Server", e);
		} finally {
			postMethod.releaseConnection();
		}
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
		return _statusString;
	}

	public int getNetworkStatus() {
		return 0;
	}

	@Override
	public void shutdown() {
		_shutDown = true;
		_incomingThread = null;
		_logger.info("HTTP Airwave shut down");
		
	}
}
