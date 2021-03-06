package se.haffatuben;

import java.util.ArrayList;
import java.util.List;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DisplayRoutesFragment extends Fragment {
	private ListView listView;
	private List<RouteListItem> routes;
	private RouteArrayAdapter adapter;
	private View rootView;
	
	public DisplayRoutesFragment() {
		routes = new ArrayList<RouteListItem>();
	}
	
	/**
	 * Sets routes list to display in list view. This method will call
	 * notifyRoutesDataChanged when done.
	 * @param routes
	 */
	public void setRoutes(List<RouteListItem> routes) {
		this.routes = routes;
		// if the adapter has been initialize, readd all routes to it
		if (adapter != null) {
			adapter.clear();
			for (RouteListItem route : this.routes) {
				adapter.add(route);
			}
		}
		notifyRoutesDataChanged();
	}
	
	/**
	 * Notify list view adapter that the data source has changed
	 */
	public void notifyRoutesDataChanged() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_displayroutes, container,
				false);
		
		adapter = new RouteArrayAdapter(getActivity(), R.layout.route_list_parent, routes);
		
		listView = (ListView) rootView.findViewById(R.id.listView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				final View ev = (View) view.findViewById(R.id.expandableView);
				if (ev.getVisibility() == View.VISIBLE) {
					// Hide trash icon.
					view.findViewById(R.id.trash).setVisibility(View.GONE);
					
					routes.get(position-1).setExpanded(false);
					
					final int initialHeight = ev.getMeasuredHeight();
					Animation a = new Animation() {
						@Override
						protected void applyTransformation(float interpolatedTime,
								Transformation t) {
							if (interpolatedTime == 1) {
								ev.setVisibility(View.GONE);
							} else {
								ev.getLayoutParams().height = initialHeight -(int)(initialHeight * interpolatedTime);
								ev.requestLayout();
							}
						}
						
						@Override
						public boolean willChangeBounds() {
							return true;
						}
					};
					a.setDuration(250);
					ev.startAnimation(a);
				} else {
					routes.get(position-1).setExpanded(true);
					// Show trash icon.
					view.findViewById(R.id.trash).setVisibility(View.VISIBLE);
					
					ev.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					final int targetHeight = ev.getMeasuredHeight();
					ev.getLayoutParams().height = 0;
					ev.setVisibility(View.VISIBLE);
					
					Animation a = new Animation() {
						@Override
						protected void applyTransformation(float interpolatedTime,
								Transformation t) {
							ev.getLayoutParams().height = interpolatedTime == 1 ?
									LayoutParams.WRAP_CONTENT : (int)(targetHeight * interpolatedTime);
							ev.requestLayout();
						}
						
						@Override
						public boolean willChangeBounds() {
							return true;
						}
					};
					// (int)(targetHeight / subtextView.getContext().getResources().getDisplayMetrics().density)
					a.setDuration(250);
					ev.startAnimation(a);
				}
			}
		});
		
		// Pull to refresh listener.
		((PullToRefreshListView) listView).setOnRefreshListener(new OnRefreshListener() {
		    @Override
		    public void onRefresh() {
		        // Refresh trips.
		    	// Load trips.
		    	((MainActivity) getActivity()).loadAllTrips();
		    	notifyRoutesDataChanged();
		    	((PullToRefreshListView) listView).onRefreshComplete();
		    }
		});
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}