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
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JEditBox;
import net.sourceforge.dscsim.controller.screens.ActionMapping;
import net.sourceforge.dscsim.controller.screens.Screen;



/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EnterMmsiScreen extends JEditBoxInputScreen {

	public EnterMmsiScreen(JDisplay display,
			Screen screen) {
		super(display, screen);
	}

	private JEditBox eb = null;



	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	@Override
	public void enter(Object msg) {
		super.enter(msg);
		eb = (JEditBox) this.getComponentByName("mmsi_input",0);
		eb.setValidator(new JEditBox.MMSIValidator());
		this.setForceRefresh(true);
		if(this.activeComponent != null)
			this.activeComponent.setCursor(true);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	@Override
	public void exit(BusMessage msg) throws Exception {			
		Dscmessage outGoing  = this.getInstanceContext().getContentManager().getOutGoingDscmessage();
		outGoing.setRecipient(eb.getValue());
	}
	
	public ActionMapping notify(BusMessage msg){		
		return super.notify(msg);
	}

	
}
