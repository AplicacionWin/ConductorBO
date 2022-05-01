package com.nikola.driver.network.newnetwork;

import android.content.Context;
import android.net.ConnectivityManager;

import com.nikola.driver.R;
import com.nikola.driver.utils.newutils.UiUtils;

public class NetworkUtils {

    public static void onApiError(Context context) {
        if (context == null) return;
        UiUtils.hideLoadingDialog();
        UiUtils.showShortToast(context, context.getString(R.string.something_went_wrong));
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = null;
        boolean isnetWork = false;
        try {
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            isnetWork = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isnetWork;
    }
}
