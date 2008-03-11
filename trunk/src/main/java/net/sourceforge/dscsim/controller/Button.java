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

/**
 * Button class - each instance represents the keys on the dsc device.
 * @author William Pennoyer.
 */

public class Button implements java.io.Serializable, Constants {

	/**
	 * top x and y position of button. 
	 */
	private int _xpos = 0, _ypos = 0;

	/**
	 * width and height of button.
	 */
	private int _width = 0, _height = 0;

	/**
	 * id of button from Constants.
	 */
	private String _keyId = NO_KEY;

	/**
	 *relesed or pressed. 
	 */
	private String _action = RELEASED;

	/**
	 * 
	 */
	private boolean sticky = false;

	/**
	 * Button Constructor.
	 */
	public Button() {

	}

	/**
	 * Button Constructor.
	 * @param keyId
	 */
	public Button(String keyId) {
		_keyId = keyId;
	}

	/**
	 * Button Constructor.
	 * @param x - x position
	 * @param y - y position
	 * @param w - width
	 * @param h - height
	 * @param keyId - see Constants
	 */
	public Button(int x, int y, int w, int h, String keyId) {
		_xpos = x;
		_ypos = y;
		_width = w;
		_height = h;
		_keyId = keyId;

	}

	/**
	 * get left x position.
	 * @return
	 */
	public int getX() {
		return _xpos;
	}

	/**
	 * get right x  position
	 * @return
	 */
	public int getXRight() {
		return _xpos + _width;
	}

	/**
	 * get y top position.
	 * @return
	 */
	public int getY() {
		return _ypos;
	}

	/**
	 * get bottom y.
	 * @return
	 */
	public int getYBottom() {
		return _ypos + _height;
	}

	/**
	 * get width of button.
	 * @return
	 */
	public int getWidth() {
		return _width;
	}

	/**
	 * get height of button.
	 */
	public int getHeight() {
		return _height;
	}

	/**
	 * get id of button.
	 * @return
	 */
	public String getKeyId() {
		return _keyId;
	}

	/**
	 * set id of button.
	 * @param keyId
	 */
	public void setKeyId(String keyId) {
		_keyId = keyId;
	}

	/**
	 * set button pressed
	 */
	public void setPressed() {
		_action = PRESSED;
	}

	/**
	 * set button release.
	 */
	public void setRelease() {
		_action = RELEASED;
	}

	/**
	 * get release or pressed.
	 * @return
	 */
	public String getAction() {
		return _action;
	}

	/**
	 * are xpos and ypos over the button.
	 * @param xpos
	 * @param ypos
	 * @return boolean.
	 */
	public boolean isOver(int xpos, int ypos) {

		if ((xpos >= _xpos) && (xpos <= _xpos + _width) && (ypos >= _ypos)
				&& (ypos <= _ypos + _height)) {
			return true;
		}

		return false;
	}

	/**
	 * is button Master Power Switch.
	 * @return boolean.
	 */
	public boolean isMasterSwitch() {
		return DSC_POWERED_OFF.equals(_keyId) || DSC_POWERED_ON.equals(_keyId);
	}

	/**
	 * @return the sticky
	 */
	public boolean isSticky() {
		return sticky;
	}

	/**
	 * @param sticky the sticky to set
	 */
	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}

}
