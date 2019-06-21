package android.capsulepharmacy.com.vendor.activity;

import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.DeleteDialog;
import android.capsulepharmacy.com.utility.DeleteGalleryDialog;
import android.capsulepharmacy.com.utility.DisplayUtils;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.adapter.EqualSpacingItemDecoration;
import android.capsulepharmacy.com.vendor.adapter.VendorGalleryAdapter;
import android.capsulepharmacy.com.vendor.modal.GalleryModal;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VendorGalleryActivity extends AppCompatActivity implements VendorGalleryAdapter.ThreeDots, VendorGalleryAdapter.DeleteImage, DeleteGalleryDialog.GalleryDelete {
    private static final String TAG = VendorGalleryActivity.class.getSimpleName();
    Context mContext;
    private Toolbar toolbar;
    private Button btnUpload;
    private boolean sentToSettings = false;
    private static final int PICKFILE_RESULT_CODE = 101;
    private static final int CAMERA_CAPTURE = 1;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private static final String CAMERA_PERMISSION = "android.permission.CAMERA";
    private SharedPreferences permissionStatus;
    private byte[] arr;
    private Bitmap thePic;
    private SharedPreferences.Editor editor;
    private static final String mypreference = "Data";
    private SharedPreferences sharedpreferences;
    private int VendorId;
    private RecyclerView rvImages;
    private ArrayList<GalleryModal> galleryModals = new ArrayList<>();
    private VendorGalleryAdapter vendorGalleryAdapter;
    private SwipeRefreshLayout mySwipeRefresh;
    private DeleteGalleryDialog deleteGalleryDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_gallery_act);
        mContext = VendorGalleryActivity.this;
        sharedpreferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        Intent i = getIntent();
        VendorId = i.getIntExtra("VendorId", 0);

        init();
        applyInit();
    }


    private void applyInit() {
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

        mySwipeRefresh.setOnRefreshListener(
                () -> {
                    if (NetUtil.isNetworkAvailable(mContext)) {
                        getImagesList();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        if (NetUtil.isNetworkAvailable(mContext)) {
            getImagesList();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void disableSwipeToRefresh() {
        if (mySwipeRefresh.isRefreshing()) {
            mySwipeRefresh.setRefreshing(false);
        }
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnUpload = findViewById(R.id.btnUpload);
        rvImages = findViewById(R.id.rvImages);
        mySwipeRefresh = findViewById(R.id.mySwipeRefresh);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryModals.size() == 3) {
                    Toast.makeText(mContext, "Not allow to add Image more then 3", Toast.LENGTH_SHORT).show();
                } else {
                    onAttachFileClicked();
                }
            }
        });
    }

    private void onAttachFileClicked() {
        showPictureDialog();
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Choose from gallery", "Capture from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICKFILE_RESULT_CODE);
    }

    public void onActivityResultAttachment(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == this.RESULT_CANCELED) {
                return;
            }
            if (resultCode == RESULT_OK) {
                //user is returning from capturing an image using the camera
                int PIC_CROP = 3;
                if (requestCode == CAMERA_CAPTURE) {
                    Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    Uri imageUri = getImageUri(getApplicationContext(), photo);

                    Cursor returnCursor = getContentResolver().query(Objects.requireNonNull(imageUri), null, null, null, null);
                    int nameIndex = Objects.requireNonNull(returnCursor).getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();

                    String fileName = returnCursor.getString(nameIndex);
                    long fileSize = returnCursor.getLong(sizeIndex);
                    String mimeType = getContentResolver().getType(imageUri);

                    Log.i("Type", mimeType);
                    Log.i("fileSize", fileSize + "");
                    long twoMb = 1024 * 1024 * 2;

                    if (fileSize <= twoMb) {
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            Objects.requireNonNull(photo).compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            arr = baos.toByteArray();
                            Log.e("Base64Util", " Capture Bitmap Size after compress : " + arr.length);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "ImageUrl string**" + Base64.encodeToString(arr, Base64.NO_WRAP));
                        editor.putString("ImageUrl", Base64.encodeToString(arr, Base64.NO_WRAP));
                        editor.putString("ImagePath", fileName);
                        editor.apply();

                        uploadImageServer();

                    } else {
                        Toast.makeText(getApplicationContext(), "Oops! File Size must be less than 2 MB", Toast.LENGTH_SHORT).show();

                    }

                } else if (requestCode == PICKFILE_RESULT_CODE) {
                    try {
                        Uri picUri = data.getData();
                        Cursor returnCursor =
                                getContentResolver().query(Objects.requireNonNull(picUri), null, null, null, null);
                        int nameIndex = Objects.requireNonNull(returnCursor).getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();

                        String fileName = returnCursor.getString(nameIndex);
                        long fileSize = returnCursor.getLong(sizeIndex);
                        String mimeType = getContentResolver().getType(picUri);
                        Log.i("Type", mimeType);
                        Log.i("fileSize", fileSize + "");
                        long twoMb = 1024 * 1024 * 2;
                        if (fileSize <= twoMb) {

                            thePic = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                            Log.e(TAG, "ImageUrl string**" + getBase64StringNew(picUri, fileSize));
                            editor.putString("ImageUrl", getBase64StringNew(picUri, fileSize));
                            editor.putString("ImagePath", fileName);
                            editor.apply();

                            uploadImageServer();
                        } else {
                            Toast.makeText(getApplicationContext(), "Oops! File Size must be less than 2 MB", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImageServer() {
        String ImageUrl = sharedpreferences.getString("ImageUrl", "");
        String ImagePath = sharedpreferences.getString("ImagePath", "");
        try {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", 0);
            jsonObject.put("VendorId", VendorId);
            jsonObject.put("ImageUrl", ImageUrl);
            jsonObject.put("ImagePath", ImagePath);
            jsonArray.put(jsonObject);

            Log.e(TAG, "*jsonArray Obj**" + jsonArray.toString());
            OkHttpClient okHttpClient = APIClient.getHttpClient();
            RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, jsonArray.toString());
            Request request;

            String url = CapsuleAPI.WEB_SERVICE_VENDOR_GALLERY_CREATE;
            request = APIClient.getGalleryPostRequest(this, url, requestBody);

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
                                }
                            });
                            final String responseData = Objects.requireNonNull(response.body()).string();
                            if (responseData != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (NetUtil.isNetworkAvailable(mContext)) {
                                                getImagesList();
                                            } else {
                                                Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
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
                                            Utility.messageDialog(VendorGalleryActivity.this, "Bad Request Error", jsonObject1.optString("error_description"));
                                            //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (jsonObject1.has("message")) {
                                                Utility.messageDialog(VendorGalleryActivity.this, "Bad Request Error", jsonObject1.optString("message"));
                                            } else {
                                                Utility.messageDialog(VendorGalleryActivity.this, "Bad Request Error", jsonObject1.optString("Message"));
                                            }

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
                                            Utility.messageDialog(VendorGalleryActivity.this, "Internal Server Error", jsonObject1.optString("error_description"));
                                            //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (jsonObject1.has("message")) {
                                                Utility.messageDialog(VendorGalleryActivity.this, "Internal Server Error", jsonObject1.optString("message"));
                                            } else {
                                                Utility.messageDialog(VendorGalleryActivity.this, "Internal Server Error", jsonObject1.optString("Message"));

                                            }

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

    private void getImagesList() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_VENDOR_GET + VendorId;
        Log.e(TAG, "Get Request**" + url);

        final Request request = APIClient.getRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    disableSwipeToRefresh();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    disableSwipeToRefresh();

                });
                try {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Log.e(TAG, "Category List Response***" + responseData);
                        if (responseData != null) {
                            runOnUiThread(() ->
                                    setServerData(responseData));
                        }

                    } else if (response.code() == Constants.BAD_REQUEST) {
                        runOnUiThread(() -> {
                            String errorBodyString = null;
                            try {
                                errorBodyString = response.body().string();

                                try {
                                    JSONObject jsonObject1 = new JSONObject(errorBodyString);
                                    if (jsonObject1.has("error_description")) {
                                        Utility.messageDialog(VendorGalleryActivity.this, "Bad Request Error", jsonObject1.optString("error_description"));
                                        //  Toast.makeText(mContext, jsonObject1.optString("Message"), Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (jsonObject1.has("message")) {
                                            Utility.messageDialog(VendorGalleryActivity.this, "Bad Request Error", jsonObject1.optString("message"));
                                        } else {
                                            Utility.messageDialog(VendorGalleryActivity.this, "Bad Request Error", jsonObject1.optString("Message"));
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (response.code() == Constants.INTERNAL_SERVER_ERROR) {
                        runOnUiThread(() -> Toast.makeText(mContext, getResources().getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show());
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

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getBase64StringNew(Uri uri, long filelength) {
        String imageStr = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

           /* InputStream finput = new FileInputStream(file);
            byte[] imageBytes = new byte[(int)file.length()];
            finput.read(imageBytes, 0, imageBytes.length);
            finput.close();
            String imageStr = Base64.encodeBase64String(imageBytes);*/

            //InputStream finput = new FileInputStream(file);
            byte[] byteFileArray = new byte[(int) filelength];
            inputStream.read(byteFileArray, 0, byteFileArray.length);
            inputStream.close();
            imageStr = Base64.encodeToString(byteFileArray, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageStr;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onActivityResultAttachment(requestCode, resultCode, data);
    }

    private void takePhotoFromCamera() {
        try {
            checkPermission();
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(VendorGalleryActivity.this, CAMERA_PERMISSION.trim()) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(VendorGalleryActivity.this, CAMERA_PERMISSION.trim())) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(VendorGalleryActivity.this);
                builder.setTitle("Need Camera Permission");
                builder.setMessage("This app needs camera permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(VendorGalleryActivity.this, new String[]{CAMERA_PERMISSION.trim()}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(CAMERA_PERMISSION.trim(), false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(VendorGalleryActivity.this);
                builder.setTitle("Need Camera Permission");
                builder.setMessage("This app needs camera permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant camera permission", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(VendorGalleryActivity.this, new String[]{CAMERA_PERMISSION}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(CAMERA_PERMISSION, true);
            editor.apply();


        } else {
            //You already have the permission, just go ahead.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, CAMERA_CAPTURE);

        }
    }

    private void setServerData(String responseData) {
        galleryModals.clear();
        try {
            JSONArray cjsonArray = new JSONArray(responseData);
            for (int i = 0; i < cjsonArray.length(); i++) {
                JSONObject jsonObject1 = cjsonArray.optJSONObject(i);
                GalleryModal galleryModal = new GalleryModal();
                galleryModal.Id = jsonObject1.optInt("Id");
                galleryModal.VendorId = jsonObject1.optInt("VendorId");
                galleryModal.ImageUrl = jsonObject1.optString("ImageUrl");
                galleryModal.ImagePath = jsonObject1.optString("ImagePath");
                galleryModals.add(galleryModal);
            }

            rvImages.setLayoutManager(new GridLayoutManager(mContext, 2));
            vendorGalleryAdapter = new VendorGalleryAdapter(mContext, galleryModals, this,this);
            rvImages.setAdapter(vendorGalleryAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onThreeDotCLicked(View v, int position) {
        GalleryModal galleryModal = galleryModals.get(position);
        showPopUpMenu(v, galleryModal.Id);
    }

    private void showPopUpMenu(View view, int id) {
        PopupMenu popup = new PopupMenu(this, view);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
            popup.getMenuInflater().inflate(R.menu.gallery_three_dots_options, popup.getMenu());
            popup.getMenu().getItem(0).setIcon(R.drawable.icon_delete);
            popup.getMenu().getItem(0).setTitle("Delete");

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem menu_item) {
                    switch (menu_item.getItemId()) {
                        case R.id.nav_deleted:
                            onGalleryDeleteClicked(id);
                            break;

                    }
                    return true;
                }
            });
            popup.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onGalleryDeleteClicked(int id) {

    }

    @Override
    public void onDeleteImage(View v, int position) {
        GalleryModal galleryModal = galleryModals.get(position);
        showDeleteConfirmation(galleryModal.Id);
    }

    private void showDeleteConfirmation(int id) {
        deleteGalleryDialog = new DeleteGalleryDialog(VendorGalleryActivity.this, this, id, "Are you sure you want to delete ?");
        deleteGalleryDialog.setCancelable(false);
        deleteGalleryDialog.setCanceledOnTouchOutside(false);
        deleteGalleryDialog.show();

    }

    @Override
    public void onGalleryDelete(int id) {
        deleteGallery(id);
    }

    private void deleteGallery(int id) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_VENDOR_GET + id;
        Log.e(TAG, "Get Request**" + url);

        final Request request = APIClient.deleteRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }

            @Override
            public void onResponse(Call call, final Response response) {
                runOnUiThread(() ->
                        progressDialog.dismiss());
                try {
                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();
                        Log.e(TAG, "Response***" + responseData);
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               getImagesList();
                           }
                       });

                    } else if (response.code() == Constants.BAD_REQUEST) {
                        runOnUiThread(() -> Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show());
                    } else if (response.code() == Constants.INTERNAL_SERVER_ERROR) {
                        runOnUiThread(() -> Toast.makeText(mContext, getResources().getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show());
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
    }
}
