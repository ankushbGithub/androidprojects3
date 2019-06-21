package android.capsulepharmacy.com.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.listener.MyListener;
import android.capsulepharmacy.com.modal.BookSubCategory;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.adapter.SubCatFilterSelectedAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * The type Edit expandable po details activity.
 */
public class CategoryFilterActivity2Step extends BaseActivity implements PaymentResultListener, MyListener, SubCatFilterSelectedAdapter.CheckBoxChecked {
    Context context;
    private String erg="",categoryName="";
    private EditText input_Members,input_Email,input_Mobile,input_payAmt,input_paylater,input_remarks;
    private MyOrderModal myOrderModal;
    private TextView tvDate;
     ProgressDialog progressDialog1;
     private RecyclerView recyclerViewSubCategory;
     private SubCatFilterSelectedAdapter subCatBookSelectedAdapter;
    private ArrayList<BookSubCategory> selectedSubCatLists=new ArrayList<>();
    private int minMembers,maxMembers;
    private TextView tvTotalPay,tvTotalGST,tvTotalAmount;
    private double amounts=0;
    private RadioGroup radioGroup;
    private RadioButton radioSexButton;
    private int catId;
    private TextInputLayout tvMembers;
    private boolean isCheckBoxSelected = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_filter_activity);
        context= CategoryFilterActivity2Step.this;
        setHeader();
        LinearLayout llItems=findViewById(R.id.llItems);
        llItems.setVisibility(View.VISIBLE);
        Intent i = getIntent();
        if (i != null) {
            catId = i.getIntExtra("categoryId", 0);
            categoryName = i.getStringExtra("categoryName");
        }
        applyInit();
        recyclerViewSubCategory=findViewById(R.id.recyclerViewSubCategory);
        radioGroup=(RadioGroup)findViewById(R.id.radio);

      // myOrderModal=(MyOrderModal) i.getSerializableExtra("model");
        Button btnProceed=findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chekValidation();


             /*   Intent i = new Intent(mContext, BookVendor.class);
                i.putExtra("category",catId);
                mContext.startActivity(i);
*/
            }
        });
      //  dateDialogfromReturn();
        initialize();
        //setListerner();
        //getVendorDetails();


        if (categoryName.equalsIgnoreCase("Caterers")||categoryName.equalsIgnoreCase("Caterer")) {

            tvMembers.setVisibility(View.GONE);
        }else {
            tvMembers.setVisibility(View.GONE);
        }




            //setSelectedRecyclerView();



     //   inializeViews();



        setRadio();

    }

    private void chekValidation() {
        boolean isSelected= false;
        for (int i=0;i<selectedSubCatLists.size();i++){
            BookSubCategory bookSubCategory = selectedSubCatLists.get(i);
            if (bookSubCategory.isSelected()){
                isSelected = true;
            }
        }

        if (isSelected){
            Intent i = new Intent(mContext, CategoryFilterActivity3Step.class);
            i.putExtra("categoryId", catId);
            i.putExtra("categoryName", categoryName);
            mContext.startActivity(i);
        }else {
            Toast.makeText(mContext,"Please select Items",Toast.LENGTH_LONG).show();

        }
    }

    private void applyInit() {
        if (NetUtil.isNetworkAvailable(mContext)) {
            getSubCategoryList(catId);

        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void getSubCategoryList(int catId) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_SUB_CAT_LIST;
   //     Log.e(TAG, "Get Request**" + url);

        final Request request = APIClient.getCatRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {

                runOnUiThread(() -> {
                    progressDialog.dismiss();

                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();

                });
                try {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();

                        if (responseData != null) {
                            runOnUiThread(() ->
                                    setServerData(responseData,catId));
                        }

                    } else if (response.code() == Constants.BAD_REQUEST) {
                        runOnUiThread(() -> {
                            Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show();
                        });
                    } else if (response.code() == Constants.INTERNAL_SERVER_ERROR) {
                        runOnUiThread(() -> Toast.makeText(mContext, getResources().getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show());
                    } else if (response.code() == Constants.URL_NOT_FOUND) {
                        runOnUiThread(() -> Toast.makeText(mContext, getResources().getString(R.string.error_url_not_found), Toast.LENGTH_SHORT).show());
                    } else if (response.code() == Constants.UNAUTHORIZE_ACCESS) {
                        runOnUiThread(() -> Toast.makeText(mContext, getResources().getString(R.string.error_unautorize_access), Toast.LENGTH_SHORT).show());
                    } else if (response.code() == Constants.CONNECTION_OUT) {
                        runOnUiThread(() -> Toast.makeText(mContext, getResources().getString(R.string.error_connection_timed_out), Toast.LENGTH_SHORT).show());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setServerData(String responseData, int catId) {

        try {
            JSONArray cjsonArray = new JSONArray(responseData);
            for (int i = 0; i < cjsonArray.length(); i++) {
                JSONObject jsonObject1 = cjsonArray.optJSONObject(i);

                if (jsonObject1.optInt("CategoryId")==catId) {
                    BookSubCategory bookSubCategory = new BookSubCategory();
                    bookSubCategory.setId(jsonObject1.optInt("Id"));
                    //  bookSubCategory.setQty(jsonObject2.optInt(""));
                    bookSubCategory.setSubCategoryDescription(jsonObject1.optString("SubCategoryDescription"));
                    bookSubCategory.setSubCategoryId(jsonObject1.optInt("Id"));
                    bookSubCategory.setSubCategoryName(jsonObject1.optString("Name"));
                    bookSubCategory.setSubCategoryPrice(jsonObject1.optDouble("SubCategoryPrice"));
                    bookSubCategory.setVendorId(jsonObject1.optInt("VendorId"));
                    bookSubCategory.setSelected(false);
                    selectedSubCatLists.add(bookSubCategory);
                }

            }

            setSelectedRecyclerView();




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRadio(){
        /*radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId=radioGroup.getCheckedRadioButtonId();
                radioSexButton=(RadioButton)findViewById(selectedId);
                double totalAmt=0;

                if (amounts==0){
                    totalAmt=myOrderModal.getPrice();
                }else {
                    totalAmt= amounts;
                }
                if (radioSexButton.getText().equals("Pay 30%")){
                 //   double payAmt= Double.parseDouble(input_payAmt.getText().toString().trim());

                        input_paylater.setText((70*totalAmt/100) + "");
                        input_payAmt.setText((30*totalAmt/100) + "");

                }else {
                    input_paylater.setText((0) + "");
                    input_payAmt.setText((totalAmt) + "");
                }
            }
        });*/
    }

    private void setSelectedRecyclerView() {

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(CategoryFilterActivity2Step.this);
        recyclerViewSubCategory.setLayoutManager(mLayoutManager);
        subCatBookSelectedAdapter = new SubCatFilterSelectedAdapter(mContext, this,selectedSubCatLists,this);
        recyclerViewSubCategory.setAdapter(subCatBookSelectedAdapter);
    }


    private void setListerner(){
        input_payAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (Utility.validateString(charSequence.toString())){
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
                }
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

        if (myOrderModal.getCategoryName().equalsIgnoreCase("Caterers")||myOrderModal.getCategoryName().equalsIgnoreCase("Caterer")) {

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

        tvMembers=findViewById(R.id.tvMembers);
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

     /*   tvCustomerName.setText(Prefs.getStringPrefs(AppConstants.NAME));
        tvVendorName.setText(myOrderModal.getVendorName());
        tvCategory.setText(myOrderModal.getCategoryName());
        tvService.setText(myOrderModal.getSubCategoryName());
        tvTotalAmt.setText("Package Price Rs. "+myOrderModal.getPrice()+"");
        tvLocation.setText(myOrderModal.getServiceAt());
        input_Email.setText(Prefs.getStringPrefs(AppConstants.USER_NAME));
*/


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
            total = total * 100;
            options.put("amount", total);

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

                Toast.makeText(mContext, jsonObject.optString("message"), Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(mContext, jsonObject.optString("message"), Toast.LENGTH_LONG).show();
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
          final ProgressDialog progressDialog = new ProgressDialog(CategoryFilterActivity2Step.this);
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

                                      //  minMembers=jsonObject1.optInt("MinMember");
                                      //  maxMembers=jsonObject1.optInt("MaxMember");
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
                finish();
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
            jsonObject.put("UsedPoints",0);
            jsonObject.put("PointsPerValue",0);

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
            builder = new AlertDialog.Builder(CategoryFilterActivity2Step.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(CategoryFilterActivity2Step.this);
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
        finish();
    }


    public  void dateDialogfromReturn() {
        final Calendar c = Calendar.getInstance();

        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(CategoryFilterActivity2Step.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        erg = year+"";
                        erg += "-" + String.valueOf(monthOfYear + 1);
                        erg += "-" + String.valueOf(dayOfMonth);


                        tvDate.setText(erg);

                    }

                }, y, m, d);
        dp.setTitle("Choose Booking Date");
        dp.show();

        dp.getDatePicker().setMinDate(System.currentTimeMillis()-1000);


    }

    @Override
    public void onListen(int position, String type) {


        amounts=0;
        for (int i = 0; i < selectedSubCatLists.size(); i++) {

            amounts+=selectedSubCatLists.get(i).getTotal();
        }

        tvTotalAmount.setText("Rs. "+amounts);

        double gst=18*amounts/100;
        amounts=amounts+gst;
        tvTotalGST.setText("Rs. "+gst);
        tvTotalPay.setText("Rs. "+amounts);
    }

    @Override
    public void onCheckBoxClicked(boolean isChecked) {
        isCheckBoxSelected = isChecked;
    }
}
