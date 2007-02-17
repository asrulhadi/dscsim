package net.sourceforge.dscsim.controller.screen;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Button;
import net.sourceforge.dscsim.controller.DscUtils;
import net.sourceforge.dscsim.controller.screen.types.BaseType;

import org.jdom.Element;


public class AnBean extends Bean {

	private String _lastKeyId = null;
	private int _counter = 0;
	private int _insertAt = -1;
	private boolean _upperCase = true;
	private StringBuffer _buffer;
	
	private static final String KEY2UPPER [][] ={
		{"0000"},	
		{"1ABC"},
		{"2DEF"},
		{"3GHI"},
		{"4JKL"},
		{"5MNO"},
		{"6PQR"},
		{"7STU"},
		{"8VWY"},
		{"9YZY"},			
	};
		
	private static final String KEY2LOWER [][] ={
		{"0000"},	
		{"1abc"},
		{"2def"},
		{"3ghi"},
		{"4jkl"},
		{"5mno"},
		{"6pqr"},
		{"7stu"},
		{"8vwx"},
		{"9yzy"},			
	};
	
	public AnBean(Element oElement, Object oBean) {
		super(oElement, oBean);
		// TODO Auto-generated constructor stub
	
	}

	public boolean isComplete(){
				
		if(_strActualValue.length() > 0){
			return true;
		}else{
			return false;
		}
	}

	public int signal(BusMessage oMessage) {
		
		String keyId = oMessage.getButtonEvent().getKeyId();
		
		if(KP_Aa.equals(keyId)){			
			_upperCase = !_upperCase;
			return 0;
		}
		
		if(MV_RIGHT.equals(keyId)){
			_lastKeyId = keyId;
			_counter = 0;
			return 0;
		}
				
		
		if(_oKeySet.contains(keyId)){
			
			if(_lastKeyId == null || !_lastKeyId.equals(keyId)){
				_counter = 0;
				_insertAt++;
				_lastKeyId = keyId;
			} else{
				_counter = (_counter+1)%4;
			}
			
			
			if(_strActualValue.length() < _strFormat.length()){
				
				
				int keyNum = Integer.parseInt(DscUtils.getKeyStringValue(keyId));
				
				String currCase[][] = _upperCase == true ? KEY2UPPER : KEY2LOWER;
				
				String nextChar = "";
				if(keyNum > -1 && keyNum < 10)
					nextChar = currCase[keyNum][0].substring(_counter,_counter+1);

			
				_strActualValue = _strActualValue.substring(0, _insertAt) + nextChar;
				
				
				_strActualValueDisplay = _strActualValue + INPUT_PATTERN.substring(_strActualValue.length(), _strFormat.length());

			}
			
		}else if(MV_LEFT.equals(oMessage.getButtonEvent().getKeyId())
				|| KP_BS.equals(oMessage.getButtonEvent().getKeyId())){
			
			int len = _strActualValue.length();
			if(len > 0){
				
				_strActualValue = _strActualValue.substring(0, len-1);
				_strActualValueDisplay = _strActualValue + INPUT_PATTERN.substring(_strActualValue.length(), _strFormat.length());
				_insertAt = _strActualValue.length()-1;
				
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
			setValue(_strActualValue);

			focus = 0;
		}
				
		return focus;


	}

}
