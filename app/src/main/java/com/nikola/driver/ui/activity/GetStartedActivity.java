package com.nikola.driver.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.nikola.driver.R;
import com.nikola.driver.ui.adapter.SpinnerAdapter;
import com.nikola.driver.utils.PreferenceHelper;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by user on 1/3/2017.
 */

public class GetStartedActivity extends AppCompatActivity {
    @BindView(R.id.sp_country_reg)
    Spinner spCountryReg;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.welcome_btn)
    CustomRegularTextView welcomeBtn;
    private SpinnerAdapter adapter_language;
    PrefUtils prefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        if (prefUtils.getBoolanValue(PrefKeys.IS_LOGGED_IN, false)) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
            return;
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
       // getPermission();
        setUpLocale();
        //setUpFirebase();
    }
/*
    private void setUpFirebase() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("asher", "getInstanceId failed", task.getException());
                        return;
                    }
                    String token = task.getResult().getToken();
                    int appVersion = getAppVersion(GetStartedActivity.this);
                    Log.d("asher", "Saving regId on app version " + appVersion);
                    Log.d("asher", "RegID " + token);
                    new PreferenceHelper(GetStartedActivity.this).putAppVersion(appVersion);
                    new PreferenceHelper(GetStartedActivity.this).putRegisterationID(token);
                    new PreferenceHelper(GetStartedActivity.this).putDeviceToken(token);

                });
    }*/

    public void setUpLocale() {
        if (!TextUtils.isEmpty(prefUtils.getStringValue(PrefKeys.LANGUAGES, ""))) {
            Locale myLocale = null;
            switch (prefUtils.getStringValue(PrefKeys.LANGUAGES, "")) {
                case "":
                    myLocale = new Locale("es");
                    break;
                case "es":
                    myLocale = new Locale("es");

                    break;
                case "en":
                    myLocale = new Locale("en");
                    break;

            }
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }

        String[] lst_currency = getResources().getStringArray(R.array.language);
        Integer[] currency_imageArray = {null, R.drawable.flag_ecuador, R.drawable.ic_united_states};
        adapter_language = new SpinnerAdapter(GetStartedActivity.this, R.layout.spinner_value_layout, lst_currency, currency_imageArray);
        spCountryReg.setAdapter(adapter_language);
        if (!TextUtils.isEmpty(prefUtils.getStringValue(PrefKeys.LANGUAGES, ""))) {

            switch (prefUtils.getStringValue(PrefKeys.LANGUAGES, "")) {
                case "":
                    spCountryReg.setSelection(0, false);
                    break;
                case "es":
                    spCountryReg.setSelection(1, false);

                    break;
                case "en":
                    spCountryReg.setSelection(2, false);
                    break;

            }

        }
        spCountryReg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        prefUtils.setValue(PrefKeys.LANGUAGES, "es");
                        break;
                    case 1:
                        prefUtils.setValue(PrefKeys.LANGUAGES, "es");
                        setLocale("es");
                        break;
                    case 2:
                        prefUtils.setValue(PrefKeys.LANGUAGES, "en");
                        setLocale("en");
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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



    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, GetStartedActivity.class);
        startActivity(refresh);
        this.overridePendingTransition(0, 0);
    }

    @OnClick(R.id.welcome_btn)
    public void onViewClicked() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }
}
