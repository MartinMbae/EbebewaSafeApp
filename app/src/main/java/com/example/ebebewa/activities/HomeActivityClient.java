package com.example.ebebewa.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ebebewa.LoginSelectionActivity;
import com.example.ebebewa.WhereFromActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import com.example.ebebewa.R;
import com.example.ebebewa.activities.post_job.PostDeliveryJobActivity;
import com.example.ebebewa.fragments.HomeFragmentClient;
import com.example.ebebewa.fragments.ProfileFragmentClient;
import com.example.ebebewa.fragments.SettingsFragment;
import com.example.ebebewa.fragments.client_jobs.AvailableDriversFragment;
import com.example.ebebewa.models.Luggqage;
import com.example.ebebewa.models.Vehicle;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.SharedPref;

public class HomeActivityClient extends AppCompatActivity {


    private static final String TAG_HOME = "home";
    private static final String TAG_PROFILE = "profile";
    private static final String TAG_JOB_LIST = "job_list";
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_HOME;
    SharedPref sharedPref;
    ProfileFragmentClient profileFragmentClient;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private String[] activityTitles;
    private Handler mHandler;
    private CircleImageView profilePicture;
    private Menu menu;

    private String category = Constants.AVAILABLE_JOBS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_client);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = new SharedPref(this);
        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        // Navigation view header
        View navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.username);
        txtWebsite = navHeader.findViewById(R.id.loggedInInfo);
        profilePicture = navHeader.findViewById(R.id.profilePicture);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles_client);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
        }

        int pos = getIntent().getIntExtra("POSTED", 0);
        if (pos == 1) {
            CURRENT_TAG = TAG_JOB_LIST;
            navItemIndex = 3;
            category = Constants.AVAILABLE_JOBS;
        } else if (pos == 2) {
            CURRENT_TAG = TAG_JOB_LIST;
            navItemIndex = 3;
            category = Constants.DELIVERED_JOBS;
        } else if (pos == 3) {
            CURRENT_TAG = TAG_JOB_LIST;
            navItemIndex = 3;
            category = Constants.TRANSIT_JOBS;
        } else {
            CURRENT_TAG = TAG_HOME;
        }

        loadHomeFragment();
        getVehicleTypes();
        getLuggageNature();
    }

    private void loadNavHeader() {
        txtName.setText(sharedPref.getUserName());
        txtWebsite.setText(R.string.logged_as_client_text);

        Picasso.get()
                .load(sharedPref.getPassportPhoto())
                .error(this.getDrawable(R.drawable.notfound))
                .placeholder(getDrawable(R.drawable.placeholder))
                .into(profilePicture);

    }

    private void loadHomeFragment() {
        selectNavMenu();

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
                setToolbarTitles("Home");
                return new HomeFragmentClient();
            case 1:

                setToolbarTitles("Profile");
                profileFragmentClient = new ProfileFragmentClient();
                return profileFragmentClient;
            case 3:
                switch (category) {
                    case Constants.DELIVERED_JOBS:
                        setToolbarTitles("Confirmed Deliveries");
                        break;
                    case Constants.TRANSIT_JOBS:
                        setToolbarTitles("Jobs On Transit");
                        break;
                    default:
                        setToolbarTitles("Jobs Under Review");
                        break;
                }
                return new AvailableDriversFragment(category);
            case 4:
                return new SettingsFragment();
            default:
                return null;
        }
    }

    public void setToolbarTitles(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
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
                    case R.id.nav_post_job:
                        startActivity(new Intent(HomeActivityClient.this, WhereFromActivity.class));
                        drawer.closeDrawers();
                        break;
//                    case R.id.nav_invite:
//                        startActivity(new Intent(HomeActivityClient.this, InviteActivity.class));
//                        drawer.closeDrawers();
//                        break;
                    case R.id.nav_job_list:
                        category = Constants.AVAILABLE_JOBS;
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_JOB_LIST;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(HomeActivityClient.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(HomeActivityClient.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.terms_conditions:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(HomeActivityClient.this, TermsConditionsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_log_out:
                        logOut();
                        break;

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

    private void logOut() {
        sharedPref.logOut();
        Intent loginIntent = new Intent(HomeActivityClient.this, LoginActivity.class);
        loginIntent.putExtra("class", Constants.CLIENT_ROLE);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

    }

    public void prepareJobPostedFragment(String categoryL) {
        navItemIndex = 3;
        CURRENT_TAG = TAG_JOB_LIST;
        category = categoryL;
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
        // show menu only when home fragment is selected
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
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logOut();
            return true;
        }
        if (id == R.id.update) {
            profileFragmentClient.updateClient();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHomeFragment();
    }


    private void getVehicleTypes() {

        String uri = Constants.BASE_URL + "api/drivers/vehicletype";
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, this::parseVehicleTypesResponse, error -> {
        });

        RequestQueue queue = Volley.newRequestQueue(HomeActivityClient.this);
        queue.add(myReq);
    }

    private void parseVehicleTypesResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);

            ArrayList<Vehicle> vehicleArrayList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String type = jsonObject.getString("type");
                String amountPerKm = jsonObject.getString("amount_per_km");
                vehicleArrayList.add(new Vehicle(id, type, amountPerKm));
            }
            Gson gson = new Gson();
            String jsonVehicles = gson.toJson(vehicleArrayList);
            sharedPref.setVehicleArrayList(jsonVehicles);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getLuggageNature() {
        String uri = Constants.BASE_URL + "api/posts/getluggagenature";
        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri, this::parseLuggageResponse, error -> {
        });
        RequestQueue queue = Volley.newRequestQueue(HomeActivityClient.this);
        queue.add(myReq);
    }

    private void parseLuggageResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            ArrayList<Luggqage> luggqageArrayList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                String luggage_nature = jsonObject.getString("luggage_nature");
                String charge = jsonObject.getString("charge");
                String status = jsonObject.getString("status");
                luggqageArrayList.add(new Luggqage(id, luggage_nature, charge, status));
            }

            Gson gson = new Gson();
            String jsonLuggages = gson.toJson(luggqageArrayList);
            sharedPref.setLuggageNatureArrayList(jsonLuggages);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}