package com.example.myadlib;

import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Description: this class offers background service. It monitors and fetches the user information
 * at fixed time intervals
 * @version:2.0
 */
public class BGService extends Service {
    private static String TAG="CourseWork:BGService";
    //it determines whether the background service continues running
    private static boolean keepGoing=true;
    public static int MSG_CODE=1;

    public static void stop(){
        BGService.keepGoing=false;
    }

    public static void start(){
        BGService.keepGoing=true;
    }

    public void onCreate(){
        super.onCreate();
        Log.i(TAG,"the service is created");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int id){
        Log.d(TAG, String.valueOf(BGService.keepGoing));
        new Thread(new Runnable(){
            @Override
            public void run() {
                while(BGService.keepGoing){
                    //this message code prompt the ad page to record the location
                    MyAdView.handler.sendEmptyMessage(MSG_CODE);
                    try {
                        //fetch data every 1 minutes
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, id);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
