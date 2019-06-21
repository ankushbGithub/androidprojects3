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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResetPasswordActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {
    private Context mContext;
    private String TAG = ResetPasswordActivity.class.getSimpleName();
    private Toolbar toolbar;
    private AppCompatButton btnProceed;
    private TextInputLayout tilNewPassword, tilCnfPass;
    private TextInputEditText etNewPassword, etConfirmPassword;
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_act);
        mContext = ResetPasswordActivity.this;
        Intent i = getIntent();
        if (i != null) {
            email = i.getStringExtra("email");
        }
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
        toolbar.setTitle(getResources().getString(R.string.toolbar_title_change_password));
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnProceed = findViewById(R.id.btnProceed);

        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilCnfPass = findViewById(R.id.tilCnfPass);

        etNewPassword.addTextChangedListener(this);
        etConfirmPassword.addTextChangedListener(this);
        btnProceed.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (etNewPassword.getText().hashCode() == s.hashCode()) {
            if (!TextUtils.isEmpty(etNewPassword.getText().toString())) {
                List<String> errorList = isValid(etNewPassword.getText().toString());
                if (!errorList.isEmpty()) {
                    for (String error : errorList) {
                        tilNewPassword.setErrorEnabled(true);
                        tilNewPassword.setError(error);
                    }
                } else {
                    tilNewPassword.setErrorEnabled(false);
                    tilNewPassword.setError(null);
                }
            } else {
                tilNewPassword.setError("Please enter new password !!");

            }
        }
        if (etConfirmPassword.getText().hashCode() == s.hashCode()) {
            if (TextUtils.isEmpty(etConfirmPassword.getText().toString())) {
                tilCnfPass.setErrorEnabled(true);
                tilCnfPass.setError("Please enter confirm password");
            } else {
                tilCnfPass.setErrorEnabled(false);
                tilCnfPass.setError(null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnProceed) {
            hideKeyboard();
            String new_password = etNewPassword.getText().toString();
            String confirm_password = etConfirmPassword.getText().toString();
            List<String> errorList = isValid(new_password);
            if (!errorList.isEmpty()) {
                for (String error : errorList) {
                    tilNewPassword.setErrorEnabled(true);
                    tilNewPassword.setError(error);
                }
            } else if (TextUtils.isEmpty(confirm_password)) {
                tilCnfPass.setErrorEnabled(true);
                tilCnfPass.setError("Please enter confirm password");
            } else {
                if (new_password.equalsIgnoreCase(confirm_password)) {
                    if (NetUtil.isNetworkAvailable(mContext)) {
                        String code = SharedPrefUtil.getString(Constants.passwordCode.trim(),"",mContext);
                        validateWithServer(code);
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, "New password and confirm password do not match !!", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void validateWithServer(String code) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Code",code.trim());
            jsonObject.put("Email", email.trim());
            jsonObject.put("NewPassword", Objects.requireNonNull(etNewPassword.getText()).toString().trim());
            jsonObject.put("ConfirmPassword", Objects.requireNonNull(etConfirmPassword.getText()).toString().trim());
            Log.e(TAG,"Reset Password Request****"+jsonObject.toString());

            progressDialog.show();
            OkHttpClient okHttpClient = APIClient.getHttpClient();
            RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, jsonObject.toString());

            String url = CapsuleAPI.WEB_SERVICE_RESET_PASSWORD.trim();
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
                            runOnUiThread(new Runnable() {
                                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, jsonObject.optString("Message"), Toast.LENGTH_LONG).show();
                                    finishAffinity();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
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
                                            Utility.messageDialog(ResetPasswordActivity.this, "Bad Request Error", jsonObject1.optString("Message"));
                                            //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Utility.messageDialog(ResetPasswordActivity.this, "Bad Request Error", jsonObject1.optString("message"));

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
                                            Utility.messageDialog(ResetPasswordActivity.this, "Internal Server Error", jsonObject1.optString("Message"));
                                            //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Utility.messageDialog(ResetPasswordActivity.this, "Internal Server Error", jsonObject1.optString("message"));

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

    private static List<String> isValid(String passwordhere) {

        List<String> errorList = new ArrayList<>();

        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        if (TextUtils.isEmpty(passwordhere)) {
            errorList.add("Please enter password");
        }
        if (passwordhere.length() < 8) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!specailCharPatten.matcher(passwordhere).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!UpperCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!lowerCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!digitCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }

        return errorList;

    }

}
