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

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.DscUtils;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditBox  extends ScreenComponent {

	private String value = "";

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
		
		if(_oKeySet.contains(oMessage.getButtonEvent().getKeyId())){
			if(value.length() < getWidthInColumns())
				value +=  DscUtils.getKeyStringValue(oMessage.getButtonEvent().getKeyId());	
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
	 * 
	 */
	public void setValue(String value){
		this.value = value;
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
