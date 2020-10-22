package com.example.ebebewa.activities.registration.helpers;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.example.ebebewa.R;
import com.example.ebebewa.activities.LoginActivity;
import com.example.ebebewa.activities.registration.steps.DriverCountyPostal;
import com.example.ebebewa.activities.registration.steps.DriverPersonalDetails;
import com.example.ebebewa.activities.registration.steps.DriverTransportInformation;
import com.example.ebebewa.activities.registration.steps.DriverUsernamePassword;
import com.example.ebebewa.activities.registration.steps.InviteAgentCode;
import com.example.ebebewa.utils.Constants;

public class NewDriverReg extends AppCompatActivity implements StepperFormListener, DialogInterface.OnClickListener {

    private VerticalStepperFormView verticalStepperForm;
    private DriverPersonalDetails driverPersonalDetails;
    private DriverUsernamePassword driverUsernamePassword;
    private InviteAgentCode inviteAgentCode;
    private DriverCountyPostal driverCountyPostal;
    private DriverTransportInformation driverTransportInformation;
    private ProgressDialog progressDialog;

    String phoneString, usernameString, passwordString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_driver);
        driverPersonalDetails = new DriverPersonalDetails("Your Personal Details", "", this, null, NewDriverReg.this, Constants.DRIVER_ROLE);
        driverUsernamePassword = new DriverUsernamePassword("Your Username and Password", "");
        inviteAgentCode = new InviteAgentCode("Agent Invite Code","");
        driverCountyPostal = new DriverCountyPostal("Your Address", "", NewDriverReg.this);
        driverTransportInformation = new DriverTransportInformation("Driver Transport Information", "", NewDriverReg.this);

        progressDialog = new ProgressDialog(this);
        verticalStepperForm = findViewById(R.id.stepper_form);
        verticalStepperForm.setup(this, driverPersonalDetails, driverCountyPostal, driverUsernamePassword, driverTransportInformation,inviteAgentCode).init();

    }

    @Override
    public void onCompletedForm() {
        saveData();
    }

    @Override
    public void onCancelledForm() {

    }

    private void saveData() {


        DriverPersonalDetails.PersonalDetails personalDetails = driverPersonalDetails.getStepData();
        DriverUsernamePassword.UsernamePassword usernamePassword = driverUsernamePassword.getStepData();
        DriverCountyPostal.CountyPostal countyPostal = driverCountyPostal.getStepData();
        DriverTransportInformation.TransportInfo transportInfo = driverTransportInformation.getStepData();
        InviteAgentCode.AgentID agentID = inviteAgentCode.getStepData();

        String agentCode = agentID.agent_id;

        String firstname = personalDetails.firstName;
        String lastname = personalDetails.lastName;
        String id = personalDetails.nationalId;
        String email = personalDetails.email;
        String phone = personalDetails.phone;
        String postalCode = countyPostal.postal;
        String county = countyPostal.county;
        String username = usernamePassword.username;
        String password = usernamePassword.password;
        String plateNumber = transportInfo.plateNumber;
        String vehicleOwned = transportInfo.vehicleOwned;
        String payment = transportInfo.payment;
        String ownership = transportInfo.owner_of_vehicle;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String passport_photo = personalDetails.photoBase64;
        String role = Constants.DRIVER_ROLE;

        usernameString = username;
        passwordString = password;
        phoneString = phone;


        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Registering...");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("first_name", firstname);
        jsonObject.addProperty("last_name", lastname);
        jsonObject.addProperty("national_id", id);
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("post_code", postalCode);
        jsonObject.addProperty("country", county);
        jsonObject.addProperty("passport_photo", passport_photo);
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("created_date", currentDate);
        jsonObject.addProperty("role", role);
        jsonObject.addProperty("vehicle_type", vehicleOwned);
        jsonObject.addProperty("plate_number", plateNumber);
        jsonObject.addProperty("payment_mode", payment);
        jsonObject.addProperty("agent_code", agentCode);
        jsonObject.addProperty("ownership", ownership);
        submitData(jsonObject);

    }

    private void openStepper() {
        verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
        verticalStepperForm.goToStep(0, true);
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
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                openStepper();
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);
                        String status = obj.getString("status");
                        String message = obj.getString("message");
                        if (status.equals("true")) {
                            Toast.makeText(NewDriverReg.this, "Account created successfully. Please login", Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(NewDriverReg.this, LoginActivity.class);
                            loginIntent.putExtra("FROM_REGISTER", 2);
                            loginIntent.putExtra("phone", phoneString);
                            loginIntent.putExtra("username", usernameString);
                            loginIntent.putExtra("password", passwordString);
                            loginIntent.putExtra("class", Constants.DRIVER_ROLE);
                            startActivity(loginIntent);
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
                showSuccessfulDialog("Registration Failed", "You do not have good internet connection or eBebewa is under maintenance at the moment. Please check your internet connection or try registering later");
                openStepper();
            }

        });
    }

    void showSuccessfulDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(NewDriverReg.this);
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
        @POST("api/drivers/registerDriver")
        Call<JsonObject> postData(@Body JsonObject body);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        switch (which) {

            // "Discard" button of the Discard Alarm dialog
            case -1:
                finish();
                break;

            // "Cancel" button of the Discard Alarm dialog
            case -2:
                verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    driverPersonalDetails.setUri(result.getUri());

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(NewDriverReg.this, "Failed to get profile picture, Try Again.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
