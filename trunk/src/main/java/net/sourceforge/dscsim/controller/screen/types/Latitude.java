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
public class Latitude extends Coordinate {
	
	/**
	 * @param oElement
	 * @param oBean
	 */
	public Object copyObject(){
		return new Latitude(this);
	}
	
	public Latitude(){
		super(new String[]{"", "", "NS"}, new int[]{2, 2, 1});
	}
	public Latitude(Latitude oOther){
		super(oOther);
	}
	public Latitude(Element oElement, Object oBean) {
		super(oElement, oBean, new String[]{"", "", "NS"}, new int[]{2, 2, 1});
	}
	
	public Latitude(Element oElement) {
		super(oElement, null, new String[]{"", "", "NS"}, new int[]{2, 2, 1});
	}
	
	
	public Latitude(String deg, String min, String hem) {
		super(new String[]{deg, min, hem}, new int[]{2, 2, 1});
	}

	protected boolean validateInput(String currValue, String newValue, int partID, int partLen) {
	
		boolean retValue = true;
		
		switch(partID){
		
			case DEG_COORD:{
				
				int checkValue = 0;
				
				if(currValue.length() == 0 || currValue.length() == 1){
					if(newValue.length()>0)
						checkValue = Integer.parseInt(currValue + newValue);
					else
						return false;
				}else{
					checkValue = Integer.parseInt(currValue);
				}
				
				if(checkValue > 90)
					retValue = false;
				
				break;
			}
			case MIN_COORD:{
			
				int checkValue = 0;
				
			    if(getDegrees()==90){
			    	
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
		// TODO Auto-generated method stub
		
		if(!super.isValid())
			return false;
		
		int t = super.getDegrees() * 10000
				+ getMinutes() *100;
		
		return (t >= 0 && t <= 900000);
		
	}
	protected String getInvalidDisplay() {
		return "no position data";
	}

	protected String getDisplayLabel() {
		return "Lat:  ";
	}
	public String toXml() {
		return "<latitude>"
		 		+ super.toXml()
		 		+ "</latitude>";
	}
	public void fromXml(String inStr) throws Exception {
		
		if(Utilities.getAttributeValue("latitude", inStr) != null){
			super.fromXml(inStr); 
		} else {
			throw new Exception("Latitude.fromXml - missing latitude tag.");
		}
	
	}
	public void reset() {
		super.reset(new String[]{"", "", "NS"}, new int[]{2, 2, 1});
		
	}
	
		
}
