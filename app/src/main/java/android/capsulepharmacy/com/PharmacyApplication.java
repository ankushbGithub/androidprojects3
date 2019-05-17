package android.capsulepharmacy.com;

import android.app.Application;
import android.capsulepharmacy.com.modal.AttachFileModel;
import android.content.Context;
import android.os.StrictMode;

import java.util.ArrayList;


public class PharmacyApplication extends Application {
    private static Context mContext;
    public static ArrayList<AttachFileModel> attachFileModels = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());



    }
    public static Context getContext() {
        return mContext;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

}


