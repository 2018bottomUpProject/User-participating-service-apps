package com.project.bottomup.upsa;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MyThread {
    private static InnerThread thread = new InnerThread();
    //runnable을 받을 큐
    private static ConcurrentLinkedQueue<Runnable> concurrentLinkedQueue = new ConcurrentLinkedQueue<Runnable>();
    public static void init(){
        thread.start();
    }
    public static void add(Runnable run) throws InterruptedException {
        concurrentLinkedQueue.add(run);
        Log.e("MyThread","add_Queue");
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
                            Log.e("MyThread","run");
                            tempRunnable.run();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}