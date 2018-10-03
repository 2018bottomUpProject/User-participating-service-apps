package com.project.bottomup.upsa;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlaceInfoFragment extends Fragment {
    private static final String TAG = "PlaceInfo";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Intent intent=getActivity().getIntent();
        String extra_information =intent.getStringExtra("extraInfo");
        Log.d(TAG, "EXTRA INFO = "+ extra_information);
        boolean[] toilet = intent.getBooleanArrayExtra("toilet");
        boolean[] parking = intent.getBooleanArrayExtra("parking");
        for(int i=0; i<toilet.length; i++){
            Log.i(TAG,"화장실"+i+" push "+toilet[i]);
        }for(int i=0; i<parking.length; i++){
            Log.i(TAG,"주차"+i+" push "+parking[i]);
        }
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_info, container, false);
        Log.i(TAG,"OnCreateView");
        return rootView;
    }
}
