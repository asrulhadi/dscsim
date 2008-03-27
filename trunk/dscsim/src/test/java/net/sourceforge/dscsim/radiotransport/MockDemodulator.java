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
 * the Initial Developer are Copyright (C) 2006, 2007, 2008.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.radiotransport;

/**
 * @author oliver
 *
 */
public class MockDemodulator implements Demodulator {

	private int callCounter;
	
	private byte[] signal;
	
	private int transmitterUid;
	
	private Frequency frequency;
	
	private Decibel decibel;
	
	
	public MockDemodulator() {
		callCounter = 0;
	}



	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Demodulator#processSignal(byte[], int, net.sourceforge.dscsim.radiotransport.Frequency, net.sourceforge.dscsim.radiotransport.Decibel)
	 */
	public void processSignal(byte[] signal, int transmitterUid,
			Frequency frequency, Decibel decibel) {
		callCounter++;
		this.signal = signal;
		this.transmitterUid = transmitterUid;
		this.frequency = frequency;
		this.decibel = decibel;
	}

	/**
	 * @return the signal
	 */
	public byte[] getSignal() {
		return signal;
	}


	/**
	 * @return the transmitterUid
	 */
	public int getTransmitterUid() {
		return transmitterUid;
	}

	/**
	 * @return the frequency
	 */
	public Frequency getFrequency() {
		return frequency;
	}


	/**
	 * @return the decibel
	 */
	public Decibel getDecibel() {
		return decibel;
	}



	/**
	 * @return the callCounter
	 */
	public int getCallCounter() {
		return callCounter;
	}

}
