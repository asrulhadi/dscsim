/*
 * Created on 04.03.2007
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


import java.awt.Component;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.network.DscMessage;

/**
 * Base State class for state handling.
 * @author katharina
* 
 */
public abstract class StateScreen extends Screen implements ScreenInterface, Constants {
	/*
	 * xml screen descr
	 */
	protected Element elemScreen = null;
	
	/*
	 * 
	 */
	private DscMessage outGoing = null;

	/*
	 * 
	 */
	private boolean forceRefresh = false;

	/*
	 * reference to Context.
	 */	
	private MultiContentManager contentMngr = null;
	/**
	 * @param oScreenElement
	 */
	public StateScreen(Element elemScreen,  MultiContentManager contentMngr) {
		super(contentMngr.getInstanceContext().getController().getScreenX(), contentMngr.getInstanceContext().getController().getScreenX(),  210, 160, 8, 21);
		this.elemScreen = elemScreen;
		this.contentMngr = contentMngr;
	}

	/**
	 * method called before signal method takes control.
	 *
	 */
	public void enter(Object msg){
		/*parse fixed text elements*/
		TextBox.parseTextBox(this, this.elemScreen);
	}
	
	/**
	 * method to handle bus messages.
	 */
	public abstract ScreenInterface signal(BusMessage msg);

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.screen.ScreenInterface#getOutGoingDscMessage()
	 */
	public DscMessage getOutGoingDscMessage() {
		return outGoing;
	}

	/**
	 * set message used during outgoing processing.
	 */
	public void setOutGoingDscMessage(DscMessage oDscMessage) {
		this.outGoing = oDscMessage;		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.screen.ScreenInterface#getIncomingDscMessage()
	 */
	public DscMessage getIncomingDscMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * get name of screen as define in xmml.
	 */
	public String getAttributeValue(String attrName){
		String attrValue = null;
		Attribute oAttr = this.elemScreen.getAttribute(attrName);	
		attrValue = oAttr != null ? oAttr.getValue() : "";			
		return attrValue;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.screen.ScreenInterface#setIncomingDscMessage(net.sourceforge.dscsim.controller.network.DscMessage)
	 */
	public void setIncomingDscMessage(DscMessage oDscMessage) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.screen.ScreenInterface#setInstanceContext(net.sourceforge.dscsim.controller.InstanceContext)
	 */
	public void setInstanceContext(InstanceContext oCtx) {
		// TODO Auto-generated method stub	
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.screen.ScreenInterface#getInstanceContext()
	 */
	public InstanceContext getInstanceContext() {
		return this.contentMngr.getInstanceContext();
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.screen.ScreenInterface#setParent(net.sourceforge.dscsim.controller.screen.ScreenContent)
	 */
	public void setParent(ScreenContent oParent) {
		// TODO Auto-generated method stub
		
	}	
	

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.common.screen.ScreenInterface#init()
	 */
	public void init() {
	
		
	}


	/**
	 * method called after signal has relenquished control.
	 *
	 */
	public void exit(BusMessage oMessage) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * get a sceen element by its name.
	 * 
	 * @author katharina
	 * 
	 */
	public ScreenComponent getComponentByName(String name, int start){
		ScreenComponent sc = null;
		Component all[] = this.getComponents();
		String targ = null;
		for(int i=0; i<all.length;i++){
			sc = (ScreenComponent)all[i];			
			targ = sc.getComponentName();
			if(i>=start && targ != null && targ.equals(name))
				break;
			else 
				sc = null;
		}
		
		return sc;
		
	}
	
	public String getAction(String eventId){
		
		String rt = null;
		
		if(eventId == null)
			return rt;
		
		Element actions = this.elemScreen.getChild("actions");		
		if(actions == null)
			return rt;

		List list = actions.getChildren();
		Element target = null;
		for(int i=0; list != null && i<list.size(); i++){
			target=(Element)list.get(i);
			if(eventId.equals(target.getAttributeValue("id"))){
				rt = target.getAttributeValue("action");
				break;
			} else{
				target =null;
			}
		}
		
		return rt;
		
	}

	/**
	 * set true for blinking.
	 * @param value
	 * @return
	 */
	public void setForceRefresh(boolean value){
		this.forceRefresh = value;
	}
	
	/**
	 * used for blinking.
	 */
	public boolean forceRefresh(){
		return forceRefresh;
	}
	
}
