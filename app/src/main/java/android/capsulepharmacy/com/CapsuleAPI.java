package android.capsulepharmacy.com;


import okhttp3.MediaType;

/**
 * API Methods of IPOS
 */


public class CapsuleAPI {

    /**
     * The constant CONTENT_TYPE.
     */
    public static String CONTENT_TYPE = "Content-Type";
    public static String ACCEPT = "Accept";
    public static String AUTHORIZATION = "Authorization";
    /**
     * The constant APPLICATION_JSON.
     */
    public static String APPLICATION_JSON = "application/json";
    public static String APPLICATION_URL_ENCODED = "application/x-www-form-urlencoded";
    /**
     * The constant JSON.
     */
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static String WEB_SERVICE_BASE_URL = "http://13.127.89.229:8010/api/";  // for ipos

    public static final String BASE = "http://epartyplanner.com/api/";
    public static final String BASE_TOKEN = "http://epartyplanner.com/";// for Development




    //login/register
    public static final String WEBSERVICE_LOGIN = BASE_TOKEN + "token";
    public static final String WEB_SERVICE_CUSTOMER_LIST = BASE + "Customer";
    public static final String WEB_SERVICE_VENDOR_LIST = BASE + "Vendor";
    public static String WEB_SERVICE_DELETE = BASE + "Customer/";
    public static String WEB_SERVICE_VENDOR_DELETE = BASE + "Vendor/";
    public static String WEB_SERVICE_CUSTOMER_CREATE= BASE + "Customer/?";
    public static String WEB_SERVICE_CUSTOMER_EDIT= BASE + "Customer/";
    public static String WEB_SERVICE_CUSTOMER_DETAILS= BASE + "Customer/";
    public static String WEB_SERVICE_VENDOR_DETAILS= BASE + "Vendor/";
    public static String WEB_SERVICE_CUSTOMER_REGISTER= BASE + "Account/Register";
    public static String WEB_SERVICE_SERVICE_AT= BASE + "ServiceAt";
    public static String WEB_SERVICE_SUB_CAT_LIST= BASE + "SubCategoryList";
    public static String WEB_SERVICE_VENDOR_CREATE= BASE + "Vendor";
    public static String WEB_SERVICE_VENDOR_UPDATE= BASE + "Vendor/";
    public static String WEB_SERVICE_CAT_LIST= BASE + "CategoryList";

    public static final String WEBSERVICE_REGISTER = BASE + "user/register";
    public static final String WEBSERVICE_GET_DASHBOARD = BASE + "dashboard";
    public static final String WEBSERVICE_UPLOAD = BASE + "";



}