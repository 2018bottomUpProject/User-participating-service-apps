package com.project.bottomup.upsa;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AddCategoryFragment extends Fragment {
    private static final String TAG = "AddCategory";

    private OnApplySelectedListener onApplySelectedListener;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof OnApplySelectedListener){
            onApplySelectedListener = (OnApplySelectedListener) context;
        }else{
            throw new RuntimeException(context.toString()+"must implement OnApplySelectedListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        onApplySelectedListener=null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_category, container, false);
        Log.i(TAG,"OnCreateView");
        final TextView restText = (TextView) rootView.findViewById(R.id.restaurant);
        restText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i(TAG,"restaurant_click");
                onApplySelectedListener.postCategory("RESTAURANT");
                //식당정보 프래그먼트로 이동
                ((FragmentReplacable) getActivity()).replaceFragment("cafe");
            }
        });
        final TextView cafeText = (TextView) rootView.findViewById(R.id.cafe);
        cafeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i(TAG,"cafe_click");
                onApplySelectedListener.postCategory("CAFE");
                //카페정보 프래그먼트로 이동
                ((FragmentReplacable) getActivity()).replaceFragment("cafe");
            }
        });
        final TextView storeText = (TextView) rootView.findViewById(R.id.store);
        storeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i(TAG,"store_click");
                onApplySelectedListener.postCategory("STORE");
                //편의점정보 프래그먼트로 이동
                ((FragmentReplacable) getActivity()).replaceFragment("convenience");
            }
        });
        final TextView convenienceText = (TextView) rootView.findViewById(R.id.convenience);
        convenienceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i(TAG,"convenience_click");
                onApplySelectedListener.postCategory("CONVENIENCE");
                //편의시설정보 프래그먼트로 이동
                ((FragmentReplacable) getActivity()).replaceFragment("convenience");
            }
        });
        final TextView restRoomText = (TextView) rootView.findViewById(R.id.restRoom);
        restRoomText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i(TAG,"restRoom_click");
                onApplySelectedListener.postCategory("RESTROOM");
                //편의시설정보 프래그먼트로 이동
                ((FragmentReplacable) getActivity()).replaceFragment("restRoom");
            }
        });

        return rootView;
    }
}
