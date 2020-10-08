package com.example.ebebewa.activities.registration.helpers;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;
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
import com.example.ebebewa.activities.registration.steps.ClientPaymentInformation;
import com.example.ebebewa.activities.registration.steps.DriverCountyPostal;
import com.example.ebebewa.activities.registration.steps.DriverPersonalDetails;
import com.example.ebebewa.activities.registration.steps.DriverUsernamePassword;
import com.example.ebebewa.activities.registration.steps.InviteAgentCode;
import com.example.ebebewa.utils.Constants;

public class NewClientReg extends AppCompatActivity implements StepperFormListener, DialogInterface.OnClickListener {
    private VerticalStepperFormView verticalStepperForm;
    private DriverPersonalDetails driverPersonalDetails;
    private DriverUsernamePassword driverUsernamePassword;
    private DriverCountyPostal driverCountyPostal;
    private ClientPaymentInformation clientPaymentInformation;
private InviteAgentCode inviteAgentCode;
    String phoneString, usernameString, passwordString;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        driverPersonalDetails = new DriverPersonalDetails("Your Personal Details", "", null, this, NewClientReg.this, Constants.CLIENT_ROLE);
        driverUsernamePassword = new DriverUsernamePassword("Your Username and Password", "");
        driverCountyPostal = new DriverCountyPostal("Your Address", "", NewClientReg.this, Constants.CLIENT_ROLE);
        clientPaymentInformation = new ClientPaymentInformation("Payment Method", "");
        inviteAgentCode = new InviteAgentCode("Agent Invite Code","");

        progressDialog = new ProgressDialog(this);
        verticalStepperForm = findViewById(R.id.stepper_form);
        verticalStepperForm.setup(this, driverPersonalDetails, driverCountyPostal, driverUsernamePassword, clientPaymentInformation,inviteAgentCode).init();
    }

    @Override
    public void onCompletedForm() {

        saveData();
    }

    @Override
    public void onCancelledForm() {
        finish();
    }

    private void saveData() {

        DriverPersonalDetails.PersonalDetails personalDetails = driverPersonalDetails.getStepData();
        DriverUsernamePassword.UsernamePassword usernamePassword = driverUsernamePassword.getStepData();
        DriverCountyPostal.CountyPostal countyPostal = driverCountyPostal.getStepData();
        String paymentInfo = clientPaymentInformation.getStepData();  InviteAgentCode.AgentID agentID = inviteAgentCode.getStepData();

        String agentCode = agentID.agent_id;

        String firstName = personalDetails.firstName;
        String lastName = personalDetails.lastName;
        String id = personalDetails.nationalId;
        String phone = personalDetails.phone;
        String email = personalDetails.email;
        String county = countyPostal.county;
        String postal = countyPostal.postal;
        String username = usernamePassword.username;
        String password = usernamePassword.password;
        String passport = personalDetails.photoBase64;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String role = Constants.CLIENT_ROLE;

        usernameString = username;
        passwordString = password;
        phoneString = phone;

        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Registering...");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("first_name", firstName);
        jsonObject.addProperty("last_name", lastName);
        jsonObject.addProperty("national_id", id);
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("post_code", postal);
        jsonObject.addProperty("country", county);
        jsonObject.addProperty("passport_photo", passport);
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("created_date", currentDate);
        jsonObject.addProperty("role", role);
        jsonObject.addProperty("payment_mode", paymentInfo);
        jsonObject.addProperty("agent_code", agentCode);

        submitData(jsonObject);


    }

    private void finishIfPossible() {

        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishIfPossible();
            return true;
        }

        return false;
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

    @Override
    public void onBackPressed() {
        finishIfPossible();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                driverPersonalDetails.setUri(result.getUri());

            }
        }
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
            public void onResponse(@NotNull Call<JsonObject> call, @NotNull retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                openStepper();
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
                            if (status.equals("true")) {
                                Toast.makeText(NewClientReg.this, "Account created successfully. Please login", Toast.LENGTH_SHORT).show();

                                Intent loginIntent = new Intent(NewClientReg.this, LoginActivity.class);
                                loginIntent.putExtra("FROM_REGISTER", 2);
                                loginIntent.putExtra("phone", phoneString);
                                loginIntent.putExtra("username", usernameString);
                                loginIntent.putExtra("password", passwordString);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                            } else {
                                showSuccessfulDialog("Oops!", message);
                                verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
                                verticalStepperForm.goToStep(0, true);
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
                openStepper();
                showSuccessfulDialog("Registration Failed", "You do not have good internet connection or eBebewa is under maintenance at the moment. Please check your internet connection or try registering later");
            }
        });
    }

    void showSuccessfulDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(NewClientReg.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Dismiss", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();

    }

    private interface ApiService {
        @POST("api/clients/registerClient")
        Call<JsonObject> postData(@Body JsonObject body);
    }


}
