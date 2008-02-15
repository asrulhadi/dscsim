/*
 * Created on 16.07.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.dscsim.controller.data.types;

import java.io.Serializable;

import net.sourceforge.dscsim.controller.BusListener;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.network.DscMessageAttribute;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class BaseType implements ActiveField, DscMessageAttribute, Constants, Serializable {


	transient protected Object _oBean = null;
	
	protected abstract String getInvalidDisplay();

	public BaseType(){
		
	}
	
	public boolean isReadOnly(){
		
		return false;
	}
	
}
