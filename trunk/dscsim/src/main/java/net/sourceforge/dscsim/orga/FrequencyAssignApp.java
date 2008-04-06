package net.sourceforge.dscsim.orga;
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
 * the Initial Developer are Copyright (C) 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import static net.sourceforge.dscsim.orga.FrequencyAssign.PROTOCOL_UDP;
import static net.sourceforge.dscsim.orga.FrequencyAssign.PROTOCOL_HTTP;


/**
 * Helper Application to generate HTML pages which contain all
 * the data required to configrure the simulator instances
 *
 */
public class FrequencyAssignApp extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JLabel titleLabel = null;

	private JPanel mainPanel = null;

	private JCheckBox portCheckBox = null;

	private JTextField portTextField = null;

	private JCheckBox magicNumberCheckBox = null;

	private JTextField magicNumberTextField = null;

	private JCheckBox nucleusIpCheckBox = null;

	private JTextField nucleusIpTextField = null;

	private JCheckBox UrlCheckBox = null;

	private JTextField urlTextField = null;

	private JCheckBox groupMmsiCheckBox = null;

	private JTextField groupMmsiTextField = null;
	
	private JComboBox protocolTypeComboBox = null;
	
	private JLabel protocolTypeLabel = null;
	
	private String selectedProtocol = PROTOCOL_UDP;
	
	private int lastSelectedProtocol = 0;

	private JButton ceateButton = null;

	private JPanel filePanel = null;

	private JButton choseDirButton = null;

	private JTextField dirNameField = null;
	
	private File workingDir = null;

	/**
	 * This method initializes mainPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 5;
			gridBagConstraints11.ipadx = 137;
			gridBagConstraints11.ipady = 4;
			gridBagConstraints11.weightx = 0.0D;
			gridBagConstraints11.gridwidth = 2;
			gridBagConstraints11.insets = new Insets(5, 5, 5, 5);
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints10.gridy = 5;
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridx = 0;

			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.gridy = 4;
			gridBagConstraints9.ipadx = 137;
			gridBagConstraints9.ipady = 4;
			gridBagConstraints9.weightx = 0.0D;
			gridBagConstraints9.gridwidth = 2;
			gridBagConstraints9.insets = new Insets(5, 5, 5, 5);
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints8.gridy = 4;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridx = 0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 3;
			gridBagConstraints7.ipadx = 137;
			gridBagConstraints7.ipady = 4;
			gridBagConstraints7.weightx = 0.0D;
			gridBagConstraints7.gridwidth = 2;
			gridBagConstraints7.insets = new Insets(5, 5, 5, 5);
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridx = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.ipadx = 137;
			gridBagConstraints5.ipady = 4;
			gridBagConstraints5.weightx = 0.0D;
			gridBagConstraints5.gridwidth = 2;
			gridBagConstraints5.insets = new Insets(5, 5, 5, 5);
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridx = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.ipadx = 137;
			gridBagConstraints3.ipady = 4;
			gridBagConstraints3.weightx = 0.0D;
			gridBagConstraints3.gridwidth = 2;
			gridBagConstraints3.insets = new Insets(5, 5, 5, 5);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.ipadx = 137;
			gridBagConstraints1.ipady = 4;
			gridBagConstraints1.weightx = 0.0D;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridx = 0;
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.add(getGroupMmsiCheckBox(), gridBagConstraints);
			mainPanel.add(getGroupMmsiTextField(), gridBagConstraints1);
			mainPanel.add(getMagicNumberCheckBox(), gridBagConstraints2);
			mainPanel.add(getMagicNumberTextField(), gridBagConstraints3);
			mainPanel.add(getProtocolTypeLabel(), gridBagConstraints4);
			mainPanel.add(getProtocolTypeComboBox(), gridBagConstraints5);
			mainPanel.add(getPortCheckBox(), gridBagConstraints6);
			mainPanel.add(getPortTextField(), gridBagConstraints7);
			mainPanel.add(getNucleusIpCheckBox(), gridBagConstraints8);
			mainPanel.add(getNucleusIpTextField(), gridBagConstraints9);
			mainPanel.add(getUrlCheckBox(), gridBagConstraints10);
			mainPanel.add(getUrlTextField(), gridBagConstraints11);
			enableDisableProtocolFields();
		}
		return mainPanel;
	}

	/**
	 * This method initializes protocolTypeComboBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JComboBox getProtocolTypeComboBox() {
		if (protocolTypeComboBox == null) {
			String[] labels = new String[]{ PROTOCOL_UDP, PROTOCOL_HTTP };
			protocolTypeComboBox = new JComboBox(labels);
			protocolTypeComboBox.setSelectedItem(selectedProtocol);
			protocolTypeComboBox.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String currentSelectedProtocol = (String)protocolTypeComboBox.getSelectedItem();
					if( currentSelectedProtocol.equals(selectedProtocol) ) {
						return;
					}
					selectedProtocol = currentSelectedProtocol;
					enableDisableProtocolFields();
				}
			});
//			protocolTypeComboBox.setText("UDP Port");
//			protocolTypeComboBox.addChangeListener(new javax.swing.event.ChangeListener() {
//				public void stateChanged(javax.swing.event.ChangeEvent e) {
//					if( portCheckBox.getModel().isSelected() ) {
//						portTextField.setEnabled(true);
//					} else {
//						portTextField.setEnabled(false);
//						portTextField.setText("");
//					}
//				}
//			});
		}
		return protocolTypeComboBox;
	}

	/**
	 * This method initializes Label for protocolTypeComboBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JLabel getProtocolTypeLabel() {
		if (protocolTypeLabel == null) {
			protocolTypeLabel = new JLabel("Protokoll: ");
		}
		return protocolTypeLabel;
	}

	/**
	 * This method initializes portCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getPortCheckBox() {
		if (portCheckBox == null) {
			portCheckBox = new JCheckBox();
			portCheckBox.setText("UDP Port");
			portCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					if( !portCheckBox.getModel().isSelected() ) {
						portTextField.setText("");
					}
					enableDisableProtocolFields();
				}
			});
		}
		return portCheckBox;
	}

	/**
	 * This method initializes portTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPortTextField() {
		if (portTextField == null) {
			portTextField = new JTextField();
			portTextField.setAlignmentX(0.5F);
			portTextField.setEnabled(false);
		}
		return portTextField;
	}

	/**
	 * This method initializes magicNumberCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getMagicNumberCheckBox() {
		if (magicNumberCheckBox == null) {
			magicNumberCheckBox = new JCheckBox();
			magicNumberCheckBox.setText("Magic-Number");
			magicNumberCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					if( magicNumberCheckBox.getModel().isSelected() ) {
						magicNumberTextField.setEnabled(true);
					} else {
						magicNumberTextField.setEnabled(false);
						magicNumberTextField.setText("");
					}
				}
			});
		}
		return magicNumberCheckBox;
	}

	/**
	 * This method initializes magicNumberTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMagicNumberTextField() {
		if (magicNumberTextField == null) {
			magicNumberTextField = new JTextField();
			magicNumberTextField.setEnabled(false);
		}
		return magicNumberTextField;
	}

	/**
	 * This method initializes nucleusIpCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getNucleusIpCheckBox() {
		if (nucleusIpCheckBox == null) {
			nucleusIpCheckBox = new JCheckBox();
			nucleusIpCheckBox.setText("Nucleus-IP");
			nucleusIpCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					if( !nucleusIpCheckBox.getModel().isSelected() ) {
						nucleusIpTextField.setText("");
					}
					enableDisableProtocolFields();
				}
			});
		}
		return nucleusIpCheckBox;
	}

	/**
	 * This method initializes nucleusIpTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNucleusIpTextField() {
		if (nucleusIpTextField == null) {
			nucleusIpTextField = new JTextField();
			nucleusIpTextField.setEnabled(false);
		}
		return nucleusIpTextField;
	}

	/**
	 * This method initializes UrlCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getUrlCheckBox() {
		if (UrlCheckBox == null) {
			UrlCheckBox = new JCheckBox();
			UrlCheckBox.setText("Http-URL");
			UrlCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					if( !UrlCheckBox.getModel().isSelected() ) {
						urlTextField.setText("");
					}
					enableDisableProtocolFields();
				}
			});
		}
		return UrlCheckBox;
	}

	/**
	 * This method initializes urlTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getUrlTextField() {
		if (urlTextField == null) {
			urlTextField = new JTextField();
			urlTextField.setEnabled(false);
		}
		return urlTextField;
	}

	/**
	 * This method initializes groupMmsiCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getGroupMmsiCheckBox() {
		if (groupMmsiCheckBox == null) {
			groupMmsiCheckBox = new JCheckBox();
			groupMmsiCheckBox.setText("Group-MMSI");
			groupMmsiCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					if( groupMmsiCheckBox.getModel().isSelected() ) {
						groupMmsiTextField.setEnabled(true);
					} else {
						groupMmsiTextField.setEnabled(false);
						groupMmsiTextField.setText("");
					}
				}
			});
		}
		return groupMmsiCheckBox;
	}

	/**
	 * This method initializes groupMmsiTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getGroupMmsiTextField() {
		if (groupMmsiTextField == null) {
			groupMmsiTextField = new JTextField();
			groupMmsiTextField.setEnabled(false);
			groupMmsiTextField.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		}
		return groupMmsiTextField;
	}

	/**
	 * This method initializes ceateButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCeateButton() {
		if (ceateButton == null) {
			ceateButton = new JButton();
			ceateButton.setText("create Files");
			ceateButton.setAlignmentX(0.5F);
			ceateButton.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					try {
						FrequencyAssign regTpInstance = new FrequencyAssign();
						regTpInstance.setDir(workingDir);
						String tmpText = portTextField.getText();
						if( tmpText != null && !tmpText.equals("") ) {
							regTpInstance.setPort( Integer.parseInt(tmpText) );
						}
						tmpText = magicNumberTextField.getText();
						if( tmpText != null && !tmpText.equals("") ) {
							regTpInstance.setMagicNumber( Integer.parseInt(tmpText) );
						}
						tmpText = nucleusIpTextField.getText();
						if( tmpText != null && !tmpText.equals("") ) {
							regTpInstance.setNucleusIP(tmpText);
						}
						tmpText = groupMmsiTextField.getText();
						if( tmpText != null && !tmpText.equals("") ) {
							regTpInstance.setGroupMmsi(tmpText);
						}
						regTpInstance.setProtocol(selectedProtocol);
						tmpText = urlTextField.getText();
						if( tmpText != null && !tmpText.equals("") ) {
							regTpInstance.setUrl(tmpText);
						}
						
						regTpInstance.makeFiles();
						JOptionPane.showMessageDialog(FrequencyAssignApp.this,
							    "Frequenzzuteilungsurkunden erzeugt");
					} catch( Exception ex ) {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						ex.printStackTrace(pw);
						JOptionPane.showMessageDialog(FrequencyAssignApp.this,
					    sw.getBuffer().toString(),
					    "dscsimOrga error message",
					    JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
		return ceateButton;
	}

	/**
	 * This method initializes filePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFilePanel() {
		if (filePanel == null) {
			filePanel = new JPanel();
			filePanel.setLayout(new FlowLayout());
			filePanel.add(getChoseDirButton(), null);
			filePanel.add(getCeateButton(), null);
		}
		return filePanel;
	}

	/**
	 * This method initializes choseDirButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getChoseDirButton() {
		if (choseDirButton == null) {
			choseDirButton = new JButton();
			choseDirButton.setText("change Dir");
			choseDirButton.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					JFileChooser fc = new JFileChooser(workingDir );
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnVal = fc.showDialog(FrequencyAssignApp.this,"Select");	
					if( returnVal == JFileChooser.APPROVE_OPTION ) {
						workingDir = fc.getSelectedFile();
						updateDir();
					}
				}
			});
		}
		return choseDirButton;
	}

	/**
	 * This method initializes dirNameField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDirNameField() {
		if (dirNameField == null) {
			dirNameField = new JTextField();
			dirNameField.setEditable(false);
			workingDir = new File(".");
			updateDir();
		}
		return dirNameField;
	}

	/**
	 * 
	 */
	private void updateDir() {
		try {
			dirNameField.setText(workingDir.getCanonicalPath());
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				FrequencyAssignApp thisClass = new FrequencyAssignApp();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.pack();
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public FrequencyAssignApp() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("dscsimOrga");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			titleLabel = new JLabel();
			titleLabel.setText("dscsimOrga - Frequenzzuteilungsurkunden für dscsim");
			titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			titleLabel.setAlignmentX(0.5F);
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.add(titleLabel, null);
			jContentPane.add(getMainPanel(), null);
			jContentPane.add(getDirNameField(), null);
			jContentPane.add(getFilePanel(), null);
		}
		return jContentPane;
	}

	/**
	 * Enable or disable the text fields depending on the otjer fields
	 */
	private void enableDisableProtocolFields() {
		if( selectedProtocol.equals(PROTOCOL_UDP)) {
			nucleusIpCheckBox.setEnabled(true);
			if( nucleusIpCheckBox.getModel().isSelected() ) {
				nucleusIpTextField.setEnabled(true);
			} else {
				nucleusIpTextField.setEnabled(false);
			}
			portCheckBox.setEnabled(true);
			if( portCheckBox.getModel().isSelected() ) {
				portTextField.setEnabled(true);
			} else {
				portTextField.setEnabled(false);
			}
			UrlCheckBox.setEnabled(false);
			urlTextField.setEnabled(false);
		} else if ( selectedProtocol.equals(PROTOCOL_HTTP) ) {
			nucleusIpCheckBox.setEnabled(false);
			nucleusIpTextField.setEnabled(false);
			portCheckBox.setEnabled(false);
			portTextField.setEnabled(false);
			UrlCheckBox.setEnabled(true);
			if( UrlCheckBox.getModel().isSelected() ) {
				urlTextField.setEnabled(true);
			} else {
				urlTextField.setEnabled(false);
			}
			
		}

	
	
	}
	
}
