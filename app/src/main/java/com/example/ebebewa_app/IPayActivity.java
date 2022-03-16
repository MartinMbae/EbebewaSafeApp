package com.example.ebebewa_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ipay.iPaycheckout.PaymentActivity;

import com.example.ebebewa_app.utils.Constants;
import com.example.ebebewa_app.utils.SharedPref;

public class IPayActivity extends AppCompatActivity {
    Context context;

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ipay);
        context = this;
        final String invoiceNumber = getIntent().getStringExtra("invoice");
        final String amounts = getIntent().getStringExtra("amount");

        sharedPref = new SharedPref(this);
//        String email = sharedPref.getEmailAddress();
//         email =null;
        String phones = sharedPref.getPhoneNumber();
        if (phones != null)
            if (phones.startsWith("254")) {
                phones = "0" + phones.substring(3);
            }
        callIpay(context, amounts, phones, invoiceNumber);
    }
    // {"status":"aei7p7yrx4ae34","txncd":"OG17EPRW7H","msisdn_id":"Martin Chege","msisdn_idnum":"254705537065","p1":"airtel","p2":"020102292999","p3":"","p4":"900","uyt":"460846136","agt":"","qwh":"2133127300","ifd":"1472875659","afd":"44010542","poi":"1409658232","id":"tetttttt","ivm":"tetttttt","mc":"1.00","channel":"MPESA","sign":"b5c3505a9535a8ac651045f985c17230c7fc075ea6c09bfd53e14ec1e92f951c"}

    public void callIpay(Context context, String amounts, String phone, String invoiceNumber) {
        //data to send to ipay
        String live = "1";
        String mer = "Ebebewa merchant"; //merchant name
        String vid = "tushibe"; //Vendor ID
        String curr = "KES"; //or USD
        String cst = "1"; //email notification
        String crl = "0";
        String autopay = "1";
        String cbk = Constants.BASE_URL + "payment/processPayment?userId=" + sharedPref.getLoggedInUserID(); // this is your callback
        String security_key = "65oiuhg654uhgfd";
        String p1 = "airtel";
        String p2 = "020102292999";
        String p3 = "";
        String p4 = "900";


        String mpesa_status = "1";
        String mbonga_status = "0";
        String airtel_status = "1";
        String easy_status = "1";
        String visa_status = "1";

        Intent data = new Intent(context, PaymentActivity.class);
        data.putExtra("live", live);
        data.putExtra("mer", mer);
        data.putExtra("oid", invoiceNumber);
        data.putExtra("vid", vid);
        data.putExtra("cbk", cbk);
        data.putExtra("key", security_key);
        data.putExtra("amount", amounts);
        data.putExtra("autopay", autopay);
        data.putExtra("p1", p1);
        data.putExtra("p2", p2);
        data.putExtra("p3", p3);
        data.putExtra("p4", p4);
        data.putExtra("currency", curr);
        data.putExtra("cst", cst);
        data.putExtra("crl", crl);
        data.putExtra("phone", phone);
        data.putExtra("mpesa_status", mpesa_status);
        data.putExtra("mbonga_status", mbonga_status);
        data.putExtra("airtel_status", airtel_status);
        data.putExtra("easy_status", easy_status);
        data.putExtra("visa_status", visa_status);
        context.startActivity(data);
        ((Activity) context).finish();
    }
}