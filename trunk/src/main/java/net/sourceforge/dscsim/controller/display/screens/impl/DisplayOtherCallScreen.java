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

import net.sourceforge.dscsim.controller.AddressIdEntry;
import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JEditBox;
import net.sourceforge.dscsim.controller.display.screens.framework.JMenu;
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;
import net.sourceforge.dscsim.controller.display.screens.framework.MenuScreen;
import net.sourceforge.dscsim.controller.network.DscMessage;
/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayOtherCallScreen extends MenuScreen {

	private JTextBox tbDescr = null;
	private JTextBox tbFrom = null;
	private JTextBox tbChannel = null;


	public DisplayOtherCallScreen(JDisplay display,
			net.sourceforge.dscsim.controller.panels.Screen screen) {
		super(display, screen);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);
		
		this.tbDescr = (JTextBox) this.getComponentByName("descr", 0);
		this.tbFrom = (JTextBox) this.getComponentByName("from", 0);
		this.tbChannel = (JTextBox) this.getComponentByName("channel", 0);

		MultiContentManager oMngr = getInstanceContext().getContentManager();	
		DscMessage selected = oMngr.getSelectedIncomingOtherCall();
		if(selected == null)
			return;
		
		Properties props = oMngr.getProperties();
		this.tbDescr.setText(selected.getCatagoryForTypeText());
		this.tbFrom.setText(selected.getFromMMSI());
		this.tbChannel.setText(selected.getChannel());
		

		
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {

	}


	
}
