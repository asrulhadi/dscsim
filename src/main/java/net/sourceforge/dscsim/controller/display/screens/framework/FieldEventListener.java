package net.sourceforge.dscsim.controller.display.screens.framework;

import net.sourceforge.dscsim.controller.BusMessage;

public interface FieldEventListener {
	
	public void onShow();
	public void onHide();
	
	public boolean tab(BusMessage msg);
	
	public void onFocusGain();
	public void onFocusLose();

}
