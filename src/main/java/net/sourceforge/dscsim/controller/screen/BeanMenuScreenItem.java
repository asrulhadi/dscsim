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
public class BeanMenuScreenItem extends BeanBaseScreen implements Constants {

	private  BeanLine _activeBeanLine = null;
	private  BeanLine _firstBeanLine = null;
	private  BeanLine _lastBeanLine = null;
	private Object _oData = null;

public BeanMenuScreenItem(Element oScreenElement, MultiContentManager oCMngr)
throws Exception {
	super(oScreenElement);
	
	List oList = _oScreenElement.getChildren("line");
	
	//see if any data stored in session context
	String strStorage = oScreenElement.getAttributeValue("storage");
	String strMode = oScreenElement.getAttributeValue("mode");
	
	if("add".equals(strMode)){
		Element oStorage = oCMngr.getStorageElement(strStorage);
		String strClass = oStorage.getAttributeValue("type");
		Class clazz = Class.forName(strClass);
		_oData = clazz.newInstance();
	} else {
		_oData = oCMngr.getInstanceContext().getProperty(strStorage);		
	}
	
	Element oChild = null;
	BeanLine oBeanLine = null;
	for(int i=0; i<oList.size(); i++){	
		oChild = (Element)oList.get(i);
		
		oBeanLine = new BeanLine(oChild, _oData, getInstanceContext());
		
		if(oBeanLine.hasEntryBean()){
			
			if(_firstBeanLine == null){
				_firstBeanLine = oBeanLine;
				_activeBeanLine = _firstBeanLine;
			}
			
			_lastBeanLine = oBeanLine;
			
		}
		_oLines.add(oBeanLine);
	}
	
	init();
	
	int firstLine = getHeaderCount();
	if(_firstBeanLine  != null)
		firstLine = getIndexOfLine(_firstBeanLine) - firstLine;
	
	_currLine = firstLine;
}

public boolean isComplete(){
	
	if(_lastBeanLine == null || _lastBeanLine.isComplete())
		return true;
	else
		return false;
}

public ScreenInterface signal(BusMessage oMessage) {

		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		if(keyAction.equals(RELEASED))
			return this;
		
		if(FK_CLR.equals(keyID)){
			return null;
		} 
		
		if(FK_ENT.equals(keyID)){
			
			String strMode = _oScreenElement.getAttributeValue("mode");
			
			if("add".equals(strMode)){
				
				if(isComplete()) {
					MultiContentManager oMCmgr = getInstanceContext().getContentManager();
					
					Element oStorage = oMCmgr.getStorageElement(getAttributeValue("storage"));
					BeanList oBeanList = oMCmgr.getBeanList(oStorage);
					
					oBeanList.addItem(_oData);
					
					oMCmgr.storeBeanList(oBeanList);
					
					return getParent();
				}
				
				return this;
				

			} else if("delete".equals(strMode)){
				
				MultiContentManager oMCmgr = getInstanceContext().getContentManager();
				
				Element oStorage = oMCmgr.getStorageElement(getAttributeValue("storage"));
				BeanList oBeanList = oMCmgr.getBeanList(oStorage);
				
				oBeanList.removeItem(_oData);
				
				oMCmgr.storeBeanList(oBeanList);
				
				ScreenContent oParent = getParent();
								
				//call init as a line has been deleted and _currLine and 
				//the rest state variables have to be reset.
				oParent.init();
				
				return oParent;
				
			}
			
			//default is view
			return this;
		}			
		
		if(MV_DOWN.equals(keyID) 
			&& _activeBeanLine != null
			&& "add".equals(_oScreenElement.getAttributeValue("mode"))){
			
			BeanLine oNext = getNieghborInputLine(_activeBeanLine, true);	
			
			if(oNext != null)
				_activeBeanLine = oNext;

			int tmp = getIndexOfLine(_activeBeanLine);
			
			_currLine = tmp - getHeaderCount();
			
			return this;
			
		}
		
		if(MV_UP.equals(keyID) 
			&& _activeBeanLine != null
			&& "add".equals(_oScreenElement.getAttributeValue("mode"))){
	
			BeanLine oPrev = getNieghborInputLine(_activeBeanLine, false);	
			
			if(oPrev != null)
				_activeBeanLine = oPrev;

			int tmp = getIndexOfLine(_activeBeanLine);
			
			_currLine = tmp - getHeaderCount();
			
			return this;

		}

		
		if(_activeBeanLine != null
			&& "add".equals(_oScreenElement.getAttributeValue("mode"))){
			
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

