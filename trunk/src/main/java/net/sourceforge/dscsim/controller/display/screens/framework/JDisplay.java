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
 
package net.sourceforge.dscsim.controller.display.screens.framework;

import java.awt.Frame;
import java.awt.Rectangle;

/**
 * @author katharina
 * Screen is a display on which object can be displayed.
 */
public class JDisplay  {
	

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
	private 	int xScale, yScale;

	/**
	 * 
	 */
	private Rectangle bounds = null;
		
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
	public JDisplay(int x, int y, int width, int height, int rows, int cols) {		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rows = rows;
		this.cols = cols;	
		this.yScale = height/rows;	
		this.xScale = width/cols;		
		this.bounds = new Rectangle(x, y, width+1, height+1);
	
	}
	/**
	 * 
	 */
	


	
	/**
	 * get the size size of the x length of a sqaure on the screen.
	 * Is width per column.
	 * @return
	 */
	public int getXScale(){
		return this.xScale;
	}
	
	/**
	 * get the size size of the y length of a sqaure on the screen.
	 * Is height per row.
	 * @return
	 */
	public int getYScale(){
		return this.yScale;
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
		test1(args);
	}
	public static void test1(String[]args){
		

		/*javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			*/
				Frame frame = new Frame();	
				//frame.setLayout(null);
				
				JDisplay display = new JDisplay(100, 200, 210, 160, 8, 21);	
				//screen.scaleScreen(2,2);
			
				
				JScreen screen = new JScreen(display, null);
				//frame.setBackground(Color.red);
				frame.setBounds(0, 0, 800,500);
				
	
				/*in terms of row/cols of screen*/
				JTextBox tbox = new JTextBox(0, 0, 6, 1);
				tbox.setText("hello");
				screen.add(tbox);
				
				tbox = new JTextBox(7, 15, 6, 2);
				tbox.setText("xbye");
				//screen.add(tbox);
							
				JMenu m = new JMenu(1, 2, 7, 5);
				m.addItem("choice1", new String("next"), new String("1"));
				m.addItem("choice2", new String("next"), new String("2"));
				m.addItem("choice3", new String("next"), new String("3"));
				m.addItem("choiceA", new String("next"), new String("4"));
				m.addItem("choiceB", new String("next"), new String("5"));
				screen.add(m);
				
				//frame.getContentPane().add(display);
				frame.add(screen);
				//frame.add(display);
				frame.setVisible(true);
				
	}




	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setXScale(int xScale) {
		this.xScale = xScale;
	}
	
	public void setYScale(int yScale) {
		this.yScale = yScale;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

}
