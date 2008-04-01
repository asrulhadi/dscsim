/**
 * 
 */
package net.sourceforge.dscsim.httpserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * The QueueManager manages the airwave packet queues of a single airwave group.
 * 
 * @author oliver
 */
public class QueueManager {

	/**
	 * The timeout for empty QueueManagers until they get deleted
	 */
	private static final int QUEUEMANAGER_DELETION_TIMEOUT = 300000;

	/**
	 * The logger for this class
	 */
	private static Logger LOGGER = Logger.getLogger(QueueManager.class);
	
	/**
	 * The magic number of the queue managers airwave group
	 */
	private final int magicNumber;
	
	/**
	 * The map of queues owned by this manager
	 */
	private final Map<Integer,PacketQueue> queues;
	
	/**
	 * Timestamp indicating since when this QueueManager does not contain any queues
	 */
	private long emptySince;

	/**
	 * the time in milliseconds until the empty queue manager is marked for deletion
	 */
	private int queueManagerDeletionTimeout;

	/**
	 * The time in milliseconds until an empty packet
	 * queue is marked for deletion. If >= 0 this parameter will
	 * be used as initialization parameter when creating queues
	 */
	private int packetQueueDeletionTimeout;
	
	/**
	 * The blocking time in milliseconds until the call of method poll on 
	 * an empty queue returns null. If >= 0 this parameter will
	 * be used as initialization parameter when creating queues.

	 */
	private int packetQueueBlockingTimeout;

	/**
	 * Constructor 
	 * @param magicNumber the magic number of the queue manages airwave group
	 * @param queueManagerDeletionTimeout time in milliseconds until the empty
	 * queue manager is marked for deletion 
	 */
	public QueueManager(int magicNumber, int queueManagerDeletionTimeout) {
		this.magicNumber = magicNumber;
		this.queues = new HashMap<Integer,PacketQueue>();
		this.emptySince = System.currentTimeMillis();
		this.queueManagerDeletionTimeout = queueManagerDeletionTimeout;
		this.packetQueueDeletionTimeout = -1;
		this.packetQueueBlockingTimeout = -1;
	}
	
	/**
	 * Constructor 
	 * @param magicNumber the magic number of the queue manages airwave group
	 */
	public QueueManager(int magicNumber) {
		this(magicNumber, QUEUEMANAGER_DELETION_TIMEOUT);
	}

	/**
	 * Gets the queue for the airwave given by its uid
	 */
	public synchronized PacketQueue getQueue(int airwaveUid) {
		PacketQueue pQ = queues.get(airwaveUid);
		if( pQ == null ) {
			if( packetQueueDeletionTimeout < 0 ) {
				pQ = new PacketQueue(airwaveUid);
			} else {
				pQ = new PacketQueue(airwaveUid, packetQueueDeletionTimeout, packetQueueBlockingTimeout );
			}
			queues.put(airwaveUid, pQ);
			LOGGER.debug("New Queue for airwave with UID "+airwaveUid);
		}
		return pQ;
		
	}
	
	/**
	 * Pushes the given packet to the queues of all airwaves except
	 * the airwave which is identified by the uid
	 * @param excludedAirwaveUid the uid of the airwave where the packet should not be added to the queue
	 * @param packet the data packet to be added to the queues
	 */
	public synchronized void pushToQueues(int excludedAirwaveUid, byte[] packet) {
			int count = 0;
			for(Iterator<Map.Entry<Integer,PacketQueue>> i = queues.entrySet().iterator(); i.hasNext();  ) {
				Map.Entry<Integer,PacketQueue> mE = i.next();
				if( !mE.getKey().equals(excludedAirwaveUid) ) {
					mE.getValue().add(packet);
					count++;
				}
			}
			LOGGER.debug("Packet received from Airwave "+excludedAirwaveUid+" is pushed to "+count+" other airwaves");
	}

	/**
	 * Gets the magic number of the queue managers airwave group
	 * @return the magicNumber
	 */
	protected int getMagicNumber() {
		return magicNumber;
	}

	/**
	 * Performs the housekeeping. This includes calling the method houseKeepingAndTestForDelete
	 * of all connected PacketQueues and deleting them from the Map if the method
	 * returns true. If the QueueManager does not contain any queues for more than
	 * QUEUEMANAGER_TIMEOUT milliseconds the method will return true, indicating that it
	 * is outdated and might be deleted.
	 * @return <code>true</code> if no queues are contained for more than QUEUEMANAGER_TIMEOUT
	 * milliseconds
	 */
	public synchronized boolean houseKeepingAndTestForDelete() {
		Set<Map.Entry<Integer,PacketQueue>> eS = queues.entrySet();
		for( Iterator<Map.Entry<Integer,PacketQueue>> i = eS.iterator(); i.hasNext(); ) {
			Map.Entry<Integer,PacketQueue> pQME = i.next();
			PacketQueue pQ = pQME.getValue();
			if( pQ.houseKeepingAndTestForDelete() ) {
				i.remove();
				LOGGER.debug("Removed PacketQueue with airwave uid "+pQ.getAirwaveUid()+" due to inactivity");
			}
		}
		long currentTime = System.currentTimeMillis();
		if( queues.isEmpty() ) {
			if( emptySince == Long.MAX_VALUE ) {  // not empty last time!
				emptySince = currentTime;         // got empty now
			} 
			if( (currentTime-emptySince) > queueManagerDeletionTimeout ) {
				return true;
			}
		} else {
			emptySince = Long.MAX_VALUE;		// indicates: not empty
		}
		return false;
	}
	
	/**
	 * Gets the number of queues of this QueueManager
	 * @return the number of queues
	 */
	public synchronized int getSize() {
		return queues.size();
	}
	
	/**
	 * Sets the timeout parameters for creating the underlying queues
	 * @param packetQueueDeletionTimeout parameter for the queue creation. Needs to be a nonnegative integer.
	 * @param packetQueueBlockingTimeout parameter for the queue creation. Needs to be a nonnegative integer.
	 */
	public void setQueueTimeoutParameters( int packetQueueDeletionTimeout, int packetQueueBlockingTimeout ) {
		if( packetQueueDeletionTimeout < 0 ) {
			throw new IllegalArgumentException("packetQueueDeletionTimeout needs to be a nonnegaive integer");
		}
		if( packetQueueBlockingTimeout < 0 ) {
			throw new IllegalArgumentException("packetQueueBlockingTimeout needs to be a nonnegative integer");
		}
		this.packetQueueDeletionTimeout = packetQueueDeletionTimeout;
		this.packetQueueBlockingTimeout = packetQueueBlockingTimeout;
	}
}
