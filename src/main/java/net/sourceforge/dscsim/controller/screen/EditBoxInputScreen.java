/*
 * Created on 15.03.2007
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

import org.jdom.Element;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditBoxInputScreen extends StateScreen  {

	private  ScreenComponent activeComponent = null;
	private  ScreenComponent firstComponent = null;
	private  ScreenComponent lastComponent = null;
	
	/**
	 * @param oScreenElement
	 */
	public EditBoxInputScreen(Element oScreenElement, MultiContentManager oCMngr) {
		super(oScreenElement, oCMngr);
	}

	/**
	 * method called before signal method takes control.
	 *
	 */
	public void enter(Object msg){
		super.enter(msg);
		EditBox.parseEditBox(this, elemScreen);		
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public ScreenInterface signal(BusMessage oMessage) {
	/*	
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

				//storePageProperties();
				
				return oMCmgr.getScreenContent(this.getAttributeValue("next"), getInstanceContext());

			} 
		}
		
		if(MV_LEFT.equals(keyID) 
				&& this.activeComponent != null){

			ScreenComponent oPrev = getNieghborInputLine(this.activeComponent, false);

			if(oPrev != null)
				this.activeComponent = oPrev;
			
			return this;
			
		} else 	if(MV_RIGHT.equals(keyID) 
				&& this.activeComponent != null){
			
			ScreenComponent oNext = getNieghborInputLine(this.activeComponent, true);
			
			if(oNext != null)
				this.activeComponent = oNext;							
			
			return this;
		}

		if( this.activeComponent != null){
			
			int lineFocus =  this.activeComponent.signal(oMessage);
			
			if(lineFocus > 0){
				
				ScreenComponent oNext = getNieghborInputLine(_activeBeanLine, true);
					
				if(oNext != null)
					 this.activeComponent = oNext;							
				
			} else if(lineFocus < 0){
				
				ScreenComponent oPrev = getNieghborInputLine(_activeBeanLine, false);
					
				if(oPrev != null)
					 this.activeComponent = oPrev;
				
			} 
			
			//_currLine = getIndexOfLine( this.activeComponent) -getHeaderCount();
			
		}
	
		*/
		return this;

	}
	
	public ScreenComponent getNieghborInputLine(ScreenComponent target, boolean next) {
		
	ScreenComponent curr = null;
	BeanLine oLine = null;
/*	
	//forwards
	if(next == false){			
		ScreenComponent arr[] = (ScreenComponent[])this.getComponents();
		for(int i = 0; i < arr.length); i++) {
			curr = arr[i];
			
			if(target == curr)
				return oNeighborBeanLine;
			
			if(oLine.hasEntryBean()){
				oNeighborBeanLine = oLine;
				
			}					
		}						
	} else {
	
		for(int i = _oLines.size()-1; i > -1; i--) {
			oLine = (BeanLine)_oLines.get(i);
			
			if(oTarget == oLine)
				return oNeighborBeanLine;
			
			if(oLine.hasEntryBean()){
				oNeighborBeanLine = oLine;
				
			}					
		}	
	}

	return oNeighborBeanLine;
*/
	return null;
}

	/**
	 * are all ScreenComponents complete.
	 * @return
	 */
	public boolean isComplete(){		
		ScreenComponent oObj =null;
		ScreenComponent arr[] = (ScreenComponent[])this.getComponents();		
		for(int i=0; i <arr.length; i++){			
			oObj = (ScreenComponent)arr[i];			
			if(oObj.isComplete()==false)
				return false;			
		}
		return true;
	}

}
