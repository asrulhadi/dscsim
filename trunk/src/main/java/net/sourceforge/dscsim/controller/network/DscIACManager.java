/*
 * Created on 05.09.2006
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
 
package net.sourceforge.dscsim.controller.network;

import net.sourceforge.dscsim.controller.ApplicationContext;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DscIACManager implements Constants {
	
	/*DscMessage message transport layer*/
	private static String _strIACMethod = null;
	private static DscsimReceiver _receiver = null;
	private static DscsimTransmitter _transmitter = null;
	
	/*SyncMessage message transport layer*/
	private static SyncPublisherInterface _syncPublisher= null;
	private static SyncListenerInterface _syncListener= null;
	
	
	
	private DscIACManager(){}
	
	public static synchronized void initIAC(InstanceContext oInstCtx, String strIACMethod, InternalBusListener oListener){
	
		if(_strIACMethod != null)
			return;
		
		//if the mode is master then all message will arrive via the synchronization handler.
		if(OP_MODE_MASTER.equals(oInstCtx.getApplicationContext().getMode())){			
			NullTransmitterReceiver oNTransRec = new NullTransmitterReceiver();			
			_transmitter = oNTransRec;
			_receiver = oNTransRec;	
		} else if(IAC_YIMSG.equals(strIACMethod)){
			
			/*
			AppLogger.debug2("DscIACManager.init - YIMSGReceiverTransmitter");
			YIMSGReceiverTransmitter oIAC = new YIMSGReceiverTransmitter(oInstCtx, oListener);
			_receiver = oIAC;
			_receiver.addDependent(oListener);
			_transmitter = oIAC;			
			_receiver.turnon();
			*/
		} else if(IAC_JMS.equals(strIACMethod)){
			/*
			AppLogger.debug2("DscIACManager.init - JMSReceiverTransmitter");
			JMSReceiverTransmitter oIAC = new JMSReceiverTransmitter(oInstCtx, oListener);
			_receiver = oIAC;
			_receiver.addDependent(oListener);
			_transmitter = oIAC;			
			_receiver.turnon();
			*/
		} else if(IAC_AIRWAVE.equals(strIACMethod)){
			
			//stand alone slave
			AppLogger.debug2("DscIACManager.init - AirwaveReceiver");
		
			_strIACMethod = IAC_UDP;
			_receiver = new AirwaveReceiver(oInstCtx);
			_receiver.addDependent(oListener);
			_receiver.turnon();
			_transmitter = AirwaveTransmitter.getInstance(oInstCtx);
		}
		
	
	}
	
	
	public static DscsimReceiver getReceiver(){
		if(_receiver == null)
			throw new RuntimeException("DscIACManager.getReceiver - Interapplication communication not initialized.");
		return _receiver;
	}
	
	public static DscsimTransmitter getTransmitter(){
		
		if(_transmitter == null)
			throw new RuntimeException("DscIACManager.geTransmitter - Interapplication communication not initialized.");

		return _transmitter;
	}
	
	 public static synchronized void initSyncPublisher(ApplicationContext oAppCtx){

		 if(OP_MODE_SLAVE.equals(oAppCtx.getMode())){
			 
			if(IACS_TCP.equals(oAppCtx.getIACSync())){
			
				//slave only needs a publisher
				_syncPublisher = SyncPublisher.getInstance();
				if(!SyncPublisher.getInstance().connect("localhost", 8777)){
					AppLogger.error("MultiClu.ctor -unable to connect.");
				} 	
				//slave doesn't need a listerner.
				_syncListener = new NullSyncPublisherListener();
				
			} else {	
				
				_syncPublisher = (SyncPublisherInterface)DscIACManager.getTransmitter();
				
				if(_syncPublisher == null)
					throw new RuntimeException("IAC transport must be intialized before Sync.");
	
	
			}
		 } else {
		 	//master or stand alone don't publish however give a dummy impl.
			 _syncPublisher = new NullSyncPublisherListener();
		 }
	
	 }
	 
	 public static synchronized void initSyncListener(InstanceContext oInstCtx){
			 
	 	ApplicationContext oAppCtx = oInstCtx.getApplicationContext();
	 	if(OP_MODE_STANDALONE.equals(oAppCtx.getMode())==false){
	 			 	
			 if(IACS_TCP.equals(oAppCtx.getIACSync())){				 
				 _syncListener = SyncListenerDispatcher.getInstance();
			 } else {
			 	//_syncListener = new YIMSGReceiverTransmitter(oInstCtx, null);
				 
			 }	
	 	} else {
	 		
	 		_syncListener = new NullSyncPublisherListener();
	 	}

	 }
	 
	 public static SyncListenerInterface getSyncListener(){
		 return _syncListener;
	 }
	 
	 public static SyncPublisherInterface getSyncPublisher(){
		 return _syncPublisher;
	 }

}
