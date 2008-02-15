package net.sourceforge.dscsim.controller.network;

import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.utils.AppLogger;

public class NullTransmitterReceiver implements DscsimTransmitter, DscsimReceiver {

	public void transmit(Dscmessage oMsg) {
		AppLogger.debug2("NullTransmitterReceiver.transmit called");		
	}

	public void addDependent(InternalBusListener oDependent) {
		AppLogger.debug2("NullTransmitterReceiver.addDependent called");
	}

	public void turnon() {
		AppLogger.debug2("NullTransmitterReceiver.turnon called");
		
	}

}
