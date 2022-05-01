package com.nikola.driver.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.nikola.driver.R;
import com.nikola.driver.network.model.History;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.ui.activity.ChatActivity;
import com.nikola.driver.ui.activity.HistoryActivity;
import com.nikola.driver.ui.activity.MainActivity;
import com.nikola.driver.utils.AndyUtils;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.PreferenceHelper;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import java.util.Map;

import android.media.MediaPlayer;

import static android.content.ContentValues.TAG;

/**
 * Created by user on 6/29/2015.
 */
public class FCMIntentService extends FirebaseMessagingService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private PreferenceHelper preferenceHelper;
    private String date = "";
    private Map data;
    String from;
    public static String NOTIFICATION_CHANNEL_ID = "fcm_fallback_notification_channel";
    final long[] VIBRATE_PATTERN    = { 100, 200, 300, 400, 500, 400, 300, 200, 100 };
    @Override
    public void onMessageReceived(RemoteMessage message) {
        try {
            Log.d("msg", "onMessageReceived: " + message.getData().get("message"));
            Log.e("data", "msg" + message.getData());
            for (Map.Entry<String, String> entry : message.getData().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
            }
            Intent intent = null;
            String requestId = "", type = "", hostId = "";
            if (message.getData().get("request_id") != null)
                requestId = message.getData().get("request_id");
            if (message.getData().get("redirection_type") != null)
                type = message.getData().get("redirection_type");
            Log.e("Type ", type);
            switch (type) {
                case APIConstants.PushNotificationStatus.PUSH_NOTIFICATION_REDIRECT_HOME:
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    break;
                case APIConstants.PushNotificationStatus.PUSH_NOTIFICATION_REDIRECT_CHAT:
                    intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra(APIConstants.Params.PROVIDER_ID, "");
                    intent.putExtra(APIConstants.Params.USER_PICTURE, "");
                    intent.putExtra(APIConstants.Params.REQUEST_ID, requestId);
                    break;
                case APIConstants.PushNotificationStatus.PUSH_NOTIFICATION_REDIRECT_REQUEST_ONGOING:
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    break;
                case APIConstants.PushNotificationStatus.PUSH_NOTIFICATION_REDIRECT_REQUEST_VIEW:
                    intent = new Intent(getApplicationContext(), HistoryActivity.class);
                    intent.putExtra("isHistory", true);
                    break;
                case APIConstants.PushNotificationStatus.PUSH_NOTIFICATION_REDIRECT_REQUESTS:
                    intent = new Intent(getApplicationContext(), HistoryActivity.class);
                    intent.putExtra("isHistory", true);
                    break;
                default:
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    break;
            }

            Uri uriBeep = Uri.parse("android.resource://com.winrideshareec.driver/" + R.raw.win);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notificaciones Conductor", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.setLightColor(Color.GRAY);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                channel.setSound(uriBeep, audioAttributes);
                channel.enableVibration(true);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }

            }


            if (intent == null)
            {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(uriBeep)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(message.getNotification().getBody())
                    .setAutoCancel(true)
                    .setPriority(1)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setVibrate(VIBRATE_PATTERN)
                    .setContentIntent(pendingIntent);

            if (manager != null) {
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //startActivity(intent);
                builder.setChannelId(NOTIFICATION_CHANNEL_ID);
                manager.notify(0, builder.build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        saveDeviceToken(token);
    }

    private void saveDeviceToken(String token) {
        PrefUtils.getInstance(this).setValue(PrefKeys.FCM_TOKEN, token);
    }
}
