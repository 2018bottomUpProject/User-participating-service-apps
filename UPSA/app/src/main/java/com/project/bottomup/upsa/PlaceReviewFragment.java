package com.project.bottomup.upsa;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PlaceReviewFragment extends Fragment {
    private static final String TAG = "PlaceReview";
    Handler handler;
    JSONArray root;
    int placeId;
    String placeName;
    ArrayList<HashMap<String,String>> list;
    HashMap<String, String> item;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_place_review, container, false);
        Log.i(TAG,"OnCreateView");

        // 리뷰 5개씩 출력
        final int index_start = 0;
        final int index_end = 4;

        final TextView review_explain = (TextView) rootView.findViewById(R.id.textView_placeReview);
        list =  new ArrayList<HashMap<String,String>>();

        // document activity에서 placeId를 받아오기
        Bundle bundle = getArguments();
        if(bundle != null){
            placeId = bundle.getInt("_id");
            placeName = bundle.getString("placeName");
        }

        // 서버와 연결
        try {
            NetworkManager.add(new Runnable() {
                @Override
                public void run() {
                    try{
                        Log.i(TAG,"쓰레드런");
                        String site = NetworkManager.url + "/review?PlaceId="+placeId+"&index_start="+index_start+"&index_end="+index_end;
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
                                root=new JSONArray(rec_data);
                                for(int i=0; i<root.length(); i++){
                                    JSONObject obj1=root.getJSONObject(i);
                                    item = new HashMap<String, String>();
                                    item.put("article", obj1.getString("article"));
                                    item.put("timestamp",obj1.getString("timestamp"));
                                    list.add(item);
                                }
                                handler.postDelayed(new Runnable(){
                                    @Override
                                    public void run() {
                                        if(root.length()>0){
                                            review_explain.setText("\" "+placeName + " \"의 리뷰는 다음과 같습니다.");
                                            // ArrayAdapter 생성. 아이템 View를 하나의 텍스트뷰로 구성하도록 레이아웃을 설정함.
                                            final SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, android.R.layout.simple_list_item_2,
                                                    new String[]{"article", "timestamp"}, new int[]{android.R.id.text1, android.R.id.text2});
                                            final ListView review_detail = (ListView)rootView.findViewById(R.id.printContainer_review);
                                            review_detail.setAdapter(adapter);
                                        }else{
                                            review_explain.setText("\" "+placeName + " \"에 등록된 리뷰가 없습니다.");
                                        }
                                    }
                                },3000); //3초 뒤에 Runner객체 실행하도록 함
                            }
                            connection.disconnect(); // 연결 끊기
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rootView;
    }
}
