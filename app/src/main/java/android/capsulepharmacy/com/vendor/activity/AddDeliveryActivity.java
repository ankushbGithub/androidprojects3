package android.capsulepharmacy.com.vendor.activity;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.modal.AddressModal;
import android.capsulepharmacy.com.vendor.singleton.AddressSingleton;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;


public class AddDeliveryActivity extends AppCompatActivity implements TextWatcher {
    private Context mContext;

    private TextInputLayout tilName, tilMobile, tilAltMobile, tilFlat, tilArea, tilLandmark, tilState, tilCity, tilPinCode;
    private TextInputEditText tieName, tieMobile, tieAltMobile, tieFlat, tieArea, tielandmark, tieState, tieCity, tiePincode;
    private String name, mobile, altMobile, flat, area, landmark, state, city, pinCode;
    private int id, VendorId,position;
    private boolean isAdded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_delivery_address_activity);
        mContext = AddDeliveryActivity.this;
        getAddress();
        setHeader();
        init();
        setValue();
    }

    private void getAddress() {
        Intent i = getIntent();
        id = i.getIntExtra("id", 0);
        VendorId = i.getIntExtra("VendorId", 0);
        name = i.getStringExtra("name");
        mobile = i.getStringExtra("mobile");
        altMobile = i.getStringExtra("altMobile");
        flat = i.getStringExtra("flat");
        area = i.getStringExtra("area");
        landmark = i.getStringExtra("landmark");
        state = i.getStringExtra("state");
        city = i.getStringExtra("city");
        pinCode = i.getStringExtra("pinCode");
        isAdded = i.getBooleanExtra("isAdded", false);
        position = i.getIntExtra("position", 0);
    }

    private void setValue() {
        tieName.setText(name);
        tieMobile.setText(mobile);
        tieAltMobile.setText(altMobile);
        tieFlat.setText(flat);
        tieArea.setText(area);
        tieState.setText(state);
        tieCity.setText(city);
        tiePincode.setText(pinCode);
    }

    public void setHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    private void init() {
        tilName = findViewById(R.id.tilName);
        tilMobile = findViewById(R.id.tilMobile);
        tilAltMobile = findViewById(R.id.tilAltMobile);
        tilFlat = findViewById(R.id.tilFlat);
        tilArea = findViewById(R.id.tilArea);
        tilLandmark = findViewById(R.id.tilLandmark);
        tilState = findViewById(R.id.tilState);
        tilCity = findViewById(R.id.tilCity);
        tilPinCode = findViewById(R.id.tilPinCode);

        tieName = findViewById(R.id.tieName);
        tieMobile = findViewById(R.id.tieMobile);
        tieAltMobile = findViewById(R.id.tieAltMobile);
        tieFlat = findViewById(R.id.tieFlat);
        tieArea = findViewById(R.id.tieArea);
        tielandmark = findViewById(R.id.tielandmark);
        tieState = findViewById(R.id.tieState);
        tieCity = findViewById(R.id.tieCity);
        tiePincode = findViewById(R.id.tiePincode);

        tieName.addTextChangedListener(this);
        tieMobile.addTextChangedListener(this);
        tieAltMobile.addTextChangedListener(this);
        tieFlat.addTextChangedListener(this);
        tieArea.addTextChangedListener(this);
        tielandmark.addTextChangedListener(this);
        tieState.addTextChangedListener(this);
        tieCity.addTextChangedListener(this);
        tiePincode.addTextChangedListener(this);

        Button btn_login = findViewById(R.id.btn_Save);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideSoftKeyboard(AddDeliveryActivity.this);
                if (validate()) {
                    addAddress();
                }
            }
        });
    }

    private void addAddress() {
        if (isAdded) {
            AddressModal addressModal1 = AddressSingleton.getInstance().getArray().get(position);
            addressModal1.setId(id);
            addressModal1.setVendorId(VendorId);
            addressModal1.setName(tieName.getText().toString().trim());
            addressModal1.setMobileNo(tieMobile.getText().toString().trim());
            addressModal1.setAlternateMobileNo(tieAltMobile.getText().toString().trim());
            addressModal1.setFlatNoOrBuildingName(tieFlat.getText().toString().trim());
            addressModal1.setAreaOrStreet(tieArea.getText().toString().trim());
            addressModal1.setLandmark(tielandmark.getText().toString().trim());
            addressModal1.setState(tieState.getText().toString().trim());
            addressModal1.setCity(tieCity.getText().toString().trim());
            addressModal1.setPostcode(tiePincode.getText().toString().trim());
            addressModal1.setSelected(false);
            AddressSingleton.getInstance().getArray().set(position, addressModal1);

        } else {
            AddressModal addressModal = new AddressModal();
            addressModal.setId(0);
            addressModal.setVendorId(0);
            addressModal.setName(tieName.getText().toString().trim());
            addressModal.setMobileNo(tieMobile.getText().toString().trim());
            addressModal.setAlternateMobileNo(tieAltMobile.getText().toString().trim());
            addressModal.setFlatNoOrBuildingName(tieFlat.getText().toString().trim());
            addressModal.setAreaOrStreet(tieArea.getText().toString().trim());
            addressModal.setLandmark(tielandmark.getText().toString().trim());
            addressModal.setState(tieState.getText().toString().trim());
            addressModal.setCity(tieCity.getText().toString().trim());
            addressModal.setPostcode(tiePincode.getText().toString().trim());
            addressModal.setSelected(false);
            AddressSingleton.getInstance().addToArray(addressModal);
        }
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();


    }

    private boolean validate() {
        boolean isValid = true;

        String name = tieName.getText().toString().trim();
        String mobile = tieMobile.getText().toString().trim();
        String alt_mobile = tieAltMobile.getText().toString().trim();
        String flat = tieFlat.getText().toString().trim();
        String area = tieArea.getText().toString().trim();
        String state = tieState.getText().toString().trim();
        String landmark = tielandmark.getText().toString().trim();
        String city = tieCity.getText().toString().trim();
        String strPincode = tiePincode.getText().toString().trim();

        if (!Utility.validateString(name)) {
            isValid = false;
            tilName.setErrorEnabled(true);
            tilName.setError("please enter your name");
        }
        if (!Utility.validateString(flat)) {
            isValid = false;
            tilFlat.setErrorEnabled(true);
            tilFlat.setError("please enter your flat no");
        }

        if (!Utility.validateString(mobile) || mobile.length() < 10) {
            isValid = false;
            tilMobile.setErrorEnabled(true);
            tilMobile.setError("please enter valid mobile number");
        }
        if (!Utility.validateString(state)) {
            isValid = false;
            tilState.setErrorEnabled(true);
            tilState.setError("please enter your state");
        }
        if (!Utility.validateString(city)) {
            isValid = false;
            tilCity.setErrorEnabled(true);
            tilCity.setError("please enter your city");
        }
        if (!Utility.validateString(area)) {
            isValid = false;
            tilArea.setErrorEnabled(true);
            tilArea.setError("please enter your Area or Street");

        }
        if (!Utility.validateString(strPincode)) {
            isValid = false;
            tilPinCode.setErrorEnabled(true);
            tilPinCode.setError("Please select your pincode");
        }

        return isValid;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == tieName.getEditableText()) {
            tilName.setErrorEnabled(false);
            tilName.setError(null);
        }
        if (s == tieFlat.getEditableText()) {
            tilFlat.setErrorEnabled(false);
            tilFlat.setError(null);
        }
        if (s == tieMobile.getEditableText()) {
            tilMobile.setErrorEnabled(false);
            tilMobile.setError(null);
        }
        if (s == tieState.getEditableText()) {
            tilState.setErrorEnabled(false);
            tilState.setError(null);
        }
        if (s == tieCity.getEditableText()) {
            tilCity.setErrorEnabled(false);
            tilCity.setError(null);
        }
        if (s == tieArea.getEditableText()) {
            tilArea.setErrorEnabled(false);
            tilArea.setError(null);
        }
        if (s == tiePincode.getEditableText()) {
            tilPinCode.setErrorEnabled(false);
            tilPinCode.setError(null);
        }
    }
}
