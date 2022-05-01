package com.nikola.driver.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.nikola.driver.R;
import com.nikola.driver.utils.customText.CustomRegularTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoInternetActivity extends AppCompatActivity {

    @BindView(R.id.close)
    CustomRegularTextView close;
    public static boolean isInternetActivityCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        ButterKnife.bind(this);
        isInternetActivityCalled = true;
        close.setOnClickListener(view -> {
            finish();
            System.exit(0);
        });
    }
}
