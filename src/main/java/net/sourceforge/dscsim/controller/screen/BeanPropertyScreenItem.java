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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.dscsim.controller.BusMessage;
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
public class BeanPropertyScreenItem extends BeanScreen implements Constants {

	private  BeanLine _activeBeanLine = null;
	private  BeanLine _firstBeanLine = null;
	private  BeanLine _lastBeanLine = null;
	private MultiContentManager _oContentManager = null;
	
	public BeanPropertyScreenItem(Element oScreenElement, MultiContentManager oMngr) throws Exception  {
		super(oScreenElement);
		_oContentManager = oMngr;
		
		setInstanceContext(oMngr.getInstanceContext());
	
		parseLines();
		
		init();
		
		int firstLine = getHeaderCount();
		if(_firstBeanLine  != null)
			firstLine = getIndexOfLine(_firstBeanLine) - firstLine;
		
		_currLine = firstLine;
	}
	
	private void parseLines() throws Exception {

		_oLines.clear();
		
		List oList = _oScreenElement.getChildren("line");
		Object oData = null;

		Element oChild = null;
		BeanLine oBeanLine = null;
		for(int i=0; i<oList.size(); i++){	
			oChild = (Element)oList.get(i);
						
			oBeanLine = new BeanLine(oChild, getInstanceContext());
			
			if(oBeanLine.hasEntryBean()){
				
				if(_firstBeanLine == null){
					_firstBeanLine = oBeanLine;
					_activeBeanLine = _firstBeanLine;
				}
				
				_lastBeanLine = oBeanLine;
				//set last incompleted line to active line
				if(_lastBeanLine != null 
						&& _lastBeanLine.isComplete() == true){
					_activeBeanLine = _lastBeanLine;
				}
				
			}
			_oLines.add(oBeanLine);
		}
		
	}
	
	public net.sourceforge.dscsim.controller.screen.ScreenLineList getLines(){
	
		return super.getLines();
	}
	
	private void storePageProperties() {
		

		BeanLine oLine = null;
			
		MultiContentManager oCMngr = getInstanceContext().getContentManager();
			
		for(int i=0; i<_oLines.size();i++){				
			oLine = (BeanLine)_oLines.get(i);
			
			List oBeanList = oLine.getBeans();
			
			ActiveField oField = null;
			for(int j=0; j<oBeanList.size(); j++){
				
				oField = (ActiveField)oBeanList.get(j);
				String prop = oField.getElement().getAttributeValue("property");
				
				if(prop != null){
					try{
						oCMngr.setSetting(prop, oField);	
					}catch(Exception oEx){
						AppLogger.error(oEx);
					}
												
				}
			
			}
			
			
		}
		
		_oContentManager.saveProperties();
		
		
	}
	public void exit(BusMessage oMessage) throws Exception {
		super.exit(oMessage);
		
		String keyID = oMessage.getButtonEvent().getKeyId();

		//anything but enter will cause the changes to be lost;
		if(FK_ENT.equals(keyID) == true){
									
			MultiContentManager oCMngr = getInstanceContext().getContentManager();

			oCMngr.reloadProperties();
		}
		
	}
	
	public boolean isComplete(){
		
		BeanLine oLine =null;
		for(int i=0; i <_oLines.size(); i++){
			
			oLine = (BeanLine)_oLines.get(i);
			
			if(oLine.isComplete()==false)
				return false;
			
		}
		
		/*
		if(_lastBeanLine == null || _lastBeanLine.isComplete())
			return true;
		else
			return false;
		*/
		return true;
	}
	
	public int getActiveLine(){
		return getLines().indexOf(_activeBeanLine);	
	}
	

	public ScreenContent signal(BusMessage oMessage) {

		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		if(keyAction.equals(RELEASED))
			return this;
		
		if(FK_CLR.equals(keyID)){
			return null;
		} 
		
		if(FK_ENT.equals(keyID)){
						
			if(isComplete()){
				MultiContentManager oMCmgr = getInstanceContext().getContentManager();

				storePageProperties();
				
				return oMCmgr.getScreenContent(this.getAttributeValue("next"), getInstanceContext());

			} 
		}
		
		if(MV_LEFT.equals(keyID) 
				&& _activeBeanLine != null){

			BeanLine oPrev = getNieghborInputLine(_activeBeanLine, false);

			if(oPrev != null)
				_activeBeanLine = oPrev;
			
			return this;
			
		} else 	if(MV_RIGHT.equals(keyID) 
				&& _activeBeanLine != null){
			
			BeanLine oNext = getNieghborInputLine(_activeBeanLine, true);
			
			if(oNext != null)
				_activeBeanLine = oNext;							
			
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
