/**
 * 
 */
package net.sourceforge.dscsim.httpserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The QueueManager manages the airwave packet queues of a single airwave group.
 * 
 * @author oliver
 */
public class QueueManager {
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
	 * Constructor 
	 * @param magicNumber the magic number of the queue manages airwave group
	 */
	public QueueManager(int magicNumber) {
		this.magicNumber = magicNumber;
		this.queues = new HashMap<Integer,PacketQueue>();
	}
	
	/**
	 * Gets the queue for the airwave given by its uid
	 */
	public synchronized PacketQueue getQueue(int airwaveUid) {
		PacketQueue pQ = queues.get(airwaveUid);
		if( pQ == null ) {
			pQ = new PacketQueue(airwaveUid);
			queues.put(airwaveUid, pQ);
			// TODO: set correct log level
			LOGGER.info("New Queue for airwave with UID "+airwaveUid);
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
			// TODO: set correct log level
			LOGGER.info("Packet received from Airwave "+excludedAirwaveUid+" is pushed to "+count+" other airwaves");
	}

	/**
	 * Gets the magic number of the queue managers airwave group
	 * @return the magicNumber
	 */
	protected int getMagicNumber() {
		return magicNumber;
	}
	
}
