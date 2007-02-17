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

import net.sourceforge.dscsim.radiotransport.Decibel;
import net.sourceforge.dscsim.radiotransport.Frequency;
import net.sourceforge.dscsim.radiotransport.GeoPosition;
import net.sourceforge.dscsim.radiotransport.InvalidPacketException;
import net.sourceforge.dscsim.util.ByteConverter;

/**
 * Wraps the data exchanged via the UDPAirwave between 
 * the UDPTransmitter and the UDPReceiver
 *
 */
public class UDPTransmissionPacket implements Cloneable {
	
	/*
	 * Offsets of data in byte array representation
	 */
	private static final int ELEVATION_OFFSET = 0;
	private static final int GEO_LATITUDE_OFFSET = ELEVATION_OFFSET + ByteConverter.INT_LEN_IN_BYTES;
	private static final int GEO_LONGITUDE_OFFSET = GEO_LATITUDE_OFFSET + ByteConverter.INT_LEN_IN_BYTES;
	private static final int FREQUENCY_OFFSET = GEO_LONGITUDE_OFFSET + ByteConverter.INT_LEN_IN_BYTES;
	private static final int POWER_OFFSET = FREQUENCY_OFFSET + ByteConverter.INT_LEN_IN_BYTES;
	private static final int FLAG_OFFSET = POWER_OFFSET + ByteConverter.INT_LEN_IN_BYTES;
	private static final int TRANSMITTER_UID_OFFSET = FLAG_OFFSET + 1;
	private static final int PAYLOAD_OFFSET = TRANSMITTER_UID_OFFSET + ByteConverter.INT_LEN_IN_BYTES;

	/**
	 * Bitmap for storing flags in the flag byte
	 */
	private static final byte MASTER_BIT = 0x01;
	
	/**
	 * The elevation of the antenna (metres above sea level)
	 */
	private int _elevation;
	
	/**
	 * The geographic position of the antenna
	 */
	private GeoPosition _position;
	
	/**
	 * The master flag (enables unlimitted coverage)
	 */
	private boolean _master;

	/**
	 * The frequency of the transmission
	 */
	private Frequency _frequency;
	
	/**
	 * The power of the sent or received signal
	 */
	private Decibel _signalPower;
	
	/**
	 * UID of the transmitter
	 */
	private int _transmitterUid;
	
	/**
	 * The data (byte array) exchanged with the modulator/demodulator
	 */
	private byte[] _payload;

	/**
	 * @return the elevation
	 */
	public int getElevation() {
		return _elevation;
	}

	/**
	 * @param elevation the elevation to set
	 */
	public void setElevation(int elevation) {
		this._elevation = elevation;
	}

	/**
	 * @return the frequency
	 */
	public Frequency getFrequency() {
		return _frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(Frequency frequency) {
		this._frequency = frequency;
	}

	/**
	 * @return the master flag
	 */
	public boolean isMaster() {
		return _master;
	}

	/**
	 * @param master the master to set
	 */
	public void setMaster(boolean master) {
		this._master = master;
	}

	/**
	 * @return the payload
	 */
	public byte[] getPayload() {
		return _payload;
	}

	/**
	 * @param payload the payload to set
	 */
	public void setPayload(byte[] payload) {
		this._payload = payload;
	}

	/**
	 * @return the position
	 */
	public GeoPosition getPosition() {
		return _position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(GeoPosition position) {
		this._position = position;
	}

	/**
	 * @return the signalPower
	 */
	public Decibel getSignalPower() {
		return _signalPower;
	}

	/**
	 * @param power the signalPower to set
	 */
	public void setSignalPower(Decibel power) {
		_signalPower = power;
	}

	/**
	 * @return the _transmitterUid
	 */
	public int getTransmitterUid() {
		return _transmitterUid;
	}

	/**
	 * @param uid the _transmitterUid to set
	 */
	public void setTransmitterUid(int uid) {
		_transmitterUid = uid;
	}

	/**
	 * Converts the object to a byte array representation.
	 */
	public byte[] getByteArray(){
		byte[] result = new byte[PAYLOAD_OFFSET+_payload.length];
		ByteConverter.intToByteArray(_elevation, result, ELEVATION_OFFSET );
		ByteConverter.intToByteArray(_position.getLatitude(), result, GEO_LATITUDE_OFFSET );
		ByteConverter.intToByteArray(_position.getLongitude(), result, GEO_LONGITUDE_OFFSET );
		ByteConverter.intToByteArray(_frequency.getFrequency(), result, FREQUENCY_OFFSET );
		_signalPower.toByteArray( result, POWER_OFFSET );
		result[FLAG_OFFSET] = ( _master ? MASTER_BIT : 0 ); 
		ByteConverter.intToByteArray(_transmitterUid, result, TRANSMITTER_UID_OFFSET );
    	System.arraycopy(_payload, 0, result, PAYLOAD_OFFSET, _payload.length);
		return result;
	}
	
	/**
	 * Extracts an UDPTransmissionPacket object from the given byte array
	 * @param src the byte array which contains the objects data
	 * @param offset the offset to start the extraction within byte array.
	 */
	public static UDPTransmissionPacket fromByteArray(byte[] src, int offset ){
		if( src.length <= offset+PAYLOAD_OFFSET ){
			throw new InvalidPacketException("Packet is too short (required at least "+(offset+PAYLOAD_OFFSET+1)+" bytes, but was "+src.length+")");
		}
		UDPTransmissionPacket result = new UDPTransmissionPacket();
		result._elevation = ByteConverter.byteArrayToInt(src, offset+ELEVATION_OFFSET);
		result._position = new GeoPosition(ByteConverter.byteArrayToInt(src, offset+GEO_LATITUDE_OFFSET),
										   ByteConverter.byteArrayToInt(src, offset+GEO_LONGITUDE_OFFSET) );
		result._frequency = new Frequency( ByteConverter.byteArrayToInt(src, offset+FREQUENCY_OFFSET) );
		result._signalPower = Decibel.fromByteArray(src, offset+POWER_OFFSET);
		byte flags = src[offset+FLAG_OFFSET];
		result._master = ((flags & MASTER_BIT) != 0);
		result._transmitterUid = ByteConverter.byteArrayToInt(src, offset+TRANSMITTER_UID_OFFSET);
		int payloadLength = src.length-offset-PAYLOAD_OFFSET;
		result._payload = new byte[payloadLength];
    	System.arraycopy(src, offset+PAYLOAD_OFFSET, result._payload, 0, payloadLength);
    	return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
	}

	/**
	 * Amplify the signal power with the given amplification
	 * @param amplification the amplification in decibel
	 */
	public void amplify(Decibel amplification) {
		_signalPower = _signalPower.add(amplification);
	}


}
