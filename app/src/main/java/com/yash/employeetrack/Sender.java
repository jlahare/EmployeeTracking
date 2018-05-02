package com.yash.employeetrack;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vipin.jain on 4/27/2018.
 */;import javax.security.auth.login.LoginException;

public class Sender extends Service implements BeaconConsumer {
    private boolean isRunning = false;
    private Timer executor = null;
    private final int TIMER_DELAY = 8000;
    private final String TAG = "Beacon";
    private Subscriber mqttManager;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        mqttHandler.sendMessage(mqttHandler.obtainMessage());
        startBeconScanning();
        initTask();

        return START_STICKY;
    }
    Handler mqttHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            startMQTT();
        }
    };

    private void startMQTT() {
        try {
            mqttManager = new Subscriber(this);
        } catch (Throwable throwable) {
            Log.e(TAG, "ERROR MQTT :" + throwable.toString());
        }

    }

    private void initTask() {

        if (executor == null)
            executor = new Timer();
        else {
            cancelTimer();
        }
        executor.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isRunning) {
                    sendDataToServer();
                }
            }
        }, TIMER_DELAY);
    }

    private void cancelTimer() {
        try {
            executor.cancel();
        } catch (Throwable t) {
        }
        try {
            executor.purge();
        } catch (Throwable t) {
        }
        executor = null;
        executor = new Timer();
    }


    private void sendDataToServer(){
        Log.e("TAG", "TICK");

        if(mqttManager!=null)
        {
            Log.e("TAG", "STEP  -1 size = " + beaconList.size());
            boolean isNotify = false;
            for (HashMap.Entry<String, BeaconInfo> entry : beaconList.entrySet())
            {
                if(isNotify==false)
                {
                    BeaconNotifier.show(getApplicationContext());
                    isNotify = true;
                }
                Log.e("TAG", "STEP  -1.X");

                try {

               /* System.out.println("Key = " + entry.getKey() +
                        ", Value = " + entry.getValue());*/
                    BeaconInfo beaconInfo = entry.getValue();

                    JSONObject json = new JSONObject();
                    json.put("deviceId", getDeviceId());

                    JSONObject jsonChild = new JSONObject();
                    jsonChild.put("name", beaconInfo.getName());
                    jsonChild.put("macId", beaconInfo.getMac());
                    jsonChild.put("rssi", beaconInfo.getSignal());
                    jsonChild.put("uuid", beaconInfo.getUuid());
                    jsonChild.put("timeStamp", beaconInfo.getTimeStamp());


                    json.put("beacon", jsonChild);

                    Log.e(TAG, "Sending : " + json.toString());

                    mqttManager.sendMessage(json.toString());

                }catch (Throwable throwable)
                {
                    Log.e(TAG ,"Send Error 1 : " + throwable.toString());
                }
            }

            try {
                mqttManager.sendMessage("//*****************************************//");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else
        {
            Log.e(TAG ,"MQTT is Null ");
        }

        initTask();
    }


    public String getDeviceId() {
        SharedPreferences sh_Pref = getSharedPreferences("Credentials", MODE_PRIVATE);
        return  sh_Pref.getString("deviceId", "1");
    }



    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.e("Service" , "Stopping service");
        isRunning = false;
        cancelTimer();
        beaconManager.unbind(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //=============== BLE RELATED CODE =====================

    private BeaconManager beaconManager;
    private void startBeconScanning()
    {

        beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        beaconManager.getBeaconParsers().add(new BeaconParser()
                //.setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.setForegroundBetweenScanPeriod(100);
        beaconManager.setBackgroundScanPeriod(100);
        beaconManager.setBackgroundBetweenScanPeriod(100);
        beaconManager.setBackgroundMode(false);
        beaconManager.setRegionExitPeriod(100);
        beaconManager.setAndroidLScanningDisabled(false);

        try {
            beaconManager.updateScanPeriods();
        } catch (Exception e) {
            Log.e(TAG , "Error bManager : " + e.toString());
        }



        beaconManager.bind(this);

        Toast.makeText(getApplicationContext() , "Scanning Started" , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.e(TAG , "Inside onBeaconServiceConnect()");
        Region region = new Region("MyGroup" , null , null,null);

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.e(TAG , "Inside didEnterRegion()");
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e(TAG , "error 1 didEnterRegion()" + e.toString());
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    //Toast.makeText(getApplicationContext() , "Gone : " +region.getUniqueId() , Toast.LENGTH_SHORT).show();
                    Log.e(TAG , "Inside didExitRegion()");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    Log.e(TAG , "Error Inside didExitRegion()" + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

                Log.e(TAG , "State is : " + i + region.getUniqueId()) ;
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region)
            {
                Log.e(TAG , "Inside didRangeBeaconsInRegion()");
                for (Beacon b : beacons)
                {


                    //BeaconInfo bi = new BeaconInfo(b.getBluetoothName() , ""+b.getId1() , ""+b.getRssi()  , ""+b.getBluetoothAddress() , ""+b.getDistance() , ""+b.getTxPower() , ""+b.getBeaconTypeCode());
                    String name = b.getBluetoothName();
                    String uuid = ""+b.getId1();
                    String rssi = ""+b.getRssi();
                    String mac =  ""+b.getBluetoothAddress();
                    String distance =  ""+ Beacon.getDistanceCalculator().calculateDistance(b.getTxPower()  , b.getRssi());
                    String txPower =  ""+b.getTxPower();
                    String typeCode = ""+b.getBeaconTypeCode();
                    String timeStamp = ""+(new Date()).getTime();

                    BeaconInfo bi = new BeaconInfo(name , uuid , rssi  , mac , distance , txPower , typeCode, timeStamp);

                    synchronized (beaconList) {
                        beaconList.put(bi.getMac(), bi);
                    }
                }

                /* ArrayList<BeaconInfo> list =new ArrayList<>();


                for (HashMap.Entry<String, BeaconInfo> entry : beaconList.entrySet()) {
                    System.out.println("Key = " + entry.getKey() +
                            ", Value = " + entry.getValue());
                    list.add(entry.getValue());
                }*/

                /*Message msg = handler.obtainMessage();
                msg.obj = list;
                handler.sendMessage(msg);*/

            }
        });


        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    HashMap<String , BeaconInfo> beaconList = new HashMap<>();

   /* Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            ArrayList<BeaconInfo> list = (ArrayList<BeaconInfo>) msg.obj;
            for(int i=0; i< list.size() ; i++)
            {
                BeaconInfo bi = list.get(i);
                LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.beacon_row ,null, false);
                ((TextView)view.findViewById(R.id.uuidTxt)).setText(bi.getUuid());
                ((TextView)view.findViewById(R.id.nameTxt)).setText(bi.getName());
                ((TextView)view.findViewById(R.id.signalTxt)).setText(bi.getSignal());
                ((TextView)view.findViewById(R.id.macTxt)).setText(bi.getMac());
                ((TextView)view.findViewById(R.id.distanceTxt)).setText(bi.getDistance());
                ((TextView)view.findViewById(R.id.powerTxt)).setText(bi.getPower());
                ((TextView)view.findViewById(R.id.typeTxt)).setText(bi.getBeaconType());
                container.addView(view);
            }
        }
    };*/

}
