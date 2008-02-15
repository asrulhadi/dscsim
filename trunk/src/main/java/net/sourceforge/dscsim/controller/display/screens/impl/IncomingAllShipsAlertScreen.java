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
import net.sourceforge.dscsim.controller.RadioCoreController;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.screens.Screen;
import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JMenu;
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;

/**
 * @author wnpr
 *
 */
public class IncomingAllShipsAlertScreen  extends ActionScreen
implements Constants {

	public IncomingAllShipsAlertScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}

	@Override
	public void enter(Object msg) {
		super.enter(msg);
		
		JTextBox tb = (JTextBox)this.getComponentByName("mmsi", 0);			
		MultiContentManager mngr = getInstanceContext().getContentManager();		
		tb.setText(this.getIncomingDscmessage().getSender());
		
		Dscmessage inComing = this.getIncomingDscmessage();
		if(inComing != null){
			RadioCoreController oRadio = getInstanceContext().getRadioCoreController();
			oRadio.setChannel(inComing.getChannelStr());					
		}

		
	}
	
	@Override
	public void exit(BusMessage msg) throws Exception {

		String keyID = msg.getButtonEvent().getKeyId();
				
		if (keyID.equals(FK_ENT) || keyID.equals(FK_CALL)) {
			MultiContentManager mngr = getInstanceContext()
					.getContentManager();
			mngr.setIncomingDscmessage(this.getIncomingDscmessage());
			Dscmessage outGoing  = new Dscmessage();
			outGoing.setRecipient(this.getIncomingDscmessage().getSender());
			outGoing.setCallTypeCd(CALL_TYPE_INDIVIDUAL_ACK);
			mngr.setOutGoingDscmessage(outGoing);
		}
	}

}
