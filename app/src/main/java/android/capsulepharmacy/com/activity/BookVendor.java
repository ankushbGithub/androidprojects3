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
import android.capsulepharmacy.com.utility.SpacesItemDecoration;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class BookVendor extends BaseActivity implements View.OnClickListener,MyListener{
    private RecyclerView recyclerviewCategory;
    private ArrayList<MyOrderModal> myOrderModals=new ArrayList<>();
    private  MyOrderAdapter myOrderAdapter;
    private LinearLayout llCanceled,llDelivered,llDispatched,llConfirmed;
    private View vCanceled,vDelivered,vDispatched,vConfirmed;
    private String keyword="";
    private int categoryId,rating=0,fromMain;
    private double minPrice=0,maxPrice=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vendor);

        init();
        setHeader();
        setRecycleItems();

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
            Intent i=new Intent(BookVendor.this,FilterActivity.class);
            startActivityForResult(i,200);

            return true;
        }

        return super.onOptionsItemSelected(item);
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
        final ProgressDialog progressDialog = new ProgressDialog(BookVendor.this);
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Type","search");
            jsonObject.put("Rating", rating);
            jsonObject.put("CategoryId", categoryId);
            jsonObject.put("SearchParam", keyword);
            jsonObject.put("minPrice", minPrice);
            jsonObject.put("maxPrice", maxPrice);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_BOOKING_FilterVendorList;
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
                                            JSONObject jsonObject1=new JSONObject(responseData);
                                            jsonArray = jsonObject1.optJSONArray("filterVendorList");

                                            ArrayList<MyOrderModal> orderModals=new ArrayList<>();
                                            Gson gson = new Gson();
                                            Type type = new TypeToken<List<MyOrderModal>>(){}.getType();
                                            orderModals.addAll(gson.fromJson(jsonArray.toString(), type));

                                            if (categoryId!=0) {
                                                for (int i = 0; i < orderModals.size(); i++) {
                                                    if (orderModals.get(i).getCategoryId() == categoryId) {

                                                        myOrderModals.add(orderModals.get(i));
                                                    }
                                                }
                                            }else {
                                                myOrderModals.addAll(gson.fromJson(jsonArray.toString(), type));
                                            }


                                            if (maxPrice!=0) {
                                                orderModals.clear();
                                                orderModals.addAll(myOrderModals);
                                                myOrderModals.clear();
                                                for (int i = 0; i < orderModals.size(); i++) {
                                                    if (orderModals.get(i).getPrice()>=minPrice &&orderModals.get(i).getPrice()<=maxPrice ) {

                                                        myOrderModals.add(orderModals.get(i));
                                                    }
                                                }
                                            }

                                            if (rating!=0) {
                                                orderModals.clear();
                                                orderModals.addAll(myOrderModals);
                                                myOrderModals.clear();
                                                for (int i = 0; i < orderModals.size(); i++) {
                                                    if (orderModals.get(i).getRating()<=rating ) {

                                                        myOrderModals.add(orderModals.get(i));
                                                    }
                                                }
                                            }

                                            setAdapter();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }




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

    private void setAdapter(){
        myOrderAdapter = new MyOrderAdapter(mContext, myOrderModals,this);

        recyclerviewCategory.setAdapter(myOrderAdapter);
    }

    private void ReOrder(String orderid) {
        final ProgressDialog progressDialog = new ProgressDialog(BookVendor.this);
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
                                            if (Utility.isConnected())
                                                getOrders();
                                            else {
                                                Utility.showToast("Please check your internet connectivity");
                                            }

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

        Intent data=getIntent();
        fromMain=data.getIntExtra("fromMain",0);
        if (fromMain==1) {
            keyword = data.getStringExtra("searchText");
            rating = data.getIntExtra("rating", 0);

            minPrice = data.getDoubleExtra("minPrice", 0);
            maxPrice = data.getDoubleExtra("maxPrice", 0);
        }
        categoryId = data.getIntExtra("category", 0);
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
        myOrderAdapter = new MyOrderAdapter(mContext, myOrderModals,this);

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

        Intent i=getIntent();
        categoryId=i.getIntExtra("category",0);
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
    public void onListen(int position,String ty) {
        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
            Intent i = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(i);
        } else {
            Intent i=new Intent(BookVendor.this,OrderScreenActivity.class);
            i.putExtra("model",myOrderModals.get(position));
            BookVendor.this.startActivity(i);
        }

       /* DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");*/

        //  ReOrder(myOrderModals.get(position).getId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==200){
            if (resultCode==RESULT_OK){
                 keyword=data.getStringExtra("searchText");
                rating=data.getIntExtra("rating",0);
                categoryId=data.getIntExtra("category",0);
                minPrice=data.getDoubleExtra("minPrice",0);
                maxPrice=data.getDoubleExtra("maxPrice",0);
                 getOrders();
            }
        }
    }
}
