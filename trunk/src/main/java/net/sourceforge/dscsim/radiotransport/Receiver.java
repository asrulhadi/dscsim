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
package net.sourceforge.dscsim.radiotransport;

/**
 * Represents a Receiver.
 *
 */
public interface Receiver {

	/**
	 * Sets the frequency at which this receiver is receiving signals
	 * @param frequency the receiving frequency
	 */
	public void setFrequency(Frequency frequency);
	
	/**
	 * Sets the gain of the Receiver.
	 * @param gain the gain in dezibel
	 */
	public void setGain(int gain);
	
	/**
	 * Adds a Demodulator to this Receiver. Multiple Demodulators might be 
	 * connected to the receiver at the same time.
	 * @param demodulator the Demodulator to be connected to the receiver
	 */
	public void addDemodulator(Demodulator demodulator);
	
	/**
	 * Disables receiving signals. This can be used e.g. to inhibit receiving
	 * while at the same time sending signals via the transmitter build into
	 * the same radio. 
	 * @param disabled <code>true</code> disable receiving; <code>false</code>
	 * enable receiving
	 */
	public void disable(boolean disable);
}
