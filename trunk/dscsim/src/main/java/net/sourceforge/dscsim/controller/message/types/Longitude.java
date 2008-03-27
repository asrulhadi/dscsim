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

import java.text.MessageFormat;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import net.sourceforge.dscsim.controller.Constants;


/**
 * <p>Java class for Longitude complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Longitude">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="degrees">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="-180"/>
 *               &lt;maxInclusive value="180"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="minutes" type="{http://dscsim.sourceforge.net/controller/message/types}Minutes"/>
 *         &lt;element name="hemisphere">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="W"/>
 *               &lt;enumeration value="E"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Longitude", propOrder = {
	    "hemisphere"
})
public class Longitude 
	extends Coordinate implements Cloneable, Constants{

	@XmlType(name = "Hemisphere", namespace="Longitude", propOrder = {
			"hemisphere"
	})
    public enum Hemisphere {E, W, X};
	
    @XmlElement(required = true)
    protected Hemisphere hemisphere = Hemisphere.X;
    
    private static final String DEG_FORMAT = "{0,number,000}";
    
    public Longitude(){
    	
    }
    
    public Longitude(int degrees, int minutes, Longitude.Hemisphere hemisphere){
    	super(degrees, minutes);
    	this.hemisphere = hemisphere;
    }
    
    public Longitude(Longitude other){
    	this.degrees = other.degrees;
    	this.minutes = other.minutes;
    	this.hemisphere = other.hemisphere != null ? other.hemisphere : null;

    }
    /**
     * Gets the value of the hemisphere property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Hemisphere getHemisphere() {
        return hemisphere;
    }

    /**
     * Sets the value of the hemisphere property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHemisphere(Hemisphere hemisphere) {
    	if(Hemisphere.X == hemisphere){
    		setDegrees(0);
    		setMinutes(0);
    	}
        this.hemisphere = hemisphere;
    }   
    
    public void setHemisphere(String hemisphere){
    	this.setHemisphere(Hemisphere.valueOf(hemisphere));
    }
    
	public String getDegreesAsString() {
	    return super.getDegreesAsString(DEG_FORMAT);
	}
    
	public String getAsFromattedString(Properties props){
		if(this.isValid())
			return MessageFormat.format(props.getProperty(LON_FORMAT), new Object[]{this.getDegrees(),this.getMinutes(), this.getHemisphere()});
		else
			return props.getProperty(MS_LON_NON);
	}
    
	public boolean isValid() {
	
		if(Hemisphere.X == this.hemisphere)
			return false;
		
		int t = Math.abs(getDegrees()) * 10000
				+ getMinutes() *100;
		
		return (t >= 0 && t <= 1800000);
		
	}
	
    @Override
    public Longitude clone() {
    	return new Longitude(this);	
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + degrees;
		result = prime * result + minutes;
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
		final Longitude other = (Longitude) obj;
		if (degrees != other.degrees)
			return false;
		if (minutes != other.minutes)
			return false;
		return true;
	}
	public boolean hasValue(){
		return this.hemisphere != Hemisphere.X;
	}

}
