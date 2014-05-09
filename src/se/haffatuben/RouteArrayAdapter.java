package se.haffatuben;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	private List<RouteListItem> data;
	private int resourceId;
	
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
		this.resourceId = resource;
		this.data = objects;
	}
	
	/**
	 * Returns the view to the ListView. The expandableLayout item is
	 * hidden / shown according to the result from the RouteListItem
	 * at index position in the objects array.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
			convertView = inflater.inflate(resourceId, parent, false);
		}
		
		LinearLayout expandableLayout = (LinearLayout) convertView.findViewById(R.id.expandableView);
		expandableLayout.removeAllViews();
		
		// Pass the view to the RotueListItem object
		data.get(position).updateView(getContext(), convertView);
		
		// Show / Hide the expandable view
		if (data.get(position).isExpanded()) {
			expandableLayout.setVisibility(View.VISIBLE);
		} else {
			expandableLayout.setVisibility(View.GONE);
		}
		
		// Delete handler.
		TextView trash = (TextView) convertView.findViewById(R.id.trash);
		trash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				ListView lv = (ListView) v.getParent().getParent();
				final int position = lv.getPositionForView(v);
				new AlertDialog.Builder(v.getContext())
				.setTitle(getContext().getString(R.string.delete_route))
				.setMessage(getContext().getString(R.string.route_are_you_sure))
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(getContext().getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Delete route.
						RoutePreferences rp = new RoutePreferences(v.getContext());
						rp.removeRoute(data.get(position).route.id);
						data.remove(position);
						v.setVisibility(View.GONE);
						notifyDataSetChanged();
						// Toast success.
						
						Toast.makeText(v.getContext(), getContext().getString(R.string.route_deleted),
								Toast.LENGTH_SHORT).show();
					}
					
				}).setNegativeButton(getContext().getString(android.R.string.no), null).show();
			}
		});
		return convertView;
	}
}
