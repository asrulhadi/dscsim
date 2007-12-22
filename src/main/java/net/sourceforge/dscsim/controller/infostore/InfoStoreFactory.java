package net.sourceforge.dscsim.controller.infostore;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;

import net.sourceforge.dscsim.controller.utils.AppLogger;

public class InfoStoreFactory {

	private InfoStoreType infostore = null;
	private String fileName = "";
	
	public InfoStoreFactory(DataInputStream dis, String fileName){
		this.fileName = fileName;
		try {			
			JAXBContext jc =  JAXBContext.newInstance("net.sourceforge.dscsim.controller.infostore", InfoStoreFactory.class.getClassLoader());		
			Unmarshaller u = jc.createUnmarshaller();  
			//infostore = (InfoStoreType)u.unmarshal(new FileInputStream("D:\\Projekte\\sourceforge\\dscsim\\src\\main\\resources\\etc\\infostore.xml"));
			infostore = (InfoStoreType)u.unmarshal(dis);
		} catch (Exception e) {
			AppLogger.error(e);
		}
	}
	
	public InfoStoreType getInfoStore(){
		return this.infostore;
	}
	
	public InfoStoreType persistInfoStore(){
		
		try{
			JAXBContext jc =  JAXBContext.newInstance("net.sourceforge.dscsim.controller.infostore", InfoStoreFactory.class.getClassLoader());		
			Marshaller m = jc.createMarshaller();  			
			//m.marshal(this.infostore, new FileOutputStream("D:\\Projekte\\sourceforge\\dscsim\\src\\main\\resources\\etc\\infostore.xml"));	
			FileOutputStream fos = new FileOutputStream(this.fileName);
			OutputStream os = new DataOutputStream(fos);
			m.marshal(this.infostore, os);	
		}catch(Exception e){
			AppLogger.error(e);
		}
		
		return this.infostore;
			
	}
	
}
