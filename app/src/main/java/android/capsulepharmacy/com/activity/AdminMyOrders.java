package android.capsulepharmacy.com.activity;

import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;

import android.capsulepharmacy.com.adapter.MyOrderAdapter;
import android.capsulepharmacy.com.adapter.MyOrdersAdapter;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.listener.MyListener;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.SpacesItemDecoration;
import android.capsulepharmacy.com.utility.Utility;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminMyOrders extends BaseActivity implements View.OnClickListener,MyListener{

    private RecyclerView recyclerviewCategory;
    private ArrayList<MyOrderModal> myOrderModals=new ArrayList<>();
    private MyOrdersAdapter myOrderAdapter;
    private LinearLayout llCanceled,llDelivered,llDispatched,llConfirmed;
    private View vCanceled,vDelivered,vDispatched,vConfirmed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history);

        init();
        setHeader();
        setRecycleItems();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utility.isConnected()) {

            getOrders();
        }else {
            Utility.showToast("Please check your internet connectivity");
        }
    }


    private void getOrders() {
        final ProgressDialog progressDialog = new ProgressDialog(AdminMyOrders.this);
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserRole", Prefs.getStringPrefs(AppConstants.USER_ROLE));
            jsonObject.put("VendorId", Prefs.getIntegerPrefs(AppConstants.USER_ID));
            jsonObject.put("CustomerId", Prefs.getIntegerPrefs(AppConstants.USER_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_BOOKING_LIST;
        RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, jsonObject.toString());
        Request request = APIClient.simplePostRequest(this, url,requestBody);

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
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                        }
                    });
                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();

                        if (responseData != null) {

                            try {
                                final JSONObject jsonObject = new JSONObject(responseData);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myOrderModals.clear();
                                        JSONArray jsonArray= null;
                                        try {
                                            jsonArray = new JSONArray(responseData);

                                            Gson gson = new Gson();
                                            Type type = new TypeToken<List<MyOrderModal>>(){}.getType();
                                            myOrderModals = gson.fromJson(responseData, type);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        myOrderAdapter.notifyDataSetChanged();

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void ReOrder(String orderid, int status, int position) {
        final ProgressDialog progressDialog = new ProgressDialog(AdminMyOrders.this);
        progressDialog.show();


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserRole", Prefs.getStringPrefs(AppConstants.USER_ROLE));
            jsonObject.put("VendorId", Prefs.getIntegerPrefs(AppConstants.USER_ID));
            jsonObject.put("CustomerId", Prefs.getIntegerPrefs(AppConstants.USER_ID));
            jsonObject.put("Status", status);
            jsonObject.put("BookingNumber", orderid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_BOOKING_Action;
        RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, jsonObject.toString());
        Request request = APIClient.simplePostRequest(this, url,requestBody);

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
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                        }
                    });
                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();

                        if (responseData != null) {

                            try {
                                final JSONObject jsonObject = new JSONObject(responseData);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (jsonObject.optInt("code")==200) {

                                            MyOrderModal myOrderModal=myOrderModals.get(position);
                                            myOrderModal.setStatus(status);
                                            myOrderModals.set(position,myOrderModal);
                                            myOrderAdapter.notifyItemChanged(position);
                                            Utility.showToast(jsonObject.optString("message"));

                                        }else {
                                            Utility.showToast(jsonObject.optString("message"));
                                        }

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private void init() {
        vCanceled=findViewById(R.id.vCanceled);
        vConfirmed=findViewById(R.id.vConfirmed);
        vDelivered=findViewById(R.id.vDelivered);
        vDispatched=findViewById(R.id.vDispatched);

        llCanceled=findViewById(R.id.llCanceled);
        llConfirmed=findViewById(R.id.llConfirmed);
        llDelivered=findViewById(R.id.llDelivered);
        llDispatched=findViewById(R.id.llDispatched);
        llCanceled.setOnClickListener(this);
        llConfirmed.setOnClickListener(this);
        llDelivered.setOnClickListener(this);
        llDispatched.setOnClickListener(this);
        recyclerviewCategory=findViewById(R.id.recycler_view);
    }

    private void setRecycleItems() {
        recyclerviewCategory.setHasFixedSize(true);
        recyclerviewCategory.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        recyclerviewCategory.addItemDecoration(new SpacesItemDecoration(10));


        myOrderAdapter = new MyOrdersAdapter(mContext, myOrderModals,this);
        recyclerviewCategory.setAdapter(myOrderAdapter);
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

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.llConfirmed:
                vConfirmed.setVisibility(View.VISIBLE);
                vCanceled.setVisibility(View.GONE);
                vDelivered.setVisibility(View.GONE);
                vDispatched.setVisibility(View.GONE);
                break;
            case R.id.llDispatched:
                vConfirmed.setVisibility(View.GONE);
                vCanceled.setVisibility(View.GONE);
                vDelivered.setVisibility(View.GONE);
                vDispatched.setVisibility(View.VISIBLE);
                break;
            case R.id.llDelivered:
                vConfirmed.setVisibility(View.GONE);
                vCanceled.setVisibility(View.GONE);
                vDelivered.setVisibility(View.VISIBLE);
                vDispatched.setVisibility(View.GONE);
                break;
            case R.id.llCanceled:
                vConfirmed.setVisibility(View.GONE);
                vCanceled.setVisibility(View.VISIBLE);
                vDelivered.setVisibility(View.GONE);
                vDispatched.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void onListen(int position,String type) {


        ReOrder(myOrderModals.get(position).getBookingNumber(),3,position);
    }
}
