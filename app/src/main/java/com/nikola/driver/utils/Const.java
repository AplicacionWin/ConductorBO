package com.nikola.driver.utils;

/**
 * Created by Mahesh on 8/5/2016.
 */
public class Const {

    public static final String WAYPOINTS ="waypoints" ;
    public static String PREF_NAME = "SMARCAR_PRERENCE";
    public static final int GET = 0;
    public static final int POST = 1;
    public static final int TIMEOUT = 30000;
    public static final int MAX_RETRY = 5;
    public static final float DEFAULT_BACKOFF_MULT = 1f;

    public static final int CHOOSE_PHOTO = 100;
    public static final int TAKE_PHOTO = 101;
    public static final String PROVIDER_REQUEST_STATUS = "provider_request_status";
    public static final String PROVIDER_INTENT_MESSAGE = "provider_intent_message";
    public static final String CARD = "card";
    public static final String CASH = "cod";


    //Provider status

    public static final int IS_PROVIDER_ACCEPTED = 1;
    public static final int IS_PROVIDER_STARTED = 2;
    public static final int IS_PROVIDER_ARRIVED = 3;
    public static final int IS_PROVIDER_SERVICE_STARTED = 4;
    public static final int IS_PROVIDER_SERVICE_COMPLETED = 5;
    public static final int IS_USER_RATED = 6;


    public static final String PROVIDER_STATUS = "provider_status";
    public static final String STATUS = "status";


    public static final int NO_REQUEST = -1;
    public static final long DELAY = 0;
    public static final long TIME_SCHEDULE = 5 * 1000;
    public static final long DELAY_OFFLINE = 15 * 60 * 1000;
    public static final long TIME_SCHEDULE_OFFLINE = 15 * 60 * 1000;

    public static final String PLACES_AUTOCOMPLETE_API_KEY = "AIzaSyBC_Ff-gjCriYGBVzZ6BMpJzTO1Hk7uMaY";
    public static final String GOOGLE_API_KEY = "AIzaSyBC_Ff-gjCriYGBVzZ6BMpJzTO1Hk7uMaY";
//    public static final String GOOGLE_API_KEY = "AIzaSyAh9OgcosLuybd7aEn9B4-huX-RLsxP4ok";

    //Fragments
    public static final String HOME_MAP_FRAGMENT = "home_map_fragment";
    public static final String REGISTER_FRAGMENT = "register_fragment";
    public static final String FORGOT_PASSWORD_FRAGMENT = "forgot_fragment";
    public static final String TRAVEL_MAP_FRAGMENT = "travel_map";
    public static final String FEEDBACK_FRAGMENT = "feedback_fragment";

    // no request

    public static final String DEVICE_TYPE = "android";
    public static final String DEVICE_TYPE_ANDROID = "android";
    public static final String SOCIAL_FACEBOOK = "facebook";
    public static final String SOCIAL_GOOGLE = "google";
    public static final String MANUAL = "manual";
    public static final String SOCIAL = "social";
    public static final String REQUEST_DETAIL = "requestDetails";


    // error code
    public static final int INVALID_TOKEN = 104;
    public static final int REQUEST_ID_NOT_FOUND = 408;
    public static final int INVALID_REQUEST_ID = 101;


    public static final String GOOGLE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";

    public class Params {
        public static final String ID = "id";
        public static final String TOKEN = "token";
        public static final String STATUS = "status";
        public static final String SOCIAL_ID = "social_unique_id";
        public static final String URL = "url";
        public static final String PICTURE = "picture";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String REPASSWORD = "confirm_password";
        public static final String FIRSTNAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String PHONE = "mobile";
        public static final String OTP = "otp";
        public static final String SSN = "ssn";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String ICON = "icon";
        public static final String DEVICE_TYPE = "device_type";
        public static final String LOGIN_BY = "login_by";
        public static final String CURRENCEY = "currency_code";
        public static final String LANGUAGE = "language";
        public static final String REQUEST_ID = "request_id";
        public static final String GENDER = "gender";
        public static final String COUNTRY = "country";
        public static final String TIMEZONE = "timezone";
        public static final String LATTITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String RATING = "rating";
        public static final String SENSOR = "sensor";
        public static final String ORIGINS = "origins";
        public static final String DESTINATION = "destinations";
        public static final String MODE = "mode";
        public static final String TIME = "time";
        public static final String DISTANCE = "distance";
        public static final String DOC_URL = "document_url";
        public static final String SUCCESS = "success";
        public static final String MODEL = "model";
        public static final String CAR_IMAGE = "car_image";
        public static final String COLOR = "color";
        public static final String PLATE_NUMBER = "plate_no";

        public static final String FORCE_CLOSE ="force_close" ;
        public static final String APP_VERSION ="app_version" ;
    }

    public class ServiceType {


        //public static final String HOST_URL = "http://216.24.255.252";// basic server
        public static final String HOST_URL = "http://216.24.255.252";// basic server
        public static final String SOCKET_URL = "http://216.24.255.252:3000?type=provider&id="; // socketUrl

        //public static final String HOST_URL = "http://167.172.97.206";// prod server
        //public static final String SOCKET_URL = "http://167.172.97.206:3000?type=provider&id="; // socketUrl

        public static final String BASE_URL = HOST_URL + "api/provider/";
        public static final String LOGIN = BASE_URL + "nuevoLogin";
        public static final String REGISTER = BASE_URL + "register";
        public static final String UPDATE_PROFILE = BASE_URL + "updateProfile";
        public static final String FORGOT_PASSWORD = BASE_URL + "forgotpassword";
        public static final String TAXI_TYPE = HOST_URL + "serviceList";
        public static final String UPDATE_LOCATION_URL = BASE_URL + "location_update";
        public static final String INCOMING_REQUEST_IN_PROGRESS_URL = BASE_URL + "incomingRequest";
        public static final String CHECK_REQUEST_STATUS_URL = BASE_URL + "requestStatusCheck";
        public static final String PROVIDER_ACCEPTED_URL = BASE_URL + "serviceAccept";
        public static final String PROVIDER_REJECTED_URL = BASE_URL + "serviceReject";
        public static final String PROVIDER_STARTED_URL = BASE_URL + "providerStarted";
        public static final String PROVIDER_ARRIVED_URL = BASE_URL + "arrived";
        public static final String PROVIDER_SERVICE_STARTED_URL = BASE_URL + "serviceStarted";
        public static final String PROVIDER_SERVICE_COMPLETED_URL = BASE_URL + "serviceCompleted";
        public static final String RATE_USER_URL = BASE_URL + "rateUser";
        public static final String COD_CONFIRM_URL = BASE_URL + "codPaidConfirmation";
        public static final String GET_CHECK_AVAILABLE_STATUS_URL = BASE_URL + "checkAvailableStatus?";
        public static final String POST_AVAILABILITY_STATUS_URL = BASE_URL + "availableUpdate";
        public static final String POST_CANCEL_TRIP_URL = BASE_URL + "cancelrequest";
        public static final String POST_HISTORY_URL = BASE_URL + "history";
        public static final String GET_DOC = BASE_URL + "documents?";
        public static final String UPLOAD_DOC = BASE_URL + "upload_documents";
        public static final String USER_MESSAGE_NOTIFY = BASE_URL + "message_notification?";
        public static final String UPDATETIME = BASE_URL + "update_timezone";
        public static final String ADVERTISEMENTS = BASE_URL + "adsManagement";
        public static final String EARNINGS = BASE_URL+"earnings";
        public static final String LOGOUT = BASE_URL + "logout";
        public static final String GET_VERSION = HOST_URL + "get_version";
        public static final String MESSAGE_GET = BASE_URL+"message/get";
    }

    // service codes
    public class ServiceCode {
        public static final int REGISTER = 1;
        public static final int LOGIN = 2;
        public static final int UPDATE_PROFILE = 3;
        public static final int FORGOT_PASSWORD = 4;
        public static final int GOOGLE_DIRECTION_API = 5;
        public static final int TAXI_TYPE = 6;
        public static final int UPDATE_LOCATION = 7;
        public static final int INCOMING_REQUEST = 8;
        public static final int CHECK_REQUEST_STATUS = 9;
        public static final int DRIVER_ACCEPTED = 10;
        public static final int DRIVER_REJECTED = 11;
        public static final int PROVIDER_STARTED = 12;
        public static final int PROVIDER_ARRIVED = 13;
        public static final int PROVIDER_SERVICE_STARTED = 14;
        public static final int PROVIDER_SERVICE_COMPLETED = 15;
        public static final int RATE_USER = 16;
        public static final int COD_CONFIRM = 17;
        public static final int GET_CHECK_AVAILABLE_STATUS = 18;
        public static final int POST_AVAILABILITY_STATUS = 19;
        public static final int GOOGLE_MATRIX = 20;
        public static final int POST_CANCEL_TRIP = 21;
        public static final int POST_HISTORY = 22;
        public static final int GET_DOC = 23;
        public static final int UPLOAD_DOC = 24;
        public static final int USER_MESSAGE_NOTIFY = 25;
        public static final int GOOGLE_DIRECTION_forcar_API = 26;
        public static final int UPDATETIME = 27;
        public static final int ADVERTISEMENTS = 28;
        public static final int EARNINGS =29 ;
        public static final int LOGOUT = 30;
        public static final int GET_VERSION = 31;
        public static final int MESSAGE_GET = 32;
    }


    // Placesurls
    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    public static final String TYPE_NEAR_BY = "/nearbysearch";
    public static final String OUT_JSON = "/json";
    public static boolean SEND_POSITION_DRIVER = false;
    // direction API
    public static final String DIRECTION_API_BASE = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String ORIGIN = "origin";
    public static final String DESTINATION = "destination";
    public static final String EXTANCTION = "sensor=false&mode=driving&alternatives=true&key=" + Const.GOOGLE_API_KEY;
}
