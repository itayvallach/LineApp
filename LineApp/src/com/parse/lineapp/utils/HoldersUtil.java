package com.parse.lineapp.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.lineapp.utils.HoldersUtil.EventHolder;

public class HoldersUtil {
	
	public static class EventHolder {
		public String id;
		public String category;
		public String address;
		public Date startTime;
		public Date endTime;
		public int currentPar;
		public int maxPar;
		public JSONArray participants;
		public int price;
		public String description;
		public Date firstNot;
		public Date secondNot;
		
		
		
		public EventHolder() {}
		
		public EventHolder (String id, String category, String address, Date startTime, Date endTime,
				int currentPar, int maxPar, JSONArray participants, int price, String description,
				Date firstNot, Date secondNot) {
			
			this.id = id;
			this.category = category;
			this.address = address;
			this.startTime = startTime;
			this.endTime = endTime;
			this.currentPar = currentPar;
			this.maxPar = maxPar;
			this.participants = participants;
			this.price = price;
			this.description = description;
			this.firstNot = firstNot;
			this.secondNot = secondNot;
			
		}
		
	}

	public static List<EventHolder> createEventHolders(List<ParseObject> eventsList) {
		 List<EventHolder> result = new  ArrayList<EventHolder>();
		 Iterator<ParseObject> iter = eventsList.iterator();
		 while (iter.hasNext()) {
			 ParseObject event = iter.next();
			 result.add(createEventHolder(event));
		 }
		return result;
	}
	


	public static EventHolder createEventHolder(ParseObject event) {
		return new EventHolder(event.getObjectId(),
							event.getString("category"),
							event.getString("address"),
							event.getDate("startTime"), 
							event.getDate("endTime"),
							event.getJSONArray("participants").length(),
							event.getInt("maxPar"),
							event.getJSONArray("participants"),
							event.getInt("price"),
							event.getString("description"),
							event.getDate("firstNot"), 
							event.getDate("secondNot"));
	}
	
	
	
	
	
	public static UserHolder getManagerHolder(EventHolder eventHolder) {
		try {
			return (UserHolder) eventHolder.participants.get(0);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static class UserHolder {
		public String id;
		public String name;
		public boolean male;
		
		public UserHolder(String id, String name, boolean male) {
			this.id = id;
			this.name = name;
			this.male = male;
		}
	}

	public static UserHolder createUserHolder(ParseUser user) {
		return new UserHolder(user.getObjectId(), user.getUsername(), user.getBoolean("male"));
	}



	

	

}
