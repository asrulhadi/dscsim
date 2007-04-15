/*
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

import net.sourceforge.dscsim.controller.*;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.network.MulticastTransmitter;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.utils.InputLine;

import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.filter.Filter;
import org.jdom.filter.ElementFilter;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;




class DataListField extends ScreenField {

	private Object _oValue = null;
	DataListField(Element oElement, Object oValue){
		super(oElement);
		_oValue = oValue;
	}

	public String getDisplayText() {
		// TODO Auto-generated method stub
		return _oValue.toString();
	}
	
}

class DataListItemLine extends ScreenLine {

	private static int _cnt =0;
	private int _id = _cnt++;
	
	private Object _oItem;
	public DataListItemLine(Element oInputLine, Object oItem) {
		super(oInputLine);
		_oItem = oItem;
	}


	public String getDisplayText() {
		// TODO Auto-generated method stub
		
		String strLine = "";
		
		String attrType = _oInputLine.getAttributeValue("type");
		
		AppLogger.debug("DataListItemLine.getDisplayText " + attrType);

		//simple line.
		if(attrType.equals("text")){
			strLine = _oInputLine.getValue();
		}else{
			strLine += concatLine(_oInputLine);
		}

		AppLogger.debug("DataListItemLine.getDisplayText returning id" + _id + " = "+ strLine);

		return strLine;
	}
	
	private String concatLine(Element oParent){

		AppLogger.debug("DataListItemLine.concatLine ");

		List oFields = oParent.getChildren();
		
		String strLine = "";		
		String strType = "";
		Element oElem = null;
		Object oObj = null;
		for(int i=0; i<oFields.size();i++){
			oObj = oFields.get(i);
			
			if((oObj instanceof Element)==false)
				continue;			
			oElem = (Element)oObj;
			strType = oElem.getAttributeValue("type");
			if(strType.equals("text")){
				strLine += oElem.getValue();
			}else{
				strLine += callGet(oElem);
			}
			
		}
	
		return strLine;
	}
	
	private String callGet(Element oField){
		String strValue = "";
		
		
		try {
			String strClass = oField.getAttributeValue("class");
			String strMethod = oField.getAttributeValue("get");
			
            Class c = Class.forName(strClass);
            Class paramTypes[] = {};
            
            Method m = c.getMethod(strMethod, paramTypes);
            
            Object parmValues[] = {};
    
            strValue = m.invoke(_oItem, parmValues).toString();
            
			
		} catch (Exception oEx){
			AppLogger.error(oEx);
		}
				
		
		return strValue;
		
	}
		
}
class DataListLine extends ScreenLine {

	private ArrayList _oFields = new ArrayList();
	private Object _oData = null;
	
	
	
	public DataListLine(Element oSource, Object oData){
		super(oSource);
		_oData = oData;
	}
	
	public DataListLine(Element oSource){
		super(oSource);
	}
	public String getDisplayText() {
		// TODO Auto-generated method stub
				
		String strLine = "";
		
		for(int i=0; i<_oFields.size(); i++){
			strLine += ((DataListField)_oFields.get(i)).getDisplayText();
			strLine += "  ";
		}
		return strLine;
	}
	
	public Object getData(){
		return _oData;
	}
	
	public List getFields(){
		return _oFields;	
	}

}

class MenuLine extends ScreenLine {
	
	private String _strDisplayText = "N/A";
	private String _strLink = "none";
	
	public MenuLine(Element oLine) throws Exception {
		super(oLine);
		
	  	Attribute oAttr = oLine.getAttribute("link");
	  	
	  	if(oAttr == null || oAttr.getValue() == null )
	  		throw new Exception("line type choice must have link");
	  	
		_strDisplayText = oLine.getValue();
		_strLink = oAttr.getValue();
		
	}
	public String getDisplayText() {
		
		List oList = _oInputLine.getChildren("field");
		
			
			if(oList.size() == 0)
				return _oInputLine.getValue();
			
			Element oElement =null;
			String subFields   = "";
			for(int i=0; i<oList.size(); i++){
				
				oElement =(Element)oList.get(i);
				
				subFields += oElement.getValue();
				
				
			} 
			
			return  subFields;
		//return _strDisplayText;
	}
	public String getLink() {
		return _strLink;
	}
	
}

class InfoLine extends ScreenLine {
		
	public InfoLine(Element oLine){
		super(oLine);
		
	}
	public String getDisplayText() {
		
		List oList = _oInputLine.getChildren("field");
	
		
		if(oList.size() == 0)
			return _oInputLine.getValue();
		
		Element oElement =null;
		String subFields   = "";
		for(int i=0; i<oList.size(); i++){
			
			oElement =(Element)oList.get(i);
			
			subFields += oElement.getValue();
			
			
		} 
		
		return  subFields;
	}
	
}

class TextField extends ScreenField  {
	private String _strText = "";
	public TextField(String strText, int xpos){
		_strText = strText;
	}
	
	public String getDisplayText(){
		return _strText;
	}
	
}

class EntryField extends ScreenField implements Constants {
	
	private String _strName = "";
	private String _strFormat = "";
	private String _strDefault = "";
	private String _strActualValue = "";
	private String _strActualValueDisplay = "";
	
	public EntryField(Element oField){
		super(oField);
		
		Attribute oInputAttr = oField.getAttribute("name");
		_strName = oInputAttr == null ? "" : oInputAttr.getValue();

		oInputAttr = oField.getAttribute("format");
		_strFormat = oInputAttr == null ? "" : oInputAttr.getValue();

		if(_strFormat != null)
			_strActualValueDisplay = INPUT_PATTERN.substring(0, _strFormat.length());

		_strDefault = oField.getValue();

	}
	
	public int signal(BusMessage oMessage) {
				
		if(_oKeySet.contains(oMessage.getButtonEvent().getKeyId())){
			
			if(_strActualValue.length() < _strFormat.length()){
				_strActualValue +=  DscUtils.getKeyStringValue(oMessage.getButtonEvent().getKeyId());
				
				_strActualValueDisplay = _strActualValue + INPUT_PATTERN.substring(_strActualValue.length(), _strFormat.length());

			}
			
		}else if(MV_LEFT.equals(oMessage.getButtonEvent().getKeyId())
				|| KP_BS.equals(oMessage.getButtonEvent().getKeyId())){
			
			int len = _strActualValue.length();
			if(len > 0){
				_strActualValue = _strActualValue.substring(0, len-1);
				_strActualValueDisplay = _strActualValue + INPUT_PATTERN.substring(_strActualValue.length(), _strFormat.length());
		
			}
		}
		 
		
		int focus = 0;
		if(_strFormat.length() == _strActualValue.length())
			focus = 1;
		else if(_strActualValue.length()==0)
			focus = -1;
		else
			focus = 0;
				
		return focus;
	}
	
	public boolean isComplete(){
	
		if(_strActualValue.length() == _strFormat.length())
			return true;
		else
			return false;
	}
	
	public String getDisplayText(){
		return _strActualValueDisplay;
	}
	
	public void setDisplayText(String value){
		
		_strActualValue = value;
		
		_strActualValueDisplay = _strActualValue + INPUT_PATTERN.substring(_strActualValue.length(), _strFormat.length());

		
	}
	
	public String getFieldName(){
		
		return _oSrcElement.getAttributeValue("name");
	}
	
}

abstract class ScreenField {
	
	protected Element _oSrcElement = null;
	
	public ScreenField(){
		
	}	
	public ScreenField(Element oSrcElement){
		_oSrcElement = oSrcElement;
	}
	public Element getObjectsRootElement(){
		return _oSrcElement;
	}
	public abstract String getDisplayText();
	
}
class EntryLine extends ScreenLine {
		
	private ArrayList _arrFields = new ArrayList();
	
	private EntryField _activeEntryField = null;
	private EntryField _firstEntryField = null;
	private EntryField _lastEntryField = null;
	
	
	public EntryLine(Element oEntryLine){
		super(oEntryLine);
		
	}
	
	public void init(){
		_firstEntryField = getFirstEntryField();
		_lastEntryField = getLastEntryField();
		
		_activeEntryField = _firstEntryField;
		
	}
	
	public void addScreenField(ScreenField oScreenField){
		_arrFields.add(oScreenField);
	}

	public int signal(BusMessage oMessage) {
		//-1 give previous line focus
		// 0 this line keeps focus
		// 1 give next line focus
		
		int lineFocus = 0;
		int fieldFocus = 0;
		if(_activeEntryField != null){
			
			fieldFocus = _activeEntryField.signal(oMessage);
			
			if(fieldFocus > 0){
				
				EntryField oNext = getNieghborInputField(_activeEntryField, true);
				
				if(oNext == null){
					lineFocus = 1;					
				}else{
					lineFocus = 0;
					_activeEntryField = oNext;
				}
				
			} else if(fieldFocus < 0){
				
				EntryField oPrev = getNieghborInputField(_activeEntryField, false);
				
				if(oPrev == null){
					lineFocus = -1;
				}else{
					lineFocus = 0;
					_activeEntryField = oPrev;
				}
				
				
			} else {
				lineFocus = 0;
			}
				
			
		}
		
		return lineFocus;
		
	}

	public EntryField getNieghborInputField(EntryField oTarget, boolean next) {
					
		EntryField  oNiehborEntryField = null;
		Object oField = null;
		if(next == false){			
			for(int i = 0; i < _arrFields.size(); i++) {
				oField = _arrFields.get(i);
				
				if(oTarget == oField)
					return oNiehborEntryField;
				
				if(oField instanceof EntryField){
					oNiehborEntryField = (EntryField)oField;
					
				}					
			}						
		} else {
			
			for(int i = _arrFields.size()-1; i > -1; i--) {
				oField = _arrFields.get(i);
				
				if(oTarget == oField)
					return oNiehborEntryField;
				
				if(oField instanceof EntryField){
					oNiehborEntryField = (EntryField)oField;
					
				}					
			}			
			
		}
			
		return oNiehborEntryField;
	}
	
	public int getIndexOfNeighborOfEntryField(EntryField oTarget, boolean next){
		
		EntryField oReturn = getNieghborInputField(oTarget, next);
		
		if(oReturn == null){
			return -1;
		}else{
			return getIndexOfField(oReturn);
		}
		
	}
	
	public EntryField getFirstEntryField(){
		
		Object oObject = null;
		for(int i=0; i<_arrFields.size(); i++){
			oObject = _arrFields.get(i);
			
			if(oObject instanceof EntryField)
				return (EntryField)_arrFields.get(i);
			
		}
		
		return null;
		
	}
	
	public EntryField getLastEntryField(){
		
		Object oObject = null;
		for(int i=_arrFields.size()-1; i > -1; i--){
			oObject = _arrFields.get(i);
			
			if(oObject instanceof EntryField)
				return (EntryField)_arrFields.get(i);
			
		}
		
		return null;
		
	}
	
	public int getIndexOfField(EntryField oTarget) {
		
		Object oField = null;
		for(int i=0; i<_arrFields.size(); i++) {
			oField = _arrFields.get(i);
			
			if(oField == oTarget)
				return i;
		}
					
		return -1;
	}
	public int getNumberOfInputFields() {
		
		int inputFieldCount = 0;
		
		ScreenField oLine = null;
		for(int i=0; i<_arrFields.size(); i++) {
			oLine = (ScreenField)_arrFields.get(i);
			
			if(oLine instanceof EntryField)
				inputFieldCount++;
		}
		
	
		
		return inputFieldCount;
		
	}
	
	public boolean isComplete(){
		
		if(_lastEntryField == null || _lastEntryField.isComplete())
			return true;
		else
			return false;
	}
	
	public void setField(String fldName, String strValue) {
		
		EntryField oFld =getEntryField(fldName);
		
		if(oFld != null)
			oFld.setDisplayText(strValue);
	
	}
	
	public String getDisplayText() {
		
		String strLine = "";
		
		ScreenField oField = null;
		for(int i=0; i<_arrFields.size(); i++) {
			oField = (ScreenField)_arrFields.get(i);
			strLine += oField.getDisplayText();
		}
		
		return strLine;
	}
	
	public EntryField getEntryField(String strName){

		
		ScreenField oField = null;
		EntryField oEntryField = null;
		Element oElement = null;
		for(int i=0; i<_arrFields.size(); i++) {
			oField = (ScreenField)_arrFields.get(i);
			
			if(oField instanceof EntryField)
				oEntryField = (EntryField)oField;
			else
				continue;
			
			oElement = oField.getObjectsRootElement();
			
			Attribute oAttr = oElement != null ? 
					oElement.getAttribute("name") :
						null;
			String strFldName = oAttr != null ?
					oAttr.getValue() : "";
			
			if(strFldName.equals(strName)){
				return oEntryField;
			}
			
		}
		
		return null;
		
	}
	
}


class DataListItemScreen extends ScreenContent implements Constants{


	public DataListItemScreen(Element oScreenElement) {
		super(oScreenElement);

	}
	
	
	public ScreenLineList getLines() {
		
		_oLines = new ScreenLineList();
		
		List oLineList = _oScreenElement.getChildren();
		AppLogger.debug("DataListItemScreen lines="+ oLineList.size());
		
		//context info store under name bye DataListScreen.
		String strName = _oScreenElement.getAttributeValue("name");
		
		init();
		setCursonOn(false);
		
		Element oLine = null;
		String attrType = null;
		_currLine = 0;
		for(int i=0; i<oLineList.size();i++){
			
			oLine = (Element)oLineList.get(i);
			
			attrType = oLine.getAttributeValue("subtype");
			
			if(attrType != null  && attrType.equals("header"))
				_currLine++;
			
			_oLines.add(new DataListItemLine(oLine, getInstanceContext().getProperty(strName)));
			
			AppLogger.debug("DataListItemScreen number="+ i);
					
		}
		
		AppLogger.debug("DataListItemScreen _oLines count ="+ _oLines.size());
		
		
		return _oLines;		
	}


	public ScreenInterface signal(BusMessage oMessage) {
	
		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();
		
		if(PRESSED.equals(keyAction)==false)
			return this;

		if(FK_CLR.equals(oMessage.getButtonEvent().getKeyId())) {
			return null;
		}
		
		
		
		if(MV_LEFT.equals(oMessage.getButtonEvent().getKeyId())){
			return getParent();
		}
				
	
		
		return this;
	}
	
	
}

class DataListScreen extends ScreenContent implements Constants {

	public DataListScreen(Element oSource){
		super(oSource);
		
	}
	public ScreenInterface signal(BusMessage oMessage) {
		// TODO Auto-generated method stub
		
		if(FK_CLR.equals(oMessage.getButtonEvent().getKeyId())) {
			return null;
		}
		
		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();
		
		AppLogger.debug("DataListScreen.signal - keyId" + oMessage.toString());
		
		 if(keyAction.equals(PRESSED)){

			if(MV_DOWN.equals(keyID)){
				int tmp = getNextChoiceIdx(_currLine);
				_currLine = tmp > -1 ? tmp : _currLine;
			}else if(MV_UP.equals(keyID)){
				int tmp = getPrevChoiceIdx(_currLine);
				_currLine = tmp > -1 ? tmp : _currLine;
			} else if(KP_BS.equals(keyID)){
				
				DataListLine oCurrLine = (DataListLine)_oLines.get(_currLine);	
				
				Object oData = oCurrLine.getData();

				String strName = _oScreenElement.getAttributeValue("storage");
				
				getInstanceContext().getContentManager().getPersistantList(strName).remove(oData);
				
				refresh();
				init();	
				return this;
				
			} if(FK_ENT.equals(keyID)){
				
				DataListLine oCurrLine = (DataListLine)_oLines.get(_currLine);	
				
				Object oData = oCurrLine.getData();
				
				//is empty screen
				if(oData == null)
					return this;
				
				String screenName = _oScreenElement.getAttributeValue("link");
				
				ScreenInterface oScreenNext = getInstanceContext().getContentManager().getScreenContent(screenName, getInstanceContext());
				
				//getInstanceContext().setProperty(screenName, oData);
				
				oScreenNext.setParent(this);
							
				return oScreenNext;
			}
		 	
		 }

		
		return this;
	}

	public void init() {
		// TODO Auto-generated method stub
		
		super.init();
		_currLine  = getFirstChoiceIdx();
		_minLine = _currLine;
		_maxLine = getLastChoiceIdx(); 
		
	}
	
	public int getFirstChoiceIdx() {
      	
		ScreenLine oLine = null;
		for(int i =0; i<_oLines.size(); i++){
			
			oLine = (ScreenLine)_oLines.get(i);
			if(oLine instanceof DataListLine){
				return i;
			}
			
		}
		
		return -1;
      	
      	
	}
	public int getLastChoiceIdx() {
		
		ScreenLine oLine = null;
		for(int i=_oLines.size()-1; i>-1; i--){
			oLine = (ScreenLine)_oLines.get(i);
			if(oLine instanceof DataListLine){
				return i;
			}
			
		}
		
		return -1;	
	}
	
	public int getPrevChoiceIdx(int start) {
		
		ScreenLine oLine = null;
		for(int i=start-1; i > -1; i--){
			oLine = (ScreenLine)_oLines.get(i);
			if(oLine instanceof DataListLine){
				return i;
			}
			
		}
		
		return -1; 	
	}
	
	public int getNextChoiceIdx(int start) {
		
	   	
		ScreenLine oLine = null;
		for(int i=start+1; i < _oLines.size(); i++){
			oLine = (ScreenLine)_oLines.get(i);
			if(oLine instanceof DataListLine){
				return i;
			}
			
		}
		
		return -1;
 	
	}
	
	
	
}

class SendActionScreen extends SendBaseScreen {


	public SendActionScreen(Element oScreenElement) {
		super(oScreenElement);
	}
	

	public ScreenInterface signal(BusMessage oMessage) {
		
		if(oMessage.getButtonEvent().getAction().equals(RELEASED))
			return this;
		
		if(FK_CLR.equals(oMessage.getButtonEvent().getKeyId())) {
			return null;
		} 
		
		if(FK_ENT.equals(oMessage.getButtonEvent().getKeyId())){
			sendDcsMessageDyn();
		}
		
		
		
		return null;
	}

	public void init() {

		_currLine = getFirstChoiceIdx("choice");
		

	}
	
	
}
	
class EntryScreen extends ScreenContent implements Constants {

	private EntryLine _activeEntryLine = null;
	private EntryLine _firstEntryLine = null;
	private EntryLine _lastEntryLine = null;
	
	public EntryScreen(Element oScreenElement){
		super(oScreenElement);
				
	}
	
	public void init(){
		_firstEntryLine = getFirstEntryLine();
		_lastEntryLine = getLastEntryLine();
		
		_activeEntryLine = _firstEntryLine;
		
		_currLine = this.getFirstChoiceIdx();
						
	}
	
	public String getSelectedValue(String name){
		return getEntryFieldValue(name);
	}

	public String getEntryFieldValue(String strName){
		String strValue = "";
		
		EntryField oEntryField = this.getEntryField(strName);
		
		if(oEntryField != null)
			return oEntryField.getDisplayText();
		else
			return strValue;
		
	}
	public EntryField getEntryField(String strName){
		
		EntryLine oEntryLine = null;
		ScreenLine oScreen = null;
		EntryField oEntryField = null;
		for(int i=0; i<_oLines.size(); i++){
			oScreen = (ScreenLine)_oLines.get(i);
			
			if(oScreen instanceof EntryLine){
				oEntryLine = (EntryLine)oScreen;
			}else{
				continue;
			}
				
			oEntryField = oEntryLine.getEntryField(strName);
			
			if(oEntryField != null)
				return oEntryField;
			
		}
						
		return null;
	}
	public boolean isComplete(){
		
		if(_lastEntryLine == null || _lastEntryLine.isComplete())
			return true;
		else
			return false;
	}
	
    public int getPrevChoiceIdx(int start) {
      	return getPrevChoiceIdx(start, "input");  	
	}
	
	public int getNextChoiceIdx(int start) {
	   	return getNextChoiceIdx(start, "input");
	}
	public int getLastChoiceIdx(){
		return getLastChoiceIdx("input");
	}
	
	public int getFirstChoiceIdx(){
		return getFirstChoiceIdx("input");
	}

	public String getNextScreen() {
		
		String strNextScreen = "N/A";
		
		Attribute oAttr = _oScreenElement.getAttribute("next");
		
		strNextScreen = oAttr != null ? oAttr.getValue() : strNextScreen;
		
		return strNextScreen;
		
	}
	public EntryLine getFirstEntryLine(){
		Object oObject = null;
		for(int i=0; i<_oLines.size(); i++){
			oObject = _oLines.get(i);
			
			if(oObject instanceof EntryLine)
				return (EntryLine)_oLines.get(i);
			
		}
		
		return null;		
	}
	
	public EntryLine getNieghborInputLine(EntryLine oTarget, boolean next) {
		
	EntryLine oNieghborEntryLine = null;
	Object oLine = null;
	
	//forwards
	if(next == false){			
		for(int i = 0; i < _oLines.size(); i++) {
			oLine = _oLines.get(i);
			
			if(oTarget == oLine)
				return oNieghborEntryLine;
			
			if(oLine instanceof EntryLine){
				oNieghborEntryLine = (EntryLine)oLine;
				
			}					
		}						
	} else {
	
		for(int i = _oLines.size()-1; i > -1; i--) {
			oLine = _oLines.get(i);
			
			if(oTarget == oLine)
				return oNieghborEntryLine;
			
			if(oLine instanceof EntryLine){
				oNieghborEntryLine = (EntryLine)oLine;
				
			}					
		}	
	}

	return oNieghborEntryLine;

}
	
	public EntryLine getLastEntryLine(){
		
		Object oObject = null;
		for(int i=_oLines.size()-1; i > -1; i--){
			oObject = _oLines.get(i);
			
			if(oObject instanceof EntryLine)
				return (EntryLine)_oLines.get(i);
			
		}
		
		return null;
		
	}
	
	public int getIndexOfLine(EntryLine oTarget) {
		
		Object oLine = null;
		for(int i=0; i<_oLines.size(); i++) {
			oLine = _oLines.get(i);
			
			if(oLine == oTarget)
				return i;
		}
					
		return -1;
	}
	public int getNumberOfInputLines() {
					
		int inputLineCount = 0;
		ScreenLine oLine = null;
		for(int i=0; i<_oLines.size(); i++) {
			oLine = (ScreenLine)_oLines.get(i);
			
			if(oLine instanceof EntryLine)
				inputLineCount++;
		}
		
		return inputLineCount;
		
	}
	
    public void enter(Object arg0){
    	
    		if(arg0 == null)
    			return;
    	
	    	try{
	    		
	    		_enterArg0 = arg0;
	    		
	    		Iterator oItr = _oScreenElement.getDescendants();
	    		ArrayList oTempList = new ArrayList();
	    		
	    		Object oObj = null;
	    		Element oElem = null;
	    		while(oItr.hasNext()){
	    			
	    			oObj = oItr.next();
	    			
	    			if(oObj instanceof Element){
		    			oElem = (Element)oObj;
		    			    			
		    			if(oElem.getName().equals("field")
		    					/*&&  oElem.getAttributeValue("type").equals("data")*/){
		    				
		    				oTempList.add(oElem);
		    			}
	    				
	    			}
	    			
	    			
	    		}
	    		
	    		for(int i=0; i< oTempList.size(); i++){
	    			
	    			oElem = (Element)oTempList.get(i);
    				String strClass = oElem.getAttributeValue("class");
    	    		
    	    			String strMethod  = oElem.getAttributeValue("get");
    	    			
    	    			if(strClass == null || strMethod == null)
    	    				continue;
    	    		
    	    			Class c = Class.forName(strClass);
    	    			Class classParams[] = {};
                
    	    			Method m = c.getMethod(strMethod, classParams);
                
    	    			Object objectParms[] = {};
    	    			Object oValue = m.invoke(arg0, objectParms);
         
    	    			//AppLogger.debug("EntryScreen value=" + oValue.toString());
    	    			  	    			
    	    			setEntryFieldValue(oElem.getAttributeValue("name"), oValue.toString());	    			
	    		}
	    		

	    		
	    	}catch(Exception oEx){
	    		AppLogger.error(oEx);
	    	}
 			
       
       
               
    }
    
    public void setEntryFieldValue(String name, String value){
    	
    		EntryField oFld = getEntryField(name);
    		
    		if(oFld != null)
    			oFld.setDisplayText(value);

    }

	public ScreenInterface signal(BusMessage oMessage) {
				
		
		if(oMessage.getButtonEvent().getAction().equals(RELEASED))
			return this;
		
		if(FK_CLR.equals(oMessage.getButtonEvent().getKeyId())) {
			return null;
		} 
		
		if(FK_ENT.equals(oMessage.getButtonEvent().getKeyId())
				&& this.isComplete()){
			return getInstanceContext().getContentManager().getScreenContent(getNextScreen(), getInstanceContext());
		}
		
		if(_activeEntryLine != null){
			
			int lineFocus = _activeEntryLine.signal(oMessage);
			
			if(lineFocus > 0){
				
				EntryLine oNext = getNieghborInputLine(_activeEntryLine, true);
					
				if(oNext != null)
					_activeEntryLine = oNext;
							
				
			} else if(lineFocus < 0){
				
				EntryLine oPrev = getNieghborInputLine(_activeEntryLine, false);
					
				if(oPrev != null)
					_activeEntryLine = oPrev;
				
			} 
			
			_currLine = this.getIndexOfLine(_activeEntryLine);
			
		}
	
		
		return this;
		
	}

}

public abstract class ScreenContent implements Constants, ScreenInterface {
	
	protected ScreenLineList _oLines = new ScreenLineList();
	protected Element _oScreenElement = null; 
	protected Object _enterArg0 = null;
	protected ScreenContent _oParent = null;
	private InstanceContext _oInstanceContext = null;
	
	protected int _currLine = 0;
	protected int _currField = 0;
	protected int _maxLine = 0;
	protected int _minLine = 0;
	protected boolean _cursorOn = true;
	
	private int _displayedFrom = -1;
	private int _displayedTo = -1;
	private boolean _refresh = false;
	
	
	private DscMessage _inComing = null;
	private DscMessage _outGoing = null;

	public DscMessage getOutGoingDscMessage(){
		
		if(_outGoing == null)
			_outGoing = new DscMessage();
		
		return _outGoing;
	}
	
	public void setOutGoingDscMessage(DscMessage oDscMessage){
		_outGoing = oDscMessage;
	}
	
	public DscMessage getIncomingDscMessage(){
		
		if(_inComing == null)
			_inComing = new DscMessage();
		return _inComing;
	}
	
	public void setIncomingDscMessage(DscMessage oDscMessage){
		_inComing = oDscMessage;
	}
	public void setInstanceContext(InstanceContext oCtx){
		_oInstanceContext = oCtx;
	}
	
	public InstanceContext getInstanceContext(){
		return _oInstanceContext;
	}
	public int getDisplayedFrom(){
		return _displayedFrom;
	}

	public void setDisplayedFrom(int from){
		_displayedFrom = from;
	}
	public int getDisplayedTo(){
		return _displayedTo;
	}
	
	public void setDisplayedTo(int to){
		_displayedTo = to;
	}

	public boolean forceRefresh() {
		return _refresh;
	}
	public void resetDisplayed(){
		_displayedFrom = -1;
		_displayedTo = -1;
	}
	public abstract ScreenInterface signal(BusMessage oMessage);
    public void init() {
    	
	    _currLine = 0;
	    	_currField = 0;
	    	_maxLine = 0;
	    	_minLine = 0;
	    	
	    _displayedFrom = -1;
	    	_displayedTo = -1;
	   		
    	
    }
    
    //state enter exit routines
    public void exit(BusMessage oMessage) throws Exception {}
    
    public void enter(Object arg0){
    	
    	
    		if(arg0 == null)
    			return;
    	
	    	try{
	    		
	    		_enterArg0 = arg0;
	    		
	    		Iterator oItr = _oScreenElement.getDescendants();
	    		ArrayList oTempList = new ArrayList();
	    		
	    		Object oObj = null;
	    		Element oElem = null;
	    		while(oItr.hasNext()){
	    			
	    			oObj = oItr.next();
	    			
	    			if(oObj instanceof Element){
		    			oElem = (Element)oObj;
		    			    			
		    			if(oElem.getName().equals("field")
		    					/*&&  oElem.getAttributeValue("type").equals("data")*/){
		    				
		    				oTempList.add(oElem);
		    			}
	    				
	    			}
	    			
	    			
	    		}
	    		
	    		for(int i=0; i< oTempList.size(); i++){
	    			
	    			oElem = (Element)oTempList.get(i);
    				String strClass = oElem.getAttributeValue("class");
    	    		
    	    			String strMethod  = oElem.getAttributeValue("get");
    	    		
       	    		if(strClass == null || strMethod == null)
    	    				continue;
 
    	    			Class c = Class.forName(strClass);
    	    			Class classParams[] = {};
                
    	    			Method m = c.getMethod(strMethod, classParams);
                
    	    			Object objectParms[] = {};
    	    			Object oValue = m.invoke(arg0, objectParms);
         
    	    			//AppLogger.debug("Screen value=" + oValue.toString());
    	    			oElem.setText(oValue.toString());	    			
	    		}
	    		

	    		
	    	}catch(Exception oEx){
	    		AppLogger.error(oEx);
	    	}
 			
       
       
               
    }
    
	public ScreenContent(Element oScreenElement){
		_oScreenElement = oScreenElement;
		
		if(_oScreenElement.getAttributeValue("refresh") != null)
			_refresh = true;
		
		if(_oScreenElement.getAttributeValue("scroll") != null 
				&& _oScreenElement.getAttributeValue("scroll").equals("false"))
			_cursorOn = false;
		
	}
	
    public int getPrevChoiceIdx(int start, String tag) {
    	
		ScreenLine oLine = null;
		for(int i=start-1; i > -1; i--){
			oLine = (ScreenLine)_oLines.get(i);
			if(oLine.getAttributeValue("type").equals(tag)){
				return i;
			}
			
		}
		
		return -1;
	}
	
	public int getNextChoiceIdx(int start, String tag) {
	   	
		ScreenLine oLine = null;
		for(int i=start+1; i < _oLines.size(); i++){
			oLine = (ScreenLine)_oLines.get(i);
			if(oLine.getAttributeValue("type").equals(tag)){
				return i;
			}
			
		}
		
		return -1;
	}
	public int getLastChoiceIdx(String tag){
	   	
		ScreenLine oLine = null;
		for(int i=_oLines.size()-1; i>-1; i--){
			oLine = (ScreenLine)_oLines.get(i);
			if(oLine.getAttributeValue("type").equals(tag)){
				return i;
			}
			
		}
		
		return -1;
	
	}
	
	public int getFirstChoiceIdx(String tag){
	   	
		ScreenLine oLine = null;
		for(int i =0; i<_oLines.size(); i++){
			oLine = (ScreenLine)_oLines.get(i);
			if(oLine.getAttributeValue("type").equals(tag)){
				return i;
			}
			
		}
		
		return -1;
	
	}

	
	public int getActiveLine() {
		return _currLine;
	}
	public int getActiveCharacter(){
		return _currField;
	}
	public int getMaxLine(){
		return _maxLine;
	}
	
	public int getMinLineg(){
		return _minLine;
	}
	
	public ScreenContent getParent(){
		return _oParent;
	}
	
	public void setParent(ScreenContent oParent){
		_oParent = oParent;
	}
	
	public boolean isCursorOn(){
		return _cursorOn;
	}
	
	public void setCursonOn(boolean onTrue){
		_cursorOn = onTrue;
	}
	
	public int getHeaderCount(){
		int headerCount = 0;
		
		List oList = getLines();
		
		ScreenLine oLine = null;
		for(int i=0; i<oList.size();i++){
			oLine = (ScreenLine)oList.get(i);
			
			if(oLine.getAttributeValue("subtype").equals("header")){
				headerCount++;
			} else{
				break;
			}
		
		}
		
		return headerCount;		
	}
	public int getFooterCount(){
		int footerCount = 0;
		
		List oList = getLines();
		
		ScreenLine oLine = null;
		for(int i=0; i<oList.size();i++){
			oLine = (ScreenLine)oList.get(i);
			
			if(oLine.getAttributeValue("subtype").equals("footer")){
				footerCount++;
			}
		
		}
		
		return footerCount;
		
	}

	public ScreenLineList getLines() {		
		return _oLines;		
	}
	
	public String toString() {
		return _oLines.toString();	
	}
	
	public void add(String strLine) {
		_oLines.add(strLine);
	}
	
	public Element getObjectsRootElement(){
		return _oScreenElement;
	}

	public String getScreenName(){
		return getAttributeValue("name");
	}
	
	public void refresh() {
		
		try {
	  		Iterator oItr = _oScreenElement.getChildren().iterator();
			 
	   		_oLines = new ScreenLineList();
	   		
			Element oLine = null;
			while(oItr.hasNext()){
				oLine = (Element)oItr.next();
				
				if(oLine.getName().equals("line")){
		   			parseLine(oLine);   			
				}
				
			}			
					
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}
 
	}
	public String getAttributeValue(String attrName){
		String attrValue = null;
		
		Attribute oAttr = _oScreenElement.getAttribute(attrName);
		
		attrValue = oAttr != null ? oAttr.getValue() : "";
				
		return attrValue;
	}
	
	public String getSelectedValue(String name){		
		return "";	
	}
	
	private static ScreenInterface createScreenContent(Element oRoot, Element oScreenRoot, MultiContentManager oCMngr)  throws Exception {
		
		ScreenInterface oRet= null;
		
		 if(oScreenRoot.getAttribute("clazz") != null ) {
			oRet = createClazz(oScreenRoot, oCMngr);
			return oRet;
		 }
		 
		 
		//is bean screen
		 if(oScreenRoot.getAttribute("class") != null ) {
			oRet = createBeanScreen(oScreenRoot, oCMngr);
			return oRet;
		 }
		 
		Attribute oTypeAttr = oScreenRoot.getAttribute("type");
		String strScreenType = oTypeAttr != null ? oTypeAttr.getValue() : null;
		
		
		if(strScreenType.equals("menu")){
			oRet = new MenuScreen(oScreenRoot);
		}else if(strScreenType.equals("input")){
			oRet = new EntryScreen(oScreenRoot);
		} else if(strScreenType.equals("data")) {
			oRet = new DataListScreen(oScreenRoot);
		} else if(strScreenType.equals("detail")){
			oRet = new DataListItemScreen(oScreenRoot);
		} else if(strScreenType.equals("action")) {
		
			
			//need to be change to use introspection
			String strName = oScreenRoot.getAttributeValue("class");
			if(strName == null || strName.length() == 0)
				oRet = new SendActionScreen(oScreenRoot);		
			//else
			//	oRet = new SendDistressScreen(oScreenRoot);
			
			oRet.setInstanceContext(oCMngr.getInstanceContext());
			
		} else {
			throw new Exception("screen attribute type must be menu or input");
		}
		
		return oRet;
	}
	

	public static ScreenInterface createFromXml(Element oRoot, Element oScreenRoot, MultiContentManager oCMngr)  {
		
		ScreenInterface oRet1 = null;
		ScreenContent oRet =null;
		try {
			
			oRet1 = ScreenContent.createScreenContent(oRoot, oScreenRoot, oCMngr);
			
			if (oRet1 instanceof net.sourceforge.dscsim.controller.screen.StateScreen)
				return oRet1;
			else
				oRet = (ScreenContent)oRet1;
			
			oRet.parseScreenAttributes(oScreenRoot);
			
			//no line processing needed until display time for bean screens
			//part of migration to new screens.
			if(oRet instanceof BeanBaseScreen){
				return oRet;
			}

			
	   		Iterator oItr = oScreenRoot.getChildren().iterator();
				
	   		Element oLine = null;
	   		while(oItr.hasNext()){
	   			oLine = (Element)oItr.next();
	   			
	   			if(oLine.getName().equals("line")){
	   	   			oRet.parseLine(oLine);
	   			}
	  			
	   		}			
			
			oRet.init();

		} catch(Exception oEx){
			AppLogger.error(oEx);
		}
			

			return oRet;
		
	}
	
	private void parseScreenAttributes(Element oRoot) throws Exception {
/*		
		Attribute oAttr = oRoot.getAttribute("minline");		
		_minLine = oAttr == null ? 0 : oAttr.getIntValue();
		_currLine = _minLine;
		
		 oAttr = oRoot.getAttribute("maxline");		
		_maxLine = oAttr == null ? 0 : oAttr.getIntValue();
*/		
	}
		
	protected void parseLine(Element oLine) throws Exception {
			
		
	  Attribute oTypeAttr = oLine.getAttribute("type");
	  String strAttrType = oTypeAttr != null ? oTypeAttr.getValue() : null;
	  
	  if(strAttrType == null)
		  throw new Exception("line must have a type attribute.");

	  ScreenLine oScreenLine = null;
	  
	  if( strAttrType.equals("text")){
	  		_oLines.add(new InfoLine(oLine));	  		  	
	  } else if(strAttrType.equals("choice")) {	  		  	
	  		oScreenLine = new MenuLine(oLine);
	  		_oLines.add(oScreenLine);	  		  	
	  } else if(strAttrType.equals("input")) {
	  		parseInputLine(oLine);
	  }else if(strAttrType.equals("list")) {	
	  		String storeName = _oScreenElement.getAttributeValue("storage");	
	  	    expandDataLineList(_oLines, oLine, storeName);
 	  }else{
		throw new Exception("line type attribute must be {text | input |output}"); 
		 
	  }
	  
	}
	
	
	protected void expandDataLineList(ArrayList oDstLines, Element oLine, String storeName) {

		try {
			
			
			AppLogger.debug("ScreenContent.expandDataLineList storeName=" + storeName);
			
			List oList = getInstanceContext().getContentManager().getPersistantList(storeName);
			           
			Iterator oItr = oList.iterator();
			
			Object oObj = null;
			DataListLine oDataListLine = null;
			while(oItr.hasNext()){
				
			    	oObj = oItr.next();
			    	
			    	oDataListLine = createDataListLine(oLine, oObj);
			    	
			    	
			    	if(oDataListLine != null)
			    		oDstLines.add(oDataListLine);
			}
			

		} catch (Exception oEx){
			AppLogger.error(oEx);
		}
		
		
		
			

		
	}
	
	private void expandDataList(ArrayList oDstLines, Element oLine) {
		
		try {
			String strClass = oLine.getAttributeValue("class");
			String strMethod = oLine.getAttributeValue("method");
			
            Class c = Class.forName(strClass);
            Class paramTypes[] = {};
            
            Method m = c.getMethod(strMethod, paramTypes);
            
            Object parmValues[] = {};
    
            List oList = (List)m.invoke(null, parmValues);
            
            Iterator oItr = oList.iterator();
            
            Object oObj = null;
            DataListLine oDataListLine = null;
            while(oItr.hasNext()){
            	
	            	oObj = oItr.next();
	            	
	            	oDataListLine = createDataListLine(oLine, oObj);
	            	
	            	if(oDataListLine != null)
	            		oDstLines.add(oDataListLine);
            }
		
			
		} catch (Exception oEx){
			AppLogger.error(oEx);
		}
		
		
		
			
			
	}
	
	private static DataListLine createDataListLine(Element oLineDescr, Object srcObj) throws Exception {
		
		List oList = oLineDescr.getChildren("field");
		
		if(oList == null)
			return null;
		
		Element oFieldElements = null;		
		DataListLine oDataListLine = new DataListLine(oLineDescr, srcObj);
		
		for(int i =0; i < oList.size(); i++){
			oFieldElements = (Element)oList.get(i);
					
			addFieldToDataListLine(oDataListLine,  oFieldElements, srcObj);

			//oDstLines.add(oDataListLine);

		}
		
		return oDataListLine;
			
		
	}
	private static void addFieldToDataListLine(DataListLine oDataListLine, Element oFieldElements, Object srcObj) throws Exception {

		Attribute oAttr = oFieldElements.getAttribute("class");
		String strClass = oAttr != null ? oAttr.getValue() : null;
		
		oAttr = oFieldElements.getAttribute("get");
		String strMethod = oAttr != null ? oAttr.getValue() : null;
		
        Class c = Class.forName(strClass);
        Class classParams[] = {};
        
        Method m = c.getMethod(strMethod, classParams);
        
        Object objectParms[] = {};
        Object oValue = m.invoke(srcObj, objectParms);
        
        oDataListLine.getFields().add(new DataListField(oFieldElements, oValue));
               
         
	}

	private void parseInputLine(Element oInputLine) throws Exception {

   		Iterator oItr = oInputLine.getChildren().iterator();
			
   		Element oField = null;
   		
   		EntryLine oEntryLine = new EntryLine(oInputLine);
   		while(oItr.hasNext()){
   			oField = (Element)oItr.next();
   			
   			 if(oField.getName().equals("field") == false){
   	   			throw new Exception("lines can only contain field elements.");
   			 }
   			
			  String fieldType = oField.getAttribute("type").getValue();
			  
			  if(fieldType.equals("text")){
				  oEntryLine.addScreenField(new TextField(oField.getValue(), 0));
			  } else if(fieldType.equals("input")){
				  
				 EntryField oEntryField = new EntryField(oField);

				 oEntryLine.addScreenField(oEntryField);
				 
			  } else {
	   	   		throw new Exception("field type must either be label or input");		  	
			  }
  			
   		}						
	  
   		 oEntryLine.init();
   		
	    _oLines.add(oEntryLine);
	  
	}
	
	public static ScreenContent createInnerBeanScreen(Element oScreenRoot, MultiContentManager oCMngr)
	throws Exception {
		
		ScreenContent oRetScreen = null;
		
		String strClazzName = oScreenRoot.getAttributeValue("class");
		
		StringTokenizer oTokenizer = new StringTokenizer(strClazzName, "$");
		
		String strOuter = oTokenizer.nextToken();
		String strInner = oTokenizer.nextToken();
		
		Class scrnClazz = Class.forName(strOuter);
		
		Constructor oCtr = scrnClazz.getConstructor(new Class[]{oScreenRoot.getClass(), oCMngr.getClass(), String.class});
		
		oRetScreen = (ScreenContent)oCtr.newInstance(new Object[]{oScreenRoot, oCMngr, strClazzName});
		
		oRetScreen.setInstanceContext(oCMngr.getInstanceContext());

		oRetScreen.init();
				
		return oRetScreen;

		
	}
	
	public static ScreenContent createBeanScreen(Element oScreenRoot, MultiContentManager oCMngr)
	throws Exception {

		ScreenContent oScreenContent = null;
				
		String strClazzName = oScreenRoot.getAttributeValue("class");
		
		
		if(strClazzName.indexOf("$")>-1){
			return createInnerBeanScreen(oScreenRoot, oCMngr);
		}
		
		Class scrnClazz = Class.forName(strClazzName);
		
		Constructor oCtr = scrnClazz.getConstructor(new Class[]{oScreenRoot.getClass(), oCMngr.getClass()});
		
		oScreenContent = (ScreenContent)oCtr.newInstance(new Object[]{oScreenRoot, oCMngr});
		
		oScreenContent.setInstanceContext(oCMngr.getInstanceContext());

		oScreenContent.init();
				
		return oScreenContent;
		
	}	
	
	
	public static ScreenInterface createClazz(Element oScreenRoot, MultiContentManager oCMngr)
	throws Exception {

		ScreenInterface oScreenContent = null;
				
		String strClazzName = oScreenRoot.getAttributeValue("clazz");
		
		
		Class scrnClazz = Class.forName(strClazzName);
		
		Constructor oCtr = scrnClazz.getConstructor(new Class[]{oScreenRoot.getClass(), oCMngr.getClass()});
		
		oScreenContent = (ScreenInterface)oCtr.newInstance(new Object[]{oScreenRoot, oCMngr});
		
		oScreenContent.setInstanceContext(oCMngr.getInstanceContext());

		//oScreenContent.init();
				
		return oScreenContent;
		
	}	
		

}
