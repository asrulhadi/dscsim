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
 * the Initial Developer are Copyright (C) 2008, 2009.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.controller.settings;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;


import net.sourceforge.dscsim.controller.utils.AppLogger;

public class InfoStoreFactory implements net.sourceforge.dscsim.controller.Constants {

	private InfoStoreType infostore = null;
	private String mmsi = null;
	private String storeName = null;
	
	public InfoStoreFactory(String mmsi){
		this.mmsi = mmsi;
		this.storeName = STORE_BASE + mmsi + "_" + INFO_STORE_XML;

		try {
			this.loadStore(this.storeName);	
			
			if(this.infostore != null)
				return;
		} catch (Exception ex) {
			AppLogger.error(ex);
		}
		
		//try default file;
		try {
			this.loadStore(STORE_BASE + INFO_STORE_XML);		
		} catch (Exception ex) {
			AppLogger.error(ex);
		}
	}
	
	private InfoStoreType loadStore(String sourceName) throws Exception {
		DataInputStream dis = new DataInputStream(new FileInputStream(sourceName));
		JAXBContext jc =  JAXBContext.newInstance("net.sourceforge.dscsim.controller.settings", InfoStoreFactory.class.getClassLoader());		
		Unmarshaller u = jc.createUnmarshaller();  
		JAXBElement<InfoStoreType>element =(JAXBElement<InfoStoreType>)u.unmarshal(dis);
	
		this.infostore = element.getValue();
		return this.infostore;	
	}
	
	public InfoStoreType getInfoStore(){
		return this.infostore;
	}
	
	public InfoStoreType persistInfoStore(){
		
		try{
			JAXBContext jc =  JAXBContext.newInstance("net.sourceforge.dscsim.controller.settings", InfoStoreFactory.class.getClassLoader());		
			Marshaller m = jc.createMarshaller();  			
			//m.marshal(this.infostore, new FileOutputStream("D:\\Projekte\\sourceforge\\dscsim\\src\\main\\resources\\etc\\infostore.xml"));	
			FileOutputStream fos = new FileOutputStream(this.storeName);
			OutputStream os = new DataOutputStream(fos);
			m.marshal(this.infostore, os);	
		}catch(Exception e){
			AppLogger.error(e);
		}
		
		return this.infostore;
			
	}
	
}
