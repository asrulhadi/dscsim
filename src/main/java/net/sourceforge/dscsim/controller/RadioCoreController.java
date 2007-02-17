/*
 * Created on 07.01.2007
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
 
package net.sourceforge.dscsim.controller;

import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.radio.core.RadioCore;
import net.sourceforge.dscsim.radio.core.RadioEventListener;
import net.sourceforge.dscsim.radio.core.VHFChannel;
import net.sourceforge.dscsim.radiotransport.Antenna;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RadioCoreController implements Constants, BusListener, RadioEventListener {

	 /*last incoming acknowlegement observed*/
	private DscMessage _lastAck = null;
	private RadioCore _radioCore = null;
	private InstanceContext _instCtx = null;
	private boolean _masterSwitchOn = false;
	
	public RadioCoreController(RadioCore radioCore, InstanceContext instCtx){
		//AppLogger.debug2("RadioCoreController.ctr" + radioCore);
		_instCtx = instCtx;
		_radioCore = radioCore;
		_radioCore.registerListener(this);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public void signal(BusMessage oMessage) {
				
		String busType = oMessage.getType();

		AppLogger.debug2("RadioCoreController.signal - message type:" + oMessage.getType());

		/*first incoming ack must be obeserved*/
		if(busType.equals(BusMessage.MSGTYPE_NETWORK)){			
			DscMessage dscMsg = oMessage.getDscMessage();	
				
			AppLogger.debug2("RadioCoreController.signal - dscmsg type:" + dscMsg.getCallType());
			AppLogger.debug2("RadioCoreController.signal - isAck:" + dscMsg.isCallAcknowledgement());
			
			String callType = dscMsg.getCallType(); 
			if(dscMsg.isCallAcknowledgement()
				|| callType.equals(CALL_TYPE_ALL_SHIPS)
				|| callType.equals(CALL_TYPE_GROUP)){
				_lastAck = dscMsg;
			}else{
				_lastAck = null;
			}
			
		/*then an enter release must follow*/
		} else if(busType.equals(BusMessage.MSGTYPE_KEY)){
			AppLogger.debug2("RadioCoreController.signal - busType" + busType);
			
			String keyID = oMessage.getButtonEvent().getKeyId();
			String keyAction = oMessage.getButtonEvent().getAction();
			if( _lastAck != null
				&& _radioCore != null
				&& PRESSED.equals(keyAction) 
				&& FK_ENT.equals(keyID)){

				AppLogger.debug2("RadioCoreController.signal - setting channel to:" + _lastAck.getChannel());
				
				setChannel(_lastAck.getChannel());				
				_lastAck = null;
			}
			
			_lastAck = null;
		}
				
	}
	
	public void setRadioCore(RadioCore radioCore){
		_radioCore = radioCore;
	}
		
	public synchronized void setChannel(String channel){		
		
		try{
			VHFChannel vhfChan = VHFChannel.getByName(channel);
			AppLogger.debug2("RadioCoreController.setChannel - setting channel to:" + vhfChan.getName());
			_radioCore.setChannel(vhfChan);				
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
					
	}

	public synchronized String getChannel(){
		
		String channel = null;;
		
		try{
			channel =  _radioCore.getChannel().getName();			
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
			
		return channel;
	}

	/**
	 * @param dscSignal
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#sendDscSignal(byte[])
	 */
	public void sendDscSignal(byte[] dscSignal) {
		_radioCore.sendDscSignal(dscSignal);
	}

	/**
	 * @return
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#getAntenna()
	 */
	public Antenna getAntenna() {
		return _radioCore.getAntenna();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioEventListener#notifyMasterSwitch()
	 */
	public void notifyMasterSwitch() {
		
		Button btn  = _masterSwitchOn ? BTN_POWER_OFF : BTN_POWER_ON;		
		_masterSwitchOn = !_masterSwitchOn;
		_instCtx.getBus().publish(new BusMessage(this, btn));
		
		if(_masterSwitchOn)
			_instCtx.getBus().publish(new BusMessage(this, BTN_RESET));

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioEventListener#notifyChannel()
	 */
	public void notifyChannel() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioEventListener#notifyPower()
	 */
	public void notifyPower() {
		// TODO Auto-generated method stub
		
	}
	
}
