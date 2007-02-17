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
import java.util.Random;

import net.sourceforge.dscsim.radiotransport.Decibel;

/**
 *SoundSignalQueue which delivers quiteness
 */
public class NoiseSoundSignalQueue extends SoundSignalQueue {
	
	/**
	 * The power of the noise signal in db[W]
	 */
	public static final double NOISE_POWER = 1e-6; 
	
	/**
	 * data representing silence
	 */
	private byte[] noise;

	/**
	 * The random signal generator
	 */
	private Random r;

	/**
	 * Length of the sound data packet
	 */
	private int packetLength;
	
	/**
	 * the power of the noise signal
	 */
	private Decibel noisePower;
	
	/**
	 * @param uid
	 */
	public NoiseSoundSignalQueue() {
		super(0);
		// construct array of same length as decoderdata
		byte[] dummyData=null;
		try {
			dummyData = super.getNextSoundPacket();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		}
		packetLength = dummyData.length;
		noise = new byte[dummyData.length]; // implicitely all 0
		r = new Random();
		r.nextBytes(noise);
		noisePower = Decibel.fromLinearPower(NOISE_POWER);
	}

	public byte[] getNextSoundPacket() {
		byte noise[] = new byte[packetLength];
		r.nextBytes(noise);
		return noise;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.impl.SoundSignalQueue#getPower()
	 */
	public Decibel getPower() {
		// TODO Auto-generated method stub
		return noisePower;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.impl.SoundSignalQueue#isEmpty()
	 */
	public boolean isEmpty() {
		return false;
	}
	

}
