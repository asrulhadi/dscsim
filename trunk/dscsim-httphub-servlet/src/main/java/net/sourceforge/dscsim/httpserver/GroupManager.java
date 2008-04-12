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
 * @author oliver
 *
 */
public class GroupManager {
	
	/**
	 * The housekeeping thread
	 */
	private class HouseKeeper extends Thread {
		
		/**
		 * Constructor which takes the threads name
		 */
		private HouseKeeper(String threadName) {
			super(threadName);
			setDaemon(true);
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			while(houseKeeper!=null) {
				try{
					LOGGER.debug("Performing housekeeping for "+GroupManager.this.toString());
					performHouseKeeping();
					sleep(housekeepingSleep);
				} catch( InterruptedException e ) {
					LOGGER.info("Housekeeper thread interrupted");
				} catch( Exception e ) {
					LOGGER.error("Unhandled exception occured in housekeeper thread, trying to continue",e);
				} catch( Error t) {
					LOGGER.error("Unhandled error occured in housekeeper thread, aborting thread",t);
					throw t;
				}
			}
			LOGGER.info("Housekeeper thread shutting down");
		}
		
	}
	
	/**
	 * The default sleep time for the housekeeping thread (milliseconds)
	 */
	private static final int HOUSEKEEPING_SLEEP = 1000;
	
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
	 * The housekeeper thread
	 */
	private Thread houseKeeper;

	/**
	 * The time in milliseconds until the empty queue manager is marked for deletion.
	 * This parameter overrides the default parameter defined in the QueueManager
	 * if it is set to a nonnegative integer.
	 */
	private int queueManagerDeletionTimeout;
	
	/**
	 * The sleep time for the housekeeping thread (milliseconds)
	 */
	private int housekeepingSleep;

	
	/**
	 * private constructor prevents external instantiation
	 */
	private GroupManager() {
		queueManagers = new HashMap<Integer,QueueManager>();
		queueManagerDeletionTimeout = -1;
		housekeepingSleep = HOUSEKEEPING_SLEEP;
		houseKeeper = new HouseKeeper("GroupManager_HouseKeeper");
		houseKeeper.start();
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
	public static void reInitialize() {
		Thread houseKeeperTemp = theInstance.houseKeeper;
		theInstance.houseKeeper = null;		// shuts down the housekeeper thread
		houseKeeperTemp.interrupt();
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
			if( queueManagerDeletionTimeout < 0 ) {
				qM = new QueueManager(magicNumber);
			} else {
				qM = new QueueManager(magicNumber, queueManagerDeletionTimeout);
			}
			queueManagers.put(magicNumber, qM);
			LOGGER.debug("New QueueManager for airwave group with magic number "+magicNumber);
		}
		return qM;
	}
	
	/**
	 * Performs the housekeeping. This includes calling the method houseKeepingAndTestForDelete
	 * of all connected QueueManagers and deleting them from the Map if the method
	 * returns true.
	 */
	private synchronized void performHouseKeeping() {
		Set<Map.Entry<Integer,QueueManager>> eS = queueManagers.entrySet();
		for( Iterator<Map.Entry<Integer,QueueManager>> i = eS.iterator(); i.hasNext(); ) {
			Map.Entry<Integer,QueueManager> qMME = i.next();
			QueueManager qM = qMME.getValue();
			if( qM.houseKeepingAndTestForDelete() ) {
				i.remove();
				LOGGER.debug("Removed QueueManager with magic number "+qM.getMagicNumber()+" due to inactivity");
			}
		}
	}
	
	/**
	 * Gets the number of QueueManagers in this GroupManager
	 * @return the number of conatined QueueManagers
	 */
	public synchronized int getSize() {
		return queueManagers.size();
	}

	/**
	 * Sets the timeout parameters for creating the underlying queue managers
	 * @param pqueueManagerDeletionTimeout parameter for the queue manager creation.
	 * Needs to be a nonnegative integer.
	 */
	public void setQueueManagerTimeoutParameters( int queueManagerDeletionTimeout ) {
		if( queueManagerDeletionTimeout < 0 ) {
			throw new IllegalArgumentException("queueManagerDeletionTimeout needs to be a nonnegaive integer");
		}
		this.queueManagerDeletionTimeout = queueManagerDeletionTimeout;
	}

	/**
	 * Sets the sleep time of the housekeeping thread
	 * @param housekeepingSleep the housekeepingSleep to set (positive integer)
	 */
	void setHousekeepingSleep(int housekeepingSleep) {
		if( housekeepingSleep <= 0 ) {
			throw new IllegalArgumentException("housekeepingSleep must be positive");
		}
		this.housekeepingSleep = housekeepingSleep;
	}
	
	
}
