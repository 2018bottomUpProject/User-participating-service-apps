package com.project.bottomup.upsa;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
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
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import network.DummyPlaceConnector;


public class BackgroundService extends Service {

    private static final String TAG = "BG Service";
    WifiManager wifimanager;
    private int scanCount = 0;// 스캔 횟수 저장 변수
    String text = "";
    private List<ScanResult> mScanResult; // 스캔 결과 저장할 리스트
    //ScanResult bResult1 = null, bResult2 = null, bResult3 = null;// wifi 상위 3개 저장할 변수들
    private ArrayList<String> currentResultList;
    private ArrayList<String> prevResultList;
    private Location prev = null;// 이전 위치를 저장할 변수
    HashMap<String, Integer> map;
    private DummyPlaceConnector dummyPlaceConnector;
    String deviceID="";// 디바이스 ID 저장 변수
    GPSListener gpsListener = new GPSListener();

    public BackgroundService() {
    }

    //최초 생성되었을 때 한 번 실행하는 메서드
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("service", "onCreate()");

        // 사용자의 디바이스 ID 가져오기
        Context mContext=getApplicationContext();
        deviceID  = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i(TAG,"deviceID: "+deviceID);

        currentResultList = new ArrayList<String>();
        prevResultList = new ArrayList<String>();

        //최초 백그라운드 실행시 현재 위치의 WiFi 정보 받아오기
        //initWIFIScan();
        //최초 위치 GPS 정보 받아오기
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
        try{
            mScanResult = wifimanager.getScanResults(); // ScanResult
            postWiFiInfo(mScanResult); // 서버에 보내줄 정보 맵에 넣기
        /*
         스캔 결과 중 세기가 좋은 상위 3개를 찾고
         이전 상위 3개와 비교해서 3개 모두 변경되었다면
         위치가 변경되었다고 인식한다
         */
            if(mScanResult.size()==0){//WiFi 결과가 없다면
                return;
            }

            ScanResult max=null;
            boolean first=false; // 처음 WiFi리스트를 읽는 것인지 판단
            int count=0; // 상위 3개의 wifi가 모두 바꼈는지 판단
            currentResultList.clear(); //현재 ResultList 비우기

            if(mScanResult.get(0)!=null){
                max=mScanResult.get(0);// max를 찾아 저장할 변수
            }
            if(prevResultList.size()!=3){//WiFI리스트를 처음 읽는 것이라면
                first=true;
            }

            boolean check = true;//Result들이 모두 변경되었는지 알아보기 위한 변수
            for(int i=0;i<3;i++) {//3개의 상위 wifi를 찾을 때 까지 반복
                for (int j = 0; j < mScanResult.size(); j++) {//mScanResult 돌면서 상위 값 찾기
                    if (max.level < mScanResult.get(j).level) {
                        max = mScanResult.get(j); //max에 제일 큰 값이 들어감
                    }
                }
                if(first==true) {
                    prevResultList.add(max.SSID);
                }else{
                    check = prevResultList.contains(max.SSID); //직전 ResultList에 max가 존재하는지 판단
                    if(check==false){ //없다면
                        count++;
                        check=true;
                        Log.i(TAG,"max = "+max);
                        Log.i(TAG,"prevResult:"+prevResultList.get(0)+"/ "+prevResultList.get(1)+"/ "+prevResultList.get(2));
                    }
                    currentResultList.add(max.SSID); //현재 ResultList에 max값 넣어주기
                }
                mScanResult.remove(max);// max로 찾은 값은 list에서 지워주기
                max=mScanResult.get(0); //max 초기화
            }

            unregisterReceiver(mReceiver);//WiFi 스캔 종료

            if(check==true && count<3){
                if(first==false){ //리스트가 처음이 아닐때
                    Log.i(TAG,"WiFi 현재 같은 위치 변경X");
                    Log.i(TAG,"prevResult:"+prevResultList.get(0)+"/ "+prevResultList.get(1)+"/ "+prevResultList.get(2));
                    Log.i(TAG,"currentResult:"+currentResultList.get(0)+"/ "+currentResultList.get(1)+"/ "+currentResultList.get(2));
                    setNotifi(); // 알림바띄우기
                    pushInfo();// 1분동안 같은 위치에 머물렀으므로 정보 등록 기능 획득
                    prevResultList.clear();
                    prevResultList.addAll(currentResultList);
                } else {// 리스트를 처음 읽었다면 아직 1분이 안된 경우이므로 한 번 쉰다
                    Log.i(TAG,"FIRST_READ LIST");
                    Log.i(TAG,"prevResult:"+prevResultList.get(0)+"/ "+prevResultList.get(1)+"/ "+prevResultList.get(2));
                }
            }else if(check==false && count==3){
                Log.i(TAG, "WiFi 현재 위치 변경됨 -> prevList 리셋(초기화)");
                prevResultList.clear();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 서버에 WiFi 리스트 보내주기 위한 메소드
    public void postWiFiInfo(List<ScanResult> resultList){
        Log.i(TAG,"postInfo()");
        map = new HashMap<String,Integer>();
        for(int i=0;i<resultList.size();i++){
            ScanResult result=resultList.get(i);
            map.put(result.SSID,result.level);
        }
    }

    public void checkWiFi(){// WiFi가 켜져있는지 확인
        wifimanager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        //WiFi가 꺼져있으면 켠다
        if (!wifimanager.isWifiEnabled()) {
            wifimanager.setWifiEnabled(true);
            printToast("WiFi ON");
        }
    }

    public void initWIFIScan() {
        checkWiFi();// WiFi 켜져있는지 확인
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

    // 상단바에 알림 띄우는 메소드
    public void setNotifi(){
        Log.i(TAG,"setNotifi()");
        NotificationCompat.Builder mBuilder=
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.intro) // 아이콘 임의로 아무거나 설정>>수정
                .setContentTitle("정보 등록 가능")
                .setContentText("1분 동안 위치 변경X -> 정보등록 가능합니다");
        Intent resultIntent=new Intent(getApplicationContext(),MainActivity.class);
        //MainActivity에 경도,위도 전송
        resultIntent.putExtra("lat",gpsListener.latitude);
        resultIntent.putExtra("lng",gpsListener.longitude);
        Log.i(TAG,"put lat / lng :"+gpsListener.latitude+"/"+gpsListener.longitude);

        TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent=
                stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager=
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(3452,mBuilder.build());
        //노티피케이션 지우는 코드
        //NotificationManager mNotificationManager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //mNotificationManager.cancel(3452);
    }

    // GPS를 위한 메소드

    public void getMyLocation() {// 내 위치 받아오는 메소드
        Log.i(TAG,"getMyLocation()");
        LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //GPSListener firstListener=new GPSListener();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        // 처음 한번만 받아오고 종료
        //locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, firstListener);
        //locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, firstListener);
        //locationmanager.removeUpdates(firstListener);
        // 5분마다 GPS 정보 가져오기<<<<<지금은 테스트를 위해 1분으로 설정
        locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000,0, gpsListener);
    }

    // 서버에 보낼 메소드

    public void pushInfo(){
        dummyPlaceConnector=new DummyPlaceConnector();
        Log.i(TAG,"pushInfo()");
        try{
            MyThread.add(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"pushInfo() "+deviceID+" "+gpsListener.latitude+" "+gpsListener.longitude+" "+map.get("KT_GiGA_2G_aplus 2층"));
                    // 서버에 위치 등록 정보 넘겨주기
                    dummyPlaceConnector.postLocationBG(deviceID,gpsListener.latitude,gpsListener.longitude,map);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private class GPSListener implements LocationListener {
        double latitude,longitude,altitude;
        float accuracy;
        String provider;
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude=location.getAltitude();
            accuracy=location.getAccuracy();
            provider=location.getProvider();
            Log.i(TAG,"lat: "+latitude+" lng: "+longitude+" alt: "+altitude+" acc: "+accuracy+" pro: "+provider);
            /*
             이전 GPS와 현재 GPS의 위치가 같은 범위에 있는 지 확인한 후
             같다면 WiFi 정보가 같은지 확인한다
             */
            if(prev==null){//처음 GPS 위치를 받아온 것이라면
                prev=location;//5분뒤의 위치와 비교하기 위함
                Log.i(TAG,"GPS 처음 prev lat,lng:"+prev.getLatitude()+", "+prev.getLongitude());
            }
            else{//이전 값이 존재한다면(5분 전의 GPS 값이 존재)
                double distance=prev.distanceTo(location);
                Log.i(TAG,"distance: "+distance);
                if(distance<50){//거리 차가 50m 내외라면
                    Log.i(TAG,"GPS 현재 위치 변경X (-> WiFi도 바뀌었는지 확인하러 함수호출)");
                    initWIFIScan(); // WiFi 스캔을 시작하여 같은 위치가 맞는 지 판별
                }
                else{//50m 이상이면 위치가 갱신된 것이므로
                    prev=location;//prev 값을 현재 location 값으로 변경
                    Log.i(TAG,"GPS 현재 위치 변경됨");
                }
            }
        }
        public void onProviderDisabled(String provider) {
        }
        public void onProviderEnabled(String provider) {
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }
}
