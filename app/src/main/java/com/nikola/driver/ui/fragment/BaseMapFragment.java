package com.nikola.driver.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.nikola.driver.network.APIManager.AsyncTaskCompleteListener;
import com.nikola.driver.ui.activity.MainActivity;

import java.util.List;

/**
 * Created by user on 1/5/2017.
 */

public abstract class BaseMapFragment extends Fragment implements
        View.OnClickListener, AsyncTaskCompleteListener {
    MainActivity activity;
    public static LatLng pic_latlan;
    public static LatLng drop_latlan;
    public static boolean searching =false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();


    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

    }

    @Override
    public void onClick(View v) {


    }

    public abstract void onPermissionsDenied(int requestCode, @NonNull List<String> perms);
}
