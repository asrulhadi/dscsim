/*
 * Created on 29.06.2006
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

import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.screen.types.ActiveField;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Element;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class BeanBaseScreen extends ScreenContent implements Constants {
	
	protected MultiContentManager _oContentManager = null;
	public int getDisplayedFrom(){
		
		int t = super.getDisplayedFrom();
		
		AppLogger.debug("BeanScreen.getDisplayForm "+ t);
		
		return t;
	}
	
	protected MultiContentManager getMultiContentManager(){
		return _oContentManager;
	}
	public int getDisplayedTo(){
		
		int t = super.getDisplayedTo();
		
		AppLogger.debug("BeanScreen.getDisplayTo "+ t);
		
		return t;
	}
	public int getActiveLine(){
		
		int t = super.getActiveLine();
		
		AppLogger.debug("BeanScreen.getActiveLine "+ t);
		
		return t+1;
	}
	
	public BeanLine getNieghborInputLine(BeanLine oTarget, boolean next) {
			
		BeanLine oNeighborBeanLine = null;
		BeanLine oLine = null;
		
		//forwards
		if(next == false){			
			for(int i = 0; i < _oLines.size(); i++) {
				oLine = (BeanLine)_oLines.get(i);
				
				if(oTarget == oLine)
					return oNeighborBeanLine;
				
				if(oLine.hasEntryBean()){
					oNeighborBeanLine = oLine;
					
				}					
			}						
		} else {
		
			for(int i = _oLines.size()-1; i > -1; i--) {
				oLine = (BeanLine)_oLines.get(i);
				
				if(oTarget == oLine)
					return oNeighborBeanLine;
				
				if(oLine.hasEntryBean()){
					oNeighborBeanLine = oLine;
					
				}					
			}	
		}
	
		return oNeighborBeanLine;
	
	}

	public int getIndexOfLine(BeanLine oTarget) {
		
		Object oLine = null;
		for(int i=0; i<_oLines.size(); i++) {
			oLine = _oLines.get(i);
			
			if(oLine == oTarget)
				return i;
		}
					
		return -1;
	}

	public String getBeanValue(String beanName) {
			
		BeanLine oBeanLine = null;
		ActiveField oBean = null;
		String tarName = null;
		for(int i=0; i<_oLines.size();i++){
			
			oBeanLine = (BeanLine)_oLines.get(i);
			
			oBean = oBeanLine.getBean(beanName);
			
			if(oBean != null){
				
				tarName = oBean.getFieldName();
				
				if(beanName.equals(tarName)){
					return oBean.getDisplayValue();
				}
				
			}
				
		}
		
		return "";
	}

	public BeanBaseScreen(Element oScreenElement) {
		super(oScreenElement);	
	}
	
	public BeanBaseScreen(Element oScreenElement, MultiContentManager oCMngr) {
		super(oScreenElement);	
		
		_oContentManager = oCMngr;
	}

}


