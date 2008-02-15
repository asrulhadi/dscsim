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

import java.util.Properties;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.MenuScreen;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.message.types.Latitude;
import net.sourceforge.dscsim.controller.message.types.Longitude;
import net.sourceforge.dscsim.controller.message.types.Position;
import net.sourceforge.dscsim.controller.screens.Screen;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayDistressCallScreen extends MenuScreen {

	public DisplayDistressCallScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);

		MultiContentManager oMngr = getInstanceContext().getContentManager();	
		Dscmessage selected = oMngr.getSelectedIncomingDistressCall();
		if(selected == null)
			return;
		Properties props = oMngr.getProperties();
		this.setTextBox("from", selected.getFrom());
		this.setTextBox("nature", props.getProperty(selected.getNatureCd()));
		
		this.setTextBox("time", selected.getPosition().getTime().toString());
		
		Position pos = selected.getPosition();
		if(pos != null){
		Latitude lat = pos.getLatitude();
		Longitude lon  = pos.getLongitude();
		this.setTextBox("lat", lat.getAsFromattedString(props));
		this.setTextBox("lon", lon.getAsFromattedString(props));	
		}else{
			setTextBox("lat", props.getProperty("MS_POS_NON"));
		}
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {
		
		if(msg.getButtonEvent().getKeyId().equals(KP_Aa)){
			MultiContentManager oMCmgr = getInstanceContext().getContentManager();		
			Dscmessage distmsg = oMCmgr.getSelectedIncomingDistressCall();			
			if(distmsg != null){				
				oMCmgr.removeIncomingDistressCall(distmsg);				
			}
		}
	}


	
}
