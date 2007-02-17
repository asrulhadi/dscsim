/*
 * Created on 08.12.2006
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
 
package net.sourceforge.dscsim.controller.screen;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ScreenLineList extends ArrayList {

	/**
	 * @param arg0
	 */
	public ScreenLineList(int arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public ScreenLineList() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public ScreenLineList(Collection arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public int getVisibleCount(){
		
		int visible=0;
		
		ScreenLine oLine = null;
		for(int i=0;i<size();i++){
			
			oLine = (ScreenLine)get(i);
			
			if(oLine.isVisible()){
				visible++;
			}
			
			
		}
		
		return visible;
	}

}
