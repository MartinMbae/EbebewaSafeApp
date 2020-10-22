package com.example.ebebewa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ebebewa.activities.HomeActivityClient;
import com.example.ebebewa.activities.LoginActivity;
import com.example.ebebewa.activities.post_job.PostDeliveryJobActivity;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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

public class LocationLuggageDescActivity extends AppCompatActivity {


    SharedPref sharedPref;
    private EditText locationDesc, luggageDesc;
    private Button postBtn, prevBtn;
    private TextView errorTextView;
    private ProgressDialog progressDialog;

    public String pickUpDate, pickUpTime, luggage_origin, luggage_destination, luggageWeight, selectedVehicleId, selectedLuggageNature, calculatedAmount, sendNumber, receiverNumber, locationDescription, luggageDescription;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luggage_location_desc);


        locationDesc = findViewById(R.id.location_desc);
        luggageDesc = findViewById(R.id.luggage_desc);
        postBtn = findViewById(R.id.postBtn);
        prevBtn = findViewById(R.id.prevBtn);
        errorTextView = findViewById(R.id.errorMessage);


        Bundle bundle = getIntent().getExtras();
        luggage_destination = bundle.getString("destination");
        luggage_origin = bundle.getString("origin");
        sendNumber = bundle.getString("sender");
        receiverNumber = bundle.getString("receiver");
        pickUpDate = bundle.getString("date");
        pickUpTime = bundle.getString("time");
        selectedLuggageNature = bundle.getString("luggage_nature");
        selectedVehicleId = bundle.getString("vehicle_id");
        luggageWeight = bundle.getString("luggage_weight");
        calculatedAmount = bundle.getString("amount");


        progressDialog = new ProgressDialog(LocationLuggageDescActivity.this);

        sharedPref = new SharedPref(LocationLuggageDescActivity.this);
        setTextWatchers(locationDesc);
        setTextWatchers(luggageDesc);

        locationDesc.setText(locationDescription);
        luggageDesc.setText(luggageDescription);

        prevBtn.setOnClickListener(v -> {

            locationDescription = locationDesc.getText().toString();
            luggageDescription = luggageDesc.getText().toString();


        });
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String locationDescription = locationDesc.getText().toString();
                String luggageDescription = luggageDesc.getText().toString();


                Intent transportIntent = new Intent(LocationLuggageDescActivity.this, SummaryActivity.class);
                transportIntent.putExtra("destination", luggage_destination);
                transportIntent.putExtra("origin",luggage_origin);
                transportIntent.putExtra("sender",sendNumber);
                transportIntent.putExtra("receiver", receiverNumber);
                transportIntent.putExtra("date", pickUpDate);
                transportIntent.putExtra("time", pickUpTime);
                transportIntent.putExtra("luggage_nature", selectedLuggageNature);
                transportIntent.putExtra("vehicle_id", selectedVehicleId);
                transportIntent.putExtra("luggage_weight", luggageWeight);
                transportIntent.putExtra("amount", calculatedAmount);
                transportIntent.putExtra("location_desc", locationDescription);
                transportIntent.putExtra("luggage_desc", luggageDescription);
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

    private void setTextWatchers(EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAccuracy();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void checkAccuracy() {

        String locationD = locationDesc.getText().toString();
        String luggageD = luggageDesc.getText().toString();

        String errorMessage;
        if (TextUtils.isEmpty(locationD)) {
            errorMessage = "Describe your location";
        } else if (TextUtils.isEmpty(luggageD)) {
            errorMessage = "Describe the luggage";
        } else {
            errorMessage = "true";
        }

        if (errorMessage.equalsIgnoreCase("true")) {
            errorTextView.setVisibility(View.INVISIBLE);
            postBtn.setEnabled(true);
        } else {
            errorTextView.setText(errorMessage);
            errorTextView.setVisibility(View.VISIBLE);
            postBtn.setEnabled(false);
        }
    }

    public void toPreviousPage(View view) {
        finish();
    }
}
