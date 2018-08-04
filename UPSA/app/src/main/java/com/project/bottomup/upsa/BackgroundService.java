package com.project.bottomup.upsa;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class BackgroundService extends Service {

    private static final String TAG = "WIFIScanner";
    WifiManager wifimanager;
    private int scanCount = 0;//스캔 횟수 저장 변수
    String text = "";
    private List<ScanResult> mScanResult; //스캔 결과 저장할 리스트

    public BackgroundService() {
    }

    //최초 생성되었을 때 한 번 실행하는 메서드
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("service", "onCreate()");
        //WIFI 설정
        wifimanager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        //WiFi가 꺼져있으면 켠다
        if (wifimanager.isWifiEnabled() == false) {
            wifimanager.setWifiEnabled(true);
            printToast("WiFi ON");
        }
        //최초 백그라운드 실행시 WiFi 스캔 시작
        initWIFIScan();
        //GPS 스캔 시작
        getMyLocation();
    }

    //백그라운드로 실행되는 동작 메서드
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("service", "onStartCommand()");

        //메모리 공간 부족으로 서비스가 종료되었을 때, START_STICKY를 통해 재생성과 onStartCommand()호출
        return START_STICKY;
    }

    //서비스가 종료될 때 실행되는 메서드
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);//WiFi 스캔 종료
        Log.i("service", "onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    // WiFi Scan 을 위한 메소드들

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {//스캔 결과 받는다
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                getWIFIScanResult();
                wifimanager.startScan();
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

    public void getWIFIScanResult() {
        mScanResult = wifimanager.getScanResults(); // ScanResult
        Log.i(TAG, "scanning size : " + mScanResult.size());
    }

    public void initWIFIScan() {
        scanCount = 0;
        text = "";
        final IntentFilter filter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        wifimanager.startScan();
        Log.d(TAG, "initWIFIScan()");
    }

    public void printToast(String messageToast) {//toast 생성 및 출력
        Toast.makeText(this, messageToast, Toast.LENGTH_SHORT).show();
    }

    // GPS를 위한 메소드

    public void getMyLocation() {// 내 위치 받아오는 메소드
        printToast("getMyLocation()");
        LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GPSListener gpsListener = new GPSListener();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
        locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, gpsListener);
    }



    private class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double altitude=location.getAltitude();
            float accuracy=location.getAccuracy();
            String provider=location.getProvider();
            Log.i(TAG,"lat: "+latitude+" lng: "+longitude+" alt: "+altitude+" acc: "+accuracy+" pro: "+provider);
        }
        public void onProviderDisabled(String provider) {
        }
        public void onProviderEnabled(String provider) {
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }
}
