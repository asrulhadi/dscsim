/*
 * Created on 11.07.2006
 * katharina
 * 
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

package net.sourceforge.dscsim.controller.display.screens.impl;

import org.hibernate.Session;

import net.sourceforge.dscsim.controller.Button;
import net.sourceforge.dscsim.controller.MultiBeeper;
import net.sourceforge.dscsim.controller.BusMessage;
import net.sourceforge.dscsim.controller.InstanceContext;
import net.sourceforge.dscsim.controller.MultiContentManager;
import net.sourceforge.dscsim.controller.RadioCoreController;
import net.sourceforge.dscsim.controller.network.DscIACManager;
import net.sourceforge.dscsim.controller.screens.Screen;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JDisplay;
import net.sourceforge.dscsim.controller.screens.ActionMapping;

/**
 * Screen used to send message. User is pressed with a choice to proceed or
 * abort. The signal method will react accordingly.
 * 
 * @author katharina
 */
public class SendAckActionScreen extends ActionScreen {

	/**
	 * Add to make simulation more realistic. Without a delay the controller
	 * reacts too quickly.
	 */
	private static long SLEEP_AFTER_SEND = 1500;

	public SendAckActionScreen(JDisplay display, Screen screen) {
		super(display, screen);

	}

	/**
	 * handle outgoing messages and channel switching logic.
	 */
	@Override
	public ActionMapping notify(BusMessage oMessage) {

		String keyID = oMessage.getButtonEvent().getKeyId();
		String keyAction = oMessage.getButtonEvent().getAction();

		if (keyAction.equals(RELEASED)) {
			return null;
		}

		if (FK_ENT.equals(keyID)) {

			Button btCall = this.getInstanceContext().getController()
					.getButton(FK_CALL);
			if (btCall.getAction().equals(PRESSED)) {

				MultiContentManager mngr = getInstanceContext()
						.getContentManager();
				Dscmessage outGoing = mngr.getOutGoingDscmessage();

				if (outGoing.hasRecipient()==false) {
					outGoing.setRecipient(mngr.getIncomingDscmessage()
							.getSender());
				}

				/* if individual call, then wait for ack before changing channel */
				Dscmessage inComing = (Dscmessage) getInstanceContext()
						.getContentManager().getIncomingDscmessage();
				if (inComing != null
						&& inComing.getCallTypeCd().equals(CALL_TYPE_INDIVIDUAL)) {
					if (inComing.getChannel().equals(outGoing.getChannel()) == false
							&& COMPL_ABLE.equals(outGoing.getComplianceCd())) {
						outGoing.setCallTypeCd(CALL_TYPE_INDIVIDUAL);
					} else {
						/* set radio to agree upon channel */
						RadioCoreController oRadio = getInstanceContext()
								.getRadioCoreController();
						oRadio.setChannel(outGoing.getChannelStr());
						outGoing.setCallTypeCd(CALL_TYPE_INDIVIDUAL_ACK);
					}

				}
				/*
				 * if outgoing is one of the following, then switch the channel
				 * immediately as there will be no acks.
				 */
				if (CALL_TYPE_ALL_SHIPS.equals(outGoing.getCallTypeCd())
						|| CALL_TYPE_GROUP.equals(outGoing.getCallTypeCd())) {
					RadioCoreController oRadio = getInstanceContext()
							.getRadioCoreController();
					oRadio.setChannel(outGoing.getChannelStr());
				}

				/* make sure outgoing message has my mmsi */
				outGoing.setSender(getInstanceContext().getContentManager()
						.getMMSI());
				getInstanceContext().getBeeper().beepSync(
						MultiBeeper.BEEP_TRANSMITTING);
				AppLogger
						.debug("BeanSendScreen.signal =" + outGoing.toString());
				DscIACManager.getTransmitter().transmit(outGoing);

				inComing.setAckdTime(java.util.Calendar.getInstance().getTime());				
				Session session = mngr.getSessionFactory().getCurrentSession();
				session.beginTransaction();
				session.update(inComing);
				session.getTransaction().commit();

				// for affect and as well a yield to Transmitter.
				try {
					Thread.sleep(SLEEP_AFTER_SEND);
				} catch (Exception oEx) {
					AppLogger.error(oEx);
				}
				InstanceContext oInstanceContext = getInstanceContext();

				oInstanceContext.removeProperties();

				btCall.setRelease();

				return this.findActionMapping(keyAction, keyID);

			} else {
				return null;
			}

		}

		return null;
	}

}
