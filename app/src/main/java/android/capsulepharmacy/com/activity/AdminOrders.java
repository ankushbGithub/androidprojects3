package android.capsulepharmacy.com.activity;

import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.adapter.MyOrderAdapter;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.listener.MyListener;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminOrders extends BaseActivity implements View.OnClickListener,MyListener{
    private RecyclerView recyclerviewCategory;
    private ArrayList<MyOrderModal> myOrderModals=new ArrayList<>();
    private  MyOrderAdapter myOrderAdapter;
    private LinearLayout llCanceled,llDelivered,llDispatched,llConfirmed;
    private View vCanceled,vDelivered,vDispatched,vConfirmed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        init();
        setHeader();
        setRecycleItems();
        if (Utility.isConnected())
        getOrders();
        else {
            Utility.showToast("Please check your internet connectivity");
        }
    }

    private void getOrders() {
        final ProgressDialog progressDialog = new ProgressDialog(AdminOrders.this);
        progressDialog.show();
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEBSERVICE_LOGIN+"/"+ Prefs.getStringPrefs("id")+"/orders";
        Request request = APIClient.getRequest(this, url);

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
                                        JSONObject object=jsonObject.optJSONObject("response");
                                        JSONArray array=object.optJSONArray("orders");
                                        for (int i=0;i<array.length();i++){
                                            MyOrderModal myOrderModal=new MyOrderModal();
                                            JSONObject object1=array.optJSONObject(i);
                                            myOrderModal.setConfirmDate(object1.optString("confirm_date"));
                                            myOrderModal.setCreatedDate(object1.optString("createdAt"));
                                            myOrderModal.setDeliverDate(object1.optString("delivery_date"));
                                            myOrderModal.setDispatchDate(object1.optString("dispatch_date"));
                                            myOrderModal.setInvoiceImage(object1.optString("invoice_image"));
                                            myOrderModal.setImage(object1.optString("order_id"));
                                            myOrderModal.setReturnDate(object1.optString("return_date"));
                                            myOrderModal.setWeekday(object1.optString("weekday"));
                                            myOrderModal.setId(object1.optString("_id"));
                                            myOrderModal.setOrderId(object1.optString("order_id"));
                                            myOrderModals.add(myOrderModal);
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

    private void ReOrder(String orderid) {
        final ProgressDialog progressDialog = new ProgressDialog(AdminOrders.this);
        progressDialog.show();
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEBSERVICE_UPLOAD+"order"+"/"+ orderid+"/reorder";
        Request request = APIClient.getRequest(this, url);

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
                                        if (jsonObject.optString("errorMsg").equalsIgnoreCase("")) {
                                            Utility.showToast("New Order Placed Successfully");

                                        }else {
                                            Utility.showToast(jsonObject.optString("errorMsg"));
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
        recyclerviewCategory.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,true));


        myOrderAdapter = new MyOrderAdapter(mContext, myOrderModals,this);
        recyclerviewCategory.setAdapter(null);
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
    public void onListen(int position) {

        Intent i=new Intent(AdminOrders.this,OrderConfirmationActivity.class);
        i.putExtra("orderId",myOrderModals.get(position).getOrderId());
        startActivity(i);
    }
}
