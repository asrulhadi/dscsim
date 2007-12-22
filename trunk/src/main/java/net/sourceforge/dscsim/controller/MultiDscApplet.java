/*
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
package net.sourceforge.dscsim.controller;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Dimension2D;
import java.util.HashMap;

import javax.swing.JApplet;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;

import net.sourceforge.dscsim.controller.network.DscIACManager;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.network.InternalBusListener;
import net.sourceforge.dscsim.controller.network.MulticastReceiver;
import net.sourceforge.dscsim.controller.network.SyncListenerDispatcher;
import net.sourceforge.dscsim.controller.network.SyncListenerSubscriber;
import net.sourceforge.dscsim.controller.network.SyncMessage;
import net.sourceforge.dscsim.controller.screen.types.MMSI;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.utils.AppletSoundList;





class DscMainPanel extends JPanel implements  ItemListener, SyncListenerSubscriber {
	
	private HashMap _Mmsi2PanelMap =new HashMap();
	
	public DscMainPanel(){
		
		//setLayout(new BorderLayout());
		setBorder(new EtchedBorder());
	    add(createMenuBar(), BorderLayout.NORTH);
    
	    JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("serif", Font.PLAIN, 12));

        String strMMSI = "211001602";
        DscAppPanel oAppPanel = new DscAppPanel(strMMSI, MultiDscApplet._oInstance);
        oAppPanel.init(MultiDscApplet._oInstance);
        tabbedPane.add(oAppPanel);
        _Mmsi2PanelMap.put(strMMSI, oAppPanel);
 
        strMMSI = "002110124";
        oAppPanel = new DscAppPanel(strMMSI, MultiDscApplet._oInstance);
        oAppPanel.init(MultiDscApplet._oInstance);
        tabbedPane.add(oAppPanel);
        _Mmsi2PanelMap.put(strMMSI, oAppPanel);
 
        
        //add panel collection to main.
        add(tabbedPane, BorderLayout.CENTER);
        
        DscIACManager.initSyncListener(oAppPanel);
        DscIACManager.getSyncListener().addSubscriber(this);
        
        //SyncListenerDispatcher.getInstance().addSubscriber(this);

        String strIAC = MultiDscApplet._oInstance.getIACMethod();
        DscIACManager.initIAC(oAppPanel, strIAC, oAppPanel);

	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		AppLogger.debug("DscMainPanel.itemStateChange called.");
		
	}
	
    private JMenuBar createMenuBar() {

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        JMenuBar menuBar = new JMenuBar();


        JMenu options = (JMenu) menuBar.add(new JMenu("Options"));

        return menuBar;
    }
    
	/* (non-Javadoc)
	 * @see network.SyncListenerSubscriber#notifySycMessage(network.SyncMessage)
	 */
	public void notifySycMessage(SyncMessage oMessage) {
		// TODO Auto-generated method stub
		
		String incomingMmsi = oMessage.getMMSI();
		
		DscAppPanel oPanel = (DscAppPanel)_Mmsi2PanelMap.get(incomingMmsi);
		
		if(oPanel != null){
			
			 oPanel.getBus().publish((BusMessage)oMessage.getBusMessage());		 
			 
		}
		
		
		
	}
	
}


public class MultiDscApplet extends JApplet implements Constants, ApplicationContext {

	
	public static MultiDscApplet _oInstance = null; 
	
	public void init() {

		AppLogger.debug("applet.MultiDscApplet initiating AppletSoundList");	
		DscIACManager.initSyncPublisher(this);
		AppletSoundList.createSingleton(getCodeBase());

		DscMainPanel oMainPanel = new DscMainPanel();
		//getContentPane().setLayout(new BorderLayout());
		getContentPane().add(oMainPanel, BorderLayout.CENTER);
	

	}
	
	public MultiDscApplet(){
		_oInstance = this;
	}
	
    public void start() {
       
    }
	
    
    public void stop() {
     
    }
    
    
	public static void main(String[] args){
		
		
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
	
	
	public String getProviderUrl() {
		String strParam = getParameter(KEY_PROVIDER_URL);
				
		return strParam;

	}
	public String getProviderUser() {
		String strParam = getParameter(KEY_PROVIDER_USER);
				
		return strParam;

	}
	
	public String getProviderSubject() {
		String strParam = getParameter(KEY_PROVIDER_SUBJECT);
				
		return strParam;

	}
	
	public String getProviderPassword() {
		String strParam = getParameter(KEY_PROVIDER_PWD);
				
		return strParam;

	}
	public static ApplicationContext getApplicationContext(){
		return _oInstance;
		
	}

	/* (non-Javadoc)
	 * @see applet.ApplicationContext#getIACMethod()
	 */
	public String getIACMethod() {
		String strParam = getParameter(KEY_IAC);
		
		if(strParam == null)
			strParam = IAC_UDP;
				
		return strParam;
	}

	public String getMode() {
		
		String strParam = getParameter(KEY_OP_MODE);
		
		//stand alone and MASTER are only allowed values.
		if(strParam == null || OP_MODE_SLAVE.equals(strParam))
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
			strParam = "00000000";
				
		return strParam;
	}
	
	/* (non-Javadoc)
	 * @see applet.ApplicationContext#getGroupMmsi()
	 */
	public String getGroupMmsi() {
		
		String strParam = getParameter(KEY_GROUP_MMSI);
		
		if(strParam == null)
			strParam = "00000000";
				
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
	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.ApplicationContext#getStationScreens()
	 */
	public String getScreenFileName() {
		
		String strParam = getParameter(KEY_SCREEN_FILE);
		
		if(strParam == null || strParam.length()==0)
			strParam = SCREEN_FILE;
				
		return strParam;
	}

	public String getUdpAirWave() {
		
		String strParam = getParameter(KEY_UDP_AIRWAVE);
		
		if(strParam == null || strParam.length()==0)
			strParam = "localhost";
				
		return strParam;
	}	
}
