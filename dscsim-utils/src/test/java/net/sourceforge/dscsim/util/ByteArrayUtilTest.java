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
	List<byte[]> testList5;
	
	int pfL;

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

		testList5 = new LinkedList<byte[]>();
		
		pfL = getPrefixLength();
	}

	/**
	 * Determine the value for the prefixLength used throughout the test
	 * @return the prefixLength
	 */
	protected int getPrefixLength() {
		return 0;
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#encode(java.util.List, int)}.
	 */
	public void testEncode() {
		byte[] result = ByteArrayUtil.encode(testList1, pfL);
		assertEquals(9+pfL, result.length);
		assertEquals(1, ByteConverter.byteArrayToInt(result, 0+pfL));
		assertEquals(1, ByteConverter.byteArrayToInt(result, 4+pfL));
		assertEquals(1, result[8+pfL]);
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[], int)}.
	 */
	public void testDecodeSimple1() {
		byte[] encoded = ByteArrayUtil.encode(testList1, pfL);
		List<byte[]> result = ByteArrayUtil.decode(encoded, pfL);
		assertTrue( arrayListEquals( testList1, result ) );

		encoded = ByteArrayUtil.encode(testList2, pfL);
		result = ByteArrayUtil.decode(encoded, pfL);
		assertTrue( arrayListEquals( testList2, result ) );
	}
	
	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[], int)}.
	 */
	public void testDecodeSimple2() {
		byte[] encoded = ByteArrayUtil.encode(testList2, pfL);
		List<byte[]>result = ByteArrayUtil.decode(encoded, pfL);
		assertTrue( arrayListEquals( testList2, result ) );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[], int)}.
	 */
	public void testDecodeNullElements() {
		byte[] encoded = ByteArrayUtil.encode(testList3, pfL);
		List<byte[]>result = ByteArrayUtil.decode(encoded, pfL);
		assertTrue( arrayListEquals( testList3, result ) );
	}
	
	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[], int)}.
	 */
	public void testDecodeEmptyList() {
		byte[] encoded = ByteArrayUtil.encode(testList5, pfL);
		List<byte[]>result = ByteArrayUtil.decode(encoded, pfL);
		assertTrue( arrayListEquals( testList5, result ) );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[], int)}.
	 */
	public void testDecodeMaxAllowedSingleArraySize() {
		byte[] encoded = ByteArrayUtil.encode(testList4, pfL);
		List<byte[]>result = ByteArrayUtil.decode(encoded, pfL);
		assertTrue( arrayListEquals( testList4, result ) );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[], int)}.
	 */
	public void testDecodeCorrupted1() {
		byte[] encoded = ByteArrayUtil.encode(testList2, pfL);
		encoded[2+pfL] = 1;	// corrupt the counter for number of arrays contained
		boolean caught = false;
		try {
			ByteArrayUtil.decode(encoded, pfL);
		} catch( IllegalArgumentException e ) {
			caught = true;
		}
		assertTrue(caught);
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[], int)}.
	 */
	public void testDecodeCorrupted2() {
		byte[] encoded = ByteArrayUtil.encode(testList2, pfL);
		encoded[4+pfL] = 2;	// corrupt the length info of the first byte array
		boolean caught = false;
		try {
			ByteArrayUtil.decode(encoded, pfL);
		} catch( IllegalArgumentException e ) {
			caught = true;
		}
		assertTrue(caught);
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[], int)}.
	 */
	public void testDecodeCorrupted3() {
		byte[] encoded = ByteArrayUtil.encode(testList2, pfL);
		byte[] encodedManipulated = new byte[encoded.length+1];
		System.arraycopy(encoded, 0, encodedManipulated, 0, encoded.length );
		boolean caught = false;
		try {
			ByteArrayUtil.decode(encodedManipulated, pfL);
		} catch( IllegalArgumentException e ) {
			caught = true;
		}
		assertTrue(caught);

	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[], int)}.
	 */
	public void testDecodeNullElementsCorrupted() {
		byte[] encoded = ByteArrayUtil.encode(testList3, pfL);
		int length = ByteConverter.byteArrayToInt(encoded, 0+pfL);
		length++;
		ByteConverter.intToByteArray(length, encoded, 0+pfL );
		boolean caught = false;
		try {
			List<byte[]>result = ByteArrayUtil.decode(encoded, 0+pfL);
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
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[], int)}.
	 */
	public void testDecodeMaxAllowedSingleArraySizeCorrupted() {
		byte[] encoded = ByteArrayUtil.encode(testList4, pfL);
		int length = ByteConverter.byteArrayToInt(encoded, 4+pfL);
		length++;
		ByteConverter.intToByteArray(length, encoded, 4+pfL );
		boolean caught = false;
		try {
			List<byte[]>result = ByteArrayUtil.decode(encoded, pfL);
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
	protected static boolean arrayListEquals( List<byte[]> list1, List<byte[]> list2) {
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
