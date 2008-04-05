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
 * the Initial Developer are Copyright (C) 2006, 2007, 2008.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.radiotransport;

import org.apache.log4j.Logger;

import net.sourceforge.dscsim.radiotransport.http.HttpAirwave;
import net.sourceforge.dscsim.radiotransport.mock.MockAirwave;
import net.sourceforge.dscsim.radiotransport.udp.UDPAirwave;
import junit.framework.TestCase;

/**
 * @author oliver
 *
 */
public class AirwaveTest extends TestCase {

	/**
	 * The static logger object
	 */
	private static final Logger LOGGER = Logger.getLogger(AirwaveTest.class);
	
	/**
	 * The Airwave under test
	 */
	private Airwave aW;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if( aW != null ) {
			try {
				aW.shutdown();
			} catch( Exception e ) {
				LOGGER.warn("Could not shutdown tested Airwave", e);
			}
		}
		try {
			Airwave.clearInstance();
		} catch( Exception e ) {
			LOGGER.warn("Could not clear instance of tested Airwave", e);
		}
		System.clearProperty("parameter.dscsim.airwave");
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.radiotransport.Airwave#getInstance()}.
	 * Test if an UDPAirwave is generated by default.
	 */
	public void testGetInstanceUDPByDefault() {
		aW = Airwave.getInstance();
		assertTrue( aW instanceof UDPAirwave );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.radiotransport.Airwave#getInstance()}.
	 * Test if an UDPAirwave is generated as fallback in case of error.
	 */
	public void testGetInstanceUDPAsFallback() {
		System.setProperty("parameter.dscsim.airwave", "DoesNotExist");
		aW = Airwave.getInstance();
		assertTrue( aW instanceof UDPAirwave );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.radiotransport.Airwave#getInstance()}.
	 * Test if an UDPAirwave is generated on demand.
	 */
	public void testGetInstanceUDP() {
		System.setProperty("parameter.dscsim.airwave", "UDP");
		aW = Airwave.getInstance();
		assertTrue( aW instanceof UDPAirwave );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.radiotransport.Airwave#getInstance()}.
	 * Tests if a MockAirwave is generated if property "parameter.dscsim.airwave" is set to "Mock".
	 */
	public void testGetInstanceMock() {
		System.setProperty("parameter.dscsim.airwave", "Mock");
		aW = Airwave.getInstance();
		assertTrue( aW instanceof MockAirwave );
	}

}
