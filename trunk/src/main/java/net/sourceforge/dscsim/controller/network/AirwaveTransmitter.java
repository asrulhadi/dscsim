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
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.RadioCoreController;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.settings.InfoStoreFactory;
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
         
         
		Dscmessage tobeDispatched = null;
        while(true){

            try {
                
                
 				synchronized (_outboundQueue) {
 					
 					_outboundQueue.wait(1000);
					
	                if(_outboundQueue.size() > 0 ){
	                    tobeDispatched = (Dscmessage) _outboundQueue.get(0);	
	                    _outboundQueue.remove(tobeDispatched);
	                }
	                
	                //AppLogger.debug("queue size is ="+ _outboundQueue.size());
	                					
 
				}

				if(tobeDispatched != null) {

					Dscmessage tmpMessage = tobeDispatched;
					tobeDispatched = null;
	                
	                ByteArrayOutputStream oOutStrm = new ByteArrayOutputStream();	                
	    			JAXBContext jc =  JAXBContext.newInstance("net.sourceforge.dscsim.controller.message.types", AirwaveTransmitter.class.getClassLoader());		
	    			Marshaller m = jc.createMarshaller();  			
	    			//OutputStream os = new DataOutputStream(oOutStrm);
	    			m.marshal(tmpMessage, oOutStrm);	
                
	    			String strMsg = new String(oOutStrm.toByteArray());
	                AppLogger.info("AirwaveTransmitter.run - message being sent is=" + strMsg);
	    			
	                byte[] byteMsg = strMsg.getBytes();
	                byte[] msgWithHeader = new byte[byteMsg.length+4];
	                AppLogger.debug("AirwaveTransmitter.run - message length bytes=" + byteMsg.length);

	                ByteConverter.intToByteArray(PACKET_TYPE, msgWithHeader, 0 );
	                System.arraycopy(byteMsg, 0, msgWithHeader, 4, byteMsg.length);
	                try {
		                radioCoreController.sendDscSignal(msgWithHeader);
	                } catch( IOException ioE ) {
	                	// TODO: This Exception indicates that the dsc message could not be sent
	                	// (radio off or currently transmitting voice). The Controller should
	                	// switch back to basic status (see [1667403] Beim Senden über das Radio sollte der
	                	// Controller in den..)
	                	// how can we give this feedback to the controller?
	                }
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
    
	public void transmit(Dscmessage oMsg) {
		
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
  

}
