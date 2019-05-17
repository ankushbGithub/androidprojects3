package android.capsulepharmacy.com.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.capsulepharmacy.com.utility.Utility.getStringRes;


/**
 * Created by gaurav.pandey on 02-01-2018.
 */

public class BaseActivity extends AppCompatActivity {
    //Save the path as a string value

    protected Context mContext;
    private ProgressDialog mProgressDialog;
    public boolean isReplaced = false;
    public int CAMERA_REQUEST = 2121;
    public int GALLERY_REQUEST = 2221;
    protected Activity mActivity;
    protected Fragment fragmentCurrent;

    private String mImageUrl;
    private boolean isUpdateImage = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mActivity = this;

    }


    public int setLayoutId() {
        return R.layout.activity_main;
    }

    public void addFragment(Fragment fragment, int containerId) {
        fragmentCurrent = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerId, fragment)
                .commitAllowingStateLoss();
    }

    public void replaceFragment(Fragment fragment, int containerId) {
        isReplaced = true;
        fragmentCurrent = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void showProgress() {
        showProgress(getStringRes(R.string.msg_load_default));
    }

    public void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(msg);
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(msg);
            mProgressDialog.show();
        }
    }

    public void showProgress(int msgId) {
        String message = getStringRes(msgId);
        showProgress(message);
    }

    public void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }




    protected boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }




}
