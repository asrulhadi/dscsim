/**
 * 
 */
package net.sourceforge.dscsim.radiotransport.mock;

import net.sourceforge.dscsim.radiotransport.Airwave;
import net.sourceforge.dscsim.radiotransport.Antenna;

/**
 * A mock Airwave.
 * 
 * @author oliver
 */
public class MockAirwave extends Airwave {

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Airwave#createAntenna()
	 */
	@Override
	public Antenna createAntenna() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Airwave#shutdown()
	 */
	@Override
	public void shutdown() {
	}

}
