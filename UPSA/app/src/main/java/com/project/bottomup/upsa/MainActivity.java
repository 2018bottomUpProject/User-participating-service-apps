package com.project.bottomup.upsa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    //체크할 권한 배열
    String[] permission_list = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    // 현재 사용자 위치
    Location myLocation;
    // 위치 정보를 관리
    LocationManager manager;

    // 지도 관리
    protected GoogleMap map;
    LatLng position = new LatLng(37.56, 126.97); //초기설정(서울)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("시작", "onCreate");

        //지도 불러오기
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLng(position)); //서울로 초기위치 설정
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
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
                getMyLocation();
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
            getMyLocation();
        }
        return;
    }

    // 현재 위치를 가져오는 메서드
    public void getMyLocation() {
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 권한이 모두 허용되어 있을 때만 동작
        int chk1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int chk2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (chk1 == PackageManager.PERMISSION_GRANTED && chk2 == PackageManager.PERMISSION_GRANTED) {
            myLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            showMyLocation();
        }
        // 새롭게 위치를 측정한다.
        GpsListener listener = new GpsListener();
        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, listener);
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener);
        }
    }

    // GPS Listener
    class GpsListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // 현재 위치 값을 저장한다.
            myLocation = location;
            // 위치 측정을 중단한다.
            manager.removeUpdates(this);
            // 지도를 현재 위치로 이동시킨다.
            showMyLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }
    }

    public void showMyLocation() {
        if (myLocation == null) {
            // LocationManager.GPS_PROVIDER 부분에서 null 값을 가져올 경우
            return;
        }
        // 현재 위치값을 추출
        double lat = myLocation.getLatitude();
        double lng = myLocation.getLongitude();

        // 위도 경도를 관리하는 객체를 생성
        position = new LatLng(lat, lng);
        // 현재 위치를 설정
        map.moveCamera(CameraUpdateFactory.newLatLng(position));

        // 현재 위치 표시
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        // 지도 모드 변경
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

}

