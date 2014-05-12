package se.haffatuben;

import java.util.ArrayList;
import java.util.Map;

import com.android.volley.toolbox.Volley;

import se.haffatuben.AddRouteDialogFragment.AddRouteResultReciever;
import se.haffatuben.Route.RouteLoadedReciever;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements AddRouteResultReciever, LocationListener {
	// Location
	Location location;
	LocationManager locationManager;
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
			Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.could_not_fetch_trips), Toast.LENGTH_SHORT);
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
		
		// Add trips to views
		addAllTrips();

		// Location
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if (!isGPSEnabled && !isNetworkEnabled) {
			// Can't get location, use regular routes
			loadAllTrips();
		} else {
			if (isGPSEnabled) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						60000, // Every one minute
						10,	   // or every ten meters
						this);
				Log.d("", "LocationManager requesting GPS location updates");
			} else {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						60000, // One minute
						10, // 10 meters
						this);
				Log.d("", "LocationManager requesting GPS location updates");
			}
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
	 * Add trips to view. This method does not do any trip loading.
	 */
	public void addAllTrips() {
		// Load Routes from storage
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
	}
	
	/**
	 * Load trips.
	 * 
	 */
	public void loadAllTrips() {
		// Load trips for all routes.
		for (RouteListItem routeListItem : routeListItems) {
			Route route = routeListItem.route;
			boolean reverse = false;
			if (location != null) {
				// we have a location, find the closes station and reverse route if necessary
				if (location.distanceTo(route.a.getLocation()) > location.distanceTo(route.b.getLocation())) {
					reverse = true;
				}
			}
			route.loadTrips(Volley.newRequestQueue(this), reverse, rc);
		}
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
		boolean reverse = false;
		if (location != null && location.distanceTo(r.a.getLocation()) > location.distanceTo(r.b.getLocation())) {
			reverse = true;
		}
		r.loadTrips(Volley.newRequestQueue(this), reverse, rc);
	}

	/**
	 * This gets called when we receive a location update from
	 * either the GPS provider or network provider. When we have
	 * a location we stop requesting more updates and then load
	 * trips.
	 */
	@Override
	public void onLocationChanged(Location location) {
		Log.d("", "LocationManager Location changed");
		this.location = location;
		// we only need one location so stop requesting further
		locationManager.removeUpdates(this);
		
		// load trips
		loadAllTrips();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}
}
