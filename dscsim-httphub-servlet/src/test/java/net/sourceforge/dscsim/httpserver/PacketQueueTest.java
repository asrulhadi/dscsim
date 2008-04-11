/**
 * 
 */
package net.sourceforge.dscsim.httpserver;

import junit.framework.TestCase;

/**
 * @author oliver
 *
 */
public class PacketQueueTest extends TestCase {

	private static final int UID = 12345;
	
	private byte[] packet1 = new byte[10];
	private byte[] packet2 = new byte[10];
	private byte[] packet3 = new byte[10];
	private byte[] packet4 = new byte[10];

	private PacketQueue testPacketQueue;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		testPacketQueue = new PacketQueue(UID, 10, 10);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.PacketQueue#PacketQueue(int)}.
	 */
	public void testGetAirwaveUid() {
		assertEquals(UID, testPacketQueue.getAirwaveUid() );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.PacketQueue#add(byte[])}.
	 */
	public void testAdd() {
		testPacketQueue.add(packet1);
		try {
			testPacketQueue.add(null);
			fail( "Expected Exception was not thrown" );
		} catch( IllegalArgumentException e) {
		}
		testPacketQueue.add(packet2);
	}

	/**
	 * Test method for 
	 * {@link net.sourceforge.dscsim.httpserver.PacketQueue#poll()}
	 * and {@link net.sourceforge.dscsim.httpserver.PacketQueue#add(byte[])}.
	 */
	public void testAddAndPoll() {
		assertNull(testPacketQueue.poll());
		
		testPacketQueue.add(packet1);
		assertSame( packet1, testPacketQueue.poll() );
		assertNull(testPacketQueue.poll());
		assertNull(testPacketQueue.poll());

		testPacketQueue.add(packet2);
		testPacketQueue.add(packet3);
		testPacketQueue.add(packet4);
		assertSame( packet2, testPacketQueue.poll() );
		assertSame( packet3, testPacketQueue.poll() );
		assertSame( packet4, testPacketQueue.poll() );
		assertNull(testPacketQueue.poll());
	}

	/**
	 * Test method for 
	 * {@link net.sourceforge.dscsim.httpserver.PacketQueue#size()}.
	 */
	public void testSize() {
		assertEquals(0, testPacketQueue.getSize() );
		testPacketQueue.add(packet1);
		testPacketQueue.add(packet2);
		assertEquals(2, testPacketQueue.getSize() );
		testPacketQueue.poll();
		testPacketQueue.poll();		
		assertEquals(0, testPacketQueue.getSize() );
	}

	/**
	 * Tests the timeout of the method 
	 * {@link net.sourceforge.dscsim.httpserver.PacketQueue#poll()}.
	 */
	public void testPollTimeout() {
		testPacketQueue = new PacketQueue(UID);
		long startTime = System.currentTimeMillis();
		testPacketQueue.poll();
		long endTime = System.currentTimeMillis();
		int duration = (int)(endTime-startTime);
		// allow for 20 ms deviation from nominal value of 5000 ms
		assertTrue("Default timeout failed", Math.abs(duration-5000)<20);
	}
	
	/**
	 * Tests the behaviour of the method 
	 * {@link net.sourceforge.dscsim.httpserver.PacketQueue#poll()}
	 * {@link net.sourceforge.dscsim.httpserver.PacketQueue#add(byte[])} when working
	 * on an empty queue.
	 */
	public void testPollAddEmptyQueue() {
		testPacketQueue = new PacketQueue(UID);

		new Thread() { // add something after 500 ms
			public void run() {
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				testPacketQueue.add(packet1);
			}
		}.start();
		
		long startTime = System.currentTimeMillis();
		byte[] result = testPacketQueue.poll();
		long endTime = System.currentTimeMillis();
		long duration = (int)(endTime-startTime);
		// allow for 20 ms deviation from nominal value of 500 ms
		assertTrue("Poll did not return after expected time", Math.abs(duration-500)<20);
		assertEquals(packet1, result);
	}
	
	/**
	 * Tests the behaviour of the method on a non empty queue
	 * {@link net.sourceforge.dscsim.httpserver.PacketQueue#houseKeepingAndTestForDelete()}
	 */
	public void testHouseKeepingFilledQueue() throws Exception {
		testPacketQueue = new PacketQueue(UID,500,500);
		testPacketQueue.add(packet1);
		testPacketQueue.add(packet2);
		testPacketQueue.add(packet3);
		assertFalse(testPacketQueue.houseKeepingAndTestForDelete());
		Thread.sleep(450);
		assertFalse(testPacketQueue.houseKeepingAndTestForDelete());
		testPacketQueue.poll();
		assertFalse(testPacketQueue.houseKeepingAndTestForDelete());
		Thread.sleep(450);
		assertFalse(testPacketQueue.houseKeepingAndTestForDelete());
		testPacketQueue.poll();
		assertFalse(testPacketQueue.houseKeepingAndTestForDelete());
		Thread.sleep(450);
		assertFalse(testPacketQueue.houseKeepingAndTestForDelete());
		Thread.sleep(100);
		assertTrue(testPacketQueue.houseKeepingAndTestForDelete());
		testPacketQueue.poll();
		assertFalse(testPacketQueue.houseKeepingAndTestForDelete()); // revival...
	}

	/**
	 * Tests the behaviour of the method on an empty queue 
	 * {@link net.sourceforge.dscsim.httpserver.PacketQueue#houseKeepingAndTestForDelete()}
	 */
	public void testHouseKeepingEmptyQueue() throws Exception {

		testPacketQueue = new PacketQueue(UID,500,500);
		assertFalse(testPacketQueue.houseKeepingAndTestForDelete());
		Thread.sleep(450);
		assertFalse(testPacketQueue.houseKeepingAndTestForDelete());

		new Thread() { // just do a poll in a separate thread
			public void run() {
				testPacketQueue.poll();
			}
		}.start();
		long startTime = System.currentTimeMillis();
		assertFalse( testPacketQueue.houseKeepingAndTestForDelete());
		long endTime = System.currentTimeMillis();
		long duration = (int)(endTime-startTime);
		assertTrue("Method seemed to be blocked", duration<20);

		Thread.sleep(950);
		assertFalse(testPacketQueue.houseKeepingAndTestForDelete());
		Thread.sleep(100);
		assertTrue(testPacketQueue.houseKeepingAndTestForDelete());
	}

	/**
	 * Test method for
	 * {@link net.sourceforge.dscsim.httpserver.PacketQueue#getSendSequenceByte()}
	 */
	public void testGetSendSequenceByte() throws Exception {
		assertEquals(0, testPacketQueue.getSendSequenceByte() );
		assertEquals(1, testPacketQueue.getSendSequenceByte() );
		while( testPacketQueue.getSendSequenceByte() < 127 ) {}
		assertEquals(-128, testPacketQueue.getSendSequenceByte() );  // test wrapping
		
		
	}

	/**
	 * Test method for
	 * {@link net.sourceforge.dscsim.httpserver.PacketQueue#getSendSequenceByte()}
	 */
	public void testGetSetReceiveSequenceByte() throws Exception {
		assertEquals(0, testPacketQueue.getSetReceiveSequenceByte((byte)34) );
		assertEquals(35, testPacketQueue.getSetReceiveSequenceByte((byte)127) );
		assertEquals(-128, testPacketQueue.getSetReceiveSequenceByte((byte)0) );
	}
	
}
