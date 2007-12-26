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
 
package net.sourceforge.dscsim.controller.display.screens.framework;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedHashMap;

import net.sourceforge.dscsim.controller.BusMessage;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JMenu extends JScreenComponent {

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
	private LinkedHashMap<String, JChoice> list = new LinkedHashMap<String, JChoice>();
	/**
	 * @param x position in rows
	 * @param y position in col
	 * @param width in columns
	 * @param height in rows
	 */
	public JMenu(int row, int col, int width, int height) {
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
     	
     	/*draw cursor*/     	
     	//AppLogger.debug2("Menu.paint cursor.getBound" + cursor.getBounds().toString());
		/*create cursor*/
		int[]xx = new int[3];
		int[]yy = new int[3];
		int n = 3; 

		JDisplay scr = this.getScreen(); 
		int xs = scr.getXScale();
		int ys = scr.getYScale();   		
		xx[0]=0; xx[1]=xs; xx[2]=0;
		yy[0]=0; yy[1]=ys/2; yy[2]=ys;
		Cursor cursor = new Cursor(xx, yy, n);
		int curpos = selected-from;
     	//AppLogger.debug2("Menu.paint curpos " + curpos + "; selected "+ selected);
     	cursor.translate(0, curpos*yscale);
     	g2d.draw(cursor);
     	g2d.fill(cursor);

    }
   
    /**
     * add an item to the menu.
     * @param item
     * @param data
     */
    public void addItem(String text, String link, String code){    	
    		this.list.put(text, new JChoice(text, link, code));
    }
    
    /**
     * 
     * remove item from list.
     * @param item
     */
    public void removeItem(String item){
    		this.list.remove(item);
    }
    
    public void removeAll(){
    	list.clear();
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
    		if(list.size()<1){
    			return null;
    		}
    		return list.keySet().toArray()[selected].toString();
    }
    
    
    /**
     * 
     * @returns data stored with text.
     */
    public JChoice getSelectedData(){  	
    		if(this.list.size()>0){
        		String key = list.keySet().toArray()[selected].toString();
        		return list.get(key);
    		}
    		return null;
    }
    /**
     * 
     * @author katharina
     */
    public void addNotify(){
    		super.addNotify();     		
    		JDisplay scr = this.getScreen();   		    		
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
	
	public static class JChoice {
		private String link;
		private String code;
		private String text;
		
		public JChoice(String text, String link, String code){
			this.link = link;
			this.code = code;
			this.text = text;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
		
	}
}
