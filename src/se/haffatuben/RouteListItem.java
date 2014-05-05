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
	private Route route;
	
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
		TextView textView = (TextView) view.findViewById(R.id.textView);
		TextView textView2 = (TextView) view.findViewById(R.id.textView2);
		
		// TODO: Use strings from strings.xml
		textView.setText(route.b.name);
		textView2.setText("från " + route.a.name);
				
		LinearLayout expandableLayout = (LinearLayout) view.findViewById(R.id.expandableView);

		expandableLayout.addView(getChild(context, expandableLayout, 0));
		expandableLayout.addView(getChild(context, expandableLayout, 1));
		expandableLayout.addView(getChild(context, expandableLayout, 2));
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
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(route.b.name);
		
		return view;
	}
}
