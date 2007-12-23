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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreenFactory;
import net.sourceforge.dscsim.controller.infostore.InfoStoreType;
import net.sourceforge.dscsim.controller.infostore.InfoStoreFactory;
import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.panels.ActionMapping;
import net.sourceforge.dscsim.controller.panels.Device;
import net.sourceforge.dscsim.controller.screen.BeanList;
import net.sourceforge.dscsim.controller.screen.MenuScreen;
import net.sourceforge.dscsim.controller.screen.ScreenContent;
import net.sourceforge.dscsim.controller.screen.ScreenInterface;
import net.sourceforge.dscsim.controller.screen.types.ActiveField;
import net.sourceforge.dscsim.controller.screen.types.DscBoolean;
import net.sourceforge.dscsim.controller.utils.AppLogger;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.input.SAXBuilder;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

public class MultiContentManager implements BusListener, Constants {

	private static String cDISTRESS_CALL_PERSISTANCE = STORE_BASE
			+ "distresscalls";
	private String _strMMSI = "";

	private InstanceContext _oInstanceContext = null;
	private Document m_oDocument = null;

	//cache for lists
	private ArrayList _oSessionCache = new ArrayList();

	//session distress calls
	private ArrayList _oIncomingDistressCalls = new ArrayList();
	private ArrayList _oIncomingDistressAcks = new ArrayList();

	private HashMap _oBeanListSession = new HashMap();
	private HashMap _oBeanListPersist = new HashMap();

	//persistant list
	private HashMap _oSessionLists = new HashMap();

	//persistant properties
	private HashMap _oSessionProperties = null;
	private String _sessionPropFilename = "settings";

	private static Properties _appProperties = null;
	private static Properties _appSettings = null;

	private JScreenFactory screenFactory = null;
	private InfoStoreFactory infostoreFactory = null;

	private DscMessage outGoingDscMessage = new DscMessage();
	private DscMessage incomingDscMessage = new DscMessage();

	private AddressIdEntry selectedAddressId = null;
	private ArrayList<AddressIdEntry> addressIdList = null;

	private AddressIdEntry selectedGroupId = null;
	private ArrayList<AddressIdEntry> groupIdList = null;

	public static MultiContentManager getInstance(InstanceContext oCtx) {
		return new MultiContentManager(oCtx);
	}

	public MultiContentManager(InstanceContext oInstanceContext) {
		_oInstanceContext = oInstanceContext;
			
		try {
			
			String xmlName = oInstanceContext.getApplicationContext().getDeviceXmlName();
			DataInputStream dataInput = new DataInputStream(getResourceStream(xmlName, this.getClass()));
			this.screenFactory = new JScreenFactory(new JDisplay(DISPLAY_X-11,
					DISPLAY_Y+1, 273, 160, 8, 21), dataInput);
			
			dataInput = new DataInputStream(getResourceStream(INFO_STORE_XML, this.getClass()));
			this.infostoreFactory = new InfoStoreFactory(dataInput, INFO_STORE_XML);

			SAXBuilder oBuilder = new SAXBuilder();
			xmlName = oInstanceContext.getApplicationContext()
					.getScreenFileName();
			dataInput = new DataInputStream(getResourceStream(xmlName, this
					.getClass()));
			m_oDocument = oBuilder.build(dataInput);
			oBuilder = null;
		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

	}

	public String getActionFromEventName(String eventId) {

		//TODO cache the events
		String strActionName = null;

		Element oRoot = m_oDocument.getRootElement();

		Element oEvents = oRoot.getChild("events");

		Iterator oIter = oEvents.getChildren().iterator();

		Element oElement = null;
		String strNameAttr = null;
		while (oIter.hasNext()) {

			oElement = (Element) oIter.next();

			strNameAttr = oElement.getAttributeValue("name");

			if (strNameAttr.equals(eventId))
				return oElement.getAttributeValue("action");

		}

		return strActionName;
	}

	public void setMMSI(String strMMSI) {
		if (strMMSI != null)
			_strMMSI = strMMSI;
		else
			_strMMSI = "";
	}

	public String getMMSI() {

		if (_strMMSI != null)
			return _strMMSI;
		else
			return "";
	}

	public void putCache(ScreenInterface oScreen) {

		/*
		String scope = oScreen.getAttributeValue("scope");
		
		if(scope.equals("session"))
			_oSessionCache.add(oScreen);
		 */
	}

	private void clearCache() {
		_oSessionCache.clear();
	}

	public Element getSettingsElement(String listName) {

		List oChildren = m_oDocument.getRootElement().getChild("settings")
				.getChildren();

		Element oSettings = null;
		for (int i = 0; i < oChildren.size(); i++) {

			oSettings = (Element) oChildren.get(i);

			if (oSettings.getAttributeValue("property").equals(listName)) {
				break;
			}

			oSettings = null;
		}

		return oSettings;
	}

	public Element getStorageElement(String listName) {

		List oChildren = m_oDocument.getRootElement().getChild("storage")
				.getChildren();

		Element oStorage = null;
		for (int i = 0; i < oChildren.size(); i++) {

			oStorage = (Element) oChildren.get(i);

			if (oStorage.getAttributeValue("name").equals(listName)) {
				break;
			}

			oStorage = null;
		}

		return oStorage;
	}

	private ScreenInterface getCache(String srchName) {

		String name = null;
		ScreenInterface oScreen = null;
		for (int i = 0; i < this._oSessionCache.size(); i++) {

			oScreen = (ScreenInterface) _oSessionCache.get(i);

			if (oScreen instanceof JScreen)
				name = ((JScreen) oScreen).getName();
			else
				name = oScreen.getAttributeValue("name");

			if (name.equals(srchName))
				return oScreen;

		}

		return null;
	}

	public InfoStoreType getInfoStore() {
		return this.infostoreFactory.getInfoStore();
	}

	public InfoStoreType persistInfoStore() {
		return this.infostoreFactory.persistInfoStore();
	}

	public Object getScreenContent(String strScreenName, InstanceContext oCtx) {
		ScreenInterface oRet = getCache(strScreenName);

		if (oRet != null)
			return oRet;

		try {
			JScreen screen = this.screenFactory.getScreen(strScreenName, oCtx);

			String scope = screen.getScreenBindings().getScope();

			if (screen != null && scope.equals("session"))
				_oSessionCache.add(screen);

			if (screen != null)
				return screen;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//TODO delete after conversion is complete.
		try {

			oRet = getCache(strScreenName);

			if (oRet != null)
				return oRet;

			Element oScreenRoot = getScreenElement(strScreenName);

			if (oScreenRoot != null) {
				oRet = ScreenContent.createFromXml(
						m_oDocument.getRootElement(), oScreenRoot, this);

				oRet.setInstanceContext(oCtx);

				String scope = oRet.getAttributeValue("scope");

				if (scope != null && scope.equals("session"))
					_oSessionCache.add(oRet);

			} else {
				oRet = ScreenContent.createFromXml(
						m_oDocument.getRootElement(),
						getScreenElement("system_not_implemented"), this);
			}

		} catch (Exception oEx) {
			AppLogger.error(oEx);
			oRet = new MenuScreen(null);
		}

		return oRet;

	}

	/**
	 * search utility for mappings.
	 * @param actions
	 * @param event
	 * @param source
	 * @return
	 */
	public static ActionMapping findActionMapping(List<ActionMapping> actions,
			String event, String source) {
		for (ActionMapping act : actions) {
			if (act.getEvent().equals(event) && act.getSource().equals(source)) {
				return act;
			}
		}
		return null;

	}

	public String getCallTypeMappingValue(String srchFor) {

		String retValue = "";

		try {

			Device device = screenFactory.getDevice();
			ActionMapping am = findActionMapping(device.getActions()
					.getAction(), srchFor, BusMessage.MSGTYPE_NETWORK);
			if (am != null)
				retValue = am.getForward();

		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

		return retValue != null ? retValue : "";
	}

	public Element getScreenElement(String strScreenName) {

		Element oRoot = m_oDocument.getRootElement();

		Element oScreens = oRoot.getChild("screens");

		Iterator oIter = oScreens.getChildren().iterator();

		Element oTarget = null;
		Attribute oAttr = null;

		while (oIter.hasNext()) {
			oTarget = (Element) oIter.next();

			oAttr = oTarget.getAttribute("name");
			String strValue = oAttr != null ? oAttr.getValue() : null;

			if (strValue != null && strValue.equals(strScreenName))
				return oTarget;
			else
				oTarget = null;

		}

		return oTarget;

	}

	/*
	public Element getCodeString(String strId){
		
		Element oRoot = m_oDocument.getRootElement();
		
		Element oScreens = oRoot.getChild("strings");
		
		Iterator oIter = oScreens.getChildren().iterator();
		
	   	Element oTarget = null; 
	   	Attribute oAttr = null;
	   	
		while(oIter.hasNext()) {
			oTarget =(Element)oIter.next();
			
			oAttr = oTarget.getAttribute("code");
			String strValue = oAttr != null ? oAttr.getValue() : null;
			
			if(strValue != null && strValue.equals(strId))
				return oTarget;
			else
				oTarget = null;
			
		}
		
		return oTarget;
		
	}
	 */

	public String getCodeString(String strId) {

		return getProperties().get(strId).toString();

	}

	public Element getContextElement(String strScreenName) {

		Element oRoot = m_oDocument.getRootElement();

		Element oScreens = oRoot.getChild("contexts");

		Iterator oIter = oScreens.getChildren().iterator();

		Element oTarget = null;
		Attribute oAttr = null;

		while (oIter.hasNext()) {
			oTarget = (Element) oIter.next();

			oAttr = oTarget.getAttribute("name");
			String strValue = oAttr != null ? oAttr.getValue() : null;

			if (strValue != null && strValue.equals(strScreenName))
				return oTarget;
			else
				oTarget = null;

		}

		return oTarget;

	}

	public boolean isIncomingOtherRequest(DscMessage oMessage) {

		boolean bValue = false;

		if (CALL_TYPE_GROUP.equals(oMessage.getCallType())) {
			bValue = true;
		} else if (CALL_TYPE_INDIVIDUAL.equals(oMessage.getCallType())) {
			bValue = true;
		} else if (CALL_TYPE_ALL_SHIPS.equals(oMessage.getCallType())) {
			bValue = true;
		} else if (CALL_TYPE_INDIVIDUAL_ACK.equals(oMessage.getCallType())) {
			bValue = true;
		}

		return bValue;

	}

	public void storeIncomingMessage(DscMessage oMessage) {

		if (MultiContentManager.CALL_TYPE_DISTRESS.equals(oMessage
				.getCallType())
				|| MultiContentManager.CALL_TYPE_DISTRESS_ACK.equals(oMessage
						.getCallType())) {
			addIncomingDistressCall(oMessage);
		} else if (isIncomingOtherRequest(oMessage)) {
			addIncomingOtherCalls(oMessage);
		}

	}

	public void addIncomingOtherCalls(DscMessage oMessage) {

		Element elemStorage = getStorageElement("incoming_other_calls");

		BeanList oStorage = getBeanList(elemStorage);

		oStorage.addItem(oMessage);

		storeBeanList(oStorage);

	}

	public void addIncomingDistressCall(DscMessage oMessage) {

		Element elemStorage = getStorageElement("incoming_distress_calls");

		BeanList oStorage = getBeanList(elemStorage);

		oStorage.addItem(oMessage);

		storeBeanList(oStorage);

	}

	public List getStorageList(String storageName) {

		try {
			return getBeanList(storageName).getList();
		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

		return null;

	}

	public DscMessage findLatestMessage(DscMessage oTarget) {

		List oCalls = getIncomingOtherCalls();

		if (oCalls == null)
			return null;

		DscMessage oFound = null;
		for (int i = 0; i < oCalls.size(); i++) {
			oFound = (DscMessage) oCalls.get(i);

			if (oFound.getUid().equals(oTarget.getUid())) {
				break;
			} else {
				oFound = null;
			}

		}

		return oFound;
	}

	public List getIncomingOtherCalls() {

		try {
			return getStorageList("incoming_other_calls");
		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

		return null;

	}

	public List getIncomingDistressCalls() {

		try {
			return getPersistantList("incoming_distress_calls");
		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

		return null;

	}

	public void addIncomingDistressAcks(DscMessage oMessage) {

		_oIncomingDistressAcks.add(0, oMessage);

		for (int i = _oIncomingDistressAcks.size() - 1; i > 6; i--) {
			_oIncomingDistressAcks.remove(i);
		}

	}

	public ArrayList getPersistantList(String name) {

		//is list already in cache.
		ArrayList oList = (ArrayList) _oSessionLists.get(name);

		//yes, return it.
		if (oList != null)
			return oList;

		//find xml definition of the list
		Element elemStorage = m_oDocument.getRootElement().getChild("storage");

		Iterator oItr = elemStorage.getChildren("list").iterator();

		Element elemList = null;
		while (oItr.hasNext()) {

			elemList = (Element) oItr.next();

			String listName = elemList.getAttributeValue("name");

			if (listName != null && listName.equals(name))
				break;
			else
				elemList = null;

		}

		if (elemList == null)
			return null;

		oList = new ArrayList();

		String strScope = elemList.getAttributeValue("scope");

		if (strScope == null || strScope.equals("session")) {
			_oSessionLists.put(name, oList);
			return oList;
		}

		/*
		if(true)
			throw new RuntimeException("permant list persistance not yet implemented.");
		
		return null;
		 */
		return oList;
	}

	public ArrayList getIncomingDistressAcks() {

		return _oIncomingDistressCalls;

	}

	public ArrayList fetchDistressCalls() {

		//AppLogger.debug("fetching store called for"+ cDISTRESS_CALL_PERSISTANCE + getStoreExtension());

		ArrayList oRetList = null;

		try {

			FileInputStream fis = new FileInputStream(
					cDISTRESS_CALL_PERSISTANCE + getStoreExtension());

			ObjectInputStream ois = new ObjectInputStream(fis);

			oRetList = (ArrayList) ois.readObject();

		} catch (Exception oEx) {
			AppLogger.error(oEx);
			oRetList = new ArrayList();
		}

		return oRetList;

	}

	public void storeIncomingCall(DscMessage oDscMessage) {

		//AppLogger.debug("storeIncomingCall called for"+ cDISTRESS_CALL_PERSISTANCE + getStoreExtension());

		if (CALL_CAT_DISTRESS.equals(oDscMessage.getClass())) {

		} else {

		}

	}

	public void storeLastDistressCalls(DscMessage oDscMsg) {

		//AppLogger.debug("storeLastDistressCalls called for"+ cDISTRESS_CALL_PERSISTANCE + getStoreExtension());

		ArrayList oLastCalls = fetchDistressCalls();

		oLastCalls.add(0, oDscMsg);

		for (int i = oLastCalls.size() - 1; i > 6; i--) {
			oLastCalls.remove(i);
		}

		storeDistressCalls(oLastCalls);

	}

	public String getStoreExtension() {

		String strExt = getMMSI();
		strExt = strExt.length() > 0 ? "_" + strExt : "";

		return strExt + ".obj";

	}

	public String getStorePrefix() {
		return Constants.STORE_BASE;
	}

	public void storeDistressCalls(ArrayList oCalls) {

		//	AppLogger.debug("storeDistressCalls called for"+ cDISTRESS_CALL_PERSISTANCE + getStoreExtension());

		try {

			FileOutputStream fos = new FileOutputStream(getStorePrefix()
					+ cDISTRESS_CALL_PERSISTANCE + getStoreExtension());

			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(oCalls);

		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

	}

	public BeanList getBeanList(String name) {

		//AppLogger.debug("MultiContentManager.storeCalls " + name + getStoreExtension());

		BeanList oBeanList = null;
		try {
			FileInputStream fis = new FileInputStream(getStorePrefix() + name
					+ getStoreExtension());
			ObjectInputStream ois = new ObjectInputStream(fis);
			oBeanList = (BeanList) ois.readObject();
		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

		return oBeanList;

	}

	/**-----------------------------------------------------------------------------
	@method {
	@name
	@description   method from Paulo Soares  psoares@consiste.pt
	}
	------------------------------------------------------------------------------*/
	public static InputStream getResourceStream(String key, Class oTarget) {
		AppLogger.debug2("ContentManager.getResourceStream - called key=" + key
				+ " target=" + oTarget.getName());

		InputStream is = null;
		// Try to use Context Class Loader to load the properties file.
		try {
			java.lang.reflect.Method getCCL = Thread.class.getMethod(
					"getContextClassLoader", new Class[0]);

			if (getCCL != null) {

				AppLogger
						.debug("ContentManager.getResourceStream - found getContextClassLoader.");

				ClassLoader contextClassLoader = (ClassLoader) getCCL.invoke(
						Thread.currentThread(), new Object[0]);

				AppLogger
						.debug("ContentManager.getResourceStream - invoked getContextClassLoader.");

				is = contextClassLoader.getResourceAsStream(key);

				AppLogger
						.debug("ContentManager.getResourceStream -  getContextClassLoader is="
								+ is);

			}
		} catch (Exception oEx) {
			AppLogger.error("ContentManager.getResourceStream - Exception="
					+ oEx.getMessage());
		}

		if (is == null) {
			is = oTarget.getResourceAsStream(key);
		}

		if (is == null) {
			String sMessage = "Exception in getResourceStream: Unable to load resource "
					+ key + " as stream.";
			AppLogger.error("ContentManager.getResourceStream " + sMessage);
			throw new RuntimeException(sMessage);
		}

		return is;
	}

	/**-----------------------------------------------------------------------------
	 @method {
	 @name
	 @description
	 @signature
	 }
	------------------------------------------------------------------------------*/
	public static byte[] getResource(String sKey, Class oClass) {

		AppLogger.debug("ContentManager.getResource - called.");

		InputStream oIs = getResourceStream(sKey, oClass);

		ByteArrayOutputStream oByteArray = new ByteArrayOutputStream();

		AppLogger.debug("ContentManager.getResource - ByteArrayOutputStream.");

		try {
			byte buf[] = new byte[1024];
			while (true) {
				int size = oIs.read(buf);
				if (size < 0)
					break;
				oByteArray.write(buf, 0, size);
			}
			oIs.close();
		} catch (Exception oEx) {
			AppLogger.error(oEx);
			throw new RuntimeException(oEx.toString());
		}

		return oByteArray.toByteArray();
	}

	public void storeBeanList(BeanList oList) {

		String listName = oList.getListName();
		String storeExt = getStoreExtension();

		try {
			FileOutputStream fos = new FileOutputStream(getStorePrefix()
					+ listName + storeExt);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(oList);
			//refreshBeanListCache(oList);        	
		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

	}

	/*
	 * if the storage is permanent, first look for already
	 * deserialized instance of the list in the cache
	 * and if nothing is found then look on disk.
	 */
	public void refreshBeanListCache(BeanList oBeanList) {

		String strListName = oBeanList.getListName();
		Element elemStore = getStorageElement(strListName);

		BeanList oldBeanList = getBeanList(elemStore);

		if (_oBeanListPersist.containsValue(oldBeanList)) {
			_oBeanListPersist.remove(oldBeanList);
			_oBeanListPersist.put(strListName, oBeanList);

		}

		if (_oBeanListSession.containsValue(oldBeanList)) {
			_oBeanListSession.remove(oldBeanList);
			_oBeanListSession.put(strListName, oBeanList);

		}

	}

	public BeanList getBeanList(Element oElement) {

		BeanList oBeanList = null;

		String name = oElement.getAttributeValue("name");
		String scope = oElement.getAttributeValue("scope");

		if (scope.equals("persistant")) {

			//is list in cache
			if (_oBeanListPersist.containsKey(name)) {

				oBeanList = (BeanList) _oBeanListPersist.get(name);

			} else {
				oBeanList = loadList(this.getStoreExtension(), getStorePrefix()
						+ name);

				if (oBeanList == null) {
					String type = oElement.getAttributeValue("type");
					String key = oElement.getAttributeValue("key");
					oBeanList = new BeanList(name, type, key);
				}

				_oBeanListPersist.put(name, oBeanList);

			}

			return oBeanList;

		} else if (scope.equals("session")) {

			oBeanList = (BeanList) _oBeanListSession.get(name);

			if (oBeanList == null) {

				String type = oElement.getAttributeValue("type");
				String key = oElement.getAttributeValue("key");
				oBeanList = new BeanList(name, type, key);

				_oBeanListSession.put(name, oBeanList);
			}

		}

		return oBeanList;
	}

	public static BeanList loadList(String listContext, String listName) {

		AppLogger.debug("BeanList.loadList:" + listName + listContext);

		BeanList oBeanList = null;

		try {

			FileInputStream fis = new FileInputStream(listName + listContext);

			ObjectInputStream ois = new ObjectInputStream(fis);

			oBeanList = (BeanList) ois.readObject();

		} catch (java.io.FileNotFoundException oNoFileEx) {
			AppLogger.debug(oNoFileEx.toString());
		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

		return oBeanList;
	}

	public InstanceContext getInstanceContext() {
		return _oInstanceContext;
	}

	public static void removeContextProperty(InstanceContext oInstance,
			String strContextKey) throws Exception {

		if (strContextKey == null)
			throw new Exception("Context Element must have a name.");

		oInstance.setProperty(strContextKey, null);

	}

	public static void clearContext(InstanceContext oInstance) throws Exception {
		oInstance.removeProperties();
	}

	public static Object getContextPropertyEx(InstanceContext oInstance,
			String strContextKey) throws Exception {

		if (strContextKey == null)
			throw new Exception("Context Element must have a name.");

		Object oInfo = oInstance.getProperty(strContextKey);

		if (oInfo == null) {

			//try{

			Element oStorage = oInstance.getContentManager().getContextElement(
					strContextKey);
			String strClass = oStorage.getAttributeValue("class");
			Class clazz = Class.forName(strClass);
			oInfo = clazz.newInstance();

			//}catch(Exception oEx){
			//	AppLogger.error(oEx);
			//}

			oInstance.setProperty(strContextKey, oInfo);

		}

		return oInfo;

	}

	public void reloadProperties() {

		_oSessionProperties = this.loadProperties();

	}

	public HashMap loadProperties() {

		String storeExt = getStoreExtension();
		String storeName = getStorePrefix() + _sessionPropFilename + storeExt;

		//AppLogger.debug("MulticontentManager.loadProperties:" + storeName);

		HashMap oProperties = null;

		try {

			FileInputStream fis = new FileInputStream(storeName);

			ObjectInputStream ois = new ObjectInputStream(fis);

			oProperties = (HashMap) ois.readObject();

		} catch (java.io.FileNotFoundException oNoFileEx) {
			AppLogger.debug(oNoFileEx.toString());
		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

		return oProperties;
	}

	public void saveProperties() {

		String storeExt = getStoreExtension();
		String storeName = getStorePrefix() + _sessionPropFilename + storeExt;

		AppLogger.debug("MultiContentManager.saveProperites " + storeName);

		try {

			FileOutputStream fos = new FileOutputStream(storeName);

			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(_oSessionProperties);

			//refreshBeanListCache(oList);

		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

	}

	public ActiveField getSetting(String name) throws Exception {

		Element oElement = getSettingsElement(name);

		return getPropertyEx(oElement);

	}

	public void setSetting(String name, ActiveField oValue) throws Exception {

		Element oElement = getSettingsElement(name);

		setPropertyEx(name, oValue, oElement);

	}

	public synchronized HashMap getPropertyMap() {

		if (_oSessionProperties == null)
			_oSessionProperties = loadProperties();

		if (_oSessionProperties == null)
			_oSessionProperties = new HashMap();

		return _oSessionProperties;

	}

	public void replaceProperty(String key, ActiveField oValue) {

		HashMap oMap = getPropertyMap();

		oMap.remove(key);

		oMap.put(key, oValue);

	}

	public void setPropertyEx(String name, ActiveField oNew, Element oElement) {

		HashMap oMap = getPropertyMap();

		ActiveField oOld = (ActiveField) _oSessionProperties.get(name);

		if (oOld == null) {

			try {
				String strClass = oElement.getAttributeValue("class");
				strClass = strClass == null ? oElement
						.getAttributeValue("type") : strClass;

				Class clazz = Class.forName(strClass);

				Constructor oCtor = clazz.getConstructor(new Class[] { oElement
						.getClass() });

				ActiveField created = (ActiveField) oCtor
						.newInstance(new Object[] { oElement });

				created.setValue(oNew);
				created.setElement(oElement);

				oMap.put(name, created);

			} catch (Exception oEx) {
				AppLogger.error(oEx);
			}
		} else {

			oOld.setValue(oNew);

		}

	}

	public ActiveField getPropertyEx(Element oElement) throws Exception {

		HashMap oMap = getPropertyMap();

		String key = oElement.getAttributeValue("property");
		ActiveField oValue = (ActiveField) _oSessionProperties.get(key);

		if (oValue == null) {

			String strClass = oElement.getAttributeValue("class");
			strClass = strClass == null ? oElement.getAttributeValue("type")
					: strClass;

			Class clazz = Class.forName(strClass);

			Constructor oCtor = clazz.getConstructor(new Class[] { oElement
					.getClass() });

			oValue = (ActiveField) oCtor.newInstance(new Object[] { oElement });

			oMap.put(key, oValue);

		} else {

			oValue = (ActiveField) oValue.copyObject();
			oValue.setElement(oElement);
		}

		return oValue;

	}

	public void resetProperty(String key) throws Exception {

		HashMap oMap = getPropertyMap();

		ActiveField oValue = (ActiveField) _oSessionProperties.get(key);

		if (oValue != null) {
			oValue.reset();
		}

	}

	public static Properties getProperties() {

		if (_appProperties == null) {

			try {

				synchronized (MultiContentManager.class) {

					if (_appProperties == null) {
						_appProperties = new Properties();
						InputStream propInput = new DataInputStream(
								getResourceStream(APPL_STRINGS,
										MultiContentManager.class));
						//FileInputStream oStream = new FileInputStream(m_inFile);  	     	    			
						_appProperties.load(propInput);
					}

				}

			} catch (Exception oEx) {
				AppLogger.error(oEx);
			}

		}

		return _appProperties;

	}

	public static Properties getAppSettings() {

		if (_appSettings == null) {

			try {

				synchronized (MultiContentManager.class) {

					if (_appSettings == null) {

						_appSettings = new Properties();

						InputStream propInput = new DataInputStream(
								getResourceStream(SETUP_STRINGS,
										MultiContentManager.class));
						//FileInputStream oStream = new FileInputStream(m_inFile);

						_appSettings.load(propInput);

					}

				}

			} catch (Exception oEx) {
				AppLogger.error(oEx);
			}

		}

		return _appSettings;

	}

	public static void storeAppSettings(Properties newProperties) {

		try {

			synchronized (MultiContentManager.class) {
				_appSettings = newProperties;

				storeAppSettings();
			}

		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

	}

	public static void storeAppSettings() {

		if (_appSettings != null) {

			try {
				FileOutputStream oFile = new FileOutputStream(SETUP_STRINGS);
				OutputStream setupOutput = new DataOutputStream(oFile);
				_appSettings.store(setupOutput, "");
				//FileInputStream oStream = new FileInputStream(m_inFile);

			} catch (Exception oEx) {
				AppLogger.error(oEx);
			}

		}

	}

	public boolean isFalse(String conditionName) {

		boolean retVal = false;

		Element oElement = getSettingsElement(conditionName);

		DscBoolean oField = null;
		try {
			oField = (DscBoolean) getPropertyEx(oElement);

			retVal = oField.getValue();
		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

		return retVal;

	}

	public boolean isCondition(String conditionName) {

		boolean retVal = false;

		String setting = MultiContentManager.getAppSettings().getProperty(
				conditionName);

		return Boolean.valueOf(setting).booleanValue();

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.BusListener#signal(net.sourceforge.dscsim.controller.BusMessage)
	 */
	public void signal(BusMessage oMessage) {
		if (BusMessage.MSGTYPE_KEY.equals(oMessage.getType())
				&& DSC_RESET.equals(oMessage.getButtonEvent().getKeyId())) {
			getInstanceContext().removeProperties();
			return;
		}
	}

	/**
	 * set selected AddressEntryId into session info.
	 * @param addr
	 */
	public void setSelectedAddressId(AddressIdEntry addr) {
		this.selectedAddressId = addr;
	}

	public void setSelectedGroupId(AddressIdEntry addr) {
		this.selectedGroupId = addr;
	}

	/**
	 * get selected AddressEntryId from session info.
	 * @param addr
	 */
	public AddressIdEntry getSelectedAddressId() {
		return this.selectedAddressId;
	}

	public AddressIdEntry getSelectedGroupId() {
		return this.selectedGroupId;
	}

	public DscMessage getOutGoingDscMessage() {
		return outGoingDscMessage;
	}

	public void setOutGoingDscMessage(DscMessage outGoingDscMessage) {
		this.outGoingDscMessage = outGoingDscMessage;
	}

	public DscMessage getIncomingDscMessage() {
		return incomingDscMessage;
	}

	public void setIncomingDscMessage(DscMessage incomingDscMessage) {
		this.incomingDscMessage = incomingDscMessage;
	}

	public ArrayList<AddressIdEntry> getAddressIdList() {

		if (this.addressIdList == null) {
			this.addressIdList = readList(this.getStoreExtension(),
					getStorePrefix() + ADDRESS_BOOK_FNAME);

			if (this.addressIdList == null) {
				this.addressIdList = new ArrayList<AddressIdEntry>();
			}
		}
		return addressIdList;
	}

	public ArrayList<AddressIdEntry> getGroupIdList() {

		if (this.groupIdList == null) {
			this.groupIdList = readList(this.getStoreExtension(),
					getStorePrefix() + GROUP_BOOK_FNAME);

			if (this.groupIdList == null) {
				this.groupIdList = new ArrayList<AddressIdEntry>();
			}
		}
		return groupIdList;
	}

	public void storeListAddressIdList() {
		this.storeList(ADDRESS_BOOK_FNAME, this.addressIdList);
	}

	public void storeListGroupIdList() {
		this.storeList(GROUP_BOOK_FNAME, this.groupIdList);
	}

	public <T> void storeList(String listName, ArrayList<T> objList) {

		String storeExt = getStoreExtension();

		try {
			FileOutputStream fos = new FileOutputStream(getStorePrefix()
					+ listName + storeExt);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(objList);
		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

	}

	public static <T> ArrayList<T> readList(String listContext, String listName) {

		ArrayList<T> list = null;

		T m = null;
		try {

			FileInputStream fis = new FileInputStream(listName + listContext);

			ObjectInputStream ois = new ObjectInputStream(fis);

			list = (ArrayList<T>) ois.readObject();

		} catch (java.io.FileNotFoundException oNoFileEx) {
			AppLogger.debug(oNoFileEx.toString());
		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

		return list;
	}
}
