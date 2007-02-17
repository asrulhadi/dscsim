/*
 * Created on 29.06.2006
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

import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Attribute;
import org.jdom.Element;



/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ScreenLine  {
	
	protected Element _oInputLine = null;
	protected InstanceContext _oInstanceContext = null;
	
	public ScreenLine(Element oInputLine){
		_oInputLine =  oInputLine;
	}
	
	public ScreenLine(Element oInputLine, InstanceContext oContext){
		_oInputLine =  oInputLine;
		_oInstanceContext = oContext;
	}
	
	
	
	public Element getElement(){
		return _oInputLine;
	}
	public String getAttributeValue(String attrName){
		String attrValue = null;
		
		Attribute oAttr = _oInputLine.getAttribute(attrName);
		
		attrValue = oAttr != null ? oAttr.getValue() : "";
				
		return attrValue;
	}
	
	public int getAttributeIntValue(String attrName){
		int attrValue = 0;
		
		Attribute oAttr = _oInputLine.getAttribute(attrName);
		
		try {
			attrValue = oAttr != null ? oAttr.getIntValue() :  0;	
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
		
				
		return attrValue;
	}

	public boolean isVisible(){
		
		String condition = _oInputLine.getAttributeValue("condition");
		
		if(condition == null)
			return true;
		
		return _oInstanceContext.getContentManager().isCondition(condition);
		
	}
	
	public abstract String getDisplayText();

	
}

