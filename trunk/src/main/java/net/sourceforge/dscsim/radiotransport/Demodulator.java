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
 * A Demodulator describes the callback interface of a Receiver. Objects which
 * need to process the data received by a Receiver need to implement this interface.
 */
public interface Demodulator {
	/**
	 * Callback method to process the data which is received by a Receiver
	 * @param signal the received data
	 * @param the id of the sending transmitter
	 * @param frequency the frequency at which the signal was received
	 * @param decibel value which indicates the strength of the signal
	 */
	public void processSignal(byte[] signal, int transmitterUid, Frequency frequency, Decibel decibel);
	
}
