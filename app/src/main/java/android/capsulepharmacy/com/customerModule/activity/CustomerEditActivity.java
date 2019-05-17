package android.capsulepharmacy.com.customerModule.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.DateAndTimeUtil;
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
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.capsulepharmacy.com.utility.DateAndTimeUtil.DATE_AND_TIME_FORMAT_INDIA1;

public class CustomerEditActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {
    private Context mContext;
    private static final String TAG = CustomerEditActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TextInputLayout tilFirstName, tilLastName, tilDOB, tilEmail, tilPassword, tilMobileNumber, tilGstn;
    private TextInputEditText tieFirstName, tieLastName, tieDOB, tieEmail, tiePassword, tieMobileNumber, tieGstn;
    private boolean clicked = false;
    private Calendar calendar;
    private Button btnSubmit, btnUserPic;
    private static final int PICKFILE_RESULT_CODE = 101;
    private static final int CAMERA_CAPTURE = 1;
    private static final String CAMERA_PERMISSION = "android.permission.CAMERA";
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private String photoPath = "";
    private String photoUrl = "";
    private Bitmap thePic;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private int GALLERY = 1, CAMERA = 2;
    private CircleImageView img_ProfilePic;
    private String response;
    private int id;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cutomer_update);
        mContext = CustomerEditActivity.this;
        calendar = Calendar.getInstance();

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
        toolbar.setTitle("Customer Update");

        setData(response);
    }

    private void setData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            id = jsonObject.optInt("Id");
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

        tilFirstName = findViewById(R.id.tilFirstName);
        tilLastName = findViewById(R.id.tilLastName);
        tilDOB = findViewById(R.id.tilDOB);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilMobileNumber = findViewById(R.id.tilMobileNumber);
        tilGstn = findViewById(R.id.tilGstn);

        tieFirstName = findViewById(R.id.tieFirstName);
        tieLastName = findViewById(R.id.tieLastName);
        tieDOB = findViewById(R.id.tieDOB);
        tieEmail = findViewById(R.id.tieEmail);
        tiePassword = findViewById(R.id.tiePassword);
        tieMobileNumber = findViewById(R.id.tieMobileNumber);
        tieGstn = findViewById(R.id.tieGstin);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnUserPic = findViewById(R.id.btnUserPic);
        img_ProfilePic = findViewById(R.id.img_ProfilePic);

        tieDOB.setClickable(true);
        tieDOB.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnUserPic.setOnClickListener(this);

        tieFirstName.addTextChangedListener(this);
        tieLastName.addTextChangedListener(this);
        tieEmail.addTextChangedListener(this);
        tiePassword.addTextChangedListener(this);
        tieMobileNumber.addTextChangedListener(this);
        tieGstn.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == tieFirstName.getEditableText()) {
            tilFirstName.setErrorEnabled(false);
            tilFirstName.setError(null);
        }
        if (s == tieLastName.getEditableText()) {
            tilLastName.setErrorEnabled(false);
            tilLastName.setError(null);
        }
        if (s == tieEmail.getEditableText()) {
            tilEmail.setErrorEnabled(false);
            tilEmail.setError(null);
        }
        if (s == tiePassword.getEditableText()) {
            tilPassword.setErrorEnabled(false);
            tilPassword.setError(null);
        }
        if (s == tieMobileNumber.getEditableText()) {
            tilMobileNumber.setErrorEnabled(false);
            tilMobileNumber.setError(null);
        }
        if (s == tieGstn.getEditableText()) {
            tilGstn.setErrorEnabled(false);
            tilGstn.setError(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tieDOB:
                clicked = true;
                clickAddDate();
                break;
            case R.id.btnSubmit:
                checkValidation();
                break;
            case R.id.btnUserPic:
                onAttachFileClicked();

            default:
                break;
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

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
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
        if (ActivityCompat.checkSelfPermission(CustomerEditActivity.this, CAMERA_PERMISSION.trim()) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CustomerEditActivity.this, CAMERA_PERMISSION.trim())) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerEditActivity.this);
                builder.setTitle("Need Camera Permission");
                builder.setMessage("This app needs camera permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(CustomerEditActivity.this, new String[]{CAMERA_PERMISSION.trim()}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerEditActivity.this);
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
                ActivityCompat.requestPermissions(CustomerEditActivity.this, new String[]{CAMERA_PERMISSION}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(CAMERA_PERMISSION, true);
            editor.apply();


        } else {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);

        }
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

    private void checkValidation() {
        String fName = tieFirstName.getText().toString();
        String lName = tieLastName.getText().toString();
        String dob = tieLastName.getText().toString();
        String email = tieEmail.getText().toString();
        String passord = tiePassword.getText().toString();
        String mNumber = tieMobileNumber.getText().toString();
        String gstin = tieGstn.getText().toString();

        boolean isValidated = false;
        if (!Utility.validateString(fName)) {
            isValidated = true;
            tilFirstName.setErrorEnabled(true);
            tilFirstName.setError(getResources().getString(R.string.invalid_f_name));
        }
        if (!Utility.validateString(lName)) {
            isValidated = true;
            tilLastName.setErrorEnabled(true);
            tilLastName.setError(getResources().getString(R.string.invalid_l_name));
        }
        if (!Utility.validateString(dob)) {
            isValidated = true;
            tilDOB.setErrorEnabled(true);
            tilDOB.setError(getResources().getString(R.string.dateerror));
        }
        if (!Utility.validateString(email)) {
            isValidated = true;
            tilEmail.setErrorEnabled(true);
            tilEmail.setError(getResources().getString(R.string.invalid_email));
        }
        if (Utility.validateString(email)) {
            if (!emailValidator(email)) {
                isValidated = true;
                tilEmail.setErrorEnabled(true);
                tilEmail.setError(getResources().getString(R.string.invalid_email));
            }
        }
        if (!Utility.validateString(passord)) {
            isValidated = true;
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("Please enter Password");
        }
        if (passord.length() < 8) {
            isValidated = true;
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("Password must be of 8 characters");
        }
        if (TextUtils.isEmpty(mNumber)) {
            isValidated = true;
            tilMobileNumber.setErrorEnabled(true);
            tilMobileNumber.setError(getResources().getString(R.string.invalid_phone));
        }
        if (mNumber.length() < 10 || mNumber.length() > 10) {
            isValidated = true;
            tilMobileNumber.setErrorEnabled(true);
            tilMobileNumber.setError("Mobile number must be of 10 characters");
        }
        if (!TextUtils.isEmpty(gstin)) {
            if (gstin.length() < 15 || gstin.length() > 15) {
                isValidated = true;
                tilGstn.setErrorEnabled(true);
                tilGstn.setError("Please enter a valid GSTN number");
            }
        }

        if (!isValidated) {
            if (NetUtil.isNetworkAvailable(mContext)) {
                sendCustomerDetails(id);

            } else {
                Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendCustomerDetails(int id) {

        String dob = Utility.getFormattedDates(tieDOB.getText().toString(), Constants.DATE_FORMAT, Constants.format21);
        try {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", id);
            jsonObject.put("FirstName", tieFirstName.getText().toString());
            jsonObject.put("LastName", tieLastName.getText().toString());
            jsonObject.put("DOB", dob);
            jsonObject.put("Email", tieEmail.getText().toString());
            jsonObject.put("GSTIN", tieGstn.getText().toString());
            jsonObject.put("MobileNo", tieMobileNumber.getText().toString());
            jsonObject.put("PhotoPath", photoPath);
            jsonObject.put("PhotoUrl", photoUrl);
            jsonObject.put("ImagePath", "");
            jsonObject.put("Password", tiePassword.getText().toString());
            Log.e(TAG, "Customer Creation json***" + jsonObject.toString());

            OkHttpClient okHttpClient = APIClient.getHttpClient();
            RequestBody requestBody = RequestBody.create(CapsuleAPI.JSON, jsonObject.toString());
            String url = CapsuleAPI.WEB_SERVICE_CUSTOMER_EDIT + id;

            final Request request = APIClient.getPutRequest(this, url, requestBody);
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
                    runOnUiThread(progressDialog::dismiss);
                    try {
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(TAG, "USER update Response***" + Objects.requireNonNull(response.body()).toString());

                                }
                            });
                            final String responseData = Objects.requireNonNull(response.body()).string();
                            if (responseData != null) {
                                runOnUiThread(() ->
                                        setSuccessDescription(responseData));
                            }
                        } else if (response.code() == Constants.BAD_REQUEST) {
                            runOnUiThread(() -> {
                                Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show();
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setSuccessDescription(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            int code = jsonObject.optInt("code");
            if (code == 200) {
                Toast.makeText(mContext, jsonObject.optString("message"), Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(mContext, jsonObject.optString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onActivityResultAttachment(requestCode, resultCode, data);
    }

    public void onActivityResultAttachment(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == this.RESULT_CANCELED) {
                return;
            }
            if (requestCode == GALLERY) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    thePic = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
                    Cursor returnCursor = getContentResolver().query(Objects.requireNonNull(contentURI), null, null, null, null);
                    int nameIndex = Objects.requireNonNull(returnCursor).getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();

                    String fileName = returnCursor.getString(nameIndex);
                    long fileSize = returnCursor.getLong(sizeIndex);
                    String mimeType = getContentResolver().getType(contentURI);
                    Log.i("Type", mimeType);
                    Log.i("fileSize", fileSize + "");
                    long twoMb = 1024 * 1024 * 2;

                    if (fileSize <= twoMb) {
                        photoPath = fileName;
                        photoUrl = getBase64StringNew(contentURI, fileSize);
                        img_ProfilePic.setImageBitmap(thePic);
                    } else {
                        Toast.makeText(getApplicationContext(), "Oops! File Size must be less than 2 MB", Toast.LENGTH_SHORT).show();
                    }
                }

            } else if (requestCode == CAMERA) {
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
                    photoPath = fileName;
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Objects.requireNonNull(photo).compress(Bitmap.CompressFormat.JPEG, 50, baos);
                        byte[] arr = baos.toByteArray();
                        Log.e("Base64Util", " Capture Bitmap Size after compress : " + arr.length);
                        photoUrl = Base64.encodeToString(arr, Base64.NO_WRAP);
                        img_ProfilePic.setImageBitmap(photo);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//
//                    // CALL THIS METHOD TO GET THE ACTUAL PATH
//                    File finalFile = new File(getRealPathFromURI(imageUri));
//                    if (finalFile.exists()) {
//                        finalFile.delete();
//                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Oops! File Size must be less than 2 MB", Toast.LENGTH_SHORT).show();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            imageStr = android.util.Base64.encodeToString(byteFileArray, android.util.Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageStr;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void clickAddDate() {
        if (clicked) {
            calendar = Calendar.getInstance();
            android.app.DatePickerDialog datePicker = new android.app.DatePickerDialog(mContext, new android.app.DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    tieDOB.setText(DateAndTimeUtil.toCustomStringDateAndTime(calendar, DATE_AND_TIME_FORMAT_INDIA1));
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePicker.show();
            datePicker.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccentDark));
            datePicker.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccentDark));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            clicked = false;
        }
    }
}

