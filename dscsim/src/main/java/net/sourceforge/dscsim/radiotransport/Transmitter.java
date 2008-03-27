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
 * Represents a Transmitter
 *
 */
public interface Transmitter {
	/**
	 * Describes a hint for the transmitter which might influence the way of transportation
	 * of the signal. (e.g. to achieve low latency for voice signals).
	 * TODO: further implement this concept
	 */
	public class Hint {}

	/**
	 * Sets the frequency at which this transmitter is sending signals
	 * @param frequency the transmitter frequency
	 */
	public void setFrequency(Frequency frequency);
	
	/**
	 * Sets the transmitting power.
	 * @param power the transmitting power in Watt
	 */
	public void setPower(float power);
	
	/**
	 * Sends the given signal.
	 * @param signal the signal to send
	 * @param hint a hint which might influence the way of transportation to achieve
	 * a specific quality of service (QoS)
	 */
	public void transmit(byte[] signal, Hint hint);

}
