package com.example.ebebewa_app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ebebewa_app.activities.ClientWelcomeActivity;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;

import com.example.ebebewa_app.activities.HomeActivityDriver;
import com.example.ebebewa_app.activities.IntroActivity;
import com.example.ebebewa_app.utils.Constants;
import com.example.ebebewa_app.utils.SharedPref;

public class CheckUpdateActivity extends AppCompatActivity {

    private final String FIRST_TIME = "first_time";
    private final int MY_REQUEST_CODE = 23432;
    private SharedPref sharedPref;
    private LinearLayout noInternetLayout;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_update);
        noInternetLayout = findViewById(R.id.noInternetLayout);
        progress_bar = findViewById(R.id.progress_bar);
        Button try_again = findViewById(R.id.try_again);
        sharedPref = new SharedPref(this);

        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        retry();

    }

    private void retry() {

        progress_bar.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);

        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (IsConnected()) {
            checkForUpdate();
        } else {
            progress_bar.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.VISIBLE);
        }

    }

    private void checkForUpdate() {
        // Creates instance of the manager.
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(CheckUpdateActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener((AppUpdateInfo appUpdateInfo) -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, CheckUpdateActivity.this, MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    progress_bar.setVisibility(View.GONE);
                    noInternetLayout.setVisibility(View.VISIBLE);
                }
            } else {
                goToHomeActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                goToHomeActivity();
            }
        });
    }


    private void goToHomeActivity() {
        String roleId = sharedPref.getRoleID();
        if (roleId == null) roleId = "login";
        if (sharedPref.isFirstTime()) roleId = FIRST_TIME;

        switch (roleId) {
            case Constants.DRIVER_ROLE:
                Intent driverIntent = new Intent(CheckUpdateActivity.this, HomeActivityDriver.class);
                startActivity(driverIntent);
                finish();
                break;

            case Constants.CLIENT_ROLE:
                Intent clientIntent = new Intent(CheckUpdateActivity.this, ClientWelcomeActivity.class);
                startActivity(clientIntent);
                finish();
                break;

            case FIRST_TIME:
                Intent introIntent = new Intent(CheckUpdateActivity.this, IntroActivity.class);
                startActivity(introIntent);
                finish();
                break;

            default:
                startActivity(new Intent(CheckUpdateActivity.this, LoginSelectionActivity.class));
                finish();
                break;
        }
    }

    private boolean IsConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                progress_bar.setVisibility(View.GONE);
                noInternetLayout.setVisibility(View.VISIBLE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
