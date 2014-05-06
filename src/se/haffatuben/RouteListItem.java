package se.haffatuben;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * RouteListItem holds a Route object and properties for the object
 * to be displayed in a ListView with RouteArrayAdapter properly.
 * 
 * @author jonas
 *
 */
public class RouteListItem {
	private boolean isExpanded;
	public Route route;
	
	/**
	 * Create a new RouteListItem
	 * @param route Route object
	 */
	public RouteListItem(Route route) {
		this.route = route;
		this.isExpanded = false;
	}
	
	/**
	 * Return a boolean whether the expandable view
	 * should be hidden or shown.
	 * 
	 * @return
	 */
	public boolean isExpanded() {
		return isExpanded;
	}
	
	/**
	 * Sets the isExpandable boolean that indicates
	 * whether the expandable view should be hidden or shown.
	 * 
	 * @param isExpanded
	 */
	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}
	
	/**
	 * Update the view with the correct information and add
	 * subviews to the expandable list.
	 * 
	 * @param context
	 * @param view
	 */
	public void updateView(Context context, View view) {
		TextView textView = (TextView) view.findViewById(R.id.destination);
		TextView textView2 = (TextView) view.findViewById(R.id.origin);
		TextView textView3 = (TextView) view.findViewById(R.id.departureTime);
		
		// TODO: Use strings from strings.xml
		textView.setText(route.b.name.replaceAll("\\(.+", ""));
		textView2.setText(context.getString(R.string.from) + " " + route.a.name.replaceAll("\\(.+", ""));
		
		// Check if there is any trips.
		if (route.trips.size() == 0) {
			return;
		}
		// Time delta.
		long timeDelta = 0;
		// First position of trips.
		int firstPosition = 0;
		// Get first departure time with delta > 0.
		for (int i = 0; i < route.trips.size(); i++) {
			timeDelta = (route.trips.get(i).departure.getTime() - System.currentTimeMillis())/60000;
			if (timeDelta >= 0) { 
				firstPosition = i;
				break; 
			}
		}
		// Set first departure time.
		textView3.setText(timeDelta+" min");
				
		LinearLayout expandableLayout = (LinearLayout) view.findViewById(R.id.expandableView);
		
		// Populate child views. Skip first.
		firstPosition++;
		int lastPosition = route.trips.size() - firstPosition + 1;
		if (lastPosition - firstPosition > 3) {
			lastPosition = firstPosition + 3;
		}
		for (int i = firstPosition; i < lastPosition; i++) {
			expandableLayout.addView(getChild(context, expandableLayout, i));
		}
	}
	
	/**
	 * Returns a new child view
	 * 
	 * @param context Context
	 * @param parent Parent view group
	 * @param position Child position
	 * @return
	 */
	private View getChild(Context context, ViewGroup parent, int position) {
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View view = inflater.inflate(R.layout.route_list_child, parent, false);
		// Get text views.
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView departureTime = (TextView) view.findViewById(R.id.departureTime);
		TextView icon = (TextView) view.findViewById(R.id.icon);
		
		// Set text views.
		title.setText(route.b.name.replaceAll("\\(.+", ""));
		departureTime.setText((route.trips.get(position).departure.getTime() - System.currentTimeMillis())/60000 + " min");
		icon.setText(route.trips.get(position).type.iconString);
		return view;
	}
}
