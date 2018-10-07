package com.project.bottomup.upsa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DocumentActivity extends AppCompatActivity implements OnMapReadyCallback,FragmentReplacable{
    private static final String TAG = "DocumentActivity";
    //툴바 생성
    Toolbar toolbar;
    //지도 관리
    protected GoogleMap map;
    //정보 관리
    private int placeId;
    private double currentlat;
    private double currentlng;
    private String placeName;
    private String placeBuilding;
    private String placeTel;
    private String placeCategory;
    private String extraInfo;
    private boolean[] toilet = new boolean[3];
    private boolean[] parking = new boolean[2];
    private String menu;

    private String deviceID="";
    private String permission="";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        Log.d(TAG, "onCreate()");

        Intent intent=getIntent();
        placeId = intent.getIntExtra("_id",0);
        String data = intent.getStringExtra("data");

        //디바이스 아이디 받아오기
        Context mContext=getApplicationContext();
        deviceID  = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        try{
            // JSON 데이터 분석
            // 객체를 추출한다.(장소하나의 정보)
            JSONObject root = new JSONObject(data);

            // 위도 경도 추출
            currentlat=root.getDouble("lat");
            currentlng=root.getDouble("lng");
            // 장소 이름 추출 및 전송
            placeName =root.getString("name");
            // 빌딩 이름 추출 및 전송
            placeBuilding=root.getString("building");
            // 전화번호 추출 및 전송
            placeTel=root.getString("tel");
            //카테고리 추출 및 전송
            placeCategory=root.getString("category");
            // 부가 정보 추출 및 전송
            extraInfo = root.getString("extraInfo");
            // 화장실에 대한 정보 추출 및 전송
            toilet[0] = root.getBoolean("화장실");
            toilet[1] = root.getBoolean("휴지 유무");
            toilet[2] = root.getBoolean("남녀공용");
            // 주차 장소에 대한 정보 추출 및 전송
            parking[0] = root.getBoolean("주차 공간");
            parking[1] = root.getBoolean("유료");
            // 메뉴 정보 추출 및 전송(카테고리가 카페, 레스토랑일 때만)
            if(placeCategory.equals("CAFE") || placeCategory.equals("RESTAURANT")){
                menu = root.getJSONArray("menu").toString();
            }else{
                menu = "undefined";
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        //지도 불러오기
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.addmap);
        mapFragment.getMapAsync(this);

        // 툴바 세팅
        toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        toolbar.setTitle(placeName+"의 정보");
        setSupportActionBar(toolbar);

        // 장소 기본 정보 세팅
        TextView nameGet = (TextView)findViewById(R.id.getname);
        TextView buildingGet = (TextView)findViewById(R.id.getbuilding);
        TextView telGet = (TextView)findViewById(R.id.gettel);
        nameGet.setText("장소 이름:      \" "+placeName+"\""); buildingGet.setText("빌딩 이름:      \" "+placeBuilding+" \"");
        telGet.setText("전화번호:       \" "+placeTel+" \"");

        setDefaultFragment();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions().position(new LatLng(currentlat, currentlng))).showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentlat, currentlng)));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    //초기 프래그먼트 설정하는 메서드
    public void setDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //첫번째로 보여지는 fragment는 placeInfoFragment로 설정
        PlaceInfoFragment fragment = new PlaceInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("extraInfo", extraInfo);
        bundle.putBooleanArray("toilet", toilet);
        bundle.putBooleanArray("parking", parking);
        fragment.setArguments(bundle);
        transaction.add(R.id.placeChild_fragment, fragment);
        transaction.commit();
    }

    //프래그먼트 변경하는 메서드
    @Override
    public void replaceFragment(String fragmentId){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if( fragmentId == "placeInfo" ) {
            PlaceInfoFragment fragment = new PlaceInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("extraInfo", extraInfo);
            bundle.putBooleanArray("toilet", toilet);
            bundle.putBooleanArray("parking", parking);
            fragment.setArguments(bundle);
            transaction.replace(R.id.placeChild_fragment, fragment);
        }
        else if( fragmentId == "placeMenu" ) {
            PlaceMenuFragment fragment = new PlaceMenuFragment();
            Bundle bundle = new Bundle();
            bundle.putString("placeName", placeName);
            bundle.putString("menu", menu);
            fragment.setArguments(bundle);
            transaction.replace(R.id.placeChild_fragment, fragment);
        }
        else if( fragmentId == "placeReview" ) {
            PlaceReviewFragment fragment = new PlaceReviewFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("_id", placeId);
            bundle.putString("placeName", placeName);
            fragment.setArguments(bundle);
            transaction.replace(R.id.placeChild_fragment, fragment);
        }

        transaction.commit();
    }
    // 버튼을 클릭했을 때 이벤트 처리 메서드
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonPlaceInfo:
                replaceFragment("placeInfo");
                break;
            case R.id.buttonPlaceMenu:
                replaceFragment("placeMenu");
                break;
            case R.id.buttonPlaceReview:
                replaceFragment("placeReview");
                break;
        }
    }

    // 메뉴에 대한 메서드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_document, menu);
        return true;
    }

    // 메뉴 항목을 터치하면 호출되는 메서드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // id 추출
        int id = item.getItemId();

        switch(id){
            case R.id.menu4 :     // 문서 수정
//              returnPermission();//퍼미션 받아오기
                permission="1";//지금은 1단계로 설정
                Log.i(TAG,permission);

                Intent intent=new Intent(getApplicationContext(),EditActivity.class);
                intent.putExtra("placeLat",currentlat);
                intent.putExtra("placeLng", currentlng);
                intent.putExtra("placeName",placeName);
                intent.putExtra("placeBuilding",placeBuilding);
                intent.putExtra("placeTel",placeTel);
                intent.putExtra("extraInfo",extraInfo);
                intent.putExtra("toilet",toilet);
                intent.putExtra("parking", parking);
                intent.putExtra("menu", menu);
                startActivity(intent);//EditActivity실행
                break;

            case R.id.menu5 :  // 문서 삭제

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void returnPermission(){//서버에서 퍼미션(신뢰도 단계)을 받아 리턴
        try{
            NetworkManager.add(new Runnable() {
                @Override
                public void run() {
                    String site = NetworkManager.url + "/permission";
                    try {
                        site += "?DeviceId=" + deviceID + "&PlaceId=" + placeId;
                        URL url = new URL(site);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();//URL 연결한 객체 생성
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {//연결이 되면{
                            Log.i(TAG, "서버와 연결됨");
                            Log.i(TAG, site);
                        }
                        // 스트림 추출
                        InputStream is = conn.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is, "utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        String str = null;
                        StringBuffer buf = new StringBuffer();
                        // 읽어온다
                        do {
                            str = br.readLine();
                            if (str != null) {
                                buf.append(str);
                            }
                        } while (str != null);
                        br.close(); //스트림 해제

                        String rec_data = buf.toString();
                        Log.i(TAG, "서버에서 받아온 DATA = " + rec_data);
                        if (rec_data.equals("")) {
                            permission = "";
                        } else {
                            // 객체를 추출한다.(장소하나의 정보)
                            JSONArray root = new JSONArray(rec_data);
                            JSONObject obj1 = root.getJSONObject(4);//퍼미션 위치
                            permission = obj1.getString("permission");
                            Log.i(TAG, "추출 결과 permission: " + permission);
                        }
                    } catch (MalformedURLException e) {// for URL
                        e.printStackTrace();
                    } catch (IOException e) {// for URLConnection
                        e.printStackTrace();
                    }catch (JSONException e) {// for mapToJson()
                            e.printStackTrace();
                    }
                }
            });
            }catch(Exception e){
                e.printStackTrace();
            }
        }
}
