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
 * Methods for getting the status of an Airwave 
 */
public interface AirwaveStatusInterface {

	/**
	 * Indicates that network status is GREEN
	 */
	public static final int STATUS_GREEN = 0;
	
	/**
	 * Indicates that network status is YELLOW
	 */
	public static final int STATUS_BLUE = 1;

	/**
	 * Indicates that network status is YELLOW
	 */
	public static final int STATUS_YELLOW = 2;
	
	/**
	 * Indicates that network status is RED
	 */
	public static final int STATUS_RED = 3;

	/**
	 * Gets the network status of the airwave
	 * @return the network status given as one of the predifined
	 *   constants
	 */
	public int getNetworkStatus();
	
	/**
	 * Registers a listener for status notfications
	 * @param listener the object which should receive status information
	 */
	public void registerStatusListener( AirwaveStatusListener listener );
	
	/**
	 * Gets a very brief status info. This might be displayed on some
	 * monitoring GUI
	 */
	public String getStatusString();
	

}
