/*
 * Created on 17.07.2006
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

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.DscUtils;
import net.sourceforge.dscsim.controller.utils.Utilities;

import org.jdom.Element;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Time extends BaseType {
	
	private static final long serialVersionUID = 1L;
	private String _strParts[] = {"", ""};
	private static final int _lenParts[] = {2,2};
	private transient int _activePart = 0;
	
	public static final int HOURS = 0;
	public static final int MINUTES = 1;

	public Object copyObject(){
		return new Time(this);
	}
	
	public Time(){
	}
	public Time(Time oOther){
		super(oOther);
		
		System.arraycopy(oOther._strParts, 0, _strParts, 0, _strParts.length);
		//_lenParts = oOther._lenParts;
		_activePart = oOther._activePart;
		
		setActive();
		
	}
	public Time(Element oElement){
		super(oElement, null);
		
	}
	
	public Time(Element oElement, Object oObj){
		super(oElement, oObj);
	}
	
	public Time(String hrs, String min){
		_strParts[0] = hrs;
		_strParts[1] = min;
		
	}
	
	private boolean hasPart(int partId){
		if(_strParts[partId].length()>0)
			return true;
		else
			return false;		
	}
	
	protected boolean hasHours(){
		return hasPart(HOURS);
	}
	
	protected boolean hasMinutes(){
		return hasPart(MINUTES);
	}
	
	public boolean isValid() {
		
		if(!hasHours() || !hasMinutes()){
			return false;
		}
		if((getHours() >= 0 && getHours() <= 24)
		    && (getMinutes()>=0 && getMinutes() < 60)){
			return true;
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isEntryField()
	 */
	public boolean isEntryField() {
		return true;
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#getFieldName()
	 */
	public String getFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#getDisplayValue()
	 */
	public String getDisplayValue() {
		
		if(isReadOnly()){
			if(this.isValid()==false){
				return getInvalidDisplay();
			}
		}
		return _strParts[HOURS] 
			+ INPUT_PATTERN.substring(_strParts[HOURS].length(), _lenParts[HOURS])
			+ ":" 
			+ _strParts[MINUTES]
			+ INPUT_PATTERN.substring(_strParts[MINUTES].length(), _lenParts[MINUTES])
			+ " UTC";
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isComplete()
	 */
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return isDone(MINUTES);
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#signal(applet.BusMessage)
	 */
	private boolean isDone(int activePart){		
		return _strParts[activePart].length() == _lenParts[activePart];
	}
	public int signal(BusMessage oMessage) {
		int nextField = 0;

		if(_oKeySet.contains(oMessage.getButtonEvent().getKeyId())
				&& _activePart <= MINUTES){

				
			String nextValue = DscUtils.getKeyStringValue(oMessage.getButtonEvent().getKeyId());

			if(isDone(_activePart) == false){
				nextField = _activePart;
				
				if(validateInput(_strParts[_activePart], nextValue, _activePart, _lenParts[_activePart]))
					_strParts[_activePart] += nextValue;
				
			} else {
				nextField  = getNextIncomplete(_activePart);
				
				if(nextField > 0 && isDone(nextField)==false){
					_activePart = nextField;
				
					if(validateInput(_strParts[_activePart], nextValue, _activePart, _lenParts[_activePart]))
						_strParts[_activePart] += nextValue;
				}
			}
				
		} else if(KP_BS.equals(oMessage.getButtonEvent().getKeyId())){
			
			if(_strParts[_activePart].length() > 0){
				nextField = _activePart;
				_strParts[_activePart] = _strParts[_activePart].substring(0, _strParts[_activePart].length()-1);
			} else {
				
				nextField = _activePart;
				nextField--;
				if(_activePart > 0){
					_activePart--;
				}
				
				if(_strParts[_activePart].length() > 0)
					_strParts[_activePart] = _strParts[_activePart].substring(0, _strParts[_activePart].length()-1);
			}
			
		} else if(FK_ENT.equals(oMessage.getButtonEvent().getKeyId()) 
				|| isDone(MINUTES)){
			return 1;
		}
		
		if(nextField < 0)
			return -1;
		else if(nextField == _strParts.length-1 && isDone(MINUTES))
			return 1;
		else
			return 0;
		
		
	}

	private int getPart(int partId){
		if(_strParts[partId].length()>0)
			return Integer.parseInt(_strParts[partId]);
		else
			return 0;
	}
	protected int getHours(){
		return getPart(HOURS);
	}
	
	protected int getMinutes(){
		return getPart(MINUTES);
	}
	
	protected boolean validateInput(String currValue, String newValue, int partID, int partLen){
		
		int tst = 0;
		boolean result = false;
		switch(partID){
		
			case HOURS:
								
				if(currValue.length()==0){
					tst = Integer.parseInt(newValue);
					
					if(tst >= 0 && tst < 3)
						result = true;
					
				}else if(currValue.length()==1){
					tst = Integer.parseInt(currValue + newValue);
					
					if(tst>=0 && tst < 25)
						result = true;
				}
			
				break;
				
			case MINUTES:
				int hrs = getHours();

				if(currValue.length()==0){
					
					tst = Integer.parseInt(newValue);
					
					if(hrs < 24 && tst >= 0 && tst < 6)
						result = true;
					else if(hrs==24 && tst == 0)
						result = true;
					
				}else if(currValue.length()==1){
					tst = Integer.parseInt(currValue + newValue);
					
					if(hrs < 24 && tst>=0 && tst < 60)
						result = true;
					else if(hrs == 24 && tst==0)
						result = true;
				}
				break;
				
			default:
				
					
		}
		return result;
	}

	private int getNextIncomplete(int start){
		
		for(int i=start+1; i< _strParts.length;i++){
			if(_strParts[i].length() < _lenParts[i]){
				return i;
			}
		}
			
		return -1;
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.BaseType#getInvalidDisplay()
	 */
	protected String getInvalidDisplay() {
		// TODO Auto-generated method stub
		return "no time data";
	}
	
	public String toString(){	
		return getDisplayValue();
	}
	public String toXml() {
		return "<time>"
			   + "<hours>" + _strParts[0] + "</hours>"
		       + "<minutes>" + _strParts[1] + "</minutes>"
		       + "</time>";
	}
	
	public void fromXml(String inStr) throws Exception {
		
		_strParts[0]=Utilities.getAttributeValue("hours", inStr);
		_strParts[1]=Utilities.getAttributeValue("minutes", inStr);
		
	}
	
	public void setActive(){
		int i=HOURS;
		for(i=0; i < MINUTES;i++){
			if(isDone(i)==false)
				break;
		}
		
		_activePart = i;
		
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#reset()
	 */
	public void reset() {
		_strParts[0] = "";
		_strParts[1] = "";
		_activePart = 0;
		
	}
	
	public void setValue(ActiveField oValue) {
		
		if(oValue instanceof Time){
				
			Time oOther = (Time)oValue;
			
			System.arraycopy(oOther._strParts, 0, _strParts, 0, _strParts.length);
			System.arraycopy(Time._lenParts, 0, _lenParts, 0, _lenParts.length);
			_activePart = oOther._activePart;

			
		}
	}
}
