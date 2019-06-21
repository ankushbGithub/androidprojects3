package android.capsulepharmacy.com.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.PharmacyApplication;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.adapter.InstructionsAdapter;
import android.capsulepharmacy.com.adapter.SpinnerDropDownAdapter;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.listener.AttachmentListener;
import android.capsulepharmacy.com.listener.MyListener;
import android.capsulepharmacy.com.modal.AttachFileModel;
import android.capsulepharmacy.com.modal.BookSubCategory;
import android.capsulepharmacy.com.modal.InstructionModal;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.activity.VendorDetailsActivity;
import android.capsulepharmacy.com.vendor.adapter.SubCatBookSelectedAdapter;
import android.capsulepharmacy.com.vendor.adapter.SubCatSelectedAdapter;
import android.capsulepharmacy.com.vendor.adapter.SubCatViewBookSelectedAdapter;
import android.capsulepharmacy.com.vendor.modal.SelectedSubCatList;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;

import static android.capsulepharmacy.com.PharmacyApplication.attachFileModels;


/**
 * The type Edit expandable po details activity.
 */
public class OrderScreenActivity extends BaseActivity implements PaymentResultListener, MyListener {


    Context context;
    private String erg="";
    private EditText input_Members,input_Email,input_Mobile,input_payAmt,input_paylater,input_remarks;
    private MyOrderModal myOrderModal;
    private TextView tvDate;
     ProgressDialog progressDialog1;
     private RecyclerView recyclerViewSubCategory;
     private SubCatBookSelectedAdapter subCatBookSelectedAdapter;
    private ArrayList<BookSubCategory> selectedSubCatLists=new ArrayList<>();
    private int minMembers,maxMembers;
    private TextView tvTotalPay,tvTotalGST,tvTotalAmount,tvPoints,tvTotalPayAfter;
    private double amounts=0;
    private RadioGroup radioGroup;
    private RadioButton radioSexButton;
    private int usedPoints;
    private double listAmount=0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_screen_activity);
        context= OrderScreenActivity.this;
        setHeader();
        recyclerViewSubCategory=findViewById(R.id.recyclerViewSubCategory);
        radioGroup=(RadioGroup)findViewById(R.id.radio);
        Intent i=getIntent();
       myOrderModal=(MyOrderModal) i.getSerializableExtra("model");
        Button btnProceed=findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate()){
                    if (!Utility.validateString(erg)){
                        Utility.showToast("Please choose Booking date");
                        dateDialogfromReturn();
                    }else {
                        progressDialog1 = new ProgressDialog(OrderScreenActivity.this);
                        progressDialog1.show();
                        startPayment();
                    }
                }

            }
        });
        dateDialogfromReturn();
        initialize();
        setListerner();
        getVendorDetails();
        int points=Prefs.getIntegerPrefs(AppConstants.USER_POINTS);
        int pointsUsed=Prefs.getIntegerPrefs(AppConstants.USER_USED_POINTS);

        tvPoints.setText((50*points/100)+" P");

        if (myOrderModal.getCategoryName().equalsIgnoreCase("Caterers")||myOrderModal.getCategoryName().equalsIgnoreCase("Caterer")){
         /*   || myOrderModal.getCategoryName().equalsIgnoreCase("Photographer")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Flower Decoration")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Balloon Decoration")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Invitations Card")
                || myOrderModal.getCategoryName().equalsIgnoreCase("DJs")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Ghodiwala")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Dhool")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Band")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Persist")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Pandit Ji")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Tailoring")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Persist/Pandit Ji")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Band/Dhool/Ghodiwala")) {*/

            input_Members.setVisibility(View.VISIBLE);
        }else {
            input_Members.setVisibility(View.GONE);
        }




            //setSelectedRecyclerView();



     //   inializeViews();


        setRadio();

    }

    private void setRadio(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId=radioGroup.getCheckedRadioButtonId();
                radioSexButton=(RadioButton)findViewById(selectedId);
                double totalAmt=0;

                if (listAmount==0){
                    totalAmt=myOrderModal.getPrice();
                }else {
                    totalAmt= amounts;
                }
                if (radioSexButton.getText().equals("Pay 30%")){
                 //   double payAmt= Double.parseDouble(input_payAmt.getText().toString().trim());

                        input_paylater.setText(Math.round((70*totalAmt/100)) + "");
                        input_payAmt.setText(Math.round((30*totalAmt/100)) + "");

                }else {
                    input_paylater.setText(Math.round((0)) + "");
                    input_payAmt.setText(Math.round((totalAmt)) + "");
                }


               // tvTotalPayAfter.setText("Rs. "+total);

            }
        });
    }

    private void setSelectedRecyclerView() {

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(OrderScreenActivity.this);
        recyclerViewSubCategory.setLayoutManager(mLayoutManager);
        subCatBookSelectedAdapter = new SubCatBookSelectedAdapter(mContext, selectedSubCatLists,this);
        recyclerViewSubCategory.setAdapter(subCatBookSelectedAdapter);
    }


    private void setListerner(){
        input_payAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               /* if (Utility.validateString(charSequence.toString())){
                    double payAmt= Double.parseDouble(input_payAmt.getText().toString().trim());
                    double totalAmt=0;

                    if (amounts==0){
                         totalAmt=myOrderModal.getPrice();
                    }else {
                       totalAmt= amounts;
                    }

                    if ((totalAmt-payAmt)<0) {
                        input_paylater.setText(0 + "");
                    }else {
                        input_paylater.setText((totalAmt - payAmt) + "");
                    }
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean validate(){
        boolean isValid=true;
        String email =input_Email.getText().toString().trim();
        String members =input_Members.getText().toString().trim();
        String mobile =input_Mobile.getText().toString().trim();
        String payAmt =input_payAmt.getText().toString().trim();
        String payLaterAmt =input_paylater.getText().toString().trim();

        if (!Utility.validateEmail(email)){
            isValid=false;
            input_Email.setError("Please enter valid email");

        }

        if (!Utility.validateString(mobile) &&mobile.length()!=10){
            isValid=false;
            input_Mobile.setError("Please enter valid mobile number");
        }

        if (!Utility.validateString(payAmt)){
            isValid=false;
            input_payAmt.setError("Please enter Amount to pay");
        }

        if (myOrderModal.getCategoryName().equalsIgnoreCase("Caterers")||myOrderModal.getCategoryName().equalsIgnoreCase("Caterer")){
      /* || myOrderModal.getCategoryName().equalsIgnoreCase("Photographer")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Flower Decoration")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Balloon Decoration")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Invitations Card")
                || myOrderModal.getCategoryName().equalsIgnoreCase("DJs")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Ghodiwala")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Dhool")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Band")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Persist")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Pandit Ji")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Tailoring")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Persist/Pandit Ji")
                || myOrderModal.getCategoryName().equalsIgnoreCase("Band/Dhool/Ghodiwala")) {*/

            if (Utility.validateString(input_Members.getText().toString().trim())){

                if (Integer.parseInt(input_Members.getText().toString().trim())<minMembers){
                    isValid=false;
                    input_Members.setError("Members range must be "+minMembers +" - "+maxMembers);
                }
            }else {
                isValid=false;
                input_Members.setError("Members range must be "+minMembers +" - "+maxMembers);
            }
        }





        return isValid;
    }

    private void initialize(){

        Button btnSummary=findViewById(R.id.btnSummary);
        tvPoints=findViewById(R.id.tvPoints);
        tvTotalPayAfter=findViewById(R.id.tvTotalPayAfter);
        tvTotalAmount=findViewById(R.id.tvTotalAmount);
        tvTotalGST=findViewById(R.id.tvTotalGST);
         tvTotalPay=findViewById(R.id.tvTotalPay);
        TextView tvCustomerName=findViewById(R.id.tvCustomerName);
         tvDate=findViewById(R.id.tvDate);
        TextView tvVendorName=findViewById(R.id.tvVendorName);
        TextView tvCategory=findViewById(R.id.tvCategory);
        TextView tvService=findViewById(R.id.tvService);
        TextView tvTotalAmt=findViewById(R.id.tvTotalAmt);
        TextView tvLocation=findViewById(R.id.tvLocation);

        input_Members=findViewById(R.id.input_Members);
        input_Email=findViewById(R.id.input_Email);
        input_Mobile=findViewById(R.id.input_Mobile);
        input_payAmt=findViewById(R.id.input_payAmt);
        input_paylater=findViewById(R.id.input_paylater);
        input_remarks=findViewById(R.id.input_remarks);

        tvCustomerName.setText(Prefs.getStringPrefs(AppConstants.NAME));
        tvVendorName.setText("Vendor: "+myOrderModal.getVendorName());
        tvCategory.setText("Service "+myOrderModal.getCategoryName());
        tvService.setText(myOrderModal.getSubCategoryName());
        tvTotalAmt.setText("Package Price Rs. "+myOrderModal.getPrice()+"");
        tvLocation.setText(myOrderModal.getServiceAt());
        input_Email.setText(Prefs.getStringPrefs(AppConstants.USER_NAME));

        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNeedHelpBottomSheet();
            }
        });

    }

    public void startPayment() {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Party Planner");
            options.put("description", myOrderModal.getCategoryName());
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");

         //   String payment = editTextPayment.getText().toString();

            double total = Double.parseDouble(input_payAmt.getText().toString().trim());
            total = Math.round(total) * 100;
            options.put("amount", Math.round(total));

            JSONObject preFill = new JSONObject();
            preFill.put("email", input_Email.getText().toString().trim());
            preFill.put("contact", input_Mobile.getText().toString().trim());

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {

        createOrder(200,"Payment successfully done!",razorpayPaymentID);
        Toast.makeText(this, "Payment successfully done! " + razorpayPaymentID, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            createOrder(code,response,"");
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }

    private void setData(String responseData, int code) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            if (jsonObject.optString("code").equalsIgnoreCase("200")) {

                Prefs.putIntegerPrefs(AppConstants.USER_POINTS,Prefs.getIntegerPrefs(AppConstants.USER_POINTS)-usedPoints);
                Toast.makeText(mContext, jsonObject.optString("message"), Toast.LENGTH_LONG).show();
                finish();
            } else {
                Prefs.putIntegerPrefs(AppConstants.USER_POINTS,Prefs.getIntegerPrefs(AppConstants.USER_POINTS)-usedPoints);
                finish();
                // Toast.makeText(mContext, jsonObject.optString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createOrder(int code,String response,String successResponse) {
      //  final ProgressDialog progressDialog = new ProgressDialog(OrderScreenActivity.this);
        progressDialog1.show();



        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_BOOKING_Submit;
        RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON,createJson(code,response,successResponse));
        Request request = APIClient.simplePostRequest(this, url,requestBody);

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog1.dismiss();
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog1.dismiss();

                        }
                    });
                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();

                        if (responseData != null) {




                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (code==200)
                                        setData(responseData,code);

                                    }
                                });



                        } else if (response.code() == AppConstants.BAD_REQUEST) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.INTERNAL_SERVER_ERROR) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.URL_NOT_FOUND) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_url_not_found), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.UNAUTHORIZE_ACCESS) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_unautorize_access), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.CONNECTION_OUT) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_connection_timed_out), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getVendorDetails() {
          final ProgressDialog progressDialog = new ProgressDialog(OrderScreenActivity.this);
        progressDialog.show();


        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("Id",myOrderModal.getVendorId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_BOOKING_VendorDetails;
        RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON,jsonObject.toString());
        Request request = APIClient.simplePostRequest(this, url,requestBody);

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                        }
                    });
                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();

                        if (responseData != null) {




                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        JSONObject jsonObject1=new JSONObject(responseData);

                                        minMembers=jsonObject1.optInt("MinMember");
                                        maxMembers=jsonObject1.optInt("MaxMember");
                                        JSONArray jsonArray=jsonObject1.optJSONArray("VendorSubCategory");
                                        for (int i=0;i<jsonArray.length();i++){
                                            JSONObject jsonObject2=jsonArray.optJSONObject(i);
                                            BookSubCategory bookSubCategory=new BookSubCategory();
                                            bookSubCategory.setId(jsonObject2.optInt("Id"));
                                          //  bookSubCategory.setQty(jsonObject2.optInt(""));
                                            bookSubCategory.setSubCategoryDescription(jsonObject2.optString("SubCategoryDescription"));
                                            bookSubCategory.setSubCategoryId(jsonObject2.optInt("SubCategoryId"));
                                            bookSubCategory.setSubCategoryName(jsonObject2.optString("SubCategoryName"));
                                            bookSubCategory.setSubCategoryPrice(jsonObject2.optDouble("SubCategoryPrice"));
                                            bookSubCategory.setVendorId(jsonObject2.optInt("VendorId"));

                                            selectedSubCatLists.add(bookSubCategory);

                                        }

                                        setSelectedRecyclerView();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });



                        } else if (response.code() == AppConstants.BAD_REQUEST) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.INTERNAL_SERVER_ERROR) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.URL_NOT_FOUND) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_url_not_found), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.UNAUTHORIZE_ACCESS) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_unautorize_access), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.CONNECTION_OUT) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_connection_timed_out), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        // toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }






    @Override
    protected void onResume() {
        super.onResume();

    }

    private String createJson(int code,String response,String successResponse) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("BookingDate",erg);
            jsonObject.put("BookingNumber","");
            jsonObject.put("VendorId",myOrderModal.getVendorId());
            jsonObject.put("CustomerId",Prefs.getIntegerPrefs(AppConstants.USER_ID));
            jsonObject.put("SubCategoryId",myOrderModal.getSubCategoryId());
            jsonObject.put("ServiceAtId",myOrderModal.getServiceAtId());
            jsonObject.put("CategoryId",myOrderModal.getCategoryId());
            jsonObject.put("BookingAmt",Double.parseDouble(input_payAmt.getText().toString().trim()));
            jsonObject.put("PayLaterAmt",Double.parseDouble(input_paylater.getText().toString().trim()));
            jsonObject.put("MobileNo",input_Mobile.getText().toString().trim());
            jsonObject.put("Email",input_Email.getText().toString().trim());
            jsonObject.put("Remarks",input_remarks.getText().toString().trim());

            jsonObject.put("PaymentGatewayStatusCode",code);
            jsonObject.put("PaymentGatewayStatusDescription",response);

            jsonObject.put("PaymentGatewayRemarks",successResponse);
            jsonObject.put("Code",0);
            jsonObject.put("Message","");
            jsonObject.put("DeviceToken","");
            jsonObject.put("NotificationMsg","");

            if (Utility.validateString(input_Members.getText().toString().trim()))
            jsonObject.put("Members",Integer.parseInt(input_Members.getText().toString().trim()));
            else {
                jsonObject.put("Members",1);
            }
            jsonObject.put("UsedPoints",usedPoints);
            jsonObject.put("PointsPerValue",Prefs.getIntegerPrefs(AppConstants.USER_PER_POINTS));

            JSONArray jsonArray=new JSONArray();

            for (int i =0;i<selectedSubCatLists.size();i++){
                JSONObject jsonObject1=new JSONObject();
                jsonObject1.put("Id",selectedSubCatLists.get(i).getId());
                jsonObject1.put("BookingNo",selectedSubCatLists.get(i).getBookingNo());
                jsonObject1.put("VendorId",selectedSubCatLists.get(i).getVendorId());
                jsonObject1.put("CustomerId",Prefs.getIntegerPrefs(AppConstants.USER_ID));
                jsonObject1.put("SubCategoryId",selectedSubCatLists.get(i).getSubCategoryId());
                jsonObject1.put("SubCategoryName",selectedSubCatLists.get(i).getSubCategoryName());
                jsonObject1.put("SubCategoryDescription",selectedSubCatLists.get(i).getSubCategoryDescription());
                jsonObject1.put("SubCategoryPrice",selectedSubCatLists.get(i).getSubCategoryPrice());
                jsonObject1.put("Qty",selectedSubCatLists.get(i).getQty());
                jsonArray.put(jsonObject1);
            }

            jsonObject.put("BookingDetails",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();


    }


































    private void confirmationDialog(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(OrderScreenActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(OrderScreenActivity.this);
        }
        builder.setTitle("Alert")
                .setMessage("Are you sure you want to exit the screen?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onBackPressed() {
        //    super.onBackPressed();
        confirmationDialog();
    }


    public  void dateDialogfromReturn() {
        final Calendar c = Calendar.getInstance();

        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(OrderScreenActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        erg = year+"";
                        erg += "-" + String.valueOf(monthOfYear + 1);
                        erg += "-" + String.valueOf(dayOfMonth);


                        tvDate.setText("Date: "+erg);

                    }

                }, y, m, d);
        dp.setTitle("Choose Booking Date");
        dp.show();

        dp.getDatePicker().setMinDate(System.currentTimeMillis()-1000);


    }

    @Override
    public void onListen(int position, String type) {


        amounts=0;
        listAmount=0;
        for (int i = 0; i < selectedSubCatLists.size(); i++) {

            amounts+=selectedSubCatLists.get(i).getTotal();
            listAmount+=selectedSubCatLists.get(i).getTotal();
        }

        tvTotalAmount.setText("Rs. "+amounts);

        double gst=18*amounts/100;
        amounts=amounts+gst;
        tvTotalGST.setText("Rs. "+gst);
        tvTotalPay.setText("Rs. "+amounts);
        int points=Prefs.getIntegerPrefs(AppConstants.USER_POINTS);
        int pointsUsed=Prefs.getIntegerPrefs(AppConstants.USER_USED_POINTS);

        tvPoints.setText((50*points/100)+" P");

        int availablepoints=(50*points/100);
        if (amounts>=availablepoints){
            amounts=(amounts-availablepoints);
            usedPoints=availablepoints;
        }else {
            usedPoints= (int) amounts;
            amounts=0;
        }
        int roundoff= (int) Math.round(amounts);
        tvTotalPayAfter.setText("Rs. "+roundoff);


        double totalAmt=0;

        if (listAmount==0){
            totalAmt=Math.round(myOrderModal.getPrice());
        }else {
            totalAmt= Math.round(amounts);
        }
        if (radioSexButton!=null &&Utility.validateString(radioSexButton.getText().toString())) {
            if (radioSexButton.getText().toString().equals("Pay 30%")) {
                //   double payAmt= Double.parseDouble(input_payAmt.getText().toString().trim());

                input_paylater.setText(Math.round((70 * totalAmt / 100) )+ "");
                input_payAmt.setText(Math.round((30 * totalAmt / 100)) + "");

            } else {
                input_paylater.setText(Math.round((0)) + "");
                input_payAmt.setText(Math.round((totalAmt)) + "");
            }
        }

    }


    public void openNeedHelpBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.summary_order, null);
        TextView txt_uninstall = view.findViewById(R.id.txt_uninstall);
        txt_uninstall.setText("Order Summary");

        RecyclerView recyclerViewSubCategorySummary=view.findViewById(R.id.recyclerViewSubCategorySummary);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(OrderScreenActivity.this);
        recyclerViewSubCategorySummary.setLayoutManager(mLayoutManager);
        SubCatViewBookSelectedAdapter subCatBookSelectedAdapter1 = new SubCatViewBookSelectedAdapter(mContext, selectedSubCatLists,this);
        recyclerViewSubCategorySummary.setAdapter(subCatBookSelectedAdapter1);

      TextView  tvTotalAmt=view.findViewById(R.id.tvTotalAmountSummary);
        TextView tvTGST=view.findViewById(R.id.tvTotalGSTSummary);
        TextView tvTPay=view.findViewById(R.id.tvTotalPaySummary);
        TextView tvPts=view.findViewById(R.id.tvPointsSummary);
        TextView tvTotalPayAfterSummary=view.findViewById(R.id.tvTotalPayAfterSummary);

        tvTotalAmt.setText(tvTotalAmount.getText().toString());
        tvTGST.setText(tvTotalGST.getText().toString());
        tvTPay.setText(tvTotalPay.getText().toString());
        tvPts.setText(tvPoints.getText().toString());
        tvTotalPayAfterSummary.setText(input_payAmt.getText().toString());

        Button btnDone=view.findViewById(R.id.btnDone);

        final Dialog mBottomSheetDialog = new Dialog(OrderScreenActivity.this,
                R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();




            }
        });


    }
}
