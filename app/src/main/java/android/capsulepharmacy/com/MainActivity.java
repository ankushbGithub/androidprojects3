package android.capsulepharmacy.com;

import android.app.ProgressDialog;
import android.capsulepharmacy.com.Category.activity.CategoryListingActivity;
import android.capsulepharmacy.com.activity.AboutActivity;
import android.capsulepharmacy.com.activity.BookVendor;
import android.capsulepharmacy.com.activity.FilterActivity;
import android.capsulepharmacy.com.activity.LoginActivity;
import android.capsulepharmacy.com.activity.MyAccountActivity;
import android.capsulepharmacy.com.activity.MyRequests;
import android.capsulepharmacy.com.activity.OffersActivity;
import android.capsulepharmacy.com.activity.Support;
import android.capsulepharmacy.com.activity.TermsActivity;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.customerModule.activity.CustomerEditActivity;
import android.capsulepharmacy.com.customerModule.activity.CustomerListActivity;
import android.capsulepharmacy.com.fragment.HomeFragment;
import android.capsulepharmacy.com.notifications.NotificationUtils;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Config;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.activity.VendorCreationActivity;
import android.capsulepharmacy.com.vendor.activity.VendorListActivity;
import android.capsulepharmacy.com.vendor.modal.CategoryListModal;
import android.capsulepharmacy.com.vendor.modal.SelectedSubCatList;
import android.capsulepharmacy.com.vendor.modal.ServiceAtModal;
import android.capsulepharmacy.com.vendor.modal.SubListingModal;
import android.capsulepharmacy.com.vendor.singleton.CategoryListSignleton;
import android.capsulepharmacy.com.vendor.singleton.SelectedSubCatListSingleton;
import android.capsulepharmacy.com.vendor.singleton.ServiceAtSingleton;
import android.capsulepharmacy.com.vendor.singleton.SubCatListingSingleton;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int mContainerId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    TextView name;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainerId = R.id.fragment_container;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        int mainScreen = 0;
        Intent i = getIntent();
        if (i != null) {
            mainScreen = i.getIntExtra("mainScreen", 0);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();


        if (Prefs.getStringPrefs(AppConstants.USER_ROLE).equalsIgnoreCase("admin")) {
            menu.findItem(R.id.nav_home).setVisible(true);
            menu.findItem(R.id.customer_creation).setVisible(true);
            menu.findItem(R.id.vendor_creation).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(true);
            menu.findItem(R.id.nav_myorder).setVisible(true);
            menu.findItem(R.id.cat_creation).setVisible(true);
            //  menu.findItem(R.id.nav_reorder).setVisible(true);
            //  menu.findItem(R.id.nav_trackorder).setVisible(false);
            // menu.findItem(R.id.nav_reorder).setVisible(false);
        } else if (Prefs.getStringPrefs(AppConstants.USER_ROLE).equalsIgnoreCase("vendor")) {
            menu.findItem(R.id.nav_myorder).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(true);
            menu.findItem(R.id.nav_home).setVisible(true);
            menu.findItem(R.id.cat_creation).setVisible(false);
            //   menu.findItem(R.id.nav_reorder).setVisible(true);
            //   menu.findItem(R.id.nav_trackorder).setVisible(true);
            //  menu.findItem(R.id.nav_reorder).setVisible(true);
        } else {
            menu.findItem(R.id.nav_home).setVisible(true);
            menu.findItem(R.id.nav_myorder).setVisible(true);
            menu.findItem(R.id.nav_trackorder).setVisible(true);
            //   menu.findItem(R.id.nav_reorder).setVisible(true);
            menu.findItem(R.id.support).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(true);
            menu.findItem(R.id.cat_creation).setVisible(false);
        }

        View viewHeader = navigationView.getHeaderView(0);
        LinearLayout viewProfile = (LinearLayout) viewHeader.findViewById(R.id.llProfile);
        name = viewHeader.findViewById(R.id.name);
       TextView points = viewHeader.findViewById(R.id.points);
        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
            name.setText("Login or Register");
            menu.findItem(R.id.nav_logout).setVisible(false);
            points.setVisibility(View.GONE);
        } else {
            points.setVisibility(View.VISIBLE);
            points.setText(""+Prefs.getIntegerPrefs(AppConstants.USER_POINTS)+" Wallet Points");
            name.setText(Prefs.getStringPrefs(AppConstants.USER_NAME));
            menu.findItem(R.id.nav_logout).setVisible(true);
        }


        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    if (Prefs.getStringPrefs(AppConstants.USER_ROLE).equalsIgnoreCase("vendor")) {
                        onVendorEditClicked(Prefs.getIntegerPrefs(AppConstants.USER_ID));
                    }else if (Prefs.getStringPrefs(AppConstants.USER_ROLE).equalsIgnoreCase("customer")){

                        onCustomerEditClicked(Prefs.getIntegerPrefs(AppConstants.USER_ID));
                    }
                    else
                     {
                        Intent i = new Intent(MainActivity.this, MyAccountActivity.class);
                        startActivity(i);
                    }
                }

            }
        });
        addFragment(new HomeFragment(), mContainerId);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    String regId = pref.getString("regId", null);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                }
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                    name.setText("Login or Register");
                } else {
                    name.setText(Prefs.getStringPrefs("name"));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            replaceFragment(new HomeFragment(), mContainerId);
            // Handle the camera action
        } else if (id == R.id.nav_myorder) {
            if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
            } else {
                Intent i = new Intent(mContext, MyRequests.class);
                startActivity(i);
            }
           /* if (Prefs.getStringPrefs("id").equalsIgnoreCase("")) {
               Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
            } else {
                if (Prefs.getStringPrefs("type").equalsIgnoreCase("admin")) {
                    Intent i  =new Intent(mContext, AdminMyOrders.class);
                    startActivity(i);
                }else {
                    Intent i  =new Intent(mContext, BookVendor.class);
                    startActivity(i);
                }

            }*/

        } else if (id == R.id.nav_trackorder) {
            if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                Intent i = new Intent(mContext, BookVendor.class);
                startActivity(i);
            } else {
                Intent i = new Intent(mContext, BookVendor.class);
                startActivity(i);
            }

           /* if (Prefs.getStringPrefs("id").equalsIgnoreCase("")) {
                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
            } else {
                if (Prefs.getStringPrefs("type").equalsIgnoreCase("admin")) {
                    Intent i  =new Intent(mContext, AdminMyOrders.class);
                    startActivity(i);
                }else {
                    Intent i  =new Intent(mContext, BookVendor.class);
                    startActivity(i);
                }
            }*/

        } else if (id == R.id.nav_about) {
            Intent i = new Intent(mContext, AboutActivity.class);
            mContext.startActivity(i);
        } else if (id == R.id.nav_reorder) {
            Intent i = new Intent(mContext, MyRequests.class);
            startActivity(i);
           /* if (Prefs.getStringPrefs("id").equalsIgnoreCase("")) {
                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
            } else {
                if (Prefs.getStringPrefs("type").equalsIgnoreCase("admin")) {
                    Intent i  =new Intent(mContext, AdminMyOrders.class);
                    startActivity(i);
                }else {
                    Intent i  =new Intent(mContext, BookVendor.class);
                    startActivity(i);
                }
            }*/
        } else if (id == R.id.nav_offers) {
            Intent i = new Intent(mContext, OffersActivity.class);
            mContext.startActivity(i);
        } else if (id == R.id.nav_tc) {
            Intent i = new Intent(mContext, TermsActivity.class);
            mContext.startActivity(i);
        } else if (id == R.id.nav_logout) {
            Prefs.clearSharedPrefs();
            Intent i = new Intent(mContext, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(i);
            finish();

        } else if (id == R.id.vendor_creation) {
            Intent i = new Intent(mContext, VendorListActivity.class);
            mContext.startActivity(i);
        } else if (id == R.id.cat_creation) {
            Intent i = new Intent(mContext, CategoryListingActivity.class);
            mContext.startActivity(i);
        } else if (id == R.id.customer_creation) {
            Intent i = new Intent(mContext, CustomerListActivity.class);
            mContext.startActivity(i);
        } else if (id == R.id.support) {
            Intent i = new Intent(mContext, Support.class);
            mContext.startActivity(i);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    private void onVendorEditClicked(int id) {
        if (NetUtil.isNetworkConnected(mContext)) {
            getVendorDetailsList(id);
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
            Log.e("", "Get Request**" + url);

            final Request request = APIClient.getRequest(mContext, url);
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
                    // dismissProgress();

                    runOnUiThread(() -> {
                        progressDialog.dismiss();

                    });

                    try {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            Log.e("", "Response***" + responseData);
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

    private void fetchCategoryList(boolean isUpdate, String responseData) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_CAT_LIST;
        Log.e("", "Get Request**" + url);

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
                        Log.e("", "Response***" + rData);
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
        Log.e("", "Get Request**" + url);

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
                        Log.e("", "Response***" + rData);
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
        Log.e("", "Get Request**" + url);

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
                        Log.e("", "Response***" + rData);
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

    private void onCustomerEditClicked(int id) {
        if (NetUtil.isNetworkConnected(mContext)) {
            getUserDetailsList(id,false);
        }else {
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
            Log.e("", "Get Request**" + url);

            final Request request = APIClient.getRequest(mContext, url);
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
                    // dismissProgress();

                    runOnUiThread(() -> {
                        progressDialog.dismiss();

                    });

                    try {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            Log.e("", "Response***" + responseData);
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
            Intent i =new Intent(mContext, CustomerEditActivity.class);
            i.putExtra("response",responseData);
            startActivity(i);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (menu != null) {

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i=new Intent(MainActivity.this, FilterActivity.class);
            i.putExtra("fromMain",1);
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
