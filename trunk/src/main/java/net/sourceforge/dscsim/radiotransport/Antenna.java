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

/**
 * Represents an antenna.
 *
 */
public interface Antenna {
	/**
	 * Sets the elevation of the antenna
	 * @param elevation of the antenna in metres above sea level
	 */
	public void setElevation(int elevation);
	
	/**
	 * Sets the geographical position of the antenna.
	 * @param the geographical position of the antenna.
	 */
	public void setPostion(GeoPosition position);
	
	/**
	 * Sets the master flag. This is only to be used for simulation/education pursposes.
	 * If the master flag is set then a connected receiver will receive all stations, even
	 * those which can normally not be received due to exceeded coverage. Transmitters connected
	 * to such an antenna will also be received by all other stations. This flag can be used
	 * enable the instructors station to tals to all other stations and to receive all other
	 * stations
 	 * @param flag the master flag. When set to <code>true</code> the coverage of the antenna
 	 * is unlimitted.
	 */
	public void setMaster(boolean flag);

	/**
	 * Creates a Receiver.
	 * @return a Receiver which is connected to this antenna
	 */
	public Receiver createReceiver();
	
	/**
	 * Creates a Transmitter.
	 * @return a Transmitter which is connected to this antenna
	 */
	public Transmitter createTransmitter();
	
}
