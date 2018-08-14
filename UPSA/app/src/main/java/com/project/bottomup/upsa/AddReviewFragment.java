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
import android.widget.EditText;


public class AddReviewFragment extends Fragment {
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
        View rootView = inflater.inflate(R.layout.fragment_add_review, container, false);
        Log.i("AddReview","OnCreateView");

        //editText 내용 가져오기
        EditText editText = (EditText) rootView.getRootView().findViewById(R.id.ReEditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력하기 전에
                //리뷰 보내기
                onApplySelectedListener.postReview("initial");
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
                    //리뷰 보내기
                    onApplySelectedListener.postReview(extraContent);
                }
            }
        });

        return rootView;
    }
}
