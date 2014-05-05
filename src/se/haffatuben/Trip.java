package se.haffatuben;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
		BUS, METRO, COMMUTE, FERRY;
	}

	// Type of Trip. Metro, Bus etc.
	public Type type;
	// Departure time.
	public Date departure;
	// Line number.
	public String lineNumber;
	
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
		try {
			// Get departure date.
			String departureString = (String) trip.getJSONObject("segment").getJSONObject("departure").get("datetime");
			// Format and create date object.
			departure = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(departureString);
			// Get transport type.
			String typeString = (String) trip.getJSONObject("segment").getJSONObject("segmentid").getJSONObject("mot").get("@displaytype");
			// Set Type enum.
			if (typeString.equals("B")) { type = Type.BUS; }
			else if (typeString.equals("U")) {type = Type.METRO; }
			else if (typeString.equals("J")) {type = Type.COMMUTE; }
			else if (typeString.equals("F")) {type = Type.FERRY; }
			// Set line number.
			lineNumber = trip.getJSONObject("segment").getJSONObject("segmentid").getJSONObject("carrier").get("number").toString();
			//System.out.printf("Departure: %s; Line: %s; Type: %s\n", departure, lineNumber, type);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
