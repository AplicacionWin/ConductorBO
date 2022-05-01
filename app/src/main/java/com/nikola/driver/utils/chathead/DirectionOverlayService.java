package com.nikola.driver.utils.chathead;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.danialgoodwin.globaloverlay.GlobalOverlay;
import com.nikola.driver.R;
import com.nikola.driver.ui.activity.MainActivity;

public class DirectionOverlayService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private GlobalOverlay mGlobalOverlay;
    public static ImageView imageView;
    int LAYOUT_FLAG;
    WindowManager windowManager;;


    @Override
    public void onCreate() {
        super.onCreate();
        mGlobalOverlay = new GlobalOverlay(this);
//        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
//        }
//
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                LAYOUT_FLAG,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                PixelFormat.TRANSLUCENT);
//        params.gravity = Gravity.TOP | Gravity.LEFT;
//        params.x = 0;
//        params.y = 100;
//        windowManager.addView(imageView, params);

        mGlobalOverlay.addOverlayView(imageView, v -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }
}
