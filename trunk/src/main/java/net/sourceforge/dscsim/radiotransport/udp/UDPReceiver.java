/**
 * 
 */
package net.sourceforge.dscsim.radiotransport.udp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.dscsim.radiotransport.Demodulator;
import net.sourceforge.dscsim.radiotransport.Frequency;
import net.sourceforge.dscsim.radiotransport.Receiver;

/**
 * @author oliver
 *
 */
public class UDPReceiver implements Receiver {
	
	/**
	 * The connected antenna
	 */
	private UDPAntenna _antenna;
	
	/**
	 * The connected demodulators
	 */
	private Set _demodulators;
	
	/**
	 * The receivers frequency
	 */
	private Frequency _frequency;
	
	/**
	 * The disabled bit
	 */
	private boolean _disabled;

	/**
	 * Constructor which takes the antenna as argument
	 * @param antenna the connected antenna
	 */
	UDPReceiver(UDPAntenna antenna) {
		_antenna = antenna;
		_demodulators = new HashSet();
		_frequency = new Frequency(0);  		// just a dummy
		_disabled = false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Receiver#addDemodulator(net.sourceforge.dscsim.radiotransport.Demodulator)
	 */
	public void addDemodulator(Demodulator demodulator) {
		synchronized(_demodulators) {
			_demodulators.add(demodulator);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Receiver#setFrequency(net.sourceforge.dscsim.radiotransport.Frequency)
	 */
	public void setFrequency(Frequency frequency) {
		_frequency = frequency;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Receiver#setGain(int)
	 */
	public void setGain(int gain) {
		// TODO Auto-generated method stub
		// Does this make sense???

	}

	/**
	 * Propagate the received signal to the connected demodulators
	 * @param antennaSignal.getPayload() the received signal
	 */
	void receiveSignal(UDPTransmissionPacket antennaSignal) {
		if( !_disabled ) {
			if( _frequency.equals(antennaSignal.getFrequency()) || _antenna.isMaster() || antennaSignal.isMaster() ){
				synchronized(_demodulators){
					for(Iterator i = _demodulators.iterator(); i.hasNext(); ) {
						Demodulator d = (Demodulator)i.next();
						d.processSignal(antennaSignal.getPayload(), antennaSignal.getTransmitterUid(), antennaSignal.getFrequency(), antennaSignal.getSignalPower().add(_antenna.getGain() ) );
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Receiver#disable(boolean)
	 */
	public void disable(boolean disable) {
		_disabled = disable;
	}
	

}
