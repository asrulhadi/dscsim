/*
 * Created on 16.07.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.dscsim.controller.screen.types;

import java.io.Serializable;

import net.sourceforge.dscsim.controller.BusListener;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.network.DscMessageAttribute;

import org.jdom.Element;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class BaseType implements ActiveField, DscMessageAttribute, Constants, Serializable {

	transient protected Element _oElement = null;
	transient protected Object _oBean = null;
	
	protected abstract String getInvalidDisplay();

	public BaseType(){
		
	}
	
	public BaseType(BaseType oOther){
		_oElement = new Element("bean");
		_oBean = oOther._oBean;
	}
	public BaseType(Element oElement, Object oBean){
		_oElement = oElement;
		_oBean = oBean;
		
	}
	public boolean isEntryField() {
		return _oElement.getAttribute("attribute") != null 
		|| _oElement.getAttribute("property") != null;
	}
	public String getFieldName() {
		return _oElement.getAttributeValue("name");
	}
	
	public void setElement(Element oElement){
		_oElement = oElement;
	}
	public boolean isReadOnly(){
		
		if(_oElement == null)
			return true;
		
		String mode = _oElement.getAttributeValue("mode");
		if(mode != null){			
			if(mode.equals("readonly"))
				return true;
		}
	
		return false;
	}
	
	public Element getElement(){
		return _oElement;
	}

}
