package net.sourceforge.dscsim.radiotransport.impl;

import java.util.Random;

import net.sourceforge.dscsim.radiotransport.Decibel;
import net.sourceforge.dscsim.radiotransport.Frequency;
import net.sourceforge.dscsim.radiotransport.Transmitter;

public abstract class AbstractTransmitter {

	/**
	 * The uid of the transmitter
	 */
	private int _uid;
	/**
	 * The frequency at which the transmitter is working
	 */
	private Frequency _frequency;
	/**
	 * The power at which the transmitter is working (stored as decibel)
	 */
	private Decibel _power;

	public AbstractTransmitter() {
		super();
		while( _uid <= 0){
			// do not allow the uid to be less or equal to 0
			_uid = new Random().nextInt(); // lets assume this globally unique...
		}
		_frequency = new Frequency(0);  		// just a dummy
		_power = Decibel.fromLinearPower(1.0);	// just a dummy
	}

	public void setFrequency(Frequency frequency) {
		_frequency = frequency;
	}

	public void setPower(float power) {
		_power = Decibel.fromLinearPower(power);
	}

	/**
	 * Creates the transmission packet which encapsulates the radio signal and
	 * all parameters
	 * @param signal the radio signal containing the transmitted data
	 * @return the transmission packet
	 */
	protected TransmissionPacket createTransmissionPacket(byte[] signal) {
		TransmissionPacket packet = new TransmissionPacket();
		packet.setTransmitterUid(_uid);
		packet.setFrequency(_frequency);
		packet.setSignalPower(_power);
		packet.setPayload(signal);
		return packet;
	}

}