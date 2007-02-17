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

import java.io.Serializable;

/**
 * Class used for storing address information in controllers addressbook.
 * 
 * @author Pennoyer
 *
 */
public class AddressIdEntry implements Serializable {

	/**
	 * Storage field for MMSI.
	 */
	private String _id = null;
	
	/**
	 * Name associated with MMSI.
	 */
	private String _name = null;
	
	/**
	 * Constructor for creating empty address.
	 *
	 */
	public AddressIdEntry(){
		this("", "");
	}
	
	/**
	 * Constructor for creating instance with an MMSI and name.
	 * @param id is normally the MMSI.
	 * @param name is the description for the MMSI.
	 */
	public AddressIdEntry(String id, String name){
		_id = id;
		_name = name;
	}
	
	/**
	 * set the MMSI.
	 * @param id
	 */
	public void setId(String id){
		_id = id;
	}
	
	/**
	 * get the MMSI.
	 * @return MMSI as String.
	 */
	public String getId(){
		return _id;
	}
	
	/**
	 * Set name of address.
	 * @param name
	 */
	public void setName(String name){
		_name = name;
	}
	
	/**
	 * Get name associated with address.
	 * @return Name of address.
	 */
	public String getName(){
		return _name;
	}
}
