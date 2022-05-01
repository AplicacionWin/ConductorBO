package com.nikola.driver.fcm;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.nikola.driver.network.APIManager.AsyncTaskCompleteListener;
import com.nikola.driver.network.APIManager.VollyRequester;
import com.nikola.driver.ui.activity.NoInternetActivity;
import com.nikola.driver.utils.AndyUtils;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.PreferenceHelper;

import java.util.HashMap;
import java.util.List;

import static com.nikola.driver.ui.activity.NoInternetActivity.isInternetActivityCalled;

/**
 * Created by user on 1/2/2017.
 */

public class OnBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(isAppIsInBackground(context))
        {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            context.startActivity(launchIntent);
        }

    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}