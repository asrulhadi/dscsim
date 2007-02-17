/*
 * The contents of this file are subject to the Mozilla Public License Version 1.0
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'dscsim'.
 *
 * The Initial Developer of the Original Code is Oliver Hecker. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.radio.core.impl;

import org.apache.log4j.Logger;

import net.sourceforge.dscsim.radio.core.RadioCore;
import net.sourceforge.dscsim.radio.core.RadioEventListener;
import net.sourceforge.dscsim.radio.core.VHFChannel;
import net.sourceforge.dscsim.radiotransport.Airwave;
import net.sourceforge.dscsim.radiotransport.Antenna;
import net.sourceforge.dscsim.radiotransport.Decibel;
import net.sourceforge.dscsim.radiotransport.Demodulator;
import net.sourceforge.dscsim.radiotransport.Frequency;
import net.sourceforge.dscsim.radiotransport.Receiver;
import net.sourceforge.dscsim.radiotransport.Transmitter;
import net.sourceforge.dscsim.util.ByteConverter;

/**
 * @author oliver
 *
 */
public class RadioCoreImpl implements RadioCore, Demodulator {

	/**
	 * The logger for this class
	 */
	private static Logger _logger = Logger.getLogger(RadioCoreImpl.class);	

	/**
	 * High transmission power (Watt)
	 */
	private static final double HIGH_POWER = 25.0;
	
	/**
	 * High transmission power (Watt)
	 */
	private static final double LOW_POWER = 1.0;
	
	/**
	 * Flag which indicates if SoundDataPackets should be aggregated to sequences
	 */
	private static final boolean USE_PACKET_SEQUENCES = true;

	/**
	 * The sequence of sound data packets (used when aggregating sound data packets)
	 */
	SoundDataPacketSequence _packetSequence;
	
	/**
	 * The audio capture and transmit thread
	 */
	private SoundCaptureThread _soundCaptureThread;
	
	/**
	 * The audio playback thread
	 */
	private SoundPlaybackThread _soundPlaybackThread;
	
	/**
	 * The antenna
	 */
	private Antenna _antenna;
	
	/**
	 * The transmitter
	 */
	private Transmitter _transmitter;

	/**
	 * The receiver
	 */
	private Receiver _receiver;
	
	/**
	 * Flag which indicates if the transmit button is pressed
	 */
	private boolean _transmit;
	
	/**
	 * The VHF channel
	 */
	private VHFChannel _channel;
	
	/**
	 * high/low power flag
	 */
	private boolean _high;
	
	/**
	 * masterswitch state
	 */
	private boolean _on;
	
	/**
	 * <code>true</code>: ship station; <code>false</code>: coast station
	 */
	private boolean _shipstation;
	
	/**
	 * proxy for RadioEvent listeners
	 */
	private ListenerProxy _listenerProxy;
	
	
	/**
	 * Buffer for incoming audio data; TODO: handle multiple sources
	 */
//	private ByteBuffer _audioByteBuffer;
	
	/**
	 * Buffer for incoming audio data
	 */
	private SoundSignalQueueGroup _audioDataBuffer;

	/**
	 * The volume of the sound
	 */
	private double _volume;
	
	/**
	 * The squelch level
	 */
	private double _squelch;

	/**
	 * Constructor
	 * @param shipStation <code>true</code> if this is a ship
	 */
	public RadioCoreImpl( boolean shipStation ) {
		_on = false;
		_shipstation = shipStation;
		_volume = 1.0;
		_squelch = 0.3;
		_listenerProxy = new ListenerProxy();
		_antenna = Airwave.getInstance().createAntenna();
		_transmitter = _antenna.createTransmitter();
		_receiver = _antenna.createReceiver();
		_receiver.addDemodulator(this);
		setPower(true);
		setChannel(VHFChannel.VHF_CHANNEL_16) ;
//		_audioByteBuffer = new ByteBuffer();
		_audioDataBuffer = new SoundSignalQueueGroup();
		_transmit = false;
		_packetSequence = new SoundDataPacketSequence();
		_soundCaptureThread = new SoundCaptureThread(this);
		_soundPlaybackThread = new SoundPlaybackThread(this);
		_soundCaptureThread.start();
		_soundPlaybackThread.start();
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setTransmit(boolean)
	 */
	public void setTransmit(boolean active) {
		_transmit = active;
		_soundCaptureThread.setTransmit(active);
		_receiver.disable(active);  //disable receiving while sending
	}

	/**
	 * @param sdp
	 */
	public void transmitSound(SoundDataPacket sdp) {
		if (USE_PACKET_SEQUENCES) {
			_packetSequence.addPacket(sdp);
			if (_packetSequence.isLast()) {
				if (_on && _transmit) {
					_transmitter.transmit(_packetSequence.getBytes(), null);
				}
				_packetSequence = new SoundDataPacketSequence();
			}
		} else {
			if (_on && _transmit) {
				_transmitter.transmit(sdp.getBytes(), null);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.dscsim.radiotransport.Demodulator#processSignal(byte[],
	 *      net.sourceforge.dscsim.radiotransport.Frequency, int)
	 */
	public void processSignal(byte[] signal, int transmitterUid, Frequency frequency, Decibel power) {
		int type = ByteConverter.byteArrayToInt(signal, 0);
		if( type == SoundDataPacketSequence.PACKET_TYPE ) {
			SoundDataPacketSequence inSequence = SoundDataPacketSequence.getFromBytes(signal, signal.length);
			while( !inSequence.isLast() ) {
				SoundDataPacket sdp = inSequence.getNextPacket();
				if( sdp != null ){
					_audioDataBuffer.add(transmitterUid, power, sdp.getDataBytes(), sdp.getSequenceNumber());
				}
			}
		} else {
			SoundDataPacket sdp = SoundDataPacket.getFromBytes(signal, 0, signal.length);
			if( sdp != null ){
				_audioDataBuffer.add(transmitterUid, power, sdp.getDataBytes(), sdp.getSequenceNumber());
			}
		}
	}

//	/**
//	 * @return the _audioByteBuffer
//	 */
//	public ByteBuffer getAudioByteBuffer() {
//		return _audioByteBuffer;
//	}

	/**
	 * @return the audioDataBuffer
	 */
	public SoundSignalQueueGroup getAudioDataBuffer() {
		return _audioDataBuffer;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#getChannel()
	 */
	public VHFChannel getChannel() {
		return _channel;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#getPower()
	 */
	public boolean getPower() {
		return _high;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#registerListener(net.sourceforge.dscsim.radio.core.RadioEventListener)
	 */
	public void registerListener(RadioEventListener listener) {
		_listenerProxy.registerListener(listener);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#sendDscSignal(byte[])
	 */
	public void sendDscSignal(byte[] dscSignal) {
		if( !_on ) {
			_logger.debug("Not sending DSC-Signal because radio is OFF ");
			return;
		}
		_logger.debug("Sending DSC-Signal");
		VHFChannel oldChannel = getChannel();
		boolean oldPower = getPower();
		_receiver.disable(true);  // disable receiver (also switched to 70)
		setChannel(VHFChannel.VHF_CHANNEL_70);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_transmitter.transmit(dscSignal, null);
		setPower(oldPower);
		setChannel(oldChannel);
		_receiver.disable(false);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setChannel(int)
	 */
	public void setChannel(VHFChannel channel) {
		_channel = channel;
		_listenerProxy.notifyChannel();
		if(_shipstation){
			_transmitter.setFrequency(channel.getShipStationFrequency());
			_receiver.setFrequency(channel.getCoastStationFrequency());
		} else {
			_transmitter.setFrequency(channel.getCoastStationFrequency());
			_receiver.setFrequency(channel.getShipStationFrequency());
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setMasterSwitch(boolean)
	 */
	public void setMasterSwitch(boolean on) {
		if( _on != on) {
			_on = on;
			_listenerProxy.notifyMasterSwitch();
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#getMasterSwitch()
	 */
	public boolean getMasterSwitch() {
		return _on;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setPower(boolean)
	 */
	public void setPower(boolean high) {
		_high=high;
		_transmitter.setPower((float)(high ? HIGH_POWER : LOW_POWER) );
		_listenerProxy.notifyPower();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setSquelch(double)
	 */
	public void setSquelch(double squelch) {
		if(squelch > 1.0) {
			squelch = 1.0;
		}
		if(squelch < 0.0 ) {
			squelch = 0.0;
		}
		_squelch = squelch;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#setVolume(double)
	 */
	public void setVolume(double volume) {
		if(volume > 1.0) {
			volume = 1.0;
		}
		if(volume < 0.0 ) {
			volume = 0.0;
		}
		_volume = volume;
	}


	/**
	 * @return the volume
	 */
	public double getVolume() {
		return _volume;
	}

	/**
	 * @return the squelch
	 */
	public double getSquelch() {
		return _squelch;
	}


	/**
	 * @return the _transmit
	 */
	public boolean getTransmit() {
		return _transmit;
	}


	public Decibel getSquelchAsDecibel() {
		Decibel ref = Decibel.fromLinearPower(NoiseSoundSignalQueue.NOISE_POWER);
		double diff = _squelch - 0.2;
		return ref.add(Decibel.fromDb(10.0 * 10 *diff )); // 10 dB per 0.1 tick 
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radio.core.RadioCore#getAntenna()
	 */
	public Antenna getAntenna() {
		return _antenna;
	}
	
}
