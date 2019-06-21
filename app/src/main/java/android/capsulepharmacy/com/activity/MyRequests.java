package android.capsulepharmacy.com.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.adapter.MyHistoryAdapter;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
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

public class MyRequests extends BaseActivity implements View.OnClickListener,MyListener{

    private RecyclerView recyclerviewCategory;
    private ArrayList<MyOrderModal> myOrderModals=new ArrayList<>();
    private MyHistoryAdapter myOrderAdapter;
    private LinearLayout llCanceled,llDelivered,llDispatched,llConfirmed;
    private View vCanceled,vDelivered,vDispatched,vConfirmed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

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
        final ProgressDialog progressDialog = new ProgressDialog(MyRequests.this);
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




                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myOrderModals.clear();
                                        JSONArray jsonArray= null;
                                        try {
                                            jsonArray = new JSONArray(responseData);

                                            Gson gson = new Gson();
                                            Type type = new TypeToken<List<MyOrderModal>>(){}.getType();
                                            myOrderModals.addAll(gson.fromJson(responseData, type));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        myOrderAdapter.notifyDataSetChanged();

                                    }
                                });



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
        final ProgressDialog progressDialog = new ProgressDialog(MyRequests.this);
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

    private void ratingAPI(String orderid, float status, int position, String remarks, Dialog dialog) {
        final ProgressDialog progressDialog = new ProgressDialog(MyRequests.this);
        progressDialog.show();


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserRole", Prefs.getStringPrefs(AppConstants.USER_ROLE));
            jsonObject.put("Id", Prefs.getIntegerPrefs(AppConstants.USER_ID));
            jsonObject.put("Rating", status);
            jsonObject.put("BookingNumber", orderid);
            jsonObject.put("RatingRemarks", remarks);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_BOOKING_Rating;
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
                        dialog.dismiss();
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
                            dialog.dismiss();
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
                                            myOrderModal.setRating(status);
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
        myOrderAdapter = new MyHistoryAdapter(mContext, myOrderModals,this);
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
    public void onListen(int position,String type) {

        if (type.equalsIgnoreCase("accept")){
            ReOrder(myOrderModals.get(position).getBookingNumber(),2,position);
        }else if(type.equalsIgnoreCase("rating")){

            if (Prefs.getStringPrefs(AppConstants.USER_ROLE).equalsIgnoreCase("customer")) {
                showRateDialog(myOrderModals.get(position).getBookingNumber(), position,myOrderModals.get(position).getVendorName());
            }else {
                showRateDialog(myOrderModals.get(position).getBookingNumber(), position,myOrderModals.get(position).getCustomerName());
            }
        }
        else{
            ReOrder(myOrderModals.get(position).getBookingNumber(),3,position);
        }


    }


    private void showRateDialog(String bookingNumber, int position, String customerName){
        Dialog dialog = new Dialog(MyRequests.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rating_dialog);
        TextView titleTV = (TextView)dialog.findViewById(R.id.rateHeader);
        final TextView rateTV = (TextView)dialog.findViewById(R.id.rateTV);
        Button submitBtn = (Button)dialog.findViewById(R.id.submitRateBtn);
        Button cancelBtn = (Button)dialog.findViewById(R.id.cancelRateBtn);
        RatingBar ratingBar = (RatingBar)dialog.findViewById(R.id.ratingsBar);
        final EditText reviewED = (EditText)dialog.findViewById(R.id.reviewED);
        titleTV.setText("Rate "+customerName);
       // titleTV.setText("Rate "+title);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rateTV.setText("Rate : "+v);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingAPI(bookingNumber,ratingBar.getRating(),position,reviewED.getText().toString().trim(),dialog);

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
