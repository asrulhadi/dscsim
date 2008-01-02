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
 * The Initial Developer of the Original Code is wnpr. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.controller.display.screens.impl;

import java.util.ArrayList;

import net.sourceforge.dscsim.controller.AddressIdEntry;
import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.MenuScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JMenu;
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;
import net.sourceforge.dscsim.controller.panels.Screen;
import net.sourceforge.dscsim.controller.network.DscMessage;

/**
 * @author wnpr
 *
 */
public class IncomingSelectOtherIdScreen  extends MenuScreen
implements Constants {

	public IncomingSelectOtherIdScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}

	@Override
	public void enter(Object msg) {
		super.enter(msg);
		
		JMenu menu = this.getMenu();		
		MultiContentManager mngr = getInstanceContext().getContentManager();		
		ArrayList<DscMessage>calls = mngr.getIncomingOtherCalls();		
		for(DscMessage call: calls){		
			menu.addItem(call.getFromMMSI(), "ack_individual_compliance", "");		
		}
		
	}
	
	@Override
	public void exit(BusMessage msg) throws Exception {

		String keyID = msg.getButtonEvent().getKeyId();
				
		if (keyID.equals(FK_ENT) || keyID.equals(FK_CALL)) {
			MultiContentManager mngr = getInstanceContext()
					.getContentManager();
			ArrayList<DscMessage>calls = mngr.getIncomingOtherCalls();		
			int selected = this.getMenu().getSelected();
			mngr.setIncomingDscMessage(calls.get(selected));
			
		}
	}

}
