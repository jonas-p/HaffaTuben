package se.haffatuben;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
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
		
		// Delete handler.
		TextView trash = (TextView) convertView.findViewById(R.id.trash);
		trash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				ListView lv = (ListView) v.getParent().getParent();
				int position = lv.getPositionForView(v);
				AlertDialog show = new AlertDialog.Builder(v.getContext())
				.setTitle("Ta bort rutt")
				.setMessage("Vill du ta bort rutten?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO: Delete route.
						
						// Toast success.
						Toast.makeText(v.getContext(), "Rutten raderades", Toast.LENGTH_SHORT).show();
					}
					
				}).setNegativeButton("Nej", null).show();
				// TODO Auto-generated method stub
				System.out.println("TRASH CLICKED!");
			}
		});
		return convertView;
	}
}
