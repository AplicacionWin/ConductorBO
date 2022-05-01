package com.nikola.driver.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlanillaViajeActivity extends AppCompatActivity {

    @BindView(R.id.webViewPlanillaViaje)
    WebView webViewPlanillaViaje;

    @BindView(R.id.planilla_back)
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planilla_viaje);
        ButterKnife.bind(this);
        try {

        webViewPlanillaViaje.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

        webViewPlanillaViaje.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });

           webViewPlanillaViaje.loadUrl(APIConstants.URLs.PLANILLA_VIAJES);
       }
       catch (Exception e)
       {
           e.printStackTrace();
           Toast.makeText(this, getText(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
       }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



}