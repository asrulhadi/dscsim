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
import java.awt.Container;
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

import javax.swing.JFrame;

import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreen;
import net.sourceforge.dscsim.controller.utils.AppLogger;

public class MultiController extends Thread implements MouseListener,
		Constants, BusListener, KeyListener {

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

	private ArrayList<Button> _oButtons = null;
	private static final Button noButton = new Button();
	private Button lastKeyPressed = noButton;
	private Button callButton = noButton;
	private Button lastClick = noButton;

	public String _text = "N/A";

	/*lcd screen*/
	private Object screenGuard = new Object();
	private java.awt.Component lcd = null;
	/*
	 * for minimizing keyboard events.
	 */
	private int _lastKeyAction = KeyEvent.KEY_RELEASED;

	private int[][] _numKeys = { { 439, 58 }, { 494, 58 }, { 551, 58 },
			{ 439, 117 }, { 494, 117 }, { 551, 117 }, { 439, 177 },
			{ 494, 177 }, { 551, 177 }, { 439, 232 }, { 494, 232 },
			{ 551, 232 } };

	private int[][] _distressKeys = { { 88, 337 } };

	private int[][] _funcKeys = { { 183, 324 }, { 254, 324 }, { 324, 324 } };

	private int[][] _verArrowKeys = { { 488, 302 }, //up
			{ 488, 368 }, //down
	};
	private int[][] _horArrowKeys = { { 440, 316 }, //left
			{ 555, 316 } //right
	};

	private Container _oContainer = null;
	private boolean _initialized = false;

	//button shading
	private AlphaComposite _ac = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 0.4f);
	private BasicStroke _stroke = new BasicStroke(2.0f);

	private InstanceContext _oContext = null;

	private MultiController(InstanceContext oSessionContext) {
		AppLogger.debug("MultiController ctor called");
		setName("Controller.MultiController");
		_oContext = oSessionContext;
		_initialized = false;

		//start auto refreshin after so many seconds.
		_paintElapsedTime = System.currentTimeMillis() + 2000;

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
		this.callButton = this.getButton(FK_CALL);

		AppLogger.debug("MultiController.init - end");

		_initialized = true;

	}

	public void setScreenContent(Object oScreen) {

		synchronized (screenGuard) {
			if (oScreen instanceof JScreen) {
				this.setLcdOff();
				java.awt.Component nxtlcd = (java.awt.Component) oScreen;
				this.setLcdOn(nxtlcd);
			}
		}

	}

	/*
	 * paint called by AWT via Panel. 
	 * @param g
	 * @param container
	 */
	public void paint(Graphics g, Container container) {

		if (_imageDsc == null)
			return;

		Graphics2D g2 = (Graphics2D) g;

		/*draw the dsc image*/
		g2.drawImage(_imageDsc, _x_offset, _y_offset, _oContainer);

		paintButtons(g2);
		_paintElapsedTime = System.currentTimeMillis();
	}

	/**
	 * 
	 * @param g
	 */
	private void paintBlank(Graphics2D g2) {

		RoundRectangle2D.Float oRec = new RoundRectangle2D.Float(DISPLAY_X,
				DISPLAY_Y, DISPLAY_W, DISPLAY_H, 15, 15);

		Color fg3D = Color.GRAY;

		g2.setPaint(fg3D);
		g2.fill(oRec);

	}

	/**
	 * paintButtons - handle graphics for buttons.
	 */
	private void paintButtons(Graphics2D g2) {
		// hutton shading for press and release

		for (Button b : this._oButtons) {
			if (b.getAction().equals(Button.PRESSED)) {
				int xb = b.getX();
				int yb = b.getY();
				int wb = b.getWidth();
				int hb = b.getHeight();

				RoundRectangle2D.Float oRec = new RoundRectangle2D.Float(xb,
						yb, wb, hb, 15, 15);
				g2.setStroke(_stroke);
				g2.setComposite(_ac);
				g2.fill(oRec);
			}
		}
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//AppLogger.debug("Controller.mouseClicked");

		//AppLogger.debug("Controller.mouseClicked x=" + arg0.getPoint().x + ", y=" + arg0.getPoint().y);

	}

	private ArrayList<Button> createButtons() {

		ArrayList<Button> oButtons = new ArrayList<Button>();
		Button oButton = null;

		//numberpad
		for (int i = 0; i < _numKeys.length; i++) {
			oButton = new Button(_numKeys[i][0] + _x_offset, _numKeys[i][1]
					+ _y_offset, _width_numkey, _height_numkey, arrKeyPad[i]);

			oButtons.add(oButton);

		}
		//DISTRESS	
		for (int i = 0; i < _distressKeys.length; i++) {

			oButton = new Button(_distressKeys[i][0] + _x_offset,
					_distressKeys[i][1] + _y_offset, _width_distrkey,
					_height_distrkey, distressPad[i]);

			oButtons.add(oButton);

		}

		//FK_CRL, FK_CALL, FK_ENTER	
		for (int i = 0; i < _funcKeys.length; i++) {

			oButton = new Button(_funcKeys[i][0] + _x_offset, _funcKeys[i][1]
					+ _y_offset, _width_funckey, _height_funckey, arrFuncPad[i]);

			oButtons.add(oButton);

		}

		//LEFT, RIGHT ...	
		for (int i = 0; i < _horArrowKeys.length; i++) {

			oButton = new Button(_horArrowKeys[i][0] + _x_offset,
					_horArrowKeys[i][1] + _y_offset, _width_arrhorkey,
					_height_arrhorkey, arrHorArrowPad[i]);

			oButtons.add(oButton);

		}
		//UP DOWN
		for (int i = 0; i < _verArrowKeys.length; i++) {

			oButton = new Button(_verArrowKeys[i][0] + _x_offset,
					_verArrowKeys[i][1] + _y_offset, _width_arrverkey,
					_height_arrverkey, arrVerArrowPad[i]);

			oButtons.add(oButton);

		}

		return oButtons;
	}
	/**
	 * get Button by id.
	 * @param keyID
	 * @return
	 */
	public Button getButton(String keyID){
		
		for(Button b: this._oButtons){
			if(b.getKeyId().equals(keyID)){
				return b;
			}
		}
		return null;
	}

	private Button getButtonPressed(MouseEvent arg0) {
		int xpos = 0, ypos = 0;

		xpos = arg0.getPoint().x;
		ypos = arg0.getPoint().y;

		Button oButton = null;
		for (int i = 0; i < _oButtons.size(); i++) {

			oButton = (Button) _oButtons.get(i);
			if (oButton.isOver(xpos, ypos)) {
				return oButton;
			}

		}

		return noButton;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {

		Button oButton = getButtonPressed(arg0);
		String keyId = oButton.getKeyId();
		
		/*
		 * last clicked is anything including non-key or buttons.
		 * */
		this.lastClick = oButton;
		
		//if call button already pressed, then do nothing.
		if((keyId.equals(FK_CALL))){
			if(oButton.getAction().equals(PRESSED)){
				return;				
			} else if(arg0.getButton() != MouseEvent.BUTTON1){
				oButton.setPressed();
				this.lastKeyPressed = oButton;
				/*send out a no button event to cause a repaint*/
				this._oContainer.repaint();
				return;
			}	
		}
		
		oButton.setPressed();
		if (NO_KEY.equals(keyId) == false) {
			_oContext.getBus().publish(new BusMessage(null, oButton));
			this.lastKeyPressed = oButton;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {

		Button oButton = getButtonPressed(arg0);
		String keyId = oButton.getKeyId();
		int button = arg0.getButton();
		
		if (NO_KEY.equals(keyId) == false
				&& FK_CALL.equals(lastKeyPressed.getKeyId())==true
				&& button != MouseEvent.BUTTON1) {		
			return;
		} 
					
		this.callButton.setRelease();
		this.lastKeyPressed.setRelease();	
		
		if(this.lastClick.getKeyId().equals(NO_KEY)==false){
			_oContext.getBus().publish(new BusMessage(null, lastClick));	
		}
		
		return;

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

		if (msgType.equals(BusMessage.MSGTYPE_KEY)) {

			String keyId = oMessage.getButtonEvent().getKeyId();
			if (DSC_POWERED_OFF.equals(keyId)) {
				_powerOn = false;
				this.setLcdHidden();
			} else if (DSC_POWERED_ON.equals(keyId)) {
				_powerOn = true;
				this.setLcdOn(this.lcd);
			}

			lastKeyPressed = oMessage.getButtonEvent();
			_oContainer.repaint();

		}

	}

	/* primarily for blinking.
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		boolean bContinue = true;
		while (bContinue) {
			long timeDiff = System.currentTimeMillis() - _paintElapsedTime;
			//AppLogger.info("MultiController.run - timeDiff =" + timeDiff);

			if ((this.lcd != null && ((JScreen) lcd).forceRefresh())
					|| (lcd != null && ((ActionScreen) lcd).forceRefresh())) {
				if (timeDiff >= PAINT_EXPIRE_TIME) {
					//screen has expired repaint the children and not the grid;		

					synchronized (this.screenGuard) {
						if (lcd != null) {
							lcd.invalidate();
							lcd.repaint();
						}

					}
				} else {
					try {
						long sleepTime = PAINT_EXPIRE_TIME - timeDiff;
						//AppLogger.info("MultiController.run 1 - sleepTime =" + sleepTime);
						Thread.sleep(sleepTime);
					} catch (InterruptedException oEx) {
						//AppLogger.error(oEx);
						AppLogger.error(oEx);
						bContinue = false;
						continue;
					}
				}
			} else {

				try {
					long sleepTime = PAINT_EXPIRE_TIME;
					//AppLogger.info("MultiController.run 2 - sleepTime =" + sleepTime);
					Thread.sleep(sleepTime);
				} catch (InterruptedException oEx) {
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

	private void handleKeyEvent(KeyEvent event, int action) {

		String keyCode = String.valueOf(event.getKeyCode());
		for (int i = 0; i < Constants.keyCode2Buttons.length; i++) {

			if (keyCode2Buttons[i][0].equals(keyCode)) {

				Button oButton = null;
				for (int j = 0; j < _oButtons.size(); j++) {

					oButton = (Button) _oButtons.get(j);
					if (oButton.getKeyId().equals(keyCode2Buttons[i][1])) {

						if (KeyEvent.KEY_PRESSED == action) {
							oButton.setPressed();
							lastKeyPressed = oButton;
						} else {
							oButton.setRelease();
							lastKeyPressed = noButton;
						}

						_oContext.getBus().publish(
								new BusMessage(null, oButton));

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
		if (_lastKeyAction == KeyEvent.KEY_RELEASED) {
			_lastKeyAction = KeyEvent.KEY_PRESSED;
			handleKeyEvent(arg0, KeyEvent.KEY_PRESSED);
			//AppLogger.debug2("keyPressed " + arg0.toString());
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent arg0) {
		if (_lastKeyAction == KeyEvent.KEY_PRESSED) {
			_lastKeyAction = KeyEvent.KEY_RELEASED;
			handleKeyEvent(arg0, KeyEvent.KEY_RELEASED);
			//AppLogger.debug2("keyReleased " + arg0.toString());
		}
	}

	private void setLcdOn(java.awt.Component lcd) {
		this.lcd = lcd;
		JFrame o = (JFrame) _oContainer;

		if (this._powerOn == true) {
			o.getContentPane().add(lcd, 0);
			/*if container is already visible then validate must be called 
			 * on the container - java api doc.*/
			o.getContentPane().validate();
			o.getContentPane().repaint();
		}

	}

	private void setLcdOff() {
		//this.setScreenContent(null);	
		if (this.lcd != null) {
			JFrame o = (JFrame) _oContainer;
			o.getContentPane().remove(lcd);
			o.getContentPane().validate();
			o.getContentPane().repaint();
		}

		this.lcd = null;

	}

	private void setLcdHidden() {
		//this.setScreenContent(null);	
		if (this.lcd != null) {
			JFrame o = (JFrame) _oContainer;
			o.getContentPane().remove(lcd);
			o.getContentPane().validate();
			o.getContentPane().repaint();
		}

	}

	public int getScreenX() {
		return Constants.DISPLAY_X;
	}

	public int getScreenY() {
		return Constants.DISPLAY_Y;
	}

}
