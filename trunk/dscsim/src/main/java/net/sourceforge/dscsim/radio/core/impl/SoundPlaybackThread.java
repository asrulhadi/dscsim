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
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.radiotransport.Decibel;

import org.apache.log4j.Logger;
import org.xiph.speex.SpeexDecoder;
import org.xiph.speex.SpeexEncoder;

/**
 * Implements the thread for playing back the audio signal which
 * was received
 */
public class SoundPlaybackThread implements Runnable {
	
	/**
	 * The logger to use for this class
	 */
	Logger _logger = Logger.getLogger(SoundPlaybackThread.class);
	
	/**
	 * Sample rate for transmitting; 8000 is the prefered rate for SPEEX
	 */
	public static final float TRANSMIT_SAMPLE_RATE = 8000.0f;

	/**
	 * Sample size in bits; 16 should be available on all platforms (?)
	 */
	private static final int SAMPLE_SIZE = 16;
	
	/**
	 * Number of channels; 2 (stereo) should be available on all platforms (?)
	 */
	public static final int CHANNEL_NUMBER = 2;
	
    /**
     * Samples should be signed
     */
	private static final boolean SAMPLES_SIGNED = true;
	
	/**
	 * Do not use big endian; use little endian instead
	 */
	private static final boolean BIG_ENDIAN = false;
	
	/**
	 * The mode of the speex encoder (0 = NARROWBAND)
	 */
	public static final int SPEEX_MODE = 0;
	
	/**
	 * The size of the audio buffer of the line
	 */
	private static final int AUDIO_BUFFER_SIZE = 5000; //20000

	/**
	 * The RadioCoreImpl to which this thread belongs
	 */
	private RadioCoreImpl _radioCoreImpl;
	
	/**
	 * The line to which the audio data is sent
	 */
    private SourceDataLine _line;

    /**
     * The (J)SPEEX decoder
     */
    private SpeexDecoder _decoder;
   
    /**
     * The thread which performs the capturing/transmitting of the sound
     */
    private Thread _thread;
    
    /**
     * Constructor which initializes the audio line
     * @param radioCoreImpl the RadioCoreImpl object (parent)
     */
    public SoundPlaybackThread(RadioCoreImpl radioCoreImpl) {
    	this._radioCoreImpl = radioCoreImpl; 
    	// set up the target line (input line)
    	AudioFormat playbackFormat = 
    		new AudioFormat( TRANSMIT_SAMPLE_RATE, SAMPLE_SIZE, CHANNEL_NUMBER, 
    				         SAMPLES_SIGNED, BIG_ENDIAN);
    	DataLine.Info info = new DataLine.Info(SourceDataLine.class, playbackFormat);
    	try {
			_line = (SourceDataLine)AudioSystem.getLine(info);
	        _line.open(_line.getFormat(), AUDIO_BUFFER_SIZE );
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			_logger.error("",e);
//			AppLogger.error(e);
		}
        AudioFormat form = _line.getFormat();
        _logger.debug("Audio format of output line: "+form);
//        AppLogger.debug("Audio format of output line: "+form);
        // set up the SPEEX decoder
        _decoder = new SpeexDecoder();
		_decoder.init(SPEEX_MODE, (int)TRANSMIT_SAMPLE_RATE, CHANNEL_NUMBER, false);
    }
    
    public void start() {
        _thread = new Thread(this);
        _thread.setName("Playback");
        _thread.start();
    }

    public void stop() {
        _thread = null;
    }
 
    public void run() {
        
    	ChannelAlligner alligner = new ChannelAlligner();
    	VolumeControl volumeControl = new VolumeControl();
        // the buffer
        byte[] outData;
        byte[] speexData;
        int bytesReady;
//        ByteBuffer audioByteBuffer = _radioCoreImpl.getAudioByteBuffer();
        SoundSignalQueueGroup audioDataBuffer = _radioCoreImpl.getAudioDataBuffer();
        _line.start();

        try {
        	while (_thread != null) {
        		// no sound if radio is off or currently transmitting!
        		boolean quiet = !_radioCoreImpl.getMasterSwitch() || _radioCoreImpl.getTransmit();
    			outData = audioDataBuffer.getNext(_radioCoreImpl.getSquelchAsDecibel(), quiet );
//    			SoundUtils.allignChannels(outData,true);
//            	SoundUtils.scaleVolume(outData,_radioCoreImpl.getVolume(),true);
    			alligner.process(outData,true);
    			volumeControl.setGain(_radioCoreImpl.getVolume());
            	volumeControl.process(outData,true);
    			_line.write(outData, 0, outData.length);
            }
        } catch( Exception e) {
        	_logger.error("",e);
        }
        // we reached the end of the stream.  stop and close the line.
        _line.stop();
        _line.close();
        _line = null;
    }

}
