package com.nikola.driver.ui.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Layout;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.bumptech.glide.Glide;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.nikola.driver.R;
import com.nikola.driver.network.location.LocationHelper;
import com.nikola.driver.network.model.CancelReason;
import com.nikola.driver.network.model.LocationUpdate;
import com.nikola.driver.network.model.RequestDetails;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIConstants.Constants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.network.newnetwork.ParserUtils;
import com.nikola.driver.ui.activity.ChatActivity;
import com.nikola.driver.ui.activity.MainActivity;
import com.nikola.driver.ui.adapter.CancelReasonAdapter;
import com.nikola.driver.utils.CarAnimation.AnimateMarker;
import com.nikola.driver.utils.Commonutils;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.RecyclerLongPressClickListener;
import com.nikola.driver.utils.chathead.ChatHeadService;
import com.nikola.driver.utils.chathead.Utils;
import com.nikola.driver.utils.customText.CustomBoldRegularTextView;
import com.nikola.driver.utils.customText.CustomLightTextView;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by user on 1/12/2017.
 */

public class OngoingFragment extends Fragment implements LocationHelper.OnLocationReceived, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {

    @BindView(R.id.travel_map_lay)
    RelativeLayout travelMapLay;
    @BindView(R.id.address_title)
    CustomRegularTextView addressTitle;
    @BindView(R.id.tv_current_location)
    CustomRegularTextView tvCurrentLocation;
    @BindView(R.id.tv_trip_id)
    CustomRegularTextView tvTripId;
    @BindView(R.id.sosCall)
    FloatingActionButton sosCallButton;
    @BindView(R.id.stopLabel)
    CustomBoldRegularTextView stopLabel;
    @BindView(R.id.stopAddress)
    CustomRegularTextView stopAddress;
    @BindView(R.id.stopLay)
    RelativeLayout stopLay;
    @BindView(R.id.layout_address)
    LinearLayout layoutAddress;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.iv_user)
    CircleImageView ivUser;
    @BindView(R.id.tv_userName)
    CustomBoldRegularTextView tvUserName;
    @BindView(R.id.tv_origenDur)
    CustomRegularTextView tv_origenDur;

    @BindView(R.id.tv_origenDist)
    CustomRegularTextView tv_origenDist;

    @BindView(R.id.origenDataLayout)
    LinearLayout origenDataLayout;

    //@BindView(R.id.simple_rating_bar)
    //SimpleRatingBar simpleRatingBar;

    @BindView(R.id.tv_userMobileNumber)
    CustomRegularTextView tvUserMobileNumber;
    @BindView(R.id.ll_user_details)
    LinearLayout llUserDetails;
    @BindView(R.id.driver_contact)
    LinearLayout driverContact;
    @BindView(R.id.btn_direction)
    LinearLayout btnDirection;
    @BindView(R.id.cancel_trip)
    LinearLayout cancelTrip;
    @BindView(R.id.tv_trip_status)
    CustomLightTextView tvTripStatus;
    @BindView(R.id.layout_driverdetails)
    RelativeLayout layoutDriverdetails;
    private GoogleMap googleMap;
    private Bundle mBundle;
    private SupportMapFragment driver_travel_map;
    private View view;
    private LocationHelper locHelper;
    private Location myLocation;
    private MainActivity activity;
    private int jobStatus = 0;
    private Bundle requestBundle;
    private RequestDetails requestDetails;
    private LatLng srcLang;
    private LatLng desLang, stop_latlng;
    private Marker pickup_marker, drop_marker, currentMarker, stopMarker;
    private Location lastLocation, currentLocation;
    private float bearing = 0.0f;
    private String mobileNo = "";
    private boolean isMarkerRotating = false;
    private Handler reqHandler;
    private boolean isShown = false, isShownStop = false, isShownDest = false;
    private ArrayList<LatLng> driverlatlan;
    int mIndexCurrentPoint = 0;
    private List<LatLng> mPathPolygonPoints;
    Bitmap mMarkerIcon;
    private LatLng delayLatlan;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    long starttime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedtime = 0L;
    int t = 1;
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    int hours = 0;
    int count = 0;
    Handler timerHandler;
    private String duration = "";
    private int trip_duration = 0;
    Polyline poly_line, polylineProviderAccepted, polylineProviderStarted, polylineProviderArrived, polylineProviderServiceStarted;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    private boolean changed = false;
    int notifiedDest = 0, notifiedStop = 0;
    Unbinder unbinder;

    APIInterface apiInterface;
    PrefUtils prefutils;
    private Socket socket;
    private ArrayList<CancelReason> cancelReasonLst;
    boolean markerAnimated = false;
    LatLng currLatLng;
    LocationManager locationManager;
    Location location;
    LatLng oldLatLng = new LatLng(0, 0);
    LatLng newLatLng = new LatLng(0, 0);
    static Response<String> responsePath;

    Runnable runnable = new Runnable() {
        public void run() {
            if (requestDetails != null) {
                newRequestStatusCheck();
                if (currLatLng != null)
                    attemptToSendLocation(currLatLng.latitude, currLatLng.longitude);
                reqHandler.postDelayed(this, 3000);
                tvTripStatus.setEnabled(true);
            }
        }
    };

    public void onCurrentLocationObtained(LatLng latLng) {
        if (latLng != null) {
            LatLng myLatLng = new LatLng(latLng.latitude, latLng.longitude);
            currLatLng = new LatLng(latLng.latitude, latLng.longitude);
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(() -> {
                try {
                    JSONObject object = new JSONObject();
                    object.put(Params.COMMONID, MessageFormat.format("user_id_{0}_provider_id_{1}_request_id_{2}",
                            requestDetails.getClientId(), prefutils.getIntValue(PrefKeys.ID, 0), String.valueOf(requestDetails.getRequestId())));
                    object.put(Params.MYID, prefutils.getIntValue(PrefKeys.ID, 0));
                    socket.emit(Params.SENDER_UPDATE, object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = args -> getActivity().runOnUiThread(() -> {

    });

    private Emitter.Listener onConnectError = args -> {

    };

    private Emitter.Listener onNewMessage = args -> getActivity().runOnUiThread(() -> {
        JSONObject locationObject = (JSONObject) args[0];
        LocationUpdate userLocation = new LocationUpdate();
        userLocation.setLatitude(locationObject.optDouble(Params.LATITUDE));
        userLocation.setLongitude(locationObject.optDouble(Params.LONGITUDE));
        userLocation.setBearing(locationObject.optDouble(Params.BEARING));
        updateMarkerOnMap(userLocation.getLatitude(), userLocation.getLongitude(), userLocation.getBearing());
    });


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        activity = (MainActivity) getActivity();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefutils = PrefUtils.getInstance(getContext());
        timerHandler = new Handler();
        driverlatlan = new ArrayList<>();
        mPathPolygonPoints = new ArrayList<>();
        cancelReasonLst = new ArrayList<>();
        reqHandler = new Handler();
        requestBundle = getArguments();

        if (requestBundle != null) {
            jobStatus = requestBundle.getInt(Constants.PROVIDER_STATUS, Constants.IS_PROVIDER_ACCEPTED);
            requestDetails = (RequestDetails) requestBundle.getSerializable(Constants.REQUEST_DETAIL);
            mobileNo = requestDetails.getClientPhoneNumber();
        }
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.travel_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        tvTripStatus.setEnabled(false);
        tvCurrentLocation.setSelected(true);

        initSocket();
        disableStatusBtn();
        clickListerners(savedInstanceState);
        driver_travel_map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.driver_travel_map);
        if (null != driver_travel_map) {
            driver_travel_map.getMapAsync(this);
        }

        if (requestDetails.getIsAdStop() != null && Integer.valueOf(requestDetails.getIsAdStop()) == 1 && jobStatus == Const.IS_PROVIDER_SERVICE_STARTED) {
            if (stopLay.getVisibility() == View.GONE) {
                stopLay.setVisibility(View.VISIBLE);
                stopAddress.setText(requestDetails.getAdStopAddress());
            }
        }

        cancelTrip.setOnClickListener(view -> {
            final iOSDialog cancelDialog = new iOSDialog(getActivity());
            cancelDialog.setTitle(getResources().getString(R.string.txt_cancel_ride));
            cancelDialog.setSubtitle(getResources().getString(R.string.cancel_txt));
            cancelDialog.setNegativeLabel(getResources().getString(R.string.txt_no));
            cancelDialog.setPositiveLabel(getResources().getString(R.string.txt_yes));
            cancelDialog.setBoldPositiveLabel(false);
            cancelDialog.setNegativeListener(view1 -> cancelDialog.dismiss());
            cancelDialog.setPositiveListener(view12 -> {
                //Get cancel reasons before cancelling trip
                geCancelReason();
                //cancelRide();
                cancelDialog.dismiss();
            });
            cancelDialog.show();
        });

        driverContact.setOnClickListener(view -> {
            final iOSDialog ContactDialog = new iOSDialog(activity);
            ContactDialog.setTitle(getResources().getString(R.string.txt_contact_user));
            ContactDialog.setSubtitle(mobileNo);
            ContactDialog.setNegativeLabel(getResources().getString(R.string.txt_call));
            ContactDialog.setPositiveLabel(getResources().getString(R.string.txt_msg));
            ContactDialog.setBoldPositiveLabel(false);
            ContactDialog.setNegativeListener(view13 -> {
                if (!mobileNo.equals("")) {
                    int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{Manifest.permission.CALL_PHONE}, 123);
                    } else {
                        call();
                    }
                }
                ContactDialog.dismiss();
            });
            ContactDialog.setPositiveListener(view14 -> {
                if (requestDetails != null) {
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    startActivity(i);

                }
                ContactDialog.dismiss();
            });
            ContactDialog.show();
        });

        return view;
    }

    private void clickListerners(Bundle savedInstanceState) {

        sosCallButton.setOnClickListener(view -> UiUtils.showShortToast(activity, getString(R.string.panicClic)));
        sosCallButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sendPanicMessage();
                return true;
            }
        });
    }

    protected void sendPanicMessage() {
        UiUtils.showLoadingDialog(activity);
        String currentAddress = "";
        if (currLatLng != null) {
            currentAddress = "en ".concat(getAddressFromLatLng(currLatLng.latitude, currLatLng.longitude));
        } else {
            currentAddress = "en camino al destino";
        }
        Call<String> call = apiInterface.sendPanicMessage(prefutils.getIntValue(PrefKeys.ID, 0)
                , prefutils.getIntValue(PrefKeys.REQUEST_ID, 0)
                , currentAddress
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject sendPanicMessageResponse = null;
                try {
                    sendPanicMessageResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (sendPanicMessageResponse != null) {
                    if (sendPanicMessageResponse.optString(Const.Params.SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        UiUtils.showShortToast(activity, sendPanicMessageResponse.optString(Params.MESSAGE));
                    } else {
                        UiUtils.showShortToast(activity, sendPanicMessageResponse.optString(Params.ERROR));
                    }
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

    private void initSocket() {
        try {
            socket = IO.socket(APIConstants.URLs.SOCKET_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on(Params.USER_LOCATION, onNewMessage);
        socket.connect();
    }

    private void attemptToSendLocation(double latitude, double longitude) {
        if (!socket.connected()) return;
        JSONObject locationObject = new JSONObject();
        try {
            locationObject.put(Params.USER_ID, requestDetails.getClientId());
            locationObject.put(Params.PROVIDER_ID, prefutils.getIntValue(PrefKeys.ID, 0));
            locationObject.put(Params.REQUEST_ID, requestDetails.getRequestId());
            locationObject.put(Params.PROVIDER_TOKEN, prefutils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
            locationObject.put(Params.LATITUDE, latitude);
            locationObject.put(Params.LONGITUDE, longitude);
            if (null != location) {
                locationObject.put(Params.BEARING, location.getBearing());
            } else
                locationObject.put(Params.BEARING, currentLocation.getBearing());
            socket.emit(Params.PROVIDER_LOCATION_UPDATE, locationObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void geCancelReason() {
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.cancelReasonsList(prefutils.getIntValue(PrefKeys.ID, 0)
                , prefutils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();

                JSONObject cancelReasonsResponse = null;
                try {
                    cancelReasonsResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cancelReasonsResponse != null) {
                    if (cancelReasonsResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        JSONArray cancelReasonArray = cancelReasonsResponse.optJSONArray(Params.DATA);
                        if (null != cancelReasonArray && cancelReasonArray.length() > 0)
                            cancelReasonLst.clear();
                        for (int i = 0; i < cancelReasonArray.length(); i++) {
                            JSONObject dataObj = cancelReasonArray.optJSONObject(i);
                            CancelReason cancel = new CancelReason();
                            cancel.setReasonId(dataObj.optString("reason_id"));
                            cancel.setReasontext(dataObj.optString("cancel_reason"));
                            cancelReasonLst.add(cancel);
                        }
                        if (null != cancelReasonLst && cancelReasonLst.size() > 0) {
                            CancelReasonDialog(cancelReasonLst);
                        } else {
                            UiUtils.showShortToast(activity, getString(R.string.txt_no_cancel_reason));
                        }

                    } else {
                        UiUtils.showShortToast(activity, cancelReasonsResponse.optString(Params.ERROR));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                try {
                    if (NetworkUtils.isNetworkConnected(getActivity())) {
                        UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void CancelReasonDialog(ArrayList<CancelReason> cancelReasonLst) {
        final Dialog CancelReasondialog = new Dialog(activity, R.style.DialogThemeforview);
        CancelReasondialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CancelReasondialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));
        CancelReasondialog.setCancelable(false);
        CancelReasondialog.setContentView(R.layout.cancel_request_layout);
        RecyclerView cancel_reason_lst = CancelReasondialog.findViewById(R.id.cancel_reason_lst);
        CancelReasonAdapter CancelAdapter = new CancelReasonAdapter(activity, cancelReasonLst);
        cancel_reason_lst.setLayoutManager(new LinearLayoutManager(getActivity()));
        cancel_reason_lst.setAdapter(CancelAdapter);
        cancel_reason_lst.addOnItemTouchListener(new RecyclerLongPressClickListener(activity, cancel_reason_lst, new RecyclerLongPressClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                cancelOngoingRide(cancelReasonLst.get(position).getReasonId(), cancelReasonLst.get(position).getReasontext());
                CancelReasondialog.dismiss();
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
        CancelReasondialog.show();
    }

    protected void cancelOngoingRide(String reason_id, String reasontext) {
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.cancelOngoingRide(prefutils.getIntValue(PrefKeys.ID, 0)
                , prefutils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , requestDetails.getRequestId()
                , reason_id
                , reasontext);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject cancelResponse = null;
                try {
                    cancelResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cancelResponse != null) {
                    if (cancelResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
//                        TODO:Clear data
//                        new PreferenceHelper(activity).clearRequestData();
                        prefutils.setValue(PrefKeys.TRIP_TIME, 0L);
                        activity.addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                    } else {
                        UiUtils.showShortToast(activity, cancelResponse.optString(Params.ERROR));
                    }
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


    private void setJobStatus(int jobStatus) {
        switch (jobStatus) {
            case Constants.IS_PROVIDER_ACCEPTED:
                try {
                    tvTripStatus.setText(activity.getResources().getString(R.string.btn_status_txt_1));
                    tvTripId.setText(getContext().getString(R.string.viaje).concat(requestDetails.getRequesUniqueId()));
                    origenDataLayout.setVisibility(View.GONE);
                    if (null != delayLatlan)
                        if (polylineProviderAccepted == null) {
                            getDirectionsWay(Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()), delayLatlan.latitude, delayLatlan.longitude);
                        } else {
                            LatLng origen = new LatLng(Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()));
                            if (PolyUtil.isLocationOnPath(origen, polylineProviderAccepted.getPoints(), true, 100)) {
                                drawResponse(responsePath);
                            }
                            else
                            {
                                currLatLng = LocationHelper.getCurrentLocation();
                                getDirectionsWay(currLatLng.latitude,currLatLng.longitude,Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()));
                            }
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Constants.IS_PROVIDER_STARTED:
                tvTripStatus.setText(activity.getResources().getString(R.string.btn_status_txt_2));
                tvTripId.setText(getContext().getString(R.string.viaje).concat(requestDetails.getRequesUniqueId()));
                if(null!=currLatLng && null!= srcLang)
                {
                    getDistanceAndDuration(currLatLng,srcLang);
                    origenDataLayout.setVisibility(View.VISIBLE);
                }


                if (null != delayLatlan)
                    if (polylineProviderStarted == null) {
                        if(currLatLng != null){
                            getDirectionsWay(currLatLng.latitude,currLatLng.longitude,Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()));
                        }
                        else
                        {
                            currLatLng = LocationHelper.getCurrentLocation();
                            getDirectionsWay(currLatLng.latitude,currLatLng.longitude,Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()));
                        }
                    } else {
                        LatLng origen = delayLatlan;
                        if (PolyUtil.isLocationOnPath(origen, polylineProviderStarted.getPoints(), true, 100)) {
                            drawResponse(responsePath);
                        }
                        else
                        {
                            currLatLng = LocationHelper.getCurrentLocation();
                            getDirectionsWay(currLatLng.latitude,currLatLng.longitude,Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()));
                        }
                    }

                break;
            case Constants.IS_PROVIDER_ARRIVED:
                try {
                    tvTripId.setText(getContext().getString(R.string.viaje).concat(requestDetails.getRequesUniqueId()));
                    origenDataLayout.setVisibility(View.GONE);
                    addressTitle.setText(getString(R.string.drop_address));
                    tvTripStatus.setText(activity.getResources().getString(R.string.btn_status_txt_3));
                    addressTitle.setTextColor(Color.parseColor("#ff0000"));
                    if (!requestDetails.getDestinationAddress().equals("")) {
                        tvCurrentLocation.setText(requestDetails.getDestinationAddress());
                    } else {
                        tvCurrentLocation.setText("--Not Available--");
                    }
                    if (null != delayLatlan)
                    {
                        if (polylineProviderArrived == null) {
                            getDirectionsWay(Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()),
                                    delayLatlan.latitude, delayLatlan.longitude);
                        } else {
                            LatLng origen = new LatLng(Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()));
                            if (PolyUtil.isLocationOnPath(origen, polylineProviderArrived.getPoints(), true, 100)) {
                                drawResponse(responsePath);
                            }
                            else
                            {
                                currLatLng = LocationHelper.getCurrentLocation();
                                getDirectionsWay(currLatLng.latitude,currLatLng.longitude,Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case Constants.IS_PROVIDER_SERVICE_STARTED:
                try {
                    tvTripId.setText(getContext().getString(R.string.viaje).concat(requestDetails.getRequesUniqueId()));
                    origenDataLayout.setVisibility(View.GONE);
                    cancelTrip.setVisibility(View.GONE);
                    addressTitle.setText(activity.getString(R.string.drop_address));
                    addressTitle.setTextColor(activity.getResources().getColor(R.color.red));
                    if (!requestDetails.getDestinationAddress().equals("")) {
                        tvCurrentLocation.setText(requestDetails.getDestinationAddress());
                    } else {
                        tvCurrentLocation.setText(getResources().getString(R.string.txt_not_avialbel));
                    }
                    tvTripStatus.setText(activity.getResources().getString(R.string.btn_status_txt_4));
                    if (null != delayLatlan && requestDetails.getdLatitude() != null && requestDetails.getdLongitude() != null) {
                        if (Integer.valueOf(requestDetails.getIsAdStop()) == 1 && stopMarker == null) {
                            if (stopLay.getVisibility() == View.GONE) {
                                stopLay.setVisibility(View.VISIBLE);
                                stopAddress.setText(requestDetails.getAdStopAddress());
                            }
                            notifiedStop = 1;
                            if (polylineProviderServiceStarted == null) {
                                getDirectionsWay(delayLatlan.latitude, delayLatlan.longitude,
                                        Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                            } else {
                                LatLng origen = delayLatlan;
                                if (PolyUtil.isLocationOnPath(origen, polylineProviderServiceStarted.getPoints(), true, 100)) {
                                    drawResponse(responsePath);
                                }
                                else
                                {
                                    currLatLng = LocationHelper.getCurrentLocation();
                                    getDirectionsWay(currLatLng.latitude,currLatLng.longitude,Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                }
                            }

                        } else if (Integer.valueOf(requestDetails.getIsAddressChanged()) == 1 && notifiedDest == 0) {
                            if (Integer.valueOf(requestDetails.getIsAdStop()) == 1 && stopMarker == null) {
                                if (stopLay.getVisibility() == View.GONE) {
                                    stopLay.setVisibility(View.VISIBLE);
                                    stopAddress.setText(requestDetails.getAdStopAddress());
                                }
                                notifiedStop = 1;
                                if (polylineProviderServiceStarted == null) {
                                    getDirectionsWay(delayLatlan.latitude, delayLatlan.longitude,
                                            Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                } else {
                                    LatLng origen = delayLatlan;
                                    if (PolyUtil.isLocationOnPath(origen, polylineProviderServiceStarted.getPoints(), true, 100)) {
                                        drawResponse(responsePath);
                                    }
                                    else
                                    {
                                        currLatLng = LocationHelper.getCurrentLocation();
                                        getDirectionsWay(currLatLng.latitude,currLatLng.longitude,Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                    }
                                }

                            } else {
                                if (polylineProviderServiceStarted == null) {
                                    getDirectionsWay(delayLatlan.latitude, delayLatlan.longitude,
                                            Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                } else {
                                    LatLng origen = delayLatlan;
                                    if (PolyUtil.isLocationOnPath(origen, polylineProviderServiceStarted.getPoints(), true, 100)) {
                                        drawResponse(responsePath);
                                    }
                                    else
                                    {
                                        currLatLng = LocationHelper.getCurrentLocation();
                                        getDirectionsWay(currLatLng.latitude,currLatLng.longitude,Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                    }
                                }

                            }
                            notifiedDest = 1;
                            changed = true;
                        } else {
                            if (polylineProviderServiceStarted == null) {
                                getDirectionsWay(delayLatlan.latitude, delayLatlan.longitude,
                                        Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                            } else {
                                LatLng origen = delayLatlan;
                                if (PolyUtil.isLocationOnPath(origen, polylineProviderServiceStarted.getPoints(), true, 100)) {
                                    drawResponse(responsePath);
                                }
                                else
                                {
                                    currLatLng = LocationHelper.getCurrentLocation();
                                    getDirectionsWay(currLatLng.latitude,currLatLng.longitude,Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }
    }

    public Runnable updateTimer = new Runnable() {
        public void run() {
            timeInMilliseconds = System.currentTimeMillis() - starttime;

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            String start_date = formatter.format(new Date(starttime));
            String end_date = formatter.format(new Date(System.currentTimeMillis()));


            duration = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeInMilliseconds),
                    TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) % TimeUnit.MINUTES.toSeconds(1));
            String[] units = duration.split(":");

            int hours = Integer.valueOf(units[0]);
            int minutes = Integer.valueOf(units[1]);
            int seconds = Integer.valueOf(units[2]);
//            trip_duration = 3600 * hours + 60 * minutes + seconds;
            trip_duration = (int) findDifference(start_date, end_date);
            timerHandler.postDelayed(this, 0);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
        }

        locHelper = new LocationHelper(activity);
        locHelper.setLocationReceivedLister(this);
        if (requestDetails != null) {
            tvUserName.setText(requestDetails.getClientName());
            tvUserMobileNumber.setText(getResources().getString(R.string.txt_mobile) + " " + requestDetails.getClientPhoneNumber());
            Glide.with(activity).load(requestDetails.getClientProfile())
                    .placeholder(R.drawable.defult_user)
                    .error(R.drawable.defult_user)
                    .thumbnail(0.5f)
                    .into(ivUser);
            tvCurrentLocation.setText(requestDetails.getSourceAddress());
            startCheckingUpcomingRequests();
        }
        setJobStatus(jobStatus);
        if (jobStatus == 4) {
            startTimer();
        }
    }

    private void setSourceDestinationMarkerOnMap() {
        if (requestDetails != null) {
            srcLang = new LatLng(Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()));
            desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
            if (srcLang != null && desLang != null) {
                MarkerOptions opt = new MarkerOptions();
                opt.position(srcLang);
                opt.title(getResources().getString(R.string.txt_pickup_address));
                opt.anchor(0.5f, 0.5f);
                opt.icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.pickup_location));
                pickup_marker = googleMap.addMarker(opt);
                mHashMap.put(pickup_marker, 0);
                mHashMap.put(drop_marker, 1);
                googleMap.addMarker(opt);
                googleMap.setOnMarkerClickListener(this);
                if (pickup_marker != null && drop_marker != null && !requestDetails.getDestinationAddress().equals("")) {
                    fitmarkers_toMap();
                }
            }
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

            if (null == currLatLng) {
                currLatLng = LocationHelper.getCurrentLocation();
            }
            currentMarker = googleMap.addMarker(new MarkerOptions()
                    .position(currLatLng)
                    .rotation(segmentBearing)
                    .anchor(0.5f, 0.5f)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_booking_lux_map_topview))
                    .title(activity.getResources().getString(R.string.txt_driver)));

            if (googleMap != null) {
                //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLng, 15));
            }

            oldLatLng = currLatLng;
        }
    }

    /*private void getDirections(double latitude, double longitude, double latitude1, double longitude1) {
        String directionUrl = Const.DIRECTION_API_BASE + Const.ORIGIN + "="
                + latitude + "," + longitude + "&" + Const.DESTINATION + "="
                + latitude1 + "," + longitude1 + "&" + Const.EXTANCTION;
        Call<String> call = apiInterface.getDirections(directionUrl);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null) {
                    if (jobStatus > 3) {
                        try {
                            if (drop_marker == null) {
                            } else {
                                drop_marker.remove();
                            }
                            if (Integer.valueOf(requestDetails.getIsAdStop()) == 1) {
                                if (Integer.valueOf(requestDetails.getIsAdStop()) == 1) {
                                    if (stopLay.getVisibility() == View.GONE) {
                                        stopLay.setVisibility(View.VISIBLE);
                                        stopAddress.setText(requestDetails.getAdStopAddress());
                                    }
                                }
                            }
                            if (requestDetails.getIsAdStop() != null && Integer.valueOf(requestDetails.getIsAdStop()) == 1 && stopMarker == null) {
                                stop_latlng = new LatLng(Double.valueOf(requestDetails.getAdStopLatitude()), Double.valueOf(requestDetails.getAdStopLongitude()));
                                MarkerOptions opt = new MarkerOptions();
                                opt.position(stop_latlng);
                                opt.anchor(0.5f, 0.5f);
                                opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_stop));
                                stopMarker = googleMap.addMarker(opt);
                                if (drop_marker != null) {
                                    drop_marker.remove();
                                    desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                    MarkerOptions opty = new MarkerOptions();
                                    opty.position(desLang);
                                    opty.anchor(0.5f, 0.5f);
                                    opty.icon(BitmapDescriptorFactory
                                            .fromResource(R.mipmap.drop_location));
                                    drop_marker = googleMap.addMarker(opty);
                                } else {
                                    desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                    MarkerOptions opta = new MarkerOptions();
                                    opta.position(desLang);
                                    opta.anchor(0.5f, 0.5f);
                                    opta.icon(BitmapDescriptorFactory
                                            .fromResource(R.mipmap.drop_location));
                                    drop_marker = googleMap.addMarker(opta);
                                }
                            } else if (Integer.valueOf(requestDetails.getIsAddressChanged()) == 1 && changed == true && notifiedDest == 1) {
                                notifiedDest = 2;
                                if (drop_marker != null) {
                                    drop_marker.remove();
                                    desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                    MarkerOptions opt = new MarkerOptions();
                                    opt.position(desLang);
                                    opt.anchor(0.5f, 0.5f);
                                    opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                                    drop_marker = googleMap.addMarker(opt);
                                } else {
                                    desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                    MarkerOptions opt = new MarkerOptions();
                                    opt.position(desLang);
                                    opt.anchor(0.5f, 0.5f);
                                    opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                                    drop_marker = googleMap.addMarker(opt);
                                }
                            } else {
                                if (drop_marker != null) {
                                    drop_marker.remove();
                                    Log.e("asher", "directions response 7");
                                    desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                    MarkerOptions opt = new MarkerOptions();
                                    opt.position(desLang);
                                    opt.anchor(0.5f, 0.5f);
                                    opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                                    drop_marker = googleMap.addMarker(opt);
                                } else {
                                    desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                    MarkerOptions opt = new MarkerOptions();
                                    opt.position(desLang);
                                    opt.anchor(0.5f, 0.5f);
                                    opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                                    drop_marker = googleMap.addMarker(opt);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    drawPath(response.body());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }*/


    public void drawResponse(Response<String> response) {
        Log.e("asher", "directions response " + response);
        responsePath = response;
        if (response != null) {
            if (jobStatus > 3) {
                try {
                    if (drop_marker == null) {
                    } else {
                        drop_marker.remove();
                    }
                    if (Integer.valueOf(requestDetails.getIsAdStop()) == 1) {
                        if (Integer.valueOf(requestDetails.getIsAdStop()) == 1) {
                            if (stopLay.getVisibility() == View.GONE) {
                                stopLay.setVisibility(View.VISIBLE);
                                stopAddress.setText(requestDetails.getAdStopAddress());
                            }
                        }
                    }
                    if (requestDetails.getIsAdStop() != null && Integer.valueOf(requestDetails.getIsAdStop()) == 1 && stopMarker == null) {
                        stop_latlng = new LatLng(Double.valueOf(requestDetails.getAdStopLatitude()), Double.valueOf(requestDetails.getAdStopLongitude()));
                        MarkerOptions opt = new MarkerOptions();
                        opt.position(stop_latlng);
                        opt.anchor(0.5f, 0.5f);
                        opt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_stop));
                        stopMarker = googleMap.addMarker(opt);
                        if (drop_marker != null) {
                            drop_marker.remove();
                            desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                            MarkerOptions opty = new MarkerOptions();
                            opty.position(desLang);
                            opty.anchor(0.5f, 0.5f);
                            opty.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                            drop_marker = googleMap.addMarker(opty);
                        } else {
                            desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                            MarkerOptions opta = new MarkerOptions();
                            opta.position(desLang);
                            opta.anchor(0.5f, 0.5f);
                            opta.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                            drop_marker = googleMap.addMarker(opta);
                        }
                    } else if (Integer.valueOf(requestDetails.getIsAddressChanged()) == 1 && changed == true && notifiedDest == 1) {
                        notifiedDest = 2;
                        if (drop_marker != null) {
                            drop_marker.remove();
                            desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                            MarkerOptions opt = new MarkerOptions();
                            opt.position(desLang);
                            opt.anchor(0.5f, 0.5f);
                            opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                            drop_marker = googleMap.addMarker(opt);
                        } else {
                            desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                            MarkerOptions opt = new MarkerOptions();
                            opt.position(desLang);
                            opt.title(activity.getResources().getString(R.string.txt_drop_loc));
                            opt.anchor(0.5f, 0.5f);
                            opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                            drop_marker = googleMap.addMarker(opt);
                        }
                    } else {
                        if (drop_marker != null) {
                            drop_marker.remove();
                            Log.e("asher", "directions response 7");
                            desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                            MarkerOptions opt = new MarkerOptions();
                            opt.position(desLang);
                            opt.anchor(0.5f, 0.5f);
                            opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                            drop_marker = googleMap.addMarker(opt);
                        } else {
                            desLang = new LatLng(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                            MarkerOptions opt = new MarkerOptions();
                            opt.position(desLang);
                            opt.anchor(0.5f, 0.5f);
                            opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                            drop_marker = googleMap.addMarker(opt);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            drawPath(response.body());
        }
    }

    private void getDirectionsWay(double latitude, double longitude, double latitude1, double longitude1) {
        responsePath = null;
        String directionwayUrl = Const.DIRECTION_API_BASE + Const.ORIGIN + "="
                + latitude + "," + longitude + "&" + Const.DESTINATION + "="
                + latitude1 + "," + longitude1 + "&" + Const.EXTANCTION;
        Call<String> call = apiInterface.getDirectionsWay(directionwayUrl);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                drawResponse(response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });

    }

    public void drawPath(String result) {
        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            PolylineOptions options = new PolylineOptions().width(8).color(Color.BLACK).geodesic(true);
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                options.add(point);
            }
            if (googleMap != null) {
                if (polylineProviderAccepted != null)
                    polylineProviderAccepted.remove();
                if (polylineProviderStarted != null)
                    polylineProviderStarted.remove();
                if (polylineProviderArrived != null)
                    polylineProviderArrived.remove();
                if (polylineProviderServiceStarted != null)
                    polylineProviderServiceStarted.remove();
                switch (jobStatus) {
                    case Const.IS_PROVIDER_ACCEPTED:
                        polylineProviderAccepted = googleMap.addPolyline(options);
                        polylineProviderStarted = null;
                        polylineProviderArrived = null;
                        polylineProviderServiceStarted = null;
                        break;
                    case Const.IS_PROVIDER_STARTED:
                        polylineProviderAccepted = null;
                        polylineProviderStarted = googleMap.addPolyline(options);
                        polylineProviderArrived = null;
                        polylineProviderServiceStarted = null;
                        break;
                    case Const.IS_PROVIDER_ARRIVED:
                        polylineProviderAccepted = null;
                        polylineProviderStarted = null;
                        polylineProviderArrived = googleMap.addPolyline(options);
                        polylineProviderServiceStarted = null;
                        break;
                    case Const.IS_PROVIDER_SERVICE_STARTED:
                        polylineProviderAccepted = null;
                        polylineProviderStarted = null;
                        polylineProviderArrived = null;
                        polylineProviderServiceStarted = googleMap.addPolyline(options);
                        break;

                }
                /*if (poly_line != null)
                    poly_line.remove();
                poly_line = googleMap.addPolyline(options);*/
            }
        } catch (Exception e) {
        }

    }

   /* public void drawPath(String result) {
        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            PolylineOptions options = new PolylineOptions().width(8).color(Color.BLACK).geodesic(true);
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                options.add(point);
            }
            if (poly_line != null)
                poly_line.remove();
            if (googleMap != null) {
                if (null != poly_line) {
                    poly_line = googleMap.addPolyline(options);
                } else {
                    poly_line = googleMap.addPolyline(options);
                }
            }
        } catch (JSONException e) {

        }
    }*/

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void fitmarkers_toMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pickup_marker.getPosition());
        builder.include(drop_marker.getPosition());
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.moveCamera(cu);
    }


    @Override
    public void onResume() {
        super.onResume();
        checkRequestStatus();
        activity.currentFragment = Const.TRAVEL_MAP_FRAGMENT;
    }

    @Override
    public void onLocationReceived(LatLng latlong) {

    }

    @Override
    public void onLocationReceived(Location location) {
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions mOptions = new MarkerOptions();
            delayLatlan = latLng;
            if (currentMarker == null) {
                onCurrentLocationObtained(new LatLng(location.getLatitude(),
                        location.getLongitude()));
                setJobStatus(jobStatus);
//                AnimateMarker.animateMarker(activity, location, currentMarker, googleMap, String.valueOf(location.getBearing()));
                updateMarkerOnMap(delayLatlan.latitude,delayLatlan.longitude,location.getBearing());
            } else {
//                AnimateMarker.animateMarker(activity, location, currentMarker, googleMap, String.valueOf(location.getBearing()));
                updateMarkerOnMap(delayLatlan.latitude,delayLatlan.longitude,location.getBearing());
            }
            if (null != googleMap && !markerAnimated) {
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,
                        16);
                googleMap.moveCamera(update);
                markerAnimated = true;
            }
        }
    }


    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {
        if (location != null) {
            currentLocation = location;
        }
    }

    private void startTimer() {
        if (prefutils.getLongValue(PrefKeys.TRIP_TIME, 0L) == 0L) {
            prefutils.setValue(PrefKeys.TRIP_TIME, System.currentTimeMillis());
        }
        starttime = prefutils.getLongValue(PrefKeys.TRIP_TIME, 0L);
        timerHandler.postDelayed(updateTimer, 0);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(updateTimer);
    }

    private void cancelRide() {
        Call<String> call = apiInterface.postCancelTrip(prefutils.getIntValue(PrefKeys.ID, 0),
                prefutils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , requestDetails.getRequestId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject postCancelResponse = null;
                try {
                    postCancelResponse = new JSONObject(response.body());
                    if (postCancelResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        stopCheckingUpcomingRequests();
                        prefutils.setValue(PrefKeys.TRIP_TIME, 0L);
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        stopTimer();
                        activity.finish();
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

    private void providerStarted() {
        if (polylineProviderAccepted != null)
            polylineProviderAccepted.remove();
        disableStatusBtn();
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.startRequest(prefutils.getIntValue(PrefKeys.ID, 0),
                prefutils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                requestDetails.getRequestId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject providerStartedResponse = null;
                try {
                    providerStartedResponse = new JSONObject(response.body());
                    if (providerStartedResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        jobStatus = Const.IS_PROVIDER_STARTED;
                        setJobStatus(jobStatus);
                        enableStatusBtn();
                    } else {
                        UiUtils.hideLoadingDialog();
                    }
                } catch (JSONException e) {
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

    private void providerArrived() {
        disableStatusBtn();
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.notifyProviderArrived(prefutils.getIntValue(PrefKeys.ID, 0),
                prefutils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                requestDetails.getRequestId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject providerArrivedResponse = null;
                try {
                    providerArrivedResponse = new JSONObject(response.body());
                    if (providerArrivedResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        jobStatus = Const.IS_PROVIDER_ARRIVED;
                        setJobStatus(jobStatus);
                        enableStatusBtn();
                    } else {
                        UiUtils.hideLoadingDialog();
                    }
                } catch (JSONException e) {
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

    private void providerServiceStarted() {
        if (poly_line != null)
            poly_line.remove();
        disableStatusBtn();
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.providerServiceStarted(prefutils.getIntValue(PrefKeys.ID, 0),
                prefutils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                requestDetails.getRequestId());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject providerStartedResponse = null;
                try {
                    providerStartedResponse = new JSONObject(response.body());
                    if (providerStartedResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        Commonutils.progressdialog_hide();
                        jobStatus = Const.IS_PROVIDER_SERVICE_STARTED;
                        setJobStatus(jobStatus);
                        enableStatusBtn();
                    } else {
                        Commonutils.progressdialog_hide();
                    }
                } catch (JSONException e) {
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


    private void startCheckingUpcomingRequests() {
        startCheckRegTimer();
    }

    private void stopCheckingUpcomingRequests() {
        if (reqHandler != null) {
            reqHandler.removeCallbacks(runnable);
        }
    }

    public void startCheckRegTimer() {
        reqHandler.postDelayed(runnable, 5000);
    }


    private void providerServiceCompleted(String distance, String time) {
        disableStatusBtn();
        prefutils.setValue(PrefKeys.TRIP_TIME, 0L);
        String dAddress = getAddressFromLatLng(delayLatlan.latitude, delayLatlan.longitude);
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.providerComplete(prefutils.getIntValue(PrefKeys.ID, 0),
                prefutils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                requestDetails.getRequestId(), time, distance, delayLatlan.latitude, delayLatlan.longitude, dAddress);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject providerStartedResponse = null;
                try {
                    providerStartedResponse = new JSONObject(response.body());
                    if (providerStartedResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        enableStatusBtn();
                        UiUtils.hideLoadingDialog();
                        requestDetails = ParserUtils.parseTripCompleted(response.body());
                        jobStatus = Const.IS_USER_RATED;
                        stopCheckingUpcomingRequests();
                        FeedBackFragment feedbackFrament = new FeedBackFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Const.REQUEST_DETAIL, requestDetails);
                        bundle.putString("SCHEDULE", "0");
                        feedbackFrament.setArguments(bundle);
                        activity.addFragment(feedbackFrament, false, Const.FEEDBACK_FRAGMENT, true);
                        stopTimer();

                    }
                } catch (JSONException e) {
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

    private String getAddressFromLatLng(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(0)).append("\n");
                result.append(address.getCountryName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public void checkRequestStatus() {
        Call<String> call = apiInterface.checkRequestStatus(
                prefutils.getIntValue(PrefKeys.ID, 0),
                prefutils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body());
                    JSONObject data = jsonObject.optJSONObject(Params.DATA);
                    JSONArray dataObjectArray = data.optJSONArray(Params.DATA);
                    requestDetails = ParserUtils.parseRequestStatus(response.body());
                    prefutils.setValue(PrefKeys.REQUEST_ID, requestDetails.getRequestId());
                    prefutils.setValue(PrefKeys.USER_ID, requestDetails.getClientId());
                    tvUserMobileNumber.setText("Mobile:" + requestDetails.getClientPhoneNumber());
                    mobileNo = requestDetails.getClientPhoneNumber();
                    enableStatusBtn();
                    setButtonAndMapStatus(dataObjectArray);
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

    protected void newRequestStatusCheck() {
        Call<String> call = apiInterface.requestStatusCheckNew(prefutils.getIntValue(PrefKeys.ID, 0)
                , prefutils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject requestResponse = null;
                try {
                    requestResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (requestResponse != null) {
                    if (requestResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        JSONArray data = requestResponse.optJSONArray(Params.DATA);
                        if (data != null && data.length() != 0) {
                            JSONObject requestObject = data.optJSONObject(0);
                            requestDetails.setRequestId(requestObject.optInt(Params.REQUEST_ID));
                            requestDetails.setRequest_type(requestObject.optString(Params.REQUEST_SERVICE_TYPE));
                            requestDetails.setStatus(requestObject.optString(Params.STATUS));
                            requestDetails.setIsAdStop(requestObject.optString(Params.IS_ADD_STOP));
                            requestDetails.setAdStopAddress(requestObject.optString(Params.ADD_STOP_ADDRESS));
                            requestDetails.setAdStopLatitude(requestObject.optString(Params.ADD_STOP_LATITUDE));
                            requestDetails.setAdStopLongitude(requestObject.optString(Params.ADD_STOP_LONGITUDE));

                            requestDetails.setsLatitude(requestObject.optString(Params.S_LATITUDE));
                            requestDetails.setsLongitude(requestObject.optString(Params.S_LONGITUDE));
                            requestDetails.setdLatitude(requestObject.optString(Params.D_LATITUDE));
                            requestDetails.setdLongitude(requestObject.optString(Params.D_LONGITUDE));
                        }
                        setButtonAndMapStatus(data);
                    } else {
                        UiUtils.showShortToast(activity, requestResponse.optString(Params.ERROR));
                    }
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


    private void setButtonAndMapStatus(JSONArray dataObjectArray) {
        try {
            if (dataObjectArray != null && dataObjectArray.length() == 0) {
                if (!isShown) {
                    if (isAdded() && activity.currentFragment.equals(Constants.TRAVEL_MAP_FRAGMENT)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(getResources().getString(R.string.txt_cancel_msg_user))
                                .setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.txt_ok), (dialog, id) -> {
                                    dialog.dismiss();
                                    stopCheckingUpcomingRequests();
                                    stopTimer();
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    activity.startActivity(intent);
                                    activity.finish();
                                });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    isShown = true;
                }
                stopCheckingUpcomingRequests();
                stopTimer();
            }
            if (jobStatus > 3) {
                if (Integer.valueOf(requestDetails.getIsAdStop()) == 1 && stopMarker == null && notifiedStop == 0) {
                    if (!isShownStop) {
                        if (isAdded() && activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage(getResources().getString(R.string.txt_stop_added_user))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            notifiedStop = 1;
                                            getDirectionsWay(Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()),
                                                    Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        isShownStop = true;
                    }
                }
                if (Integer.valueOf(requestDetails.getIsAddressChanged()) == 1 && changed == false && notifiedDest == 0) {
                    changed = true;
                    if (!isShownDest) {
                        if (isAdded() && activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage(getResources().getString(R.string.txt_dest_change_user))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.txt_ok), (dialog, id) -> {
                                        notifiedDest = 1;
                                        if (Integer.valueOf(requestDetails.getIsAdStop()) == 1) {

                                            getDirectionsWay(Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()),
                                                    Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));

                                            if (!requestDetails.getDestinationAddress().equals("")) {
                                                tvCurrentLocation.setText(requestDetails.getDestinationAddress());
                                            } else {
                                                tvCurrentLocation.setText("--Not Available--");
                                            }
                                            drop_marker.remove();

                                        } else {

                                            getDirectionsWay(Double.valueOf(requestDetails.getsLatitude()), Double.valueOf(requestDetails.getsLongitude()),
                                                    Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()));

                                            if (!requestDetails.getDestinationAddress().equals("")) {
                                                tvCurrentLocation.setText(requestDetails.getDestinationAddress());
                                            } else {
                                                tvCurrentLocation.setText("--Not Available--");
                                            }
                                            if (drop_marker != null) {
                                                drop_marker.remove();
                                            }
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        isShownDest = true;
                    }
                } else {
                    setJobStatus(jobStatus);
                }
            } else {
                setJobStatus(jobStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findDistanceAndTime(LatLng s_latlan, LatLng d_latlan) {
        String finddistanceandTime = Const.GOOGLE_MATRIX_URL + Const.Params.ORIGINS + "="
                + s_latlan.latitude + "," + s_latlan.longitude + "&" + Const.Params.DESTINATION + "="
                + d_latlan.latitude + "," + d_latlan.longitude + "&" + Const.Params.MODE + "="
                + "driving" + "&" + Const.Params.LANGUAGE + "="
                + "en-EN" + "&" + "key=" + Const.GOOGLE_API_KEY + "&" + Const.Params.SENSOR + "="
                + false;

        Call<String> call = apiInterface.findDistanceandTime(finddistanceandTime);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response.body());
                    if (jsonObject != null) {
                        if (jsonObject.optString(Params.STATUS).equals(Constants.OK)) {
                            JSONArray sourceArray = jsonObject.getJSONArray(Params.ORIGIN_ADDRESSES);
                            String sourceObject = (String) sourceArray.get(0);
                            JSONArray destinationArray = jsonObject.optJSONArray(Params.DESTINATION_ADDRESSES);
                            String destinationObject = (String) destinationArray.get(0);
                            JSONArray jsonArray = jsonObject.optJSONArray("rows");
                            JSONObject elementsObject = jsonArray.optJSONObject(0);
                            JSONArray elementsArray = elementsObject.optJSONArray("elements");
                            JSONObject distanceObject = elementsArray.optJSONObject(0);
                            JSONObject dObject = distanceObject.optJSONObject("distance");
                            String distance = dObject.optString("value");
                            JSONObject durationObject = distanceObject.optJSONObject("duration");
                            String duration = durationObject.optString("value");
                            double trip_dis = Integer.valueOf(distance) * 0.001;
                            float dura = TimeUnit.MILLISECONDS.toSeconds(trip_duration);
                            providerServiceCompleted(String.valueOf(trip_dis), String.valueOf(trip_duration));
                        }
                    }
                } catch (JSONException e) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tearDownSocket();
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.driver_travel_map);
        if (f != null) {
            try {
                getFragmentManager().beginTransaction().remove(f).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*TO clear all views */
        ViewGroup mContainer = getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();
        stopCheckingUpcomingRequests();
    }

    private void tearDownSocket() {
        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off(Socket.EVENT_MESSAGE, onNewMessage);
    }


    @Override
    public void onMapReady(GoogleMap mgoogleMap) {
        googleMap = mgoogleMap;
        Commonutils.progressdialog_hide();
        if (googleMap != null) {
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            setSourceDestinationMarkerOnMap();
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if (null != mHashMap.get(marker)) {
            switch (mHashMap.get(marker)) {
                case 0:
                    Intent sourcenNvi = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + requestDetails.getsLatitude() + "," + requestDetails.getsLongitude()));
                    startActivity(sourcenNvi);
                    break;
                case 1:
                    Intent destNavi = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + requestDetails.getdLatitude() + "," + requestDetails.getdLongitude()));
                    startActivity(destNavi);
                    break;
            }
        }
        return false;
    }


    private void requestPermission(int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD) {
            if (!Utils.canDrawOverlays(activity)) {
                needPermissionDialog(requestCode);
            } else {
                activity.startService(new Intent(activity, ChatHeadService.class));
            }
        }
    }


    private void needPermissionDialog(final int requestCode) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setMessage(getResources().getString(R.string.txt_allow_permission));
        builder.setPositiveButton(getResources().getString(R.string.txt_ok),
                (dialog, which) -> {
                    requestPermission(requestCode);
                });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call();
                } else {
                    UiUtils.showShortToast(getActivity(), "Cannot call without Call Permission");
                }
                break;
        }
    }

    private void call() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mobileNo));
        startActivity(callIntent);
    }


    @OnClick({R.id.tv_current_location, R.id.btn_direction, R.id.tv_trip_status, R.id.cancel_trip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_current_location:
            case R.id.btn_direction:
                if (Utils.canDrawOverlays(activity)) {
                    activity.startService(new Intent(activity, ChatHeadService.class));
                    if (jobStatus == 1 || jobStatus == 2) {
                        Intent sourcenNvi = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + requestDetails.getsLatitude() + "," + requestDetails.getsLongitude()));
                        startActivity(sourcenNvi);
                    } else {
                        if (!requestDetails.getDestinationAddress().equals("")) {
                            if (Integer.valueOf(requestDetails.getIsAdStop()) == 0) {
                                Intent destNavi = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("http://maps.google.com/maps?daddr=" + requestDetails.getdLatitude() + "," + requestDetails.getdLongitude()));
                                startActivity(destNavi);
                            } else if (Integer.valueOf(requestDetails.getIsAdStop()) == 1) {
                                Log.e("asher", "stop direction " + requestDetails.getAdStopLatitude() + "," + requestDetails.getAdStopLongitude());
                                Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + requestDetails.getdLatitude() + "," + requestDetails.getdLongitude() + "&waypoints=" + requestDetails.getAdStopLatitude() + "," + requestDetails.getAdStopLongitude() + "&travelmode=driving");
                                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                intent.setPackage("com.google.android.apps.maps");
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException ex) {
                                    try {
                                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        startActivity(unrestrictedIntent);
                                    } catch (ActivityNotFoundException innerEx) {
                                        Toast.makeText(activity, "Please install a maps application", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                    }
                } else {
                    requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD);
                }
                break;
            case R.id.tv_trip_status:
                switch (jobStatus) {
                    case Const.IS_PROVIDER_ACCEPTED:
                        providerStarted();
                        break;
                    case Const.IS_PROVIDER_STARTED:
                        providerArrived();
                        break;
                    case Const.IS_PROVIDER_ARRIVED:
                        addressTitle.setText(getString(R.string.drop_address));
                        addressTitle.setTextColor(Color.parseColor("#ff0000"));
                        if (!requestDetails.getDestinationAddress().equals("")) {
                            tvCurrentLocation.setText(requestDetails.getDestinationAddress());
                        } else {
                            tvCurrentLocation.setText(getResources().getString(R.string.txt_not_avialbel));
                        }
                        providerServiceStarted();
                        startTimer();
                        if (null != delayLatlan) {
                            prefutils.setValue(PrefKeys.TRIPSTART_LAT, String.valueOf(delayLatlan.latitude));
                            prefutils.setValue(PrefKeys.TRIPSTART_LON, String.valueOf(delayLatlan.longitude));
                        }
                        break;
                    case Const.IS_PROVIDER_SERVICE_STARTED:
                        addressTitle.setText(getString(R.string.drop_address));
                        addressTitle.setTextColor(Color.parseColor("#ff0000"));
                        if (!requestDetails.getDestinationAddress().equals("")) {
                            tvCurrentLocation.setText(requestDetails.getDestinationAddress());
                        } else {
                            tvCurrentLocation.setText(getResources().getString(R.string.txt_not_avialbel));
                        }

                        if (!TextUtils.isEmpty(prefutils.getStringValue(PrefKeys.TRIPSTART_LAT, "")) || !TextUtils.isEmpty(prefutils.getStringValue(PrefKeys.TRIPSTART_LON, ""))) {
                            LatLng s_latlan = new LatLng(Double.valueOf(prefutils.getStringValue(PrefKeys.TRIPSTART_LAT, "")), Double.valueOf(prefutils.getStringValue(PrefKeys.TRIPSTART_LON, "")));
                            if (null != delayLatlan) {
                                findDistanceAndTime(s_latlan, delayLatlan);
                            }
                        } else {
                            if (null != delayLatlan && null != pickup_marker) {
                                findDistanceAndTime(pickup_marker.getPosition(), delayLatlan);
                            }
                        }
                        break;
                }
        }
    }

    public void enableStatusBtn() {
        tvTripStatus.setEnabled(true);
        tvTripStatus.setBackgroundColor(activity.getResources().getColor(R.color.black));
    }

    public void disableStatusBtn() {
        tvTripStatus.setEnabled(false);
        tvTripStatus.setBackgroundColor(activity.getResources().getColor(R.color.dark_grey));
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        currLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    protected void getDistanceAndDuration(LatLng picLatLng, LatLng dropLatLng) {
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
                        JSONArray jsonArray = fareResponse.optJSONArray("rows");
                        JSONObject elementsObject = jsonArray.optJSONObject(0);
                        JSONArray elementsArray = elementsObject.optJSONArray("elements");
                        JSONObject distanceObject = elementsArray.optJSONObject(0);
                        JSONObject dObject = distanceObject.optJSONObject("distance");
                        String distance = dObject.optString("text");
                        JSONObject durationObject = distanceObject.optJSONObject("duration");
                        String duration = durationObject.optString("text");
                        tv_origenDist.setText("Dist: ".concat(distance));
                        tv_origenDur.setText("Dur: ".concat(duration));

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
    public long findDifference(String start_date, String end_date) {

        SimpleDateFormat sdf = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");
        long differenceTotalInSec = 0;
        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            // Calucalte time difference in
            // seconds, minutes, hours, years,
            // and days
            long difference_In_Seconds
                    = (difference_In_Time
                    / 1000)
                    % 60;

            long difference_In_Minutes
                    = (difference_In_Time
                    / (1000 * 60))
                    % 60;

            long difference_In_Hours
                    = (difference_In_Time
                    / (1000 * 60 * 60))
                    % 24;

            long difference_In_Years
                    = (difference_In_Time
                    / (1000l * 60 * 60 * 24 * 365));

            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;

            differenceTotalInSec = difference_In_Seconds + (difference_In_Minutes * 60) + ((difference_In_Hours * 60) * 60);

        }

        // Catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }
        return differenceTotalInSec;
    }

}

