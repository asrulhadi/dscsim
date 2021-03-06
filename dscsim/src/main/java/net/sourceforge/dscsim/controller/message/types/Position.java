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
 * the Initial Developer are Copyright (C) 2008, 2009.
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import net.sourceforge.dscsim.controller.utils.AppLogger;


/**
 * <p>Java class for Position complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Position">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="latitude" type="{http://dscsim.sourceforge.net/controller/message/types}Latitude"/>
 *         &lt;element name="longitude" type="{http://dscsim.sourceforge.net/controller/message/types}Longitude"/>
 *         &lt;element name="time" type="{http://dscsim.sourceforge.net/controller/message/types}Time"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Position", propOrder = {
    "latitude",
    "longitude",
    "time"
})
public class Position 
	implements Cloneable, CompositeUserType
{

    @XmlElement(required = true)
    protected Latitude latitude = null;
    @XmlElement(required = true)
    protected Longitude longitude = null;
    @XmlElement(required = true)
    protected net.sourceforge.dscsim.controller.message.types.Time time = null;

    public Position(){
    	this.latitude = new Latitude();
    	this.longitude = new Longitude();
    	this.time = new Time();
    }
    
    public Position(Latitude latitude, Longitude longitude, Time time){
    	this.latitude = latitude;
    	this.longitude = longitude;
    	this.time = time;
    }

    public Position(Position other){   
		this.latitude = other.latitude != null ? other.latitude.clone() : null;
		this.longitude = other.longitude != null ? other.longitude.clone() : null;
		this.time = other.time != null ? other.time.clone() : null;
    }
    
    /**
     * Gets the value of the latitude property.
     * 
     * @return
     *     possible object is
     *     {@link Latitude }
     *     
     */
    public Latitude getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link Latitude }
     *     
     */
    public void setLatitude(Latitude value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the longitude property.
     * 
     * @return
     *     possible object is
     *     {@link Longitude }
     *     
     */
    public Longitude getLongitude() {
        return longitude;
    }
    public int getLatitudeDegrees(){
    	return this.getLatitude().getDegrees();
    }

    public void setLatitudeDegrees(int degrees){
    	this.getLatitude().setDegrees(degrees);
    }

    public int getLatitudeMinutes(){
    	return this.getLatitude().getMinutes();
    }

    public void setLatitudeMinutes(int minutes){
    	this.getLatitude().setMinutes(minutes);
    }
    
    public Latitude.Hemisphere getLatitudeHemisphere(){
    	return this.getLatitude().getHemisphere();
    }

    public void setLatitudeHemisphere(Latitude.Hemisphere hemisphere){
    	this.getLatitude().setHemisphere(hemisphere);
    }
 
    public Longitude.Hemisphere getLongitudeHemisphere(){
    	return this.getLongitude().getHemisphere();
    }

    public void setLongitudeHemisphere(Longitude.Hemisphere hemisphere){
    	this.getLongitude().setHemisphere(hemisphere);
    }
    
    /**
     * Sets the value of the longitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link Longitude }
     *     
     */
    public void setLongitude(Longitude value) {
        this.longitude = value;
    }
    
    public int getLongitudeDegrees(){
    	return this.getLongitude().getDegrees();
    }

    public void setLongitudeDegrees(int degrees){
    	this.getLongitude().setDegrees(degrees);
    }

    public int getLongitudeMinutes(){
    	return this.getLongitude().getMinutes();
    }

    public void setLongitudeMinutes(int minutes){
    	this.getLongitude().setMinutes(minutes);
    }

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link Time }
     *     
     */
    public Time getTime() {
        return time;
    }
    
    public int getTimeHours(){
    	return this.getTime().getHours();
    }
    
    public int getTimeMinutes(){
    	return this.getTime().getMinutes();
    }
    public void setTimeHours(int hours){
    	this.getTime().setHours(hours);
    }
    
    public void setTimeMinutes(int minutes){
    	this.getTime().setMinutes(minutes);
    }
    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link Time }
     *     
     */
    public void setTime(Time value) {
        this.time = value;
    }

	public Object assemble(Serializable cached, SessionImplementor session,
			Object owner) throws HibernateException {
		
		if(1==1)
			throw new RuntimeException("Not implemented");

		return null;
	}

	public Object deepCopy(Object value) throws HibernateException {
		return ((Position)value).clone();
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

	static final String propertySetter [] = {"setLatitudeDegrees", "setLatitudeMinutes", "setLatitudeHemisphere", "setLongitudeDegrees", "setLongitudeMinutes", "setLongitudeHemisphere", "setTimeHours", "setTimeMinutes"};
	static final String propertyGetter [] = {"getLatitudeDegrees", "getLatitudeMinutes", "getLatitudeHemisphere", "getLongitudeDegrees", "getLongitudeMinutes", "getLatitudeHemisphere", "getTimeHours", "getTimeMinutes"};
	static final String propertyNames [] = {"latitudeDegrees", "latitudeMinutes", "latitudeHemisphere", "longitudeDegrees", "longitudeMinutes", "longitudeHemisphere", "timeHours", "timeMinutes"};
	public String[] getPropertyNames() {
		return propertyNames;
	}

	static final Type propertyTypes[]= {Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.STRING, Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.STRING, Hibernate.INTEGER, Hibernate.INTEGER};
	public Type[] getPropertyTypes() {
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
		return true;
	}

	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		
		int idx = 0;
		this.setLatitudeDegrees(rs.getInt(names[idx++]));
		this.setLatitudeMinutes(rs.getInt(names[idx++]));
		this.setLatitudeHemisphere(Latitude.Hemisphere.valueOf(rs.getString(names[idx++])));
		this.setLongitudeDegrees(rs.getInt(names[idx++]));
		this.setLongitudeMinutes(rs.getInt(names[idx++]));
		this.setLongitudeHemisphere(Longitude.Hemisphere.valueOf(rs.getString(names[idx++])));
		this.setTimeHours(rs.getInt(names[idx++]));
		this.setTimeMinutes(rs.getInt(names[idx++]));
	
		return this.clone();
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		
		Position pos = (Position)value;
		Hibernate.INTEGER.nullSafeSet(st, pos.getLatitude().getDegrees(), index++);
		Hibernate.INTEGER.nullSafeSet(st, pos.getLatitude().getMinutes(), index++);
		Hibernate.STRING.nullSafeSet(st, pos.getLatitude().getHemisphere().name(), index++);
		Hibernate.INTEGER.nullSafeSet(st, pos.getLongitude().getDegrees(), index++);
		Hibernate.INTEGER.nullSafeSet(st, pos.getLongitude().getMinutes(), index++);
		Hibernate.STRING.nullSafeSet(st, pos.getLongitude().getHemisphere().name(), index++);
		Hibernate.INTEGER.nullSafeSet(st, pos.getTime().getHours(), index++);
		Hibernate.INTEGER.nullSafeSet(st, pos.getTime().getMinutes(), index++);
	
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
	
	@Override
    public Position clone(){
    	return new Position(this);	
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result
				+ ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		final Position other = (Position) obj;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

}
