package net.sourceforge.dscsim.controller.data.types;

import net.sourceforge.dscsim.controller.BusMessage;


public interface ActiveField  {

	public abstract String getDisplayValue();
	public abstract boolean isComplete();
	public abstract int signal(BusMessage oMessage);	
	public abstract boolean isValid();	
	public abstract void reset();	
	public abstract Object copyObject();
	public abstract void setValue(ActiveField oValue);
	

}