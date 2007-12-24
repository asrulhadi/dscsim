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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.panels.ActionMapping;
import net.sourceforge.dscsim.controller.panels.Device;
import net.sourceforge.dscsim.controller.panels.Editbox;
import net.sourceforge.dscsim.controller.panels.MenuType;
import net.sourceforge.dscsim.controller.panels.Menubox;
import net.sourceforge.dscsim.controller.panels.Screen;
import net.sourceforge.dscsim.controller.panels.Textbox;
import net.sourceforge.dscsim.controller.panels.impl.FieldsImpl;
import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * @author katharina
 * Screen is a display on which object can be displayed.
 */
public class JScreen extends Container
 implements net.sourceforge.dscsim.controller.Constants{
	
	/*
	 * 
	 */
	private InstanceContext instanceContext = null;
	
	/*
	 * jaxb representation of screen as described in the xml file.
	 */
	private Screen screenBindings = null;

	/*
	 * jaxb representation of device as described in the xml file.
	 */
	private Device deviceBindings = null;

	private Color background = Color.YELLOW;
		
	/**
	 * used for blending text over background.
	 */
	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

	/**
	 * Perimeter outline of screen.
	 */
	private RoundRectangle2D.Float perim = null;
		
	/**
	 * 
	 */
	private BasicStroke stroke = new BasicStroke(1.0f);
	
		

	/**
	 * 
	 */
	private JDisplay display = null;
	

	public JScreen(JDisplay display, Screen screen) {
		super();
		//this.setLayout(null);
		//this.setBounds(display.getX(), display.getY(), display.getWidth(), display.getHeight());
		this.display = display;
		this.screenBindings = screen;
		this.setName(screen.getName());
		//this.setLayout(null);
		this.setBounds(0, 0, display.getWidth(), display.getHeight());
				
		//this.setBounds(display.getX(), display.getY(), display.getWidth()+1, display.getHeight()+1);		
		//AppLogger.debug2("Screen count=" + (++count) + ";width="+ width + "; height="+ height);
		perim = new RoundRectangle2D.Float(display.getX(), display.getY(), display.getWidth(), display.getHeight(), 20, 20);	
		Font theFont = new Font("Courier", Font.PLAIN, 14);
	 	this.setFont(theFont);
	 	//AppLogger.debug2("Screen.Screen parent=" + this.getParent());
	 	
	 	//handle jaxb.
		List<FieldsImpl>fields = screen.getFields();
		for(FieldsImpl field: fields){
			System.out.println(field.getClass().getName());
			List kids = field.getAny();			
			for(Object kid: kids){
				System.out.println(kid.getClass().getName());	
		
				if(kid instanceof Textbox){
					Textbox b = (Textbox)kid;
					JTextBox item = new JTextBox(b.getRow(), b.getColumn(), b.getWidth(), b.getHeight());
					item.setText(b.getValue());
					item.setName(b.getName());
					item.setBlink(b.getBlink());
					this.add(item);
				}else if(kid instanceof Editbox){
					Editbox b = (Editbox)kid;
					JEditBox item = new JEditBox(b.getRow(), b.getColumn(), b.getWidth(), b.getHeight());
					item.setValue(b.getValue());
					item.setName(b.getName());
					this.add(item);				
				}else if(kid instanceof Menubox){					
					Menubox b = (Menubox)kid;
					JMenu item = new JMenu(b.getRow(), b.getColumn(), b.getWidth(), b.getHeight());
					item.setName(b.getName());
					List<MenuType.ChoiceType> choices = b.getChoice();					
					for(MenuType.ChoiceType c: choices){
						item.addItem(c.getValue(), c.getLink(), c.getCode());
					}
					this.add(item);						
				}			
			}
		}
	 
	}
	/**
	 * 
	 */
	public void removeNotify(){
		AppLogger.debug2("Screen.remveNotify getBounds=" + this.toString());
	}
	
	public void addNotify(){
		super.addNotify();
		//this.setBounds(display.getX(), display.getY(), display.getWidth()+1, display.getHeight()+1);		
		this.setBounds(0, 0, display.getWidth()+150, display.getHeight()+150);		

		AppLogger.debug2("Screen.addNotify after getBounds=" + this.toString());
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
	public Component add(JScreenComponent com){
		com.setScreen(display);
		super.add(com);
		return this;
		
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
		for(int r=1; r<display.getRows();r++){
			g2d.drawLine(display.getX(),  display.getY()+display.getYScale()*r, display.getX()+display.getWidth(),  display.getY()+display.getYScale()*r);
		}
		/*draw vertical lines*/
		for(int c=1; c<display.getCols();c++){
			g2d.drawLine(display.getX()+display.getXScale()*c, display.getY(), display.getX()+display.getXScale()*c, display.getY()+display.getHeight());
		}
	
		super.paint(g);	

	}
	public Screen getScreenBindings() {
		return screenBindings;
	}
	public void setScreenBindings(Screen screenBindings) {
		this.screenBindings = screenBindings;
	}
	
	public Device getDeviceBindings() {
		return deviceBindings;
	}
	public void setDeviceBindings(Device deviceBindings) {
		this.deviceBindings = deviceBindings;
	}

	/**
	 * look for action mapping relative to the screen.
	 * @param event
	 * @param source
	 * @return
	 */
	protected ActionMapping findScreenActionMapping(String event, String source){
		if(this.screenBindings.getActions()!= null)
			return findActionMapping(this.screenBindings.getActions().getAction(), event, source);
		
		return null;
	}
	
	/**
	 * look for action mapping relative to the device.
	 * @param event
	 * @param source
	 * @return
	 */
	protected ActionMapping findGlobalActionMapping(String event, String source){	
		if(this.deviceBindings.getActions()!= null)
			return findActionMapping(this.deviceBindings.getActions().getAction(), event, source);
		
		return null;
	}

	/**
	 * search utility for mappings.
	 * @param actions
	 * @param event
	 * @param source
	 * @return
	 */
	public static ActionMapping findActionMapping(List<ActionMapping>actions, String event, String source){
		for(ActionMapping act: actions){
			if(act.getEvent().equals(event)
					&& act.getSource().equals(source)){
				return act;
			}
		}	
		return null;		
		
	}
	
	/**
	 * Search first for screen and then for global.
	 * @param event
	 * @param source
	 * @return
	 */
	protected ActionMapping findActionMapping(String event, String source){	
	
		ActionMapping mapping = null;		
		if((mapping = findScreenActionMapping(event, source)) != null)
			return mapping;
		else
			return findGlobalActionMapping(event, source);		
	}
	
	/**
	 * get a sceen element by its name.
	 * 
	 * @author katharina
	 * 
	 */
	public JScreenComponent getComponentByName(String name, int start){
		JScreenComponent sc = null;
		Component all[] = this.getComponents();
		String targ = null;
		for(int i=0; i<all.length;i++){
			sc = (JScreenComponent)all[i];			
			targ = sc.getComponentName();
			if(i>=start && targ != null && targ.equals(name))
				break;
			else 
				sc = null;
		}
		
		return sc;
		
	}
	
	public boolean forceRefresh(){
		return false;
	}
	
	public void repaintChildren(){
		
	}
	
	public void setInstanceContext(InstanceContext instanceContext) {
		this.instanceContext = instanceContext;
		
	}
	public InstanceContext getInstanceContext() {
		return this.instanceContext;
	}

}
