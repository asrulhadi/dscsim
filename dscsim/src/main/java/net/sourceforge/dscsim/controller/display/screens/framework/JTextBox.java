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
 * the Initial Developer are Copyright (C) 2006, 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */

package net.sourceforge.dscsim.controller.display.screens.framework;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.sourceforge.dscsim.controller.BusMessage;

/**
 * Provided a simple Component that display text in a small windows.
 * 
 * @author wpenn
 */
public class JTextBox extends JScreenComponent {

	/**
	 * 
	 */
	private String value = "";

	private long lastChanged = 0;
	private long blinkPeriod = 0;
	private int changedCount = 0;

	/**
	 * TextBox constructor.
	 * @param row
	 * @param col
	 * @param width in cols unit.
	 * @param height in rows units.
	 */

	public JTextBox(int row, int col, int width, int height) {
		super(row, col, width, height);
	}

	/**
	 * Override paint.
	 */
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		FontMetrics fontMetrics = g2d.getFontMetrics();
		BasicStroke stroke = new BasicStroke(2.0f);

		Color fg3D = Color.BLACK;
		g2d.setPaint(fg3D);
		int height = this.getHeight();
		int xscale = this.getScreen().getXScale();
		int yscale = this.getScreen().getYScale();
		g2d.setFont(this.getParent().getFont());

		long currTime = 0;
		long elapsed = 0;
		if (this.blinkPeriod > 0) {
			currTime = System.currentTimeMillis();
			elapsed = currTime - this.lastChanged;
		}

		if (elapsed > this.blinkPeriod) {
			changedCount++;
			this.lastChanged = currTime;
		}

		if (this.blinkPeriod < 0 || changedCount % 2 == 0) {
			for (int p = 0; value != null && p < value.length(); p++) {
				g2d.drawString(Character.toString(value.charAt(p)),
						(p * xscale) + 4, height - 5);
				//g2d.drawString(Character.toString(value.charAt(p)),p*xscale, yscale);
			}

		}

	}

	/**
	 * set the value in the text area.
	 * @param value
	 */
	public void setText(String value) {
		this.value = value;
	}

	/**
	 * set the value in the text area.
	 * @param value
	 */
	public String getText() {
		return this.value;
	}

	/**
	 * 
	 * @author katharina
	 */
	public void addNotify() {
		super.addNotify();

		JDisplay scr = this.getScreen();
		int xscale = scr.getXScale();/*width/cols*/
		int yscale = scr.getYScale();/*height/row*/
		Rectangle rec = scr.getBounds();

		int x = rec.x + this.getLeftColum() * xscale;
		int y = rec.y + this.getTopRow() * yscale;
		int w = xscale * getWidthInColumns();//one for the cursor
		int h = yscale * this.getHeightInRows();

		/*coordinates are relative to the Screen.*/
		this.setBounds(x, y, w, h);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public void signal(BusMessage oMessage) {
		// TODO Auto-generated method stub

	}

	public long getBlinkPeriond(long period) {
		return blinkPeriod;
	}

	public void setBlink(long blink) {
		this.blinkPeriod = blink;
	}

	public void onFocusGain() {
		// TODO Auto-generated method stub

	}

	public void onFocusLose() {
		// TODO Auto-generated method stub

	}

	public void onHide() {
		// TODO Auto-generated method stub

	}

	public void onShow() {
		// TODO Auto-generated method stub

	}

	public boolean tab(BusMessage msg) {
		return true;
	}

}