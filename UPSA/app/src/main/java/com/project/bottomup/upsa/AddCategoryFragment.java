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
        Log.i("AddCategory","OnCreateView");
        final TextView restText = (TextView) rootView.findViewById(R.id.restaurant);
        restText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i("AddCategory","restaurant_click");
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
                Log.i("AddCategory","cafe_click");
                onApplySelectedListener.postCategory("CAFE");
                //식당정보 프래그먼트로 이동
                ((FragmentReplacable) getActivity()).replaceFragment("cafe");
            }
        });
        final TextView parkText = (TextView) rootView.findViewById(R.id.park);
        parkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i("AddCategory","park_click");
                onApplySelectedListener.postCategory("PARK");
                //공원정보 프래그먼트로 이동
                ((FragmentReplacable) getActivity()).replaceFragment("park");
            }
        });

        return rootView;
    }
}
