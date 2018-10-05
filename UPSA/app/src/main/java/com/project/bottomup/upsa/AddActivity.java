package com.project.bottomup.upsa;

import android.content.Intent;
import android.net.wifi.ScanResult;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddActivity extends AppCompatActivity implements OnMapReadyCallback,FragmentReplacable, OnApplySelectedListener{
    private static final String TAG = "AddActivity";
    //툴바 생성
    Toolbar toolbar;
    //지도 관리
    protected GoogleMap map;
    private double currentlat;
    private double currentlng;
    //서버 관리
    protected JSONObject document = new JSONObject();
    //장소 정보 관리
    private HashMap<String, Integer> hashMap;
    private JSONObject placeWifiList;
    private String placeName;
    private String placeBuilding;
    private String placeTel;
    private String placeCategory;
    private CheckBox[] placeToilet;
    private CheckBox[] placeParking;
    private String placeInfo;
    private ArrayList<MenuInfo> placeMenu;
    private String placeReview;
    private ArrayList<String> noMenu;

    public AddActivity() {
    }

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

              try{
                  Intent intent=getIntent();
                  currentlat=intent.getDoubleExtra("현재lat",37.56);
                  currentlng=intent.getDoubleExtra("현재lng",126.97);
                  ArrayList<ScanResult> temp =intent.getParcelableArrayListExtra("현재wifiList");

                  postWiFiInfo(temp);
                  placeWifiList = mapToJson(hashMap);

                  //지도 불러오기
                  FragmentManager fragmentManager = getSupportFragmentManager();
                  SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.addmap);
                  mapFragment.getMapAsync(this);
                  setDefaultFragment();

                  //메뉴 필수 아닌 카테고리
                  noMenu = new ArrayList<>();
                  noMenu.add("STORE"); noMenu.add("CONVENIENCE"); noMenu.add("RESTROOM");
              }catch(JSONException e){
                e.printStackTrace();
              }
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
                        document.put("name",placeName);
                    }else{
                        Toast.makeText(this,"이름을 등록해주세요.",Toast.LENGTH_LONG).show();
                        throw new Exception();
                    }
                    if(telPut.getText().length()>0){
                        placeTel=telPut.getText().toString();
                        document.put("tel",placeTel);
                    }else{
                        Toast.makeText(this,"전화번호를 등록해주세요.",Toast.LENGTH_LONG).show();
                        throw new Exception();
                    }
                    //필수사항 아님
                    if(buildingPut.getText().length()>0){
                        placeBuilding=buildingPut.getText().toString();
                        document.put("building",placeBuilding);
                    }else{
                        document.put("building",null);
                    }

                    document.put("lat",currentlat);
                    document.put("lng",currentlng);

                    if(placeCategory !=null){
                        document.put("category",placeCategory);
                    }else{
                        Toast.makeText(this,"카테고리를 등록해주세요.",Toast.LENGTH_LONG).show();
                        throw  new Exception();
                    }
                    if(!noMenu.contains(placeCategory)) {
                        if (placeMenu != null && placeMenu.size() > 0) {
                            JSONArray postMenu = new JSONArray();
                            for (int i = 0; i < placeMenu.size(); i++) {
                                JSONObject temp = new JSONObject();
                                temp.put("name", placeMenu.get(i).getName());
                                temp.put("price", placeMenu.get(i).getPrice());
                                postMenu.put(i, temp);
                            }
                            document.put("menu", postMenu);
                        } else {
                            Toast.makeText(this, "메뉴를 등록해주세요.", Toast.LENGTH_LONG).show();
                            throw new Exception();
                        }
                    }
                    if(placeInfo!="initial" && placeInfo!=null) {
                        document.put("extraInfo", placeInfo);
                    }else{
                        Toast.makeText(this,"세부 정보를 등록해주세요.",Toast.LENGTH_LONG).show();
                        throw  new Exception();
                    }
                    if(placeToilet!=null && placeParking!=null){
                        for(int i=0; i<placeToilet.length; i++) {
                            document.put(placeToilet[i].getText().toString(), placeToilet[i].isChecked());
                        }
                        for(int i=0; i<placeParking.length; i++) {
                            document.put(placeParking[i].getText().toString(), placeParking[i].isChecked());
                        }
                    }else{
                        Toast.makeText(this,"checkBox error",Toast.LENGTH_LONG).show();
                        throw  new Exception();
                    }

                    Log.i(TAG,"카테고리 push "+placeCategory);
                    Log.i(TAG,"이름 push "+placeName);
                    Log.i(TAG,"빌딩 push "+placeBuilding);
                    Log.i(TAG,"전화번호 push "+placeTel);
                    Log.i(TAG,"세부정보 push "+placeInfo);
                    Log.i(TAG,"리뷰 push "+placeReview);
                    Log.i(TAG,"lat, lng push "+currentlat+"/"+currentlng);
                    for(int i=0; i<placeToilet.length; i++){
                        Log.i(TAG,"화장실"+i+" push "+placeToilet[i].getText().toString()+"/"+placeToilet[i].isChecked());
                    }for(int i=0; i<placeParking.length; i++){
                        Log.i(TAG,"주차"+i+" push "+placeParking[i].getText().toString()+"/"+placeParking[i].isChecked());
                    }

                    //location 기본 정보 전송
                    NetworkManager nm = new NetworkManager();
                    String location_site = "/locationfg?"+"X="+currentlat+"&Y="+currentlng+
                            "&WifiList="+placeWifiList.toString()+"&BuildingName="+placeBuilding+"&PlaceName="+placeName+"&Category="+placeCategory;
                    Log.i(TAG, "SITE= "+location_site);
                    nm.postInfo(location_site); //기본 정보 전송 -> placeId 받기
                    while(true){ // thread 작업이 끝날 때까지 대기
                        if(nm.isEnd){
                            break;
                        }
                        Log.d(TAG, "아직 작업 안끝남.");
                    }
                    JSONObject rec_data = nm.getResult();
                    int placeId = rec_data.getInt("_id");
                    document.put("_id", placeId); // placeId -> 문서 정보에 등록

                    //location에 대한 document 정보 전송
                    String document_site = "/document/"+placeId+"?Article="+document.toString();
                    Log.i(TAG, document.toString());
                    nm.postInfo(document_site); //받은 placeId에 따른 장소 세부 정보

                    //필수사항 아님
                    if(placeReview!=null && placeReview.length()>0) {
                        // location에 대한 review 정보 전송
                        String document_review = "/review?PlaceId="+placeId+"&Article="+placeReview;
                        nm.postInfo(document_review);
                    }

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
        else if( fragmentId == "convenience" ) {
            transaction.replace(R.id.childfragment, new AddConvenienceFragment());
        }
        else if( fragmentId == "restRoom" ) {
            transaction.replace(R.id.childfragment, new AddRestRoomFragment());
        }

        //Back 버튼 클릭 시 이전 프래그먼트로 이동
        transaction.addToBackStack(null);

        transaction.commit();
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

    // 서버에 WiFi 리스트 보내주기 위한 메소드
    public void postWiFiInfo(List<ScanResult> resultList){
        Log.i(TAG,"postWiFiInfo()");
        hashMap = new HashMap<String,Integer>();
        for(int i=0;i<5;i++){
            if(i>=resultList.size()-1){
                break;
            }
            ScanResult result=resultList.get(i);
            hashMap.put(result.SSID,result.level);
        }
        Log.i(TAG,"map size: "+hashMap.size());
    }
    public JSONObject mapToJson(HashMap<String,Integer> map) throws JSONException {
        JSONObject json=new JSONObject();
        for(Map.Entry<String,Integer> entry : map.entrySet()){
            String key=entry.getKey();
            Object value=entry.getValue();
            json.put(key,value);
        }
        return json;
    }

}
