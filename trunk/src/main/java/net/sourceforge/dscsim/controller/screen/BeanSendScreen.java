/*
 * Created on 11.07.2006
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

import net.sourceforge.dscsim.controller.MultiBeeper;
import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.RadioCoreController;
import net.sourceforge.dscsim.controller.network.DscIACManager;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.utils.AppLogger;
//import network.MulticastTransmitter;
//import network.YIMSGReceiverTransmitter;

import org.jdom.Element;


/**
 * Screen used to send message. User is pressed with a choice to proceed or abort. The signal
 * method will react accordingly.
 * @author katharina
 */
public class BeanSendScreen extends BeanScreen {

	/**
	 * Add to make simulation more realistic. Without a delay the controller reacts too quickly.
	 */
	private static long SLEEP_AFTER_SEND = 1500;
	/**
	 * @param oScreenElement
	 */
	public BeanSendScreen(Element oScreenElement) {
		this(oScreenElement, null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param oScreenElement
	 * @param oCMngr
	 */
	public BeanSendScreen(Element oScreenElement, MultiContentManager oCMngr) {
		super(oScreenElement, oCMngr);
		// TODO Auto-generated constructor stub
				
		int firstLine = getHeaderCount();
		_currLine = firstLine+1;
		_minLine = firstLine;
		_maxLine = firstLine; 

		//init();
		
	}
	
	/**
	 * initialize screen line and fields.
	 */
    public void init() {
	   	super.init();
	   	setCursonOn(false);  
	   	_currLine = getHeaderCount();
    }
    
    /**
     * return the active line on the screen
     * @return int for current line on screen.
     */
	public int getActiveLine() {		
		int t = super.getActiveLine();		
		AppLogger.debug("BeanSendScreen.getActiveLine=" + t);
		return t;
	}
	/**
	 * handle outgoing messages and channel switching logic. 
	 */
	public ScreenContent signal(BusMessage oMessage) {
		
		
		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		if(keyAction.equals(RELEASED)){
			return this;
		}
		
		if(FK_CLR.equals(oMessage.getButtonEvent().getKeyId())) {
			
			String event = getAttributeValue("event");
			
			if(event != null && keyID.equals(event)){
				String screenName = this.getAttributeValue("link");					
				ScreenContent oScreenNext = getInstanceContext().getContentManager().getScreenContent(screenName, getInstanceContext());
				oScreenNext.setParent(this);
				oScreenNext.setOutGoingDscMessage(getOutGoingDscMessage());
				oScreenNext.setIncomingDscMessage(getIncomingDscMessage());
				oScreenNext.setInstanceContext(getInstanceContext());
				return oScreenNext;
	 		}
			
			return null;
		}
	
		if(FK_ENT.equals(keyID)){
			
			Element oContext = _oScreenElement.getChild("context");
			DscMessage oMsg = null;
			
			if(oContext != null && oContext.getAttributeValue("name") != null)
			{
				String strContextName = oContext.getAttributeValue("name");	
				oMsg = (DscMessage)getInstanceContext().getProperty(strContextName);	
			} else {
				oMsg = getOutGoingDscMessage();
				oMsg.setToMMSI(getIncomingDscMessage().getFromMMSI());
			}
			
	
			/*if individual call, then wait for ack before changing channel*/
			DscMessage oInbound = getIncomingDscMessage();
			if(oInbound != null && oInbound.getCallType().equals(CALL_TYPE_INDIVIDUAL)){
				if(oInbound.getChannel().equals(oMsg.getChannel())==false){
					oMsg.setCallType(CALL_TYPE_INDIVIDUAL);
				}else{
					/*set radio to agree upon channel*/
					RadioCoreController oRadio = getInstanceContext().getRadioCoreController();
					oRadio.setChannel(oMsg.getChannel());
					oMsg.setCallType(CALL_TYPE_INDIVIDUAL_ACK);
				}
				
			}
			/*if outgoing is one of the following, then switch the channel immediately as there 
			 * will be no acks.*/
			if(CALL_TYPE_ALL_SHIPS.equals(oMsg.getCallType())
					|| CALL_TYPE_GROUP.equals(oMsg.getCallType())){
				RadioCoreController oRadio = getInstanceContext().getRadioCoreController();
				oRadio.setChannel(oMsg.getChannel());				
			}

			/*make sure outgoing message has my mmsi*/
			oMsg.setFromMMSI(getInstanceContext().getContentManager().getMMSI());
			getInstanceContext().getBeeper().beepSync(MultiBeeper.BEEP_TRANSMITTING);
			AppLogger.debug("BeanSendScreen.signal ="+ oMsg.toString());			
			DscIACManager.getTransmitter().transmit(oMsg);
						
			//for affect and as well a yield to Transmitter.
			try{
				Thread.sleep(SLEEP_AFTER_SEND);
			}catch(Exception oEx)
			{
				AppLogger.error(oEx);
			}
			InstanceContext oInstanceContext = getInstanceContext();
			
			oInstanceContext.removeProperties();
			
			return oInstanceContext.getContentManager().getScreenContent("welcome", oInstanceContext);
			
		}

		return null;
	}

}
