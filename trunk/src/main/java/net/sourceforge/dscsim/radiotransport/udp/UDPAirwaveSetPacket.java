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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.dscsim.radiotransport.Decibel;
import net.sourceforge.dscsim.radiotransport.Frequency;
import net.sourceforge.dscsim.radiotransport.GeoPosition;
import net.sourceforge.dscsim.radiotransport.InvalidPacketException;
import net.sourceforge.dscsim.util.ByteConverter;

/**
 * Represents a set of UDP airwave data for exchange
 * via the network ("white pages")
 *
 */
public class UDPAirwaveSetPacket  {
	
	/*
	 * Offsets of data in byte array representation
	 */
	private static final int UID_LENGTH = 4;
	private static final int IP_OFFSET = UID_LENGTH;
	private static final int IP_LENGTH = 4;
	private static final int PORT_OFFSET = IP_OFFSET + IP_LENGTH;
	private static final int PORT_LENGTH = 4;
	private static final int ENTRY_SIZE = PORT_OFFSET + PORT_LENGTH;

	/**
	 * The set of UDPAirwaveEntries wrapped by the object
	 */
	private Set _udpAirwaveEntries;

	/**
	 * Standard constructor only for private usage
	 */
	public UDPAirwaveSetPacket() {
		_udpAirwaveEntries = new HashSet();
	}
	
	/**
	 * Constructor which takes the set of UDPAirwaveEntries as argument
	 * @param udpAirwaveEnries
	 */
	public UDPAirwaveSetPacket(Set udpAirwaveEnries) {
		super();
		_udpAirwaveEntries = udpAirwaveEnries;
	}

	/**
	 * @return the udpAirwaveEnries
	 */
	public Set getUdpAirwaveEntries() {
		return _udpAirwaveEntries;
	}

	/**
	 * Converts the object to a byte array representation.
	 */
	public byte[] getByteArray(){
		int entryCount = _udpAirwaveEntries.size();
		
		byte[] result = new byte[ENTRY_SIZE*entryCount];
		int offset = 0;
		for(Iterator i = _udpAirwaveEntries.iterator(); i.hasNext(); ){
			UDPAirwaveEntry entry = (UDPAirwaveEntry)i.next();
			int uid = entry.getUid();
			InetSocketAddress socketAddress = entry.getSocketAddress();
			byte [] addressBytes = socketAddress.getAddress().getAddress();
			int port = socketAddress.getPort();
			ByteConverter.intToByteArray(uid, result, offset);
	    	System.arraycopy(addressBytes, 0, result, offset+IP_OFFSET, addressBytes.length);
			ByteConverter.intToByteArray(port, result, offset+PORT_OFFSET);
			offset += ENTRY_SIZE;
		}
		return result;
	}
	
	/**
	 * Extracts an TransmissionPacket object from the given byte array
	 * @param src the byte array which contains the objects data
	 * @param offset the offset to start the extraction within byte array.
	 */
	public static UDPAirwaveSetPacket fromByteArray(byte[] src, int offset ){
		UDPAirwaveSetPacket result = new UDPAirwaveSetPacket();
		while( offset < src.length ){
			try {
				int uid = ByteConverter.byteArrayToInt(src, offset);
				byte[] addressBytes = new byte[IP_LENGTH];
				System.arraycopy(src, offset+IP_OFFSET, addressBytes, 0, IP_LENGTH);
				InetAddress address = InetAddress.getByAddress(addressBytes);
				int port = ByteConverter.byteArrayToInt(src, offset+PORT_OFFSET);
				InetSocketAddress socketAddress = new InetSocketAddress(address,port);
				UDPAirwaveEntry entry = new UDPAirwaveEntry(uid,socketAddress);
				result._udpAirwaveEntries.add(entry);
			} catch(IndexOutOfBoundsException e) {
				throw new InvalidPacketException("Packet has wrong length",e);
			} catch (UnknownHostException e) {
				// this can onyl happen due to programming error
				throw new IllegalArgumentException("IP-Address of wrong length");
			}
			offset += ENTRY_SIZE;
		}
    	return result;
	}

}
