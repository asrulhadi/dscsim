package net.sourceforge.dscsim.controller.network;

import net.sourceforge.dscsim.controller.utils.AppLogger;

public class NullSyncPublisherListener implements SyncListenerInterface, SyncPublisherInterface{

	public void addSubscriber(SyncListenerSubscriber oSubscriber) {
		//AppLogger.debug2("NullSyncPublisherListener.addSubscriber called");

	}

	public void removeSubscriber(SyncListenerSubscriber oSubscriber) {
		//AppLogger.debug2("NullSyncPublisherListener.removeSubscriber called");
		
	}

	public void sendSync(String strMMSI, Object oBusMessage) {
		//AppLogger.debug2("NullSyncPublisherListener.sendSync called");
		
	}

}
