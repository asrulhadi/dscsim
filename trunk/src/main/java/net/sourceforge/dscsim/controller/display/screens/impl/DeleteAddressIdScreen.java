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
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JEditBox;
import net.sourceforge.dscsim.controller.panels.Screen;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteAddressIdScreen extends JEditBoxInputScreen {

	private JEditBox ebMmsi = null;
	private JEditBox ebAddress = null;
	
	public DeleteAddressIdScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);
		
		AddressIdEntry addr = this.getInstanceContext().getContentManager().getSelectedAddressId();	
		if(addr == null)
			return;
		
		ebMmsi = (JEditBox) this.getComponentByName("mmsi",0);
		ebMmsi.setValue(addr.getId());
		ebMmsi.setEditMode(false);
		
		ebAddress = (JEditBox) this.getComponentByName("addressid",0);
		ebAddress.setValue(addr.getName());
		ebAddress.setEditMode(false);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {		
		
		if(msg.getButtonEvent().getKeyId().equals(FK_ENT)){
			MultiContentManager oMCmgr = getInstanceContext().getContentManager();		
			ArrayList<AddressIdEntry>beanList = oMCmgr.getAddressIdList();
			
			String entMmsi = this.ebMmsi.getValue();
			String entAddr = this.ebAddress.getValue();

			for(AddressIdEntry address:beanList){	
				if(entMmsi.equals(address.getId()) && entAddr.equals(address.getName())){
					beanList.remove(address);
					oMCmgr.storeListAddressIdList();
					break;
				} else {
					address = null;
				}
			}	
		}
	
	}
	/**
	 * are all ScreenComponents complete.
	 * @return
	 */
	public boolean isScreenComplete(){		
		return true;
	}

	
}
