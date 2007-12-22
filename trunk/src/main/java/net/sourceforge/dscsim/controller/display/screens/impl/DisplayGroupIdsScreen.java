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
import java.util.List;

import net.sourceforge.dscsim.controller.AddressIdEntry;
import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JMenu;
import net.sourceforge.dscsim.controller.display.screens.framework.MenuScreen;
/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayGroupIdsScreen extends MenuScreen {

	public DisplayGroupIdsScreen(JDisplay display,
			net.sourceforge.dscsim.controller.panels.Screen screen) {
		super(display, screen);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);
		
		JMenu menu = (JMenu)this.getComponentByName("menu", 0);			
		MultiContentManager oMCmgr = getInstanceContext().getContentManager();		
		ArrayList<AddressIdEntry>beanList = oMCmgr.getGroupIdList();	
		
		if(beanList.size()<1){
			this.remove(menu);
			return;
		}
		for(AddressIdEntry address: beanList){	
			menu.addItem(address.getName(), "delete_group", "");
		}	
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {

		JMenu menu = (JMenu)this.getComponentByName("menu", 0);	
		
		if(menu == null)
			return;
		
		String selectedKey = menu.getSelectedKey();
		if(msg.getButtonEvent().getKeyId().equals(FK_ENT) && selectedKey != null){
			MultiContentManager oMCmgr = getInstanceContext().getContentManager();		
			ArrayList<AddressIdEntry>beanList = oMCmgr.getGroupIdList();
			for(AddressIdEntry address: beanList){		
				if(selectedKey.equals(address.getName())){
					oMCmgr.setSelectedGroupId(address);
					break;
				}
			}	
		}else{
			getInstanceContext().getContentManager().setSelectedGroupId(null);
		}
	}


	
}
