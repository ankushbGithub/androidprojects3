package android.capsulepharmacy.com.customerModule.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.customerModule.adapter.CustomerListAdapter;
import android.capsulepharmacy.com.customerModule.modal.CustomerModal;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.DeleteDialog;
import android.capsulepharmacy.com.utility.MessageDialog;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
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

public class CustomerListActivity extends AppCompatActivity implements CustomerListAdapter.ThreeDots, CustomerListAdapter.CardOnClick, DeleteDialog.InDelete {
    private Context mContext;
    private String TAG = CustomerListActivity.class.getSimpleName();
    private Toolbar toolbarCustomerInfo;
    private RecyclerView recyclerviewCustomerCard;
    private SwipeRefreshLayout mySwipeRefresh;
    private ArrayList<CustomerModal> customerModals = new ArrayList<>();
    private ArrayList<CustomerModal> arrSearlist = new ArrayList<>();
    private CustomerListAdapter customerListAdapter;
    private FloatingActionButton fab;
    private DeleteDialog deleteDialog;
    private View mSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cutomer_list_activity);
        mContext = CustomerListActivity.this;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyInit();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.vendor_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);


        SearchView searchView = (SearchView) searchItem.getActionView();
        ((EditText)searchView.findViewById(R.id.search_src_text)).setTextColor(getResources().getColor(R.color.white));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    searchView.setIconified(true);
                }
                filterData(query, customerModals);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    searchView.setIconified(true);
                }
                filterData(newText, customerModals);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void filterData(String newText, ArrayList<CustomerModal> responseList) {
        if (responseList != null) {
            newText = newText.toLowerCase(Locale.getDefault());
            arrSearlist.clear();
            if (newText.length() == 0) {
                arrSearlist.addAll(responseList);
            } else {
                for (CustomerModal userListModel : responseList) {
                    if (userListModel.FirstName != null) {

                        if (userListModel.FirstName.toLowerCase().contains(newText) ||
                                userListModel.FirstName.toUpperCase().contains(newText) ||
                                userListModel.MobileNo.toLowerCase().contains(newText) ||
                                userListModel.MobileNo.toUpperCase().contains(newText)) {
                            arrSearlist.add(userListModel);
                        }
                    }
                }
            }
        }
        Collections.sort(arrSearlist, (p1, p2) -> p1.FirstName.compareToIgnoreCase(p2.FirstName));
        recyclerviewCustomerCard.setHasFixedSize(true);
        recyclerviewCustomerCard.setLayoutManager(new LinearLayoutManager(mContext));
        customerListAdapter = new CustomerListAdapter(mContext, arrSearlist, this, this);
        recyclerviewCustomerCard.setAdapter(customerListAdapter);

    }

    private void applyInit() {
        if (NetUtil.isNetworkAvailable(mContext)) {
            getCustomerList();

        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void disableSwipeToRefresh() {
        if (mySwipeRefresh.isRefreshing()) {
            mySwipeRefresh.setRefreshing(false);
        }
    }


    private void getCustomerList() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_CUSTOMER_LIST;
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
        customerModals.clear();
        arrSearlist.clear();
        try {
            final JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                CustomerModal customerModal = new CustomerModal();
                customerModal.Id = jsonObject.optInt(Constants.Id);
                customerModal.FirstName = jsonObject.optString(Constants.FirstName);
                customerModal.LastName = jsonObject.optString(Constants.LastName);
                customerModal.DOB = jsonObject.optString(Constants.DOB);
                customerModal.Email = jsonObject.optString(Constants.Email);
                customerModal.GSTIN = jsonObject.optString(Constants.GSTIN);
                customerModal.MobileNo = jsonObject.optString(Constants.MobileNo);
                customerModal.PhotoPath = jsonObject.optString(Constants.PhotoPath);
                customerModal.PhotoUrl = jsonObject.optString(Constants.PhotoUrl);
                customerModal.ImagePath = jsonObject.optString(Constants.ImagePath);
                customerModal.ImageFile = jsonObject.optString(Constants.ImageFile);
                customerModal.Password = jsonObject.optString(Constants.Password);
                customerModals.add(customerModal);
                arrSearlist.add(customerModal);
            }
            Collections.sort(arrSearlist, (p1, p2) -> p1.FirstName.compareToIgnoreCase(p2.FirstName));
            recyclerviewCustomerCard.setHasFixedSize(true);
            recyclerviewCustomerCard.setLayoutManager(new LinearLayoutManager(mContext));
            customerListAdapter = new CustomerListAdapter(mContext, arrSearlist, this, this);
            recyclerviewCustomerCard.setAdapter(customerListAdapter);

        }catch (Exception e){
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
        toolbarCustomerInfo.setTitle(getResources().getString(R.string.toolbar_title_customer_list));
        toolbarCustomerInfo.setTitleTextColor(getResources().getColor(R.color.white));

        mySwipeRefresh.setOnRefreshListener(
                () -> {
                    if (NetUtil.isNetworkAvailable(mContext)) {
                        getCustomerList();

                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, CustomerCreationActivity.class);
                startActivity(i);
            }
        });

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
        CustomerModal customerModal = arrSearlist.get(position);
        showPopUpMenu(v, customerModal.Id, customerModal.FirstName);
    }

    private void showPopUpMenu(View view, int id, String firstName) {
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
                            onCustomerEditClicked(id);
                            break;
                        case R.id.nav_deleted:
                            onCustomerDeleteClicked(id, firstName);
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

    private void onCustomerDeleteClicked(int id, String firstName) {
        showDeleteConfirmation(id,firstName);
    }

    private void showDeleteConfirmation(int id, String firstName) {
        deleteDialog = new DeleteDialog(CustomerListActivity.this,this,firstName,id,"Are you sure you want to delete " + firstName + " ?");
        deleteDialog.setCancelable(false);
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.show();

    }

    private void deleteCustomer(int id) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_DELETE + id;
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
                                getCustomerList());

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

    private void onCustomerEditClicked(int id) {
        if (NetUtil.isNetworkConnected(mContext)) {
            getUserDetailsList(id,false);
        }else {
            Toast.makeText(mContext, getResources().getText(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCardClick(int position) {
        CustomerModal customerModal = arrSearlist.get(position);
        if (NetUtil.isNetworkConnected(mContext)) {
            getUserDetailsList(customerModal.Id,true);
        } else {
            Toast.makeText(mContext, getResources().getText(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserDetailsList(int id,boolean isView) {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            OkHttpClient okHttpClient = APIClient.getHttpClient();
            String url = CapsuleAPI.WEB_SERVICE_CUSTOMER_DETAILS+id;
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
                                        if (isView){
                                            setViewData(responseData);
                                        }else {
                                            setEditData(responseData);
                                        }
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

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setEditData(String responseData) {
        try{
            Intent i =new Intent(mContext,CustomerEditActivity.class);
            i.putExtra("response",responseData);
            startActivity(i);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setViewData(String responseData) {
        try{
            Intent i =new Intent(mContext,CustomerViewActivity.class);
            i.putExtra("response",responseData);
            startActivity(i);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onInDelete(String firstName, int id) {
        deleteCustomer(id);
    }
}
