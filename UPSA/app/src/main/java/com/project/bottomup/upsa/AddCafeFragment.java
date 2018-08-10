package com.project.bottomup.upsa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddCafeFragment extends Fragment{
    private CheckBox cb1; //화장실
    private CheckBox cb1_1; //휴지
    private CheckBox cb1_2; //남녀공용

    private CheckBox cb2; //주차 공간
    private CheckBox cb2_1; //유료? 무료?

    //메뉴 정보 관리
    final ArrayList<MenuInfo> menuInfo = new ArrayList<>();
    final ArrayList<String> menuPrint = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_cafe, container, false);
        Log.i("AddCafe","OnCreateView");

        cb1 = (CheckBox)rootView.findViewById(R.id.cafe_1);
        cb1_1 = (CheckBox)rootView.findViewById(R.id.cafe1_1);
        cb1_2 = (CheckBox)rootView.findViewById(R.id.cafe1_2);

        cb2 = (CheckBox)rootView.findViewById(R.id.cafe_2);
        cb2_1 = (CheckBox)rootView.findViewById(R.id.cafe2_1);

        final CheckBox[] toilet = {cb1,cb1_1,cb1_2};
        final CheckBox[] parking = {cb2,cb2_1};

        //화장실에 대한 세부 정보 체크
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for(int i=0;i<toilet.length;i++){
                        toilet[i].setVisibility(View.VISIBLE);
                    }
                }else{
                    for(int i=1;i<toilet.length;i++){
                        toilet[i].setChecked(false);
                        toilet[i].setVisibility(View.GONE);
                    }
                }
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
            }
        });

        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice,menuPrint);

        // listView 생성 및 adapter 지정
        final ListView listview = (ListView)rootView.findViewById(R.id.printContainer) ;
        listview.setAdapter(adapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                listview.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //메뉴 추가 버튼 클릭했을 때 이벤트
        Button button_c1 = (Button) rootView.findViewById(R.id.cafe_btn1);
        button_c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //다이얼로그
                Log.i("AddCafe","addmenu_click");

                AddMenuFragment dialog = AddMenuFragment.newInstance(new AddMenuFragment.MenuInputListener() {
                    @Override
                    public void onMenuInputComplete(String name, int price) {
                        Log.i("AddCafe","name은"+name+", price는"+price);

                        MenuInfo temp1 = new MenuInfo(name,price);
                        menuInfo.add(temp1);
                        menuPrint.add(name+"     :     "+price);
                        Log.i("AddCafe","add_menuSize="+menuInfo.size());
                        Log.i("AddCafe","add_printSize="+menuPrint.size());

                        // listview 갱신
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.show(getFragmentManager(), "menu");
            }
        });

        //메뉴 삭제 버튼을 클릭했을 때 이벤트
        Button button_c2 = (Button) rootView.findViewById(R.id.cafe_btn2);
        button_c2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SparseBooleanArray checkedItems = listview.getCheckedItemPositions();
                int count = adapter.getCount();

                try {
                    for (int i = count - 1; i >= 0; i--) {
                        if (checkedItems.get(i)) {
                            menuInfo.remove(i);
                            menuPrint.remove(i);
                        }
                    }
                    Log.i("AddCafe","delete_menuSize="+menuInfo.size());
                    Log.i("AddCafe","delete_printSize="+menuPrint.size());
                    // 모든 선택 상태 초기화.
                    listview.clearChoices();
                    // listview 갱신
                    adapter.notifyDataSetChanged();

                }catch(Exception e){
                    Log.i("AddCafe","deleteError");
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"삭제할 메뉴가 없습니다.",Toast.LENGTH_LONG).show();
                }
            }
        });

        //다음 프래그먼트로 넘어가게 하는 이벤트(리뷰 등록)
        Button button2 = (Button) rootView.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //프래그먼트 교체
                Log.i("AddCafe","button_click");
                ((FragmentReplacable) getActivity()).replaceFragment("review");
            }
        });
        return rootView;
    }
}
