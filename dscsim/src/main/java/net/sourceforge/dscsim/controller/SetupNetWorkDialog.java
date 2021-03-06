/*
 * SetupDialog.java
 *
 * Created on 12. September 2006, 21:10
 */

package net.sourceforge.dscsim.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JLabel;

import net.sourceforge.dscsim.controller.utils.AppLogger;


import java.awt.Rectangle;


/**
 *
 * @author  katharina
 */
public class SetupNetWorkDialog extends  JDialog implements ActionListener, Constants {
    
	private ApplicationContext _appCtx = null;
	
    /** Creates new form SetupDialog */
    public SetupNetWorkDialog(java.awt.Frame parent, ApplicationContext appCtx) {
        super(parent, true);
        _appCtx = appCtx;
		setSize(DEF_DIALOG_WIDTH,DEF_DIALOG_HEIGHT); 
        initComponents();

    }
    
    private void addField(String label, String value, Rectangle rec){
    	
    	int xoffset = 150;
 
        JLabel jLabel = new javax.swing.JLabel();
        jLabel.setText(label);
        getContentPane().add(jLabel);
        jLabel.setBounds(rec.x, rec.y, rec.width, rec.height);

    	JTextField jTextField = new javax.swing.JTextField();
        jTextField.setText(value);
        jTextField.setEditable(false);
        getContentPane().add(jTextField);
        jTextField.setBounds(rec.x + xoffset, rec.y, rec.width+25, rec.height);
    	
    	
    }
    /** This method is called from within the constructor to
      * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {
    	
    	this.setTitle("Show Startup Settings");
    	
    	int  x=10, y=20, width=150, height=20;
    	
    	int y_delta = 22, offset=0;

    	//dscsim.provider.protocol=%3 dscsim.provider.user=%4 dscsim.provider.pwd=%5 dscsim.provider.url=%6
    	addField("Individual MMSI", _appCtx.getIndividualMmsi(), new Rectangle(x, y+y_delta*offset, width, height));
    	addField("Group MMSI", _appCtx.getGroupMmsi(), new Rectangle(x, y+y_delta*++offset, width, height));
    	addField("Provider Mode", _appCtx.getIACMethod(), new Rectangle(x, y+y_delta*++offset, width, height));
    	addField("Provider User", _appCtx.getProviderUser(), new Rectangle(x, y+y_delta*++offset, width, height));
    	addField("Provider Password", _appCtx.getProviderPassword(), new Rectangle(x, y+y_delta*++offset, width, height));
    	addField("Provider Url", _appCtx.getProviderUrl(), new Rectangle(x, y+y_delta*++offset, width, height));
       	addField("UDP Airwave", _appCtx.getUdpAirWave(), new Rectangle(x, y+y_delta*++offset, width, height));
    	
        jButnOK = new javax.swing.JButton();
        jButnOK.setActionCommand(AWT_OK);        
        jButnOK.addActionListener(this);

        getContentPane().setLayout(null);

        setFont(new java.awt.Font("Courier New", 0, 13));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jButnOK.setText("OK");
        getContentPane().add(jButnOK);
        jButnOK.setBounds(190, 180, 75, 29);

    }

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {                             
        setVisible(false);
        dispose();
    }                            
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SetupNetWorkDialog(new java.awt.Frame(), null).setVisible(true);
            }
        });
    }
    
    
    // Variables declaration - do not modify
    private javax.swing.JButton jButnOK;
    

    // End of variables declaration
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	
		AppLogger.debug2("SetupNetWorkDialog.actionPerformed" + e.getActionCommand());
		
		if(e.getActionCommand().equals(AWT_OK)){
			closeDialog(null);
		}
		
	}
	
    
}
