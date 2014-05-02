package se.haffatuben;

import org.json.JSONException;
import org.json.JSONObject;

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
	// Station
	public String name;
	// SL ID.
	public String id;
	// Latitude, Longitude coordinates.
	public double lat, lng;
	
	/** Station Constructor.
	 * @param String name.
	 * @param String id.
	 * @param double lat.
	 * @param double lng.
	 */
	public Station(String name, String id, double lat, double lng) {
		// Set fields.
		this.name = name;
		this.id = id;
		this.lat = lat;
		this.lng = lng;
	}
	
	/**
	 * Create a Station object from JSON (Resrobot API)
	 * 
	 * @param jsonObject json
	 */
	public Station(JSONObject jsonObject) {
		try {
			this.name = jsonObject.getString("displayname");
			this.id = jsonObject.getString("locationid");
			this.lat = jsonObject.getDouble("@y");
			this.lng = jsonObject.getDouble("@x");
		} catch (JSONException e) {
			// TODO: Implement error handling
		}
	}
}