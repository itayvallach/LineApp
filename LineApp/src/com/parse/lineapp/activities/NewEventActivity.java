package com.parse.lineapp.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.lineapp.R;
import com.parse.lineapp.fragments.DatePickerFragment;
import com.parse.lineapp.fragments.EndTimePickerFragment;
import com.parse.lineapp.fragments.ParticipantsPickerDialog;
import com.parse.lineapp.fragments.PricePickerDialog;
import com.parse.lineapp.fragments.StartTimePickerFragment;
import com.parse.lineapp.utils.HoldersUtil;
import com.parse.lineapp.utils.LADateUtils;
import com.parse.lineapp.utils.PlaceDetailsJSONParser;
import com.parse.lineapp.utils.PlaceJSONParser;

public class NewEventActivity extends FragmentActivity {
	Activity mActivity;
	public ParseObject event;
	public Date startTime = new Date();
	public Date endTime = new Date();
	public int secondNotificationPos = 0;
	public int firstNotificationPos = 0;
	public int startTimeDefaultHour = 0;
	public int startTimeDefaultMinute = 0;
	public int endTimeDefaultHour = 0;
	public int endTimeDefaultMinute = 0;
	public EditText dateEditText;
	public EditText startTimeEditText;
	public EditText endTimeEditText;
	public EditText numOfParticipantsEditText;
	public EditText priceEditText;
	public EditText descriptionEditText;



	ArrayList<String> selectedContactsNames; 

	AutoCompleteTextView atvPlaces;
	DownloadTask placesDownloadTask;
	DownloadTask placeDetailsDownloadTask;
	ParserTask placesParserTask;
	ParserTask placeDetailsParserTask;
	final int PLACES=0;
	final int PLACES_DETAILS=1;
	GoogleMap googleMap;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		event = new ParseObject("Event");

		setContentView(R.layout.new_event_activity);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		dateEditText = (EditText) findViewById(R.id.EditTextDate);
		dateEditText.setKeyListener(null);
		startTimeEditText = (EditText) findViewById(R.id.EditTextStartTime);
		startTimeEditText.setKeyListener(null);
		endTimeEditText = (EditText) findViewById(R.id.EditTextEndTime);
		endTimeEditText.setKeyListener(null);
		numOfParticipantsEditText = (EditText) findViewById(R.id.EditTextNumOfParticipants);
		numOfParticipantsEditText.setKeyListener(null);
		priceEditText = (EditText) findViewById(R.id.EditTextPrice);
		priceEditText.setKeyListener(null);

		descriptionEditText = (EditText) findViewById(R.id.EditTextDescription);

		Button finish = (Button) findViewById(R.id.finish_btn);
		finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				createEvent();

			}
		});


		Spinner categorySpinner = (Spinner) findViewById(R.id.CategorySpinner);
		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				event.put("category", arg0.getItemAtPosition(arg2).toString());

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		Spinner firstNotificationSpinner = (Spinner) findViewById(R.id.FirstNotificationSpinner);
		firstNotificationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				firstNotificationPos = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				firstNotificationPos = 0;
			}

		});
		Spinner secondNotificationSpinner = (Spinner) findViewById(R.id.SecondNotificationSpinner);
		secondNotificationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				secondNotificationPos = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				secondNotificationPos= 0;				
			}

		});
		atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
		atvPlaces.setThreshold(1);

		// Adding textchange listener
		atvPlaces.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Creating a DownloadTask to download Google Places matching "s"
				placesDownloadTask = new DownloadTask(PLACES);

				// Getting url to the Google Places Autocomplete api
				String url = getAutoCompleteUrl(s.toString());

				// Start downloading Google Places
				// This causes to execute doInBackground() of DownloadTask class
				placesDownloadTask.execute(url);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});

		// Setting an item click listener for the AutoCompleteTextView dropdown list
		atvPlaces.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long id) {

				ListView lv = (ListView) arg0;
				SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

				HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);

				// Creating a DownloadTask to download Places details of the selected place
				placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

				// Getting url to the Google Places details api
				String url = getPlaceDetailsUrl(hm.get("reference"));

				// Start downloading Google Place Details
				// This causes to execute doInBackground() of DownloadTask class
				placeDetailsDownloadTask.execute(url);

			}
		});
	}



	private String getAutoCompleteUrl(String place) {

		// Obtain browser key from https://code.google.com/apis/console
		String key = "key=AIzaSyBsRBMcmNTQSbDi1a4h-jCTwO8fzRjNTF0";

		// place to be be searched
		String input;
		try {
			input = "input="+ URLEncoder.encode(place, "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			input = null;
		}

		// place type to be searched
		String types = "types=geocode";

		String components = "components=country:il";

		// Language
		String language = "language=iw";

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = input+"&"+types+"&"+sensor+"&"+key+"&"+components+"&"+language;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

		return url;
	}

	private String getPlaceDetailsUrl(String ref){

		// Obtain browser key from https://code.google.com/apis/console
		String key = "key=AIzaSyBsRBMcmNTQSbDi1a4h-jCTwO8fzRjNTF0";

		// reference of place
		String reference = "reference="+ref;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = reference+"&"+sensor+"&"+key;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/place/details/"+output+"?"+parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try{
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb  = new StringBuffer();

			String line = "";
			while( ( line = br.readLine())  != null){
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		}catch(Exception e){
			Log.d("Exception while downloading url", e.toString());
		}finally{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{

		private int downloadType=0;

		// Constructor
		public DownloadTask(int type){
			this.downloadType = type;
		}

		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			switch(downloadType){
			case PLACES:
				// Creating ParserTask for parsing Google Places
				placesParserTask = new ParserTask(PLACES);

				// Start parsing google places json data
				// This causes to execute doInBackground() of ParserTask class
				placesParserTask.execute(result);

				break;

			case PLACES_DETAILS :
				// Creating ParserTask for parsing Google Places
				placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

				// Starting Parsing the JSON string
				// This causes to execute doInBackground() of ParserTask class
				placeDetailsParserTask.execute(result);
			}
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

		int parserType = 0;

		public ParserTask(int type){
			this.parserType = type;
		}

		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<HashMap<String, String>> list = null;

			try{
				jObject = new JSONObject(jsonData[0]);

				switch(parserType){
				case PLACES :
					PlaceJSONParser placeJsonParser = new PlaceJSONParser();
					// Getting the parsed data as a List construct
					list = placeJsonParser.parse(jObject);
					break;
				case PLACES_DETAILS :
					PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
					// Getting the parsed data as a List construct
					list = placeDetailsJsonParser.parse(jObject);
				}

			}catch(Exception e){
				Log.d("Exception",e.toString());
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			switch(parserType){
			case PLACES :
				String[] from = new String[] { "description"};
				int[] to = new int[] { android.R.id.text1 };

				// Creating a SimpleAdapter for the AutoCompleteTextView
				SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

				// Setting the adapter
				atvPlaces.setAdapter(adapter);
				break;
			case PLACES_DETAILS :
				HashMap<String, String> hm = result.get(0);

				// Getting latitude from the parsed data
				double latitude = Double.parseDouble(hm.get("lat"));

				// Getting longitude from the parsed data
				double longitude = Double.parseDouble(hm.get("lng"));

				// Getting reference to the SupportMapFragment of the activity_main.xml
				//SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

				// Getting GoogleMap from SupportMapFragment
				//googleMap = fm.getMap();

				LatLng point = new LatLng(latitude, longitude);


				// Showing the user input location in the Google Map
				CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(point);
				CameraUpdate cameraZoom = CameraUpdateFactory.zoomBy(5);


				googleMap.moveCamera(cameraPosition);
				googleMap.animateCamera(cameraZoom);

				MarkerOptions options = new MarkerOptions();
				options.position(point);
				options.title("Position");
				options.snippet("Latitude:"+latitude+",Longitude:"+longitude);

				// Adding the marker in the Google Map
				googleMap.addMarker(options);

				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void showStartTimePickerDialog(View v) {
		DialogFragment newFragment = new StartTimePickerFragment();
		newFragment.show(getSupportFragmentManager(), "startTimePicker");
	}

	public void showEndTimePickerDialog(View v) {
		DialogFragment newFragment = new EndTimePickerFragment();
		newFragment.show(getSupportFragmentManager(), "endTimePicker");
	}

	public void showParticipantsPickerDialog(View v) {
		ParticipantsPickerDialog d = new ParticipantsPickerDialog(NewEventActivity.this);
		d.show();
	}

	public void showPricePickerDialog(View v) {
		PricePickerDialog d = new PricePickerDialog(NewEventActivity.this);
		d.show();
	}

	public void launchContactsPicker(View v) {
		final int request_code = 1010;
		startActivityForResult(new Intent(NewEventActivity.this, ContactPickerActivity.class), request_code);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);


		if(resultCode==RESULT_OK && !data.getExtras().isEmpty() && data.getExtras().containsKey("selectedContacts"))
		{

			Object[] objArray = (Object[])data.getExtras().getSerializable("selectedContacts");
			String selectedContacts[][]=null;
			if(objArray!=null)
			{
				selectedContacts = new String[objArray.length][];
				for(int i=0; i<objArray.length; i++)
				{
					selectedContacts[i] = (String[]) objArray[i];
				}

				//Now selectedContacts[] contains the selected contacts

				selectedContactsNames = new ArrayList<String>();
				for (String[] contact : selectedContacts) {
					selectedContactsNames.add(contact[1]);
				}

				ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.contact_simple_row, selectedContactsNames);
				ListView contactsNamesList = (ListView) findViewById(R.id.selectedContactsList);
				contactsNamesList.setAdapter(listAdapter);
			}
		}

	}


	public void createEvent() {
		final ParseUser user = ParseUser.getCurrentUser();
		JSONArray participants = new JSONArray();
		participants.put(new Gson().toJson(HoldersUtil.createUserHolder(user)));
		event.put("participants", participants);
		event.put("startTime", startTime);
		event.put("endTime", endTime);
		event.put("firstNot", LADateUtils.getDateFromPos(firstNotificationPos, startTime));
		event.put("secondNot", LADateUtils.getDateFromPos(secondNotificationPos, startTime));
		event.put("description", descriptionEditText.getText().toString());
		event.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {

					List<String> eventIds = user.getList("events");
					eventIds.add(event.getObjectId());
					user.put("events", eventIds);
					user.saveInBackground(new SaveCallback() {

						@Override
						public void done(ParseException e) {
							if (e == null) {
								Toast.makeText(mActivity, getResources().getString(R.string.event_created_toast), Toast.LENGTH_SHORT).show();
							}
							else {  //something went wrong
								System.out.println("SECOND EXCEPTION ********************" +  e.getMessage());
							}
						}
					});
				}
				else { //something went wrong
					System.out.println("FIRST EXCEPTION ********************" + e.getMessage());
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		onBackPressed();
		return true;
	}
}
