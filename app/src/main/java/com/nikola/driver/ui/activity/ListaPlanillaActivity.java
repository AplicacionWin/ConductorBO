package com.nikola.driver.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

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
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListaPlanillaActivity extends AppCompatActivity {

    @BindView(R.id.webViewListaPlanillaViaje)
    WebView webViewPlanillaViaje;
    PrefUtils prefUtils;
    @BindView(R.id.listaPlanilla_back)
    ImageView back;
    int providerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_planilla);
        ButterKnife.bind(this);
        prefUtils = PrefUtils.getInstance(this);
        providerId = prefUtils.getIntValue(PrefKeys.ID, 0);
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

            String url = APIConstants.URLs.LISTA_PLANILLA_VIAJES+providerId;
            Log.d("url: ", url);
            webViewPlanillaViaje.loadUrl(url);
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