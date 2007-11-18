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

import java.awt.Component;

import org.jdom.Element;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;

/**
 * @author katharina
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EditBoxInputScreen extends StateScreen {

	/**
	 * for tabbing within screen.
	 */
	protected ScreenComponent activeComponent = null;

	protected EditBoxInputScreen outer = null;
	
	protected Signalhander handler= new MoveOnEnter();

	/**
	 * @param oScreenElement
	 */
	public EditBoxInputScreen(Element oScreenElement, MultiContentManager oCMngr) {
		super(oScreenElement, oCMngr);
		this.outer = this;
	}
	
	/**
	 * method called before signal method takes control.
	 * 
	 */
	public void enter(Object msg) {
		super.enter(msg);
		EditBox.parseEditBox(this, elemScreen);

		/* set first screen element to active one and turn of all other cursors */
		Component arr[] = this.getComponents();
		ScreenComponent curr = null;
		for (int i = 0; i < arr.length; i++) {
			curr = (ScreenComponent) arr[i];
			curr.setCursor(false);
			if (curr instanceof EditBox) {
				if (activeComponent == null) {
					activeComponent = curr;
				}
			}
		}
	}

	public void setSignalHandler(Signalhander handler){
		this.handler = handler;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public ScreenInterface signal(BusMessage oMessage) {
		
		if(this.handler != null)
			return this.handler.signal(oMessage);
		else
			return this;
	}

	public ScreenComponent getNieghborEditBox(ScreenComponent target,
			boolean next) {

		ScreenComponent curr = null;
		ScreenComponent ret = null;
		boolean passed = false;
		Component arr[] = this.getComponents();

		// forwards
		if (next == true) {
			for (int i = 0; i < arr.length; i++) {
				curr = (ScreenComponent) arr[i];
				if (target == curr) {
					passed = true;
					continue;
				}
				if (passed && curr instanceof EditBox) {
					ret = curr;
					break;
				}
			}
		} else {
			for (int i = arr.length - 1; i > -1; i--) {
				curr = (ScreenComponent) arr[i];
				if (target == curr) {
					passed = true;
					continue;
				}
				if (passed && curr instanceof EditBox) {
					ret = curr;
					break;
				}
			}
		}

		return ret;
	}

	/**
	 * are all ScreenComponents complete.
	 * 
	 * @return
	 */
	public boolean isScreenComplete() {
		ScreenComponent oObj = null;
		Component arr[] = this.getComponents();
		for (int i = 0; i < arr.length; i++) {
			oObj = (ScreenComponent) arr[i];
			if (oObj.isComplete() == false)
				return false;
		}
		return true;
	}

	abstract class Signalhander {
		public abstract ScreenInterface signal(BusMessage oMessage);
	}

	public class MoveOnEnter extends Signalhander {
		public ScreenInterface signal(BusMessage oMessage) {

			String keyID = oMessage.getButtonEvent().getKeyId();
			String keyAction = oMessage.getButtonEvent().getAction();

			if (keyAction.equals(RELEASED))
				return outer;

			if (FK_CLR.equals(keyID)) {
				MultiContentManager oMCmgr = getInstanceContext()
						.getContentManager();
				String action = getAction(keyID);
				if (action != null)
					return oMCmgr
							.getScreenContent(action, getInstanceContext());
				else
					return null;
			}

			if (FK_ENT.equals(keyID)) {
				if (isScreenComplete()) {
					MultiContentManager oMCmgr = getInstanceContext()
							.getContentManager();
					String action = getAction(keyID);
					if (action != null)
						return oMCmgr.getScreenContent(action,
								getInstanceContext());
					else
						return outer;
				}
			}

			if (MV_LEFT.equals(keyID) && activeComponent != null) {
				if (activeComponent.getCursorPos() == 0) {
					ScreenComponent prev = getNieghborEditBox(activeComponent,
							false);
					if (prev != null) {
						activeComponent.setCursor(false);
						activeComponent = prev;
						activeComponent.setCursor(true);
						return outer;
					}
				}

			} else if ((MV_RIGHT.equals(keyID) || FK_ENT.equals(keyID))
					&& activeComponent != null) {
				if (activeComponent.isComplete()) {
					ScreenComponent next = getNieghborEditBox(activeComponent,
							true);
					if (next != null) {
						activeComponent.setCursor(false);
						activeComponent = next;
						activeComponent.setCursor(true);
						return outer;
					}
				}
			}

			if (activeComponent != null) {
				activeComponent.signal(oMessage);
			}

			return outer;
		}
	}
	
	public class MoveWhenComplete extends Signalhander {
		public ScreenInterface signal(BusMessage oMessage) {

			String keyID = oMessage.getButtonEvent().getKeyId();
			String keyAction = oMessage.getButtonEvent().getAction();

			if (keyAction.equals(RELEASED))
				return outer;

			if (FK_CLR.equals(keyID)) {
				MultiContentManager oMCmgr = getInstanceContext()
						.getContentManager();
				String action = getAction(keyID);
				if (action != null)
					return oMCmgr
							.getScreenContent(action, getInstanceContext());
				else
					return null;
			}

			if (FK_ENT.equals(keyID)) {
				if (isScreenComplete()) {
					MultiContentManager oMCmgr = getInstanceContext()
							.getContentManager();
					String action = getAction(keyID);
					if (action != null)
						return oMCmgr.getScreenContent(action,
								getInstanceContext());
					else
						return outer;
				}
			}

			if (MV_LEFT.equals(keyID) && activeComponent != null) {
				if (activeComponent.getCursorPos() == 0) {
					ScreenComponent prev = getNieghborEditBox(activeComponent,
							false);
					if (prev != null) {
						activeComponent.setCursor(false);
						activeComponent = prev;
						activeComponent.setCursor(true);
						return outer;
					}
				}

			} else if ((MV_RIGHT.equals(keyID) || FK_ENT.equals(keyID))
					&& activeComponent != null) {
				if (activeComponent.isComplete()) {
					ScreenComponent next = getNieghborEditBox(activeComponent,
							true);
					if (next != null) {
						activeComponent.setCursor(false);
						activeComponent = next;
						activeComponent.setCursor(true);
						return outer;
					}
				}
			}

			if (activeComponent != null) {
				activeComponent.signal(oMessage);
				
				if(activeComponent.isComplete()){
					ScreenComponent next = getNieghborEditBox(activeComponent,
							true);
					if (next != null) {
						activeComponent.setCursor(false);
						activeComponent = next;
						activeComponent.setCursor(true);
						return outer;
					}
					
				}
			}

			return outer;
		}
	}


}
