package com.example.ebebewa.activities.job_applicants;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import com.example.ebebewa.R;
import com.example.ebebewa.fragments.available_jobs.BidJob;
import com.example.ebebewa.utils.Constants;

/**
 * Created by Martin Mbae on 14,June,2020.
 */
public class JobApplicantsAdapter extends RecyclerView.Adapter<JobApplicantsAdapter.MyViewHolder> {

    private Context context;
    private List<Applicants> applicantsList;
    private JobApplicantsActivity jobApplicantsActivity;
    private String postId;

    public JobApplicantsAdapter(Context context, List<Applicants> applicantsList, JobApplicantsActivity jobApplicantsActivity, String postId) {
        this.context = context;
        this.applicantsList = applicantsList;
        this.jobApplicantsActivity = jobApplicantsActivity;
        this.postId = postId;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_applicants_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Applicants applicant = applicantsList.get(position);
        final String fullName = MessageFormat.format("{0} {1}", applicant.getFirst_name(), applicant.getLast_name());
        holder.fullName.setText(fullName);
        holder.phoneNumber.setText(applicant.getPhone());
        holder.vehicleType.setText(MessageFormat.format("{0} {1}", applicant.getType(), applicant.getPlate_number()));
        holder.deliveryStatusLayout.setVisibility(View.GONE);
        holder.onTransitLayout.setVisibility(View.GONE);
        holder.view_more.setText("View More");
        checkGetDriverRatings(applicant.getDriver(),holder.driver_ratings);
        final String status;
        if (applicant.getStatus().equals("0")) {
            status = "Pending";
            holder.view_more.setText("Accept this driver to deliver your luggage?");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.status.setTextColor(context.getColor(R.color.orange));
            }
        } else if (applicant.getStatus().equals("2")) {
            status = "Unsuccessful";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.status.setTextColor(context.getColor(R.color.red));
            }
            holder.view_more.setVisibility(View.GONE);
        } else {
            status = "Successful";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.status.setTextColor(context.getColor(R.color.green));
            }
            checkIfLuggageDelivered(applicant.getDriver(), holder.view_more, holder.deliveryStatusLayout, holder.onTransitLayout);

        }
        holder.status.setText(status);
        holder.view_more.setOnClickListener(v -> {
            double amount = Double.parseDouble(applicant.getAmount());
            jobApplicantsActivity.confirmJobApplication(fullName, applicant.getDriver(), applicant.getAmount(), status.equals("Pending"), applicant.getPayment_mode(), amount);
        });
    }

    @Override
    public int getItemCount() {
        return applicantsList.size();
    }

    private void checkIfLuggageDelivered(String driver_id, TextView view_more, LinearLayout deliveryLayout, LinearLayout onTransitLayout) {
        String uri = Constants.BASE_URL + "api/posts/getapplications/" + driver_id;
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, response -> parseAppliedJobs(response, view_more, deliveryLayout, onTransitLayout), error -> {
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(myReq);
    }

    private void checkGetDriverRatings(String driver_id, TextView driver_ratings) {
        String uri = Constants.BASE_URL + "api/posts/getratings/" + driver_id;
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, response -> parseDriverRating(response, driver_ratings), error -> {
            Log.d("ddddddddddd",error.getMessage());
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(myReq);
    }

    private void parseDriverRating(String response, TextView driver_ratings) {

        try {
            JSONObject jsonObject = new JSONObject(response);

          String rating =   jsonObject.getString("rating");

          rating = rating +" Points";

          driver_ratings.setText(rating);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseAppliedJobs(String response, TextView view_more, LinearLayout deliveryLayout, LinearLayout onTransitLayout) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() == 0) {
            } else {

                List<BidJob> bidJobs = Arrays.asList(new GsonBuilder().create().fromJson(jsonArray.toString(), BidJob[].class));
                for (int i = 0; i < bidJobs.size(); i++) {
                    BidJob bidJob = bidJobs.get(i);
                    if (bidJob.getPost().equalsIgnoreCase(postId)) {
                        String deliveryStatus = bidJob.getDelivery_status();
                        String clientConfirmedReceipt = bidJob.getClient_receipt_confirmation();
                        String bidStatus = bidJob.getBid_status();

                        String driver_id = bidJob.getDriver();

                        if (clientConfirmedReceipt.equalsIgnoreCase("0") && deliveryStatus.equalsIgnoreCase("1")) {
                            view_more.setText("Confirm that you have received the luggage");
                            view_more.setOnClickListener(v -> jobApplicantsActivity.confirmLuggageDeliveryAndRate(driver_id));
                        } else if (clientConfirmedReceipt.equalsIgnoreCase("1") && deliveryStatus.equalsIgnoreCase("1")) {
                            view_more.setVisibility(View.GONE);
                            deliveryLayout.setVisibility(View.VISIBLE);
                        } else if (deliveryStatus.equalsIgnoreCase("0")) {
                            onTransitLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView fullName, phoneNumber, vehicleType, status, view_more,driver_ratings;
        private LinearLayout deliveryStatusLayout, onTransitLayout;

        MyViewHolder(View view) {
            super(view);
            fullName = view.findViewById(R.id.fullName);
            phoneNumber = view.findViewById(R.id.phoneNumber);
            vehicleType = view.findViewById(R.id.vehicleType);
            driver_ratings = view.findViewById(R.id.driver_ratings);
            status = view.findViewById(R.id.status);
            deliveryStatusLayout = view.findViewById(R.id.deliveryStatusLayout);
            view_more = view.findViewById(R.id.view_more);
            onTransitLayout = view.findViewById(R.id.onTransitLayout);

        }
    }

}
