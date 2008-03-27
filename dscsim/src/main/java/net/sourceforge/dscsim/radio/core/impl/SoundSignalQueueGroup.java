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

import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import net.sourceforge.dscsim.radiotransport.Decibel;

/**
 * Group of queues of incoming sound data
 *
 */
public class SoundSignalQueueGroup {
	
	/**
	 * Random number generator
	 */
	private Random _randomGenerator;
	
	/**
	 * Variance of the signal power (in Decibel)
	 */
	private static final double POWER_VARIANCE = 2.0; 
	
	private Map _signalQueues;
	private QuietSoundSignalQueue _silenceQueue;

	public SoundSignalQueueGroup() {
		_randomGenerator = new Random();
		_signalQueues = new HashMap();
		_signalQueues.put(new Integer(0), new NoiseSoundSignalQueue());
		_silenceQueue = new QuietSoundSignalQueue();
	}

	public synchronized void add(int transmitterUid, Decibel power, byte[] dataBytes, int sequenceNumber) {
		Integer key = new Integer(transmitterUid);
		SoundSignalQueue queue;
		if( !_signalQueues.containsKey(key) ) {
			queue = new SoundSignalQueue(transmitterUid);
			_signalQueues.put(key, queue);
		} else {
			queue = (SoundSignalQueue)_signalQueues.get(key);
		}
		queue.setPower(power);
		queue.add(dataBytes, sequenceNumber);
	}

	public synchronized byte[] getNext(Decibel squelch, boolean quiet) {
		byte[] resulting = null;
		Decibel power = null;
		Decibel powerVariance;
		for( Iterator i = _signalQueues.values().iterator(); i.hasNext(); ){
			SoundSignalQueue queue = (SoundSignalQueue)i.next();
			byte[] data;
			try {
				data = queue.getNextSoundPacket();
			} catch (StreamCorruptedException e) {
				break;
			}
			powerVariance = Decibel.fromDb(((_randomGenerator.nextDouble()*2.0)-1)*POWER_VARIANCE);
			if( (power == null) || (power.compareTo(queue.getPower()) < 0 ) ) {
				resulting = data;
				power = queue.getPower().add(powerVariance);
			}
		}
		for( Iterator i = _signalQueues.values().iterator(); i.hasNext(); ){
			SoundSignalQueue queue = (SoundSignalQueue)i.next();
			if( queue.isEmpty() ){
				i.remove();
			}
		}
		if( power == null || quiet || (power.compareTo(squelch) < 0 )){
			resulting = _silenceQueue.getNextSoundPacket();
		}
		return resulting;
	}
	
	
}
