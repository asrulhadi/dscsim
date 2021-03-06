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
import java.util.Properties;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.RadioCoreController;
import net.sourceforge.dscsim.controller.message.types.AddressIdEntry;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.message.types.Position;
import net.sourceforge.dscsim.controller.message.types.Latitude;
import net.sourceforge.dscsim.controller.message.types.Longitude;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JEditBox;
import net.sourceforge.dscsim.controller.display.screens.framework.JMenu;
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;
import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.screens.ActionMapping;
import net.sourceforge.dscsim.controller.screens.Screen;
/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IncomingDistressAlertAckScreen extends ActionScreen {

	private int pressCount = 0;
	
	public IncomingDistressAlertAckScreen(JDisplay display,
			Screen screen) {
		super(display, screen);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);

		pressCount=0;
		
		MultiContentManager oMngr = getInstanceContext().getContentManager();	
		Dscmessage incoming = oMngr.getIncomingDscmessage();
		if(incoming == null)
			return;
		
		this.setTextBox("mmsi", incoming.getSender());
		this.setTextBox("callerid", oMngr.findAddressId(incoming.getSender()));
	
		Dscmessage inComing = this.getIncomingDscmessage();
		if(inComing != null){
			RadioCoreController oRadio = getInstanceContext().getRadioCoreController();
			oRadio.setChannel(inComing.getChannelStr());					
		}


	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {
		super.exit(msg);
	}

	@Override
	public ActionMapping notify(BusMessage oMessage) {
		
		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		if(FK_CLR.equals(keyID)){
			
			if(pressCount > 0){
				return this.findActionMapping(keyAction, keyID);
			}
			
			if(PRESSED.equals(keyAction)){
				this.pressCount++;
			}
			
			return new ActionScreen.JActionMapping("", "", NULL);
		}
		
		return super.notify(oMessage);
	}
	
}
