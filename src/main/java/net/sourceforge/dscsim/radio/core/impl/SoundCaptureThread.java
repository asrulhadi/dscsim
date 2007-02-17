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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.apache.log4j.Logger;
import org.xiph.speex.SpeexEncoder;

/**
 * Implements the thread for capturing and possibly transmitting the
 * sound captured from the microphone
 */
public class SoundCaptureThread implements Runnable {

	/**
	 * The logger to use for this class
	 */
	Logger _logger = Logger.getLogger(SoundCaptureThread.class);
	
	/**
	 * Sample rate for capturing; 44100 should be available on all platforms (?)
	 */
	private static final float CAPTURE_SAMPLE_RATE = 44100.0f;
	
	/**
	 * Sample rate for transmitting; 8000 is the prefered rate for SPEEX
	 */
	private static final float TRANSMIT_SAMPLE_RATE = 8000.0f;

	/**
	 * Sample size in bits; 16 should be available on all platforms (?)
	 */
	private static final int SAMPLE_SIZE = 16;
	
	/**
	 * Number of channels; 2 (stereo) should be available on all platforms (?)
	 */
	private static final int CHANNEL_NUMBER = 2;
	
    /**
     * Samples should be signed
     */
	private static final boolean SAMPLES_SIGNED = true;
	
	/**
	 * Do not use big endian; use little endian instead
	 */
	private static final boolean BIG_ENDIAN = false;
	
	/**
	 * The size of the pcm byte buffer for input to the Speex encoder
	 */
	private static final int PCM_BUFFER_SIZE = 640;
	
	/**
	 * The mode of the speex encoder (0 = NARROWBAND)
	 */
	private static final int SPEEX_MODE = 0;
	
	/**
	 * The quality of the speex encoding
	 */
	private static final int SPEEX_QUALITY = 5;
	
	/**
	 * The default preamplification
	 */
	private static final double PREAMPLIFY = 10.0;

	/**
	 * The RadioCoreImpl to which this thread belongs
	 */
	private RadioCoreImpl _radioCoreImpl;
	
	/**
	 * The line from which the sound data is captured
	 */
    private TargetDataLine _line;

    /**
     * Audio stream before sample rate conversion
     */
    private AudioInputStream _rawStream;
    
    /**
     * Audio stream after sample rate conversion
     */
    private AudioInputStream _condStream;
    
    /**
     * The (J)SPEEX encoder
     */
    private SpeexEncoder _encoder;
    
    /**
     * Flag which indicates if one time logging has been done
     */
    private boolean _loggingDone;
    
    /**
     * The preamplification value
     */
    private double _preamplification;
   
    /**
     * The thread which performs the capturing/transmitting of the sound
     */
    private Thread _thread;
    
    /**
     * Flag which indicates if sound should be transmitted
     */
    private boolean _transmit;
    
    /**
     * Constructor which initializes the audio line
     * @param radioCoreImpl the RadioCoreImpl object (parent)
     */
    public SoundCaptureThread(RadioCoreImpl radioCoreImpl) {
    	this._radioCoreImpl = radioCoreImpl;
    	_transmit = false;
    	_preamplification = PREAMPLIFY;
    	// set up the target line (input line)
    	AudioFormat captureFormat = 
    		new AudioFormat( CAPTURE_SAMPLE_RATE, SAMPLE_SIZE, CHANNEL_NUMBER, 
    				         SAMPLES_SIGNED, BIG_ENDIAN);
    	DataLine.Info info = new DataLine.Info(TargetDataLine.class, captureFormat);
    	try {
			_line = (TargetDataLine)AudioSystem.getLine(info);
	        _line.open(_line.getFormat(), _line.getBufferSize());
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			_logger.error("",e);
//			AppLogger.error(e);
		}
        AudioFormat form = _line.getFormat();
        _logger.debug("Audio format of input line: "+form);
 //       AppLogger.debug("Audio format of input line: "+form);
        //set up the sample rate conversion
    	AudioFormat transmitFormat = 
    		new AudioFormat( TRANSMIT_SAMPLE_RATE, SAMPLE_SIZE, CHANNEL_NUMBER, 
			         SAMPLES_SIGNED, BIG_ENDIAN);
        _rawStream = new AudioInputStream(_line);
        _condStream = AudioSystem.getAudioInputStream(transmitFormat, _rawStream );
        // set up the SPEEX encoder
        _encoder = new SpeexEncoder();
		_encoder.init(SPEEX_MODE, SPEEX_QUALITY,
				     (int)_condStream.getFormat().getSampleRate(), 
				     _condStream.getFormat().getChannels());
    }
    
    public void start() {
        _thread = new Thread(this);
        _thread.setName("Capture");
        _thread.start();
    }

    public void stop() {
        _thread = null;
    }
 
    public void run() {

    	ChannelAlligner alligner = new ChannelAlligner();
    	VolumeControl volumeControl = new VolumeControl(_preamplification);
    	CompressorAmplifier compressorAmplifier = new CompressorAmplifier();
        // the buffer
        byte[] inData = new byte[PCM_BUFFER_SIZE];
        byte[] speexData;;
        _line.start();

        int sequence = 0;
        try {
        	while (_thread != null) {
                speexData = new byte[PCM_BUFFER_SIZE];
                synchronized(this) {
                	boolean wasTransmitting = _transmit; 
                	while( !_transmit ) {
                		this.wait();
                	}
                	if( !wasTransmitting ) {
                		// skip everything that was already in the buffer
                		_condStream.skip(_condStream.available());
                	}
                }
            	int bytesRead = _condStream.read(inData, 0, PCM_BUFFER_SIZE);
                if( bytesRead == -1) {
                    break;
                }
            	alligner.process(inData,!BIG_ENDIAN);
               	compressorAmplifier.process(inData, !BIG_ENDIAN);
//               	volumeControl.process(inData, !BIG_ENDIAN);
                SoundDataPacket sdp;
    			_encoder.processData(inData, 0, inData.length);
    			int compressedBytes = _encoder.getProcessedData(speexData, 0);
    			if( !_loggingDone ) {
    				_logger.debug("Compressed audio packet size is "+compressedBytes);
    				_loggingDone = true;
    			}
                sdp = new SoundDataPacket(sequence++,compressedBytes);
                sdp.setDataBytes(speexData);
                _radioCoreImpl.transmitSound(sdp);
            }
        } catch( Exception e) {
        	_logger.error("",e);
//        	AppLogger.error(e);
        }
        // we reached the end of the stream.  stop and close the line.
        _line.stop();
        _line.close();
        _line = null;
    }
    
    /**
     * Sets the transmit flag
     */
    public synchronized void setTransmit(boolean transmit) {
    	_transmit = transmit;
    	notify();
    }

}
