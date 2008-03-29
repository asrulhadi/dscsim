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
package net.sourceforge.dscsim.radiotransport.http;

import net.sourceforge.dscsim.httpserver.server.DscsimServer;
import net.sourceforge.dscsim.radiotransport.Airwave;
import net.sourceforge.dscsim.radiotransport.RadiotransportTest;

/**
 * @author oliver
 *
 */
public class HttpRadiotransportTest extends RadiotransportTest {

	private DscsimServer server;

	public HttpRadiotransportTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		server = new DscsimServer();
		server.startServer();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		server.stopServer();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.RadiotransportTest#createAirwave()
	 */
	@Override
	public Airwave createAirwave() {
		System.setProperty("parameter.dscsim.http_airwave.server_url", "http://localhost:8080/");
		HttpAirwave aw = new HttpAirwave();
		return aw;
	}

}
