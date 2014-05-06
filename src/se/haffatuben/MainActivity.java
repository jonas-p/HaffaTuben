package se.haffatuben;

import java.util.ArrayList;
import java.util.Map;

import com.android.volley.toolbox.Volley;

import se.haffatuben.AddRouteDialogFragment.AddRouteResultReciever;
import se.haffatuben.Route.RouteLoadedReciever;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements AddRouteResultReciever {
	// ArrayList containing Route objects.
	ArrayList<Route> routes;
	ArrayList<RouteListItem> routeListItems;
	// DisplayRoutesFragment.
	DisplayRoutesFragment displayRoutesFragment;
	
	// RouteLoadedReciever.
	RouteLoadedReciever rc = new RouteLoadedReciever() {
		
		@Override
		public void onRouteLoaded(Route r) {
			// Notify fragment.
			displayRoutesFragment.notifyRoutesDataChanged();
		}
		
		@Override
		public void onRouteFailed(Error e) {
			// Toast.
			Toast toast = Toast.makeText(getApplicationContext(), "Resor kunde inte hämtas", Toast.LENGTH_SHORT);
			toast.show();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Initialize DisplayRoutesFragment.
		displayRoutesFragment = new DisplayRoutesFragment();

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, displayRoutesFragment).commit();
		}
		// Load Routes.
		RoutePreferences rp = new RoutePreferences(getApplicationContext());
		Map<String, ?> routeMap = rp.getRoutes();
		// Load Route objects.
		routeListItems = new ArrayList<RouteListItem>();
		for (Map.Entry<String, ?> entry : routeMap.entrySet()) {
			Route r = Route.create((String) entry.getValue());
			routeListItems.add(new RouteListItem(r));
		}
		// Send routes to view.
		displayRoutesFragment.setRoutes(routeListItems);
		// Load trips for all routes.
		boolean reverse = false; // TODO
		for (RouteListItem routeListItem : routeListItems) {
			routeListItem.route.loadTrips(Volley.newRequestQueue(this), reverse, rc);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * This method is called from the UI when the user clicks
	 * the add new route button and it launches the AddRoute fragment
	 * to allow the user to add a new route.
	 * 
	 * @param view The view that invoked the method
	 */
	public void addRoute(View view) {
		FragmentManager fm = getSupportFragmentManager();
		AddRouteDialogFragment addRouteDialog = new AddRouteDialogFragment();
		addRouteDialog.show(fm, "addroutedialog");
	}
	
	/**
	 * This method handles positive result from the addRouteDialogFragment.
	 */
	public void onAddRoutePositiveResult(Station a, Station b) {
		Log.d(getString(R.string.app_name), "Station A: " + a.name + " Station B: " + b.name);
		// RoutePreferences.
		RoutePreferences rp = new RoutePreferences(getApplicationContext());
		// Create Route object.
		Route r = new Route(a, b);
		// Serialize Route object.
		String routeString = r.serialize();
		// Put in SharedPreferences.
		rp.addRoute(r.id, routeString);
		// Append to route list.
		routeListItems.add(new RouteListItem(r));
		// Notify.
		displayRoutesFragment.notifyRoutesDataChanged();
		// Load trips.
		boolean reverse = false; // TODO
		r.loadTrips(Volley.newRequestQueue(this), reverse, rc);
	}
}
