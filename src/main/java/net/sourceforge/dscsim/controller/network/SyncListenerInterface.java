package net.sourceforge.dscsim.controller.network;

public interface SyncListenerInterface {

	public abstract void addSubscriber(SyncListenerSubscriber oSubscriber);

	public abstract void removeSubscriber(SyncListenerSubscriber oSubscriber);

}