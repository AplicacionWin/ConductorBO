package com.nikola.driver.ui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.nikola.driver.network.APIManager.AsyncTaskCompleteListener;
import com.nikola.driver.ui.activity.LoginActivity;

/**
 * Created by user on 8/29/2016.
 */
public class BaseRegFragment extends Fragment implements
        View.OnClickListener, AsyncTaskCompleteListener {
    LoginActivity activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        activity = (LoginActivity) getActivity();


    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

    }

    @Override
    public void onClick(View v) {


    }
}
