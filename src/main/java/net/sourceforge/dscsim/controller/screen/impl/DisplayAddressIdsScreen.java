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
import net.sourceforge.dscsim.controller.screen.BeanList;
import net.sourceforge.dscsim.controller.screen.Menu;
import net.sourceforge.dscsim.controller.screen.Screen;
import net.sourceforge.dscsim.controller.screen.ScreenContent;
import net.sourceforge.dscsim.controller.screen.SingleMenuScreen;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayAddressIdsScreen extends SingleMenuScreen {

	public DisplayAddressIdsScreen(Element oScreenElement, MultiContentManager oCMngr) {
		super(oScreenElement, oCMngr);	
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);
		
		Menu menu = (Menu)this.getComponentByName("the_menu", 0);			
		MultiContentManager oMCmgr = getInstanceContext().getContentManager();		
		BeanList beanList = oMCmgr.getBeanList("mmsi_addressbook");
		List list = beanList.getList();	
		AddressIdEntry address = null; 
		for(int i=0; i<list.size();i++){
			address = (AddressIdEntry)list.get(i);		
			menu.addItem(address.getName(), "delete_address");
		}	
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {

		Menu menu = (Menu)this.getComponentByName("the_menu", 0);	
		String selectedKey = menu.getSelectedKey();
		if(msg.getButtonEvent().getKeyId().equals(FK_ENT) && selectedKey != null){
			MultiContentManager oMCmgr = getInstanceContext().getContentManager();		
			BeanList beanList = oMCmgr.getBeanList("mmsi_addressbook");
			List list = beanList.getList();	
			AddressIdEntry address = null; 
			for(int i=0; i<list.size();i++){
				address = (AddressIdEntry)list.get(i);			
				if(selectedKey.equals(address.getName())){
					oMCmgr.setSelectedAddressEntryId(address);
					break;
				}
			}	
		}else{
			getInstanceContext().getContentManager().setSelectedAddressEntryId(null);
		}
	}


	
}
