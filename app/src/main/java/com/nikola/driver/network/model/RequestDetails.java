package com.nikola.driver.network.model;

import java.io.Serializable;

/**
 * @author Mahesh
 */
@SuppressWarnings("serial")
public class RequestDetails implements Serializable {
    private int requestId;
    private String sourceAddress, currency_unit;
    private String destinationAddress, requesUniqueId;
    private String serviceType;
    private int timeLeft;
    private int jobStatus;
    private int onlinejobStatus;
    private long startTime;
    private String time, distance, unit, treatmentfee, medicinefee, date, total, bookingPrice,
            distanceCost, timecost, payment_type, referralBonus, promoBonus, user_id;
    private String clientName, clientProfile, clientLatitude, clientLongitude,
            clientPhoneNumber,
            pricePerDistance, pricePerTime;
    private float clientRating;
    private String sLatitude;
    private String sLongitude;
    private String dLatitude;
    private String dLongitude;
    private String userRating;
    private String status;
    private int providerStatus;
    private String typePicture;
    private String clientId;
    private String request_type, no_tolls,distance_unit,cancellationFee,isAdStop,isAddressChanged;
    private String adStopLatitude;
    private String adStopLongitude;
    private String adStopAddress;
    private String driverLat;
    private String driverLong;

    public String getRequesUniqueId() {
        return requesUniqueId;
    }

    public void setRequesUniqueId(String requesUniqueId) {
        this.requesUniqueId = requesUniqueId;
    }

    public String getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(String driverLat) {
        this.driverLat = driverLat;
    }

    public String getDriverLong() {
        return driverLong;
    }

    public void setDriverLong(String driverLong) {
        this.driverLong = driverLong;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypePicture() {
        return typePicture;
    }

    public void setTypePicture(String typePicture) {
        this.typePicture = typePicture;
    }

    public int getProviderStatus() {
        return providerStatus;
    }

    public void setProviderStatus(int providerStatus) {
        this.providerStatus = providerStatus;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getReferralBonus() {
        return referralBonus;
    }

    public void setReferralBonus(String referralBonus) {
        this.referralBonus = referralBonus;
    }

    public String getsLatitude() {
        return sLatitude;
    }

    public void setsLatitude(String sLatitude) {
        this.sLatitude = sLatitude;
    }

    public String getsLongitude() {
        return sLongitude;
    }

    public void setsLongitude(String sLongitude) {
        this.sLongitude = sLongitude;
    }

    public String getdLatitude() {
        return dLatitude;
    }

    public void setdLatitude(String dLatitude) {
        this.dLatitude = dLatitude;
    }

    public String getdLongitude() {
        return dLongitude;
    }

    public void setdLongitude(String dLongitude) {
        this.dLongitude = dLongitude;
    }

    public String getPromoBonus() {
        return promoBonus;
    }

    public void setPromoBonus(String promoBonus) {
        this.promoBonus = promoBonus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getBookingPrice() {
        return bookingPrice;
    }

    public void setBookingPrice(String basePrice) {
        this.bookingPrice = basePrice;
    }

    public String getDistanceCost() {
        return distanceCost;
    }

    public void setDistanceCost(String distanceCost) {
        this.distanceCost = distanceCost;
    }

    public String getTimecost() {
        return timecost;
    }

    public void setTimecost(String timecost) {
        this.timecost = timecost;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the jobStatus
     */
    public int getJobStatus() {
        return jobStatus;
    }

    /**
     * @param jobStatus the jobStatus to set
     */
    public void setJobStatus(int jobStatus) {
        this.jobStatus = jobStatus;
    }

    /**
     * @return the requestId
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    /**
     * @return the clientName
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * @param clientName the clientName to set
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * @return the clientProfile
     */
    public String getClientProfile() {
        return clientProfile;
    }

    /**
     * @param clientProfile the clientProfile to set
     */
    public void setClientProfile(String clientProfile) {
        this.clientProfile = clientProfile;
    }

    /**
     * @return the clientRating
     */
    public float getClientRating() {
        return clientRating;
    }

    /**
     * @param clientRating the clientRating to set
     */
    public void setClientRating(float clientRating) {
        this.clientRating = clientRating;
    }

    /**
     * @return the clientLatitude
     */
    public String getClientLatitude() {
        return clientLatitude;
    }

    /**
     * @param clientLatitude the clientLatitude to set
     */
    public void setClientLatitude(String clientLatitude) {
        this.clientLatitude = clientLatitude;
    }

    /**
     * @return the clientLongitude
     */
    public String getClientLongitude() {
        return clientLongitude;
    }

    /**
     * @param clientLongitude the clientLongitude to set
     */
    public void setClientLongitude(String clientLongitude) {
        this.clientLongitude = clientLongitude;
    }

    /**
     * @return the clientPhoneNumber
     */
    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    /**
     * @param clientPhoneNumber the clientPhoneNumber to set
     */
    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    /**
     * @return the timeLeft
     */
    public int getTimeLeft() {
        return timeLeft;
    }

    /**
     * @param timeLeft the timeLeft to set
     */
    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getTreatmentfee() {
        return treatmentfee;
    }

    public void setTreatmentfee(String treatmentfee) {
        this.treatmentfee = treatmentfee;
    }

    public String getPricePerDistance() {
        return pricePerDistance;
    }

    public void setPricePerDistance(String pricePerDistance) {
        this.pricePerDistance = pricePerDistance;
    }

    public String getPricePerTime() {
        return pricePerTime;
    }

    public void setPricePerTime(String pricePerTime) {
        this.pricePerTime = pricePerTime;
    }

    public String getMedicinefee() {
        return medicinefee;
    }

    public void setMedicinefee(String medicinefee) {
        this.medicinefee = medicinefee;
    }

    public int getOnlinejobStatus() {
        return onlinejobStatus;
    }

    public void setOnlinejobStatus(int onlinejobStatus) {
        this.onlinejobStatus = onlinejobStatus;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getNo_tolls() {
        return no_tolls;
    }

    public void setNo_tolls(String no_tolls) {
        this.no_tolls = no_tolls;
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

    public String getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(String cancellationFee) {
        this.cancellationFee = cancellationFee;
    }
    public String getAdStopAddress() {
        return adStopAddress;
    }

    public void setAdStopAddress(String adStopAddress) {
        this.adStopAddress = adStopAddress;
    }

    public String getAdStopLatitude() {
        return adStopLatitude;
    }

    public void setAdStopLatitude(String adStopLatitude) {
        this.adStopLatitude = adStopLatitude;
    }

    public String getAdStopLongitude() {
        return adStopLongitude;
    }

    public void setAdStopLongitude(String adStopLongitude) {
        this.adStopLongitude = adStopLongitude;
    }

    public String getIsAdStop() {
        return isAdStop;
    }

    public void setIsAdStop(String isAdStop) {
        this.isAdStop = isAdStop;
    }

    public String getIsAddressChanged() {
        return isAddressChanged;
    }

    public void setIsAddressChanged(String isAddressChanged) {
        this.isAddressChanged = isAddressChanged;
    }
}
