package se.haffatuben;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

/**
 * Route Model.
 * A Route is represented by two Stations and an ArrayList of Trips.
 * 
 * @field Station a
 * @field Station b
 * @field ArrayList<Trip> trips
 */
public class Route {
	// Route id.
	public String id;
	// Stations a, b.
	public Station a, b;
	// ArrayList for Trip storage.
	public ArrayList<Trip> trips = new ArrayList<Trip>();
	
	/** Route.
	 * Route constructor.
	 * @param String id.
	 * @param Station a.
	 * @param Station b. 
	 */
	public Route(String id, Station a, Station b) {
		// Set fields.
		this.id = id;
		this.a = a;
		this.b = b;
	}
	
	/** buildRequestURL.
	 * Creates a SL API request URL.
	 * @param boolean reverse.
	 * @return URL String.
	 */
	private String buildRequestURL(boolean reverse) {
		// URL String.
		StringBuilder url = new StringBuilder();
		// Base URL.
		String base = "https://api.trafiklab.se/samtrafiken/resrobot/Search.json?";
		// URL Params.
		String apiKey = "key=Zi5RGn9ZKWfqNxCZxvGdjMz6dErIf3Dr";
		// If reverse, change a, b.
		String stations;
		if (reverse) {
			stations = "&fromId=" + b.id + "&toId=" + a.id;
		} else {
			stations = "&fromId=" + a.id + "&toId=" + b.id;
		}
		String options = "&searchType=T&coordSys=WGS84&apiVersion=2.1";
		// Build URL.
		url.append(base);
		url.append(apiKey);
		url.append(stations);
		url.append(options);
		return url.toString();
	}
	
	/**
	 * Load Trips from SL API.
	 * Calls TripLoadedReciever.onTripLoaded(this) on success.
	 * Calls TripLoadedReciever.onTripFailed(VolleyError error) on fail.
	 * @param Interface TripLoadedReciever.
	 * @param RequestQueue queue.
	 * @param Boolean reverse.
	 */
	public void loadTrips(RequestQueue queue, boolean reverse) {
		// Build request URL.
		String url = buildRequestURL(reverse);
		// Make request.
		JsonRequestISO jsObjReq = new JsonRequestISO(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {
					// On response from API.
					@Override
					public void onResponse(JSONObject response) {
						// Get Trips array.
						try {
							JSONArray tripArray = ((JSONObject) response.get("timetableresult")).getJSONArray("ttitem");
							// Iterate Trips and create Trip objects. Add objects to trips list.
							for (int i = 0; i < tripArray.length(); i++) {
								// If mode is Walk. Skip.
								String mode = (String) tripArray.getJSONObject(i).getJSONObject("segment").getJSONObject("segmentid").getJSONObject("mot").get("@displaytype");
								if (mode.equals("G")) {
									continue;
								}
								// New object.
								Trip t = new Trip((JSONObject) tripArray.get(i));
								// Append to trips.
								trips.add(t);
							}	
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					// On fail from API.
					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println(error.toString());
					}
				});
		queue.add(jsObjReq);
	}
	
	/**
	 * Serializes object to JSON format using GSON.
	 * @return Serialized String representation of object 
	 * to store in ShardPreferences.
	 */
	public String serialize() {
		// Create GSON object.
		Gson gson = new Gson();
		// Return string representation.
		return gson.toJson(this);
	}
	
	/**
	 * Static method.
	 * Creates a Route object from ShardPreferences serialized data.
	 * @param String of with serialized data.
	 * @return Route object.
	 */
	public static Route create(String serializedData) {
		// Create GSON object.
		Gson gson = new Gson();
		// Return Route object.
		return gson.fromJson(serializedData, Route.class);
	}
}
