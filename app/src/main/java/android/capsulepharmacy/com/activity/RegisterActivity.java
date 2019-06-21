package android.capsulepharmacy.com.activity;

import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.MainActivity;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Config;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity implements TextWatcher {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText input_password, input_email, input_name, input_Role;
    private Context mContext;
    private JSONObject objectSubmit;

    private TextInputLayout tilName, tilEmail, tilPassword, tilRole;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;
        setHeader();
        init();
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

    private void init() {
        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilRole = findViewById(R.id.tilRole);

        input_password = findViewById(R.id.input_password);
        input_email = findViewById(R.id.input_email);
        input_name = findViewById(R.id.input_name);
        input_Role = findViewById(R.id.input_Role);


        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utility.showToast("Register Successfully");
//                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
//                Prefs.putStringPrefs("type","user");
//                startActivity(i);
//                finish();

                if (NetUtil.isNetworkAvailable(mContext)) {
                    Utility.hideSoftKeyboard(RegisterActivity.this);
                    checkValidation();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
        input_name.addTextChangedListener(this);
        input_email.addTextChangedListener(this);
        input_password.addTextChangedListener(this);
        input_Role.addTextChangedListener(this);

    }

    private boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void checkValidation() {
        boolean isValid = true;
        String name = input_name.getText().toString().trim();
        String email = input_email.getText().toString().trim();
        String password = input_password.getText().toString().trim();
        String role = input_Role.getText().toString().trim();
        List<String> errorList = isValidPassword(password);

        if (!Utility.validateString(name)) {
            tilName.setErrorEnabled(true);
            tilName.setError("Please enter your name");
            isValid = false;
        }
        if (!Utility.validateString(email)) {
            isValid = false;
            tilEmail.setErrorEnabled(true);
            tilEmail.setError(getResources().getString(R.string.invalid_email));
        }
        if (Utility.validateString(email)) {
            if (!emailValidator(email)) {
                isValid = false;
                tilEmail.setErrorEnabled(true);
                tilEmail.setError(getResources().getString(R.string.invalid_email));
            }
        }
        if (!Utility.validateEmail(email)) {
            tilEmail.setErrorEnabled(true);
            tilEmail.setError("Please enter valid email");
            isValid = false;
        }
        if (!Utility.validateString(password)) {
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("Please enter password");
            isValid = false;
        }
        if (password.length() < 8) {
            isValid = false;
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("Password must be of 8 characters");
        }
        if (!errorList.isEmpty()) {
            for (String error : errorList) {
                isValid = false;
                tilPassword.setErrorEnabled(true);
                tilPassword.setError(error);
            }
        }
        if (!Utility.validateString(role)) {
            tilRole.setErrorEnabled(true);
            tilRole.setError("Please enter Role");
            isValid = false;

        }

        if (isValid) {
            submitRegister();
        }

    }

    private static List<String> isValidPassword(String password) {
        List<String> errorList = new ArrayList<>();

        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        if (TextUtils.isEmpty(password)) {
            errorList.add("Please enter password");
        }
        if (password.length() < 8) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!specailCharPatten.matcher(password).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!UpperCasePatten.matcher(password).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!lowerCasePatten.matcher(password).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!digitCasePatten.matcher(password).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        return errorList;
    }


    public void submitRegister() {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Email", input_email.getText().toString().trim());
            jsonObject.put("Password", input_password.getText().toString().trim());
            jsonObject.put("ConfirmPassword", input_password.getText().toString().trim());
            jsonObject.put("UserRoles", input_Role.getText().toString().trim());
            jsonObject.put("UserName", input_name.getText().toString().trim());
            Log.e(TAG, "Register Request Json***" + jsonObject.toString().trim());

            OkHttpClient okHttpClient = APIClient.getHttpClient();
            RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, jsonObject.toString());
            String url = CapsuleAPI.WEB_SERVICE_CUSTOMER_REGISTER;

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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setData(responseData);
                                    }
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
                                            Utility.messageDialog(RegisterActivity.this,"Error",jsonObject1.optString("Message"));
                                          //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void userRoleAPi(String email) {
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.show();
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("EmailId", email);
            jsonObject.put("DeviceToken","");
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


                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
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
                                        Utility.messageDialog(RegisterActivity.this,"Error",jsonObject1.optString("error_description"));
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
    private void setData(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            if (jsonObject.optInt("Code") == 200) {

                Toast.makeText(mContext, jsonObject.optString("Message"), Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(mContext, jsonObject.optString("Message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
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
                                        Utility.showToast("Register Successfully");
                                        Utility.hideSoftKeyboard(RegisterActivity.this);
                                        Intent intent = new Intent();
                                        setResult(RESULT_OK, intent);
                                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == input_name.getEditableText()) {
            tilName.setErrorEnabled(false);
            tilName.setError(null);
        }
        if (s == input_email.getEditableText()) {
            tilEmail.setErrorEnabled(false);
            tilEmail.setError(null);
        }
        if (s == input_password.getEditableText()) {
            tilPassword.setErrorEnabled(false);
            tilPassword.setError(null);
        }
        if (s == input_Role.getEditableText()) {
            tilRole.setErrorEnabled(false);
            tilRole.setError(null);
        }
    }
}
