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
import javax.xml.bind.annotation.XmlType;

import net.sourceforge.dscsim.controller.Constants;


/**
 * <p>Java class for Time complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Time">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hours">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="24"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="minutes" type="{http://dscsim.sourceforge.net/controller/message/types}Minutes"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Time", propOrder = {
    "hours",
    "minutes"
})
public class Time 
	implements Cloneable, Constants {

	/*valid is 0 to 23*/
    protected int hours = -1;
    protected int minutes;

    private static final String PARTS_FORMAT = "{0,number,00}";

    public Time(){
    	
    }
 
    public Time(int hours, int minutes){
    	this.hours = hours;
    	this.minutes = minutes;
    }
    public Time(Time other){
    	this.hours = other.hours;
    	this.minutes = other.minutes;
    }

    /**
     * Gets the value of the hours property.
     * 
     */
    public int getHours() {
        return hours;
    }

    public String hoursAsString() {
		if(this.hasValue())
			return MessageFormat.format(PARTS_FORMAT, new Object[]{this.getHours()});
		else
			return "";   
    }
    /**
     * Sets the value of the hours property.
     * 
     */
    public void setHours(int value) {
        this.hours = value;
    }

    public void setHours(String  hours) {
    	if(hours == null || hours.length()<1)
    		setHours(0);
    	else
    		setHours(Integer.valueOf(hours));
    }
    /**
     * Gets the value of the minutes property.
     * 
     */
    public int getMinutes() {
        return minutes;
    }

    public String minutesAsString() { 	
		if(this.hasValue())
			return MessageFormat.format(PARTS_FORMAT, new Object[]{this.getMinutes()});
		else
			return "";       
    }
    /**
     * Sets the value of the minutes property.
     * 
     */
    public void setMinutes(int value) {
        this.minutes = value;
    }
    
    public void setMinutes(String  minutes) {
    	if(minutes == null || minutes.length()<1)
    		setMinutes(0);
    	else
    		setMinutes(Integer.valueOf(minutes));
    
    }
    
    
    public String getAsFormattedString2(Properties props){
		if(this.hasValue())
			return MessageFormat.format(props.getProperty(TIME_FORMAT), new Object[]{this.getHours(),this.getMinutes()});
		else
			return props.getProperty(EMPTY_TIME);
    }
    
    public String getAsFormattedString(Properties props){
		if(this.hasValue())
			return MessageFormat.format(props.getProperty(TIME_FORMAT), new Object[]{this.getHours(),this.getMinutes()});
		else
			return props.getProperty(MS_TIME_NON);
    }
    
	public boolean isValid() {
		return (this.hours<0 ? false : true);
	}
	
	public void inValidate() {
		hours = -1;
	}
	
	@Override
    public Time clone() {
    	return new Time(this);	
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hours;
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
		final Time other = (Time) obj;
		if (hours != other.hours)
			return false;
		if (minutes != other.minutes)
			return false;
		return true;
	}

	public boolean hasValue(){
		return hours > -1;
	}

}
