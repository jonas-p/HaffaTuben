package se.haffatuben;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

/**
 * DialogFragment for adding routes to the application. The
 * user specifies Station A and Station B.
 * 
 * TODO: Return Station objects
 * TODO: Fix so no request is fired when user selects a site (Reason: the textview gets changed)
 * TODO: Implement postive button
 * 
 * @author jonas
 *
 */
public class AddRouteDialogFragment extends DialogFragment {
	public static final String REQUEST_TAG = "typeahead-request";
	private static final String TAG = "AppRouteDialogFragment";
	
	// Volley request queue
	private RequestQueue requestQueue;
	HashMap<String, String> sitesMap;
	ArrayList<String> sites;
	
	// Autocompletion adapter
	ArrayAdapter<String> adapter;
	
	public AddRouteDialogFragment() {
		sitesMap = new HashMap<String, String>();
		sites = new ArrayList<String>();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		requestQueue = Volley.newRequestQueue(getActivity());
		
		// Inflate view
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.fragment_addroutedialog, null);
		
		AutoCompleteTextView stationA = (AutoCompleteTextView) view.findViewById(R.id.station_a);
		AutoCompleteTextView stationB = (AutoCompleteTextView) view.findViewById(R.id.station_b);
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_dropdown_item_1line, sites);
		stationA.setAdapter(adapter);
		stationB.setAdapter(adapter);

		stationA.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO: Error checking if item does not exist
				String selected = adapter.getItem(position);
				Log.d(TAG, "Station A: Selected " + selected + "(" + sitesMap.get(selected) + ")");
			}
		});
		
		stationB.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO: Error checking if item does not exist
				String selected = adapter.getItem(position);
				Log.d(TAG, "Station B: Selected " + selected + "(" + sitesMap.get(selected) + ")");
			}
		});
		
		
		TimedTextWatcher watcher = new TimedTextWatcher(1000) {
			@Override
			public void exceededTimeSinceLastChange(CharSequence s) {
				updateSites(s.toString());
			}
		};
		
		stationA.addTextChangedListener(watcher);
		stationB.addTextChangedListener(watcher);
		
		// Create dialog
		Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.button_add_route);
		dialog.setView(view);
		dialog.setCancelable(true);
		
		// Cancel/OK buttons
		dialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO: Create route object and save to storage
						Log.d(TAG, AddRouteDialogFragment.class.getName() + ": Positive button not yet implemented!");
					}
				});
		dialog.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		
		return dialog.create();
	}
	
	/**
	 * This method requests sites from SL API using the query parameter and
	 * updates the Adapters connected to the AutoCompleteTextViews inside
	 * the Fragment.
	 * 
	 * @param query Site query parameter
	 */
	protected void updateSites(final String query) {
		// TODO: Move to config
		String url = "https://api.trafiklab.se/sl/realtid/GetSite.json?key=PFGie3SkJrJKTIRtg01wWAxABIRsfFYQ&stationSearch=" + query;
		
		// Cancel all previous requests
		requestQueue.cancelAll(REQUEST_TAG);
		
		if (query.length() == 0) return;
		JsonObjectRequest req = new JsonObjectRequest(url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						sitesMap.clear();
						adapter.clear();
						
						try {
							Object jsonSites = response.getJSONObject("Hafas")
									.getJSONObject("Sites").get("Site");
							
							if (jsonSites instanceof JSONObject) {
								// One site
								JSONObject jsonSite = (JSONObject) jsonSites;
								String name = jsonSite.getString("Name");
								String number = jsonSite.getString("Number");
								
								adapter.add(name);
								sitesMap.put(name, number);
							} else {
								// Multiple sites
								JSONArray jsonSitesArray = (JSONArray) jsonSites;
								for (int i = 0; i < jsonSitesArray.length(); i++) {
									JSONObject site = jsonSitesArray.getJSONObject(i);
									String name = site.getString("Name");
									String number = site.getString("Number");
									
									adapter.add(name);
									sitesMap.put(name, number);
								}
							}
						} catch (JSONException e) {
							// TODO: Error parsing
							Log.d(TAG, "Error parsing");
						}
						
						// Update view
						adapter.notifyDataSetChanged();
						adapter.getFilter().filter(query);
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO: Display error message
						Log.d(TAG, error.toString());
					}
				});
		
		req.setTag(REQUEST_TAG);
		requestQueue.add(req);
		Log.d(TAG, "Typeahead request sent");
	}
}
