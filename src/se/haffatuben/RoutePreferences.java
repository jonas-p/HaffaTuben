package se.haffatuben;

import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class RoutePreferences {
	 SharedPreferences routeDB;
	 SharedPreferences.Editor routeEditor;
	 
	 /**
	  * RoutePreferences helper class to add and get Routes.
	  * @param context
	  */
	 public RoutePreferences(Context context) {
		 routeDB = context.getSharedPreferences("routes", Activity.MODE_PRIVATE);
	 }
	 
	 /**
	  * Adds a serialized route to SharedPreferences.
	  * @param Route id.
	  * @param Serialized route 
	  */
	 public void addRoute(String id, String route) {
		 // Init editor.
		 routeEditor = routeDB.edit();
		 // Add route.
		 routeEditor.putString(id, route);
		 routeEditor.commit();
	 }
	 
	 /**
	  * Returns a Map of serialized routes.
	  * @return Serialized routes.
	  */
	 public Map<String, ?> getRoutes() {
		 // Get routes.
		 // Return routes.
		 return routeDB.getAll();
	 }
	 
	 /**
	  * Removes a route.
	  * @param Route id.
	  */
	 public void removeRoute(String id) {
		 // Init editor.
		 routeEditor = routeDB.edit();
		 // Remove route.
		 routeEditor.remove(id);
		 routeEditor.commit();
	 }
	
}
