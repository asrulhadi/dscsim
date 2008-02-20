package net.sourceforge.dscsim.controller.message.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.type.NullableType;


public class AddressIdEntryType extends PersistentStringEnum{
	
	public static final String STR_IN = "IN";
	public static final String STR_GR = "GR";
	
	public static final AddressIdEntryType IN = new AddressIdEntryType("Individual", STR_IN);
	public static final AddressIdEntryType GR = new AddressIdEntryType("Group", STR_GR);

	private AddressIdEntryType actual = null;

	/**
	 * for hibernate.
	 */
	public AddressIdEntryType(){
		this(IN);
	}

	/*
	 * should remain private
	 */
	private AddressIdEntryType(AddressIdEntryType other){
		this.actual = other.actual;
	}
	public AddressIdEntryType(String name, String value){
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
