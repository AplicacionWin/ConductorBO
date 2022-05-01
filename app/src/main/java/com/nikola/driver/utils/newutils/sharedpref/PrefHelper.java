package com.nikola.driver.utils.newutils.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

import com.nikola.driver.ui.activity.LoginActivity;
import com.nikola.driver.utils.Const;

import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.ABOUT;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.COUNTRY;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.CURRENCY;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.EMAIL;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.EMAIL_NOTIFICATIONS;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.FIRSTNAME;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.GENDER;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.ID;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.IS_LOGGED_IN;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.LAST_NAME;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.LOGIN_TYPE;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.PHONE;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.PICTURE;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.PLATE_NUMBER;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.PROVIDER_STATUS;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.PUSH_NOTIFICATIONS;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.REQUEST_ID;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.SESSION_TOKEN;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.NAME;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.DESCRIPTION;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.TIME_ZONE;
import static com.nikola.driver.utils.newutils.sharedpref.PrefKeys.COLOR;

public class PrefHelper {



    public static void setUserLoggedOut(Context context) {
        PrefUtils preferences = PrefUtils.getInstance(context);

        preferences.removeKey(ID);
        preferences.removeKey(NAME);
        preferences.removeKey(EMAIL);
        preferences.removeKey(PICTURE);
        preferences.removeKey(PHONE);
        preferences.removeKey(DESCRIPTION);
        preferences.removeKey(SESSION_TOKEN);
        preferences.removeKey(LOGIN_TYPE);
        preferences.removeKey(PROVIDER_STATUS);
        preferences.removeKey(TIME_ZONE);
        preferences.removeKey(COUNTRY);
        preferences.removeKey(CURRENCY);
        preferences.removeKey(PLATE_NUMBER);
        preferences.removeKey(COLOR);
    }

    public static void setUserLoggedIn(Context context, int id, String name, String firstName, String lastName, String email, String picture, String phone, String description ,
                                       String token, String loginType, int providerStatus, String timeZone, String country,
                                       int currencyCode, String plateNo, String color, String gender) {
        PrefUtils preferences = PrefUtils.getInstance(context);
        preferences.setValue(IS_LOGGED_IN, true);
        preferences.setValue(ID, id);
        preferences.setValue(NAME, name);
        preferences.setValue(FIRSTNAME, firstName);
        preferences.setValue(LAST_NAME, lastName);
        preferences.setValue(EMAIL, email);
        preferences.setValue(PICTURE, picture);
        preferences.setValue(PHONE, phone);
        preferences.setValue(DESCRIPTION, description);
        preferences.setValue(SESSION_TOKEN, token);
        preferences.setValue(LOGIN_TYPE, loginType);
        preferences.setValue(PROVIDER_STATUS,providerStatus);
        preferences.setValue(TIME_ZONE, timeZone);
        preferences.setValue(COUNTRY, country);
        preferences.setValue(CURRENCY, currencyCode);
        preferences.setValue(PLATE_NUMBER, plateNo);
        preferences.setValue(COLOR, color);
        preferences.setValue(GENDER, gender);


    }
    public void putRequestId( Context context,int reqId) {
        PrefUtils prefUtils =   PrefUtils.getInstance(context);
        prefUtils.setValue(REQUEST_ID,reqId);
    }

    public void putUserId(Context context,String userId) {
        PrefUtils prefUtils =   PrefUtils.getInstance(context);
        prefUtils.setValue(ID, userId);
    }


    public void putTrip_time(Context context,String trip_time) {
        PrefUtils prefUtils =   PrefUtils.getInstance(context);
        prefUtils.setValue(PrefKeys.TRIP_TIME, trip_time);
    }

    public void putTrip_Start_Lat(Context context,String trip_lat) {
        PrefUtils prefUtils =   PrefUtils.getInstance(context);
        prefUtils.setValue(PrefKeys.TRIPSTART_LAT, trip_lat);

    }
    public void putTrip_Start_Lon(Context context,String trip_lat) {
        PrefUtils prefUtils =   PrefUtils.getInstance(context);
        prefUtils.setValue(PrefKeys.TRIPSTART_LON, trip_lat);

    }
}
