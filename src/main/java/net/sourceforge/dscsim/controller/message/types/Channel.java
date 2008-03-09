package net.sourceforge.dscsim.controller.message.types;

import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Channel", propOrder = {
    "channel"
})

public class Channel 
	implements Cloneable {
	
	private int channel = -1;

	public Channel(){
	}
	public Channel(int channel){
		this.channel = channel;
	}

	public Channel(Channel other){
		this.channel = other.channel;
	}
	
	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	public Object clone(){
		return new Channel(this);
	}

	public String toString(){	
	    return MessageFormat.format("{0, number,00}", new Object[]{this.channel});
	}
	
	public static boolean isValid(int channel){		
		if(channel == -1)
			return false;
		else
			return true;		
	}
	
	public static Channel valueOf(int channel){
		return new Channel(channel);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + channel;
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Channel))
			return false;
		final Channel other = (Channel) obj;
		if (channel != other.channel)
			return false;
		return true;
	}
}
