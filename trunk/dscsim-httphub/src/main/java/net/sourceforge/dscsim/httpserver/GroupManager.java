/**
 * 
 */
package net.sourceforge.dscsim.httpserver;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author oliver
 *
 */
public class GroupManager {
	/**
	 * The logger for this class
	 */
	private static Logger LOGGER = Logger.getLogger(GroupManager.class);
	
	/**
	 * The singleton instance
	 */
	private static GroupManager theInstance = new GroupManager();
	
	/**
	 * The map of contained QueueManagers
	 */
	private Map<Integer,QueueManager> queueManagers;
	
	/**
	 * private constructor prevents external instantiation
	 */
	private GroupManager() {
		queueManagers = new HashMap<Integer,QueueManager>();
	}

	/**
	 * Returns the singleton instance
	 * @return the instance
	 */
	public static GroupManager getInstance() {
		return theInstance;
	}
	
	/**
	 * Reinitializes the singleton. This should only be used for testing.
	 */
	protected static void reInitialize() {
		theInstance = new GroupManager();		
	}
	
	/**
	 * Gets the queueManager for a group qualified by the groups magic
	 * number
	 * @param magicNumber the groups magic number
	 * @return the corresponding QueueManager
	 */
	public synchronized QueueManager getQueueManager(int magicNumber) {
		QueueManager qM = queueManagers.get(magicNumber);
		if( qM == null ) {
			qM = new QueueManager(magicNumber);
			queueManagers.put(magicNumber, qM);
			// TODO: set correct log level
			LOGGER.info("New QueueManager for airwave group with magic number "+magicNumber);
		}
		return qM;
		
	}
}
