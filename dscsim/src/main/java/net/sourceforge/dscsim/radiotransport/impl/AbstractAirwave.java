package net.sourceforge.dscsim.radiotransport.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.sourceforge.dscsim.radiotransport.Airwave;
import net.sourceforge.dscsim.radiotransport.AirwaveStatusInterface;
import net.sourceforge.dscsim.radiotransport.AirwaveStatusListener;

public abstract class AbstractAirwave extends Airwave {

	/**
	 * The antennas which exist for this airwave
	 */
	protected Set _antennas;
	/**
	 * The (hopefully) global unique ID of this UDPAirwave
	 */
	protected int _uid;
	/**
	 * The set of listeners for the network status
	 */
	protected Set _networkStatusListeners;

	public AbstractAirwave() {
		super();
		while( _uid <= 0){
			// do not allow the uid to be less or equal to 0
			_uid = new Random().nextInt(); // lets assume this globally unique...
		}
		_antennas = new HashSet();
	}

    /**
     * Sends a given signal to all antennas (local and remote)
     * @param aPacket the signal packet to send
     */
	public abstract void sendSignal(TransmissionPacket antennaSignal);

	/**
	 * Pushes a signal to all antennas of this airwave
	 * @param antennaSignal the signal to be pushed to the antennas
	 */
	protected void pushSignalToLocalAntennas(TransmissionPacket antennaSignal) {
		synchronized(_antennas){
	    	for( Iterator i = _antennas.iterator(); i.hasNext(); ) {
	    		AbstractAntenna a = (AbstractAntenna)i.next();
	    		a.receiveSignal(antennaSignal);
	    	}
		}
	}

	public void registerStatusListener(AirwaveStatusListener listener) {
		synchronized( _networkStatusListeners ) {
			_networkStatusListeners.add(listener);
		}
	}

	/**
	 * Notify all registered status listeners that status has changed
	 */
	protected void notifyStatusListeners() {
		synchronized(_networkStatusListeners) {
			for( Iterator i = _networkStatusListeners.iterator(); i.hasNext(); ) {
				((AirwaveStatusListener)i.next()).notifyNetworkStatus();
			}
		}
	}

}