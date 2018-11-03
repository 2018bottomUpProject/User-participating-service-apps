package com.project.bottomup.upsa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddActivity extends AppCompatActivity implements OnMapReadyCallback,FragmentReplacable{
    //툴바 생성
    Toolbar toolbar;
    //지도 관리
    protected GoogleMap map;
    double currentlat;
    double currentlng;

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

}
