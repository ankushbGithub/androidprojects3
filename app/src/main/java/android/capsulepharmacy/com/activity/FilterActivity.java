package android.capsulepharmacy.com.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.MainActivity;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.base.CustomFilterDialog;
import android.capsulepharmacy.com.listener.Init;
import android.capsulepharmacy.com.modal.FilterModel;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Config;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.modal.CategoryListModal;
import android.capsulepharmacy.com.vendor.modal.ServiceAtModal;
import android.capsulepharmacy.com.vendor.modal.ServiceFilterModal;
import android.capsulepharmacy.com.vendor.singleton.CategoryListSignleton;
import android.capsulepharmacy.com.vendor.singleton.ServiceAtSingleton;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class FilterActivity extends AppCompatActivity {


    private Context mContext;
    private EditText input_searchBox;
    private JSONObject objectSubmit;
    private int rating=5;
    private ArrayList<FilterModel> filterModels = new ArrayList<>();
    private ArrayList<ServiceFilterModal> serviceFilterModals = new ArrayList<>();
    private int intentCategoryId,fromMain;
    private EditText etCategory,etMinPrice,etMaxPrice;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        mContext = FilterActivity.this;
        setHeader();

        Intent i=getIntent();
        fromMain=i.getIntExtra("fromMain",0);
        init();
        fetchCategoryList(false,"");


    }
    public void setHeader() {
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        input_searchBox = findViewById(R.id.input_searchBox);
        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingsBar);
        Button btn_search = findViewById(R.id.btn_search);
        etCategory = findViewById(R.id.etCategory);
        etMinPrice = findViewById(R.id.etMinPrice);
        etMaxPrice = findViewById(R.id.etMaxPrice);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating= (int) v;
            }
        });

        etCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategory();
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    if (NetUtil.isNetworkAvailable(mContext)) {
                        double minPrice=0,maxPrice=0;
                        if (Utility.validateString(etMinPrice.getText().toString())){
                            minPrice= Double.parseDouble(etMinPrice.getText().toString());
                        }
                        if (Utility.validateString(etMaxPrice.getText().toString())){
                            maxPrice= Double.parseDouble(etMaxPrice.getText().toString());
                        }
                        if (minPrice>maxPrice){
                            Toast.makeText(mContext,"min price must be less than or equal to max price.",Toast.LENGTH_LONG).show();
                            return;
                        }
                        Utility.hideSoftKeyboard(FilterActivity.this);
                        if (fromMain==1){
                            Intent i = new Intent(FilterActivity.this,BookVendor.class);
                            i.putExtra("searchText", input_searchBox.getText().toString());
                            i.putExtra("rating", rating);
                            i.putExtra("category", intentCategoryId);
                            i.putExtra("minPrice", minPrice);
                            i.putExtra("maxPrice", maxPrice);
                            i.putExtra("fromMain", 1);
                            startActivity(i);
                        }else {
                            Intent i = new Intent();
                            i.putExtra("searchText", input_searchBox.getText().toString());
                            i.putExtra("rating", rating);
                            i.putExtra("category", intentCategoryId);
                            i.putExtra("minPrice", minPrice);
                            i.putExtra("maxPrice", maxPrice);
                            setResult(RESULT_OK, i);
                        }
                        finish();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
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


    }


    private void showCategory() {
        Collections.sort(CategoryListSignleton.getInstance().getArray(), (p1, p2) -> p1.Name.compareToIgnoreCase(p2.Name));
        CustomFilterDialog.with(getSupportFragmentManager()).setFilterList(filterModels)
                .setFilterHeader("Category and Nature of Work")
                .setApplyButtonName("Done")
                .setListener(new CustomFilterDialog.OnFilterSelectionListener() {
                    @Override
                    public void onFilterSelected(List<FilterModel> list, FilterModel filterModel) {
                        ArrayList<CategoryListModal> categoryListModals = CategoryListSignleton.getInstance().getArray();
                        String[] reportingMgr = new String[categoryListModals.size()];
                        for (int i = 0; i < categoryListModals.size(); i++) {
                            reportingMgr[i] = categoryListModals.get(i).Name;
                            if (reportingMgr[i].equalsIgnoreCase(filterModel.title)) {
                                intentCategoryId = categoryListModals.get(i).Id;
                            }
                        }
                        etCategory.setText(filterModel.title);


                    }
                }).build();


    }


    private void setDropDownList() {
        filterModels.clear();
        serviceFilterModals.clear();
        ArrayList<CategoryListModal> categoryListModals = CategoryListSignleton.getInstance().getArray();
        String[] rfilterArray = new String[categoryListModals.size()];
        for (int i = 0; i < categoryListModals.size(); i++) {
            if (categoryListModals.get(i).Name != null && !categoryListModals.get(i).Name.isEmpty() && !categoryListModals.get(i).equals("null")) {
                rfilterArray[i] = categoryListModals.get(i).Name;
            }
        }
        for (String aRfilterArray : rfilterArray) {
            FilterModel filterModel = new FilterModel();
            filterModel.title = aRfilterArray;
            filterModel.isSelected = false;
            filterModels.add(filterModel);
        }


        ArrayList<ServiceAtModal> serviceAtModals = ServiceAtSingleton.getInstance().getArray();
        String[] rServiceArray = new String[serviceAtModals.size()];
        for (int i = 0; i < serviceAtModals.size(); i++) {
            if (serviceAtModals.get(i).Name != null && !serviceAtModals.get(i).Name.isEmpty() && !serviceAtModals.get(i).equals("null")) {
                rServiceArray[i] = serviceAtModals.get(i).Name;
            }
        }
        for (String aRServiceArray : rServiceArray) {
            ServiceFilterModal serviceFilterModal = new ServiceFilterModal();
            serviceFilterModal.title = aRServiceArray;
            serviceFilterModal.isSelected = false;
            serviceFilterModals.add(serviceFilterModal);
        }
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

            setDropDownList();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchCategoryList(boolean isUpdate, String responseData) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_CAT_LIST;


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


}
