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
 
package net.sourceforge.dscsim.controller.screen;

import java.lang.reflect.Method;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Button;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.DscUtils;
import net.sourceforge.dscsim.controller.screen.types.ActiveField;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Element;

;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Bean implements Constants, ActiveField {
	
	protected String _strName = "";
	protected String _strFormat = null;
	protected String _strDefault = "";
	protected String _strActualValue = "";
	protected String _strActualValueDisplay = "";
	
	protected Element _oElement = null;
	protected Object _oBean = null;
	
	private boolean _blink = false;
	private boolean _blinkstate = false;
	private long _lastBlink = 0;
	private String _lastActualValueDisplay = null;
	private static final long BLINK_INTERVAL = 500;
	
	public Object copyObject(){
		throw new RuntimeException("Bean.copyObject not implemented.");
	}
	
	public Bean(Element oElement){
		_oElement = oElement;		
		_strFormat = oElement.getAttributeValue("format");
		
		setDisplayText(getValue());
				
		initilize(oElement);
	}
	
	private void initilize(Element oElement){
		if(oElement.getAttributeValue("blink") != null && oElement.getAttributeValue("blink").equals("true")){
			_blink = true;
		}		
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.ActiveField#isEntryField()
	 */
	public boolean isEntryField(){
		return _oElement.getAttribute("attribute") != null;
	}
	public Bean(Element oElement, Object oObj){
		_oElement = oElement;			
		_strFormat = oElement.getAttributeValue("format");
		setDisplayText(getValue(oObj));	
		_oBean = oObj;
		initilize(oElement);

	}

	protected void setDisplayText(String value){						
		_strActualValue = value;	
		
		if(_strFormat != null){
			int end = _strFormat.length() > _strActualValue.length() ? _strActualValue.length() : _strFormat.length();
			_strActualValue = _strActualValue.substring(0, end);		
			_strActualValueDisplay = _strActualValue + INPUT_PATTERN.substring(_strActualValue.length(),  _strFormat.length());
		}else{
			_strActualValueDisplay = _strActualValue;
		}
	}
	
	/* (non-Javadoc)
	 * @see applet.screen.bean.ActiveField#getFieldName()
	 */
	public String getFieldName(){
		return _oElement.getAttributeValue("name");
	}
	protected String getValue(){

		String strVal = null;	
	    
	    if(_oBean == null){
	    		strVal = _oElement.getText();
	    }else{
	    		strVal = getValue(_oBean);
	    }
		
		return strVal;

	}
	protected String getValue(Object oObj){
		
		String retVal = null;
		String attrName = _oElement.getAttributeValue("attribute");

		if(attrName != null){
			retVal = getValue(oObj, attrName);
		}else{
			retVal = _oElement.getText();						
				
		}
		
		return retVal;

	}
	
	protected String getValue(Object oBean, String attrName){
		String retVal = "";

		try{
			    			    					    				
			Class argTypes[] = {};
		
			Method oMethod = oBean.getClass().getMethod("get"+attrName, argTypes);
			
		    Object actParams[] = {};
		    
			retVal = oMethod.invoke(oBean, actParams).toString();
				
			
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
			


		
		
		return retVal;
		
	}
	
	public void setElement(Element oElement){
		_oElement = oElement;
	}
	
	public Element getElement(){
		return _oElement;
	}
	protected void setValue(String value){
								
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
	
	
	public void setValue(ActiveField oValue) {
		throw new RuntimeException("method not implemented.");
	}
	/* (non-Javadoc)
	 * @see applet.screen.bean.ActiveField#getDisplayValue()
	 */
	public String getDisplayValue(){
		
		if(_blink == false){
			return _strActualValueDisplay;
		} else {
			
			String retVal = null;
			long currTime = System.currentTimeMillis();
			long blinkInterval = currTime - _lastBlink;
			
			//AppLogger.info("blinking inter =" + blinkInterval);

			if(blinkInterval > BLINK_INTERVAL){
				_lastBlink = currTime;
				_blinkstate = !_blinkstate;	
				
				if(_blinkstate == true){
					_lastActualValueDisplay =  _strActualValueDisplay;
				}else{
					_lastActualValueDisplay =  "";	
				}
			}
						
			return _lastActualValueDisplay;

			
		}
	}
	
	/* (non-Javadoc)
	 * @see applet.screen.bean.ActiveField#isComplete()
	 */
	public boolean isComplete(){
	
		if(isEntryField() == false)
			return true;
		
		if(_strFormat != null && _strActualValue.length() == _strFormat.length()){
			return true;
		}else{
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see applet.screen.bean.ActiveField#signal(applet.BusMessage)
	 */
	public int signal(BusMessage oMessage) {	
		
		if(_oKeySet.contains(oMessage.getButtonEvent().getKeyId())){
			
			if(_strActualValue.length() < _strFormat.length()){
				_strActualValue +=  DscUtils.getKeyStringValue(oMessage.getButtonEvent().getKeyId());
				
				_strActualValueDisplay = _strActualValue + INPUT_PATTERN.substring(_strActualValue.length(), _strFormat.length());

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

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isValid()
	 */
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public String toXml() {
		return null;
	}

	public void fromXml(String inStr) {
		
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#reset()
	 */
	public void reset() {
		_strActualValue = "";
		_strActualValueDisplay = "";
		
	}				
}

