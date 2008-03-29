/**
 * 
 */
package net.sourceforge.dscsim.httpserver;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

/**
 * @author oliver
 *
 */
public class PacketQueue {
	/**
	 * The logger for this class
	 */
	private static Logger LOGGER = Logger.getLogger(PacketQueue.class);

	/**
	 * the unique id of the airwave associated to this queue
	 */
	private int airwaveUid;
	
	/**
	 * the queue of data packets
	 */
	private Queue<byte[]> queue;

	/**
	 * Constructor
	 * @param airwaveUid the unique id of the airwave associated to this queue
	 */
	public PacketQueue(int airwaveUid) {
		this.airwaveUid = airwaveUid;
		this.queue = new LinkedList<byte[]>();
	}

	/**
	 * Add a data packet to the queue
	 * @param packet the data packet to be added to the queue
	 */
	public void add(byte[] packet) {
		if( packet == null ) {
			throw new IllegalArgumentException("packet should never be null");
		}
		queue.add(packet);
	}

	/**
	 * Get the new packet in the queue.
	 * @return the next packet in the queue. Null if the queue is empty
	 * @see java.util.Queue#poll()
	 */
	public byte[] poll() {
		byte[] result = queue.poll();
		if( result != null ) {
			// TODO: set correct log level
			LOGGER.info("Delivered data packet with length "+result.length+" for airwave "+airwaveUid);
		} else {
			// TODO: set correct log level
			LOGGER.info("No data packet available for airwave "+airwaveUid);
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
	
	

}
