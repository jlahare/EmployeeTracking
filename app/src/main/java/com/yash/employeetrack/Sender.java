package com.yash.employeetrack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vipin.jain on 4/27/2018.
 */;

public class Sender extends Service
{
    private boolean isRunning = false;
    private Timer executor = null;
    private final int TIMER_DELAY = 1000;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        isRunning = true;
        initTask();
        return super.onStartCommand(intent, flags, startId);
    }
    private void initTask()
    {

        if(executor == null)
            executor = new Timer();
        else
        {
           cancelTimer();
        }
        executor.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isRunning) {
                    sendDataToServer();
                }
            }
        } , TIMER_DELAY);
    }
    private void cancelTimer()
    {
        try {
            executor.cancel();
        } catch (Throwable t) {
        }
        try {
            executor.purge();
        }catch (Throwable t){
        }
        executor = null;
        executor = new Timer();
    }



    private void sendDataToServer()
    {
        Log.e("TAG" , "TICK");
        initTask();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.e("Service" , "Stopping service");
        isRunning = false;
        cancelTimer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
