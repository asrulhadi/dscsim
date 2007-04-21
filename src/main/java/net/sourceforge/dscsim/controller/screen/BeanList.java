/*
 * Created on 29.05.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.dscsim.controller.screen;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sourceforge.dscsim.controller.AddressIdEntry;
import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.Constants;
import net.sourceforge.dscsim.controller.DscUtils;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;



/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
//spoof that list is ScreenContent
public class BeanList implements Serializable {
	
	/*
	 * main list.
	 */
	private ArrayList _oBeans = new ArrayList();

	//see storage section of xml
	private String _listName = null;
	
	//class of elements in list
	private String _beanType = null;
	
	//field of stored element which acts as key.
	private String _keyFieldName;
	
	/*currently select item in list*/
	private int _selectedIdx = 0;
	
	public BeanList(){
		//super(null);
	}
	public BeanList(String listName, String beanType, String keyFieldName){
		//super(null);		
		_listName = listName;
		_beanType = beanType;
		_keyFieldName = keyFieldName;		
	}

	public String getListName(){
		return _listName;
	}
	
	public List getList(){
		return _oBeans;
	}

	/**
	 * remove object from list.
	 * @param oObj
	 */
	public void removeItem(Object oObj){
		_oBeans.remove(oObj);
	}
	/**
	 * add item to list.
	 * @param oObj
	 */
	public void addItem(Object oObj){
		_oBeans.add(0, oObj);
	}
	
	/**
	 * test 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		
	
		BeanList oList = new BeanList("address_entry", "applet.AddressIdEntry", "id");
		
		oList._oBeans.add(new AddressIdEntry("002110124", "BREMEN"));
		oList._oBeans.add(new AddressIdEntry("211001604", "ARABELLA"));
		oList.store("211001602", "mmsi_addressbook");
		
		MultiContentManager oMngr = MultiContentManager.getInstance(null);
		
		Element oScreenRoot = oMngr.getScreenElement("address_entry");
		
		
		ScreenContent.createBeanScreen(oScreenRoot, oMngr);
	
		
	}
		
	/**
	 * set select index of item.
	 * @param idx
	 */
	public void setSelectedIdx(int idx){
		_selectedIdx = idx;
	}
	
	/**
	 * get index of selected item.
	 * @return
	 */
	public int getSelectedIdx(){
		return _selectedIdx;
	}
	

	/**
	 * store list to file.
	 * @param extension
	 * @param name
	 */
	public void store(String extension, String name){   	
	    	AppLogger.debug("BeanList.store " + name + extension);	 	
	    	try {	    				
	    	   	FileOutputStream fos = new FileOutputStream(name + "_" + extension);	    	   	
	        	ObjectOutputStream oos = new ObjectOutputStream(fos);	        		
	        	oos.writeObject(this);	        	
	    	} catch(Exception oEx){
	    		AppLogger.error(oEx);
	    	}	    	 	
    }
      

}

