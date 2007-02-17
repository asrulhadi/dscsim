package net.sourceforge.dscsim.controller.screen.types;

import net.sourceforge.dscsim.controller.BusMessage;

import org.jdom.Element;

public interface ActiveField  {

	public abstract boolean isEntryField();
	public abstract String getFieldName();
	public abstract String getDisplayValue();
	public abstract boolean isComplete();
	public abstract int signal(BusMessage oMessage);	
	public abstract void setElement(Element oElement);	
	public abstract Element getElement();	
	public abstract boolean isValid();	
	public abstract void reset();	
	public abstract Object copyObject();
	public abstract void setValue(ActiveField oValue);
	

}