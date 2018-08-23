package com.project.bottomup.upsa;

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
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network.DummyPlaceConnector;


public class BackgroundService extends Service {

    private static final String TAG = "BG Service";
    WifiManager wifimanager;
    private int scanCount = 0;// 스캔 횟수 저장 변수
    String text = "";
    private List<ScanResult> mScanResult; // 스캔 결과 저장할 리스트
    private ArrayList<String> currentResultList;
    private ArrayList<String> prevResultList;
    private Location prev = null;// 이전 위치를 저장할 변수
    HashMap<String, Integer> map;
    private DummyPlaceConnector dummyPlaceConnector;
    String deviceID = "";// 디바이스 ID 저장 변수
    GPSListener gpsListener = new GPSListener();
    Handler networkHandler;
    int minute=1;
    String category="";

    public BackgroundService() {
    }

    //최초 생성되었을 때 한 번 실행하는 메서드
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        networkHandler = new Handler();
        dummyPlaceConnector = new DummyPlaceConnector();

        // 사용자의 디바이스 ID 가져오기
        Context mContext=getApplicationContext();
        deviceID  = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i(TAG,"deviceID: "+deviceID);

        currentResultList = new ArrayList<String>();
        prevResultList = new ArrayList<String>();

        // WiFi 켜져있나 확인
        checkWiFi();
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
            int three=0; // 상위 3개의 wifi가 모두 바꼈는지 판단
            int count=0; // wifi가 몇개 바뀌었는지 판단
            currentResultList.clear(); //현재 ResultList 비우기

            if(mScanResult.get(0)!=null){
                max=mScanResult.get(0);// max를 찾아 저장할 변수
            }
            if(prevResultList.size()==0){//WiFI리스트를 처음 읽는 것이라면
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
                        three++;
                        check=true;
                        Log.i(TAG,"max = "+max);
                        Log.i(TAG,"prevResult:"+prevResultList.get(0)+"/ "+prevResultList.get(1)+"/ "+prevResultList.get(2));
                    }
                    currentResultList.add(max.SSID); //현재 ResultList에 max값 넣어주기
                }
                mScanResult.remove(max);// max로 찾은 값은 list에서 지워주기
                max=mScanResult.get(0); //max 초기화
            }

            //나머지 WiFi값 저장
            for(int i=0; i<mScanResult.size(); i++){
                if(first==true){
                    prevResultList.add(mScanResult.get(i).SSID);
                }else if(three<3){
                    currentResultList.add(mScanResult.get(i).SSID);
                }
            }

            //몇개가 달라졌는지 판단
            for(int i=0; i<currentResultList.size();i++){
                if(!prevResultList.contains(currentResultList.get(i))){
                    count++;
                }
            }

            unregisterReceiver(mReceiver);//WiFi 스캔 종료

            if(check==true && count<3){
                if(first==false){ //리스트가 처음이 아닐때
                    Log.i(TAG,"WiFi 현재 같은 위치 변경X");
                    String log ="";
                    for(int i=0; i<prevResultList.size();i++){
                        log += "prev"+i+"=> "+prevResultList.get(i)+", ";
                    }
                    Log.i(TAG,log);
                    log="";
                    for(int i=0; i<currentResultList.size();i++){
                        log += "current"+i+"=> "+currentResultList.get(i)+", ";
                    }
                    Log.i(TAG,log);
                    Log.i(TAG, "★★★WiFi 바뀐 정도 - "+count+"★★★");
                    setNotifi(); // 알림바띄우기
                    pushInfo();// 1분동안 같은 위치에 머물렀으므로 정보 등록 기능 획득
                    prevResultList.clear();
                    prevResultList.addAll(currentResultList);
                } else {// 리스트를 처음 읽었다면 아직 1분이 안된 경우이므로 한 번 쉰다
                    Log.i(TAG,"FIRST_READ LIST");
                    String log ="";
                    for(int i=0; i<prevResultList.size();i++){
                        log += "prev"+i+"=> "+prevResultList.get(i)+", ";
                    }
                    Log.i(TAG,log);
                }
            }else if(three==3){ //이전 상위 3개 WIFI정보와 현재 상위 3개 WIFI정보가 모두 일치하지 않을 때
                Log.i(TAG, "WiFi 현재 위치 변경됨 -> prevList 리셋(초기화)");
                prevResultList.clear();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 서버에 WiFi 리스트 보내주기 위한 메소드
    public void postWiFiInfo(List<ScanResult> resultList){
        map = new HashMap<String,Integer>();
        for(int i=0;i<resultList.size();i++){
            ScanResult result=resultList.get(i);
            map.put(result.SSID,result.level);
        }
    }

    public void checkWiFi(){// WiFi가 켜져있는지 확인
        Log.i(TAG,"checkWiFi()");
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
                .setContentText("1분 동안 위치 변경X -> 정보등록 가능합니다")
                .setAutoCancel(true); // 클릭 시 지우기
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
    }

    // GPS를 위한 메소드

    public void checkGPS(){
        Log.i(TAG,"checkGPS()");
        LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if((!locationmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))||(!locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER))){
            printToast("GPS를 켜주세요");
        }
    }

    public void getMyLocation() {// 내 위치 받아오는 메소드
        Log.i(TAG,"getMyLocation()");
        checkGPS();// GPS 켜져있나 확인
        LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
        // 5분마다 GPS 정보 가져오기<<<<<지금은 테스트를 위해 1분으로 설정, GPS로 받는 것은 삭제해둠, minTime은 빠르거나 느릴 수 있음
        locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000,0, gpsListener);
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

    //주변 정보 가져오기
    public void getServerData(){
        try{
            final StringBuffer sb = new StringBuffer();
            NetworkManager.add(new Runnable() {
                @Override
                public void run() {
                    try{
                        Log.i(TAG,"getServerData()");
                        String site = NetworkManager.url + "/locationbg";
                        site+="?X="+36.3623087+"&Y="+127.348543300000074+"&WifiList="+"{}";//mapToJson(map)

                        URL url = new URL(site);
                        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                        if(connection != null){
                            connection.setConnectTimeout(2000);//서버 타임아웃시간 2분
                            connection.setUseCaches(false);

                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){//연결이 되면
                                // 스트림 추출
                                InputStream is=connection.getInputStream();
                                InputStreamReader isr =new InputStreamReader(is,"utf-8");
                                BufferedReader br=new BufferedReader(isr);
                                String str=null;
                                StringBuffer buf=new StringBuffer();
                                // 읽어온다
                                do{
                                    str=br.readLine();
                                    if(str!=null){
                                        buf.append(str);
                                    }
                                }while(str!=null);
                                br.close(); //스트림 해제
                                String rec_data=buf.toString();
                                Log.i(TAG,"서버에서 받아온 DATA = "+rec_data);
                                // JSON 데이터 분석
                                JSONArray root=new JSONArray(rec_data);
                                //개수만큼 반복
                                for(int i=0; i<root.length() ; i++){
                                    // 객체를 추출한다.(장소하나의 정보)
                                    JSONObject obj1=root.getJSONObject(i);
                                    //카테고리 추출
                                    category=obj1.getString("Category");
                                }
                            }
                            connection.disconnect(); // 연결 끊기
                        }
                        networkHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                printToast("category: "+category);
                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 서버에 보낼 메소드
    public void pushInfo(){
        Log.i(TAG,"pushInfo()");
        try{
            NetworkManager.add(new Runnable() {
                @Override
                public void run() {
                    // 소수 4자리까지 사용
                    String strlat=String.format("%.4f",gpsListener.latitude);
                    String strlng=String.format("%.4f",gpsListener.longitude);
                    double lat=Double.parseDouble(strlat);
                    double lng=Double.parseDouble(strlng);
                    Log.i(TAG,"pushInfo() "+deviceID+" "+lat+" "+lng+" "+map.get("KT_GiGA_2G_aplus 2층"));
                    // 서버에 위치 등록 정보 넘겨주기
                    dummyPlaceConnector.postLocationBG(deviceID,lat,lng,map);
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
            checkLocationStatus(location);
        }
        public void checkLocationStatus(Location location){
            /*
             이전 GPS와 현재 GPS의 위치가 같은 범위에 있는 지 확인한 후
             같다면 WiFi 정보가 같은지 확인한다
             */
            if(prev==null){//처음 GPS 위치를 받아온 것이라면
                prev=location;//5분뒤의 위치와 비교하기 위함
                Log.i(TAG,"GPS 처음 prev lat,lng:"+prev.getLatitude()+", "+prev.getLongitude());
            }
            else{//이전 값이 존재한다면(5분 전의 GPS 값이 존재)
                // 서버한테 위치 보내주고 등록된 장소인지 확인하기

//                if(minute==1) {//처음 체크할 때
//                    initWIFIScan();// map 갱신하기 위해 WiFi 체크
//                    getServerData();// 위치가 서버에 존재하는 지 확인
//                    Log.i(TAG, "category: " + category);
//                }
//                if(category==null){// 등록되지 않은 장소라면
//                    setNotifi();// 노티피케이션 띄우기
//                }
//                else{// 서버에 등록된 장소라면 카테고리별로 시간 측정
//                    // 현재 위치 카테고리에 따라 머무르는 시간, 거리 범위가 달라짐
//                    if(category.equals("CAFE")){//카페라면 5분 동안 있는 지 체크
//                        if(minute==5){//5분이 지났으면
//                            double distance=prev.distanceTo(location);
//                            if(distance<10){//이전 위치와 거리 차가 10m 내이면 같은 위치에 있다고 판별
//                                Log.i(TAG,"GPS 현재 위치 변경X (-> WiFi도 바뀌었는지 확인하러 함수호출)");
//                                initWIFIScan(); // WiFi 스캔을 시작하여 같은 위치가 맞는 지 판별
//                            }
//                            else{//위치가 바뀌었다면
//                                prev=location;//prev 값을 현재 location 값으로 변경
//                                Log.i(TAG,"GPS 현재 위치 변경됨");
//                            }
//                            minute=1;//초기화
//                        }
//                        else{
//                            //5분이 안되었으면 될 때까지 기다림
//                            minute++;
//                            Log.i(TAG,minute+"분");
//                        }
//                    }
//                }

                double distance=prev.distanceTo(location);
                Log.i(TAG,"distance: "+distance);
                // 카페, 식당이면 10m, 공원 50m
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
            // GPS가 꺼지면 함수 호출
            checkGPS();
            Log.i(TAG,"onProviderDisabled");
        }
        public void onProviderEnabled(String provider) {
            // GPS가 다시 켜지면 함수 호출
            Log.i(TAG,"onProviderEnabled");
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG,"onStatusChanged");
        }

    }
}
