package com.example.ebebewa.activities.job_applicants;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ebebewa.activities.LoginActivity;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

import com.example.ebebewa.R;
import com.example.ebebewa.activities.HomeActivityClient;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.SharedPref;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

public class JobApplicantsActivity extends AppCompatActivity implements RatingDialogListener {
    String id, in_voice;
    private SharedPref sharedPref;
    private LinearLayout emptyAvailableJobLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private String driver_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_applicants);

        id = getIntent().getStringExtra("id");
        in_voice = getIntent().getStringExtra("in_voice");
        sharedPref = new SharedPref(this);
        emptyAvailableJobLayout = findViewById(R.id.emptyJobsLayout);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(JobApplicantsActivity.this);
        getJobApplicants();
    }

    private void getJobApplicants() {

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyAvailableJobLayout.setVisibility(View.GONE);

        String uri = Constants.BASE_URL + "api/posts/getapplicants/" + id;
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseApplicantsDetails(response);
                progressBar.setVisibility(View.GONE);

            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            emptyAvailableJobLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });

        RequestQueue queue = Volley.newRequestQueue(JobApplicantsActivity.this);
        queue.add(myReq);
    }

    private void checkIfLuggageDelivered() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyAvailableJobLayout.setVisibility(View.GONE);

        String uri = Constants.BASE_URL + "api/posts/getapplicants/" + id;
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseApplicantsDetails(response);
                progressBar.setVisibility(View.GONE);

            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            emptyAvailableJobLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });

        RequestQueue queue = Volley.newRequestQueue(JobApplicantsActivity.this);
        queue.add(myReq);
    }


    private void parseApplicantsDetails(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() == 0) {
                emptyAvailableJobLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyAvailableJobLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                List<Applicants> applicantsList = Arrays.asList(new GsonBuilder().create().fromJson(jsonArray.toString(), Applicants[].class));
                JobApplicantsAdapter jobApplicantsAdapter = new JobApplicantsAdapter(JobApplicantsActivity.this, applicantsList, this, id);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(JobApplicantsActivity.this);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(jobApplicantsAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            emptyAvailableJobLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void confirmJobApplication(String fullName, final String driver, String amount, boolean showApply, String paymentMethod, double cost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(JobApplicantsActivity.this);
        if (showApply)
            builder.setMessage("Do you accept " + fullName + " to deliver your luggage. \nOnce you accept, your luggage will be placed on transit");
        else
            builder.setMessage("Your luggage wil be delivered by " + fullName + ".\nPayment method: " + paymentMethod + ". \nTotal Amount:" + Constants.addCommaToNumber(cost));
        builder.setCancelable(false);
        if (showApply) {
            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    acceptJobApplication(id, driver, amount);
                }
            });
        }
        builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void confirmLuggageDeliveryAndRate(String driver_id) {
        this.driver_id = driver_id;
        showDialog();
    }

    private void showDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Confirm Delivery")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(5)
                .setTitle("Confirm Delivery")
                .setDescription("Confirm that you have received the luggage and give feedback on the service offered.")
                .setCommentInputEnabled(true)
                .setStarColor(R.color.starColor)
                .setNoteDescriptionTextColor(R.color.noteDescriptionTextColor)
                .setTitleTextColor(R.color.titleTextColor)
//                .setDescriptionTextColor(R.color.contentTextColor)
                .setHint("Leave your feedback (Optional)")
//                .setDefaultComment("")
//                .setHintTextColor(R.color.hintTextColor)
                .setCommentTextColor(R.color.commentTextColor)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
//                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(JobApplicantsActivity.this)
//                .setTargetFragment(this, TAG) // only if listener is implemented by fragment
                .show();
    }

    public void confirmLuggageDelivery() {


        AlertDialog.Builder builder = new AlertDialog.Builder(JobApplicantsActivity.this);


        builder.setTitle("Confirm Delivery");
        builder.setMessage("You are about to confirm that you have received this luggage. Please leave a feedback on the service you received");
        builder.setCancelable(false);

        final EditText input = new EditText(JobApplicantsActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        input.setHint("Provide a feedback");
        input.setLines(4);
        input.setBackground(getDrawable(R.drawable.black_border));
        input.setGravity(Gravity.START);
        input.setPadding(5, 5, 5, 5);

        builder.setPositiveButton("Yes, I have received", null);

        builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();

        alert.setView(input, 20, 10, 20, 10);
        alert.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String feedback = input.getText().toString();
                        if (TextUtils.isEmpty(feedback)) {
                            input.setError("Please leave some feedback");
                        } else {
                            alert.dismiss();
                            acceptDelivery(id, feedback);
                        }
                    }
                });
            }
        });

        alert.show();
    }

    public void acceptJobApplication(String id, String driver, String amount) {
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("driver", driver);
        jsonObject.addProperty("post", id);
        jsonObject.addProperty("invoice_no", in_voice);
        jsonObject.addProperty("amount", amount);
        submitData(jsonObject);

    }

    public void acceptDelivery(String post_id, String feedback) {
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post", post_id);
        jsonObject.addProperty("feedback", feedback);
        submitDataCOnfirmDelivery(jsonObject);

    }

    public void approveJobApplication(String id) {
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post", id);
        submitDataApproveJob(jsonObject);

    }

    private void submitDataApproveJob(JsonObject jsonObject) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceApprove service = retrofit.create(ApiServiceApprove.class);
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
                        if (status.equalsIgnoreCase("true")) {
                            Toast.makeText(JobApplicantsActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(JobApplicantsActivity.this, HomeActivityClient.class);
                            intent.putExtra("POSTED", 3);
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
                    showSuccessfulDialog("Failed", "Something went wrong. Please try again.");

                }
            }

            @Override
            public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());

            }

        });
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
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);
                        String status = obj.getString("status");
                        String message = obj.getString("message");
                        if (status.equalsIgnoreCase("true")) {
                            Toast.makeText(JobApplicantsActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(JobApplicantsActivity.this, HomeActivityClient.class);
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
                    showSuccessfulDialog("Failed", "Something went wrong. Please try again.");

                }
            }

            @Override
            public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());

            }

        });
    }

    private void submitDataCOnfirmDelivery(JsonObject jsonObject) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiConfirmDelivery service = retrofit.create(ApiConfirmDelivery.class);
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
                        if (status.equalsIgnoreCase("true")) {
                            Toast.makeText(JobApplicantsActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(JobApplicantsActivity.this, HomeActivityClient.class);
                            intent.putExtra("POSTED", 2);
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
                    showSuccessfulDialog("Failed", "Something went wrong. Please try again.");

                }
            }

            @Override
            public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());

            }

        });
    }

    void showSuccessfulDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(JobApplicantsActivity.this);
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

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {

        int points = i * 10;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("client", sharedPref.getLoggedInUserID());
        jsonObject.addProperty("driver", driver_id);
        jsonObject.addProperty("job", id);
        jsonObject.addProperty("points", points);

        acceptDelivery(id, s);

        submitDataRating(jsonObject);

    }

    private void submitDataRating(JsonObject jsonObject) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceRatings service = retrofit.create(ApiServiceRatings.class);
        Call<JsonObject> call = service.postRatings(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();

                } else {
                    showSuccessfulDialog("Failed", "Something went wrong. Please try again.");

                }
            }

            @Override
            public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());

            }

        });
    }

    private interface ApiServiceRatings {
        @POST("api/posts/driverrating")
        Call<JsonObject> postRatings(@Body JsonObject body);
    }
    private interface ApiService {
        @PUT("api/posts/clientacceptance")
        Call<JsonObject> postData(@Body JsonObject body);
    }

    private interface ApiServiceApprove {
        @PUT("api/posts/clientapproval")
        Call<JsonObject> postData(@Body JsonObject body);
    }

    private interface ApiConfirmDelivery {
        @PUT("api/posts/finalclientdeliveryconfirmation")
        Call<JsonObject> postData(@Body JsonObject body);
    }
}
