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

import org.apache.log4j.Logger;

/**
 * Changes the volume of the processed sound
 *
 */
public class CompressorAmplifier extends SoundProcessorImpl {
	
	/**
	 * The logger
	 */
	private static final Logger logger = Logger.getLogger(CompressorAmplifier.class);
	
	/**
	 * Scaling factor
	 */
	private static final int SCALE = 1024;

	/**
	 * The gain to apply
	 */
	private double _currentGain;
	
	/**
	 * The factor for scaling
	 */
	private int _factor;

	
	/**
	 * The maximum gain to apply
	 */
	private double _maxGain;

	/**
	 * The minumum gain to apply
	 */
	private double _minGain;
	
	/**
	 * The target amplitude
	 */
	private int _targetAmplitude;
	
	/**
	 * Averaged level
	 */
	private double _averageAmplitude;
	
	/**
	 * counter for cyclig logging
	 */
	private int _logCounter;
	
	/**
	 * Constructor which sets the gain limits to the given value
	 * @param minGain 
	 * @param maxGain 
	 */
	public CompressorAmplifier(double minGain, double maxGain) {
        _currentGain = maxGain;
        _factor = (int)(_currentGain * SCALE);
        _averageAmplitude = 0.0;
        setMinGain(minGain);
		setMaxGain(maxGain);
		setTargetAmplitude((int)(Short.MAX_VALUE*0.25));
        _averageAmplitude = _targetAmplitude;
        _logCounter = 0;
	}
	
	/**
	 * Standard constructor which sets the gains to 1.0 and 100.0
	 */
	public CompressorAmplifier() {
		this(1.0, 100.0);
	}
	
	/**
	 * Sets the maximum gain to the given value
	 * @param maxGain the maximum gain allowed for the amplifier
	 */
	public void setMaxGain(double maxGain) {
		_maxGain = maxGain;
	}

	/**
	 * Sets the minimum gain to the given value
	 * @param minGain the minimum gain allowed for the amplifier
	 */
	public void setMinGain(double minGain) {
		_minGain = minGain;
	}

	/**
	 * Sets the target amplitude of the sound data
	 */
	public void setTargetAmplitude(int targetAmplitude) {
		_targetAmplitude = targetAmplitude;
	}

	/**
	 * Performs the action of the channel data
	 */
	protected void performAction(int[] chData) {
		int currentAmp = Math.max(Math.abs(chData[0]),Math.abs(chData[1]));
		_averageAmplitude = _averageAmplitude + 0.01 * (currentAmp-_averageAmplitude);

		int decisionAmplitude = (int)(_averageAmplitude*_currentGain);
		double newGain = _currentGain;
		if( decisionAmplitude > (_targetAmplitude*1.1) ) {
			newGain = _currentGain * (1 - 0.2);
		}
		if( decisionAmplitude < (_targetAmplitude*0.9) ) {
			newGain = _currentGain * (1 + 0.0001);
		}
		if( newGain < _minGain ) {
			newGain = _minGain;
		}
		if( newGain > _maxGain ) {
			newGain = _maxGain;
		}
		_currentGain = newGain;

		_logCounter++;
		if( _logCounter > 1000 ) {
			_logCounter = 0;
			logger.trace("Gain of audio amplifier is is "+_currentGain);
		}

		_factor = (int)(_currentGain * SCALE);
		for( int i = 0; i<2; i+=1) {
			long temp = ((long)chData[i] * _factor / SCALE);
			temp = (temp > (long)Short.MAX_VALUE) ? Short.MAX_VALUE : temp; 
			temp = (temp < (long)Short.MIN_VALUE) ? Short.MIN_VALUE : temp; 
			chData[i] = (int)temp;
		}
		
	}
	
}
