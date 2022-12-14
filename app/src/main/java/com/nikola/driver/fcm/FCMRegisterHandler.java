package com.nikola.driver.fcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nikola.driver.utils.PreferenceHelper;

;

/**
 * Created by user on 6/29/2015.
 */
public class FCMRegisterHandler {

    private Activity activity;
    public static final String EXTRA_MESSAGE = "message";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    PreferenceHelper prefs;

    private String regid;
    private String TAG = "pavan";

    public FCMRegisterHandler(Activity activity, BroadcastReceiver mHandleMessageReceiver) {

        prefs = new PreferenceHelper(activity);

        try {
            this.activity = activity;
            if (checkPlayServices()) {
                regid = getRegistrationId(activity);
                prefs.putDeviceToken(regid);
                if (regid.isEmpty()) {
                    registerInBackground();
                }
            } else {
                Log.d(TAG, "No valid Google Play Services APK found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    private String getRegistrationId(Context context) {

        String registrationId = prefs.getRegistrationID();
        if (registrationId.isEmpty()) {
            Log.d(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getAppVersion();
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                msg = "Device registered, registration ID=" + regid;
                storeRegistrationId(activity, regid);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);

    }

    private void storeRegistrationId(Context context, String regId) {

        int appVersion = getAppVersion(context);
        Log.d(TAG, "Saving regId on app version " + appVersion);
        Log.d(TAG,"RegID "+regId);
        prefs.putAppVersion(appVersion);
        prefs.putRegisterationID(regId);
        prefs.putDeviceToken(regId);
    }
}
