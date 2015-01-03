package com.parse.lineapp.adapters;
import java.util.List;

import com.parse.lineapp.R;
import com.parse.lineapp.utils.HoldersUtil.EventHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class EventListAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected List<EventHolder> mHolders;
	protected LayoutInflater mInflater;

	public EventListAdapter(Context context, List<EventHolder> eventHolders) {
		mContext = context;
		mHolders = eventHolders;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
	}
	
	@Override
	public int getCount() {
		return mHolders.size();
	}

	@Override
	public Object getItem(int position) {
		return mHolders.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CellViewHolder viewHolder;
		EventHolder eventHolder = mHolders.get(position);
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.small_event_cell, parent, false);

			viewHolder = new CellViewHolder();
			viewHolder.categoryText = (TextView) convertView.findViewById(R.id.category_text);
			viewHolder.addressText = (TextView) convertView.findViewById(R.id.address_text);
			viewHolder.dateText = (TextView) convertView.findViewById(R.id.date_text);
			viewHolder.participantsText = (TextView) convertView.findViewById(R.id.participants_text);
			viewHolder.managerText = (TextView) convertView.findViewById(R.id.manager_text);

			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (CellViewHolder) convertView.getTag();
		}
		
		viewHolder.categoryText.setText(eventHolder.category);
		viewHolder.addressText.setText(eventHolder.address);
		viewHolder.dateText.setText(eventHolder.startTime.toString());
		viewHolder.participantsText.setText(eventHolder.currentPar+" "+mContext.getResources().getString(R.string.out_of)+" "+eventHolder.maxPar);
		viewHolder.managerText.setText("text");
		
		return convertView;
		
	}
	
	
	private static class CellViewHolder {
	    public TextView categoryText;
	    public TextView dateText;
	    public TextView addressText;
	    public TextView participantsText;
	    public TextView managerText;
	    public ImageView joinIcon;
	}
}
	
