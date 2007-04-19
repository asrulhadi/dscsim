/*
 * Created on 25.02.2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.dscsim.controller.screen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.xpath.XPath;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.DscUtils;
import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditBox  extends ScreenComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 891796259681334714L;

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
	private Validator validator= null;
	/**
	 * @param row
	 * @param col
	 * @param width
	 * @param height
	 */
	public EditBox(int row, int col, int width, int height) {
		super(row, col, width, height);
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public void signal(BusMessage oMessage) {
		
		String keyAction = oMessage.getButtonEvent().getAction();
		if(RELEASED.equals(keyAction))
			return;
		
		String keyID = oMessage.getButtonEvent().getKeyId();
		if(_oKeySet.contains(keyID)){			
			if((value.length() < getWidthInColumns())){
				String testValue = value + DscUtils.getKeyStringValue(oMessage.getButtonEvent().getKeyId());						
				if((validator == null) || (validator != null && validator.validate(testValue))){
					value = testValue; 
				}
			}			
		}else if(MV_LEFT.equals(oMessage.getButtonEvent().getKeyId())
				|| KP_BS.equals(oMessage.getButtonEvent().getKeyId())){
			
			int len = value.length();
			if(len > 0){
				value = value.substring(0, len-1);		
			}
		}
		 		
		int focus = 0;
		if(this.getWidthInColumns() == value.length())
			focus = 1;
		else if(value.length()==0)
			focus = -1;
		else
			focus = 0;
				
		return;
		
	}
	
	/**
	 * set contents of editbox.
	 * @param value
	 */
	public void setValue(String value){		
		this.value = value;
	}
	
	/**
	 * get the contents of the editbox.
	 * @return
	 */
	public String getValue(){		
		return this.value;
	}
	/**
	 * for edit input boxes and menus.
	 * @return
	 */
	public void setCursor(boolean onoff){
		this.cursorOn = onoff;
	}
    /**
     * 
     * @author katharina
     */
    public void addNotify(){
    		super.addNotify();      		
    		Screen scr = this.getScreen();   		    		
    		/*setbound of menu*/
    		Rectangle rec = scr.getBounds();    
    		int xscale=scr.getXScale();
    		int yscale=scr.getYScale();
    		int x = rec.x + this.getLeftColum()*xscale;
    		int y = rec.y + this.getTopRow()*yscale;		
    		int w = xscale*getWidthInColumns();
    		int h = yscale*getHeightInRows();
    		
    		/*coordinates are relative to the Screen.*/
    		/*make room for cursor at end*/
    		this.setBounds(x, y, w+xscale, h);		
    	
    		
    }

	/**
	 * Override paint.
	 */
    public void paint(Graphics g) {
		super.paint(g);	 		
		Graphics2D g2d = (Graphics2D)g;    	   	       
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
     	
     	for(int p=0; p<value.length();p++){     		
     		g2d.drawString(Character.toString(value.charAt(p)),p*xscale,yscale-5);
     	}
     	    
     	/*underline it*/
    	Stroke tmp = g2d.getStroke();
     	g2d.setStroke(new BasicStroke(3));
		g2d.drawLine(0,yscale, x*xscale, yscale);
     	g2d.setStroke(tmp);
     	/*
      	for(int j=0; j< x; j++){
     		g2d.drawLine(j*xscale,yscale, j*xscale+xscale, yscale);
     	}
     	*/
     	
     	//AppLogger.debug2("EditBox - cursor " + updateCnt);

     	if(cursorOn && (++updateCnt%5 > 1)){
    		int[]xx = new int[4];
    		int[]yy = new int[4];
    		int n = 4;
    		Screen scr = this.getScreen(); 		
    		int xs = scr.getXScale();
    		int ys = scr.getYScale(); 
    		
    		xx[0]=0; xx[1]=xs; xx[2]=xs;xx[3]=0; 
    		yy[0]=0; yy[1]=0; yy[2]=ys;yy[3]=ys;
    		Polygon cursor = new Polygon(xx, yy, n);
         	cursor.translate(value.length()*xs, 0);
         	g2d.draw(cursor);
         	g2d.fill(cursor);   		
     	}
			
    }
    
	public static void parseEditBox(Screen dest, Element source){
		
		try {
			XPath xpath = XPath.newInstance("element[@type='editbox']");			
			List list = xpath.selectNodes(source);			
			Iterator itr  = list.iterator();
			Element elem = null;
			EditBox eb = null;
			while(itr.hasNext()){
				elem = (Element)itr.next();		
				int c = Integer.parseInt(elem.getAttributeValue("column"));
				int r = Integer.parseInt(elem.getAttributeValue("row"));
				int w = Integer.parseInt(elem.getAttributeValue("width"));
				int h = Integer.parseInt(elem.getAttributeValue("height"));			
				eb = new EditBox(r, c, w, h);
				eb.setValue(elem.getText());
				eb.setComponentName(elem.getAttributeValue("name"));
				dest.add(eb);
			}
		} catch (Exception e) {
			AppLogger.error(e.getMessage());
		}
		
	}
	/**
	 * set validator according to type of box.
	 * @param validator
	 */
	public void setValidator(Validator validator){
		this.validator = validator;
	}
	
	/**
	 * is the box done according to validation rules.
	 *
	 */
	public boolean isComplete(){
		return this.validator.isComplete(value);
	}
	/**
	 * returns position of cursor in character offset.
	 * @return
	 */
	public int getCursorPos(){
		return value.length();
	}
	
	interface Validator{
    		boolean validate(String input);
    		public boolean isComplete(String strMMSI);
    }
	
	public static class MMSIValidator implements Validator{

		/* (non-Javadoc)
		 * @see net.sourceforge.dscsim.controller.screen.EditBox.Validator#validate(java.lang.String)
		 */
		public boolean validate(String strMMSI) {
			boolean result = false;
			try {
				int intMMSI = Integer.parseInt(strMMSI);
				if(strMMSI.length()<=9){
					result = true;
				}else{
					result =  false;
				}						
			}catch(Exception oEx){
				AppLogger.error(oEx);
			}		
			return result;
		}
		
		public boolean isComplete(String strMMSI) {
			if(strMMSI.length() == 9){
				return true;
			}else{
				return false;
			}			
		}
		
	}
}
