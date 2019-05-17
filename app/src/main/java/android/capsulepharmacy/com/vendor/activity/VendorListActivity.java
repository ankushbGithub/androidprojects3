package android.capsulepharmacy.com.vendor.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.DeleteDialog;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.adapter.VendorListAdapter;
import android.capsulepharmacy.com.vendor.modal.CategoryListModal;
import android.capsulepharmacy.com.vendor.modal.ServiceAtModal;
import android.capsulepharmacy.com.vendor.modal.SubListingModal;
import android.capsulepharmacy.com.vendor.modal.VendorListModal;
import android.capsulepharmacy.com.vendor.singleton.CategoryListSignleton;
import android.capsulepharmacy.com.vendor.singleton.ServiceAtSingleton;
import android.capsulepharmacy.com.vendor.singleton.SubCatListingSingleton;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VendorListActivity extends AppCompatActivity implements VendorListAdapter.ThreeDots, VendorListAdapter.CardOnClick, DeleteDialog.InDelete {
    private Context mContext;
    private String TAG = VendorListActivity.class.getSimpleName();
    private Toolbar toolbarCustomerInfo;
    private RecyclerView recyclerviewCustomerCard;
    private SwipeRefreshLayout mySwipeRefresh;
    private ArrayList<VendorListModal> vendorListModals = new ArrayList<>();
    private ArrayList<VendorListModal> arrSearlist = new ArrayList<>();

    private VendorListAdapter vendorListAdapter;
    private FloatingActionButton fab;
    private DeleteDialog deleteDialog;
    private SearchView svCustomer;
    private SharedPreferences.Editor editor;
    private static final String mypreference = "Data";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_list_activity);
        mContext = VendorListActivity.this;
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        editor.clear();
        editor.apply();
        applyInit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.vendor_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        ((EditText) searchView.findViewById(R.id.search_src_text)).setTextColor(getResources().getColor(R.color.white));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    searchView.setIconified(true);
                }
                filterData(query, vendorListModals);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    searchView.setIconified(true);
                }
                filterData(newText, vendorListModals);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filterData(String newText, ArrayList<VendorListModal> responseList) {
        if (responseList != null) {
            newText = newText.toLowerCase(Locale.getDefault());
            arrSearlist.clear();
            if (newText.length() == 0) {
                arrSearlist.addAll(responseList);
            } else {
                for (VendorListModal vendorListModal : responseList) {
                    if (vendorListModal.Name != null) {

                        if (vendorListModal.Name.toLowerCase().contains(newText) ||
                                vendorListModal.Name.toUpperCase().contains(newText) ||
                                vendorListModal.MobileNo.toLowerCase().contains(newText) ||
                                vendorListModal.MobileNo.toUpperCase().contains(newText)) {
                            arrSearlist.add(vendorListModal);
                        }
                    }
                }
            }
        }
        Collections.sort(arrSearlist, (p1, p2) -> p1.Name.compareToIgnoreCase(p2.Name));
        recyclerviewCustomerCard.setHasFixedSize(true);
        recyclerviewCustomerCard.setLayoutManager(new LinearLayoutManager(mContext));
        vendorListAdapter = new VendorListAdapter(mContext, arrSearlist, this, this);
        recyclerviewCustomerCard.setAdapter(vendorListAdapter);

    }

    private void applyInit() {
        if (NetUtil.isNetworkAvailable(mContext)) {
            getVendorList();

        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void disableSwipeToRefresh() {
        if (mySwipeRefresh.isRefreshing()) {
            mySwipeRefresh.setRefreshing(false);
        }
    }


    private void getVendorList() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_VENDOR_LIST;
        Log.e(TAG, "Get Request**" + url);

        final Request request = APIClient.getRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    disableSwipeToRefresh();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    disableSwipeToRefresh();
                });
                try {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Log.e(TAG, "Response***" + responseData);
                        if (responseData != null) {
                            runOnUiThread(() ->
                                    setServerData(responseData));
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

    private void setServerData(String response) {
        vendorListModals.clear();
        arrSearlist.clear();
        try {
            final JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                VendorListModal vendorListModal = new VendorListModal();
                vendorListModal.Id = jsonObject.optInt(Constants.Id);
                vendorListModal.Name = jsonObject.optString(Constants.Name);
                vendorListModal.GSTIN = jsonObject.optString(Constants.GSTIN);
                vendorListModal.MobileNo = jsonObject.optString(Constants.MobileNo);
                vendorListModal.FirmName = jsonObject.optString(Constants.FirmName);
                vendorListModal.CategoryId = jsonObject.optInt(Constants.CategoryId);
                vendorListModal.SubCategoryId = jsonObject.optInt(Constants.SubCategoryId);
                vendorListModal.ServiceLocationId = jsonObject.optInt(Constants.ServiceLocationId);
                vendorListModal.Category = jsonObject.optString(Constants.Category);
                vendorListModal.FilteredSubCategory = jsonObject.optString(Constants.FilteredSubCategory);
                vendorListModal.ServiceAt = jsonObject.optString(Constants.ServiceAt);
                vendorListModal.ImagePath = jsonObject.optString(Constants.ImagePath);
                vendorListModal.ImageFile = jsonObject.optString(Constants.ImageFile);
                vendorListModal.AadhaarPathL = jsonObject.optString(Constants.AadhaarPathL);
                vendorListModal.AadhaarFile = jsonObject.optString(Constants.AadhaarFile);
                vendorListModal.PanPathL = jsonObject.optString(Constants.PanPathL);
                vendorListModal.PanFile = jsonObject.optString(Constants.PanFile);
                vendorListModal.PhotoPath = jsonObject.optString(Constants.PhotoPath);
                vendorListModal.PanPath = jsonObject.optString(Constants.PanPath);
                vendorListModal.AadhaarPath = jsonObject.optString(Constants.AadhaarPath);
                vendorListModal.PhotoUrl = jsonObject.optString(Constants.PhotoUrl);
                vendorListModal.AadhaarUrl = jsonObject.optString(Constants.AadhaarUrl);
                vendorListModal.VendorAddressDetails = jsonObject.optString(Constants.VendorAddressDetails);
                vendorListModal.VendorBankDetails = jsonObject.optString(Constants.VendorBankDetails);
                vendorListModals.add(vendorListModal);
                arrSearlist.add(vendorListModal);
            }
            Collections.sort(arrSearlist, (p1, p2) -> p1.Name.compareToIgnoreCase(p2.Name));
            recyclerviewCustomerCard.setHasFixedSize(true);
            recyclerviewCustomerCard.setLayoutManager(new LinearLayoutManager(mContext));
            vendorListAdapter = new VendorListAdapter(mContext, arrSearlist, this, this);
            recyclerviewCustomerCard.setAdapter(vendorListAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        toolbarCustomerInfo = findViewById(R.id.toolbarCustomerInfo);
        recyclerviewCustomerCard = findViewById(R.id.recyclerviewCustomerCard);
        mySwipeRefresh = findViewById(R.id.mySwipeRefresh);
        fab = findViewById(R.id.fab);
        setSupportActionBar(toolbarCustomerInfo);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbarCustomerInfo.setTitle(getResources().getString(R.string.toolbar_title_vendor_list));
        toolbarCustomerInfo.setTitleTextColor(getResources().getColor(R.color.white));

        mySwipeRefresh.setOnRefreshListener(
                () -> {
                    if (NetUtil.isNetworkAvailable(mContext)) {
                        getVendorList();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCategoryList(false, "");
            }
        });

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
                    disableSwipeToRefresh();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    disableSwipeToRefresh();
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
                    disableSwipeToRefresh();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    disableSwipeToRefresh();
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
                    disableSwipeToRefresh();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    disableSwipeToRefresh();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Utility.hideSoftKeyboard((Activity) mContext);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onThreeDotCLicked(View v, int position) {
        VendorListModal vendorListModal = arrSearlist.get(position);
        showPopUpMenu(v, vendorListModal.Id, vendorListModal.Name);
    }

    private void showPopUpMenu(View view, int id, String Name) {
        PopupMenu popup = new PopupMenu(this, view);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
            popup.getMenuInflater().inflate(R.menu.three_dots_options, popup.getMenu());
            popup.getMenu().getItem(0).setIcon(R.drawable.icon_edit);
            popup.getMenu().getItem(0).setTitle("Edit");

            popup.getMenu().getItem(1).setIcon(R.drawable.icon_delete);
            popup.getMenu().getItem(1).setTitle("Delete");

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem menu_item) {
                    switch (menu_item.getItemId()) {
                        case R.id.nav_edit:
                            //handle the click here
                            onVendorEditClicked(id);
                            break;
                        case R.id.nav_deleted:
                            onVendorDeleteClicked(id, Name);
                            break;

                    }
                    return true;
                }
            });
            popup.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onVendorDeleteClicked(int id, String firstName) {
        showDeleteConfirmation(id, firstName);
    }

    private void showDeleteConfirmation(int id, String firstName) {
        deleteDialog = new DeleteDialog(VendorListActivity.this, this, firstName, id, "Are you sure you want to delete " + firstName + " ?");
        deleteDialog.setCancelable(false);
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.show();

    }

    private void deleteCustomer(int id) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_VENDOR_DELETE + id;
        Log.e(TAG, "Get Request**" + url);

        final Request request = APIClient.deleteRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(Call call, final Response response) {
                runOnUiThread(() ->
                        progressDialog.dismiss());
                try {
                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();
                        Log.e(TAG, "Response***" + responseData);
                        runOnUiThread(() ->
                                getVendorList());

                    } else if (response.code() == Constants.BAD_REQUEST) {
                        runOnUiThread(() -> Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show());
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

    private void onVendorEditClicked(int id) {
        if (NetUtil.isNetworkConnected(mContext)) {
            getVendorDetailsList(id);
        } else {
            Toast.makeText(mContext, getResources().getText(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCardClick(int position) {
        VendorListModal customerModal = arrSearlist.get(position);
        if (NetUtil.isNetworkConnected(mContext)) {
            getVendorDetailsList(customerModal.Id);
        } else {
            Toast.makeText(mContext, getResources().getText(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void getVendorDetailsList(int id) {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            OkHttpClient okHttpClient = APIClient.getHttpClient();
            String url = CapsuleAPI.WEB_SERVICE_VENDOR_DETAILS + id;
            Log.e(TAG, "Get Request**" + url);

            final Request request = APIClient.getRequest(mContext, url);
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull final IOException e) {

                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        disableSwipeToRefresh();
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) {
                    // dismissProgress();

                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        disableSwipeToRefresh();
                    });

                    try {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            Log.e(TAG, "Response***" + responseData);
                            if (responseData != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setViewData(responseData);

                                    }
                                });

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setViewData(String responseData) {
        try {
            fetchCategoryList(true, responseData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInDelete(String firstName, int id) {
        deleteCustomer(id);
    }
}

