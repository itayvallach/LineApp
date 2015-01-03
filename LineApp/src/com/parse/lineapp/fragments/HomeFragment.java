package com.parse.lineapp.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.lineapp.R;
import com.parse.lineapp.adapters.EventListAdapter;
import com.parse.lineapp.utils.HoldersUtil;
import com.parse.lineapp.utils.HoldersUtil.EventHolder;

public class HomeFragment extends Fragment {
	
	private ListView mListView;
	private ProgressBar mProgressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.home_fragment, container, false);

        mListView = (ListView) homeView.findViewById(R.id.home_event_list);
		
		mProgressBar = (ProgressBar) homeView.findViewById(R.id.home_progress_bar);
		mProgressBar.setVisibility(View.VISIBLE);
		
		loadHomeData();
		
        return homeView;
	}

	private void loadHomeData() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> result, ParseException e) {
		        if (e == null) {
		            Log.d("score", "Retrieved " + result.size() + " events");
		            onHomeDataLoaded(HoldersUtil.createEventHolders(result));
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		        }
		    }
		});
		
	}


	public void onHomeDataLoaded(final List<EventHolder> eventHolders) {

		if (mProgressBar != null) {
			mProgressBar.setVisibility(View.GONE);
		}

		if (isAdded()) {
			EventListAdapter adapter = new EventListAdapter(getActivity(), eventHolders);
			
			mListView.setAdapter(adapter);
		}
	}
}


