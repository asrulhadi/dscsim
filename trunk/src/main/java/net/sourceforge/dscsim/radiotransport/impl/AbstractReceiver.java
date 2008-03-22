package net.sourceforge.dscsim.radiotransport.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.dscsim.radiotransport.Demodulator;
import net.sourceforge.dscsim.radiotransport.Frequency;
import net.sourceforge.dscsim.radiotransport.Receiver;

public abstract class AbstractReceiver {

	/**
	 * The connected demodulators
	 */
	protected Set _demodulators;
	/**
	 * The receivers frequency
	 */
	protected Frequency _frequency;
	/**
	 * The disabled bit
	 */
	protected boolean _disabled;

	public AbstractReceiver() {
		super();
		_demodulators = new HashSet();
		_frequency = new Frequency(0);  		// just a dummy
		_disabled = false;
	}

	public void addDemodulator(Demodulator demodulator) {
		synchronized(_demodulators) {
			_demodulators.add(demodulator);
		}
	}

	public void setFrequency(Frequency frequency) {
		_frequency = frequency;
	}

	public void setGain(int gain) {
		// TODO Auto-generated method stub
		// Does this make sense???
	
	}

	/**
	 * Propagate the received signal to the connected demodulators
	 * @param antennaSignal.getPayload() the received signal
	 */
	protected void receiveSignal(TransmissionPacket antennaSignal) {
		if( !_disabled ) {
			if( _frequency.equals(antennaSignal.getFrequency()) || getAntenna().isMaster() || antennaSignal.isMaster() ){
				synchronized(_demodulators){
					for(Iterator i = _demodulators.iterator(); i.hasNext(); ) {
						Demodulator d = (Demodulator)i.next();
						d.processSignal(antennaSignal.getPayload(), antennaSignal.getTransmitterUid(), antennaSignal.getFrequency(), antennaSignal.getSignalPower().add(getAntenna().getGain() ) );
					}
				}
			}
		}
	}

	public void disable(boolean disable) {
		_disabled = disable;
	}

	/**
	 * Gets the antenna to which this receiver is connected
	 * @return the antenna
	 */
	protected abstract AbstractAntenna getAntenna();

}