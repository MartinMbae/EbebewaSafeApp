package com.example.ebebewa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.robinhood.ticker.TickerView;

import java.text.MessageFormat;

import com.example.ebebewa.BuildConfig;
import com.example.ebebewa.CheckUpdateActivity;
import com.example.ebebewa.R;
import com.example.ebebewa.utils.BaseActivity;

public class LaunchActivity extends BaseActivity {

    private TickerView ticker3;

    TextView versioName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        ticker3 = findViewById(R.id.ticker1);
        versioName = findViewById(R.id.versionName);
        String versionName = BuildConfig.VERSION_NAME;

        versioName.setText(MessageFormat.format("Version {0}", versionName));

        ticker3.setPreferredScrollingDirection(TickerView.ScrollingDirection.ANY);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               startActivity(new Intent(LaunchActivity.this, CheckUpdateActivity.class));
               finish();
            }
        }, 5000);

    }



    @Override
    protected void onUpdate() {
        String name = (getString(R.string.app_name));
        ticker3.setText(name);
    }


}