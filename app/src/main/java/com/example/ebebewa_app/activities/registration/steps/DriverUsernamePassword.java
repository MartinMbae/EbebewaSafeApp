package com.example.ebebewa_app.activities.registration.steps;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import ernestoyaquello.com.verticalstepperform.Step;
import com.example.ebebewa_app.R;

public class DriverUsernamePassword extends  Step<DriverUsernamePassword.UsernamePassword>  {


    private TextInputEditText usernameEdittext,passwordEdittext, confirmEdittext;


    public DriverUsernamePassword(String title, String subtitle) {
        super(title, subtitle);

    }

    @Override
    public UsernamePassword getStepData() {

        String u,p,c;
//
        u = usernameEdittext.getText() != null ? usernameEdittext.getText().toString() :  "";
        p = passwordEdittext.getText() != null ? passwordEdittext.getText().toString() :  "";
        c = confirmEdittext.getText() != null ? confirmEdittext.getText().toString() :  "";

        return new UsernamePassword(u,p,c);
    }

    @NonNull
    @Override
    protected View createStepContentLayout() {

        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.step_driver_username_password, null, false);

        usernameEdittext = view.findViewById(R.id.username);
        passwordEdittext = view.findViewById(R.id.password);
        confirmEdittext = view.findViewById(R.id.confirm_password);

        setListenerEditText(usernameEdittext);
        setListenerEditText(passwordEdittext);
        setListenerEditText(confirmEdittext);

        return view;
    }

    private void setListenerEditText(TextInputEditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                markAsCompletedOrUncompleted(true);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                getFormView().goToNextStep(true);
                return false;
            }
        });

    }



    @Override
    protected void onStepOpened(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // No need to do anything here
    }


    @Override
    public String getStepDataAsHumanReadableString() {
        return "All fields are set";
    }

    @Override
    public void restoreStepData(UsernamePassword data) {
        if (usernameEdittext != null) usernameEdittext.setText(data.username);
        if (passwordEdittext != null) passwordEdittext.setText(data.password);
        if (confirmEdittext != null) confirmEdittext.setText(data.confirmPassword);

    }

    @Override
    protected IsDataValid isStepDataValid(UsernamePassword stepData) {

        if (stepData.username.length() < 3){
            return new IsDataValid(false,"Username must contain 3 or more characters");
        }
        if (stepData.password == null){
            return new IsDataValid(false,"Provide your password required");
        }
        if (stepData.password.length() < 8) {
            return new IsDataValid(false, "Password must contain 8 or more characters");
        }
        if (stepData.confirmPassword.equals("")){
            return new IsDataValid(false,"Confirm your password");
        }
        if (stepData.password.equals(stepData.confirmPassword)){
            return new IsDataValid(true);
        }else {
            return new IsDataValid(false,"Passwords failed to match");
        }

    }

public static class UsernamePassword{

    public String username, password, confirmPassword;

    public UsernamePassword(String username, String password, String confirmPassword) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}

}
