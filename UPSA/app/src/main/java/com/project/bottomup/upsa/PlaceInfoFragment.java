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

        Bundle bundle = getArguments();
        if(bundle != null){
            String extra_information = bundle.getString("extraInfo");

            boolean[] toilet = bundle.getBooleanArray("toilet");
            boolean[] parking = bundle.getBooleanArray("parking");
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_info, container, false);
        Log.i(TAG,"OnCreateView");
        return rootView;
    }
}
