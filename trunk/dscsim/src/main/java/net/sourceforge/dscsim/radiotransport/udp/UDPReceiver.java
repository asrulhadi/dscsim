/**
 * 
 */
package net.sourceforge.dscsim.radiotransport.udp;

import java.util.HashSet;

import net.sourceforge.dscsim.radiotransport.Frequency;
import net.sourceforge.dscsim.radiotransport.Receiver;
import net.sourceforge.dscsim.radiotransport.impl.AbstractReceiver;

/**
 * @author oliver
 *
 */
public class UDPReceiver extends AbstractReceiver implements Receiver {
	
	/**
	 * The connected antenna
	 */
	UDPAntenna _antenna;
	
	/**
	 * Constructor which takes the antenna as argument
	 * @param antenna the connected antenna
	 */
	UDPReceiver(UDPAntenna antenna) {
		super();
		_antenna = antenna;
	}

	@Override
	protected UDPAntenna getAntenna() {
		return _antenna;
	}
	

}
