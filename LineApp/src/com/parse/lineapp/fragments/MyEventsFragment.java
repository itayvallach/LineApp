package com.parse.lineapp.fragments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.lineapp.R;
import com.parse.lineapp.adapters.EventListAdapter;
import com.parse.lineapp.utils.HoldersUtil;
import com.parse.lineapp.utils.HoldersUtil.EventHolder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MyEventsFragment extends Fragment{
	
	private ListView mListView;
	private ProgressBar mProgressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myEventsView = inflater.inflate(R.layout.my_events_fragment, container, false);

        mListView = (ListView) myEventsView.findViewById(R.id.my_events_list);
		
		mProgressBar = (ProgressBar) myEventsView.findViewById(R.id.progress_bar);
		mProgressBar.setVisibility(View.VISIBLE);
		
		loadMyEventsData();

        return myEventsView;
	}


	private void loadMyEventsData() {
		ParseUser user = ParseUser.getCurrentUser();
		List<String> eventIds = user.getList("events");
		Iterator<String> iter = eventIds.iterator();
		final List<EventHolder> eventHolders = new ArrayList<EventHolder>();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
		while (iter.hasNext()) {
			query.getInBackground(iter.next(), new GetCallback<ParseObject>() {
				public void done(ParseObject event, ParseException e) {
					if (e == null) {
						eventHolders.add(HoldersUtil.createEventHolder(event));
					}
				}
			});
		}
		onDataLoaded(eventHolders);
	}


	public void onDataLoaded(final List<EventHolder> eventHolders) {

		if (mProgressBar != null) {
			mProgressBar.setVisibility(View.GONE);
		}

		if (isAdded()) {
			EventListAdapter adapter = new EventListAdapter(getActivity(), eventHolders);
			
			mListView.setAdapter(adapter);
		}
	}
}
