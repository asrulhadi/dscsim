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
 */package net.sourceforge.dscsim.radio.core.impl;

import net.sourceforge.dscsim.radio.core.RadioCore;
import net.sourceforge.dscsim.radio.core.RadioEventListener;
import net.sourceforge.dscsim.radio.core.VHFChannel;
import net.sourceforge.dscsim.radiotransport.Antenna;
import net.sourceforge.dscsim.radiotransport.Frequency;

/**
 * Dummy implementation of the RadioCore interface without real
 * functionality. Getters will only return the values given by the setter method.
 * RadioEvents will immediately be generated when the objects state is manipulated
 * via the setter method. This class is mainly intented as mock object for implementing
 * the GUI for the simulated radio.
 */
public class RadioCoreDummy implements RadioCore {
	
	/**
	 * The VHF channel
	 */
	private VHFChannel _channel;
	
	/**
	 * high/low power flag
	 */
	private boolean _high;
	
	/**
	 * masterswitch state
	 */
	private boolean _on;
	
	/**
	 * proxy for RadioEvent listeners
	 */
	private ListenerProxy _listenerProxy;
	
	/**
	 * Standard constructor
	 */
	public RadioCoreDummy() {
		_channel = VHFChannel.VHF_CHANNEL_16;
		_high = true;
		_on = true;
		_listenerProxy = new ListenerProxy();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#getChannel()
	 */
	public VHFChannel getChannel() {
		return _channel;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#getPower()
	 */
	public boolean getPower() {
		return _high;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#registerListener(net.sourceforge.dscsim.radio.core.RadioEventListener)
	 */
	public void registerListener(RadioEventListener listener) {
		_listenerProxy.registerListener(listener);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#sendDscSignal(byte[])
	 */
	public void sendDscSignal(byte[] dscSignal) {
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setChannel(int)
	 */
	public void setChannel(VHFChannel channel) {
		_channel = channel;
		_listenerProxy.notifyChannel();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setMasterSwitch(boolean)
	 */
	public void setMasterSwitch(boolean on) {
		_on = on;
		_listenerProxy.notifyMasterSwitch();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setPower(boolean)
	 */
	public void setPower(boolean high) {
		_high=high;
		_listenerProxy.notifyPower();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setSquelch(double)
	 */
	public void setSquelch(double squelch) {
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setTransmit(boolean)
	 */
	public void setTransmit(boolean active) {
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setVolume(double)
	 */
	public void setVolume(double volume) {
	}

	public Antenna getAntenna() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getMasterSwitch() {
		return _on;
	}

}
