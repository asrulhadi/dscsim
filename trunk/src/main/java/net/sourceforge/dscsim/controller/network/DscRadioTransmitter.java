/*
 * Created on 13.01.2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.dscsim.controller.network;

import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class DscRadioTransmitter implements DscsimTransmitter, Constants {
	
	private InstanceContext _oInstCtx = null;
	private String _origChan = null;
	public static int DELAY_TIME = 2000;
	
	public DscRadioTransmitter(InstanceContext oInstCtx){
		_oInstCtx = oInstCtx;	
	}
	protected void preTransmit(){
		try{
			_origChan = _oInstCtx.getRadioCoreController().getChannel();
			_oInstCtx.getRadioCoreController().setChannel(CH_70);
			//Thread.sleep(DELAY_TIME);
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
	}
		
	protected void postTransmit(Object oMsg){
		try{
			
			if(_origChan != null)
				_oInstCtx.getRadioCoreController().setChannel(_origChan);
			
			//wait a little for special affects
			Thread.sleep(DELAY_TIME);
			//notify sender that message has been sent.			
			synchronized(oMsg){
				oMsg.notifyAll();
			}			
			
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
		
	}

}
