package com.example.ebebewa.fragments.available_jobs;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.example.ebebewa.R;

/**
 * Created by Martin Mbae on 14,June,2020.
 */

public class BiddingJobAdapter extends RecyclerView.Adapter<BiddingJobAdapter.MyViewHolder> {

    Context context;
    private List<BidJob> bidJobList;
    private BiddingHistoryFragment biddingHistoryFragment;

    public BiddingJobAdapter(List<BidJob> bidJobList, Context context, BiddingHistoryFragment biddingHistoryFragment) {
        this.bidJobList = bidJobList;
        this.context = context;
        this.biddingHistoryFragment = biddingHistoryFragment;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_bidding_job_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final BidJob bidJob = bidJobList.get(position);
        holder.date.setText(bidJob.getCreated_date());

        String deliveryStatus = bidJob.getDelivery_status();
        String clientConfirmedReceipt = bidJob.getClient_receipt_confirmation();
        String bidStatus = bidJob.getBid_status();

        boolean displayClientInfo = false;
        String appSatus;
        if (bidJob.getStatus().equals("1")) {
            appSatus = "Successful";
            displayClientInfo = true;
            holder.view_more.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.appStatus.setTextColor(context.getColor(R.color.green));
            }
            if (deliveryStatus.equalsIgnoreCase("0")) {
                holder.view_more.setText("Confirm Delivery");
                holder.view_more.setVisibility(View.VISIBLE);
                holder.view_more.setOnClickListener(v -> confirmDelivery(bidJob.getPost()));
            }

        } else if (bidJob.getStatus().equals("0")) {
            appSatus = "Pending";
            holder.view_more.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.appStatus.setTextColor(context.getColor(R.color.black));
            }
        } else {
            appSatus = "Unsuccessful";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.appStatus.setTextColor(context.getColor(R.color.red));
            }
            holder.view_more.setVisibility(View.GONE);
        }

        String deliveryStatusString;

        if (deliveryStatus.equalsIgnoreCase("0") && bidStatus.equalsIgnoreCase("1")) {
            deliveryStatusString = "On Transit";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.delStat.setTextColor(context.getColor(R.color.orange));
            }
        } else if (clientConfirmedReceipt.equalsIgnoreCase("1") && deliveryStatus.equalsIgnoreCase("1") && bidStatus.equalsIgnoreCase("1")) {
            deliveryStatusString = "Delivered";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.delStat.setTextColor(context.getColor(R.color.green));
            }
        } else if (clientConfirmedReceipt.equalsIgnoreCase("0") && deliveryStatus.equalsIgnoreCase("1") && bidStatus.equalsIgnoreCase("1")) {
            deliveryStatusString = "Delivered but client hasn't confirmed";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.delStat.setTextColor(context.getColor(R.color.green));
            }
        } else if (bidStatus.equalsIgnoreCase("0")) {
            deliveryStatusString = "N/A";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.delStat.setTextColor(context.getColor(R.color.black));
            }
        } else {
            deliveryStatusString = "Awaiting Review by client";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.delStat.setTextColor(context.getColor(R.color.red));
            }
        }


        holder.appStatus.setText(appSatus);
        holder.delStat.setText(deliveryStatusString);

        boolean finalDisplayClientInfo = displayClientInfo;
        holder.viewInfo.setOnClickListener(v -> biddingHistoryFragment.showClientInformation(bidJob.getPost(), finalDisplayClientInfo, bidJob.getDue_date(), bidJob.getOrigin_place(), bidJob.getDestination_place(), bidJob.getAmount(), bidJob.getDescription(), bidJob.getLocation_description()));

//        String jobPostId = job.getId();
//        String appliedStatus = appliedJobsHashMap.get(jobPostId);
//        //0  ->  Pending
//        //1  ->  Successful
//        //2  ->  Failed

    }

    @Override
    public int getItemCount() {
        return bidJobList.size();
    }

    private void confirmDelivery(String postId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Confirm Delivery of this luggage");
        alertDialogBuilder.setMessage("Would you like to confirm that you have delivered this luggage");
        alertDialogBuilder.setPositiveButton("Yes, I have delivered", (dialog, which) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("post", postId);

            dialog.dismiss();
            biddingHistoryFragment.confirmDelivery(jsonObject);
        });
        alertDialogBuilder.setNeutralButton("No, I have not yet delivered", (dialog, which) -> dialog.dismiss());
        alertDialogBuilder.show();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView date, appStatus, delStat, viewInfo;
        private TextView view_more;

        MyViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.Date);
            appStatus = view.findViewById(R.id.appStatus);
            delStat = view.findViewById(R.id.delStat);
            view_more = view.findViewById(R.id.view_more);
            viewInfo = view.findViewById(R.id.viewInfo);
        }
    }


}