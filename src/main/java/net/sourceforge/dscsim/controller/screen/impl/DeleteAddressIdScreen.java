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

import java.awt.Component;
import java.util.List;

import org.jdom.Element;

import net.sourceforge.dscsim.controller.AddressIdEntry;
import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.screen.BeanList;
import net.sourceforge.dscsim.controller.screen.EditBox;
import net.sourceforge.dscsim.controller.screen.EditBoxInputScreen;
import net.sourceforge.dscsim.controller.screen.ScreenComponent;
import net.sourceforge.dscsim.controller.screen.ScreenInterface;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteAddressIdScreen extends EditBoxInputScreen {

	private EditBox ebMmsi = null;
	private EditBox ebAddress = null;
	
	public DeleteAddressIdScreen(Element oScreenElement, MultiContentManager oCMngr) {
		super(oScreenElement, oCMngr);
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);
		
		AddressIdEntry addr = this.getInstanceContext().getContentManager().getSelectedAddressEntryId();	
		if(addr == null)
			return;
		
		ebMmsi = (EditBox) this.getComponentByName("mmsi",0);
		ebMmsi.setValue(addr.getId());
		ebMmsi.setEditMode(false);
		
		ebAddress = (EditBox) this.getComponentByName("addressid",0);
		ebAddress.setValue(addr.getName());
		ebAddress.setEditMode(false);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {		
		
		if(msg.getButtonEvent().getKeyId().equals(FK_ENT)){
			MultiContentManager oMCmgr = getInstanceContext().getContentManager();		
			BeanList beanList = oMCmgr.getBeanList("mmsi_addressbook");
			
			String entMmsi = this.ebMmsi.getValue();
			String entAddr = this.ebAddress.getValue();
			List list = beanList.getList();	
			AddressIdEntry address = null; 
			for(int i=0; i<list.size();i++){
				address = (AddressIdEntry)list.get(i);		
				if(entMmsi.equals(address.getId()) && entAddr.equals(address.getName())){
					break;
				} else {
					address = null;
				}
			}	
			
			if(address != null){
				beanList.removeItem(address);
				oMCmgr.storeBeanList(beanList);
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
	public ScreenInterface signal(BusMessage msg){		
		return super.signal(msg);
	}

	
}
