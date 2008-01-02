/*
 * Created on 18.12.2007
 * penn
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

import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;
import net.sourceforge.dscsim.controller.panels.Screen;
import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.infostore.Position.TimeType;
import net.sourceforge.dscsim.controller.infostore.Position;
import net.sourceforge.dscsim.controller.infostore.Position.LatitudeType;
import net.sourceforge.dscsim.controller.infostore.Position.LongitudeType;
import net.sourceforge.dscsim.controller.Constants;

import java.util.Properties;

public class JMainScreen extends ActionScreen
	implements Constants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8298589430293128416L;

	private JTextBox tbSource = null;
	private JTextBox tbTime = null;
	private JTextBox tbLat = null;
	private JTextBox tbLon = null;

	public JMainScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}

	public void enter(Object msg) {		
		this.tbSource = (JTextBox) this.getComponentByName("source", 0);
		this.tbTime = (JTextBox) this.getComponentByName("time", 0);
		this.tbLat = (JTextBox) this.getComponentByName("lat", 0);
		this.tbLon = (JTextBox) this.getComponentByName("lon", 0);
		
		MultiContentManager oMngr = getInstanceContext().getContentManager();		
		Properties props = oMngr.getProperties();
		
		this.tbSource.setText(props.getProperty("MS_SOURCE_GPS"));
		
		Position pos = oMngr.getInfoStore().getPosition();
		TimeType time = pos.getTime();
		
		if(time.getHours().length()<1){
			this.tbTime.setText(props.getProperty("MS_TIME_NON"));
		} else{
			this.tbTime.setText(props.getProperty("MS_TIME_PREF") + time.getHours() + ":" + time.getMinutes());
		}
		
		LatitudeType lat = pos.getLatitude();
		LongitudeType lon = pos.getLongitude();
		
		if (lat.getDegrees().length() < 1) {
			this.tbLat.setText(props.getProperty("MS_POS_NON"));
			this.tbLon.setText("");
		} else {
			this.tbLat.setText(props.getProperty("MS_LAT_PREF")
					+ lat.getDegrees() + props.getProperty("DEGREE_SYMBOL")
					+ lat.getMinutes() + props.getProperty("MINUTE_SYMBOL")
					+ lat.getHemisphere());
			this.tbLon.setText(props.getProperty("MS_LON_PREF")
					+ lon.getDegrees() + props.getProperty("DEGREE_SYMBOL")
					+ lon.getMinutes() + props.getProperty("MINUTE_SYMBOL")
					+ lon.getHemisphere());

		}
		

	
	}

}
