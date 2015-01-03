package com.parse.lineapp.fragments;

import java.util.Calendar;
import java.util.Date;

import com.parse.lineapp.R;
import com.parse.lineapp.activities.NewEventActivity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;

public class DatePickerFragment extends DialogFragment  implements DatePickerDialog.OnDateSetListener{
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
       ((NewEventActivity) getActivity()).dateEditText.setText(day +" / "+(month + 1)+" / "+year);
       
       ((NewEventActivity) getActivity()).startTime.setYear(year);
       ((NewEventActivity) getActivity()).startTime.setMonth(month);
       ((NewEventActivity) getActivity()).startTime.setDate(day);
       
       ((NewEventActivity) getActivity()).endTime.setYear(year);
       ((NewEventActivity) getActivity()).endTime.setMonth(month);
       ((NewEventActivity) getActivity()).endTime.setDate(day);
       
    }
}
