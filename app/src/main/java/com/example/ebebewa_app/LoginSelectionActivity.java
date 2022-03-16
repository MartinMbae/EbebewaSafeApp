package com.example.ebebewa_app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ebebewa_app.activities.LoginActivity;
import com.example.ebebewa_app.activities.registration.helpers.NewClientReg;
import com.example.ebebewa_app.activities.registration.helpers.NewDriverReg;
import com.example.ebebewa_app.utils.Constants;

public class LoginSelectionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_selection);
    }

    public void LoginClient(View view) {
        Intent loginIntent = new Intent(LoginSelectionActivity.this, LoginActivity.class);
        loginIntent.putExtra("class", Constants.CLIENT_ROLE);
        startActivity(loginIntent);
    }
    public void LoginDriver(View view) {
        Intent loginIntent = new Intent(LoginSelectionActivity.this, LoginActivity.class);
        loginIntent.putExtra("class", Constants.DRIVER_ROLE);
        startActivity(loginIntent);
    }

    public void toRegister(View view) {
        ClickSignUp();
    }


    //The method for opening the registration page and another processes or checks for registering
    private void ClickSignUp() {

        final Dialog dialog = new Dialog(LoginSelectionActivity.this);

//        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.register, null);
        dialog.setContentView(dialogView);

        Button register_driver = dialogView.findViewById(R.id.reg_register_driver);
        Button register_client = dialogView.findViewById(R.id.reg_register_client);


        register_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(LoginSelectionActivity.this, NewClientReg.class));
            }
        });

        register_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(LoginSelectionActivity.this, NewDriverReg.class));
            }
        });

        dialog.show();


    }

}
