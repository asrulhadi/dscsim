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
 
package net.sourceforge.dscsim.controller.screen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.JFrame;

import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.network.DscIACManager;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.network.DscPosition;
import net.sourceforge.dscsim.controller.network.DscRadioTransmitter;
import net.sourceforge.dscsim.controller.network.MulticastTransmitter;
import net.sourceforge.dscsim.controller.screen.types.Latitude;
import net.sourceforge.dscsim.controller.screen.types.Longitude;
import net.sourceforge.dscsim.controller.screen.types.Time;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Attribute;
import org.jdom.Element;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class SendBaseScreen extends BeanScreen  {

	protected DscMessage _oMsg = new DscMessage();


	public SendBaseScreen(Element oScreenElement) {
		super(oScreenElement);
		
	}
	
	public SendBaseScreen(Element oScreenElement, net.sourceforge.dscsim.controller.MultiContentManager oMCMgr) {
		super(oScreenElement, oMCMgr);
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
		/*
		DscMessage oMsg = sendUndesignatedDistressAck();
			
		
		try{
			
			
			synchronized(oMsg){
				oMsg.wait(2*DscRadioTransmitter.DELAY_TIME);				
			}
		}catch(InterruptedException oEx){
			AppLogger.error(oEx);
		}
		*/
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
			synchronized(_oMsg){
				_oMsg.wait(2*DscRadioTransmitter.DELAY_TIME);				
			}
		
		}catch(InterruptedException oEx){
			AppLogger.error(oEx);
		}
		
	}
	protected void sendDistressAlert() {
		
		
		try{
			_oMsg.setCallType(CALL_TYPE_DISTRESS);
			_oMsg.setCatagory(CALL_CAT_DISTRESS);
			_oMsg.setChannel(CH_16);
			//_oMsg.setNature(CALL_NATURE_UNDESIGNATED);		
			sendDscMessage(_oMsg);
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
				
		
		
	}
	protected void sendDscMessage(DscMessage oMsg){
		
		oMsg.setFromMMSI(getInstanceContext().getContentManager().getMMSI());		
		//AppLogger.debug("SendBaseScreen.sendDscMessage ="+ oMsg.toString());
		//MulticastTransmitter.getInstance().transmit(oMsg);	
		DscIACManager.getTransmitter().transmit(oMsg);
		
	}
	
	protected boolean sendDcsMessageDyn()  {
	
		Element oElement = _oScreenElement.getChild("mappings");
		
		if(oElement == null)
			return false;
		
		DscMessage oMsg = new DscMessage();
		
		List oList = oElement.getChildren();
		
		Attribute oAttr = null;
		Element oMapping = null;
		String attrValue = null;
		for(int i=0; i<oList.size(); i++){
			
			oMapping = (Element)oList.get(i);
			
			attrValue = oMapping.getAttributeValue("screen");
			
			if(attrValue != null){
				
				ScreenContent oScreen = getScreen(attrValue);
				
				if(oScreen == null)
					continue;
						
				setMessageAttributeFromScreen(oMapping, oScreen, oMsg);
				
				continue;
				
			}
			
			attrValue = oMapping.getAttributeValue("constant");
			
			if(attrValue != null){
										
				setMessageAttributeFromConstants(oMapping, attrValue, oMsg);
				
				continue;
				
			}
					
			
		}
		
		sendDscMessage(oMsg);
		
		return true;
		
	}
	
	protected boolean setMessageAttributeFromConstants(Element oMapping, String constName, DscMessage oMsg){
		
		if(oMapping == null || constName == null || oMsg == null)
			return false;
		
	
		try {

			Field oFld = Constants.class.getDeclaredField(constName);
			
			Object value = oFld.get(this);
		
			//set method of object
			Class arrParams[] = {String.class};
			String methodName = oMapping.getAttributeValue("set");
			
			Method oSetMethod = oMsg.getClass().getMethod(methodName, arrParams);

			Object arrObjs[] = {value.toString()};
			
			oSetMethod.invoke(oMsg, arrObjs);
			
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}	
		
		
		
		return true;
		
	}
	protected void setMessageAttributeFromScreen(Element oMapping, ScreenContent oScreen, DscMessage oMsg){
		
		if(oMapping == null || oScreen == null || oMsg == null)
			return;
		
		//screen field name
		String fldName = oMapping.getAttributeValue("field");
		
		String setValue = oScreen.getSelectedValue(fldName);
		
		
		try {

			//set method of object
			Class arrParams[] = {String.class};
			String methodName = oMapping.getAttributeValue("set");
			
			Method oSetMethod = oMsg.getClass().getMethod(methodName, arrParams);

			Object arrObjs[] = {setValue};
			
			oSetMethod.invoke(oMsg, arrObjs);
			
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
			
		
		
	}
	

	protected ScreenContent getScreen(String srchName){
		

		List oList = getInstanceContext().getClu().getScreenCallStack();
		
		ScreenContent oRet = null;
		String strScreenName = null;
		for(int i=0; i<oList.size(); i++){
		
			oRet = (ScreenContent)oList.get(i);
			
			strScreenName = oRet.getAttributeValue("name");
			
			if(strScreenName != null && strScreenName.equals(srchName)){
				return oRet;
			}

		}
		
		return null;
		
	}
	
	protected void sendDscMessage() {
		List oList = getInstanceContext().getClu().getScreenCallStack();
		
		ScreenContent oScreen = null;
		String strScreenName;
		DscMessage oDscMessage = new DscMessage();
		EntryScreen oEntryScreen = null;
		for(int i=0; i<oList.size(); i++){
		
			oScreen = (ScreenContent)oList.get(i);
			
			
			strScreenName = oScreen.getAttributeValue("name");
			
			if(strScreenName.equals("channel_input")){
				oEntryScreen = (EntryScreen)oScreen;
				String strChannel = oEntryScreen.getEntryFieldValue("CHANNEL");
				oDscMessage.setChannel(strChannel);
				
			} else if(strScreenName.equals("select_category")){
				
				MenuScreen oMenuScreen = (MenuScreen)oScreen;

				MenuLine oMenuLine  = oMenuScreen.getActiveChoice();
				
				String strCatCode = oMenuLine.getAttributeValue("code");
				
				oDscMessage.setCatagory(strCatCode);
									
				
			} else if(strScreenName.equals("select_call_id")){
				
				
			} else if(strScreenName.equals("enter_address")){
				
				oEntryScreen = (EntryScreen)oScreen;
				String strToMMSI = oEntryScreen.getEntryFieldValue("MMSI");				
				oDscMessage.setToMMSI(strToMMSI);
	
			}
										
		}
		
		sendDscMessage(oDscMessage);
		
	}


}
