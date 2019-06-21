package android.capsulepharmacy.com.activity;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Prefs;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MyAccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        setHeader();
        init();

    }

    private void init() {
        EditText name=findViewById(R.id.input_name);
        EditText email=findViewById(R.id.input_email);
        EditText mobile=findViewById(R.id.input_mobile);
        EditText input_points=findViewById(R.id.input_points);

        name.setText(Prefs.getStringPrefs(AppConstants.NAME));
        email.setText(Prefs.getStringPrefs(AppConstants.USER_NAME));
        mobile.setText(Prefs.getStringPrefs(AppConstants.USER_ROLE));
      //  input_points.setText(Prefs.getStringPrefs(AppConstants.USER_POINTS));
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

}
