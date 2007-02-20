/*
 * Created on 29.06.2006
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

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Button;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.MultiBeeper;
import net.sourceforge.dscsim.controller.MultiBus;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.network.DscPosition;
import net.sourceforge.dscsim.controller.screen.types.DscString;
import net.sourceforge.dscsim.controller.screen.types.Latitude;
import net.sourceforge.dscsim.controller.screen.types.Longitude;
import net.sourceforge.dscsim.controller.screen.types.Time;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import org.jdom.Element;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SendDistressScreen extends SendBaseScreen implements Runnable {


	private int _sentAlerts =0;
	private  boolean _bContinue = true;
	//private Object _semaphore = new Object();
	private ScreenContent _screenWaiting = null;
	private String _lstChannel = null;
	
	
	/**
	 * Standard ctor
	 */
	public SendDistressScreen(Element oScreenElement, MultiContentManager oCMgr) {
		super(oScreenElement, oCMgr);

	}
	/**
	 * isContinue - should distress transmission continue
	 */
	private synchronized boolean isContinue(){
		AppLogger.debug2("SendDistressScreen.isContinuu _bContinue=" + _bContinue);		
		return _bContinue;
	}
	
	/**
	 * isContinue - stop distress transmission completely
	 */
	private synchronized void stop(){
		AppLogger.debug2("SendDistressScreen.stop and notify");
		_bContinue = false;
		notify();
	}
	/**
	 * waitFor - see if should continue and wait for given period.
	 * @param duration in ms.
	 * @throws InterruptedException
	 */
	private synchronized void waitFor(long duration) throws InterruptedException{
		AppLogger.debug2("SendDistressScreen.waitFor=" + duration + " _bContinue=" + _bContinue);

		if(_bContinue != true)
			return;
		
		wait(duration);
		
		AppLogger.debug2("SendDistressScreen.waitFor wokenup  _bContinue=" + _bContinue);

	}
	/**
	 * setWaitingScreen - show distess transmission in progress.
	 */
	private synchronized void setWaitingScreen(ScreenContent oScreen){
		_screenWaiting = oScreen;
	}
	
	/**
	 * signal - GUI and airwave incoming event handler.
	 */
	public ScreenContent signal(BusMessage oMessage) {
						
		String msgType = oMessage.getType();
		
		if(BusMessage.MSGTYPE_KEY.equals(msgType)){
					
			String keyId = oMessage.getButtonEvent().getKeyId();
			String subCode = oMessage.getButtonEvent().getAction();
				
			if(FK_SOS.equals(keyId)
					&& RELEASED.equals(subCode) &&
					_sentAlerts < 1){
				
				
				stop();
				/*
				synchronized(_semaphore){
					_bContinue = false;
					_semaphore.notify();	
				}
				*/			
	
				/*set radio back to original channel*/
				if(_lstChannel != null)
					getInstanceContext().getRadioCoreController().setChannel(_lstChannel);
	
				return null;
				
			} else if(FK_CLR.equals(keyId) && isContinue()){
						
				AppLogger.debug2("signal CLR before _bContinue =" + _bContinue);
				
				stop();
				/*
				synchronized(_semaphore){
					_bContinue = false;
					_semaphore.notify();				
				}
				*/
				//AppLogger.debug2("signal - CLR after _bContinue =" + _bContinue);			
				//transmit acknowlege.
				AppLogger.debug2("calling sendUndesignatedDistressAck");
				getInstanceContext().getBeeper().beepSync(MultiBeeper.BEEP_TRANSMITTING);
				sendSyncUndesignatedDistressAck(_lstChannel);			
				
				//clear everything off the screen stack.			
				getInstanceContext().getBus().publish(new BusMessage(null, BTN_RESET));
	
				/*that was it.*/
				return null;
			} else if(DSC_POWERED_OFF.equals(keyId)){			
				stop();				
				return null;
			}
			
		}
		
		return this;
	}

	public void exit(BusMessage oMessage) throws Exception {
			
		if(isContinue()){
			stop();	
		}		
		//reset call nature to undesignated.
		DscString nature = (DscString)getInstanceContext().getContentManager().getSetting("Nature");
		
		nature.setCode("NATURE_DISTR_UNDESIG");
		
		getInstanceContext().getContentManager().replaceProperty("Nature", nature);
		
		getInstanceContext().getContentManager().saveProperties();	
		
	}
	public void init()  {
				
		super.init();
		
		Element ctxElement = getInstanceContext().getContentManager().getContextElement("distress_call");
		
		try {
					
			MultiContentManager.removeContextProperty(getInstanceContext(), "distress_call");
			
			_oMsg = (DscMessage)MultiContentManager.getContextPropertyEx(getInstanceContext(), "distress_call");
			
			Latitude lat = (Latitude)getInstanceContext().getContentManager().getSetting("Latitude");					
			Longitude longitude = (Longitude)getInstanceContext().getContentManager().getSetting("Longitude");
			DscPosition oPos = new DscPosition(lat,longitude);
			_oMsg.setPosition(oPos);
			
			Time time = (Time)getInstanceContext().getContentManager().getSetting("Time");
			_oMsg.setTime(time);
									
			DscString nature = (DscString)getInstanceContext().getContentManager().getSetting("Nature");
			_oMsg.setNature(nature.toString());
		
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
		
		_currLine = getHeaderCount();
		
		setCursonOn(false);
		
		/*record current channel before setting to 70*/
		_lstChannel = getInstanceContext().getRadioCoreController().getChannel();
		getInstanceContext().getRadioCoreController().setChannel(CH_70);
		
		Thread worker = new Thread(this);
		worker.setName("Controller.SendDistressScreen");
		worker.start();
	}

	/*
	 * broadCast - 5 second countdown and message transmission.
	 * if the Distress button is released before the first transmit then abort distress call completely.
	 */
	private void broadCast(){
				
		AppLogger.debug2("SendDistressScreen broadCast starting - _bContinue=" + _bContinue);
		
		long startTime = System.currentTimeMillis();
		long currentTime = 0, waitTime =0;
		/*the 5 second countdown loop before sending*/
		while(isContinue()) {
			
			currentTime = System.currentTimeMillis();
			
			if(currentTime - startTime > 5000) {
				//5 seconds done.
				break;
			}

			AppLogger.debug2("SendDistressScreen broadCast beeping");
			
			InstanceContext  oContext =  getInstanceContext();
			MultiBeeper oBeeper = oContext.getBeeper();
			
			/*short beep*/
			oBeeper.beepSync(MultiBeeper.BEEP_DISTRESS_COUNTDOWN);

			//should at max 1sec including beep time. 
			//if beep takes longer than one sec they don't wait.
			waitTime = 1000 - (System.currentTimeMillis() - currentTime);
			waitTime = waitTime <= 0 ? 0 : waitTime;
			AppLogger.debug2("SendDistressScreen broadCast waitTime" + waitTime);
	
			if(waitTime > 0){
				try {										
					waitFor(waitTime);
					/*
					synchronized(_semaphore){
						_semaphore.wait(waitTime);		
						AppLogger.debug2("SendDistressScreen broadCast wokenafter " + waitTime);	
					}
					*/						
				}catch(InterruptedException ex) {
					AppLogger.error(ex);
				}
				
			}
	
		}

		/*check to see that CLR wasn't pushed while this thread was waiting*/
		AppLogger.debug2("SendDistressScreen broadCast _bContinue=" + _bContinue);
		if(!isContinue())
			return;
		
		AppLogger.debug2("SendDistressScreen broadCast final beep");
		InstanceContext  oContext =  getInstanceContext();
		MultiBeeper oBeeper = oContext.getBeeper();

		MultiBus.getInstance().publish(new BusMessage(this, BTN_RESET));
		
		oBeeper.beepSync(MultiBeeper.BEEP_TRANSMITTING);
				
		AppLogger.debug2("SendDistressScreen broadCast sending  _sentAlerts=" + _sentAlerts);
		_sentAlerts++;
		sendSyncDistressAlert();			
		getInstanceContext().getRadioCoreController().setChannel(CH_16);		

		AppLogger.debug2("SendDistressScreen broadCast ending");
		
		/*5 second countdown and message sent completed*/
		return;

	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		AppLogger.debug2("SendDistressScreen run starting");
			
		//first do countdown and initial send.
		broadCast();

		//AppLogger.debug2("SendDistressScreen run after broadCast _bContinue" + _bContinue);
		/*check that a CLR didn't cancel distress call*/
		if(!isContinue()){
			AppLogger.debug2("SendDistressScreen run exiting after first broadCast");
			return;
		}
					
		long lastSentTime = System.currentTimeMillis();
		
		//now every five 5 minutes resend signal
		long currTime = 0;
		//long interVal = 300000; //5 minutes		
		long interVal = 60000; //1 minutes
		
		ScreenContent oScreen  = getInstanceContext().getContentManager().getScreenContent("distress_call_sent", getInstanceContext());
		
		_oLines = oScreen.getLines();
		oScreen.init();
		oScreen.setCursonOn(false);
		oScreen._currLine = oScreen.getHeaderCount();
		
		setWaitingScreen(oScreen);
		//getInstanceContext().getController().setScreenContent(_screenWaiting);

		while(isContinue()){						
			currTime = System.currentTimeMillis();
			long waitTime = interVal - (currTime- lastSentTime) + 1000;
			waitTime = waitTime < 0 ? 0 : waitTime;

			if(waitTime > 0){
				try {					
					waitFor(waitTime);					
					/*
					synchronized(_semaphore){
						_semaphore.wait(waitTime);		
						AppLogger.debug2("SendDistressScreen - run is wokenup after =" + waitTime);
					}
					*/
				}catch(InterruptedException oEx) {
					AppLogger.error(oEx);
				}
				
			}
			
			/*may have */
			currTime = System.currentTimeMillis(); 
			if(currTime - lastSentTime > interVal){
				broadCast();
				lastSentTime = System.currentTimeMillis();
			}
						
			AppLogger.debug2("SendDistressScreen - run message sofar sent=" + _sentAlerts);
				
		}
		
		AppLogger.debug2("SendDistressScreen run exiting.");
			
	}
	
	public ScreenLineList getLines(){
		
		if(_screenWaiting != null)
			return _screenWaiting._oLines;
		else
			return this._oLines;
	}
	
	public boolean forceRefresh() {
		
		if(_screenWaiting != null)
			return true;
		else
			return false;
		
	}
	
	public int getActiveLine() {
		if(_screenWaiting != null)
			return _screenWaiting._currLine;
		else
			return _currLine;
		
	}

	public int getHeaderCount(){
		if(_screenWaiting != null)
			return _screenWaiting.getHeaderCount();
		else
			return super.getHeaderCount();
	}
	
	public int getFooterCount(){
		if(_screenWaiting != null)
			return _screenWaiting.getFooterCount();
		else
			return super.getFooterCount();
	}
	public int getDisplayedFrom(){
		if(_screenWaiting != null)
			return _screenWaiting.getDisplayedFrom();
		else
			return super.getDisplayedFrom();
	}
	
	public int getDisplayedTo(){
		if(_screenWaiting != null)
			return _screenWaiting.getDisplayedTo();
		else
			return super.getDisplayedTo();
	}
	
	public void setDisplayedTo(int to){
		if(_screenWaiting != null)
			_screenWaiting.setDisplayedTo(to);
		else
			super.setDisplayedTo(to);
	}
	
	public void setDisplayedFrom(int to){
		if(_screenWaiting != null)
			_screenWaiting.setDisplayedFrom(to);
		else
			super.setDisplayedFrom(to);
	}
	
}

