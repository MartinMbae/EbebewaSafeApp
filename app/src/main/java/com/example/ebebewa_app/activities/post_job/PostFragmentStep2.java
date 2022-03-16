package com.example.ebebewa_app.activities.post_job;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

import com.example.ebebewa_app.R;

public class PostFragmentStep2 extends Fragment {

    private Button nextBtn, prevBtn;
    private PostDeliveryJobActivity postDeliveryJobActivity;

    public PostFragmentStep2(PostDeliveryJobActivity postDeliveryJobActivity) {
        this.postDeliveryJobActivity = postDeliveryJobActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_job_step2, container, false);

        if (getActivity() != null) {
            nextBtn = view.findViewById(R.id.nextBtn);
            prevBtn = view.findViewById(R.id.prevBtn);

            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        ((PostDeliveryJobActivity) getActivity()).goToFragment(new PostFragmentStep3(postDeliveryJobActivity), 2);
                }
            });

            prevBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        ((PostDeliveryJobActivity) getActivity()).goToFragment(new PostFragmentStep1(postDeliveryJobActivity), 0);
                }
            });

            Places.initialize(getActivity(), getString(R.string.google_maps_key));

            AutocompleteSupportFragment autocompleteFragmentDestination = (AutocompleteSupportFragment)
                    getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment_destination);

            if (autocompleteFragmentDestination != null) {
                autocompleteFragmentDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS));
                autocompleteFragmentDestination.setCountry("KE");    //country type
                autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {


                        String name =  place.getName()+", "+place.getAddress();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                autocompleteFragmentDestination.setText(name);
                            }
                        },300);

                        postDeliveryJobActivity.luggage_destination = name;
                        setButtonNextVisibility();
                    }

                    @Override
                    public void onError(Status status) {
                    }
                });
            }

            if (postDeliveryJobActivity.luggage_destination != null) {
                if (autocompleteFragmentDestination != null) {
                    autocompleteFragmentDestination.setText(postDeliveryJobActivity.luggage_destination);
                    setButtonNextVisibility();
                }
            }
        }
        return view;
    }

    private void setButtonNextVisibility() {
        String desination = postDeliveryJobActivity.luggage_destination;
        if (!TextUtils.isEmpty(desination)) {
            nextBtn.setEnabled(true);
        } else {
            nextBtn.setEnabled(false);
        }
    }


}
