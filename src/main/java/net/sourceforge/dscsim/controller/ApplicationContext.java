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
 * The Initial Developer of the Original Code is William Pennoyer. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007, 2008, 20010.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.controller;

/**
 * Information used to configure simulator from startup parameters.
 *  
 * @author William Pennoyer
 *
 */
public interface ApplicationContext {
	
	/**
	 * Get the inter-application communication method - UDP, AIRWAVE.
	 * @return String.
	 */
	String getIACMethod();
	
	/**
	 * Get the ip address ofthe master.
	 * @return
	 */
	String getMasterIPAddress();
	/**
	 * Get the port of the master.
	 * @return String.
	 */
	String getMasterPort();
	
	/**
	 * Get user for the provider.
	 * @return String.
	 */
	String getProviderUser();
	/**
	 * Get the User's password.
	 * @return String.
	 */
	String getProviderPassword();
	
	/**
	 * Get the mode, which is either slave or master.
	 * @return
	 */
	String getMode();
	
	/**
	 * Get inter-application sychronization method.
	 * @return String.
	 */
	String getIACSync();
	
	/**
	 * Get provider URL.
	 * @return String.
	 */
	String getProviderUrl();
	
	/**
	 * Get provider subject.
	 * @return String.
	 */
	String getProviderSubject();
	
	/**
	 * Get the session's MMSI.
	 * @return String.
	 */
	String getIndividualMmsi();
	
	/**
	 * Get the session's group MMSI.
	 * @return String.
	 */
	String getGroupMmsi();
	
	/**
	 * Get the xml file's name containing the screen information.
	 * @return String.
	 */
	String getScreenFileName();
	
	/**
	 * Get the IP address for AIRWAVE.
	 * @return String.
	 */
	String getUdpAirWave();
}
