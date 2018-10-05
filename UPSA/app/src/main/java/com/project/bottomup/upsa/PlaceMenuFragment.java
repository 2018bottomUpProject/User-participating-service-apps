package com.project.bottomup.upsa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaceMenuFragment extends Fragment {
    private static final String TAG = "PlaceMenu";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_menu, container, false);
        Log.i(TAG,"OnCreateView");
        try{
            JSONArray menuArr;
            String placeName = "";
            ArrayList<String> menuPrint = new ArrayList<String>();
            TextView menu_explain = (TextView) rootView.findViewById(R.id.textView_placeMenu);

            Bundle bundle = getArguments();
            if(bundle != null) {
                placeName = bundle.getString("placeName");
                String menu = bundle.getString("menu");

                if(!menu.equals("undefined")){ // 메뉴가 있을 때
                    // 메뉴 정보를 출력할 수 있도록 ArrayList에 담기
                    menuArr = new JSONArray(menu);
                    for(int i=0; i< menuArr.length(); i++){
                        JSONObject obj1=menuArr.getJSONObject(i);
                        String menuName = obj1.getString("name");
                        int menuPrice = obj1.getInt("price");
                        String temp = menuName+":       "+menuPrice;
                        menuPrint.add(temp);
                    }

                    //화면 출력
                    menu_explain.setText("\" "+placeName + " \"의 메뉴는 다음과 같습니다.");
                    // ArrayAdapter 생성. 아이템 View를 하나의 텍스트뷰로 구성하도록 레이아웃을 설정함.
                    final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1  ,menuPrint);
                    final ListView menu_detail = (ListView)rootView.findViewById(R.id.printContainer_menu) ;
                    menu_detail.setAdapter(adapter);
                }else{ // 메뉴가 없을 때
                    menu_explain.setText("\" "+placeName + " \"에 등록된 메뉴가 없습니다.");
                }
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
        return rootView;
    }
}
