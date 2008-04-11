/**
 * 
 */
package net.sourceforge.dscsim.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods to convert lists of byte arrays into a single
 * byte array and vice versa
 * 
 * @author oliver
 */
public class ByteArrayUtil {
	

	/**
	 * Encodes the given List of byte arrays into a single byte array.
	 * @param byteArrayList the list of byte array to be encoded.
	 * @param prefixLength length of the prefix (in bytes) which will
	 * be prepended to the encoded array data. This area may be used to
	 * encode any other data in the resulting byte array.
	 * @return the byte array which contains the encode data
	 */
	public static byte[] encode(List<byte[]> byteArrayList, int prefixLength) {
		int listLength = byteArrayList.size();
		int totalLength = 4 + prefixLength;
		for(byte[] ba : byteArrayList) {
			if( ba != null ) {
				totalLength += 4 + ba.length;
			} else {
				totalLength += 4;
			}
		}
		
		byte[] result = new byte[totalLength];
		int position = prefixLength;
		ByteConverter.intToByteArray(listLength, result, position);
		position += 4;

		for(byte[] ba : byteArrayList) {
			if( ba != null ) {
				ByteConverter.intToByteArray(ba.length, result, position);
				position += 4;
				System.arraycopy(ba, 0, result, position, ba.length);
				position += ba.length;
			} else {
				ByteConverter.intToByteArray(-1, result, position);  // -1 indicates null value
				position += 4;
			}
		}
		
		return result;
	}
	
	/**
	 * Decodes the given byte array into a list of byte arrays. If the byte
	 * array does not contain valid data an IllegalArgumentException is
	 * thrown.
	 * @param byteArray byte array which contains the encoded data
	 * @param prefixLength length of the prefix (in bytes) which is prepended to
	 * the encoded array data. Decoding will skip this area.
	 * @return the List of byte array which is extracted
	 * @throws IllegalArgumentException if the byte array does not contain valid encoded data
	 */
	public static List<byte[]> decode(byte[] byteArray, int prefixLength) {
		List<byte[]> result = null;
		try {
			int position = prefixLength;
			int listLength = ByteConverter.byteArrayToInt(byteArray, position);
			position += 4;
			
			//check for plausibilty
			int maxPossibleListLength = (byteArray.length-prefixLength-4) / 4;
			if(listLength > maxPossibleListLength) {
				throw new IllegalArgumentException( "List length info corrupted ("+listLength+")" );
			}
			int maxPossibleSingleArraySize = byteArray.length-prefixLength-4*(1+listLength);
			
			result = new ArrayList<byte[]>(listLength);
			for(int i=0; i<listLength; i++) {
				int oneByteArraySize = ByteConverter.byteArrayToInt(byteArray, position);
				position += 4;
				byte[] oneByteArray;
				if( oneByteArraySize == -1 ){
					oneByteArray = null;
				} else {
					if(oneByteArraySize > maxPossibleSingleArraySize) {
						throw new IllegalArgumentException( "Array size info corrupted ("+listLength+")" );
					}
					oneByteArray = new byte[oneByteArraySize];
					System.arraycopy(byteArray, position, oneByteArray, 0, oneByteArraySize);
					position += oneByteArraySize;
				}
				result.add(oneByteArray);
			}
			if( position != byteArray.length ) {
				throw new IllegalArgumentException( "Unknown trailing data" );
			}
		} catch(Exception e) {
			throw new IllegalArgumentException("Encoded list of byte arrays is corrupted", e);
		}
		return result;
	}
}
