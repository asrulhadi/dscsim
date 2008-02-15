package net.sourceforge.dscsim.controller.message.types;

import java.lang.reflect.Method;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.DscUtils;
import net.sourceforge.dscsim.controller.data.types.ActiveField;
import net.sourceforge.dscsim.controller.utils.AppLogger;

public class MMSI implements Constants {

	private static String _strFormat = "#########";
	private String _strActualValue = "";
	private String _strActualValueDisplay = "";
	
	public MMSI(){
	}
	
	public MMSI(String strMMSI){		
		_strActualValue = strMMSI;
	}
	
	public MMSI(MMSI oOther){
		
		//_strFormat = oOther._strFormat;
		_strActualValue = oOther._strActualValue;
		_strActualValueDisplay = oOther._strActualValueDisplay;
	
	}
	
	
	public String getValue(){

		String strVal = null;	

		return strVal;

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
