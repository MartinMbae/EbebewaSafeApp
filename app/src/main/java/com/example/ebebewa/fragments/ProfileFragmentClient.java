package com.example.ebebewa.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import com.example.ebebewa.R;
import com.example.ebebewa.activities.HomeActivityClient;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.SharedPref;
import studios.luxurious.kenya47counties.activities.CountyReturned;
import studios.luxurious.kenya47counties.activities.Kenya47Counties;
import studios.luxurious.kenya47counties.models.County;
import studios.luxurious.kenyanpostalcodes.activities.KenyaPostalCodes;
import studios.luxurious.kenyanpostalcodes.activities.PostalAreaSelected;
import studios.luxurious.kenyanpostalcodes.models.PostalArea;

public class ProfileFragmentClient extends Fragment {

    private TextView fullNameTxt, national_idTxt, phone_numberTxt, emailTxt, countyTxt, postalCodeTxt, payment_methodTxt;
    private SharedPref sharedPref;
    private boolean hasFilledAll = false;
    private String partFirstName, partLastName, partNational_id, partPhoneNumber, partEmail, partPostalCode, partCounty, partPaymentMethod;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_client_profile, container, false);


        if (getActivity() != null) {

            ImageButton edit_personal_details = view.findViewById(R.id.edit_personal_details);
            ImageButton edit_address_btn = view.findViewById(R.id.edit_address);
            ImageButton edit_payment = view.findViewById(R.id.paymentEdit);

            fullNameTxt = view.findViewById(R.id.full_name);
            national_idTxt = view.findViewById(R.id.national_id);
            phone_numberTxt = view.findViewById(R.id.phone);
            emailTxt = view.findViewById(R.id.email);
            countyTxt = view.findViewById(R.id.county);
            postalCodeTxt = view.findViewById(R.id.postal_code);
            payment_methodTxt = view.findViewById(R.id.payment_mode);

            sharedPref = new SharedPref(getActivity());

            progressDialog = new ProgressDialog(getActivity());


            fillPersonalDetails(sharedPref.getFirstName(), sharedPref.getLastName(), sharedPref.getNationalId(), sharedPref.getPhoneNumber(), sharedPref.getEmailAddress());
            fillAddressDetails(sharedPref.getCountyName(), sharedPref.getPostalCode());
            fillPaymentMethod(sharedPref.getPaymentMethod());


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
                    showEditPaymentDetails();
                }
            });

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

    private void fillPaymentMethod(String paymentMethod) {
        payment_methodTxt.setText(paymentMethod);

        partPaymentMethod = paymentMethod;

        hasFilledAll = true;
        checkDifferences();
    }

    private void checkDifferences() {
        if (hasFilledAll) {
            if (!partFirstName.equals(sharedPref.getFirstName()) || !partLastName.equals(sharedPref.getLastName()) || !partNational_id.equals(sharedPref.getNationalId()) || !partPhoneNumber.equals(sharedPref.getPhoneNumber()) || !partEmail.equals(sharedPref.getEmailAddress()) || !partCounty.equals(sharedPref.getCountyName()) || !partPostalCode.equals(sharedPref.getPostalCode()) || !partPaymentMethod.equals(sharedPref.getPaymentMethod())) {
                showUpdateOption();
            } else {
                hideUpdateOption();
            }
        }
    }

    private void showUpdateOption() {
        if (getActivity() != null)
            ((HomeActivityClient) getActivity()).showOption(R.id.update);
    }

    private void hideUpdateOption() {
        if (getActivity() != null)
            ((HomeActivityClient) getActivity()).hideOption(R.id.update);
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


    private void showEditPaymentDetails() {
        if (getActivity() != null) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.edit_dialog_paymet_info);
            dialog.setCancelable(false);

            final RadioButton mpesaBtn = dialog.findViewById(R.id.mpesa);
            RadioButton paypalBtn = dialog.findViewById(R.id.paypal);

            if (partPaymentMethod.contains("Mpesa")) {
                mpesaBtn.setChecked(true);
            } else {
                paypalBtn.setChecked(true);
            }

            Button saveBtn = dialog.findViewById(R.id.saveBtn);
            Button cancelBtn = dialog.findViewById(R.id.cancelBtn);


            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mpesaBtn.isChecked()) {
                        partPaymentMethod = "Mpesa";
                    } else {
                        partPaymentMethod = "Paypal";
                    }

                    fillPaymentMethod(partPaymentMethod);

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

    public void updateClient() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("first_name", partFirstName);
        jsonObject.addProperty("last_name", partLastName);
        jsonObject.addProperty("national_id", partNational_id);
        jsonObject.addProperty("phone", partPhoneNumber);
        jsonObject.addProperty("email", partEmail);
        jsonObject.addProperty("post_code", partPostalCode);
        jsonObject.addProperty("country", partCounty);
        jsonObject.addProperty("passport_photo", sharedPref.getPassportPhoto());
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
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String response_string = response.body().toString();
                    try {
                        JSONObject obj = new JSONObject(response_string);
                        String status = obj.getString("status");
                        String message = obj.getString("message");
                        if (status.equals("true")) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            sharedPref.setFirstName(partFirstName);
                            sharedPref.setLastName(partLastName);
                            sharedPref.setNationalId(partNational_id);
                            sharedPref.setPhoneNumber(partPhoneNumber);
                            sharedPref.setEmailAddress(partEmail);
                            sharedPref.setPostalCode(partPostalCode);
                            sharedPref.setCountyName(partCounty);
                            sharedPref.setPaymentMethod(partPaymentMethod);
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
                showSuccessfulDialog("Failed", "Something went wrong. Please try again." + t.getMessage());
                progressDialog.dismiss();
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
        @PUT("api/clients/updateClient/{id}")
        Call<JsonObject> updateUser(@Path("id") String id, @Body JsonObject body);
    }

}
