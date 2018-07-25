package com.project.bottomup.upsa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// 부팅시 서비스(BackgroundService) 실행하기
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        // 시스템 상태가 부팅이 완료되었다면
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            // BackgroundService를 실행한다
            Intent i=new Intent(context, BackgroundService.class);
            context.startService(i);
        }
    }
}
