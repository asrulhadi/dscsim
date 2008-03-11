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
 * the Initial Developer are Copyright (C) 2006, 2007, 2008, 20010.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.controller;

public interface InstanceContext {

	public MultiClu getClu();

	public MultiContentManager getContentManager();

	public MultiBus getBus();

	public MultiController getController();

	public MultiBeeper getBeeper();

	public RadioCoreController getRadioCoreController();

	public ApplicationContext getApplicationContext();

	public Object getProperty(String key);

	public void setProperty(String key, Object value);

	public void removeProperties();

}
