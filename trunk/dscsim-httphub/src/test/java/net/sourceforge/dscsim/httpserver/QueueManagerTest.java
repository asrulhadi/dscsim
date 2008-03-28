/**
 * 
 */
package net.sourceforge.dscsim.httpserver;

import junit.framework.TestCase;

/**
 * @author oliver
 *
 */
public class QueueManagerTest extends TestCase {

	private static final int MAGIC_NUMBER = 7567;
	
	private QueueManager testQueueManager;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		testQueueManager = new QueueManager(MAGIC_NUMBER);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.QueueManager#QueueManager(int)}.
	 */
	public void testGetMagicNumber() {
		assertEquals(MAGIC_NUMBER, testQueueManager.getMagicNumber() );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.QueueManager#getQueue(int)}.
	 */
	public void testGetQueue() {
		PacketQueue pQ123 = testQueueManager.getQueue(123);
		PacketQueue pQ456 = testQueueManager.getQueue(456);
		assertNotSame(pQ123, pQ456);
		assertSame( pQ123, testQueueManager.getQueue(123));
		assertSame( pQ456, testQueueManager.getQueue(456));
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.QueueManager#pushToQueues(int, byte[])}.
	 */
	public void testPushToQueues() {
		byte[] packet = new byte[10];
		assertNull(testQueueManager.getQueue(123).poll());
		assertNull(testQueueManager.getQueue(456).poll());
		assertNull(testQueueManager.getQueue(789).poll());

		testQueueManager.pushToQueues(456, packet);
		assertNull(testQueueManager.getQueue(888).poll());
		assertSame(packet, testQueueManager.getQueue(123).poll());
		assertNull(testQueueManager.getQueue(456).poll());
		assertSame(packet, testQueueManager.getQueue(789).poll());
		assertNull(testQueueManager.getQueue(123).poll());
		assertNull(testQueueManager.getQueue(456).poll());
		assertNull(testQueueManager.getQueue(789).poll());
	}

}
