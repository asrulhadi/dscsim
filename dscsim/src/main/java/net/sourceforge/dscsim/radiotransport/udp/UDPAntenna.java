/**
 * 
 */
package net.sourceforge.dscsim.radiotransport.udp;

import java.util.HashSet;

import net.sourceforge.dscsim.radiotransport.Antenna;
import net.sourceforge.dscsim.radiotransport.Decibel;
import net.sourceforge.dscsim.radiotransport.GeoPosition;
import net.sourceforge.dscsim.radiotransport.Receiver;
import net.sourceforge.dscsim.radiotransport.Transmitter;
import net.sourceforge.dscsim.radiotransport.impl.AbstractAirwave;
import net.sourceforge.dscsim.radiotransport.impl.AbstractAntenna;

/**
 * @author oliver
 *
 */
public class UDPAntenna extends AbstractAntenna implements Antenna {
	
	/**
	 * The Airwave object to use
	 */
	AbstractAirwave _airwave;
	
	/**
	 * Constructor which takes the Airwave as argument
	 * @param _airwave the airwave to use
	 */
	UDPAntenna(AbstractAirwave airwave) {
		super();
		_airwave = airwave;
		_master = false;
		_elevation = 0;
		_position = new GeoPosition(0,0);
		_receivers = new HashSet();
		_transmitters = new HashSet();
		_gAnt = Decibel.fromDb(2.15);   // assume dipol  
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Antenna#createReceiver()
	 */
	public Receiver createReceiver() {
		UDPReceiver receiver = new UDPReceiver(this);
		synchronized(_receivers) {
			_receivers.add(receiver);
		}
		return receiver;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Antenna#createTransmitter()
	 */
	public Transmitter createTransmitter() {
		UDPTransmitter transmitter = new UDPTransmitter(this);
		synchronized(_transmitters) {
			_transmitters.add(transmitter);
		}
		return transmitter;
	}

	/**
	 * @return the _airwave
	 */
	@Override
	protected AbstractAirwave getAirwave() {
		return _airwave;
	}
}
