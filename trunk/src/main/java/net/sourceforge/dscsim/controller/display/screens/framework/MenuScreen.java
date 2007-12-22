/*
 * Created on 15.03.2007
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

package net.sourceforge.dscsim.controller.display.screens.framework;

import java.awt.Component;

import org.jdom.Element;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.MultiController;
import net.sourceforge.dscsim.controller.panels.ActionMapping;
import net.sourceforge.dscsim.controller.panels.Screen;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MenuScreen extends ActionScreen implements
		net.sourceforge.dscsim.controller.Constants {

	private JMenu menu = null;

	/**
	 * @param oScreenElement
	 */
	public MenuScreen(JDisplay display, Screen screen) {
		super(display, screen);

		for (Object o : this.getComponents()) {
			if (o instanceof JMenu) {
				menu = (JMenu) o;
				break;
			}
		}
	}

	/**
	 * method called before signal method takes control.
	 *
	 */
	public void enter(Object msg) {

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public ActionMapping notify(BusMessage oMessage) {

		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		if (keyAction.equals(PRESSED) && keyID.equals(FK_ENT)
				&& getMenu() != null) {
			return new JActionMapping(PRESSED, FK_ENT, getMenu()
					.getSelectedData().getLink());
		}

		ActionMapping mapping = super.findActionMapping(keyAction, keyID);

		if (mapping != null)
			return mapping;

		if (menu != null)
			menu.signal(oMessage);

		return null;

	}

	public JMenu getMenu() {
		return menu;
	}

	public void setMenu(JMenu menu) {
		this.menu = menu;
	}

	public void paint(java.awt.Graphics g) {
		super.paint(g);
	}
	@Override
	public void remove(Component obj){
		if(obj == this.menu)
			this.menu = null;
		
		super.remove(obj);
	}

}
