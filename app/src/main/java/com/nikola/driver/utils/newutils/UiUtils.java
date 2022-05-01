package com.nikola.driver.utils.newutils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIConstants;

public class UiUtils {

    private static Dialog loadingDialog;

    public static void showShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showLoadingDialog(Context context) {
        loadingDialog = new Dialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.loading_api_lottie);
        ImageView loader = loadingDialog.findViewById(R.id.loader);
        Glide.with(context).load(R.raw.car).into(loader);
        if (!loadingDialog.isShowing())
            loadingDialog.show();
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public static void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    public static String getStaticMapApprox(Context context, LatLng latLng, int width, int height) {
        if (latLng == null) return "";

        return  "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=" + latLng.latitude + "," +latLng.longitude +
                "&zoom=16" +
                "&size=" + width + "x" + height +
                "&maptype=roadmap" +
                "&path=color:red|fillcolor:0x00d2c196|weight:1|" +
                "enc%3Aad_yHofn%60%40JyFh%40sF%60AcFxAmElBqD~BoCnCiBtC_AzCUzCTvC~%40lChB~BnCnBpDxAlE%60AbFf%40rFLxFMxFg%40rFaAbFyAlEoBpD_CnCmChBwC~%40%7BCT%7BCUuC_AoCiB_CoCmBqDyAmEaAcFi%40sFKyF%3F%3F"+
                "&key=" + APIConstants.Constants.GOOGLE_API_KEY;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
