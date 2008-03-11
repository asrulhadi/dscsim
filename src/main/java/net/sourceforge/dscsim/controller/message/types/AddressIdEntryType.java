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
 * the Initial Developer are Copyright (C) 2006, 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */

package net.sourceforge.dscsim.controller.message.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.type.NullableType;

public class AddressIdEntryType extends PersistentStringEnum {

	public static final String STR_IN = "IN";
	public static final String STR_GR = "GR";

	public static final AddressIdEntryType IN = new AddressIdEntryType(
			"Individual", STR_IN);
	public static final AddressIdEntryType GR = new AddressIdEntryType("Group",
			STR_GR);

	private AddressIdEntryType actual = null;

	/**
	 * for hibernate.
	 */
	public AddressIdEntryType() {
		this(IN);
	}

	/*
	 * should remain private
	 */
	private AddressIdEntryType(AddressIdEntryType other) {
		this.actual = other.actual;
	}

	public AddressIdEntryType(String name, String value) {
		super(name, value);
	}

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return null;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return null;
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return null;
	}

}
