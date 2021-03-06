package android.capsulepharmacy.com.utility;

import java.text.SimpleDateFormat;



public class AppConstants {

    public static final String STR_TITLE = "strTitle";
    public static final String DATE_FORMAT = "dd MMM yyyy";
    public static final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat format11 = new SimpleDateFormat("dd-MM-yyyy");
    public static final SimpleDateFormat format2 = new SimpleDateFormat(DATE_FORMAT);
    public static final String format8 = "MMM dd, yyyy hh:mm:ss";
    //    2018-12-05T00:00:00.000Z
    public static final SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000'Z'");
    public static final SimpleDateFormat format4 = new SimpleDateFormat("yyyy-MM-dd");
    public static final String utc_format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String utc_format1 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String format6 = "yyyy-MM-dd";
    public static final String format7 = "dd-MM-yyyy";
    public static final String format5 = "MMM dd, yyyy hh:mm:ss a";
    public static final SimpleDateFormat format10 = new SimpleDateFormat(AppConstants.format5);
    public static final String format = "dd/MMM/yyyy";

    /**
     * The constant SUCCESS.
     */
    public static int SUCCESS = 200;
    /**
     * The constant BAD_REQUEST.
     */
    public static int BAD_REQUEST = 400;
    /**
     * The constant INTERNAL_SERVER_ERROR.
     */
    public static int INTERNAL_SERVER_ERROR = 500;
    /**
     * The constant URL_NOT_FOUND.
     */
    public static int URL_NOT_FOUND = 404;
    /**
     * The constant UNAUTHORIZE_ACCESS.
     */
    public static int UNAUTHORIZE_ACCESS = 401;
    /**
     * The constant CONNECTION_OUT.
     */
    public static int CONNECTION_OUT = 408;
    /**
     * The constant mOrderInfoArrayList.
     */

    public static final int DashBoard = 0;
    public static final int SurveyReport = 1;
    public static final int SyncResponse = 2;
    public static final int Questionaire = 3;
    public static final int Analytics = 4;
    public static final int ManageTeams = 5;
    public static final int Help = 6;
    public static final int UpdateApplication = 7;
    public static final int LogOut = 8;
    public static final String POSITION = "position";
    public static final String CATEGORYID = "categoryId";
    public static final String CLIENTID = "clientId";
    public static final String CUSTOMERID = "customerId";
    public static final String SURVEYID = "surveyId";
    public static final String UPDATEDAT = "updatedAt";
    public static final String EXPIRYDATE = "expiryDate";
    public static final String PROPERTIES = "properties";
    public static final String STRING = "string";
    public static final String NUMBER = "number";
    public static final String FLOAT = "float";
    public static final String FORM_ANS_ID = "formAnsId";
    public static final String RESPONSES = "responses";
    public static final String RESPONSE = "response";
    public static final String SUBMITBY = "submitBy";
    public static final String RADIO = "radio";
    public static final String CHECKBOX = "checkbox";
    public static final String SELECT = "select";
    public static final String TEXTBOX = "textbox";
    public static final String TEXTAREA = "textarea";
    public static final String SECTION = "section";
    public static final String DATE = "date";
    public static final String CREATEDAT = "createdAt";
    public static final String ASTERIK_SIGN = "<font color='#03A9F4'> *</font>";
    public static final String FORM_ID = "formId";
    public static final String ID = "_id";

    public static final String ISUPDATE = "isUpdate";
    public static final String ISSYNC = "isSync";
    public static final String LONG = "long";

    //for login Module
    public static final String TOKEN_ID = "token_id";
    public static final String TOKEN_KEY = "Authorization";
    public static final String ACCESS_TOKEN = "authToken";
    public static final String TOKEN = "token_type";
    public static final String USER_NAME = "userName";
    public static final String USER_TYPE = "userType";
    public static final String NAME = "name";
    public static final String USER_ROLE = "role";
    public static final String USER_ID = "userId";
    public static final String USER_POINTS = "Points";
    public static final String USER_USED_POINTS = "MaxUsePoints";
    public static final String USER_PER_POINTS = "PointsPerValue";

    public static final String DESIGNATION = "designation";
    public static final String UserId = "user_id";
    public static final String isActive = "isActive";
    public static final String designationLevel = "designationLevel";
    public static final String phone = "phone";
    public static final String password = "password";
    public static final String email = "email";
    public static final String name = "employee_name";
    public static final String picture = "picture";
    public static final String location = "location";
    public static final String lat = "lat";
    public static final String lng = "lat";


    public static final String Login_Status = "login_status";
    public static final String AUTHORIZATION = "Authorization";
    public static final String LAST_SYNC_DATE = "LastSyncDate";
    public static final String WORKFLOWS = "workflows";
    public static final String SURVEYREPORT = "Survey Report";
    public static final String CREATED_BY = "createdBy";
    public static final String TITLE = "title";
    public static final String MINUS_ONE = "minus";
    public static final String ISACTIVE = "isActive";
    public static final String YES = "Yes";
    public static final String CATEGORY_NAME = "categoryName";
    public static final String CATEGORY = "category";
    public static final String LATTITUDE = "lattitude";
    public static final String LONGITUDE = "longitude";
    public static final String PROFILE_CHECK = "ProfileCheck";

    //survey status
    public static final int PENDING = 0;
    public static final int INPROCESS = 1;
    public static final int SUBMITTED = 2;
    public static final int COMPLETED = 3;
    public static final int REJECT = 4;
    public static final String ANSWERS = "answers";
    public static final String SUBMITBY_CD = "submitbyCD";
    public static final String SUBMITBY_RM = "submitbyRM";
    public static final String SUBMITBY_ZM = "submitbyZM";
    public static final String CD_STATUS = "cd_Status";
    public static final String RM_STATUS = "rm_Status";
    public static final String ZM_STATUS = "zm_Status";
    public static final String USERNAME = "userName";
    public static final String STATUS = "status";
    public static final String QUESTIONS = "questions";
    public static final String QUESTIONID = "questionId";
    public static final String ANSWER = "answer";
    public static final String REQUIRED = "required";
    public static final String ISVIEW = "isView";
    public static final String WORKFLOW = "workflow";
    public static final String REPORT = "report";

    public static final String MULTISELECT = "multiselect";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String DETAIL = "detail";
    public static final String TYPE = "type";
    public static final String TODATE ="toDate" ;
    public static final String CUSTOMER = "customer";
    public static final String PNG_FILE_TYPE = "image/*";
    public static final String EXCEL_FILE_TYPE = "application/excel";
    public static final String DIR_UNIVERSE = "/Universe";
    public static final String NEW = "unknown";
    public static final String ISVIEWBYZM ="isViewZM" ;
    public static final String MULTIEDITTEXT = "multiedittext";
    public static final String ROLE = "role";
    public static final String ADMIN = "admin";
    public static final String RATING = "rating";
    public static final String USERID ="userId" ;
    public static final java.lang.String LOCATION = "location";
    public static final java.lang.String ISLOCATIONSET = "isLocation";
    public static final String PERCENTAGE = "percentage";
    public static final String RANKING = "ranking";
    public static final String EXPIRY ="expiry" ;
    public static final String ISVIEWBYREQUESTER = "requesterView";
    public static final String ISVIEWBYAPPROVAL1 = "approval1View";
    public static final String ISVIEWBYAPPROVAL2 = "approval2View";
    public static final String ISVIEWBYAPPROVAL3 = "approval3View";
    public static final String ISVIEWBYAPPROVAL4 = "approval4View";
    public static final String ISVIEWBYAPPROVAL5 = "approval5View";
    public static final String ISVIEWBYAPPROVAL6 = "approval6View";
    public static final String WORKFLOWID ="workflowId" ;
    public static final String REQUESTID ="requestId" ;
    public static final String DISPLAY_ORDER = "displayOrder";
    public static final java.lang.String AUDIENCE = "audience";

    public static final java.lang.String KNOWN = "Known";
    public static final java.lang.String UNKNOWN = "Unknown";
    public static final java.lang.String BOTH = "Both";

    public static final java.lang.String KNOWNTITLE = "knownTitle";
    public static final java.lang.String UNKNOWNTITLE = "unKnownTitle";

    public static String surveyDetails = "surveyDetails";
    public static String crystalDoctorName = "crystalDoctorName";
    public static String total = "total";
    public static String completed = "completed";

    public static String target = "target";
    public static String submitted = "submitted";
    public static String inprogress = "inprogress";
    public static String CrystalCustomer = "known";
    public static String newRetailer = "newRetailer";


    public static final String UPDATEID = "updateId";
    public static final String MAPPING = "mapping";
    public static final String DETAILS = "details";
    public static final String EMPLOYEE_NAME = "employee_name";

    public static final String customer = "customer";
    public static final String employee = "employee";
    public static final String ME ="Me" ;
    public static final String REASON = "reason";
    public static final String VISIBLITY = "visiblity";
    public static final String GROUP_POSITION = "groupPosition";


    public static String Percent = "percent";
    public static String CrystaDoctorName="CrystaDoctorName";
    public static String LoginDetail="loginDetails";
    public static String detail="detail";


    public static String CDID="cdid";
    public static String FROMDATE="formdate";
    public static String STATECODE="statecode";
    public static String TerroitryCode="TerroitryCode";
    public static String CROPID="cropid";
    public static String Distributor_Id="distributor_id";


    public static String VillageId="villageId";
    public static String employee_name="employee_name";


    public static String TeamSurveyId="TeamSurveyId";
    public static String employee_code="employee_code";


    public static String retailerName="retailerName";
    public static String state_code="state_code";
    public static String territory_code="territory_code";
    public static String distributer_code="distributer_code";
    public static String mobile="mobile";
    public static String mobileNumber="mobileNumber";
    public static String pincode="pincode";
    public static String cropId="cropId";
    public static String village_code="village_code";
    public static String Address="address";
    public static String totalSales="totalSales";


    public static String CUSTOMERIMAGE="CUSTOMERIMAGE";
    public static String LocationUpdate="LocationUpdate";
    public static String FilterData="FilterData";
    public static String TeamSurveyname="TeamSurveyname";
    public static String accessToken="accessToken";
    public static String requester="requester";
    public static String lob_name="lob_name";

    public static String requester_status="requester_status";
    public static String approval1_status="approval1_status";
    public static String approval2_status="approval2_status";
    public static String approval3_status="approval3_status";
    public static String approval4_status="approval4_status";
    public static String approval5_status="approval5_status";
    public static String approval6_status="approval6_status";

    public static String VillageData="villageData";
    public static String ToDay="Today";
    public static String CurrentMonth="CurrentMonth";
    public static String CurrentWeek="CurrentWeek";
    public static String Others="Others";

    //path
    public static final String DROID_LOGO = "M 149.22,22.00\n" +
            "           C 148.23,20.07 146.01,16.51 146.73,14.32\n" +
            "             148.08,10.21 152.36,14.11 153.65,16.06\n" +
            "             153.65,16.06 165.00,37.00 165.00,37.00\n" +
            "             194.29,27.24 210.71,27.24 240.00,37.00\n" +
            "             240.00,37.00 251.35,16.06 251.35,16.06\n" +
            "             252.64,14.11 256.92,10.21 258.27,14.32\n" +
            "             258.99,16.51 256.77,20.08 255.78,22.00\n" +
            "             252.53,28.28 248.44,34.36 246.00,41.00\n" +
            "             252.78,43.16 258.78,48.09 263.96,52.85\n" +
            "             281.36,68.83 289.00,86.62 289.00,110.00\n" +
            "             289.00,110.00 115.00,110.00 115.00,110.00\n" +
            "             115.00,110.00 117.66,91.00 117.66,91.00\n" +
            "             120.91,76.60 130.30,62.72 141.04,52.85\n" +
            "             146.22,48.09 152.22,43.16 159.00,41.00\n" +
            "             159.00,41.00 149.22,22.00 149.22,22.00 Z\n" +
            "           M 70.80,56.00\n" +
            "           C 70.80,56.00 97.60,100.00 97.60,100.00\n" +
            "             101.34,106.21 108.32,116.34 110.21,123.00\n" +
            "             113.76,135.52 103.90,147.92 91.00,147.92\n" +
            "             78.74,147.92 74.44,139.06 69.00,130.00\n" +
            "             69.00,130.00 39.80,82.00 39.80,82.00\n" +
            "             35.73,75.29 28.40,66.08 29.20,58.00\n" +
            "             30.26,47.20 38.61,40.47 49.00,39.72\n" +
            "             61.22,40.24 64.96,46.28 70.80,56.00 Z\n" +
            "           M 375.80,58.00\n" +
            "           C 376.60,66.08 369.27,75.29 365.20,82.00\n" +
            "             365.20,82.00 336.00,130.00 336.00,130.00\n" +
            "             330.71,138.82 326.73,147.24 315.00,147.89\n" +
            "             301.74,148.63 291.14,135.87 294.79,123.00\n" +
            "             296.68,116.34 303.66,106.21 307.40,100.00\n" +
            "             307.40,100.00 333.00,58.00 333.00,58.00\n" +
            "             339.02,47.98 342.23,40.92 355.00,39.72\n" +
            "             365.83,40.00 374.69,46.77 375.80,58.00 Z\n" +
            "           M 289.00,116.00\n" +
            "           C 289.00,116.00 289.00,239.00 289.00,239.00\n" +
            "             288.98,249.72 285.92,257.31 275.00,261.10\n" +
            "             265.22,264.50 258.37,259.56 255.02,264.43\n" +
            "             253.78,266.24 254.00,269.84 254.00,272.00\n" +
            "             254.00,272.00 254.00,298.00 254.00,298.00\n" +
            "             254.00,304.85 254.77,310.07 250.36,315.93\n" +
            "             242.35,326.68 226.84,326.49 218.80,315.93\n" +
            "             215.07,311.00 215.01,306.83 215.00,301.00\n" +
            "             215.00,301.00 215.00,262.00 215.00,262.00\n" +
            "             215.00,262.00 190.00,262.00 190.00,262.00\n" +
            "             190.00,262.00 190.00,301.00 190.00,301.00\n" +
            "             189.99,306.83 189.93,311.00 186.20,315.93\n" +
            "             178.16,326.49 162.65,326.68 154.64,315.93\n" +
            "             151.09,311.22 151.01,307.61 151.00,302.00\n" +
            "             151.00,302.00 151.00,272.00 151.00,272.00\n" +
            "             151.00,269.84 151.22,266.24 149.98,264.43\n" +
            "             146.53,259.42 138.97,264.76 129.00,260.86\n" +
            "             118.39,256.72 116.02,248.29 116.00,238.00\n" +
            "             116.00,238.00 116.00,116.00 116.00,116.00\n" +
            "             116.00,116.00 289.00,116.00 289.00,116.00 Z";
}

