/*
 * Created on 18.07.2006
 * katharina
 * 
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
 * the Initial Developer are Copyright (C) 2006, 2007, 2008, 20010.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
 
package net.sourceforge.dscsim.controller.screen.types;

import java.util.Properties;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.DscUtils;
import net.sourceforge.dscsim.controller.utils.Utilities;

import org.jdom.Element;



/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Longitude extends Coordinate {
	
	public Longitude(){
		super(new String[]{"", "", "EW"}, new int[]{3, 2, 1});		
	}
	public Object copyObject(){
		return new Longitude(this);
	}
	public Longitude(Longitude oOther){
		super(oOther);
	}
	public Longitude(Element oElement, Object oBean) {
		super(oElement, oBean, new String[]{"", "", "EW"}, new int[]{3, 2, 1});
	}
	
	public Longitude(Element oElement) {
		super(oElement, null, new String[]{"", "", "EW"}, new int[]{3, 2, 1});
	}
	
	public Longitude(String degs, String min, String hem) {
		super(new String[]{degs, min, hem}, new int[]{3, 2, 1});
	}

	protected boolean validateInput(String currValue, String newValue, int partID, int partLen) {
		boolean retValue = true;
		
		switch(partID){
		
			case DEG_COORD:{
				int checkValue = 0;
				
				if(currValue.length() == 0){
					checkValue = Integer.parseInt(newValue);				
					if(checkValue > 1)
						retValue = false;
					
				} else if(currValue.length() == 1){
					checkValue = Integer.parseInt(currValue + newValue);
					if(checkValue > 18)
						retValue = false;
							
				} else {
					checkValue = Integer.parseInt(currValue + newValue);
					if(checkValue > 180)
						retValue = false;
					
				}
				
				break;
			}
			
			case MIN_COORD:{
			
				int checkValue = 0;
				
			    if(getDegrees()==180){
			    	
					checkValue = Integer.parseInt(newValue);
					
					if(checkValue != 0)
						retValue = false;
				
			    }
			    
				if(currValue.length() > 0){
					checkValue = Integer.parseInt(currValue + newValue);
					
					if(checkValue > 59)
						retValue = false;
				} else {
					checkValue = Integer.parseInt(newValue);
					
					if(checkValue > 5)
						retValue = false;
					
				}
				break;
			}

		}
		return retValue;

	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#isValid()
	 */
	public boolean isValid() {
		
		if(!super.isValid())
			return false;
		
		int t = super.getDegrees() * 10000
				+ getMinutes() *100;
		
		return (t >= 0 && t <= 1800000);
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.Coordinate#getInvalidDisplay()
	 */
	protected String getInvalidDisplay() {
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see applet.screen.bean.types.Coordinate#getDisplayLabel()
	 */
	protected String getDisplayLabel() {
		return "Lon: ";
	}
	public String toXml() {
		return "<longitude>"
 		+ super.toXml()
 		+ "</longitude>";
	}
	public void fromXml(String inStr) throws Exception {
		if(Utilities.getAttributeValue("longitude", inStr) != null){
			super.fromXml(inStr);	
		} else {
			throw new Exception("Longitude.fromXml - missing latitude tag.");
		}
	}
	/* (non-Javadoc)
	 * @see applet.screen.bean.types.ActiveField#reset()
	 */
	public void reset() {
		super.reset(new String[]{"", "", "EW"}, new int[]{3, 2, 1});
		
	}
	
	public String getAsFromattedString(Properties props){
		if(this.isValid())
			return super.getAsFromattedString(props.getProperty(LON_FORMAT));
		else
			return props.getProperty(MS_LON_NON);
	}
	

}
