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
package net.sourceforge.dscsim.radio.core.impl;

import net.sourceforge.dscsim.util.ByteConverter;

import org.apache.log4j.Logger;

/**
 * A Sequence of SoundDataPackets
 */
public class SoundDataPacketSequence {

	/**
	 * The logger
	 */
	private static Logger logger = Logger.getLogger(SoundDataPacketSequence.class);

	/**
	 * The numeric code for the type
	 */
	public static final int PACKET_TYPE = 11;

	/**
	 * The maximum number of packets in the sequence
	 */
	private static final int MAX_PACKET_NUMBER = 10;
	
	/**
	 * The array of SoundDataPackets
	 */
	private SoundDataPacket[] packets;
	
	/**
	 * The index of the next packet
	 */
	private int index;
	
	/**
	 * Standard constructor
	 */
	public SoundDataPacketSequence() {
		packets = new SoundDataPacket[MAX_PACKET_NUMBER];
		index = 0;
	}
	
	/**
	 * Checks if the last element is reached
	 */
	public boolean isLast() {
		return( index >= MAX_PACKET_NUMBER );
	}
	
	/**
	 * Adds a SoundDataPacket to the array of packets
	 */
	public void addPacket(SoundDataPacket sdp) {
		packets[index] = sdp;
		index++;
	}

	/**
	 * Gets the next SoundDataPacket
	 */
	public SoundDataPacket getNextPacket() {
		SoundDataPacket retValue = packets[index];
		index++;
		return retValue;
	}

	/**
	 * Serializes the sequence to a byte array
	 * @return
	 */
	public byte[] getBytes() {
		byte[] result = null;
		int packetLength = 0;
		for(int i=0; i<MAX_PACKET_NUMBER; i++) {
			byte[] packetBytes = packets[i].getBytes();
			if( result == null ){
				packetLength = packetBytes.length;
				result= new byte[packetLength*MAX_PACKET_NUMBER+8];
				ByteConverter.intToByteArray(PACKET_TYPE, result, 0 );
				ByteConverter.intToByteArray(MAX_PACKET_NUMBER, result, 4 );
			} else {
				if( packetLength != packetBytes.length ) {
					throw new IllegalArgumentException( "Size of SoundDataPacket must not change within one sequence");
				}
			}
			System.arraycopy(packetBytes, 0, result, 8+(i*packetLength), packetLength);
		}
		return result;
//		
//		
//		new byte[byteCount+8];
//		ByteConverter.intToByteArray(PACKET_TYPE, result, 0 );
//		System.arraycopy(this.dataBuff,0,result,4,byteCount/*this.dataBuff.length*/);
//		ByteConverter.intToByteArray(sequenceNumber, result, byteCount+4 );
//		return result;
	}
	
	/**
	 * Deserializes the sequence from an byte array
	 * @param inBytes
	 * @param length
	 * @return
	 */
	public static SoundDataPacketSequence getFromBytes(byte[] inBytes, int length) {
		int type = ByteConverter.byteArrayToInt(inBytes, 0);
		if( type != PACKET_TYPE ){
			//not the appropriate type
			logger.debug("Received packet is of wrong type. Expected "+PACKET_TYPE+" but was "+type);
			return null;
		}
		int packetNumber = ByteConverter.byteArrayToInt(inBytes, 4);
		if( packetNumber != MAX_PACKET_NUMBER ) {
			//not the appropriate type
			logger.debug("Received sequence is of wrong lenght. Expected "+MAX_PACKET_NUMBER+" but was "+packetNumber);
			return null;
		}
		int packetSize = ( length - 8 )/packetNumber;
		SoundDataPacketSequence sequence = new SoundDataPacketSequence();
		for(int i=0; i<packetNumber; i++ ){
			SoundDataPacket onePacket = SoundDataPacket.getFromBytes(inBytes, 8 + (i*packetSize), packetSize);
			sequence.packets[i] = onePacket;
		}
		return sequence;
	}
	
}
