package com.example.ebebewa_app.activities.registration.steps;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import ernestoyaquello.com.verticalstepperform.Step;
import com.example.ebebewa_app.R;

public class ClientPaymentInformation extends Step<String> {


    private RadioGroup  preffereedPaymentMethodRadioGroup;
    private View coverView;


    public ClientPaymentInformation(String title, String subtitle) {
        super(title, subtitle);
    }

    @Override
    public String getStepData() {

        int selectedPaymentId = preffereedPaymentMethodRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButtonPayment = coverView.findViewById(selectedPaymentId);

        if (radioButtonPayment != null)
            return radioButtonPayment.getText().toString();
        return "";
    }

    @NonNull
    @Override
    protected View createStepContentLayout() {

        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.step_client_payment_details, null, false);

        coverView = view;

        preffereedPaymentMethodRadioGroup = view.findViewById(R.id.radioGroupPayment);

        return view;
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
        String paymentInfo = getStepData();
        return paymentInfo == null || paymentInfo.isEmpty()
                ? getContext().getString(R.string.form_empty_field)
                : paymentInfo;
    }

    @Override
    public void restoreStepData(String data) {

    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {

        return new IsDataValid(true);
    }

}
