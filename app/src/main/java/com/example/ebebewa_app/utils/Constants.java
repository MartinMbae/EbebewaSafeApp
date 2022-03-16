package com.example.ebebewa_app.utils;

import java.text.DecimalFormat;

/**
 * Created by Martin Mbae on 19,May,2020.
 */
public class Constants {

    public static final String BASE_URL = "https://www.ebebewa.com/index.php/";
    public static final String DRIVER_ROLE = "3";
    public static final String CLIENT_ROLE = "4";
    public static final int POST_JOB_ACTIVITY_REQUEST_CODE = 236;

    public static String addCommaToNumber(double number) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String number_string = formatter.format(number);
        return "Ksh. " + number_string;
    }

    public static final String AVAILABLE_JOBS = "available_jobs";
    public static final String TRANSIT_JOBS = "transit_jobs";
    public static final String DELIVERED_JOBS = "delivered_jobs";
}
