/*
 * Created on 16.07.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.dscsim.controller.network;

import java.io.Serializable;


import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.screen.types.Latitude;
import net.sourceforge.dscsim.controller.screen.types.Longitude;
import net.sourceforge.dscsim.controller.utils.Utilities;

import org.jdom.Element;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DscPosition implements Serializable, DscMessageAttribute {
	
	
	private Latitude _latitude = null;
	private Longitude _longitude = null;

	public DscPosition(){
		
	}
	public DscPosition(Latitude latitude, Longitude longitude){
		_latitude = latitude;
		_longitude = longitude;
	}
	public Latitude getLatitude(){
		return _latitude;
	}
	
	public Longitude getLongitude(){
		return _longitude;
	}
	
	public void setLatitude(Latitude latitude){
		_latitude = latitude;
	}
	
	public void setLongitude(Longitude longitude){
		_longitude = longitude;
	}
	
	public String toString(){
		
		String strLat = _latitude != null ? _latitude.toString() : "N/A";
		String strLong = _longitude != null ? _longitude.toString() : "N/A";
		
		return strLat + "/" + strLong;
		
	}
	
	public String toXml(){
		
		Latitude lat = _latitude != null ? _latitude : new Latitude();
		Longitude longi = _longitude != null ? _longitude : new Longitude();
			
		return "<position>"
 		+ lat.toXml()
 		+ longi.toXml()
 		+ "</position>";
	}

	public void fromXml(String inXml) throws Exception {
		
		String strPos = Utilities.getAttributeValue("position", inXml);
		
		if(strPos != null){
			
			String strLat = Utilities.getAttribute("latitude", strPos);
			if(strLat != null){
				_latitude = new Latitude();
				_latitude.fromXml(strLat);
			} else {
				throw new Exception("latitude element not found in position.");
			}
			
			String strLon = Utilities.getAttribute("longitude", strPos);
			if(strLon != null){
				_longitude = new Longitude();
				_longitude.fromXml(strLon);
			} else {
				throw new Exception("longitude element not found in position.");
			}
				
		} else {
			throw new Exception("position element not found in DscPosition.");
		}
		
	}
}
