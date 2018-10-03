package com.project.bottomup.upsa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class MarkerFragment extends DialogFragment {
    private static final String TAG = "MarkerFragment";
    ArrayList<DocumentInfo> markerClick = new ArrayList<DocumentInfo>();
    ArrayList<String> name = new ArrayList<String>();
    ArrayList<Integer> id = new ArrayList<Integer>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_marker, null);
        builder.setView(view);
        try {
            Bundle bundle = this.getArguments();
            markerClick = bundle.getParcelableArrayList("markerClick");

            for (int i = 0; i < markerClick.size(); i++) {
                Log.i(TAG, "가게이름" + (i+1) + " : " + markerClick.get(i).getPlaceName());
                name.add(i,markerClick.get(i).getPlaceName());
                id.add(i,markerClick.get(i).getPlaceId());
            }
        }catch(Exception e){
            Toast.makeText(getActivity(), "등록된 장소(시설)가 없습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,name);

        // listView 생성 및 adapter 지정
        final ListView listview = (ListView)view.findViewById(R.id.markerContainer) ;
        listview.setAdapter(adapter);

        // listView 아이템 클릭했을 때 이벤트
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    //final int placeId = id.get(position);
                    final int placeId = 19; //임의설정
                    final Intent intent = new Intent(getActivity(), DocumentActivity.class);

                    final StringBuffer sb = new StringBuffer();
                    final boolean[] toilet = new boolean[3];
                    final boolean[] parking = new boolean[2];

                    NetworkManager.add(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Log.i(TAG,"쓰레드런");
                                String site = NetworkManager.url + "/document/"+placeId;
                                Log.i(TAG,"site = "+site);

                                URL url = new URL(site);
                                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                                if(connection != null) {
                                    connection.setConnectTimeout(2000);
                                    connection.setUseCaches(false);
                                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                        // 스트림 추출
                                        InputStream is = connection.getInputStream();
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

                                        // JSON 데이터 분석
                                        // 객체를 추출한다.(장소하나의 정보)
                                        JSONObject root = new JSONObject(rec_data);
                                        //placeId 전송
                                        intent.putExtra("_id",placeId);
                                        // 위도 경도 추출 및 전송
                                        intent.putExtra("lat", root.getDouble("lat"));
                                        intent.putExtra("lng",root.getDouble("lng"));
                                        // 빌딩 이름 추출 및 전송
                                        intent.putExtra("building",root.getString("building"));
                                        // 장소 이름 추출 및 전송
                                        intent.putExtra("name", root.getString("name"));
                                        // 전화번호 추출 및 전송
                                        intent.putExtra("tel", root.getString("tel"));
                                        //카테고리 추출 및 전송
                                        String category = root.getString("category");
                                        intent.putExtra("category",category);
                                        // 부가 정보 추출 및 전송
                                                intent.putExtra("extraInfo",root.getString("extraInfo"));
                                        // 화장실에 대한 정보 추출 및 전송
                                        toilet[0] = root.getBoolean("화장실");
                                        toilet[1] = root.getBoolean("휴지 유무");
                                        toilet[2] = root.getBoolean("남녀공용");
                                        intent.putExtra("toilet",toilet);
                                        // 주차 장소에 대한 정보 추출 및 전송
                                        parking[0] = root.getBoolean("주차 공간");
                                        parking[1] = root.getBoolean("유료");
                                        intent.putExtra("parking",parking);
                                        // 리뷰에 대한 정보 추출 및 전송
                                        String review = root.getJSONArray("review").toString();
                                        intent.putExtra("review", review);
                                        // 메뉴 정보 추출 및 전송(카테고리가 카페, 레스토랑일 때만)
                                        if(category == "CAFE" || category == "RESTAURANT"){
                                            String menu = root.getJSONArray("menu").toString();
                                            intent.putExtra("menu", menu);
                                        }
                                    }
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    //해당 장소에 대한 document activity 열기
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        return builder.create();
    }
}
