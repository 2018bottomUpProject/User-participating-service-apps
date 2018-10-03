package com.project.bottomup.upsa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DocumentActivity extends AppCompatActivity implements OnMapReadyCallback,FragmentReplacable{
    private static final String TAG = "DocumentActivity";
    //툴바 생성
    Toolbar toolbar;
    //지도 관리
    protected GoogleMap map;
    private double currentlat;
    private double currentlng;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        Log.d(TAG, "onCreate()");

        toolbar = (Toolbar) findViewById(R.id.add_toolbar);
        toolbar.setTitle("장소이름 (수정요망)");
        setSupportActionBar(toolbar);

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

    //초기 프래그먼트 설정하는 메서드
    public void setDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //첫번째로 보여지는 fragment는 placeInfoFragment로 설정
        transaction.add(R.id.placeChild_fragment, new PlaceInfoFragment());
        transaction.commit();
    }

    //프래그먼트 변경하는 메서드
    @Override
    public void replaceFragment(String fragmentId){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if( fragmentId == "placeInfo" ) {
            transaction.replace(R.id.placeChild_fragment, new PlaceInfoFragment());
        }
        else if( fragmentId == "placeMenu" ) {
            transaction.replace(R.id.placeChild_fragment, new PlaceMenuFragment());
        }
        else if( fragmentId == "placeReview" ) {
            transaction.replace(R.id.placeChild_fragment, new PlaceReviewFragment());
        }

        //Back 버튼 클릭 시 이전 프래그먼트로 이동
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
