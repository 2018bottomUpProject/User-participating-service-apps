package com.project.bottomup.upsa;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity  implements OnMapReadyCallback{
    private static final String TAG = "EditActivity";
    // 툴바 생성
    Toolbar toolbar;
    //지도 관리
    protected GoogleMap map;
    // 정보 관리
    private double placeLat, placeLng;
    private String placeName, placeBuilding, placeTel, extraInfo, menu;
    private JSONArray menuArr;
    private ArrayList<String> menuPrint = new ArrayList<String>();
    private boolean[] toilet = new boolean[3];
    private boolean[] parking = new boolean[2];
    private CheckBox cb1; //화장실
    private CheckBox cb1_1; //휴지
    private CheckBox cb1_2; //남녀공용

    private CheckBox cb2; //주차 공간
    private CheckBox cb2_1; //유료? 무료?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // DocumentActivity에서 넘겨준 값 받기
        Intent intent = getIntent();
        try {
            if (intent == null) {
                throw new Exception();
            }
            placeLat = intent.getDoubleExtra("placeLat", 37.56);
            placeLng = intent.getDoubleExtra("placeLng", 126.97);
            placeName = intent.getStringExtra("placeName");
            placeBuilding = intent.getStringExtra("placeBuilding");
            placeTel = intent.getStringExtra("placeTel");
            menu = intent.getStringExtra("menu");
            extraInfo = intent.getStringExtra("extraInfo");
            toilet = intent.getBooleanArrayExtra("toilet");
            parking = intent.getBooleanArrayExtra("parking");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 툴바 세팅
        toolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        toolbar.setTitle("정보수정");
        setSupportActionBar(toolbar);

        // 왼쪽에 아이콘 추가
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //지도 불러오기
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.addmap);
        mapFragment.getMapAsync(this);

        // 화면 초기세팅
        EditText edit_name = (EditText) findViewById(R.id.editName);
        EditText edit_building = (EditText) findViewById(R.id.editBuilding);
        EditText edit_tel = (EditText) findViewById(R.id.editTel);
        EditText edit_extra = (EditText) findViewById(R.id.editExtraText);
        edit_name.setText(placeName); edit_building.setText(placeBuilding); edit_tel.setText(placeTel); edit_extra.setText(extraInfo);

        //화장실&주차공간에 대한 정보 초기세팅
        cb1 = (CheckBox) findViewById(R.id.editToilet);
        cb1_1 = (CheckBox) findViewById(R.id.editToilet_1);
        cb1_2 = (CheckBox) findViewById(R.id.editToilet_2);
        cb2 = (CheckBox) findViewById(R.id.editParking);
        cb2_1 = (CheckBox) findViewById(R.id.editParking_1);
        final CheckBox[] checkbox_toilet = {cb1, cb1_1, cb1_2};
        final CheckBox[] checkbox_parking = {cb2,cb2_1};

        if(toilet[0] == true){
            checkbox_toilet[0].setChecked(true);
            for(int i=1;i<toilet.length;i++){
                checkbox_toilet[i].setVisibility(View.VISIBLE);
                if(toilet[i] == true){
                    checkbox_toilet[i].setChecked(true);
                }
            }
        }
        if(parking[0] == true){
            checkbox_parking[0].setChecked(true);
            checkbox_parking[1].setVisibility(View.VISIBLE);
            if(parking[1] == true){
                checkbox_parking[1].setChecked(true);
            }
        }

        //화장실에 대한 세부 정보 체크
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for(int i=1;i<checkbox_toilet.length;i++){
                        checkbox_toilet[i].setVisibility(View.VISIBLE);
                    }
                }else{
                    for(int i=1;i<checkbox_toilet.length;i++){
                        checkbox_toilet[i].setChecked(false);
                        checkbox_toilet[i].setVisibility(View.GONE);
                    }
                }
            }
        });
        //주차 공간에 대한 세부 정보 체크
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for(int i=1; i<checkbox_parking.length;i++){
                        checkbox_parking[i].setVisibility(View.VISIBLE);
                    }
                }else{
                    for(int i=1; i<checkbox_parking.length;i++){
                        checkbox_parking[i].setChecked(false);
                        checkbox_parking[i].setVisibility(View.GONE);
                    }
                }
            }
        });

        // 메뉴에 대한 정보 초기세팅
        try{
            if(!menu.equals("undefined")){ // 메뉴가 있을 때
                // 메뉴 정보를 출력할 수 있도록 ArrayList에 담기
                menuArr = new JSONArray(menu);
                for(int i=0; i< menuArr.length(); i++){
                    JSONObject obj1=menuArr.getJSONObject(i);
                    String menuName = obj1.getString("name");
                    int menuPrice = obj1.getInt("price");
                    String temp = menuName+"     :     "+menuPrice;
                    menuPrint.add(temp);
                }

                // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
                final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice,menuPrint);
                // listView 생성 및 adapter 지정
                final ListView listview = findViewById(R.id.editContainer_menu) ;
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
                Button button_c1 = (Button) findViewById(R.id.edit_btn1);
                button_c1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //다이얼로그
                        AddMenuFragment dialog = AddMenuFragment.newInstance(new AddMenuFragment.MenuInputListener() {
                            @Override
                            public void onMenuInputComplete(String name, int price) {
                                Log.i(TAG,"name은"+name+", price는"+price);
                                try {
                                    JSONObject obj = new JSONObject();
                                    obj.put("name", name);
                                    obj.put("price", price);
                                    menuArr.put(obj);
                                    menuPrint.add(name+"     :     "+price);
                                    Log.i(TAG,"menuArr Size="+menuArr.length());
                                    Log.i(TAG,"add_printSize="+menuPrint.size());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // listview 갱신
                                adapter.notifyDataSetChanged();
                            }
                        });
                        dialog.show(getSupportFragmentManager(), "menu");
                    }
                });

                //메뉴 삭제 버튼을 클릭했을 때 이벤트
                Button button_c2 = (Button) findViewById(R.id.edit_btn2);
                button_c2.setOnClickListener(new View.OnClickListener(){
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View view){
                        SparseBooleanArray checkedItems = listview.getCheckedItemPositions();
                        int count = adapter.getCount();
                        try {
                            for (int i = count - 1; i >= 0; i--) {
                                if (checkedItems.get(i)) {
                                    menuArr.remove(i);
                                    menuPrint.remove(i);
                                }
                            }
                            // 모든 선택 상태 초기화.
                            listview.clearChoices();
                            // listview 갱신
                            adapter.notifyDataSetChanged();
                        }catch(Exception e){
                            Log.i(TAG,"deleteError");
                            e.printStackTrace();
                        }
                    }
                });
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions().position(new LatLng(placeLat, placeLng))).showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(placeLat, placeLng)));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    // 메뉴에 대한 메서드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    // 메뉴 항목을 터치하면 호출되는 메서드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // id 추출
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:     // 취소
                finish();
                break;

            case R.id.menu3:  // 수정한 정보 등록

                //수정한 정보를 등록하는 코드 작성

                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}