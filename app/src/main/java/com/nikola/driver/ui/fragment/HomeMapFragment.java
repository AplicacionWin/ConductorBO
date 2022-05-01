package com.nikola.driver.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nikola.driver.R;
import com.nikola.driver.network.location.LocationHelper;
import com.nikola.driver.network.model.AdsList;
import com.nikola.driver.network.model.TaxiTypes;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.ui.activity.StatusAvailabilityActivity;
import com.nikola.driver.ui.adapter.AdsAdapter;
import com.nikola.driver.ui.adapter.TaxiAdapter;
import com.nikola.driver.utils.AbstractClient;
import com.nikola.driver.utils.Commonutils;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.ItemClickSupport;
import com.nikola.driver.utils.NotificationSendDriverPosition;
import com.nikola.driver.utils.ParseContent;
import com.nikola.driver.utils.PreferenceHelper;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mahesh on 1/5/2017.
 */

public class HomeMapFragment extends BaseMapFragment implements LocationHelper.OnLocationReceived, GoogleMap.OnCameraMoveListener, OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private static final int DURATION = 2000;
    @BindView(R.id.recycAds)
    RecyclerView recycAds;
    @BindView(R.id.btn_mylocation)
    ImageButton myLoction;
    @BindView(R.id.design_bottom_sheet)
    RelativeLayout designBottomSheet;
    @BindView(R.id.imageViewArrow)
    ImageView imageViewArrow;
    @BindView(R.id.fb_changeStatusDriver)
    FloatingActionButton changeStatusDriver;
    private GoogleMap googleMap;
    private Bundle mBundle;
    private static SupportMapFragment req_home_map;
    private View view;
    private LocationHelper locHelper;
    private Location myLocation;
    private LatLng latlong;
    private ArrayList<TaxiTypes> typesList = new ArrayList<>();
    private TaxiAdapter taxiAdapter;
    private AdsAdapter adsAdapter;
    private List<AdsList> adsLists;
    private String TAG = HomeMapFragment.class.getSimpleName();
    private BottomSheetBehavior behavior;
    private Marker currentMarker;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    Unbinder unbinder;
    private int RC_LOCATION_PERM = 124;
    String statusChangeText ="";
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 2323;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_map_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        req_home_map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.req_home_map);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(activity);
        if (null != req_home_map) {
            req_home_map.getMapAsync(this);
        }
        setUpBottomSheet();
        //setUpAdapter();
        //manageAds();

        return view;
    }

    private void setUpBottomSheet() {
        final View bottomSheet = view.findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                rotateArrow(slideOffset);
            }
        });
        behavior.setHideable(false);
        behavior.setSkipCollapsed(false);
        imageViewArrow.setOnClickListener(v -> {
            if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private void setUpAdapter() {
        recycAds.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycAds.setItemAnimator(new DefaultItemAnimator());
        ItemClickSupport.addTo(recycAds).setOnItemClickListener((recyclerView, position, v) -> {
            try{
                String url = adsLists.get(position).getAdUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }


    /*public void manageAds() {
        Call<String> call = apiInterface.manageAds(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject job1 = new JSONObject(response.body());
                    if (job1.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        JSONArray jsonArray = job1.optJSONArray(APIConstants.Params.DATA);
                        if (null != adsLists) {
                            adsLists.clear();
                        }
                        if (null != jsonArray && jsonArray.length() > 0) {
                            adsLists = new ParseContent(activity).parseAdsList(jsonArray);
                            if (adsLists != null) {
                                adsAdapter = new AdsAdapter(adsLists, activity);
                                recycAds.setAdapter(adsAdapter);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });

    }*/

    private void rotateArrow(float v) {
        imageViewArrow.setRotation(-180 * v);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            MapsInitializer.initialize(activity);
        } catch (Exception e) {
        }
        locHelper = new LocationHelper(activity);
        locHelper.setLocationReceivedLister(this);
    }

    @Override
    public void onLocationReceived(LatLng latlong) {

    }

    @Override
    public void onLocationReceived(Location location) {
        if (location != null) {
            myLocation = location;
            LatLng latLang = new LatLng(location.getLatitude(),
                    location.getLongitude());
            latlong = latLang;
            updateMarkerOnMap(latlong.latitude,latlong.longitude, myLocation.getBearing());
        }
    }

    public void updateMarkerOnMap(double latitude, double longitude, double bearing) {

        float segmentBearing = 0;
        if (currentMarker != null)
            currentMarker.remove();
        //LatLng driverLatLng = new LatLng(latitude, longitude);
        if (null != googleMap) {
            Location driver_location = new Location("Driver Location");
            driver_location.setLatitude(latitude);
            driver_location.setLongitude(longitude);

            segmentBearing = (float) bearing;
//            LatLng segmentMiddlePoint = SphericalUtil.interpolate(oldLatLng, currLatLng, 0.5);

            if (null == latlong) {
                latlong = LocationHelper.getCurrentLocation();
            }
            currentMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latlong)
                    .rotation(segmentBearing)
                    .anchor(0.5f, 0.5f)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_booking_lux_map_topview))
                    .title(activity.getResources().getString(R.string.txt_driver)));

            if (googleMap != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15));
            }

        }
    }
    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {

        if (location != null && googleMap != null) {
            LatLng currentlatLang = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d("Current Location", "onConntected: " + currentlatLang);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatLang, 16));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.currentFragment = Const.HOME_MAP_FRAGMENT;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }
        new Thread() {
            public void run() {

                boolean estado = checkAvailabilityStatus();
                activity.runOnUiThread(() -> {
                    dibujarEstado(estado);

                });
            }
        }.start();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.req_home_map);
        if (f != null) {
            try {
                getFragmentManager().beginTransaction().remove(f).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ViewGroup mContainer = getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();
        googleMap = null;
    }

    @Override
    public void onMapReady(GoogleMap mgoogleMap) {
        googleMap = mgoogleMap;
        if (googleMap != null) {
            Commonutils.progressdialog_hide();
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getPermission();
                return;
            }
            googleMap.setMyLocationEnabled(false);
            googleMap.setOnCameraMoveListener(this);
            googleMap.setOnCameraIdleListener(this);
        }
    }

    private void getPermission() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(activity)
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
                                if (!EasyPermissions.hasPermissions(activity, perms)) {
                                    EasyPermissions.requestPermissions(activity, getString(R.string.mensajePermisos), RC_LOCATION_PERM, perms);
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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(activity)) {
            RequestPermission();
        }
    }
    private void RequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }

    }
    @Override
    public void onCameraMove() {

    }
    @Override
    public void onCameraIdle() {

    }

    @OnClick({R.id.btn_mylocation, R.id.design_bottom_sheet,R.id.fb_changeStatusDriver})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_mylocation:
                if (null != latlong)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));
                break;
            case R.id.design_bottom_sheet:
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;

            case R.id.fb_changeStatusDriver:
                updateAvailabilityStatus(statusChangeText.equalsIgnoreCase("Go Online") ? 1 : 0);

                break;
        }
    }

    private void updateAvailabilityStatus(int status) {
        UiUtils.showLoadingDialog(getActivity());
        Call<String> call = apiInterface.updateAvailability(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                status);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (jsonObject.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        JSONObject data = jsonObject.optJSONObject(APIConstants.Params.DATA);
                        boolean isAvailable = data.optInt(APIConstants.Params.IS_AVAILABLE) == 1;
                        prefUtils.setValue(PrefKeys.AVAILABLE_STATUS, data.optInt(APIConstants.Params.IS_AVAILABLE) == 1);
                        statusChangeText=(isAvailable ? "Go Offline" : "Go Online");
                        if(isAvailable) {
                            Const.SEND_POSITION_DRIVER = true;
                            Objects.requireNonNull(getActivity()).startService(new Intent(getActivity(), NotificationSendDriverPosition.class));
                        }
                        else {
                            Const.SEND_POSITION_DRIVER = false;
                            Objects.requireNonNull(getActivity()).stopService(new Intent(getActivity(), NotificationSendDriverPosition.class));

                        }
                        changeStatusDriver.setImageResource(isAvailable ? R.drawable.online_taxi_icon : R.drawable.off_taxi_icon);
                        UiUtils.showShortToast(getActivity(), jsonObject.optString(APIConstants.Params.MESSAGE));
                    } else {
                        UiUtils.hideLoadingDialog();
                        UiUtils.showShortToast(getActivity(), jsonObject.optString(APIConstants.Params.ERROR));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    private void activateNotificationServiceToSendDriverPositionData() {

    }

    private boolean checkAvailabilityStatus() {
        Boolean isAvailable = false;

        try {
            String input = "id="+ prefUtils.getIntValue(PrefKeys.ID, 0) +
                    "&token="+ prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "");

            String url = APIConstants.URLs.HOST_URL + APIConstants.APIs.PROVIDER_AVAILABILITY_CHECK;

            AbstractClient serv = new AbstractClient();

            String response = serv.web(url, input);
            System.out.println("response register: " + response);
            JSONObject obj = new JSONObject(response);
            Boolean status = obj.getBoolean("success");
            if(status)
            {
                JSONObject data = obj.getJSONObject("data");
                isAvailable = data.getInt("is_available") == 1;

                Log.d("isAvailable ", isAvailable.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAvailable;
    }

    public void dibujarEstado(Boolean estado)
    {
        boolean isAvailable = estado;
        prefUtils.setValue(PrefKeys.AVAILABLE_STATUS, isAvailable);

        if(isAvailable) {
            Const.SEND_POSITION_DRIVER = true;
            Objects.requireNonNull(getActivity()).startService(new Intent(getActivity(), NotificationSendDriverPosition.class));
        }
        else {
            Const.SEND_POSITION_DRIVER = false;
            Objects.requireNonNull(getActivity()).stopService(new Intent(getActivity(), NotificationSendDriverPosition.class));

        }

        statusChangeText=(isAvailable ? "Go Offline" : "Go Online");
        changeStatusDriver.setImageResource(isAvailable ? R.drawable.online_taxi_icon : R.drawable.off_taxi_icon);

    }
}
