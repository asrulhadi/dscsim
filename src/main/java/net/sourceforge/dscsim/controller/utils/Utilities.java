package net.sourceforge.dscsim.controller.utils;

import java.lang.reflect.Field;

import net.sourceforge.dscsim.controller.network.DscMessageAttribute;
import net.sourceforge.dscsim.controller.network.DscPosition;


public class Utilities {
	
    public static String  getAttributeValue(String strName, String xml){
    	
		String val = null;
		
		String leftTag = "<"+ strName +">";
		String rightTag = "</"+ strName +">";
		
		int startPos = xml.indexOf(leftTag);
		int endPos = xml.indexOf(rightTag);
		
		if(startPos > -1 && endPos > startPos){
			val = xml.substring(startPos + leftTag.length(), endPos);
		}
	
	
		return val;
	
    }

    public static String  getAttribute(String strName, String xml){
    	
		String val = null;
		
		String leftTag = "<"+ strName +">";
		String rightTag = "</"+ strName +">";
		
		int startPos = xml.indexOf(leftTag);
		int endPos = xml.indexOf(rightTag);
		
		if(startPos > -1 && endPos > startPos){
			val = xml.substring(startPos, endPos + rightTag.length());
		}
	
	
		return val;
	
    }
    
    public static String Attr2Xml(DscPosition value){   	
	    	if(value == null)
	    		return "";
	    	else
	    		return value.toXml();	
    }
    public static String Attr2Xml(DscMessageAttribute value){   	
	    	if(value == null)
	    		return "";
	    	else
	    		return value.toXml();	
    }
    public static String Attr2Xml(String value){   	
	    	if(value == null)
	    		return "";
	    	else
	    		return value;	
    }
    
    public static String Attr2Xml(Field oField, Object oThis) {
    	
    		Object oObj = null;
    		try{
        		oObj = oField.get(oThis);   			
    		}catch(Exception oEx){
    			AppLogger.error(oEx);
    			return null;
    		}
   	
    		if(oObj == null)
    			return null;
    		
    		String retValue = null;

    		if(oObj instanceof DscMessageAttribute)
    			retValue = ((DscMessageAttribute)oObj).toXml();
    		else
    			retValue = oObj.toString();
    		   			   		
    		return retValue;
    	   
    }
  
}
