package com.project.bottomup.upsa;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class AddRestRoomFragment extends Fragment {
    private static final String TAG = "AddRestRoomFragment";

    private CheckBox cb1_1; //휴지
    private CheckBox cb1_2; //남녀공용

    private CheckBox cb2; //주차 공간
    private CheckBox cb2_1; //유료? 무료?

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
        Log.i(TAG,"onCreateView");
        View rootView=inflater.inflate(R.layout.fragment_add_rest_room, container, false);

        cb1_1 = (CheckBox)rootView.findViewById(R.id.restRoom1_1);
        cb1_2 = (CheckBox)rootView.findViewById(R.id.restRoom1_2);

        cb2 = (CheckBox)rootView.findViewById(R.id.restRoom_2);
        cb2_1 = (CheckBox)rootView.findViewById(R.id.restRoom2_1);

        final CheckBox[] toilet = {cb1_1,cb1_2};
        final CheckBox[] parking = {cb2,cb2_1};
        onApplySelectedListener.postPlaceCheck(toilet,parking);

        //화장실에 대한 세부 정보 체크
        cb1_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onApplySelectedListener.postPlaceCheck(toilet,parking);
            }
        });
        cb1_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onApplySelectedListener.postPlaceCheck(toilet,parking);
            }
        });

        //주차 공간에 대한 세부 정보 체크
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for(int i=1; i<parking.length;i++){
                        parking[i].setVisibility(View.VISIBLE);
                    }
                }else{
                    for(int i=1; i<parking.length;i++){
                        parking[i].setChecked(false);
                        parking[i].setVisibility(View.GONE);
                    }
                }
                onApplySelectedListener.postPlaceCheck(toilet,parking);
            }
        });

        //editText 내용 가져오기
        EditText editText = (EditText) rootView.getRootView().findViewById(R.id.RestRoomEditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력하기 전에
                //장소 정보 보내기
                onApplySelectedListener.postPlaceInfo("initial");
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력되는 텍스트에 변화가 있을 때
            }
            @Override
            public void afterTextChanged(Editable editable) {
                //입력이 끝났을 때
                String extraContent=editable.toString();
                if(extraContent.length()>0){
                    //장소 정보 보내기
                    onApplySelectedListener.postPlaceInfo(extraContent);
                }
            }
        });

        Button button2 = (Button) rootView.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i(TAG,"button_click");
                ((FragmentReplacable) getActivity()).replaceFragment("review");
            }
        });
        return rootView;
    }
}
