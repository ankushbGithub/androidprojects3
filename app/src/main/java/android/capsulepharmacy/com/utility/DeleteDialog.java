package android.capsulepharmacy.com.utility;

import android.app.Dialog;
import android.capsulepharmacy.com.R;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


public class DeleteDialog extends Dialog {
    public Context c;
    private TextView tvDialogMessage, tvNo, tvYes;
    private String firstName;
    private int id;

    public interface InDelete {
        void onInDelete(String firstName, int id);
    }

    public InDelete inDelete;
    String message;

    public DeleteDialog(Context a, InDelete inDelete, String firstName, int id, String message) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.inDelete = inDelete;
        this.firstName = firstName;
        this.id = id;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_custom_dialog);

        tvDialogMessage = findViewById(R.id.tvDialogMessage);
        tvNo = findViewById(R.id.tvNo);
        tvYes = findViewById(R.id.tvYes);

        tvDialogMessage.setText(message);

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                inDelete.onInDelete(firstName, id);
            }
        });

    }

}

