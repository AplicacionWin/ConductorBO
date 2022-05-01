package com.nikola.driver.utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.ui.activity.StatusAvailabilityActivity;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationSendDriverPosition extends Service {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private int hacerPeticion = 0;

    APIInterface apiInterface;
    PrefUtils prefUtils;

    @Override
    public void onCreate() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        initData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateAvailabilityStatus(1);

        ///createNotification();
        prepareForegroundNotification();
        startLocationUpdates();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Const.SEND_POSITION_DRIVER) {
                    updateAvailabilityStatus(0);
                    stopForeground(true);
                    stopSelf();
                }
                if (Const.SEND_POSITION_DRIVER) {

                    double lat = prefUtils.getDoubleValue(PrefKeys.LATITUDE_TIEMPO_REAL, "0");
                    double lon = prefUtils.getDoubleValue(PrefKeys.LONGITUD_TIEMPO_REAL, "0");
                    Log.d("Locations Esta es", "LAT: " + lat + " LON: "+ lon);
                    sendLocation(lat+"", lon+"");

                    handler.postDelayed(this, 5000);
                }
            }
        }, 5000);
        return START_NOT_STICKY;
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location currentLocation = locationResult.getLastLocation();
            prefUtils.setValue(PrefKeys.LATITUDE_TIEMPO_REAL, currentLocation.getLatitude());
            prefUtils.setValue(PrefKeys.LONGITUD_TIEMPO_REAL, currentLocation.getLongitude());
            Log.d("Locations", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
        }
    };

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(this.locationRequest, this.locationCallback, Looper.myLooper());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void prepareForegroundNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel("CHANNEL_ID", "Location Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
        Intent notificationIntent = new Intent(this, StatusAvailabilityActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this, "CHANNEL_ID")
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.posicionActivada))
                .setSmallIcon(R.drawable.car)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }

    private void initData() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
    }

    /*@RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotification() {
        Intent notificationIntent = new Intent(this, StatusAvailabilityActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notificacion = new Notification.Builder(this, createNotificationChannel())
                .setContentTitle("Servicio")
                .setContentText("Servicio posición activado")
                .setSmallIcon(R.drawable.car)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notificacion);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(){
        NotificationChannel chan = new NotificationChannel("my_service", "My Background Service", NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return "my_service";
    }*/



    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateLocationOne() {
        if (!AndyUtils.isNetworkAvailable(getApplicationContext()))
            return;
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = getLastKnownLocation();
            if(location != null) {

                //sendLocation(location.getLatitude()+"", location.getLongitude()+"");

                Log.d("PRUEBAAAA", location.getLongitude() + " y " + location.getLatitude());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }

    private boolean esPrimeraVez() {
        return hacerPeticion == 1;
    }

    private void sendLocation(String latitude, String longitude) {
        hacerPeticion =+ 1;
        if(esPrimeraVez()){
            Call<String> call = apiInterface.updateLocation(latitude, longitude, prefUtils.getIntValue(PrefKeys.ID, 0),
                    prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("PRUEBA", "EXITO");
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("PRUEBA", "FALLO");
                }
            });
        }else {
         hacerPeticion = 0;
        }
    }

    private void updateAvailabilityStatus(int status) {
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
                        prefUtils.setValue(PrefKeys.AVAILABLE_STATUS, data.optInt(APIConstants.Params.IS_AVAILABLE) == 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
            }
        });
    }

}
