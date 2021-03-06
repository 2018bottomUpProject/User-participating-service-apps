package com.project.bottomup.upsa;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkManager {
    private static final String TAG = "NetworkManager";
    public static String url = "http://oreh.onyah.net:8899"; //서버 주소192.168.1.52:8899
    public static boolean isEnd = false;

    private static InnerThread thread = new InnerThread();
    //runnable을 받을 큐
    private static ConcurrentLinkedQueue<Runnable> concurrentLinkedQueue = new ConcurrentLinkedQueue<Runnable>();
    private static JSONObject jsonObject = new JSONObject(); //서버에서 받아온 data를 저장할 변수

    public static void init(){
        thread.start();
    }
    public static void add(Runnable run) throws InterruptedException {
        concurrentLinkedQueue.add(run);
        Log.e("NetworkManager","add_Queue");
    }
    private static class InnerThread extends Thread{
        Runnable tempRunnable;
        public InnerThread(ConcurrentLinkedQueue<Runnable> temp){
            concurrentLinkedQueue = temp;
        }

        public InnerThread() {

        }

        @Override
        public void run(){
            while(true){
                try{
                    //queue안에 아무 것도 들어있지 않을 경우
                    if(concurrentLinkedQueue.peek() == null){
                        sleep(500); //0.5초 슬립
                    }
                    else{
                        tempRunnable = concurrentLinkedQueue.poll();
                        if(tempRunnable!=null) {
                            Log.e("NetworkManager","run");
                            tempRunnable.run();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void postInfo(final String str, final String method){
        isEnd = false;
        try{
            add(new Runnable() {
                @Override
                public void run() {
                    try{
                        Log.i(TAG,"쓰레드런");
                        String site = url + str;
                        URL url = new URL(site);
                        Log.i(TAG, "url site=? "+site);
                        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                        if(connection != null){
                            //전달방식 -> POST
                            connection.setRequestMethod(method);
                            connection.setConnectTimeout(2000);
                            //서버로부터 메시지를 받을 수 있도록 함.
                            connection.setDoInput(true);
                            //서버로 데이터를 전송할 수 있도록 함.
                            connection.setDoOutput(true);

                            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                Log.i(TAG,"서버 연결 성공");
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
                                //JSONObject 추출
                                for(int i=0; i<root.length(); i++){
                                    jsonObject = root.getJSONObject(0);
                                    Log.i(TAG, jsonObject.toString());
                                }
                            }
                            connection.disconnect();
                        }
                        isEnd = true;
                    }catch(JSONException e){
                        Log.i(TAG, "JSON Exception 발생");
                        e.printStackTrace();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public JSONObject getResult(){
        Log.i(TAG,"getResult- "+ jsonObject.toString());
        return jsonObject;
    }
}