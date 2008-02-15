/*
 * Created on 18.11.2006
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
 
package net.sourceforge.dscsim.controller.data.types;

import net.sourceforge.dscsim.controller.MultiContentManager;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PositionType extends DscString {

	/**
	 * @param oCode
	 */
	public PositionType(String oCode) {
		super(oCode);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param oOther
	 */
	public PositionType(DscString oOther) {
		super(oOther);
		// TODO Auto-generated constructor stub
	}

	public String getDisplayValue() {
		// TODO Auto-generated method stub
		
		Object oObj = super.getDisplayValue();
		
		if(oObj == null){
			oObj = MultiContentManager.getProperties().getProperty("POS_MNL");
		}
			
		if(oObj != null) {
			return oObj.toString();
		}else{
			return "";
		}
			
	}
}
