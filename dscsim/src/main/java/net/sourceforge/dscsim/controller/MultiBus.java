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
 * The Initial Developer of the Original Code is William Pennoyer. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007, 2008, 20010.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.controller;

import java.util.ArrayList;

import net.sourceforge.dscsim.controller.utils.AppLogger;

public class MultiBus implements Constants {

	private ArrayList _oMembers = new ArrayList();

	/*
	 * initial state is off
	 */
	private boolean _power_on = false;

	private MultiBus() {
		AppLogger.debug("MultiBus ctor" + this);

	}

	public static MultiBus getInstance() {

		return new MultiBus();
	}

	public void putOnline(BusListener oListener) {
		//AppLogger.debug("Bus.putOnline - whom" + oListener);

		_oMembers.add(oListener);

		//AppLogger.debug("Bus.putOnline - size=" + _oMembers.size());

	}

	public void takeOffline(BusListener oListener) {

	}

	public void publish(BusMessage oMessage) {

		BusListener oListener = null;
		for (int i = 0; i < _oMembers.size(); i++) {
			oListener = (BusListener) _oMembers.get(i);

			if (oMessage.getFrom() != oListener) {
				oListener.signal(oMessage);
			}
		}
	}
}
