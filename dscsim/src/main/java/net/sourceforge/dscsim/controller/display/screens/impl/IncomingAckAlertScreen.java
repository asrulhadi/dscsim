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
import java.util.Properties;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.RadioCoreController;
import net.sourceforge.dscsim.controller.message.types.AddressIdEntry;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JMenu;
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;
import net.sourceforge.dscsim.controller.screens.ActionMapping;
import net.sourceforge.dscsim.controller.screens.Screen;

/**
 * @author wnpr
 *
 */
public class IncomingAckAlertScreen extends ActionScreen implements Constants {

	public IncomingAckAlertScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}

	@Override
	public void enter(Object msg) {
		super.enter(msg);

		MultiContentManager mngr = getInstanceContext().getContentManager();
		JTextBox tb = (JTextBox) this.getComponentByName("mmsi", 0);
		tb.setText(this.getIncomingDscmessage().getSender());

		Properties props = mngr.getProperties();
		tb = (JTextBox) this.getComponentByName("compl", 0);
		tb.setText(props.getProperty(this.getIncomingDscmessage().getComplianceCd()));

		tb = (JTextBox) this.getComponentByName("reason", 0);
		tb.setText(props.getProperty(this.getIncomingDscmessage().getComplianceReasonCd()));
	}

	@Override
	public ActionMapping notify(BusMessage oMessage) {

		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();
		
		if (keyAction.equals(PRESSED)
				&& keyID.equals(FK_ENT)){
		
			Dscmessage inComing = this.getIncomingDscmessage();
			if(inComing != null && COMPL_ABLE.equals(inComing.getComplianceCd())){
				RadioCoreController oRadio = getInstanceContext().getRadioCoreController();
				oRadio.setChannel(inComing.getChannelStr());					
			}
		}

		return super.notify(oMessage);
		
		
	}
	
	@Override
	public void exit(BusMessage msg) throws Exception {
		super.exit(msg);
		

	}

}
