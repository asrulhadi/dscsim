/*
 * Created on 18.12.2007
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
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JEditBox;
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;
import net.sourceforge.dscsim.controller.display.screens.impl.JEditBoxInputScreen.MoveWhenComplete;
import net.sourceforge.dscsim.controller.message.types.Position;
import net.sourceforge.dscsim.controller.message.types.Latitude;
import net.sourceforge.dscsim.controller.message.types.Longitude;
import net.sourceforge.dscsim.controller.message.types.Time;
import net.sourceforge.dscsim.controller.screens.ActionMapping;
import net.sourceforge.dscsim.controller.screens.Screen;
import net.sourceforge.dscsim.controller.settings.DistressSettings;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.text.SimpleDateFormat;

public class JTimeEntryScreen extends JEditBoxInputScreen {

	private JEditBox ebHours = null;
	private JEditBox ebMinutes = null;
	private JTextBox ebTimeNull = null;

	
	public JTimeEntryScreen(JDisplay display, Screen screen) {
		super(display, screen);
		this.setSignalHandler(new MoveWhenComplete());



	}
	public void enter(Object msg) {
		super.enter(msg);
		this.ebHours = (JEditBox) this.getComponentByName("hours", 0);
		this.ebHours.setValidator(new JEditBox.TimeValidator()
		.setMode(JEditBox.TimeValidator.MODE.HH));
		this.setForceRefresh(true);

		this.ebMinutes = (JEditBox) this.getComponentByName("minutes", 0);	
		this.ebMinutes.setValidator(new JEditBox.TimeValidator()
		.setMode(JEditBox.TimeValidator.MODE.MM));

		this.ebTimeNull = (JTextBox) this.getComponentByName("timenull", 0);
		
		MultiContentManager oMngr = getInstanceContext().getContentManager();
		Time time = oMngr.getInfoStore().getPosition().getTime();
	
		if(time.isValid()){
			this.ebHours.setValue(time.hoursAsString());
			this.ebMinutes.setValue(time.minutesAsString());			
		}else{
			this.ebHours.setValue(BLANK);
			this.ebMinutes.setValue(BLANK);
			this.ebTimeNull.setText(NULL);
		}
		
		//position cursur.
		this.activeComponent = this.ebHours;
		if (this.ebHours.isComplete())
			this.activeComponent = this.ebHours;
		if (this.ebMinutes.isComplete())
			this.activeComponent = this.ebMinutes;

		this.activeComponent.setCursor(true);
						
		setNullFields();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -8480911484839447130L;
	
	@Override
	public boolean forceRefresh(){
		return true;
	}
	
	private void setNullFields(){
		
		if(ebHours.getValue().length()<1 
				&& ebMinutes.getValue().length()<1){
			this.ebTimeNull.setText("Null");			
		}else{
			this.ebTimeNull.setText("");
		}

	}
	
	@Override
	public ActionMapping notify(BusMessage msg) {
		ActionMapping ret = super.notify(msg);
		this.setNullFields();
		
		if(ret != null)
			return ret;
		
		String keyID = msg.getButtonEvent().getKeyId();
		String keyAction = msg.getButtonEvent().getAction();
		
		if (keyAction.equals(RELEASED) && KP_Aa.equals(keyID)) {

			MultiContentManager oMngr = getInstanceContext()
					.getContentManager();
			Properties props = oMngr.getProperties();

			this.ebHours.setValue(BLANK);
			this.ebMinutes.setValue(BLANK);
			this.ebTimeNull.setText(props.getProperty(NULL));

			this.activeComponent = this.ebHours;
			this.ebHours.setCursor(true);
			this.ebMinutes.setCursor(false);

			return null;
		}
		
		if(keyAction.equals(PRESSED) && FK_ENT.equals(keyID)) {
			if (this.ebHours.getValue().length()==0
					&& this.ebMinutes.getValue().length()==0) {
				return findActionMapping(keyAction, keyID);
			}
		}

		return null;

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {

		if (msg.getButtonEvent().getKeyId().equals(FK_ENT)) {
			MultiContentManager oMCmgr = getInstanceContext()
					.getContentManager();
			
			DistressSettings settings = oMCmgr.getInfoStore();
			Position position = settings.getPosition();
			
			Time time = position.getTime();
			
			if(NULL.compareToIgnoreCase(this.ebTimeNull.getText())== 0){
				time.inValidate();
			}else{
				time.setHours(this.ebHours.getValue());
				time.setMinutes(this.ebMinutes.getValue());
			}
			
			oMCmgr.persistInfoStore(settings);
		}

	}



}
