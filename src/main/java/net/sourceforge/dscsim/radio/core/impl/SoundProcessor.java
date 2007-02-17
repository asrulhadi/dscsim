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
package net.sourceforge.dscsim.radio.core.impl;

/**
 * Interface of objects which do processing on sound data
 */
public interface SoundProcessor {
	/**
	 * Processes the sound data given in the array 
	 * @param soundData a byte array containing the sound data
	 * @param littleEndian flag which indicates if the format is little endian or
	 * big endian
	 */
	public void process( byte[] soundData, boolean littleEndian );

}
