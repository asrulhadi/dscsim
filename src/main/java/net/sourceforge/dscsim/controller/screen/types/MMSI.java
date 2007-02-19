/*
 * Created on 23.06.2006
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
 
package net.sourceforge.dscsim.controller.screen.types;

import java.lang.reflect.Method;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Button;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.DscUtils;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.utils.Utilities;

import org.jdom.Element;

;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MMSI extends BaseType {
	
	private static String _strFormat = "#########";
	private String _strActualValue = "";
	private String _strActualValueDisplay = "";
	
	public MMSI(){
	}
	
	public MMSI(String strMMSI){		
		_strActualValue = strMMSI;
		setDisplayText(strMMSI);
	}
	
	public MMSI(MMSI oOther){
		super(oOther);
		
		//_strFormat = oOther._strFormat;
		_strActualValue = oOther._strActualValue;
		_strActualValueDisplay = oOther._strActualValueDisplay;
	
	}
	public MMSI(Element oElement){
		this(oElement, null);
		_oElement = oElement;
		
		//get default or value from default.
		String val = getValue();
		setDisplayText(val);
						
	}

	public MMSI(Element oElement, Object oObj){
		super(oElement, oObj);
		
		String strValue = getValue(oObj); 
		setDisplayText(strValue);	
		setValue(strValue);
	}

	public void setDisplayText(String value){						
		_strActualValue = value;	
		
		if(_strFormat != null)
			_strActualValueDisplay = _strActualValue + INPUT_PATTERN.substring(_strActualValue.length(), _strFormat.length());
		else
			_strActualValueDisplay = _strActualValue;
		
	}
	
	public String getValue(){

		String strVal = null;	
	    
	    if(_oBean == null){
	    		strVal = _oElement.getText();
	    }else{
	    		strVal = getValue(_oBean);
	    }
		
		return strVal;

	}
	public String getValue(Object oObj){
		
		String retVal = null;
		String attrName = _oElement.getAttributeValue("attribute");

		//first check the source object.
		if(attrName != null){
			retVal = getValue(oObj, attrName);
		}
		
		//the source was empty. See if there is a defaault.
		if(retVal.length() < 1){
			retVal = _oElement.getText();		
		}
			
		
		return retVal;

	}
	
	public String getValue(Object oBean, String attrName){
		String retVal = "";

		try{
			    			    					    				
			Class argTypes[] = {};
		
			Method oMethod = oBean.getClass().getMethod("get"+attrName, argTypes);
			
		    Object actParams[] = {};
		    
			retVal = oMethod.invoke(oBean, actParams).toString();
				
			
		}catch(Exception oEx){
			AppLogger.error("BeanField.getValue - Exception = " + oEx.getLocalizedMessage());
		}
			


		
		
		return retVal;
		
	}
	public void setValue(String value){
								
		String attrName = _oElement.getAttributeValue("attribute");
		
		if(attrName != null && _oBean != null){
			
			try{
		
				Class argTypes[] = {String.class};
			
				Method oMethod = _oBean.getClass().getMethod("set"+attrName, argTypes);
				
			    Object actParams[] = {value};
			    
				oMethod.invoke(_oBean, actParams);
				
				
			}catch(Exception oEx){
				AppLogger.error(oEx);
			}
			

		}
		
		
	}
	
	public String getDisplayValue(){
		return _strActualValueDisplay;
	}
	
	public boolean isComplete(){
	
		if(_strActualValue.length() == _strFormat.length()){
			return true;
		}else{
			return false;
		}
	}
	
	boolean validateMMSI(String strMMSI){

		boolean result = false;
		
		try {
			
			int intMMSI = Integer.parseInt(strMMSI);

			if(strMMSI.length()<=9){
				result = true;
			}else{
				result =  false;
			}
						
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
		
		return result;
	}
	public int signal(BusMessage oMessage) {	
		
		if(_oKeySet.contains(oMessage.getButtonEvent().getKeyId())){
			
			if(_strActualValue.length() < _strFormat.length()){
				
				String newValue = DscUtils.getKeyStringValue(oMessage.getButtonEvent().getKeyId());
				
				if(validateMMSI(_strActualValue + newValue)){		
					_strActualValue += newValue;
					_strActualValueDisplay = _strActualValue + INPUT_PATTERN.substring(_strActualValue.length(), _strFormat.length());
				}

			}
			
		}else if(MV_LEFT.equals(oMessage.getButtonEvent().getKeyId())
				|| KP_BS.equals(oMessage.getButtonEvent().getKeyId())){
			
			int len = _strActualValue.length();
			if(len > 0){
				_strActualValue = _strActualValue.substring(0, len-1);
				_strActualValueDisplay = _strActualValue + INPUT_PATTERN.substring(_strActualValue.length(), _strFormat.length());
		
			}
		}
		 
		
		int focus = 0;
		if(_strFormat.length() == _strActualValue.length()){
			focus = 1;
			setValue(_strActualValue);
		}else if(_strActualValue.length()==0){
			focus = -1;
			setValue(_strActualValue);
		}else{
			focus = 0;
		}
				
		return focus;

	}

	public boolean isValid() {	
		return validateMMSI(_strActualValue);
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.BaseType#getInvalidDisplay()
	 */
	protected String getInvalidDisplay() {
		// TODO Auto-generated method stub
		return "no MMSI data";
	}				
	
	public Object copyObject(){
		return new MMSI(this);
	}
	
	public String toString(){
		return _strActualValue;
	}
	public String toXml() {
		
		return "<MMSI>" 
			+ "<number>"
			+ _strActualValue
			+ "</number>"
			+ "</MMSI>";
	}
	public void fromXml(String inStr) throws Exception {
		
		_strActualValue =Utilities.getAttributeValue("number", inStr);
		setDisplayText(_strActualValue);
			
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#reset()
	 */
	public void reset() {
		_strActualValue = "";
		_strActualValueDisplay = "";
		
	}
	public void setValue(ActiveField oValue) {
		
		if(oValue instanceof MMSI){
				
			MMSI oOther = (MMSI)oValue;
			_strActualValue = oOther._strActualValue;
			_strActualValueDisplay = oOther._strActualValueDisplay;
			
		}
	}
	
	/*
	 * check if MMSI is a coastal radio station.
	 */
	public boolean isCoastal(){
		
		if(!isValid())
			return false;
		
		return _strActualValue.startsWith("00");
		
	}
}

