package com.nikola.driver.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;


public class PreferenceHelper {

    private SharedPreferences app_prefs;
    private final String USER_ID = "user_id";
    private final String CLIENT_ID = "client_id";
    private final String EMAIL = "email";
    private final String PASSWORD = "password";
    private final String PICTURE = "picture";
    private final String DEVICE_TOKEN = "device_token";
    private final String SESSION_TOKEN = "session_token";
    private final String LOGIN_BY = "login_by";
    private final String SOCIAL_ID = "social_id";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PRE_LOAD = "preLoad";
    private final String REQUEST_ID = "request_id";
    private final String NAME = "name";
    private final String REQ_TIME = "req_time";
    private final String ACCEPT_TIME = "accept_time";
    private final String CURRENT_TIME = "current_time";
    private final String CURRENCY = "currency";
    private final String LANGUAGE = "language";
    private final String TRIPSTART_LAT = "trip_lat";
    private final String TRIPSTART_LAN = "trip_lan";
    private final String TRIP_TIME = "accept_time";


    private Context context;

    public PreferenceHelper(Context context) {
        app_prefs = context.getSharedPreferences(Const.PREF_NAME,
                Context.MODE_PRIVATE);
        this.context = context;
    }


    public void putUserId(String userId) {
        Editor edit = app_prefs.edit();
        edit.putString(USER_ID, userId);
        edit.commit();
    }


    public String getUserId() {
        return app_prefs.getString(USER_ID, null);

    }
    public void putUser_name(String name) {
        Editor edit = app_prefs.edit();
        edit.putString(NAME, name);
        edit.commit();
    }

    public String getUser_name() {
        return app_prefs.getString(NAME, "");
    }


    public void putEmail(String email) {
        Editor edit = app_prefs.edit();
        edit.putString(EMAIL, email);
        edit.commit();
    }

    public String getEmail() {
        return app_prefs.getString(EMAIL, null);
    }

    public void putPicture(String picture) {
        Editor edit = app_prefs.edit();
        edit.putString(PICTURE, picture);
        edit.commit();
    }

    public void putRequestId(int reqId) {
        Editor edit = app_prefs.edit();
        edit.putInt(REQUEST_ID, reqId);
        edit.commit();
    }

    public int getRequestId() {
        return app_prefs.getInt(REQUEST_ID, Const.NO_REQUEST);
    }


    public String getPicture() {
        return app_prefs.getString(PICTURE, null);
    }

    public void putPassword(String password) {
        Editor edit = app_prefs.edit();
        edit.putString(PASSWORD, password);
        edit.commit();
    }

    public String getPassword() {
        return app_prefs.getString(PASSWORD, null);
    }

    public void putSocialId(String id) {
        Editor edit = app_prefs.edit();
        edit.putString(SOCIAL_ID, id);
        edit.commit();
    }

    public String getSocialId() {
        return app_prefs.getString(SOCIAL_ID, null);
    }


    public void putDeviceToken(String deviceToken) {
        Editor edit = app_prefs.edit();
        edit.putString(DEVICE_TOKEN, deviceToken);
        edit.commit();
    }

    public String getDeviceToken() {
        return app_prefs.getString(DEVICE_TOKEN, null);

    }



    public void putReq_time(long req_time) {
        Editor edit = app_prefs.edit();
        edit.putLong(REQ_TIME, req_time);
        edit.commit();
    }

    public long getReq_time() {
        return app_prefs.getLong(REQ_TIME, SystemClock.uptimeMillis());

    }


    public void putClient_id(String client_id) {
        Editor edit = app_prefs.edit();
        edit.putString(CLIENT_ID, client_id);
        edit.commit();
    }

    public String getClient_id() {
        return app_prefs.getString(CLIENT_ID, "");

    }

    public void putSessionToken(String sessionToken) {
        Editor edit = app_prefs.edit();
        edit.putString(SESSION_TOKEN, sessionToken);
        edit.commit();
    }

    public String getSessionToken() {
        return app_prefs.getString(SESSION_TOKEN, null);

    }

    public void putAccept_time(long accept_time) {
        Editor edit = app_prefs.edit();
        edit.putLong(ACCEPT_TIME, accept_time);
        edit.commit();
    }

    public long getAccept_time() {
        return app_prefs.getLong(ACCEPT_TIME, 0L);

    }

    public void putCurrent_time(long accept_time) {
        Editor edit = app_prefs.edit();
        edit.putLong(CURRENT_TIME, accept_time);
        edit.commit();
    }

    public long getCurent_time() {
        return app_prefs.getLong(CURRENT_TIME, 0L);

    }

    public String getTrip_Start_Lat() {
        return app_prefs.getString(TRIPSTART_LAT, "");

    }

    public void putTrip_Start_Lat(String trip_lat) {
        Editor edit = app_prefs.edit();
        edit.putString(TRIPSTART_LAT, trip_lat);
        edit.commit();
    }
    public String getTrip_Start_Lan() {
        return app_prefs.getString(TRIPSTART_LAN, "");

    }

    public void putTrip_Start_Lan(String trip_lan) {
        Editor edit = app_prefs.edit();
        edit.putString(TRIPSTART_LAN, trip_lan);
        edit.commit();
    }

    public void putTrip_time(long trip_time) {
        Editor edit = app_prefs.edit();
        edit.putLong(TRIP_TIME, trip_time);
        edit.commit();
    }
    public long getTrip_time() {
        return app_prefs.getLong(TRIP_TIME, 0L);

    }

    public void putLoginBy(String loginBy) {
        Editor edit = app_prefs.edit();
        edit.putString(LOGIN_BY, loginBy);
        edit.commit();
    }

    public String getLoginBy() {
        return app_prefs.getString(LOGIN_BY, Const.MANUAL);
    }

    public void putRegisterationID(String RegID) {
        Editor edit = app_prefs.edit();
        edit.putString(PROPERTY_REG_ID, RegID);
        edit.apply();
    }

    public String getRegistrationID() {
        return app_prefs.getString(PROPERTY_REG_ID, "");
    }


    public void putAppVersion(int version) {
        Editor edit = app_prefs.edit();
        edit.putInt(PROPERTY_APP_VERSION, version);
        edit.apply();
    }

    public int getAppVersion() {
        return app_prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
    }

    public void setPreLoad(boolean totalTime) {

        app_prefs
                .edit()
                .putBoolean(PRE_LOAD, totalTime)
                .apply();
    }

    public boolean getPreLoad() {
        return app_prefs.getBoolean(PRE_LOAD, false);
    }

    public void putCurrency(String currency) {
        Editor edit = app_prefs.edit();
        edit.putString(CURRENCY, currency);
        edit.commit();
    }

    public String getCurrency() {
        return app_prefs.getString(CURRENCY, "");

    }

    public void putLanguage(String language) {
        Editor edit = app_prefs.edit();
        edit.putString(LANGUAGE, language);
        edit.commit();
    }


    public String getLanguage() {
        return app_prefs.getString(LANGUAGE, "");
    }

    public void clearRequestData() {
        putRequestId(Const.NO_REQUEST);
        putReq_time(SystemClock.uptimeMillis());
        putTrip_time(0L);
        putAccept_time(0L);
        putCurrent_time(0L);
        putClient_id("");
        putTrip_Start_Lan("");
        putTrip_Start_Lat("");
    }

    public void Logout() {
        putUserId(null);
        putSessionToken(null);
        putSocialId(null);
        putLoginBy(Const.MANUAL);
    }

}


