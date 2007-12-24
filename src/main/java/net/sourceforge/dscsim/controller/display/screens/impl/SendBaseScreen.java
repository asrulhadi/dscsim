/*
 * Created on 29.07.2006
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

import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.infostore.InfoStoreType;
import net.sourceforge.dscsim.controller.infostore.Position;
import net.sourceforge.dscsim.controller.infostore.Position.LatitudeType;
import net.sourceforge.dscsim.controller.infostore.Position.LongitudeType;
import net.sourceforge.dscsim.controller.infostore.Position.TimeType;
import net.sourceforge.dscsim.controller.network.DscIACManager;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.network.DscPosition;
import net.sourceforge.dscsim.controller.screen.types.Latitude;
import net.sourceforge.dscsim.controller.screen.types.Longitude;
import net.sourceforge.dscsim.controller.network.DscRadioTransmitter;
import net.sourceforge.dscsim.controller.panels.Screen;
import net.sourceforge.dscsim.controller.screen.types.Latitude;
import net.sourceforge.dscsim.controller.screen.types.Longitude;
import net.sourceforge.dscsim.controller.screen.types.Time;
import net.sourceforge.dscsim.controller.utils.AppLogger;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class SendBaseScreen extends ActionScreen  {

	private DscMessage message = new DscMessage();

	public SendBaseScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}
	
	/**
	 * sendSyncUndesignatedDistressAck - send and wait for notification that message has been sent.
	 * @param channel - value to set radio 2 after transmission is done.
	 **/
	protected void sendSyncUndesignatedDistressAck(String channel){
		
		final DscMessage oMsg = sendUndesignatedDistressAck();
		final String chann = channel;

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				try{					
					synchronized(oMsg){
						oMsg.wait(2*DscRadioTransmitter.DELAY_TIME);				
					}					
					if(chann != null)
						getInstanceContext().getRadioCoreController().setChannel(chann);

				}catch(InterruptedException oEx){
					AppLogger.error(oEx);
				}		
			}
		});

	}
	
	protected DscMessage sendUndesignatedDistressAck(){
	
		DscMessage oMsg = new DscMessage();

		try{
			Latitude lat = (Latitude)getInstanceContext().getContentManager().getSetting("Latitude");			
			Longitude longitude = (Longitude)getInstanceContext().getContentManager().getSetting("Longitude");				
			DscPosition oPos = new DscPosition(lat,longitude);			
			oMsg.setPosition(oPos);			
			Time time = (Time)getInstanceContext().getContentManager().getSetting("Time");		
			oMsg.setTime(time);			
			oMsg.setCatagory(CALL_CAT_DISTRESS);
			oMsg.setCallType(CALL_TYPE_DISTRESS_ACK);
			oMsg.setNature(CALL_NATURE_UNDESIGNATED);			
			oMsg.setChannel(CH_16);
			sendDscMessage(oMsg);	
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}

		return oMsg;
		
	}
	
	protected void sendSyncDistressAlert() {
		
		sendDistressAlert();
		try{
			synchronized(this.message){
				this.message.wait(2*DscRadioTransmitter.DELAY_TIME);				
			}
		
		}catch(InterruptedException oEx){
			AppLogger.error(oEx);
		}
		
	}
	protected void sendDistressAlert() {
		
		
		try{
			this.message.setCallType(CALL_TYPE_DISTRESS);
			this.message.setCatagory(CALL_CAT_DISTRESS);
			this.message.setChannel(CH_16);		
			
			InfoStoreType store = this.getInstanceContext().getContentManager().getInfoStore();
			Position pos = store.getPosition();
			LatitudeType lat = pos.getLatitude();
			LongitudeType lon = pos.getLongitude();
			TimeType time = pos.getTime();
			
			this.message.setDistressLatitude(new Latitude(lat.getDegrees(), lat.getMinutes(), lat.getHemisphere()));
			this.message.setDistressLongitude(new Longitude(lon.getDegrees(), lon.getMinutes(), lon.getHemisphere()));
			this.message.setTime(new Time(time.getHours(), time.getMinutes()));
			this.message.setNature(store.getNature());
			
			sendDscMessage(this.message);
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
				
		
		
	}
	protected void sendDscMessage(DscMessage msg){
		//AppLogger.debug("SendBaseScreen.sendDscMessage ="+ oMsg.toString());		
		msg.setFromMMSI(getInstanceContext().getContentManager().getMMSI());		
		DscIACManager.getTransmitter().transmit(msg);		
	}

	public DscMessage getMessage() {
		return message;
	}
	
}
