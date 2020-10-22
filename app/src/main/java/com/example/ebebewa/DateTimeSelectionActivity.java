package com.example.ebebewa;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ebebewa.activities.ClientWelcomeActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeSelectionActivity extends AppCompatActivity {

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;

    private TextView selectDate,selectTime;
    private SimpleDateFormat simpleDateFormat;
    private Button nextBtn;

    private boolean dateHasBeenSet = false;
    private boolean timeHasBeenSet = false;


    // The callback received when the user "sets" the date in the Dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplay(0);
            dateHasBeenSet = true;

            if (timeHasBeenSet){
                nextBtn.setEnabled(true);
            }

        }
    };
    // The callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            updateDisplay(1);

            timeHasBeenSet = true;

            if (dateHasBeenSet){
                nextBtn.setEnabled(true);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time_picker);

        // Capture our View elements
        selectDate =  findViewById(R.id.selectDate);
        selectTime =  findViewById(R.id.selectTime);
        nextBtn =  findViewById(R.id.submit);

        // Set an OnClickListener on the Change The Date Button
        selectDate.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        // Set an OnClickListener on the Change The Time Button
        selectTime.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        // Get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        //date = c.getTime();	//get the current date time
        //epochTime = date.getTime();	//get the epoch time
        // = setEpochTime(mYear, mMonth,  mDay,  mHour,mMinute);


       
        
        
        Bundle bundle = getIntent().getExtras();
        
        String destination = bundle.getString("destination");
        String origin = bundle.getString("origin");
        String sender = bundle.getString("sender");
        String receiver = bundle.getString("receiver");

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = selectTime.getText().toString();
                String date = selectDate.getText().toString();

                Intent transportIntent = new Intent(DateTimeSelectionActivity.this, TransportMode.class);
                transportIntent.putExtra("destination", destination);
                transportIntent.putExtra("origin",origin);
                transportIntent.putExtra("sender",sender);
                transportIntent.putExtra("receiver", receiver);
                transportIntent.putExtra("date", date);
                transportIntent.putExtra("time", time);
                startActivity(transportIntent);
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void toPreviousPage(View view) {
        finish();
    }
    // Update the date in the TextView
    //replace this update display function with the access to DB
    private void updateDisplay(int type) {
        switch(type) {
            case 0:
                selectDate.setText(new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
                break;
            case 1:
                selectTime.setText(new StringBuilder().append(pad(mHour)).append(":").append(pad(mMinute)));
                break;

        }
    }

    // Prepends a "0" to 1-digit minutes
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

    // Create and return DatePickerDialog
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
                        mDay);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
                        false);
        }
        return null;
    }
}
