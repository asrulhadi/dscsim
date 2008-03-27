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
package net.sourceforge.dscsim.server;

import java.util.Properties;

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
public class AirwaveNucleusServer {
	/**
	 * The logger to be used
	 */
	private static Logger logger = Logger.getLogger(AirwaveNucleusServer.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("AirwaveNucleusServer starting");

		Properties properties = System.getProperties();

	    if(args != null){	    			
			if(args.length > 0 && args[0].startsWith(Constants.KEY_STARTUP_FILE)){
				ConfigReader.parseCfg(args, properties, false);   		
			}else{
				ConfigReader.parseArgs(args, properties);
			}
	    }
	    final Airwave airwave = Airwave.getInstance();
	    AirwaveStatusInterface statusInterface = null;
		if( airwave instanceof AirwaveStatusInterface ) {
			statusInterface = (AirwaveStatusInterface)airwave;
			statusInterface.registerStatusListener(
					new AirwaveStatusListener() {
						public void notifyNetworkStatus() {
							logger.info("Airwave status changed: "+((AirwaveStatusInterface)airwave).getStatusString());
						}
						
					}
			);
		}
		logger.info("AirwaveNucleusServer started");
	    while(true) {
	    	try {
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				logger.debug("Thread interrupted");
			}
			String additionalInfo = "";
			if( statusInterface != null ) {
				additionalInfo = statusInterface.getStatusString();
			}
			logger.info("AirwaveNucleusServer is alive "+additionalInfo);
	    }
	}

}
