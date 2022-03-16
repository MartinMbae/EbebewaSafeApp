package com.example.ebebewa_app.fragments.available_jobs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import com.example.ebebewa_app.R;

/**
 * Created by Martin Mbae on 23,June,2020.
 */
public class AvailableJobsFragmentHolder extends Fragment {

    private int position;

    public AvailableJobsFragmentHolder(int position) {
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.holder_available_jobs, container, false);

        if (getActivity() != null) {
            FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                    getChildFragmentManager(), FragmentPagerItems.with(getActivity())
                    .add("Posted Delivery Jobs", DriverAvailableJobsFragment.class)
                    .add("Bidding History", BiddingHistoryFragment.class)
                    .create());

            ViewPager viewPager = v.findViewById(R.id.viewpager);
            viewPager.setAdapter(adapter);

            SmartTabLayout viewPagerTab = v.findViewById(R.id.viewpagertab);
            viewPagerTab.setViewPager(viewPager);
            viewPager.setCurrentItem(position);
        }
        return v;
    }


}
