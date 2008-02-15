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
 
package net.sourceforge.dscsim.controller.display.screens.impl;

import java.util.ArrayList;

import net.sourceforge.dscsim.controller.AddressIdEntry;
import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JMenu;
import net.sourceforge.dscsim.controller.display.screens.framework.MenuScreen;
import net.sourceforge.dscsim.controller.screens.Screen;
import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SelectGroupIdScreen extends MenuScreen {


	public SelectGroupIdScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}

	/**
	 * default generated version id.
	 */
	private static final long serialVersionUID = -2605966413906785454L;




	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);		
		JMenu m = (JMenu)this.getComponentByName("group_ids_list", 0);
		ArrayList<AddressIdEntry>addresses = this.getInstanceContext().getContentManager().getGroupIdList();
		for(AddressIdEntry addr: addresses){
			m.addItem(addr.getName(), "select_category", addr.getId());
		}		
		setMenu(m);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage oMessage) {
		Dscmessage outGoing  = this.getInstanceContext().getContentManager().getOutGoingDscmessage();
		outGoing.setRecipient(this.getMenu().getSelectedData().getCode());		
		AppLogger.debug2("-----------"+this.getMenu().getSelectedData().toString());
	}


	
}
