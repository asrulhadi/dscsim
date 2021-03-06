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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.dscsim.controller.message.types.Dscmessage;
import net.sourceforge.dscsim.controller.display.screens.framework.ActionScreen;
import net.sourceforge.dscsim.controller.display.screens.framework.JScreen;
import net.sourceforge.dscsim.controller.network.DscIACManager;

import net.sourceforge.dscsim.controller.display.screens.impl.SendDistressScreen;
import net.sourceforge.dscsim.controller.utils.AppLogger;
import net.sourceforge.dscsim.controller.screens.ActionMapping;

/**
 * Central unit for controlling state flow.
 */
public class MultiClu implements BusListener, Constants {

	private ArrayList<ActionScreen> _oScreenStack = new ArrayList<ActionScreen>();
	private InstanceContext _oContext = null;
	private volatile boolean _powerOn = false;

	private MultiClu(InstanceContext oSessionContext) {
		_oContext = oSessionContext;
		// DscIACManager.initSyncPublisher(_oContext.getApplicationContext());
	}

	public static MultiClu getInstance(InstanceContext oSessionContext) {
		return new MultiClu(oSessionContext);
	}

	public List getScreenCallStack() {
		return _oScreenStack;
	}

	/**
	 * Return the index of the target screen.
	 * 
	 * @param oTarget.
	 * @return index of screen on stack.
	 */
	private int indexOf(ActionScreen oTarget) {
		if (oTarget == null)
			return -1;

		int idxLast = _oScreenStack.size() - 1;
		ActionScreen oCurr = null;
		for (int idxCurr = idxLast; idxCurr >= 0; idxCurr--) {

			oCurr = (ActionScreen) _oScreenStack.get(idxCurr);

			if (oTarget.getAttributeValue("name").equals(
					oCurr.getAttributeValue("name")))
				return idxCurr;
		}
		return -1;
	}

	/**
	 * remove screen from the top of the stack if the screen is
	 * SendDistressScreen screen. It could be that the user has closed the
	 * screen and that it's no long on top.
	 * 
	 * @param oMessage
	 * @throws Exception
	 */
	private void popStackDistress(BusMessage oMessage) throws Exception {
		int idxLast = _oScreenStack.size() - 1;
		if (idxLast > 0) {
			ActionScreen oScreen = (ActionScreen) _oScreenStack
					.remove(idxLast);
			if (oScreen instanceof SendDistressScreen) {
				oScreen.exit(oMessage);
				oScreen = (ActionScreen) _oScreenStack.get(idxLast - 1);
				_oContext.getController().setScreenContent(oScreen);
			}
		}
	}

	/**
	 * remove the top entry from the Stack.
	 * 
	 * @param oMessage
	 * @throws Exception
	 */
	/**
	 * remove the top entry from the Stack.
	 * 
	 * @param oMessage
	 * @throws Exception
	 */
	private void popStock(BusMessage oMessage) throws Exception {

		int idxLast = _oScreenStack.size() - 1;

		if (idxLast > 0) {

			ActionScreen oScreen = (ActionScreen) _oScreenStack
					.remove(idxLast);

			// oScreen.exit(oMessage);

			oScreen = (ActionScreen) _oScreenStack.get(idxLast - 1);

			_oContext.getController().setScreenContent(oScreen);

		}

	}

	/**
	 * if a SendDistressScreen is on top, then a distress call is in progress.
	 * 
	 * @return
	 */
	public synchronized boolean isDistressCallInprogress() {
		if (_oScreenStack.size() > -1) {
			ActionScreen oScreen = (ActionScreen) _oScreenStack
					.get(_oScreenStack.size() - 1);
			if (oScreen instanceof SendDistressScreen) {
				return true;
			}
		}
		return false;
	}

	/**
	 * reset the state stack and call exit for all states. Return to the welcome
	 * screen.
	 * 
	 * @param oMessage
	 * @throws Exception
	 */
	/**
	 * reset the state stack and call exit for all states. Return to the welcome
	 * screen.
	 * 
	 * @param oMessage
	 * @throws Exception
	 */
	private void resetScreenStack(BusMessage oMessage) throws Exception {

		// leave bottom 0 elment on stack.
		ActionScreen oScreen = null;

		for (int i = _oScreenStack.size() - 1; i >= 0; i--) {

			if (oScreen instanceof ActionScreen) {
				ActionScreen si = (ActionScreen) oScreen;
				oScreen = _oScreenStack.remove(i);
				si.exit(oMessage);
				_oContext.getContentManager().putCache(si);
				_oContext.getController().setScreenContent(oScreen);
			}
		}

		oScreen = _oContext.getContentManager().getScreenContent("welcome",
				_oContext);
		_oScreenStack.add(oScreen);
		((ActionScreen)oScreen).enter(oMessage);
		_oContext.getController().setScreenContent(oScreen);

	}

	/**
	 * handle incoming message and dispatch them to appropriate screen.
	 */
	public synchronized void signal(BusMessage oMessage) {

		// AppLogger.debug("Clu.signal - BusMessage type="+ oMessage.getId());

		ActionScreen oScreen = null;

		try {

			/* if the power is off then ingnore all other type except keyboard. */
			if (!_powerOn && !BusMessage.MSGTYPE_KEY.equals(oMessage.getType())) {
				return;
			}

			/* the source of the message is another client. */
			if (BusMessage.MSGTYPE_NETWORK.equals(oMessage.getType())) {

				Dscmessage oDscmessage = oMessage.getDscmessage();
				String callType = oDscmessage.getCallTypeCd();
				String toMMSI = oDscmessage.getRecipient();
				String myMMSI = _oContext.getApplicationContext()
						.getIndividualMmsi();

				/*
				 * first remove distress screen from stack if it's a distress
				 * ack and is for me.
				 */
				if (CALL_TYPE_DISTRESS_ACK.equals(callType)
						&& myMMSI.equals(toMMSI)) {
					popStackDistress(oMessage);
				} else {
					/* if waiting for a ack, then don't interrupt distress call */
					if (isDistressCallInprogress()) {
						return;
					}
				}
				_oContext.getContentManager().setIncomingDscmessage(oDscmessage);

				/* now show incoming ack */
				String strScreenName = _oContext.getContentManager()
						.getCallTypeMappingValue(oDscmessage.getCallTypeCd());
				ActionScreen oScreen1 = (ActionScreen) _oContext
						.getContentManager().getScreenContent(strScreenName,
								_oContext);
				oScreen1.setIncomingDscmessage(oDscmessage);

				// copy the incoming to the outcoming just to have some defaults
				Dscmessage oCopy = new Dscmessage(oDscmessage);
				oScreen1.setOutGoingDscmessage(oCopy);
				oScreen1.enter(oCopy);
				_oScreenStack.add(oScreen1);
				_oContext.getController().setScreenContent(oScreen1);
				return;
			}

			if (BusMessage.MSGTYPE_KEY.equals(oMessage.getType())
					&& DSC_RESET.equals(oMessage.getButtonEvent().getKeyId())) {
				resetScreenStack(oMessage);
				return;
			}

			// SOS pushed. Start distress call process.
			/* able to simplfy state machine. All states handle by following case.
			if (_powerOn && BusMessage.MSGTYPE_KEY.equals(oMessage.getType())
					&& FK_SOS.equals(oMessage.getButtonEvent().getKeyId())) {

				ActionScreen screen = (ActionScreen) _oScreenStack
						.get(_oScreenStack.size() - 1);

				if ((screen instanceof SendDistressScreen) == false) {
					screen = (ActionScreen) _oContext.getContentManager()
							.getScreenContent("distress_call_send", _oContext);
					screen.enter(null);
					_oScreenStack.add(screen);
					_oContext.getController().setScreenContent(screen);
				} else {
					if (screen.notify(oMessage) == null)
						resetScreenStack(oMessage);
				}
				return;
			}
			*/
			// not handled default to screen actions
			Object scr = _oScreenStack.get(_oScreenStack.size() - 1);
			ActionScreen oReturnScreen = null;
			if (scr instanceof ActionScreen) {
				ActionScreen active = (ActionScreen) scr;
				ActionMapping mapping = active.notify(oMessage);

				/*check global mappings*/
				if(mapping == null){
					String source = oMessage.getButtonEvent().getKeyId();
					String event = oMessage.getButtonEvent().getAction();
					mapping = active.findGlobalActionMapping(event, source);
				}
				
				ActionScreen next = null;
				if (mapping != null
						&& !NULL.equalsIgnoreCase(mapping.getForward())) {
					next = (ActionScreen) _oContext.getContentManager()
							.getScreenContent(mapping.getForward(), _oContext);

					int idxOf = indexOf(next);
					// instance of screen is already on stack backup to it.
					// therefore backup stack. no cylces in state diagram.
					if (idxOf > -1) {
						active.exit(oMessage);
						ActionScreen oTmp = null;
						for (int i = _oScreenStack.size() - 1; i >= idxOf; i--) {
							oTmp = (ActionScreen) _oScreenStack.get(i);
							// oTmp.exit(oMessage);
							_oScreenStack.remove(i);
						}
					} else {
						// moving down deeper in state diagram
						active.exit(oMessage);
						next.setIncomingDscmessage(active
								.getIncomingDscmessage());
						next.setOutGoingDscmessage(active
								.getOutGoingDscmessage());
					}

					next.enter(active.getIncomingDscmessage());
					_oScreenStack.add(next);
					_oContext.getController().setScreenContent(next);
				}

			} else {
				oScreen = (ActionScreen) scr;
				oReturnScreen = oScreen.signal(oMessage);
				if (oReturnScreen == null) {
					// send clr message and return
					// Bus.getInstance().publish(new BusMessage(null, FK_CLR));
					// resetScreenStack(oMessage);
					popStock(oMessage);
					return;

				} else if (oReturnScreen != oScreen) {

					int idxOf = indexOf(oReturnScreen);
					// instance of screen is already on stack backup to it.
					// therefore backup stack. no cylces in state diagram.
					if (idxOf > -1) {
						oScreen.exit(oMessage);
						ActionScreen oTmp = null;
						for (int i = _oScreenStack.size() - 1; i >= idxOf; i--) {
							oTmp = (ActionScreen) _oScreenStack.get(i);
							// oTmp.exit(oMessage);
							_oScreenStack.remove(i);
						}
					} else {
						// moving down deeper in state diagram
						oScreen.exit(oMessage);
						oReturnScreen.setIncomingDscmessage(oScreen
								.getIncomingDscmessage());
						oReturnScreen.setOutGoingDscmessage(oScreen
								.getOutGoingDscmessage());
					}

					oReturnScreen.enter(oScreen.getIncomingDscmessage());
					_oScreenStack.add(oReturnScreen);
					_oContext.getController().setScreenContent(oReturnScreen);

				}

			}

			if (BusMessage.MSGTYPE_KEY.equals(oMessage.getType())
					&& oMessage.getButtonEvent().isMasterSwitch()) {
				String keyId = oMessage.getButtonEvent().getKeyId();
				if (DSC_POWERED_OFF.equals(keyId)) {
					_powerOn = false;
				} else if (DSC_POWERED_ON.equals(keyId)) {
					_powerOn = true;
				}
			}

		} catch (Exception oEx) {
			AppLogger.error(oEx);
			_oContext.getController().setScreenContent(
					_oContext.getContentManager().getScreenContent(
							"system_error", _oContext));
		}

	}
}
