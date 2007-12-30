package net.sourceforge.dscsim.controller.infostore;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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
		JAXBContext jc =  JAXBContext.newInstance("net.sourceforge.dscsim.controller.infostore", InfoStoreFactory.class.getClassLoader());		
		Unmarshaller u = jc.createUnmarshaller();  
		this.infostore = (InfoStoreType)u.unmarshal(dis);
		return this.infostore;	
	}
	
	public InfoStoreType getInfoStore(){
		return this.infostore;
	}
	
	public InfoStoreType persistInfoStore(){
		
		try{
			JAXBContext jc =  JAXBContext.newInstance("net.sourceforge.dscsim.controller.infostore", InfoStoreFactory.class.getClassLoader());		
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
