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
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Element;




/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BeanSimpleScreen extends BeanBaseScreen implements Constants{

		
	public BeanSimpleScreen(Element oScreenElement, MultiContentManager oCMngr) throws Exception {
		super(oScreenElement, oCMngr);	
		//init();
	
	}
	public void init() {
		createLines();

	}
		   
	public void enter(Object arg0){
	   
	}
	//add case for different storages - object, list etc.
	//also add index processing.
	private Object getScreenData(Element elemData){

		if(elemData == null)
			return null;
		
		String storeName = elemData.getAttributeValue("name");
		
	    Object storeObj = null;	    
		if(storeName != null){
			java.util.List storeList = getInstanceContext().getContentManager().getStorageList(storeName);
			
			if(storeList != null && storeList.size()>0){
				storeObj = storeList.get(0);
			}
		}
		
		return storeObj;
	}
	
	private ScreenLineList createLines() {
		
		_oLines = new ScreenLineList();
				
		Object storeObj = getScreenData(_oScreenElement.getChild("data"));
		
		List oList = _oScreenElement.getChildren("line");
		Element oChild = null;
		for(int i=0; i<oList.size(); i++){	
			
			oChild = (Element)oList.get(i);
			oChild.getName();				
			if(oChild.getName().equals("line")){
				_oLines.add(new BeanLine(oChild, storeObj, getInstanceContext())); 					
			}else{
				throw new RuntimeException("BeanSimpleScreen.getLines - Page elements must be line or list.");
			}
			
		}
		
		return _oLines;		
	} 
	


	public ScreenInterface signal(BusMessage oMessage) {

		String msgType = oMessage.getType();
				
		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();
								
		if(keyAction.equals(PRESSED)){

			String event = this.getAttributeValue("event");
			
			if(event != null && keyID.equals(event)){
			
				String screenName = this.getAttributeValue("link");
													
				ScreenInterface oScreenNext = getInstanceContext().getContentManager().getScreenContent(screenName, getInstanceContext());
							
				oScreenNext.setParent(this);
	
				oScreenNext.setOutGoingDscMessage(getOutGoingDscMessage());
				oScreenNext.setIncomingDscMessage(getIncomingDscMessage());

				oScreenNext.setInstanceContext(getInstanceContext());
			
				return oScreenNext;
	 		}
	
		 }
		//default
		if(FK_CLR.equals(oMessage.getButtonEvent().getKeyId())
				&& keyAction.equals(PRESSED)) {
			return null;
		}
		
		return this;

	}
	public int getNextChoiceIdx(int start) {
		
	   	
		BeanLine oLine = null;
		
		ArrayList oScreenLines = getLines();
		int headers = getHeaderCount();
		
		for(int i=headers + start +1; i < oScreenLines.size(); i++){
			oLine = (BeanLine)oScreenLines.get(i);
			if(oLine.isChoice()){
				return i - headers;
			}
			
		}
		
		return -1;
 	
	}
	public int getPrevChoiceIdx(int start) {
		
		BeanLine oLine = null;
		
		ArrayList oScreenLines = getLines();
		int headers = getHeaderCount();
		for(int i=headers+start-1; i > -1; i--){
			oLine = (BeanLine)oScreenLines.get(i);
			if(oLine.isChoice()){
				return i - headers;
			}
			
		}
		
		return -1; 	
	}
	
}

