package com.example.ebebewa.activities.post_job;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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
import com.example.ebebewa.R;
import com.example.ebebewa.activities.HomeActivityClient;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.SharedPref;

public class PostFragmentStep4 extends Fragment {

    SharedPref sharedPref;
    private EditText senderEdt, receiverEdt, locationDesc, luggageDesc;
    private Button postBtn, prevBtn;
    private TextView errorTextView;
    private ProgressDialog progressDialog;
    private PostDeliveryJobActivity postDeliveryJobActivity;

    PostFragmentStep4(PostDeliveryJobActivity postDeliveryJobActivity) {
        this.postDeliveryJobActivity = postDeliveryJobActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_job_step4, container, false);

        if (getActivity() != null) {

            senderEdt = view.findViewById(R.id.sender_phone);
            receiverEdt = view.findViewById(R.id.receiver_phone);
            locationDesc = view.findViewById(R.id.location_desc);
            luggageDesc = view.findViewById(R.id.luggage_desc);
            postBtn = view.findViewById(R.id.postBtn);
            prevBtn = view.findViewById(R.id.prevBtn);
            errorTextView = view.findViewById(R.id.errorMessage);
            progressDialog = new ProgressDialog(getActivity());

            sharedPref = new SharedPref(getActivity());
            setTextWatchers(senderEdt);
            setTextWatchers(receiverEdt);
            setTextWatchers(locationDesc);
            setTextWatchers(luggageDesc);

            senderEdt.setText(postDeliveryJobActivity.sendNumber);
            receiverEdt.setText(postDeliveryJobActivity.receiverNumber);
            locationDesc.setText(postDeliveryJobActivity.locationDescription);
            luggageDesc.setText(postDeliveryJobActivity.luggageDescription);

            prevBtn.setOnClickListener(v -> {

                String sendNumber = senderEdt.getText().toString();
                String receiverNumber = receiverEdt.getText().toString();
                String locationDescription = locationDesc.getText().toString();
                String luggageDescription = luggageDesc.getText().toString();

                postDeliveryJobActivity.sendNumber = sendNumber;
                postDeliveryJobActivity.receiverNumber = receiverNumber;
                postDeliveryJobActivity.locationDescription = locationDescription;
                postDeliveryJobActivity.luggageDescription = luggageDescription;

                if (getActivity() != null)
                    ((PostDeliveryJobActivity) getActivity()).goToFragment(new PostFragmentStep3(postDeliveryJobActivity), 2);
            });
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String sendNumber = senderEdt.getText().toString();
                    String receiverNumber = receiverEdt.getText().toString();
                    String locationDescription = locationDesc.getText().toString();
                    String luggageDescription = luggageDesc.getText().toString();

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("due_date", postDeliveryJobActivity.pickUpDate);
                    jsonObject.addProperty("post_by", sharedPref.getLoggedInUserID());
                    jsonObject.addProperty("from_places", postDeliveryJobActivity.luggage_origin);
                    jsonObject.addProperty("origin_contact", sendNumber);
                    jsonObject.addProperty("to_places", postDeliveryJobActivity.luggage_destination);
                    jsonObject.addProperty("destination_contact", receiverNumber);
                    jsonObject.addProperty("luggage_nature", "1");
                    jsonObject.addProperty("vehicle_type", postDeliveryJobActivity.selectedVehicleId);
                    jsonObject.addProperty("amount", postDeliveryJobActivity.calculatedAmount);
                    jsonObject.addProperty("location_description", locationDescription);
                    jsonObject.addProperty("description", luggageDescription);
                    submitData(jsonObject);
                }
            });
        }
        return view;
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

        String sender = senderEdt.getText().toString();
        String receiver = receiverEdt.getText().toString();
        String locationD = locationDesc.getText().toString();
        String luggageD = luggageDesc.getText().toString();

        String errorMessage;
        if (TextUtils.isEmpty(sender)) {
            errorMessage = "Provider your phone number";
        } else if (!checkPhoneNumber(sender).equals("true")) {
            errorMessage = "Sender phone number. (" + checkPhoneNumber(sender) + ")";
        } else if (TextUtils.isEmpty(receiver)) {
            errorMessage = "Provider receiver's phone number";
        } else if (!checkPhoneNumber(receiver).equals("true")) {
            errorMessage = "Receiver phone number. (" + checkPhoneNumber(receiver) + ")";
        } else if (TextUtils.isEmpty(locationD)) {
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

    private String checkPhoneNumber(String phone) {
        if (!phone.trim().startsWith("254")) {
            return "must start with '254'";
        } else if (phone.trim().length() != 12) {
            return "Invalid length";
        } else {
            return "true";
        }
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
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), HomeActivityClient.class);
                                intent.putExtra("POSTED", 1);
                                startActivity(intent);
                                if (getActivity() != null)
                                    getActivity().finish();
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
        @POST("api/posts/postjob")
        Call<JsonObject> postData(@Body JsonObject body);
    }


}
