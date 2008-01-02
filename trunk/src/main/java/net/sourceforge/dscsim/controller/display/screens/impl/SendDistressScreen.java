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
 
package net.sourceforge.dscsim.controller.display.screens.impl;

import java.awt.Component;
import java.util.Properties;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.MultiBeeper;
import net.sourceforge.dscsim.controller.MultiBus;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreenComponent;
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;
import net.sourceforge.dscsim.controller.infostore.Position;
import net.sourceforge.dscsim.controller.infostore.Position.LatitudeType;
import net.sourceforge.dscsim.controller.infostore.Position.LongitudeType;
import net.sourceforge.dscsim.controller.infostore.Position.TimeType;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.network.DscPosition;
import net.sourceforge.dscsim.controller.panels.ActionMapping;
import net.sourceforge.dscsim.controller.panels.Screen;
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

	private int sentAlerts =0;
	private  boolean bContinue = true;
	private ActionScreen screenWaiting = null;
	private String lstChannel = null;
	
	private JTextBox tbNature = null;
	private JTextBox tbSource = null;	
	private JTextBox tbTime = null;
	private JTextBox tbLat = null;
	private JTextBox tbLon = null;


	public SendDistressScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}
	
	/**
	 * isContinue - should distress transmission continue
	 */
	private synchronized boolean isContinue(){
		AppLogger.debug2("SendDistressScreen.isContinuu bContinue=" + bContinue);		
		return bContinue;
	}
	
	/**
	 * isContinue - stop distress transmission completely
	 */
	private synchronized void stop(){
		AppLogger.debug2("SendDistressScreen.stop and notify");
		bContinue = false;
		notify();
	}
	/**
	 * waitFor - see if should continue and wait for given period.
	 * @param duration in ms.
	 * @throws InterruptedException
	 */
	private synchronized void waitFor(long duration) throws InterruptedException{
		AppLogger.debug2("SendDistressScreen.waitFor=" + duration + " bContinue=" + bContinue);

		if(bContinue != true)
			return;
		
		wait(duration);
		
		AppLogger.debug2("SendDistressScreen.waitFor wokenup  bContinue=" + bContinue);

	}
	/**
	 * setWaitingScreen - show distess transmission in progress.
	 */
	private synchronized void setWaitingScreen(ActionScreen oScreen){
		screenWaiting = oScreen;
	}
	
	/**
	 * signal - GUI and airwave incoming event handler.
	 */
	public ActionMapping notify(BusMessage oMessage) {
						
		String msgType = oMessage.getType();
		
		if(BusMessage.MSGTYPE_KEY.equals(msgType)){
					
			String keyId = oMessage.getButtonEvent().getKeyId();
			String subCode = oMessage.getButtonEvent().getAction();
				
			if(FK_SOS.equals(keyId)
					&& RELEASED.equals(subCode) &&
					sentAlerts < 1){
				
				
				stop();
				/*
				synchronized(_semaphore){
					bContinue = false;
					_semaphore.notify();	
				}
				*/			
	
				/*set radio back to original channel*/
				if(lstChannel != null)
					getInstanceContext().getRadioCoreController().setChannel(lstChannel);
	
				return this.findActionMapping("PRSD", "FK_CLR");
				
			} else if(FK_CLR.equals(keyId) && isContinue()){
						
				AppLogger.debug2("signal CLR before bContinue =" + bContinue);
				
				stop();
				/*
				synchronized(_semaphore){
					bContinue = false;
					_semaphore.notify();				
				}
				*/
				//AppLogger.debug2("signal - CLR after bContinue =" + bContinue);			
				//transmit acknowlege.
				AppLogger.debug2("calling sendUndesignatedDistressAck");
				getInstanceContext().getBeeper().beepSync(MultiBeeper.BEEP_TRANSMITTING);
				sendSyncUndesignatedDistressAck(lstChannel);			
				
				//clear everything off the screen stack.			
				getInstanceContext().getBus().publish(new BusMessage(null, BTN_RESET));
	
				/*that was it.*/
				return new ActionScreen.JActionMapping("", "", NULL);
				
			} else if(DSC_POWERED_OFF.equals(keyId)){			
				stop();				
				return null;
			} else{
				return new ActionScreen.JActionMapping("", "", NULL);
			}
			
		}
		
		return this.findActionMapping(PRESSED, FK_SOS);
	}
	@Override
	public void enter(Object msg){

		this.tbNature = (JTextBox) this.getComponentByName("nature", 0);
		this.tbSource = (JTextBox) this.getComponentByName("source", 0);
		this.tbTime = (JTextBox) this.getComponentByName("time", 0);
		this.tbLat = (JTextBox) this.getComponentByName("latitude", 0);
		this.tbLon = (JTextBox) this.getComponentByName("longitude", 0);
		
		MultiContentManager oMngr = getInstanceContext().getContentManager();		
		Properties props = oMngr.getProperties();		
		this.tbSource.setText(props.getProperty("MS_SOURCE_GPS"));

		String nature = oMngr.getInfoStore().getNature();
		this.tbNature.setText(props.getProperty(nature));
		
		Position pos = oMngr.getInfoStore().getPosition();
		TimeType time = pos.getTime();
		
		if(time.getHours().length()<1){
			this.tbTime.setText(props.getProperty("MS_TIME_NON"));
		} else{
			this.tbTime.setText(props.getProperty("MS_TIME_PREF") + time.getHours() + ":" + time.getMinutes());
		}
		
		LatitudeType lat = pos.getLatitude();
		LongitudeType lon = pos.getLongitude();
		
		if (lat.getDegrees().length() < 1) {
			this.tbLat.setText(props.getProperty("MS_POS_NON"));
			this.tbLon.setText("");
		} else {
			this.tbLat.setText(props.getProperty("MS_LAT_PREF")
					+ lat.getDegrees() + props.getProperty("DEGREE_SYMBOL")
					+ lat.getMinutes() + props.getProperty("MINUTE_SYMBOL")
					+ lat.getHemisphere());
			this.tbLon.setText(props.getProperty("MS_LON_PREF")
					+ lon.getDegrees() + props.getProperty("DEGREE_SYMBOL")
					+ lon.getMinutes() + props.getProperty("MINUTE_SYMBOL")
					+ lon.getHemisphere());

		}
		
		this.init();
	}

	public void exit(BusMessage oMessage) throws Exception {
			
		if(isContinue()){
			stop();	
		}		
			
	}
	public void init()  {
				
		super.init();
		
		/*record current channel before setting to 70*/
		lstChannel = getInstanceContext().getRadioCoreController().getChannel();
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
				
		AppLogger.debug2("SendDistressScreen broadCast starting - bContinue=" + bContinue);
		
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
		AppLogger.debug2("SendDistressScreen broadCast bContinue=" + bContinue);
		if(!isContinue())
			return;
		
		AppLogger.debug2("SendDistressScreen broadCast final beep");
		InstanceContext  oContext =  getInstanceContext();
		MultiBeeper oBeeper = oContext.getBeeper();

		MultiBus.getInstance().publish(new BusMessage(this, BTN_RESET));
		
		oBeeper.beepSync(MultiBeeper.BEEP_TRANSMITTING);
				
		AppLogger.debug2("SendDistressScreen broadCast sending  sentAlerts=" + sentAlerts);
		sentAlerts++;
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

		//AppLogger.debug2("SendDistressScreen run after broadCast bContinue" + bContinue);
		/*check that a CLR didn't cancel distress call*/
		if(!isContinue()){
			AppLogger.debug2("SendDistressScreen run exiting after first broadCast");
			return;
		}
					
		long lastSentTime = System.currentTimeMillis();
		
		//now every five 5 minutes resend signal
		long currTime = 0;
		long interVal = 300000; //5 minutes		
//		long interVal = 60000; //1 minutes
		
		ActionScreen waiting  = (ActionScreen)getInstanceContext().getContentManager().getScreenContent("distress_call_sent", getInstanceContext());		
		setWaitingScreen(waiting);

		//swap screen contents.
		synchronized (this){
			this.removeAll();
			Component comps[] = waiting.getComponents();
			for(Component c: comps){
				this.add((JScreenComponent)c);
			}			
		}



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
						
			AppLogger.debug2("SendDistressScreen - run message sofar sent=" + sentAlerts);
				
		}
		
		AppLogger.debug2("SendDistressScreen run exiting.");
			
	}
	
	public boolean forceRefresh() {		
		if(screenWaiting != null)
			return true;
		else
			return false;	
	}
	
	
}

