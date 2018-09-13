package com.project.bottomup.upsa;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkManager {
    private static final String TAG = "NetworkManager";
    public static String url = "http://oreh.onyah.net:8899"; //서버 주소192.168.1.52:8899
    private static InnerThread thread = new InnerThread();
    //runnable을 받을 큐
    private static ConcurrentLinkedQueue<Runnable> concurrentLinkedQueue = new ConcurrentLinkedQueue<Runnable>();
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

    public static Object postInfo(final String str){
        try{
            add(new Runnable() {
                @Override
                public void run() {
                    try{
                        Log.i(TAG,"쓰레드런");
                        String site = url + str;
                        URL url = new URL(site);
                        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                        //전달방식 -> POST
                        connection.setRequestMethod("POST");
                        connection.setConnectTimeout(2000);
                        //서버로부터 메시지를 받을 수 있도록 함.
                        connection.setDoInput(true);
                        //서버로 데이터를 전송할 수 있도록 함.
                        connection.setDoOutput(true);

                        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            Log.i(TAG,"서버 연결 성공");
                        }
                        connection.disconnect();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        //**서버에서 받아와서 넘겨주는 코드 작성해야함**
        return 3; // placeId 임의설정
    }
}