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
 * The Initial Developer of the Original Code is William Pennoyer. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */

package net.sourceforge.dscsim.controller.display.screens.framework;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.panels.ActionMapping;
import net.sourceforge.dscsim.controller.panels.Screen;
import net.sourceforge.dscsim.controller.screen.ScreenInterface;


public class ActionScreen extends JScreen 
	implements ScreenInterface{
	
	/*
	 * 
	 */
	private boolean forceRefresh = false;
	
	private DscMessage incomingDscMessage = null;

	
	public ActionScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}
	
	public static class JActionMapping 
		implements ActionMapping {
		
		private String event;
		private String source;
		private String forward;
		
		public JActionMapping(String event, String source, String forward){
			this.event = event;
			this.source = source;
			this.forward = forward;
		}
		public String getEvent() {
			return event;
		}
		public void setEvent(String event) {
			this.event = event;
		}
		public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
		public String getForward() {
			return forward;
		}
		public void setForward(String forward) {
			this.forward = forward;
		}
		
		
	}
	
	public ActionMapping notify(BusMessage oMessage) {
		
		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		ActionMapping mapping = super.findActionMapping(keyAction, keyID);
		
		if(mapping != null)
			return mapping;
		
		return null;
	}

	//delete as many as you can after cleaning up.

	public void enter(Object arg0) {
			
	}

	
	public void exit(BusMessage message) throws Exception {
		
	}

	/**
	 * for backwards comp.
	 */
	
	public String getAttributeValue(String attrName) {
		if(attrName != null && attrName.equals("name")){
			return this.getScreenBindings().getName();
		}
		return null;
	}

	
	public DscMessage getIncomingDscMessage() {
		return this.incomingDscMessage;
	}



	
	public DscMessage getOutGoingDscMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void init() {
		// TODO Auto-generated method stub
		
	}

	
	public void setIncomingDscMessage(DscMessage dscMessage) {
		this.incomingDscMessage = dscMessage;
		
	}
	
	
	public void setOutGoingDscMessage(DscMessage dscMessage) {
		// TODO Auto-generated method stub
		
	}

	
	public void setParent(ScreenInterface parent) {
		// TODO Auto-generated method stub
		
	}

	
	public ScreenInterface signal(BusMessage message) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * set true for blinking.
	 * @param value
	 * @return
	 */
	public void setForceRefresh(boolean value){
		this.forceRefresh = value;
	}
	
	/**
	 * used for blinking.
	 */
	public boolean forceRefresh(){
		return forceRefresh;
	}

}
