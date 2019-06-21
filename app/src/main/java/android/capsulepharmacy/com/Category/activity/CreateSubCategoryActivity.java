package android.capsulepharmacy.com.Category.activity;

import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateSubCategoryActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {
    private Context mContext;
    private String TAG = CreateSubCategoryActivity.class.getSimpleName();

    private Button btnSubmit;
    private TextInputLayout tilSubCatName;
    private TextInputEditText tieSubCatName;
    private Toolbar toolbar;

    private int id, catId;
    private String name;
    private boolean isEdit, isFromCat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_sub_cat);
        mContext = CreateSubCategoryActivity.this;

        Intent i = getIntent();
        if (i != null) {
            id = i.getIntExtra("id", 0);
            catId = i.getIntExtra("catId", 0);
            name = i.getStringExtra("Name");
            isEdit = i.getBooleanExtra("isEdit", false);
            isFromCat = i.getBooleanExtra("isFromCat", false);
        }

        initViews();
        applyInit();
    }

    private void applyInit() {
        setValue();
    }

    private void setValue() {
        try {
            if (isEdit) {
                tieSubCatName.setText(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilSubCatName = findViewById(R.id.tilSubCatName);
        tieSubCatName = findViewById(R.id.tieSubCatName);
        btnSubmit = findViewById(R.id.btnSubmit);
        if (isEdit) {
            btnSubmit.setText("Update");
        } else {
            btnSubmit.setText("Create");
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle(getResources().getString(R.string.toolbar_title_sub_cat_creation));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tieSubCatName.addTextChangedListener(this);

        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                checkValidation();
            default:
                break;
        }
    }

    private void checkValidation() {
        boolean isValidated = false;
        String Name = tieSubCatName.getText().toString();

        if (!Utility.validateString(Name)) {
            isValidated = true;
            tilSubCatName.setErrorEnabled(true);
            tilSubCatName.setError("Please enter Sub-Category Name");
        }

        if (!isValidated) {
            if (NetUtil.isNetworkAvailable(mContext)) {
                submitData();
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitData() {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", id);
            jsonObject.put("CategoryId", catId);
            jsonObject.put("Name", tieSubCatName.getText().toString());

            progressDialog.show();
            Log.e(TAG, "*JsonArr**" + jsonObject.toString());
            OkHttpClient okHttpClient = APIClient.getHttpClient();
            RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, jsonObject.toString());
            Request request;
            if (isEdit) {
                String url = CapsuleAPI.WEB_SERVICE_SUB_CAT_UPDATE + id;
                request = APIClient.getPutRequest(this, url, requestBody);
            } else {
                String url = CapsuleAPI.WEB_SERVICE_SUB_CAT_LIST;
                request = APIClient.getCustomerCreatePostRequest(this, url, requestBody);
            }

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                    try {
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (isFromCat) {
                                            if (isEdit) {
                                                Toast.makeText(mContext, "Sub Category updated successfully", Toast.LENGTH_LONG).show();
                                                finish();
                                            } else {
                                                Toast.makeText(mContext, "Sub Category created successfully", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        } else {
                                            if (isEdit) {
                                                Intent i = new Intent(mContext, SubCategoryListingActivity.class);
                                                i.putExtra("catId", catId);
                                                finish();
                                                Toast.makeText(mContext, "Sub Category updated successfully", Toast.LENGTH_LONG).show();

                                            } else {
                                                Intent i = new Intent(mContext, SubCategoryListingActivity.class);
                                                i.putExtra("catId", catId);
                                                finish();
                                                Toast.makeText(mContext, "Sub Category created successfully", Toast.LENGTH_LONG).show();
                                            }
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == tieSubCatName.getEditableText()) {
            tilSubCatName.setError(null);
            tilSubCatName.setErrorEnabled(false);
        }
    }


}
