/*
 * Created on 23.06.2006
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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.screen.BeanScreen;
import net.sourceforge.dscsim.controller.screen.types.ActiveField;
import net.sourceforge.dscsim.controller.screen.types.DscString;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Element;


/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BeanLine extends ScreenLine {
	
	private int _line_num = -1;
	private ArrayList _oFields = new ArrayList();
	private ActiveField _firstEntryBean = null;
	private ActiveField _lastEntryBean = null;
	private ActiveField _activeEntryBean = null;
	private Object _inputObject = null;
	private InstanceContext _oInstanceContext = null;
		
	protected void parseChildren()  {

		List oChildren = _oInputLine.getChildren("bean");

		Element oChild = null;
		ActiveField oBean = null;
				
		for(int i=0; i < oChildren.size(); i++){	
			
			oChild = (Element)oChildren.get(i);
			
			try {
				
				oBean = newActiveField(oChild, _inputObject);

			}catch(Exception oEx){
				AppLogger.error(oEx);
			}
			
			if(oBean.isEntryField()){
				if(_firstEntryBean == null){
					_firstEntryBean = oBean;
					
					if(!oBean.isComplete())
						_activeEntryBean = _firstEntryBean;
				}
				
				_lastEntryBean = oBean;
			}
			
			if(_activeEntryBean == null)
				_activeEntryBean = _lastEntryBean;
					
			_oFields.add(oBean);
		
		}					
		

	}
	
	private  ActiveField newActiveField(Element oElement, Object oInput) throws Exception {
		
		ActiveField oNewField = null;
		
		String clazz = oElement.getAttributeValue("class");
		
		//is regular bean
		if(clazz == null){
			
			String format = oElement.getAttributeValue("format");
			
			 if(oElement.getAttributeValue("context") != null){
				oNewField = new ContextBean(oElement, _oInstanceContext);
			 } else if(format != null && format.charAt(0) == '?'){
					 oNewField = new AnBean(oElement, oInput);
			 } else {
				 oNewField = new Bean(oElement, oInput);
			 }
			
		} else {
			
			if(oElement.getAttributeValue("property") != null)
				oNewField = _oInstanceContext.getContentManager().getPropertyEx(oElement);
			else if(oElement.getAttributeValue("code") != null)
				oNewField = createBeanField(oElement, oElement.getAttributeValue("code"));
			else
				oNewField = createBeanField(oElement, oInput);
		}
		
		
		return oNewField;
		
		
	}
	
	private ActiveField createBeanField(Element oElement, Object oInput) throws Exception {
		
		
		String strClass = oElement.getAttributeValue("class");
		
		Class oClass = Class.forName(strClass);
		
		Constructor oCtor = oClass.getConstructor(new Class[]{Element.class, Object.class});
		
		return (ActiveField)oCtor.newInstance(new Object[]{oElement, oInput});
		
		
	}
	public ActiveField getBean(String beanName){
		ActiveField oBean = null;
		
		String testName = null;
		for(int i=0; i<_oFields.size(); i++){
			oBean = (ActiveField)_oFields.get(i);
			
			testName = oBean.getFieldName();
			if(testName != null && beanName.equals(testName)){
				break;
			}
			
			oBean = null;
			
		}
		
		return oBean;
	}
	public Object getData(){
		return _inputObject;
	}
	
	
	public BeanLine(Element oLine, InstanceContext oContext){
		super(oLine, oContext);
		_inputObject = null;
		_oInstanceContext = oContext;
		
		parseChildren();
		
	}
	
	public BeanLine(Element oLine, Object oObj, InstanceContext oContext){
		super(oLine, oContext);
		_oInstanceContext = oContext;
		_inputObject = oObj;
		parseChildren();
		
	}
	
	public BeanLine(int line_num, Element oLine, Object oObj, InstanceContext oContext){
		super(oLine, oContext);
		_oInstanceContext = oContext;
		_inputObject = oObj;
		_line_num = line_num;
		parseChildren();
		
	}
	public boolean isChoice(){
		
		if(_oInputLine.getAttribute("type") != null
				&& _oInputLine.getAttributeValue("type").equals("choice")){
			return true;
		} else{
			return false;
		}
	}
	
	public boolean hasEntryBean(){
		
		ActiveField oBean = null;
		for(int i=0; i <_oFields.size(); i++){
			oBean = (ActiveField)_oFields.get(i);
			
			if(oBean.isEntryField())
				return true;
			
		}
		
		return false;
	}
	
	public boolean isComplete(){
		
		ActiveField oBean =null;
		for(int i=0; i <_oFields.size(); i++){
			
			oBean = (ActiveField)_oFields.get(i);
			
			if(oBean.isComplete()==false)
				return false;
			
		}
		/*
		if(_lastEntryBean == null || _lastEntryBean.isComplete())
			return true;
		else
			return false;
			*/
		
		return true;
		
	}
	
	public ActiveField getNieghborInputField(ActiveField oTarget, boolean next) {
			
		ActiveField   oNeighborField = null;
		ActiveField oBean = null;
		if(next == false){			
			for(int i = 0; i < _oFields.size(); i++) {
				oBean = (ActiveField)_oFields.get(i);
				
				if(oTarget == oBean)
					return oNeighborField;
				
				if(oBean.isEntryField()){
					oNeighborField = oBean;
					
				}					
			}						
		} else {
			
			for(int i = _oFields.size()-1; i > -1; i--) {
				oBean = (ActiveField)_oFields.get(i);
				
				if(oTarget == oBean)
					return oNeighborField;
				
				if(oBean.isEntryField()){
					oNeighborField = oBean;
					
				}					
			}			
			
		}
			
		return oNeighborField;
	}

	
	public BeanLine(Element oInputLine) {
		this(oInputLine, null);
		
	}

	public String getDisplayText() {
		StringBuffer strLine = new StringBuffer();
		
		if(_line_num > 0)
			strLine.append(_line_num + ":");

		ActiveField oBean = null;
		for(int i=0; i<_oFields.size(); i++){
			oBean = (ActiveField)_oFields.get(i);
			
			strLine.append(oBean.getDisplayValue());
		}
		
		String str = strLine.toString();
		
		AppLogger.debug("BeanLine.getDisplayText=" + str);
		return str;
	}
	
	public int signal(BusMessage oMessage) {	
		
		//-1 give previous line focus
		// 0 this line keeps focus
		// 1 give next line focus
		
		int lineFocus = 0;
		int fieldFocus = 0;
		if(_activeEntryBean != null){
			
			fieldFocus = _activeEntryBean.signal(oMessage);
			
			if(fieldFocus > 0){
				
				ActiveField oNext = getNieghborInputField(_activeEntryBean, true);
				
				if(oNext == null){
					lineFocus = 1;					
				}else{
					lineFocus = 0;
					_activeEntryBean = oNext;
				}
				
			} else if(fieldFocus < 0){
				
				ActiveField oPrev = getNieghborInputField(_activeEntryBean, false);
				
				if(oPrev == null){
					lineFocus = -1;
				}else{
					lineFocus = 0;
					_activeEntryBean = oPrev;
				}
				
				
			} else {
				lineFocus = 0;
			}
				
			
		}
		
		return lineFocus;		
		
	}
	
	public ArrayList getBeans(){
		
		return _oFields;
	}
}

