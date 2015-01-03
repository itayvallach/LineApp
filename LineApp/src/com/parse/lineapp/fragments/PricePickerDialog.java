package com.parse.lineapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;


import com.parse.lineapp.R;
import com.parse.lineapp.activities.NewEventActivity;

public class PricePickerDialog extends Dialog implements NumberPicker.OnValueChangeListener{

	Context mContext;

	public PricePickerDialog(final Context context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.number_picker_dialog, null);
		setTitle(context.getResources().getString(R.string.price));
		setContentView(layout);
		Button button = (Button) findViewById(R.id.button);
		final NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
		np.setMinValue(0);
		np.setMaxValue(9999);
		np.setWrapSelectorWheel(false);
		np.setOnValueChangedListener((OnValueChangeListener) this);
		View.OnClickListener listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((NewEventActivity) context).priceEditText.setText(
						np.getValue() == 0 ? context.getResources().getString(R.string.free)
											: String.valueOf(np.getValue()));
				((NewEventActivity) context).event.put("price", np.getValue());
				finish();
			}
		};
		button.setOnClickListener(listener);



		EditText input = findInput(np);    
		TextWatcher tw = new TextWatcher() {

		        @Override
		        public void onTextChanged(CharSequence s, int start, int before,
		                int count) {}

		        @Override
		        public void beforeTextChanged(CharSequence s, int start, int count,
		                int after) {}

		        @Override
		        public void afterTextChanged(Editable s) {
		                if (s.toString().length() != 0) {
		                    Integer value = Integer.parseInt(s.toString());
		                    if (value >= np.getMinValue()) {
		                        np.setValue(value);
		                    }
		                }
		        }
		    };
		input.addTextChangedListener(tw);

	}

	protected void finish() {
		dismiss();

	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
	}
	
	private EditText findInput(ViewGroup np) {
	    int count = np.getChildCount();
	    for (int i = 0; i < count; i++) {
	        final View child = np.getChildAt(i);
	        if (child instanceof ViewGroup) {
	            findInput((ViewGroup) child);
	        } else if (child instanceof EditText) {
	            return (EditText) child;
	        }
	    }
	    return null;
	}

}