/*
 * The contents of this file are subject to the Mozilla Public License Version 1.0
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'dscsim'.
 *
 * The Initial Developer of the Original Code is Oliver Hecker. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.util;

/**
 * Provides static helper methods for converting primitive data types to and from byte
 * array representation.
 *  
 * @author oliver
 */
public abstract class ByteConverter {
	
	/**
	 * Length of an integer in bytes
	 */
	public static final int INT_LEN_IN_BYTES = 4;
	
	/**
	 * private constructor prevents instantiation
	 */
	private ByteConverter(){}

	/**
	 * Static flag which indicates if conversion uses big endian representation
	 * by default. Initialized to <code>true</code>. Set this flag to false to
	 * use little endian representation by default
	 */
	public static boolean defaultBigEndian = true;
	

	/**
	 * Converts a given integer value into 4 bytes within a byte array. The destination byte array
	 * has to be preinitialized to the required length.
	 * @param src the integer value to be converted
	 * @param dest the byte array where the result is stored
	 * @param offset the offset to start within the destination byte array. <code>0</code>
	 * will start at the beginning and use <code>dest[0]...dest[3]</code>
	 * @param bigEndian <code>true</code>: use big endian format; <code>false</code>: use little endian
	 * format
	 */
	public static void intToByteArray(int src, byte[] dest, int offset, boolean bigEndian){
		if( bigEndian ) {
			dest[offset+0] = (byte) (src          & 0x000000FF);
			dest[offset+1] = (byte) ((src >>> 8)  & 0x000000FF);
			dest[offset+2] = (byte) ((src >>> 16) & 0x000000FF);
			dest[offset+3] = (byte) ((src >>> 24) & 0x000000FF);
		} else {
			dest[offset+3] = (byte) (src          & 0x000000FF);
			dest[offset+2] = (byte) ((src >>> 8)  & 0x000000FF);
			dest[offset+1] = (byte) ((src >>> 16) & 0x000000FF);
			dest[offset+0] = (byte) ((src >>> 24) & 0x000000FF);
		}
	}

	/**
	 * Converts a given integer value into 4 bytes within a byte array. The destination byte array
	 * has to be preinitialized to the required length. The byte ordering (big endian or little endian)
	 * is taken as defined by the flag {@link #defaultBigEndian}.
	 * @param src the integer value to be converted
	 * @param dest the byte array where the result is stored
	 * @param offset the offset to start within the destination byte array. <code>0</code>
	 * will start at the beginning and use <code>dest[0]...dest[3]</code>
	 */
	public static void intToByteArray(int src, byte[] dest, int offset){
		intToByteArray(src, dest, offset, defaultBigEndian );
	}

	/**
	 * Extracts an integer from 4 bytes within a byte array.
	 * @param src the byte array which contains the 4 byte to be taken
	 * @param offset the offset to start the extraction within byte array. <code>0</code>
	 * will start at the beginning and use <code>src[0]...src[3]</code>
	 * @param bigEndian <code>true</code>: use big endian format; <code>false</code>: use little endian
	 * format
	 */
	public static int byteArrayToInt(byte[] src, int offset, boolean bigEndian){
		int result;
		if( bigEndian ) {
			result =  (src[offset+0] & 0x000000FF)
			       | ((src[offset+1] & 0x000000FF) << 8)
			       | ((src[offset+2] & 0x000000FF) << 16)
			       | ((src[offset+3] & 0x000000FF) << 24);
		} else {
			result =   (src[offset+3] & 0x000000FF)
					| ((src[offset+2] & 0x000000FF) << 8)
					| ((src[offset+1] & 0x000000FF) << 16)
					| ((src[offset+0] & 0x000000FF) << 24);
		}
		return result;
	}
	
	/**
	 * Extracts an integer from 4 bytes within a byte array. The byte ordering (big endian or little endian)
	 * is taken as defined by the flag {@link #defaultBigEndian}.
	 * @param src the byte array which contains the 4 byte to be taken
	 * @param offset the offset to start the extraction within byte array. <code>0</code>
	 * will start at the beginning and use <code>src[0]...src[3]</code>
	 */
	public static int byteArrayToInt(byte[] src, int offset ){
		return byteArrayToInt(src, offset, defaultBigEndian );
	}
}
