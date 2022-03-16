package com.example.ebebewa_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ebebewa_app.activities.HomeActivityClient;

public class JobPostingSuccessActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_posted_success);

    }


    public void closeSuccessPage(View view) {

        Intent closeIntent = new Intent(JobPostingSuccessActivity.this, HomeActivityClient.class);
        closeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(closeIntent);
        finish();
    }
}
