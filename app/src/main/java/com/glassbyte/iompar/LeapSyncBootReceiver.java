package com.glassbyte.iompar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ed on 30/12/15.
 */
public class LeapSyncBootReceiver extends BroadcastReceiver {

    LeapSyncAlarmReceiver alarmReceiver = new LeapSyncAlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            alarmReceiver.setAlarm(context);
        }
    }
}
