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
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;
import net.sourceforge.dscsim.controller.display.screens.framework.MenuScreen;
import net.sourceforge.dscsim.controller.network.DscMessage;
/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayOtherCallsScreen extends MenuScreen {
	
	private JMenu menu = null;
	private JTextBox tbMessage = null;
	

	public DisplayOtherCallsScreen(JDisplay display,
			net.sourceforge.dscsim.controller.panels.Screen screen) {
		super(display, screen);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);
		
		JMenu m = (JMenu)this.getComponentByName("menu", 0);			
		if(m != null)
			this.menu = m;
		
		JTextBox t = (JTextBox)this.getComponentByName("nothing", 0);
		if(t!=null)
			this.tbMessage=t;

		
		MultiContentManager oMCmgr = getInstanceContext().getContentManager();		
		ArrayList<DscMessage>callsList = oMCmgr.getIncomingOtherCalls();	
		
		/*in case screen was cached.*/
		if(callsList.size()<1){
			if(m!=null)
				this.remove(m);
			
			if(t==null)
				this.add(this.tbMessage);
			return;
		} else{
			if(m==null){
				this.menu.removeAll();
				this.add(this.menu);
			}
			
			if(t!=null)
				this.remove(t);
		}
		int count = 1;
		for(DscMessage call: callsList){	
			menu.addItem(count + ":" + call.getCallTypeText(), "show_other_call_detail", "");
			count++;
		}	
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {

		JMenu m = (JMenu)this.getComponentByName("menu", 0);	
		
		if(m == null)
			return;
		
		int selected = m.getSelected();
		if(msg.getButtonEvent().getKeyId().equals(FK_ENT) && selected > -1){
			MultiContentManager oMCmgr = getInstanceContext().getContentManager();		
			ArrayList<DscMessage>callList = oMCmgr.getIncomingOtherCalls();
			oMCmgr.setSelectedIncomingOtherCall(callList.get(selected));
		}else{
			getInstanceContext().getContentManager().setSelectedIncomingOtherCall(null);
		}
	}


	
}
