package com.project.bottomup.upsa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlaceMenuFragment extends Fragment {
    private static final String TAG = "PlaceMenu";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_menu, container, false);
        Log.i(TAG,"OnCreateView");

        Bundle bundle = getArguments();
        if(bundle != null){
            String menu = bundle.getString("menu");
        }

        return rootView;
    }
}
