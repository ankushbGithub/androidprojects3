package android.capsulepharmacy.com.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.MainActivity;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.customerModule.activity.CustomerCreationActivity;
import android.capsulepharmacy.com.listener.Init;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Config;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.activity.VendorCreationActivity;
import android.capsulepharmacy.com.vendor.modal.CategoryListModal;
import android.capsulepharmacy.com.vendor.modal.SelectedSubCatList;
import android.capsulepharmacy.com.vendor.modal.ServiceAtModal;
import android.capsulepharmacy.com.vendor.modal.SubListingModal;
import android.capsulepharmacy.com.vendor.singleton.AddressSingleton;
import android.capsulepharmacy.com.vendor.singleton.BankDetailsSingleton;
import android.capsulepharmacy.com.vendor.singleton.CategoryListSignleton;
import android.capsulepharmacy.com.vendor.singleton.SelectedSubCatListSingleton;
import android.capsulepharmacy.com.vendor.singleton.ServiceAtSingleton;
import android.capsulepharmacy.com.vendor.singleton.SubCatListingSingleton;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends RunTimePermissionActivity implements Init {

    private static final int REQUEST_PERMISSIONS = 20;
    private static final String[] ALL_PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Context mContext;
    private EditText input_email, input_password;
    private JSONObject objectSubmit;
    private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Toolbar toolbar;
    private TextView tvForgotPassword;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;
        init();
        BankDetailsSingleton.getInstance().clearArrayList();
        AddressSingleton.getInstance().clearArrayList();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LoginActivity.super.requestAppPermissions(ALL_PERMISSIONS, R.string.runtime_permissions_txt, REQUEST_PERMISSIONS, 0);
        }
    }

    private void init() {
        input_password = findViewById(R.id.input_password);
        input_email = findViewById(R.id.input_email);
        Button btn_login = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        AppCompatButton tvRegisterAsCustomer = findViewById(R.id.tvRegisterAsCustomer);
        AppCompatButton tvRegisterAsVendor = findViewById(R.id.tvRegisterAsVendor);
        toolbar = findViewById(R.id.toolbar);

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
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (applyLocalValidation()) {
                    if (NetUtil.isNetworkAvailable(mContext)) {
                        Utility.hideSoftKeyboard(LoginActivity.this);
                        submitLogin();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
                    }
                }

//                Utility.showToast("Login Successfully");

//                Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                Prefs.putStringPrefs("type","user");
//                startActivity(i);
//                finish();

            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        tvRegisterAsCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, CustomerCreationActivity.class);
                startActivity(i);
            }
        });
        tvRegisterAsVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankDetailsSingleton.getInstance().clearArrayList();
                AddressSingleton.getInstance().clearArrayList();
                fetchCategoryList(false, "");

            }
        });
        input_email.addTextChangedListener(new GenericTextWatcher(input_email));
        input_password.addTextChangedListener(new GenericTextWatcher(input_password));

    }
    private void fetchCategoryList(boolean isUpdate, String responseData) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_CAT_LIST;
        Log.e(TAG, "Get Request**" + url);

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
                        String rData = response.body().string();
                        Log.e(TAG, "Response***" + rData);
                        if (rData != null) {
                            runOnUiThread(() ->
                                    setDropDown(isUpdate, responseData, rData));
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
    private void setDropDown(boolean isUpdate, String responseData, String rData) {
        CategoryListSignleton.getInstance().clearArrayList();
        try {
            JSONArray cjsonArray = new JSONArray(rData);
            for (int i = 0; i < cjsonArray.length(); i++) {
                JSONObject jsonObject1 = cjsonArray.optJSONObject(i);
                CategoryListModal categoryListModal = new CategoryListModal();
                categoryListModal.Id = jsonObject1.optInt("Id");
                categoryListModal.Name = jsonObject1.optString("Name");
                CategoryListSignleton.getInstance().addToArray(categoryListModal);
            }
            fetchServcieAt(isUpdate, responseData);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void fetchServcieAt(boolean isUpdate, String responseData) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_SERVICE_AT;
        Log.e(TAG, "Get Request**" + url);

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
                        String rData = response.body().string();
                        Log.e(TAG, "Response***" + rData);
                        if (rData != null) {
                            runOnUiThread(() ->
                                    setServiceAtDown(isUpdate, responseData, rData));
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

    private void setServiceAtDown(boolean isUpdate, String responseData, String rData) {
        ServiceAtSingleton.getInstance().clearArrayList();
        try {
            JSONArray sArray = new JSONArray(rData);
            for (int j = 0; j < sArray.length(); j++) {
                JSONObject jsonObject1 = sArray.optJSONObject(j);
                ServiceAtModal serviceAtModal = new ServiceAtModal();
                serviceAtModal.Id = jsonObject1.optInt("Id");
                serviceAtModal.Name = jsonObject1.optString("Name");
                ServiceAtSingleton.getInstance().addToArray(serviceAtModal);
            }
            setSubCatList(isUpdate, responseData);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void setSubCatList(boolean isUpdate, String responseData) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_SUB_CAT_LIST;
        Log.e(TAG, "Get Request**" + url);

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
                        String rData = response.body().string();
                        Log.e(TAG, "Response***" + rData);
                        if (rData != null) {
                            runOnUiThread(() ->
                                    setSubCatListDown(isUpdate, responseData, rData));
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
    private void setSubCatListDown(boolean isUpdate, String responseData, String rData) {
        SubCatListingSingleton.getInstance().clearArrayList();
        SelectedSubCatListSingleton.getInstance().clearArrayList();
        try {
            JSONArray jsonArray = new JSONArray(rData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                SubListingModal subCatListModel = new SubListingModal();
                subCatListModel.Id = jsonObject.optInt("Id");
                subCatListModel.CategoryId = jsonObject.optInt("CategoryId");
                subCatListModel.Name = jsonObject.optString("Name");
                SubCatListingSingleton.getInstance().addToArray(subCatListModel);
            }


            if (Utility.validateString(responseData)){
                JSONObject jObj = new JSONObject(responseData);
                if (jObj.has("vendorSubCategoryList")){
                    JSONArray totalArr = jObj.getJSONArray("vendorSubCategoryList");
                    if (totalArr.length()>0){
                        for (int i=0;i<totalArr.length();i++){
                            JSONObject jsonObject = totalArr.optJSONObject(i);
                            SelectedSubCatList selectedSubCatList1 = new SelectedSubCatList();
                            selectedSubCatList1.Id= jsonObject.optInt("Id");
                            selectedSubCatList1.VendorId= jsonObject.optInt("VendorId");
                            selectedSubCatList1.SubCategoryId= jsonObject.optInt("SubCategoryId");
                            selectedSubCatList1.SubCategoryName= jsonObject.optString("SubCategoryName");
                            selectedSubCatList1.SubCategoryDescription= jsonObject.optString("SubCategoryDescription");
                            selectedSubCatList1.SubCategoryPrice= jsonObject.optInt("SubCategoryPrice");
                            SelectedSubCatListSingleton.getInstance().addToArray(selectedSubCatList1);
                        }
                    }
                }
            }




            if (isUpdate) {
                Intent i = new Intent(mContext, VendorCreationActivity.class);
                i.putExtra("response", responseData);
                i.putExtra("isUpdate", true);
                startActivity(i);
            } else {
                Intent i = new Intent(mContext, VendorCreationActivity.class);
                i.putExtra("response", responseData);
                i.putExtra("isUpdate", false);
                startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean applyLocalValidation() {
        String email = input_email.getText().toString();
        String password = input_password.getText().toString();
        if (!validateEmail(email)) {
            input_email.setError(getResources().getString(R.string.edittext_email_error_activity_login));
        } else if (TextUtils.isEmpty(password)) {
            input_password.setError(getResources().getString(R.string.edittext_password_error_activity_login));
        } else {
            return true;
        }

        return false;
    }

    private boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validate() {
        objectSubmit = new JSONObject();
        boolean isValid = true;
        String email = input_email.getText().toString().trim();
        String password = input_password.getText().toString().trim();

        try {
            objectSubmit.put("email", email);
            objectSubmit.put("pwd", password);
            objectSubmit.put("source", "email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!Utility.validateEmail(email)) {
            input_email.setError("please enter valid email");
            isValid = false;
        }
        if (!Utility.validateString(password)) {
            input_password.setError("please enter password");
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if (requestCode == REQUEST_PERMISSIONS) {

        }

    }

    public void submitLogin() {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.show();
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String requestString = "grant_type" + "=" + "password" + "&" + "username" + "=" + input_email.getText().toString() + "&" + "password" + "="+input_password.getText().toString();
        RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, requestString);
        String url = CapsuleAPI.WEBSERVICE_LOGIN;

        final Request request = APIClient.postRequest(this, url, requestBody);
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
            public void onResponse(Call call, final Response response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
                try {
                    if (response != null && response.isSuccessful()) {
                        final String responseData = response.body().string();
                        if (responseData != null) {

                            final JSONObject jsonObject = new JSONObject(responseData);
                            runOnUiThread(() -> {
                                Prefs.putStringPrefs(AppConstants.ACCESS_TOKEN, jsonObject.optString("access_token"));
                                Prefs.putStringPrefs(AppConstants.TOKEN, jsonObject.optString("token_type"));
                                Prefs.putStringPrefs(AppConstants.USER_NAME, jsonObject.optString("userName"));


                                userRoleAPi(jsonObject.optString("userName"));

                            });

                        }

                    } else if (response.code() == AppConstants.BAD_REQUEST) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String errorBodyString = null;
                                try {
                                    errorBodyString = response.body().string();

                                    try {
                                        JSONObject jsonObject1=new JSONObject(errorBodyString);
                                        Utility.messageDialog(LoginActivity.this,"Error",jsonObject1.optString("error_description"));
                                        //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show();
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

                } catch (Exception e) {
                    e.printStackTrace();


                }
            }
        });
    }

    public void userRoleAPi(String email) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        //   regId = pref.getString("regId", null);
        String fcmId = pref.getString("regId", null);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.show();
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("EmailId", email);
            if (Utility.validateString(fcmId)){
                jsonObject.put("DeviceToken",fcmId);
            }else {
                jsonObject.put("DeviceToken", "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, jsonObject.toString());
        String url = CapsuleAPI.WEB_SERVICE_USERROLE;

        final Request request = APIClient.simplePostRequest(this, url, requestBody);
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
            public void onResponse(Call call, final Response response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
                try {
                    if (response != null && response.isSuccessful()) {
                        final String responseData = response.body().string();
                        if (responseData != null) {

                            final JSONObject jsonObject = new JSONObject(responseData);
                            runOnUiThread(() -> {
                                Prefs.putStringPrefs(AppConstants.NAME, jsonObject.optString("UserName"));
                                Prefs.putIntegerPrefs(AppConstants.USER_ID, jsonObject.optInt("UserId"));
                                Prefs.putStringPrefs(AppConstants.USER_ROLE, jsonObject.optString("UserRole"));
                                Prefs.putIntegerPrefs(AppConstants.USER_POINTS, jsonObject.optInt("Points"));
                                Prefs.putIntegerPrefs(AppConstants.USER_USED_POINTS, jsonObject.optInt("MaxUsePoints"));
                                Prefs.putIntegerPrefs(AppConstants.USER_PER_POINTS, jsonObject.optInt("PointsPerValue"));


                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            });

                        }

                    } else if (response.code() == AppConstants.BAD_REQUEST) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String errorBodyString = null;
                                try {
                                    errorBodyString = response.body().string();

                                    try {
                                        JSONObject jsonObject1=new JSONObject(errorBodyString);
                                        Utility.messageDialog(LoginActivity.this,"Error",jsonObject1.optString("error_description"));
                                        //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show();
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

                } catch (Exception e) {
                    e.printStackTrace();


                }
            }
        });
    }

    public void deviceInfo(String userid) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        //   regId = pref.getString("regId", null);
        String fcmId = pref.getString("regId", null);
        JSONObject object = new JSONObject();

        try {
            if (Utility.validateString(fcmId)) {
                object.put("fcmToken", fcmId);
            } else {
                object.put("fcmToken", "NA");
            }

            object.put("appId", "NA");
            object.put("deviceModel", "NA");
            object.put("deviceId", "NA");
            object.put("platform", "NA");
            object.put("deviceOSVersion", "NA");
            object.put("manufacturer", "NA");
            object.put("appVersion", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//https://s3.ap-south-1.amazonaws.com/image.orders/images/order/5b916047bbc5244cd4eac105/boom.png
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.show();
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, object.toString());
        String url = CapsuleAPI.WEB_SERVICE_BASE_URL + "user/" + userid + "/device";

        final Request request = APIClient.getPostRequest(this, url, requestBody);
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
                //  dismissProgress();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // dismissProgress();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
                try {
                    if (response != null && response.isSuccessful()) {


                        final String responseData = response.body().string();
                        if (responseData != null) {

                            final JSONObject jsonObject = new JSONObject(responseData);
                            //saveResponseLocalCreateOrder(jsonObject,requestId);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (jsonObject.optString("errorMsg").equalsIgnoreCase("")) {
                                        Utility.showToast("Login Successfully");
                                        Utility.hideSoftKeyboard(LoginActivity.this);
                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Utility.showToast(jsonObject.optString("errorMsg"));
                                    }
                                }
                            });

                        }

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

                } catch (Exception e) {
                    e.printStackTrace();


                }
            }
        });
    }

    private class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                    input_email.setError(null);

                    break;
                case R.id.input_password:
                    input_password.setError(null);
                default:
                    break;
            }
        }
    }

}
