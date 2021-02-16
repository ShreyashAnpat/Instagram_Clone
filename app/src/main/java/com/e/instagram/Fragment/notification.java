package com.e.instagram.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.e.instagram.R;

public class notification extends Fragment {

    LottieAnimationView notification ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
//        notification = view.findViewById(R.id.notification) ;
//        notification.setVisibility(View.VISIBLE);

        return view;
    }
}