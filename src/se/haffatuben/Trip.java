package se.haffatuben;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Trip Model.
 * A Trip is represented by departure time, travel type and travel line.
 * 
 * @field Type type
 * @field Date departure
 * @field String lineNumber
 * @field String lineString
 */
public class Trip {
	/**
	 * Travel Type.
	 * Represents different travel types.
	 */
	public enum Type {
		BUS, METRO, COMMUTE, LOCAL, BOAT, FERRY;
	}

	// Type of Trip. Metro, Bus etc.
	public Type type;
	// Departure time.
	public Date departure;
	// Line number.
	public String lineNumber;
	// Line string.
	public String lineString;
	
	/**
	 * Trip Constructor.
	 * @param JSONObject with Trip info.
	 */
	public Trip(JSONObject trip) {
		// Parse Trip data and populate fields.
		parseTrip(trip);
	}
	
	/**
	 * Parses trip data and populates fields.
	 * @param JSONObject with Trip info.
	 */
	private void parseTrip(JSONObject trip) {
		// Get SubTrip.
		JSONObject subTrip = null;
		try {
			subTrip = (JSONObject) trip.getJSONArray("SubTrip").get(0);
			// Get departure date.
			String departureDate = subTrip.get("DepartureDate").toString();
			// Get departure time.
			String departureTime = ((JSONObject) subTrip.get("DepartureTime")).get("#text").toString();
			// Parse date details.
			String[] date = departureDate.split("\\.");
			// Parse time details.
			String[] time = departureTime.split(":");
			// Create Calendar object.
			Calendar cal = Calendar.getInstance();
			// Set date and time.
			cal.set(Calendar.YEAR, Integer.parseInt(date[2]));
			cal.set(Calendar.MONTH, Integer.parseInt(date[1]));
			cal.set(Calendar.DATE, Integer.parseInt(date[0]));
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
			cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
			// Set date.
			departure = cal.getTime();
			// Get Transport type.
			String typeString = ((JSONObject) subTrip.get("Transport")).get("Type").toString();
			// Set Type enum.
			if (typeString.equals("BUS")) { type = Type.BUS; }
			else if (typeString.equals("MET")) {type = Type.METRO; }
			else if (typeString.equals("TRN")) {type = Type.COMMUTE; }
			else if (typeString.equals("TRM")) {type = Type.LOCAL; }
			else if (typeString.equals("SHP")) {type = Type.BOAT; }
			else if (typeString.equals("FER")) {type = Type.FERRY; }
			// Set Line string.
			lineString = ((JSONObject) subTrip.get("Transport")).get("Name").toString();
			// Get line number.
			lineNumber = ((JSONObject) subTrip.get("Transport")).get("Line").toString();
			//System.out.printf("Departure: %s; Line: %s %s; Type: %s\n", departure, lineString, lineNumber, type);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
