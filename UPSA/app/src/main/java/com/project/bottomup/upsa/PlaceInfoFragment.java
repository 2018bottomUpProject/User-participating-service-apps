package com.project.bottomup.upsa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class PlaceInfoFragment extends Fragment {
    private static final String TAG = "PlaceInfo";
    // 화장실
    private CheckBox cb1_1; //휴지
    private CheckBox cb1_2; //남녀공용
    // 주차공간
    private CheckBox cb2_1; //유료? 무료?

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_info, container, false);
        Log.i(TAG,"OnCreateView");

        String extra_information="";
        boolean[] toilet=new boolean[3];
        boolean[] parking=new boolean[2];
        Bundle bundle = getArguments();
        if(bundle != null){
            extra_information = bundle.getString("extraInfo");
            toilet = bundle.getBooleanArray("toilet");
            parking = bundle.getBooleanArray("parking");
        }

        TextView text_extra = (TextView)rootView.findViewById(R.id.textView_extra);
        TextView text_toilet = (TextView)rootView.findViewById(R.id.textView_toilet);
        TextView text_parking = (TextView)rootView.findViewById(R.id.textView_parking);
        text_extra.setText(extra_information);

        cb1_1 = (CheckBox)rootView.findViewById(R.id.toilet_1);
        cb1_2 = (CheckBox)rootView.findViewById(R.id.toilet_2);
        cb2_1 = (CheckBox)rootView.findViewById(R.id.parking_1);

        CheckBox[] checkbox_toilet = {cb1_1, cb1_2};

        //화장실에 대한 세부 정보 체크
        if(toilet[0] == true){
            text_toilet.setText("화장실이 있어요.");
            for(int i=1;i<toilet.length;i++){
                if(toilet[i] == true){
                    checkbox_toilet[i-1].setChecked(true);
                    checkbox_toilet[i-1].setVisibility(View.VISIBLE);
                }else{
                    checkbox_toilet[i-1].setChecked(false);
                }
            }
        }

        //주차 공간에 대한 세부 정보 체크
        if(parking[0] == true){
            text_parking.setText("주차공간이 있어요.");
            if(parking[1] == true){
                cb2_1.setChecked(true);
                cb2_1.setVisibility(View.VISIBLE);
            }else{
                cb2_1.setChecked(false);
            }
        }

        return rootView;
    }
}
