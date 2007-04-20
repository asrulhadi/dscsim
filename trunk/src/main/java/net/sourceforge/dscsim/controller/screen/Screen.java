/*
 * Created on 01.03.2007
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;

import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * @author katharina
 * Screen is a display on which object can be displayed.
 */
public class Screen extends Container {
	
	/**
	 * 
	 */
	private Color background = Color.YELLOW;
	
	/**
	 * used for blending text over background.
	 */
	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

	/**
	 * original top left position of screen.
	 */
	private int x, y;

	/**
	 * Dimension of screen in pixels.
	 */
	private int width, height;
	
	/**
	 * Count of rows and columns.
	 */
	private int rows, cols;
	
	/**
	 * scale of square on screen.
	 */
	private 	int xscale, yscale;
	/**
	 * Perimeter outline of screen.
	 */
	private RoundRectangle2D.Float perim = null;
	
	/**
	 * For scaling screen.
	 */
	private AffineTransform trans = null;
	
	
	/**
	 * 
	 */
	private BasicStroke stroke = new BasicStroke(1.0f);
	
		
	/**
	 * Screen constructor. For best result the width should be divisible by
	 * the number of columns and the height should be divisible by the number 
	 * of row.
	 * 
	 * @param x position relative to to left of frame.
	 * @param y position relative to to left of frame.
	 * @param width in pixels.
	 * @param height in pixels.
	 * @param row count.
	 * @param cols count.
	 */
	public Screen(int x, int y, int width, int height, int rows, int cols) {
		super();

	
		this.setLayout(null);
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rows = rows;
		this.cols = cols;	
		this.yscale = height/rows;	
		this.xscale = width/cols;			
		this.setBounds(x, y, width+1, height+1);		
		//AppLogger.debug2("Screen count=" + (++count) + ";width="+ width + "; height="+ height);
		perim = new RoundRectangle2D.Float(x, y, width, height, 1, 1);	
		Font theFont = new Font("Courier", Font.PLAIN, 14);
	 	this.setFont(theFont);
	 	//AppLogger.debug2("Screen.Screen parent=" + this.getParent());
	 
	 	
	}
	/**
	 * 
	 */
	public void removeNotify(){
		AppLogger.debug2("Screen.remveNotify getBounds=" + this.toString());
	}
	public void addNotify(){
		super.addNotify();
		this.setLayout(null);
	 	this.setFont(new Font("Courier", Font.PLAIN, 14));	 	

		AppLogger.debug2("Screen.addNotify before getBounds=" + this.toString());
		this.setBounds(x, y, width+1, height+1);		
		AppLogger.debug2("Screen.addNotify after getBounds=" + this.toString());
	 	//AppLogger.debug2("Screen.addNotify parent=" + this.getParent());
		/*this must be done because the children were 
		 * were added in before the container was active.*/		
		Component children[] = this.getComponents();
		for(int i = 0; i<children.length;i++){
			synchronized(children[i]){
				children[i].addNotify();		
			}
		
		}
		
	}
	
	/**
	 * add Component to Container and
	 * set container of parent..
	 */
	public Component add(ScreenComponent com){
		com.setScreen(this);
		super.add(com);
		return this;
		
	}
	public Component add2(ScreenComponent com){
		com.setScreen(this);
				
		/*convert from screen units to actual ones*/
		int xscale=this.getXScale();/*width/cols*/
		int yscale=this.getYScale();/*height/row*/
		int x = this.x /*this.getX()*/ + com.getLeftColum()*xscale;
		int y = this.y /*this.getY()*/ + com.getTopRow()*yscale;		
		int w = yscale*com.getWidthInColumns();
		int h = xscale*com.getHeightInRows();
		
		/*coordinates are relative to the Screen.*/
		com.setBounds(x, y, w, h);		
		super.add(com);
		
		return this;
	}
	
	public void scaleScreen(double x, double y){	
		//trans = new AffineTransform();
		//trans.scale(x, y);	
	}
	/**
	 * overriden paint method.
	 */
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g; 
        Composite original = g2d.getComposite();         
		
		/*first paint the background*/
        g2d.setComposite(this.ac);
		g2d.setColor(this.background);
		g2d.fill(perim);
		
		/*draw the grid perimeter*/
		g2d.setComposite(original);
		g2d.setStroke(stroke);
		g2d.setColor(Color.gray);
		g2d.draw(perim);

		/*draw horizontal lines*/
		for(int r=1; r<=rows;r++){
			g2d.drawLine(x,  y+yscale*r, x+width,  y+yscale*r);
		}
		/*draw vertical lines*/
		for(int c=1; c<=cols;c++){
			g2d.drawLine(x+xscale*c, y, x+xscale*c, y+height);
		}
		/*call base class impl*/
		super.paint(g);	

	}

	/**
	 * get the size size of the x length of a sqaure on the screen.
	 * Is width per column.
	 * @return
	 */
	public int getXScale(){
		return this.xscale;
	}
	
	/**
	 * get the size size of the y length of a sqaure on the screen.
	 * Is height per row.
	 * @return
	 */
	public int getYScale(){
		return this.yscale;
	}

	/**
	 * get the number of cols in the screen.
	 * Is height per row.
	 * @return
	 */
	public int getCols(){
		return this.cols;
	}
	
	/**
	 * get the number of rows in the screen.
	 * Is height per row.
	 * @return
	 */
	public int getRows(){
		return this.rows;
	}
	
	public static void main(String[]args){
		

		/*javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			*/
				JFrame frame = new JFrame();	
				//frame.setLayout(null);
				
				Screen screen = new Screen(100, 200, 210, 160, 8, 21);	
				//screen.scaleScreen(2,2);
				
				//frame.setBackground(Color.red);
				frame.setBounds(0, 0, 800,500);
	
				/*in terms of row/cols of screen*/
				TextBox tbox = new TextBox(2, 2, 6, 2);
				tbox.setText("hello");
				//screen.add(tbox);
				
				tbox = new TextBox(7, 15, 6, 2);
				tbox.setText("xbye");
				//screen.add(tbox);
							
				Menu m = new Menu(1, 2, 7, 3);
				m.addItem("choice1", new String("next"));
				m.addItem("choice2", new String("next"));
				m.addItem("choice3", new String("next"));
				m.addItem("choiceA", new String("next"));
				m.addItem("choiceB", new String("next"));
				screen.add(m);
				
				frame.getContentPane().add(screen);
				frame.setVisible(true);
				
	}

}
