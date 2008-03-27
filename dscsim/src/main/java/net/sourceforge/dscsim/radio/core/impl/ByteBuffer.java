package net.sourceforge.dscsim.radio.core.impl;

public class ByteBuffer {
	
	private static final int BUFFER_SIZE = 40;
	private static final int START_THRESHOLD = 15;
	private static final int START_TIMEOUT = 35;
	private byte[][] buffer;
	private boolean empty;
	private int lastFilled;
	private boolean startRead;
	private int _startTimeoutCounter;
	/**
	 * The external index of the object buffered at internal index 0
	 */
	private int startSequenceNumber;
	
	public ByteBuffer() {
		lastFilled = -1;
		empty = true;
		startSequenceNumber = 0;
		startRead = false;
		_startTimeoutCounter = 0;
		buffer = new byte[BUFFER_SIZE][];
	}
	
	public synchronized void add(byte[] data, int sequenceNumber){
		if( empty ) {
			startSequenceNumber = sequenceNumber;
			lastFilled = -1;
		} else {
			if( sequenceNumber >= startSequenceNumber+BUFFER_SIZE ) {
				rollContents((sequenceNumber-startSequenceNumber)-(BUFFER_SIZE+START_THRESHOLD)/2);
			}
		}
		empty = false;
		lastFilled=sequenceNumber-startSequenceNumber;
		buffer[lastFilled]=data;
		if( lastFilled >= START_THRESHOLD ) {
			startRead = true;
		}
		notify();
	}

	private void rollContents(int offset){
		for( int i = 0; i<BUFFER_SIZE; i++ ){
			if( i < (BUFFER_SIZE-offset) ){
				buffer[i]=buffer[i+offset];
			} else {
				buffer[i] = null;
			}
		}
		startSequenceNumber += offset;
		lastFilled -= offset;
		if( lastFilled <= -1 ){
			empty = true;
			startRead = false;
			_startTimeoutCounter = 0;
			lastFilled = -1;
		}
	}
	
	public synchronized byte[] getNext() {
//		while( empty || !startRead ){
//			try {
//				wait();
//			} catch( InterruptedException e) {
//				e.printStackTrace();
//				break;
//			}
//		}
		if( empty ){
			return null;
		}
		if( !startRead ){
			_startTimeoutCounter++;
			if( _startTimeoutCounter >= START_TIMEOUT ) {
				// too late... clean everything up; fir for bug 1655507
				rollContents(BUFFER_SIZE);
			}
			return null;
		}
		byte[] returnValue = buffer[0];
		rollContents(1);
		return returnValue;
	}

	/**
	 * @return the empty
	 */
	public boolean isEmpty() {
		return empty;
	}
}
