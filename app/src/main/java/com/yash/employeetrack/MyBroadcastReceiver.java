package com.yash.employeetrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by vipin.jain on 4/27/2018.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BeaconNotifier.show(context);
       context.startService(new Intent(context, Sender.class));
    }
}
