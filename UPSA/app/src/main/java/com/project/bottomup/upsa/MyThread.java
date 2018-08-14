package com.project.bottomup.upsa;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MyThread {
    private static InnerThread thread = new InnerThread();
    //runnable을 받을 큐
    private static ConcurrentLinkedQueue<Runnable> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    public static void add(Runnable run) throws InterruptedException {
        concurrentLinkedQueue.add(run);
        thread.notify();
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
                        Log.e("MyThread","wait");
                        this.wait();
                        //***sleep으로 바꿔봅시다.***
                    }
                    else{
                        tempRunnable = concurrentLinkedQueue.poll();
                        Log.e("MyThread","notify");
                        if(tempRunnable!=null) {
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