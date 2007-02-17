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
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;

import net.sourceforge.dscsim.controller.utils.AppLogger;





public class MulticastReceiver implements Runnable, DscsimReceiver {
    
    /** Creates a new instance of MulticastReceiver */
	
	private InternalBusListener _oSignalRecipient = null;
	
    public MulticastReceiver() {
        
    }
    
    
    public void run() {

         String theMessage = "Hello";
         InetAddress theGroup = null;
         MulticastSocket theSocket = null;
         int thePort = 6788;
         String theAddress = "228.5.6.8";

         try{
             theGroup = InetAddress.getByName(theAddress);
             theSocket = new MulticastSocket(thePort);
             theSocket.joinGroup(theGroup);              
         } catch(Exception oEx){
             AppLogger.error(oEx.getMessage());
         }

        while(true){

            try {
                
                byte[] buf = new byte[2048];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                AppLogger.debug("receiving inbound.");
                theSocket.receive(recv);
                
                ByteArrayInputStream oInputStr = new ByteArrayInputStream(recv.getData());
                ObjectInputStream oObjStrm = new ObjectInputStream(oInputStr);
                DscMessage oDcsMessage = (DscMessage)oObjStrm.readObject();
                
                //String inBound = new String(recv.getData());
               
                AppLogger.info("inbound message is=" + oDcsMessage.toString());
                
                _oSignalRecipient.updateSignal(oDcsMessage);
                //Thread.sleep(5000);  
             }catch(EOFException oEofEx){
            	 AppLogger.error("EOFException - possible cause: buffer too small: Message=" + oEofEx.toString());
             }catch(Exception oEx){
                 AppLogger.error(oEx.getMessage());
             }
        }        
    }
    
    public void addDependent(InternalBusListener oDependent) {
    	
    		_oSignalRecipient = oDependent;
    	
    }
    
    public static void main(String args[]){        
        new Thread(new MulticastReceiver()).start();
    }
    
    public void turnon(){        
    	Thread worker = new Thread(this);
    	worker.setName("Controller.MulticastReceiver");
        worker.start();
    }
}
