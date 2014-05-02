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
	  * @param Serialized route 
	  */
	 public void addRoute(String route) {
		 // Generate UUID.
		 Long routeLongId = UUID.randomUUID().getMostSignificantBits();
		 String routeId = Long.toString(routeLongId);
		 // Init editor.
		 routeEditor = routeDB.edit();
		 // Add route.
		 routeEditor.putString(routeId, route);
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
	
}
