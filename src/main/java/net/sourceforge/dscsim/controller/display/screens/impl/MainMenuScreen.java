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

import java.util.List;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.MenuScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JMenu;
import net.sourceforge.dscsim.controller.settings.InfoStoreType;
import net.sourceforge.dscsim.controller.message.types.Position;
import net.sourceforge.dscsim.controller.message.types.Latitude;
import net.sourceforge.dscsim.controller.message.types.Longitude;
import net.sourceforge.dscsim.controller.message.types.Time;
import net.sourceforge.dscsim.controller.screens.Screen;

/**
 * @author wnpr
 * 
 */
public class MainMenuScreen extends MenuScreen {

	public MainMenuScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}

	@Override
	public void enter(Object msg) {
		super.enter(msg);
		
		JMenu m = (JMenu)this.getComponentByName("main_menu", 0);
		List l = this.getInstanceContext().getContentManager().getIncomingOtherCalls();	
		if(l != null && l.size()>0)
			m.setChoiceVisible(CALL_TYPE_INDIVIDUAL_ACK, true);
		else
			m.setChoiceVisible(CALL_TYPE_INDIVIDUAL_ACK, false);
	}

	@Override
	public void exit(BusMessage msg) throws Exception {
		super.exit(msg);

		if (msg.getButtonEvent().getKeyId().equals(FK_ENT)) {
			String code = this.getMenu().getSelectedData().getCode();
			/*beginning of call sequence. Initialize outgoing message*/
			if (code != null
					&& (code.equals(CALL_TYPE_INDIVIDUAL)
							|| code.equals(CALL_TYPE_INDIVIDUAL_ACK)
							|| code.equals(CALL_TYPE_GROUP)
							|| code.equals(CALL_TYPE_ALL_SHIPS) || code
							.equals(CALL_TYPE_POSITION_REQ))) {

				Dscmessage outGoing = new Dscmessage();
				this.getInstanceContext().getContentManager()
						.setOutGoingDscmessage(outGoing);
				InfoStoreType store = this.getInstanceContext()
						.getContentManager().getInfoStore();
				Position pos = store.getPosition();
				Latitude lat = pos.getLatitude();
				Longitude lon = pos.getLongitude();
				Time time = pos.getTime();

				
				Latitude dlat = new Latitude(lat.getDegrees(), lat
						.getMinutes(), lat.getHemisphere());
				
				Longitude dlon = new Longitude(lon.getDegrees(),
						lon.getMinutes(), lon.getHemisphere());
				
				Time dtime = new Time(time.getHours(), time.getMinutes());
				
				Position dpos = new Position(dlat, dlon, dtime);
			
				outGoing.setPosition(dpos);
				
				outGoing.setCallTypeCd(code);
			}

		}

	}
}
