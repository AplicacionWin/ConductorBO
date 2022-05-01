package com.nikola.driver.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.nikola.driver.R;
import com.nikola.driver.fcm.FCMIntentService;
import com.nikola.driver.utils.splash.SplashAnimationHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh on 7/19/2017.
 */

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.tv_app_name)
    ImageView tvAppName;
    @BindView(R.id.splashAnimationLayout)
    RelativeLayout splashAnimationLayout;
    private SplashAnimationHelper.SplashRouteAnimation splashRouteAnimation;
    int versionCode;
    int mDriver_Version_Code;
    AppUpdateManager appUpdateManager;
    InstallStateUpdatedListener listener;
    private static final int RC_APP_UPDATE = 100;
    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;
    final long[] VIBRATE_PATTERN    = { 100, 200, 300, 400, 500, 400, 300, 200, 100 };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CreateFCMDefaultChannel();

        animateToHomeScreen();
        startProgressAnimation();
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, GetStartedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }, 1000);
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        setContentView(R.layout.layout_animation);
        ButterKnife.bind(this);
        checkForAnAppUpdate();
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.i("kalin ","Key: " + key + " Value: " + value);
            }
        }
        try {
            startService(new Intent(this, FCMIntentService.class));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void CreateFCMDefaultChannel()
    {
        Uri uriBeep = Uri.parse("android.resource://com.winridesharec.driver/" + R.raw.win);
        String channelId = getApplicationContext().getString(R.string.default_notification_channel_id);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Notificaciones Conductor", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GRAY);
            channel.setVibrationPattern(VIBRATE_PATTERN);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(uriBeep, audioAttributes);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public void checkForAnAppUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            }
        });
    }
    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, FLEXIBLE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.splashAnimationLayout),
                        getString(R.string.updateAvailable),
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.restartCaps), view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    private void startProgressAnimation() {
        this.splashRouteAnimation = new SplashAnimationHelper().createSplashAnimation(this);
        //this.splashRouteAnimation.startAnimation(this.splashAnimationLayout);
    }

    private void animateToHomeScreen() {
        AnimatorSet localAnimatorSet1 = new AnimatorSet();
        AnimatorSet localAnimatorSet2 = new AnimatorSet();
        AnimatorSet localAnimatorSet3 = new AnimatorSet();
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        localArrayList1.add(ObjectAnimator.ofFloat(this.splashAnimationLayout, "alpha", new float[]{1.0F, 0.0F}));
        localAnimatorSet2.setDuration(1000);
        localAnimatorSet2.playTogether(localArrayList1);
        localAnimatorSet3.playSequentially(localArrayList2);
        localAnimatorSet3.setDuration(500L);
        localAnimatorSet3.setStartDelay(50L);
        localAnimatorSet1.playSequentially(new Animator[]{localAnimatorSet2, localAnimatorSet3});
        localAnimatorSet1.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator paramAnonymousAnimator) {
            }

            public void onAnimationEnd(Animator paramAnonymousAnimator) {
                if (SplashActivity.this.splashRouteAnimation != null) {

                }
            }

            public void onAnimationRepeat(Animator paramAnonymousAnimator) {
            }

            public void onAnimationStart(Animator paramAnonymousAnimator) {
            }
        });
        localAnimatorSet1.start();
    }
}
