/*
 * Created on 18.07.2006
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
public abstract class Coordinate extends BaseType {
	
	
	/*0=deg, 1=min, 2=sec, 3=hem*/
	private String _strParts[] = null;
	private int _lenParts[] = null;
	private transient int _activePart = 0;
	private int _hem = 0;
	
	public static final int DEG_COORD = 0;
	public static final int MIN_COORD = 1;
	public static final int HEM_COORD = 2;	
	/**
	 * @param oElement
	 * @param oBean
	 */
	
	protected abstract boolean validateInput(String currValue, String newValue, int partID, int partLen);
	protected abstract String getDisplayLabel();
	
	public void setActive(){
		int i=MIN_COORD;
		for(i=0; i < HEM_COORD;i++){
			if(isDone(i)==false)
				break;
		}
		
		_activePart = i;
		
	}
	protected int getDegrees(){
		return getPart(DEG_COORD);
	}
	
	protected int getMinutes(){
		return getPart(MIN_COORD);
	}
	
	protected boolean hasDegrees(){
		return hasPart(DEG_COORD);
	}
	
	protected boolean hasMinutes(){
		return hasPart(MIN_COORD);
	}
	
	private boolean hasPart(int partId){
		if(_strParts[partId].length()>0)
			return true;
		else
			return false;		
	}
	
	private int getPart(int partId){
		if(_strParts[partId].length()>0)
			return Integer.parseInt(_strParts[partId]);
		else
			return 0;
	}
	private void init(String parts[], int lengths[]){
		_strParts = parts;
		_lenParts = lengths;
		setActive();
	}
	public Coordinate(Element oElement, Object oBean, String parts[], int lengths[]) {
		super(oElement, oBean);
		init(parts, lengths);
		// TODO Auto-generated constructor stub
	}
	
	public Coordinate(String parts[], int lengths[]){
		_strParts = parts;
		_lenParts = lengths;
	}
	public Coordinate(Coordinate oOther){
		super(oOther);
		
		_strParts = new String[oOther._strParts.length];
		_lenParts = new int[oOther._lenParts.length];
		
		System.arraycopy(oOther._strParts, 0, _strParts, 0, _strParts.length);
		System.arraycopy(oOther._lenParts, 0, _lenParts, 0, _lenParts.length);

		//_strParts = oOther._strParts;
		//_lenParts = oOther._lenParts;
		_activePart = oOther._activePart;
		_hem = oOther._hem;
		setActive();

	}
	public Coordinate(Element oElement, String parts[], int lengths[]) {
		super(oElement, null);
		init(parts, lengths);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#getDisplayValue()
	 */
	public String getDisplayValue() {
		// TODO Auto-generated method stub
		
		if(isReadOnly()){
			if(this.isValid()==false){
				return getInvalidDisplay();
			}
		}
		return getDisplayLabel() + _strParts[DEG_COORD] 
		+ INPUT_PATTERN.substring(_strParts[DEG_COORD].length(), _lenParts[DEG_COORD])
		+ "' " 
		+ _strParts[MIN_COORD]
		+ INPUT_PATTERN.substring(_strParts[MIN_COORD].length(), _lenParts[MIN_COORD])
		+ "\" "
		+ _strParts[HEM_COORD].charAt(_hem);
		
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isComplete()
	 */
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return isDone(MIN_COORD);
	}

	
	public boolean hasValue(){
		
		if(_strParts == null || _lenParts == null)
			return false;
		
		for(int i=0; i<MIN_COORD;i++){
			
			if(validateInput(_strParts[i], "", i, _lenParts[i])==false){
				return false;
			}
			
		}
			
		return true;
		
	}

	private int getNextIncomplete(int start){
		
		for(int i=start+1; i< _strParts.length;i++){
			if(_strParts[i].length() < _lenParts[i]){
				return i;
			}
		}
			
		return -1;
	}
	
	private boolean isDone(int activePart){		
		return _strParts[activePart].length() == _lenParts[activePart];
	}
	
	public int signal(BusMessage oMessage) {

		
		int nextField = 0;

		if(_oKeySet.contains(oMessage.getButtonEvent().getKeyId())
				&& _activePart <= MIN_COORD){

				
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
			
			//if HEM is active then jump into seconds field
			if(_activePart == HEM_COORD)
				_activePart--;
			
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
			
		} else if(MV_UP.equals(oMessage.getButtonEvent().getKeyId())
				|| MV_DOWN.equals(oMessage.getButtonEvent().getKeyId())){
			
			_hem = (_hem + 1)%2;
			
		} else if(FK_ENT.equals(oMessage.getButtonEvent().getKeyId())) {
	
			if(isDone(MIN_COORD)){
				return 1;
			}else{
				return 0;
			}
			
		}

		
		
		if(nextField < 0)
			return -1;
		else if(nextField == _strParts.length-1 && isDone(MIN_COORD))
			return 1;
		else
			return 0;
		
		
	}
	public String toString(){
			
		return getDisplayValue();
	}
	
	public boolean isValid() {
		
		if(!hasDegrees() || !hasMinutes()){
			return false;
		}
		
		return true;
	}

	public void fromXml(String inStr)  throws Exception{
		
		_strParts[DEG_COORD] = Utilities.getAttributeValue("degrees", inStr);
		_strParts[MIN_COORD] = Utilities.getAttributeValue("minutes", inStr);
		_hem = Integer.parseInt(Utilities.getAttributeValue("hemispere", inStr));

		
	}
	public String toXml(){
		return "<coordinate>"
		
		+ "<degrees>" + _strParts[DEG_COORD] + "</degrees>"
		+ "<minutes>" + _strParts[MIN_COORD] + "</minutes>"
		+ "<hemispere>" + _hem+ "</hemispere>"		
		+ "</coordinate>";
	}
	
	public void reset(String[] strParts, int[] intLens){
		_strParts = strParts;
		_lenParts = intLens;
		_activePart = 0;
		_hem = 0;
		
	}
	
	public void setValue(ActiveField oValue) {
		
		if(oValue instanceof Coordinate){
				
			Coordinate oOther = (Coordinate)oValue;
			
			System.arraycopy(oOther._strParts, 0, _strParts, 0, _strParts.length);
			System.arraycopy(oOther._lenParts, 0, _lenParts, 0, _lenParts.length);
			_activePart = oOther._activePart;
			_hem = oOther._hem;
			_activePart = oOther._activePart;
			
		}
	}
}
