package com.example.ebebewa.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.ebebewa.R;
import com.example.ebebewa.activities.HomeActivityDriver;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.SharedPref;

public class HomeFragmentDriver extends Fragment {


    private SharedPref sharedPref;
    private TextView availableJobs, transistJobs, deliveredJobs, appliedJobs;
    private ProgressBar availableJobsProgressBar, appliedJobsProgressBar, transitProgressBar, deliveredProgressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_driver, container, false);
        if (getActivity() != null) {
            sharedPref = new SharedPref(getActivity());
            availableJobs = view.findViewById(R.id.driver_availableJobs);
            transistJobs = view.findViewById(R.id.transit_jobs);
            deliveredJobs = view.findViewById(R.id.driver_delivered_jobs);
            appliedJobs = view.findViewById(R.id.driver_applied_jobs);
            availableJobsProgressBar = view.findViewById(R.id.availableJobsProgressBar);
            appliedJobsProgressBar = view.findViewById(R.id.appliedJobsProgressBar);
            transitProgressBar = view.findViewById(R.id.transitProgressBar);
            deliveredProgressBar = view.findViewById(R.id.deliveredProgressBar);
            LinearLayout availableJobsLayout = view.findViewById(R.id.availableJobsLayout);
            LinearLayout appliedJobsLayout = view.findViewById(R.id.appliedJobsLayout);
            LinearLayout onTransitLayout = view.findViewById(R.id.onTransitLayout);
            LinearLayout deliveredLayout = view.findViewById(R.id.deliveredLayout);

            getAvaiableJobsCount();
            getDeliveredJobsCount();
            getTransitJobsCount();
            getAppliedJobsCount();
            availableJobsLayout.setOnClickListener(v -> {
                if (getActivity() != null)
                    ((HomeActivityDriver) getActivity()).prepareAvailableJobsFragment(0);
            });
            appliedJobsLayout.setOnClickListener(v -> {
                if (getActivity() != null)
                    ((HomeActivityDriver) getActivity()).prepareAvailableJobsFragment(1);
            });
            onTransitLayout.setOnClickListener(v -> {
                if (getActivity() != null)
                    ((HomeActivityDriver) getActivity()).prepareAvailableJobsFragment(1);
            });
            deliveredLayout.setOnClickListener(v -> {
                if (getActivity() != null)
                    ((HomeActivityDriver) getActivity()).prepareAvailableJobsFragment(1);
            });
        }
        return view;
    }

    private void getAvaiableJobsCount() {
        String uri = Constants.BASE_URL + "api/posts/countavailablejobs/" + sharedPref.getVehicleType();
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, response -> {
            availableJobs.setText(response);
            availableJobsProgressBar.setVisibility(View.GONE);
            availableJobs.setVisibility(View.VISIBLE);
        }, error -> {
            availableJobsProgressBar.setVisibility(View.GONE);
            availableJobs.setText("Network error");
            availableJobs.setVisibility(View.VISIBLE);
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private void getDeliveredJobsCount() {
        String uri = Constants.BASE_URL + "api/posts/countdeliveredjobs/" + sharedPref.getUserName();
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, response -> {
            deliveredJobs.setText(response);
            deliveredProgressBar.setVisibility(View.GONE);
            deliveredJobs.setVisibility(View.VISIBLE);
        }, error -> {
            deliveredProgressBar.setVisibility(View.GONE);
            deliveredJobs.setText("Network error");
            deliveredJobs.setVisibility(View.VISIBLE);
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private void getTransitJobsCount() {
        String uri = Constants.BASE_URL + "api/posts/countjobsontransit/" + sharedPref.getUserName();
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, response -> {
            transistJobs.setText(response);
            transitProgressBar.setVisibility(View.GONE);
            transistJobs.setVisibility(View.VISIBLE);
        }, error -> {
            transitProgressBar.setVisibility(View.GONE);
            transistJobs.setText("Network error");
            transistJobs.setVisibility(View.VISIBLE);
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private void getAppliedJobsCount() {
        String uri = Constants.BASE_URL + "api/posts/countappliedjobs/" + sharedPref.getUserName();
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, response -> {
            appliedJobs.setText(response);
            appliedJobsProgressBar.setVisibility(View.GONE);
            appliedJobs.setVisibility(View.VISIBLE);
        }, error -> {
            appliedJobsProgressBar.setVisibility(View.GONE);
            appliedJobs.setText("Network error");
            appliedJobs.setVisibility(View.VISIBLE);
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }


}
