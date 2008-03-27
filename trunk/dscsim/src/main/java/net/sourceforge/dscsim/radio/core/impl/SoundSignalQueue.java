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

import java.io.StreamCorruptedException;

import org.apache.log4j.Logger;
import org.xiph.speex.SpeexDecoder;

import net.sourceforge.dscsim.radiotransport.Decibel;

/**
 * Queue of incoming signal data
 *
 */
public class SoundSignalQueue {
	
	/**
	 * The logger for this class
	 */
	private static Logger _logger = Logger.getLogger(SoundSignalQueue.class);
	
	private int _transmitterUid;
	
	private Decibel _power;
	
	private ByteBuffer _buffer;
	
	private SpeexDecoder _decoder;
	
	private int _inPackets;
	
	private int _outPackets;
	
	private int _packetHits;

	public SoundSignalQueue(int uid) {
		super();
		_buffer = new ByteBuffer();
		_transmitterUid = uid;
		_inPackets = 0;
		_outPackets = 0;
		_packetHits = 0;
        _decoder = new SpeexDecoder();
		_decoder.init(SoundPlaybackThread.SPEEX_MODE, (int)SoundPlaybackThread.TRANSMIT_SAMPLE_RATE, SoundPlaybackThread.CHANNEL_NUMBER, false);
	}

	/**
	 * @return the power
	 */
	public Decibel getPower() {
		return _power;
	}

	/**
	 * @param power the power to set
	 */
	public void setPower(Decibel power) {
		this._power = power;
	}

	/**
	 * @return the transmitterUid
	 */
	public int getTransmitterUid() {
		return _transmitterUid;
	}

	/**
	 * @param data
	 * @param sequenceNumber
	 * @see net.sourceforge.dscsim.radio.core.impl.ByteBuffer#add(byte[], int)
	 */
	public void add(byte[] data, int sequenceNumber) {
		_buffer.add(data, sequenceNumber);
		_inPackets++;
	}

	/**
	 * @return
	 * @see net.sourceforge.dscsim.radio.core.impl.ByteBuffer#getNext()
	 */
	public byte[] getNext() {
    	_outPackets++;
    	byte[] retValue = _buffer.getNext();
    	if( _logger.isDebugEnabled() ){
    		if( (_outPackets < 100 && (_outPackets % 10 == 0 ) ) ||
    			(_outPackets < 1000 && (_outPackets % 100 == 0 ) ) ||
    			(_outPackets % 1000 == 0 ) )
    		{
    			_logger.debug("Active Sound Queue: "+toString());
    		}
    	}
		return retValue;
	}
	
	public byte[] getNextSoundPacket() throws StreamCorruptedException {
    	byte[] speexData = getNext();
    	if(speexData!=null) {
			_decoder.processData(speexData, 0, speexData.length);
			_packetHits++;
    	} else {
			_decoder.processData(true);				
    	}
		int bytesReady = _decoder.getProcessedDataByteSize();
        byte[] outData = new byte[bytesReady];
		_decoder.getProcessedData(outData, 0); // might use only part of array
		return outData;
	}
	
	public boolean isEmpty() {
		return _buffer.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SoundSignalQueue TransmitterUID: ");
		sb.append(_transmitterUid);
		sb.append(" (IN: ");
		sb.append(_inPackets);
		sb.append("; ");
		sb.append("OUT: ");
		sb.append(_outPackets);
		sb.append("; HITS: ");
		sb.append(_packetHits);
		sb.append(")");
		return sb.toString();
	}

}
