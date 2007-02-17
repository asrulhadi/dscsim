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
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Element;



/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BeanMenuScreen extends BeanBaseScreen implements Constants{

	private BeanList _oBeanList = null;
		
	public BeanMenuScreen(Element oScreenElement, MultiContentManager oCMngr) throws Exception {
		super(oScreenElement, oCMngr);	
		init();

	}
	public void init() {
		super.init();
	}
		   
	public void enter(Object arg0){
		
		//init();
		//getLines();
				   
	}
	
	public ScreenLineList getLines() {
		
		_oLines = new ScreenLineList();
				
		List oList = _oScreenElement.getChildren();
		
		Element oChild = null;
		for(int i=0; i<oList.size(); i++){	
			
			oChild = (Element)oList.get(i);
			oChild.getName();				
			if(oChild.getName().equals("line")){
				BeanLine oBeanLine = new BeanLine(oChild, this.getOutGoingDscMessage(), getInstanceContext());				
				if(oBeanLine.isVisible())
					_oLines.add(oBeanLine); 					
			}else if(oChild.getName().equals("list")){
				addListLines(oChild);
			}else if(oChild.getName().equals("context")){
				//ok
			}else if(oChild.getName().equals("actions")){
				//ok
			}else{
				throw new RuntimeException("BeanListScreen.getLines - Unexpected Screen Element.");
			}
			
		}
		
		return _oLines;		
	} 
	
	private void addListLines(Element oListElement) {

	    Element oStorage = _oContentManager.getStorageElement(oListElement.getAttributeValue("storage"));	
	    
		if(oStorage == null)
			throw new RuntimeException("BeanList.createBeanScreen - Bean Screen must have a storage.");
		
		//get instance of list - cached or new.
		_oBeanList = _oContentManager.getBeanList(oStorage);
		
		Object oObj = null;
		List oLineElements = null;
		
		int lsize = _oBeanList.getList().size();
		int max = lsize;
		if(oListElement.getAttribute("count") != null){
			try{										
				String strCount = oListElement.getAttributeValue("count");
				max = Integer.parseInt(strCount);
			}catch(Exception  oEx){
				max = lsize;
				AppLogger.error(oEx);
			}
		
		}
		
		String strFilter = null;
		if(oListElement.getAttribute("filter") != null){
			strFilter = oListElement.getAttributeValue("filter");
		}
		
		int tcount = 0;
		for(int i=0; tcount<max && i<lsize && i<MAX_LIST_LEN;i++){
			oObj = _oBeanList.getList().get(i);
			
			if((strFilter != null) && (oObj instanceof DscMessage)){
				DscMessage oMsg = (DscMessage)oObj;
				
				if(strFilter.equals(oMsg.getCallType())==false){
					continue;
				} else{
					tcount++;
				}
				
			} else {
				tcount++;
			}

			oLineElements = oListElement.getChildren("line");
			
			Element oChild = null;
			String attrName = null;
			int line_num = -1;
			for(int k=0; k<oLineElements.size();k++){
				oChild = (Element)oLineElements.get(k);
								
				if(oListElement.getAttribute("numbered")!= null)
					line_num = i;
				
				_oLines.add(new BeanLine(line_num+1, oChild, oObj, getInstanceContext()));
				
			}
			
			
		}
		
		
	}

	public ScreenContent signal(BusMessage oMessage) {

		String typeId = oMessage.getType();
		
		if(!BusMessage.MSGTYPE_KEY.equals(typeId))
			return this;
		
		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();
		
		if(FK_CLR.equals(oMessage.getButtonEvent().getKeyId())
				&& keyAction.equals(PRESSED)) {
			return null;
		}
				
		//AppLogger.debug("DataListScreen.signal - keyId" + oMessage.toString());
		
		 if(keyAction.equals(PRESSED)){

			if(MV_DOWN.equals(keyID)){
				int tmp = getNextChoiceIdx(_currLine);
				_currLine = tmp > -1 ? tmp : _currLine;
			}else if(MV_UP.equals(keyID)){
				int tmp = getPrevChoiceIdx(_currLine);
				_currLine = tmp > -1 ? tmp : _currLine;
			} else if(KP_BS.equals(keyID)){				
				return this;
			} else if(FK_ENT.equals(keyID)){
				
				int headers = getHeaderCount();
				BeanLine oCurrLine = (BeanLine)_oLines.get(_currLine + headers);	
				
				Object oData = oCurrLine.getData();
				
				String screenName = oCurrLine.getAttributeValue("link");
				
				getInstanceContext().setProperty(oCurrLine.getAttributeValue("storage"), oData);				
									
				ScreenContent oScreenNext = getInstanceContext().getContentManager().getScreenContent(screenName, getInstanceContext());
							
				oScreenNext.setParent(this);
				oScreenNext.setOutGoingDscMessage(getOutGoingDscMessage());
				oScreenNext.setIncomingDscMessage(getIncomingDscMessage());
				oScreenNext.setInstanceContext(getInstanceContext());
				
				return oScreenNext;
	
			} else {
			 
				BeanLine oLine = (BeanLine)_oLines.get(_currLine);	
				
				
				//check to see if there is a specified event.
				//when no, then use the default
				String event = oLine.getAttributeValue("event");
				//AppLogger.debug("event=" + event + " for keyID=" + keyID);
				
				if(event.equals(keyID) || (event.length()==0 && event.equals(keyID))) {
					
					ScreenContent oScreen = getInstanceContext().getContentManager().getScreenContent(oLine.getAttributeValue("link"), getInstanceContext());
					
					//oScreen.enter(_enterArg0);
					
					return oScreen;		
				}
			}
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

