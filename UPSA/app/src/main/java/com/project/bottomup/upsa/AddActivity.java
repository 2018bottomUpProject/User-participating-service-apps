package com.project.bottomup.upsa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import network.DummyPlaceConnector;

public class AddActivity extends AppCompatActivity implements OnMapReadyCallback,FragmentReplacable, OnApplySelectedListener{
    //툴바 생성
    Toolbar toolbar;
    //지도 관리
    protected GoogleMap map;
    //서버 관리
    private DummyPlaceConnector dummyPlaceConnector;
    protected JSONObject infoObject = new JSONObject();
    //장소 정보 관리
    private double currentlat;
    private double currentlng;
    private String placeName;
    private String placeBuilding;
    private String placeTel;
    private String placeCategory;
    private CheckBox[] placeToilet;
    private CheckBox[] placeParking;
    private String placeInfo;
    private ArrayList<MenuInfo> placeMenu;
    private String placeReview;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add);

            toolbar = (Toolbar) findViewById(R.id.add_toolbar);
            toolbar.setTitle("정보등록");
            setSupportActionBar(toolbar);

            //왼쪽에 아이콘 추가
            if (getSupportActionBar() != null){
                 getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                 getSupportActionBar().setDisplayShowHomeEnabled(true);
              }

            Intent intent=getIntent();
            currentlat=intent.getDoubleExtra("현재lat",37.56);
            currentlng=intent.getDoubleExtra("현재lng",126.97);

            //지도 불러오기
            FragmentManager fragmentManager = getSupportFragmentManager();
            SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.addmap);
            mapFragment.getMapAsync(this);
            setDefaultFragment();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions().position(new LatLng(currentlat, currentlng))).showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentlat, currentlng)));
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
        switch(id){
            case android.R.id.home:     // 취소
                finish();
                break;

            case R.id.menu3 :  // 정보등록
                try{
                    //장소 이름, 전화번호, 빌딩이름 가져오기
                    EditText namePut = (EditText)findViewById(R.id.nameput);
                    EditText telPut = (EditText)findViewById(R.id.telput);
                    EditText buildingPut = (EditText)findViewById(R.id.buildingput);

                    if(namePut.getText().length()>0){
                        placeName=namePut.getText().toString();
                        infoObject.put("PlaceName",placeName);
                    }else{
                        Toast.makeText(this,"이름을 등록해주세요.",Toast.LENGTH_LONG).show();
                        throw new Exception();
                    }
                    if(telPut.getText().length()>0){
                        placeTel=telPut.getText().toString();
                        infoObject.put("tel",placeTel);
                    }else{
                        Toast.makeText(this,"전화번호를 등록해주세요.",Toast.LENGTH_LONG).show();
                        throw new Exception();
                    }
                    //필수사항 아님
                    if(buildingPut.getText().length()>0){
                        placeBuilding=buildingPut.getText().toString();
                        infoObject.put("building",placeBuilding);
                    }

                    infoObject.put("lat",currentlat);
                    infoObject.put("lng",currentlng);

                    if(placeCategory !=null){
                        infoObject.put("PlaceType",placeCategory);
                    }else{
                        Toast.makeText(this,"카테고리를 등록해주세요.",Toast.LENGTH_LONG).show();
                        throw  new Exception();
                    }
                    if(placeMenu!=null && placeMenu.size()>0){
                        JSONArray postMenu = new JSONArray();
                        for(int i=0; i<placeMenu.size(); i++){
                            JSONObject temp = new JSONObject();
                            temp.put("name",placeMenu.get(i).getName());
                            temp.put("price",placeMenu.get(i).getPrice());
                            postMenu.put(i,temp);
                        }
                        infoObject.put("menu",postMenu);
                    }else{
                        Toast.makeText(this,"메뉴를 등록해주세요.",Toast.LENGTH_LONG).show();
                        throw  new Exception();
                    }
                    if(placeInfo!="initial" && placeInfo!=null) {
                        infoObject.put("info", placeInfo);
                    }else{
                        Toast.makeText(this,"세부 정보를 등록해주세요.",Toast.LENGTH_LONG).show();
                        throw  new Exception();
                    }
                    if(placeToilet!=null && placeParking!=null){
                        for(int i=0; i<placeToilet.length; i++) {
                            infoObject.put(placeToilet[i].getText().toString(), placeToilet[i].isChecked());
                        }
                        for(int i=0; i<placeParking.length; i++) {
                            infoObject.put(placeParking[i].getText().toString(), placeParking[i].isChecked());
                        }
                    }else{
                        Toast.makeText(this,"checkBox error",Toast.LENGTH_LONG).show();
                        throw  new Exception();
                    }

                    //필수사항 아님
                    if(placeReview!=null && placeReview.length()>0) {
                        infoObject.put("Article", placeReview);
                    }
                    Log.i("AddActivity","카테고리 push "+placeCategory);
                    Log.i("AddActivity","이름 push "+placeName);
                    Log.i("AddActivity","빌딩 push "+placeBuilding);
                    Log.i("AddActivity","전화번호 push "+placeTel);
                    Log.i("AddActivity","위도,경도 push "+currentlat+"/"+currentlng);
                    Log.i("AddActivity","세부정보 push "+placeInfo);
                    Log.i("AddActivity","리뷰 push "+placeReview);
                    for(int i=0; i<placeToilet.length; i++){
                        Log.i("AddActivity","화장실"+i+" push "+placeToilet[i].getText().toString()+"/"+placeToilet[i].isChecked());
                    }for(int i=0; i<placeParking.length; i++){
                        Log.i("AddActivity","주차"+i+" push "+placeParking[i].getText().toString()+"/"+placeParking[i].isChecked());
                    }

                    postInfo();
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //초기 프래그먼트 설정하는 메서드
    public void setDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //첫번째로 보여지는 fragment는 categoryFragment로 설정
        transaction.add(R.id.childfragment, new AddCategoryFragment());
        transaction.commit();
    }

    //프래그먼트 변경하는 메서드
    @Override
    public void replaceFragment(String fragmentId){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if( fragmentId == "review" ) {
            transaction.replace(R.id.childfragment, new AddReviewFragment());
        }
        else if( fragmentId == "cafe" ) {
            transaction.replace(R.id.childfragment, new AddCafeFragment());
        }
        if( fragmentId == "park" ) {
            transaction.replace(R.id.childfragment, new AddParkFragment());
        }
        //Back 버튼 클릭 시 이전 프래그먼트로 이동
        transaction.addToBackStack(null);

        transaction.commit();
    }

    //최종 서버 전송 단계
    public void postInfo(){
        dummyPlaceConnector = new DummyPlaceConnector();
        try{
            MyThread.add(new Runnable() {
                @Override
                public void run() {
                    Log.i("AddActivity","쓰레드런");
                    String UserId = "이것은수정할아이디이다.";
                    String placeId = "이것은수정할장소아이디이다.";
                    dummyPlaceConnector.newDocument(placeId,UserId,infoObject);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //카테고리 정보 가져오는 메서드
    @Override
    public boolean postCategory(String category){
        placeCategory=category;
        return true;
    }

    //장소 세부정보 가져오는 메서드
    @Override
    public boolean postPlaceInfo(String extraContent){
        placeInfo = extraContent;
        return true;
    }

    //장소 체크박스 정보를 가져오는 메서드
    @Override
    public boolean postPlaceCheck(CheckBox[] toilet, CheckBox[] parking){
        placeToilet = toilet;
        placeParking = parking;
        return true;
    }

    //메뉴 정보 가져오는 메서드
    @Override
    public boolean postMenuInfo(ArrayList<MenuInfo> menuInfo){
        placeMenu = menuInfo;
        return true;
    }

    //리뷰 정보 가져오는 메서드
    @Override
    public boolean postReview(String content){
        placeReview = content;
        return true;
    }
}
