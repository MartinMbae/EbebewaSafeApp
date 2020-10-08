package com.example.ebebewa.activities.registration.steps;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ernestoyaquello.com.verticalstepperform.Step;
import com.example.ebebewa.R;
import com.example.ebebewa.models.Vehicle;
import com.example.ebebewa.utils.Constants;

public class DriverTransportInformation extends Step<DriverTransportInformation.TransportInfo> {

    private TextInputEditText plateNumberEditText;
    private RadioGroup preffereedPaymentMethodRadioGroup;
    private Context context;
    private CFAlertDialog alertDialog;
    private TextView typeOfVehicleTextView;
   private View coverView;


    private boolean preferedDriverVehicleType = false;
    private ArrayList<Vehicle> vehicleArrayList;
    private String selectedVehicleId = "0";
    private ProgressDialog progressDialog;

    public DriverTransportInformation(String title, String subtitle, Context context) {
        super(title, subtitle);
        this.context = context;
    }

    @Override
    public TransportInfo getStepData() {

        String plateNumber, payment, vehicleOwned;
        vehicleOwned = selectedVehicleId;
        plateNumber = plateNumberEditText.getText() != null ? plateNumberEditText.getText().toString() : "";
        int selectedPaymentId = preffereedPaymentMethodRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButtonPayment = coverView.findViewById(selectedPaymentId);
        payment = "";
        if (radioButtonPayment != null)
            payment = radioButtonPayment.getText().toString();
        return new TransportInfo(plateNumber, payment, vehicleOwned);
    }

    @NonNull
    @Override
    protected View createStepContentLayout() {

        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.step_driver_transport_details, null, false);

        coverView = view;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Preparing items");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        getVehicleTypes();

        plateNumberEditText = view.findViewById(R.id.plateNumber);
        preffereedPaymentMethodRadioGroup = view.findViewById(R.id.radioGroupPayment);
        typeOfVehicleTextView = view.findViewById(R.id.type_of_vehicle);
        setListenerEditText(plateNumberEditText);
        typeOfVehicleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] vehicleTypes = new String[vehicleArrayList.size()];
                String[] vehicleIDS = new String[vehicleArrayList.size()];
                for (int i = 0; i < vehicleArrayList.size(); i++) {
                    vehicleTypes[i] = vehicleArrayList.get(i).getType();
                    vehicleIDS[i] = vehicleArrayList.get(i).getId();
                }
//                String[] vehicleTypes = {"Truck", "Pick-up", "Van", "Sedan","Saloon", "Probox","Tractor-Trailer"};
                showSingleSelectionDialog("Select Vehicle Owned", vehicleTypes, vehicleIDS);

            }
        });

        return view;
    }

    private void getVehicleTypes() {

        String uri = Constants.BASE_URL + "api/drivers/vehicletype";
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                parseVehicleTypesResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(context, "Error Preparing registering. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(myReq);
    }


    private void parseVehicleTypesResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);

            vehicleArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String type = jsonObject.getString("type");
                String amountPerKm = jsonObject.getString("amount_per_km");
                vehicleArrayList.add(new Vehicle(id, type, amountPerKm));
            }

            progressDialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();

            progressDialog.dismiss();
            Toast.makeText(context, "Error Preparing registering. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    
    private void setListenerEditText(TextInputEditText editText) {

        editText.setSingleLine(true);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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
    public void restoreStepData(TransportInfo data) {
        if (plateNumberEditText != null) plateNumberEditText.setText(data.plateNumber);

    }

    @Override
    protected IsDataValid isStepDataValid(TransportInfo stepData) {

        if (!preferedDriverVehicleType) {
            return new IsDataValid(false, "Select your Vehicle Type");
        }

        if (stepData.plateNumber.length() < 6) {
            return new IsDataValid(false, "Plate number must contain at least 6 characters");
        }
        return new IsDataValid(true);
    }


    private void showSingleSelectionDialog(String title, final String[] vehicleNames, final String[] vehicleIds) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(context);
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        builder.setTitle(title);

        builder.setTextGravity(Gravity.CENTER_HORIZONTAL);

        builder.addButton("Select", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        // Single choice list items
        builder.setSingleChoiceItems(vehicleNames, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedOption = vehicleNames[which];
                selectedVehicleId = vehicleIds[which];
                preferedDriverVehicleType = true;
                typeOfVehicleTextView.setText(selectedOption);
                markAsCompletedOrUncompleted(true);
            }
        });

        // Cancel on background tap
        builder.setCancelable(true);
        alertDialog = builder.show();

    }


    public static class TransportInfo {

        public String plateNumber, payment, vehicleOwned;

        public TransportInfo(String plateNumber, String payment, String vehicleOwned) {
            this.plateNumber = plateNumber;
            this.payment = payment;
            this.vehicleOwned = vehicleOwned;
        }
    }

}
