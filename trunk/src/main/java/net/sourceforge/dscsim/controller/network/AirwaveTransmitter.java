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


import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.RadioCoreController;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.util.ByteConverter;





public class AirwaveTransmitter extends DscRadioTransmitter implements Runnable {
    
	public static final int PACKET_TYPE = 20;

	private String _strId = null;
    
	ArrayList _outboundQueue = new ArrayList();
	
//	private static AirwaveTransmitter oInstance = null;
	private InstanceContext _oInstCtx = null;
	
  	public static synchronized AirwaveTransmitter getInstance(InstanceContext oInstCtx)  {
  		
  		AirwaveTransmitter oInstance = new AirwaveTransmitter(oInstCtx, "static");
  		Thread worker = new Thread(oInstance);
  		worker.setName("Controller.MulticastTransmitter");
  		worker.start();
  		
  		return oInstance;
  	}
  	
    private AirwaveTransmitter(InstanceContext oInstCtx, String strId){
    	super(oInstCtx);
       _strId = strId;
       _oInstCtx = oInstCtx;
    }
    
     public void run() {

    	 RadioCoreController radioCoreController = _oInstCtx.getRadioCoreController();
         String theMessage = "Hello";
//         InetAddress theGroup = null;
//         MulticastSocket theSocket = null;
//         int thePort = 6788;
//         String theAddress = "228.5.6.8";

         try{
//           Possible TODO - Create the socket but we don't bind it as we are only going to send data
         	
//             theGroup = InetAddress.getByName(theAddress);
//           theSocket = new MulticastSocket(thePort);
//             theSocket = new MulticastSocket();

//           Note that we don't have to join the multicast group if we are only
//           sending data and not receiving
             //theSocket.joinGroup(theGroup);              
         } catch(Exception oEx){
             AppLogger.error("AirwaveTransmitter.run - Exception during setup = "+ oEx.getMessage());
         }
         
         
		DscMessage tobeDispatched = null;
        while(true){

            try {
                
                
 				synchronized (_outboundQueue) {
 					
 					_outboundQueue.wait(1000);
					
	                if(_outboundQueue.size() > 0 ){
	                    tobeDispatched = (DscMessage) _outboundQueue.get(0);	
	                    _outboundQueue.remove(tobeDispatched);
	                }
	                
	                //AppLogger.debug("queue size is ="+ _outboundQueue.size());
	                					
 
				}

				if(tobeDispatched != null) {

					DscMessage tmpMessage = tobeDispatched;
					tobeDispatched = null;
	                AppLogger.info("AirwaveTransmitter.run - message being sent is=" + tmpMessage.toString());
	                
	                ByteArrayOutputStream oOutStrm = new ByteArrayOutputStream();
	                ObjectOutputStream oObjStrm = new ObjectOutputStream(oOutStrm);
	                
	                //DcsMessage oDscMsg = new DcsMessage(theMessage);
	                oObjStrm.writeObject(tmpMessage);
	                
	                byte[] byteMsg = oOutStrm.toByteArray();
	                byte[] msgWithHeader = new byte[byteMsg.length+4];
	                AppLogger.debug("AirwaveTransmitter.run - message length bytes=" + byteMsg.length);

	                ByteConverter.intToByteArray(PACKET_TYPE, msgWithHeader, 0 );
	                System.arraycopy(byteMsg, 0, msgWithHeader, 4, byteMsg.length);
	                
	                radioCoreController.sendDscSignal(msgWithHeader);
	    			synchronized(tmpMessage){
	    				tmpMessage.notifyAll();
	    			}			
//	              	Possible TODO - And when we have finished sending data close the socket
            	}
            
             }catch(Exception oEx){
            	 oEx.printStackTrace();
                 AppLogger.error("AirwaveTransmitter.run - Exception during transmission = "+ oEx.getMessage());
             }
        }        
    }
    
	public void transmit(DscMessage oMsg) {
		
               AppLogger.debug("AirwaveTransmitter.transmit - queuing message.");
               
               try {
                   synchronized(_outboundQueue){
                        _outboundQueue.add(oMsg);

                        _outboundQueue.notify();

                    }                  
               }catch(Exception oEx){
                   AppLogger.error(oEx.getMessage());
               }
 
		
	}
    
    public static void main(String args[]) throws Exception{        
    	AirwaveTransmitter oMT = new AirwaveTransmitter(null, args[0]);
    
        Thread oTh = new Thread(oMT);
        oTh.start();
        
        DscMessage oMsg = new DscMessage();
        
        oMsg.setToMMSI("002110124");
        oMsg.setFromMMSI("211232323");
        
       
        oMT.transmit(oMsg);
        AppLogger.debug("test join");       
        oTh.join();
        
        AppLogger.debug("test done");
        
    }
  

}
