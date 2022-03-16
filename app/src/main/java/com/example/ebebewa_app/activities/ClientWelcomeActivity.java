package com.example.ebebewa_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ebebewa_app.R;
import com.example.ebebewa_app.WhereFromActivity;

public class ClientWelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_welcome);
    }

    public void askForTransport(View view) {
        startActivity(new Intent(ClientWelcomeActivity.this, WhereFromActivity.class));
    }

    public void skip(View view) {
        Intent closeIntent = new Intent(ClientWelcomeActivity.this, HomeActivityClient.class);
        closeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(closeIntent);
        finish();
    }
}
