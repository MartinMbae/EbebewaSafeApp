package com.example.ebebewa.activities.registration.steps;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.MessageFormat;

import ernestoyaquello.com.verticalstepperform.Step;
import com.example.ebebewa.R;
import com.example.ebebewa.utils.Constants;

import studios.luxurious.kenya47counties.activities.CountyReturned;
import studios.luxurious.kenya47counties.activities.Kenya47Counties;
import studios.luxurious.kenya47counties.models.County;
import studios.luxurious.kenyanpostalcodes.activities.KenyaPostalCodes;
import studios.luxurious.kenyanpostalcodes.activities.PostalAreaSelected;
import studios.luxurious.kenyanpostalcodes.models.PostalArea;

public class DriverCountyPostal extends Step<DriverCountyPostal.CountyPostal> {


    private TextView postalTextview, countyTextView;

    private String selectedCountyString = "", selectedPostalString = "";
    private Context context;

    private String role;

    public DriverCountyPostal(String title, String subtitle, Context context, String role) {
        super(title, subtitle);
        this.context = context;
        this.role = role;

    }

    @Override
    public CountyPostal getStepData() {

        return new CountyPostal(selectedCountyString, selectedPostalString);
    }

    @NonNull
    @Override
    protected View createStepContentLayout() {

        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.step_driver_postal_county, null, false);

        postalTextview = view.findViewById(R.id.postal_selection);
        countyTextView = view.findViewById(R.id.county_selection);

        countyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Kenya47Counties.showAllCountiesDialog(context, "Select your county", true, true, false, new CountyReturned() {
                    @Override
                    public void onSelectedCounty(County selectedCounty) {
                        countyTextView.setText(String.format("%s(%s)", selectedCounty.getName(), selectedCounty.getFormattedCountyNumber()));
                        selectedCountyString = String.valueOf(selectedCounty.getId());
                        markAsCompletedOrUncompleted(true);
                    }
                });
            }
        });

        postalTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KenyaPostalCodes.showAllPostalAreas(context, true, "Select your postal code", new PostalAreaSelected() {
                    @Override
                    public void OnPostalAreaSelected(PostalArea selectedPostalArea) {
                        postalTextview.setText(MessageFormat.format("{0} - {1}", selectedPostalArea.getAreaCode(), selectedPostalArea.getAreaName()));
                        selectedPostalString = selectedPostalArea.getAreaCode();
                        markAsCompletedOrUncompleted(true);
                    }
                });
            }
        });

        return view;
    }


    @Override
    protected void onStepOpened(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // No need to do anything here
    }


    @Override
    public String getStepDataAsHumanReadableString() {
        return "All fields are set";
    }

    @Override
    public void restoreStepData(CountyPostal data) {
        if (countyTextView != null) countyTextView.setText(data.county);
        if (postalTextview != null) postalTextview.setText(data.postal);

    }

    @Override
    protected IsDataValid isStepDataValid(CountyPostal stepData) {

        if (stepData.county == null) {
            return new IsDataValid(false, "Select your county");
        }

        if (stepData.postal.length() < 2 && role.equals(Constants.DRIVER_ROLE)) {
            return new IsDataValid(false, "Select your postal code");
        }

        return new IsDataValid(true);

    }

    public static class CountyPostal {

        public String county, postal;

        public CountyPostal(String county, String postal) {
            this.county = county;
            this.postal = postal;
        }
    }

}
