/*
 * Created on 13.11.2006
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
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.DscUtils;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.utils.Utilities;

import org.jdom.Element;



/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DscBoolean extends BaseType {

	private boolean _value = false;
	private transient boolean _done = true;
	
	public DscBoolean(String value) {
		super();				
		_value = Boolean.valueOf(value).booleanValue();
	}
	
	public DscBoolean(Element oElement) {
		super(oElement, null);			
	}

	public DscBoolean(Element oElement, String value){
		super(oElement, value);
		_value = Boolean.valueOf(value).booleanValue();;	
	}
	public DscBoolean(Element oElement, Object oObj){
		super(oElement, oObj);
		if(oObj instanceof String)
			_value = Boolean.valueOf((String)oObj).booleanValue();	
		
	}
	public DscBoolean(DscBoolean oOther) {
		super(oOther);		
		_value = oOther._value;	
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
		return this._oElement.getAttributeValue("name");
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#getDisplayValue()
	 */
	public String getDisplayValue() {
		return Boolean.toString(_value);
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isComplete()
	 */
	public boolean isComplete() {
			return _done;
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#signal(applet.BusMessage)
	 */
	public int signal(BusMessage oMessage) {
	
		int focus = 0;
		String evtId = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();
		
		if(keyAction.endsWith(RELEASED))
			return 0;
		
		oMessage.getButtonEvent().getAction();
		
		if(FK_ENT.equals(evtId)){
			_done = true;
			focus = 1;
		}else if(KP_0.equals(evtId)){	
			_done = true;
			_value = false;			
		} else if(KP_1.equals(evtId)){	
			_done = true;
			_value = true;	
		}
		 		
		return focus;		
		
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isValid()
	 */
	public void setValue(String value) {
		_value = Boolean.valueOf(value).booleanValue();
	}
	
	public boolean getValue(){
		return _value;
	}
	public boolean isValid() {
		return true;
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#copyObject()
	 */
	public Object copyObject() {
		return new DscBoolean(this);
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.BaseType#getInvalidDisplay()
	 */
	protected String getInvalidDisplay() {
		return "";
	}

	/* (non-Javadoc)
	 * @see network.DscMessageAttribute#toXml()
	 */
	public String toXml() {
		// TODO Auto-generated method stub
		return "<boolean>"
		   + Boolean.toString(_value)
	       + "</boolean>";
	}

	/* (non-Javadoc)
	 * @see network.DscMessageAttribute#fromXml(java.lang.String)
	 */
	public void fromXml(String inXml) throws Exception {
		_value=Boolean.valueOf(Utilities.getAttributeValue("boolean", inXml)).booleanValue();
		
	}
	
	public String toString(){
		return Boolean.toString(_value);
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#reset()
	 */
	public void reset() {
		_done = false;
		_value = false;
	}
	
	public void setValue(ActiveField oValue) {
		
		if(oValue instanceof DscBoolean){
			DscBoolean src = (DscBoolean)oValue;
			
			_value = src._value;
			_value = src._done;
		}

	}

}
