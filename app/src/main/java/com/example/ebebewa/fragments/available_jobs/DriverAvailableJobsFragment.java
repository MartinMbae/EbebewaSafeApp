package com.example.ebebewa.fragments.available_jobs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.example.ebebewa.R;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.Job;
import com.example.ebebewa.utils.SharedPref;

public class DriverAvailableJobsFragment extends Fragment {
    private SharedPref sharedPref;
    private HashMap<String, String> vehicleTypesHashMaps;
    private HashMap<String, String> luggageNatureHashMaps;
    private HashMap<String, String> appliedJobsHashMaps;
    private LinearLayout emptyAvailableJobLayout;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_available_jobs, container, false);
        if (getActivity() != null) {
            sharedPref = new SharedPref(getActivity());
            emptyAvailableJobLayout = view.findViewById(R.id.emptyJobsLayout);
            progressBar = view.findViewById(R.id.progress_bar);
            recyclerView = view.findViewById(R.id.recycler_view);
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyAvailableJobLayout.setVisibility(View.GONE);
            progressDialog = new ProgressDialog(getActivity());
            appliedJobsHashMaps = new HashMap<>();
            getVehicleTypes();
            getLuggageNature();
            getAppliedJobs();
        }
        return view;
    }
    private void getVehicleTypes() {

        if (getActivity() != null) {
            String uri = Constants.BASE_URL + "api/drivers/vehicletype";
            StringRequest myReq = new StringRequest(Request.Method.GET,
                    uri, this::parseVehicleTypesResponse, error -> {

            });
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(myReq);
        }
    }

    private void parseVehicleTypesResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);

            vehicleTypesHashMaps = new HashMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String type = jsonObject.getString("type");
//                String amountPerKm = jsonObject.getString("amount_per_km");
                vehicleTypesHashMaps.put(id, type);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAppliedJobs() {

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        if (getActivity() != null) {
            String uri = Constants.BASE_URL + "api/posts/getapplications/" + sharedPref.getLoggedInUserID();
            StringRequest myReq = new StringRequest(Request.Method.GET,
                    uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    parseAppliedJobs(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    emptyAvailableJobLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
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
                    String post = jsonObject.getString("post");
                    String status = jsonObject.getString("status");
                    appliedJobsHashMaps.put(post, status);
                }
            }
            getAvailableJobs();
        } catch (JSONException e) {
            e.printStackTrace();
            emptyAvailableJobLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void getAvailableJobs() {
        if (getActivity() != null) {
            String uri = Constants.BASE_URL + "api/posts/getjobs/" + sharedPref.getVehicleType();
            StringRequest myReq = new StringRequest(Request.Method.GET,
                    uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    parseDriverJobs(response);
                    progressBar.setVisibility(View.GONE);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressBar.setVisibility(View.GONE);
                    emptyAvailableJobLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            });

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(myReq);
        }
    }

    private void getLuggageNature() {
        if (getActivity() != null) {
            String uri = Constants.BASE_URL + "api/posts/getluggagenature";
            StringRequest myReq = new StringRequest(Request.Method.GET,
                    uri, this::parseLuggageResponse, error -> {
            });

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(myReq);
        }
    }

    private void parseLuggageResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);

            luggageNatureHashMaps = new HashMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                String luggage_nature = jsonObject.getString("luggage_nature");
//                String charge = jsonObject.getString("charge");
//                String status = jsonObject.getString("status");
                luggageNatureHashMaps.put(id, luggage_nature);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseDriverJobs(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() == 0) {
                emptyAvailableJobLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyAvailableJobLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                List<Job> jobList = Arrays.asList(new GsonBuilder().create().fromJson(jsonArray.toString(), Job[].class));
                AvailableJobAdapter availableJobAdapter = new AvailableJobAdapter(jobList, vehicleTypesHashMaps, luggageNatureHashMaps, appliedJobsHashMaps, this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(availableJobAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            emptyAvailableJobLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    void applyJob(String id) {
        progressDialog.setMessage("Applying job");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("driver", sharedPref.getLoggedInUserID());
        jsonObject.addProperty("user", sharedPref.getUserName());
        jsonObject.addProperty("post", id);
        submitData(jsonObject);

    }

    private void submitData(JsonObject jsonObject) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<JsonObject> call = service.postData(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NotNull Call<JsonObject> call, @NotNull retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);
                        String status = obj.getString("status");
                        String message = obj.getString("message");
                        if (status.equals("true")) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
        @POST("api/posts/application")
        Call<JsonObject> postData(@Body JsonObject body);
    }


}
