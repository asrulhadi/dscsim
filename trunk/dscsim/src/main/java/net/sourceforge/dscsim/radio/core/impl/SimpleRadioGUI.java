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
package net.sourceforge.dscsim.radio.core.impl;

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
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
/**
 * @author oliver
 *
 */
public class SimpleRadioGUI extends AppFrame implements RadioEventListener {

	private JPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="10,54"
	private JLabel title = null;
	private JButton sendButton = null;
	private RadioCore radioCore = null;
//	private Set<Object> objectBuffer;  //  @jve:decl-index=0:
	private Set objectBuffer;  //  @jve:decl-index=0:
	private JPanel lowPanel = null;
	private JPanel highPanel = null;
	private JSlider squelchSlider = null;
	private JSlider volumeSlider = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JLabel volumeLabel = null;
	private JLabel squelchLabel = null;
	private JPanel leftPanel = null;
	private JPanel channelPannel = null;
	private JPanel switchPanel = null;
	private JPanel upDownPanel = null;
	private JButton chUpButton = null;
	private JButton chDownButton = null;
	private JLabel chDisplayLabel = null;
	private JRadioButton jRadioButton = null;
	private JRadioButton jRadioButton1 = null;
	private JButton lowHighButton = null;
	private JLabel powerLabel = null;
	private JButton ch16Button = null;
	private JPanel jPanel2 = null;
	private JPanel jPanel3 = null;
	private JPanel jPanel4 = null;
	private JToggleButton onOffButton = null;
	private Set componentSetForOnOff;
	/**
	 * This method initializes sendButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSendButton() {
		if (sendButton == null) {
			sendButton = new JButton();
			sendButton.setPreferredSize(new Dimension(200, 40));
			sendButton.setName("sendButton");
			sendButton.setMnemonic(KeyEvent.VK_T);
			sendButton.setToolTipText("transmit the sound signal spoken into the microphone (if the window is focussed it is also possible to simply press ENTER) ");
			sendButton.setText("TRANSMIT");
			sendButton.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					if(sendButton.getModel().isPressed()) {
						radioCore.setTransmit(true);
					} else {
						radioCore.setTransmit(false);
					}
				}
			});
			componentSetForOnOff.add(sendButton);
		}
		return sendButton;
	}

	/**
	 * This method initializes lowPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLowPanel() {
		if (lowPanel == null) {
			lowPanel = new JPanel();
			lowPanel.setLayout(new FlowLayout());
			lowPanel.add(getSendButton(), null);
		}
		return lowPanel;
	}

	/**
	 * This method initializes highPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getHighPanel() {
		if (highPanel == null) {
			highPanel = new JPanel();
			highPanel.setLayout(new BoxLayout(getHighPanel(), BoxLayout.X_AXIS));
			highPanel.add(getLeftPanel(), null);
			highPanel.add(getJPanel2(), null);
		}
		return highPanel;
	}

	/**
	 * This method initializes squelchSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getSquelchSlider() {
		if (squelchSlider == null) {
			squelchSlider = new JSlider();
			squelchSlider.setOrientation(JSlider.VERTICAL);
			squelchSlider.setPaintTicks(true);
			squelchSlider.setMajorTickSpacing(100);
			squelchSlider.setMaximum(1000);
			squelchSlider.setValue(300);
			squelchSlider.setToolTipText("sensitivity of the noise gate");
			radioCore.setSquelch(squelchSlider.getValue() / 1000.0) ;
			squelchSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					radioCore.setSquelch(((JSlider)e.getSource()).getValue() / 1000.0) ;
				}
			});
		}
		return squelchSlider;
	}

	/**
	 * This method initializes volumeSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getVolumeSlider() {
		if (volumeSlider == null) {
			volumeSlider = new JSlider();
			volumeSlider.setOrientation(JSlider.VERTICAL);
			volumeSlider.setPaintLabels(false);
			volumeSlider.setMaximum(1000);
			volumeSlider.setValue(200);
			radioCore.setVolume(volumeSlider.getValue() / 1000.0) ;
			volumeSlider.setMajorTickSpacing(100);
			volumeSlider.setToolTipText("volume of the loudspeaker");
			volumeSlider.setPaintTicks(true);
			volumeSlider.setName("Volume");
			volumeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					radioCore.setVolume(((JSlider)e.getSource()).getValue() / 1000.0) ;
				}
			});
		}
		return volumeSlider;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			volumeLabel = new JLabel();
			volumeLabel.setText("Volume");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = -1;
			gridBagConstraints1.ipadx = 10;
			gridBagConstraints1.gridx = -1;
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.Y_AXIS));
			jPanel.add(volumeLabel, null);
			jPanel.add(getVolumeSlider(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			squelchLabel = new JLabel();
			squelchLabel.setText("Squelch");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridy = -1;
			gridBagConstraints.ipadx = 10;
			gridBagConstraints.gridx = -1;
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BoxLayout(getJPanel1(), BoxLayout.Y_AXIS));
			jPanel1.setToolTipText("");
			jPanel1.add(squelchLabel, null);
			jPanel1.add(getSquelchSlider(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes leftPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLeftPanel() {
		if (leftPanel == null) {
			leftPanel = new JPanel();
			leftPanel.setLayout(new BoxLayout(getLeftPanel(), BoxLayout.Y_AXIS));
			leftPanel.add(getJPanel4(), null);
			leftPanel.add(getSwitchPanel(), null);
		}
		return leftPanel;
	}

	/**
	 * This method initializes channelPannel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getChannelPannel() {
		if (channelPannel == null) {
			chDisplayLabel = new JLabel();
			chDisplayLabel.setText("16");
			chDisplayLabel.setBackground(Color.red);
			chDisplayLabel.setName("chDisplayLabel");
			chDisplayLabel.setToolTipText("The VHF channel used for sending and receiving");
			chDisplayLabel.setFont(new Font("Dialog", Font.BOLD, 48));
			channelPannel = new JPanel();
			channelPannel.setLayout(new BoxLayout(getChannelPannel(), BoxLayout.X_AXIS));
			channelPannel.setBackground(Color.magenta);
			channelPannel.setBorder(BorderFactory.createLineBorder(Color.gray, 5));
			channelPannel.setName("channelPannel");
			channelPannel.add(chDisplayLabel, null);
			channelPannel.add(getUpDownPanel(), null);
		}
		return channelPannel;
	}

	/**
	 * This method initializes switchPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSwitchPanel() {
		if (switchPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.ipadx = 0;
			gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
			powerLabel = new JLabel();
			powerLabel.setText("Power");
			switchPanel = new JPanel();
			switchPanel.setLayout(new GridBagLayout());
			switchPanel.add(powerLabel, new GridBagConstraints());
			switchPanel.add(getJRadioButton(), new GridBagConstraints());
			switchPanel.add(getJRadioButton1(), new GridBagConstraints());
			switchPanel.add(getLowHighButton(), gridBagConstraints2);
			switchPanel.add(getCh16Button(), new GridBagConstraints());
		}
		return switchPanel;
	}

	/**
	 * This method initializes upDownPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getUpDownPanel() {
		if (upDownPanel == null) {
			upDownPanel = new JPanel();
			upDownPanel.setLayout(new BoxLayout(getUpDownPanel(), BoxLayout.Y_AXIS));
			upDownPanel.setName("upDownPanel");
			upDownPanel.add(getChUpButton(), null);
			upDownPanel.add(getChDownButton(), null);
		}
		return upDownPanel;
	}

	/**
	 * This method initializes chUpButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getChUpButton() {
		if (chUpButton == null) {
			chUpButton = new JButton();
			chUpButton.setText("^");
			chUpButton.setMnemonic(KeyEvent.VK_U);
			chUpButton.setMinimumSize(new Dimension(45, 45));
			chUpButton.setMaximumSize(new Dimension(45, 45));
			chUpButton.setHorizontalAlignment(SwingConstants.CENTER);
			chUpButton.setPreferredSize(new Dimension(45, 45));
			chUpButton.setToolTipText("switch to the next higher VHF channel (or to the lowest channel if the highest is already reached)");
			chUpButton.setFont(new Font("Dialog", Font.BOLD, 18));
			chUpButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					radioCore.setChannel(radioCore.getChannel().getNext());
				}
			});
			componentSetForOnOff.add(chUpButton);

		}
		return chUpButton;
	}

	/**
	 * This method initializes chDownButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getChDownButton() {
		if (chDownButton == null) {
			chDownButton = new JButton();
			chDownButton.setText("v");
			chDownButton.setMinimumSize(new Dimension(45, 45));
			chDownButton.setMaximumSize(new Dimension(45, 45));
			chDownButton.setHorizontalAlignment(SwingConstants.CENTER);
			chDownButton.setPreferredSize(new Dimension(43, 45));
			chDownButton.setMnemonic(KeyEvent.VK_D);
			chDownButton.setToolTipText("switch to the next lower VHF channel (or to the highest channel if the lowest is already reached)");
			chDownButton.setFont(new Font("Dialog", Font.PLAIN, 18));
			chDownButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					radioCore.setChannel(radioCore.getChannel().getPrevious());
				}
			});
			componentSetForOnOff.add(chDownButton);
		}
		return chDownButton;
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton() {
		if (jRadioButton == null) {
			jRadioButton = new JRadioButton();
			jRadioButton.setText("low");
			jRadioButton.setToolTipText("indicates that the transmission power is set to low (1 W)");
			jRadioButton.setEnabled(false);
		}
		return jRadioButton;
	}

	/**
	 * This method initializes jRadioButton1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton1() {
		if (jRadioButton1 == null) {
			jRadioButton1 = new JRadioButton();
			jRadioButton1.setText("high");
			jRadioButton1.setToolTipText("indicates that the transmission power is set to high (25 W)");
			jRadioButton1.setEnabled(false);
		}
		return jRadioButton1;
	}

	/**
	 * This method initializes lowHighButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLowHighButton() {
		if (lowHighButton == null) {
			lowHighButton = new JButton();
			lowHighButton.setText("low/high");
			lowHighButton.setToolTipText("switch between low and high transmission power");
			lowHighButton.setMnemonic(KeyEvent.VK_W);
			lowHighButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					radioCore.setPower(!radioCore.getPower());
				}
			});
			componentSetForOnOff.add(lowHighButton);
		}
		return lowHighButton;
	}

	/**
	 * This method initializes ch16Button	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCh16Button() {
		if (ch16Button == null) {
			ch16Button = new JButton();
			ch16Button.setText("Ch16");
			ch16Button.setMnemonic(KeyEvent.VK_C);
			ch16Button.setToolTipText("immediately switch to channel 16 and high transmission power");
			ch16Button.setBackground(Color.red);
			ch16Button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					radioCore.setChannel(VHFChannel.VHF_CHANNEL_16);
					radioCore.setPower(true);
				}
			});
			componentSetForOnOff.add(ch16Button);
		}
		return ch16Button;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(new BoxLayout(getJPanel2(), BoxLayout.X_AXIS));
			jPanel2.add(getJPanel(), null);
			jPanel2.add(getJPanel1(), null);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setLayout(new GridBagLayout());
			jPanel3.setPreferredSize(new Dimension(101, 36));
			jPanel3.add(title, new GridBagConstraints());
		}
		return jPanel3;
	}

	/**
	 * This method initializes jPanel4	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setHgap(50);
			flowLayout.setAlignment(FlowLayout.CENTER);
			jPanel4 = new JPanel();
			jPanel4.setLayout(flowLayout);
			jPanel4.add(getOnOffButton(), null);
			jPanel4.add(getChannelPannel(), null);
		}
		return jPanel4;
	}

	/**
	 * This method initializes onOffButton	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */
	private JToggleButton getOnOffButton() {
		if (onOffButton == null) {
			onOffButton = new JToggleButton();
			onOffButton.setText("On/Off");
			onOffButton.setName("onOffButton");
			onOffButton.setMnemonic(KeyEvent.VK_O);
			onOffButton.setPreferredSize(new Dimension(70, 26));
			onOffButton.setToolTipText("Toggle the radio master swicth between off and on");
			onOffButton.setSelected(radioCore.getMasterSwitch());
			onOffButton.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					radioCore.setMasterSwitch(onOffButton.getModel().isSelected());
				}
			});
		}
		return onOffButton;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SimpleRadioGUI frame  = new SimpleRadioGUI();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.startCore(true);
                frame.init();
                frame.pack();
                frame.setVisible(true);
                frame.updateGUIElements();
            }
        });
	}

	/**
	 * This is the xxx default constructor
	 */
	public SimpleRadioGUI() {
		super();
		componentSetForOnOff = new HashSet();
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
//			this.objectBuffer = new HashSet<Object>();
			this.objectBuffer = new HashSet();
		} catch( Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes the RadioCore memberthis
	 * @param shipStation <code>true</code> ship station, <code>false</code> shore station
	 * @return void
	 */
	public void startCore(boolean shipStation) {
		try {
//			System.setProperty("dscsim.udp_airwave.peerhost","localhost");
			radioCore = RadioCoreFactory.createRadioCore(shipStation);
			radioCore.registerListener(this);
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
			title = new JLabel();
			title.setText("Generic Radio GUI");
			title.setName("title");
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.add(getJPanel3(), null);
			jContentPane.add(getHighPanel(), null);
			jContentPane.add(getLowPanel(), null);
			jContentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "pressed");
			jContentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released ENTER"), "released");
			jContentPane.getActionMap().put("pressed",
					new AbstractAction() {
						public void actionPerformed(ActionEvent actionEvent ) {
							pressTransmitButton(true);
						}
					} 
			);
			
			jContentPane.getActionMap().put("released",
					new AbstractAction() {
						public void actionPerformed(ActionEvent actionEvent ) {
							pressTransmitButton(false);
						}
					}	 					
			);
		}
		return jContentPane;
	}

	/**
	 * Sets the GUI elements according to the values retrieved
	 * from the radio core
	 */
	public void updateGUIElements() {
		boolean highPower = radioCore.getPower();
		boolean masterSwitch = radioCore.getMasterSwitch();
		getJRadioButton().setSelected(!highPower);
		getJRadioButton1().setSelected(highPower);
		for( Iterator i = componentSetForOnOff.iterator(); i.hasNext(); ) {
			JComponent component = (JComponent) i.next();
			component.setEnabled(masterSwitch);
		}
		if(masterSwitch) {
			getChannelPannel().setBackground(Color.magenta);
			chDisplayLabel.setText(radioCore.getChannel().getName());
		} else {
			getChannelPannel().setBackground(Color.lightGray);
			chDisplayLabel.setText("00");
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
	/**
	 * @param newHost
	 */
	public void addHost(InetAddress newHost) {
		synchronized(objectBuffer){
			objectBuffer.add(newHost);
		}
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	synchronized(objectBuffer){
//                	for( Object newHost : objectBuffer ){
                   	for( Iterator i = objectBuffer.iterator(); i.hasNext(); ){
                   		Object newHost = i.next();
                	}
                	objectBuffer.clear();
            	}
            }
        });
	}

	public void notifyChannel() {
		registerUpdate();
	}

	public void notifyMasterSwitch() {
		registerUpdate();
	}

	public void notifyPower() {
		registerUpdate();
	}

	public void notifyDscTransmissionFinished() {
	}
	
	/**
	 * Getter for the used RadioCore. Might be used by the DCS-Controller
	 * to changed the channel of the radio or send a DSC signal (sending DSC
	 * signal not yet implemented)
	 * @return the RadioCore object 
	 */
	public RadioCore getRadioCore() {
		return radioCore;
	}
	
	/**
	 * Remotely press the transmitt button
	 */
	public void pressTransmitButton(boolean pressed) {
		if( pressed ){
			getSendButton().getModel().setArmed(true);
			getSendButton().getModel().setPressed(true);
		} else {
			getSendButton().getModel().setPressed(false);
			getSendButton().getModel().setArmed(false);
		}

	}

}
