package se.haffatuben;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

/**
 * ArrayAdapter for displaying route objects. The resource passed
 * to the adapter needs to have an LinearLayout object with ID expandableView.
 * The expandableLayout is hidden / shown depending on the result from
 * the method isExpanded in the corresponding RouteListItem in the array.
 * 
 * @author jonas
 *
 */
public class RouteArrayAdapter extends ArrayAdapter<RouteListItem> {
	private List<RouteListItem> mData;
	private int mResourceId;
	
	/**
	 * Create a new RouteArrayAdapter
	 * 
	 * @param context Context
	 * @param resource Resource id
	 * @param objects List of RouteListItem objects
	 */
	public RouteArrayAdapter(Context context, int resource,
			List<RouteListItem> objects) {
		super(context, resource, objects);
		mResourceId = resource;
		mData = objects;
	}
	
	/**
	 * Returns the view to the ListView. The expandableLayout item is
	 * hidden / shown according to the result from the RouteListItem
	 * at index position in the objects array.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
			convertView = inflater.inflate(mResourceId, parent, false);
		}
		
		LinearLayout expandableLayout = (LinearLayout) convertView.findViewById(R.id.expandableView);
		expandableLayout.removeAllViews();
		
		// Pass the view to the RotueListItem object
		mData.get(position).updateView(getContext(), convertView);
		
		// Show / Hide the expandable view
		if (mData.get(position).isExpanded()) {
			expandableLayout.setVisibility(View.VISIBLE);
		} else {
			expandableLayout.setVisibility(View.GONE);
		}
		
		return convertView;
	}
}
