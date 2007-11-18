/*
 * Created on 09.03.2007
 * katharina
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
 
package net.sourceforge.dscsim.controller.screen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.awt.Polygon;
import java.awt.*;
import java.awt.geom.Point2D;

import org.jdom.Element;
import org.jdom.xpath.XPath;

import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChoiceBox extends ScreenComponent {

	/**
	 * 
	 */
	private int selected = 0;
	
	/**
	 * index of items in range to display.
	 */
	private int to = -1;
	private int from = -1;

	/**
	 * 
	 */
	private LinkedHashMap list = new LinkedHashMap();
	/**
	 * @param x position in rows
	 * @param y position in col
	 * @param width in columns
	 * @param height in rows
	 */
	public ChoiceBox(int row, int col, int width, int height) {
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
		int y = yscale * this.getTopRow();
     	g2d.setFont(this.getParent().getFont());	 
   
     	int displaySize = getHeightInRows();
     	int listSize = list.size();
     	int displayCnt = listSize > displaySize ? displaySize : listSize;
     	if(from < 0){
     		from = 0;
     		to = displayCnt;
     	} else if(selected < from){
     		if(from > 0){
         		from--;
         		to--;
     		}
     	} else if(selected >= to){
     		if(to<listSize){
     			to++;
     			from++;
     		}
     	}
     	
     	Iterator itr = list.keySet().iterator();
     	String item = null;
     	int q = -1, d=0;   	
     	while(itr.hasNext()){
     		item= (String)itr.next();
     		q++;
     		if(q >= from && q <= to){
     			for(int p=0; p<item.length();p++){     		
     				//AppLogger.debug2("Menu.paint " + item + " at x=" + (p*xscale) + " y=" + (yscale*d + yscale));
         			g2d.drawString(Character.toString(item.charAt(p)),(p+1)*xscale, (yscale*d+yscale)-5);
          		}  
     			d++;
     		}
     	}
     	

    }
   
    /**
     * add an item to the menu.
     * @param item
     * @param data
     */
    public void addItem(String item, String data){    	
    		this.list.put(item, data);
    }
    
    /**
     * 
     * remove item from list.
     * @param item
     */
    public void removeItem(String item){
    		this.list.remove(item);
    }
    
    /**
     * 
     * @author katharina
     *
     */
    public void setSelected(int selected){
    		this.selected = selected;
    }
    /**
     * 
     * @author katharina
     *
     */
    public int getSelected(){
    		return this.selected;
    }
    
    /**
     * get selected key.
     * @return
     */
    public String getSelectedKey(){
    		return list.keySet().toArray()[selected].toString();
    }
    
    
    /**
     * 
     * @returns data stored with text.
     */
    public String getSelectedData(){  	
    		String key = list.keySet().toArray()[selected].toString();
    		return (String)list.get(key);
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
    		int w = xscale*(getWidthInColumns()+1);//one for the cursor
    		int h = yscale*this.getHeightInRows();
    		
    		/*coordinates are relative to the Screen.*/
    		this.setBounds(x, y, w, h);		  		
    }

    protected class Cursor extends Polygon {
    		
    		public Cursor(int []x, int []y, int n){
    			super(x, y, n);
    		}
    		
    		
    		public void setItem(int row){
    		
    		}
    	
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public void signal(BusMessage oMessage) {
		
		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		if(keyAction.equals(RELEASED))
			return;
		
		if(list.size()>0){	
			if(MV_DOWN.equals(keyID)){						
				if(selected < list.size()-1){
					selected++;		
				}			
			}else if(MV_UP.equals(keyID)){
				if(selected > 0){
					selected--;		
				}				
			}
			
		}
	
	}
	
	/**
	 * 
	 * @author katharina
	 * @return void
	 */
	public static void parseChoiceBox(Screen dest, Element source){
		
		try {
			XPath xpath = XPath.newInstance("element[@type='choicebox']");			
			List list = xpath.selectNodes(source);			
			Iterator itr  = list.iterator();
			Element elem = null;
			ChoiceBox m = null;
			while(itr.hasNext()){
				elem = (Element)itr.next();		
				int c = Integer.parseInt(elem.getAttributeValue("column"));
				int r = Integer.parseInt(elem.getAttributeValue("row"));
				int w = Integer.parseInt(elem.getAttributeValue("width"));
				int h = Integer.parseInt(elem.getAttributeValue("height"));			
				m = new ChoiceBox(r, c, w, h);
				m.setComponentName(elem.getAttributeValue("name"));
				parseChoices(m, elem);
				m.setName(elem.getAttributeValue("name"));
				dest.add(m);
			}
		} catch (Exception e) {
			AppLogger.error(e.getMessage());
		}
		
	}
	
	/*
	 * 
	 * parese menu item into menu object 
	 * @author katharina
	 *
	 */
	private static void parseChoices(ChoiceBox m, Element source) throws Exception{

		XPath xpath = XPath.newInstance("choice");			
		List list = xpath.selectNodes(source);			
		Iterator itr  = list.iterator();
		Element elem = null;
		while(itr.hasNext()){
			elem = (Element)itr.next();		
			parseChoices(m, elem);
			m.addItem(elem.getText(), elem.getAttributeValue("link"));
		}

	}
}
