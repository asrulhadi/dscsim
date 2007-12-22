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
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.xpath.XPath;
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
	
	/**
	 * TextBox constructor.
	 * @param row
	 * @param col
	 * @param width in cols unit.
	 * @param height in rows units.
	 */
	
	public JTextBox(int row, int col, int width, int height){
		super(row, col, width, height);
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
        int height = this.getHeight();
		int xscale = this.getScreen().getXScale();
		int yscale = this.getScreen().getYScale();
     	g2d.setFont(this.getParent().getFont());	
     	
     	for(int p=0; value != null && p<value.length();p++){     		
     		g2d.drawString(Character.toString(value.charAt(p)),p*xscale,height-5);
 			//g2d.drawString(Character.toString(value.charAt(p)),p*xscale, yscale);
     	}
		//g2d.drawString(value, 0, height);   	   	 
		
    }
    
    /**
     * set the value in the text area.
     * @param value
     */
    public void setText(String value){
    		this.value = value;
    }
    /**
     * set the value in the text area.
     * @param value
     */
    public String getText(){
    		return this.value;
    }
    /**
     * 
     * @author katharina
     */
    public void addNotify(){
    		super.addNotify();
    		
    		JDisplay scr = this.getScreen();
    		int xscale=scr.getXScale();/*width/cols*/
    		int yscale=scr.getYScale();/*height/row*/
    		Rectangle rec = scr.getBounds();
    		
    		int x = rec.x + this.getLeftColum()*xscale;
    		int y = rec.y + this.getTopRow()*yscale;		
    		int w = xscale*getWidthInColumns();//one for the cursor
    		int h = yscale*this.getHeightInRows();
 	
    		/*coordinates are relative to the Screen.*/
    		this.setBounds(x, y, w, h);		
    		
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public void signal(BusMessage oMessage) {
		// TODO Auto-generated method stub
		
	}
	

}