package se.haffatuben;

/**
 * Station model.
 * A station has the same ID as station in SL API.
 * Location is represented by latitude and longitude doubles.
 * 
 * @field String id
 * @field double lat
 * @field double lng
 */
public class Station {
	// SL ID.
	public String id;
	// Latitude, Longitude coordinates.
	public double lat, lng;
	
	/** Station Constructor.
	 * @param String id.
	 * @param double lat.
	 * @param double lng.
	 */
	public Station(String id, double lat, double lng) {
		// Set fields.
		this.id = id;
		this.lat = lat;
		this.lng = lng;
	}
}