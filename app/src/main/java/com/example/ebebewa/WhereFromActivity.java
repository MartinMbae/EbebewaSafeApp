package com.example.ebebewa;


import android.content.Intent;
import android.graphics.Color;
import android.location.Location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebebewa.activities.ClientWelcomeActivity;
import com.example.ebebewa.utils.SharedPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WhereFromActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5445;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker originMarker, destinationMarker;
    private Location currentLocation;
    private boolean firstTimeFlag = true;
    private boolean hideFirstDialog = false;

    private EditText receiver_phone,sender_phone;

    private String destination,origin,sender,receiver;

    LinearLayout layoutBottomSheetWhereFrom, layoutBottomSheetWhereTo;

    Button stage1ProceedBtn,stage2ProceedBtn;
  private   Polyline polylineFinal;

    LatLng destinationLatLang, originLatLang;

    private SharedPref sharedPref;

    private BottomSheetBehavior sheetBehaviorWhereFrom, sheetBehaviorWhereTo;

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult.getLastLocation() == null)
                return;

            currentLocation = locationResult.getLastLocation();
            if (firstTimeFlag && googleMap != null) {
                animateCamera(currentLocation);
                firstTimeFlag = false;
                fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                showMarker(currentLocation);
                fusedLocationProviderClient = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_from);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layoutBottomSheetWhereFrom = findViewById(R.id.bottom_sheetWhereFrom);
        layoutBottomSheetWhereTo = findViewById(R.id.bottom_sheetWhereTo);
        stage1ProceedBtn = findViewById(R.id.nextBtnStage1);
        stage2ProceedBtn = findViewById(R.id.nextBtnStage2);
        receiver_phone = findViewById(R.id.receiver_phone);
        sender_phone = findViewById(R.id.sender_phone);
        sheetBehaviorWhereFrom = BottomSheetBehavior.from(layoutBottomSheetWhereFrom);
        sheetBehaviorWhereTo = BottomSheetBehavior.from(layoutBottomSheetWhereTo);

        sharedPref = new SharedPref(this);

        sender_phone.setText(sharedPref.getPhoneNumber());

        stage2ProceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                receiver = receiver_phone.getText().toString().trim();

                Intent dateIntent = new Intent(WhereFromActivity.this, DateTimeSelectionActivity.class);
                dateIntent.putExtra("destination", destination);
                dateIntent.putExtra("origin",origin);
                dateIntent.putExtra("sender",sender);
                dateIntent.putExtra("receiver", receiver);
                startActivity(dateIntent);
            }
        });

        sheetBehaviorWhereFrom.setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetBehaviorWhereTo.setState(BottomSheetBehavior.STATE_HIDDEN);

        sheetBehaviorWhereFrom.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        if (!hideFirstDialog)
                        sheetBehaviorWhereFrom.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        sheetBehaviorWhereFrom.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        Places.initialize(WhereFromActivity.this, getString(R.string.google_maps_key));



        //Destination
        AutocompleteSupportFragment autocompleteFragmentDestination = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_destination);

        if (autocompleteFragmentDestination != null) {
            autocompleteFragmentDestination.setHint("Provide luggage destination");
            autocompleteFragmentDestination.setCountry("KE");    //country type
            autocompleteFragmentDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG));

            autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {

                    String name =  place.getName()+", "+place.getAddress();

                    destination = name;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autocompleteFragmentDestination.setText(name);
                            String receiverPhone = receiver_phone.getText().toString().trim();
                            if (!TextUtils.isEmpty(receiverPhone)) {
                                stage2ProceedBtn.setEnabled(true);
                            }
                        }
                    },300);



                    destinationLatLang = place.getLatLng();
                    animateCamera(destinationLatLang);
                    showMarkerDestination(destinationLatLang);


                    new WhereFromActivity.TaskDirectionRequest().execute(buildRequestUrl(originLatLang,destinationLatLang));

//                    postDeliveryJobActivity.luggage_origin =name;
//                    setButtonNextVisibility();
                }

                @Override
                public void onError(Status status) {
                }
            });

        }

        receiver_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (destination != null){
                    if (destination.trim().length() > 0){

                        String receiverPhone = receiver_phone.getText().toString().trim();
                        if (receiverPhone.length() > 0){
                            stage2ProceedBtn.setEnabled(true);
                        }else {
                            stage2ProceedBtn.setEnabled(false);
                        }
                    }else {
                        stage2ProceedBtn.setEnabled(false);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //End of destination



        //Origin
        AutocompleteSupportFragment autocompleteFragmentOrigin = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_origin);

        if (autocompleteFragmentOrigin != null) {
            autocompleteFragmentOrigin.setHint("Provide luggage origin");
            autocompleteFragmentOrigin.setCountry("KE");    //country type
            autocompleteFragmentOrigin.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG));

            autocompleteFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {

                    String name =  place.getName()+", "+place.getAddress();

                    origin = name;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autocompleteFragmentOrigin.setText(name);
                            String senderPhone = sender_phone.getText().toString().trim();
                            if (!TextUtils.isEmpty(senderPhone)) {
                                stage1ProceedBtn.setEnabled(true);
                            }

                        }
                    },300);

                    originLatLang = place.getLatLng();
                    animateCamera(originLatLang);
                    showMarkerOrigin(originLatLang);

//                    postDeliveryJobActivity.luggage_origin =name;
//                    setButtonNextVisibility();
                }

                @Override
                public void onError(Status status) {
                }
            });

        }


        sender_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (origin != null){
                    if (origin.trim().length() > 0){

                        String receiverPhone = sender_phone.getText().toString().trim();
                        if (receiverPhone.length() > 0){
                            stage1ProceedBtn.setEnabled(true);
                        }else {
                            stage1ProceedBtn.setEnabled(false);
                        }
                    }else {
                        stage1ProceedBtn.setEnabled(false);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //End of destination

        //End of origin

        stage1ProceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sender = sender_phone.getText().toString().trim();

                hideFirstDialog = true;
                sheetBehaviorWhereFrom.setState(BottomSheetBehavior.STATE_HIDDEN);
                sheetBehaviorWhereTo.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WhereFromActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                Toast.makeText(this, "Permission denied by uses", Toast.LENGTH_SHORT).show();
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startCurrentLocationUpdates();
        }
    }

    private void animateCamera(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng, 16)));
    }

    private void animateCamera(@NonNull LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng,32)));

    }

    private void animateCameraToPath(){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
        builder.include(destinationMarker.getPosition());
        builder.include(originMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        googleMap.animateCamera(cu);


    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng, int zoomLevel) {
        return new CameraPosition.Builder().target(latLng).zoom(zoomLevel).build();
    }

    private void showMarker(@NonNull Location currentLocation) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (originMarker == null)
            originMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng).title("Origin"));
        else
            MarkerAnimation.animateMarkerToGB(originMarker, latLng, new LatLngInterpolator.Spherical());
    }

    private void showMarkerOrigin(@NonNull LatLng latLng) {
        if (originMarker == null)
            originMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng).title("Origin"));
        else
            MarkerAnimation.animateMarkerToGB(originMarker, latLng, new LatLngInterpolator.Spherical());
    }

    private void showMarkerDestination(@NonNull LatLng latLng) {
        if (destinationMarker == null)
            destinationMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng).title("Destination"));
        else {
            MarkerAnimation.animateMarkerToGB(destinationMarker, latLng, new LatLngInterpolator.Spherical());
        }
        animateCameraToPath();
    }

    private String buildRequestUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";
        String APIKEY = getResources().getString(R.string.google_maps_key);

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key="+APIKEY;
        Log.d("TAG", url);
        return url;
    }

    /**
     * Request direction from Google Direction API
     *
     * @param requestedUrl see {@link #buildRequestUrl(LatLng, LatLng)}
     * @return JSON data routes/direction
     */
    private String requestDirection(String requestedUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(requestedUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        httpURLConnection.disconnect();
        return responseString;
    }

    //Get JSON data from Google Direction
    public class TaskDirectionRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);
            //Json object parsing
            WhereFromActivity.TaskParseDirection parseResult = new WhereFromActivity.TaskParseDirection();
            parseResult.execute(responseString);
        }
    }


    //Parse JSON Object from Google Direction API & display it on Map
    public class TaskParseDirection extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonString) {
            List<List<HashMap<String, String>>> routes = null;
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(jsonString[0]);
                DirectionParser parser = new DirectionParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            ArrayList points = null;

            PolylineOptions polylineOptions = null;


            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lng"));

                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15f);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            if (polylineOptions != null) {
                if (polylineFinal != null){
                    polylineFinal.remove();
                }
                polylineFinal =    googleMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_LONG).show();
            }
        }
    }




    @Override
    protected void onStop() {
        super.onStop();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            startCurrentLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient = null;
        googleMap = null;
    }
}