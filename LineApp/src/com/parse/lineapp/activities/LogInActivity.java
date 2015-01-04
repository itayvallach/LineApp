package com.parse.lineapp.activities;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;

import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.lineapp.R;

public class LogInActivity extends Activity {

	Activity mActivity;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = new Intent(this, MainFragmentActivity.class);
		// Check if there is a currently logged in user
	    // and they are linked to a Facebook account.
	    ParseUser currentUser = ParseUser.getCurrentUser();
	    if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
	        // Go to the user info activity
	    	startActivity(intent);
	    }
		setContentView(R.layout.main);
		TextView loginText = (TextView) findViewById(R.id.login);
		mActivity = this;
		loginText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				List<String> permissions = Arrays.asList("public_profile", "user_friends");
				ParseFacebookUtils.logIn(permissions, mActivity, new LogInCallback() {
					@Override
					public void done(ParseUser user, ParseException err) {
						if (user == null) {
							Log.d("LineApp", "Uh oh. The user cancelled the Facebook login.");
						} else if (user.isNew()) {
							Log.d("LineApp", "User signed up and logged in through Facebook!");
							startActivity(intent);
						} else {
							Log.d("LineApp", "User logged in through Facebook!");
							startActivity(intent);
						}
					}
				});
			}
		});

		ParseAnalytics.trackAppOpened(getIntent());
	}

	public void onResume() {
		super.onResume();
		//		Locale locale = new Locale("iw");
		//		Locale.setDefault(locale);
		//		Configuration config = new Configuration();
		//		config.locale = locale;
		//		getBaseContext().getResources().updateConfiguration(config,
		//		      getBaseContext().getResources().getDisplayMetrics());
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
}

