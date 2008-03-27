/*
 * Created on 07.01.2007
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
 
package net.sourceforge.dscsim.common;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Dimension2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JFrame;

import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * AppFrame is a JFrame that remembers its last position and size between sessions.
 * The information is stored in the data directory in file called application.properties and has the
 * instance's MMSI appended to it.
 * @author William Pennoyer
 */
public abstract class AppFrame extends JFrame implements Constants {
	
	/**
	 * Label for last remembered x coordinate.
	 */
	public final static String DSC_WIN_X = ".x";
	/**
	 * Label for last remembered y coordinate.
	 */
	public final static String DSC_WIN_Y = ".y";
	/**
	 * Label for last remembered width.
	 */
	public final static String DSC_WIN_W = ".width";
	
	/**
	 * Label for last remembered height.
	 */
	public final static String DSC_WIN_H = ".height";
		
	/**
	 * Label derived from name of subclass class.
	 */
	public final String PREFIX = new String(getClass().getName()).toLowerCase();
	
	/**
	 * Properties are stored in a file with the session MMSI appended to its name.
	 */
    private final String _mmsi   = System.getProperty("parameter.individual.mmsi");
    
    /**
     * Full name of file with stored values.
     */
    private String _configFile = null; 
    
    /**
     * Base name of filename.
     */
	public final static String APPL_PROPS = "application.properties";
	
	/**
	 * Properties class for storage of coordinates and size.
	 */
	private static Properties _appSettings = null;

	/**
	 * Constructor
	 *
	 */
	public AppFrame(){
		super();
		
		_configFile = STORE_BASE + APPL_PROPS;
		if(_mmsi != null){
			_configFile = _configFile + "." + _mmsi;
		}
		
		
	}
	
	/**
	 * Determine position and size for the instantiated JFrame.
	 */
	protected void init(){
	
		setBounds(getControllerScreenPosition());
		addComponentListener(new ComponentListener(){

			public void componentResized(ComponentEvent arg0) {
				//AppLogger.debug2(arg0.toString());												
				int w = arg0.getComponent().getWidth();
				int h = arg0.getComponent().getHeight();											
			  	Properties props = getAppSettings();	
			  	props.setProperty(PREFIX+DSC_WIN_W, String.valueOf(w));
			  	props.setProperty(PREFIX+DSC_WIN_H, String.valueOf(h));	  						
			}

			public void componentMoved(ComponentEvent arg0) {
				//AppLogger.debug2(arg0.toString());												
				int x = arg0.getComponent().getX();
				int y = arg0.getComponent().getY();											
			  	Properties props = getAppSettings();				  	
			  	props.setProperty(PREFIX+DSC_WIN_X, String.valueOf(x));
			  	props.setProperty(PREFIX+DSC_WIN_Y, String.valueOf(y));	  		
			}

			public void componentShown(ComponentEvent arg0) {}
			public void componentHidden(ComponentEvent arg0) {}
			
		});
		
		this.addWindowListener(new WindowAdapter(){
		
			public void windowClosing(WindowEvent evt){
				storeAppSettings();
			}
		});
		
	}

	/**
	 * If no file is found, previous or default, then try to calculate initial values-
	 * @return Rectangle
	 */
	private Rectangle getControllerScreenPosition(){
  	  	
	  	Properties props = getAppSettings();
	  	
		Dimension2D oDim = Toolkit.getDefaultToolkit().getScreenSize();	
	
		String x1 = props.getProperty(PREFIX+DSC_WIN_X);
		String y1 = props.getProperty(PREFIX+DSC_WIN_Y);
		String w1 = props.getProperty(PREFIX+DSC_WIN_W);
		String h1 = props.getProperty(PREFIX+DSC_WIN_H);
		
		int x2 = x1 != null && x1.length()>0 ? Integer.valueOf(x1).intValue() : 0;		
		int y2 = y1 != null && y1.length()>0 ? Integer.valueOf(y1).intValue() : 0;
		int w2 = w1 != null && w1.length()>0 ? Integer.valueOf(w1).intValue() : (int)oDim.getWidth()*43/64;
		int h2 = h1 != null && h1.length()>0 ? Integer.valueOf(h1).intValue() : (int)oDim.getHeight()*45/64;
		
	  	Rectangle rec = new Rectangle(x2, y2, w2, h2);
	  	 	
	  	AppLogger.debug2("net.sourceforge.dscsim.gui.AppFrame " + rec.toString());
	  	return rec;

  }
  

  /**
   * Look for an existing file this mmsi. If one doesn't exist, use a default file.
   * @return Properities
   */
  private Properties getAppSettings() {
 		
	  if(_appSettings == null){
		  try {
			synchronized(this){		
				if(_appSettings == null) {						
					_appSettings =  new Properties();	
					
					//look for an existing file this mmsi
					//if one doesn't exist, use a default file.
					String configFile = _configFile;
					File file = new File(configFile);
					if(file.exists() == false){
						configFile = STORE_BASE + APPL_PROPS;
					}
					FileInputStream oFile = new FileInputStream(configFile);
		    		InputStream setupOutput = new DataInputStream(oFile);		    				
		    		//InputStream propInput = new DataInputStream(this.getClass().getResourceAsStream(STORE_BASE + APP_SETTINGS));	    	 	    			
					_appSettings.load(setupOutput);	
					
				} 	    					
			}	    			
		}catch(Exception oEx){
			AppLogger.error(oEx);
		}		    					
	}			    		
	return _appSettings;
  }

  /**
   * Store the setting when the JFrame is closed.
   *
   */
  private synchronized void storeAppSettings() {
	  
	
  	if(_appSettings != null){	
  		AppLogger.debug2("AppFrame.storeAppSettings saving.");

  		try {	  
  			FileOutputStream oFile = new FileOutputStream(_configFile);
			OutputStream setupOutput = new DataOutputStream(oFile);		    				
			_appSettings.store(setupOutput,  "");		
  		}catch(Exception oEx){
    		AppLogger.error(oEx);
  		}		        						
	}				    	
  }
  
	public void paint(java.awt.Graphics g){
		super.paint(g);
	}

}


