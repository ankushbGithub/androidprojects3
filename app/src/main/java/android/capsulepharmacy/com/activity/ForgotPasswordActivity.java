package android.capsulepharmacy.com.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.SharedPrefUtil;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.utility.VerifyEmailOtp;
import android.capsulepharmacy.com.vendor.activity.VendorDetailsActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPasswordActivity extends AppCompatActivity implements TextWatcher, VerifyEmailOtp.VerifyOtp {
    private Context mContext;
    private String TAG = ForgotPasswordActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TextInputLayout tilEmail;
    private TextInputEditText tieEmail;
    private AppCompatButton btnProceed;
    private String otpServer = "";
    private VerifyEmailOtp verifyEmailOtp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_act);
        mContext = ForgotPasswordActivity.this;
        initViews();
        applyInitValues();
    }

    private void applyInitValues() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle(getResources().getString(R.string.toolbar_title_forgot_password));
    }

    @Override
    public void onBackPressed() {
        SharedPrefUtil.clearSharedPreferences(mContext);
        finish();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilEmail = findViewById(R.id.tilEmail);
        tieEmail = findViewById(R.id.tieEmail);
        btnProceed = findViewById(R.id.btnProceed);

        tieEmail.addTextChangedListener(this);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (applyLocalValidation()) {
                    if (NetUtil.isNetworkAvailable(mContext)) {

                        callForgotPasswordAPI();

                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void callForgotPasswordAPI() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Email", tieEmail.getText().toString().trim());

            progressDialog.show();
            OkHttpClient okHttpClient = APIClient.getHttpClient();
            RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, jsonObject.toString());

            String url = CapsuleAPI.WEB_SERVICE_FORGOT_PASSWORD.trim();
            Log.e(TAG, "Url::" + url);
            final Request request = APIClient.getPostRequest(mContext, url, requestBody);
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    progressDialog.dismiss();
                }

                @Override
                public void onResponse(Call call, final Response response) {
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
                            Log.e(TAG, "Response***" + responseData);
                            final JSONObject jsonObject = new JSONObject(responseData);
                            Log.e(TAG, "Response****" + jsonObject.toString());

                            runOnUiThread(new Runnable() {
                                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, jsonObject.optString("Message"), Toast.LENGTH_LONG).show();
                                    otpServer = jsonObject.optString("OTP");
                                    SharedPrefUtil.putString(Constants.passwordCode.trim(), jsonObject.optString("Token"), mContext);
                                    showOTPScreen(jsonObject.optString("Email"));

                                }
                            });

                        } else if (response.code() == Constants.BAD_REQUEST) {
                            runOnUiThread(() -> {
                                String errorBodyString = null;
                                try {
                                    errorBodyString = response.body().string();

                                    try {
                                        JSONObject jsonObject1 = new JSONObject(errorBodyString);
                                        if (jsonObject1.has("Message")) {
                                            Utility.messageDialog(ForgotPasswordActivity.this, "Bad Request Error", jsonObject1.optString("Message"));
                                            //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Utility.messageDialog(ForgotPasswordActivity.this, "Bad Request Error", jsonObject1.optString("message"));

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else if (response.code() == Constants.INTERNAL_SERVER_ERROR) {
                            runOnUiThread(() -> {
                                String errorBodyString = null;
                                try {
                                    errorBodyString = response.body().string();

                                    try {
                                        JSONObject jsonObject1 = new JSONObject(errorBodyString);
                                        if (jsonObject1.has("Message")) {
                                            Utility.messageDialog(ForgotPasswordActivity.this, "Internal Server Error", jsonObject1.optString("Message"));
                                            //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Utility.messageDialog(ForgotPasswordActivity.this, "Internal Server Error", jsonObject1.optString("message"));

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else if (response.code() == Constants.URL_NOT_FOUND) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error_url_not_found), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == Constants.UNAUTHORIZE_ACCESS) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error_unautorize_access), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == Constants.CONNECTION_OUT) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error_connection_timed_out), Toast.LENGTH_SHORT).show();
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

    private void showOTPScreen(String email) {
        verifyEmailOtp = new VerifyEmailOtp(mContext, email, this);
        verifyEmailOtp.setCancelable(false);
        verifyEmailOtp.setCanceledOnTouchOutside(false);
        verifyEmailOtp.show();
    }

    private boolean applyLocalValidation() {
        String email = tieEmail.getText().toString();
        if (!Utility.validateEmail(email)) {
            tilEmail.setErrorEnabled(true);
            tilEmail.setError(getResources().getString(R.string.edittext_email_error_activity_login));
        } else {
            return true;
        }
        return false;
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            assert getSystemService(Context.INPUT_METHOD_SERVICE) != null;
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (tieEmail.getText().hashCode() == s.hashCode()) {
            if (!Utility.validateEmail(tieEmail.getText().toString())) {
                tilEmail.setErrorEnabled(true);
                tilEmail.setError(getResources().getString(R.string.edittext_email_error_activity_login));
            } else {
                tilEmail.setErrorEnabled(false);
                tilEmail.setError(null);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SharedPrefUtil.clearSharedPreferences(mContext);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onVerifyOtp(String email, String otp) {
        if (Utility.validateString(otp)){
            if (otpServer.trim().equalsIgnoreCase(otp.trim())) {
                Intent i = new Intent(mContext, ResetPasswordActivity.class);
                i.putExtra("email", email);
                startActivity(i);
            } else {
                Toast.makeText(mContext, "OTP mismatch!!", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(mContext, "Please enter OTP", Toast.LENGTH_LONG).show();

        }

    }
}
