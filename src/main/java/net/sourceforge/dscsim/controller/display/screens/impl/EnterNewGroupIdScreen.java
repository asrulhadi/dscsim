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


import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JEditBox;
import net.sourceforge.dscsim.controller.message.types.AddressIdEntry;
import net.sourceforge.dscsim.controller.message.types.AddressIdEntryType;
import net.sourceforge.dscsim.controller.screens.ActionMapping;
import net.sourceforge.dscsim.controller.screens.Screen;

import java.util.ArrayList;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EnterNewGroupIdScreen extends JEditBoxInputScreen {

	public EnterNewGroupIdScreen(JDisplay display,
			Screen screen) {
		super(display, screen);
	
	}

	private JEditBox ebMmsi = null;
	private JEditBox ebAddress = null;
	



	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);
		ebMmsi = (JEditBox) this.getComponentByName("mmsi",0);
		ebMmsi.setValidator(new JEditBox.MMSIValidator());
		this.setForceRefresh(true);
		if(this.activeComponent != null)
			this.activeComponent.setCursor(true);
		
		ebAddress = (JEditBox) this.getComponentByName("addressid",0);
		ebAddress.setMode(JEditBox.Mode.Alpha);
		ebAddress.setValidator(new JEditBox.AddressIdValidator());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {		
		
		if(msg.getButtonEvent().getKeyId().equals(FK_ENT)){
			MultiContentManager oMCmgr = getInstanceContext().getContentManager();		
			AddressIdEntry entry = new AddressIdEntry(this.ebMmsi.getValue(), this.ebAddress.getValue(), AddressIdEntryType.GR);					
			oMCmgr.addAddressIdEntry(entry);	
		}
	
	}
	
	public ActionMapping notify(BusMessage msg){		
		return super.notify(msg);
	}

	
}
