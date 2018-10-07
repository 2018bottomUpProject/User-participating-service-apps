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
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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

public class BackgroundService extends Service {
    private static final String TAG = "BG Service";
    WifiManager wifimanager;
    String text = "";
    //private List<ScanResult> mScanResult; // 스캔 결과 저장할 리스트
    private List<ScanResult> currentResult;
    private List<ScanResult> prevResult;
    private  List<ScanResult> sortResult;
    private Location prev = null;// 이전 위치를 저장할 변수
    HashMap<String, Integer> map;
    String deviceID = "";// 디바이스 ID 저장 변수
    GPSListener gpsListener = new GPSListener();
    Handler networkHandler;
    int minute=-1;
    String category="";
    String placeID="";
    NetworkManager nm = new NetworkManager();

    public BackgroundService() {
    }

    //최초 생성되었을 때 한 번 실행하는 메서드
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        networkHandler = new Handler();
        currentResult = new ArrayList<ScanResult>();
        prevResult = new ArrayList<ScanResult>();

        // 사용자의 디바이스 ID 가져오기
        Context mContext=getApplicationContext();
        deviceID  = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.i(TAG,"deviceID: "+deviceID);

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
                //getWIFIScanResult();
                wifimanager.startScan();
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };
    public void threeWiFi(List<ScanResult> prev, List<ScanResult> current){
        Log.i(TAG,"threeWiFi()");
        if(prev==null){
            return;
        }
        List<ScanResult> prevThree=new ArrayList<>(),currentThree=new ArrayList<>();
        int count=0;//상위 3개 비교할 변수
        //prev 상위 3개 찾기
        ScanResult max=prev.get(0);
        for(int i=0;i<3;i++){
            for(int j=0;j<prev.size();j++){
                if(max.level<prev.get(j).level){//max보다 현재 값이 더 세면
                    max=prev.get(j);//max 값 갱신
                }
            }
            prevThree.add(max);//찾은 최댓값 저장
            prev.remove(max);//최댓값 중복 피하기 위해 삭제
            max=prev.get(0);//max 초기화
        }
        //current 상위 3개 찾기
        max=current.get(0);
        for(int i=0;i<3;i++){
            for(int j=0;j<current.size();j++){
                if(max.level<current.get(j).level){//max보다 현재 값이 더 세면
                    max=current.get(j);//max 값 갱신
                }
            }
            currentThree.add(max);//찾은 최댓값 저장
            current.remove(max);//최댓값 중복 피하기 위해 삭제
            max=current.get(0);//max 초기화
        }
        //최댓값 3개 삭제했던 것 글로벌 변수에 다시 복구
        for (int i=0;i<3;i++){
            prev.add(prevThree.get(i));
            current.add(currentThree.get(i));
        }
        //prev와 current의 상위 3개 비교, 3개 모두 달라지면 위치는 변경된 것으로 파악
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(!(prevThree.get(i).SSID.equals(currentThree.get(j).SSID))){//prev의 i번째 값이 current에 없다면
                    count++;
                }
            }
        }
        if(count==3){
            Log.i(TAG,"WiFi를 통해 위치 변경 확인");
        }
        else{
            Log.i(TAG,"WiFI를 통해 위치 변경 X 확인");
        }
    }

    public void changedWiFiCount(List<ScanResult> prev, List<ScanResult> current){
        Log.i(TAG,"changedWiFiCount()");
        int count=0;
        if(prev==null){
            return;
        }
        for(int i=0;i<prev.size();i++){// 과거 값이 현재에 존재하는 지 확인
            for(int j=0;j<current.size();j++){
                if(prev.get(i).SSID.equals(current.get(j).SSID)){//같은 게 있으면 break
                    break;
                }
                if(j==current.size()-1){//마지막까지 돌았는 데도 같은게 없으면 count++
                    count++;
                }
            }
        }
        Log.i(TAG,minute+"분 WiFi "+count+"개 변경됨(prev:"+prev.size()+"개,current:"+current.size()+"개)");
    }
    public void setPrevCurrent(){
        initWIFIScan();
        Log.i(TAG,"setPrevCurrent()");
        if(minute==0){//맨 처음에 리스트 받아올 때
            currentResult=wifimanager.getScanResults();
            if(currentResult==null){
                Log.i(TAG,"current가 널이요ㅠㅠㅠ");
            }
            else{
                Log.i(TAG,"current size:"+currentResult.size());
            }
        }
        else if(minute>0){//1분마다 prev와 current 리스트를 비교하기 위해 설정
            prevResult=currentResult;
            currentResult=wifimanager.getScanResults();
            Log.i(TAG,"prev size:"+prevResult.size());
            Log.i(TAG,"current size:"+currentResult.size());
        }
        unregisterReceiver(mReceiver);
    }

    public void WiFiSort() {//FG에서 사용하기 위해 WiFi 리스트를 세기가 큰 순서로 정렬
        Log.i(TAG,"WiFISort()");
        sortResult=new ArrayList<ScanResult>();
        if(currentResult.size()==0){
            return;
        }
        ScanResult max;
        //current에서 max값을 찾아 sort에 저장
        for(int i=0;i<currentResult.size();i++){
            max=currentResult.get(i);//max값 지정
            for(int j=0;j<currentResult.size();j++){//처음부터 순회하며
                if(max.level<currentResult.get(j).level){//max보다 더 센 값이 있다면
                    max=currentResult.get(j);//max값 변경
                }
            }
            sortResult.add(max);//sort에 저장
            currentResult.remove(max);//중복 방지를 위해 max삭제
            i=-1;//다시 처음부터 시작
        }
        for(int i=0;i<sortResult.size();i++){//remove로 인해 현재 current.size가 0이므로 다시 추가
            currentResult.add(sortResult.get(i));
        }
        Log.i(TAG,"current size: "+currentResult.size()+", sort size: "+sortResult.size());
    }


    // 서버에 WiFi 리스트 보내주기 위한 메소드
    public void postWiFiInfo(List<ScanResult> resultList){
        Log.i(TAG,"postWiFiInfo()");
        map = new HashMap<String,Integer>();
        for(int i=0;i<5;i++){
            if(i>=resultList.size()-1){//리스트가 5개까지 존재하지 않을 경우
                break;
            }
            ScanResult result=resultList.get(i);
            map.put(result.SSID,result.level);
        }
        Log.i(TAG,"map size: "+map.size());
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
                        .setSmallIcon(R.drawable.alarm) // 아이콘 임의로 아무거나 설정>>수정
                        .setContentTitle("정보 등록 가능")
                        .setContentText("1분 동안 위치 변경X -> 정보등록 가능합니다")
                        .setAutoCancel(true); // 클릭 시 지우기
        Intent resultIntent=new Intent(getApplicationContext(),MainActivity.class);
        //MainActivity에 경도,위도 전송
        resultIntent.putExtra("lat",gpsListener.latitude);
        resultIntent.putExtra("lng",gpsListener.longitude);
        //MainActivity에 WifiList 전송
        resultIntent.putParcelableArrayListExtra("wifiList", (ArrayList<? extends Parcelable>) sortResult);

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

    // 서버에 보낼 메소드
    public void pushInfo(){
        Log.i(TAG,"pushInfo()");
        try{
            NetworkManager.add(new Runnable() {
                @Override
                public void run() {
                    if(map==null){
                        Log.i(TAG,"map이 널");
                    }
                    WiFiSort();
                    postWiFiInfo(sortResult);//서버에 보내줄 와이파이 리스트 map에 넣기
                    Log.i(TAG,"pushInfo() "+deviceID+" "+gpsListener.latitude+" "+gpsListener.longitude+" "+map.get("CNU WiFi"));
                    String site = NetworkManager.url + "/locationbg";
                    try {
                        site+="?X="+gpsListener.latitude+"&Y="+gpsListener.longitude+"&Radius=0.0001&Category=ALL"+"&WifiList="+mapToJson(map);//36.3628449 127.350014200000032 +mapToJson(map)
                        URL url=new URL(site);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();//URL 연결한 객체 생성
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {//연결이 되면{
                            Log.i(TAG,"서버와 연결됨");
                            Log.i(TAG,site);
                        }
                        // 스트림 추출
                        InputStream is=conn.getInputStream();
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
                        if(rec_data.equals("")){
                            category="";
                        }else{
                            // 객체를 추출한다.(장소하나의 정보)
                            JSONArray root=new JSONArray(rec_data);
                            JSONObject obj1=root.getJSONObject(0);
                            JSONObject obj2=root.getJSONObject(1);
                            category=obj1.getString("place_type");
                            placeID=obj2.getString("place_id");
                            Log.i(TAG,"추출 결과 place_type: "+category);
                            Log.i(TAG,"추출 결과 place_ID: "+placeID);
                        }


                    } catch (MalformedURLException e) {// for URL
                        e.printStackTrace();
                    } catch (IOException e) {// for URLConnection
                        e.printStackTrace();
                    }
                    catch (JSONException e) {// for mapToJson()
                        e.printStackTrace();
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private class GPSListener implements LocationListener {
        double latitude,longitude,altitude, distance;
        float accuracy;
        String provider;
        public void onLocationChanged(Location location) {
            minute++;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude=location.getAltitude();
            accuracy=location.getAccuracy();
            provider=location.getProvider();
            Log.i(TAG,"현재 위치 "+minute+" 분 lat: "+latitude+" lng: "+longitude+" alt: "+altitude+" acc: "+accuracy+" pro: "+provider);
            /*
             이전 GPS와 현재 GPS의 위치가 같은 범위에 있는 지 확인한 후
             같다면 WiFi 정보가 같은지 확인한다
             */
            if(prev==null){//처음 GPS 위치를 받아온 것이라면
                prev=location;//5분뒤의 위치와 비교하기 위함
                Log.i(TAG,"GPS 처음 prev lat,lng:"+prev.getLatitude()+", "+prev.getLongitude());
                setPrevCurrent();//1분마다 와이파이 prev와 current 갱신
            }
            else{//이전 값이 존재한다면(5분 전의 GPS 값이 존재)
                // 서버한테 위치 보내주고 등록된 장소인지 확인하기

                if(minute==1) {//처음 체크할 때
                    Log.i(TAG,"1분");
                    setPrevCurrent();// WiFi 스캔을 시작하여 같은 위치가 맞는 지 판별
                    threeWiFi(prevResult,currentResult);//상위 3개 와이파이 비교
                    changedWiFiCount(prevResult,currentResult);//변경된 와이파이 갯수 확인
                    pushInfo();//현재 위치의 정보 보내주기
                   // getServerData();// 위치가 서버에 존재하는 지 확인
                    //Log.i(TAG, "여기!0! category: " + category);
                }
                if(category==null||category.equals("null")||category.equals("")){// 등록되지 않은 장소라면
                    Log.i(TAG,minute+"분, 카테고리가 null->노티피 띄우기");
                    WiFiSort();// FG에 보내줄 와이파이 정렬
                    setNotifi();// 노티피케이션 띄우기
                    minute=0;//FG에 등록 요청 후 minute은 초기화
                    category="";//카테고리도 초기화
                }
                else{// 서버에 등록된 장소라면 카테고리별로 시간 측정
                    // 현재 위치 카테고리에 따라 머무르는 시간, 거리 범위가 달라짐
                    if(category.equals("CAFE")||category.equals("CONVENIENCE")){//카페, 편의점이라면 5분 동안 있는 지 체크
                        if(minute>=5){//5분이 지났으면
                            distance=prev.distanceTo(location);
                            if(distance<=10){//이전 위치와 거리 차가 10m 내이면 같은 위치에 있다고 판별
                                Log.i(TAG,"GPS 현재 위치 변경X (-> WiFi도 바뀌었는지 확인하러 함수호출)");
                                setPrevCurrent();// WiFi 스캔을 시작하여 같은 위치가 맞는 지 판별
                                threeWiFi(prevResult,currentResult);//상위 3개 와이파이 비교
                                changedWiFiCount(prevResult,currentResult);//변경된 와이파이 갯수 확인
                                pushInfo();//현재 위치의 정보 보내주기
                                // 5분 이상 같은 위치에 머무르고 있으므로 서버에게 디바이스아이디와 머무른 시간 알려주기'
                                String toServer="/locationbg?DeviceId="+deviceID+"&minute="+minute+"&PlaceId="+placeID;
                                if(minute==5){//여기 처음 방문한다면
                                    toServer+="&new"+1;
                                }
                                else{//현재 위치에 계속 머무르는 중이라면
                                    toServer+="&new"+0;
                                }
                                nm.postInfo(toServer);
                            }
                            else{//위치가 바뀌었다면
                                prev=location;//prev 값을 현재 location 값으로 변경
                                Log.i(TAG,"GPS 현재 위치 변경됨");
                                minute=0;//초기화
                            }
                        }
                        else{//5분이 안되었으면 될 때까지 기다림
                            minute++;
                            Log.i(TAG,minute+"분");
                        }
                    }
                    if(category.equals("RESTAURANT")||category.equals("FACILITY")){//식당이거나 편의시설이라면 5분 동안 있는 지 체크
                        if(minute>=30){//30분이 지났으면
                            distance=prev.distanceTo(location);
                            if(distance<=10){//이전 위치와 거리 차가 10m 내이면 같은 위치에 있다고 판별
                                Log.i(TAG,"GPS 현재 위치 변경X (-> WiFi도 바뀌었는지 확인하러 함수호출)");
                                setPrevCurrent();// WiFi 스캔을 시작하여 같은 위치가 맞는 지 판별
                                threeWiFi(prevResult,currentResult);//상위 3개 와이파이 비교
                                changedWiFiCount(prevResult,currentResult);//변경된 와이파이 갯수 확인
                                pushInfo();//현재 위치의 정보 보내주기
                                // 30분 이상 같은 위치에 머무르고 있으므로 서버에게 디바이스아이디와 머무른 시간 알려주기
                                String toServer="/locationbg?DeviceId="+deviceID+"&minute="+minute+"&PlaceId="+placeID;
                                if(minute==30){//여기 처음 방문한다면
                                    toServer+="&new"+1;
                                }
                                else{//현재 위치에 계속 머무르는 중이라면
                                    toServer+="&new"+0;
                                }
                                nm.postInfo(toServer);
                            }
                            else{//위치가 바뀌었다면
                                prev=location;//prev 값을 현재 location 값으로 변경
                                Log.i(TAG,"GPS 현재 위치 변경됨");
                                minute=0;//초기화
                            }
                        }
                        else{//30분이 안되었으면 될 때까지 기다림
                            minute++;
                            Log.i(TAG,minute+"분");
                        }
                    }
                    if(category.equals("TOILET")){//편의점라면 5분 동안 있는 지 체크
                        if(minute>=1){//1분이 지났으면
                            distance=prev.distanceTo(location);
                            if(distance<=10){//이전 위치와 거리 차가 10m 내이면 같은 위치에 있다고 판별
                                Log.i(TAG,"GPS 현재 위치 변경X (-> WiFi도 바뀌었는지 확인하러 함수호출)");
                                setPrevCurrent();// WiFi 스캔을 시작하여 같은 위치가 맞는 지 판별
                                threeWiFi(prevResult,currentResult);//상위 3개 와이파이 비교
                                changedWiFiCount(prevResult,currentResult);//변경된 와이파이 갯수 확인
                                pushInfo();//현재 위치의 정보 보내주기
                                // 5분 이상 같은 위치에 머무르고 있으므로 서버에게 디바이스아이디와 머무른 시간 알려주기
                                String toServer="/locationbg?DeviceId="+deviceID+"&minute="+minute+"&PlaceId="+placeID;
                                if(minute==1){//여기 처음 방문한다면
                                    toServer+="&new"+1;
                                }
                                else{//현재 위치에 계속 머무르는 중이라면
                                    toServer+="&new"+0;
                                }
                                nm.postInfo(toServer);
                            }
                            else{//위치가 바뀌었다면
                                prev=location;//prev 값을 현재 location 값으로 변경z
                                Log.i(TAG,"GPS 현재 위치 변경됨");
                                minute=0;//초기화
                            }
                        }
                        else{//1분이 안되었으면 될 때까지 기다림
                            minute++;
                            Log.i(TAG,minute+"분");
                        }
                    }
                }
            }
        }
        public void onProviderDisabled(String provider) {
            // GPS가 꺼지면 함수 호출
            checkGPS();
            Log.i(TAG,"onProviderDisabled()");
        }
        public void onProviderEnabled(String provider) {
            // GPS가 다시 켜지면 함수 호출
            Log.i(TAG,"onProviderEnabled()");
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG,"onStatusChanged()");
        }
    }
}