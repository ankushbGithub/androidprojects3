package android.capsulepharmacy.com.vendor.activity;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.modal.BankDetails;
import android.capsulepharmacy.com.vendor.singleton.BankDetailsSingleton;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

public class BankDetailsActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private Context mContext;
    private static final String TAG = BankDetailsActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TextView toolbarTtile;

    private TextInputLayout tilAccHolder, tilAccNumber, tilBranchName, tilIFSCCode;
    private TextInputEditText tieAccHolder, tieAccNumber, tieBranchName, tieIFSCCode;
    private AppCompatButton btn_Save;

    private int id,VendorId, position;
    private String holderName;
    private String accountNumber;
    private String branchName;
    private String ifscCode;
    private boolean isAdded;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bank_details);
        mContext = BankDetailsActivity.this;
        getIntentValue();
        findViewId();
        applyInit();
    }

    private void getIntentValue() {
        Intent i = getIntent();
        id = i.getIntExtra("id", 0);
        VendorId = i.getIntExtra("VendorId", 0);
        position = i.getIntExtra("position", 0);
        holderName = i.getStringExtra("holderName");
        accountNumber = i.getStringExtra("accountNumber");
        branchName = i.getStringExtra("branchName");
        ifscCode = i.getStringExtra("ifscCode");
        isAdded = i.getBooleanExtra("isAdded", false);
    }

    private void setIntentValue() {
        tieAccHolder.setText(holderName);
        tieAccNumber.setText(accountNumber);
        tieBranchName.setText(branchName);
        tieIFSCCode.setText(ifscCode);
    }

    private void findViewId() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTtile = findViewById(R.id.toolbarTtile);

        tilAccHolder = findViewById(R.id.tilAccHolder);
        tilAccNumber = findViewById(R.id.tilAccNumber);
        tilBranchName = findViewById(R.id.tilBranchName);
        tilIFSCCode = findViewById(R.id.tilIFSCCode);

        tieAccHolder = findViewById(R.id.tieAccHolder);
        tieAccNumber = findViewById(R.id.tieAccNumber);
        tieBranchName = findViewById(R.id.tieBranchName);
        tieIFSCCode = findViewById(R.id.tieIFSCCode);

        tieAccHolder.addTextChangedListener(this);
        tieAccNumber.addTextChangedListener(this);
        tieBranchName.addTextChangedListener(this);
        tieIFSCCode.addTextChangedListener(this);

        btn_Save = findViewById(R.id.btn_Save);
        btn_Save.setOnClickListener(this);
    }


    private void applyInit() {
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
        setIntentValue();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Save:
                Utility.hideSoftKeyboard(BankDetailsActivity.this);
                if (validate()) {
                    addBankDetails();
                }
            default:
                break;
        }
    }

    private void addBankDetails() {
        if (isAdded) {
            BankDetails bankDetails = BankDetailsSingleton.getInstance().getArray().get(position);
            bankDetails.setId(id);
            bankDetails.setVendorId(VendorId);
            bankDetails.setAccountHolderName(tieAccHolder.getText().toString());
            bankDetails.setAccountNumber(tieAccNumber.getText().toString());
            bankDetails.setBranchName(tieBranchName.getText().toString());
            bankDetails.setIFSCCode(tieIFSCCode.getText().toString());
            bankDetails.setSelected(false);
            BankDetailsSingleton.getInstance().getArray().set(position, bankDetails);

        } else {
            BankDetails bankDetails = new BankDetails();
            bankDetails.setId(0);
            bankDetails.setVendorId(0);
            bankDetails.setAccountHolderName(tieAccHolder.getText().toString());
            bankDetails.setAccountNumber(tieAccNumber.getText().toString());
            bankDetails.setBranchName(tieBranchName.getText().toString());
            bankDetails.setIFSCCode(tieIFSCCode.getText().toString());
            bankDetails.setSelected(false);
            BankDetailsSingleton.getInstance().addToArray(bankDetails);
        }


        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    private boolean validate() {
        boolean isValid = true;

        String accountHolder = tieAccHolder.getText().toString().trim();
        String acctNumber = tieAccNumber.getText().toString().trim();
        String branchName = tieBranchName.getText().toString().trim();
        String ifscCode = tieIFSCCode.getText().toString().trim();

        if (!Utility.validateString(accountHolder)) {
            isValid = false;
            tilAccHolder.setErrorEnabled(true);
            tilAccHolder.setError("Please enter Account holder name");
        }
        if (!Utility.validateString(acctNumber)) {
            isValid = false;
            tilAccNumber.setErrorEnabled(true);
            tilAccNumber.setError("Please enter Account number");
        }
        if (!Utility.validateString(branchName)) {
            isValid = false;
            tilBranchName.setErrorEnabled(true);
            tilBranchName.setError("Please enter Branch name");
        }
        if (!Utility.validateString(ifscCode)) {
            isValid = false;
            tilIFSCCode.setErrorEnabled(true);
            tilIFSCCode.setError("Please enter IFSC code");
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
        if (s == tieAccHolder.getEditableText()) {
            tilAccHolder.setErrorEnabled(false);
            tilAccHolder.setError(null);
        }
        if (s == tieAccNumber.getEditableText()) {
            tilAccNumber.setErrorEnabled(false);
            tilAccNumber.setError(null);
        }
        if (s == tieBranchName.getEditableText()) {
            tilBranchName.setErrorEnabled(false);
            tilBranchName.setError(null);
        }
        if (s == tieIFSCCode.getEditableText()) {
            tilIFSCCode.setErrorEnabled(false);
            tilIFSCCode.setError(null);
        }
    }
}
