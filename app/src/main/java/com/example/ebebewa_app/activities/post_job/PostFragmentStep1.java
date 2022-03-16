package com.example.ebebewa_app.activities.post_job;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.example.ebebewa_app.R;

public class PostFragmentStep1 extends Fragment implements DatePickerDialog.OnDateSetListener, DatePickerDialog.OnCancelListener {
    private TextView selectDate;
    private SimpleDateFormat simpleDateFormat;
    private boolean dateSet = false;
    private Button nextBtn;
    private PostDeliveryJobActivity postDeliveryJobActivity;

    public PostFragmentStep1(PostDeliveryJobActivity postDeliveryJobActivity) {
        this.postDeliveryJobActivity = postDeliveryJobActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_job_step1, container, false);

        if (getActivity() != null) {
            selectDate = view.findViewById(R.id.selectDate);
            nextBtn = view.findViewById(R.id.nextBtn);

            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            selectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date c = Calendar.getInstance().getTime();
                    int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(c));
                    int month = Integer.parseInt(new SimpleDateFormat("MM").format(c));
                    int day = Integer.parseInt(new SimpleDateFormat("dd").format(c)) + 1;
                    showDate(year, month, day, R.style.DatePickerSpinner);
                }
            });

            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    postDeliveryJobActivity.luggage_origin = originEdittext.getText().toString().trim();
//                    postDeliveryJobActivity.luggage_destination = destinationEdittext.getText().toString().trim();
                    if (getActivity() != null)
                        ((PostDeliveryJobActivity) getActivity()).goToFragment(new PostFragmentStep2(postDeliveryJobActivity), 1);
                }
            });

            Places.initialize(getActivity(), getString(R.string.google_maps_key));
            AutocompleteSupportFragment autocompleteFragmentOrigin = (AutocompleteSupportFragment)
                    getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_origin);

            if (autocompleteFragmentOrigin != null) {
                autocompleteFragmentOrigin.setCountry("KE");    //country type
                autocompleteFragmentOrigin.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS));

                autocompleteFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {

                        String name =  place.getName()+", "+place.getAddress();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                autocompleteFragmentOrigin.setText(name);
                            }
                        },300);


                        postDeliveryJobActivity.luggage_origin =name;
                        setButtonNextVisibility();
                    }

                    @Override
                    public void onError(Status status) {
                    }
                });

            }


            fillDate();
            if (postDeliveryJobActivity.luggage_origin != null) {
                if (autocompleteFragmentOrigin != null) {
                    autocompleteFragmentOrigin.setText(postDeliveryJobActivity.luggage_origin);
                    setButtonNextVisibility();
                }
            }
        }
        return view;
    }

    private void setButtonNextVisibility() {
        String origin = postDeliveryJobActivity.luggage_origin;

        if (!TextUtils.isEmpty(origin) && dateSet) {
            nextBtn.setEnabled(true);
        } else {
            nextBtn.setEnabled(false);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        postDeliveryJobActivity.pickUpDate = simpleDateFormat.format(calendar.getTime());
        fillDate();
    }

    private void fillDate() {
        if (postDeliveryJobActivity.pickUpDate != null) {
            selectDate.setText(postDeliveryJobActivity.pickUpDate);
            dateSet = true;
        } else {
            dateSet = false;
        }
        setButtonNextVisibility();
    }

    @VisibleForTesting
    private void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(getActivity())
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .defaultDate(year, monthOfYear - 1, dayOfMonth)
                .build()
                .show();
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

}
