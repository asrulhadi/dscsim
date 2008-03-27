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
package net.sourceforge.dscsim.radiotransport;

import net.sourceforge.dscsim.util.ByteConverter;

/**
 * Class for specifying (power) levels and gains. Internal representation is based on
 * integer values which might result in notable rounding effects 
 */
public class Decibel implements Comparable {
	
	/**
	 * The internal scaling factor
	 */
	private static final int SCALE = 1000;
	
	/**
	 * Constant for converting from ln to log10
	 */
	private static final double M = 1.0 / Math.log(10.0);
	
	/**
	 * The scaled internal value
	 */
	private int _value;
	
	/**
	 * Constructor which sets the internal value (private)
	 * @param value
	 */
	private Decibel(int value){
		_value = value;
	}
	
	/**
	 * Creates a Decibel object from a linear value
	 * @param linValue the linear value (power in watt or gain)
	 * @return the Decibel object representing the linear value
	 */
	public static Decibel fromLinearPower( double linValue ){
		return new Decibel( Math.round( (float)(M * Math.log(linValue) * 10 * SCALE) ) );
	}
	
	/**
	 * Creates a Decibel object from a decibel value
	 * @param dBValue the decibel value given as double
	 * @return the Decibel object representing the dB value
	 */
	public static Decibel fromDb( double dbValue ){
		return new Decibel( Math.round( (float)(dbValue * SCALE) ) );
	}
	
	/**
	 * Returns the linear value represented by this Decibel object
	 * @return the linear value (might e.g. be power in watt or gain)
	 */
	public double getLinearPower() {
		return Math.pow(10.0, _value / (10.0 * SCALE) );
		
	}

	/**
	 * Returns the decibel value represented by this Decibel object
	 * @return the decibel value in double format
	 */
	public double getDb() {
		return  _value / (1.0 * SCALE);
		
	}
	
	/**
	 * Adds the Decibel object given as arguments to the current Decibel object
	 * The current object remains unchanged
	 * @param arg the Decibel object to be added
	 * @return the result 
	 */
	public Decibel add(Decibel arg) {
		return new Decibel(_value + arg._value );
	}
	
	/**
	 * Subtracts the Decibel object given as arguments from the current Decibel object.
	 * The current object remains unchanged
	 * @param arg the Decibel object to be subtracted
	 * @return the result 
	 */
	public Decibel sub(Decibel arg) {
		return new Decibel(_value - arg._value );
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object other) {
	    return (this._value<((Decibel)other)._value ? -1 : (this._value==((Decibel)other)._value ? 0 : 1));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		try{
			if( this._value == ((Decibel)other)._value ) {
				return true;
			}
		} catch( ClassCastException e ) {
			// do nothing (just return false)
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return Double.toString(getDb())+" dB";
	}
	
	/**
	 * Converts the Decibel object to a byte array representation.
	 * @param dest the byte array where the result is stored
	 * @param offset the offset to start within the destination byte array. <code>0</code>
	 * will start at the beginning and use <code>dest[0]...dest[3]</code>
	 */
	public void toByteArray( byte[] dest, int offset){
		ByteConverter.intToByteArray(_value, dest, offset );
	}
	
	/**
	 * Extracts a Decibel object from 4 bytes within a byte array.
	 * @param src the byte array which contains the 4 byte to be taken
	 * @param offset the offset to start the extraction within byte array. <code>0</code>
	 * will start at the beginning and use <code>src[0]...src[3]</code>
	 */
	public static Decibel fromByteArray(byte[] src, int offset ){
		return new Decibel( ByteConverter.byteArrayToInt(src, offset ) );
	}
	
}
