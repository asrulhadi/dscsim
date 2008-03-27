/**
 * 
 */
package net.sourceforge.dscsim.radiotransport;

/**
 * Holder for a geographic position
 *
 */
public class GeoPosition {
	/**
	 * The scaling factor from the internal values to degrees
	 */
	private static final double SCALE = 1e6;
	
	/**
	 * The radius of the earth in metres
	 */
	private static final double EARTH_RADIUS = 6378137;
	
	/**
	 * Latitude in degrees (WG84) multiplied by 10<sup>6</sup>
	 */
	private int _latitude;

	/**
	 * @param Longitude in degrees (WG84) multiplied by 10<sup>6</sup>
	 */
	private int _longitude;

	/**
	 * Sets the geographical position of the antenna. The position is given in 
	 * WGS84 chart datum.
	 * @param latitude latitude in degrees (WGS84) multiplied by 10<sup>6</sup>
	 * @param longitude longitude in degrees (WGS84) multiplied by 10<sup>6</sup>
	 */
	public GeoPosition(int latitude, int longitude) {
		_latitude = latitude;
		_longitude = longitude;
	}
	
	/**
	 * Gets the latitude
	 * @return the latitude
	 */
	public int getLatitude() {
		return _latitude;
	}
	
	/**
	 * Gets the longitude
	 * @return the longitude
	 */
	public int getLongitude() {
		return _longitude;
	}
	
	/**
	 * Compute the distance between two positions
	 * @param p1 the first position
	 * @param p2 the second position
	 * @return the distance in meters between the two positions on the great circle
	 */
	public static int computeDistance(GeoPosition p1, GeoPosition p2) {
		double p1la = p1._latitude / SCALE / 180 * Math.PI;
		double p1lo = p1._longitude / SCALE / 180 * Math.PI;
		double p2la = p2._latitude / SCALE / 180 * Math.PI;
		double p2lo = p2._longitude / SCALE / 180 * Math.PI;
		double e = Math.acos( Math.sin(p1la)*Math.sin(p2la) + Math.cos(p1la)*Math.cos(p2la)*Math.cos(p2lo-p1lo) );
		double dist = e*EARTH_RADIUS;
		return Math.round((float)dist);
	}
}
