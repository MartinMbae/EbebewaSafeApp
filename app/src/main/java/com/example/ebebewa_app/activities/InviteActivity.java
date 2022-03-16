package com.example.ebebewa_app.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ebebewa_app.R;

/**
 * Created by Martin Mbae on 30,August,2020.
 */

public class InviteActivity extends AppCompatActivity {
    TextView inviteCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        inviteCode = findViewById(R.id.inviteCode);
    }
}
