package com.example.ebebewa_app.fragments.client_jobs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

import com.example.ebebewa_app.IPayActivity;
import com.example.ebebewa_app.R;
import com.example.ebebewa_app.activities.EditPostedJobActivity;
import com.example.ebebewa_app.activities.job_applicants.JobApplicantsActivity;
import com.example.ebebewa_app.utils.Constants;
import com.example.ebebewa_app.utils.Job;

/**
 * Created by Martin Mbae on 14,June,2020.
 */
public class AvailableDriversAdapter extends RecyclerView.Adapter<AvailableDriversAdapter.MyViewHolder> {

    private Context context;
    private List<Job> jobList;
    private HashMap<String, String> vehicleTypesHashMaps;
    private HashMap<String, String> luggageNatureHashMaps;
    private AvailableDriversFragment availableDriversFragment;

    public AvailableDriversAdapter(Context context, List<Job> jobList, HashMap<String, String> vehicleTypesHashMaps, HashMap<String, String> luggageNatureHashMaps, AvailableDriversFragment availableDriversFragment) {
        this.jobList = jobList;
        this.vehicleTypesHashMaps = vehicleTypesHashMaps;
        this.luggageNatureHashMaps = luggageNatureHashMaps;
        this.availableDriversFragment = availableDriversFragment;
        this.context = context;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_delivery_job_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Job job = jobList.get(position);
        holder.pickUpDate.setText(job.getDue_date());
        holder.fromTown.setText(job.getOrigin_place());
        holder.toTown.setText(job.getDestination_place());

        holder.numberOfApplicantsLayout.setVisibility(View.GONE);
        holder.paymentsProgressBar.setVisibility(View.GONE);
        try {
            holder.amount.setText(Constants.addCommaToNumber(Integer.parseInt(job.getAmount())));
        } catch (Exception e) {
            holder.amount.setText(job.getAmount());
        }

        holder.locationDesc.setText(job.getLocation_description());
        holder.luggage_desc.setText(job.getDescription());

        String vehicleType = job.getVehicle_type();
        if (vehicleTypesHashMaps != null) {
            vehicleType = vehicleTypesHashMaps.get(vehicleType);
        }
        holder.vehicle.setText(vehicleType);

        String jobStatus;
        if (job.getBid_status().equals("1")) {
            jobStatus = "Closed";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.jobStatus.setTextColor(context.getColor(R.color.red));
            }
        } else {
            jobStatus = "Open";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.jobStatus.setTextColor(context.getColor(R.color.green));
            }
        }

        holder.jobStatus.setText(jobStatus);
        holder.payStatus.setText(job.getPayment_status());
        holder.invoiceNumber.setText(job.getInvoice_no());

        if (job.getPayment_status().equalsIgnoreCase("unpaid")) {
            holder.view_applications.setVisibility(View.GONE);
            holder.make_payments.setVisibility(View.GONE);


            String formattedAmount = job.getAmount();
            try {
                int amountInt = Integer.parseInt(formattedAmount);
                formattedAmount = Constants.addCommaToNumber(amountInt);
            } catch (Exception ignored) {

            } finally {
                getNumberOfApplicants(holder.paymentsProgressBar, holder.make_payments, job.getId(), holder.numberOfApplicantsLayout, holder.applicantsNumber, formattedAmount);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.payStatus.setTextColor(context.getColor(R.color.red));
            }
        } else {
            holder.view_applications.setVisibility(View.VISIBLE);
            holder.make_payments.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.payStatus.setTextColor(context.getColor(R.color.green));
            }
        }
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(context, EditPostedJobActivity.class);
                Gson gson = new Gson();
                String myJson = gson.toJson(job);
                editIntent.putExtra("job", myJson);
                context.startActivity(editIntent);
            }
        });

        holder.view_applications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent applicantsIntent = new Intent(context, JobApplicantsActivity.class);
                applicantsIntent.putExtra("id", job.getId());
                applicantsIntent.putExtra("in_voice", job.getInvoice_no());
                context.startActivity(applicantsIntent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                availableDriversFragment.confirmDeletePos(job.getId());
            }
        });
        holder.make_payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent paymentsIntent = new Intent(context, IPayActivity.class);
                paymentsIntent.putExtra("invoice", job.getInvoice_no());
                paymentsIntent.putExtra("amount", job.getAmount());
                context.startActivity(paymentsIntent);
            }
        });
    }

    private void getNumberOfApplicants(ProgressBar paymentsProgressBar, TextView make_payments, String post_id, LinearLayout numberOfApplicantsLayout, TextView applicantsNumber, String amountToPay) {

        paymentsProgressBar.setVisibility(View.VISIBLE);

        String uri = Constants.BASE_URL + "api/posts/getapplicants/" + post_id;
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseApplicantsDetails(response, paymentsProgressBar, make_payments, numberOfApplicantsLayout, applicantsNumber, amountToPay);
            }
        }, error -> {
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(myReq);
    }

    private void parseApplicantsDetails(String response, ProgressBar paymentsProgressBar, TextView make_payments, LinearLayout numberOfApplicantsLayout, TextView applicantsNumber, String amountToPay) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() == 0) {
                make_payments.setText("No Applicants.");
                make_payments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("No Applicants");
                        builder.setMessage("No driver has applied for this job. You will be able to make payments for this job once there is at least one driver who is willing to deliver your luggage.\nPlease check later.");
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
                });
            } else {
                int numberAppl = jsonArray.length();
                String layouString;
                if (numberAppl == 1) {
                    layouString = "We have found one driver who is willing to deliver your luggage. Please make a payment of " + amountToPay + " to have your luggage delivered";
                } else {
                    layouString = "We have found " + numberAppl + " drivers who are willing to deliver your luggage. Please make a payment of " + amountToPay + " to have your luggage delivered";
                }

                numberOfApplicantsLayout.setVisibility(View.VISIBLE);
                applicantsNumber.setText(layouString);
            }
            make_payments.setVisibility(View.VISIBLE);
            paymentsProgressBar.setVisibility(View.GONE);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView pickUpDate, make_payments, fromTown, toTown, amount, locationDesc, luggage_desc, vehicle, jobStatus, payStatus, invoiceNumber, view_applications, applicantsNumber;
        private ProgressBar paymentsProgressBar;
        private ImageButton editButton, delete;
        private LinearLayout numberOfApplicantsLayout;

        MyViewHolder(View view) {
            super(view);
            pickUpDate = view.findViewById(R.id.pickUpDate);
            fromTown = view.findViewById(R.id.fromTown);
            toTown = view.findViewById(R.id.toTown);
            amount = view.findViewById(R.id.amount);
            locationDesc = view.findViewById(R.id.locationDesc);
            luggage_desc = view.findViewById(R.id.luggage_desc);
            vehicle = view.findViewById(R.id.vehicle);
            payStatus = view.findViewById(R.id.payStatus);
            jobStatus = view.findViewById(R.id.jobStatus);
            invoiceNumber = view.findViewById(R.id.invoiceNumber);
            editButton = view.findViewById(R.id.edit);
            view_applications = view.findViewById(R.id.view_applications);
            delete = view.findViewById(R.id.delete);
            make_payments = view.findViewById(R.id.make_payments);
            paymentsProgressBar = view.findViewById(R.id.paymentsProgressBar);
            numberOfApplicantsLayout = view.findViewById(R.id.numberOfApplicantsLayout);
            applicantsNumber = view.findViewById(R.id.applicantsNumber);
        }
    }
}
