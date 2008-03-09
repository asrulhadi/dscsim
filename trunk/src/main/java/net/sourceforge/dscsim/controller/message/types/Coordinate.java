package net.sourceforge.dscsim.controller.message.types;

import java.text.MessageFormat;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Coordinate", propOrder = {
    "degrees",
    "minutes"
})
public abstract class Coordinate {

	protected int degrees;
	protected int minutes;
		
    private static final String MIN_FORMAT = "{0,number,00}";

	public Coordinate(){}

	public Coordinate(int degrees, int minutes){
		this.degrees = degrees;
		this.minutes = minutes;
	}

	public Coordinate(Coordinate other){
		this.degrees = other.degrees;
		this.minutes = other.minutes;
	}

	/**
	 * Gets the value of the degrees property.
	 * 
	 */
	public int getDegrees() {
	    return degrees;
	}

	public String getDegreesAsString(String format) {
		MessageFormat mf = new MessageFormat(format);
	    return mf.format(new Object[]{getDegrees()});
	}
	
	/**
	 * Sets the value of the degrees property.
	 * 
	 */
	public void setDegrees(int value) {
	    this.degrees = value;
	}
	
	public void setDegrees(String degrees) {
		if(degrees == null || degrees.length() < 1)
			this.setDegrees(0);
		else
		    this.setDegrees(Integer.valueOf(degrees));
			
	}
	/**
	 * Gets the value of the minutes property.
	 * 
	 */
	public int getMinutes() {
	    return minutes;
	}

	public String getMinutesAsString() {
		MessageFormat mf = new MessageFormat(MIN_FORMAT);
	    return mf.format(new Object[]{getMinutes()});
	}
	
	/**
	 * Sets the value of the minutes property.
	 * 
	 */
	public void setMinutes(int value) {
	    this.minutes = value;
	}

	public void setMinutes(String minutes) {
		if(minutes == null || minutes.length()<1)
			setMinutes(0);
		else
			setMinutes(Integer.valueOf(minutes));
	}
	

}
