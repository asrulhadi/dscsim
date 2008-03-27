/**
 * 
 */
package net.sourceforge.dscsim.radiotransport.udp;

import net.sourceforge.dscsim.radiotransport.Transmitter;
import net.sourceforge.dscsim.radiotransport.impl.AbstractAntenna;
import net.sourceforge.dscsim.radiotransport.impl.AbstractTransmitter;
import net.sourceforge.dscsim.radiotransport.impl.TransmissionPacket;

/**
 * @author oliver
 *
 */
public class UDPTransmitter extends AbstractTransmitter implements Transmitter {

	/**
	 * The connected antenna
	 */
	private AbstractAntenna _antenna;
	
	/**
	 * Constructor which takes the antenna as argument
	 * @param antenna the connected antenna
	 */
	UDPTransmitter(AbstractAntenna antenna) {
		super();
		_antenna = antenna;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.Transmitter#transmit(byte[], net.sourceforge.dscsim.radiotransport.Transmitter.Hint)
	 */
	public void transmit(byte[] signal, Hint hint) {
		TransmissionPacket packet = createTransmissionPacket(signal);
		_antenna.sendSignal(packet);
	}

}
