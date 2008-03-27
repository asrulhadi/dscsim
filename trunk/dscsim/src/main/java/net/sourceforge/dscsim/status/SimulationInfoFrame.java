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
 * The Initial Developer of the Original Code is Oliver Hecker. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.status;

import java.awt.BorderLayout;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import java.awt.FlowLayout;
import javax.swing.JList;
import javax.swing.JTextArea;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import java.awt.CardLayout;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JCheckBox;

import net.sourceforge.dscsim.radio.core.RadioCore;
import net.sourceforge.dscsim.radio.core.RadioCoreFactory;
import net.sourceforge.dscsim.radio.core.RadioEventListener;
import net.sourceforge.dscsim.radio.core.VHFChannel;
import net.sourceforge.dscsim.radiotransport.Airwave;
import net.sourceforge.dscsim.radiotransport.AirwaveStatusInterface;
import net.sourceforge.dscsim.radiotransport.AirwaveStatusListener;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JRadioButton;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.Insets;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;

import net.sourceforge.dscsim.common.AppFrame;
import net.sourceforge.dscsim.controller.ApplicationContext;

import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
/**
 * @author oliver
 *
 */
public class SimulationInfoFrame extends AppFrame implements AirwaveStatusListener {

	/**
	 * Prefix of systems properties to be used by to configure this object
	 */
	private static final String PROPERTY_PREFIX = "parameter.dscsim.";

	private JPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="10,54"
	private JLabel headingLabel = null;
	private JPanel mmsiPanel = null;
	private JLabel mmsiTitle = null;
	private JTextField mmsiValue = null;
	private JPanel namePanel = null;
	private JPanel callSignPanel = null;
	private JPanel simInfoPanel = null;
	private JLabel nameTitle = null;
	private JLabel callSignTitle = null;
	private JTextField nameValue = null;
	private JTextField callSignValue = null;
	private JPanel networkPanel = null;
	private JTextField networkStatus = null;
	
	private AirwaveStatusInterface airwaveStatusInterface;
	private ApplicationContext _applCtx = null;

	/**
	 * This method initializes headingLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getHeadingLabel() {
		if (headingLabel == null) {
			headingLabel = new JLabel();
			headingLabel.setText("dscsim Info");
			headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
			headingLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			headingLabel.setAlignmentX(0.5F);
			headingLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		}
		return headingLabel;
	}

	/**
	 * This method initializes mmsiPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMmsiPanel() {
		if (mmsiPanel == null) {
			mmsiTitle = new JLabel();
			mmsiTitle.setText("MMSI: ");
			mmsiPanel = new JPanel();
			mmsiPanel.setLayout(new BoxLayout(getMmsiPanel(), BoxLayout.X_AXIS));
			mmsiPanel.add(mmsiTitle, null);
			mmsiPanel.add(getMmsiValue(), null);
		}
		return mmsiPanel;
	}

	/**
	 * This method initializes mmsiValue	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMmsiValue() {
		if (mmsiValue == null) {
			mmsiValue = new JTextField();
			mmsiValue.setEditable(false);
			mmsiValue.setMinimumSize(new Dimension(80, 20));
			mmsiValue.setBorder(null);
		}
		return mmsiValue;
	}

	/**
	 * This method initializes namePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNamePanel() {
		if (namePanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints.weightx = 1.0;
			nameTitle = new JLabel();
			nameTitle.setText("Name: ");
			namePanel = new JPanel();
			namePanel.setLayout(new BoxLayout(getNamePanel(), BoxLayout.X_AXIS));
			namePanel.add(nameTitle, null);
			namePanel.add(getNameValue(), null);
		}
		return namePanel;
	}

	/**
	 * This method initializes callSignPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCallSignPanel() {
		if (callSignPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints1.weightx = 1.0;
			callSignTitle = new JLabel();
			callSignTitle.setText("Call-Sign: ");
			callSignPanel = new JPanel();
			callSignPanel.setLayout(new BoxLayout(getCallSignPanel(), BoxLayout.X_AXIS));
			callSignPanel.add(callSignTitle, null);
			callSignPanel.add(getCallSignValue(), null);
		}
		return callSignPanel;
	}

	/**
	 * This method initializes simInfoPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSimInfoPanel() {
		if (simInfoPanel == null) {
			simInfoPanel = new JPanel();
			simInfoPanel.setLayout(new BoxLayout(getSimInfoPanel(), BoxLayout.Y_AXIS));
			simInfoPanel.setBorder(BorderFactory.createTitledBorder(null, "Common Info:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), Color.black));
			simInfoPanel.add(getMmsiPanel(), null);
			simInfoPanel.add(getNamePanel(), null);
			simInfoPanel.add(getCallSignPanel(), null);
		}
		return simInfoPanel;
	}

	/**
	 * This method initializes nameValue	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNameValue() {
		if (nameValue == null) {
			nameValue = new JTextField();
			nameValue.setEditable(false);
			nameValue.setBorder(null);
			nameValue.setMinimumSize(new Dimension(80, 20));
		}
		return nameValue;
	}

	/**
	 * This method initializes callSignValue	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCallSignValue() {
		if (callSignValue == null) {
			callSignValue = new JTextField();
			callSignValue.setEditable(false);
			callSignValue.setBorder(null);
			callSignValue.setMinimumSize(new Dimension(80, 20));
		}
		return callSignValue;
	}

	/**
	 * This method initializes networkPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNetworkPanel() {
		if (networkPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints2.weightx = 1.0;
			networkPanel = new JPanel();
			networkPanel.setLayout(new BorderLayout());
			networkPanel.setBorder(BorderFactory.createTitledBorder(null, "Network:", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), Color.black));
			networkPanel.add(getNetworkStatus(), BorderLayout.SOUTH);
		}
		return networkPanel;
	}

	/**
	 * This method initializes networkStatus	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNetworkStatus() {
		if (networkStatus == null) {
			networkStatus = new JTextField();
			networkStatus.setBackground(Color.gray);
			networkStatus.setText("Status: -");
			networkStatus.setHorizontalAlignment(JTextField.CENTER);
//			networkStatus.setEditable(false);
			networkStatus.setEditable(false);
			networkStatus.setMinimumSize(new Dimension(200, 20));
		}
		return networkStatus;
	}

	/**
	 * This is the xxx default constructor
	 */
	public SimulationInfoFrame(ApplicationContext appCtx) {
		super();	
		_applCtx = appCtx;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void init() {
		try {
			super.init();
//			this.setSize(640, 480); /*handled by base*/
			this.setContentPane(getJContentPane());
			this.setResizable(false);
			Dimension innerDim = getJContentPane().getPreferredSize();
			Dimension myDim = new Dimension(
					innerDim.width+20,
					innerDim.height+20);
			this.setSize(myDim);
			
			getMmsiValue().setText(_applCtx.getIndividualMmsi());
			String propValue = System.getProperty(PROPERTY_PREFIX+"station_name");
			if( propValue != null ) {
				getNameValue().setText(propValue);
			}
			propValue = System.getProperty(PROPERTY_PREFIX+"call_sign");
			if( propValue != null ) {
				getCallSignValue().setText(propValue);
			}
			
			Airwave airwave = Airwave.getInstance();
			if( airwave instanceof AirwaveStatusInterface ) {
				airwaveStatusInterface = (AirwaveStatusInterface)airwave;
				airwaveStatusInterface.registerStatusListener(this);
			}
			registerUpdate();
		} catch( Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.setPreferredSize(new Dimension(200, 180));
			jContentPane.add(getHeadingLabel(), null);
			jContentPane.add(getSimInfoPanel(), null);
			jContentPane.add(getNetworkPanel(), null);
		}
		return jContentPane;
	}

	/**
	 * Sets the GUI elements according to the values retrieved
	 * from the radio core
	 */
	public void updateGUIElements() {
		if( airwaveStatusInterface != null ) {
			int status = airwaveStatusInterface.getNetworkStatus();
			Color color = Color.RED;
			switch(status) {
				case AirwaveStatusInterface.STATUS_RED:
					color = Color.RED;
					break;
				case AirwaveStatusInterface.STATUS_YELLOW:
					color = Color.YELLOW;
					break;
				case AirwaveStatusInterface.STATUS_BLUE:
					color = Color.BLUE;
					break;
				case AirwaveStatusInterface.STATUS_GREEN:
					color = Color.GREEN;
					break;
				
			}
			getNetworkStatus().setBackground(color);
			getNetworkStatus().setText(airwaveStatusInterface.getStatusString());
		}
	}
	
	/**
	 * Registers Method updateGUIElements for execution
	 */
	private void registerUpdate() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	updateGUIElements();
            }
        });
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.radiotransport.AirwaveStatusListener#notifyNetworkStatus()
	 */
	public void notifyNetworkStatus() {
		registerUpdate();
	}
	
}
