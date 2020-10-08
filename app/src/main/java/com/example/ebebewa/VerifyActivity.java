package com.example.ebebewa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.example.ebebewa.utils.Constants;

public class VerifyActivity extends AppCompatActivity {

    TextInputEditText editText;
    AppCompatButton buttonVerify;
    ProgressDialog progressDialog;
    String username, phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        editText = findViewById(R.id.editTextCode);
        buttonVerify = findViewById(R.id.buttonVerify);

        username = getIntent().getStringExtra("username");
        phonenumber = getIntent().getStringExtra("phone");

        progressDialog = new ProgressDialog(this);

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = editText.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {

                    editText.setError("Enter code...");
                    editText.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });


    }

    private void verifyCode(String code) {

        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("phone", phonenumber);
        jsonObject.addProperty("code", code);

        submitVerificationData(jsonObject);

    }


    private void submitVerificationData(JsonObject jsonObject) {
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
                        showSuccessfulDialog(status, message);
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

    void showSuccessfulDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(VerifyActivity.this);
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

    private interface ApiServiceVerification {
        @POST("api/sms")
        Call<JsonObject> postData(@Body JsonObject body);
    }

}
