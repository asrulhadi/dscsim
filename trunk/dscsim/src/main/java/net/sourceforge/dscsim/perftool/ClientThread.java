/**
 * 
 */
package net.sourceforge.dscsim.perftool;

import org.apache.log4j.Logger;

import net.sourceforge.dscsim.radiotransport.Airwave;
import net.sourceforge.dscsim.radiotransport.Transmitter;
import net.sourceforge.dscsim.radiotransport.Transmitter.Hint;

/**
 * @author oliver
 *
 */
public class ClientThread extends Thread {

	/**
	 * The logger for this object
	 */
	private static final Logger LOGGER = Logger.getLogger(ClientThread.class);
	private int packetSize;
    private int delayBetweenPackets;
    private byte[] sendData;
    private Airwave airwave;
    private Transmitter transmitter;
    private boolean shutdown;

	public ClientThread(int packetSize, int delayBetweenPackets) {
		this.packetSize = packetSize;
		this.delayBetweenPackets = delayBetweenPackets;
		this.sendData = new byte[packetSize];
		for(int i=0; i<packetSize; i++) {
			sendData[i] = (byte)i;
		}
		
		airwave = Airwave.getInstance();
		Airwave.clearInstance();
		transmitter = airwave.createAntenna().createTransmitter();
		shutdown = false;
	}

	public void shutDown() {
		shutdown = true;
		airwave.shutdown();
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while(!shutdown) {
			transmitter.transmit(sendData, new Hint() );
			try {
				Thread.sleep(delayBetweenPackets);
			} catch (InterruptedException e) {
				continue;
			}
		}
	}
	

}
