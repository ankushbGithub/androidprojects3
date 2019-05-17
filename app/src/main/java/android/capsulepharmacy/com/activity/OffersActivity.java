package android.capsulepharmacy.com.activity;

import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.adapter.MyOrderAdapter;
import android.capsulepharmacy.com.adapter.OffersAdapter;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.utility.AppConstants;
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

public class OffersActivity extends BaseActivity implements View.OnClickListener{
    private String[] orderNo={"Offer 1","Offer 2","Offer 3","Offer 4","Offer 5","Offer 6","Offer 7"};
    private RecyclerView recyclerviewCategory;
    private ArrayList<MyOrderModal> myOrderModals=new ArrayList<>();
    private OffersAdapter myOrderAdapter;
    private LinearLayout llCanceled,llDelivered,llDispatched,llConfirmed;
    private View vCanceled,vDelivered,vDispatched,vConfirmed;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        init();
        setHeader();
        setRecycleItems();
        getOffers();
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

        myOrderAdapter = new OffersAdapter(mContext, myOrderModals);
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
    private void getOffers() {
        final ProgressDialog progressDialog = new ProgressDialog(OffersActivity.this);
        progressDialog.show();
        OkHttpClient okHttpClient = APIClient.getHttpClient();

        Request request = APIClient.getRequest(this, CapsuleAPI.WEBSERVICE_GET_DASHBOARD);

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
                                        JSONArray array=object.optJSONArray("banners");
                                        for (int i=0;i<array.length();i++){
                                            MyOrderModal myOrderModal=new MyOrderModal();
                                            JSONObject object1=array.optJSONObject(i);
                                            myOrderModal.setImage(object1.optString("image"));
                                            myOrderModal.setTitle(object1.optString("name"));
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
}
