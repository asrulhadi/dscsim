package net.sourceforge.dscsim.controller.screen;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.screen.BeanMenuScreen;
import net.sourceforge.dscsim.controller.screen.types.ActiveField;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Element;



public class SetupOutgoingDscMessageScreen extends BeanMenuScreen implements
		Constants {

	private static final String MENU_MODE="menu";
	private String _mode = null;
	
	//input screen
	private  BeanLine _activeBeanLine = null;
	private  BeanLine _firstBeanLine = null;
	private  BeanLine _lastBeanLine = null;
	private Object _oData = null;
	
	public SetupOutgoingDscMessageScreen(Element oScreenElement,
			MultiContentManager oCMngr) throws Exception {
		super(oScreenElement, oCMngr);
		
		_mode = _oScreenElement.getAttributeValue("mode");
		
		if(_mode==null){
			_mode = MENU_MODE;
		}
		

	}
	
	public void enter(Object arg0){
		AppLogger.debug("AcknowledgeIncomingScreen.enter");   
		super.enter(arg0);
		
		if(_mode.equals(MENU_MODE)){
			
			
			init();
			getLines();
			
			_currLine =  getFirstChoiceIdx("choice");
		   	_maxLine = getLastChoiceIdx("choice");
		   	_minLine = _currLine;
		   	
		   	
		    setDisplayedFrom(-1);
		    setDisplayedTo(-1);
	    	
			return;
		}
			
		
		List oList = _oScreenElement.getChildren("line");
		
		//see if any data stored in session context
		_oData = getOutGoingDscMessage();
		
		
		Element oChild = null;
		BeanLine oBeanLine = null;
		_oLines.clear();
		
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
		
		int firstLine = 0;//getHeaderCount();
		if(_firstBeanLine  != null)
			firstLine = getIndexOfLine(_firstBeanLine) - firstLine;
		
		_currLine = firstLine;
		
	   	_maxLine = getFooterCount();
	   	_minLine = getHeaderCount();

	    setDisplayedFrom(_minLine);
	    setDisplayedTo(_maxLine);

	}
	
	private void doGetSet(BeanLine currLine, Element oContext, Element oMapping) throws Exception {
		
		
		//first try object
		Object srcInfo = currLine.getData();
		
		if(srcInfo != null){
					
			//find the context for the screen and see if there is 
			//anything we need to extract from the screens and set in
			//the session property.
				
			String strContextName = oContext.getAttributeValue("name");
				
			Object dstInfo = getOutGoingDscMessage();
			
			List oMappings = oContext.getChildren();

			String strGetter = null;
			String strSetter = null;
			Method mthdGetter = null, mthdSetter = null;
									
			strGetter = oMapping.getAttributeValue("get");
			strSetter = oMapping.getAttributeValue("set");
					
			mthdGetter = srcInfo.getClass().getMethod(strGetter, new Class[0]);
			mthdSetter = dstInfo.getClass().getMethod(strSetter, new Class[]{String.class});
			
			Object strData = mthdGetter.invoke(srcInfo, new Object[0]);
			mthdSetter.invoke(dstInfo, new Object[]{strData} );
				
		
			return;
		}
		
	}
	private void doConstant(BeanLine currLine, Element oContext, Element oMapping) throws Exception {
		
							
		String strContextName = oContext.getAttributeValue("name");
			
		Object dstInfo = getOutGoingDscMessage();
								
		String strMemClass = oMapping.getAttributeValue("class");
		String strMemName = oMapping.getAttributeValue("member");
		
		String strSetter = oMapping.getAttributeValue("set");
		Method mthdSetter = null;
				
		//get value form class member
		String strConst = Class.forName(strMemClass).getField(strMemName).get(null).toString();
		
		mthdSetter = dstInfo.getClass().getMethod(strSetter, new Class[]{String.class});
					
		mthdSetter.invoke(dstInfo, new Object[]{strConst} );
			
		return;

	}
	
	private void doChoice(BeanLine currLine, Element oContext, Element oMapping) throws Exception {

		//2nd possibilty
		String strCode = currLine.getAttributeValue("code");
		String strSetter = null;
		Method mthdSetter = null;
		
		if(strCode.equals("")==false){
					
			String strContextName = oContext.getAttributeValue("name");
		
			Object dstInfo = getOutGoingDscMessage();
			
			strSetter = oMapping.getAttributeValue("set");
			
			mthdSetter = dstInfo.getClass().getMethod(strSetter, new Class[]{String.class});
			
			mthdSetter.invoke(dstInfo, new Object[]{strCode} );
			
		} 
			
	}	
	
	private void doReset(BeanLine currLine, Element oContext, Element oMapping) throws Exception {

		DscMessage oSelected = (DscMessage)currLine.getData();
		
		setIncomingDscMessage(new DscMessage(oSelected));
		setOutGoingDscMessage(new DscMessage(oSelected));
	
	}
	
	public ScreenContent signal(BusMessage oMessage) {
		
		if(_mode.equals(MENU_MODE))
			return menuSignal(oMessage);
		else
			return inputScreenSignal(oMessage);
		
		
	}
	public boolean isComplete(){
		
		if(_lastBeanLine == null || _lastBeanLine.isComplete())
			return true;
		else
			return false;
	}
	
	public ScreenLineList  getLines() {
		if(MENU_MODE.equals(_mode))
			return super.getLines();
		else
			return _oLines;
	}
	public ScreenContent inputScreenSignal(BusMessage oMessage) {

		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		if(keyAction.equals(RELEASED))
			return this;
		
		if(FK_CLR.equals(keyID)){
			return null;
		} 
		
		if(FK_ENT.equals(keyID)){
			
			if(isComplete()){
				
				String nextScreen = _oScreenElement.getAttributeValue("next");
				
				ScreenContent oScreenNext = _oContentManager.getScreenContent(nextScreen, getInstanceContext());
				
				oScreenNext.setParent(this);
				oScreenNext.setOutGoingDscMessage(getOutGoingDscMessage());
				oScreenNext.setIncomingDscMessage(getIncomingDscMessage());
				oScreenNext.setInstanceContext(getInstanceContext());
				
				return oScreenNext;
				
			}
		}			
		
		if(MV_DOWN.equals(keyID) 
			&& _activeBeanLine != null){
			
			BeanLine oNext = getNieghborInputLine(_activeBeanLine, true);	
			
			if(oNext != null)
				_activeBeanLine = oNext;

			int tmp = getIndexOfLine(_activeBeanLine);
			
			_currLine = tmp;// - getHeaderCount();
			
			return this;
			
		}
		
		if(MV_UP.equals(keyID) 
			&& _activeBeanLine != null){
	
			BeanLine oPrev = getNieghborInputLine(_activeBeanLine, false);	
			
			if(oPrev != null)
				_activeBeanLine = oPrev;

			int tmp = getIndexOfLine(_activeBeanLine);
			
			_currLine = tmp;// - getHeaderCount();
			
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
			
			_currLine = getIndexOfLine(_activeBeanLine);//-getHeaderCount();
			
		}
	
		
		return this;
	
	}
	
	public int getActiveLine(){
		
		return _currLine;
	}
	public ActiveField getActiveField(String beanName) {
		
		ActiveField oBean = null;
		BeanLine oLine =null;
		for(int i=0; i<_oLines.size();i++){
			
			oLine =(BeanLine)_oLines.get(i);
			
			oBean = oLine.getBean(beanName);
			
			if(oBean != null)
				break;
			
			
		}
		
		return oBean;
	}

	private void doAttributeSet(Element oContext, Element oMapping) throws Exception {

		ActiveField oBean = getActiveField(oMapping.getAttributeValue("source"));
		
		Object oOutGoing = getOutGoingDscMessage();
		
		Method setter = oOutGoing.getClass().getMethod(oMapping.getAttributeValue("dest"), new Class[]{oBean.getClass()});
		
		setter.invoke(oOutGoing, new Object []{oBean});
	}
	
	
	private void exitEntry(BusMessage oMessage) throws Exception {
		Element oContext = _oScreenElement.getChild("context");
		
		//screen has no context element. Is ok.
		if(oContext == null)
			return;
		
		List oMappings = oContext.getChildren();
		Element oMapping = null;
		String mapType = null;
		for(int i=0; i < oMappings.size(); i++){
			
			oMapping = (Element)oMappings.get(i);
			mapType = oMapping.getAttributeValue("type");
			
			if("attribute".equals(mapType))
				doAttributeSet(oContext, oMapping);
			else 
				AppLogger.error("BeanSetupSendMenuScreen.exit - Mapping not recognized.");
			
		}
	
	}
	
	public void exit(BusMessage oMessage) throws Exception {  
		super.exit(oMessage);
		
		if(_mode.equals(MENU_MODE)!= true){
			exitEntry(oMessage);
			return;
			
		}
		
		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		//if clr has been pressed then don't process
		//the screen state
		if(FK_ENT.equals(keyID) == false)
			return;
				
		if(_currLine < 0)
			return;
		
		BeanLine oBeanLine = (BeanLine)_oLines.get(_currLine/* + getHeaderCount()*/);

		Element oContext = _oScreenElement.getChild("context");
		
		//screen has no context element. Is ok.
		if(oContext == null)
			return;
		
		List oMappings = oContext.getChildren();
		Element oMapping = null;
		String mapType = null;
		for(int i=0; i < oMappings.size(); i++){
			
			oMapping = (Element)oMappings.get(i);
			mapType = oMapping.getAttributeValue("type");
			
			if("extract".equals(mapType))
				doGetSet(oBeanLine, oContext, oMapping);
			else if("choice".equals(mapType))
				doChoice(oBeanLine, oContext, oMapping);
			else if("constant".equals(mapType))
				doConstant(oBeanLine, oContext, oMapping);
			else if("reset".equals(mapType))
				doReset(oBeanLine, oContext, oMapping);
			else 
				throw new Exception("BeanSetupSendMenuScreen.exit - Mapping not recognized.");
			

		}
		
		
	}
	private void doProperty(BeanLine currLine, Element oContext, Element oMapping) throws Exception {

		
		String strProperty = oMapping.getAttributeValue("property");
			
		Object property = getMultiContentManager().getSetting(strProperty);
		Object oOutGoing = getOutGoingDscMessage();
		
		String strSetter = null;
		Method mthdSetter = null;

		strSetter = oMapping.getAttributeValue("set");		
	
		String strType = getMultiContentManager().getSettingsElement(strProperty).getAttributeValue("type");		
		mthdSetter = oOutGoing.getClass().getMethod(strSetter, new Class[]{Class.forName(strType)});

		mthdSetter.invoke(oOutGoing, new Object[]{property} );
			
	
		return;

	}
	
	public ScreenContent menuSignal(BusMessage oMessage) {

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
				
				
			
				int headers = 0; //getHeaderCount();
				BeanLine oCurrLine = (BeanLine)_oLines.get(_currLine + headers);	
				
				//Object oData = oCurrLine.getData();
				
				String screenName = oCurrLine.getAttributeValue("link");
				
				//getInstanceContext().setProperty(oCurrLine.getAttributeValue("storage"), oData);				
				
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
	
	public int getPrevChoiceIdx(int start) {
		
		int prev = -1;
		BeanLine oLine = null;
		
		ArrayList oScreenLines = getLines();
		int headers = getHeaderCount();
		for(int i=start-1; i > -1; i--){
			oLine = (BeanLine)oScreenLines.get(i);
			if(oLine.isChoice()){
				prev = i;
				break;
			}
			
		}
		
		return prev; 	
	}
	
	public int getNextChoiceIdx(int start) {
		
	   	int next = -1;
		BeanLine oLine = null;
		
		ArrayList oScreenLines = getLines();
		int headers = 0;//getHeaderCount();
		
		for(int i=headers + start +1; i < oScreenLines.size(); i++){
			oLine = (BeanLine)oScreenLines.get(i);
			if(oLine.isChoice()){
				next = i;
				break;
			}
			
		}
		
		return next;
 	
	}
}

