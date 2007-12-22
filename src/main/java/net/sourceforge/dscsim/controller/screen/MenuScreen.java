/*
 * Created on 24.06.2006
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

import java.lang.reflect.Field;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Element;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MenuScreen extends ScreenContent  implements Constants{

	public MenuScreen(Element oScreenElement){
		super(oScreenElement);
	}
	
    public void init() {
    	_minLine = getFirstChoiceIdx();
    	_currLine = _minLine;
    	_maxLine = getLastChoiceIdx();   	
    }
    
    public int getPrevChoiceIdx(int start) {
      	return getPrevChoiceIdx(start, "choice");  	
	}
	
	public int getNextChoiceIdx(int start) {
	   	return getNextChoiceIdx(start, "choice");
	}
	public int getLastChoiceIdx(){
		return getLastChoiceIdx("choice");
	}
	
	public int getFirstChoiceIdx(){
		return getFirstChoiceIdx("choice");
	}
	
	public MenuLine getActiveChoice(){
		
		if(_currLine > -1 && _currLine < _oLines.size()){
			return (MenuLine)_oLines.get(_currLine);
		}
		
		return null;
	}
	
	public String getSelectedValue(String name){		
		
		String def="";
		
		MenuLine oMenuLine  = getActiveChoice();
		
		if(oMenuLine == null)
			return def;
		
		String strCatCode = oMenuLine.getAttributeValue("code");
		
		if(strCatCode == null || strCatCode.length() == 0)
			return oMenuLine.getDisplayText();
		
		
		try {
			Class oClass = Class.forName("applet.Constants");
			
			Field oFld = oClass.getField(strCatCode);
			
			def = oFld.get(this).toString();			
			
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
	
		return def;	
	}

	public ScreenInterface signal(BusMessage oMessage) {
		
		//only up, down, enter, sos, reset
		//all other ignored
		
			String keyID = oMessage.getButtonEvent().getKeyId();
			String keyAction = oMessage.getButtonEvent().getAction();
			
			//AppLogger.debug("MenuScreen.signal - keyId" + oMessage.toString());
			
			if(keyAction.equals(PRESSED)){

				if(MV_DOWN.equals(keyID)){
					if(getAttributeValue("scroll").equals("false")==false)
						_currLine = _currLine < _maxLine ? _currLine+1 : _currLine;
				}else if(MV_UP.equals(keyID)){
					if(getAttributeValue("scroll").equals("false")==false)
						_currLine = _currLine > 1 ? _currLine-1 : _currLine;				
				}else {
									
					MenuLine oLine = (MenuLine)_oLines.get(_currLine);	
					
					
					//check to see if there is a specified event.
					//when no, then use the default
					String event = oLine.getAttributeValue("event");
					//AppLogger.debug("event=" + event + " for keyID=" + keyID);
					
					if(event.equals(keyID) || (event.length()==0 && FK_ENT.equals(keyID))) {
						
						ScreenInterface oScreen = (ScreenInterface)getInstanceContext().getContentManager().getScreenContent(oLine.getLink(), getInstanceContext());
						
						//oScreen.enter(_enterArg0);
						
						return oScreen;					
						
					}else if(FK_CLR.equals(keyID)){
													
						return null;
					}
				
				}

								
			}

			//AppLogger.debug("currLine=" + String.valueOf(_currLine) + " for keyID=" + keyID);
			
			return this;
	

		
	}

}

