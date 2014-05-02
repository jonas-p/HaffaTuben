package se.haffatuben;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
 * TODO: Fix so no request is fired when user selects a site (Reason: the textview gets changed)
 * 
 * @author jonas
 *
 */
public class AddRouteDialogFragment extends DialogFragment {
	public static final String REQUEST_TAG = "typeahead-request";
	private static final String TAG = "AppRouteDialogFragment";
	
	// Volley request queue
	protected RequestQueue requestQueue;
	
	// Sites
	protected HashMap<String, JSONObject> sitesMap;
	protected ArrayList<String> sites;
	protected Station stationA;
	protected Station stationB;
	
	// Autocompletion adapter
	protected ArrayAdapter<String> adapter;
	
	public AddRouteDialogFragment() {
		sitesMap = new HashMap<String, JSONObject>();
		sites = new ArrayList<String>();
	}
	
	/**
	 * Interface to send the result from AddRouteDialogFragment
	 * 
	 * @author jonas
	 *
	 */
	public interface AddRouteResultReciever {
		/**
		 * Called on positive result from AddRouteDialogFragment
		 * @param a Station A
		 * @param b Station B
		 */
		public void onAddRoutePositiveResult(Station a, Station b);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		requestQueue = Volley.newRequestQueue(getActivity());
		
		// Inflate view
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.fragment_addroutedialog, null);
		
		AutoCompleteTextView stationA_ac = (AutoCompleteTextView) view.findViewById(R.id.station_a);
		AutoCompleteTextView stationB_ac = (AutoCompleteTextView) view.findViewById(R.id.station_b);
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_dropdown_item_1line, sites);
		stationA_ac.setAdapter(adapter);
		stationB_ac.setAdapter(adapter);

		stationA_ac.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO: Error checking if item does not exist
				String selected = adapter.getItem(position);
				
				stationA = new Station(sitesMap.get(selected));
				// TODO: Check if should enable OK button
			}
		});
		
		stationB_ac.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO: Error checking if item does not exist
				String selected = adapter.getItem(position);
				
				stationB = new Station(sitesMap.get(selected));
				// TODO: Check if should enable OK button
			}
		});
		
		
		TimedTextWatcher watcher = new TimedTextWatcher(1000) {
			@Override
			public void exceededTimeSinceLastChange(CharSequence s) {
				updateSites(s.toString());
			}
		};
		
		stationA_ac.addTextChangedListener(watcher);
		stationB_ac.addTextChangedListener(watcher);
		
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
						// Send result to activity
						if (getActivity() instanceof AddRouteResultReciever) {
							((AddRouteResultReciever) getActivity()).onAddRoutePositiveResult(stationA, stationB);
						}
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
		String url = "https://api.trafiklab.se/samtrafiken/resrobot/FindLocation.json?key=94TSOTStVjWwbNWMnYFSGGzRGbA3nlGq&coordSys=WGS84&apiVersion=2.1&from=" + query;
		
		// Cancel all previous requests
		requestQueue.cancelAll(REQUEST_TAG);
		
		if (query.length() == 0) return;

		JsonRequestISO req = new JsonRequestISO(Method.GET, url, null,
				new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				sitesMap.clear();
				adapter.clear();
				
				try {
					JSONObject jsonSites = response.getJSONObject("findlocationresult")
							.getJSONObject("from");
					
					if (jsonSites.has("location")) {
						JSONArray sites = jsonSites.getJSONArray("location");
						for (int i = 0; i < sites.length(); i++) {
							JSONObject site = sites.getJSONObject(i);
							String name = site.getString("displayname");
							
							adapter.add(name);
							sitesMap.put(name, site);
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
