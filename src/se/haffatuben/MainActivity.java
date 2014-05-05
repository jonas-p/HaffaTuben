package se.haffatuben;

import java.util.ArrayList;
import java.util.Map;

import com.android.volley.RequestQueue;
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

public class MainActivity extends ActionBarActivity implements AddRouteResultReciever {
	// ArrayList containing Route objects.
	ArrayList<Route> routes;
	// DisplayRoutesFragment.
	DisplayRoutesFragment displayRoutesFragment;
	
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
		routes = new ArrayList<Route>();
		for (Map.Entry<String, ?> entry : routeMap.entrySet()) {
			routes.add(Route.create((String) entry.getValue()));
		}
		// Load trips for all routes.
		// TODO: Set reverse.
		boolean reverse = false;
		for (Route route : routes) {
			route.loadTrips(Volley.newRequestQueue(this), reverse, new RouteLoadedReciever() {
				
				@Override
				public void onRouteLoaded(Route r) {
					// TODO Call refresh.
					System.out.println(r.trips);
					Log.d("", "Trip loaded");
				}
				
				@Override
				public void onRouteFailed(Error e) {
					// TODO Android toast.
					Log.d("", "Trip load failed");
				}
				
			});
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
		rp.addRoute(routeString);
	}
}
