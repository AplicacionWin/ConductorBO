package com.nikola.driver.network.newnetwork;

import android.text.TextUtils;

import com.nikola.driver.network.model.ChatObject;
import com.nikola.driver.network.model.HistorialRecarga;
import com.nikola.driver.network.model.History;
import com.nikola.driver.network.model.RequestDetails;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParserUtils {


    public static ArrayList<HistorialRecarga> ParseHistorialArrayList(JSONArray data) {
        ArrayList<HistorialRecarga> tripItems = new ArrayList<>();
        if (data == null)
            return tripItems;
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.optJSONObject(i);
            HistorialRecarga tripItem = parseHistorialItem(item);
            if (tripItem != null)
                tripItems.add(tripItem);
        }
        return tripItems;
    }

    private static HistorialRecarga parseHistorialItem(JSONObject obj) {
        HistorialRecarga his = new HistorialRecarga();
        if (obj == null)
            return null;
        his.setMonto(obj.optString("monto"));
        his.setEstado(obj.optString("estado"));
        his.setFecha(obj.optString("fecha"));
        his.setFechaAprobacion(obj.optString("fechaAprobacion"));
        his.setNumero(obj.optString("numero"));
        his.setFechaIngreso(obj.optString("fechaIngresoSistema"));
        return his;
    }





    public static ArrayList<History> ParseHistoryArrayList(JSONArray data) {
        ArrayList<History> tripItems = new ArrayList<>();
        if (data == null)
            return tripItems;
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.optJSONObject(i);
            History tripItem = parseHistoryItem(item);
            if (tripItem != null)
                tripItems.add(tripItem);
        }
        return tripItems;
    }

    private static History parseHistoryItem(JSONObject obj) {
        History his = new History();
        if (obj == null)
            return null;
        his.setHistory_id(obj.optString(Params.REQUEST_ID));
        his.setRequestUniqueId(obj.optString(Params.REQUEST_UNIQUE_ID));
        his.setHistory_Dadd(obj.optString(Params.D_ADDRESS));
        his.setHistory_Sadd(obj.optString(Params.S_ADDRESS));
        his.setHistory_date(obj.optString(Params.DATE));
        his.setProvider_name(obj.optString(Params.PROVIDER_NAME));
        his.setHistory_type(obj.optString(Params.TAXI_NAME));
        his.setHistory_total(obj.optString(Params.TOTAL));
        his.setHistory_picture(obj.optString(Params.PICTURE));
        his.setMap_img(obj.optString(Params.PICTURE));
        his.setBase_price(obj.optString(Params.BASE_PRICE));
        his.setDistance_travel(obj.optString(Params.DISTANCE_TRAVEL));
        his.setTotal_time(obj.optString(Params.TOTAL_TIME));
        his.setTax_price(obj.optString(Params.TAX_PRICE));
        his.setTime_price(obj.optString(Params.TIME_PRICE));
        his.setDistance_price(obj.optString(Params.DISTANCE_PRICE));
        his.setMin_price(obj.optString(Params.MIN_PRICE));
        his.setBooking_fee(obj.optString(Params.BOOKING_FEE));
        his.setCurrency_unit(obj.optString(Params.CURRENCEY));
        his.setDistance_unit(obj.optString(Params.DISTANCE_UNIT));
        his.setCompanyCommission(obj.optString(Params.COMPANY_EARNINGS));
        his.setProviderCommission(obj.optString(Params.PORVIDER_EARNINGS));
        his.setLatitude(obj.optDouble(Params.LATITUDE));
        his.setLongitude(obj.optDouble(Params.LONGITUDE));
        his.setRequestCreatedTime(obj.optString(Params.REQUEST_CREATED_TIME));
        his.setRequest_ico_status(obj.optInt(Params.REQUEST_STAUS_ICON));
        his.setRequest_icon_status_text(obj.optString(Params.REQUEST_STAUS_ICON_TEXT));
        his.setCancelReason(obj.optString(Params.CANCELLATION_REASON));
        return his;
    }

    public int getActiveStatus(String response) {
        int active = 0;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.optJSONObject(Params.DATA);
            if (!data.optString(Params.IS_AVAILABLE).equals("") && data.optString(Params.IS_AVAILABLE) != null) {
                active = Integer.parseInt(data.optString(Params.IS_AVAILABLE));
            }
            return active;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return active;
    }

    //    TODO: Change the datas...
    public static RequestDetails parseRequestStatus(String response) {
        RequestDetails requestDetails = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.optJSONObject(Params.DATA);
            JSONArray jsonArray = data.optJSONArray(Params.DATA);
            JSONObject object = jsonArray.optJSONObject(0);
            requestDetails = new RequestDetails();
            if (object != null) {
                requestDetails.setRequestId(object.optInt("request_id"));
                requestDetails.setCurrency_unit(object.optString("currency"));
                requestDetails.setClientId(object.optString("user_id"));
                requestDetails.setRequest_type(object.optString("request_status_type"));
                requestDetails.setClientName(object.optString("user_name"));
                requestDetails.setClientProfile(object.optString("user_picture"));
                requestDetails.setClientPhoneNumber(object.optString("user_mobile_formatted"));
                requestDetails.setsLatitude(object.optString("s_latitude"));
                requestDetails.setsLongitude(object.optString("s_longitude"));
                requestDetails.setdLatitude(object.optString("d_latitude"));
                requestDetails.setdLongitude(object.optString("d_longitude"));
                requestDetails.setSourceAddress(object.optString("s_address"));
                requestDetails.setDestinationAddress(object.optString("d_address"));
                requestDetails.setServiceType(object.optString("service_type_name"));
                requestDetails.setUserRating(object.optString("user_rating"));
                requestDetails.setStatus(object.optString("status"));
                requestDetails.setProviderStatus(object.optInt("provider_status"));
                requestDetails.setAdStopAddress(object.optString("adstop_address"));
                requestDetails.setIsAdStop(object.optString("is_adstop"));
                requestDetails.setIsAddressChanged(object.optString("is_address_changed"));
                requestDetails.setAdStopLatitude(object.optString("adstop_latitude"));
                requestDetails.setAdStopLongitude(object.optString("adstop_longitude"));
                requestDetails.setTypePicture(object.optString("type_picture"));
                requestDetails.setClientPhoneNumber(object.optString("user_mobile_formatted"));
                requestDetails.setDriverLat(object.optString("driver_latitude"));
                requestDetails.setDriverLong(object.optString("driver_longitude"));
                requestDetails.setRequesUniqueId(object.optString("request_unique_id"));

                JSONArray invoiceArray = data.optJSONArray("invoice");
                if (invoiceArray != null && invoiceArray.length()>0) {
                    JSONObject invoiceObject = invoiceArray.getJSONObject(0);
                    requestDetails.setTotal(invoiceObject.optString("total"));
                    requestDetails.setDistance(invoiceObject.optString("distance_travel"));
                    requestDetails.setTime(invoiceObject.optString("total_time"));
                    requestDetails.setDistance_unit(invoiceObject.optString("distance_unit"));
                    requestDetails.setPayment_type(invoiceObject.optString("payment_mode"));
                    requestDetails.setCancellationFee(invoiceObject.optString("cancellation_fine "));

                }
            } else {
                requestDetails.setProviderStatus(APIConstants.NO_REQUEST);
            }
            return requestDetails;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestDetails;
    }

    public static RequestDetails parseAcceptedRequest(String response) {
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        RequestDetails requestDetail = new RequestDetails();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.optJSONObject(Params.DATA);
            requestDetail = new RequestDetails();
            requestDetail.setCurrency_unit(jsonObject.optString("currency"));
            requestDetail.setClientId(data.optString("user_id"));
            requestDetail.setRequest_type(data.optString("request_status_type"));
            requestDetail.setClientName(data.optString("user_name"));
            requestDetail.setClientProfile(data.optString("user_picture"));
            requestDetail.setClientPhoneNumber(data.optString("user_mobile_formatted"));
            requestDetail.setsLatitude(data.optString("s_latitude"));
            requestDetail.setsLongitude(data.optString("s_longitude"));
            requestDetail.setdLatitude(data.optString("d_latitude"));
            requestDetail.setdLongitude(data.optString("d_longitude"));
            requestDetail.setSourceAddress(data.optString("s_address"));
            requestDetail.setDestinationAddress(data.optString("d_address"));
            requestDetail.setServiceType(data.optString("service_type_name"));
            requestDetail.setUserRating(data.optString("user_rating"));
            requestDetail.setRequestId(data.optInt("request_id"));
            requestDetail.setStatus(data.optString("status"));
            requestDetail.setProviderStatus(data.optInt("provider_status"));
            requestDetail.setAdStopAddress(data.optString("adstop_address"));
            requestDetail.setIsAdStop(data.optString("is_adstop"));
            requestDetail.setIsAddressChanged(data.optString("is_address_changed"));
            requestDetail.setAdStopLatitude(data.optString("adstop_latitude"));
            requestDetail.setAdStopLongitude(data.optString("adstop_longitude"));
            requestDetail.setRequesUniqueId(data.optString("request_unique_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestDetail;
    }

    public static RequestDetails parseTripCompleted(String response) {
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        RequestDetails requestDetail = new RequestDetails();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.optJSONObject(Params.DATA);
            JSONArray invoiceArray = data.optJSONArray("invoice");
            if (invoiceArray != null && invoiceArray.length() > 0) {
                JSONObject invoiceObject = invoiceArray.getJSONObject(0);
                requestDetail.setTotal(invoiceObject.optString("total"));
                requestDetail.setDistance(invoiceObject.optString("distance_travel"));
                requestDetail.setTime(invoiceObject.optString("total_time"));
                requestDetail.setDistance_unit(invoiceObject.optString("distance_unit"));
                requestDetail.setPayment_type(invoiceObject.optString("payment_mode"));
                requestDetail.setCancellationFee(invoiceObject.optString("cancellation_fine "));
                requestDetail.setCurrency_unit(jsonObject.optString("currency"));
                requestDetail.setClientId(data.optString("user_id"));
                requestDetail.setRequest_type(data.optString("request_status_type"));
                requestDetail.setClientName(data.optString("user_name"));
                requestDetail.setClientProfile(data.optString("user_picture"));
                requestDetail.setClientPhoneNumber(data.optString("user_mobile_formatted"));
                requestDetail.setsLatitude(data.optString("s_latitude"));
                requestDetail.setsLongitude(data.optString("s_longitude"));
                requestDetail.setdLatitude(data.optString("d_latitude"));
                requestDetail.setdLongitude(data.optString("d_longitude"));
                requestDetail.setSourceAddress(data.optString("s_address"));
                requestDetail.setDestinationAddress(data.optString("d_address"));
                requestDetail.setServiceType(data.optString("service_type_name"));
                requestDetail.setUserRating(data.optString("user_rating"));
                requestDetail.setRequestId(data.optInt("request_id"));
                requestDetail.setStatus(data.optString("status"));
                requestDetail.setProviderStatus(data.optInt("provider_status"));
                requestDetail.setAdStopAddress(data.optString("adstop_address"));
                requestDetail.setIsAdStop(data.optString("is_adstop"));
                requestDetail.setIsAddressChanged(data.optString("is_address_changed"));
                requestDetail.setAdStopLatitude(data.optString("adstop_latitude"));
                requestDetail.setAdStopLongitude(data.optString("adstop_longitude"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestDetail;
    }

    public static ArrayList<ChatObject> parseChatObjects(JSONArray data) {
        ArrayList<ChatObject> chatMessages = new ArrayList<>();
        if (data == null)
            return chatMessages;
        for (int i = data.length(); i >= 0; i--) {
            JSONObject item = data.optJSONObject(i);
            ChatObject chatMessage = parseChatObject(item);
            if (chatMessage != null)
                chatMessages.add(chatMessage);
        }
        return chatMessages;
    }

    private static ChatObject parseChatObject(JSONObject item) {
        ChatObject chatMessage = new ChatObject();
        if (item == null)
            return null;
        chatMessage.setBookingId(item.optInt(Params.REQUEST_ID));
        chatMessage.setProviderId(item.optInt(Params.USER_ID));
        chatMessage.setPersonImage(item.optString(Params.USER_PICTURE));
        chatMessage.setMyText(item.optString(Params.TYPE).equals(APIConstants.Constants.ChatMessageType.PROVIDER_TO_USER));
        chatMessage.setMessageText(item.optString(Params.MESSAGE));
        chatMessage.setMessageTime(item.optString(Params.UPDATED_AT));
        return chatMessage;
    }
}
