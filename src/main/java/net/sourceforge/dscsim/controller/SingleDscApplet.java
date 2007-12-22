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
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
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
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionListener;

import net.sourceforge.dscsim.controller.network.DscIACManager;
import net.sourceforge.dscsim.controller.screen.types.MMSI;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.utils.AppletSoundList;

import javax.swing.JDialog;




class SingleDscMainPanel extends JPanel implements  ItemListener, ActionListener, Constants {
	
	private HashMap _Mmsi2PanelMap =new HashMap();
	private DscAppPanel _oAppPanel = null;
	
	public SingleDscMainPanel(){
		
		//setLayout(new BorderLayout());
		setBorder(new EtchedBorder());
	    add(createMenuBar(), BorderLayout.NORTH);
    
	    JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("serif", Font.PLAIN, 12));
             
		String strMMSI = SingleDscApplet._oInstance.getIndividualMmsi();
        _oAppPanel = new DscAppPanel(strMMSI, SingleDscApplet._oInstance);
        tabbedPane.add(_oAppPanel);
        _Mmsi2PanelMap.put(strMMSI, _oAppPanel);
 
        add(tabbedPane, BorderLayout.CENTER);
        	
        String strIAC = SingleDscApplet._oInstance.getIACMethod();       
        DscIACManager.initIAC(_oAppPanel, strIAC, _oAppPanel);
        DscIACManager.initSyncPublisher(SingleDscApplet._oInstance);

        _oAppPanel.init(SingleDscApplet._oInstance);
        
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

        JMenu oMenu = new JMenu("File");
        JMenu options = (JMenu) menuBar.add(oMenu);
        
        oMenu = new JMenu("Session");
        JMenuItem oMenuItem = new JMenuItem("Network");
        oMenuItem.setActionCommand(MENU_ACT_NETWORK_SETUP);
        oMenuItem.addActionListener(this);        
        oMenu.add(oMenuItem);
       
        oMenuItem = new JMenuItem("Simulation");
        oMenuItem.setActionCommand(MENU_ACT_SIM_SETUP);
        oMenuItem.addActionListener(this);
        
        oMenu.add(oMenuItem);

        
        
        JMenu session = (JMenu) menuBar.add(oMenu);
                  
        return menuBar;
    }
    
	
	
	public void keyReleased(String keyId) {
		
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
		AppLogger.debug2("action command is:" + arg0.getActionCommand());
		
		
		if(MENU_ACT_NETWORK_SETUP.equals(arg0.getActionCommand())){
			SetupNetWorkDialog d = new SetupNetWorkDialog(SingleDscApplet._oInstance.getFrame(), SingleDscApplet._oInstance);			
			d.setModal(true);			
			d.setVisible( true );
		
									
		}else if(MENU_ACT_SIM_SETUP.equals(arg0.getActionCommand())){		
			SetupSimulationDialog dlg = new SetupSimulationDialog(SingleDscApplet._oInstance.getFrame(), getInstanceContext());			
			dlg.setVisible( true );

		}		

	}
	
	public InstanceContext getInstanceContext(){
		return _oAppPanel;
	}
}


public class SingleDscApplet extends JApplet implements Constants, ApplicationContext {

	
	public static SingleDscApplet _oInstance = null; 
	private Frame _frame = null;
	
	public void init() {

		AppLogger.debug("applet.MultiDscApplet initiating AppletSoundList");			
		AppletSoundList.createSingleton(getCodeBase());

		SingleDscMainPanel oMainPanel = new SingleDscMainPanel();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(oMainPanel, BorderLayout.CENTER);
	

	}
	
	public SingleDscApplet(){
		_oInstance = this;
	}
	
    public void start() {
       
    }
	
    
    public void stop() {
     
    }
    
    
	public static void main(String[] args){
					
		Dimension2D oDim = Toolkit.getDefaultToolkit().getScreenSize();
		
		SingleDscApplet oApplet = new SingleDscApplet();
		MainFrame oFrame = new MainFrame(oApplet, args, (int)oDim.getWidth()*43/64, (int)oDim.getHeight()*45/64);
		
		oApplet.setFrame(oFrame);
		
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
	
	
	public static ApplicationContext getApplicationContext(){
		return _oInstance;
		
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
			strParam = IAC_UDP;
				
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
	
	public void setFrame(Frame oFrame){
		_frame = oFrame;
		
	}
	
	public Frame getFrame(){
		return _frame;
		
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
}
