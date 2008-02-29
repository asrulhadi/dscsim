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
package net.sourceforge.dscsim.controller.message.types;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.Transient;

import net.sourceforge.dscsim.controller.utils.AppLogger;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;


/**
 * Class used for storing address information in controllers addressbook.
 * 
 * @author Pennoyer
 *
 */
public class AddressIdEntry 
	implements Serializable, Cloneable {

	/**
	 * Storage field for MMSI.
	 */
	private String id = null;
	
	/**
	 * Name associated with MMSI.
	 */
	private String name = null;
	

	/**
	 * Type Code (IN=INDIVIDUAL, GR=GROUP)
	 */
	private AddressIdEntryType type = AddressIdEntryType.IN;
	
	/**
	 * Constructor for creating empty address.
	 *
	 */
	public AddressIdEntry(){
		this("", "");
	}
	
	/**
	 * Constructor for creating instance with an MMSI and name.
	 * @param id is normally the MMSI.
	 * @param name is the description for the MMSI.
	 */
	public AddressIdEntry(String id, String name){
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Constructor for creating instance with an MMSI and name.
	 * @param id is normally the MMSI.
	 * @param name is the description for the MMSI.
	 */
	public AddressIdEntry(String id, String name, AddressIdEntryType type){
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	/**
	 * copy ctor.
	 */
	public AddressIdEntry(AddressIdEntry other){
		this.id = other.id != null ? new String(other.id) : null;
		this.name = other.name != null ? new String(other.name) : null;
		this.type = other.type;
		
	}
	
	@Override
    public AddressIdEntry clone(){
    	return new AddressIdEntry(this);	
    }
	/**
	 * set the MMSI.
	 * @param id
	 */
	public void setId(String id){
		this.id = id;
	}
	
	/**
	 * get the MMSI.
	 * @return MMSI as String.
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * Set name of address.
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Get name associated with address.
	 * @return Name of address.
	 */
	public String getName(){
		return name;
	}
	
	@Transient()
	public AddressIdEntryType getType() {
		return type;
	}

	public void setType(AddressIdEntryType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AddressIdEntry other = (AddressIdEntry) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	/*
	public Object assemble(Serializable cached, SessionImplementor session,
			Object owner) throws HibernateException {
		if(1==1)
			throw new RuntimeException("Not implemented");
		return null;
	}

	public Object deepCopy(Object value) throws HibernateException {
		return null;
	}

	public Serializable disassemble(Object value, SessionImplementor session)
			throws HibernateException {
		if(1==1)
			throw new RuntimeException("Not implemented");
		return null;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		return x.equals(y);
	}

	static final String propertySetter [] = {"setId", "setName", "setType"};
	static final String propertyGetter [] = {"getId", "getName", "getType"};
	static final String propertyNames [] = {"id", "name", "type"};
	public String[] getPropertyNames() {
		return propertyNames;
	}

	static final Type propertyTypes[]= {Hibernate.STRING, Hibernate.STRING, Hibernate.STRING};
	public org.hibernate.type.Type[] getPropertyTypes() {
		return propertyTypes;
	}

	public Object getPropertyValue(Object component, int property)
			throws HibernateException {
		Object rv = null;
		try {
			Method m = this.getClass().getMethod(propertyGetter[property], new Class[]{});			
			rv = m.invoke(component, new Object[]{});		
		} catch (Exception e) {
			AppLogger.error(e);
		} 
		
		return rv;
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		int idx = 0;
		this.setId(rs.getString(names[idx++]));
		this.setName(rs.getString(names[idx++]));
		this.setType(AddressType.valueOf(rs.getString(names[idx++])));
	
		return this.clone();
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		AddressIdEntry entry = (AddressIdEntry)value;
		Hibernate.STRING.nullSafeSet(st, entry.getId(), index++);
		Hibernate.STRING.nullSafeSet(st, entry.getName(), index++);
		Hibernate.STRING.nullSafeSet(st, entry.getType().name(), index++);
	}

	public Object replace(Object original, Object target,
			SessionImplementor session, Object owner) throws HibernateException {
		
		if(1==1)
			throw new RuntimeException("Not implemented");

		return null;
	}

	public Class returnedClass() {
		return this.getClass();
	}

	public void setPropertyValue(Object component, int property, Object value)
			throws HibernateException {

		try {
			Method m = this.getClass().getMethod(propertySetter[property], new Class[]{value.getClass()});			
			m.invoke(component, new Object[]{value});		
		} catch (Exception e) {
			AppLogger.error(e);
		} 
		
		return;		
		
	}
	*/
}
