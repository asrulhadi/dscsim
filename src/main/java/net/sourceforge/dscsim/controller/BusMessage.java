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

import java.io.Serializable;

import net.sourceforge.dscsim.controller.message.types.Dscmessage;

/**
 * The BusMessage is used for communication between the controller's subcomponents.
 * @author katharina
 *
 */

public class BusMessage implements Serializable, Constants {

	/**
	 * The message contains a message, which originated from the network.
	 */
	public static final String MSGTYPE_NETWORK = "NETWORK_MSG";

	/**
	 * The message is an internal message such as a key stroke.
	 */
	public static final String MSGTYPE_KEY = "KEY";

	/**
	 * Mesage Type is either MSGTYPE_NETWORK or MSGTYPE_KEY.
	 */
	private String _msgType = "N/A";

	/**
	 * Memory address of sender.
	 */
	private Object _from = null;
	/**
	 * Information accompanying this message.
	 */
	private Object _content = null;

	/**
	 * Constructor.
	 * @param from is the memory address of the sender.
	 * @param oDscMessage is the message specific information.
	 */
	public BusMessage(Object from, Dscmessage oDscMessage) {
		_from = from;
		_content = oDscMessage;
		_msgType = MSGTYPE_NETWORK;

	}

	/**
	 * Constructor for instances containing button events.
	 * @param from is the Memory address of the sender.
	 * @param oButton is the button that was pushed.
	 */
	public BusMessage(Object from, Button oButton) {
		_from = from;
		_content = oButton;
		_msgType = MSGTYPE_KEY;
	}

	/**
	 * The sender of the message.
	 * @return Object.
	 */
	public Object getFrom() {
		return _from;
	}

	/**
	 * Get message specific information.
	 * @return DscMessage.
	 */
	public Dscmessage getDscmessage() {
		return (Dscmessage) _content;
	}

	/**
	 * If the message type is of type MSGTYPE_KEY, then return the button.
	 * @return Button.
	 */
	public Button getButtonEvent() {
		return (Button) _content;
	}

	/**
	 * Constructor passing in parameters for all member variables.
	 * @param from is the memory address of the sender.
	 * @param msgType one of the two constants.
	 * @param oContent is the message specific information.
	 */
	public BusMessage(Object from, String msgType, Object oContent) {
		_from = from;
		_msgType = msgType;
		_content = oContent;
	}

	/**
	 * Get the type of the message.
	 * @return String.
	 */
	public String getType() {
		return _msgType;
	}

	/**
	 * Get the special information related to the message.
	 * 
	 * @return
	 */
	public Object getContent() {
		return _content;
	}

	/**
	 * Convert message to a string.
	 * @return String.
	 */
	public String toString() {

		String strFrom = _from != null ? _from.getClass().getName() : null;

		return "[" + getType() + ":" + strFrom + ":"
				+ _content.getClass().getName() + "]";

	}

}
