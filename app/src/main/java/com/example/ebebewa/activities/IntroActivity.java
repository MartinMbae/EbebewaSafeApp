package com.example.ebebewa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import com.example.ebebewa.R;
import com.example.ebebewa.utils.Constants;
import com.example.ebebewa.utils.SharedPref;

public class IntroActivity extends AppCompatActivity {

    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;
    TextView tvSkip;
    SharedPref sharedPref;
    private ViewPager screenPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        // ini views
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);

        sharedPref = new SharedPref(this);

        // fill list screen

        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Place a delivery job", "Do you want a luggage delivered to you from any part of the country? Worry no more. Post a 'Delivery luggage job' on " + getString(R.string.app_name) + " and receive your luggage easily, quickly and cheaply", R.drawable.truck_delivery));
        mList.add(new ScreenItem("Fast Delivery", getString(R.string.app_name) + " will deliver your luggage quickly.", R.drawable.img2));
        mList.add(new ScreenItem("Easy Payment", "Pay easily for the service.", R.drawable.img3));

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tablayout with viewpager

        tabIndicator.setupWithViewPager(screenPager);

        // next button click Listner

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {

                    position++;
                    screenPager.setCurrentItem(position);
                }

                if (position == mList.size() - 1) { // when we rech to the last screen
                    loaddLastScreen();


                }


            }
        });

        // tablayout add change listener


        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size() - 1) {

                    loaddLastScreen();

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // Get Started button click listener

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPref.setIsFirstTime(false);
                String roleId = sharedPref.getRoleID();
                if (roleId == null) roleId = "login";
                goToHomeActivity(roleId);
                finish();


            }
        });

        // skip button click listener

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });


    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.setAnimation(btnAnim);

    }


    private void goToHomeActivity(String role) {
        switch (role) {
            case Constants.DRIVER_ROLE:
                Intent driverIntent = new Intent(IntroActivity.this, HomeActivityDriver.class);
                startActivity(driverIntent);
                finish();
                break;

            case Constants.CLIENT_ROLE:
                Intent clientIntent = new Intent(IntroActivity.this, HomeActivityClient.class);
                startActivity(clientIntent);
                finish();
                break;

            default:
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
                break;
        }


    }

}
