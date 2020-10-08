package com.example.ebebewa.fragments.client_jobs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import com.example.ebebewa.R;
import com.example.ebebewa.activities.HomeActivityClient;
import com.example.ebebewa.models.Luggqage;
import com.example.ebebewa.models.Vehicle;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.Job;
import com.example.ebebewa.utils.SharedPref;

public class AvailableDriversFragment extends Fragment {

    private SharedPref sharedPref;
    private HashMap<String, String> vehicleTypesHashMaps;
    private HashMap<String, String> luggageNatureHashMaps;
    private LinearLayout emptyAvailableJobLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private TextView emptyMessage;

    private String category;

    public AvailableDriversFragment(String category) {
        this.category = category;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_job_list, container, false);
        if (getActivity() != null) {
            sharedPref = new SharedPref(getActivity());
            emptyAvailableJobLayout = view.findViewById(R.id.emptyJobsLayout);
            recyclerView = view.findViewById(R.id.recycler_view);
            progressBar = view.findViewById(R.id.progress_bar);
            emptyMessage = view.findViewById(R.id.emptyMessage);
            progressDialog = new ProgressDialog(getActivity());
            vehicleTypesHashMaps = new HashMap<>();
            luggageNatureHashMaps = new HashMap<>();

            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyAvailableJobLayout.setVisibility(View.GONE);
            if (sharedPref.getLuggageNatureArrayList() != null) {
                getLuggageNatureFromSharedPreferences();
            }
            if (sharedPref.getVehicleArrayList() != null) {
                getVehicleTypesFromSharedPreferences();
            }

            getPostedJobs();

        }
        return view;
    }

    private void getVehicleTypesFromSharedPreferences() {

        Gson gson = new Gson();
        String jsonVehicles = sharedPref.getVehicleArrayList();
        Type type = new TypeToken<List<Vehicle>>() {
        }.getType();
        ArrayList<Vehicle> vehicleList = gson.fromJson(jsonVehicles, type);
        for (int v = 0; v < vehicleList.size(); v++) {
            String vehicleId = vehicleList.get(v).getId();
            String vehicleName = vehicleList.get(v).getType();
            vehicleTypesHashMaps.put(vehicleId, vehicleName);
        }

    }

    private void getLuggageNatureFromSharedPreferences() {
        Gson gson = new Gson();
        String jsonLuggages = sharedPref.getLuggageNatureArrayList();
        Type type = new TypeToken<List<Luggqage>>() {
        }.getType();
        ArrayList<Luggqage> luggqageList = gson.fromJson(jsonLuggages, type);
        for (int v = 0; v < luggqageList.size(); v++) {
            String id = luggqageList.get(v).getId();
            String luggage_nature = luggqageList.get(v).getLuggage_nature();
            luggageNatureHashMaps.put(id, luggage_nature);

        }
    }

    private void getPostedJobs() {
        String uri = Constants.BASE_URL + "api/posts/getpostedjobs/" + sharedPref.getLoggedInUserID();
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseDeliveryJobs(response);
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                emptyAvailableJobLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);

                if (error.toString().contains("imeout") || error.toString().contains("NoConnectionError")) {
                    emptyMessage.setText("Please check your internet connection and try again.");
                }
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
                emptyAvailableJobLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyAvailableJobLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                List<Job> jobsNotOnTransitOrDelivery = new ArrayList<>();
                List<Job> jobsOnTransit = new ArrayList<>();
                List<Job> jobsOnDelivery = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {

                    Job singleJob = new GsonBuilder().create().fromJson(jsonArray.get(i).toString(), Job.class);

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.d("rffijie", jsonObject.toString());


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
                Collections.reverse(jobsNotOnTransitOrDelivery);
                Collections.reverse(jobsOnTransit);
                Collections.reverse(jobsOnDelivery);

                AvailableDriversAdapter availableDriversAdapter;
                switch (category) {
                    case Constants.DELIVERED_JOBS:
                        if (jobsOnDelivery.size() == 0) {
                            emptyAvailableJobLayout.setVisibility(View.VISIBLE);
                            emptyMessage.setText("We could not find any delivered job for you.");
                            recyclerView.setVisibility(View.GONE);
                        }

                        availableDriversAdapter = new AvailableDriversAdapter(getActivity(), jobsOnDelivery, vehicleTypesHashMaps, luggageNatureHashMaps, this);
                        break;

                    case Constants.TRANSIT_JOBS:
                        if (jobsOnTransit.size() == 0) {
                            emptyAvailableJobLayout.setVisibility(View.VISIBLE);
                            emptyMessage.setText("We could not find any of your applied jobs that is on transit.");
                            recyclerView.setVisibility(View.GONE);
                        }

                        availableDriversAdapter = new AvailableDriversAdapter(getActivity(), jobsOnTransit, vehicleTypesHashMaps, luggageNatureHashMaps, this);

                        break;

                    default:
                        if (jobsNotOnTransitOrDelivery.size() == 0) {
                            emptyAvailableJobLayout.setVisibility(View.VISIBLE);
                            emptyMessage.setText("We could not find any of your jobs that is currently under review.");
                            recyclerView.setVisibility(View.GONE);
                        }


                        availableDriversAdapter = new AvailableDriversAdapter(getActivity(), jobsNotOnTransitOrDelivery, vehicleTypesHashMaps, luggageNatureHashMaps, this);

                        break;
                }

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(availableDriversAdapter);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            emptyAvailableJobLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    void confirmDeletePos(final String id) {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Are you sure you want to delete this post?");
            builder.setCancelable(false);
            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    deletePost(id);
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    private void deletePost(String id) {
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Deleting post");


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<JsonObject> call = service.deletePost(id);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(@NotNull Call<JsonObject> call, @NotNull retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {

                    String response_string = null;
                    if (response.body() != null) {
                        response_string = response.body().toString();
                    }
                    try {
                        JSONObject obj;
                        if (response_string != null) {
                            obj = new JSONObject(response_string);
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            if (status.equalsIgnoreCase("true")) {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), HomeActivityClient.class);
                                intent.putExtra("POSTED", 1);
                                startActivity(intent);
                                if (getActivity() != null)
                                    getActivity().finish();
                            } else {
                                showSuccessfulDialog("Oops", message);
                            }
                        } else {
                            showSuccessfulDialog("Failed", "Something went wrong. Please try again.");
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
            public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());

            }

        });
    }

    private void showSuccessfulDialog(String title, String message) {
        if (getActivity() != null) {
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
    }

    private interface ApiService {
        @Headers({"Content-Type: application/json"})
        @DELETE("api/posts/deletePost/{id}")
        Call<JsonObject> deletePost(@Path("id") String id);
    }


}
