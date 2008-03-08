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
import java.util.Arrays;

import net.sourceforge.dscsim.radiotransport.Transmitter.Hint;
import junit.framework.TestCase;


/**
 * @author oliver
 *
 */
public abstract class RadiotransportTest extends TestCase {

	/**
	 * The Airwave which is the factory for all objects to be tested
	 */
	private Airwave testAirwave;
	
	/**
	 * @param name
	 */
	public RadiotransportTest(String name) {
		super(name);
	}

	/**
	 * Creates the Airwave which is the factory for all objects under test
	 * @return the Airwave
	 */
	public abstract Airwave createAirwave();
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		testAirwave = createAirwave();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		testAirwave.shutdown();
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.radiotransport.Airwave#createAntenna()}.
	 */
	public void testSimpleTransmissionSameAntenna() {
		Antenna antenna = testAirwave.createAntenna();
		Transmitter transmitter = antenna.createTransmitter();
		Receiver receiver = antenna.createReceiver();
		MockDemodulator demodulator = new MockDemodulator();
		receiver.addDemodulator(demodulator);
		byte[] testData = new byte[100];
		for( int i=0; i<testData.length; i++ ) {
			testData[i] = (byte) (i % 11);
		}
		transmitter.transmit(testData, new Hint() );
		assertEquals(demodulator.getCallCounter(), 1);
		assertTrue("Transmitted data corrupted", Arrays.equals(testData, demodulator.getSignal()) );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.radiotransport.Airwave#createAntenna()}.
	 */
	public void testSimpleTransmissionSameAirwaveDifferentAntenna() {
		Antenna transmitAntenna = testAirwave.createAntenna();
		Antenna receiveAntenna = testAirwave.createAntenna();
		Transmitter transmitter = transmitAntenna.createTransmitter();
		Receiver receiver = receiveAntenna.createReceiver();
		MockDemodulator demodulator = new MockDemodulator();
		receiver.addDemodulator(demodulator);
		byte[] testData = new byte[100];
		for( int i=0; i<testData.length; i++ ) {
			testData[i] = (byte) (i % 11);
		}
		transmitter.transmit(testData, new Hint() );
		assertEquals(demodulator.getCallCounter(), 1);
		assertTrue("Transmitted data corrupted", Arrays.equals(testData, demodulator.getSignal()) );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.radiotransport.Airwave#createAntenna()}.
	 */
	public void testSimpleTransmissionDifferentAirwave() throws Exception {
		Airwave receiveAirwave = createAirwave();
		Thread.sleep(3000);
		try {
			Antenna transmitAntenna = testAirwave.createAntenna();
			Antenna receiveAntenna = receiveAirwave.createAntenna();
			Transmitter transmitter = transmitAntenna.createTransmitter();
			Receiver receiver = receiveAntenna.createReceiver();
			MockDemodulator demodulator = new MockDemodulator();
			receiver.addDemodulator(demodulator);
			byte[] testData = new byte[100];
			for (int i = 0; i < testData.length; i++) {
				testData[i] = (byte) (i % 11);
			}
			transmitter.transmit(testData, new Hint());
			assertEquals(demodulator.getCallCounter(), 1);
			assertTrue("Transmitted data corrupted", Arrays.equals(testData,
					demodulator.getSignal()));
		} finally {
			receiveAirwave.shutdown();
			Thread.sleep(1000);
		}
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.radiotransport.Airwave#createAntenna()}.
	 */
	public void testSimpleTransmission2DifferentAirwave() throws Exception {
		Airwave receiveAirwave = createAirwave();
		Airwave receiveAirwave2 = createAirwave();
		Thread.sleep(6000);
		try {
			Antenna transmitAntenna = testAirwave.createAntenna();
			Antenna receiveAntenna = receiveAirwave.createAntenna();
			Antenna receiveAntenna2 = receiveAirwave2.createAntenna();
			Transmitter transmitter = transmitAntenna.createTransmitter();
			Receiver receiver = receiveAntenna.createReceiver();
			Receiver receiver2 = receiveAntenna2.createReceiver();
			MockDemodulator demodulator = new MockDemodulator();
			MockDemodulator demodulator2 = new MockDemodulator();
			receiver.addDemodulator(demodulator);
			receiver2.addDemodulator(demodulator2);
			byte[] testData = new byte[100];
			for (int i = 0; i < testData.length; i++) {
				testData[i] = (byte) (i % 11);
			}
			transmitter.transmit(testData, new Hint());
			assertEquals(demodulator.getCallCounter(), 1);
			assertTrue("Transmitted data corrupted", Arrays.equals(testData,
					demodulator.getSignal()));
			assertEquals(demodulator2.getCallCounter(), 1);
			assertTrue("Transmitted data corrupted", Arrays.equals(testData,
					demodulator2.getSignal()));
		} finally {
			receiveAirwave.shutdown();
			receiveAirwave2.shutdown();
			Thread.sleep(1000);
		}
	}

}
