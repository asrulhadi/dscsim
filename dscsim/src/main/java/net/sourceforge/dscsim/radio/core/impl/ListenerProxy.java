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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.dscsim.radio.core.RadioEventListener;

/**
 * Proxy object which propagates the received RadioEvent notifications
 * to all registered listeners
 */
public class ListenerProxy implements RadioEventListener {

	/**
	 * The collection of registered RadioEventListener objects 
	 */
	private Collection listeners;
	
	/**
	 * Standard constructor
	 */
	public ListenerProxy() {
		listeners = new ArrayList();
	}
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioEventListener#notifyChannel()
	 */
	public void notifyChannel() {
		for( Iterator i = listeners.iterator(); i.hasNext(); ){
			((RadioEventListener)i.next()).notifyChannel();
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioEventListener#notifyMasterSwitch()
	 */
	public void notifyMasterSwitch() {
		for( Iterator i = listeners.iterator(); i.hasNext(); ){
			((RadioEventListener)i.next()).notifyMasterSwitch();
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioEventListener#notifyPower()
	 */
	public void notifyPower() {
		for( Iterator i = listeners.iterator(); i.hasNext(); ){
			((RadioEventListener)i.next()).notifyPower();
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioEventListener#notifyDscTransmissionFinished()
	 */
	public void notifyDscTransmissionFinished() {
		for( Iterator i = listeners.iterator(); i.hasNext(); ){
			((RadioEventListener)i.next()).notifyDscTransmissionFinished();
		}
	}


	/**
	 * Registers a RadioEventListener to receive notifications from this
	 * proxy.
	 * @param listener the RadioEventListener which should be notified via this
	 * proxy
	 */
	public void registerListener(RadioEventListener listener) {
		// use temporary structure for thread safety (notify methods)
		Collection newListeners = new ArrayList(listeners);
		newListeners.add(listener);
		listeners = newListeners;
	}
	
}
