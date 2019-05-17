package android.capsulepharmacy.com.customerModule.activity;

import android.app.Activity;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerViewActivity extends AppCompatActivity {
    private Context mContext;
    private static final String TAG = CustomerViewActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TextInputLayout tilFirstName, tilLastName, tilDOB, tilEmail, tilPassword, tilMobileNumber, tilGstn;
    private TextInputEditText tieFirstName, tieLastName, tieDOB, tieEmail, tiePassword, tieMobileNumber, tieGstn;

    private String photoPath = "";
    private String photoUrl = "";
    private Bitmap thePic;
    private CircleImageView img_ProfilePic;
    private String response;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_view_act);
        mContext = CustomerViewActivity.this;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Intent i = getIntent();
        if (i != null) {
            response = i.getStringExtra("response");
        }
        initView();
        applyInit();
    }

    private void applyInit() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle("Customer View");

        setData(response);
    }

    private void setData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            tieFirstName.setText(jsonObject.optString("FirstName"));
            tieLastName.setText(jsonObject.optString("LastName"));
            if (Utility.isValidDate(jsonObject.optString("DOB"))) {
                String bDate = Utility.getFormattedDates(jsonObject.optString("DOB"), Constants.format6, Constants.format2);
                tieDOB.setText(bDate);
            } else {
                String bDate = Utility.getFormattedDates(jsonObject.optString("DOB"), Constants.formatDate, Constants.format2);
                tieDOB.setText(bDate);
            }
            tieEmail.setText(jsonObject.optString("Email"));
            if (Utility.validateString(jsonObject.optString("Password"))) {
                tiePassword.setText(jsonObject.optString("Password"));
            } else {
                tiePassword.setText("");
            }
            tieMobileNumber.setText(jsonObject.optString("MobileNo"));
            tieGstn.setText(jsonObject.optString("GSTIN"));
            String PhotoUrl = jsonObject.optString("PhotoUrl");
            Picasso.get().load(PhotoUrl.trim()).placeholder(R.drawable.user).error(R.drawable.user).fit().into(img_ProfilePic);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);

        tieFirstName = findViewById(R.id.tieFirstName);
        tieLastName = findViewById(R.id.tieLastName);
        tieDOB = findViewById(R.id.tieDOB);
        tieEmail = findViewById(R.id.tieEmail);
        tiePassword = findViewById(R.id.tiePassword);
        tieMobileNumber = findViewById(R.id.tieMobileNumber);
        tieGstn = findViewById(R.id.tieGstin);

        img_ProfilePic = findViewById(R.id.img_ProfilePic);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Utility.hideSoftKeyboard((Activity) mContext);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

