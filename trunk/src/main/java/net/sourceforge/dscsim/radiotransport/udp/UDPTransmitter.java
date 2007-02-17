/**
 * 
 */
package net.sourceforge.dscsim.radiotransport.udp;

import java.util.Random;

import net.sourceforge.dscsim.radiotransport.Decibel;
import net.sourceforge.dscsim.radiotransport.Frequency;
import net.sourceforge.dscsim.radiotransport.Transmitter;

/**
 * @author oliver
 *
 */
public class UDPTransmitter implements Transmitter {

	/**
	 * The uid of the transmitter
	 */
	private int _uid;
	
	/**
	 * The connected antenna
	 */
	private UDPAntenna _antenna;
	
	/**
	 * The frequency at which the transmitter is working
	 */
	private Frequency _frequency;
	
	/**
	 * The power at which the transmitter is working (stored as decibel)
	 */
	private Decibel _power;
	
	/**
	 * Constructor which takes the antenna as argument
	 * @param antenna the connected antenna
	 */
	UDPTransmitter(UDPAntenna antenna) {
		while( _uid <= 0){
			// do not allow the uid to be less or equal to 0
			_uid = new Random().nextInt(); // lets assume this globally unique...
		}
		_antenna = antenna;
		_frequency = new Frequency(0);  		// just a dummy
		_power = Decibel.fromLinearPower(1.0);	// just a dummy
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Transmitter#setFrequency(net.sourceforge.dscsim.radiotransport.Frequency)
	 */
	public void setFrequency(Frequency frequency) {
		_frequency = frequency;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Transmitter#setPower(float)
	 */
	public void setPower(float power) {
		_power = Decibel.fromLinearPower(power);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Transmitter#transmit(byte[], net.sourceforge.dscsim.radiotransport.Transmitter.Hint)
	 */
	public void transmit(byte[] signal, Hint hint) {
		UDPTransmissionPacket packet = new UDPTransmissionPacket();
		packet.setTransmitterUid(_uid);
		packet.setFrequency(_frequency);
		packet.setSignalPower(_power);
		packet.setPayload(signal);
		_antenna.sendSignal(packet);
	}

}
