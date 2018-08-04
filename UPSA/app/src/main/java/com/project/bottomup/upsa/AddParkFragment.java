package com.project.bottomup.upsa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AddParkFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("AddPark","onCreateView");
        View rootView=inflater.inflate(R.layout.fragment_add_park, container, false);
        Button button2 = (Button) rootView.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i("AddPark","button_click");
                ((FragmentReplacable) getActivity()).replaceFragment("review");
            }
        });
        return rootView;
    }
}
