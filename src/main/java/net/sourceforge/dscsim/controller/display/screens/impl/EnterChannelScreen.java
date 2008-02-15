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

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.screens.Screen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JEditBox;


/**
 * @author wnpr
 *
 */
public class EnterChannelScreen extends JEditBoxInputScreen {

	private JEditBox eb = null;
	
	public EnterChannelScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);
		eb = (JEditBox) this.getComponentByName("channel",0);
		eb.setValidator(new JEditBox.ChannelValidator());
		this.setForceRefresh(true);
		if(this.activeComponent != null)
			this.activeComponent.setCursor(true);
		
		eb.setValue(this.CH_16);

	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	@Override
	public void exit(BusMessage msg) throws Exception {			
		Dscmessage outGoing  = this.getInstanceContext().getContentManager().getOutGoingDscmessage();
		outGoing.setChannel(Integer.valueOf(eb.getValue()));
	}

}
