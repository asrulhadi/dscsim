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

import java.awt.Component;
import java.awt.Graphics;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.JApplet;
import javax.swing.JPanel;

import net.sourceforge.dscsim.controller.network.DscMessage;
import net.sourceforge.dscsim.controller.network.InternalBusListener;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.utils.AppletSoundList;


class DscAppPanel extends JPanel implements InstanceContext, InternalBusListener, Constants {
	
		//private Radio _oRadio = null;
	
		private MultiContentManager _oContentManager = null;
		private MultiBus _oBus = null;
		private MultiController _oController = null;
		private MultiClu _oClu = null;
		private MultiBeeper _oBeeper = null;
		private ApplicationContext _oAppContext = null;
		private RadioCoreController _oRadioCoreController = null;
		
		private Hashtable _oSessionPropertis = new Hashtable();
		
		public DscAppPanel(String strMMSI, ApplicationContext oAppContext){
			
			AppLogger.info("DiscAppPanel.DscAppPanel - MMSI=" + strMMSI);
			
			_oAppContext = oAppContext;
			setName(strMMSI);
			
			_oContentManager = new MultiContentManager(this);
			_oContentManager.setMMSI(strMMSI);
			

		}
		

		public MultiClu getClu(){
			return _oClu;
			
			
		}
		public MultiContentManager getContentManager(){
			return _oContentManager;
		}
		public MultiBus getBus(){
			return _oBus;
		}

		public MultiBeeper getBeeper(){
			
			if(_oBeeper == null)
				throw new RuntimeException("DscAppPanel.getBeeper = Beeper is null.");
			
			return _oBeeper;
		}

		public MultiController getController(){
			return _oController;
		}

		
		public void stop() {
			
			removeAll();
							
			AppletSoundList.destroySingleton();
							
			AppLogger.debug("applet.DscAppPanel stop finished");
			
		}
		public void init(Component oComponent) {
						
			AppLogger.debug("applet.DscAppPanel init started");
			
			_oBus = MultiBus.getInstance();
			_oController = MultiController.getInstance(this);
			_oClu = MultiClu.getInstance(this);
									
			getController().init(oComponent, 0, 0);
			
			addMouseListener(getController());
							
			AppLogger.debug("applet.DscAppPanel putting Controller online");			
			getBus().putOnline(getController());
		
			AppLogger.debug("applet.DscAppPanel putting Clu online");			
			//put Clu online.
			getBus().putOnline(getClu());
			
			AppLogger.debug("applet.DscAppPane putting Beeper on line.");	
			_oBeeper = MultiBeeper.getInstance(this);
			getBus().putOnline(_oBeeper);
													
			AppLogger.debug("applet.DscApp sending RESET");						
			getBus().publish(new BusMessage(this, BTN_RESET));

			AppLogger.debug("applet.DscAppPanel init finished");


    }
	
    public void paint(Graphics g) {
    	
 
       getController().paint(g);
       
       //_oRadio.paint(g);
                   
 
    }




	public void keyReleased(String keyId) {
		
	}


	public void updateSignal(DscMessage oDscMessage) {
		
		AppLogger.debug("applet.DscAppPanel.updateSignal=" + oDscMessage.toString());
		
		String msgFromMMSI = oDscMessage.getFromMMSI();
		String msgType = oDscMessage.getCallType();
		String msgToMMSI = oDscMessage.getToMMSI();
		String myMMSI = getApplicationContext().getIndividualMmsi();
		String myGroupMMSI = getApplicationContext().getGroupMmsi();
		boolean hasToMMSI = oDscMessage.hasToMMSI();
		
		if((myMMSI.equals(msgFromMMSI)==false)){
			
			if((CALL_TYPE_DISTRESS.equals(msgType) || CALL_TYPE_DISTRESS_ACK.equals(msgType))
				||  (CALL_TYPE_ALL_SHIPS.equals(msgType) || CALL_TYPE_ALL_SHIPS_ACK.equals(msgType))
				||  ((hasToMMSI && myMMSI.equals(msgToMMSI)==true)) && (CALL_TYPE_INDIVIDUAL.equals(msgType) || CALL_TYPE_INDIVIDUAL_ACK.equals(msgType)) 
				||  ((hasToMMSI && myGroupMMSI.equals(msgToMMSI)==true) && (CALL_TYPE_GROUP.equals(msgType) || CALL_TYPE_GROUP.equals(msgType)))){
			
				//ContentManager.storeLastDistressCalls(oDscMessage);
				
				getContentManager().storeIncomingMessage((DscMessage)oDscMessage.clone());
				
				BusMessage oBusMessage = new BusMessage(this, oDscMessage);
				
				getBus().publish(oBusMessage);
		
			}
			
		}
				
		//this.repaint();
	}

	/* (non-Javadoc)
	 * @see applet.InstanceContext#getProperty(java.lang.String)
	 */
	public Object getProperty(String key) {
		// TODO Auto-generated method stub
		return _oSessionPropertis.get(key);
	}

	/* (non-Javadoc)
	 * @see applet.InstanceContext#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String key, Object value) {
		// TODO Auto-generated method stub
		
		if(value == null){
			_oSessionPropertis.remove(key);
			return;
		}
		
		if(_oSessionPropertis.containsKey(key)){
			_oSessionPropertis.remove(key);			
		}
		_oSessionPropertis.put(key, value);
		
	}

	public ApplicationContext getApplicationContext() {
		return _oAppContext;
	}
	
	public RadioCoreController getRadioCoreController(){
		return _oRadioCoreController;
	}

	public void setRadioCoreController(RadioCoreController oRadioCoreController){
		_oRadioCoreController = oRadioCoreController;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.dscsim.controller.InstanceContext#removeProperties()
	 */
	public void removeProperties() {
		_oSessionPropertis.clear();
	}


}

