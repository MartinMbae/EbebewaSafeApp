package com.example.ebebewa.fragments.available_jobs;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import com.example.ebebewa.R;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.Job;

/**
 * Created by Martin Mbae on 14,June,2020.
 */

public class AvailableJobAdapter extends RecyclerView.Adapter<AvailableJobAdapter.MyViewHolder> {

    private List<Job> jobList;
    private HashMap<String, String> vehicleTypesHashMaps;
    private HashMap<String, String> luggageNatureHashMaps;
    private HashMap<String, String> appliedJobsHashMap;
    private DriverAvailableJobsFragment driverAvailableJobsFragment;

    public AvailableJobAdapter(List<Job> jobList, HashMap<String, String> vehicleTypesHashMaps, HashMap<String, String> luggageNatureHashMaps, HashMap<String, String> appliedJobsHashMap, DriverAvailableJobsFragment driverAvailableJobsFragment) {
        this.jobList = jobList;
        this.vehicleTypesHashMaps = vehicleTypesHashMaps;
        this.luggageNatureHashMaps = luggageNatureHashMaps;
        this.driverAvailableJobsFragment = driverAvailableJobsFragment;
        this.appliedJobsHashMap = appliedJobsHashMap;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_available_job_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Job job = jobList.get(position);
        holder.postDate.setText(job.getPost_date());
        holder.fromTown.setText(job.getOrigin_place());
        holder.toTown.setText(job.getDestination_place());
        holder.locationDesc.setText(job.getLocation_description());
        holder.luggage_desc.setText(job.getDescription());

        String jobPostId = job.getId();
        String appliedStatus = appliedJobsHashMap.get(jobPostId);
        //0  ->  Pending
        //1  ->  Successful
        //2  ->  Failed


        Log.d("dddddddddd", "JobID " + jobPostId + "   applied status" + appliedStatus);

        if (appliedStatus != null) {
            if (appliedStatus.equals("0")) {
                holder.applyButton.setEnabled(false);
                holder.applyButton.setText("Applied");

                holder.waitingClientConfirmationLayout.setVisibility(View.VISIBLE);
            } else {
                holder.applyButton.setEnabled(true);
                holder.applyButton.setText("Apply");
                holder.waitingClientConfirmationLayout.setVisibility(View.GONE);
            }
        } else {
            holder.waitingClientConfirmationLayout.setVisibility(View.GONE);
        }

        try {
            double amount = Double.parseDouble(job.getAmount());
            holder.amount.setText(Constants.addCommaToNumber(amount));
        } catch (Exception e) {
            holder.amount.setText(job.getAmount());
        }

        holder.applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverAvailableJobsFragment.applyJob(job.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView postDate, fromTown, toTown, amount, locationDesc, luggage_desc;
        private Button applyButton;
        private LinearLayout waitingClientConfirmationLayout;

        MyViewHolder(View view) {
            super(view);
            postDate = view.findViewById(R.id.postDate);
            fromTown = view.findViewById(R.id.fromTown);
            toTown = view.findViewById(R.id.toTown);
            amount = view.findViewById(R.id.amount);
            locationDesc = view.findViewById(R.id.locationDesc);
            luggage_desc = view.findViewById(R.id.luggage_desc);
            applyButton = view.findViewById(R.id.applyButton);
            waitingClientConfirmationLayout = view.findViewById(R.id.waitingClientConfirmationLayout);

        }
    }
}