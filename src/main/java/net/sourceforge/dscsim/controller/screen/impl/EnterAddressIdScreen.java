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

import org.jdom.Element;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.MultiController;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.screen.EditBox;
import net.sourceforge.dscsim.controller.screen.EditBoxInputScreen;
import net.sourceforge.dscsim.controller.screen.Screen;
import net.sourceforge.dscsim.controller.screen.ScreenContent;
import net.sourceforge.dscsim.controller.screen.ScreenInterface;
import net.sourceforge.dscsim.controller.screen.SingleMenuScreen;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EnterAddressIdScreen extends EditBoxInputScreen {

	private EditBox ebMmsi = null;
	private EditBox ebAddress = null;
	
	public EnterAddressIdScreen(Element oScreenElement, MultiContentManager oCMngr) {
		super(oScreenElement, oCMngr);
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);
		ebMmsi = (EditBox) this.getComponentByName("mmsi",0);
		ebMmsi.setValidator(new EditBox.MMSIValidator());
		this.setForceRefresh(true);
		if(this.activeComponent != null)
			this.activeComponent.setCursor(true);
		
		ebAddress = (EditBox) this.getComponentByName("addressid",0);
		ebAddress.setModeDigit(false);
		ebAddress.setValidator(new EditBox.AddressIdValidator());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {		

	}
	
	public ScreenInterface signal(BusMessage msg){		
		return super.signal(msg);
	}

	
}