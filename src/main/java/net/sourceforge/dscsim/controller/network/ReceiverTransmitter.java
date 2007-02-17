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




public class ReceiverTransmitter implements Runnable {

	ArrayList _outboundQueue = new ArrayList();


	public void run() {

         AppLogger.debug("ReceiverTransmitter.running.");
                
		DatagramChannel theDatagramChannel = null;
		Selector theSelector = null;
		InetSocketAddress theInetSocketAddress = null;
		try {
			
                    theDatagramChannel = DatagramChannel.open();
			
                    theDatagramChannel.configureBlocking( false );

                    theSelector = Selector.open();

                    theDatagramChannel.register( theSelector, SelectionKey.OP_READ);

                    theInetSocketAddress = new InetSocketAddress("134.101.22.158", 4446);
                    //theInetSocketAddress = new InetSocketAddress("239.0.10.1", 4446);
                    //theInetSocketAddress = new InetSocketAddress(4446);
                    
                    theDatagramChannel.socket().bind(theInetSocketAddress);
	        
		}catch(Exception oEx)   {
			AppLogger.error("ReceiverTransmitter Exception - Message=" + oEx.getMessage());	     	
		}
	   
		DscMessage tobeDispatched = null;
		byte[] receiveData = new byte[ 512 ];
		ByteBuffer theReceiveDataByteBuffer = ByteBuffer.wrap( receiveData );

		while(true){
			
			//look for something to read
			try {
                            if(theSelector.select(2000) > 0) {
                                //read and dispatch to listener

                                theDatagramChannel.receive(theReceiveDataByteBuffer);

                                //put the bytedate into a DscMessage and dispatch it 
                                //to the dependant listern.
                                
                                String msg = new String(theReceiveDataByteBuffer.array());
                                AppLogger.debug("received:" + msg);

                                Iterator it = theSelector.selectedKeys().iterator( );

                                // will be exactly one key in the set, but iterator is
                                // only way to get at it
                                while( it.hasNext() ){
                                        it.next();
                                        // Remove key from selected set; it's been handled
                                        it.remove();
                                 }
                                
                               

                            } else {
                              AppLogger.debug("select timeout.");
                            } 	
                            
			} catch(Exception oEx)   {
				AppLogger.error("ReceiverTransmitter Exception - Message=" + oEx.getMessage());	     	
			}

	
			//look for something to send.
			try {
				
				synchronized (_outboundQueue) {
					tobeDispatched = (DscMessage) _outboundQueue.get(0);								
				}
							
				if(tobeDispatched != null) {
					
					String msg = tobeDispatched.toString();
					
					ByteBuffer oByteBuffer = ByteBuffer.wrap(msg.getBytes());
					
	                 theDatagramChannel.send(oByteBuffer, theInetSocketAddress );
	                 										
					tobeDispatched = null	;

				}

			}catch (Exception oEx){
				AppLogger.error("Exception RadioTransmitter.run - Message="+oEx.getMessage());				
			}
			
					
			/*failed transmits will be lost*/
			tobeDispatched = null	;
			
		}
	 
	     	
		
		
	}

	public synchronized void transmit(DscMessage oMsg) {
		
		_outboundQueue.add(oMsg);
		
		_outboundQueue.notify();
		
	}
        
        public static void main(String args[]) {
            
            new Thread(new ReceiverTransmitter()).start();
        }
}
