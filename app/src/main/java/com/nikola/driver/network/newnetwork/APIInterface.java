package com.nikola.driver.network.newnetwork;

import com.nikola.driver.network.newnetwork.APIConstants.APIs;
import com.nikola.driver.network.newnetwork.APIConstants.Params;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

import static com.nikola.driver.network.newnetwork.APIConstants.APIs.ADD_CARDS;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.ADD_MONEY_TO_WALLET;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.CANCEL_REDEEM_REQUEST;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.CARDS;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.CHANGE_DEFAULT_PAYMENT_MODE;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.CHANGE_PASSWORD;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.DELETE_CARD;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.GET_PANIC_CONTACT;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.MAKE_DEFAULT_CARD;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.PROFILE;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.REDEEMS;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.REDEEM_REQUESTS;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.REFERRAL_CODE;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.REQUEST_STATUS_CHECK_NEW;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.SEND_MONEY_TO_REDEEM;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.SEND_PANIC_MESSAGE;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.SET_PANIC_CONTACT;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.WALLET;
import static com.nikola.driver.network.newnetwork.APIConstants.APIs.WALLET_PAYMENTS;

public interface APIInterface {


    @FormUrlEncoded
    @POST(APIs.LOGIN)
    Call<String> loginUser(@Field(Params.EMAIL) String email
            , @Field(Params.PASSWORD) String password
            , @Field(Params.LOGIN_BY) String loginBy
            , @Field(Params.DEVICE_TYPE) String deviceType
            , @Field(Params.DEVICE_TOKEN) String deviceToken
            , @Field(Params.TIMEZONE) String timeZone);

    @Multipart
    @POST(APIs.REGISTER)
    Call<String> register(@Part(Params.FIRSTNAME) String name
            , @Part(Params.LAST_NAME) String lastName
            , @Part(Params.EMAIL) String email
            , @Part(Params.PASSWORD) String password
            , @Part(Params.LOGIN_BY) String loginBy
            , @Part(Params.DEVICE_TYPE) String deviceType
            , @Part(Params.DEVICE_TOKEN) String deviceToken
            , @Part(Params.PHONE) String phone
            , @Part MultipartBody.Part picture
            , @Part(Params.TIMEZONE) String timeZone
            , @Part(Params.SERVICE_TYPE_ID) String serviceTypeId
            , @Part(Params.PLATE_NUMBER) String plateNumber
            , @Part(Params.COLOR) String color
            , @Part(Params.MODEL) String model
            , @Part(Params.COUNTRY_CODE) String countryCode
            , @Part MultipartBody.Part carpicture
            , @Part(Params.USERNAME) String username
            , @Part(Params.REFERRAL_CODE) String referral_code
           );

    @FormUrlEncoded
    @POST(APIs.FORGOT_PASSWORD)
    Call<String> forgotPassword(@Field(Params.EMAIL) String email);

    @FormUrlEncoded
    @POST(APIs.RECARGAR_SALDO)
    Call<String> recargarSaldo(
            @Field(Params.ID) int id,
            @Field(Params.MONTO) String monto,
            @Field(Params.NUMERO) String numero,
            @Field(Params.FECHA) String fecha

    );

    @FormUrlEncoded
    @POST(APIs.CONSULTAR_SALDO)
    Call<String> consultarSaldo(
            @Field(Params.ID) int id

    );

    @FormUrlEncoded
    @POST(APIs.LOGOUT)
    Call<String> logOutUser(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token);

    @FormUrlEncoded
    @POST(PROFILE)
    Call<String> profile(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token);

    @FormUrlEncoded
    @POST(REFERRAL_CODE)
    Call<String> getReferralCode(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
    );

    @Multipart
    @POST(APIs.UPDATE_PROFILE)
    Call<String> updateUserProfile(@Part(Params.ID) int id
            , @Part(Params.TOKEN) String token
            , @Part(Params.FIRSTNAME) String firstName
            , @Part(Params.LAST_NAME) String lastName
            , @Part(Params.EMAIL) String email
            , @Part MultipartBody.Part picture
            , @Part(Params.DEVICE_TYPE) String deviceType
            , @Part(Params.DEVICE_TOKEN) String deviceToken
            , @Part(Params.LOGIN_BY) String loginBy
            , @Part(Params.MOBILE) String phone
            , @Part(Params.TIMEZONE) String timeZone
            , @Part(Params.GENDER) String gender
            , @Part(Params.COUNTRY_CODE) String countryCode);

    @GET
    Call<String> getLocationBasedResponse(@Url String url);
    //Earnings
    @FormUrlEncoded
    @POST(APIs.DASHBOARD)
    Call<String> getDashboardData(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token);

    @FormUrlEncoded
    @POST(APIs.OV_EARNIGS)
    Call<String> getEarningsData(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token);

    //Chat
    @FormUrlEncoded
    @POST(APIs.MESSAGE_GET)
    Call<String> chatingMessage(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) int requestId);

    @FormUrlEncoded
    @POST(SEND_PANIC_MESSAGE)
    Call<String> sendPanicMessage(
            @Field(Params.ID) int id
            ,@Field(Params.REQUEST_ID) int requestId
            ,@Field(Params.ADDRESS) String actualAddress

    );

    @FormUrlEncoded
    @POST(SET_PANIC_CONTACT)
    Call<String> setPanicContact(
            @Field(Params.ID) int id
            ,@Field(Params.CONTACT_NUMBER) String contactNumber

    );

    @FormUrlEncoded
    @POST(GET_PANIC_CONTACT)
    Call<String> getPanicContact(
            @Field(Params.ID) int id


    );
    //History
    @FormUrlEncoded
    @POST(APIs.HISTORY)
    Call<String> history(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.SKIP) int skip);

    @FormUrlEncoded
    @POST(APIs.HISTORIAL_RECARGAS)
    Call<String> historialRecarga(
            @Field(Params.ID) int id);

    @FormUrlEncoded
    @POST(APIs.UPDATE_LOCATION)
    Call<String> updateLocation(@Field(Params.LATITUDE) String latitude,
                                @Field(Params.LONGITUDE) String longitude,
                                @Field(Params.ID) int id,
                                @Field(Params.TOKEN) String token);

    //Status Avavilability
    @FormUrlEncoded
    @POST(APIs.UPDATE_PROVIDER_AVAILABILITY)
    Call<String> updateAvailability(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.IS_AVAILABLE) int status);

    //Check availaboility
    @FormUrlEncoded
    @POST(APIs.PROVIDER_AVAILABILITY_CHECK)
    Call<String> checkAvailability(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token);


    //GEt Document
    @FormUrlEncoded
    @POST(APIs.GET_DOC)
    Call<String> getDoc(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token);

    //Upload document
    @Multipart
    @POST(APIs.UPLOAD_DOC)
    Call<String> uploadDoc(@Part(Params.ID) int id
            , @Part(Params.TOKEN) String token
            , @Part MultipartBody.Part documentImage
            , @Part(Params.DOCUMENT_ID) String documentId);

    //COD comfrim
    @FormUrlEncoded
    @POST(APIs.COD_CONFIRM)
    Call<String> codConfirm(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) String requestId);

    //Rate Url
    @FormUrlEncoded
    @POST(APIs.RATE_USER)
    Call<String> rateUser(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) int requestId
            , @Field(Params.RATING) int rating);

    //Check Request status
    @FormUrlEncoded
    @POST(APIs.CHECK_REQUEST_STATUS)
    Call<String> checkRequestStatus(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token);

    //Post Cancel Trip
    @FormUrlEncoded
    @POST(APIs.CANCEL_TRIP)
    Call<String> postCancelTrip(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) int requestId);

    //Ongoing Process
    //Provider started
    @FormUrlEncoded
    @POST(APIs.REQUEST_STARTED)
    Call<String> startRequest(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) int requestId);

    //Provider Arrived
    @FormUrlEncoded
    @POST(APIs.PROVIDER_ARRIVED)
    Call<String> notifyProviderArrived(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) int requestId);

    @FormUrlEncoded
    @POST(APIs.SERVICE_STARTED)
    Call<String> providerServiceStarted(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) int requestId);

    //Provider Accepted
    @FormUrlEncoded
    @POST(APIs.PROVIDER_ACCEPTED)
    Call<String> providerAccepted(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) String requestId);

    //Provider Rejected
    @FormUrlEncoded
    @POST(APIs.PROVIDER_REJECTED)
    Call<String> providerRejected(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) String requestId);

    //Provider Complete
    @FormUrlEncoded
    @POST(APIs.SERVICE_COMPLETED)
    Call<String> providerComplete(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) int requestId
            , @Field(Params.TIME) String time
            , @Field(Params.DISTANCE) String distance
            , @Field(Params.D_LATITUDE) double dLat
            , @Field(Params.D_LONGITUDE) double dLong
            , @Field(Params.D_ADDRESS) String dAddress);

    //Incoming request
    @FormUrlEncoded
    @POST(APIs.INCOMING_REQUEST_IN_PROGRESS)
    Call<String> incomingRequest(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token);

    //Ads Management
    @FormUrlEncoded
    @POST(APIs.ADS_MANAGEMENT)
    Call<String> manageAds(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token);

    // Get Direction
    @GET
    Call<String> getDirections(@Url String url);

    // Get Direction
    @GET
    Call<String> getDirectionsWay(@Url String url);

    //send Notifiaction
    @GET
    Call<String> sendNotification(@Url String url);

    //find distance and time
    @GET
    Call<String> findDistanceandTime(@Url String url);

    // Get Taxi Type
    @GET
    Call<String> getTaxiType(String taxiTypeUrl);


    @FormUrlEncoded
    @POST (APIs.SERVICES_LISTS)
    Call<String> servicesList(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token);


    @FormUrlEncoded
    @POST(APIs.SERVICE_LIST)
    Call<String> getServiceTypes(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token);

    @FormUrlEncoded
    @POST(CHANGE_PASSWORD)
    Call<String> changePassword(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.OLD_PASSWORD) String curPassword
            , @Field(Params.PASSWORD) String newPassword
            , @Field(Params.CONFIRM_PASSWORD) String newPasswordConfirm);

    @FormUrlEncoded
    @POST(APIConstants.APIs.CHAT_DETAILS)
    Call<String> getChatDetails(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) int bookingId
            , @Field(Params.USER_ID) String userId
            , @Field(Params.SKIP) int skip);

    @FormUrlEncoded
    @POST(APIs.CANCEL_REASON)
    Call<String> cancelReasonsList(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
    );
    @FormUrlEncoded
    @POST(APIs.CANCEL_ONGOING_RIDE)
    Call<String> cancelOngoingRide(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) int requestId
            , @Field(Params.REASON_ID) String reasonId
            , @Field(Params.CANCELLATION_REASON) String cancellationReason
    );

    @FormUrlEncoded
    @POST(APIs.REQUESTS_VIEW)
    Call<String> getRequestsView(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.REQUEST_ID) String requestId
    );

    @GET(APIs.STATIC_PAGES)
    Call<String> getStaticPages(@Query(Params.PAGE_TYPE) String pageType);

    @FormUrlEncoded
    @POST(WALLET)
    Call<String> getWalletData(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(WALLET_PAYMENTS)
    Call<String> getWalletTransactions(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.SKIP) int skip
    );

    @FormUrlEncoded
    @POST(ADD_MONEY_TO_WALLET)
    Call<String> addMoneyToWallet(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.AMOUNT) String amount
            , @Field(Params.PAYMENT_MODE) String paymentMode
    );

    @FormUrlEncoded
    @POST(CANCEL_REDEEM_REQUEST)
    Call<String> cancelRedeemRequest(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.PROVIDER_REDEEM_REQUEST_ID) String userRequestId
    );

    @FormUrlEncoded
    @POST(REDEEMS)
    Call<String> getRedeemsList(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(SEND_MONEY_TO_REDEEM)
    Call<String> sendMoneyForRedeem(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.AMOUNT) String amount
    );

    @FormUrlEncoded
    @POST(REDEEM_REQUESTS)
    Call<String> getAllRedeemRequests(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.SKIP) int skip
    );

    @FormUrlEncoded
    @POST(CARDS)
    Call<String> getAllCards(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(ADD_CARDS)
    Call<String> addCard(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.CARD_TOKEN) String cardToken
    );

    @FormUrlEncoded
    @POST(DELETE_CARD)
    Call<String> deleteCard(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.PROVIDER_CARD_ID) String cardId);


    @FormUrlEncoded
    @POST(MAKE_DEFAULT_CARD)
    Call<String> makeCardDefault(@Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
            , @Field(Params.PROVIDER_CARD_ID) String cardId);

    @FormUrlEncoded
    @POST(CHANGE_DEFAULT_PAYMENT_MODE)
    Call<String> changeDefaultPaymentMode(
            @Field(Params.ID) int id,
            @Field(Params.TOKEN) String token,
            @Field(Params.PAYMENT_MODE) String paymentMode
    );

    @FormUrlEncoded
    @POST(PROFILE)
    Call<String> getProfile(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
    );

    @FormUrlEncoded
    @POST(REQUEST_STATUS_CHECK_NEW)
    Call<String> requestStatusCheckNew(
            @Field(Params.ID) int id
            , @Field(Params.TOKEN) String token
    );

}
