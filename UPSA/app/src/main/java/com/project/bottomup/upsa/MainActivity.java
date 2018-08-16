package com.project.bottomup.upsa;

import android.Manifest;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback ,AddDialogFragment.OnCompleteListener, GoogleMap.OnMarkerClickListener{
    // 구글 서버로 부터 받아온 데이터를 저장할 리스트
    ArrayList<Double> lat_list; //위도
    ArrayList<Double> lng_list; //경도
    ArrayList<String> name_list; //이름
    ArrayList<String> vicinity_list; //주소
    // 지도의 표시한 마커(주변장소표시)를 관리하는 객체를 담을 리스트
    ArrayList<Marker> markers_list;

    // 카테고리 배열 (앱 UI에 사용)
    String[] category_ui_array={ "전체","카페","식당","공원" };
    // 타입값 배열 (url 요청코드는 영어로 값이 전달됨)
    String[] category_type_array={ "all","cafe","restaurant","park" };

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
    LatLng position = new LatLng(37.56, 126.97); //초기설정(서울)37.56, 126.97
    LatLng Clickgps;

    //마커에 띄울 장소 이름 배열
    ArrayList<String> markerClick;
    //notification 받았을 때 현재 사용자 위치
    Double notifiLat;
    Double notifiLng;

    //툴바 생성
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate()");

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //서비스 실행
        this.startService(new Intent(this,BackgroundService.class));
        Log.d("MainActivity","startService");

        //지도 불러오기
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //객체 생성
        lat_list=new ArrayList<>();
        lng_list=new ArrayList<>();
        name_list=new ArrayList<>();
        vicinity_list=new ArrayList<>();
        markers_list=new ArrayList<>();

        markerClick=new ArrayList<>();
        //임의로 장소 이름 설정 // test용
        markerClick.add("에이플러스");
        markerClick.add("새빨간 죠스 찜닭");
        markerClick.add("국민야시장");
        //임의로 장소 이름 설정 // test용

        checkPermission();

        //background에서 넘어온 lat,lng 저장
        Intent intent = getIntent();
        try{
            if(intent == null){
                throw new Exception();
            }
            notifiLat = intent.getDoubleExtra("lat",0);
            notifiLng = intent.getDoubleExtra("lng",0);
            Log.i("MainActivity", "getIntent lat : " + notifiLat);
            Log.i("MainActivity", "getIntent lng : " + notifiLng);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "notification 오류", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLng(position)); //서울로 초기위치 설정
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Point screenPt = map.getProjection().toScreenLocation(latLng);
                Clickgps = map.getProjection().fromScreenLocation(screenPt);

                MarkerOptions markerOptions = new MarkerOptions();
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.)); //아이콘 변경
                markerOptions.position(latLng); //마커위치설정
                markerOptions.title("장소를 등록해주세요");
                map.clear(); //맵 초기화
                map.addMarker(markerOptions); //마커 생성
                show();
            }
        });

        //마커 클릭에 대한 리스너
        map.setOnMarkerClickListener(this);
    }

    void show()
    {
        DialogFragment newFragment = new AddDialogFragment();
        newFragment.show(getFragmentManager(), "dialog"); //"dialog"라는 태그를 갖는 프래그먼트를 보여준다.
    }

    //마커 클릭했을 때 이벤트
    @Override
    public boolean onMarkerClick(Marker marker){
        try{
            if(markerClick.size()<=0){
                throw new Exception();
            }
            DialogFragment newFragment = new MarkerFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("markerClick",markerClick);
            newFragment.setArguments(bundle);
            newFragment.show(getFragmentManager(), "marker"); //"marker"라는 태그를 갖는 프래그먼트를 보여준다.
        } catch(Exception e){
            Toast.makeText(this, "등록된 장소(시설)가 없습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onInputedData(String admit) {
        //DialogFragment에서 OK했을 때
        if(admit == "OK") {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            intent.putExtra("현재lat", Clickgps.latitude);
            intent.putExtra("현재lng", Clickgps.longitude);
            startActivity(intent);
        }
        //DialogFragment에서 NO했을 때
        else if(admit == "NO"){
        }
        map.clear();
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
        }
        // 새로운 위치 측정
        GpsListener listener = new GpsListener();
        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, listener);
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener);
        }

        //현재 WIFI위치(또는 GPS)가 켜져있지 않은 경우
        if((!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))||(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))){
            Toast.makeText(this,"GPS(또는 WIFI위치)사용이 설정되어 있지 않습니다.",Toast.LENGTH_LONG).show();
        }
    }

    //네트워크 연결상태인지 확인하는 메서드
    public boolean isConnected() {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=connectivityManager.getActiveNetworkInfo();
        boolean connected = false;
        if(activeNetwork!=null && activeNetwork.isConnectedOrConnecting()){
            connected = true;
        }
        return connected;
    }

    // GPS Listener
    class GpsListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // 현재 위치 값을 저장
            myLocation = location;
            // 위치 측정을 중단
            manager.removeUpdates(this);
            // 지도를 현재 위치로 이동
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
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        // 지도 모드 변경
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

    // 메뉴에 대한 메서드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    // 메뉴 항목을 터치하면 호출되는 메서드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // id 추출
        int id = item.getItemId();
        switch(id){
            case R.id.menu1 :           // 현재 위치

                getMyLocation();
                break;
            case R.id.menu2 :           // 카테고리 검색
                //네트워크 연결되지 않은 경우
                if(isConnected()==false){
                    Toast.makeText(this, "네트워크가 연결되지 않았습니다.", Toast.LENGTH_LONG).show();
                }
                //네트워크가 연결된 경우
                else {
                    showCategoryList();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 카테고리 검색 리스트
    private void showCategoryList() {
        //AlertDialog.Builder을 사용하여 layout을 가운데를 밝게 주변은 어둡게 구현
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("검색할 카테고리 선택");
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(
                //simple_list_item_1 : 하나의 텍스트뷰로 구성
                this,android.R.layout.simple_list_item_1,category_ui_array
        );
        DialogListener listener=new DialogListener();
        builder.setAdapter(adapter,listener);
        builder.setNegativeButton("취소",null);
        builder.show();
    }
    // 다이얼로그의 리스너
    class DialogListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // 선택한 항목 인덱스번째의 타입값을 가져온다.
            String type=category_type_array[i];
            getNearbyPlace(type);
        }
    }

    //주변 정보 가져오기
    public void getNearbyPlace(String type_keyword){
        NetworkThread thread=new NetworkThread(type_keyword);
        thread.start();
    }

    //네트워크에 연결할 때는 항상 메인스레드가 아닌 별도의 스레드를 통해 해당 기능을 구현
    //주변 정보 가져오는 스레드
    class NetworkThread extends Thread{
        String type_keyword;
        public NetworkThread(String type_keyword){
            this.type_keyword=type_keyword;
        }
        @Override
        public void run() {
            try{
                //데이터를 담아놓을 리스트를 초기화한다.
                lat_list.clear();
                lng_list.clear();
                name_list.clear();
                vicinity_list.clear();

                // 접속할 페이지 주소
                //google 장소 검색 참고 (https://developers.google.com/places/web-service/search?hl=ko)
                String site="https://maps.googleapis.com/maps/api/place/nearbysearch/json"; //json 형식으로 나타냄
                site+="?location="+myLocation.getLatitude()+","
                        +myLocation.getLongitude()
                        +"&radius=1000&sensor=false&language=ko"; //1000미터
                if(type_keyword!=null){
                    if(type_keyword.equals("all")==false){
                        site+="&types="+type_keyword;
                    }
                    else if(type_keyword.equals("all")==true) {
                        //전체 검색 구현해야함
                    }
                }
                site+="&key=AIzaSyAKZhz5lT8ga5YWEgkPsxPfYVz4S9J1Wu8";
                // 접속
                URL url=new URL(site);
                URLConnection conn=url.openConnection();
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
                String rec_data=buf.toString();
                // JSON 데이터 분석
                JSONObject root=new JSONObject(rec_data);
                //status 값을 추출한다.
                String status=root.getString("status");
                // 가져온 값이 있을 경우
                if(status.equals("OK")){
                    //results 배열을 가져온다
                    JSONArray results=root.getJSONArray("results");
                    // 개수만큼 반복한다.
                    for(int i=0; i<results.length() ; i++){
                        // 객체를 추출한다.(장소하나의 정보)
                        JSONObject obj1=results.getJSONObject(i);
                        // 위도 경도 추출
                        JSONObject geometry=obj1.getJSONObject("geometry");
                        JSONObject location=geometry.getJSONObject("location");
                        double lat=location.getDouble("lat");
                        double lng=location.getDouble("lng");
                        // 장소 이름 추출
                        String name=obj1.getString("name");
                        // 대략적인 주소 추출
                        String vicinity=obj1.getString("vicinity");
                        // 데이터를 담는다
                        lat_list.add(lat);
                        lng_list.add(lng);
                        name_list.add(name);
                        vicinity_list.add(vicinity);
                    }
                    //지도에 표시
                    showMarker();
                }
                else{ //가져온 값이 없는 경우
                    Toast.makeText(getApplicationContext(),"저장된 장소가 없습니다.",Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){e.printStackTrace();}
        }
    }


    // 지도에 표시해주는 메서드
    public void showMarker(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 지도에 마커를 표시
                // 지도에 전에 표시되어있던 마커를 모두 제거
                for(Marker marker : markers_list){
                    marker.remove();
                }
                markers_list.clear();
                // 가져온 데이터의 수 만큼 마커 객체를 만들어 표시한다.
                for(int i= 0 ; i< lat_list.size() ; i++){
                    // 값 추출
                    double lat= lat_list.get(i);
                    double lng=lng_list.get(i);
                    String name=name_list.get(i);
                    String vicinity=vicinity_list.get(i);
                    // 생성할 마커의 정보를 가지고 있는 객체를 생성
                    MarkerOptions options=new MarkerOptions();
                    // 위치설정
                    LatLng pos=new LatLng(lat,lng);
                    options.position(pos);
                    // 말풍선이 표시될 값 설정
                    options.title(name);
                    options.snippet(vicinity);
                    // 아이콘 설정
                    //BitmapDescriptor icon= BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker);
                    //options.icon(icon);
                    // 마커를 지도에 표시한다.
                    Marker marker=map.addMarker(options);
                    markers_list.add(marker);
                }
            }
        });
    }

}