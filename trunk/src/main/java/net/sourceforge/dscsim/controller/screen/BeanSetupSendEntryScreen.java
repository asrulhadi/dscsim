package net.sourceforge.dscsim.controller.screen;

import java.lang.reflect.Method;
import java.util.List;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.screen.BeanMenuScreen;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Element;



public class BeanSetupSendEntryScreen extends BeanBaseScreen implements
		Constants {

	private  BeanLine _activeBeanLine = null;
	private  BeanLine _firstBeanLine = null;
	private  BeanLine _lastBeanLine = null;
	private Object _oData = null;

	public BeanSetupSendEntryScreen(Element oScreenElement,
			MultiContentManager oCMngr) throws Exception {
		super(oScreenElement, oCMngr);

		List oList = _oScreenElement.getChildren("line");
		
		//see if any data stored in session context
		Element oContext = oScreenElement.getChild("context");
		if(oContext != null && oContext.getAttributeValue("name") != null){
			
			String strCtxName = oContext.getAttributeValue("name");

			_oData = oCMngr.getInstanceContext().getProperty(strCtxName);		
		}
		
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
		
		int firstLine = getHeaderCount();
		if(_firstBeanLine  != null)
			firstLine = getIndexOfLine(_firstBeanLine) - firstLine;
		
		_currLine = firstLine;
	}
	
	public void enter(Object arg0){
		AppLogger.debug("BeanSetupSendEntryScreen.enter");   
		super.enter(arg0);
		
		
	}
	public void exit(BusMessage oMessage) throws Exception {
		AppLogger.debug("BeanSetupSendEntryScreen.exit");   
		super.exit(oMessage);
		
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
			
			if(isComplete()){
				
				String nextScreen = _oScreenElement.getAttributeValue("next");
				
				ScreenInterface oScreenNext = (ScreenInterface)_oContentManager.getScreenContent(nextScreen, getInstanceContext());
				
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
			
			_currLine = tmp - getHeaderCount();
			
			return this;
			
		}
		
		if(MV_UP.equals(keyID) 
			&& _activeBeanLine != null){
	
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
