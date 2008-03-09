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
 * The Initial Developer of the Original Code is William Pennoyer. Portions created by
 * the Initial Developer are Copyright (C) 2008, 2009.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */

package net.sourceforge.dscsim.controller.message.types;

import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.utils.AppLogger;

public class MMSI implements Constants {

	private String mmsi = "";
	
	public MMSI(){
	}
	
	public MMSI(String mmsi){		
		this.mmsi = mmsi;
	}
	
	public MMSI(MMSI oOther){
		this.mmsi = oOther.mmsi;
	}
	
	public String getValue(){
		String strVal = null;	
		return strVal;
	}

	public boolean isComplete(){	
		if(mmsi.length() == 9){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean validateMMSI(String strMMSI){
		boolean result = false;
		
		if(strMMSI == null)
			return result;
		
		try {
			int intMMSI = Integer.parseInt(strMMSI);

			if(strMMSI.length()<=9){
				result = true;
			}else{
				result =  false;
			}
						
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
		
		return result;
	}

	public boolean isValid() {	
		return validateMMSI(mmsi);
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.BaseType#getInvalidDisplay()
	 */
	protected String getInvalidDisplay() {
		// TODO Auto-generated method stub
		return "no MMSI data";
	}				
	
	public Object copyObject(){
		return new MMSI(this);
	}
	
	public String toString(){
		return mmsi;
	}

	
	/*
	 * check if MMSI is a coastal radio station.
	 */
	public boolean isCoastal(){	
		if(!isValid())
			return false;		
		return mmsi.startsWith("00");		
	}
	
	public static boolean isValidMMSI(String mmsi){
		return validateMMSI(mmsi); 
	}
}