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
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.settings.InfoStoreFactory;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.radio.core.RadioEventListener;
import net.sourceforge.dscsim.radio.core.VHFChannel;
import net.sourceforge.dscsim.radiotransport.Antenna;
import net.sourceforge.dscsim.radiotransport.Decibel;
import net.sourceforge.dscsim.radiotransport.Demodulator;
import net.sourceforge.dscsim.radiotransport.Frequency;
import net.sourceforge.dscsim.radiotransport.Receiver;
import net.sourceforge.dscsim.util.ByteConverter;





public class AirwaveReceiver implements Demodulator, DscsimReceiver {

	private static Logger logger = Logger.getLogger(AirwaveReceiver.class);
	
    /** Creates a new instance of MulticastReceiver */
	
	private InternalBusListener _oSignalRecipient = null;
	private InstanceContext _oInstCtx = null;
	
    public AirwaveReceiver(InstanceContext oInstCtx) {
    	_oInstCtx = oInstCtx;
        
    }
    

	public void processSignal(byte[] signal, int transmitterUid, Frequency frequency, Decibel decibel) {
		try {
			int type = ByteConverter.byteArrayToInt(signal, 0);
			if( type != AirwaveTransmitter.PACKET_TYPE ){
				//not the appropriate type
				logger.debug("Received packet is of wrong type. Expected "+AirwaveTransmitter.PACKET_TYPE+" but was "+type);
			} else {
				
	            ByteArrayInputStream oInputStr = new ByteArrayInputStream(signal,4,signal.length-4);
	            AppLogger.info("inbound message is=" + new String(signal));
	
	    		JAXBContext jc =  JAXBContext.newInstance("net.sourceforge.dscsim.controller.message.types", AirwaveReceiver.class.getClassLoader());		
	    		Unmarshaller u = jc.createUnmarshaller();  
	    		Dscmessage inComing =(Dscmessage)u.unmarshal(oInputStr);
	      	          	   
	            _oSignalRecipient.updateSignal(inComing);
	          
			}
         }catch(Exception oEx){
             AppLogger.error(oEx.getMessage());
         }
    }
    
    public void addDependent(InternalBusListener oDependent) {
    	
    		_oSignalRecipient = oDependent;
    	
    }
    
    public void turnon(){
    	Antenna antenna = _oInstCtx.getRadioCoreController().getAntenna();
    	Receiver receiver = antenna.createReceiver();
    	receiver.setFrequency(VHFChannel.VHF_CHANNEL_70.getShipStationFrequency());
    	receiver.addDemodulator(this);
    }

}
