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

/**
 * Base implementation for sound processors
 *
 */
public abstract class SoundProcessorImpl implements SoundProcessor {

	/**
	 * The currents sample value of both channels
	 */
	private int _chData[];
	
	/**
	 * the data array
	 */
	private byte[] _dataArray;
	
	/**
	 * Standard Constructor
	 */
	public SoundProcessorImpl() {
		_chData = new int[2];
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.impl.SoundProcessor#process(byte[], boolean)
	 */
	public void process(byte[] soundData, boolean littleEndian) {
		_dataArray = soundData;
		for( int i = 0; i<soundData.length; i+=4) {
			toChannel(i, littleEndian);
			performAction(_chData);
			fromChannel(i, littleEndian);
		}
	}
	
	/**
	 * Convert the current byte array sequence to channel data
	 */
	protected void toChannel(int index, boolean littleEndian) {
		for( int i = 0; i<2; i+=1) {
			if(littleEndian) {
				_chData[i]  = (_dataArray[index+2*i+1]<<8)|(_dataArray[index+2*i]&0xFF);
			} else {
				_chData[i]  = (_dataArray[index+2*i]<<8)|(_dataArray[index+2*i+1]&0xFF);
			}
		}
		
	}

	/**
	 * Convert the current channel data to the byte array sequence
	 */
	protected void fromChannel(int index, boolean littleEndian) {
		for( int i = 0; i<2; i+=1) {
			if(littleEndian) {
				_dataArray[index+2*i+0] = (byte)(_chData[i] & 0x000000FF);
				_dataArray[index+2*i+1] = (byte)((_chData[i] >>> 8) & 0x000000FF);
			} else {
				_dataArray[index+2*i+1] = (byte)(_chData[i] & 0x000000FF);
				_dataArray[index+2*i+0] = (byte)((_chData[i] >>> 8) & 0x000000FF);
			}
		}
		
	}

	/**
	 * Performs the action of the channel data
	 */
	protected abstract void performAction(int[] chData);
	
}
