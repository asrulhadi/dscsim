/**
 * 
 */
package net.sourceforge.dscsim.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author oliver
 *
 */
public class ByteArrayUtilTest extends TestCase {
	
	List<byte[]> testList1;
	List<byte[]> testList2;
	List<byte[]> testList3;
	List<byte[]> testList4;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		testList1 = new LinkedList<byte[]>();
		testList1.add( new byte[]{ (byte)1 } );

		testList2 = new LinkedList<byte[]>();
		testList2.add( new byte[]{ (byte)1 } );
		testList2.add( new byte[]{} );
		testList2.add( null );
		testList2.add( new byte[]{ (byte)1, (byte)10, (byte)100, (byte)12 } );

		testList3 = new LinkedList<byte[]>();
		testList3.add(null);
		testList3.add(null);
		testList3.add(null);
		testList3.add(null);

		testList4 = new LinkedList<byte[]>();
		testList4.add(new byte[]{ (byte)1, (byte)1, (byte)1, (byte)1 });
		testList4.add(null);
		testList4.add(null);
		testList4.add(null);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#encode(java.util.List)}.
	 */
	public void testEncode() {
		byte[] result = ByteArrayUtil.encode(testList1);
		assertEquals(9, result.length);
		assertEquals(1, ByteConverter.byteArrayToInt(result, 0));
		assertEquals(1, ByteConverter.byteArrayToInt(result, 4));
		assertEquals(1, result[8]);
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[])}.
	 */
	public void testDecodeSimple1() {
		byte[] encoded = ByteArrayUtil.encode(testList1);
		List<byte[]> result = ByteArrayUtil.decode(encoded);
		assertTrue( arrayListEquals( testList1, result ) );

		encoded = ByteArrayUtil.encode(testList2);
		result = ByteArrayUtil.decode(encoded);
		assertTrue( arrayListEquals( testList2, result ) );
	}
	
	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[])}.
	 */
	public void testDecodeSimple2() {
		byte[] encoded = ByteArrayUtil.encode(testList2);
		List<byte[]>result = ByteArrayUtil.decode(encoded);
		assertTrue( arrayListEquals( testList2, result ) );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[])}.
	 */
	public void testDecodeNullElements() {
		byte[] encoded = ByteArrayUtil.encode(testList3);
		List<byte[]>result = ByteArrayUtil.decode(encoded);
		assertTrue( arrayListEquals( testList3, result ) );
	}
	
	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[])}.
	 */
	public void testDecodeMaxAllowedSingleArraySize() {
		byte[] encoded = ByteArrayUtil.encode(testList4);
		List<byte[]>result = ByteArrayUtil.decode(encoded);
		assertTrue( arrayListEquals( testList4, result ) );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[])}.
	 */
	public void testDecodeCorrupted1() {
		byte[] encoded = ByteArrayUtil.encode(testList2);
		encoded[2] = 1;	// corrupt the counter for number of arrays contained
		boolean caught = false;
		try {
			ByteArrayUtil.decode(encoded);
		} catch( IllegalArgumentException e ) {
			caught = true;
		}
		assertTrue(caught);
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[])}.
	 */
	public void testDecodeCorrupted2() {
		byte[] encoded = ByteArrayUtil.encode(testList2);
		encoded[1] = 1;	// corrupt the length info of the first byte array
		boolean caught = false;
		try {
			ByteArrayUtil.decode(encoded);
		} catch( IllegalArgumentException e ) {
			caught = true;
		}
		assertTrue(caught);
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[])}.
	 */
	public void testDecodeCorrupted3() {
		byte[] encoded = ByteArrayUtil.encode(testList2);
		byte[] encodedManipulated = new byte[encoded.length+1];
		System.arraycopy(encoded, 0, encodedManipulated, 0, encoded.length );
		boolean caught = false;
		try {
			ByteArrayUtil.decode(encodedManipulated);
		} catch( IllegalArgumentException e ) {
			caught = true;
		}
		assertTrue(caught);

	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[])}.
	 */
	public void testDecodeNullElementsCorrupted() {
		byte[] encoded = ByteArrayUtil.encode(testList3);
		int length = ByteConverter.byteArrayToInt(encoded, 0);
		length++;
		ByteConverter.intToByteArray(length, encoded, 0 );
		boolean caught = false;
		try {
			List<byte[]>result = ByteArrayUtil.decode(encoded);
		} catch( IllegalArgumentException e ) {
			Throwable innerE = e.getCause();
			String text = innerE.getMessage();
			if( text.contains("List length info corrupted") ) {
				caught = true;
			}
		}
		assertTrue( caught );
	}
	
	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[])}.
	 */
	public void testDecodeMaxAllowedSingleArraySizeCorrupted() {
		byte[] encoded = ByteArrayUtil.encode(testList4);
		int length = ByteConverter.byteArrayToInt(encoded, 4);
		length++;
		ByteConverter.intToByteArray(length, encoded, 4 );
		boolean caught = false;
		try {
			List<byte[]>result = ByteArrayUtil.decode(encoded);
		} catch( IllegalArgumentException e ) {
			Throwable innerE = e.getCause();
			String text = innerE.getMessage();
			if( text.contains("Array size info corrupted") ) {
				caught = true;
			}
		}
		assertTrue( caught );
	}
	
	/**
	 * Helper method to check if two lists of byte arrays are equal
	 */
	private static boolean arrayListEquals( List<byte[]> list1, List<byte[]> list2) {
		if( list1.size() != list2.size() ) {
			return false;
		}
		Iterator<byte[]> i = list1.iterator();
		Iterator<byte[]> j = list2.iterator();
		while( i.hasNext() ) {
			if( !Arrays.equals(i.next(), j.next() ) ){
				return false;
			}
		}
		return true;
	}
}
