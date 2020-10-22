package com.example.ebebewa.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ebebewa.utils.Constants;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import com.example.ebebewa.R;
import com.example.ebebewa.fragments.HomeFragmentDriver;
import com.example.ebebewa.fragments.ProfileFragmentDriver;
import com.example.ebebewa.fragments.SettingsFragment;
import com.example.ebebewa.fragments.available_jobs.AvailableJobsFragmentHolder;
import com.example.ebebewa.utils.SharedPref;
import com.yayandroid.locationmanager.base.LocationBaseActivity;
import com.yayandroid.locationmanager.configuration.Configurations;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.ProcessType;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeActivityDriver extends LocationBaseActivity {


    private static final String TAG_HOME = "home";
    private static final String TAG_PROFILE = "profile";
    private static final String TAG_AVAILABLE_JOBS = "available_jobs";
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_HOME;
    SharedPref sharedPref;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private CircleImageView profilePicture;
    private String[] activityTitles;

    private Handler mHandler;
    private Menu menu;

    private int available_job_position = 0;

    private ProfileFragmentDriver profileFragmentDriver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_driver);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        sharedPref = new SharedPref(this);

        // Navigation view header
        View navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.username);
        txtWebsite = navHeader.findViewById(R.id.loggedInInfo);
        profilePicture = navHeader.findViewById(R.id.profilePicture);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles_driver);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        getLocation();
    }

    private void loadNavHeader() {
        txtName.setText(sharedPref.getUserName());
        txtWebsite.setText(R.string.logged_as_driver_text);

        Picasso.get()
                .load(sharedPref.getPassportPhoto())
                .error(this.getDrawable(R.drawable.notfound))
                .placeholder(getDrawable(R.drawable.placeholder))
                .into(profilePicture);
//        navigationView.getMenu().getItem(2).setActionView(R.layout.menu_dot);
    }

    private void loadHomeFragment() {
        selectNavMenu();
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                if (fragment != null)
                    fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);

                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        mHandler.post(mPendingRunnable);


        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return new HomeFragmentDriver();
            case 1:
                profileFragmentDriver = new ProfileFragmentDriver();
                return profileFragmentDriver;
            case 2:
                return new AvailableJobsFragmentHolder(available_job_position);
            case 3:
                return new SettingsFragment();
            default:
                return null;
        }
    }

    private void setToolbarTitle() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_profile:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PROFILE;
                        break;
                    case R.id.nav_available_jobs:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_AVAILABLE_JOBS;
                        break;
//                    case R.id.nav_invite:
//                        startActivity(new Intent(HomeActivityDriver.this, InviteActivity.class));
//                        drawer.closeDrawers();
//                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(HomeActivityDriver.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_log_out:

                        logOut();

                        break;
                    case R.id.nav_privacy_policy:
                        startActivity(new Intent(HomeActivityDriver.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;

                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        //noinspection deprecation
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    public void prepareAvailableJobsFragment(int position) {
        navItemIndex = 2;
        CURRENT_TAG = TAG_AVAILABLE_JOBS;
        available_job_position = position;
        loadHomeFragment();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHomeFragment();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        if (navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        this.menu = menu;
        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        if (navItemIndex == 1) {
            getMenuInflater().inflate(R.menu.update, menu);
        }
        return true;
    }

    public void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    public void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logOut();
            return true;
        }
        if (id == R.id.update) {
            profileFragmentDriver.updateDriver();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        sharedPref.logOut();
        Intent loginIntent = new Intent(HomeActivityDriver.this, LoginActivity.class);
        loginIntent.putExtra("class", Constants.DRIVER_ROLE);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

    }

    @Override
    public LocationConfiguration getLocationConfiguration() {
        return Configurations.defaultConfiguration("Give us the permission!", "Would you mind to turn GPS on?");
    }

    @Override
    public void onLocationChanged(Location location) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(HomeActivityDriver.this, Locale.getDefault());

        double   latitude = location.getLatitude();
        double  longitude = location.getLongitude();

        String address = "";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                String addressLoc = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String county = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
//                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                if (country.equals("Kenya")) {
                    address = (county + " " + addressLoc);
                }else {
                    address = "Could not get location";
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String appendValue = location.getLatitude() + ", " + location.getLongitude() + ", " + address + "\n";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("latitude",  location.getLatitude());
        jsonObject.addProperty("longitude",  location.getLongitude());
        jsonObject.addProperty("current_location", address);
        submitLocationUpdates(jsonObject);

    }

    @Override
    public void onLocationFailed(@FailType int failType) {
    }

    @Override
    public void onProcessTypeChanged(@ProcessType int processType) {
    }


    private void submitLocationUpdates(JsonObject jsonObject) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<JsonObject> call = service.updateLocationDetails(sharedPref.getLoggedInUserID(), jsonObject);
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
            }

        });
    }

    void showSuccessfulDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivityDriver.this);
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
        @PUT("api/drivers/updateDriverLocation/{id}")
        Call<JsonObject> updateLocationDetails(@Path("id") String driver_id, @Body JsonObject body);
    }



}