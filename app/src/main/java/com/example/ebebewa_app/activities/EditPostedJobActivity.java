package com.example.ebebewa_app.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import com.example.ebebewa_app.R;
import com.example.ebebewa_app.adapters.HighLightArrayAdapter;
import com.example.ebebewa_app.models.Luggqage;
import com.example.ebebewa_app.models.Vehicle;
import com.example.ebebewa_app.utils.Constants;
import com.example.ebebewa_app.utils.Job;
import com.example.ebebewa_app.utils.SharedPref;

public class EditPostedJobActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, DatePickerDialog.OnCancelListener {

    Job job;
    SharedPref sharedPref;
    private TextView selectDate, total_costTextView;
    private EditText senderEdt, receiverEdt, locationDesc, luggageDesc;
    private Spinner vehicle_type_spinner, luggage_nature_spinner;
    private SimpleDateFormat simpleDateFormat;
    private String updatedPickUpDate, updatedOrigin, updatedDestination, updatedAmount, updatedSenderNumber, updatedReceiver, updatedLocationDesc, updatedLuggageDesc, updatedVehicleId, updatedLuggageNatureId;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_posted_job);
        Gson gson = new Gson();
        job = gson.fromJson(getIntent().getStringExtra("job"), Job.class);
        selectDate = findViewById(R.id.selectDate);
        senderEdt = findViewById(R.id.sender_phone);
        receiverEdt = findViewById(R.id.receiver_phone);
        locationDesc = findViewById(R.id.location_desc);
        luggageDesc = findViewById(R.id.luggage_desc);
        vehicle_type_spinner = findViewById(R.id.vehicle_type_spinner);
        luggage_nature_spinner = findViewById(R.id.natureSpinner);
        total_costTextView = findViewById(R.id.total_cost);

        progressDialog = new ProgressDialog(EditPostedJobActivity.this);

        updatedPickUpDate = job.getDue_date();
        updatedOrigin = job.getOrigin_place();
        updatedDestination = job.getDestination_place();
        updatedAmount = job.getAmount();
        updatedSenderNumber = job.getOrigin_contact();
        updatedReceiver = job.getDestination_contact();
        updatedLocationDesc = job.getLocation_description();
        updatedLuggageDesc = job.getDescription();
        updatedVehicleId = job.getVehicle_type();
        updatedAmount = job.getAmount();
        updatedLuggageNatureId = job.getLuggage_nature();


        sharedPref = new SharedPref(EditPostedJobActivity.this);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date c = Calendar.getInstance().getTime();
                int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(c));
                int month = Integer.parseInt(new SimpleDateFormat("MM").format(c));
                int day = Integer.parseInt(new SimpleDateFormat("dd").format(c)) + 1;

                try {
                    Date theDate = simpleDateFormat.parse(updatedPickUpDate);
                    if (theDate != null) {
                        Calendar myCal = new GregorianCalendar();
                        myCal.setTime(theDate);
                        day = myCal.get(Calendar.DAY_OF_MONTH);
                        month = myCal.get(Calendar.MONTH) + 1;
                        year = myCal.get(Calendar.YEAR);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    showDate(year, month, day, R.style.DatePickerSpinner);
                }
            }
        });


        Places.initialize(EditPostedJobActivity.this, getString(R.string.google_maps_key));

        AutocompleteSupportFragment autocompleteFragmentOrigin = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_origin);

        if (autocompleteFragmentOrigin != null) {
            autocompleteFragmentOrigin.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS));
            autocompleteFragmentOrigin.setCountry("KE");
            autocompleteFragmentOrigin.setText(updatedOrigin);
            autocompleteFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    if (place != null && place.getName() != null) {
                        if (!place.getName().equals(updatedOrigin)) {
                            String name =  place.getName()+", "+place.getAddress();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    autocompleteFragmentOrigin.setText(name);
                                }
                            },300);

                            updatedOrigin = name;
                            fetchAmount();
                        }
                    }
                }

                @Override
                public void onError(Status status) {
                }
            });

        }

        AutocompleteSupportFragment autocompleteFragmentDestination = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_destination);

        if (autocompleteFragmentDestination != null) {
            autocompleteFragmentDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS));
            autocompleteFragmentDestination.setCountry("KE");
            autocompleteFragmentDestination.setText(updatedDestination);
            autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    if (place != null && place.getName() != null) {


                        String name =  place.getName()+", "+place.getAddress();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                autocompleteFragmentDestination.setText(name);
                            }
                        },300);

                        if (!place.getName().equals(updatedOrigin)) {
                            updatedDestination =name;
                            fetchAmount();
                        }
                    }
                }

                @Override
                public void onError(Status status) {
                }
            });
        }

        fillFields();
    }

    private void fillFields() {

        if (sharedPref.getVehicleArrayList() != null) {
            setUpVehicleSpinner();
        }
        if (sharedPref.getLuggageNatureArrayList() != null) {
            setUpLuggageNatureSpinner();
        }
        selectDate.setText(updatedPickUpDate);
        senderEdt.setText(updatedSenderNumber);
        receiverEdt.setText(updatedReceiver);
        locationDesc.setText(updatedLocationDesc);
        luggageDesc.setText(updatedLuggageDesc);

        String amountString = updatedAmount;
        try {
            int am = Integer.parseInt(amountString);
            amountString = Constants.addCommaToNumber(am);
        } catch (Exception ignored) {

        } finally {

            total_costTextView.setText(amountString);
        }

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
            if (vehicleList.get(v).getId().equals(updatedVehicleId)) {
                selectedId = v;
            }
        }
        vehicleNames.add("Select Vehicle Type");
        final HighLightArrayAdapter hintAdapter = new HighLightArrayAdapter(EditPostedJobActivity.this, android.R.layout.simple_list_item_1, vehicleNames);
        vehicle_type_spinner.setAdapter(hintAdapter);
        vehicle_type_spinner.setSelection(selectedId);
        hintAdapter.setSelection(selectedId);
        vehicle_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < vehicleIds.size()) {
                    if (!vehicleIds.get(position).equals(updatedVehicleId)) {
                        updatedVehicleId = vehicleIds.get(position);
                        hintAdapter.setSelection(position);
                        fetchAmount();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
            if (luggqageList.get(v).getId().equals(updatedLuggageNatureId)) {
                selectedId = v;
            }
        }
        luggageNatureNames.add("Select Luggage Nature");
        final HighLightArrayAdapter hintAdapter = new HighLightArrayAdapter(EditPostedJobActivity.this, android.R.layout.simple_list_item_1, luggageNatureNames);
        luggage_nature_spinner.setAdapter(hintAdapter);
        luggage_nature_spinner.setSelection(selectedId);
        hintAdapter.setSelection(selectedId);
        luggage_nature_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < luggageNatureIds.size()) {
                    if (!luggageNatureIds.get(position).equals(updatedLuggageNatureId)) {
                        updatedLuggageNatureId = luggageNatureIds.get(position);
                        hintAdapter.setSelection(position);
                        fetchAmount();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        selectDate.setText(simpleDateFormat.format(calendar.getTime()));
    }

    @VisibleForTesting
    private void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(EditPostedJobActivity.this)
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .defaultDate(year, monthOfYear - 1, dayOfMonth)
                .build()
                .show();
    }

    public void fetchAmount() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("from_places", updatedOrigin);
        jsonObject.addProperty("to_places", updatedDestination);
        jsonObject.addProperty("luggage_nature", updatedLuggageNatureId);
        jsonObject.addProperty("vehicle_type", updatedVehicleId);
        submitDataAmount(jsonObject);
    }

    private void submitDataAmount(JsonObject jsonObject) {

        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Fetching new delivery cost.");
        progressDialog.setCancelable(false);
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceAmount service = retrofit.create(ApiServiceAmount.class);
        Call<JsonObject> call = service.postData(jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);
                        String distance = obj.getString("distance");
                        String amount_per_km = obj.getString("amount_per_km");
                        String total_cost = obj.getString("total_cost");
                        updatedAmount = total_cost;
                        try {
                            float roundedAmount = Float.parseFloat(total_cost);
                            int amount = Math.round(roundedAmount);
                            total_cost = Constants.addCommaToNumber(amount);

                        } catch (Exception ignored) {

                        } finally {
                            total_costTextView.setText(total_cost);
                        }

//                        showSuccessfulDialogFetchedAmount("Amount Changed", "You will be charged " + total_cost + " for this job. \n\n Do you wish to proceed with this change", total_cost);

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
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());
                progressDialog.dismiss();
            }

        });
    }

    public void update(View v) {


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", job.getId());
        jsonObject.addProperty("due_date", updatedPickUpDate);
        jsonObject.addProperty("from_places", updatedOrigin);
        jsonObject.addProperty("origin_contact", updatedSenderNumber);
        jsonObject.addProperty("to_places", updatedDestination);
        jsonObject.addProperty("destination_contact", updatedReceiver);
        jsonObject.addProperty("luggage_nature", updatedLuggageNatureId);
        jsonObject.addProperty("vehicle_type", updatedVehicleId);
        jsonObject.addProperty("amount", updatedAmount);
        jsonObject.addProperty("location_description", updatedLocationDesc);
        jsonObject.addProperty("description", updatedLuggageDesc);

        submitData(jsonObject);

    }

    private void submitData(JsonObject jsonObject) {
        progressDialog.setMessage("Updating");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<JsonObject> call = service.updateUser(jsonObject);
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
                        if (status.equalsIgnoreCase("true")) {
                            Toast.makeText(EditPostedJobActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditPostedJobActivity.this, HomeActivityClient.class);
                            intent.putExtra("POSTED", 1);
                            startActivity(intent);
                            finish();
                        } else {
                            showSuccessfulDialog("Oops", message);
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
//                alertDialog.dismiss();
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());
                progressDialog.dismiss();
            }

        });
    }

    void showSuccessfulDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(EditPostedJobActivity.this);
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

//    void showSuccessfulDialogFetchedAmount(String title, String message, final String total_cost) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(EditPostedJobActivity.this);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.setCancelable(false);
//        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//            }
//        });
//
//        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                update(total_cost);
//            }
//        });
//
//        AlertDialog alert = builder.create();
//        alert.show();
//
//    }

    private interface ApiServiceAmount {
        @POST("api/posts")
        Call<JsonObject> postData(@Body JsonObject body);
    }

    private interface ApiService {
        @Headers({"Content-Type: application/json"})
        @PUT("api/posts/updatejob")
        Call<JsonObject> updateUser(@Body JsonObject body);
    }

}
