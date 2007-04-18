/*
 * Created on 13.04.2007
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

import org.jdom.Attribute;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.network.DscMessage;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ScreenInterface {
	public abstract DscMessage getOutGoingDscMessage();

	public abstract void setOutGoingDscMessage(DscMessage oDscMessage);

	public abstract DscMessage getIncomingDscMessage();

	public abstract void setIncomingDscMessage(DscMessage oDscMessage);

	public abstract void setInstanceContext(InstanceContext oCtx);

	public abstract InstanceContext getInstanceContext();

	public abstract ScreenInterface signal(BusMessage oMessage);

	public abstract void init();

	//state enter exit routines
	public abstract void exit(BusMessage oMessage) throws Exception;

	public abstract void enter(Object arg0);

	public abstract void setParent(ScreenContent oParent);
	
	public abstract String getAttributeValue(String attrName);
	
	public boolean forceRefresh();
	
}