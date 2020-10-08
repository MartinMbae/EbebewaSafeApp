package com.example.ebebewa.activities.post_job;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.example.ebebewa.R;
import com.example.ebebewa.adapters.HighLightArrayAdapter;
import com.example.ebebewa.models.Luggqage;
import com.example.ebebewa.models.Vehicle;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.SharedPref;

public class PostFragmentStep3 extends Fragment {

    private Spinner vehicle_type_spinner, nature_luggage_spinner;
    private TextView amountEdittext;
    private Button nextBtn, prevBtn;
    private ProgressBar amountProgressBar;
    private PostDeliveryJobActivity postDeliveryJobActivity;
    private SharedPref sharedPref;

    public PostFragmentStep3(PostDeliveryJobActivity postDeliveryJobActivity) {
        this.postDeliveryJobActivity = postDeliveryJobActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_job_step3, container, false);

        if (getActivity() != null) {

            sharedPref = new SharedPref(getActivity());
            vehicle_type_spinner = view.findViewById(R.id.vehicle_type_spinner);
            nature_luggage_spinner = view.findViewById(R.id.natureSpinner);
            nextBtn = view.findViewById(R.id.nextBtn);
            prevBtn = view.findViewById(R.id.prevBtn);
            amountEdittext = view.findViewById(R.id.amountEdittext);
            amountProgressBar = view.findViewById(R.id.amountProgressBar);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        ((PostDeliveryJobActivity) getActivity()).goToFragment(new PostFragmentStep4(postDeliveryJobActivity), 3);
                }
            });

            prevBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        ((PostDeliveryJobActivity) getActivity()).goToFragment(new PostFragmentStep2(postDeliveryJobActivity), 1);
                }
            });

            if (sharedPref.getLuggageNatureArrayList() != null) {
                setUpLuggageNatureSpinner();
            }
            if (sharedPref.getVehicleArrayList() != null) {
                setUpVehicleSpinner();
            }
            fetchAmount();
        }
        return view;
    }

    private void setUpLuggageNatureSpinner() {
        Gson gson = new Gson();
        String jsonLuggages = sharedPref.getLuggageNatureArrayList();
        Type type = new TypeToken<List<Luggqage>>() {
        }.getType();
        ArrayList<Luggqage> luggqageList = gson.fromJson(jsonLuggages, type);

        int selectedId = 0;
        ArrayList<String> luggageNatureNames = new ArrayList<>();
        final ArrayList<String> luggageNatureIds = new ArrayList<>();
        luggageNatureNames.add("Passenger");
        luggageNatureIds.add("1");
        for (int v = 0; v < luggqageList.size(); v++) {
            luggageNatureNames.add(luggqageList.get(v).getLuggage_nature());
            luggageNatureIds.add(luggqageList.get(v).getId());
            if (postDeliveryJobActivity.selectedLuggageNature != null) {
                if (luggqageList.get(v).getId().equals(postDeliveryJobActivity.selectedLuggageNature)) {
                    selectedId = v;
                }
            }
        }
        luggageNatureNames.add("Select Luggage Nature");
        final HighLightArrayAdapter hintAdapter = new HighLightArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, luggageNatureNames);
        nature_luggage_spinner.setAdapter(hintAdapter);
        if (postDeliveryJobActivity.selectedLuggageNature != null) {
            hintAdapter.setSelection(selectedId);
            nature_luggage_spinner.setSelection(selectedId);
        } else {
            nature_luggage_spinner.setSelection(hintAdapter.getCount());
        }

        nature_luggage_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < luggageNatureIds.size()) {
                    postDeliveryJobActivity.selectedLuggageNature = luggageNatureIds.get(position);
                    hintAdapter.setSelection(position);

                    fetchAmount();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void setUpVehicleSpinner() {
        Gson gson = new Gson();
        String jsonVehicles = sharedPref.getVehicleArrayList();
        Type type = new TypeToken<List<Vehicle>>() {
        }.getType();
        ArrayList<Vehicle> vehicleList = gson.fromJson(jsonVehicles, type);
        int selectedId = 0;
        ArrayList<String> vehicleNames = new ArrayList<>();
        final ArrayList<String> vehicleIds = new ArrayList<>();

        for (int v = 0; v < vehicleList.size(); v++) {
            vehicleNames.add(vehicleList.get(v).getType());
            vehicleIds.add(vehicleList.get(v).getId());
            if (postDeliveryJobActivity.selectedVehicleId != null) {
                if (vehicleList.get(v).getId().equals(postDeliveryJobActivity.selectedVehicleId)) {
                    selectedId = v;
                }
            }
        }
        vehicleNames.add("Select Vehicle Type");
        final HighLightArrayAdapter hintAdapter = new HighLightArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, vehicleNames);
        vehicle_type_spinner.setAdapter(hintAdapter);
        if (postDeliveryJobActivity.selectedVehicleId != null) {
            hintAdapter.setSelection(selectedId);
            vehicle_type_spinner.setSelection(selectedId);
        } else {
            vehicle_type_spinner.setSelection(hintAdapter.getCount());
        }
        vehicle_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < vehicleIds.size()) {
                    postDeliveryJobActivity.selectedVehicleId = vehicleIds.get(position);
                    hintAdapter.setSelection(position);

                    fetchAmount();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void fetchAmount() {
        if (postDeliveryJobActivity.selectedLuggageNature != null && postDeliveryJobActivity.selectedVehicleId != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("from_places", postDeliveryJobActivity.luggage_origin);
            jsonObject.addProperty("to_places", postDeliveryJobActivity.luggage_destination);
            jsonObject.addProperty("luggage_nature", postDeliveryJobActivity.selectedLuggageNature);
            jsonObject.addProperty("vehicle_type", postDeliveryJobActivity.selectedVehicleId);
            submitDataAmount(jsonObject);

        }


    }

    private void submitDataAmount(JsonObject jsonObject) {

        amountEdittext.setVisibility(View.GONE);
        amountProgressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceAmount service = retrofit.create(ApiServiceAmount.class);
        Call<JsonObject> call = service.postData(jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);
                        String distance = obj.getString("distance");
                        String amount_per_km = obj.getString("amount_per_km");
                        String total_cost = obj.getString("total_cost");
                        postDeliveryJobActivity.calculatedAmount = total_cost;
                        try {
                            float roundedAmount = Float.parseFloat(total_cost);
                            int amount = Math.round(roundedAmount);
                            total_cost = Constants.addCommaToNumber(amount);

                        } catch (Exception ignored) {

                        } finally {

                            amountEdittext.setText(total_cost);
                        }


                        amountEdittext.setVisibility(View.VISIBLE);
                        amountProgressBar.setVisibility(View.GONE);
                        nextBtn.setEnabled(true);


                    } catch (JSONException e) {
                        e.printStackTrace();

                        amountEdittext.setVisibility(View.VISIBLE);
                        amountProgressBar.setVisibility(View.GONE);

                        showSuccessfulDialog("Failed", "Something went wrong. Please try again " + e.getMessage());
                    }
                } else {

                    amountEdittext.setVisibility(View.VISIBLE);
                    amountProgressBar.setVisibility(View.GONE);
                    showSuccessfulDialog("Failed", "Something went wrong. Please try again ");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                amountEdittext.setVisibility(View.VISIBLE);
                amountProgressBar.setVisibility(View.GONE);
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

    private interface ApiServiceAmount {
        @POST("api/posts")
        Call<JsonObject> postData(@Body JsonObject body);
    }
}
