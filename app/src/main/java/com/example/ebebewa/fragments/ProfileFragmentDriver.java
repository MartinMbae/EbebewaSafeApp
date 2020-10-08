package com.example.ebebewa.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import com.example.ebebewa.R;
import com.example.ebebewa.activities.HomeActivityDriver;
import com.example.ebebewa.adapters.HighLightArrayAdapter;
import com.example.ebebewa.models.Vehicle;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.SharedPref;
import studios.luxurious.kenya47counties.activities.CountyReturned;
import studios.luxurious.kenya47counties.activities.Kenya47Counties;
import studios.luxurious.kenya47counties.models.County;
import studios.luxurious.kenyanpostalcodes.activities.KenyaPostalCodes;
import studios.luxurious.kenyanpostalcodes.activities.PostalAreaSelected;
import studios.luxurious.kenyanpostalcodes.models.PostalArea;

public class ProfileFragmentDriver extends Fragment {
    private TextView fullNameTxt, national_idTxt, phone_numberTxt, emailTxt, countyTxt, postalCodeTxt, payment_methodTxt, vehicle_owned, vehicle_plate_number;
    private SharedPref sharedPref;
    private boolean hasFilledAll = false;
    private String partFirstName, partLastName, partNational_id, partPhoneNumber, partEmail, partPostalCode, partCounty, partPaymentMethod, partVehicleOwnedId, partVehicleOwnedName = "", partPlateNumber;
    private String tempVehicleId, tempVehicleName;
    private ProgressDialog progressDialog;
    private ArrayList<Vehicle> vehicleArrayList;
    private ImageButton edit_payment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driver_profile, container, false);

        if (getActivity() != null) {

            ImageButton edit_personal_details = view.findViewById(R.id.edit_personal_details);
            ImageButton edit_address_btn = view.findViewById(R.id.edit_address);
            edit_payment = view.findViewById(R.id.paymentEdit);


            fullNameTxt = view.findViewById(R.id.full_name);
            national_idTxt = view.findViewById(R.id.national_id);
            phone_numberTxt = view.findViewById(R.id.phone);
            emailTxt = view.findViewById(R.id.email);
            countyTxt = view.findViewById(R.id.county);
            postalCodeTxt = view.findViewById(R.id.postal_code);
            payment_methodTxt = view.findViewById(R.id.payment_mode);
            vehicle_owned = view.findViewById(R.id.vehicle_owned);
            vehicle_plate_number = view.findViewById(R.id.vehicle_plate_number);

            sharedPref = new SharedPref(getActivity());

            progressDialog = new ProgressDialog(getActivity());

            fillPersonalDetails(sharedPref.getFirstName(), sharedPref.getLastName(), sharedPref.getNationalId(), sharedPref.getPhoneNumber(), sharedPref.getEmailAddress());
            fillAddressDetails(sharedPref.getCountyName(), sharedPref.getPostalCode());
            fillPaymentMethod(sharedPref.getPaymentMethod(), partVehicleOwnedName, sharedPref.getVehicleType(), sharedPref.getPlateNumber());

            edit_payment.setVisibility(View.GONE);

            edit_personal_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditPersonalDetailsDialog();
                }
            });

            edit_address_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditAddressDetailsDialog();
                }
            });

            edit_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditTransportDetaisDialog();
                }
            });

            getVehicleTypes();

        }
        return view;
    }

    private void fillPersonalDetails(String firstName, String lastname, String national_id, String phone, String email) {
        fullNameTxt.setText(MessageFormat.format("{0} {1}", firstName, lastname));
        national_idTxt.setText(national_id);
        phone_numberTxt.setText(phone);
        emailTxt.setText(email);

        partFirstName = firstName;
        partLastName = lastname;
        partNational_id = national_id;
        partPhoneNumber = phone;
        partEmail = email;

        checkDifferences();
    }


    private void fillAddressDetails(String county, String postaCode) {
        countyTxt.setText(county);
        postalCodeTxt.setText(postaCode);

        partCounty = county;
        partPostalCode = postaCode;
        checkDifferences();
    }

    private void fillPaymentMethod(String paymentMethod, String vehicleOwnedName, String vehicleOwnedID, String vehiclePlateNumber) {
        payment_methodTxt.setText(paymentMethod);
        vehicle_owned.setText(vehicleOwnedName);
        vehicle_plate_number.setText(vehiclePlateNumber);

        partPaymentMethod = paymentMethod;
        partVehicleOwnedId = vehicleOwnedID;
        partVehicleOwnedName = vehicleOwnedName;
        partPlateNumber = vehiclePlateNumber;

        hasFilledAll = true;
        checkDifferences();
    }

    private void checkDifferences() {
        if (hasFilledAll) {
            if (!partFirstName.equals(sharedPref.getFirstName()) || !partLastName.equals(sharedPref.getLastName()) || !partNational_id.equals(sharedPref.getNationalId()) || !partPhoneNumber.equals(sharedPref.getPhoneNumber()) || !partEmail.equals(sharedPref.getEmailAddress()) || !partCounty.equals(sharedPref.getCountyName()) || !partPostalCode.equals(sharedPref.getPostalCode()) || !partPaymentMethod.equals(sharedPref.getPaymentMethod()) || !partVehicleOwnedId.equals(sharedPref.getVehicleType()) || !partPlateNumber.equals(sharedPref.getPlateNumber())) {
                showUpdateOption();
            } else {
                hideUpdateOption();
            }
        }
    }

    private void showUpdateOption() {
        if (getActivity() != null)
            ((HomeActivityDriver) getActivity()).showOption(R.id.update);
    }

    private void hideUpdateOption() {
        if (getActivity() != null)
            ((HomeActivityDriver) getActivity()).hideOption(R.id.update);
    }

    private void showEditPersonalDetailsDialog() {

        if (getActivity() != null) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.edit_diaog_driver_personal_details);
            dialog.setCancelable(false);

            final TextInputEditText fnameEditText = dialog.findViewById(R.id.fname);
            final TextInputEditText lnameEditText = dialog.findViewById(R.id.lname);
            final TextInputEditText nationalIdEditText = dialog.findViewById(R.id.national_id);
            final TextInputEditText phoneEditText = dialog.findViewById(R.id.phone);
            final TextInputEditText emailEditText = dialog.findViewById(R.id.email);
            Button saveBtn = dialog.findViewById(R.id.saveBtn);
            Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

            fnameEditText.setText(partFirstName);
            fnameEditText.setSelection(partFirstName.length());

            lnameEditText.setText(partLastName);
            lnameEditText.setSelection(partLastName.length());

            nationalIdEditText.setText(partNational_id);
            nationalIdEditText.setSelection(partNational_id.length());

            phoneEditText.setText(partPhoneNumber);
            phoneEditText.setSelection(partPhoneNumber.length());

            emailEditText.setText(partEmail);
            emailEditText.setSelection(partEmail.length());

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fname = fnameEditText.getText().toString().trim();
                    String lname = lnameEditText.getText().toString().trim();
                    String nat_id = nationalIdEditText.getText().toString().trim();
                    String phone = phoneEditText.getText().toString().trim();
                    String email = emailEditText.getText().toString().trim();
                    fillPersonalDetails(fname, lname, nat_id, phone, email);
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }


    private void showEditAddressDetailsDialog() {
        if (getActivity() != null) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.edit_diaog_driver_address_dialog);
            dialog.setCancelable(false);

            final TextView countyView = dialog.findViewById(R.id.county);
            final TextView postalView = dialog.findViewById(R.id.postalCode);

            countyView.setText(partCounty);
            postalView.setText(partPostalCode);
            Button saveBtn = dialog.findViewById(R.id.saveBtn);
            Button cancelBtn = dialog.findViewById(R.id.cancelBtn);


            countyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        Kenya47Counties.showAllCountiesDialog(getActivity(), "Select County", true, true, false, new CountyReturned() {
                            @Override
                            public void onSelectedCounty(County selectedCounty) {
                                countyView.setText(String.valueOf(selectedCounty.getId()));
                            }
                        });
                }
            });

            postalView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        KenyaPostalCodes.showAllPostalAreas(getActivity(), true, "Select your Postal Address", new PostalAreaSelected() {
                            @Override
                            public void OnPostalAreaSelected(PostalArea selectedPostalArea) {
                                postalView.setText(selectedPostalArea.getAreaCode());
                            }
                        });
                }
            });

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    partCounty = countyView.getText().toString();
                    partPostalCode = postalView.getText().toString();

                    fillAddressDetails(partCounty, partPostalCode);

                    dialog.dismiss();
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }


    private void showEditTransportDetaisDialog() {
        if (getActivity() != null) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.edit_dialog_transport_driver);
            dialog.setCancelable(false);


            final TextInputEditText plateNumberEdtx = dialog.findViewById(R.id.plateNumber);
            plateNumberEdtx.setText(partPlateNumber);
            plateNumberEdtx.setSelection(partPlateNumber.length());

            final RadioButton mpesaBtn = dialog.findViewById(R.id.mpesa);
            RadioButton paypalBtn = dialog.findViewById(R.id.paypal);

            if (partPaymentMethod.contains("Mpesa")) {
                mpesaBtn.setChecked(true);
            } else {
                paypalBtn.setChecked(true);
            }

            Button saveBtn = dialog.findViewById(R.id.saveBtn);
            Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

            Spinner vehicle_type_spinner = dialog.findViewById(R.id.vehicle_type_spinner);
            setUpVehicleSpinner(vehicle_type_spinner);
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mpesaBtn.isChecked()) {
                        partPaymentMethod = "Mpesa";
                    } else {
                        partPaymentMethod = "Paypal";
                    }
                    partPlateNumber = plateNumberEdtx.getText().toString().trim();
                    partVehicleOwnedId = tempVehicleId;
                    partVehicleOwnedName = tempVehicleName;
                    fillPaymentMethod(partPaymentMethod, partVehicleOwnedName, partVehicleOwnedId, partPlateNumber);

                    dialog.dismiss();
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
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

            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
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

                if (id.equals(sharedPref.getVehicleType())) {
                    partVehicleOwnedName = type;
                    partVehicleOwnedId = id;
                    tempVehicleId = id;
                    tempVehicleName = type;
                    vehicle_owned.setText(partVehicleOwnedName);
                }
            }
            edit_payment.setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUpVehicleSpinner(final Spinner vehicle_type_spinner) {

        int selectedId = 0;
        final List<String> list = new ArrayList<>();
        final List<String> vehicle_ids = new ArrayList<>();
        for (int i = 0; i < vehicleArrayList.size(); i++) {
            list.add(vehicleArrayList.get(i).getType());
            vehicle_ids.add(vehicleArrayList.get(i).getId());
            if (vehicleArrayList.get(i).getId().equals(partVehicleOwnedId)) {
                selectedId = i;
            }
        }
        list.add("Select Vehicle Type");

        final HighLightArrayAdapter hintAdapter = new HighLightArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        vehicle_type_spinner.setAdapter(hintAdapter);
        vehicle_type_spinner.setSelection(selectedId);
        hintAdapter.setSelection(selectedId);

        vehicle_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < vehicle_ids.size()) {
                    tempVehicleId = vehicle_ids.get(position);
                    hintAdapter.setSelection(position);
                    tempVehicleName = list.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void updateDriver() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("first_name", partFirstName);
        jsonObject.addProperty("last_name", partLastName);
        jsonObject.addProperty("national_id", partNational_id);
        jsonObject.addProperty("phone", partPhoneNumber);
        jsonObject.addProperty("email", partEmail);
        jsonObject.addProperty("post_code", partPostalCode);
        jsonObject.addProperty("country", partCounty);
        jsonObject.addProperty("payment_mode", partPaymentMethod);

        submitData(jsonObject);

    }

    private void submitData(JsonObject jsonObject) {
        progressDialog.setMessage("Updating");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<JsonObject> call = service.updateUser(sharedPref.getLoggedInUserID(), jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);
                        String status = obj.getString("status");
                        String message = obj.getString("message");
                        if (status.equals("true")) {
                            sharedPref.setFirstName(partFirstName);
                            sharedPref.setLastName(partLastName);
                            sharedPref.setNationalId(partNational_id);
                            sharedPref.setPhoneNumber(partPhoneNumber);
                            sharedPref.setEmailAddress(partEmail);
                            sharedPref.setPostalCode(partPostalCode);
                            sharedPref.setCountyName(partCounty);
                            sharedPref.setPaymentMethod(partPaymentMethod);
                            checkDifferences();

                            if (sharedPref.getVehicleType().equals(partVehicleOwnedId) && sharedPref.getPlateNumber().equals(partPlateNumber)) {
                                progressDialog.dismiss();

                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            } else {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("vehicle_type", partVehicleOwnedId);
                                jsonObject.addProperty("plate_number", partPlateNumber);
                                submitTransportData(jsonObject);
                            }

                        } else {
                            progressDialog.dismiss();

                            showSuccessfulDialog("Oops", "Something went wrong. Please try again ");
                        }
                    } catch (JSONException e) {
                        progressDialog.dismiss();

                        e.printStackTrace();
                        showSuccessfulDialog("Failed", "Something went wrong. Please try again " + e.getMessage());
                    }
                } else {
                    progressDialog.dismiss();

                    showSuccessfulDialog("Failed", "Something went wrong. Please try again ");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                alertDialog.dismiss();
                progressDialog.dismiss();
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());

            }

        });
    }

    private void submitTransportData(JsonObject jsonObject) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceTransportData service = retrofit.create(ApiServiceTransportData.class);
        Call<JsonObject> call = service.updateUser(sharedPref.getLoggedInUserID(), jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);
                        String status = obj.getString("status");
                        String message = obj.getString("message");
                        if (status.equals("true")) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            sharedPref.setPlateNumber(partPlateNumber);
                            sharedPref.setVehicleType(partVehicleOwnedId);
                            checkDifferences();

                        } else {
                            showSuccessfulDialog("Oops", "Something went wrong. Please try again ");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showSuccessfulDialog("Failed", "Something went wrong. Please try again " + e.getMessage());
                    }
                } else {
                    showSuccessfulDialog("Failed", "Something went wrong. Please try again ");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                alertDialog.dismiss();
                progressDialog.dismiss();
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());

            }

        });
    }

    void showSuccessfulDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private interface ApiService {
        @Headers({"Content-Type: application/json"})
        @PUT("api/drivers/updateDriver/{id}")
        Call<JsonObject> updateUser(@Path("id") String id, @Body JsonObject body);
    }

    private interface ApiServiceTransportData {
        @Headers({"Content-Type: application/json"})
        @PUT("api/drivers/updateTransportDetails/{id}")
        Call<JsonObject> updateUser(@Path("id") String id, @Body JsonObject body);
    }

}
