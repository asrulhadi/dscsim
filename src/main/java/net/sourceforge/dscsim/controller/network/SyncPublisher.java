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
package net.sourceforge.dscsim.controller.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.Selector;

import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.utils.AppLogger;





public class SyncPublisher implements SyncPublisherInterface {
	
	private static SyncPublisher _oInstance = new SyncPublisher();

	private String _masterIPAddr = null;
	private int _masterPort = 0;
	private Socket _clientSocket = null;
	private int CONNECT_TIMEOUT = 5000;
	private int TRY_COUNT = 1;
	
	private SyncPublisher(){}

	public static SyncPublisher getInstance() {		
		return _oInstance;
	}

	/*
	 * connect to master
	 */
	public boolean connect(String masterIPAddr, int masterPort){
		_masterIPAddr = masterIPAddr;
		_masterPort = masterPort;
		
		if((_clientSocket == null || _clientSocket.isConnected() == false)){


			InetSocketAddress sadd = null;
			
			try {
			   						   	
				 sadd = new InetSocketAddress(_masterIPAddr, _masterPort);
			   	
			}catch(Exception oEx){
			   AppLogger.error("SyncPublisher.connect - Exception message =" + oEx.getMessage());
			   return false;
			}
		   
		   
			for(int i = TRY_COUNT; i>0; i--){

				_clientSocket = new Socket();

				AppLogger.error("SyncPublisher.connect - attempt #=" + (TRY_COUNT - i +1));

				if(connect(_clientSocket, sadd)){
					AppLogger.error("SyncPublisher.connect - successful.");
					return true;
				} 
				
			}
			
			return false;
		}
		
		return true;
	}
	
	private boolean connect(Socket oSocket, SocketAddress oSAdd){
		
		boolean success = false;
		
		try {
	
			oSocket.connect(oSAdd, CONNECT_TIMEOUT);
			
			try{
				
				//important fix for solving multapp sychron
				//problem in which the master was not receiving 
				//all the messages in a timely manner.
				oSocket.setTcpNoDelay(true);
			}catch(Exception oEx){
				AppLogger.error(oEx);
			}
			
			success=true;
		}catch(Exception oEx){
			AppLogger.error("SyncPublisher.connect -Exception message= " + oEx.getMessage());
		}
		
		return success;
	}
	
	/*
	 * disconnect from master
	 */
	public void disconnect(){
		
		
		if(_clientSocket != null){
			try{
				_clientSocket.close();				
			} catch(Exception oEx){
				AppLogger.error(oEx);
			}
		}
		
	}
	
	/*
	 * send syn message to master
	 */
	
	/* (non-Javadoc)
	 * @see network.SyncPublisherInterface#sendSync(java.lang.String, java.lang.Object)
	 */
	public void sendSync(String strMMSI, Object oBusMessage){
		sendSync(new SyncMessage(strMMSI, oBusMessage));	
	}
	
	public boolean sendSync(SyncMessage obj){
		
		boolean ret = false;
		
		if(_clientSocket != null && _clientSocket.isConnected()){
			
			try{
	            ByteArrayOutputStream oOutStrm = new ByteArrayOutputStream();
	            ObjectOutputStream oObjStrm = new ObjectOutputStream(oOutStrm);
	            
	            oObjStrm.writeObject(obj);
	            
	            byte[] byteMsg = oOutStrm.toByteArray();

				_clientSocket.getOutputStream().write(byteMsg);
				
				//this combined with the setNoTcpDelay seamd to
				//do the trick - eventhough doco says this has no affect.
				_clientSocket.getOutputStream().flush();
				
				ret = true;
				
				AppLogger.error("SyncListener.sendSync - message send." + obj.toXml());

				
			}catch(Exception oEx){
				AppLogger.error("SyncListener.sendSync - Exception message =" + oEx.getMessage());
			}
		}
		
		return ret;
	}
	

	public static void main(String args[]){

		AppLogger.debug("SyncPublisher.main started.");

		SyncPublisher oBroker = new SyncPublisher();
		
		if(!oBroker.connect("localhost", 8777)){
			AppLogger.error("SyncPublisher.main -unable to connect.");
			
		}
		
		SyncMessage oMsg = new SyncMessage("211001604", new Dscmessage());
		
		for(int i =0;i< 100; i++){
			AppLogger.debug("SyncPublisher.main sendSync #" + i);
			
			if(!oBroker.sendSync(oMsg) == true){
				AppLogger.error("SyncPublisher.main sendSync failed");
			}
			
			try{
			    Thread.sleep(50);
				
			}catch(Exception oEx){
				AppLogger.error(oEx);
			}

			
		}
		
		oBroker.disconnect();
		
		AppLogger.debug("SyncPublisher.main end.");

	}

}
