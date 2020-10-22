package com.example.ebebewa.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.ebebewa.LoginSelectionActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

import com.example.ebebewa.R;
import com.example.ebebewa.activities.registration.helpers.NewClientReg;
import com.example.ebebewa.activities.registration.helpers.NewDriverReg;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.SharedPref;
import com.stepstone.apprating.AppRatingDialog;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {


    EditText username, password;
    Button login;
    TextView forgotPassword,loginUserType;
    TextInputLayout txtInLayoutUsername, txtInLayoutPassword;
    TextView errorTextView;
    ProgressDialog progressDialog;
    SharedPref sharedPref;

    String userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        txtInLayoutUsername = findViewById(R.id.txtInLayoutUsername);
        txtInLayoutPassword = findViewById(R.id.txtInLayoutPassword);
        errorTextView = findViewById(R.id.loginErrorMessage);
        forgotPassword = findViewById(R.id.forgotPassword);
        loginUserType = findViewById(R.id.loginUserType);
        errorTextView.setVisibility(View.GONE);

        sharedPref = new SharedPref(this);

        Bundle bundle = getIntent().getExtras();
       userType =  bundle.getString("class");

       if (userType.equals(Constants.CLIENT_ROLE)){
           loginUserType.setText("Login as Client");
       }else if (userType.equals(Constants.DRIVER_ROLE)){
           loginUserType.setText("Login as Driver");
       }


        int pos = getIntent().getIntExtra("FROM_REGISTER", 0);
        if (pos != 0) {
            String phoneString = getIntent().getStringExtra("phone");
            String usernameString = getIntent().getStringExtra("username");
            String passwordString = getIntent().getStringExtra("password");
            username.setText(usernameString);
            if (usernameString != null)
                username.setSelection(usernameString.length());
            password.setText(passwordString);
            showInputPhoneNumberDialog(phoneString, usernameString, passwordString);

        }

        ClickLogin();


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void processForgotPasswordRequest(String username, LinearLayout hiddenLayout, TextInputEditText editUsername) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Submitting your request...");
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        submitForgotPasswordData(jsonObject, hiddenLayout,editUsername);

    }

    private void processForgotPasswordRequestFInal(String resetcode, String new_password, String confirm_password) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Submitting your request...");
        progressDialog.show();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resetcode", resetcode);
        jsonObject.addProperty("new_password", new_password);
        jsonObject.addProperty("confirm_password", confirm_password);
        submitForgotPasswordFinalData(jsonObject);

    }

    private void submitForgotPasswordData(JsonObject jsonObject, LinearLayout hiddenLayout, TextInputEditText editUsername) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceForgotPassword service = retrofit.create(ApiServiceForgotPassword.class);
        Call<JsonObject> call = service.sendForgotPasswordRequest(jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);

                        String status = obj.getString("Status");
                        String message = obj.getString("Message");
                        if (status.equalsIgnoreCase("true")) {
                            showSuccessfulDialog("Success", "A code has been sent to your phone number. Use the code to reset your password.");
                            hiddenLayout.setVisibility(View.VISIBLE);
                            editUsername.setEnabled(false);
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

    private void submitForgotPasswordFinalData(JsonObject jsonObject) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIResendCodeFinal service = retrofit.create(APIResendCodeFinal.class);
        Call<JsonObject> call = service.resendCodeData(jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);

                        String status = obj.getString("Status");
                        String message = obj.getString("Message");
                        if (status.equalsIgnoreCase("true")) {
                            showSuccessfulDialog("Success", "Your password has been successfully updated.");
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

    //This is method for doing operation of check login
    private void ClickLogin() {


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String usernameText = username.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                if (usernameText.isEmpty()) {

                    Snackbar snackbar = Snackbar.make(view, "Please fill out these field", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();
                    txtInLayoutUsername.setError("Username should not be empty");
                } else if (passwordText.isEmpty()) {
                    txtInLayoutUsername.setError("");
                    Snackbar snackbar = Snackbar.make(view, "Please fill out these field", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();
                    txtInLayoutPassword.setError("Password should not be empty");
                } else {
                    txtInLayoutPassword.setError("");


                    loginAction("", usernameText, passwordText);


                }
            }

        });
    }

    private void loginAction(String phone, String usernameText, String passwordText) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", usernameText);
        jsonObject.addProperty("pass", passwordText);
        jsonObject.addProperty("role", userType);

        submitData(jsonObject, phone, usernameText, passwordText);
    }

    private void submitData(JsonObject jsonObject, final String phone, final String usernameText, final String passwordText) {

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Signing in");
        progressDialog.setMessage("Please wait...");
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
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);
                        String success = obj.getString("success");

                        if (success.equalsIgnoreCase("true")) {
                            errorTextView.setVisibility(View.GONE);
                            String message = obj.getString("message");
                            String role = obj.getString("role");
                            String id = obj.getString("id");
                            String first_name = obj.getString("first_name");
                            String last_name = obj.getString("last_name");
                            String national_id = obj.getString("national_id");
                            String phone = obj.getString("phone");
                            String email = obj.getString("email");
                            String post_code = obj.getString("post_code");
                            String county = obj.getString("county");
                            String passport_photo = obj.getString("passport_photo");
                            String username = obj.getString("username");
                            String vehicle_type = obj.getString("vehicle_type");
                            String plate_number = obj.getString("plate_number");
                            String payment_mode = obj.getString("payment_mode");
                            String created_date = obj.getString("created_date");
                            passport_photo = "https://ebebewa.com/uploads/docs/" + passport_photo;
                            switch (role) {
                                case Constants.DRIVER_ROLE:

                                    sharedPref.setFirstName(first_name);
                                    sharedPref.setLastName(last_name);
                                    sharedPref.setUserName(username);
                                    sharedPref.setPhoneNumber(phone);
                                    sharedPref.setNationalId(national_id);
                                    sharedPref.setEmailAddress(email);
                                    sharedPref.setPostalCode(post_code);
                                    sharedPref.setCountyName(county);
                                    sharedPref.setPassportPhoto(passport_photo);
                                    sharedPref.setPaymentMethod(payment_mode);
                                    sharedPref.setRoleID(role);
                                    sharedPref.setLoggedInUserID(id);
                                    sharedPref.setVehicleType(vehicle_type);
                                    sharedPref.setPlateNumber(plate_number);

                                    Intent driverIntent = new Intent(LoginActivity.this, HomeActivityDriver.class);
                                    driverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(driverIntent);


                                    break;

                                case Constants.CLIENT_ROLE:

                                    sharedPref.setFirstName(first_name);
                                    sharedPref.setLastName(last_name);
                                    sharedPref.setUserName(username);
                                    sharedPref.setPhoneNumber(phone);
                                    sharedPref.setNationalId(national_id);
                                    sharedPref.setEmailAddress(email);
                                    sharedPref.setPostalCode(post_code);
                                    sharedPref.setCountyName(county);
                                    sharedPref.setPassportPhoto(passport_photo);
                                    sharedPref.setPaymentMethod(payment_mode);
                                    sharedPref.setRoleID(role);
                                    sharedPref.setLoggedInUserID(id);

                                    Intent clientIntent = new Intent(LoginActivity.this, HomeActivityClient.class);
                                    clientIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(clientIntent);
                                    break;

                                default:

                                    showSuccessfulDialog("Login Failed", "You are neither registered as a driver or a client. Please contact the admin.");
                                    break;
                            }


                        } else {
                            String message = obj.getString("message");

                            if (message.contains("inactive")) {
                                showInputPhoneNumberDialog(phone, usernameText, passwordText);
                            } else {
                                errorTextView.setText(message);
                                errorTextView.setVisibility(View.VISIBLE);
                                showSuccessfulDialog("Login Failed", message);

                            }
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
                showSuccessfulDialog("Login Failed", "You do not have good internet connection or eBebewa is under maintenance at the moment. Please check your internet connection or try again later");
            }
        });
    }

    private int timeToResendDefault = 40;
    private int timeToResend = timeToResendDefault;

    private void showInputPhoneNumberDialog(final String phoneNumber, final String usernameText, final String passwordText) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.activity_verify);
        dialog.setCancelable(true);

        TextView errorTetxView = dialog.findViewById(R.id.errorMessage);
        TextView resend_code = dialog.findViewById(R.id.resend_code);
        final TextInputEditText codeEdt = dialog.findViewById(R.id.editTextCode);
        final AppCompatButton verifyBtn = dialog.findViewById(R.id.buttonVerify);
        ImageButton cancelDialog = dialog.findViewById(R.id.canceDialog);

        cancelDialog.setOnClickListener(v -> dialog.dismiss());

        codeEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkAccuracy(errorTextView, codeEdt, verifyBtn);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeEdt.getText().toString().trim();
                verifyCode(code, usernameText, passwordText, dialog);
            }
        });

        checkAccuracy(errorTetxView, codeEdt, verifyBtn);


        Timer T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String resendText = "Will resend code in " + timeToResend + " seconds";
                        resend_code.setText(resendText);
                        timeToResend--;

                        if (timeToResend == 0) {
                            T.cancel();
                            resend_code.setText("Click to resend code");
                            resend_code.setTextColor(getResources().getColor(R.color.blue));


                            resend_code.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();

                                    requestResendCode(phoneNumber, usernameText, passwordText);

                                }
                            });

                            timeToResend = timeToResendDefault;
                        }
                    }
                });
            }
        }, 1000, 1000);


        dialog.show();
    }


    private void checkAccuracy(TextView errorTextView, TextInputEditText codeEdt, AppCompatButton verifyBtn) {

        String code = codeEdt.getText().toString();

        String errorMessage;
        if (TextUtils.isEmpty(code)) {
            errorMessage = "Provide activation code";
        } else {
            errorMessage = "true";
        }

        if (errorMessage.equalsIgnoreCase("true")) {
            errorTextView.setVisibility(View.GONE);
            verifyBtn.setEnabled(true);
        } else {
            errorTextView.setText(errorMessage);
            errorTextView.setVisibility(View.VISIBLE);
            verifyBtn.setEnabled(false);
        }
    }

    void showSuccessfulDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

    private void verifyCode(String code, String username, String password, Dialog dialog) {

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setTitle("Activating your account");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("activation_code", code);


        submitVerificationData(jsonObject, username, password, dialog);

    }

    private void submitVerificationData(JsonObject jsonObject, final String username, final String password, final Dialog dialog) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceVerification service = retrofit.create(ApiServiceVerification.class);
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
                            dialog.dismiss();
                            loginAction("", username, password);
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
            public void onFailure(@NotNull Call<JsonObject> call, Throwable t) {
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());
                progressDialog.dismiss();
            }

        });
    }

    public void requestResendCode(String phone, String username, String password) {


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("username", username);

        submitResendCode(jsonObject, phone, username, password);

    }

    private void submitResendCode(JsonObject jsonObject, String phone, String username, String password) {

        progressDialog = new ProgressDialog(LoginActivity.this);

        progressDialog.setMessage("Resending code");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIResendCode service = retrofit.create(APIResendCode.class);
        Call<JsonObject> call = service.resendCodeData(jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();

                    Log.e("dddddddddddddddd", response_string);


                    try {
                        JSONObject obj = new JSONObject(response_string);
                        String status = obj.getString("Status");
                        String message = obj.getString("Message");
                        if (status.equalsIgnoreCase("true")) {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                            showInputPhoneNumberDialog(phone, username, password);

                            showSuccessfulDialog("Success", "Another code has been generated and sent to your phone number(" + phone + ")");
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

    private void showForgotPasswordDialog() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.dialog_reset_password);
        dialog.setCancelable(false);
        final TextInputEditText editUsername = dialog.findViewById(R.id.editUsername);
        final TextInputEditText editOTPCode = dialog.findViewById(R.id.editOTPCode);
        final TextInputEditText editNewPassword = dialog.findViewById(R.id.editNewPassword);
        final TextInputEditText editConfirmPassword = dialog.findViewById(R.id.editConfirmPassword);
        final AppCompatButton verifyBtn = dialog.findViewById(R.id.buttonVerify);
        ImageButton cancelDialog = dialog.findViewById(R.id.canceDialog);
        LinearLayout hiddenLayout = dialog.findViewById(R.id.hiddenLayout);
        hiddenLayout.setVisibility(View.GONE);
        cancelDialog.setOnClickListener(v -> dialog.dismiss());

        editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 2) {
                    verifyBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString().trim();

                if (hiddenLayout.getVisibility() == View.VISIBLE) {

                    String OTP = editOTPCode.getText().toString().trim();
                    String newPass = editNewPassword.getText().toString().trim();
                    String confPass = editConfirmPassword.getText().toString().trim();

                    if (OTP.length() < 2) {
                        editOTPCode.setError("Please provide the OTP code");
                        return;
                    }

                    if (newPass.length() < 8) {
                        editNewPassword.setError("Password must contain more 8 or more characters");
                        return;
                    }

                    if (!newPass.equals(confPass)) {
                        editConfirmPassword.setError("Passwords failed to match");
                        return;
                    }
                    dialog.dismiss();
                    processForgotPasswordRequestFInal(OTP,newPass,confPass);

                } else {
                    if (TextUtils.isEmpty(username)) {
                        editUsername.setError("Provide this field");
                    } else {
                        processForgotPasswordRequest(username, hiddenLayout,editUsername);
                    }
                }
            }
        });

        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            Intent loginSelectionIntent = new Intent(LoginActivity.this, LoginSelectionActivity.class);
            loginSelectionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginSelectionIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private interface ApiService {
        @POST("api/login")
        Call<JsonObject> postData(@Body JsonObject body);
    }

    private interface ApiServiceVerification {
        @PUT("api/clients/activateaccount")
        Call<JsonObject> postData(@Body JsonObject body);
    }

    private interface APIResendCode {
        @PUT("api/login/resendcode")
        Call<JsonObject> resendCodeData(@Body JsonObject body);
    }

    private interface APIResendCodeFinal {
        @Headers({"Content-Type: application/json"})
        @PUT("api/login/resetpassword")
        Call<JsonObject> resendCodeData(@Body JsonObject body);
    }

    private interface ApiServiceForgotPassword {
        @Headers({"Content-Type: application/json"})
        @PUT("api/login/sendrecoverycode")
        Call<JsonObject> sendForgotPasswordRequest(@Body JsonObject body);
    }

}