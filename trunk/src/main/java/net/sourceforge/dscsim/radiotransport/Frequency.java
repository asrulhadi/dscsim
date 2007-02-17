/**
 * 
 */
package net.sourceforge.dscsim.radiotransport;

/**
 * Represents the frequency of a radio transmission
 *
 */
public class Frequency {

	/**
	 * The frequency in Hz
	 */
	private int _frequency;
	
	/**
	 * Constructor which takes a given frequency value
	 * @param frequency the frequency in Hz
	 */
	public Frequency(int frequency) {
		_frequency = frequency;
	}
	
	/**
	 * Gets the frequency
	 * @return the frequency in Hz
	 */
	public int getFrequency() {
		return _frequency;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if( !(obj instanceof Frequency ) ) {
			return false;
		}
		if( _frequency == ((Frequency)obj)._frequency ) {
			return true;
		} else {
			return false;
		}
	}
	
}
