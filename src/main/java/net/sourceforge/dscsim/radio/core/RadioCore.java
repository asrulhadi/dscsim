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
package net.sourceforge.dscsim.radio.core;

import net.sourceforge.dscsim.radiotransport.Antenna;


/**
 * Public interface of the core functionality of the simulated radio.
 * This interface is used by the radio GUI and the dsc controller.
 */
public interface RadioCore {
	
	/**
	 * Switches the radio on and off
	 * @param on <code>true</code> radio is on, <code>false</code> radio is off
	 */
	public void setMasterSwitch(boolean on);
	
	/**
	 * Gets the state of the master switch
	 * @return <code>true</code> radio is on, <code>false</code> radio is off
	 */
	public boolean getMasterSwitch();
	
	/**
	 * Activates/Deactivates sending the sound captured via the microphone. This
	 * represents the "transmit button"
	 * @param active <code>true</code> sound will be transmitted, <code>false</code>
	 * sound will not be transmitted 
	 */
	public void setTransmit(boolean active);
	
	/**
	 * Sets the channel
	 * @param the VHF channel to use
	 */
	public void setChannel(VHFChannel channel);
	
	/**
	 * Gets the currently active channel
	 * @return the currently active channel
	 */
	public VHFChannel getChannel();
	
	/**
	 * Sets the transmission power
	 * @param high <code>true</code> use high power. <code>false</code> use low power
	 */
	public void setPower(boolean high);
	
	/**
	 * Gets the transmission power
	 * @return <code>true</code> high power is selected, <code>false</code> low power
	 * is selected
	 */
	public boolean getPower();
	
	/**
	 * Sets the volume level.
	 * @param volume the speaker volume. Normalized betwenn 0.0 and 1.0
	 */
	public void setVolume(double volume);
	
	/**
	 * Sets the squelch level.
	 * @param squelch the squelch level. Normalized between 0.0 and 1.0
	 */
	public void setSquelch(double squelch);
	
	/**
	 * Sends a (DSC) signal. The radio will switch to the predefined DSC channel
	 * and transmit the given signal. The method will block for the simulated time
	 * of sending the dsc signal.
	 * @param dscSignal the data to be transmitted on channel 70    
	 */
	public void sendDscSignal(byte[] dscSignal);
	
	/**
	 * Registers a RadioEventListener to receive notifications about
	 * state changes of this RadioCore object.
	 * @param listener the RadioEventListener which should be notified about
	 * state changes of the RadioCore object
	 */
	public void registerListener(RadioEventListener listener);
	
	/**
	 * Get the antenna to which this RadioCore is connected
	 * @param the Antenna to which this RadioCore is connected
	 */
	public Antenna getAntenna();
}
