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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreenFactory;
import net.sourceforge.dscsim.controller.message.types.AddressIdEntry;
import net.sourceforge.dscsim.controller.message.types.AddressIdEntryType;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.message.types.MMSI;
import net.sourceforge.dscsim.controller.screens.ActionMapping;
import net.sourceforge.dscsim.controller.screens.Device;
import net.sourceforge.dscsim.controller.settings.DistressSettings;
import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class MultiContentManager implements BusListener, Constants {

	/*
	 * MMSI for this instance of dscsmim.
	 */
	private MMSI mmsi = null;

	/*
	 * 
	 */
	private InstanceContext _oInstanceContext = null;

	/**
	 * Hibernate session factory.
	 */
	private SessionFactory sessionFactory = null;

	/*
	 * Cache for screens
	 */
	private ArrayList<ActionScreen> _oSessionCache = new ArrayList<ActionScreen>();

	private static Properties appProperties = null;
	private static Properties appSettings = null;

	private JScreenFactory screenFactory = null;

	private Dscmessage outGoingDscmessage = new Dscmessage();
	private Dscmessage incomingDscmessage = new Dscmessage();

	private AddressIdEntry selectedAddressId = null;

	private AddressIdEntry selectedGroupId = null;

	private Dscmessage selectedIncomingOtherCall = null;

	private Dscmessage selectedIncomingDistressCall = null;

	public static MultiContentManager getInstance(InstanceContext oCtx) {
		return new MultiContentManager(oCtx.getContentManager().getMMSI(), oCtx);
	}

	public MultiContentManager(String mmsi, InstanceContext oInstanceContext) {
		_oInstanceContext = oInstanceContext;

		try {

			String xmlName = oInstanceContext.getApplicationContext()
					.getDeviceXmlName();

			DataInputStream dataInput = new DataInputStream(ClassLoader.getSystemResourceAsStream(xmlName));

			this.screenFactory = new JScreenFactory(new JDisplay(
					DISPLAY_X - 11, DISPLAY_Y + 1, 273, 160, 8, 21), dataInput);

			initHibernate(mmsi);

		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

	}

	/**
	 * Initialize hibernate and create database file if one doesn't already
	 * exist.
	 * 
	 * @param mmsi
	 * @throws Exception
	 */
	private void initHibernate(String mmsi) throws Exception {

		String uid = null;
		if (mmsi == null || mmsi.length() == 0) {
			uid = "000000000";
		} else {
			uid = mmsi;
		}

		String CONN_URL_PATH = STORE_BASE + uid + "_" + HSQLDB_NAME;
		String HSQL_SCRIPT = STORE_BASE + HSQLDB_NAME + ".script";
		String HSQL_MMSI_SCRIPT = CONN_URL_PATH + ".script";

		File dbFile = new File(HSQL_MMSI_SCRIPT);
		if (!dbFile.exists()) {
			/* No file for mmsi. Copy the template */
			InputStream is = ClassLoader.getSystemResourceAsStream(HSQL_SCRIPT);
			FileOutputStream os = new FileOutputStream(HSQL_MMSI_SCRIPT);
			byte buff[] = new byte[1024];
			int read = 0;
			while ((read = is.read(buff)) > 0) {
				os.write(buff, 0, read);
			}
			is.close();
			os.close();
		}

		/* configure hibernate with hsql database */
		Configuration conf = new Configuration();
		conf.setProperty("hibernate.connection.url", "jdbc:hsqldb:file:"
				+ CONN_URL_PATH);
		conf.configure("etc/hibernate.cfg.xml");
		sessionFactory = conf.buildSessionFactory();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
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
			name = ((ActionScreen) oScreen).getName();

			if (name.equals(srchName))
				return oScreen;
		}

		return null;
	}

	public DistressSettings getInfoStore() {

		DistressSettings setting = null;

		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		setting = (DistressSettings) session.get(DistressSettings.class, this
				.getMMSI());
		if (setting == null) {
			setting = new DistressSettings();
			setting.setSender(this.getMMSI());
			setting.setEnteredTime(java.util.Calendar.getInstance().getTime());
			session.save(setting);
		}

		return setting;
	}

	public void persistInfoStore(DistressSettings settings) {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		settings.setEnteredTime(java.util.Calendar.getInstance().getTime());
		session.update(settings);
		session.getTransaction().commit();
	}

	public ActionScreen getScreenContent(String strScreenName,
			InstanceContext oCtx) {
		ActionScreen oRet = getCache(strScreenName);

		if (oRet != null)
			return oRet;

		try {
			ActionScreen screen = this.screenFactory.getScreen(strScreenName,
					oCtx);

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
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.save(message);
		session.getTransaction().commit();
	}

	public void removeCallMessage(Dscmessage message) {

		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.delete(message);
		session.getTransaction().commit();
		/*
		 * ArrayList<Dscmessage> calls = this.getIncomingDistressCalls();
		 * calls.remove(oMessage); this.storeList(INCOMING_DISTRESS_CALLS,
		 * calls);
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
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();

		List<Dscmessage> list = session.createCriteria(Dscmessage.class).add(
				Restrictions.ne(this.PROP_SENDER, this.getMMSI())).add(
				Restrictions.ne(PROP_NATURE_CD, this.CALL_TYPE_DISTRESS))
				.addOrder(Order.desc(PROP_UID)).list();

		return list;
	}

	public List<Dscmessage> getIncomingDistressCalls() {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction().begin();
		List<Dscmessage> list = session.createCriteria(Dscmessage.class).add(
				Restrictions.ne(PROP_SENDER, this.getMMSI())).add(
				Restrictions.eq(PROP_NATURE_CD, this.CALL_TYPE_DISTRESS))
				.addOrder(Order.desc(PROP_UID)).list();

		return list;
	}

	public List<Dscmessage> getIncomingDistressAcks() {

		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction().begin();
		List<Dscmessage> list = session.createCriteria(Dscmessage.class).add(
				Restrictions.ne(PROP_SENDER, this.getMMSI())).add(
				Restrictions.eq(PROP_NATURE_CD, this.CALL_TYPE_DISTRESS_ACK))
				.addOrder(Order.desc(PROP_UID)).list();

		return list;

	}

	public List fetchDistressCalls() {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Dscmessage> list = session.createCriteria(Dscmessage.class).add(
				Restrictions.ne(PROP_SENDER, this.getMMSI())).add(
				Restrictions.eq(PROP_NATURE_CD, this.CALL_TYPE_DISTRESS))
				.addOrder(Order.desc(PROP_UID)).list();

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

	public static Properties getProperties() {

		if (appProperties == null) {

			try {
				synchronized (MultiContentManager.class) {
					if (appProperties == null) {
						appProperties = new Properties();
						InputStream is = ClassLoader.getSystemResourceAsStream(APPL_STRINGS);
						InputStream propInput = new DataInputStream(is);
						appProperties.load(propInput);
					}
				}

			} catch (Exception oEx) {
				AppLogger.error(oEx);
			}
		}

		return appProperties;

	}

	public static Properties getAppSettings() {
		if (appSettings == null) {
			try {
				synchronized (MultiContentManager.class) {
					if (appSettings == null) {
						appSettings = new Properties();
						InputStream propInput = new DataInputStream(
								ClassLoader.getSystemResourceAsStream(SETUP_STRINGS));
						appSettings.load(propInput);
					}
				}

			} catch (Exception oEx) {
				AppLogger.error(oEx);
			}
		}

		return appSettings;

	}

	public static void storeAppSettings(Properties newProperties) {

		try {

			synchronized (MultiContentManager.class) {
				appSettings = newProperties;

				storeAppSettings();
			}

		} catch (Exception oEx) {
			AppLogger.error(oEx);
		}

	}

	public static void storeAppSettings() {

		if (appSettings != null) {

			try {
				FileOutputStream oFile = new FileOutputStream(SETUP_STRINGS);
				OutputStream setupOutput = new DataOutputStream(oFile);
				appSettings.store(setupOutput, "");
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

	public List<AddressIdEntry> getAddressIdList() {

		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction().begin();
		List<AddressIdEntry> list = session
				.createCriteria(AddressIdEntry.class).add(
						Restrictions.eq(PROP_TYPE_CD, AddressIdEntryType.IN))
				.addOrder(Order.desc(PROP_ID)).list();
		session.flush();
		return list;

	}

	public void removeAddressIdEntry(AddressIdEntry entry) {

		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction().begin();
		session.delete(entry);
		session.getTransaction().commit();

	}

	public List<AddressIdEntry> getGroupIdList() {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction().begin();
		List<AddressIdEntry> list = session
				.createCriteria(AddressIdEntry.class).add(
						Restrictions.eq(PROP_TYPE_CD, AddressIdEntryType.GR))
				.addOrder(Order.desc(PROP_ID)).list();
		return list;
	}

	public void addAddressIdEntry(AddressIdEntry entry) {
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.save(entry);
		session.getTransaction().commit();
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
