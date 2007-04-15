/*
 * Created on 02.03.2007
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

import javax.swing.JComponent;

import net.sourceforge.dscsim.controller.BusListener;
import net.sourceforge.dscsim.controller.Constants;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ScreenComponent extends JComponent implements BusListener, Constants {
	/**
	 * name of item as specified by the xml attribute name.
	 */
	protected String name;
	
	/**
	 * x and y in terms of row and columns
	 */
	protected int col_x, row_y;
	
	/**
	 * width and height in terms of row and columns.
	 */
	protected int col_width, row_height;
	
	protected Screen screen = null;
	/**
	 * abstract class for all object that are to be contained on a screen.
	 * @param x in terms of rows and colums.
	 * @param y in terms of rows and colums.
	 * @param height in terms of rows and colums.
	 * @param width in terms of rows and colums.
	 */
	public ScreenComponent(int row, int col, int width, int height){
		this.col_x = col;
		this.row_y = row;
		this.col_width = width;
		this.row_height = height;
	}
	
	/**
	 * set the screen to which this element belongs.
	 * @param screen
	 */
	public void setScreen(Screen screen){
		this.screen = screen;
	}	
	
	/**
	 * get the screen to which this element belongs.
	 * @return Screen
	 */
	public Screen getScreen(){
		return this.screen;
	}
	/**
	 * set the name of the screen element.
	 * @param screen
	 */
	public void setComponentName(String name){
		this.name = name;
	}	
	
	/**
	 * get the name of the screen element.
	 * @param screen
	 */
	public String getComponentName(){
		return this.name;
	}
		
	/**
	 * x interms of colums.
	 * @return int
	 */
	public int getLeftColum(){
		return col_x;
	}
	
	/**
	 * x interms of colums.
	 * @return int
	 */
	public int getHeightInRows(){
		return row_height;
	}
	
	/**
	 * x interms of colums.
	 * @return int
	 */
	public int getWidthInColumns(){
		return col_width;
	}
	
	/**
	 * x interms of colums.
	 * @return int
	 */
	public int getTopRow(){
		return row_y;
	}
}
