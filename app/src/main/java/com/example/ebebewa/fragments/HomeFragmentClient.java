package com.example.ebebewa.fragments;

import android.content.Intent;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;
import com.example.ebebewa.R;
import com.example.ebebewa.activities.HomeActivityClient;
import com.example.ebebewa.activities.post_job.PostDeliveryJobActivity;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.Job;
import com.example.ebebewa.utils.SharedPref;

public class HomeFragmentClient extends Fragment {

    private SharedPref sharedPref;
    private TextView postedJobsTxt, jobsTransit, deliveredJobs;
    private ProgressBar postedJobsProgressBar, transitProgressBar, deliveredProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_client, container, false);

        if (getActivity() != null) {

            sharedPref = new SharedPref(getActivity());
            postedJobsTxt = view.findViewById(R.id.jobsPosted);
            jobsTransit = view.findViewById(R.id.jobsTransit);
            deliveredJobs = view.findViewById(R.id.deliveredJobs);
            postedJobsProgressBar = view.findViewById(R.id.postedJobsProgressBar);
            transitProgressBar = view.findViewById(R.id.transitProgressBar);
            deliveredProgressBar = view.findViewById(R.id.deliveredProgressBar);
            FloatingTextButton floatingActionButton = view.findViewById(R.id.fabFloating);
            LinearLayout postedJobsLayout = view.findViewById(R.id.postedJobsLayout);
            LinearLayout postJobsLayout = view.findViewById(R.id.postJobsLayout);
            LinearLayout onTransitLayout = view.findViewById(R.id.onTransitLayout);
            LinearLayout confirmedDeliveryLayout = view.findViewById(R.id.confirmedDeliveryLayout);

            postedJobsLayout.setOnClickListener(v -> {
                if (getActivity() != null)
                    ((HomeActivityClient) getActivity()).prepareJobPostedFragment(Constants.AVAILABLE_JOBS);
            });

            onTransitLayout.setOnClickListener(v -> {
                if (getActivity() != null)
                    ((HomeActivityClient) getActivity()).prepareJobPostedFragment(Constants.TRANSIT_JOBS);
            });

            confirmedDeliveryLayout.setOnClickListener(v -> {
                if (getActivity() != null)
                    ((HomeActivityClient) getActivity()).prepareJobPostedFragment(Constants.DELIVERED_JOBS);
            });


//            getPostedJobsCounts();
//            getJobsOnTransitCount();
//            getDeliveredCount();

            getPostedJobs();
            postJobsLayout.setOnClickListener(v -> startActivity(new Intent(getActivity(), PostDeliveryJobActivity.class)));
            floatingActionButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), PostDeliveryJobActivity.class)));
        }
        return view;
    }

    private void getPostedJobsCounts() {
        String uri = Constants.BASE_URL + "api/posts/countpostedjobs/" + sharedPref.getLoggedInUserID();
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                postedJobsTxt.setText(response);
                postedJobsProgressBar.setVisibility(View.GONE);
                postedJobsTxt.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                postedJobsProgressBar.setVisibility(View.GONE);
                postedJobsTxt.setText("Network error");
                postedJobsTxt.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private void getJobsOnTransitCount() {
        String uri = Constants.BASE_URL + "api/posts/countclientjobsontransit/" + sharedPref.getLoggedInUserID();
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jobsTransit.setText(response);
                transitProgressBar.setVisibility(View.GONE);
                jobsTransit.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                transitProgressBar.setVisibility(View.GONE);
                jobsTransit.setText("Network error");
                jobsTransit.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }

    private void getDeliveredCount() {

        String uri = Constants.BASE_URL + "api/posts/countclientjobsdelivered/" + sharedPref.getLoggedInUserID();
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                deliveredJobs.setText(response);
                deliveredProgressBar.setVisibility(View.GONE);
                deliveredJobs.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                deliveredProgressBar.setVisibility(View.GONE);
                deliveredJobs.setText("Network error");
                deliveredJobs.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(myReq);
    }


    private void getPostedJobs() {
        String uri = Constants.BASE_URL + "api/posts/getpostedjobs/" + sharedPref.getLoggedInUserID();
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseDeliveryJobs(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                deliveredProgressBar.setVisibility(View.GONE);
                deliveredJobs.setText("Network error");
                deliveredJobs.setVisibility(View.VISIBLE);

                transitProgressBar.setVisibility(View.GONE);
                jobsTransit.setText("Network error");
                jobsTransit.setVisibility(View.VISIBLE);

                postedJobsProgressBar.setVisibility(View.GONE);
                postedJobsTxt.setText("Network error");
                postedJobsTxt.setVisibility(View.VISIBLE);

            }
        });

        if (getActivity() != null) {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(myReq);
        }
    }

    private void parseDeliveryJobs(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() == 0) {
                deliveredProgressBar.setVisibility(View.GONE);
                deliveredJobs.setText("0");
                deliveredJobs.setVisibility(View.VISIBLE);

                transitProgressBar.setVisibility(View.GONE);
                jobsTransit.setText("0");
                jobsTransit.setVisibility(View.VISIBLE);

                postedJobsProgressBar.setVisibility(View.GONE);
                postedJobsTxt.setText("0");
                postedJobsTxt.setVisibility(View.VISIBLE);
            } else {

                List<Job> jobsNotOnTransitOrDelivery = new ArrayList<>();
                List<Job> jobsOnTransit = new ArrayList<>();
                List<Job> jobsOnDelivery = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {

                    Job singleJob = new GsonBuilder().create().fromJson(jsonArray.get(i).toString(), Job.class);
                    String deliveryStatus = singleJob.getDelivery_status();
                    String bidStatus = singleJob.getBid_status();
                    if (deliveryStatus.equalsIgnoreCase("0") && bidStatus.equalsIgnoreCase("1")) {
                        //On transit
                        jobsOnTransit.add(singleJob);
                    } else if (deliveryStatus.equalsIgnoreCase("1") && bidStatus.equalsIgnoreCase("1")) {
                        //Delivered
                        jobsOnDelivery.add(singleJob);
                    } else {
                        jobsNotOnTransitOrDelivery.add(singleJob);
                    }

                }

                deliveredProgressBar.setVisibility(View.GONE);
                deliveredJobs.setText(String.valueOf(jobsOnDelivery.size()));
                deliveredJobs.setVisibility(View.VISIBLE);

                transitProgressBar.setVisibility(View.GONE);
                jobsTransit.setText(String.valueOf(jobsOnTransit.size()));
                jobsTransit.setVisibility(View.VISIBLE);

                postedJobsProgressBar.setVisibility(View.GONE);
                postedJobsTxt.setText(String.valueOf(jobsNotOnTransitOrDelivery.size()));
                postedJobsTxt.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            deliveredProgressBar.setVisibility(View.GONE);
            deliveredJobs.setText("Network error");
            deliveredJobs.setVisibility(View.VISIBLE);

            transitProgressBar.setVisibility(View.GONE);
            jobsTransit.setText("Network error");
            jobsTransit.setVisibility(View.VISIBLE);

            postedJobsProgressBar.setVisibility(View.GONE);
            postedJobsTxt.setText("Network error");
            postedJobsTxt.setVisibility(View.VISIBLE);
        }
    }


}
