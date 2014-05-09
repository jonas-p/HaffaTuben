package se.haffatuben;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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
	
	// Database
	protected StationsAdapter stations;
	protected FetchStationsAsync fetchTask;
	
	// Sites
	protected HashMap<String, Station> sitesMap;
	protected ArrayList<String> sites;
	protected Station stationA;
	protected Station stationB;
	
	// Autocompletion adapter
	protected ArrayAdapter<String> adapter;
	
	public AddRouteDialogFragment() {
		sitesMap = new HashMap<String, Station>();
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
		stations = new StationsAdapter(getActivity());
		
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
				stationA = sitesMap.get(selected);
				togglePositiveButton();
			}
		});
		
		stationB_ac.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO: Error checking if item does not exist
				String selected = adapter.getItem(position);
				stationB = sitesMap.get(selected);
				togglePositiveButton();
			}
		});
		
		/**
		 * TextWatcher to help listen on changes on the TextViews
		 * so we can fetch suggestions from the stations database.
		 */
		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (fetchTask != null) {
					fetchTask.cancel(true);
				}
				fetchTask = new FetchStationsAsync(s.toString());
				fetchTask.execute();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		};
		
		// Add listeners
		stationA_ac.addTextChangedListener(textWatcher);
		stationB_ac.addTextChangedListener(textWatcher);
		
		// Create dialog
		Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		dialogBuilder.setTitle(R.string.button_add_route);
		dialogBuilder.setView(view);
		dialogBuilder.setCancelable(true);
		
		// Cancel/OK buttons
		dialogBuilder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Send result to activity
						if (getActivity() instanceof AddRouteResultReciever) {
							((AddRouteResultReciever) getActivity()).onAddRoutePositiveResult(stationA, stationB);
						}
					}
				});
		dialogBuilder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		
		// Create dialog and disable positive button		
		return dialogBuilder.create();
	}
	
	/**
	 * Dialog has been created, disable positive button.
	 */
	@Override
	public void onStart() {
		super.onStart();
		togglePositiveButton();
	}
	
	/**
	 * Toggles positive button on the condition that if
	 * both stationA and stationB are set the button is enabled
	 * otherwise it's disabled.
	 */
	private void togglePositiveButton() {
		AlertDialog dialog = (AlertDialog) this.getDialog();
		Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		
		if (stationA == null || stationB == null) {
			button.setEnabled(false);
		} else {
			button.setEnabled(true);
		}
	}
	
	/**
	 * FetchStationsAsync fetches stations from the SQLite database
	 * asynchronously. When done the sitesMap and adapter is updated
	 * and notified so the AutoCompleteTextViews can display suggestions.
	 * @author jonas
	 *
	 */
	private class FetchStationsAsync extends AsyncTask<Void, Void, Void> {
		private String query;
		private List<Station> result;
		
		/**
		 * Initialize a new object.
		 * @param query Search query
		 */
		public FetchStationsAsync(String query) {
			this.query = query;
		}

		/**
		 * Fetch stations from the database
		 */
		@Override
		protected Void doInBackground(Void... params) {
			result = stations.getStations(query);
			return null;
		}
		
		/**
		 * Clear adapter and add the new matches and notify
		 * that the data has changed.
		 */
		@Override
		protected void onPostExecute(Void v) {
			adapter.clear();
			for (Station s : result) {
				adapter.add(s.name);
				sitesMap.put(s.name, s);
			}
			adapter.notifyDataSetChanged();
			adapter.getFilter().filter(query);
		}
		
	}
}
