/*
 * Created on 04.03.2007
 * katharina
 * 
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
 
package net.sourceforge.dscsim.controller.screen.impl;

import java.util.List;

import org.jdom.Element;

import net.sourceforge.dscsim.controller.AddressIdEntry;
import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.screen.Menu;
import net.sourceforge.dscsim.controller.screen.SingleMenuScreen;
import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SelectAddressIdScreen extends SingleMenuScreen {


	/**
	 * default generated version id.
	 */
	private static final long serialVersionUID = -2605966413906785454L;

	public SelectAddressIdScreen(Element oScreenElement, MultiContentManager oCMngr) {
		super(oScreenElement, oCMngr);		
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);		
		Menu m = (Menu)this.getComponentByName("address_ids_list", 0);
		List addresses = this.getInstanceContext().getContentManager().getStorageList("mmsi_addressbook");
		AddressIdEntry addr = null;
		for(int i=0; addresses!=null && i<addresses.size();++i){
			addr = (AddressIdEntry)addresses.get(i);
			m.addItem(addr.getName(), "select_category");
		}		
		setMenu(m);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage oMessage) {
		AppLogger.debug2("-----------"+this.getMenu().getSelectedData());
	}


	
}
