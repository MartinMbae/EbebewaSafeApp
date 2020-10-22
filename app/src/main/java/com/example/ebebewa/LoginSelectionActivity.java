package com.example.ebebewa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.ebebewa.activities.LoginActivity;
import com.example.ebebewa.activities.registration.helpers.NewClientReg;
import com.example.ebebewa.activities.registration.helpers.NewDriverReg;
import com.example.ebebewa.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
