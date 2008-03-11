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

import java.io.DataInputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.screens.*;
import net.sourceforge.dscsim.controller.utils.AppLogger;

public class JScreenFactory {

	private Device device = null;

	private JDisplay display = null;

	public JScreenFactory(JDisplay display, DataInputStream dis) {

		this.display = display;

		try {
			JAXBContext jc = JAXBContext.newInstance(
					"net.sourceforge.dscsim.controller.screens",
					JScreenFactory.class.getClassLoader());
			Unmarshaller u = jc.createUnmarshaller();
			// device = (Device)u.unmarshal(new
			// FileInputStream("D:\\Projekte\\sourceforge\\dscsim\\src\\main\\resources\\etc\\ship-screens.xml"));
			device = (Device) u.unmarshal(dis);
		} catch (Exception e) {
			AppLogger.error(e);
		}

	}

	public ActionScreen getScreen(String name, InstanceContext instanceContext)
			throws Exception {

		ActionScreen screen = null;

		if (device == null)
			throw new Exception("Factory is not properly initialized.");

		List<Screen> screens = device.getScreens().getScreen();
		Screen jaxb = null;
		for (Screen s : screens) {
			if (s.getName().equals(name)) {
				jaxb = s;
				break;
			}
		}

		if (jaxb == null)
			throw new Exception("Screen with name:" + name + " does not exist.");

		String impl = jaxb.getImplementation();
		if (impl != null) {
			Class<?> clazz = Class.forName(impl);
			java.lang.reflect.Constructor<?> ctor = clazz
					.getConstructor(new Class[] {
							net.sourceforge.dscsim.controller.display.screens.framework.JDisplay.class,
							net.sourceforge.dscsim.controller.screens.Screen.class });
			screen = (ActionScreen) ctor.newInstance(this.display, jaxb);
		} else {
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
