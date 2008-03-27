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
package net.sourceforge.dscsim.radiotransport.udp;

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
public class UDPAirwave extends AbstractAirwave implements AirwaveStatusInterface {
	/**
	 * The logger for this class
	 */
	private static Logger _logger = Logger.getLogger(UDPAirwave.class);
	
	/**
	 * Prefix of systems properties to be used by to configure this object
	 */
	private static final String PROPERTY_PREFIX = "parameter.dscsim.udp_airwave.";
		
	/**
	 * The default value for the prefered port to use for UDP
	 */
	private static final int START_PORT_DEFAULT = 37534;
	
	/**
	 * The default value for the maximum number of ports to use on the machine
	 */
	private static final int PORT_COUNT_DEFAULT = 10;
	
	/**
	 * The maximum allowed length of the UPD packets (buffer size)
	 */
	private static final int MAX_PACKET_SIZE = 16384;

	/**
	 * Default for Magic number which identifies all UDP packets used by this airwave
	 */
	private static final int MAGIC_NUMBER_DEFAULT = 369876138;
	
	/**
	 * Version of the protocol
	 */
	private static final byte PROTOCOL_VERSION = 1;
	
	/**
	 * Content type of the packet: Userdata
	 */
	private static final byte PACKET_TYPE_USER = 0;
	
	/**
	 * Content type of packet: set of known other airwaves
	 */
	private static final byte PACKET_TYPE_AIRWAVE_SET = 1;
	
	/**
	 * Content type of packet: confirm of a connection
	 */
	private static final byte PACKET_TYPE_CONFIRM = 2;

	/**
	 * Length of the fixed part of the header in bytes
	 */
	private static final int FIXED_HEADER_LENGTH = 5;
	
	/**
	 * The offset of the uid in the packet
	 */
	private static final int UID_OFFSET = 5;
	
	/**
	 * The offset of the type byte
	 */
	private static final int TYPE_OFFSET = UID_OFFSET + 4;
	
	/**
	 * The offset of the data
	 */
	private static final int DATA_OFFSET = TYPE_OFFSET + 1;
	
	/**
	 * Duration of pausing of the handshake thread
	 */
	private static final int HANDSHAKE_THREAD_PAUSE = 1000;
	
	/**
	 * Period of sending the whitepages to all other airwaves
	 */
	private static final long WHITE_PAGES_INTERVALL = 30000;
	
	/**
	 * The prefered port to use for UDP
	 */
	private int _startPort;
	
	/**
	 * If already used: try to use ports up to this port number
	 */
	private int _highestPort;

	/**
	 * Magic number which identifies all UDP packets used by this airwave
	 */
	private int _magicNumber;
	
	/**
	 * The constant part of the _fixedHeader
	 */
	private final byte[] _fixedHeader;
	
	
	
	/**
	 * The set of remote SocketAdresses (senders/receivers of packets)
	 */
	private Map _remoteAirwaves;
	
	/**
	 * Flag which indicates if the status of any of the remote airwaves has
	 * changed
	 */
	private boolean _remoteAirwaveStatusChanged;
	
	/**
	 * The UDP socket for sending and receiving packets
	 */
	private DatagramSocket _udpSocket;
	
	/**
	 * Thread which processes the incomming UPD packets
	 */
	private Thread _socketThread;

	/**
	 * Thread which handles the state machine of the handshaking
	 */
	private Thread _handshakeThread;
	
	/**
	 * List of runnables which should be processed asynchronously
	 */
	private List _asynchronousCommands;
	
	/**
	 * Timestamp when the whitepages where sent for the last time
	 */
	private long _lastWhitepagesSent;
	
	/**
	 * A brief status string with 3 numbers:
	 * <ul>
	 *  <li>number of totally known airwaves</li>
	 *  <li>number of airwaves from where packages are received</li>
	 *  <li>number of established connections</li>
	 * </ul> 
	 */
	private String _statusString;

	/**
	 * The status of the network
	 */
	protected int _networkStatus;

	/**
	 * Flag which indicates if the Airwave is already shut down
	 */
	private boolean _shutDown;
	
	
	/**
	 * Thread for handling the incoming data packets
	 */
	private class SocketThread implements Runnable {
		/**
		 * Run method of the thread which receives incoming UDP packets
		 */
	    public void run() {
	        // initialize the socket
	    	byte[] buffer = new byte[MAX_PACKET_SIZE];
	        while (_socketThread != null) {
	        	DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
	        	try {
	            	_udpSocket.receive(packet);
	        	} catch( Exception e ) {
	        		if( _udpSocket.isClosed() ) {
		        		_logger.info("UDP Receiver Socket closed");
	        		} else {
	        			_logger.warn("UDP Receive failed", e);
	        		}
	        		continue;
	        	}
	        	byte[] data = new byte[packet.getLength()];
	        	System.arraycopy(packet.getData(), 0, data, 0, data.length);
	           	if( data.length < TYPE_OFFSET ){
	        		_logger.warn("Received invalid packet (too short)");
	        		return;
	        	}
	        	if( !matchesFirst(data, _fixedHeader) ){
	        		_logger.debug("Received invalid packet (protcol version or magic number wrong)");
	        		break;  // is not a valid packet
	        	}
	           	int uid = ByteConverter.byteArrayToInt(data, UID_OFFSET);
	           	if( uid != _uid ) {  // ignore my own packets
		            InetSocketAddress fromSocketAddress = (InetSocketAddress)packet.getSocketAddress();
		        	UDPAirwaveEntry sourceAirwave = new UDPAirwaveEntry(uid,fromSocketAddress);
		        	sourceAirwave = addToSet(sourceAirwave);
		        	sourceAirwave.packetReceived();
		        	processRawPacket(data, sourceAirwave);
           		
	           	}
	        }

	    }
		
	}
	
	/**
	 * Thread for doing the handshaking of the P2P protocol
	 */
	private class HandshakeThread implements Runnable {
		/**
		 * Run method of the thread which receives incoming UDP packets
		 */
	    public void run() {
	        while (_handshakeThread != null) {
	        	synchronized(_remoteAirwaves) {
	        		for( Iterator i = _remoteAirwaves.values().iterator(); i.hasNext(); ){
	        			UDPAirwaveEntry entry = (UDPAirwaveEntry)i.next();
	        			entry.timerCheck();
	        			if( entry.isDead() ) {
	        				_logger.debug("Removing dead airwave: "+entry.toString());
	        				i.remove();
	        			}
	        		}
	        	}
	        	checkRemoteAirwaveStatus();
	        	if( _lastWhitepagesSent + WHITE_PAGES_INTERVALL < System.currentTimeMillis() ) {
	        		_lastWhitepagesSent = System.currentTimeMillis();
	        		sendWhitePagesToAll();
	        	}
	        	List currentCommands;
	        	synchronized(this){
	        		currentCommands = _asynchronousCommands;
	        		_asynchronousCommands = new LinkedList();
	        	}
	        	for( Iterator i = currentCommands.iterator(); i.hasNext(); ){
	        		Runnable r = (Runnable)i.next();
	        		r.run();
	        	}
	        	try {
					Thread.sleep(HANDSHAKE_THREAD_PAUSE);
				} catch (InterruptedException e) {
				}
	        }

	    }
		
	}

	/**
	 * Standard constructor
	 */
	public UDPAirwave() {
		super();
		readParameters();
		_remoteAirwaves = new HashMap();
		_networkStatus = STATUS_RED;
		_statusString = "(X/X/X)";
		_shutDown = false;
		_networkStatusListeners = new HashSet();
		_remoteAirwaveStatusChanged = true;
		_asynchronousCommands = new LinkedList();
		// determine the fixed part of the packet _fixedHeader
		_fixedHeader = new byte[5];
		// byte[0..3]: magic number
		ByteConverter.intToByteArray(_magicNumber, _fixedHeader, 0);
		// byte[4]: protocol version
		_fixedHeader[4] = PROTOCOL_VERSION;
		// if the property "parameter.dscsim.udp_airwave.peerhost" is set then take
		// the host as IP adress of first remote host
		String[] hosts = getPredefinedHosts();
		if(hosts!=null) {
			for( int i=0; i<hosts.length; i++){
	        	InetSocketAddress peerSocketAddress = new InetSocketAddress(hosts[i], _startPort);
	        	UDPAirwaveEntry entry = new UDPAirwaveEntry(0,peerSocketAddress);
	        	addToSet(entry).known();
			}
		}
//		String peerHost = System.getProperty("parameter.dscsim.udp_airwave.peerhost");
//		if( peerHost != null ) {
//        	InetSocketAddress peerSocketAddress = new InetSocketAddress(peerHost, START_PORT);
//        	UDPAirwaveEntry entry = new UDPAirwaveEntry(0,peerSocketAddress);
//        	addToSet(entry).known();
//		} else {
//			throw new RuntimeException( "parameter.dscsim.udp_airwave.peerhost must be set");
//		}
		// prepare the socket for sending datagrams
		int port = _startPort;
		while( true ) {
			try {
				_udpSocket = new DatagramSocket(port);
				_logger.debug("Port "+port+" will be used for UDPAirwave "+_uid);
				break;
			} catch( SocketException e ) {
				if( port < _highestPort ){
					_logger.debug("Port "+port+" is not available, trying next");
					port++;
				} else {
					_logger.error("No port available, aborting",e);
					throw new RuntimeException("Socket could not be created", e);
				}
			}
		}
		
		// prepare and start the receiver thread
        _socketThread = new Thread(new SocketThread());
        _socketThread.setName("UDPAirwaveSocketThread");
        _socketThread.start();
		
		// prepare and start the handshake thread
        _handshakeThread = new Thread(new HandshakeThread());
        _handshakeThread.setName("UDPAirwaveHandshakeThread");
        _handshakeThread.start();
	}

	/**
	 * Gets the network parameters either from the default or from the
	 * system properties if defined
	 */
	private void readParameters() {
		String propValue = System.getProperty(PROPERTY_PREFIX+"startport");
		if( propValue != null ) {
			_startPort = Integer.parseInt(propValue);
		} else {
			_startPort = START_PORT_DEFAULT;
		}
		_logger.info("Start port is: "+_startPort);

		propValue = System.getProperty(PROPERTY_PREFIX+"portcount");
		if( propValue != null ) {
			_highestPort = _startPort + Integer.parseInt(propValue);
		} else {
			_highestPort = _startPort + PORT_COUNT_DEFAULT;
		}
		_logger.info("Highest port is: "+_highestPort);

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
		UDPAntenna antenna = new UDPAntenna(this);
		synchronized(_antennas) {
			_antennas.add(antenna);
		}
		return antenna;
	}

    /**
     * Private method to process different types of incoming data packets
     * @param data the packet to process
     */
    private void processRawPacket(byte[] data, UDPAirwaveEntry sourceAirwave) {
    	if( data.length <= TYPE_OFFSET ){
    		_logger.warn("Received invalid packet (only fixed header)");
    		return;
    	}
    	byte packetType = data[TYPE_OFFSET];
    	switch(packetType){
    		case PACKET_TYPE_USER:
    			TransmissionPacket antennaSignal = TransmissionPacket.fromByteArray(data, DATA_OFFSET);
    			pushSignalToLocalAntennas(antennaSignal);
    			break;
    		case PACKET_TYPE_AIRWAVE_SET:
    			UDPAirwaveSetPacket airwaveSet = UDPAirwaveSetPacket.fromByteArray(data, DATA_OFFSET);
    			processAirwaveSet(airwaveSet, sourceAirwave);
    			break;
    		case PACKET_TYPE_CONFIRM:
    			processConfirm( sourceAirwave);
    			break;
    		default:
        		_logger.warn("Received unknown packet type: "+packetType);
    			break;
    	}
    	
    }
    
    /**
     * Processes an incoming confirm packet
     * @param sourceAirwave the sender of the packet
     */
    private void processConfirm(UDPAirwaveEntry sourceAirwave) {
		addToSet(sourceAirwave).confirmReceived();
	}

	/**
	 * Process an incoming packet which holds a list of known other airwaves ("white pages")
	 * @param airwaveSet the set of known airwaves
	 * @param sourceAirwave the source airwave wgich sent this packet
	 */
	private void processAirwaveSet(UDPAirwaveSetPacket airwaveSet, UDPAirwaveEntry sourceAirwave) {
		addToSet(sourceAirwave).whitePagesReceived();
    	for( Iterator i = airwaveSet.getUdpAirwaveEntries().iterator(); i.hasNext(); ) {
    		UDPAirwaveEntry entry = (UDPAirwaveEntry)i.next();
    		if( entry.getUid() == _uid ) {
    			// thats my own uid --> confirmation of receipt
    			addToSet(sourceAirwave).confirmReceived();
    		} else {
    			addToSet(entry).known();
    		}
    	}
	}

	/**
     * Sends a given signal to all antennas (local and remote)
     * @param aPacket the signal packet to send
     */
    @Override
    public void sendSignal(TransmissionPacket antennaSignal) {
    	pushSignalToLocalAntennas(antennaSignal);
    	byte[] data = antennaSignal.getByteArray();
    	byte[] packet = new byte[DATA_OFFSET+data.length];
    	System.arraycopy(_fixedHeader, 0, packet, 0, FIXED_HEADER_LENGTH);
    	ByteConverter.intToByteArray(_uid, packet, UID_OFFSET);
    	packet[TYPE_OFFSET]=PACKET_TYPE_USER;
    	System.arraycopy(data, 0, packet, DATA_OFFSET, data.length);
    	DatagramPacket dPacket = new DatagramPacket(packet,packet.length);
    	synchronized(_remoteAirwaves){
        	for(Iterator i = _remoteAirwaves.values().iterator(); i.hasNext(); ){
        		UDPAirwaveEntry airwave = (UDPAirwaveEntry)i.next();
        		if( airwave.isConfirmed() ) {
            		InetSocketAddress remoteSocket = airwave.getSocketAddress();
            		dPacket.setSocketAddress(remoteSocket);
            		try {
                		_udpSocket.send(dPacket);
            		} catch( IOException e ) {
                		if( _udpSocket.isClosed() ) {
                    		_logger.info("UDP Socket closed");
                		} else {
                			_logger.warn("UDP Send failed", e);
                		}
            		}
        		}
        	}
    	}
    }
    
    /**
     * Sends the list of known remote airwaves to all remote airwaves
     */
    void sendWhitePagesToAll() {
		_logger.debug("Sending white pages to all established airwaves");
		byte[] whitePages = createWhitePagesPacket();
    	DatagramPacket dPacket = new DatagramPacket(whitePages,whitePages.length);
    	synchronized(_remoteAirwaves){
        	for(Iterator i = _remoteAirwaves.values().iterator(); i.hasNext(); ){
        		UDPAirwaveEntry airwave = (UDPAirwaveEntry)i.next();
        		if( airwave.isConfirmed() ) {
            		InetSocketAddress remoteSocket = airwave.getSocketAddress();
            		dPacket.setSocketAddress(remoteSocket);
            		try {
                		_udpSocket.send(dPacket);
            		} catch( IOException e ) {
                		if( _udpSocket.isClosed() ) {
                    		_logger.info("UDP Socket closed");
                		} else {
                			_logger.warn("UDP Send failed", e);
                		}
            		}
        		}
        	}
    	}
    }

    /**
     * Sends the list of known remote airwaves to a specific airwave
     * @param airwave the airwave to which the white pages should be send to
     */
    void sendWhitePages(UDPAirwaveEntry airwave) {
    	byte[] whitePages = createWhitePagesPacket();
    	DatagramPacket dPacket = new DatagramPacket(whitePages,whitePages.length);
		InetSocketAddress remoteSocket = airwave.getSocketAddress();
		dPacket.setSocketAddress(remoteSocket);
		try {
    		_udpSocket.send(dPacket);
		} catch( IOException e ) {
    		if( _udpSocket.isClosed() ) {
        		_logger.info("UDP Socket closed");
    		} else {
    			_logger.warn("UDP Send failed", e);
    		}
		}
    }

    /**
     * Creates a packet with a set of known airwave enries ("whitepages" to be send
     * to other airwaves)
     * @return a byte array containing the packet
     */
    private byte[] createWhitePagesPacket() {
    	Set whitePages = new HashSet();
    	synchronized(_remoteAirwaves){
        	for(Iterator i = _remoteAirwaves.values().iterator(); i.hasNext(); ){
        		UDPAirwaveEntry airwave = (UDPAirwaveEntry)i.next();
        		airwave.addToWhitePages(whitePages);
        	}
    	}
		UDPAirwaveSetPacket pack = new UDPAirwaveSetPacket(whitePages);
    	byte[] data = pack.getByteArray();
    	byte[] packet = new byte[DATA_OFFSET+data.length];
    	System.arraycopy(_fixedHeader, 0, packet, 0, FIXED_HEADER_LENGTH);
    	ByteConverter.intToByteArray(_uid, packet, UID_OFFSET);
    	packet[TYPE_OFFSET]=PACKET_TYPE_AIRWAVE_SET;
    	System.arraycopy(data, 0, packet, DATA_OFFSET, data.length);
    	return packet;
	}

    /**
     * Sends a confirm to  a specific airwave
     * @param airwave the airwave to which the confirm should be send to
     */
    void sendConfirm(UDPAirwaveEntry airwave) {
    	byte[] packet = new byte[DATA_OFFSET];
    	System.arraycopy(_fixedHeader, 0, packet, 0, FIXED_HEADER_LENGTH);
    	ByteConverter.intToByteArray(_uid, packet, UID_OFFSET);
    	packet[TYPE_OFFSET]=PACKET_TYPE_CONFIRM;
    	DatagramPacket dPacket = new DatagramPacket(packet,packet.length);
		InetSocketAddress remoteSocket = airwave.getSocketAddress();
		dPacket.setSocketAddress(remoteSocket);
		try {
    		_udpSocket.send(dPacket);
		} catch( IOException e ) {
    		if( _udpSocket.isClosed() ) {
        		_logger.info("UDP Socket closed");
    		} else {
    			_logger.warn("UDP Send failed", e);
    		}
		}
    }

    /**
     * Determines the start of the byte array given as first arguments with the
     * byte array given as second argument
     * @param array byte array which should start with the given pattern
     * @param pattern the pattern to check for
     * @return <code>true</code> if the array starts with the given pattern, <code>false</code>
     * otherwise
     */
    private static boolean matchesFirst(byte[] array, byte[] pattern) {
    	if( array.length < pattern.length ){
    		return false;
    	}
    	for(int i=0; i<pattern.length; i++){
    		if( array[i] != pattern[i] ){
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * Checks if the given UDPAirwaveEntry is already contained in the set
     * of known remote airwaves. If not it will be added. The method returns
     * the instance of the object which was possibly already entered before
     * (Note: UDPAirwaveEntries might be equal if if they are not the same instance
     * 
     * @param entry the entry which will be entered to the set
     * @return the original entry
     */
    private UDPAirwaveEntry addToSet( UDPAirwaveEntry entry ) {
    	if( !_remoteAirwaves.containsKey(entry) ){
    		synchronized(_remoteAirwaves){
        		_remoteAirwaves.put(entry,entry);
        		entry.setParent(this);
        		_remoteAirwaveStatusChanged = true;
    		}
    		_logger.debug("New airwave added to set of known remote airwaves: "+entry);
    	}
    	UDPAirwaveEntry inSet = (UDPAirwaveEntry)_remoteAirwaves.get(entry);
    	inSet.updateUid(entry.getUid());
    	return inSet;
    	
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
     * Check the status of all known airwave entries
     * @return <code>true</code> if the status of any of the remote
     * airwaves has changed,<code>false</code> otherwise
     */
    private boolean checkRemoteAirwaveStatus() {
    	synchronized(_remoteAirwaves) {
    		if(_remoteAirwaveStatusChanged) {
    			_remoteAirwaveStatusChanged = false;
    			int countTotal = 0;
    			int countSending = 0;
    			int countEstablished = 0;
    			int countUnreachable = 0;
        		for( Iterator i = _remoteAirwaves.values().iterator(); i.hasNext(); ){
        			UDPAirwaveEntry entry = (UDPAirwaveEntry)i.next();
        			_logger.debug("Known airwaves: "+entry.toString());
        			countTotal++;
        			if( entry.isSendingPackets() ) { countSending++; }
        			if( entry.isConfirmed() ) { countEstablished++; }
        			if( entry.isUnreachable() ) { countUnreachable++; }
        		}
        		_networkStatus = STATUS_RED;
        		if( countTotal == 0 ) {
        			_logger.info("No remote airwaves are known");
        		} else if( countTotal == countUnreachable ) {
        			_logger.warn(countTotal+" airwaves are known, but all are unreachable; I can't communicate with anybody outside");
        		} else if( countEstablished == 0) {
        			_logger.debug("No connectivity established (receiving packets from "+countSending+" remote airwaves)");
        			_networkStatus = STATUS_YELLOW;
        		} else {
        			_logger.info("Established connectivity to "+countEstablished+" airwaves");
        			_networkStatus = STATUS_BLUE;
        			if( countTotal == countEstablished ) {
            			_logger.info("Established connectivity to all known airwaves ("+countEstablished+")");
            			_networkStatus = STATUS_GREEN;
        			}
        			
        		}
        		_statusString = "("+countTotal+"/"+countSending+"/"+countEstablished+")";
        		notifyStatusListeners();
        		return true;
    		}
    	}
    	return false;
    }
 
    /**
     * gets the list of predefined remote hosts from the property
 	 * @return the list of names given via property "parameter.dscsim.udp_airwave.peerhost"
     */
    private String[] getPredefinedHosts() {
		String peerHostString = System.getProperty(PROPERTY_PREFIX+"peerhost");
		String[] hostList = null;
		if( peerHostString != null ) {
			StringTokenizer st = new StringTokenizer(peerHostString, ",;");
			hostList = new String[st.countTokens()];
			int i=0;
			while(st.hasMoreTokens()){
				hostList[i] = st.nextToken();
				i++;
			}
		}
		return hostList;
    }
    
    /**
     * Sets the flag that the status of any of the remote airwaves has changed
     */
    void notifyRemoteAirwaveChanged() {
    	synchronized(_remoteAirwaves) {
    		_remoteAirwaveStatusChanged = true;
    	}
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
		return _networkStatus;
	}

	@Override
	public void shutdown() {
		_shutDown = true;
		_socketThread = null;
		_handshakeThread = null;
		if( _udpSocket != null ) {
			_udpSocket.close();
		}
		_logger.info("UDP Airwave shut down");
		
	}
	
}
