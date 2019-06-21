package android.capsulepharmacy.com.utility;

import android.app.Dialog;
import android.capsulepharmacy.com.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class VerifyEmailOtp extends Dialog implements View.OnClickListener {
    public Context mContext;
    ImageView ivClose;
    TextView tvEmail;
    EditText etField;
    public AppCompatButton btnVerify;

    String email;

    private String TAG = VerifyEmailOtp.class.getSimpleName();
    public VerifyOtp verifyOtp;

    public interface VerifyOtp {
        void onVerifyOtp(String email, String otp);
    }
    String otp;
    public VerifyEmailOtp(Context mContext, String email, VerifyOtp verifyOtp) {
        super(mContext);
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        this.email = email;
        this.verifyOtp = verifyOtp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.email_otp_custom_dialog);

        ivClose = findViewById(R.id.ImvClose);
        tvEmail = findViewById(R.id.tvEmail);
        etField = findViewById(R.id.etField);

        btnVerify = findViewById(R.id.btnVerify);

        ivClose.setOnClickListener(this);
        btnVerify.setOnClickListener(this);

        tvEmail.setText(email);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ImvClose:
                dismiss();
                break;
            case R.id.btnVerify:
                if (Utility.validateString(etField.getText().toString())) {
                    verify();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.enter_otp_error), Toast.LENGTH_LONG).show();
                }
            default:
                break;
        }
    }

    private void verify() {
        verifyOtp.onVerifyOtp(email, etField.getText().toString());
    }

}


