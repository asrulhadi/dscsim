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

import java.util.List;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.MultiContentManager;

import org.jdom.Element;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BeanScreen extends BeanBaseScreen implements Constants {

	private  BeanLine _activeBeanLine = null;
	private  BeanLine _firstBeanLine = null;
	private  BeanLine _lastBeanLine = null;

	public BeanScreen(Element oScreenElement){
		super(oScreenElement);
	}
	public BeanScreen(Element oScreenElement, MultiContentManager oCMngr) {
		super(oScreenElement);
		
		List oList = _oScreenElement.getChildren("line");
					
		Element oChild = null;
		BeanLine oBeanLine = null;
		for(int i=0; i<oList.size(); i++){	
			oChild = (Element)oList.get(i);
			
			oBeanLine = new BeanLine(oChild, oCMngr.getInstanceContext());
			
			
			if(oBeanLine.hasEntryBean()){
				
				if(_firstBeanLine == null){
					_firstBeanLine = oBeanLine;
					_activeBeanLine = _firstBeanLine;
				}
				
				_lastBeanLine = oBeanLine;
				
			}
			_oLines.add(oBeanLine);
		}
		
		
	}

	public boolean isComplete(){
		
		if(_lastBeanLine == null || _lastBeanLine.isComplete())
			return true;
		else
			return false;
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

	public ScreenContent signal(BusMessage oMessage) {

		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		if(keyAction.equals(RELEASED))
			return this;
		
		if(FK_CLR.equals(keyID)){
			return null;
		} 
		
		if(FK_ENT.equals(keyID) && isComplete()){
			return getParent();
		}			
		
		if(MV_DOWN.equals(keyID) && _activeBeanLine != null){
			
			BeanLine oNext = getNieghborInputLine(_activeBeanLine, true);	
			
			if(oNext != null)
				_activeBeanLine = oNext;

			int tmp = getIndexOfLine(_activeBeanLine);
			
			_currLine = tmp - getHeaderCount();
			
			return this;
			
		}
		
		if(MV_UP.equals(keyID) && _activeBeanLine != null){
	
			BeanLine oPrev = getNieghborInputLine(_activeBeanLine, false);	
			
			if(oPrev != null)
				_activeBeanLine = oPrev;

			int tmp = getIndexOfLine(_activeBeanLine);
			
			_currLine = tmp - getHeaderCount();
			
			return this;

		}

		
		if(_activeBeanLine != null){
			
			int lineFocus = _activeBeanLine.signal(oMessage);
			
			if(lineFocus > 0){
				
				BeanLine oNext = getNieghborInputLine(_activeBeanLine, true);
					
				if(oNext != null)
					_activeBeanLine = oNext;
				
				
							
				
			} else if(lineFocus < 0){
				
				BeanLine oPrev = getNieghborInputLine(_activeBeanLine, false);
					
				if(oPrev != null)
					_activeBeanLine = oPrev;
				
			} 
			
			_currLine = getIndexOfLine(_activeBeanLine) -getHeaderCount();
			
		}
	
		
		return this;

	}
}

