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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sourceforge.dscsim.controller.screen.Screen;
import net.sourceforge.dscsim.controller.screen.ScreenContent;
import net.sourceforge.dscsim.controller.screen.ScreenInterface;
import net.sourceforge.dscsim.controller.screen.ScreenLine;
import net.sourceforge.dscsim.controller.screen.ScreenLineList;
import net.sourceforge.dscsim.controller.screen.StateScreen;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreen;


public class MultiController extends Thread implements MouseListener, Constants, BusListener, KeyListener {

	private Image _imageDsc = null;
	private static int _x_offset = 0; 
	private static int _y_offset = 0;
	private volatile boolean _powerOn = false;
	
	private static long _paintElapsedTime = 0;
	private final static long PAINT_EXPIRE_TIME = 1000;//Screen should be no older than 1 second
	
	private static final int _width_numkey = 40, _height_numkey = 40;
	private static final int _width_arrverkey = 50, _height_arrverkey = 40;	
	private static final int _width_arrhorkey = 35, _height_arrhorkey = 80;
	private static final int _width_funckey = 50, _height_funckey = 60;
	private static final int _width_distrkey = 60, _height_distrkey = 55;
	
	
	private ArrayList _oButtons = null;
	private static final Button _oNoButton = new Button();
	private Button _oLastPressed = _oNoButton;
	
	public String _text = "N/A";
	private ScreenContent _oContent = null;
	
	
	/*lcd screen*/
	private Object screenGuard = new Object();
	private java.awt.Component lcd = null;
	private Container container = null;

	
	/*
	 * for minimizing keyboard events.
	 */
	private int _lastKeyAction = KeyEvent.KEY_RELEASED;
	
	
	private int[][] _numKeys = {
		{439,58},  {494,58},  {551,58},
		{439,117}, {494,117}, {551,117},
		{439,177}, {494,177}, {551,177},
		{439,232}, {494,232}, {551,232}	
	}; 
	
	private int [][] _distressKeys  = {
		{88,337}			
	};
	
	private int [][] _funcKeys  = {
		{183,324}, {254,324}, {324,324}			
	};
		
	private int [][] _verArrowKeys  = {
			{488,302},  //up
			{488,368},  //down
		};
	private int [][] _horArrowKeys  = {
		{440,316},  //left
		{555,316}   //right
	};
		
	private Container _oContainer = null;
	private boolean _initialized = false;
  	
	//button shading
    private AlphaComposite _ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
    private BasicStroke _stroke = new BasicStroke(2.0f);
    
    private InstanceContext _oContext = null;


	private MultiController(InstanceContext oSessionContext){
		AppLogger.debug("MultiController ctor called");
		setName("Controller.MultiController");
		_oContext = oSessionContext;
		_initialized = false;
		
		//start auto refreshin after so many seconds.
		_paintElapsedTime = System.currentTimeMillis()+2000;
			
		start();
		
	}

	public boolean isInitialized() {
			return _initialized;
	}
		
	public static MultiController getInstance(InstanceContext oSessionContext) {

		return new MultiController(oSessionContext);

	}
	
	public void init(Container oContainer, int x, int y) {
		
		AppLogger.debug("MultiController.init - started");
				
		_oContainer = oContainer;
		_x_offset = x;
		_y_offset = y;
					
		MediaTracker oImageLoader = new MediaTracker(_oContainer);
		
		try {
					
				//AppLogger.debug2("MultiController.init - getCodeBase="+ _oContainer.getCodeBase().toString());
							
				_imageDsc = Toolkit.getDefaultToolkit().getImage(SKIN_ICM504);
//				_imageDsc = _oContainer.getImage(_oContainer.getCodeBase(), SKIN_ICM504);
			
				oImageLoader.addImage(_imageDsc, 0);
			
				oImageLoader.waitForAll();		

				AppLogger.debug("MultiController.init - waitForAll ended");

		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}
				
		_oButtons = createButtons();
	
		AppLogger.debug("MultiController.init - end");
		
		_initialized = true;
				
	}	
	
	
	private synchronized ScreenContent getScreenContent(){
		return _oContent;
	}
	public  void setScreenContent(Object oScreen) {
		
		synchronized (screenGuard){
			if(oScreen instanceof JScreen
					|| oScreen instanceof net.sourceforge.dscsim.controller.screen.Screen){
				this.setLcdOff();
				java.awt.Component nxtlcd = (java.awt.Component)oScreen;
				this.setLcdOn(nxtlcd);
			} else {	
				this.setLcdOff();
				_oContent = (ScreenContent)oScreen;		
				_oContainer.repaint();			
			}			
		}
		
	}

	private int calculateMaxLines(Font o, int yOffset){
		int maxLines = 0;
		int fontHeight = _oContainer.getFontMetrics(o).getHeight();
		
		int screenHeight = _screenSize[1][1] - _screenSize[0][1] - yOffset ;
		
		maxLines = screenHeight/fontHeight;
		
		//AppLogger.debug("max lines for fontHeight of " + fontHeight + " is " + maxLines);
		
		return maxLines;
	}
	
	public void paint(Graphics g, Container container ){		
		
        if(_imageDsc == null)
    			return;
    
        Graphics2D g2 = (Graphics2D) g; 
        
        
        /*draw the dsc image*/
    	//int x = (_oContainer.getSize().width - _imageDsc.getWidth(_oContainer))/2;
    	//int y = (_oContainer.getSize().height - _imageDsc.getHeight(_oContainer))/2;
    
    	g2.drawImage(_imageDsc, _x_offset, _y_offset, _oContainer);
			

    	if(_powerOn)
    		paintScreen(g2, container);
    	else
    		paintBlank(g2);	
		
		paintButtons(g2);				
		_paintElapsedTime = System.currentTimeMillis();				
	}
	
	/**
	 * 
	 * @param g
	 */
	private void paintBlank(Graphics2D g2){
		        		
	    RoundRectangle2D.Float oRec = new RoundRectangle2D.Float(DISPLAY_X, DISPLAY_Y, DISPLAY_W, DISPLAY_H, 15, 15);
	   
        Color fg3D = Color.GRAY;

	    g2.setPaint(fg3D);
	    g2.fill(oRec);
			
	}
	/**
	 * paintButtons - handle graphics for buttons.
	 */
	private void paintButtons(Graphics2D g2){
		//hutton shading for press and release 
		if(_oLastPressed !=null 
				&& _oLastPressed.getAction().equals(Button.PRESSED)){

			//AppLogger.debug("Controller.paint - oLastPressed "+ _oLastPressed.getKeyId());	              
	    	    int xb = _oLastPressed.getX();
	    	    int yb = _oLastPressed.getY();
	    	    int wb = _oLastPressed.getWidth();
	    	    int hb = _oLastPressed.getHeight();
	    	    
	    	    RoundRectangle2D.Float oRec = new RoundRectangle2D.Float(xb, yb, wb, hb,15,15);
	    	    g2.setStroke(_stroke);
	    	    g2.setComposite(_ac);
	    	    g2.fill(oRec);
	    			
		}
		
	}
	
	
	private void handleState(StateScreen scr, Container con, Graphics2D g2d){
		
		/*
		Screen sc = scr.getScreen(DISPLAY_X,DISPLAY_Y,DISPLAY_W, DISPLAY_H);
		//sc.paint(g2d);
		
		for(int i=0; i<10;i++){			
			g2d.drawString("hell", i*5, i*5);			
		}
		*/
	}
	/**
	 * paintScreen - paint device's screen area.
	 * @param g Graphics
	 */
	private void paintScreen(Graphics2D g2, Container container) {
		    
		//AppLogger.debug("Controller.paint - started");
                        
		if(this.lcd != null){
			//lcd.paint(g2);
			return;
		}
        ScreenContent oContent = getScreenContent();
        if(oContent != null) {
    		//AppLogger.debug("Controller.paint - adding ScreenContent lines=" + oContent.getLines());      
	    	
	     	Font thisFont = new Font("Courier", Font.PLAIN, 14);
	            
	    		ScreenLineList oLines = oContent.getLines();
	    		ScreenLine oLine = null;
	    		int fontHeight = _oContainer.getFontMetrics(thisFont).getHeight();
	   		
	    		int xOffset = 0, yOffset = 15;
	    		int screenMaxLines = calculateMaxLines(thisFont, yOffset); 
	    		
	    		//laydown headers first
	    		int headerCount = oContent.getHeaderCount();
	    		int displayLine = 0;
	    		for(; displayLine < headerCount; displayLine++){
	    			oLine = (ScreenLine)oLines.get(displayLine);
	    			AppLogger.debug("printing header#" + displayLine + ":" + oLine.getDisplayText());
	    			
	    			xOffset = oLine.getAttributeIntValue("column");
				    g2.setColor(Color.black);	 				
	        		g2.setFont(thisFont);	 
	        		g2.drawString(oLine.getDisplayText(), 
	        				_screenSize[0][0]+xOffset, 
	        				_screenSize[0][1]+yOffset + displayLine*fontHeight);	      				
	    			
	    		}
	    		
	    		int footerCnt = oContent.getFooterCount();
	    		int maxBodyLines = screenMaxLines - footerCnt - headerCount;
	    		int visibleLines = oLines.getVisibleCount();
	    		int actBodyLines = visibleLines - footerCnt - headerCount;
	    		int calcBodyLines = actBodyLines > maxBodyLines ? maxBodyLines : actBodyLines;
	    		//AppLogger.debug2("headers=" + headerCount + ",body=" +maxBodyLines + ",footer="+footerCnt);
	    			
	    		 
	    		int displayFrom = oContent.getDisplayedFrom();   	
	    		displayFrom = displayFrom > -1 ? displayFrom : headerCount;
	    			
	    		int displayTo = oContent.getDisplayedTo();
	    		displayTo = displayTo > -1 ? displayTo : displayFrom + calcBodyLines-1;
	      		
	    		int activeLine = oContent.getActiveLine();
	    		
	       	//AppLogger.debug2("displayFrom=" + displayFrom + ",displayTo=" +displayTo + ",activeLine="+activeLine);
	       	    		
	    		if(activeLine > displayTo){
	    			displayFrom++;
	    			displayTo++;
	    		} else if(activeLine < displayFrom){
	    			displayFrom--;
	    			displayTo--;
	    		}
	    		
	    		if(displayTo == visibleLines)
	    			displayTo = oContent.getFooterCount()-1;
	    		
	    		if(displayFrom == 0)
	    			displayFrom  = oContent.getHeaderCount();
	    		
	    		oContent.setDisplayedFrom(displayFrom);
	    		oContent.setDisplayedTo(displayTo);
	    		int leading = 2;
	  		
	
	    		//laydown body 
	    		for(int i=displayFrom; i < displayTo+1; i++, displayLine++){
	 
	        		oLine = (ScreenLine)oLines.get(i);
	       		//AppLogger.debug("printing body#" + displayLine + ":" + oLine.getDisplayText());
	        		/*
	        		if(oLine.isVisible() == false)
	        			continue;
	        		*/
	        		
			 	if(oContent.getActiveLine() == i
			 				&&oContent.isCursorOn())
					g2.setColor(Color.red);
				else
					g2.setColor(Color.black);
				
	        		g2.setFont(thisFont);       		
	        		xOffset = oLine.getAttributeIntValue("column");
	
	        		g2.drawString(oLine.getDisplayText(), 
	        				_screenSize[0][0]+xOffset, 
	        				_screenSize[0][1]+yOffset + displayLine*fontHeight+(leading*i));
	        		
		        		      		             			
	        	}
	    		
	    		//laydown footers
	    		displayLine = screenMaxLines - footerCnt+1;
	    		for(int k= visibleLines-footerCnt; k < visibleLines; k++, displayLine++){
	        		oLine = (ScreenLine)oLines.get(k);
	    			//AppLogger.debug("printing footer#" + displayLine + ":" + oLine.getDisplayText());
	    			
					g2.setColor(Color.black);	 				
			    		g2.setFont(thisFont);	 
			   		xOffset = oLine.getAttributeIntValue("column");
			    		g2.drawString(oLine.getDisplayText(), 
			    				_screenSize[0][0]+xOffset, 
			    				_screenSize[0][1]+yOffset + displayLine*fontHeight);
			      				
	 			
	        	}
	    		
        }
                     
        
  }	
  
	
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//AppLogger.debug("Controller.mouseClicked");

		//AppLogger.debug("Controller.mouseClicked x=" + arg0.getPoint().x + ", y=" + arg0.getPoint().y);
		
	}

	private ArrayList createButtons(){

		ArrayList oButtons = new ArrayList();
		Button oButton = null;
		
		//numberpad
		for(int i = 0; i < _numKeys.length; i++) {
				oButton = new Button(
					_numKeys[i][0] + _x_offset,
					_numKeys[i][1] + _y_offset,
					_width_numkey,
					_height_numkey,
					arrKeyPad[i]);
								
				oButtons.add(oButton);
			    					
		}
		//DISTRESS	
		for(int i = 0; i < _distressKeys.length; i++) {

			oButton = new Button(
				_distressKeys[i][0] + _x_offset,
				_distressKeys[i][1] + _y_offset,
				_width_distrkey,
				_height_distrkey,
				distressPad[i]);
								
			oButtons.add(oButton);
						
		}	
		
		//FK_CRL, FK_CALL, FK_ENTER	
		for(int i = 0; i < _funcKeys.length; i++) {

			oButton = new Button(
				_funcKeys[i][0] + _x_offset,
				_funcKeys[i][1] + _y_offset,
				_width_funckey,
				_height_funckey,
				arrFuncPad[i]);
								
			oButtons.add(oButton);
						
		}		
		
		//LEFT, RIGHT ...	
		for(int i = 0; i < _horArrowKeys.length; i++) {

			oButton = new Button(
					_horArrowKeys[i][0] + _x_offset,
					_horArrowKeys[i][1] + _y_offset,
					_width_arrhorkey,
					_height_arrhorkey,
					arrHorArrowPad[i]);
									
			oButtons.add(oButton);
						
		}		
		//UP DOWN
		for(int i = 0; i < _verArrowKeys.length; i++) {

			oButton = new Button(
					_verArrowKeys[i][0] + _x_offset,
					_verArrowKeys[i][1] + _y_offset,
					_width_arrverkey,
					_height_arrverkey,
					arrVerArrowPad[i]);
									
			oButtons.add(oButton);
						
		}		
		
		return oButtons;
	}

	private Button getButtonPressed(MouseEvent arg0) {
		int xpos = 0, ypos = 0;
	    
		xpos = arg0.getPoint().x;
		ypos = arg0.getPoint().y;
							
		Button oButton = null;
		for(int i = 0; i < _oButtons.size(); i++) {
					
			oButton = (Button)_oButtons.get(i);
			if(oButton.isOver(xpos, ypos)){
				return oButton;
			}
									
		}
		
		return _oNoButton;
	}

	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		//AppLogger.debug("Controller.mousePressed nokey.");					

		Button oButton = getButtonPressed(arg0);
		oButton.setPressed();
		
		String keyId = oButton.getKeyId();
				
		if(NO_KEY.equals(keyId) == false) {
			_oContext.getBus().publish(new BusMessage(null, oButton));
			_oLastPressed = oButton;			
		}

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {

		Button oButton = getButtonPressed(arg0);
		oButton.setRelease();

		String keyId = oButton.getKeyId();
			
		_oContext.getBus().publish(new BusMessage(null, oButton));
		_oLastPressed = _oNoButton;
	 
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
				
		//AppLogger.debug("Controller.mouseEntered x=" + arg0.getPoint().x + ", y=" + arg0.getPoint().y);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		//TODO Auto-generated method stub

		//AppLogger.debug("Controller.mouseExited x=" + arg0.getPoint().x + ", y=" + arg0.getPoint().y);
		
	}

	/* (non-Javadoc)
	 * @see applet.BusListener#signal(applet.BusMessage)
	 */
	public void signal(BusMessage oMessage) {
		
		//AppLogger.debug("Contoller.signal called" + oMessage.toString());		
		String msgType = oMessage.getType();
		
		if(msgType.equals(BusMessage.MSGTYPE_KEY)){
			
			String keyId = oMessage.getButtonEvent().getKeyId();
			if(DSC_POWERED_OFF.equals(keyId)){
				_powerOn = false;
			}else if(DSC_POWERED_ON.equals(keyId)){
				_powerOn = true;
			}
		
			_oLastPressed = oMessage.getButtonEvent();			
			_oContainer.repaint();	

		}
		
	}
	/* primarily for blinking.
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){		
		boolean bContinue = true;
		while(bContinue){
			long timeDiff = System.currentTimeMillis() - _paintElapsedTime;			
			//AppLogger.info("MultiController.run - timeDiff =" + timeDiff);
			ScreenContent oScreen = getScreenContent();
			if((this.lcd != null && ((JScreen)lcd).forceRefresh())
					 || (lcd != null && ((ScreenInterface)lcd).forceRefresh())){				
				if(timeDiff >= PAINT_EXPIRE_TIME){
					//screen has expired repaint the children and not the grid;		
					
					synchronized (this.screenGuard){
						if(lcd != null)				
							lcd.repaint();
						
						if(oScreen != null)
							_oContainer.repaint();
					}
				} else {	
					try{									
						long sleepTime = PAINT_EXPIRE_TIME - timeDiff;							
						//AppLogger.info("MultiController.run 1 - sleepTime =" + sleepTime);
						Thread.sleep(sleepTime);	
					}catch(InterruptedException oEx){
						//AppLogger.error(oEx);
						AppLogger.error(oEx);
						bContinue = false;
						continue;
					}
				}			
			} else {
				
				try{								
					long sleepTime = PAINT_EXPIRE_TIME;					
					//AppLogger.info("MultiController.run 2 - sleepTime =" + sleepTime);
					Thread.sleep(sleepTime);				
				}catch(InterruptedException oEx){
					//AppLogger.error(oEx);
					AppLogger.error(oEx);
					bContinue = false;
					continue;
				}								
			}									
		}
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void handleKeyEvent(KeyEvent event, int action){
		
		String keyCode = String.valueOf(event.getKeyCode());
		for(int i= 0; i < Constants.keyCode2Buttons.length; i++){
			
			if(keyCode2Buttons[i][0].equals(keyCode)){
				
				Button oButton = null;
				for(int j = 0; j < _oButtons.size(); j++) {
							
					oButton = (Button)_oButtons.get(j);
					if(oButton.getKeyId().equals(keyCode2Buttons[i][1])){
						
						if(KeyEvent.KEY_PRESSED == action){
							oButton.setPressed();			
							_oLastPressed = oButton;
						}else{
							oButton.setRelease();
							_oLastPressed = _oNoButton;
						}
						
						_oContext.getBus().publish(new BusMessage(null, oButton));
						
						return;
					}
											
				}
				
			}			
			
		}
			
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent arg0) {		
		if(_lastKeyAction == KeyEvent.KEY_RELEASED){
			_lastKeyAction = KeyEvent.KEY_PRESSED;
			handleKeyEvent(arg0, KeyEvent.KEY_PRESSED);
			AppLogger.debug2("keyPressed " + arg0.toString());
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent arg0) {
		if(_lastKeyAction == KeyEvent.KEY_PRESSED){
			_lastKeyAction = KeyEvent.KEY_RELEASED;
			handleKeyEvent(arg0, KeyEvent.KEY_RELEASED);
			AppLogger.debug2("keyReleased " + arg0.toString());
		}		
	}
	
	private void setLcdOn(java.awt.Component lcd){	
		this.lcd = lcd;
		this._oContent = null;		
		JFrame o = (JFrame)_oContainer;
		o.getContentPane().add(lcd, 0);
		/*if container is already visible then validate must be called 
		 * on the container - java api doc.*/
		o.getContentPane().validate();
		o.getContentPane().repaint();	
	}
	
	private void setLcdOff(){		
		//this.setScreenContent(null);	
		if(this.lcd != null){
			JFrame o = (JFrame)_oContainer;		
			o.getContentPane().remove(lcd);
			o.getContentPane().validate();		
			o.getContentPane().repaint();				
		}
		
		this.lcd = null;

	}
	
	public int getScreenX(){
		return this.DISPLAY_X;
	}	
	
	public int getScreenY(){
		return this.DISPLAY_Y;
	}

}

