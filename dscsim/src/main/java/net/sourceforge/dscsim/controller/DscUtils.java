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

import java.util.SortedSet;
import java.util.TreeSet;

public class DscUtils implements Constants {

	public static int getKeyIntValue(String keyID) {

		return Integer.parseInt(keyID.substring(3, keyID.length()));

	}

	public static String getKeyStringValue(String keyID) {

		return keyID.substring(3, keyID.length());
	}

	public static SortedSet createKeySet() {

		SortedSet oSet = new TreeSet();

		oSet.add(KP_0);
		oSet.add(KP_1);
		oSet.add(KP_2);
		oSet.add(KP_3);
		oSet.add(KP_4);
		oSet.add(KP_5);
		oSet.add(KP_6);
		oSet.add(KP_7);
		oSet.add(KP_8);
		oSet.add(KP_9);

		return oSet;
	}
}
