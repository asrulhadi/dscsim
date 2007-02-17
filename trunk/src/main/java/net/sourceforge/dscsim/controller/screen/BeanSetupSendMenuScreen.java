package net.sourceforge.dscsim.controller.screen;

import java.lang.reflect.Method;
import java.util.List;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.screen.BeanMenuScreen;
import net.sourceforge.dscsim.controller.screen.types.ActiveField;
import net.sourceforge.dscsim.controller.screen.types.DscString;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Element;



public class BeanSetupSendMenuScreen extends BeanMenuScreen implements
		Constants {

	public BeanSetupSendMenuScreen(Element oScreenElement,
			MultiContentManager oCMngr) throws Exception {
		super(oScreenElement, oCMngr);
		// TODO Auto-generated constructor stub
	}
	
	public void enter(Object arg0){
		AppLogger.debug("BeanSetupSendMenuScreen.enter");   
		super.enter(arg0);
		
		
	}
	
	private void doProperty(BeanLine currLine, Element oActions, Element oAction) throws Exception {

			
		String strProperty = oAction.getAttributeValue("property");
			
		Object property = getMultiContentManager().getSetting(strProperty);
		Object oOutGoing = MultiContentManager.getContextPropertyEx(getInstanceContext(), oAction.getAttributeValue("context"));//getOutGoingDscMessage();
		
		String strSetter = null;
		Method mthdSetter = null;

		strSetter = oAction.getAttributeValue("set");		
	
		String strType = getMultiContentManager().getSettingsElement(strProperty).getAttributeValue("type");		
		mthdSetter = oOutGoing.getClass().getMethod(strSetter, new Class[]{Class.forName(strType)});

		mthdSetter.invoke(oOutGoing, new Object[]{property} );
				
		return;

	}
	
	private void doSetting(BeanLine currLine, Element oActions, Element oAction) throws Exception {
		
	String strName = oAction.getAttributeValue("property");

	ActiveField oSetting = (ActiveField)getMultiContentManager().getPropertyEx(oAction);
	
	if(oSetting instanceof DscString){
		((DscString)oSetting).setCode(currLine.getElement().getAttributeValue("code"));
	}else{
		oSetting.setElement(currLine.getElement());		
	}
	
	getMultiContentManager().replaceProperty(strName, oSetting);
	
	getMultiContentManager().saveProperties();
	
	return;

}
	private void doGetSet(BeanLine currLine, Element oActions, Element oAction) throws Exception {
		
		
		//first try object
		Object srcInfo = currLine.getData();
		
		if(srcInfo != null){
					
			//find the context for the action and see if there is 
			//anything we need to extract from the current line and set in
			//the context.
				
			String strContextName = oAction.getAttributeValue("context");
				
			Object dstInfo = MultiContentManager.getContextPropertyEx(getInstanceContext(), strContextName);
	
			String strGetter = null;
			String strSetter = null;
			Method mthdGetter = null, mthdSetter = null;
									
			strGetter = oAction.getAttributeValue("get");
			strSetter = oAction.getAttributeValue("set");
					
			mthdGetter = srcInfo.getClass().getMethod(strGetter, new Class[0]);
			mthdSetter = dstInfo.getClass().getMethod(strSetter, new Class[]{String.class});
			
			Object strData = mthdGetter.invoke(srcInfo, new Object[0]);
			mthdSetter.invoke(dstInfo, new Object[]{strData} );
				
		
			return;
		}
		
	}
	private void doConstant(BeanLine currLine, Element oActions, Element oAction) throws Exception {
		
							
		String strContextName = oAction.getAttributeValue("context");
			
		Object dstInfo = MultiContentManager.getContextPropertyEx(getInstanceContext(), strContextName);
								
		String strMemClass = oAction.getAttributeValue("class");
		String strMemName = oAction.getAttributeValue("member");
		
		String strSetter = oAction.getAttributeValue("set");
		Method mthdSetter = null;
				
		//get value form class member
		String strConst = Class.forName(strMemClass).getField(strMemName).get(null).toString();
		
		mthdSetter = dstInfo.getClass().getMethod(strSetter, new Class[]{String.class});
					
		mthdSetter.invoke(dstInfo, new Object[]{strConst} );
			
		return;

	}
	
	private void doChoice(BeanLine currLine, Element oActions, Element oAction) throws Exception {

		//2nd possibilty
		String strCode = currLine.getAttributeValue("code");
		String strSetter = null;
		Method mthdSetter = null;
		
		if(strCode.equals("")==false){
					
			String strContextName = oAction.getAttributeValue("context");
		
			Object dstInfo = MultiContentManager.getContextPropertyEx(getInstanceContext(), strContextName);
			
			strSetter = oAction.getAttributeValue("set");
			
			mthdSetter = dstInfo.getClass().getMethod(strSetter, new Class[]{String.class});
			
			mthdSetter.invoke(dstInfo, new Object[]{strCode} );
			
		} 
			
	}		
	
	public void exit(BusMessage oMessage) throws Exception {
		AppLogger.debug("BeanSetupSendMenuScreen.exit");   
		super.exit(oMessage);
		
		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		//if clr has been pressed then don't process
		//the screen state
		if(FK_ENT.equals(keyID) == false)
			return;
				
		if(_currLine < 0)
			return;
		
		BeanLine oBeanLine = (BeanLine)_oLines.get(_currLine + getHeaderCount());

		//first try screen then line for actions;
		Element oActions = _oScreenElement.getChild("actions");
		oActions = oActions == null ? oBeanLine.getElement().getChild("actions") : oActions;
		
		//ok,there are neither screen or line actions
		if(oActions == null){
			return;
		}			
		
		List lstActions = oActions.getChildren();
		Element oAction = null;
		String subType = null;
		for(int i=0; i < lstActions.size(); i++){
			
			oAction = (Element)lstActions.get(i);
			subType = oAction.getAttributeValue("subtype");
			
			if("extract".equals(subType))
				doGetSet(oBeanLine, oActions, oAction);
			else if("choice".equals(subType))
				doChoice(oBeanLine, oActions, oAction);
			else if("constant".equals(subType))
				doConstant(oBeanLine, oActions, oAction);
			else if("property".equals(subType))
				doProperty(oBeanLine, oActions, oAction);
			else if("setting".equals(subType))
				doSetting(oBeanLine, oActions, oAction);
			else 
				throw new Exception("BeanSetupSendMenuScreen.exit - Mapping not recognized.");
			

		}
		
		
	}
	  
}
