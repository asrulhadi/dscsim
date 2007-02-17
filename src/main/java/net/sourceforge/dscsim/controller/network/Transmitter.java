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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.dscsim.controller.utils.AppLogger;





public class Transmitter implements Runnable {

	ArrayList _outboundQueue = new ArrayList();


	public void run() {

        AppLogger.debug("ReceiverTransmitter.running.");
                
		DatagramChannel theDatagramChannel = null;
		Selector theSelector = null;
		InetSocketAddress theInetSocketAddress = null;
		try {
			
			theDatagramChannel = DatagramChannel.open();
	        theDatagramChannel.configureBlocking( false );
	        //theSelector = Selector.open();
	        //theDatagramChannel.register( theSelector, SelectionKey.OP_READ);
	        theInetSocketAddress = new InetSocketAddress("134.101.22.158", 4446);
	        //theDatagramChannel.socket().bind(theInetSocketAddress);
	        
		}catch(Exception oEx)   {
			AppLogger.error("Transmitter Exception init - Message=" + oEx.getMessage());	     	
		}
	   
		DscMessage tobeDispatched = null;
		byte[] receiveData = new byte[ 512 ];
		ByteBuffer theReceiveDataByteBuffer = ByteBuffer.wrap( receiveData );

		while(true){
			
			//look for something to send.
			try {
				
				synchronized (_outboundQueue) {
                                        if(_outboundQueue.size() > 0 ){
                                            tobeDispatched = (DscMessage) _outboundQueue.get(0);	
                                            _outboundQueue.remove(tobeDispatched);
                                        }
				}
							
				if(tobeDispatched != null) {
					
                                        AppLogger.debug("sending message:" + tobeDispatched.getMessage());
                                       
					String msg = tobeDispatched.getMessage();
					
					ByteBuffer oByteBuffer = ByteBuffer.wrap(msg.getBytes());
					
                                        theDatagramChannel.send(oByteBuffer, theInetSocketAddress );
	                 										
					tobeDispatched = null	;

				}

			}catch (Exception oEx){
				AppLogger.error("Exception Tansmitter.run - Message="+oEx.getMessage());				
			}
			
					
			/*failed transmits will be lost*/
			tobeDispatched = null	;
                        
                        
                        try{
                            Thread.sleep(5000);
                        }catch(Exception oEx){
                            
                        }
			
                        String msg = "Elapsed time=" + java.lang.System.currentTimeMillis();
                        transmit(new DscMessage(msg));
                        
                        
		}
	 
	     	
		
		
	}

	public void transmit(DscMessage oMsg) {
		
               AppLogger.debug("queuing message.");
               
               try {
                   synchronized(_outboundQueue){
                        _outboundQueue.add(oMsg);

                        _outboundQueue.notify();

                    }                  
               }catch(Exception oEx){
                   AppLogger.error(oEx.getMessage());
               }
 
		}
        
        public static void main(String args[]) {
            
            new Thread(new Transmitter()).start();
        }
}
