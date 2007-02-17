package net.sourceforge.dscsim.controller.network;

public interface SyncPublisherInterface {

	public abstract void sendSync(String strMMSI, Object oBusMessage);

}