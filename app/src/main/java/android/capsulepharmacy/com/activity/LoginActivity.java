package android.capsulepharmacy.com.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.MainActivity;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.listener.Init;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Config;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private Context mContext;
    private EditText input_email, input_password;
    private JSONObject objectSubmit;
    private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;
        init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LoginActivity.super.requestAppPermissions(ALL_PERMISSIONS, R.string.runtime_permissions_txt, REQUEST_PERMISSIONS, 0);
        }
    }

    private void init() {
        input_password = findViewById(R.id.input_password);
        input_email = findViewById(R.id.input_email);
        Button btn_login = findViewById(R.id.btn_login);
        TextView tvRegister = findViewById(R.id.tvRegister);

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
//
//                Utility.showToast("Login Successfully");
//
//                Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                Prefs.putStringPrefs("type","user");
//                startActivity(i);
//                finish();

            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
        input_email.addTextChangedListener(new GenericTextWatcher(input_email));
        input_password.addTextChangedListener(new GenericTextWatcher(input_password));

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


                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
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
