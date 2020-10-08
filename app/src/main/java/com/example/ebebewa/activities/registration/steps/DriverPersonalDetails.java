package com.example.ebebewa.activities.registration.steps;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.ebebewa.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ernestoyaquello.com.verticalstepperform.Step;
import com.example.ebebewa.R;
import com.example.ebebewa.activities.registration.helpers.NewClientReg;
import com.example.ebebewa.activities.registration.helpers.NewDriverReg;


public class DriverPersonalDetails extends Step<DriverPersonalDetails.PersonalDetails> {


    private TextInputEditText firstNameEditText, lastNameEditText, nationalIDeditText, phoneEditText, emailEditText;
    public ImageButton uploadProfile;
    public Uri profileUri;

    private NewDriverReg newDriverReg;
    private NewClientReg newClientReg;
    private Context context;



    private String role;

    public DriverPersonalDetails(String title, String subtitle, NewDriverReg newDriverReg, NewClientReg newClientReg, Context context, String role) {
        super(title, subtitle);
        this.newDriverReg = newDriverReg;
        this.newClientReg = newClientReg;
        this.context = context;
        this.role = role;
    }

    public void setUri(Uri uri) {
        profileUri = uri;
        uploadProfile.setImageURI(profileUri);
        markAsCompletedOrUncompleted(true);
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }
        return extension;
    }

    @Override
    public PersonalDetails getStepData() {

        String f, l, n, p, e, base64;
//
        f = firstNameEditText.getText() != null ? firstNameEditText.getText().toString() : "";
        l = lastNameEditText.getText() != null ? lastNameEditText.getText().toString() : "";
        n = nationalIDeditText.getText() != null ? nationalIDeditText.getText().toString() : "";
        p = phoneEditText.getText() != null ? phoneEditText.getText().toString() : "";
        e = emailEditText.getText() != null ? emailEditText.getText().toString() : "";

        if (profileUri == null) {
            base64 = "";
        } else {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), profileUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream);
                byte[] byteArray = outputStream.toByteArray();

                base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (IOException ex) {
                ex.printStackTrace();
                base64 = "";
            }
            base64 = "data:image/" + getMimeType(context, profileUri) + ";base64," + base64;
        }
        return new PersonalDetails(f, l, n, p, e, base64);
    }

    @NonNull
    @Override
    protected View createStepContentLayout() {

        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.step_driver_personal_details, null, false);

        firstNameEditText = view.findViewById(R.id.fname);
        lastNameEditText = view.findViewById(R.id.lname);
        nationalIDeditText = view.findViewById(R.id.nationalId);
        phoneEditText = view.findViewById(R.id.telephone);
        emailEditText = view.findViewById(R.id.email);
        uploadProfile = view.findViewById(R.id.profileUlpoad);

        setListenerEditText(firstNameEditText);
        setListenerEditText(lastNameEditText);
        setListenerEditText(nationalIDeditText);
        setListenerEditText(phoneEditText);
        setListenerEditText(emailEditText);

        uploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newClientReg != null)
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(newClientReg);
                else
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(newDriverReg);
            }
        });
        return view;
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
    public void restoreStepData(PersonalDetails data) {
        if (firstNameEditText != null) firstNameEditText.setText(data.firstName);
        if (lastNameEditText != null) lastNameEditText.setText(data.lastName);
        if (nationalIDeditText != null) nationalIDeditText.setText(data.nationalId);
        if (phoneEditText != null) phoneEditText.setText(data.phone);
        if (emailEditText != null) emailEditText.setText(data.email);

    }

    @Override
    protected IsDataValid isStepDataValid(PersonalDetails stepData) {

        if (stepData.firstName.length() < 3) {
            return new IsDataValid(false, "Firstname must contain 3 or more characters");
        }
        if (stepData.lastName.length() < 3) {
            return new IsDataValid(false, "Lastname must contain 3 or more characters");
        }
        if (stepData.nationalId.length() < 6) {
            return new IsDataValid(false, "ID must contain 6 or more characters");
        }
        if (TextUtils.isEmpty(stepData.phone)) {
            return new IsDataValid(false, "Provide Phone Number");
        }

        if (!checkPhoneNumber(stepData.phone).equals("true")) {
            return new IsDataValid(false, checkPhoneNumber(stepData.phone));
        }



        if (profileUri == null && role.equals(Constants.DRIVER_ROLE)) {

            return new IsDataValid(false, "Upload your profile picture");

        }

        return new IsDataValid(true);
    }


    private String checkPhoneNumber(String phone) {
        if (!phone.trim().startsWith("254")) {
            return "Phone Number must start with '254'";
        } else if (phone.trim().length() != 12) {
            return "Invalid length(Phone Number)";
        } else {
            return "true";
        }
    }


    public static class PersonalDetails {

        public String firstName, lastName, nationalId, phone, email, photoBase64;

        public PersonalDetails(String firstName, String lastName, String nationalId, String phone, String email, String photoBase64) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.nationalId = nationalId;
            this.phone = phone;
            this.email = email;
            this.photoBase64 = photoBase64;
        }
    }

}
