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
package net.sourceforge.dscsim.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.StringTokenizer;

import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.StartupConfiguration;
import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * Utilities for reading in the configuration settings (command line
 * or configuration file)
 */
public class ConfigReader {

	/**
	* Parse startup information from file. If no file is found, then prompt the user for a 
	* startup configuration string and write it to disc. 
	* @param as[] runtime parameter from main.
	* @param interactive <code>true</code> if the file was not found will prompt the user to enter
	* configuration data and create the file; <code>false</code> will throw an Exception
	* if the file was not found
	*/
	public static void parseCfg(String as[], Properties properties, boolean interactive){
	
	    boolean again = true;	
		do{
			int j = as[0].indexOf(61);
			String fileName =  as[0].substring(j + 1);
	
			try {
				
				String cfgStr = ConfigReader.getConfigString(fileName);
				
				StringTokenizer cfgTknzr = new StringTokenizer(cfgStr);
	
				while(cfgTknzr.hasMoreElements()){
					
					cfgStr =  cfgTknzr.nextToken();
				    AppLogger.debug2("MainFrame.parseCfg = " + cfgStr);	
				    j = cfgStr.indexOf(61);
				    
				    if(j>-1){
				    	properties.put("parameter." + cfgStr.substring(0, j).toLowerCase(), cfgStr.substring(j + 1));
				    }
					
				}
				
				again = false;
	
			} catch (FileNotFoundException oEx) {
				AppLogger.error(oEx);
				if(interactive) {
					again = ConfigReader.getUserInput(fileName);
				} else {
					again = false;
				}
			} catch(Exception e){
				AppLogger.error(e);
				again = false;
			}
			
		}while(again == true);
		
	}

	/**
	* getConfigString try to read file and parse contents.
	* @param fileName where to find input file
	* @return parse startup string.
	*/
	public static String getConfigString(String fileName) throws FileNotFoundException, IOException, StringIndexOutOfBoundsException {
		 	String cfgStr = null;
					
			FileInputStream fis = new FileInputStream(fileName);
			char buf[] = new char[Constants.READ_BUFF_SMALL];
			
			Reader r = new BufferedReader(new InputStreamReader(fis));
			
			int read = r.read(buf);
							
			cfgStr =  String.valueOf(buf, 0, read);
		  
			return cfgStr;
	  }

	/**
	* getUserInput pop up dialog and prompt user for string
	* @param fileName where to write contents of string
	* @return true is OK button was pushed on dialog; otherwise, false;
	*/
	public static boolean getUserInput(String fileName){
		StartupConfiguration dlg = new StartupConfiguration();
		
		dlg.setCfgFile(fileName);
		dlg.setModal(true);
		dlg.show();
		
		return dlg.okPressed();
		
	}

	public static void parseArgs(String as[], Properties properties){
	    for(int i = 0; i < as.length; i++){
	      AppLogger.debug2("SimpleStation.parseArgs = " + as[i]);
	      String s = as[i];
	      int j = s.indexOf(61);
	      if(j == -1)
	        properties.put("parameter." + s.toLowerCase(), "");
	      else
	        properties.put("parameter." + s.substring(0, j).toLowerCase(), s.substring(j + 1));
	    }
	
	  }

}
