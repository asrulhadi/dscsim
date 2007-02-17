/*
 * Created on 05.08.2006
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

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.screen.types.ActiveField;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Element;


import java.lang.reflect.Method;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ContextBean implements ActiveField, Constants {

	/**
	 * 
	 */
	private Element _beanElement = null;
	private InstanceContext _instanceCtx = null;
	private Object _ctxObject = null;
	
	private Class _ctxClass = null;
	private Method _fetchMethod = null;
	
	public Object copyObject(){
		throw new RuntimeException("Bean.copyObject not implemented.");
	}

	public ContextBean(Element oElement, InstanceContext oInstanceContext) throws Exception {
		
		_beanElement = oElement;
		_instanceCtx = oInstanceContext;
		
		String ctxName = oElement.getAttributeValue("context");
		Element ctxElement = oInstanceContext.getContentManager().getContextElement(ctxName);
				
		_ctxObject = MultiContentManager.getContextPropertyEx(_instanceCtx, ctxName);
		
		_ctxClass  = Class.forName(ctxElement.getAttributeValue("class"));
		
		String methodName = _beanElement.getAttributeValue("method");

		_fetchMethod = _ctxClass.getMethod(methodName, new Class[]{});
				
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isEntryField()
	 */
	public boolean isEntryField() {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		
		Object retValue = "";
		try{
			retValue = _fetchMethod.invoke(this._ctxObject, new Object[]{});
			
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}

		return retValue.toString();
		
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isComplete()
	 */
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#signal(applet.BusMessage)
	 */
	public int signal(BusMessage oMessage) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#setElement(org.jdom.Element)
	 */
	public void setElement(Element oElement) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isValid()
	 */
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}

	public void fromXml(String inStr) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#reset()
	 */
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#setValue(applet.screen.bean.types.ActiveField)
	 */
	public void setValue(ActiveField oValue) {
		throw new RuntimeException("method not implemented.");
	}
	
	public Element getElement(){
		return _beanElement;
	}

}
