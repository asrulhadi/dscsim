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
 
package net.sourceforge.dscsim.controller.screen;

import org.jdom.Element;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.MultiController;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SingleMenuScreen extends StateScreen  {

	protected Menu menu = null;
	/**
	 * @param oScreenElement
	 */
	public SingleMenuScreen(Element oScreenElement, MultiContentManager oCMngr) {
		super(oScreenElement, oCMngr);
	}

	/**
	 * method called before signal method takes control.
	 *
	 */
	public void enter(Object msg){
		super.enter(msg);
		/*parse menu*/
		Menu.parseMenu(this, elemScreen);
		
		/**/
		setMenu((Menu)this.getComponentByType(Menu.class, 0));	
	}
	/**
	 * 
	 */
	protected void setMenu(Menu menu){	
		this.menu  = menu;
		this.add(menu);
	}
	
	
	/**
	 * 
	 * @return menu item.
	 */
	protected Menu getMenu(){
		return this.menu;		
	}
	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public ScreenInterface signal(BusMessage oMessage) {
		
		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		if(keyAction.equals(PRESSED) && keyID.equals(super.FK_ENT)){			
    			String chosen = getMenu().getSelectedData();
    			net.sourceforge.dscsim.controller.utils.AppLogger.debug2("SingleMenuScreen.signal - chosen = " + chosen);
    			ScreenInterface oScreen = (ScreenInterface)getInstanceContext().getContentManager().getScreenContent(chosen, getInstanceContext());		
    			return oScreen;
		}	
		
		if(keyAction.equals(PRESSED) && keyID.equals(super.FK_CLR)){			
			MultiContentManager oMCmgr = getInstanceContext().getContentManager();
			String action = this.getAction(keyID);
			if(action != null)
				return (ScreenInterface)oMCmgr.getScreenContent(action, getInstanceContext());
			else
				return null;
	}		
		
		menu.signal(oMessage);
		
		return this;

	}

}
