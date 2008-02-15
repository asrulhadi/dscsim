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
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.display.screens.framework.MenuScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JMenu;
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;
import net.sourceforge.dscsim.controller.screens.Screen;

/**
 * @author wnpr
 *
 */
public class IncomingSelectIdScreen  extends MenuScreen
implements Constants {

	public IncomingSelectIdScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}

	@Override
	public void enter(Object msg) {
		super.enter(msg);
		
		JMenu menu = this.getMenu();		
		MultiContentManager mngr = getInstanceContext().getContentManager();
		
		ArrayList<Dscmessage>calls = mngr.getIncomingOtherCalls();		
		Dscmessage dscmsg = calls.get(0);

		if(dscmsg != null){
			menu.addItem(dscmsg.getSender(), "ack_individual_compliance", "");
			mngr.setIncomingDscmessage(dscmsg);
		}
		
		menu.addItem("Other", "select_ack_other_calls", "OTHER");
	
	}
	
	@Override
	public void exit(BusMessage msg) throws Exception {

		String keyID = msg.getButtonEvent().getKeyId();
				
		if (keyID.equals(FK_ENT) || keyID.equals(FK_CALL)) {
			MultiContentManager mngr = getInstanceContext()
					.getContentManager();
			//set to default which is good if other is not chosen.
			//mngr.setIncomingDscmessage();
			
		}
	}

}
