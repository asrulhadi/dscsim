/**
 * 
 */
package net.sourceforge.dscsim.httpserver;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import sun.misc.GC.LatencyRequest;

/**
 * @author oliver
 *
 */
public class PacketQueue {

	/**
	 * The timeout for inactive PacketQueues until they get deleted
	 */
	private static final int PACKETQUEUE_DELETION_TIMEOUT = 10000;

	/**
	 * The blocking time for an empty queue until the call on method 
	 * poll return true
	 */
	private static final int PACKETQUEUE_BLOCKING_TIMEOUT = 5000;
	
	/**
	 * The logger for this class
	 */
	private static Logger LOGGER = Logger.getLogger(PacketQueue.class);

	/**
	 * the unique id of the airwave associated to this queue
	 */
	private int airwaveUid;
	
	/**
	 * the time in milliseconds until an empty packet
	 * queue is marked for deletion
	 */
	private int packetQueueDeletionTimeout;
	
	/**
	 * the blocking time in milliseconds until the call of method poll on 
	 * an empty queue returns null
	 */
	private int packetQueueBlockingTimeout;
	
	/**
	 * the queue of data packets
	 */
	private Queue<byte[]> queue;
	
	/**
	 * The timestamp of the last poll activity
	 */
	private long lastPollActivity;
	
	/**
	 * The lock which controls access to the variable lastPollActivity
	 */
	private Lock pollActivityLock;
	
	/**
	 * Constructor
	 * @param airwaveUid the unique id of the airwave associated to this queue
	 * @param packetQueueDeletionTimeout the time in milliseconds until an empty packet
	 * queue is marked for deletion
	 * @param packetQueueBlockingTimeout the blocking time in milliseconds until
	 * the call of method poll on an empty queue returns null
	 * 
	 */
	public PacketQueue(int airwaveUid, int packetQueueDeletionTimeout, int packetQueueBlockingTimeout) {
		this.airwaveUid = airwaveUid;
		this.packetQueueDeletionTimeout = packetQueueDeletionTimeout;
		this.packetQueueBlockingTimeout = packetQueueBlockingTimeout;
		this.queue = new LinkedList<byte[]>();
		this.lastPollActivity = System.currentTimeMillis();
		this.pollActivityLock = new ReentrantLock();
	}

	/**
	 * Constructor which takes the default timeout parameters
	 * @param airwaveUid the unique id of the airwave associated to this queue
	 */
	public PacketQueue(int airwaveUid) {
		this( airwaveUid, PACKETQUEUE_DELETION_TIMEOUT, PACKETQUEUE_BLOCKING_TIMEOUT  );
	}
	
	/**
	 * Add a data packet to the queue.
	 * @param packet the data packet to be added to the queue
	 */
	public synchronized void add(byte[] packet) {
		if( packet == null ) {
			throw new IllegalArgumentException("packet should never be null");
		}
		queue.add(packet);
		notify(); 	// notifies the poll method
	}

	/**
	 * Get the new packet in the queue. If the queue is empty then this method will
	 * wait for <code>packetQueueBlockingTimeout</code> milliseconds for data to arrive
	 * before it returns <code>null</code>.
	 * @return the next packet in the queue. Null if the queue is empty
	 * @see java.util.Queue#poll()
	 */
	public synchronized byte[] poll() {
		byte[] result;
		try {
			pollActivityLock.lock();
			result = queue.poll();
			if( result != null ) {
				LOGGER.debug("Delivered data packet with length "+result.length+" for airwave "+airwaveUid+" immediately");
			} else {
				if( packetQueueBlockingTimeout > 0 ) {
					try {
						wait(packetQueueBlockingTimeout);
					} catch( InterruptedException e ) {
						// nothing to do here; even if this happens its just another timeout
					}
					result = queue.poll();
				}
				if( LOGGER.isDebugEnabled() ) {
					if( result == null ) {
						LOGGER.debug("No data packet available for airwave "+airwaveUid);
					} else {
						LOGGER.debug("Delivered data packet with length "+result.length+" for airwave "+airwaveUid+" after waiting");
					}
				}
			}
			lastPollActivity = System.currentTimeMillis();
		} finally {
			pollActivityLock.unlock();
		}
		return result;
	}

	/**
	 * Gets the airwaveUid
	 * @return the airwaveUid
	 */
	public int getAirwaveUid() {
		return airwaveUid;
	}
	
	/**
	 * Returns true if the last poll activity was longer ago than PACKETQUEUE_TIMEOUT
	 * milliseconds. In case that some other thread is currently being blocked
	 * in method poll the method will return <code>false</code> as well.
	 * @return <code>true</code> if the queue timed out
	 */
	public synchronized boolean houseKeepingAndTestForDelete() {
		if( pollActivityLock.tryLock() ) {
			try {
				if( (System.currentTimeMillis() - lastPollActivity) > packetQueueDeletionTimeout ) {
					return true;
				} else {
					return false;
				}
			} finally {
				pollActivityLock.unlock();
			}
		} else {
			return false;
		}
	}

	/**
	 * Get the number of packets in the queue
	 * @return the number of packets in the queue
	 */
	public synchronized int getSize() {
		return queue.size();
	}

}
