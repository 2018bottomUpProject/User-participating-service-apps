package com.project.bottomup.upsa;

import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //체크할 권한 배열
    String[] permission_list = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    // 현재 사용자 위치
    Location myLocation;
    // 위치 정보를 관리하는 매니저
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("시작", "onCreate");

        //지도 불러오기
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkPermission();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.56, 126.97))); //서울로 초기위치 설정
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    //권한 승인여부 확인
    public void checkPermission() {
        for (String str : permission_list) {
            if (ContextCompat.checkSelfPermission(this, str) != PackageManager.PERMISSION_GRANTED) {
                //권한이 없을 경우

                //최초 권한 요청인지, 사용자에 의한 재요청인지 확인
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, str)) {
                    //사용자에 의한 재요청 -> 권한 재요청
                    ActivityCompat.requestPermissions(this, new String[]{str}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    //최초 권한 요청
                    ActivityCompat.requestPermissions(this, new String[]{str}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                //사용 권한이 있는 경우
            }
        }
    }

    // 사용자가 권한 허용/거부 버튼을 눌렀을 때 호출되는 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                //사용자가 권한 동의 안함, 권한 동의 취소 버튼 선택
                Toast.makeText(this, "권한허용을 동의한 후, 사용 가능합니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //권한 동의 버튼 선택
        }
        return;
    }

}

