package android.capsulepharmacy.com.vendor.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.activity.OrderScreenActivity;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.base.CustomFilterDialog;
import android.capsulepharmacy.com.listener.AddressCardClick;
import android.capsulepharmacy.com.listener.BankDetailsCardClick;
import android.capsulepharmacy.com.modal.FilterModel;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.adapter.BankDetailsAdapter;
import android.capsulepharmacy.com.vendor.adapter.DeliveryAddressAdapter;
import android.capsulepharmacy.com.vendor.adapter.SubCatSelectedAdapter;
import android.capsulepharmacy.com.vendor.modal.AddressModal;
import android.capsulepharmacy.com.vendor.modal.BankDetails;
import android.capsulepharmacy.com.vendor.modal.CategoryListModal;
import android.capsulepharmacy.com.vendor.modal.SelectedSubCatList;
import android.capsulepharmacy.com.vendor.modal.ServiceAtModal;
import android.capsulepharmacy.com.vendor.modal.ServiceFilterModal;
import android.capsulepharmacy.com.vendor.modal.SubCatFilterModal;
import android.capsulepharmacy.com.vendor.modal.SubCatListModel;
import android.capsulepharmacy.com.vendor.modal.SubListingModal;
import android.capsulepharmacy.com.vendor.singleton.AddressSingleton;
import android.capsulepharmacy.com.vendor.singleton.BankDetailsSingleton;
import android.capsulepharmacy.com.vendor.singleton.CategoryListSignleton;
import android.capsulepharmacy.com.vendor.singleton.SelectedSubCatListSingleton;
import android.capsulepharmacy.com.vendor.singleton.ServiceAtSingleton;
import android.capsulepharmacy.com.vendor.singleton.SubCatListingSingleton;
import android.capsulepharmacy.com.vendor.vendorBase.ServiceBottomDialog;
import android.capsulepharmacy.com.vendor.vendorBase.SubCatBottomDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.AadhaarPath;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.AadhaarUrl;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.Email;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.MaxMember;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.MaxPrice;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.MinMember;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.MinPrice;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.PDescription;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.PanPath;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.PanUrl;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.Password;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.PhotoPath;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.PhotoUrl;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.Price;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.ServiceLocationId;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.VendorAddressDetails;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.VendorBankDetails;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.intentCategoryId;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.selectedSubCatLists;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.serviceFilterModals;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.subCatFilterModals;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.subCatListModels;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.vFirmName;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.vGstin;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.vMobile;
import static android.capsulepharmacy.com.vendor.activity.VendorCreationActivity.vName;


public class VendorDetailsActivity extends BaseActivity implements PaymentResultListener,View.OnClickListener, TextWatcher, AddressCardClick, BankDetailsCardClick {
    private static final String TAG = VendorDetailsActivity.class.getSimpleName();
    private RecyclerView recycler_viewAddress, rvBankDetails;
    DeliveryAddressAdapter deliveryAddressAdapter;
    SubCatSelectedAdapter selectedAdapter;
    BankDetailsAdapter bankDetailsAdapter;
    Context context;
    private TextView tvAddAttach;
    private TextView tvBankDetails;
    private EditText etCategory, etCategoryText, etServiceAt, etMaxMem, etMinMem, etPackagePrice;

    private SignaturePad signature_pad;
    private Button clearButton;
    private SharedPreferences sharedpreferences;
    private static final String mypreference = "Data";


    private AppCompatButton btnSubmit;
    private boolean isUpdate;
    private EditText etPackDesp, etMinPrice, etMaxPrice;
    private RecyclerView rvSubCatList;
    private LinearLayout lSubCatList, lMinMem, lMaxMem;
    public int Id, SubCategoryId;
    private String response;
    public static ArrayList<FilterModel> filterModels = new ArrayList<>();
    private CheckBox checkbox;
    private boolean isChecked;
    public static Activity activity;
    public static boolean activityState = false;
    ProgressDialog progressDialog1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor_details);
        context = VendorDetailsActivity.this;
        activity = VendorDetailsActivity.this;
        selectedSubCatLists.clear();
        sharedpreferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Intent i = getIntent();
        isUpdate = i.getBooleanExtra("isUpdate", false);
        response = i.getStringExtra("response");

        getSharedValue();

        setHeader();
        inializeViews();
        setAddRecyclerview();
        setBankRecyclerview();
        setSelectedRecyclerView();
        setAddress(VendorAddressDetails);
        setRvBankDetails(VendorBankDetails);
        setEtValues();

        if (isUpdate) {
            btnSubmit.setText("Proceed");
            setUpdatedData();
        } else {
            btnSubmit.setText("Proceed to pay Rs. 500");
            setDropDownList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityState = true;
        Log.e(TAG, "Destroyed");
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityState = true;
        Log.e(TAG, "Stopped");
    }


    private void setEtValues() {
        if (MinMember != 0) {
            etMinMem.setText(MinMember + "");
        } else {
            etMinMem.setText(0 + "");
        }

        if (MaxMember != 0) {
            etMaxMem.setText(MaxMember + "");
        } else {
            etMaxMem.setText(0 + "");
        }

        if (Price != 0) {
            etPackagePrice.setText(Price + "");
        } else {
            etPackagePrice.setText(0 + "");
        }

        if (Utility.validateString(PDescription) && !PDescription.equalsIgnoreCase("null")) {
            etPackDesp.setText(PDescription);
        } else {
            etPackDesp.setText("");
        }

        if (MinPrice != 0) {
            etMinPrice.setText(MinPrice + "");
        } else {
            etMinPrice.setText(0 + "");
        }

        if (MaxPrice != 0) {
            etMaxPrice.setText(MaxPrice + "");
        } else {
            etMaxPrice.setText(0 + "");
        }
    }

    private void setSelectedRecyclerView() {
        if (selectedSubCatLists.size() > 0) {
            lSubCatList.setVisibility(View.VISIBLE);
        } else {
            lSubCatList.setVisibility(View.GONE);
        }
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(VendorDetailsActivity.this);
        rvSubCatList.setLayoutManager(mLayoutManager);
        selectedAdapter = new SubCatSelectedAdapter(mContext, selectedSubCatLists);
        rvSubCatList.setAdapter(selectedAdapter);
    }

    private void setBankRecyclerview() {
        rvBankDetails.setHasFixedSize(true);
        rvBankDetails.setLayoutManager(new LinearLayoutManager(VendorDetailsActivity.this));
        bankDetailsAdapter = new BankDetailsAdapter(mContext, BankDetailsSingleton.getInstance().getArray(), this);
        rvBankDetails.setAdapter(bankDetailsAdapter);
    }

    private void setAddRecyclerview() {
        recycler_viewAddress.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(VendorDetailsActivity.this);
        recycler_viewAddress.setLayoutManager(mLayoutManager);
        deliveryAddressAdapter = new DeliveryAddressAdapter(mContext, AddressSingleton.getInstance().getArray(), this);
        recycler_viewAddress.setAdapter(deliveryAddressAdapter);
    }


    private void setUpdatedData() {
        String categoryName = "";
        ArrayList<CategoryListModal> categoryListModals = CategoryListSignleton.getInstance().getArray();
        String[] rfilterArray = new String[categoryListModals.size()];
        for (int i = 0; i < categoryListModals.size(); i++) {
            if (categoryListModals.get(i).Name != null && !categoryListModals.get(i).Name.isEmpty() && !categoryListModals.get(i).equals("null")) {
                rfilterArray[i] = String.valueOf(categoryListModals.get(i).Id);
                if (rfilterArray[i].equalsIgnoreCase(String.valueOf(intentCategoryId))) {
                    categoryName = categoryListModals.get(i).Name;
                }
            }
        }
        String catCode = intentCategoryId + "-" + categoryName;
        etCategory.setText(categoryName);

        if (categoryName.equalsIgnoreCase("Caterers") || categoryName.equalsIgnoreCase("Caterer")) {
             /*   || categoryName.equalsIgnoreCase("Photographer")
                || categoryName.equalsIgnoreCase("Flower Decoration")
                || categoryName.equalsIgnoreCase("Balloon Decoration")
                || categoryName.equalsIgnoreCase("Invitations Card")
                || categoryName.equalsIgnoreCase("DJs")
                || categoryName.equalsIgnoreCase("Ghodiwala")
                || categoryName.equalsIgnoreCase("Dhool")
                || categoryName.equalsIgnoreCase("Band")
                || categoryName.equalsIgnoreCase("Persist")
                || categoryName.equalsIgnoreCase("Pandit Ji")
                || categoryName.equalsIgnoreCase("Tailoring")
                || categoryName.equalsIgnoreCase("Persist/Pandit Ji")
                || categoryName.equalsIgnoreCase("Band/Dhool/Ghodiwala")) {*/
            lMinMem.setVisibility(View.VISIBLE);
            lMaxMem.setVisibility(View.VISIBLE);
        } else {
            lMinMem.setVisibility(View.GONE);
            lMaxMem.setVisibility(View.GONE);
        }


        String locationName = "";
        ArrayList<ServiceAtModal> serviceAtModals = ServiceAtSingleton.getInstance().getArray();
        String[] rserviceArray = new String[serviceAtModals.size()];
        for (int i = 0; i < serviceAtModals.size(); i++) {
            if (serviceAtModals.get(i).Name != null && !serviceAtModals.get(i).Name.isEmpty() && !serviceAtModals.get(i).equals("null")) {
                rserviceArray[i] = String.valueOf(serviceAtModals.get(i).Id);
                if (rserviceArray[i].equalsIgnoreCase(String.valueOf(ServiceLocationId))) {
                    locationName = serviceAtModals.get(i).Name;
                }
            }
        }
        String locationCode = String.valueOf(ServiceLocationId);
        etServiceAt.setText(locationName);

        getSubCatList(intentCategoryId, true);
        setDropDownList();

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

    private void getSharedValue() {
        vName = sharedpreferences.getString("Name", "");
        Id = sharedpreferences.getInt("Id", 0);
        vFirmName = sharedpreferences.getString("FirmName", "");
        vGstin = sharedpreferences.getString("GSTIN", "");
        vMobile = sharedpreferences.getString("MobileNo", "");
        intentCategoryId = sharedpreferences.getInt("CategoryId", 0);
        ServiceLocationId = sharedpreferences.getInt("ServiceLocationId", 0);
        SubCategoryId = sharedpreferences.getInt("SubCategoryId", 0);
        PhotoUrl = sharedpreferences.getString("PhotoUrl", "");
        PanUrl = sharedpreferences.getString("PanUrl", "");
        AadhaarUrl = sharedpreferences.getString("AadhaarUrl", "");
        PhotoPath = sharedpreferences.getString("PhotoPath", "");
        AadhaarPath = sharedpreferences.getString("AadhaarPath", "");
        PanPath = sharedpreferences.getString("PanPath", "");
        Password = sharedpreferences.getString("Password", "");
        Email = sharedpreferences.getString("Email", "");
        VendorAddressDetails = sharedpreferences.getString("VendorAddressDetails", "");
        VendorBankDetails = sharedpreferences.getString("VendorBankDetails", "");
        MaxMember = sharedpreferences.getInt("MaxMember", 0);
        MinMember = sharedpreferences.getInt("MinMember", 0);
        Price = sharedpreferences.getInt("Price", 0);
        PDescription = sharedpreferences.getString("PDescription", "");
        MaxPrice = sharedpreferences.getInt("MaxPrice", 0);
        MinPrice = sharedpreferences.getInt("MinPrice", 0);


    }


    public void setHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        // toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VendorDetailsActivity.this, VendorCreationActivity.class);
                i.putExtra("isVendorDetails", true);
                i.putExtra("isUpdate", isUpdate);
                i.putExtra("response", response);
                startActivityForResult(i, 300);
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void inializeViews() {
        checkbox = findViewById(R.id.checkbox);
        etCategory = findViewById(R.id.etCategory);
        etCategoryText = findViewById(R.id.etCategoryText);
        etServiceAt = findViewById(R.id.etServiceAt);
        etMaxMem = findViewById(R.id.etMaxMem);
        etMinMem = findViewById(R.id.etMinMem);
        etPackagePrice = findViewById(R.id.etPackagePrice);
        etPackDesp = findViewById(R.id.etPackDesp);
        etMinPrice = findViewById(R.id.etMinPrice);
        etMaxPrice = findViewById(R.id.etMaxPrice);

        signature_pad = findViewById(R.id.signature_pad);
        clearButton = findViewById(R.id.clearButton);
        //  clearButton.setEnabled(false);
        tvBankDetails = findViewById(R.id.tvBankDetails);
        lSubCatList = findViewById(R.id.lSubCatList);
        recycler_viewAddress = findViewById(R.id.recycler_viewAddress);
        rvBankDetails = findViewById(R.id.rvBankDetails);
        rvSubCatList = findViewById(R.id.rvSubCatList);
        lMinMem = findViewById(R.id.lMinMem);
        lMaxMem = findViewById(R.id.lMaxMem);

        btnSubmit = findViewById(R.id.btnSubmit);



        tvAddAttach = findViewById(R.id.tvAddAttach);
        tvAddAttach.setVisibility(View.VISIBLE);


        tvAddAttach.setOnClickListener(this);
        tvBankDetails.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        etCategory.setOnClickListener(this);
        etCategoryText.setOnClickListener(this);
        etServiceAt.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        etCategory.addTextChangedListener(this);
        etCategoryText.addTextChangedListener(this);
        etServiceAt.addTextChangedListener(this);
        etPackagePrice.addTextChangedListener(this);
        etPackDesp.addTextChangedListener(this);
        etMinPrice.addTextChangedListener(this);
        etMaxPrice.addTextChangedListener(this);


        signature_pad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                //  clearButton.setEnabled(false);
            }

            @Override
            public void onClear() {

            }
        });

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isChecked = true;
                } else {
                    isChecked = false;
                }

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        activityState = true;
        getSharedValue();
    }


    private void setAddress(String vendorAddressDetails) {
        AddressSingleton.getInstance().clearArrayList();
        try {
            JSONObject jsonObject = new JSONObject(vendorAddressDetails);
            AddressModal addressModal = new AddressModal();
            addressModal.setId(jsonObject.optInt("Id"));
            addressModal.setVendorId(jsonObject.optInt("VendorId"));
            addressModal.setName(jsonObject.optString("Name"));
            addressModal.setMobileNo(jsonObject.optString("MobileNo"));
            addressModal.setAlternateMobileNo(jsonObject.optString("AlternateMobileNo"));
            addressModal.setFlatNoOrBuildingName(jsonObject.optString("FlatNoOrBuildingName"));
            addressModal.setAreaOrStreet(jsonObject.optString("AreaOrStreet"));
            addressModal.setLandmark(jsonObject.optString("Landmark"));
            addressModal.setState(jsonObject.optString("State"));
            addressModal.setCity(jsonObject.optString("City"));
            addressModal.setPostcode(jsonObject.optString("Postcode"));
            addressModal.setSelected(true);
            AddressSingleton.getInstance().addToArray(addressModal);

            deliveryAddressAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setRvBankDetails(String VendorBankDetails) {
        BankDetailsSingleton.getInstance().clearArrayList();
        try {
            JSONObject jsonObject = new JSONObject(VendorBankDetails);
            BankDetails bankDetails = new BankDetails();
            bankDetails.setId(jsonObject.optInt("Id"));
            bankDetails.setVendorId(jsonObject.optInt("VendorId"));
            bankDetails.setAccountHolderName(jsonObject.optString("AccountHolderName"));
            bankDetails.setAccountNumber(jsonObject.optString("AccountNumber"));
            bankDetails.setBranchName(jsonObject.optString("BranchName"));
            bankDetails.setIFSCCode(jsonObject.optString("IFSCCode"));
            bankDetails.setSelected(true);
            BankDetailsSingleton.getInstance().addToArray(bankDetails);

            bankDetailsAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showServiceAt() {
        Collections.sort(ServiceAtSingleton.getInstance().getArray(), (p1, p2) -> p1.Name.compareToIgnoreCase(p2.Name));
        ServiceBottomDialog.with(getSupportFragmentManager()).setFilterList(serviceFilterModals)
                .setFilterHeader("Service At")
                .setApplyButtonName("Done")
                .setListener(new ServiceBottomDialog.OnFilterSelectionListener() {
                    @Override
                    public void onFilterSelected(List<ServiceFilterModal> list, ServiceFilterModal filterModel) {
                        ArrayList<ServiceAtModal> serviceAtModals = ServiceAtSingleton.getInstance().getArray();
                        String[] serviceArr = new String[serviceAtModals.size()];
                        for (int i = 0; i < serviceAtModals.size(); i++) {
                            serviceArr[i] = serviceAtModals.get(i).Name;
                            if (serviceArr[i].equalsIgnoreCase(filterModel.title)) {
                                ServiceLocationId = serviceAtModals.get(i).Id;
                            }
                        }
                        String serviceCode = ServiceLocationId + "-" + filterModel.title;
                        etServiceAt.setText(filterModel.title);
                    }
                }).build();

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
                        String eEmpCode = intentCategoryId + "-" + filterModel.title;
                        etCategory.setText(filterModel.title);

                        if (filterModel.title.equalsIgnoreCase("Caterers") || filterModel.title.equalsIgnoreCase("Caterer")) {
                         /* || filterModel.title.equalsIgnoreCase("Photographer")
                                || filterModel.title.equalsIgnoreCase("Flower Decoration")
                                || filterModel.title.equalsIgnoreCase("Balloon Decoration")
                                || filterModel.title.equalsIgnoreCase("Invitations Card")
                                || filterModel.title.equalsIgnoreCase("DJs")
                                || filterModel.title.equalsIgnoreCase("Ghodiwala")
                                || filterModel.title.equalsIgnoreCase("Dhool")
                                || filterModel.title.equalsIgnoreCase("Band")
                                || filterModel.title.equalsIgnoreCase("Persist")
                                || filterModel.title.equalsIgnoreCase("Pandit Ji")
                                || filterModel.title.equalsIgnoreCase("Tailoring")
                                || filterModel.title.equalsIgnoreCase("Persist/Pandit Ji")
                                || filterModel.title.equalsIgnoreCase("Band/Dhool/Ghodiwala")) {*/
                            lMinMem.setVisibility(View.VISIBLE);
                            lMaxMem.setVisibility(View.VISIBLE);
                        } else {
                            lMinMem.setVisibility(View.GONE);
                            lMaxMem.setVisibility(View.GONE);
                        }

                        etCategoryText.setText("");
                        SubCategoryId = 0;
                        getSubCatList(intentCategoryId, false);
                    }
                }).build();


    }

    private void getSubCatList(int catId, boolean b) {
        try {
            ArrayList<SubListingModal> subListingModals = SubCatListingSingleton.getInstance().getArray();
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = null;
            for (int i = 0; i < subListingModals.size(); i++) {
                SubListingModal subListingModal = subListingModals.get(i);
                if (catId == subListingModal.CategoryId) {
                    jsonObject = new JSONObject();
                    jsonObject.put("Id", subListingModal.Id);
                    jsonObject.put("Name", subListingModal.Name);
                    jsonArray.put(jsonObject);
                }
            }
            Log.e(TAG, "subCatArray**" + jsonArray.toString());
            setSubCatList(jsonArray.toString(), b);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setSubCatList(String rData, boolean b) {
        subCatListModels.clear();
        subCatFilterModals.clear();
        selectedSubCatLists.clear();
        try {
            JSONArray cjsonArray = new JSONArray(rData);
            for (int i = 0; i < cjsonArray.length(); i++) {
                JSONObject jsonObject1 = cjsonArray.optJSONObject(i);
                SubCatListModel subCatListModel = new SubCatListModel();
                subCatListModel.Id = jsonObject1.optInt("Id");
                subCatListModel.Name = jsonObject1.optString("Name");
                subCatListModels.add(subCatListModel);
            }

//            String[] rSubCatArray = new String[subCatListModels.size()];
//            for (int i = 0; i < subCatListModels.size(); i++) {
//                if (subCatListModels.get(i).Name != null && !subCatListModels.get(i).Name.isEmpty() && !subCatListModels.get(i).equals("null")) {
//                    rSubCatArray[i] = subCatListModels.get(i).Name;
//                }
//            }
            for (int i = 0; i < subCatListModels.size(); i++) {
                if (subCatListModels.get(i).Name != null && !subCatListModels.get(i).Name.isEmpty() && !subCatListModels.get(i).equals("null")) {
                    SubCatFilterModal subCatFilterModal = new SubCatFilterModal();
                    subCatFilterModal.title = subCatListModels.get(i).Name;
                    subCatFilterModal.SubCategoryId = subCatListModels.get(i).Id;
                    subCatFilterModals.add(subCatFilterModal);
                }
            }
//            for (String aRrSubCatArray : rSubCatArray) {
//                SubCatFilterModal subCatFilterModal = new SubCatFilterModal();
//                subCatFilterModal.title = aRrSubCatArray;
//                subCatFilterModal.isSelected = false;
//                subCatFilterModals.add(subCatFilterModal);
//            }

            if (b) {
                for (int i = 0; i < SelectedSubCatListSingleton.getInstance().getArray().size(); i++) {
                    SelectedSubCatList selectedSubCatList1 = SelectedSubCatListSingleton.getInstance().getArray().get(i);
                    if (Utility.validateString(selectedSubCatList1.SubCategoryName) && !selectedSubCatList1.SubCategoryName.equalsIgnoreCase("null")) {
                        SelectedSubCatList selectedSubCatList = new SelectedSubCatList();
                        selectedSubCatList.Id = selectedSubCatList1.Id;
                        selectedSubCatList.VendorId = selectedSubCatList1.VendorId;
                        selectedSubCatList.SubCategoryId = selectedSubCatList1.SubCategoryId;
                        selectedSubCatList.SubCategoryName = selectedSubCatList1.SubCategoryName;
                        selectedSubCatList.SubCategoryDescription = selectedSubCatList1.SubCategoryDescription;
                        selectedSubCatList.SubCategoryPrice = selectedSubCatList1.SubCategoryPrice;
                        selectedSubCatLists.add(selectedSubCatList);
                    }

                }
                if (selectedSubCatLists.size() > 0) {
                    lSubCatList.setVisibility(View.VISIBLE);
                } else {
                    lSubCatList.setVisibility(View.GONE);
                }
                selectedAdapter.notifyDataSetChanged();

//                String SubCatName = "";
//                ArrayList<SubCatListModel> subCatListModelArrayList = subCatListModels;
//                String[] rSubCArray = new String[subCatListModelArrayList.size()];
//                for (int i = 0; i < subCatListModelArrayList.size(); i++) {
//                    if (subCatListModelArrayList.get(i).Name != null && !subCatListModelArrayList.get(i).Name.isEmpty() && !subCatListModelArrayList.get(i).equals("null")) {
//                        rSubCArray[i] = String.valueOf(subCatListModelArrayList.get(i).Id);
//                        if (rSubCArray[i].equalsIgnoreCase(String.valueOf(SubCategoryId))) {
//                            SubCatName = subCatListModelArrayList.get(i).Name;
//                        }
//                    }
//                }
//                String SubCatCode = String.valueOf(SubCategoryId);
//                etCategoryText.setText(SubCatName);
            } else {
//                showSubCategoryList(subCatFilterModals);
                for (int i = 0; i < subCatFilterModals.size(); i++) {
                    SubCatFilterModal subCatFilterModal = subCatFilterModals.get(i);
                    if (Utility.validateString(subCatFilterModal.title) && !subCatFilterModal.title.equalsIgnoreCase("null")) {
                        SelectedSubCatList selectedSubCatList = new SelectedSubCatList();
                        selectedSubCatList.SubCategoryName = subCatFilterModal.title;
                        selectedSubCatList.SubCategoryId = subCatFilterModal.SubCategoryId;
                        selectedSubCatList.VendorId = Id;
                        selectedSubCatLists.add(selectedSubCatList);
                    }

                }
                if (selectedSubCatLists.size() > 0) {
                    lSubCatList.setVisibility(View.VISIBLE);
                } else {
                    lSubCatList.setVisibility(View.GONE);
                }
                selectedAdapter.notifyDataSetChanged();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showSubCategoryList(ArrayList<SubCatFilterModal> subCatFilterModals1) {
        Collections.sort(subCatListModels, (p1, p2) -> p1.Name.compareToIgnoreCase(p2.Name));
        SubCatBottomDialog.with(getSupportFragmentManager()).setFilterList(subCatFilterModals1)
                .setFilterHeader("Sub Category List")
                .setApplyButtonName("Done")
                .setListener(new SubCatBottomDialog.OnFilterSelectionListener() {
                    @Override
                    public void onFilterSelected(List<SubCatFilterModal> list, SubCatFilterModal filterModel) {
                        ArrayList<SubCatListModel> subCatListModelArrayList = subCatListModels;
                        String[] subCatArr = new String[subCatListModelArrayList.size()];
                        for (int i = 0; i < subCatListModelArrayList.size(); i++) {
                            subCatArr[i] = subCatListModelArrayList.get(i).Name;
                            if (subCatArr[i].equalsIgnoreCase(filterModel.title)) {
                                SubCategoryId = subCatListModelArrayList.get(i).Id;
                            }
                        }
                        String eSubCatCode = String.valueOf(SubCategoryId);
                        etCategoryText.setText(filterModel.title);

                    }
                }).build();


    }

    private void filterData(String[] arr) {
        filterModels.clear();
        for (int i = 0; i < arr.length; i++) {
            FilterModel filterModel = new FilterModel();
            filterModel.title = arr[i];
            if (i == 0)
                filterModel.isSelected = true;
            filterModels.add(filterModel);
        }
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(VendorDetailsActivity.this, VendorCreationActivity.class);
        i.putExtra("isVendorDetails", true);
        i.putExtra("isUpdate", isUpdate);
        i.putExtra("response", response);
        startActivityForResult(i, 300);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            deliveryAddressAdapter.notifyDataSetChanged();
        }
        if (requestCode == 200) {
            bankDetailsAdapter.notifyDataSetChanged();
        }
        if (requestCode == 300) {
            selectedAdapter.notifyDataSetChanged();
        }

    }

    private boolean checkValidation() {
        boolean isValid = true;

        String categoryName = etCategory.getText().toString();
        String subCategoryName = etCategoryText.getText().toString();
        String serviceAt = etServiceAt.getText().toString();


        if (!Utility.validateString(categoryName)) {
            isValid = false;
            etCategory.setError("Please select Category");
            return isValid;
        }
        if (!Utility.validateString(serviceAt)) {
            isValid = false;
            etServiceAt.setError("Please select Service at");
            return isValid;
        }
        if (!Utility.validateString(etPackagePrice.getText().toString())) {
            isValid = false;
            etPackagePrice.setError("Please enter Complete package price");
            return isValid;
        }
//        if (!Utility.validateString(etPackDesp.getText().toString())){
//            isValid = false;
//            etPackDesp.setError("Please enter Package desciption");
//            return isValid;
//        }
//        if (!Utility.validateString(etMinPrice.getText().toString())){
//            isValid = false;
//            etMinPrice.setError("Please enter Minimum price");
//            return isValid;
//        }
//        int minPrice = Integer.parseInt(etMinPrice.getText().toString());
//        if (!Utility.validateString(etMinPrice.getText().toString())&&minPrice==0){
//            isValid = false;
//            etMinPrice.setError("Please enter Minimum price");
//            return isValid;
//        }
//        if (!Utility.validateString(etMaxPrice.getText().toString())){
//            isValid = false;
//            etMaxPrice.setError("Please enter Maximun price");
//            return isValid;
//        }
//        int maxPrice = Integer.parseInt(etMaxPrice.getText().toString());
//        if (!Utility.validateString(etMaxPrice.getText().toString())&& maxPrice==0){
//            isValid = false;
//            etMaxPrice.setError("Please enter Maximun price");
//            return isValid;
//        }
        int completePackage = Integer.parseInt(etPackagePrice.getText().toString());
        if (!Utility.validateString(etPackagePrice.getText().toString()) && completePackage == 0) {
            isValid = false;
            etPackagePrice.setError("Please enter Complete package price");
            return isValid;
        }

        boolean isAddressAdded = false;
        for (int i = 0; i < AddressSingleton.getInstance().getArray().size(); i++) {
            AddressModal addressModal = AddressSingleton.getInstance().getArray().get(i);
            if (addressModal.isSelected()) {
                isAddressAdded = true;
            }
        }

        boolean isBankAddes = false;
        for (int i = 0; i < BankDetailsSingleton.getInstance().getArray().size(); i++) {
            BankDetails bankDetails = BankDetailsSingleton.getInstance().getArray().get(i);
            if (bankDetails.isSelected()) {
                isBankAddes = true;
            }
        }

        if (isValid) {
            if (isAddressAdded) {
                if (isBankAddes) {
                    Bitmap signatureBitmap = signature_pad.getTransparentSignatureBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    Log.e(TAG, "Signature***" + encoded);
                    return isValid;


                } else {
                    Toast.makeText(mContext, "Please select Bank details", Toast.LENGTH_LONG).show();
                    isValid = false;
                    return isValid;
                }
            } else {
                Toast.makeText(mContext, "Please select Address", Toast.LENGTH_LONG).show();
                isValid = false;
                return isValid;
            }
        }


        return isValid;
    }

    private void submitDetails(boolean isUpdate) {
        try {
            int MinMem = 0, MaxMem = 0;
            double Price = 0;
            String packageDes = "";
            double minPrice = 0;
            double maxPrice = 0;
            if (Utility.validateString(etMinMem.getText().toString())) {
                MinMem = Integer.parseInt(etMinMem.getText().toString());
            }
            if (Utility.validateString(etMaxMem.getText().toString())) {
                MaxMem = Integer.parseInt(etMaxMem.getText().toString());
            }
            if (Utility.validateString(etPackagePrice.getText().toString())) {
                Price = Integer.parseInt(etPackagePrice.getText().toString());
            }
            if (Utility.validateString(etPackDesp.getText().toString())) {
                packageDes = etPackDesp.getText().toString();
            }

            if (Utility.validateString(etMinPrice.getText().toString())) {
                minPrice = Double.parseDouble(etMinPrice.getText().toString());
            }
            if (Utility.validateString(etMaxPrice.getText().toString())) {
                maxPrice = Double.parseDouble(etMaxPrice.getText().toString());
            }


            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            JSONObject jsonObject = new JSONObject();
            if (isUpdate) {
                jsonObject.put("Id", Id);
            } else {
                jsonObject.put("Id", 0);
            }
            jsonObject.put("Name", vName);
            if (Utility.validateString(vGstin)) {
                jsonObject.put("GSTIN", vGstin);
            } else {
                jsonObject.put("GSTIN", "NA");
            }

            jsonObject.put("MobileNo", vMobile);
            jsonObject.put("PDescription", packageDes);
            jsonObject.put("MinPrice", minPrice);
            jsonObject.put("MaxPrice", maxPrice);
            jsonObject.put("FirmName", vFirmName);
            jsonObject.put("CategoryId", intentCategoryId);
            jsonObject.put("SubCategoryId", SubCategoryId);
            jsonObject.put("ServiceLocationId", ServiceLocationId);
            if (PhotoPath.startsWith("G://") || PhotoPath.startsWith("G:\\") || PhotoPath.startsWith("F://") || PhotoPath.startsWith("F:\\") || PhotoPath.startsWith("C://") || PhotoPath.startsWith("C:\\")) {
                jsonObject.put("PhotoPath", "na");
            } else {
                jsonObject.put("PhotoPath", PhotoPath);
            }
            if (AadhaarPath.startsWith("G://") || AadhaarPath.startsWith("G:\\") || AadhaarPath.startsWith("F://") || AadhaarPath.startsWith("F:\\") || AadhaarPath.startsWith("C://") || AadhaarPath.startsWith("C:\\")) {
                jsonObject.put("AadhaarPath", "na");
            } else {
                jsonObject.put("AadhaarPath", AadhaarPath);
            }
            if (PanPath.startsWith("G://") || PanPath.startsWith("G:\\") || PanPath.startsWith("F://") || PanPath.startsWith("F:\\") || PanPath.startsWith("C://") || PanPath.startsWith("C:\\")) {
                jsonObject.put("PanPath", "na");
            } else {
                jsonObject.put("PanPath", PanPath);
            }
            jsonObject.put("Password", Password);
            jsonObject.put("Email", Email);
            if (PhotoUrl.startsWith("http://") || PhotoUrl.startsWith("https://")) {
                jsonObject.put("PhotoUrl", "na");
            } else {
                jsonObject.put("PhotoUrl", PhotoUrl);
            }

            if (PanUrl.startsWith("http://") || PanUrl.startsWith("https://")) {
                jsonObject.put("PanUrl", "na");
            } else {
                jsonObject.put("PanUrl", PanUrl);
            }

            if (AadhaarUrl.startsWith("http://") || AadhaarUrl.startsWith("https://")) {
                jsonObject.put("AadhaarUrl", "na");
            } else {
                jsonObject.put("AadhaarUrl", AadhaarUrl);
            }
            jsonObject.put("MinMember", MinMem);
            jsonObject.put("MaxMember", MaxMem);
            jsonObject.put("Price", Price);

            for (int i = 0; i < AddressSingleton.getInstance().getArray().size(); i++) {
                AddressModal addressModal = AddressSingleton.getInstance().getArray().get(i);
                if (addressModal.isSelected()) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("Id", addressModal.getId());
                    jsonObject1.put("VendorId", addressModal.getVendorId());
                    jsonObject1.put("Name", addressModal.getName());
                    jsonObject1.put("MobileNo", addressModal.getMobileNo());
                    if (Utility.validateString(addressModal.getAlternateMobileNo())) {
                        jsonObject1.put("AlternateMobileNo", addressModal.getAlternateMobileNo());
                    } else {
                        jsonObject1.put("AlternateMobileNo", "na");
                    }
                    jsonObject1.put("FlatNoOrBuildingName", addressModal.getFlatNoOrBuildingName());
                    jsonObject1.put("AreaOrStreet", addressModal.getAreaOrStreet());
                    jsonObject1.put("Landmark", addressModal.getLandmark());
                    jsonObject1.put("State", addressModal.getState());
                    jsonObject1.put("City", addressModal.getCity());
                    jsonObject1.put("Postcode", addressModal.getPostcode());
                    jsonObject.put("VendorAddressDetails", jsonObject1);
                }
            }
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < selectedSubCatLists.size(); i++) {
                SelectedSubCatList selectedSubCatList = selectedSubCatLists.get(i);
                if (selectedSubCatList.SubCategoryPrice != 0) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("Id", selectedSubCatList.Id);
                    jsonObject1.put("VendorId", Id);
                    jsonObject1.put("SubCategoryId", selectedSubCatList.SubCategoryId);
                    jsonObject1.put("SubCategoryName", selectedSubCatList.SubCategoryName);
                    jsonObject1.put("SubCategoryDescription", selectedSubCatList.SubCategoryDescription);
                    jsonObject1.put("SubCategoryPrice", selectedSubCatList.SubCategoryPrice);
                    jsonArray.put(jsonObject1);
                }
            }
            jsonObject.put("vendorSubCategoryList", jsonArray);


            for (int i = 0; i < BankDetailsSingleton.getInstance().getArray().size(); i++) {
                BankDetails bankDetails = BankDetailsSingleton.getInstance().getArray().get(i);
                if (bankDetails.isSelected()) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("Id", bankDetails.getId());
                    jsonObject1.put("VendorId", bankDetails.getVendorId());
                    jsonObject1.put("AccountHolderName", bankDetails.getAccountHolderName());
                    jsonObject1.put("AccountNumber", bankDetails.getAccountNumber());
                    jsonObject1.put("BranchName", bankDetails.getBranchName());
                    jsonObject1.put("IFSCCode", bankDetails.getIFSCCode());
                    jsonObject.put("VendorBankDetails", jsonObject1);
                }
            }

            Log.e(TAG, "*JsonArr**" + jsonObject.toString());
            OkHttpClient okHttpClient = APIClient.getHttpClient();
            RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, jsonObject.toString());
            Request request;
            if (isUpdate) {
                String url = CapsuleAPI.WEB_SERVICE_VENDOR_UPDATE + Id;
                request = APIClient.getPutRequest(this, url, requestBody);
            } else {
                String url = CapsuleAPI.WEB_SERVICE_VENDOR_CREATE;
                request = APIClient.getCustomerCreatePostRequest(this, url, requestBody);
            }

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                    try {
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Log.e(TAG, "USER creation Response***" + Objects.requireNonNull(response.body()).toString());

                                }
                            });
                            final String responseData = Objects.requireNonNull(response.body()).string();
                            if (responseData != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject jsonObject1 = new JSONObject(responseData);
                                            if (jsonObject1.optInt("code") == 200) {
                                                Log.e(TAG, "Json***" + jsonObject1.toString());
                                                VendorCreationActivity.activity.finish();
                                                if (isUpdate) {
                                                    Toast.makeText(mContext, jsonObject1.optString("message"), Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(mContext, jsonObject1.optString("message"), Toast.LENGTH_LONG).show();
                                                }
                                                if (isUpdate) {
                                                    Intent i = new Intent(mContext, VendorGalleryActivity.class);
                                                    i.putExtra("VendorId", Id);
                                                    startActivity(i);
                                                    finish();
                                                } else {
                                                    Intent i = new Intent(mContext, VendorGalleryActivity.class);
                                                    i.putExtra("VendorId", jsonObject1.optInt("id"));
                                                    startActivity(i);
                                                    finish();
                                                }


                                            } else {
                                                Toast.makeText(mContext, jsonObject1.optString("message"), Toast.LENGTH_LONG).show();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                        } else if (response.code() == Constants.BAD_REQUEST) {
                            runOnUiThread(() -> {
                                String errorBodyString = null;
                                try {
                                    errorBodyString = response.body().string();

                                    try {
                                        JSONObject jsonObject1 = new JSONObject(errorBodyString);
                                        if (jsonObject1.has("error_description")) {
                                            Utility.messageDialog(VendorDetailsActivity.this, "Bad Request Error", jsonObject1.optString("error_description"));
                                            //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Utility.messageDialog(VendorDetailsActivity.this, "Bad Request Error", jsonObject1.optString("message"));

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else if (response.code() == Constants.INTERNAL_SERVER_ERROR) {

                            runOnUiThread(() -> {
                                String errorBodyString = null;
                                try {
                                    errorBodyString = response.body().string();

                                    try {
                                        JSONObject jsonObject1 = new JSONObject(errorBodyString);
                                        if (jsonObject1.has("error_description")) {
                                            Utility.messageDialog(VendorDetailsActivity.this, "Internal Server Error", jsonObject1.optString("error_description"));
                                            //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Utility.messageDialog(VendorDetailsActivity.this, "Internal Server Error", jsonObject1.optString("message"));

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAddAttach:
                Intent i = new Intent(VendorDetailsActivity.this, AddDeliveryActivity.class);
                i.putExtra("isAdded", false);
                startActivityForResult(i, 100);
                break;
            case R.id.tvBankDetails:
                Intent i1 = new Intent(VendorDetailsActivity.this, BankDetailsActivity.class);
                i1.putExtra("isAdded", false);
                startActivityForResult(i1, 200);
                break;
            case R.id.btnSubmit:

                if (isChecked) {
                    if (checkValidation()) {
                        if (!isUpdate) {
                            progressDialog1 = new ProgressDialog(VendorDetailsActivity.this);
                            progressDialog1.show();
                            startPayment();
                        }else {
                            submitDetails(isUpdate);
                        }


                    }
                } else {
                    Utility.showToast("Please accept terms and conditions");
                }


                break;
            case R.id.etCategory:
                showCategory();
                break;
            case R.id.etCategoryText:
                if (Utility.validateString(etCategory.getText().toString())) {
                    getSubCatList(intentCategoryId, false);
                } else {
                    Toast.makeText(mContext, "Please select Category", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.etServiceAt:
                showServiceAt();
                break;
            case R.id.clearButton:
                signature_pad.clear();
            default:
                break;
        }
    }

    public void startPayment() {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Party Planner");
            options.put("description", "Membership");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");

            //   String payment = editTextPayment.getText().toString();

         //   double total = Double.parseDouble(input_payAmt.getText().toString().trim());
          //  total = total * 100;
            options.put("amount", 500*100);

            JSONObject preFill = new JSONObject();
            preFill.put("email",Email);
            preFill.put("contact",vMobile);

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        submitDetails(isUpdate);
      //  createOrder(200,"Payment successfully done!",razorpayPaymentID);
        Toast.makeText(this, "Payment successfully done! " + razorpayPaymentID, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            //createOrder(code,response,"");
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == etCategory.getEditableText()) {
            etCategory.setError(null);
        }
        if (s == etCategoryText.getEditableText()) {
            etCategoryText.setError(null);
        }
        if (s == etServiceAt.getEditableText()) {
            etServiceAt.setError(null);
        }
        if (s == etPackagePrice.getEditableText()) {
            etPackagePrice.setError(null);
        }
        if (s == etPackDesp.getEditableText()) {
            etPackDesp.setError(null);
        }
        if (s == etMinPrice.getEditableText()) {
            etMinPrice.setError(null);
        }
        if (s == etMaxPrice.getEditableText()) {
            etMaxPrice.setError(null);
        }
    }

    @Override
    public void onAddressCardClick(int pos) {
        AddressModal addressModal = AddressSingleton.getInstance().getArray().get(pos);
        Intent i = new Intent(context, AddDeliveryActivity.class);
        i.putExtra("position", pos);
        i.putExtra("id", addressModal.getId());
        i.putExtra("VendorId", addressModal.getVendorId());
        i.putExtra("name", addressModal.getName());
        i.putExtra("mobile", addressModal.getMobileNo());
        i.putExtra("altMobile", addressModal.getAlternateMobileNo());
        i.putExtra("flat", addressModal.getFlatNoOrBuildingName());
        i.putExtra("area", addressModal.getAreaOrStreet());
        i.putExtra("landmark", addressModal.getLandmark());
        i.putExtra("state", addressModal.getState());
        i.putExtra("city", addressModal.getCity());
        i.putExtra("pinCode", addressModal.getPostcode());
        i.putExtra("isAdded", true);
        startActivityForResult(i, 100);

    }

    @Override
    public void onBankCardClick(int pos) {
        BankDetails bankDetails = BankDetailsSingleton.getInstance().getArray().get(pos);
        Intent i = new Intent(context, BankDetailsActivity.class);
        i.putExtra("position", pos);
        i.putExtra("id", bankDetails.getId());
        i.putExtra("VendorId", bankDetails.getVendorId());
        i.putExtra("holderName", bankDetails.getAccountHolderName());
        i.putExtra("accountNumber", bankDetails.getAccountNumber());
        i.putExtra("branchName", bankDetails.getBranchName());
        i.putExtra("ifscCode", bankDetails.getIFSCCode());
        i.putExtra("isAdded", true);
        startActivityForResult(i, 200);
    }
}
