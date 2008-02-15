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

package net.sourceforge.dscsim.controller.display.screens.impl;

import java.awt.Component;


import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JEditBox;
import net.sourceforge.dscsim.controller.display.screens.framework.JMenu;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreenComponent;
import net.sourceforge.dscsim.controller.screens.ActionMapping;
import net.sourceforge.dscsim.controller.screens.Screen;
/**
 * @author katharina
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class JEditBoxInputScreen extends ActionScreen {

	/**
	 * for tabbing within screen.
	 */
	protected JScreenComponent activeComponent = null;

	protected JEditBoxInputScreen outer = null;
	
	protected Signalhander handler= new MoveOnTabCtrl();

	/**
	 * @param oScreenElement
	 */
	public JEditBoxInputScreen(JDisplay display, Screen screen) {
		super(display, screen);
		this.outer = this;
	}
	
	/**
	 * method called before signal method takes control.
	 * 
	 */
	public void enter(Object msg) {
		super.enter(msg);

		/* set first screen element to active one and turn of all other cursors */
		Component arr[] = this.getComponents();
		JScreenComponent curr = null;
		for (int i = 0; i < arr.length; i++) {
			curr = (JScreenComponent) arr[i];
			curr.setCursor(false);
			if (isTabable(curr)) {
				if (activeComponent == null) {
					activeComponent = curr;
				}
			}
		}
	}
	
	private boolean isTabable(JScreenComponent c){
		if(c instanceof JEditBox
				|| c instanceof JMenu)
			return true;
		else
			return false;
	}

	public void setSignalHandler(Signalhander handler){
		this.handler = handler;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public ActionMapping notify(BusMessage oMessage) {
		
		if(this.handler != null)
			return this.handler.notify(oMessage);
		else
			return null;
	}

	public JScreenComponent getNieghborEditBox(JScreenComponent target,
			boolean next) {

		JScreenComponent curr = null;
		JScreenComponent ret = null;
		boolean passed = false;
		Component arr[] = this.getComponents();

		// forwards
		if (next == true) {
			for (int i = 0; i < arr.length; i++) {
				curr = (JScreenComponent) arr[i];
				if (target == curr) {
					passed = true;
					continue;
				}
				if (passed && isTabable(curr)) {
					ret = curr;
					break;
				}
			}
		} else {
			for (int i = arr.length - 1; i > -1; i--) {
				curr = (JScreenComponent) arr[i];
				if (target == curr) {
					passed = true;
					continue;
				}
				if (passed && isTabable(curr)) {
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
		JScreenComponent oObj = null;
		Component arr[] = this.getComponents();
		for (int i = 0; i < arr.length; i++) {
			oObj = (JScreenComponent) arr[i];
			if (oObj.isComplete() == false)
				return false;
		}
		return true;
	}

	abstract class Signalhander implements net.sourceforge.dscsim.controller.Constants {
		public abstract ActionMapping notify(BusMessage oMessage);
	}

	public class MoveOnEnter extends Signalhander {
		public ActionMapping notify(BusMessage oMessage) {

			String keyID = oMessage.getButtonEvent().getKeyId();
			String keyAction = oMessage.getButtonEvent().getAction();

			if (keyAction.equals(RELEASED))
				return null;

			if (FK_CLR.equals(keyID)) {
				return outer.findActionMapping(keyAction, keyID);
			}

			if (FK_ENT.equals(keyID)) {
				if (isScreenComplete()) {
					return outer.findActionMapping(keyAction, keyID);
				}
			}

			if (MV_LEFT.equals(keyID) && activeComponent != null) {
				if (activeComponent.getCursorPos() == 0) {
					JScreenComponent prev = getNieghborEditBox(activeComponent,
							false);
					if (prev != null) {
						activeComponent.setCursor(false);
						activeComponent = prev;
						activeComponent.setCursor(true);
						return null;
					}
				}

			} else if ((MV_RIGHT.equals(keyID) || FK_ENT.equals(keyID))
					&& activeComponent != null) {
				if (activeComponent.isComplete()) {
					JScreenComponent next = getNieghborEditBox(activeComponent,
							true);
					if (next != null) {
						activeComponent.setCursor(false);
						activeComponent = next;
						activeComponent.setCursor(true);
						return null;
					}
				}
			}

			if (activeComponent != null) {
				activeComponent.signal(oMessage);
			}

			return null;
		}
	}
	
	public class MoveWhenComplete extends Signalhander {
		public ActionMapping notify(BusMessage oMessage) {

			String keyID = oMessage.getButtonEvent().getKeyId();
			String keyAction = oMessage.getButtonEvent().getAction();

			if (keyAction.equals(RELEASED))
				return null;

			if (FK_CLR.equals(keyID)) {
				return outer.findActionMapping(keyAction, keyID);
			}

			if (FK_ENT.equals(keyID)) {
				if (isScreenComplete()) {
					return outer.findActionMapping(keyAction, keyID);
				}
			}

			if (MV_LEFT.equals(keyID) && activeComponent != null) {
				if (activeComponent.getCursorPos() == 0) {
					JScreenComponent prev = getNieghborEditBox(activeComponent,
							false);
					if (prev != null) {
						activeComponent.setCursor(false);
						activeComponent = prev;
						activeComponent.setCursor(true);
						return null;
					}
				}

			} else if ((MV_RIGHT.equals(keyID) || FK_ENT.equals(keyID))
					&& activeComponent != null) {
				if (activeComponent.isComplete()) {
					JScreenComponent next = getNieghborEditBox(activeComponent,
							true);
					if (next != null) {
						activeComponent.setCursor(false);
						activeComponent = next;
						activeComponent.setCursor(true);
						return null;
					}
				}
			}

			if (activeComponent != null) {
				activeComponent.signal(oMessage);
				
				if(activeComponent.isComplete()){
					JScreenComponent next = getNieghborEditBox(activeComponent,
							true);
					if (next != null) {
						activeComponent.setCursor(false);
						activeComponent = next;
						activeComponent.setCursor(true);
						return null;
					}
					
				}
			}

			return null;
		}
	}

	public class MoveOnTabCtrl extends Signalhander {
		public ActionMapping notify(BusMessage oMessage) {

			String keyID = oMessage.getButtonEvent().getKeyId();
			String keyAction = oMessage.getButtonEvent().getAction();

			if (keyAction.equals(RELEASED))
				return null;

			if (FK_CLR.equals(keyID)) {
				return outer.findActionMapping(keyAction, keyID);
			}

			if (FK_ENT.equals(keyID)) {
				if (isScreenComplete()) {
					return outer.findActionMapping(keyAction, keyID);
				}
			}

			if (MV_LEFT.equals(keyID) && activeComponent != null) {
				if (activeComponent.getCursorPos() == 0) {
					JScreenComponent prev = getNieghborEditBox(activeComponent,
							false);
					if (prev != null) {
						activeComponent.onFocusLose();
						prev.onFocusGain();
						activeComponent = prev;
						return null;
					}
				}

			} else if ((MV_RIGHT.equals(keyID))
					&& activeComponent != null) {
				if (activeComponent.isComplete()
						&& activeComponent.tab(oMessage)) {
					JScreenComponent next = getNieghborEditBox(activeComponent,
							true);
					
					if (next != null) {
						activeComponent.onFocusLose();
						next.onFocusGain();
						activeComponent = next;
						return null;
					}
				}
			}

			if (activeComponent != null) {
				activeComponent.signal(oMessage);
				
				if(activeComponent.isComplete()
						&& activeComponent.tab(oMessage)){
					JScreenComponent next = getNieghborEditBox(activeComponent,
							true);
					if (next != null) {
						activeComponent.onFocusLose();
						next.onFocusGain();
						activeComponent = next;
						return null;
					}
					
				}
			}

			return null;
		}
	}

}
