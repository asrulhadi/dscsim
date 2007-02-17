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
 * Changes the volume of the processed sound
 *
 */
public class VolumeControl extends SoundProcessorImpl {

	/**
	 * Scaling factor
	 */
	private static final int SCALE = 1024;

	/**
	 * The gain to apply
	 */
	private double _gain;
	
	/**
	 * Constructor which sets the gain to the given value
	 * @param gain in the range between 0.0 and 100.0
	 * @throws IllegalArgumentException if the gain is not in the specified range
	 */
	public VolumeControl(double gain) {
		setGain(gain);
	}
	
	/**
	 * Standard constructor which sets the gain to 1.0
	 */
	public VolumeControl() {
		this(1.0);
	}
	
	/**
	 * Sets the gain to the given value
	 * @param gain in the range between 0.0 and 100.0
	 * @throws IllegalArgumentException if the gain is not in the specified range
	 */
	public void setGain(double gain) {
		if( gain > 100.0 || gain < 0.0 ) {
			throw new IllegalArgumentException("Gain not in allowed range (was: "+gain+")"); 
		}
		_gain = gain;
	}

	/**
	 * Performs the action of the channel data
	 */
	protected void performAction(int[] chData) {
		int factor = (int)(_gain * SCALE);
		for( int i = 0; i<2; i+=1) {
			long temp = ((long)chData[i] * factor / SCALE);
			temp = (temp > (long)Short.MAX_VALUE) ? Short.MAX_VALUE : temp; 
			temp = (temp < (long)Short.MIN_VALUE) ? Short.MIN_VALUE : temp; 
			chData[i] = (int)temp;
		}
		
	}
	
}
