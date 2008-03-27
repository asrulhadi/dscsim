/*
 * Created on 29.06.2006
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
import java.util.Properties;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.MultiBeeper;
import net.sourceforge.dscsim.controller.MultiBus;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.message.types.Position;
import net.sourceforge.dscsim.controller.message.types.Latitude;
import net.sourceforge.dscsim.controller.message.types.Longitude;
import net.sourceforge.dscsim.controller.message.types.Time;
import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreenComponent;
import net.sourceforge.dscsim.controller.display.screens.framework.JTextBox;
import net.sourceforge.dscsim.controller.screens.ActionMapping;
import net.sourceforge.dscsim.controller.screens.Screen;
import net.sourceforge.dscsim.controller.utils.AppLogger;



/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SendDistressReviewScreen extends ActionScreen {

	private int sentAlerts =0;
	private  boolean bContinue = true;
	private ActionScreen screenWaiting = null;
	private String lstChannel = null;
	
	private JTextBox tbNature = null;
	private JTextBox tbSource = null;	
	private JTextBox tbTime = null;
	private JTextBox tbLat = null;
	private JTextBox tbLon = null;


	public SendDistressReviewScreen(JDisplay display, Screen screen) {
		super(display, screen);
	}
	
	@Override
	public void enter(Object msg){

		this.tbNature = (JTextBox) this.getComponentByName("nature", 0);
		this.tbSource = (JTextBox) this.getComponentByName("source", 0);
		this.tbTime = (JTextBox) this.getComponentByName("time", 0);
		this.tbLat = (JTextBox) this.getComponentByName("latitude", 0);
		this.tbLon = (JTextBox) this.getComponentByName("longitude", 0);
		
		MultiContentManager oMngr = getInstanceContext().getContentManager();		
		Properties props = oMngr.getProperties();		
		this.tbSource.setText(props.getProperty("MS_SOURCE_GPS"));

		String nature = oMngr.getInfoStore().getNature();
		this.tbNature.setText(props.getProperty(nature));
		
		Position pos = oMngr.getInfoStore().getPosition();
		Time time = pos.getTime();
		
		if(!time.hasValue()){
			this.tbTime.setText(props.getProperty("MS_TIME_NON"));
		} else{
			this.tbTime.setText(props.getProperty("MS_TIME_PREF") + time.getHours() + ":" + time.getMinutes());
		}
		
		Latitude lat = pos.getLatitude();
		Longitude lon = pos.getLongitude();
		
		if (!lat.hasValue()) {
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
		
		this.init();
	}

	public void exit(BusMessage message) throws Exception {
		super.exit(message);
			
	}

	
}

