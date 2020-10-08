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

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import com.example.ebebewa.R;
import com.example.ebebewa.fragments.HomeFragmentDriver;
import com.example.ebebewa.fragments.ProfileFragmentDriver;
import com.example.ebebewa.fragments.SettingsFragment;
import com.example.ebebewa.fragments.available_jobs.AvailableJobsFragmentHolder;
import com.example.ebebewa.utils.SharedPref;

public class HomeActivityDriver extends AppCompatActivity {


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
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

    }
}