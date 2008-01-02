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
 * the Initial Developer are Copyright (C) 2006, 2007, 2008, 20010.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.controller;

import javax.sound.sampled.Clip;

import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.utils.AppletSoundList;



public class MultiBeeper implements Runnable, BusListener, Constants {
			
	
	public final static String BEEP_SIMPLE = RESOURCE_BASE + "beep_simple.wav";
	public final static String BEEP_ALARM = RESOURCE_BASE + "beep_alarm.wav";
	public final static String BEEP_DISTRESS_COUNTDOWN = RESOURCE_BASE + "beep_countdown.wav";
	public final static String BEEP_TRANSMITTING = RESOURCE_BASE + "beep_transmitting.wav";

	private Object _oBeeperSemaphor = new Object();
	private String _strCurrentNextTone = null;
	private String _strCurrentTone = null;
	private boolean _powerOn = false;
	private InstanceContext _ctx = null;

	private MultiBeeper(InstanceContext ctx){
		_ctx = ctx;
		init();
	}

	private void init() {
		
		
		//Thread oThread = new Thread(this);
		//oThread.start();
		
		AppletSoundList oSoundList = AppletSoundList.getInstance();
		
		oSoundList.load(BEEP_SIMPLE);
		oSoundList.load(BEEP_ALARM);
		oSoundList.load(BEEP_DISTRESS_COUNTDOWN);
		oSoundList.load(BEEP_TRANSMITTING);
				
		Thread oThread = new Thread(this);
		oThread.setName("Controller.MultiBeeper");
		oThread.start();
				
	}
	
	
	
	public static MultiBeeper getInstance(InstanceContext ctx){		
		return new MultiBeeper(ctx);		
	}

	public void run() {
		// TODO Auto-generated method stub
		
		boolean bContinue = true;
		while(bContinue){
			_strCurrentTone = null;

			try {
				synchronized (_oBeeperSemaphor){
					
					if(_strCurrentNextTone == null){
						_oBeeperSemaphor.wait();
					} 					
				}
							
				synchronized (_oBeeperSemaphor){
					
					if(_strCurrentNextTone != null){
						_strCurrentTone = _strCurrentNextTone;
		
					}else{
						continue;
					}
				}			
				
			}catch(InterruptedException oEx){
				AppLogger.error(oEx);
				bContinue = false;
				continue;
			}
			
			for(int i = 0; _strCurrentTone != null && i< 1 ;i++){
				beepSync(_strCurrentTone);
				try{
					Thread.sleep(500);
				}catch(InterruptedException oEx){
					AppLogger.error(oEx);
					bContinue = false;
					continue;
				}
			}						
		}
		
	}

	/* (non-Javadoc)
	 * @see applet.BusListener#signal(applet.BusMessage)
	 */
	public void signal(BusMessage oMessage) {
		// TODO Auto-generated method stub
		
		//this could be configured in xml mappings
		String msgType = oMessage.getType();
		if(_powerOn && msgType.equals(BusMessage.MSGTYPE_NETWORK) == true){
			
			DscMessage oDscMessage = (DscMessage)oMessage.getDscMessage();
			
			if(CALL_CAT_DISTRESS.equals(oDscMessage.getCatagory())){
				soundIncomingDistressAlarm();
			} else {
				beepSync(BEEP_TRANSMITTING);
			}
						
		} else if(_powerOn 
				&& BusMessage.MSGTYPE_KEY.equals(msgType)){
			Button oBut = oMessage.getButtonEvent();
			String keyId = oBut.getKeyId();
			String action = oBut.getAction();
			if(FK_CLR.equals(keyId) && action.equals(PRESSED)
				/*|| FK_ENT.equals(keyId) && action.equals(PRESSED)*/) {
				clearBeeper();
			} 
			
			if(oBut.getAction().equals(PRESSED)){	
				beepSync(BEEP_SIMPLE);				
			}
			
			/*turn off*/
			if(DSC_POWERED_OFF.equals(keyId)){
					_powerOn = false;
					clearBeeper();
			}

		} 
		
		if(!_powerOn && BusMessage.MSGTYPE_KEY.equals(msgType)){
			Button oBut = oMessage.getButtonEvent();
			String keyId = oBut.getKeyId();			
		    if(DSC_POWERED_ON.equals(keyId)){
		    	_powerOn = true;
		    }
		}		
		
	}
	
	/**
	 * stop the current beeping
	 *
	 */
	public void clearBeeper(){
		try {
			synchronized (_oBeeperSemaphor){
				
				_strCurrentNextTone = null;

				_oBeeperSemaphor.notify();
			}
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
			
	}
	
	public void soundIncomingDistressAlarm(){
		if(!_ctx.getClu().isDistressCallInprogress()){
			beepAysnc(BEEP_ALARM);
		}
	}
	
	public void beepAysnc(String strBeepId){
		
		if(BEEP_ALARM.equals(strBeepId) &&
				BEEP_SIMPLE.equals(strBeepId)){
			return;
		}
		
		try {
			synchronized (_oBeeperSemaphor){
				
				_strCurrentNextTone = strBeepId;

				_oBeeperSemaphor.notify();
			}
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
		
		
	}
	
	public void beepSync(String strBeepId){
	
		try{
			AppletSoundList oSoundList = AppletSoundList.getInstance();
			Clip oClip = oSoundList.getRealClip(strBeepId);
			oClip.setMicrosecondPosition(0);
			oClip.start();
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
		
	}
	

	
}
