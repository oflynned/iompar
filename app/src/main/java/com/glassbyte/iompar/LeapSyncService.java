package com.glassbyte.iompar;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ed on 30/12/15.
 */
public class LeapSyncService extends IntentService {

    Leap leap;

    public LeapSyncService() {
        super("LeapSyncService");
        leap = new Leap(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        leap.scrape();
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Activity activity){
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
