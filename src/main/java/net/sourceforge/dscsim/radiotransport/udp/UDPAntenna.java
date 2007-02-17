/**
 * 
 */
package net.sourceforge.dscsim.radiotransport.udp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.dscsim.radiotransport.Antenna;
import net.sourceforge.dscsim.radiotransport.Decibel;
import net.sourceforge.dscsim.radiotransport.GeoPosition;
import net.sourceforge.dscsim.radiotransport.PropagationModel;
import net.sourceforge.dscsim.radiotransport.Receiver;
import net.sourceforge.dscsim.radiotransport.Transmitter;

/**
 * @author oliver
 *
 */
public class UDPAntenna implements Antenna {
	
	/**
	 * The Airwave object to use
	 */
	private UDPAirwave _airwave;
	
	/**
	 * The elevation of the antenna (metres above sea level)
	 */
	private int _elevation;
	
	/**
	 * The geographic position of the antenna
	 */
	private GeoPosition _position;
	
	/**
	 * The gain of the antenna
	 */
	private Decibel _gAnt;
	
	/**
	 * The master flag (enables unlimitted coverage)
	 */
	private boolean _master;
	
	/**
	 * The set of connected receivers
	 */
	private Set _receivers;
	
	/**
	 * The set of connected transmitters
	 */
	private Set _transmitters;
	
	/**
	 * Constructor which takes the Airwave as argument
	 * @param _airwave the airwave to use
	 */
	UDPAntenna(UDPAirwave airwave) {
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

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Antenna#setElevation(int)
	 */
	public void setElevation(int elevation) {
		_elevation = elevation;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Antenna#setMaster(boolean)
	 */
	public void setMaster(boolean flag) {
		_master = flag;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Antenna#setPostion(net.sourceforge.dscsim.radiotransport.GeoPosition)
	 */
	public void setPostion(GeoPosition position) {
		_position = position;
	}

	/**
	 * Gets the gain of the antenna
	 * @return the gain in decibel
	 */
	public Decibel getGain() {
		return _gAnt;
	}
	
	/**
	 * Process the signal coming via the airwave
	 * @param signal the incoming antenna signal
	 */
	void receiveSignal(UDPTransmissionPacket antennaSignal) {
		synchronized(_receivers) {
			for(Iterator i = _receivers.iterator(); i.hasNext(); ) {
				UDPReceiver r = (UDPReceiver)i.next();
				antennaSignal = (UDPTransmissionPacket)antennaSignal.clone();
				GeoPosition otherPos = antennaSignal.getPosition();
				int distance = GeoPosition.computeDistance(otherPos, _position);
				Decibel attenuation = PropagationModel.computeAttenuation(distance);
				antennaSignal.amplify(attenuation);
				r.receiveSignal(antennaSignal);
			}
		}
	}

	/**
	 * Sending a signal coming from a transmitter
	 */
	void sendSignal(UDPTransmissionPacket packet) {
		//TODO: add additional information
		packet.setElevation(_elevation);
		packet.setPosition(_position);
		packet.setMaster(_master);
		packet.setSignalPower(packet.getSignalPower().add(_gAnt));
		_airwave.sendSignal(packet);
	}

	/**
	 * Gets the master flag
	 * @return the master flag
	 */
	public boolean isMaster() {
		return _master;
	}
}
