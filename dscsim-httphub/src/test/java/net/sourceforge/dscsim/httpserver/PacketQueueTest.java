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
		testPacketQueue = new PacketQueue(UID);
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
	 * {@link net.sourceforge.dscsim.httpserver.PacketQueue#poll()}.
	 * and {@link net.sourceforge.dscsim.httpserver.PacketQueue#add()}.
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

}
