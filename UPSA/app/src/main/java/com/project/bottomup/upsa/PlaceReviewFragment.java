package com.project.bottomup.upsa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlaceReviewFragment extends Fragment {
    private static final String TAG = "PlaceReview";
    JSONArray root;
    int placeId;
    int index_start;
    int index_end;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place_review, container, false);
        Log.i(TAG,"OnCreateView");

        Bundle bundle = getArguments();
        if(bundle != null){
            placeId = bundle.getInt("_id");
        }
        index_start = 0;
        index_end = 4;

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

        Log.i(TAG, "review ? "+ root.toString());

        return rootView;
    }
}
