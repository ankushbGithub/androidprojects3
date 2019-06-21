package android.capsulepharmacy.com.utility;

import java.text.SimpleDateFormat;


public class Constants {
    public static final int MB = 1 * 1024 * 1024;

    public static final int MAX_MEMORY_CACHE_SIZE = 4 * MB;
    public static final String showCaseShown = "showCaseShown";
    public static final String helpShown = "helpShown";

    public static final int MAX_DISK_CACHE_SIZE = 50 * MB;
    public static final String APP_SHARED_PREF_NAME = "profile";
    public static final int GALLERY_PICTURE = 10;
    public static final int CAMERA_PICTURE = 100;

    public static final String DATE_FORMAT = "dd-MMM-yyyy";
    public static final String DATE_FORMAT1 = "MM/yy";
    public static final String DATE_FORMAT2 = "dd/MM/yyyy";
    public static final String DATE_FORMAT4 = "dd-MM-yyyy";
    public static final String DATE_FORMAT3 = "dd MMM";
    public static final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat format11 = new SimpleDateFormat("dd-MM-yyyy");
    public static final SimpleDateFormat formatIndian = new SimpleDateFormat("dd-MMM-yyyy");
    public static final SimpleDateFormat format2 = new SimpleDateFormat(DATE_FORMAT);
    public static final SimpleDateFormat format34 = new SimpleDateFormat(DATE_FORMAT1);
    public static final SimpleDateFormat format35 = new SimpleDateFormat(DATE_FORMAT2);
    public static final SimpleDateFormat format36 = new SimpleDateFormat(DATE_FORMAT3);
    public static final String format8 = "MMM dd, yyyy hh:mm:ss";
    //    2018-12-05T00:00:00.000Z
    public static final SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000'Z'");
    public static final SimpleDateFormat format4 = new SimpleDateFormat("yyyy-MM-dd");
    public static final String format9 = "dd-MMMM-yyyy hh:mm a";

    public static final String formatCommon = "yyyy-MM-dd'T'hh:mm:ss.SSS";
    public static final String calenderFormat = "yyyy-MMM-dd";
    public static final String indianCalender = "dd-MMM-yyyy";
    public static final String formatPrice = "yyyy-MM-dd'T'hh:mm:ss";
    public static final SimpleDateFormat formatToServer = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

    public static final SimpleDateFormat formatPriceHitory = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
    public static final String formatEditPrice = "dd-MMM-yyyy";


    public static final String utc_format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String utc_format1 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String format6 = "yyyy-MM-dd";
    public static final String format17 = "dd-MM-yyyy";
    public static final String format5 = "MMM dd, yyyy hh:mm:ss a";
    public static final String format7 = "dd/MM/yyyy hh:mm:ss a";
    public static final String format12 = "MM/dd/yyyy hh:mm:ss a";
    public static final String format15 = "hh:mm:ss a";
    public static final String format16 = "hh:mm a";
    public static final SimpleDateFormat format13 = new SimpleDateFormat("dd-MMM-yy");
    public static final SimpleDateFormat format20 = new SimpleDateFormat("dd-MMM-yyyy");
    public static final SimpleDateFormat format21 = new SimpleDateFormat(Constants.format6);
    public static final SimpleDateFormat format10 = new SimpleDateFormat(Constants.format17);
    public static final SimpleDateFormat format22 = new SimpleDateFormat(Constants.indianCalender);
    public static final String format = "dd/MMM/yyyy";
    public static final String formatDate = "dd-MM-yyyy";
    public static final String formatDate2 = "MM/dd/yyyy";
    public static final SimpleDateFormat format14 = new SimpleDateFormat("dd-MMM");


    // mandatory or not mandatory
    public static final String ASTERIK_SIGN = "<font color='#e64f20'> *</font>";
    public static final String ASTERIK_SIGN_BLUE = "<font color='#03A9F4'> *</font>";
    public static final String text = "<font color='#03A9F4'> 63%</font>";

    public static final int API_METHOD_GET = 0;
    public static final String SP_KEY_USER_DETAIL = "data";


    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
    public static final String TOTAL_AMOUNT = "totalAmount";
    public static final int APP_DIALOG_OTC = 0;
    public static final int API_METHOD_POST = 1;
    public static final int APP_DIALOG_PERMISSION = 2;
    public static final int APP_DIALOG_BACK = 4;
    public static final int APP_DIALOG_BILLING = 5;
    public static final int APP_DIALOG_PRICENONZERO = 8;
    public static final int DISCOUNT = 2;
    public static final String ISUPDATE = "isUpdate";
    public static final String ISSYNC = "isSync";
    public static final String CHECK_DISCOUNT = "CHECK_DISCOUNT";
    public static final int ACT_PAYMENT = 100;
    public static final int ACT_PAYMENT_NEW_BILLING = 300;
    public static final int ACT_CUSTOMER = 301;
    public static final String KEY_CUSTOMER_POINTS = "CUSTOMER_POINTS";
    public static final int ACT_PINNED = 302;
    public static final int ACT_B2B_CUSTOMER = 305;
    public static final String PAYMENT_REQUEST = "payment_request";
    public static final String KEY_CUSTOMER_POINTS_PER = "CUSTOMER_POINTS_PER";
    public static final String KEY_CUSTOMER_CREDIT = "CUSTOMER_CREDIT";
    public static final String KEY_CUSTOMER_POINTS_EMAIL = "CUSTOMER_POINTS_EMAIL";
    public static final String KEY_ORDER_ID = "KEY_ORDER_ID";
    public static final int APP_DIALOG_DELETE_ITEM = 6;
    public static final int APP_DIALOG_FORCE_UPDATE = 60;
    public static final int APP_DIALOG_OTC_DIALOG_1 = 7;
    public static final String APP_DIALOG_CUSTOMER = "app_dialog_customer";
    public static final String KEY_CUSTOMER_POINTS_NUMBER = "CUSTOMER_POINTS_NUMBER";
    public static final String RECEIPT = "RECEIPT";
    public static final String RECEIPT_FROM = "RECEIPT_FROM";
    public static final String SHOW_BUTTON = "showButton";
    public static final String MATERIAL_CODE = "material_code";
    public static final int ACT_ADD = 422;
    public static final int ACT_EDIT = 433;
    public static final int ACT_PROFILE = 530;
    public static final int ACT_ENTITY = 540;
    public static final int PDF_GEN = 550;
    public static final String IMAGE_BASE = "image_base";
    public static final String SIGN_IMAGE_BASE = "sign_image_base";
    public static final String IMAGE_TYPE = "image_type";
    public static final String SIGN_IMAGE_TYPE = "sign_image_type";
    public static final String DELIVERED_DATE = "delivered date";
    public static final String BANK_JSON = "bank_json";
    public static final String ACT = "act";
    public static final String VENDOR_JSON = "vendor_json";
    public static final int ACT_VENDOR = 340;
    public static final String EARNED_POINTS = "earned_points";
    public static final String POINTS_PER_VALUE = "points_per_value";
    public static final String CREDIT_NOTE = "credit_note";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String ORDER_DATE = "order_date";
    public static final String LocationId = "LocationId";
    public static final String RetailEntityPos = "RetailEntityPos";
    public static final String RetailEntityName = "RetailEntityName";
    public static final String RetailEntityCode = "RetailEntityCode";
    public static final String RetailEntityLocId = "RetailEntityLocId";
    public static final String RetailEntityJSON = "RetailEntityJSON";
    public static final String Retail_Sales = "Retail Sales";
    public static final String Retail_Sales_OC = "Retail Sales OC";
    public static final String CPEntityId = "CPEntityId";
    public static final String CPEntityName = "CPEntityName";
    public static final String New = "new";
    public static final String Response = "response";
    public static final String RetailEntityList = "RetailEntityList";
    public static final String OTCDefaultScheme = "OTCDefaultScheme";
    public static final String OTCDefaultSchemeID = "OTCDefaultSchemeID";
    public static final String StoreName = "StoreName";
    public static final String RETAIL_JSON = "RETAIL_JSON";
    public static final int ACT_PAY = 560;
    public static final String ORDER_CENTER = "order_center";
    public static final String ACCBAL = "ACCBAL";
    public static final String CL = "CL";
    public static final String CreditDays = "CD";
    public static final String POS = "pos";
    public static final String PrevBusinessPlaceId = "prevBusinessPlaceId";
    public static final String PrevEntityId = "PrevEntityId";


    public static int APP_DIALOG_Pinned_ORDER = 3;

    public static String UserProfilePic = "http://upload.wikimedia.org/wikipedia/commons/d/d3/User_Circle.png";
    public static String customer_detail = "customer_detail";
    public static int APP_DIALOG_Cart = 1;
    public static String Product_List = "product_list";
    public static String Order_List = "order_list";


    public static String FCM_KEY = "fcmToken";
    public static int SUCCESS = 200;
    public static int BAD_REQUEST = 400;
    public static int INTERNAL_SERVER_ERROR = 500;
    public static int URL_NOT_FOUND = 404;
    public static int UNAUTHORIZE_ACCESS = 401;
    public static int CONNECTION_OUT = 408;
    public static String mOrderInfoArrayList = "mOrderInfoArrayList";
    public static String mInfoArrayList = "mInfoArrayList";


    public static final String ISLOGGEDIN = "ISLOGGEDIN";
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String STORE_ID = "WorklocationID";
    public static final String KEY_CUSTOMER = "customer";

    //Shared preference keys
    public static final String PREF_KEY_PRODUCT_MAIN = "ProductMain";
    public static final String PREF_KEY_SEARCHED_ITEM = "SearchedItem";

    public static final String PREF_KEY_SEARCHED_SUB_ITEM = "SearchedSUBItem";
    public static final String PREF_KEY_PRODUCT_DETAIL = "ProductDetailMain";
    public static final String PREF_KEY_PRODUCT_COUNT = "ProductCount";

    //Shared preference file name
    public static final String PREF_KEY_SEARCHED_PRODUCT_DATA = "SearchedProductData";
    public static final String PREF_KEY_PRODUCT_DATA = "ProductData";
    public static final String PREF_KEY_PRODUCT_DETAIL_KEY = "ProductDetail";

    //SpinnerListKey
    public static final String KEY_STATE_LIST = "stateList";
    public static final String KEY_COUNTRY_LIST = "countryList";
    public static final String KEY_CITY_LIST = "cityList";
    public static final String KEY_DESIGNATION_LIST = "designationList";
    public static final String KEY_COMPANY_LIST = "companyList";
    public static final String KEY_CUSTOMER_LIST = "customerList";
    public static final String KEY_RELATION_LIST = "relationshipList";


    public static String Login_result = "login_result";
    //  public static String USER_PROFILE="userProfile";
    public static final String businessPlaceCode = "businessPlaceCode";
    public static final String businessPlaceId = "businessPlaceId ";
    public static final String entityStateCode = "entityStateCode";

    public static final String ACCOUNT = "Account";
    public static final String AccessTo = "AccessTo";

    public static final String USERTYPE = "UserType";
    public static final String entityCode = "entityCode";
    public static final String UserEmailID = "UserEmailID";
    public static final String ID = "ID";
    public static final String entityName = "entityName";
    public static final String entityRole = "entityRole";
    public static final String employeeCode = "employeeCode";
    public static final String fromDate = "fromDate";
    public static final String toDate = "toDate";


    public static final String revenue = "revenue";
    public static final String expenses = "expenses";
    public static final String summary = "summary";


    public static final String sales = "sales";
    public static final String others = "others";
    public static final String purchases = "purchases";
    public static final String employeesExpenses = "employeesExpenses";
    public static final String sandryExpenses = "sandryExpenses";
    public static final String otherExpenses = "otherExpenses";
    public static final String totalExpenses = "totalExpenses";
    public static final String profitAndLoss = "profitAndLoss";
    public static final String taxes = "taxes";
    public static final String input = "input";
    public static final String output = "output";
    public static final String total = "total";

    public static final String employeeRole = "employeeRole";
    public static final String counterId = "counterId";
    public static final String dayClosureDate = "dayClosureDate";
    public static final String isEdit = "isEdit";
    public static final String cashRemark = "cashRemark";
    public static final String cardRemark = "cardRemark";
    public static final String walletRemark = "walletRemark";
    public static final String vouchersRemark = "vouchersRemark";
    public static final String totalVouchersSale = "totalVouchersSale";
    public static final String confirmVouchersValue = "confirmVouchersValue";
    public static final String vouchersDifference = "vouchersDifference";

    public static final String globalSettings = "globalSettings";

    public static final String EmpCode = "EmpCode";
    public static final String UserRole = "UserRole";
    public static final String EntityName = "EntityName";
    public static final String EntityId = "EntityId";


    public static String KEY_CUSTOMER_POINTS_EMPCODE = "POINTS_EMPCODE";
    public static String isOTC = "isOTC";
    public static String otcPerc = "otcPerc";
    public static String otcValue = "otcValue";

    public static String paymentMode = "paymentMode";
    public static String outboxMode = "outboxMode";
    public static String OrderCenterMode = "OrderCenterMode";
    public static int App_DIALOG_UPLOAD = 8;

    //DayClosure
    public static String CASH = "cash";

    public static String OPENING_BALANCE = "openingBalance";
    public static String ALLOWED_CASH_LIMIT = "allowedCashLimit";
    public static String CASH_SALES = "cashSales";
    public static String BAL_IN_HAND = "balanceInHand";
    public static String DIFFERENCE = "difference";

    public static String DATE_OF_DEPOSIT = "dateOfDeposit";
    public static String NO_DEPOSIT_REASON = "noDepositReason";
    public static String PROPOSED_BANK_DEPOSIT = "proposedBankDeposit";
    public static String CARD = "card";
    public static String TOTAL_CARD_SALE = "totalCardSale";
    public static String CONFIRM_SWIPE_VALUE = "confirmSwipeValue";
    public static String CREDIT_DIFFERENCE = "creditDifference";
    public static String WALLET = "wallet";
    public static String TOTAL_WALLET_SALE = "totalWalletSale";
    public static String CONFIRM_WALLET_VALUE = "confirmWalletValue";
    public static String WALLET_DIFFERENCE = "walletDifference";


    public static String POINTS = "points";
    public static String VOUCHERS = "vouchers";
    public static String TOTAL_POINTS_SALE = "totalPointsSale";
    public static String CONFIRM_POINTS_VALUE = "confirmPointsValue";
    public static String POINTS_DIFFERENCE = "pointsDifference";
    public static String TOTLE_sALE = "totlSales";
    public static String CASH_REMARK = "cashRemark";

    public static String CARD_REMARK = "cardRemark";
    public static String WALLET_REMARK = "walletRemark";
    public static String POINTS_REMARK = "pointsRemark";

    public static String From = "from";
    public static String To = "to";
    public static String Detail = "detail";
    public static String OFFER_JSON = "offer_json";
    public static String ADD = "add";
    public static String Create = "create";
    public static String locationCode = "location_code";
    public static String EntityAccessType = "EntityAccessType";


    //Generate doc variables
    public static String empCode = "empCode";
    public static String tranID = "tranID";
    public static String isGRN = "isGRN";
    public static String isGRNOrQC = "isGRNOrQC";
    public static String tran = "tran";
    public static String inventory = "inventory";
    public static String transactionType = "transactionType";
    public static String emailId = "emailId";
    public static String docPath = "docPath";
    public static String docUrl = "docUrl";
    public static final String entityLob = "entityLob";
    public static final String entityGroup = "entityGroup";
    public static String from = "from";
    public static String userLevel = "userLevel";
    public static String fieldConfig = "fieldConfig";
    public static String paymentConfig = "paymentConfig";
    public static String customerPaymentsRetail = "Retail";
    public static String customerPaymentsB2B = "B2B";
    public static String customerPayments = "customerPayments";
    public static String cpaymentModeClicked = "paymentModeClicked";
    public static String actionType = "actionType";
    public static String refNo = "refNo";
    public static final String PNG_FILE_TYPE = "image/*";
    public static final String EXCEL_FILE_TYPE = "application/vnd.ms-excel";
    public static final String DIR_IPOS = "/IPOS";
    public static String lineNo = "lineNo";
    public static String configId = "configId";
    public static String isRealize = "isRealize";
    public static String isAllowed="isAllowed";
    public static String bpName = "bpName";

    public interface paymentType {

        String cash = "Cash";
        String dc = "DC";
        String cc = "CC";
        String pnt = "PNT";
        String onl = "ONL";
        String chq = "CHQ";
        String accbal = "ACCBAL";
        String cl = "Credit Limit";
        String bank = "bank";
        String credit = "credit";
        String cod = "cod";
    }

    ;


    //                              Payment Pending: 13
//                               	Delivery Pending: 14
//                                	Payment & Delivery both pending:  11
//                               	Return:  9
//                               	Exchange :10
//                                	Cancelled: 3
//                               	Delivered : 7
    public interface RetailOrderType {
        int Payment_Pending = 13;
        int Delivery_Pending = 14;
        int Both_Pending = 11;
        int Return = 9;
        int Exchange = 10;
        int Cancelled = 3;
        int Delivered = 7;
    }

    public interface ModuleType {
        String Create = "create";
        String View = "view";
        String Edit = "edit";
        String Realize = "realize";
    }


    public interface ModuleMode {
        int CreateMode = 1;
        int ViewMode = 2;
        int EditMode = 3;

    }

    public interface EntityTypeName {
        String IPOS = "IPOS";
        String PSS = "PSS";
    }
    public static String Id = "Id";
    public static String FirstName = "FirstName";
    public static String LastName = "LastName";
    public static String DOB = "DOB";
    public static String Name = "Name";
    public static String Email = "Email";
    public static String GSTIN = "GSTIN";
    public static String FirmName = "FirmName";
    public static String CategoryId = "CategoryId";
    public static String SubCategoryId = "SubCategoryId";
    public static String ServiceLocationId = "ServiceLocationId";
    public static String Category = "Category";
    public static String FilteredSubCategory = "FilteredSubCategory";
    public static String ServiceAt = "ServiceAt";
    public static String MobileNo = "MobileNo";
    public static String PhotoPath = "PhotoPath";
    public static String PanUrl = "PanUrl";
    public static String AadhaarUrl = "AadhaarUrl";
    public static String VendorAddressDetails = "VendorAddressDetails";
    public static String VendorBankDetails = "VendorBankDetails";
    public static String PhotoUrl = "PhotoUrl";
    public static String PanPath = "PanPath";
    public static String AadhaarPath = "AadhaarPath";
    public static String ImagePath = "ImagePath";
    public static String ImageFile = "ImageFile";
    public static String AadhaarPathL = "AadhaarPathL";
    public static String PanPathL = "PanPathL";
    public static String PanFile = "PanFile";
    public static String AadhaarFile = "AadhaarFile";
    public static String Password = "Password";
    public static String passwordCode = "Code";


}

