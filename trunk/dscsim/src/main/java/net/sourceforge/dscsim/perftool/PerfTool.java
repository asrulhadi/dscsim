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
package net.sourceforge.dscsim.perftool;

import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import net.sourceforge.dscsim.common.SplashScreen;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.radiotransport.Airwave;
import net.sourceforge.dscsim.radiotransport.AirwaveStatusInterface;
import net.sourceforge.dscsim.radiotransport.AirwaveStatusListener;
import net.sourceforge.dscsim.station.SimpleStationInvoker;
import net.sourceforge.dscsim.util.ConfigReader;

/**
 * Standalone application without GUI and radio or controller
 * functionality which provides a "nucleus" for establishing an Airwave
 * P2P network.
 */
public class PerfTool {
	/**
	 * The logger to be used
	 */
	private static Logger logger = Logger.getLogger(PerfTool.class);


    /**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("PerfTool starting");

		Properties properties = System.getProperties();

	    if(args != null){	    			
			if(args.length > 0 && args[0].startsWith(Constants.KEY_STARTUP_FILE)){
				ConfigReader.parseCfg(args, properties, false);   		
			}else{
				ConfigReader.parseArgs(args, properties);
			}
	    }
	    
	    int numberOfAirwaves = 5;
	    int packetSize = 388;   // This is equals to a standard voice packet
	    int delayBetweenPackets = 200;	// 5 packets per second
	    int testIntervall = 10000;

	    
	    ClientThread[] cT = new ClientThread[numberOfAirwaves];
	    for( int i = 0; i < numberOfAirwaves; i++ ){
//	    	cT[i] = new ClientThread(packetSize, delayBetweenPackets);
	    }
	    for( int i = 0; i < numberOfAirwaves; i++ ){
	    	logger.info( "Starting Airwave number "+(i+1));
	    	cT[i] = new ClientThread(packetSize, delayBetweenPackets);
	    	cT[i].start();
		    try {
				Thread.sleep(testIntervall);
			} catch (InterruptedException e1) {
				logger.warn("PerfTool was interrupted");
			}
	    }
	    for( int i = 0; i < numberOfAirwaves; i++ ){
	    	cT[i].shutDown();
	    }
	    for( int i = 0; i < numberOfAirwaves; i++ ){
	    	try {
				cT[i].join();
			} catch (InterruptedException e) {
				logger.warn("PerfTool was interrupted (in join)");
			}
	    }
		logger.info("PerfTool finished");
	    
	}

}
