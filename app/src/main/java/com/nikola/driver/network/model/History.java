package com.nikola.driver.network.model;

/**
 * Created by user on 1/20/2017.
 */

public class History {
    private String history_id,map_img,providerCommission, cancelReason;
    private String history_date, provider_name, history_picture, history_total, history_type, history_Sadd, history_Dadd;
    private String total_time,base_price,time_price,distance_travel,tax_price,distance_price,currency_unit,companyCommission;

    private String min_price,booking_fee,distance_unit;
    private String requestUniqueId, requestCreatedTime;

    private int request_ico_status;
    private String request_icon_status_text;

    public int getRequest_ico_status() {
        return request_ico_status;
    }

    public void setRequest_ico_status(int request_ico_status) {
        this.request_ico_status = request_ico_status;
    }

    public String getRequest_icon_status_text() {
        return request_icon_status_text;
    }

    public void setRequest_icon_status_text(String request_icon_status_text) {
        this.request_icon_status_text = request_icon_status_text;
    }

    private double longitude, latitude;

    public String getRequestCreatedTime() {
        return requestCreatedTime;
    }

    public void setRequestCreatedTime(String requestCreatedTime) {
        this.requestCreatedTime = requestCreatedTime;
    }

    public String getRequestUniqueId() {
        return requestUniqueId;
    }

    public void setRequestUniqueId(String requestUniqueId) {
        this.requestUniqueId = requestUniqueId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public String getHistory_id() {
        return history_id;
    }

    public void setHistory_id(String history_id) {
        this.history_id = history_id;
    }

    public String getHistory_date() {
        return history_date;
    }

    public void setHistory_date(String history_date) {
        this.history_date = history_date;
    }

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    public String getHistory_picture() {
        return history_picture;
    }

    public void setHistory_picture(String history_picture) {
        this.history_picture = history_picture;
    }

    public String getHistory_total() {
        return history_total;
    }

    public void setHistory_total(String history_total) {
        this.history_total = history_total;
    }

    public String getHistory_type() {
        return history_type;
    }

    public void setHistory_type(String history_type) {
        this.history_type = history_type;
    }

    public String getHistory_Sadd() {
        return history_Sadd;
    }

    public void setHistory_Sadd(String history_Sadd) {
        this.history_Sadd = history_Sadd;
    }

    public String getHistory_Dadd() {
        return history_Dadd;
    }

    public void setHistory_Dadd(String history_Dadd) {
        this.history_Dadd = history_Dadd;
    }

    public String getMap_img() {
        return map_img;
    }

    public void setMap_img(String map_img) {
        this.map_img = map_img;
    }

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public String getBase_price() {
        return base_price;
    }

    public void setBase_price(String base_price) {
        this.base_price = base_price;
    }

    public String getTime_price() {
        return time_price;
    }

    public void setTime_price(String time_price) {
        this.time_price = time_price;
    }

    public String getDistance_travel() {
        return distance_travel;
    }

    public void setDistance_travel(String distance_travel) {
        this.distance_travel = distance_travel;
    }

    public String getTax_price() {
        return tax_price;
    }

    public void setTax_price(String tax_price) {
        this.tax_price = tax_price;
    }

    public String getDistance_price() {
        return distance_price;
    }

    public void setDistance_price(String distance_price) {
        this.distance_price = distance_price;
    }

    public String getBooking_fee() {
        return booking_fee;
    }

    public void setBooking_fee(String booking_fee) {
        this.booking_fee = booking_fee;
    }

    public String getMin_price() {
        return min_price;
    }

    public void setMin_price(String min_price) {
        this.min_price = min_price;
    }

    public String getCurrency_unit() {
        return currency_unit;
    }

    public void setCurrency_unit(String currency_unit) {
        this.currency_unit = currency_unit;
    }

    public String getDistance_unit() {
        return distance_unit;
    }

    public void setDistance_unit(String distance_unit) {
        this.distance_unit = distance_unit;
    }

    public String getCompanyCommission() {
        return companyCommission;
    }

    public void setCompanyCommission(String companyCommission) {
        this.companyCommission = companyCommission;
    }

    public String getProviderCommission() {
        return providerCommission;
    }

    public void setProviderCommission(String providerCommission) {
        this.providerCommission = providerCommission;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
