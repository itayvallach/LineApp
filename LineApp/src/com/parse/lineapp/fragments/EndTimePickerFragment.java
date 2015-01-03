package com.parse.lineapp.fragments;
import java.util.Calendar;

import com.parse.lineapp.activities.NewEventActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.app.TimePickerDialog;


public class EndTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = ((NewEventActivity) getActivity()).endTimeDefaultHour != 0 ? ((NewEventActivity) getActivity()).endTimeDefaultHour : c.get(Calendar.HOUR_OF_DAY);
		int minute = ((NewEventActivity) getActivity()).endTimeDefaultMinute;

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				true);
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		((NewEventActivity) getActivity()).endTimeDefaultHour = hourOfDay;
		((NewEventActivity) getActivity()).endTimeDefaultMinute = minute;

		String minuteString;
		String hourString;
		if (minute < 10) {
			minuteString = "0"+String.valueOf(minute);
		}
		else {
			minuteString =String.valueOf(minute);
		}
		if (hourOfDay < 10) {
			hourString = "0"+String.valueOf(hourOfDay);
		}
		else {
			hourString = String.valueOf(hourOfDay);
		}
		((NewEventActivity) getActivity()).endTimeEditText.setText(hourString+" : "+minuteString);
		
		((NewEventActivity) getActivity()).endTime.setHours(hourOfDay);
		((NewEventActivity) getActivity()).endTime.setMinutes(minute);
	}
}