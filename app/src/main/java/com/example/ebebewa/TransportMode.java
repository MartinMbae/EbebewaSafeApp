package com.example.ebebewa;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ebebewa.activities.ClientWelcomeActivity;
import com.example.ebebewa.activities.HomeActivityClient;
import com.example.ebebewa.activities.post_job.PostDeliveryJobActivity;
import com.example.ebebewa.activities.post_job.PostFragmentStep2;
import com.example.ebebewa.activities.post_job.PostFragmentStep3;
import com.example.ebebewa.activities.post_job.PostFragmentStep4;
import com.example.ebebewa.adapters.HighLightArrayAdapter;
import com.example.ebebewa.models.Luggqage;
import com.example.ebebewa.models.Vehicle;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.SharedPref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class TransportMode extends AppCompatActivity {


    private Spinner vehicle_type_spinner, nature_luggage_spinner;
    private TextView amountEdittext;
    private Button nextBtn, prevBtn;
    private ProgressBar amountProgressBar;
    private SharedPref sharedPref;

    private String selectedLuggageNature, selectedVehicleId;

    private String luggage_origin, luggage_destination, calculatedAmount,sendNumber, receiverNumber,pickUpDate, pickUpTime;

    RadioGroup radioGroup;
    String luggageWeight;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_mode);

        sharedPref = new SharedPref(TransportMode.this);
        vehicle_type_spinner = findViewById(R.id.vehicle_type_spinner);
        nature_luggage_spinner = findViewById(R.id.natureSpinner);
        nextBtn = findViewById(R.id.nextBtn);
        prevBtn = findViewById(R.id.prevBtn);
        amountEdittext = findViewById(R.id.amountEdittext);
        amountProgressBar = findViewById(R.id.amountProgressBar);
        radioGroup = findViewById(R.id.radioGroupWeight);


        Bundle bundle = getIntent().getExtras();
        luggage_destination = bundle.getString("destination");
        luggage_origin = bundle.getString("origin");
        sendNumber = bundle.getString("sender");
        receiverNumber= bundle.getString("receiver");
        pickUpDate= bundle.getString("date");
        pickUpTime= bundle.getString("time");

        progressDialog = new ProgressDialog(TransportMode.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setTitle("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();


        fetchAmount();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int radioButtonID = radioGroup.getCheckedRadioButtonId();

                RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonID);

                luggageWeight = (String) radioButton.getText();

                fetchAmount();
            }
        });

        getVehicleTypes();
        getLuggageNature();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent location_luggage_intent = new Intent(TransportMode.this, LocationLuggageDescActivity.class);
                location_luggage_intent.putExtra("destination", luggage_destination);
                location_luggage_intent.putExtra("origin",luggage_origin);
                location_luggage_intent.putExtra("sender",sendNumber);
                location_luggage_intent.putExtra("receiver", receiverNumber);
                location_luggage_intent.putExtra("date", pickUpDate);
                location_luggage_intent.putExtra("time", pickUpTime);
                location_luggage_intent.putExtra("luggage_nature", selectedLuggageNature);
                location_luggage_intent.putExtra("vehicle_id", selectedVehicleId);
                location_luggage_intent.putExtra("luggage_weight", luggageWeight);
                location_luggage_intent.putExtra("amount", calculatedAmount);
                startActivity(location_luggage_intent);
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

    private void setUpLuggageNatureSpinner() {
        Gson gson = new Gson();
        String jsonLuggages = sharedPref.getLuggageNatureArrayList();
        Type type = new TypeToken<List<Luggqage>>() {
        }.getType();
        ArrayList<Luggqage> luggqageList = gson.fromJson(jsonLuggages, type);

        int selectedId = 0;
        ArrayList<String> luggageNatureNames = new ArrayList<>();
        final ArrayList<String> luggageNatureIds = new ArrayList<>();
        for (int v = 0; v < luggqageList.size(); v++) {
            luggageNatureNames.add(luggqageList.get(v).getLuggage_nature());
            luggageNatureIds.add(luggqageList.get(v).getId());
            if (selectedLuggageNature != null) {
                if (luggqageList.get(v).getId().equals(selectedLuggageNature)) {
                    selectedId = v;
                }
            }
        }
        luggageNatureNames.add("Select Luggage Nature");
        final HighLightArrayAdapter hintAdapter = new HighLightArrayAdapter(TransportMode.this, android.R.layout.simple_list_item_1, luggageNatureNames);
        nature_luggage_spinner.setAdapter(hintAdapter);
        if (selectedLuggageNature != null) {
            hintAdapter.setSelection(selectedId);
            nature_luggage_spinner.setSelection(selectedId);
        } else {
            nature_luggage_spinner.setSelection(hintAdapter.getCount());
        }

        nature_luggage_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < luggageNatureIds.size()) {
                    selectedLuggageNature = luggageNatureIds.get(position);
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
            if (selectedVehicleId != null) {
                if (vehicleList.get(v).getId().equals(selectedVehicleId)) {
                    selectedId = v;
                }
            }
        }
        vehicleNames.add("Select Vehicle Type");
        final HighLightArrayAdapter hintAdapter = new HighLightArrayAdapter(TransportMode.this, android.R.layout.simple_list_item_1, vehicleNames);
        vehicle_type_spinner.setAdapter(hintAdapter);
        if (selectedVehicleId != null) {
            hintAdapter.setSelection(selectedId);
            vehicle_type_spinner.setSelection(selectedId);
        } else {
            vehicle_type_spinner.setSelection(hintAdapter.getCount());
        }
        vehicle_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < vehicleIds.size()) {
                    selectedVehicleId = vehicleIds.get(position);
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
        if (selectedLuggageNature != null && selectedVehicleId != null && luggageWeight != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("from_places", luggage_origin);
            jsonObject.addProperty("to_places", luggage_destination);
            jsonObject.addProperty("luggage_nature", selectedLuggageNature);
            jsonObject.addProperty("vehicle_type", selectedVehicleId);
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
                        calculatedAmount = total_cost;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(TransportMode.this);
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


    private void getVehicleTypes() {

        String uri = Constants.BASE_URL + "api/drivers/vehicletype";
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, this::parseVehicleTypesResponse, error -> {
        });

        RequestQueue queue = Volley.newRequestQueue(TransportMode.this);
        queue.add(myReq);
    }

    private void parseVehicleTypesResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);

            ArrayList<Vehicle> vehicleArrayList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String type = jsonObject.getString("type");
                String amountPerKm = jsonObject.getString("amount_per_km");
                vehicleArrayList.add(new Vehicle(id, type, amountPerKm));
            }
            Gson gson = new Gson();
            String jsonVehicles = gson.toJson(vehicleArrayList);
            sharedPref.setVehicleArrayList(jsonVehicles);

            if (sharedPref.getVehicleArrayList() != null) {
                setUpVehicleSpinner();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();
    }

    private void getLuggageNature() {
        String uri = Constants.BASE_URL + "api/posts/getluggagenature";
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, this::parseLuggageResponse, error -> {
        });
        RequestQueue queue = Volley.newRequestQueue(TransportMode.this);
        queue.add(myReq);
    }

    private void parseLuggageResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            ArrayList<Luggqage> luggqageArrayList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                String luggage_nature = jsonObject.getString("luggage_nature");
                String charge = jsonObject.getString("charge");
                String status = jsonObject.getString("status");
                luggqageArrayList.add(new Luggqage(id, luggage_nature, charge, status));
            }

            Gson gson = new Gson();
            String jsonLuggages = gson.toJson(luggqageArrayList);
            sharedPref.setLuggageNatureArrayList(jsonLuggages);

            if (sharedPref.getLuggageNatureArrayList() != null) {
                setUpLuggageNatureSpinner();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();

    }
    public void toPreviousPage(View view) {
        finish();
    }
}
