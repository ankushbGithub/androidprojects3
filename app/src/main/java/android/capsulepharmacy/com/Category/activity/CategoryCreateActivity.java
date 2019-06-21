package android.capsulepharmacy.com.Category.activity;

import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Utility;
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
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CategoryCreateActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private Context mContext;
    private String TAG = CategoryCreateActivity.class.getSimpleName();
    private Bitmap thePic;
    private boolean sentToSettings = false;
    private static final int PICKFILE_RESULT_CODE = 101;
    private static final int CAMERA_CAPTURE = 1;

    private static final String CAMERA_PERMISSION = "android.permission.CAMERA";
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private SharedPreferences permissionStatus;
    private byte[] arr;


    private ImageView ivCat;
    private Button btnCatUpload, btnSubmit;
    private TextInputLayout tilCatName;
    private TextInputEditText tieCatName;
    private Toolbar toolbar;
    private TextView toolbarTtile;

    private int id;
    private String name, imageUri, imagePath;
    private boolean isEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_create_act);
        mContext = CategoryCreateActivity.this;

        Intent i = getIntent();
        if (i != null) {
            id = i.getIntExtra("id", 0);
            name = i.getStringExtra("name");
            imageUri = i.getStringExtra("imageUri");
            imagePath = i.getStringExtra("imagePath");
            isEdit = i.getBooleanExtra("isEdit", false);
        }


        initViews();
        appltInit();
    }

    private void appltInit() {
        setValue();
    }

    private void setValue() {
        try {
            if (isEdit) {
                tieCatName.setText(name);
                Picasso.get().load(imageUri.trim()).placeholder(R.drawable.cat).error(R.drawable.cat).fit().into(ivCat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilCatName = findViewById(R.id.tilCatName);
        tieCatName = findViewById(R.id.tieCatName);
        ivCat = findViewById(R.id.ivCat);
        btnCatUpload = findViewById(R.id.btnCatUpload);
        btnSubmit = findViewById(R.id.btnSubmit);
        if (isEdit){
            btnSubmit.setText("Update");
        }else {
            btnSubmit.setText("Create");
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle(getResources().getString(R.string.toolbar_title_cat_creation));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tieCatName.addTextChangedListener(this);

        btnCatUpload.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCatUpload:
                onAttachFileClicked();
                break;
            case R.id.btnSubmit:
                checkValidation();
            default:
                break;
        }
    }

    private void checkValidation() {
        boolean isValidated = false;
        String Name = tieCatName.getText().toString();

        if (!Utility.validateString(Name)) {
            isValidated = true;
            tilCatName.setErrorEnabled(true);
            tilCatName.setError("Please enter Category Name");
        }

        if (!isValidated) {
            if (NetUtil.isNetworkAvailable(mContext)) {
                submitData();
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitData() {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);


            JSONObject jsonObject = new JSONObject();
            if (isEdit) {
                jsonObject.put("Id", id);
            } else {
                jsonObject.put("Id", 0);
            }
            jsonObject.put("Name", tieCatName.getText().toString());
            if (Utility.validateString(imagePath)){
                if (imagePath.startsWith("G:\\") || imagePath.startsWith("G://") || imagePath.startsWith("F://") || imagePath.startsWith("F:\\") || imagePath.startsWith("C://") || imagePath.startsWith("C:\\")) {
                    jsonObject.put("ImagePath", "");
                } else {
                    jsonObject.put("ImagePath", imagePath);
                }
            }else {
                jsonObject.put("ImagePath", "");
            }
            if (Utility.validateString(imageUri)){
                if (imageUri.startsWith("http://") || imageUri.startsWith("https://")) {
                    jsonObject.put("ImageUri", "");
                } else {
                    jsonObject.put("ImageUri", imageUri);
                }
            }else {
                jsonObject.put("ImageUri", "");
            }

            progressDialog.show();
            Log.e(TAG, "*JsonArr**" + jsonObject.toString());
            OkHttpClient okHttpClient = APIClient.getHttpClient();
            RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, jsonObject.toString());
            Request request;
            if (isEdit) {
                String url = CapsuleAPI.WEB_SERVICE_CAT_UPDATE + id;
                request = APIClient.getPutRequest(this, url, requestBody);
            } else {
                String url = CapsuleAPI.WEB_SERVICE_CAT_LIST;
                request = APIClient.getCustomerCreatePostRequest(this, url, requestBody);
            }

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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (isEdit) {
                                            Toast.makeText(mContext, "Category updated successfully", Toast.LENGTH_LONG).show();
                                            finish();
                                        } else {
                                            Toast.makeText(mContext, "Category created successfully", Toast.LENGTH_LONG).show();
                                            finish();
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        } else if (response.code() == Constants.BAD_REQUEST) {
                            runOnUiThread(() -> {
                                Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show();
                            });
                        } else if (response.code() == Constants.INTERNAL_SERVER_ERROR) {
                            runOnUiThread(() ->
                                    Toast.makeText(mContext, getResources().getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show());
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
        if (ActivityCompat.checkSelfPermission(CategoryCreateActivity.this, CAMERA_PERMISSION.trim()) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CategoryCreateActivity.this, CAMERA_PERMISSION.trim())) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryCreateActivity.this);
                builder.setTitle("Need Camera Permission");
                builder.setMessage("This app needs camera permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(CategoryCreateActivity.this, new String[]{CAMERA_PERMISSION.trim()}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryCreateActivity.this);
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
                ActivityCompat.requestPermissions(CategoryCreateActivity.this, new String[]{CAMERA_PERMISSION}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
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

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICKFILE_RESULT_CODE);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == tieCatName.getEditableText()) {
            tilCatName.setError(null);
            tilCatName.setErrorEnabled(false);
        }
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
                    Uri uri = getImageUri(getApplicationContext(), photo);

                    Cursor returnCursor = getContentResolver().query(Objects.requireNonNull(uri), null, null, null, null);
                    int nameIndex = Objects.requireNonNull(returnCursor).getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();

                    String fileName = returnCursor.getString(nameIndex);
                    long fileSize = returnCursor.getLong(sizeIndex);
                    String mimeType = getContentResolver().getType(uri);

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
                        ivCat.setImageBitmap(photo);
                        imagePath = fileName;
                        imageUri = Base64.encodeToString(arr, Base64.NO_WRAP);
                        Log.e(TAG, "PhotoUrl string**" + Base64.encodeToString(arr, Base64.NO_WRAP));

                    } else {
                        Toast.makeText(getApplicationContext(), "Oops! File Size must be less than 2 MB", Toast.LENGTH_SHORT).show();

                    }

                } else if (requestCode == PICKFILE_RESULT_CODE) {
                    try {

                        Uri picUri = data.getData();
                    //    Bitmap photo = (Bitmap) Objects.requireNonNull(picUri);
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
                           /* try {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                Objects.requireNonNull(photo).compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                arr = baos.toByteArray();
                                Log.e("Base64Util", " Capture Bitmap Size after compress : " + arr.length);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                            ivCat.setImageBitmap(thePic);
                            imagePath = fileName;
                            imageUri = getBase64StringNew(picUri, fileSize);
                          //  imageUri = Base64.encodeToString(arr, Base64.NO_WRAP);
                           // Log.e(TAG, "PhotoUrl string**" + Base64.encodeToString(arr, Base64.NO_WRAP));

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
}
