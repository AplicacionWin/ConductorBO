package com.nikola.driver.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.model.LatLng;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.nikola.driver.R;
import com.nikola.driver.network.location.LocationHelper;
import com.nikola.driver.network.model.RequestDetails;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIConstants.Constants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIEvent;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.network.newnetwork.ParserUtils;
import com.nikola.driver.ui.fragment.FeedBackFragment;
import com.nikola.driver.ui.fragment.HomeMapFragment;
import com.nikola.driver.ui.fragment.OngoingFragment;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.PreferenceHelper;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefHelper;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nikola.driver.AppController.activityPaused;
import static com.nikola.driver.AppController.isActivityVisible;
import static com.nikola.driver.network.newnetwork.APIConstants.isMediaPlaying;
import static com.nikola.driver.ui.fragment.NavigationDrawableFragment.ivUserIcon;
import static com.nikola.driver.utils.chathead.ChatHeadService.chatheadImg;

public class MainActivity extends AppCompatActivity implements LocationHelper.OnLocationReceived, EasyPermissions.PermissionCallbacks {

    private final long interval = 1 * 1000;
    public String currentFragment = "";
    @BindView(R.id.bnt_menu)
    ImageButton bntMenu;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.content_frame)
    FrameLayout contentFrame;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    static MediaPlayer mediaPlayer;
    private ActionBarDrawerToggle drawerToggle;
    private Handler reqhandler;
    private Handler requestStatusHandler;
    private AlertDialog gpsAlertDialog, internetDialog;
    AlertDialog.Builder gpsBuilder;
    private MyCountDownTimer countDownTimer;
    RequestDetails requestDetails;
    String request_id, trip_duration, trip_distance;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    private int RC_LOCATION_PERM = 124;
    Uri uri;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 2323;
    Runnable runnable = new Runnable() {
        public void run() {
            getIncomingRequestsInProgress();
            reqhandler.postDelayed(this, 5000);
        }
    };

    Runnable requestStatusRunnable = new Runnable() {
        public void run() {
            requestStatusCheck();
            requestStatusHandler.postDelayed(this, 5000);
        }
    };

    private Dialog requestDialog;
    TextView tv_timer;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(Const.PROVIDER_INTENT_MESSAGE);
            try {
                JSONObject messageObj = new JSONObject(message);
                JSONObject jsonObject = messageObj.optJSONObject(Params.DATA);

                if (Integer.parseInt(jsonObject.getString("status")) == 6) {
                    if (requestDialog != null && requestDialog.isShowing()) {
                        requestDialog.cancel();
                        mediaPlayer.stop();
                        startCheckingUpcomingRequests();
                        countDownTimer.cancel();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setUpLocale();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(this);
        getPermission();
        initDrawer();
        reqhandler = new Handler();
        mediaPlayer = new MediaPlayer();
        uri = Uri.parse("android.resource://com.urPackageName/" + R.raw.beep);
        requestStatusHandler = new Handler();
    }

    private void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.txt_allow_permission))
                    .setMessage(getString(R.string.mensajePermisos))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.permitir), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
                                String[] perms = {
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                };
                                if (!EasyPermissions.hasPermissions(MainActivity.this, perms)) {
                                    EasyPermissions.requestPermissions(MainActivity.this, getString(R.string.mensajePermisos), RC_LOCATION_PERM, perms);
                                }
                            }
                        }
                    })
                    .show();
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(getApplicationContext())) {
            RequestPermission();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerEventBus();
    }

    private void setUpLocale() {
        if (!TextUtils.isEmpty(new PreferenceHelper(this).getLanguage())) {
            Locale myLocale = null;
            switch (new PreferenceHelper(this).getLanguage()) {
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
            Locale.setDefault(myLocale);
            Configuration config = new Configuration();
            config.locale = myLocale;
            this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenExpiry(APIEvent event) {
        unregisterEventBus();
        logOutUserFromDevice();
    }

    private void logOutUserFromDevice() {
        PrefHelper.setUserLoggedOut(MainActivity.this);
        prefUtils.setValue(PrefKeys.IS_LOGGED_IN, false);
        Const.SEND_POSITION_DRIVER = false;
        Intent restartActivity = new Intent(MainActivity.this, SplashActivity.class);
        restartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(restartActivity);
        MainActivity.this.finish();
    }

    private void initDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Glide.with(getApplicationContext())
                        .load(prefUtils.getStringValue(PrefKeys.PICTURE, ""))
                        .thumbnail(.4f)
                        .placeholder(R.drawable.account)
                        .into(ivUserIcon);
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.post(() -> drawerToggle.syncState());
    }

    public void startCheckRegTimer() {
        reqhandler.postDelayed(runnable, 5000);
    }

    public void startCheckingRequest() {
        requestStatusHandler.postDelayed(requestStatusRunnable, 5000);
    }

    public void getIncomingRequestsInProgress() {
        Call<String> call = apiInterface.incomingRequest(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String number_hours = "";
                JSONObject incomingResponse;
                try {
                    incomingResponse = new JSONObject(response.body());
                    if (incomingResponse != null) {
                        if (incomingResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                            JSONArray incomingReqArray = incomingResponse.getJSONArray(Params.DATA);
                            if (incomingReqArray.length() != 0) {
                                Log.e("Incoming request", String.valueOf(incomingReqArray));
                                JSONObject requestObject = incomingReqArray.getJSONObject(0);
                                String picture = requestObject.optString(Params.USER_PICTURE);
                                String name = requestObject.optString(Params.USER_NAME);
                                request_id = requestObject.optString(Params.REQUEST_ID);
                                String address = requestObject.optString(Params.S_ADDRESS);
                                String d_address = requestObject.optString(Params.D_ADDRESS);
                                Double s_lat = Double.parseDouble(requestObject.optString(Params.S_LATITUDE));
                                Double s_lan = Double.parseDouble(requestObject.optString(Params.S_LONGITUDE));

                                Double d_lat = Double.parseDouble(requestObject.optString(Params.D_LATITUDE));
                                Double d_lan = Double.parseDouble(requestObject.optString(Params.D_LONGITUDE));
                                boolean recarga = requestObject.optBoolean(Params.RECARGA);

                                String request_status_type = requestObject.optString(Params.REQUEST_STATUS_TYPE);
                                if (request_status_type.equals("2")) {
                                    JSONObject hourObj = incomingResponse.getJSONObject(Params.HOURLY_PACKAGE_DEATILS);
                                    number_hours = hourObj.getString(Params.NUMBER_HOURS);
                                }
                                String staticMapUrl = "http://maps.google.com/maps/api/staticmap?center=" + s_lat + "," + s_lan + "&markers=" + s_lat + "," + s_lan + "&zoom=14&size=270x270&sensor=false&key=" + Const.GOOGLE_API_KEY;
                                long countDown = Long.parseLong(requestObject.optString("time_left_to_respond"));
                                String request_type = requestObject.getString(Params.REQUEST_TYPE);
                                getDistanceAndDuration(new LatLng(s_lat,s_lan),new LatLng(d_lat,d_lan),false);
                                Log.e("RequestType ", request_type);
                                if (countDown > 0) {
                                    showRequestDialog(picture, name, request_id, address, countDown, staticMapUrl, request_status_type, number_hours, trip_distance, trip_duration, d_address, recarga);
                                    stopCheckingUpcomingRequests();
                                }
                            } else {
                                if (requestDialog != null && requestDialog.isShowing())
                                    requestDialog.cancel();

                                if (mediaPlayer != null && mediaPlayer.isPlaying())
                                    mediaPlayer.stop();
                                isMediaPlaying = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private void startCheckingUpcomingRequests() {
        startCheckRegTimer();
    }

    private void startCheckingRequestStatus() {
        startCheckRegTimer();
    }

    private void stopCheckingUpcomingRequests() {
        if (reqhandler != null) {
            reqhandler.removeCallbacks(runnable);
        }
    }

    private void stopCheckingRequest() {
        if (requestStatusHandler != null) {
            requestStatusHandler.removeCallbacks(requestStatusRunnable);
        }
    }

    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        openExitDialog();
    }

    public void addFragment(Fragment fragment, boolean addToBackStack,
                            String tag, boolean isAnimate) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (isAnimate) {
            ft.setCustomAnimations(R.anim.slide_in_right,
                    R.anim.slide_out_left, R.anim.slide_in_left,
                    R.anim.slide_out_right);
        }
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.replace(R.id.content_frame, fragment, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            if (!currentFragment.equals(Const.HOME_MAP_FRAGMENT) && !currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)
                    && !currentFragment.equals(Const.FEEDBACK_FRAGMENT)) {
                requestStatusCheck();
            }
        }
        getIncomingRequestsInProgress();
        isActivityVisible();
        if (chatheadImg != null)
            chatheadImg.setVisibility(View.INVISIBLE);
    }

    private void requestStatusCheck() {
        try {
            Call<String> call = apiInterface.checkRequestStatus(prefUtils.getIntValue(PrefKeys.ID, 0)
                    , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    JSONObject statusCheckResponse = null;
                    try {
                        statusCheckResponse = new JSONObject(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (statusCheckResponse != null) {
                        if (!statusCheckResponse.optString(Params.SUCCESS).equalsIgnoreCase(Constants.TRUE)) {
                            if (statusCheckResponse.optInt(Params.ERROR_CODE) == Constants.INVALID_TOKEN) {
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(i);
                            } else if (statusCheckResponse.optInt(Params.ERROR_CODE) == Constants.REQUEST_ID_NOT_FOUND) {
                                UiUtils.showShortToast(MainActivity.this, "Request is not found");
                            } else if (statusCheckResponse.optInt(Params.ERROR_CODE) == Constants.INVALID_REQUEST_ID) {
                                startCheckingUpcomingRequests();
                            }
                            return;
                        } else {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.body());
                                if (jsonObject.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                                    String dataObject = String.valueOf(jsonObject.optInt(Params.REQUEST_CANCELLED));
                                    requestDetails = ParserUtils.parseRequestStatus(response.body());
                                    if (requestDetails != null) {
                                        Bundle bundle = new Bundle();
                                        if (mediaPlayer != null && mediaPlayer.isPlaying())
                                            mediaPlayer.stop();
                                        isMediaPlaying = false;
                                        OngoingFragment travelMapFragment = new OngoingFragment();
                                        switch (requestDetails.getProviderStatus()) {
                                            case APIConstants.NO_REQUEST:
                                                addFragment(new HomeMapFragment(), false, APIConstants.HOME_FRAGMENT, true);
                                                startCheckRegTimer();
                                                break;
                                            case Constants.IS_PROVIDER_ACCEPTED:
                                                bundle.putSerializable(Constants.REQUEST_DETAIL, requestDetails);
                                                bundle.putInt(Constants.PROVIDER_STATUS, Constants.IS_PROVIDER_ACCEPTED);
                                                travelMapFragment.setArguments(bundle);
                                                addFragment(travelMapFragment, false, Constants.TRAVEL_MAP_FRAGMENT, true);
                                                stopCheckingUpcomingRequests();
                                                break;
                                            case Constants.IS_PROVIDER_STARTED:
                                                bundle.putSerializable(Constants.REQUEST_DETAIL, requestDetails);
                                                bundle.putInt(Constants.PROVIDER_STATUS, Constants.IS_PROVIDER_STARTED);
                                                travelMapFragment.setArguments(bundle);
                                                addFragment(travelMapFragment, false, Constants.TRAVEL_MAP_FRAGMENT, true);
                                                stopCheckingUpcomingRequests();
                                                break;
                                            case Constants.IS_PROVIDER_ARRIVED:
                                                bundle.putSerializable(Constants.REQUEST_DETAIL, requestDetails);
                                                bundle.putInt(Constants.PROVIDER_STATUS, Constants.IS_PROVIDER_ARRIVED);
                                                travelMapFragment.setArguments(bundle);
                                                addFragment(travelMapFragment, false, Constants.TRAVEL_MAP_FRAGMENT, true);
                                                stopCheckingUpcomingRequests();
                                                break;
                                            case Constants.IS_PROVIDER_SERVICE_STARTED:
                                                bundle.putSerializable(Constants.REQUEST_DETAIL, requestDetails);
                                                bundle.putInt(Constants.PROVIDER_STATUS, Constants.IS_PROVIDER_SERVICE_STARTED);
                                                travelMapFragment.setArguments(bundle);
                                                addFragment(travelMapFragment, false, Constants.TRAVEL_MAP_FRAGMENT, true);
                                                stopCheckingUpcomingRequests();
                                                break;
                                            case Constants.IS_PROVIDER_SERVICE_COMPLETED:
                                                bundle.putSerializable(Constants.REQUEST_DETAIL, requestDetails);
                                                bundle.putInt(Constants.PROVIDER_STATUS, Constants.IS_PROVIDER_SERVICE_COMPLETED);
                                                bundle.putString("SCHEDULE", "0");
                                                FeedBackFragment feedbackFragment = new FeedBackFragment();
                                                feedbackFragment.setArguments(bundle);
                                                feedbackFragment.setRequestId(requestDetails.getRequestId());
                                                addFragment(feedbackFragment, false, Constants.FEEDBACK_FRAGMENT, true);
                                                stopCheckingUpcomingRequests();
                                                break;

                                        }
                                    }
                                } else {

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        activityPaused();
    }

    public void requestAccepted(boolean recarga) {
        if(recarga)
        {
            UiUtils.showLoadingDialog(MainActivity.this);
            Call<String> call = apiInterface.providerAccepted(prefUtils.getIntValue(PrefKeys.ID, 0),
                    prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                    request_id);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    JSONObject acceptResponse;
                    try {
                        UiUtils.hideLoadingDialog();
                        acceptResponse = new JSONObject(response.body());
                        if (acceptResponse != null) {
                            if (acceptResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                                JSONObject data = acceptResponse.optJSONObject(Params.DATA);
                                requestDetails = ParserUtils.parseAcceptedRequest(response.body());
                                stopCheckingUpcomingRequests();
                                UiUtils.hideLoadingDialog();
                                Bundle bundle2 = new Bundle();
                                OngoingFragment tripFragment = new OngoingFragment();
                                bundle2.putInt(Constants.PROVIDER_STATUS, Constants.IS_PROVIDER_ACCEPTED);
                                bundle2.putSerializable(Const.REQUEST_DETAIL, requestDetails);
                                tripFragment.setArguments(bundle2);
                                addFragment(tripFragment, false, Constants.TRAVEL_MAP_FRAGMENT, true);
                                if (countDownTimer != null) {
                                    countDownTimer.cancel();
                                }
                            }
                        } else {
                            UiUtils.hideLoadingDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    try {
                        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                            UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else
        {
            AlertDialog.Builder noSaldo = new AlertDialog.Builder(this);
            noSaldo.setTitle(getString(R.string.noSaldoTitulo));
            noSaldo.setMessage(getString(R.string.noSaldoMensaje));
            noSaldo.setCancelable(true);

            noSaldo.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = noSaldo.create();
            alert11.show();
        }
    }

    public void driverRejected() {
        UiUtils.showLoadingDialog(MainActivity.this);
        Call<String> call = apiInterface.providerRejected(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                request_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    UiUtils.hideLoadingDialog();
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    //JSONObject job1 = new JSONObject(response.toString());
                    UiUtils.hideLoadingDialog();
                    startCheckingUpcomingRequests();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(MainActivity.this)) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });

    }


    private void showRequestDialog(String picture, String name, final String request_id, String address, long countDown, String map_img, String req_type, String number_hours, String distance, String duration, String destination, boolean recarga) {
        if (requestDialog == null || !requestDialog.isShowing()) {
            requestDialog = new Dialog(this, R.style.DialogSlideAnim_leftright_Fullscreen);
            requestDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            requestDialog.setCancelable(false);
            requestDialog.setContentView(R.layout.dialog_request_layout);
            try {
                if (!isMediaPlaying) {
                    mediaPlayer.release();
                    mediaPlayer = MediaPlayer.create(this, R.raw.beep);
                    isMediaPlaying = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            CircleImageView iv_location = requestDialog.findViewById(R.id.iv_location);
            if (map_img != null) {
                Log.e("asher", "map url static " + map_img);
                Glide.with(this).load(map_img).into(iv_location);
            }
            TextView tv_name = requestDialog.findViewById(R.id.tv_name);
            TextView tv_hours = requestDialog.findViewById(R.id.tv_hours);

            TextView tv_d_address = requestDialog.findViewById(R.id.tv_d_address);
            CircularProgressBar circularProgressBar = requestDialog.findViewById(R.id.req_progress_bar);
            tv_timer = requestDialog.findViewById(R.id.tv_timer);
            tv_name.setText(getResources().getString(R.string.txt_name) + " " + name);


            TextView tv_address = requestDialog.findViewById(R.id.tv_address);
            ImageButton btn_accept = requestDialog.findViewById(R.id.btn_accept);
            ImageButton btn_reject = requestDialog.findViewById(R.id.btn_reject);
            tv_address.setText(getResources().getString(R.string.txt_pic_address) + " " + address);
            tv_d_address.setText(getResources().getString(R.string.txt_des_address).concat(" ").concat(destination));
            if (req_type.equals("2")) {
                tv_hours.setVisibility(View.VISIBLE);
                tv_hours.setText(getResources().getString(R.string.txt_no_hours) + " " + number_hours);
            } else {
                tv_hours.setVisibility(View.GONE);
            }
            int animationDuration = (int) (countDown * 1000); // 2500ms = 2,5s
            circularProgressBar.setProgressWithAnimation(countDown * 2, animationDuration);
            countDownTimer = new MyCountDownTimer(countDown * 1000, interval);
            countDownTimer.start();
            btn_reject.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer= null;

                }

                isMediaPlaying = false;
                if (requestDialog.isShowing())
                    requestDialog.dismiss();
                driverRejected();
                countDownTimer.cancel();
                startCheckingUpcomingRequests();
            });
            btn_accept.setOnClickListener(v -> {

                if(mediaPlayer!=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer= null;
                }


                isMediaPlaying = false;
                if (requestDialog.isShowing())
                    requestDialog.dismiss();
                requestAccepted(recarga);
                countDownTimer.cancel();
            });
            requestDialog.show();
        }
    }

    private void RequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getApplicationContext())) {
                    //Permisos otorgados

                    if(isMiUi())
                    {
                        Toast.makeText(this,getText(R.string.otorgarPermisos),Toast.LENGTH_LONG).show();
                        showPermissionsDialog();
                    }
                }
            }
        }
    }

    public static boolean isMiUi() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            java.lang.Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    private void showPermissionsDialog() {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", getPackageName());
        startActivity(intent);
    }

    @Override
    public void onLocationReceived(LatLng latlong) {

    }

    @Override
    public void onLocationReceived(Location location) {

    }

    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {
        location = location;
    }

    @Override
    protected void onDestroy() {
        try {
            stopCheckingUpcomingRequests();
            if (mediaPlayer != null && mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer= null;

            }
            isMediaPlaying = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @OnClick(R.id.bnt_menu)
    public void onViewClicked() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            try {
                tv_timer.setText(getString(R.string.space) + (l / 1000));
                if(null != mediaPlayer)
                {
                    mediaPlayer.start();
                }
                if(null!=requestDialog && requestDialog.isShowing())
                {
                    TextView tv_duration = requestDialog.findViewById(R.id.tv_duration);
                    TextView tv_distance = requestDialog.findViewById(R.id.tv_distance);
                    tv_duration.setText("Dur: ".concat(null!=trip_duration?trip_duration:""));
                    tv_distance.setText("Dis: ".concat(null!=trip_distance?trip_distance:""));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            if (requestDialog != null && requestDialog.isShowing()) {
                try {
                    requestDialog.cancel();
                    if (mediaPlayer != null)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer= null;

                    }
                    startCheckingUpcomingRequests();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void openExitDialog() {
        final Dialog exit_dialog = new Dialog(this);
        exit_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exit_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exit_dialog.setCancelable(true);
        exit_dialog.setContentView(R.layout.exit_layout);
        TextView tvExitOk = exit_dialog.findViewById(R.id.tvExitOk);
        TextView tvExitCancel = exit_dialog.findViewById(R.id.tvExitCancel);
        tvExitOk.setOnClickListener(view -> {
            exit_dialog.dismiss();
            finishAffinity();
        });
        tvExitCancel.setOnClickListener(view -> exit_dialog.dismiss());
        exit_dialog.show();
    }


    private void unregisterEventBus() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    private void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        requestStatusCheck();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    protected void getDistanceAndDuration(LatLng picLatLng, LatLng dropLatLng, boolean isFareCalculation) {
        String url = APIConstants.URLs.DISTANCE_LOCATION_API + picLatLng.latitude + "," + picLatLng.longitude + "&destinations=" + dropLatLng.latitude + "," + dropLatLng.longitude +
                "&mode=drving&language=en-EN&key=" + APIConstants.Constants.GOOGLE_API_KEY + "&sensor=false";
        Call<String> call = apiInterface.getLocationBasedResponse(url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject fareResponse = null;
                try {
                    fareResponse = new JSONObject(response.body());
                    if (fareResponse != null) {
                        fareResponse.optString(APIConstants.Constants.STATUS).equals(APIConstants.Constants.OK);
                        JSONArray sourceArray = fareResponse.optJSONArray("origin_addresses");
                        String sourceObject = (String) sourceArray.opt(0);
                        JSONArray destinationArray = fareResponse.optJSONArray("destination_addresses");
                        String destinationObject = (String) destinationArray.opt(0);
                        JSONArray jsonArray = fareResponse.optJSONArray("rows");
                        JSONObject elementsObject = jsonArray.optJSONObject(0);
                        JSONArray elementsArray = elementsObject.optJSONArray("elements");
                        JSONObject distanceObject = elementsArray.optJSONObject(0);
                        JSONObject dObject = distanceObject.optJSONObject("distance");
                        String distance = dObject.optString("text");
                        JSONObject durationObject = distanceObject.optJSONObject("duration");
                        String duration = durationObject.optString("text");
                        String dis = dObject.optString("value");
                        int dur = durationObject.optInt("value");
                        double trip_dis = Integer.valueOf(dis) * 0.001;
                        trip_distance = distance;
                        trip_duration = duration;
                        /*if (isFareCalculation) {
                            getFareCalculation(trip_dis, String.valueOf(dur), taxi_type);
                            if (tv_total_dis != null)
                                tv_total_dis.setText(distance);
                        } else
                            getServiceTypes((float) trip_dis, dur);*/
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //UiUtils.showLongToast(this, getString(R.string.cannotScheduleRide));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

}
