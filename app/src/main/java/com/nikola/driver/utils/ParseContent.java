package com.nikola.driver.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.nikola.driver.network.model.AdsList;
import com.nikola.driver.network.model.RequestDetails;
import com.nikola.driver.network.model.User;
import com.nikola.driver.realmController.RealmController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by user on 8/22/2016.
 */
public class ParseContent {
    public static final String IS_CANCELLED = "is_cancelled";
    private final String KEY_SUCCESS = "success";
    private final String KEY_ERROR = "error";
    private final String KEY_ERROR_CODE = "error_code";
    private Activity activity;
    private Realm mRealm;
    private PreferenceHelper preferenceHelper;

    public ParseContent(Activity activity) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        preferenceHelper = new PreferenceHelper(activity);
    }


    public boolean isSuccessWithStoreId(String response) {

        if (TextUtils.isEmpty(response))
            return false;
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(KEY_SUCCESS)) {
                preferenceHelper.putUserId(jsonObject
                        .getString(Const.Params.ID));
                preferenceHelper.putSessionToken(jsonObject
                        .getString(Const.Params.TOKEN));
                preferenceHelper.putUser_name(jsonObject
                        .getString(Const.Params.FIRSTNAME));
                preferenceHelper.putEmail(jsonObject
                        .optString(Const.Params.EMAIL));
                preferenceHelper.putPicture(jsonObject
                        .optString(Const.Params.PICTURE));
                if (jsonObject.has(Const.Params.LOGIN_BY)) {
                    preferenceHelper.putLoginBy(jsonObject
                            .getString(Const.Params.LOGIN_BY));
                }


                if (!preferenceHelper.getLoginBy().equalsIgnoreCase(
                        Const.MANUAL)) {
                    preferenceHelper.putSocialId(jsonObject
                            .getString(Const.Params.SOCIAL_ID));
                }

                return true;
            } else {

                return false;

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public User parseUserAndStoreToDb(String response) {
        User user = null;
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getBoolean(KEY_SUCCESS)) {
                user = new User();
                mRealm = Realm.getInstance(activity);
                RealmController.with(activity).clearAll();
                user.setId(jsonObject.getInt(Const.Params.ID));
                user.setEmail(jsonObject.optString(Const.Params.EMAIL));
                user.setFname(jsonObject.getString(Const.Params.FIRSTNAME));
                user.setLname(jsonObject.getString(Const.Params.LAST_NAME));
                user.setProfileurl(jsonObject.getString(Const.Params.PICTURE));
                user.setPhone(jsonObject.getString(Const.Params.PHONE));
                if (jsonObject.has(Const.Params.CURRENCEY)) {
                    user.setCurrency(jsonObject.getString(Const.Params.CURRENCEY));
                }
                if (jsonObject.has(Const.Params.GENDER)) {
                    user.setGender(jsonObject.getString(Const.Params.GENDER));

                }
                if (jsonObject.has(Const.Params.COUNTRY)) {
                    user.setCountry(jsonObject.getString(Const.Params.COUNTRY));
                }
                if (jsonObject.has(Const.Params.CURRENCEY)) {
                    preferenceHelper.putCurrency(jsonObject.getString(Const.Params.CURRENCEY));
                }


                mRealm.beginTransaction();
                mRealm.copyToRealm(user);
                mRealm.commitTransaction();


            } else {
                // AndyUtils.showToast(jsonObject.getString(KEY_ERROR),
                // activity);

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return user;
    }

    public RequestDetails parseRequestStatus(String response) {
        RequestDetails requestDetails = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            if (dataObject != null) {
                requestDetails = new RequestDetails();
                requestDetails.setCurrency_unit(jsonObject.optString("currency"));
                requestDetails.setClientId(dataObject.optString("user_id"));
                requestDetails.setRequest_type(dataObject.optString("request_status_type"));
                requestDetails.setClientName(dataObject.optString("user_name"));
                requestDetails.setClientProfile(dataObject.optString("user_picture"));
                requestDetails.setClientPhoneNumber(dataObject.optString("user_mobile"));
                requestDetails.setsLatitude(dataObject.optString("s_latitude"));
                requestDetails.setsLongitude(dataObject.optString("s_longitude"));
                requestDetails.setdLatitude(dataObject.optString("d_latitude"));
                requestDetails.setdLongitude(dataObject.optString("d_longitude"));
                requestDetails.setSourceAddress(dataObject.optString("s_address"));
                requestDetails.setDestinationAddress(dataObject.optString("d_address"));
                requestDetails.setServiceType(dataObject.optString("service_type_name"));
                requestDetails.setUserRating(dataObject.optString("user_rating"));
                requestDetails.setRequestId(dataObject.optInt("request_id"));
                requestDetails.setStatus(dataObject.optString("status"));
                requestDetails.setProviderStatus(dataObject.optInt("provider_status"));
                requestDetails.setAdStopAddress(dataObject.optString("adstop_address"));
                requestDetails.setIsAdStop(dataObject.optString("is_adstop"));
                requestDetails.setIsAddressChanged(dataObject.optString("is_address_changed"));
                requestDetails.setAdStopLatitude(dataObject.optString("adstop_latitude"));
                requestDetails.setAdStopLongitude(dataObject.optString("adstop_longitude"));

                return requestDetails;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestDetails;
    }

    public RequestDetails parseRequestArrayStatus(String response) {
        RequestDetails requestDetails = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataObjectArray = jsonObject.optJSONArray("data");
            if (dataObjectArray != null && dataObjectArray.length() > 0) {


                requestDetails = new RequestDetails();
                JSONObject dataObject = dataObjectArray.optJSONObject(0);
                requestDetails.setCurrency_unit(jsonObject.optString("currency"));
                requestDetails.setNo_tolls(jsonObject.optString("number_tolls"));
                requestDetails.setClientId(dataObject.optString("user_id"));
                new PreferenceHelper(activity).putClient_id(dataObject.optString("user_id"));
                new PreferenceHelper(activity).putRequestId(Integer.valueOf(dataObject.optString("request_id")));
                requestDetails.setClientName(dataObject.optString("user_name"));
                requestDetails.setClientProfile(dataObject.optString("user_picture"));
                requestDetails.setRequest_type(dataObject.optString("request_status_type"));
                requestDetails.setClientPhoneNumber(dataObject.optString("user_mobile"));
                requestDetails.setsLatitude(dataObject.optString("s_latitude"));
                requestDetails.setsLongitude(dataObject.optString("s_longitude"));
                requestDetails.setdLatitude(dataObject.optString("d_latitude"));
                requestDetails.setdLongitude(dataObject.optString("d_longitude"));
                requestDetails.setSourceAddress(dataObject.optString("s_address"));
                requestDetails.setDestinationAddress(dataObject.optString("d_address"));
                requestDetails.setServiceType(dataObject.optString("service_type_name"));
                requestDetails.setUserRating(dataObject.optString("user_rating"));
                requestDetails.setRequestId(dataObject.optInt("request_id"));
                requestDetails.setStatus(dataObject.optString("status"));
                requestDetails.setProviderStatus(dataObject.optInt("provider_status"));
                requestDetails.setTypePicture(dataObject.optString("type_picture"));

                requestDetails.setAdStopAddress(dataObject.optString("adstop_address"));
                requestDetails.setIsAdStop(dataObject.optString("is_adstop"));
                requestDetails.setIsAddressChanged(dataObject.optString("is_address_changed"));
                requestDetails.setAdStopLatitude(dataObject.optString("adstop_latitude"));
                requestDetails.setAdStopLongitude(dataObject.optString("adstop_longitude"));

            }

            JSONArray invoiceArray = jsonObject.optJSONArray("invoice");
            if (invoiceArray != null && invoiceArray.length() > 0) {
                JSONObject invoiceObject = invoiceArray.getJSONObject(0);
                requestDetails.setTotal(invoiceObject.optString("total"));
                requestDetails.setDistance(invoiceObject.optString("distance_travel"));
                requestDetails.setTime(invoiceObject.optString("total_time"));
                requestDetails.setDistance_unit(invoiceObject.optString("distance_unit"));
                requestDetails.setPayment_type(invoiceObject.optString("payment_mode"));
                requestDetails.setCancellationFee(invoiceObject.optString("cancellation_fine "));

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestDetails;
    }


    public List<AdsList> parseAdsList(JSONArray jsonArray) {
        List<AdsList> adsLists = null;
        Log.e("asher", "region array " + jsonArray);

        adsLists = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            //Creating a json object of the current index

            try {
                //getting json object from current index
                JSONObject obj = jsonArray.getJSONObject(i);
                //getting subCategories from json object
                AdsList details = new AdsList();
                details.setAdDescription(obj.optString("description"));
                details.setAdId(obj.optString("id"));
                details.setAdImage(obj.optString("picture"));
                details.setAdUrl(obj.optString("url"));
                adsLists.add(details);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Creating ListViewAdapter Object


        return adsLists;
    }


    public RequestDetails parseRequestArrayInvoice(String response) {
        RequestDetails requestDetails = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataObjectArray = jsonObject.getJSONArray("invoice");
            if (dataObjectArray != null) {
                JSONObject dataObject = dataObjectArray.getJSONObject(0);
                if (dataObject != null) {
                    requestDetails = new RequestDetails();
                    requestDetails.setCurrency_unit(jsonObject.optString("currency"));
                    requestDetails.setNo_tolls(jsonObject.optString("number_tolls"));
                    requestDetails.setRequest_type(jsonObject.optString("request_status_type"));
                    requestDetails.setClientId(dataObject.optString("user_id"));
                    requestDetails.setClientName(dataObject.optString("user_name"));
                    requestDetails.setClientProfile(dataObject.optString("picture"));
                    requestDetails.setClientPhoneNumber(dataObject.optString("user_mobile"));
                    requestDetails.setRequestId(jsonObject.optInt("request_id"));
                    requestDetails.setTotal(dataObject.optString("total"));
                    requestDetails.setdLatitude(dataObject.optString("d_latitude"));
                    requestDetails.setdLongitude(dataObject.optString("d_longitude"));
                    requestDetails.setProviderStatus(jsonObject.optInt("provider_status"));
                    requestDetails.setTypePicture(dataObject.optString("type_picture"));
                    requestDetails.setDistance(dataObject.optString("distance_travel"));
                    requestDetails.setTime(dataObject.optString("total_time"));
                    requestDetails.setDistance_unit(dataObject.optString("distance_unit"));
                    requestDetails.setPayment_type(dataObject.optString("payment_mode"));
                    requestDetails.setCancellationFee(dataObject.optString("cancellation_fine "));
                    return requestDetails;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestDetails;
    }


    public boolean isSuccess(String response) {
        if (TextUtils.isEmpty(response))
            return false;
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(KEY_SUCCESS)) {
                return true;
            } else {

                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getActiveStatus(String response) {
        int active = 0;
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (!jsonObject.optString("active").equals("") && jsonObject.optString("active") != null) {
                active = Integer.parseInt(jsonObject.optString("active"));
            }
            return active;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return active;
    }

    public boolean parseIsApproved(String response) {
        if (TextUtils.isEmpty(response)) {
            return false;
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(KEY_SUCCESS)) {
                if (jsonObject.getString("is_approved")
                        .equals("1")) {
                    return true;
                }
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getErrorCode(String response) {
        if (TextUtils.isEmpty(response))
            return 0;
        try {

            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getInt(KEY_ERROR_CODE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int parseRequestInProgress(String response) {
        if (TextUtils.isEmpty(response)) {
            return Const.NO_REQUEST;
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(KEY_SUCCESS)) {
                JSONArray jarray = jsonObject.optJSONArray("data");
                if (jarray != null && jarray.length() > 0) {
                    JSONObject Jobj = jarray.getJSONObject(0);
                    if (Jobj.has("request_id")) {
                        int requestId = Jobj
                                .getInt(Const.Params.REQUEST_ID);

                        return requestId;
                    }
                } else {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return Const.NO_REQUEST;
    }


}
