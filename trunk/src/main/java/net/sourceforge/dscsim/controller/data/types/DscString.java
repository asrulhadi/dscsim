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
 
package net.sourceforge.dscsim.controller.data.types;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.MultiContentManager;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DscString extends BaseType {

	/**
	 * 
	 */
	private String _oCode = null;
	
	public DscString(String oCode) {
		super();		
		_oCode = oCode;	
	}
	
	public DscString(DscString oOther) {		
		
		if(oOther._oCode != null)
			_oCode = new String(oOther._oCode.toString());	
	}
	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isEntryField()
	 */
	public boolean isEntryField() {
		return false;
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#getFieldName()
	 */
	public String getFieldName() {
		return _oCode;
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#getDisplayValue()
	 */
	public String getDisplayValue() {
		// TODO Auto-generated method stub
		
		Object oObj = null;
		
		if(_oCode != null) {
			oObj = MultiContentManager.getProperties().getProperty(_oCode);
		}
			
		if(oObj != null) {
			return oObj.toString();
		}else{
			return null;
		}
			
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isComplete()
	 */
	public boolean isComplete() {

		if(_oCode != null)
			return true;
		else
			return false;
		
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#signal(applet.BusMessage)
	 */
	public int signal(BusMessage oMessage) {
		return 0;
	}



	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isValid()
	 */
	public String getCode() {
		return _oCode;
	}
	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isValid()
	 */
	public void setCode(String oCode) {
		_oCode = oCode;
	}
	
	public boolean isValid() {
		return true;
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#copyObject()
	 */
	public Object copyObject() {
		return new DscString(this);
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.BaseType#getInvalidDisplay()
	 */
	protected String getInvalidDisplay() {
		return "N/A";
	}

	/* (non-Javadoc)
	 * @see network.DscMessageAttribute#toXml()
	 */
	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see network.DscMessageAttribute#fromXml(java.lang.String)
	 */
	public void fromXml(String inXml) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public String toString(){
		return _oCode;
	}

	
	public void setValue(ActiveField oValue) {
	
		if(oValue instanceof DscString){
			
			DscString src = (DscString)oValue;
			_oCode = src._oCode;
		}
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
