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

package net.sourceforge.dscsim.controller.display.screens.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JEditBox;
import net.sourceforge.dscsim.controller.display.screens.framework.JMenu;
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;
import net.sourceforge.dscsim.controller.panels.ActionMapping;
import net.sourceforge.dscsim.controller.panels.Screen;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.infostore.InfoStoreType;
import net.sourceforge.dscsim.controller.infostore.Position;
import net.sourceforge.dscsim.controller.infostore.Position.LatitudeType;
import net.sourceforge.dscsim.controller.infostore.Position.LongitudeType;
import net.sourceforge.dscsim.controller.infostore.impl.InfoStoreTypeImpl;

/**
 * @author katharina
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class JPositionEntryScreen extends JEditBoxInputScreen {

	private JEditBox ebLatDeg = null;
	private JEditBox ebLatMin = null;
	private JEditBox ebLatHem = null;
	private JTextBox ebLatNull = null;

	private JEditBox ebLonDeg = null;
	private JEditBox ebLonMin = null;
	private JEditBox ebLonHem = null;
	private JTextBox ebLonNull = null;

	public JPositionEntryScreen(JDisplay display, Screen screen) {
		super(display, screen);

		this.setSignalHandler(new MoveOnTabCtrl());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#enter()
	 */
	public void enter(Object msg) {
		super.enter(msg);

		//set respective local member.
		ebLatDeg = (JEditBox) this.getComponentByName("latdeg", 0);
		ebLatDeg.setValidator(new JEditBox.PositionValidator()
				.setMode(JEditBox.PositionValidator.MODE.LAT));
		this.setForceRefresh(true);

		ebLatMin = (JEditBox) this.getComponentByName("latmin", 0);
		ebLatMin.setValidator(new JEditBox.PositionValidator()
				.setMode(JEditBox.PositionValidator.MODE.MIN));
		this.setForceRefresh(true);
		this.ebLatHem = (JEditBox) this.getComponentByName("lathem", 0);
		this.ebLatHem.setMode(JEditBox.Mode.Pick);
		ArrayList<String>m=new ArrayList<String>();
		ArrayList<String>p = new ArrayList<String>();
		m.add(KP_6);
		m.add(KP_7);
		p.add("N");
		p.add("S");
		this.ebLatHem.setPickList(p, m);
		this.ebLatHem.setValidator(new JEditBox.PickListValidator());
		
		ebLatNull = (JTextBox) this.getComponentByName("latnull", 0);

		ebLonDeg = (JEditBox) this.getComponentByName("londeg", 0);
		ebLonDeg.setValidator(new JEditBox.PositionValidator()
				.setMode(JEditBox.PositionValidator.MODE.LON));
		this.setForceRefresh(true);

		ebLonMin = (JEditBox) this.getComponentByName("lonmin", 0);
		ebLonMin.setValidator(new JEditBox.PositionValidator()
				.setMode(JEditBox.PositionValidator.MODE.MIN));
		this.setForceRefresh(true);
		
		this.ebLonHem = (JEditBox) this.getComponentByName("lonhem", 0);
		
		this.ebLonHem.setMode(JEditBox.Mode.Pick);
		p = new ArrayList<String>();
		m=new ArrayList<String>();
		m.add(KP_3);
		m.add(KP_9);
		p.add("E");
		p.add("W");
		this.ebLonHem.setPickList(p, m);
		
		
		this.ebLonHem.setValidator(new JEditBox.PickListValidator());

		ebLonNull = (JTextBox) this.getComponentByName("lonnull", 0);

		//retrieve existing values if they exist.
		MultiContentManager oMngr = getInstanceContext().getContentManager();
		Position position = oMngr.getInfoStore().getPosition();

		LatitudeType lat = position.getLatitude();
		LongitudeType lon = position.getLongitude();

		this.ebLatDeg.setValue(lat.getDegrees());
		this.ebLatMin.setValue(lat.getMinutes());
		this.ebLatHem.setValue(lat.getHemisphere());

		this.ebLonDeg.setValue(lon.getDegrees());
		this.ebLonMin.setValue(lon.getMinutes());
		this.ebLonHem.setValue(lon.getHemisphere());

		//position cursur.
		this.activeComponent = ebLatDeg;

		if (ebLatMin.isComplete())
			this.activeComponent = ebLatHem;
		
		if (ebLonDeg.isComplete())
			this.activeComponent = ebLonDeg;
		
		if (ebLonMin.isComplete())
			this.activeComponent = ebLonHem;
		
		this.activeComponent.setCursor(true);

		setNullFields();

	}

	private void setNullFields() {

		if (ebLatDeg.getValue().length() < 1
				&& ebLatMin.getValue().length() < 1) {
			this.ebLatNull.setText("Null");
		} else {
			this.ebLatNull.setText("");
		}
		if (ebLonDeg.getValue().length() < 1
				&& ebLonMin.getValue().length() < 1) {
			this.ebLonNull.setText("Null");
		} else {
			this.ebLonNull.setText("");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.dscsim.common.display.textscreen.State#exit()
	 */
	public void exit(BusMessage msg) throws Exception {

		if (msg.getButtonEvent().getKeyId().equals(FK_ENT)) {
			MultiContentManager oMCmgr = getInstanceContext()
					.getContentManager();

			Position position = oMCmgr.getInfoStore().getPosition();

			LatitudeType lat = position.getLatitude();
			LongitudeType lon = position.getLongitude();

			lat.setDegrees(this.ebLatDeg.getValue());
			lat.setMinutes(this.ebLatMin.getValue());
			lat.setHemisphere(this.ebLatHem.getValue());

			lon.setDegrees(this.ebLonDeg.getValue());
			lon.setMinutes(this.ebLonMin.getValue());
			lon.setHemisphere(this.ebLonHem.getValue());

			oMCmgr.persistInfoStore();
		}

	}

	public ActionMapping notify(BusMessage msg) {
		ActionMapping ret = super.notify(msg);
		this.setNullFields();

		if (ret != null)
			return ret;

		String keyID = msg.getButtonEvent().getKeyId();
		String keyAction = msg.getButtonEvent().getAction();

		if (keyAction.equals(RELEASED) && KP_Aa.equals(keyID)) {

			MultiContentManager oMngr = getInstanceContext()
					.getContentManager();
			Properties props = oMngr.getProperties();

			this.ebLatDeg.setValue(BLANK);
			this.ebLatMin.setValue(BLANK);
			this.ebLatNull.setText(props.getProperty(NULL));

			this.ebLonDeg.setValue(BLANK);
			this.ebLonMin.setValue(BLANK);
			this.ebLonNull.setText(props.getProperty(NULL));

			this.activeComponent = this.ebLatDeg;
			this.ebLatDeg.setCursor(true);
			this.ebLatMin.setCursor(false);
			this.ebLatHem.setCursor(false);
			this.ebLonDeg.setCursor(false);
			this.ebLonMin.setCursor(false);
			this.ebLonHem.setCursor(false);
			
			return null;
		}

		if (keyAction.equals(PRESSED) && FK_ENT.equals(keyID)) {
			if (this.ebLatDeg.getValue().length()==0
					&& this.ebLatMin.getValue().length()==0
					&& this.ebLonDeg.getValue().length()==0
					&& this.ebLonMin.getValue().length()==0) {
				return findActionMapping(keyAction, keyID);
			}
		}

		return null;

	}

}
