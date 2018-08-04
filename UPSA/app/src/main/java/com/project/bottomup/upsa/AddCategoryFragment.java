package com.project.bottomup.upsa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AddCategoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_category, container, false);
        Log.i("AddCategory","OnCreateView");
        TextView restText = (TextView) rootView.findViewById(R.id.restaurant);
        restText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i("AddCategory","restaurant_click");
                //식당정보 프래그먼트로 이동
                ((FragmentReplacable) getActivity()).replaceFragment("cafe");
            }
        });
        TextView cafeText = (TextView) rootView.findViewById(R.id.cafe);
        cafeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i("AddCategory","cafe_click");
                //식당정보 프래그먼트로 이동
                ((FragmentReplacable) getActivity()).replaceFragment("cafe");
            }
        });
        TextView parkText = (TextView) rootView.findViewById(R.id.park);
        parkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i("AddCategory","park_click");
                //공원정보 프래그먼트로 이동
                ((FragmentReplacable) getActivity()).replaceFragment("park");
            }
        });

        return rootView;
    }
}
