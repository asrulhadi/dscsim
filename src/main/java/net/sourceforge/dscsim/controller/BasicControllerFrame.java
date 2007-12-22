/*
 * Created on 03.01.2007
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
 
package net.sourceforge.dscsim.controller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URL;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sourceforge.dscsim.common.AppFrame;
import net.sourceforge.dscsim.controller.network.DscIACManager;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.utils.AppletSoundList;
import net.sourceforge.dscsim.radio.core.RadioCore;
/**
 * Implements the Controllers in the dscsim.
 * 
 * @author William Pennoyer.
 *
 */
public class BasicControllerFrame extends AppFrame implements  ItemListener, ActionListener, Constants {
	
	/**
	 * The Panel of the controller.
	 */
	private DscAppPanel _oAppPanel = null;
	
	/**
	 * Reference to ApplicationContext.
	 */
	private ApplicationContext _applCtx = null;
	
	/**
	 * Reference to RadioCore.
	 */
	private RadioCore _radioCore = null;
	
	/**
	 * URL for loading properties.
	 */
	private URL _url = null;
	
	/**
	 * Constructor.
	 * @param appCtx Application Context for configuration parameters.
	 * @param radioCore
	 * @throws Exception
	 */
	public BasicControllerFrame(ApplicationContext appCtx, RadioCore radioCore)
		throws Exception {
		super();	
		_applCtx = appCtx;
		_radioCore = radioCore;
		
		String userHome = "file:/"
			+ System.getProperties().getProperty("user.dir") 
			+ File.separator;

		AppLogger.debug2("BasicControllerFrame.ctor " + userHome);
		
		_url = new URL(userHome);   
    
	}
	
	/**
	 * Instantiate internal parts of Controller.
	 * @see MultiBus, MultiBeeper, MultiClu, MultiController.
	 */
	public void init() {

		AppletSoundList.createSingleton(_url);
		//getContentPane().setLayout(new BorderLayout());
		
		//getContentPane().add(createMenuBar(), BorderLayout.NORTH);
		getContentPane().add(createMenuBar());
		        
		String strMMSI = _applCtx.getIndividualMmsi();
		_oAppPanel = new DscAppPanel(strMMSI, _applCtx);
		//getContentPane().add(_oAppPanel, BorderLayout.CENTER);
		getContentPane().add(_oAppPanel);
			
		_oAppPanel.setRadioCoreController(new RadioCoreController(_radioCore, getInstanceContext()));

		String strIAC = _applCtx.getIACMethod();       
		DscIACManager.initIAC(_oAppPanel, strIAC, _oAppPanel);
		DscIACManager.initSyncPublisher(_applCtx);
		
		_oAppPanel.init(this);
		
		//allow incoming messages to control radio channels.
//		_oAppPanel.setRadioCoreController(new RadioCoreController(_radioCore));
		_oAppPanel.getBus().putOnline(_oAppPanel.getRadioCoreController());

		this.addKeyListener(this.getInstanceContext().getController());
		
		getInstanceContext().getBus().putOnline(getInstanceContext().getContentManager());
		
		super.init();
	}
	

	/**
	 * 
	 */
	public void itemStateChanged(ItemEvent e) {
		//AppLogger.debug("DscMainPanel.itemStateChange called.");
		
	}
	
	/**
	 * Create frame's menubar.
	 */
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
    
	
	/**
	 * 
	 */
	public void keyReleased(String keyId) {
		
	}

	/**
	 * show setup dialogs in menu bar.
	 */
	public void actionPerformed(ActionEvent arg0) {
		
		//AppLogger.debug2("action command is:" + arg0.getActionCommand());
			
		if(MENU_ACT_NETWORK_SETUP.equals(arg0.getActionCommand())){
			SetupNetWorkDialog d = new SetupNetWorkDialog(this, _applCtx);			
			d.setModal(true);			
			d.setVisible( true );
									
		}else if(MENU_ACT_SIM_SETUP.equals(arg0.getActionCommand())){		
			SetupSimulationDialog dlg = new SetupSimulationDialog(this, getInstanceContext());			
			dlg.setVisible( true );

		}		

	}
	
	/**
	 * get InstanceContext.
	 * @return InstanceContext.
	 */
	public InstanceContext getInstanceContext(){
		return _oAppPanel;
	}
	
	public void paint(java.awt.Graphics g){
		super.paint(g);
	}
}
