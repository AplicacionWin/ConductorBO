package com.nikola.driver.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import static android.content.ContentValues.TAG;

public class InstanceIdServices  extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        saveDeviceToken(token);
    }

    private void saveDeviceToken(String token) {
        PrefUtils.getInstance(this).setValue(PrefKeys.FCM_TOKEN, token);
    }

}
