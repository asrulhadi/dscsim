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

import java.io.Serializable;
import java.lang.reflect.Field;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.utils.Utilities;





public class SyncMessage implements Serializable, DscSendable, DscMessageAttribute  {
	
	
	private String _srcMMSI = null;
	private Object _busMessage;
	
	public SyncMessage(String srcMMSI, Object oDscMessage){
		_srcMMSI = srcMMSI;		
		_busMessage = oDscMessage;
	}
	
	public String getMMSI(){
		return _srcMMSI;
	}
	
	public Object getBusMessage(){
		return _busMessage;
	}

	public String toXml() {
		
   		String strXml = "<syncMessage>";
    	
	    if(_srcMMSI != null){    		
	    		strXml += "<_srcMMSI>" + _srcMMSI + "</_srcMMSI>";	    	
	    }
	    
   		if(_busMessage != null){
   			strXml += "<_busMessage>" + ((DscMessageAttribute)_busMessage).toXml() + "</_busMessage>";	    	
    		}
    		
    		strXml += "</syncMessage>";
    		
    		return strXml;

	}


	public void fromXml(String inXml) throws Exception {
		
		_srcMMSI = Utilities.getAttributeValue("_srcMMSI", inXml);
		
	
		String subXml = Utilities.getAttributeValue("_busMessage", inXml);
		
		if(subXml != null){
			
			DscMessageAttribute tmpBusMsg =null;
			tmpBusMsg = new BusMessage(null, null, null);
			
			tmpBusMsg.fromXml(subXml);
			
			_busMessage =  tmpBusMsg;
			
		}
		
	}

	public String getTo() {
		return "dscsync";
	}

}
