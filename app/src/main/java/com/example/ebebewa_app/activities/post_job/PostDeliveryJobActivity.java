package com.example.ebebewa_app.activities.post_job;


import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import com.example.ebebewa_app.R;

public class PostDeliveryJobActivity extends AppCompatActivity {

    public String pickUpDate, luggage_origin, luggage_destination, selectedVehicleId, selectedLuggageNature, calculatedAmount, sendNumber, receiverNumber, locationDescription, luggageDescription;
    StepView stepView;
    FrameLayout frameLayout;

    public PostDeliveryJobActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_delivery_job);
        stepView = findViewById(R.id.step_view);

        frameLayout = findViewById(R.id.frame);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        List<String> steps = new ArrayList<>();
        steps.add("Step 1");
        steps.add("Step 2");
        steps.add("Step 3");
        steps.add("Finish");
        stepView.setSteps(steps);

        goToFragment(new PostFragmentStep1(this), 0);

    }

    public void goToFragment(Fragment nextFragment, int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, nextFragment).commit();
        stepView.go(id, true);
    }

    @Override
    public void onBackPressed() {

        if (stepView.getCurrentStep() == 0) {
            if (pickUpDate == null && luggage_origin == null) {
                PostDeliveryJobActivity.super.onBackPressed();
                return;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDeliveryJobActivity.this);
        builder.setTitle("Are you sure?");
        builder.setMessage("If you choose to go back, you will lose all the details you have provided for this job. Are you sure you want to go back?");
        builder.setCancelable(false);
        builder.setNeutralButton("Resume Posting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PostDeliveryJobActivity.super.onBackPressed();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
