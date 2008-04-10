/**
 * 
 */
package net.sourceforge.dscsim.util;

import java.util.List;

/**
 * @author oliver
 *
 */
public class ByteArrayUtilPrefixedTest extends ByteArrayUtilTest {

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.util.ByteArrayUtilTest#getPrefixLength()
	 */
	@Override
	protected int getPrefixLength() {
		return 7;
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.util.ByteArrayUtil#decode(byte[], int)}.
	 */
	public void testDecodeWithPrefixedData() {
		byte[] encoded = ByteArrayUtil.encode(testList2, pfL);
		for( int i=0; i<pfL; i++ ) {
			encoded[i] = (byte)i;
		}
		List<byte[]>result = ByteArrayUtil.decode(encoded, pfL);
		assertTrue( arrayListEquals( testList2, result ) );
	}
	
}
