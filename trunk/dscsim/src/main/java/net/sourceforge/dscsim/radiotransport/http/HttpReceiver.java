/**
 * 
 */
package net.sourceforge.dscsim.radiotransport.http;

import java.util.HashSet;

import net.sourceforge.dscsim.radiotransport.Frequency;
import net.sourceforge.dscsim.radiotransport.Receiver;
import net.sourceforge.dscsim.radiotransport.impl.AbstractReceiver;

/**
 * @author oliver
 *
 */
public class HttpReceiver extends AbstractReceiver implements Receiver {
	
	/**
	 * The connected antenna
	 */
	HttpAntenna _antenna;
	
	/**
	 * Constructor which takes the antenna as argument
	 * @param antenna the connected antenna
	 */
	HttpReceiver(HttpAntenna antenna) {
		super();
		_antenna = antenna;
	}

	@Override
	protected HttpAntenna getAntenna() {
		return _antenna;
	}
	

}
