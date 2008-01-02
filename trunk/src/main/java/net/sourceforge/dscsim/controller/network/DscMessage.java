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

 
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.screen.types.Channel;
import net.sourceforge.dscsim.controller.screen.types.Latitude;
import net.sourceforge.dscsim.controller.screen.types.Longitude;
import net.sourceforge.dscsim.controller.screen.types.MMSI;
import net.sourceforge.dscsim.controller.screen.types.Time;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.utils.Utilities;

import org.jdom.Element;



public class DscMessage implements java.io.Serializable, Cloneable, DscSendable, net.sourceforge.dscsim.controller.Constants {
	
	/**
	 * for version compatibility.
	 */
	public final static long serialVersionUID = 4301620182757853881L;
	
	private String _uidts =  String.valueOf(System.currentTimeMillis());
	
    private String _toMMSI = "";
	private String _fromMMSI = "";
	
	//[Group|Individual|All Ships|Distress|Distress Acknowledge|Indivual  Acknowledge]
	private String _call_type = "";
    	
    //[Undesignate|Undesignate]
    private String _nature = "";
    
    private String _catagory = "";
    
    private Time _position_time = new net.sourceforge.dscsim.controller.screen.types.Time((org.jdom.Element)null);
    
    private DscPosition _position = null;
    
    private String _channel = "";
          
    private String _complianceCode = "";
    private String _complianceReasonCode = "";
    	
    private String _msgdata = "";
    
    private boolean _aknowledged = false;
  
    public String toString() {
    		return "[" + _uidts + "|" +
		_fromMMSI + "|" + 
    		_toMMSI + "|" + 
    		_call_type +  "|" + 
    		_nature +  "|" + 
		_catagory + "|" +
		_complianceCode + "|"+
		_complianceReasonCode + "|" +
    		_position_time.getDisplayValue() +  "|" + 
    		(_position != null ? _position.toString() : "N/A" ) +  "|" + 
    		_channel +  "|" +    
    		_msgdata +"]";
    	 	
    }
    
    public String toXml(){
    	   	
    		String strXml = "<message>";
    	
    		Field oFields[] = getClass().getDeclaredFields();   		
    		Field oField = null;
    		for(int i=0; i<oFields.length; i++){
    			
    			oField = (Field)oFields[i];
    			
    			String strName = oField.getName();
    			
	    		String attr = Attr2Xml(oField);
	    		if(attr != null){
	    			strXml += "<" + strName + ">"
	    			+ attr
	    			+ "</" + strName +">";
	    		}
 
    		}
    		
    		strXml += "</message>";
    		
    		return strXml;
    		  	
    }
    
    public String Attr2Xml(Field oField) {
    	
		Object oObj = null;
		try{
    		oObj = oField.get(this);   			
		}catch(Exception oEx){
			AppLogger.error(oEx);
			return null;
		}
	
		if(oObj == null)
			return null;
		
		String retValue = null;

		if(oObj instanceof DscMessageAttribute)
			retValue = ((DscMessageAttribute)oObj).toXml();
		else
			retValue = oObj.toString();
		   			   		
		return retValue;
	   
    } 
    public void fromXml(String strXml){
    	
   		Field oFields[] = getClass().getDeclaredFields();   		
		Field oField = null;
		for(int i=0; i<oFields.length; i++){
			
			oField = (Field)oFields[i];
			
			String strName = oField.getName();
			
			String attrXml = Utilities.getAttributeValue(strName, strXml);
			
			try{
				
				//AppLogger.debug2("class="+oField.getType().getName());				
				Constructor oCtr = oField.getType().getConstructor(new Class[]{});
								
				if(oField.getType().getName().equals(String.class.getName())){
					oField.set(this, new String(attrXml));
				}else{
					Object newObject = oCtr.newInstance(new Object[]{});
					((DscMessageAttribute)newObject).fromXml(attrXml);		
					oField.set(this, newObject);
				}
				
			}catch (Exception oEx){
				AppLogger.debug(oEx.toString());
			}

			
		}
    	
    }
    

    
    public void setNature(String nature){
    		_nature = nature;
    }
 
    public String getNature(){
		return _nature;
    }
    
    public DscMessage(DscMessage oOther){
    		this(oOther.toXml());
    }
    public DscMessage(){
    }
    public DscMessage(String strXml){
    		fromXml(strXml);
    }
    
    public Object clone(){
    		return new DscMessage(this);
    }

    public String getMessage(){
        return _msgdata;
    }
    
    public boolean hasFromMMSI() {
    	return _fromMMSI ==null ? false : true;
    }
    public void setFromMMSI(String strMMSI) {
    	_fromMMSI = strMMSI;
    }
    
    public String getFromMMSI() {
    		return _fromMMSI;
    }
     
    public boolean hasToMMSI() {
    		if(_toMMSI == null || _toMMSI.equals(""))
    			return false;
    		else
    			return true;
    }
    
    public void setToMMSI(MMSI oMMSI) {
		_toMMSI = oMMSI.toString();
    }
    
    public void setToMMSI(String strMMSI) {
    		_toMMSI = strMMSI;
    }
    
    public String getToMMSI() {
    		return _toMMSI;
    }
    
    //used for yimsg -dscsim001 or dscsync
    public String getTo() {
		return null;
}
    
    public String getChannel(){
    		return _channel;
    }
    
    
    public void setChannel(Channel oChannel){
    		setChannel(oChannel.toString());
    }
    
    
    public void setChannel(String strChannel){
    	_channel = strChannel;
    }
    
    public String getCatagory(){
    		return _catagory;
    }
    
    public String getCatagoryForTypeText(){
	
    	String strTxt = null;
    	
    	if(_catagory != null && _call_type != null){
    		String cat_type = _catagory + "_" +_call_type;
    		Object txtObj = MultiContentManager.getProperties().getProperty(cat_type);
    	
    		if(txtObj != null)
    			strTxt = txtObj.toString();
    	}
    	
    	if(strTxt == null)
    		strTxt = "";
    	
    	return strTxt;
    }
    
    public String getCatagoryAsText(){
    
		if(_catagory != null){
		
			if(CALL_CAT_ROUTINE.equals(_catagory)){
				return CALL_CAT_ROUTINE_TEXT;
			}else if(CALL_CAT_SAFETY.equals(_catagory)){
				return CALL_CAT_SAFETY_TEXT;
    			}else if(CALL_CAT_URGENCY.equals(_catagory)){
    				return CALL_CAT_URGENCY_TEXT;
    			}else if(CALL_CAT_DISTRESS.equals(_catagory)){
				return CALL_CAT_DISTRESS_TEXT;
			} else {
				return "unknown catagory";
			}
		}else{
			return "no catagory";
		}
    }
    
    
    public void setCatagory(String strCatagory){
    		_catagory = strCatagory;
    }
    
    public String getCallType(){
    		return _call_type;
    }
    
    
    public String getCallTypeText(){
    	
		String strTxt = MultiContentManager.getProperties().getProperty(_call_type).toString();
    	
    		if(strTxt == null)
    			strTxt = "N/A";
    	
    		return strTxt;

}
    
    public void setCallType(String callType){
    		_call_type = callType;
    }
    
    public void setPosition(DscPosition position) {
    	this._position = position;
    }
    
    public DscPosition getPosition() {
    		return _position;
    } 
    
    public void setDistressLatitude(){
    	
    }
    
    public String getDistressLatitude(){
    		
    		Latitude oLat =getLatitude();
    		
    		if(oLat.isValid())
    			return oLat.toString();
    		else
    			return "No position data";
    	
    }
    
    public String getDistressTime(){
    	
    		Time oTime = getTime();
    		
    		if(oTime.isValid())
    			return oTime.toString();
    		else
    			return "No time data";
    }
    
    public String getDistressLongitude(){
		
		Longitude oLong =getLongitude();
		
		if(oLong.isValid())
			return oLong.toString();
		else
			return "";
	
}
    public Latitude getLatitude(){
    		
    		Latitude oLat = null;
    		
    		if(_position != null){
    			oLat = _position.getLatitude();
    		} else {
    			oLat = new Latitude((Element)null);
    		}
    		
    		return oLat;
    }

    public void setDistressLongitude(Longitude longitude){
    	
    		if(_position == null){
    			_position = new DscPosition(null, null);
    		}
    		
    		_position.setLongitude(longitude);
    		
    }
    public void setDistressLatitude(Latitude lat){
    	
    		if(_position == null){
    			_position = new DscPosition(null, null);
    		}
    		
    		_position.setLatitude(lat);
    		
    }
    public Longitude getLongitude(){
		
		Longitude oLong = null;
		
		if(_position != null){
			oLong = _position.getLongitude();
		} else {
			oLong = new Longitude((Element)null);
		}
		
		return oLong;
    }
    public Time getTime(){
		
		Time oTime = null;
		
		if(_position_time != null){
			oTime = this._position_time;
		} else {
			oTime = new Time((Element)null);
		}
		
		return oTime;
    }
    
    public void setTime(Time oTime){
    	_position_time = oTime;
    }
    
    public void setComplianceCode(String code){
    	_complianceCode = code;
    }
    
    public String getComplianceCode(){
    	return _complianceCode;
    }
    
    public String getComplianceAsText(){
    	String strTxt = null;
    	
    	if(_complianceCode != null){
       	 	strTxt = MultiContentManager.getProperties().getProperty(_complianceCode).toString();	
    	}
        
        if(strTxt == null)
        	strTxt = "not specified.";
        	
        return strTxt;
        		
    }
    
    
    public String getNatureText(){
    	String strTxt = "";
    	
    	if(_nature != null){
    		strTxt = (String)MultiContentManager.getProperties().getProperty(_nature, "").toString();
    	}
    	
    	if(strTxt == null)
    		strTxt = "unkown";
    	
    	return strTxt;
    }
    
    public String getComplianceReasonCode(){
    	return _complianceReasonCode;
    }
    
    public void setComplianceReasonCode(String complianceReasonCode){
    	_complianceReasonCode = complianceReasonCode;
    }
    
    public String getComplianceReasonText(){
		
    	Object objTxt = null;
    	
    	if(_complianceReasonCode != null){
    		objTxt = MultiContentManager.getProperties().getProperty(_complianceReasonCode);
    	}
    	
    	if(objTxt == null){
    		return "";
    	}else{
    		return objTxt.toString();
    	}
    	
    	
    }   
    public String getUid(){
    		return _uidts;
    }
    
    public boolean isCallAcknowledgement(){
    
    		String callType = getCallType();   		
    		if(callType.equals(CALL_TYPE_INDIVIDUAL_ACK)
    			|| callType.equals(CALL_TYPE_GROUP_ACK)
    			|| callType.equals(CALL_TYPE_DISTRESS_ACK)
			|| callType.equals(CALL_TYPE_ALL_SHIPS_ACK)){
    			
    			return true;
    		}else{
    			return false;
    		}   	
    }

	public boolean isAknowledged() {
		return _aknowledged;
	}

	public void setAknowledged(boolean aknowledged) {
		this._aknowledged = aknowledged;
	}
 
}
