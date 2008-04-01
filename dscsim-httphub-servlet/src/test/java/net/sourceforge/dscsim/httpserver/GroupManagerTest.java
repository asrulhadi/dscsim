/**
 * 
 */
package net.sourceforge.dscsim.httpserver;

import junit.framework.TestCase;

/**
 * @author oliver
 *
 */
public class GroupManagerTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		GroupManager.reInitialize();
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.GroupManager#getInstance()}.
	 */
	public void testGetInstance() {
		GroupManager gM = GroupManager.getInstance();
		assertSame( gM, GroupManager.getInstance() );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.GroupManager#reInitialize()}.
	 */
	public void testReInitialize() {
		GroupManager gM = GroupManager.getInstance();
		GroupManager.reInitialize();
		assertNotSame( gM, GroupManager.getInstance() );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.GroupManager#getQueueManager(int)}.
	 */
	public void testGetQueueManager() {
		GroupManager gM = GroupManager.getInstance();
		QueueManager qM123 = gM.getQueueManager(123);
		QueueManager qM456 = gM.getQueueManager(456);
		assertNotSame(qM123, qM456);
		assertSame(qM123, gM.getQueueManager(123));
		assertSame(qM456, gM.getQueueManager(456));
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.GroupManager#getSize()}.
	 */
	public void testHouseKeeperThread() throws Exception {
		GroupManager gM = GroupManager.getInstance();
		gM.setQueueManagerTimeoutParameters(500);
		gM.setHousekeepingSleep(1);
		Thread.sleep(1100);	// wait until housekeeper has finished first sleep

		assertEquals(0, gM.getSize());
		QueueManager qM123 = gM.getQueueManager(123);
		QueueManager qM456 = gM.getQueueManager(456);
		assertEquals(2, gM.getSize());
		
		Thread.sleep(450);
		assertEquals(2, gM.getSize());
		assertSame( qM123, gM.getQueueManager(123) );
		assertSame( qM456, gM.getQueueManager(456) );
		assertEquals(2, gM.getSize());
		
		Thread.sleep(100);
		assertEquals(0, gM.getSize());
		assertNotSame( qM123, gM.getQueueManager(123) );
		assertNotSame( qM456, gM.getQueueManager(456) );
		assertEquals(2, gM.getSize());
		
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.GroupManager#getSize()}.
	 */
	public void testGetSize() {
		GroupManager gM = GroupManager.getInstance();
		assertEquals(0, gM.getSize());
		QueueManager qM123 = gM.getQueueManager(123);
		QueueManager qM456 = gM.getQueueManager(456);
		assertEquals(2, gM.getSize());
	}
	
}
