package net.sourceforge.dscsim.controller.display.screens.framework;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jdom.input.SAXBuilder;

import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.panels.impl.FieldsImpl;
import net.sourceforge.dscsim.controller.panels.Device;
import net.sourceforge.dscsim.controller.panels.Editbox;
import net.sourceforge.dscsim.controller.panels.MenuType;
import net.sourceforge.dscsim.controller.panels.Textbox;
import net.sourceforge.dscsim.controller.panels.ActionMapping;
import net.sourceforge.dscsim.controller.panels.Screen;
import net.sourceforge.dscsim.controller.utils.AppLogger;




public class JScreenFactory {

	
	private Device device = null;
	
	private JDisplay display = null;
	
	public JScreenFactory(JDisplay display, DataInputStream dis){
		
		this.display = display;
		
		try {					
			JAXBContext jc =  JAXBContext.newInstance("net.sourceforge.dscsim.controller.panels", JScreenFactory.class.getClassLoader());		
			Unmarshaller u = jc.createUnmarshaller();  
			//device = (Device)u.unmarshal(new FileInputStream("D:\\Projekte\\sourceforge\\dscsim\\src\\main\\resources\\etc\\ship-screens.xml"));
			device = (Device)u.unmarshal(dis);
		} catch (Exception e) {
			AppLogger.error(e);
		}

	}
	
	public JScreen getScreen(String name, InstanceContext instanceContext) throws Exception{
		
		JScreen screen = null;
		
		if(device == null)
			throw new Exception("Factory is not properly initialized.");
		
		List<Screen> screens = device.getScreens().getScreen();
		Screen jaxb = null;
		for(Screen s: screens){
			if(s.getName().equals(name)){
				jaxb = s;
				break;
			}	
		}
			
		if(jaxb == null)
			throw new Exception("Screen with name:" + name + " does not exist.");
		
		String impl = jaxb.getImplementation();	
		if(impl != null){
			Class<?> clazz = Class.forName(impl);		
			java.lang.reflect.Constructor<?> ctor = clazz.getConstructor(new Class[]{net.sourceforge.dscsim.controller.display.screens.framework.JDisplay.class, net.sourceforge.dscsim.controller.panels.Screen.class});
			screen = (JScreen)ctor.newInstance(this.display, jaxb);
		}else{
			screen = new ActionScreen(this.display, jaxb);			
		}

		screen.setInstanceContext(instanceContext);
		screen.setDeviceBindings(device);
					
		return screen;
	}

	public Device getDevice() {
		return device;
	}
		
	
}
