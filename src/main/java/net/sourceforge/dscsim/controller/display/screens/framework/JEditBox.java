/*
 * Created on 25.02.2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.dscsim.controller.display.screens.framework;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.DscUtils;
import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JEditBox extends JScreenComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 891796259681334714L;

	public static enum TAB_EVENT {
		ENT, ANY;

		public static TAB_EVENT asEnum(String value) {
			if (ENT.toString().equals(value))
				return ENT;
			else
				return ANY;
		}
	};

	protected TAB_EVENT tabOn = TAB_EVENT.ENT;

	/**
	 * tracking values for alphanumeric mode.
	 */
	private String lastKeyId = null;
	private int counter = 0;
	private int insertAt = -1;
	private boolean upperCase = true;

	private ArrayList<String> pickList = null;

	private static final String KEY2UPPER[][] = { { "0000" }, { "1ABC" },
			{ "2DEF" }, { "3GHI" }, { "4JKL" }, { "5MNO" }, { "6PQR" },
			{ "7STU" }, { "8VWY" }, { "9YZY" }, };

	private static final String KEY2LOWER[][] = { { "0000" }, { "1abc" },
			{ "2def" }, { "3ghi" }, { "4jkl" }, { "5mno" }, { "6pqr" },
			{ "7stu" }, { "8vwx" }, { "9yzy" }, };

	/**
	 * 
	 */
	private boolean editable = true;

	/**
	 * 
	 */

	public enum Mode {
		Alpha, Digit, Pick
	}

	private Mode mode = Mode.Digit;

	/**
	 * value of box content.
	 */
	private String value = "";

	/**
	 * cursor is on or off
	 */
	private boolean cursorOn = true;

	/**
	 * used for blinking cursor.
	 */
	private int updateCnt = 0;
	/**
	 * pattern used to check input.
	 */
	private Validator validator = null;

	/**
	 * @param row
	 * @param col
	 * @param width
	 * @param height
	 */
	public JEditBox(int row, int col, int width, int height) {
		super(row, col, width, height);

	}

	public void signal(BusMessage oMessage) {

		if (this.editable == false)
			return;

		if (this.mode == Mode.Digit)
			doDigitMode(oMessage);
		else if (this.mode == Mode.Pick)
			this.doPickMode(oMessage);
		else
			doAlphaMode(oMessage);

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public void doAlphaMode(BusMessage oMessage) {
		String keyId = oMessage.getButtonEvent().getKeyId();

		if (KP_Aa.equals(keyId)) {
			upperCase = !upperCase;
			return;
		}

		if (MV_RIGHT.equals(keyId)) {
			lastKeyId = keyId;
			counter = 0;
			return;
		}

		if (_oKeySet.contains(keyId)) {
			if (lastKeyId == null || !lastKeyId.equals(keyId)) {
				counter = 0;
				insertAt++;
				lastKeyId = keyId;
			} else {
				counter = (counter + 1) % 4;
			}

			if ((value.length() < getWidthInColumns())) {
				int keyNum = Integer
						.parseInt(DscUtils.getKeyStringValue(keyId));
				String currCase[][] = upperCase == true ? KEY2UPPER : KEY2LOWER;
				String nextChar = "";
				if (keyNum > -1 && keyNum < 10)
					nextChar = currCase[keyNum][0].substring(counter,
							counter + 1);

				String testValue = value;
				testValue = testValue.substring(0, insertAt) + nextChar;
				if ((validator == null)
						|| (validator != null && validator.validate(testValue))) {
					value = testValue;
				}

			}

		} else if (MV_LEFT.equals(oMessage.getButtonEvent().getKeyId())
				|| KP_BS.equals(oMessage.getButtonEvent().getKeyId())) {
			int len = value.length();
			if (len > 0) {
				value = value.substring(0, len - 1);
			}
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public void doDigitMode(BusMessage oMessage) {

		String keyAction = oMessage.getButtonEvent().getAction();
		if (RELEASED.equals(keyAction))
			return;

		String keyID = oMessage.getButtonEvent().getKeyId();
		if (_oKeySet.contains(keyID)) {
			if ((value.length() < getWidthInColumns())) {
				String testValue = value
						+ DscUtils.getKeyStringValue(oMessage.getButtonEvent()
								.getKeyId());
				if ((validator == null)
						|| (validator != null && validator.validate(testValue))) {
					value = testValue;
				}
			}
		} else if (MV_LEFT.equals(oMessage.getButtonEvent().getKeyId())
				|| KP_BS.equals(oMessage.getButtonEvent().getKeyId())) {
			int len = value.length();
			if (len > 0) {
				value = value.substring(0, len - 1);
			}
		}
	
		return;

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public void doPickMode(BusMessage oMessage) {

		String keyAction = oMessage.getButtonEvent().getAction();
		if (RELEASED.equals(keyAction))
			return;

		String keyID = oMessage.getButtonEvent().getKeyId();
		int idx = this.pickList.indexOf(value);
		if (MV_UP.equals(oMessage.getButtonEvent().getKeyId())) {
			value = this.pickList.get((idx + 1) % this.pickList.size());
		} else if (MV_DOWN.equals(oMessage.getButtonEvent().getKeyId())) {
			value = this.pickList.get((idx-1+this.pickList.size()) % this.pickList.size());
		}

		return;

	}

	/**
	 * set contents of editbox.
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * get the contents of the editbox.
	 * @return
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * for edit input boxes and menus.
	 * @return
	 */
	public void setCursor(boolean onoff) {
		this.cursorOn = onoff;
	}

	/**
	 * 
	 * @author katharina
	 */
	public void addNotify() {
		super.addNotify();
		JDisplay scr = this.getScreen();
		/*setbound of menu*/
		Rectangle rec = scr.getBounds();
		int xscale = scr.getXScale();
		int yscale = scr.getYScale();
		int x = rec.x + this.getLeftColum() * xscale;
		int y = rec.y + this.getTopRow() * yscale;
		int w = xscale * getWidthInColumns();
		int h = yscale * getHeightInRows();

		/*coordinates are relative to the Screen.*/
		/*make room for cursor at end*/
		this.setBounds(x, y, w + xscale, h);

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

		int x = this.getWidthInColumns();
		int y = this.getHeightInRows();
		int height = this.getHeight();
		int xscale = this.getScreen().getXScale();
		int yscale = this.getScreen().getYScale();
		g2d.setFont(this.getParent().getFont());

		for (int p = 0; p < value.length(); p++) {
			g2d.drawString(Character.toString(value.charAt(p)), (p*xscale)+4,
					yscale - 5);
		}

		/*underline it*/
		Stroke tmp = g2d.getStroke();
		g2d.setStroke(new BasicStroke(3));
		g2d.drawLine(0, yscale, x * xscale, yscale);
		g2d.setStroke(tmp);
		/*
		for(int j=0; j< x; j++){
			g2d.drawLine(j*xscale,yscale, j*xscale+xscale, yscale);
		}
		 */

		//AppLogger.debug2("EditBox - cursor " + updateCnt);
		if (cursorOn && (++updateCnt % 5 > 1)) {
			int[] xx = new int[4];
			int[] yy = new int[4];
			int n = 4;
			JDisplay scr = this.getScreen();
			int xs = this.getScreen().getXScale();
			int ys = this.getScreen().getYScale();

			xx[0] = 0;
			xx[1] = xs;
			xx[2] = xs;
			xx[3] = 0;
			yy[0] = 0;
			yy[1] = 0;
			yy[2] = ys;
			yy[3] = ys;
			Polygon cursor = new Polygon(xx, yy, n);
			cursor.translate(value.length() * xs, 0);
			g2d.draw(cursor);
			g2d.fill(cursor);
		}

	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * set validator according to type of box.
	 * @param validator
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	/**
	 * is the box done according to validation rules.
	 *
	 */
	public boolean isComplete() {
		if (this.validator != null)
			return this.validator.isComplete(value);
		else
			return false;
	}

	/**
	 * returns position of cursor in character offset.
	 * @return
	 */
	public int getCursorPos() {
		if(Mode.Pick.equals(this.mode)){
			return 0;
		}else{
			return value.length();
		}	
	}

	/**
	 */
	public void setEditMode(boolean onoff) {
		this.editable = onoff;
		this.setCursor(onoff);
	}

	interface Validator {
		boolean validate(String input);

		public boolean isComplete(String strMMSI);
	}

	public static class PositionValidator implements Validator {

		public enum MODE {
			LAT, LON, MIN;
		};

		MODE mode = MODE.LAT;

		public PositionValidator setMode(MODE m) {
			mode = m;
			return this;
		}

		public boolean validate(String strValue) {
			boolean result = true;
			try {
				int intValue = Integer.parseInt(strValue);

				if (mode.equals(MODE.LAT)) {
					if (strValue.length() == 1 && intValue > 8)
						result = false;
					else if (strValue.length() == 2 && intValue > 90)
						result = false;
				}

				if (mode.equals(MODE.LON)) {
					if (strValue.length() == 1 && intValue > 1)
						result = false;
					else if (strValue.length() == 2 && intValue > 18)
						result = false;
					else if (strValue.length() == 3 && intValue > 180)
						result = false;
				}

				if (mode.equals(MODE.MIN)) {
					if (strValue.length() == 1 && intValue > 5)
						result = false;
					else if (strValue.length() == 2 && intValue > 59)
						result = false;
				}

			} catch (Exception oEx) {
				AppLogger.error(oEx);
				result = false;
			}
			return result;
		}

		public boolean isComplete(String strValue) {

			boolean rv = false;

			if (strValue == null)
				return rv;

			switch (mode) {

			case LAT:
			case MIN:
				rv = strValue.length() == 2 ? true : false;
				break;
			case LON:
				rv = strValue.length() == 3 ? true : false;
				break;
			default:
				rv = false;
			}
			;

			return rv;
		}

	}

	public static class TimeValidator implements Validator {

		public enum MODE {
			HH, MM;
		};

		MODE mode = MODE.HH;

		public TimeValidator setMode(MODE m) {
			mode = m;
			return this;
		}

		public boolean validate(String strValue) {
			boolean result = true;
			try {
				int intValue = Integer.parseInt(strValue);

				if (mode.equals(MODE.HH)) {
					if (strValue.length() == 1 && intValue > 2)
						result = false;
					else if (strValue.length() == 2 && intValue > 24)
						result = false;
				}

				if (mode.equals(MODE.MM)) {
					if (strValue.length() == 1 && intValue > 5)
						result = false;
					else if (strValue.length() == 2 && intValue > 59)
						result = false;
				}

			} catch (Exception oEx) {
				AppLogger.error(oEx);
				result = false;
			}
			return result;
		}

		public boolean isComplete(String strValue) {

			boolean rv = false;

			if (strValue == null)
				return rv;

			switch (mode) {

			case HH:
			case MM:
				rv = strValue.length() == 2 ? true : false;
				break;
			default:
				rv = false;
			}
			;

			return rv;
		}

	}

	public static class MMSIValidator implements Validator {

		/* (non-Javadoc)
		 * @see net.sourceforge.dscsim.controller.screen.EditBox.Validator#validate(java.lang.String)
		 */
		public boolean validate(String strMMSI) {
			boolean result = false;
			try {
				int intMMSI = Integer.parseInt(strMMSI);
				if (strMMSI.length() <= 9) {
					result = true;
				} else {
					result = false;
				}
			} catch (Exception oEx) {
				AppLogger.error(oEx);
			}
			return result;
		}

		public boolean isComplete(String strMMSI) {
			if (strMMSI.length() == 9) {
				return true;
			} else {
				return false;
			}
		}

	}

	public static class AddressIdValidator implements Validator {

		/* (non-Javadoc)
		 * @see net.sourceforge.dscsim.controller.screen.EditBox.Validator#validate(java.lang.String)
		 */
		public boolean validate(String strMMSI) {
			return true;
		}

		public boolean isComplete(String strMMSI) {
			if (strMMSI.length() > 0) {
				return true;
			} else {
				return false;
			}
		}

	}

	public static class ChannelValidator implements Validator {

		public boolean isComplete(String strChannel) {
			if (strChannel.length() == strChannel.length()) {
				return true;
			} else {
				return false;
			}
		}

		public boolean validate(String strChannel) {
			boolean result = false;

			try {

				int intChannel = Integer.parseInt(strChannel);

				if (strChannel.length() < 2) {
					if ((intChannel >= 0 && intChannel < 3)
							|| (intChannel > 5 && intChannel < 9))
						result = true;
					else
						result = false;
				} else {
					if ((intChannel > 0 && intChannel < 29)
							|| (intChannel > 65 && intChannel < 91))
						result = true;
					else
						result = false;
				}

			} catch (Exception oEx) {
				AppLogger.error(oEx);
			}

			return result;
		}

	}

	public static class PickListValidator implements Validator {

		/* (non-Javadoc)
		 * @see net.sourceforge.dscsim.controller.screen.EditBox.Validator#validate(java.lang.String)
		 */
		public boolean validate(String value) {
			return true;
		}

		public boolean isComplete(String value) {
			return true;
		}

	}

	public ArrayList<String> getPickList() {
		return pickList;
	}

	public void setPickList(ArrayList<String> pickList) {
		this.pickList = pickList;
		this.value = this.pickList.get(0);
	}

	public void onFocusGain() {
		this.setCursor(true);
	}

	public void onFocusLose() {
		this.setCursor(false);
	}

	public void onHide() {

	}

	public void onShow() {

	}

	public TAB_EVENT getTabOn() {
		return tabOn;
	}

	public void setTabOn(TAB_EVENT tabOn) {
		this.tabOn = tabOn;
	}

	public boolean tab(BusMessage msg) {

		String keyID = msg.getButtonEvent().getKeyId();

		if (FK_ENT.equals(keyID) && this.getTabOn().equals(TAB_EVENT.ENT)) {
			return true;
		} else if ((!NO_KEY.equals(keyID) && !FK_ENT.equals(keyID))
				&& this.getTabOn().equals(TAB_EVENT.ANY)) {
			return true;
		}
		return false;
	}
}
