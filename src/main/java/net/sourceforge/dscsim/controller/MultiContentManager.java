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
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import net.sourceforge.dscsim.controller.data.types.ActiveField;
import net.sourceforge.dscsim.controller.message.types.MMSI;
import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreenFactory;
import net.sourceforge.dscsim.controller.infostore.InfoStoreFactory;
import net.sourceforge.dscsim.controller.settings.*;
import net.sourceforge.dscsim.controller.data.types.*;
import net.sourceforge.dscsim.controller.screens.ActionMapping;
import net.sourceforge.dscsim.controller.screens.Device;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.screens.Screen;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.persistence.HibernateUtil;


public class MultiContentManager implements BusListener, Constants {

	private static String cDISTRESS_CALL_PERSISTANCE = STORE_BASE
			+ "distresscalls";
	private MMSI mmsi = null;

	private InstanceContext _oInstanceContext = null;

	// cache for lists
	private ArrayList _oSessionCache = new ArrayList();

	// session distress calls
	private ArrayList _oIncomingDistressCalls = new ArrayList();
	private ArrayList _oIncomingDistressAcks = new ArrayList();

	private HashMap _oBeanListSession = new HashMap();
	private HashMap _oBeanListPersist = new HashMap();

	// persistant list
	private HashMap _oSessionLists = new HashMap();

	// persistant properties
	private HashMap _oSessionProperties = null;
	private String _sessionPropFilename = "settings";

	private static Properties _appProperties = null;
	private static Properties _appSettings = null;

	private JScreenFactory screenFactory = null;
	private InfoStoreFactory infostoreFactory = null;

	private Dscmessage outGoingDscmessage = new Dscmessage();
	private Dscmessage incomingDscmessage = new Dscmessage();

	private AddressIdEntry selectedAddressId = null;
	private ArrayList<AddressIdEntry> addressIdList = null;

	private AddressIdEntry selectedGroupId = null;
	private ArrayList<AddressIdEntry> groupIdList = null;

	private ArrayList<Dscmessage> incomingOtherCalls = null;
	private Dscmessage selectedIncomingOtherCall = null;

	private ArrayList<Dscmessage> incomingDistressCalls = null;
	private Dscmessage selectedIncomingDistressCall = null;

	static {
		try {
			HibernateUtil.getSessionFactory().getCurrentSession();			
		}catch(Exception e){
			AppLogger.error(e);
		}
	}
	public static MultiContentManager getInstance(InstanceContext oCtx) { 
		return new MultiContentManager(oCtx.getContentManager().getMMSI(), oCtx);
	}

	public MultiContentManager(String mmsi, InstanceContext oInstanceContext) {
		_oInstanceContext = oInstanceContext;

		try {

			String xmlName = oInstanceContext.getApplicationContext()
					.getDeviceXmlName();
			DataInputStream dataInput = new DataInputStream(getResourceStream(
					xmlName, this.getClass()));
			this.screenFactory = new JScreenFactory(new JDisplay(
					DISPLAY_X - 11, DISPLAY_Y + 1, 273, 160, 8, 21), dataInput);

			this.infostoreFactory = new InfoStoreFactory(mmsi);

		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

	}

	public MMSI getAsMMSI() {
		return mmsi;
	}

	public void setMMSI(String strMMSI) {
		if (strMMSI != null)
			mmsi = new MMSI(strMMSI);
		else
			mmsi = null;
	}

	public String getMMSI() {

		if (mmsi != null)
			return mmsi.toString();
		else
			return "";
	}

	public void putCache(ActionScreen oScreen) {

		/*
		 * String scope = oScreen.getAttributeValue("scope");
		 * 
		 * if(scope.equals("session")) _oSessionCache.add(oScreen);
		 */
	}

	private void clearCache() {
		_oSessionCache.clear();
	}

	private ActionScreen getCache(String srchName) {

		String name = null;
		ActionScreen oScreen = null;
		for (int i = 0; i < this._oSessionCache.size(); i++) {
			oScreen = (ActionScreen) _oSessionCache.get(i);
			name = ((ActionScreen)oScreen).getName();

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

	public ActionScreen getScreenContent(String strScreenName, InstanceContext oCtx) {
		ActionScreen oRet = getCache(strScreenName);

		if (oRet != null)
			return oRet;

		try {
			ActionScreen screen = this.screenFactory.getScreen(strScreenName, oCtx);

			String scope = screen.getScreenBindings().getScope();

			if (screen != null && scope.equals("session"))
				_oSessionCache.add(screen);

			if (screen != null)
				return screen;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return oRet;

	}

	/**
	 * search utility for mappings.
	 * 
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

	/*
	 * public Element getCodeString(String strId){
	 * 
	 * Element oRoot = m_oDocument.getRootElement();
	 * 
	 * Element oScreens = oRoot.getChild("strings");
	 * 
	 * Iterator oIter = oScreens.getChildren().iterator();
	 * 
	 * Element oTarget = null; Attribute oAttr = null;
	 * 
	 * while(oIter.hasNext()) { oTarget =(Element)oIter.next();
	 * 
	 * oAttr = oTarget.getAttribute("code"); String strValue = oAttr != null ?
	 * oAttr.getValue() : null;
	 * 
	 * if(strValue != null && strValue.equals(strId)) return oTarget; else
	 * oTarget = null;
	 *  }
	 * 
	 * return oTarget;
	 *  }
	 */

	public String getCodeString(String strId) {

		return getProperties().get(strId).toString();

	}


	public boolean isIncomingOtherRequest(Dscmessage oMessage) {

		boolean bValue = false;

		if (CALL_TYPE_GROUP.equals(oMessage.getCallTypeCd())) {
			bValue = true;
		} else if (CALL_TYPE_INDIVIDUAL.equals(oMessage.getCallTypeCd())) {
			bValue = true;
		} else if (CALL_TYPE_ALL_SHIPS.equals(oMessage.getCallTypeCd())) {
			bValue = true;
		} else if (CALL_TYPE_INDIVIDUAL_ACK.equals(oMessage.getCallTypeCd())) {
			bValue = true;
		}

		return bValue;

	}

	public void storeCallMessage(Dscmessage message) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.save(message);
		session.getTransaction().commit();
	}

	public void removeCallMessage(Dscmessage message) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.delete(message);
		session.getTransaction().commit();
		/*
		ArrayList<Dscmessage> calls = this.getIncomingDistressCalls();
		calls.remove(oMessage);
		this.storeList(INCOMING_DISTRESS_CALLS, calls);
		*/
	}

	public List getStorageList(String storageName) {

		return null;

	}

	public Dscmessage findLatestMessage(Dscmessage oTarget) {

		List oCalls = getIncomingOtherCalls();

		if (oCalls == null)
			return null;

		Dscmessage oFound = null;
		for (int i = 0; i < oCalls.size(); i++) {
			oFound = (Dscmessage) oCalls.get(i);
			
			if (oFound.getUid() == oTarget.getUid()) {
				break;
			} else {
				oFound = null;
			}

		}

		return oFound;
	}

	public List<Dscmessage> getIncomingOtherCalls() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();		
		session.beginTransaction();
		
		List<Dscmessage>list = session.createCriteria(Dscmessage.class)
		.add(Restrictions.ne(this.PROP_SENDER, this.getMMSI()))
		.add(Restrictions.ne(PROP_NATURE_CD, this.CALL_TYPE_DISTRESS))
		.addOrder(Order.desc(PROP_UID))
		.list();
	
		return list;
	}

	public List<Dscmessage> getIncomingDistressCalls() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction().begin();
		List<Dscmessage>list = session.createCriteria(Dscmessage.class)
		.add(Restrictions.ne(PROP_SENDER, this.getMMSI()))
		.add(Restrictions.eq(PROP_NATURE_CD, this.CALL_TYPE_DISTRESS))
		.addOrder(Order.desc(PROP_UID))
		.list();
		
		return list;
	}

	public List<Dscmessage> getIncomingDistressAcks() {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction().begin();
		List<Dscmessage>list = session.createCriteria(Dscmessage.class)
		.add(Restrictions.ne(PROP_SENDER, this.getMMSI()))
		.add(Restrictions.eq(PROP_NATURE_CD, this.CALL_TYPE_DISTRESS_ACK))
		.addOrder(Order.desc(PROP_UID))
		.list();
		
		return list;
		
	}

	public List fetchDistressCalls() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Dscmessage>list = session.createCriteria(Dscmessage.class)
		.add(Restrictions.ne(PROP_SENDER, this.getMMSI()))
		.add(Restrictions.eq(PROP_NATURE_CD, this.CALL_TYPE_DISTRESS))
		.addOrder(Order.desc(PROP_UID))
		.list();
		
		return list;
	}

	public String getStoreExtension() {

		String strExt = getMMSI();
		strExt = strExt.length() > 0 ? "_" + strExt : "";

		return strExt + ".obj";

	}

	public String getStorePrefix() {
		return Constants.STORE_BASE;
	}
	/**
	 * -----------------------------------------------------------------------------
	 * 
	 * @method {
	 * @name
	 * @description method from Paulo Soares psoares@consiste.pt }
	 *              ------------------------------------------------------------------------------
	 */
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

	/**
	 * -----------------------------------------------------------------------------
	 * 
	 * @method {
	 * @name
	 * @description
	 * @signature }
	 *            ------------------------------------------------------------------------------
	 */
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


	public void reloadProperties() {

		_oSessionProperties = this.loadProperties();

	}

	public HashMap loadProperties() {

		String storeExt = getStoreExtension();
		String storeName = getStorePrefix() + _sessionPropFilename + storeExt;

		// AppLogger.debug("MulticontentManager.loadProperties:" + storeName);

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

			// refreshBeanListCache(oList);

		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

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
						// FileInputStream oStream = new
						// FileInputStream(m_inFile);
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
						// FileInputStream oStream = new
						// FileInputStream(m_inFile);

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
				// FileInputStream oStream = new FileInputStream(m_inFile);

			} catch (Exception oEx) {
				AppLogger.error(oEx);
			}

		}

	}
	
	public boolean isCondition(String conditionName) {

		boolean retVal = false;

		String setting = MultiContentManager.getAppSettings().getProperty(
				conditionName);

		return Boolean.valueOf(setting).booleanValue();

	}

	/*
	 * (non-Javadoc)
	 * 
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
	 * 
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
	 * 
	 * @param addr
	 */
	public AddressIdEntry getSelectedAddressId() {
		return this.selectedAddressId;
	}

	public AddressIdEntry getSelectedGroupId() {
		return this.selectedGroupId;
	}

	public Dscmessage getOutGoingDscmessage() {
		return outGoingDscmessage;
	}

	public void setOutGoingDscmessage(Dscmessage outGoingDscmessage) {
		this.outGoingDscmessage = outGoingDscmessage;
	}

	public Dscmessage getIncomingDscmessage() {
		return incomingDscmessage;
	}

	public void setIncomingDscmessage(Dscmessage incomingDscmessage) {
		this.incomingDscmessage = incomingDscmessage;
	}

	public String findAddressId(String mmsi) {
		for (AddressIdEntry a : this.getAddressIdList()) {
			if (a.getId().equals(mmsi)) {
				return a.getName();
			}
		}
		return null;
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

	public Dscmessage getSelectedIncomingOtherCall() {
		return selectedIncomingOtherCall;
	}

	public void setSelectedIncomingOtherCall(
			Dscmessage selectedIncomingOtherCall) {
		this.selectedIncomingOtherCall = selectedIncomingOtherCall;
	}

	public Dscmessage getSelectedIncomingDistressCall() {
		return selectedIncomingDistressCall;
	}

	public void setSelectedIncomingDistressCall(
			Dscmessage selectedIncomingDistressCall) {
		this.selectedIncomingDistressCall = selectedIncomingDistressCall;
	}
	
}
