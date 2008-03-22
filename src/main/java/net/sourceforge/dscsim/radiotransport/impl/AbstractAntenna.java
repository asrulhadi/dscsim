package net.sourceforge.dscsim.radiotransport.impl;

import java.util.Iterator;
import java.util.Set;

import net.sourceforge.dscsim.radiotransport.Antenna;
import net.sourceforge.dscsim.radiotransport.Decibel;
import net.sourceforge.dscsim.radiotransport.GeoPosition;
import net.sourceforge.dscsim.radiotransport.PropagationModel;

public abstract class AbstractAntenna {

	/**
	 * The elevation of the antenna (metres above sea level)
	 */
	protected int _elevation;
	/**
	 * The geographic position of the antenna
	 */
	protected GeoPosition _position;
	/**
	 * The gain of the antenna
	 */
	protected Decibel _gAnt;
	/**
	 * The master flag (enables unlimitted coverage)
	 */
	protected boolean _master;
	/**
	 * The set of connected receivers
	 */
	protected Set _receivers;
	/**
	 * The set of connected transmitters
	 */
	protected Set _transmitters;

	public AbstractAntenna() {
		super();
	}

	public void setElevation(int elevation) {
		_elevation = elevation;
	}

	public void setMaster(boolean flag) {
		_master = flag;
	}

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
	public void receiveSignal(TransmissionPacket antennaSignal) {
		synchronized(_receivers) {
			for(Iterator i = _receivers.iterator(); i.hasNext(); ) {
				AbstractReceiver r = (AbstractReceiver)i.next();
				antennaSignal = (TransmissionPacket)antennaSignal.clone();
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
	public void sendSignal(TransmissionPacket packet) {
		//TODO: add additional information
		packet.setElevation(_elevation);
		packet.setPosition(_position);
		packet.setMaster(_master);
		packet.setSignalPower(packet.getSignalPower().add(_gAnt));
		getAirwave().sendSignal(packet);
	}

	/**
	 * Gets the master flag
	 * @return the master flag
	 */
	public boolean isMaster() {
		return _master;
	}
	
	/**
	 * Gets the connected airwave
	 * @return the connected airwave
	 */
	protected abstract AbstractAirwave getAirwave();

}