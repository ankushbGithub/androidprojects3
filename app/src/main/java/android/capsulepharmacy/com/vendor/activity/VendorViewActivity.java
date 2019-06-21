package android.capsulepharmacy.com.vendor.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.activity.BookVendor;
import android.capsulepharmacy.com.activity.LoginActivity;
import android.capsulepharmacy.com.activity.OrderScreenActivity;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.listener.MyListener;
import android.capsulepharmacy.com.modal.BookSubCategory;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.DeleteGalleryDialog;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.adapter.SubCatBookSelectedAdapter;
import android.capsulepharmacy.com.vendor.adapter.SubCatSelectedAdapter;
import android.capsulepharmacy.com.vendor.adapter.SubCatViewSelectedAdapter;
import android.capsulepharmacy.com.vendor.adapter.VendorGalleryAdapter;
import android.capsulepharmacy.com.vendor.adapter.VendorViewGalleryAdapter;
import android.capsulepharmacy.com.vendor.modal.CategoryListModal;
import android.capsulepharmacy.com.vendor.modal.GalleryModal;
import android.capsulepharmacy.com.vendor.modal.SelectedSubCatList;
import android.capsulepharmacy.com.vendor.modal.ServiceFilterModal;
import android.capsulepharmacy.com.vendor.modal.SubCatFilterModal;
import android.capsulepharmacy.com.vendor.modal.SubCatListModel;
import android.capsulepharmacy.com.vendor.singleton.CategoryListSignleton;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.selectedSubCatLists;

public class VendorViewActivity extends BaseActivity implements MyListener,VendorViewGalleryAdapter.ThreeDots, VendorViewGalleryAdapter.DeleteImage, DeleteGalleryDialog.GalleryDelete{

    private Context mContext;
    private static final String TAG = VendorViewActivity.class.getSimpleName();
    private Toolbar toolbar;
    private RecyclerView rvImages;
    private ArrayList<GalleryModal> galleryModals = new ArrayList<>();
    private VendorViewGalleryAdapter vendorGalleryAdapter;
    private MyOrderModal myOrderModal;
    TextView tvCategoryName,tvServiceAt;
    private RecyclerView recyclerViewSubCategory;
    private SubCatViewSelectedAdapter subCatBookSelectedAdapter;
    private ArrayList<BookSubCategory> selectedSubCatLists=new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_view);
        mContext = VendorViewActivity.this;


        Intent i = getIntent();

        myOrderModal=(MyOrderModal) i.getSerializableExtra("model");
        if (NetUtil.isNetworkConnected(mContext)) {
            getVendorDetailsList(myOrderModal.getVendorId());
        } else {
            Toast.makeText(mContext, getResources().getText(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        applyInit();
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
                            Log.e(TAG, "Response***" + responseData);
                            if (responseData != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        findViewById(responseData);
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

    private void applyInit() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
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


    private void findViewById(String responseData) {
        recyclerViewSubCategory=findViewById(R.id.recyclerViewSubCategory);

        rvImages = findViewById(R.id.rvImages);
       CircleImageView img_ProfilePic = findViewById(R.id.img_ProfilePic);
       TextView tvName = findViewById(R.id.tvName);
       TextView tvFirmName = findViewById(R.id.tvFirmName);
        tvCategoryName = findViewById(R.id.tvCategoryName);
        tvServiceAt = findViewById(R.id.tvServiceAt);
       TextView tvMembers = findViewById(R.id.tvMembers);
       TextView tvPackagePrice = findViewById(R.id.tvPackagePrice);
       TextView tvPriceRange = findViewById(R.id.tvPriceRange);
       TextView tvPackageDescription = findViewById(R.id.tvPackageDescription);
       Button btnProceed = findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                    Intent i = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(i);
                } else {
                    Intent i=new Intent(VendorViewActivity.this, OrderScreenActivity.class);
                    i.putExtra("model",myOrderModal);
                    VendorViewActivity.this.startActivity(i);
                }

            }
        });

        JSONObject jObj = null;
        try {
            jObj = new JSONObject(responseData);
            tvName.setText(jObj.optString("Name"));
            tvFirmName.setText(jObj.optString("FirmName"));
            tvPackageDescription.setText(jObj.optString("PDescription"));
            tvPackagePrice.setText("Rs. "+jObj.optDouble("Price"));

            tvMembers.setText(jObj.optInt("MinMember")+" - "+jObj.optInt("MaxMember"));
            tvPriceRange.setText("Rs. "+jObj.optInt("MinPrice")+" - Rs. "+jObj.optInt("MaxPrice"));

            tvCategoryName.setText(myOrderModal.getCategoryName());
            tvServiceAt.setText(myOrderModal.getServiceAt());
            if (Utility.validateString(jObj.optString("PhotoUrl"))) {
                Picasso.get().load(jObj.optString("PhotoUrl").trim()).placeholder(R.drawable.user).error(R.drawable.user).fit().into(img_ProfilePic);
            }

            JSONArray jsonArray=jObj.optJSONArray("vendorSubCategoryList");
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject2=jsonArray.optJSONObject(i);
                BookSubCategory bookSubCategory=new BookSubCategory();
                bookSubCategory.setId(jsonObject2.optInt("Id"));
                //  bookSubCategory.setQty(jsonObject2.optInt(""));
                bookSubCategory.setSubCategoryDescription(jsonObject2.optString("SubCategoryDescription"));
                bookSubCategory.setSubCategoryId(jsonObject2.optInt("SubCategoryId"));
                bookSubCategory.setSubCategoryName(jsonObject2.optString("SubCategoryName"));
                bookSubCategory.setSubCategoryPrice(jsonObject2.optDouble("SubCategoryPrice"));
                bookSubCategory.setVendorId(jsonObject2.optInt("VendorId"));

                selectedSubCatLists.add(bookSubCategory);

            }

            setSelectedRecyclerView();
            } catch (JSONException e) {
            e.printStackTrace();
        }






        if (NetUtil.isNetworkAvailable(mContext)) {
            getImagesList();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void setSelectedRecyclerView() {

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(VendorViewActivity.this);
        recyclerViewSubCategory.setLayoutManager(mLayoutManager);
        subCatBookSelectedAdapter = new SubCatViewSelectedAdapter(mContext, selectedSubCatLists,this);
        recyclerViewSubCategory.setAdapter(subCatBookSelectedAdapter);
    }

    private void getImagesList() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_VENDOR_GET + myOrderModal.getVendorId();
        Log.e(TAG, "Get Request**" + url);

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
                runOnUiThread(() -> {
                    progressDialog.dismiss();


                });
                try {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Log.e(TAG, "Category List Response***" + responseData);
                        if (responseData != null) {
                            runOnUiThread(() ->
                                    setServerData(responseData));
                        }

                    } else if (response.code() == Constants.BAD_REQUEST) {
                        runOnUiThread(() -> {
                            String errorBodyString = null;
                            try {
                                errorBodyString = response.body().string();

                                try {
                                    JSONObject jsonObject1 = new JSONObject(errorBodyString);
                                    if (jsonObject1.has("error_description")) {
                                        Utility.messageDialog(VendorViewActivity.this, "Bad Request Error", jsonObject1.optString("error_description"));
                                        //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (jsonObject1.has("message")) {
                                            Utility.messageDialog(VendorViewActivity.this, "Bad Request Error", jsonObject1.optString("message"));
                                        } else {
                                            Utility.messageDialog(VendorViewActivity.this, "Bad Request Error", jsonObject1.optString("Message"));
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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


    private void setServerData(String responseData) {
        galleryModals.clear();
        try {
            JSONArray cjsonArray = new JSONArray(responseData);
            for (int i = 0; i < cjsonArray.length(); i++) {
                JSONObject jsonObject1 = cjsonArray.optJSONObject(i);
                GalleryModal galleryModal = new GalleryModal();
                galleryModal.Id = jsonObject1.optInt("Id");
                galleryModal.VendorId = jsonObject1.optInt("VendorId");
                galleryModal.ImageUrl = jsonObject1.optString("ImageUrl");
                galleryModal.ImagePath = jsonObject1.optString("ImagePath");
                galleryModals.add(galleryModal);
            }

            rvImages.setLayoutManager(new GridLayoutManager(mContext, 2));
            vendorGalleryAdapter = new VendorViewGalleryAdapter(mContext, galleryModals, this,this);
            rvImages.setAdapter(vendorGalleryAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

       /* try {
            fetchCategoryList();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
    private void fetchCategoryList() {
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
                        Log.e(TAG, "Response***" + rData);
                        if (rData != null) {
                            runOnUiThread(() ->

                                    setDropDown( rData));
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

    private void setDropDown(String rData) {

        try {
            JSONArray cjsonArray = new JSONArray(rData);
            for (int i = 0; i < cjsonArray.length(); i++) {
                JSONObject jsonObject1 = cjsonArray.optJSONObject(i);
                CategoryListModal categoryListModal = new CategoryListModal();
                categoryListModal.Id = jsonObject1.optInt("Id");
                categoryListModal.Name = jsonObject1.optString("Name");
                CategoryListSignleton.getInstance().addToArray(categoryListModal);
            }
          //  fetchServcieAt(isUpdate, responseData);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onGalleryDelete(int id) {

    }

    @Override
    public void onThreeDotCLicked(View v, int position) {

    }

    @Override
    public void onDeleteImage(View v, int position) {

    }

    @Override
    public void onListen(int position, String type) {

    }
}
