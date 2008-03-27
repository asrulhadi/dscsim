/*
 * Created on 03.01.2007
 * 
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
 * the Initial Developer are Copyright (C) 2006, 2007, 2008, 20010.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
 
package net.sourceforge.dscsim.station;

import java.awt.Toolkit;
import java.awt.geom.Dimension2D;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JProgressBar;


import net.sourceforge.dscsim.common.SplashScreen;
import net.sourceforge.dscsim.controller.ApplicationContext;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.BasicControllerFrame;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.radio.core.impl.SimpleRadioGUI;
import net.sourceforge.dscsim.status.SimulationInfoFrame;
import net.sourceforge.dscsim.util.ConfigReader;
import net.sourceforge.dscsim.controller.message.types.MMSI;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimpleStation implements Constants, ApplicationContext {
		
	
	public void init(String args[]) throws Exception {
		
	    Properties properties = System.getProperties();

	    if(args != null){	    			
			if(args.length > 0 && args[0].startsWith(KEY_STARTUP_FILE)){
				ConfigReader.parseCfg(args, properties, true);   		
			}else{
				ConfigReader.parseArgs(args, properties);
			}
	    }
	   
		//create instance of radio	
	    //should be ok because gui initialization is done in init and therefore
	    //by the event threat.
	    
	    final ApplicationContext appCtx = this;
		final SimpleRadioGUI radio = new SimpleRadioGUI();
	    // set the argument to true for ship station, false for shore station
		radio.startCore(!isCoastal()); // initializes the radio core
		radio.setTitle(appCtx.getIndividualMmsi() + MultiContentManager.getProperties().getProperty("FRAME_TITLE_RADIO"));
		radio.setIconImage(SplashScreen.getInstance().getImage());
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				radio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				radio.startCore();
				radio.init();
                radio.updateGUIElements();
				//frame.pack();
				//radio.setVisible(true);				
			}
		});
		
		
		//create instance of controller
		final BasicControllerFrame controller = new BasicControllerFrame(appCtx, radio.getRadioCore());
		controller.setTitle(appCtx.getIndividualMmsi() + MultiContentManager.getProperties().getProperty("FRAME_TITLE_CNTRL") );
		controller.setIconImage(SplashScreen.getInstance().getImage());
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				controller.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
				controller.init();
				//controller.setVisible(true);			
			}
		});
	
		//create instance of info frame
		final SimulationInfoFrame infoFrame = new SimulationInfoFrame(appCtx);
		infoFrame.setTitle(appCtx.getIndividualMmsi() + MultiContentManager.getProperties().getProperty("FRAME_TITLE_INFO"));
		infoFrame.setIconImage(SplashScreen.getInstance().getImage());
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				infoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
				infoFrame.init();
				//controller.setVisible(true);			
			}
		});
        
		/*looks better when the windows are shown once they can respond*/
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				radio.setVisible(true);
				controller.setVisible(true);	
				infoFrame.setVisible(true);
			}
		});
	}
	
	public SimpleStation(){
	
	}
	
    public void start() {
       
    }
	
    
    public void stop() {
     
    }
    
    
	public static void main(String[] args) throws Exception {
					
		Dimension2D oDim = Toolkit.getDefaultToolkit().getScreenSize();
		
		SimpleStation app = new SimpleStation();		
		//MainFrame oFrame = new MainFrame(app, args, (int)oDim.getWidth()*43/64, (int)oDim.getHeight()*45/64);		
		app.init(args);
		
	}
	
	
	private String getParameter(String key){
		return System.getProperty("parameter." + key.toLowerCase());
	}

	public String getMasterIPAddress() {

		String strParam = getParameter(KEY_MASTER_IPADDRESS);
		
		if(strParam == null || strParam.length() < 0)
			strParam = MASTER_IPADDRESS;
		
		return strParam;
	}


	public String getMasterPort() {
		String strParam = getParameter(KEY_MASTER_PORT);
		
		if(strParam == null || strParam.length() < 0)
			strParam = MASTER_PORT;
		
		return strParam;

	}
	


	public String getProviderUser() {
		String strParam = getParameter(KEY_PROVIDER_USER);
		
		if(strParam == null)
			strParam = "anonymous";
				
		return strParam;

	}
	
	public String getProviderSubject() {
		String strParam = getParameter(KEY_PROVIDER_SUBJECT);
		
		if(strParam == null)
			strParam = "TOOL.DEFAULT";
				
		return strParam;

	}
	public String getProviderUrl() {
		String strParam = getParameter(KEY_PROVIDER_URL);
		
		if(strParam == null)
			strParam = "tcp://localhost:61616";
				
		return strParam;

	}
	
	public String getProviderPassword() {
		String strParam = getParameter(KEY_PROVIDER_PWD);
		
		if(strParam == null)
			strParam = "anonymous";
				
		return strParam;

	}


	public String getIACMethod() {
		String strParam = getParameter(KEY_IAC);
		
		if(strParam == null)
			strParam = IAC_AIRWAVE;
				
		return strParam;
		
	}

	public String getMode() {
		String strParam = getParameter(KEY_OP_MODE);
		
		//stand alone and slave are only allowed.
		if(strParam == null || OP_MODE_MASTER.equals(strParam))
			strParam = OP_MODE_STANDALONE;
		
				
		return strParam;

	}

	public String getIACSync() {
		String strParam = getParameter(KEY_IAC_SYNC);
		
		if(strParam == null)
			strParam = IACS_TCP;
				
		return strParam;
	}

	/* (non-Javadoc)
	 * @see applet.ApplicationContext#getIndividualMmsi()
	 */
	public String getIndividualMmsi() {
		
		String strParam = getParameter(KEY_INDIVIDUAL_MMSI);
		
		if(strParam == null)
			strParam = "211000000";
				
		return strParam;
	}
	
	/* (non-Javadoc)
	 * @see applet.ApplicationContext#getGroupMmsi()
	 */
	public String getGroupMmsi() {
		
		String strParam = getParameter(KEY_GROUP_MMSI);
		
		if(strParam == null)
			strParam = "021100000";
				
		return strParam;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.ApplicationContext#getStationScreens()
	 */
	public String getScreenFileName() {		
		String strParam = getParameter(KEY_SCREEN_FILE);		
		if(strParam == null || strParam.length()==0){						
			MMSI mmsi = new MMSI(getIndividualMmsi());			
			if(mmsi.isCoastal()){
				strParam = DEVICE_SHORE_XML;
			}else{
				strParam = DEVICE_SHIP_XML;
			}			
		}
				
		return strParam;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.ApplicationContext#getStationScreens()
	 */
	public String getDeviceXmlName() {		
		String strParam = getParameter(KEY_DEVICE_FILE);		
		if(strParam == null || strParam.length()==0){						
			MMSI mmsi = new MMSI(getIndividualMmsi());			
			if(mmsi.isCoastal()){
				strParam = DEVICE_SHORE_XML;
			}else{
				strParam = DEVICE_SHIP_XML;
			}			
		}
				
		return strParam;
	}
	
	public String getUdpAirWave() {
		
		String strParam = getParameter(KEY_UDP_AIRWAVE);
		
		if(strParam == null || strParam.length()==0)
			// ohecker: default is empty. localhost is not default			
//			strParam = "localhost";
			strParam = "";
				
		return strParam;
	}
	/**
	 * 
	 * @return true if mmsi is a coastal number. 
	 */
	
	public boolean isCoastal(){
		MMSI mmsi = new MMSI(getIndividualMmsi());		
		if(mmsi.isCoastal()){
			return true;
		}else{
			return false;
		}
	}	  
	  
}

