package com.yash.employeetrack;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by jayesh.lahare on 4/27/2018.
 */

public final class BeaconNotifier
{
    //private static WeakReference<Context> weakContext;
    private static Context weakContext;

    private static int notificationId = 1001;
    private static NotificationCompat.Builder mBuilder;

    public static void show(Context context) {
        //weakContext = new WeakReference(context);
        weakContext = context;

        mBuilder = new NotificationCompat.Builder(context, "EMP_TRACKING")
                .setSmallIcon(R.drawable.ic_ibeacon_green)
                .setContentTitle("Beacon")
                .setContentText("Sending data")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, mBuilder.build());
        blink();
    }

    private static void blink() {
        Thread t = new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        Log.e("Test", " - " + i);
                        //handler.sendMessage(handler.obtainMessage());
                        blinkMe();
                       /* synchronized (mBuilder) {
                            mBuilder.setSmallIcon(R.drawable.blinker_sending);
                            mBuilder.notify();
                        }*/
                        sleep(1000);
                    }

                } catch (Throwable t) {
                    Log.e("Issue", "Error : " + t.toString());
                }
                cancelNotification();
            }
        };
        t.start();
    }

    private static int i = 0;
   /* private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (i) {
                    case 0:
                        mBuilder.setSmallIcon(R.drawable.ic_ibeacon_green);
                        break;
                    case 1:
                        mBuilder.setSmallIcon(R.drawable.ic_ibeacon_green_big);
                        break;
                }

                i++;
                i = i % 2;


                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(weakContext.getApplicationContext());
                notificationManager.notify(notificationId, mBuilder.build());
            } catch (Throwable throwable) {
                Log.e("Error ", "" + throwable.toString());
            }
        }
    };*/

    private static void blinkMe()
    {
        try {
            switch (i) {
                case 0:
                    mBuilder.setSmallIcon(R.drawable.ic_ibeacon_green);
                    break;
                case 1:
                    mBuilder.setSmallIcon(R.drawable.ic_ibeacon_green_big);
                    break;
            }

            i++;
            i = i % 2;


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(weakContext.getApplicationContext());
            notificationManager.notify(notificationId, mBuilder.build());
        } catch (Throwable throwable) {
            Log.e("Error ", "" + throwable.toString());
        }
    }

    public static void cancelNotification() {
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(weakContext.getApplicationContext());
            notificationManager.cancelAll();
        } catch (Throwable throwable) {
            Log.e("Cancel Error ", "" + throwable.toString());
        }
    }
}
