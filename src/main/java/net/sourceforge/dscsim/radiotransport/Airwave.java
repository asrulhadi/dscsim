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

import net.sourceforge.dscsim.radiotransport.udp.UDPAirwave;

/**
 * The main entry into the whole radiotransport package. The Airwave is implemented as
 * singleton and is the factory for Antenna objects.
 *
 */
public abstract class Airwave {
	
	/**
	 * The single instance
	 */
	private static Airwave _theInstance;
	
	/**
	 * default constructor
	 */
	protected Airwave() {
	}
	
	/**
	 * Gets the Airwave instance (singleton)
	 * @return
	 */
	public static Airwave getInstance() {
		synchronized( Airwave.class ){
			if( _theInstance == null ){
				_theInstance = new UDPAirwave();
			}
		}
		return _theInstance;
	}

	/**
	 * Creates an Antenna object
	 * @return an Antenna object in the context of this Airwave
	 */
	public abstract Antenna createAntenna();
	
	/**
	 * Shutdown this Airwave.
	 * The airwave will stop working. Trying to communicate via this airwave
	 * might throw an {@IllegalStateException}
	 */
	public abstract void shutdown();

}
