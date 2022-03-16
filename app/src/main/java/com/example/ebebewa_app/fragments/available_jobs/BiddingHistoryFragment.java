package com.example.ebebewa_app.fragments.available_jobs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import com.example.ebebewa_app.R;
import com.example.ebebewa_app.utils.Constants;
import com.example.ebebewa_app.utils.SharedPref;

/**
 * Created by Martin Mbae on 23,June,2020.
 */


public class BiddingHistoryFragment extends Fragment {

    private SharedPref sharedPref;
    private LinearLayout emptyAvailableJobLayout;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bidding_fragment, container, false);
        if (getActivity() != null) {
            sharedPref = new SharedPref(getActivity());
            emptyAvailableJobLayout = view.findViewById(R.id.emptyJobsLayout);
            progressBar = view.findViewById(R.id.progress_bar);
            recyclerView = view.findViewById(R.id.recycler_view);
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyAvailableJobLayout.setVisibility(View.GONE);

            progressDialog = new ProgressDialog(getActivity());

            getAppliedJobs();
        }
        return view;
    }


    private void getAppliedJobs() {

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        if (getActivity() != null) {
            String uri = Constants.BASE_URL + "api/posts/getapplications/" + sharedPref.getLoggedInUserID();
            StringRequest myReq = new StringRequest(Request.Method.GET,
                    uri, response -> {
                parseAppliedJobs(response);
                progressBar.setVisibility(View.GONE);
            }, error -> {
                progressBar.setVisibility(View.GONE);
                emptyAvailableJobLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            });

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(myReq);
        }
    }

    private void parseAppliedJobs(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() == 0) {
                emptyAvailableJobLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyAvailableJobLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.d("ffffff", jsonObject.toString());
                }

                List<BidJob> bidJobs = Arrays.asList(new GsonBuilder().create().fromJson(jsonArray.toString(), BidJob[].class));
                Collections.reverse(bidJobs);
                BiddingJobAdapter biddingJobAdapter = new BiddingJobAdapter(bidJobs, getActivity(), this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(biddingJobAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            emptyAvailableJobLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    public void showClientInformation(String post_id, boolean displayClientInformation, String pickUpDateString, String pickupTownString, String destinationTownString, String amountString, String luggageDescString, String locationDescString) {

        if (getActivity() != null) {
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.more_info_dialog_bid_jobs);

            TextView pickUpDate = dialog.findViewById(R.id.pickUpDate);
            TextView pickUpTown = dialog.findViewById(R.id.pickUpTown);
            TextView destinationTown = dialog.findViewById(R.id.destinationTown);
            TextView amount = dialog.findViewById(R.id.amount);
            TextView luggageDesc = dialog.findViewById(R.id.luggageDesc);
            TextView locationDesc = dialog.findViewById(R.id.locationDesc);
            TextView postedBy = dialog.findViewById(R.id.postedBy);
            TextView phoneNumber = dialog.findViewById(R.id.phoneNumber);
            TextView pickUpPlaceContact = dialog.findViewById(R.id.pickUpPlaceContact);
            TextView destinationPlaceContact = dialog.findViewById(R.id.destinationPlaceContact);

            try {
                int amountInt = Integer.parseInt(amountString);
                amountString = Constants.addCommaToNumber(amountInt);
            } catch (Exception ignored) {
            }

            pickUpDate.setText(pickUpDateString);
            pickUpTown.setText(pickupTownString);
            destinationTown.setText(destinationTownString);
            amount.setText(amountString);
            luggageDesc.setText(luggageDescString);
            locationDesc.setText(locationDescString);
            LinearLayout clientInfoLayout = dialog.findViewById(R.id.clientInfoLayout);
            LinearLayout progress_barLayout = dialog.findViewById(R.id.progress_barLayout);
            LinearLayout clientInfoInnerLayout = dialog.findViewById(R.id.clientInfoInnerLayout);
            Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            if (displayClientInformation) {
                clientInfoLayout.setVisibility(View.VISIBLE);
                getClientDetails(post_id, progress_barLayout, clientInfoInnerLayout, postedBy, phoneNumber, pickUpPlaceContact, destinationPlaceContact);
            } else {
                clientInfoLayout.setVisibility(View.GONE);
            }

            dialog.show();

        }
    }

    private void getClientDetails(String post_id, LinearLayout progress_barLayout, LinearLayout clientInfoInnerLayout, TextView postedBy, TextView phoneNumber, TextView pickUpPlaceContact, TextView destinationPlaceContact) {

        if (getActivity() != null) {
            String uri = Constants.BASE_URL + "api/posts/getclientdetails/" + post_id;
            StringRequest myReq = new StringRequest(Request.Method.GET,
                    uri, response -> {
                parseClientDetails(response, progress_barLayout, clientInfoInnerLayout, postedBy, phoneNumber, pickUpPlaceContact, destinationPlaceContact);
            }, error -> {
            });

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(myReq);
        }
    }

    private void parseClientDetails(String response, LinearLayout progress_barLayout, LinearLayout clientInfoInnerLayout, TextView postedBy, TextView phoneNumber, TextView pickUpPlaceContact, TextView destinationPlaceContact) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            String first_name = jsonObject.getString("first_name");
            String last_name = jsonObject.getString("last_name");
            String origin_contact = jsonObject.getString("origin_contact");
            String destination_contact = jsonObject.getString("destination_contact");
            String phone = jsonObject.getString("phone");
            postedBy.setText(MessageFormat.format("{0} {1}", first_name, last_name));
            phoneNumber.setText(phone);
            pickUpPlaceContact.setText(origin_contact);
            destinationPlaceContact.setText(destination_contact);
            progress_barLayout.setVisibility(View.GONE);
            clientInfoInnerLayout.setVisibility(View.VISIBLE);

            phoneNumber.setOnClickListener(v -> {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(callIntent);
            });
            pickUpPlaceContact.setOnClickListener(v -> {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + origin_contact));
                startActivity(callIntent);
            });
            destinationPlaceContact.setOnClickListener(v -> {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + destination_contact));
                startActivity(callIntent);
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void confirmDelivery(JsonObject jsonObject) {
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceDeliveryConfirmation service = retrofit.create(ApiServiceDeliveryConfirmation.class);
        Call<JsonObject> call = service.postData(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);
                        String status = obj.getString("status");
                        String message = obj.getString("message");
                        if (status.equals("true")) {
                            Toast.makeText(getActivity(), "You have confirmed the delivery of this luggage.", Toast.LENGTH_SHORT).show();
                            showSuccessfulDialog("Success", "You have confirmed that you have delivered this luggage.\n\nPayments will be made to you once the client confirms that they have received this luggage.\n\nThank you for using " + getString(R.string.app_name));
                            getAppliedJobs();
                        } else {
                            showSuccessfulDialog("Oops!", message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showSuccessfulDialog("Failed", "Something went wrong. Please try again " + e.getMessage());
                    }

                } else {
                    showSuccessfulDialog("Failed", "Something went wrong. Please try again ");

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());
            }

        });
    }

    void showSuccessfulDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }


    private interface ApiService {
        @PUT("api/posts/driverconfirmation")
        Call<JsonObject> postData(@Body JsonObject body);
    }


    private interface ApiServiceDeliveryConfirmation {
        @PUT("api/posts/finaldriverdeliveryconfirmation")
        Call<JsonObject> postData(@Body JsonObject body);
    }

}
