package se.haffatuben;

import java.util.ArrayList;

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
	private ListView mListView;
	private ArrayList<RouteListItem> mData;
	
	public DisplayRoutesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_displayroutes, container,
				false);
		
		// TODO: Load routes from main activity
		mData = new ArrayList<>();
		mData.add(new RouteListItem(new Route(new Station("Midsommarkransen", "1", 0.0, 0.0),
				new Station("Hornstull", "2", 0.0, 0.0))));
		mData.add(new RouteListItem(new Route(new Station("Liljeholmen", "1", 0.0, 0.0),
				new Station("Karlaplan", "2", 0.0, 0.0))));
		
		RouteArrayAdapter adapter = new RouteArrayAdapter(getActivity(), R.layout.route_list_parent, mData);
		
		mListView = (ListView) rootView.findViewById(R.id.listView);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				final View ev = (View) view.findViewById(R.id.expandableView);
				if (ev.getVisibility() == View.VISIBLE) {
					mData.get(position).setExpanded(false);
					
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
					mData.get(position).setExpanded(true);
					
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
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}