package com.nikola.driver.network.newnetwork;

public class APIConstants {

    public static final String HOME_FRAGMENT = "homeFragment";
    public static final String ONGOING_FRAGMENT = "ongoingFragment";
    public static final int NO_REQUEST = -1;
    public static boolean isMediaPlaying= false;

    public APIConstants() {

    }

    public static class PushNotificationStatus {
        public static final String PUSH_NOTIFICATION_REDIRECT_HOME = "1";

        public static final String PUSH_NOTIFICATION_REDIRECT_REQUESTS = "2";

        public static final String PUSH_NOTIFICATION_REDIRECT_REQUEST_VIEW = "3";

        public static final String PUSH_NOTIFICATION_REDIRECT_REQUEST_ONGOING = "4";

        public static final String PUSH_NOTIFICATION_REDIRECT_CHAT = "5";
    }

    //Constants
    public static class Constants {
        public static final String MANUAL_LOGIN = "manual";
        public static final String GOOGLE_LOGIN = "google";
        public static final String FACEBOOK_LOGIN = "facebook";
        public static final String ANDROID = "android";
        public static final String SUCCESS = "success";
        public static final String ERROR = "error";
        public static final String TRUE = "true";
        public static final String DATA = "data";
        public static final String FALSE = "false";
        public static final String REQUEST_ACCEPT = "REQUEST_ACCEPT";
        public static final String REQUEST_CANCEL = "REQUEST_CANCEL";

        public static final String PROVIDER_REQUEST_STATUS = "provider_request_status";
        public static final String PROVIDER_INTENT_MESSAGE = "provider_intent_message";


        //Provider Status

        public static final int IS_PROVIDER_ACCEPTED = 1;
        public static final int IS_PROVIDER_STARTED = 2;
        public static final int IS_PROVIDER_ARRIVED = 3;
        public static final int IS_PROVIDER_SERVICE_STARTED = 4;
        public static final int IS_PROVIDER_SERVICE_COMPLETED = 5;
        public static final int IS_USER_RATED = 6;

        public static final String PROVIDER_STATUS = "provider_status";

        // error code
        public static final int INVALID_TOKEN = 104;
        public static final int REQUEST_ID_NOT_FOUND = 408;
        public static final int INVALID_REQUEST_ID = 101;

        //Fragments
        public static final String HOME_MAP_FRAGMENT = "home_map_fragment";
        public static final String REGISTER_FRAGMENT = "register_fragment";
        public static final String FORGOT_PASSWORD_FRAGMENT = "forgot_fragment";
        public static final String TRAVEL_MAP_FRAGMENT = "travel_map";
        public static final String FEEDBACK_FRAGMENT = "feedback_fragment";

        public static final String REQUEST_DETAIL = "requestDetails";
        public static final String OK = "OK";
        public static final String ANDRIOD = "android";
        public static final String GOOGLE_API_KEY = "AIzaSyBC_Ff-gjCriYGBVzZ6BMpJzTO1Hk7uMaY";
        public static final String STATUS = "status";

        public class ChatMessageType {
            public static final String USER_TO_PROVIDER = "up";
            public static final String PROVIDER_TO_USER = "pu";
            public static final String HOST_TO_USER = "hu";
            public static final String USER_TO_HOST = "uh";
        }
        private Constants(){
        }
    }

    //URLs
    public static class URLs {

        public static final String HOST_URL = "http://ecuador.aplicacionwin.com/";
        public static final String SOCKET_URL = "http://ecuador.aplicacionwin.com:3000/";

        public static final String BASE_URL = HOST_URL;
        public static final String DISTANCE_LOCATION_API = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="; //TEST
        public static final String PLANILLA_VIAJES = "http://test.co.conductores.wintecnologies.com/planilla-viaje/create-cond"; //TEST
        public static final String LISTA_PLANILLA_VIAJES = "http://test.co.conductores.wintecnologies.com/planilla-viaje/";
        public static final String WEBVIEW = "http://216.24.255.252/";

        /*public static final String PLANILLA_VIAJES = "http://co.conductores.wintecnologies.com/planilla-viaje/create-condro"; //prod
        public static final String LISTA_PLANILLA_VIAJES = "http://co.conductores.wintecnologies.com/planilla-viaje/id_provider"; //prod
        public static final String HOST_URL = "http://167.172.97.206/";
        public static final String SOCKET_URL = "http://167.172.97.206:3000/";
        public static final String WEBVIEW = "http://167.172.97.206/";*/

        private URLs() {

        }
    }

    //Error Codes
    public static class ErrorCodes {
        public static final int TOKEN_EXPIRED = 1003;
        public static final int USER_DOESNT_EXIST = 1002;
        public static final int INVALID_TOKEN = 1004;
        public static final int CARD_NOT_ADDED = 1005;
        public static final int ID_OR_TOKEN_MISSING = 1006;
        public static final int VERIFY_USER = 1000;
        public static final int REDIRECT_PAYMENTS = 111;

        private ErrorCodes() {
        }
    }

    //APIs
    public static class APIs {
        public static final String API_STR = "api/provider/";

        //public static final String GET_VERSION = HOST_URL + "get_version";
        // public static final String BASE_URL = HOST_URL + "providerApi/";
        public static final String LOGIN = API_STR + "login";
        public static final String REGISTER = API_STR + "register";
        public static final String PROFILE = API_STR + "profile";
        public static final String REFERRAL_CODE = API_STR + "referral_codes";
        public static final String UPDATE_PROFILE = API_STR + "update_profile";

        public static final String FORGOT_PASSWORD = API_STR + "forgot_password";
        public static final String RECARGAR_SALDO = API_STR + "registrar-recarga";
        public static final String CONSULTAR_SALDO = API_STR + "saldo-recarga";
        public static final String CHANGE_PASSWORD = API_STR + "change_password";
        public static final String DELETE_ACCOUNT = API_STR + "delete_account";

        public static final String UPDATE_LOCATION_URL = API_STR + "locationUpdate";
        public static final String INCOMING_REQUEST_IN_PROGRESS = API_STR + "get_incoming_request";
        public static final String CHECK_REQUEST_STATUS = API_STR + "requests_status_check";
        public static final String PROVIDER_ACCEPTED = API_STR + "requests_accept";
        public static final String PROVIDER_REJECTED = API_STR + "requests_reject";
        public static final String REQUEST_STARTED = API_STR + "requests_started";
        public static final String PROVIDER_ARRIVED = API_STR + "requests_provider_arrived";
        public static final String SERVICE_STARTED = API_STR + "requests_service_started";
        public static final String SERVICE_COMPLETED = API_STR + "requests_service_completed";
        public static final String RATE_USER = API_STR + "requests_provider_rated";
        public static final String COD_CONFIRM = API_STR + "codPaidConfirmation";
        public static final String PROVIDER_AVAILABILITY_CHECK = API_STR + "provider_available_check";
        public static final String UPDATE_PROVIDER_AVAILABILITY = API_STR + "provider_available_update";
        public static final String CANCEL_TRIP = API_STR + "requests_cancel";
        public static final String HISTORY = API_STR + "requests_history";
        public static final String HISTORIAL_RECARGAS = API_STR + "historial-recargas";
        public static final String GET_DOC = API_STR + "documents";
        public static final String UPLOAD_DOC = API_STR + "documents_upload";
        public static final String USER_MESSAGE_NOTIFY = API_STR + "message_notification?";
        public static final String UPDATETIME = API_STR + "update_timezone";
        public static final String ADVERTISEMENTS = API_STR + "advertisements";
        public static final String DASHBOARD = API_STR + "dashboard";
        public static final String OV_EARNIGS = API_STR + "get-balance";
        public static final String LOGOUT = API_STR + "logout";

        public static final String MESSAGE_GET = API_STR + "message/get";
        public static final String SEND_PANIC_MESSAGE = API_STR + "sendPanicMessage";
        public static final String SET_PANIC_CONTACT = API_STR + "setPanicContact";
        public static final String GET_PANIC_CONTACT = API_STR + "getPanicContact";
        public static final String ADS_MANAGEMENT = API_STR + "advertisements";
        public static final String SERVICE_LIST= API_STR + "services_list";
        public static final String UPDATE_LOCATION = API_STR + "location_update";
        public static final String CHAT_DETAILS = API_STR + "requests_chat_view";
        public static final String CANCEL_REASON = API_STR + "cancellation_reasons";
        public static final String CONTACT_NUMBER = "contact_number";
        public static final String WALLET = API_STR + "wallets";
        public static final String WALLET_PAYMENTS = API_STR + "wallets_payments";
        public static final String ADD_MONEY_TO_WALLET = API_STR + "wallets_add_money";
        public static final String REDEEMS = API_STR + "redeems";
        public static final String CANCEL_REDEEM_REQUEST = API_STR + "redeems_requests_cancel";
        public static final String SEND_MONEY_TO_REDEEM = API_STR + "redeems_requests_send";
        public static final String REDEEM_REQUESTS = API_STR + "redeems_requests";

        public static final String CARDS = API_STR + "cards_list";
        public static final String ADD_CARDS = API_STR + "cards_add";
        public static final String DELETE_CARD = API_STR + "cards_delete";
        public static final String MAKE_DEFAULT_CARD = API_STR + "cards_default";
        public static final String CHANGE_DEFAULT_PAYMENT_MODE = API_STR + "payment_mode_default";

        // direction API
        public static final String DIRECTION_API_BASE = "https://maps.googleapis.com/maps/api/directions/json?";

        public static final String SERVICES_LISTS =  API_STR + "services_list";
        public static final String CANCEL_ONGOING_RIDE = API_STR + "requests_cancel";
        public static final String REQUESTS_VIEW = API_STR + "requests_view";
        public static final String STATIC_PAGES = API_STR + "pages/list";
        public static final String REQUEST_STATUS_CHECK_NEW = API_STR + "requests_status_check_new";

        private APIs() {
        }
    }

    //Params
    public class Params {
        public static final String ID = "id";
        public static final String MONTO = "monto";
        public static final String NUMERO = "numero";
        public static final String FECHA = "fecha";
        public static final String PROVIDER_ID = "provider_id";
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
        public static final String CONTACT_NUMBER = "contact_number";
        public static final String REFERRER_BONUS_FORMATTED = "referrer_bonus_formatted";
        public static final String REFERAL_SHARE_MESSAGE = "referral_share_message";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String ICON = "icon";
        public static final String DEVICE_TYPE = "device_type";
        public static final String LOGIN_BY = "login_by";
        public static final String CURRENCEY = "currency";
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

        public static final String MODEL = "model";
        public static final String CAR_IMAGE = "car_image";
        public static final String COLOR = "color";
        public static final String PLATE_NUMBER = "plate_no";

        public static final String FORCE_CLOSE = "force_close";
        public static final String APP_VERSION = "app_version";

        public static final String SUCCESS = "success";
        public static final String MESSAGE = "message";
        public static final String ERROR_MESSAGE = "error_message";
        public static final String ERROR_CODE = "error_code";
        public static final String DATA = "data";
        public static final String POSTS = "posts";
        public static final String CURRENCIES = "currencies";
        public static final String RECARGAS = "recargas";
        public static final String DESCUENTOS = "descuentos";
        public static final String ERROR = "error";
        //History
        public static final String REQUESTS = "requests";
        public static final String D_ADDRESS = "d_address";
        public static final String S_ADDRESS = "s_address";
        public static final String ADDRESS = "address";
        public static final String DATE = "date";
        public static final String USER_NAME = "user_name";
        public static final String TAXI_NAME = "taxi_name";
        public static final String TOTAL = "total";
        public static final String MAP_IMAGE = "map_image";
        public static final String BASE_PRICE = "base_price";
        public static final String DISTANCE_TRAVEL = "distance_travel";
        public static final String TOTAL_TIME = "total_time";
        public static final String TAX_PRICE = "tax_price";
        public static final String TIME_PRICE = "time_price";
        public static final String DISTANCE_PRICE = "distance_price";
        public static final String MIN_PRICE = "min_fare";
        public static final String BOOKING_FEE = "booking_fee";
        public static final String DISTANCE_UNIT = "distance_unit";
        public static final String COMPANY_EARNINGS = "company_earnings";
        public static final String PORVIDER_EARNINGS = "provider_earnings";
        //get Document
        public static final String DOCUMENTS = "documents";
        public static final String NAME = "name";
        public static final String DOCUMENT_URL = "document_url";
        public static final String DOCUMENT_ID = "document_id";
        public static final String INVOICE = "invoice";
        public static final String PAYMENT_MODE = "payment_mode";
        public static final String ACTIVE = "active";
        public static final String IS_AVAILABLE = "is_available";
        public static final String USER_PICTURE = "user_picture";
        public static final String S_LATITUDE = "s_latitude";
        public static final String S_LONGITUDE = "s_longitude";
        public static final String REQUEST_STATUS_TYPE = "request_status_type";
        public static final String HOURLY_PACKAGE_DEATILS= "hourly_package_details";
        public static final String NUMBER_HOURS = "number_hours";
        public static final String REQUEST_TYPE = "request_type";
        public static final String TOTAL_EARNINS = "total_earnings";
        public static final String EARNINGS = "earnings";
        public static final String REQUEST_CANCELLED = "request_cancelled";
        public static final String ORIGIN_ADDRESSES = "origin_addresses";
        public static final String DESTINATION_ADDRESSES = "destination_addresses";
        public static final String SERVICES = "services";
        public static final String DESCRIPTION = "description";
        public static final String PROVIDER_STATUS = "provider_status" ;
        public static final String TIME_ZONE = "timezone" ;
        public static final String SERVICE_TYPE_ID = "service_type_id";
        public static final String SERVICE_TYPE_NAME = "service_type_name";

        public static final String MOBILE = "mobile";
        public static final String COUNTRY_CODE = "country_code";
        public static final String USERNAME = "username";
        public static final String REFERRAL_CODE = "referral_code";
        public static final String DOCUMENT_NAME = "document_name";
        public static final String PREVIEW = "preview";
        public static final String IS_DOCUMENT_UPLOADED = "is_document_uploaded";
        public static final String OLD_PASSWORD = "old_password";
        public static final String CONFIRM_PASSWORD = "password_confirmation";
        public static final String COMMONID = "commonid";
        public static final String MYID = "myid";
        public static final String UPDATED_AT = "updated_at";
        public static final String TYPE = "type";
        public static final String CHAT_TYPE = "chat_type";
        public static final String PROVIDER_PICTURE = "provider_picture";
        public static final String SKIP = "skip";
        public static final String USER_ID = "user_id";
        public static final String TODAY_EARNINGS_FORMATTED = "today_earnings_formatted";
        public static final String AMOUNT_FORMATTED = "amount_formatted";
        public static final String TOTAL_REQUESTS = "total_requests";
        public static final String TOTAL_EARNINGS_FORMATTED = "total_earnings_formatted";
        public static final String COMPLETED_TRIPS = "total_completed_requests";
        public static final String LAST_X_DAYS_REQUESTS = "last_x_days_earnings";
        public static final String LATITUDE = "latitude";
        public static final String PROVIDER_NAME = "provider_name";
        public static final String REASON_ID = "reason_id";
        public static final String CANCELLATION_REASON = "cancellation_reason";

        public static final String REQUEST_UNIQUE_ID = "request_unique_id";
        public static final String REQUEST_MAP_IMAGE = "request_map_image";
        public static final String TOTAL_FORMATTED = "total_formatted";
        public static final String RIDE_FARE = "ride_fare_formatted";
        public static final String CANCELLATION_FARE_FORMATTED = "cancellation_fare_formatted";
        public static final String TAX_FARE_FORMATTED = "tax_fare_formatted";
        public static final String SERVICE_FARE_FORMATTED = "service_fare_formatted";
        public static final String DISCOUNT = "discount_formatted";
        public static final String REQUEST_BUTTON_STATUS = "requests_btn_status";
        public static final String TRACK_STATUS = "track_status";
        public static final String CANCEL_BUTTON_STATUS = "cancel_btn_status";
        public static final String MESSAGE_BTN_STATUS = "message_btn_status";
        public static final String REQUEST_CREATED_TIME = "request_created_time";
        public static final String INVOICE_DETAILS = "invoice_details";
        public static final String PROVIDER_DETAILS = "provider_details";
        public static final String SERVICE_MODEL = "service_model";
        public static final String TYPE_PICTURE = "type_picture";
        public static final String INVOICE_BUTTON_STATUS = "invoice_status";
        public static final String REQUEST_STAUS_ICON = "request_icon_status";
        public static final String REQUEST_STAUS_ICON_TEXT = "request_icon_status_text";
        public static final String USER_MOBILE = "user_mobile";
        public static final String PAGE_TYPE = "page_type";
        public static final String PROVIDER_EARNINGS = "provider_earnings";

        public static final String UNIQUE_ID = "unique_id";
        public static final String WALLET_SYSTEM_SYMBOL = "wallet_amount_symbol";
        public static final String WALLET_IMAGE = "wallet_image";
        public static final String PAID_AMOUNT = "paid_amount";
        public static final String AMOUNT = "amount";
        public static final String USED = "used";
        public static final String IMAGE = "image";
        public static final String AMOUNT_SYMBOL = "amount_symbol";
        public static final String PROVIDER_REDEEM_REQUEST_ID = "provider_redeem_request_id";
        public static final String IS_REDEEM = "isRedeem";
        public static final String TITLE = "title";
        public static final String WALLET = "wallet";
        public static final String REMAINING = "remaining";
        public static final String PAYMENTS = "payments";
        public static final String CARD_TOKEN = "card_token";
        public static final String PROVIDER_CARD_ID = "provider_card_id";
        public static final String PAYMENT_MODES = "payment_modes";
        public static final String PAYMENT_MODE_IMAGE = "payment_mode_image";
        public static final String CARDS = "cards";
        public static final String CARD_LAST_FOUR = "last_four";
        public static final String CARD_NAME = "card_holder_name";
        public static final String IS_DEFAULT = "is_default";
        public static final String REMAINING_FORMATTED = "remaining_formatted";
        public static final String USED_FORMATTED = "used_formatted";
        public static final String D_LATITUDE = "d_latitude";
        public static final String D_LONGITUDE = "d_longitude";
        public static final String MOBILE_FORMATTED = "mobile_formatted";
        public static final String USER_MOBILE_FORMATTED = "user_mobile_formatted";
        public static final String PROVIDER_EARNINGS_FORMATTED = "provider_earnings_formatted";
        public static final String USER_TOKEN = "user_token";
        public static final String PROVIDER_TOKEN = "provider_token";
        public static final String CURRENT_MONTH_FORMATTED = "current_month_earnings_formatted";
        public static final String USER_LOCATION = "user-location";
        public static final String PROVIDER_LOCATION_UPDATE = "provider-location-update";
        public static final String SENDER_UPDATE = "update sender";
        public static final String STATUS_TEXT = "status_text" ;
        public static final String IS_ADD_STOP = "is_adstop";
        public static final String ADD_STOP_ADDRESS = "adstop_address";
        public static final String ADD_STOP_LATITUDE = "adstop_latitude";
        public static final String ADD_STOP_LONGITUDE = "adstop_longitude";
        public static final String REQUEST_SERVICE_TYPE = "request_status_type";
        public static final String BEARING = "bearing";
        public static final String RECARGA = "recarga";

        Params() {

        }

    }
}
