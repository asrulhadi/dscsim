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
package net.sourceforge.dscsim.common;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import net.sourceforge.dscsim.orga.FrequencyAssignApp;
import net.sourceforge.dscsim.server.AirwaveNucleusServer;
import net.sourceforge.dscsim.station.SimpleStationInvoker;

/**
 * Starter Class for dscsim to be used when started via the executable jar file
 */
public class DscsimStarter {

	/**
	 * static main method which dispatches to <br/>
	 * {@link net.sourceforge.dscsim.server.AirwaveNucleusServer#main(String[])} 
	 * if the first command line argument is "server" (arguments will be
	 * shifted by one)<br/>
	 * {@link net.sourceforge.dscsim.station.SimpleStationInvoker#main(String[])} 
	 * otherwise
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		String log4jConfigFile = System.getProperty("log4j.configFile");
		if( log4jConfigFile != null ) {
			File configFile = new File(log4jConfigFile);
			if( !configFile.isFile() ) {
				throw new RuntimeException( "Log4J config file given " +
						"by property log4j.configFile ("+log4jConfigFile+
						") does not exist");
			}
			PropertyConfigurator.configure(log4jConfigFile);
		}
		
		if( args.length == 0 ) {
			startRadioAndController(args);
		} else {
			if( "nucleus".equals(args[0]) ) {
				String[] newArgs = new String[args.length-1];
				for( int i=0; i<newArgs.length; i++) {
					newArgs[i] = args[i+1];
				}
				startAirwaveNucleus(newArgs);
			} else if( "orga".equals(args[0]) ) {
				startOrga(args);
			} else {
				startRadioAndController(args);
			}
		}

	}

	/**
	 * Starts the airwave nucleus
	 * @param args the command line parameters
	 */
	private static void startAirwaveNucleus(String[] args) {
		AirwaveNucleusServer.main(args);
	}


	/**
	 * Starts the normal station gui (radio and controller)
	 * @param args the command line parameters
	 */
	private static void startRadioAndController(String[] args) {
		SimpleStationInvoker.main(args);
	}

	/**
	 * Starts the gui to generate HTML pages with setup
	 * parameters
	 * @param args the command line parameters
	 */
	private static void startOrga(String[] args) {
		FrequencyAssignApp.main(args);
	}
	
}
