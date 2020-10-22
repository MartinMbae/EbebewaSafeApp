package com.example.ebebewa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.ebebewa.activities.HomeActivityClient;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.SharedPref;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class SummaryActivity extends AppCompatActivity {

    AppCompatButton buttonPost;
    TextView edtFrom, edtTo, edtDate, edtAmount;

    private ProgressDialog progressDialog;

    public String pickUpDate, pickUpTime, luggage_origin, luggage_destination, luggageWeight, selectedVehicleId, selectedLuggageNature, calculatedAmount, sendNumber, receiverNumber, locationDescription, luggageDescription;

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);


        sharedPref = new SharedPref(this);

        buttonPost = findViewById(R.id.buttonPost);
        edtFrom = findViewById(R.id.edtFrom);
        edtTo = findViewById(R.id.edtTo);
        edtDate = findViewById(R.id.edtDate);
        edtAmount = findViewById(R.id.edtAmount);

        progressDialog = new ProgressDialog(SummaryActivity.this);

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
        locationDescription = bundle.getString("location_desc");
        luggageDescription = bundle.getString("luggage_desc");


        edtFrom.setText(luggage_origin);
        edtTo.setText(luggage_destination);

        String completeDate = pickUpDate +" at "+ pickUpTime;
        edtDate.setText(completeDate);


        String total_cost = calculatedAmount;
        try {
            float roundedAmount = Float.parseFloat(calculatedAmount);
            int amount = Math.round(roundedAmount);
            total_cost = Constants.addCommaToNumber(amount);

        } catch (Exception ignored) {

        } finally {

            edtAmount.setText(total_cost);
        }

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("due_date", pickUpDate);
                jsonObject.addProperty("post_by", sharedPref.getLoggedInUserID());
                jsonObject.addProperty("from_places", luggage_origin);
                jsonObject.addProperty("origin_contact", sendNumber);
                jsonObject.addProperty("to_places", luggage_destination);
                jsonObject.addProperty("destination_contact", receiverNumber);
                jsonObject.addProperty("luggage_nature", selectedLuggageNature);
                jsonObject.addProperty("vehicle_type", selectedVehicleId);
                jsonObject.addProperty("amount", calculatedAmount);
                jsonObject.addProperty("location_description", locationDescription);
                jsonObject.addProperty("description", luggageDescription);
                submitData(jsonObject);
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

    public void toPreviousPage(View view) {
        finish();
    }

    private void submitData(JsonObject jsonObject) {


        progressDialog.setMessage("Submitting");
        progressDialog.setCancelable(false);
        progressDialog.show();

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
                if (response.isSuccessful()) {
                    String response_string;
                    if (response.body() != null) {
                        response_string = response.body().toString();
                        try {
                            JSONObject obj = new JSONObject(response_string);
                            String status = obj.getString("status");
                            String message = obj.getString("message");
                            if (status.equalsIgnoreCase("true")) {
                                Toast.makeText(SummaryActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SummaryActivity.this, JobPostingSuccessActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                showSuccessfulDialog("Oops", message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showSuccessfulDialog("Failed", "Something went wrong. Please try again ");
                        }
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

        AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
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
        @POST("api/posts/postjob")
        Call<JsonObject> postData(@Body JsonObject body);
    }


}
