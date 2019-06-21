package android.capsulepharmacy.com.utility;

import android.app.Dialog;
import android.capsulepharmacy.com.R;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class DeleteGalleryDialog extends Dialog {
    public Context c;
    private TextView tvDialogMessage, tvNo, tvYes;
    private int id;
    public GalleryDelete galleryDelete;
    public interface GalleryDelete {
        void onGalleryDelete(int id);
    }

    String message;

    public DeleteGalleryDialog(Context a, GalleryDelete galleryDelete, int id, String message) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.galleryDelete = galleryDelete;
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
                galleryDelete.onGalleryDelete(id);
            }
        });

    }

}


