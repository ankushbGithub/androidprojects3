package android.capsulepharmacy.com.vendor.activity;

import android.app.Activity;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.modal.SelectedSubCatList;
import android.capsulepharmacy.com.vendor.modal.ServiceFilterModal;
import android.capsulepharmacy.com.vendor.modal.SubCatFilterModal;
import android.capsulepharmacy.com.vendor.modal.SubCatListModel;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class VendorCreationActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    public static ArrayList<ServiceFilterModal> serviceFilterModals = new ArrayList<>();
    public static ArrayList<SubCatFilterModal> subCatFilterModals = new ArrayList<>();
    public static ArrayList<SelectedSubCatList> selectedSubCatLists = new ArrayList<>();
    public static ArrayList<SubCatListModel> subCatListModels = new ArrayList<>();

    public static String vName, vFirmName, vGstin, vMobile, PhotoUrl, PanUrl, AadhaarUrl, PhotoPath, AadhaarPath, PanPath, Password, Email, VendorAddressDetails, VendorBankDetails;
    public static int intentCategoryId, ServiceLocationId, SubCategoryId, MaxMember, MinMember, Price;
    public static int Id;

    public static String PDescription;
    public static int MaxPrice;
    public static int MinPrice;

    private Context mContext;
    private static final String TAG = VendorCreationActivity.class.getSimpleName();
    private Bitmap thePic;

    private TextInputLayout tilName, tilFirmName, tilGSTIN, tilMobNum, tilEmail, tilPassword;
    private TextInputEditText tieName, tieFirmName, tieGSTIN, tieMobNum, tieEmail, tiePassword;
    private boolean sentToSettings = false;
    private static final int PICKFILE_RESULT_CODE = 101;
    private static final int CAMERA_CAPTURE = 1;

    private ImageView ivAadhar, ivPan;
    private static final String CAMERA_PERMISSION = "android.permission.CAMERA";
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private Button btnAdharUpload, btnPanUpload;
    private AppCompatButton btnProceed;
    private SharedPreferences permissionStatus;
    private boolean isAdhar = false;
    private boolean isPan = false;
    private boolean isUser = false;
    private Toolbar toolbar;
    private TextView toolbarTtile;
    private Button btnUserPic;
    private CircleImageView img_ProfilePic;

    private SharedPreferences.Editor editor;
    private static final String mypreference = "Data";
    private String response;
    private byte[] arr;
    private boolean isUpdate, isVendorDetails;
    public static Activity activity;
    private boolean isAadharUpload, isPanUpload;
    private SharedPreferences sharedpreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_creation);
        mContext = VendorCreationActivity.this;
        activity = VendorCreationActivity.this;

        Intent i = getIntent();
        response = i.getStringExtra("response");
        isUpdate = i.getBooleanExtra("isUpdate", false);
        isVendorDetails = i.getBooleanExtra("isVendorDetails", false);

        sharedpreferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        findViewById();
        applyInit();
    }

    private void applyInit() {
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                if (VendorDetailsActivity.activityState) {
                    VendorDetailsActivity.activity.finish();
                    finish();
                } else {
                    finish();
                }

            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        setValue();
    }

    private void setValue() {
        try {
            if (isUpdate) {
                if (Utility.validateString(response)) {
                    String PhotoUrl = sharedpreferences.getString("PhotoUrl", "");
                    String PanUrl = sharedpreferences.getString("PanUrl", "");
                    String AadhaarUrl = sharedpreferences.getString("AadhaarUrl", "");
                    String Name = sharedpreferences.getString("Name", "");
                    String FirmName = sharedpreferences.getString("FirmName", "");
                    String GSTIN = sharedpreferences.getString("GSTIN", "");
                    String MobileNo = sharedpreferences.getString("MobileNo", "");
                    String Email = sharedpreferences.getString("Email", "");


                    JSONObject jObj = new JSONObject(response);
                    if (Utility.validateString(Name)) {
                        tieName.setText(Name);
                    } else {
                        tieName.setText(jObj.optString("Name"));
                    }

                    if (Utility.validateString(FirmName)) {
                        tieFirmName.setText(FirmName);
                    } else {
                        tieFirmName.setText(jObj.optString("FirmName"));

                    }

                    if (Utility.validateString(GSTIN)) {
                        tieGSTIN.setText(GSTIN);
                    } else {
                        tieGSTIN.setText(jObj.optString("GSTIN"));
                    }

                    if (Utility.validateString(MobileNo)) {
                        tieMobNum.setText(MobileNo);
                    } else {
                        tieMobNum.setText(jObj.optString("MobileNo"));
                    }

                    if (Utility.validateString(Email)) {
                        tieEmail.setText(Email);
                    } else {
                        tieEmail.setText(jObj.optString("Email"));
                    }
                    tilPassword.setVisibility(View.GONE);
//                tiePassword.setText(jObj.optString("Password"));


                    if (Utility.validateString(PhotoUrl)) {
                        if (PhotoUrl.startsWith("http://") || PhotoUrl.startsWith("https://")) {
                            Picasso.get().load(PhotoUrl.trim()).placeholder(R.drawable.user).error(R.drawable.user).fit().into(img_ProfilePic);
                        } else {
                            img_ProfilePic.setImageBitmap(decodeBase64(PhotoUrl.trim()));

                        }

                    } else {
                        Picasso.get().load(jObj.optString("PhotoUrl").trim()).placeholder(R.drawable.user).error(R.drawable.user).fit().into(img_ProfilePic);
                        editor.putString("PhotoUrl", jObj.optString("PhotoUrl").trim());
                        editor.putString("PhotoPath", jObj.optString("PhotoPath").trim());

                    }

                    if (Utility.validateString(PanUrl)) {
                        if (PanUrl.startsWith("http://") || PanUrl.startsWith("https://")) {
                            Picasso.get().load(PanUrl.trim()).placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).fit().into(ivPan);
                        } else {
                            ivPan.setImageBitmap(decodeBase64(PanUrl.trim()));
                        }

                    } else {
                        Picasso.get().load(jObj.optString("PanUrl").trim()).placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).fit().into(ivPan);
                        editor.putString("PanUrl", jObj.optString("PanUrl").trim());
                        editor.putString("PanPath", jObj.optString("PanPath").trim());

                    }

                    if (Utility.validateString(AadhaarUrl)) {
                        if (AadhaarUrl.startsWith("http://") || AadhaarUrl.startsWith("https://")) {
                            Picasso.get().load(AadhaarUrl.trim()).placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).fit().into(ivAadhar);
                        } else {
                            ivAadhar.setImageBitmap(decodeBase64(AadhaarUrl.trim()));
                        }

                    } else {
                        Picasso.get().load(jObj.optString("AadhaarUrl").trim()).placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).fit().into(ivAadhar);
                        editor.putString("AadhaarUrl", jObj.optString("AadhaarUrl").trim());
                        editor.putString("AadhaarPath", jObj.optString("AadhaarPath").trim());

                    }

                    editor.putInt("Id", jObj.optInt("Id"));
                    editor.putInt("MaxMember", jObj.optInt("MaxMember"));
                    editor.putInt("MinMember", jObj.optInt("MinMember"));
                    editor.putInt("Price", jObj.optInt("Price"));
                    editor.putString("PDescription", jObj.optString("PDescription"));
                    editor.putInt("MaxPrice", jObj.optInt("MaxPrice"));
                    editor.putInt("MinPrice", jObj.optInt("MinPrice"));
                    editor.putInt("CategoryId", jObj.optInt("CategoryId"));
                    editor.putInt("ServiceLocationId", jObj.optInt("ServiceLocationId"));
                    editor.putInt("SubCategoryId", jObj.optInt("SubCategoryId"));

                    editor.putString("Password", jObj.optString("Password").trim());
                    editor.putString("Email", jObj.optString("Email").trim());
                    editor.putString("VendorAddressDetails", jObj.optString("VendorAddressDetails").trim());
                    editor.putString("VendorBankDetails", jObj.optString("VendorBankDetails").trim());
                    editor.apply();
                }
            } else {
                String PhotoUrl = sharedpreferences.getString("PhotoUrl", "");
                String PanUrl = sharedpreferences.getString("PanUrl", "");
                String AadhaarUrl = sharedpreferences.getString("AadhaarUrl", "");
                String Name = sharedpreferences.getString("Name", "");
                String FirmName = sharedpreferences.getString("FirmName", "");
                String GSTIN = sharedpreferences.getString("GSTIN", "");
                String MobileNo = sharedpreferences.getString("MobileNo", "");
                String Email = sharedpreferences.getString("Email", "");
                String Password = sharedpreferences.getString("Password", "");

                tieName.setText(Name);
                tieFirmName.setText(FirmName);
                tieGSTIN.setText(GSTIN);
                tieMobNum.setText(MobileNo);
                tieEmail.setText(Email);
                tiePassword.setText(Password);

                if (Utility.validateString(PhotoUrl)) {
                    img_ProfilePic.setImageBitmap(decodeBase64(PhotoUrl.trim()));
                }

                if (Utility.validateString(PanUrl)) {
                    ivPan.setImageBitmap(decodeBase64(PanUrl.trim()));
                }

                if (Utility.validateString(AadhaarUrl)) {
                    ivAadhar.setImageBitmap(decodeBase64(AadhaarUrl.trim()));
                }

            }

        } catch (Exception e) {
            Log.e(TAG, "Exception***" + e);
            e.printStackTrace();
        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void findViewById() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTtile = findViewById(R.id.toolbarTtile);

        tilName = findViewById(R.id.tilName);
        tilFirmName = findViewById(R.id.tilFirmName);
        tilGSTIN = findViewById(R.id.tilGSTIN);
        tilMobNum = findViewById(R.id.tilMobNum);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);

        tieName = findViewById(R.id.tieName);
        tieFirmName = findViewById(R.id.tieFirmName);
        tieGSTIN = findViewById(R.id.tieGSTIN);
        tieMobNum = findViewById(R.id.tieMobNum);
        tieEmail = findViewById(R.id.tieEmail);
        tiePassword = findViewById(R.id.tiePassword);

        ivAadhar = findViewById(R.id.ivAadhar);
        ivPan = findViewById(R.id.ivPan);
        img_ProfilePic = findViewById(R.id.img_ProfilePic);

        btnAdharUpload = findViewById(R.id.btnAdharUpload);
        btnPanUpload = findViewById(R.id.btnPanUpload);
        btnProceed = findViewById(R.id.btnProceed);
        btnUserPic = findViewById(R.id.btnUserPic);

        btnAdharUpload.setOnClickListener(this);
        btnPanUpload.setOnClickListener(this);
        btnProceed.setOnClickListener(this);
        btnUserPic.setOnClickListener(this);

        ivAadhar.setOnClickListener(this);
        ivPan.setOnClickListener(this);
        img_ProfilePic.setOnClickListener(this);

        tieName.addTextChangedListener(this);
        tieFirmName.addTextChangedListener(this);
        tieGSTIN.addTextChangedListener(this);
        tieMobNum.addTextChangedListener(this);
        tieEmail.addTextChangedListener(this);
        tiePassword.addTextChangedListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdharUpload:
                isAdhar = true;
                isPan = false;
                isUser = false;
                onAttachFileClicked();
                break;
            case R.id.btnUserPic:
                isUser = true;
                isAdhar = false;
                isPan = false;
                onAttachFileClicked();
                break;
            case R.id.btnPanUpload:
                isAdhar = false;
                isPan = true;
                isUser = false;
                onAttachFileClicked();
                break;
            case R.id.ivAadhar:

                break;
            case R.id.ivPan:

                break;
            case R.id.img_ProfilePic:

                break;
            case R.id.btnProceed:
                checkValidation();

            default:
                break;
        }
    }

    private static List<String> isValid(String passwordhere) {
        List<String> errorList = new ArrayList<>();

        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        if (TextUtils.isEmpty(passwordhere)) {
            errorList.add("Please enter password");
        }
        if (passwordhere.length() < 8) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!specailCharPatten.matcher(passwordhere).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!UpperCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!lowerCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }
        if (!digitCasePatten.matcher(passwordhere).find()) {
            errorList.add("Password length must be of atleast 8 character,1 special character ,1 Uppercase,1 lowercase and 1 number  !!");
        }

        return errorList;

    }

    private void checkValidation() {
        try {
            boolean isValidated = false;

            String Name = tieName.getText().toString();
            String firmName = tieFirmName.getText().toString();
            String GSTIN = tieGSTIN.getText().toString();
            String mobileNum = tieMobNum.getText().toString();
            String email = tieEmail.getText().toString();
            String password = tiePassword.getText().toString();
            String photoUrl = sharedpreferences.getString("PhotoUrl", "");
            String PanUrl = sharedpreferences.getString("PanUrl", "");
            String AadhaarUrl = sharedpreferences.getString("AadhaarUrl", "");
            List<String> errorList = isValid(password);

            if (!Utility.validateString(Name)) {
                isValidated = true;
                tilName.setErrorEnabled(true);
                tilName.setError("Please enter Name");
            }
            if (!Utility.validateString(firmName)) {
                isValidated = true;
                tilFirmName.setErrorEnabled(true);
                tilFirmName.setError("Please enter Firm Name");
            }
//        if (!Utility.validateString(GSTIN) || GSTIN.length() < 15) {
//            isValidated = true;
////            Utility.messageDialog(VendorCreationActivity.this, "Required Conditions for a Valid GST Number", "- GST should be of 15 characters.\n" +
////                    "- first 2 characters should match with State GST code.\n" +
////                    "- 3rd to 7th character must be letters.\n" +
////                    "- 8th to 11 character must be numbers.\n" +
////                    "- 12th character must be a letter.\n" +
////                    "- 13th to 15th can be letter or number.\n");
//            tilGSTIN.setErrorEnabled(true);
//            tilGSTIN.setError("Please enter 15 digit GSTIN number");
//        }

            if (!Utility.validateString(mobileNum)) {
                isValidated = true;
                tilMobNum.setErrorEnabled(true);
                tilMobNum.setError("Please enter valid Mobile number");
            }
            if (mobileNum.length() < 10 || mobileNum.length() > 10) {
                isValidated = true;
                tilMobNum.setErrorEnabled(true);
                tilMobNum.setError("Mobile number must be of 10 characters");
            }
            if (!Utility.validateString(email) || !emailValidator(email)) {
                isValidated = true;
                tilEmail.setErrorEnabled(true);
                tilEmail.setError(getResources().getString(R.string.invalid_email));
            }
            if (!Utility.validateString(AadhaarUrl) && !isAadharUpload) {
                isValidated = true;
                Utility.showToast("Please upload Aadhaar card");
            }
            if (!Utility.validateString(PanUrl) && !isPanUpload) {
                isValidated = true;
                Utility.showToast("Please upload Pan card");
            }
            if (!isUpdate) {
                if (!Utility.validateString(password)) {
                    isValidated = true;
                    tilPassword.setErrorEnabled(true);
                    tilPassword.setError("Please enter Password");
                }
                if (password.length() < 8) {
                    isValidated = true;
                    tilPassword.setErrorEnabled(true);
                    tilPassword.setError("Password must be of 8 characters");
                }
                if (!errorList.isEmpty()) {
                    for (String error : errorList) {
                        isValidated = true;
                        tilPassword.setErrorEnabled(true);
                        tilPassword.setError(error);
                    }
                }

            }


            if (!isValidated) {
                if (isVendorDetails) {
                    Intent i = new Intent();
                    setResult(RESULT_OK, i);
                    finish();
                } else {
                    Intent i = new Intent(mContext, VendorDetailsActivity.class);
                    i.putExtra("isUpdate", isUpdate);
                    i.putExtra("response", response);
                    startActivity(i);
                    finish();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
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


//                    //get the returned data
//                    Bundle extras = data.getExtras();
//                    //get the cropped bitmap
//                    assert extras != null;
//                    thePic = (Bitmap) extras.get("data");
//                    //display the returned cropped image
                        if (isAdhar) {
                            isAadharUpload = true;
                            ivAadhar.setImageBitmap(photo);
                            Log.e(TAG, "AadhaarUrl string**" + Base64.encodeToString(arr, Base64.NO_WRAP));
                            editor.putString("AadhaarUrl", Base64.encodeToString(arr, Base64.NO_WRAP));
                            editor.putString("AadhaarPath", fileName);
                            editor.apply();

                        }
                        if (isPan) {
                            isPanUpload = true;
                            ivPan.setImageBitmap(photo);
                            Log.e(TAG, "PanUrl string**" + Base64.encodeToString(arr, Base64.NO_WRAP));
                            editor.putString("PanUrl", Base64.encodeToString(arr, Base64.NO_WRAP));
                            editor.putString("PanPath", fileName);
                            editor.apply();

                        }
                        if (isUser) {
                            img_ProfilePic.setImageBitmap(photo);
                            Log.e(TAG, "PhotoUrl string**" + Base64.encodeToString(arr, Base64.NO_WRAP));
                            editor.putString("PhotoUrl", Base64.encodeToString(arr, Base64.NO_WRAP));
                            editor.putString("PhotoPath", fileName);
                            editor.apply();

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Oops! File Size must be less than 2 MB", Toast.LENGTH_SHORT).show();

                    }


                    isAdhar = false;
                    isPan = false;
                    isUser = false;

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
                            //display the returned cropped image
                            if (isAdhar) {
                                isAadharUpload = true;
                                ivAadhar.setImageBitmap(thePic);
                                Log.e(TAG, "AadhaarUrl string**" + getBase64StringNew(picUri, fileSize));
                                editor.putString("AadhaarUrl", getBase64StringNew(picUri, fileSize));
                                editor.putString("AadhaarPath", fileName);
                                editor.apply();

                            }
                            if (isPan) {
                                isPanUpload = true;
                                ivPan.setImageBitmap(thePic);
                                Log.e(TAG, "PanUrl string**" + getBase64StringNew(picUri, fileSize));
                                editor.putString("PanUrl", getBase64StringNew(picUri, fileSize));
                                editor.putString("PanPath", fileName);
                                editor.apply();
                            }
                            if (isUser) {
                                img_ProfilePic.setImageBitmap(thePic);
                                Log.e(TAG, "PhotoUrl string**" + getBase64StringNew(picUri, fileSize));
                                editor.putString("PhotoUrl", getBase64StringNew(picUri, fileSize));
                                editor.putString("PhotoPath", fileName);
                                editor.apply();

                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Oops! File Size must be less than 2 MB", Toast.LENGTH_SHORT).show();
                        }

                        isAdhar = false;
                        isPan = false;
                        isUser = false;
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
            imageStr = android.util.Base64.encodeToString(byteFileArray, android.util.Base64.NO_WRAP);
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
        if (ActivityCompat.checkSelfPermission(VendorCreationActivity.this, CAMERA_PERMISSION.trim()) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(VendorCreationActivity.this, CAMERA_PERMISSION.trim())) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(VendorCreationActivity.this);
                builder.setTitle("Need Camera Permission");
                builder.setMessage("This app needs camera permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(VendorCreationActivity.this, new String[]{CAMERA_PERMISSION.trim()}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(VendorCreationActivity.this);
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
                ActivityCompat.requestPermissions(VendorCreationActivity.this, new String[]{CAMERA_PERMISSION}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == tieName.getEditableText()) {
            tilName.setError(null);
            tilName.setErrorEnabled(false);
            editor.putString("Name", Objects.requireNonNull(tieName.getText()).toString());
            editor.apply();
        }
        if (editable == tieFirmName.getEditableText()) {
            tilFirmName.setError(null);
            tilFirmName.setErrorEnabled(false);
            editor.putString("FirmName", Objects.requireNonNull(tieFirmName.getText()).toString());
            editor.apply();
        }
        if (editable == tieGSTIN.getEditableText()) {
            tilGSTIN.setError(null);
            tilGSTIN.setErrorEnabled(false);
            editor.putString("GSTIN", Objects.requireNonNull(tieGSTIN.getText()).toString());
            editor.apply();
        }
        if (editable == tieMobNum.getEditableText()) {
            tilMobNum.setError(null);
            tilMobNum.setErrorEnabled(false);
            editor.putString("MobileNo", Objects.requireNonNull(tieMobNum.getText()).toString());
            editor.apply();
        }
        if (editable == tieEmail.getEditableText()) {
            tilEmail.setError(null);
            tilEmail.setErrorEnabled(false);
            editor.putString("Email", Objects.requireNonNull(tieEmail.getText()).toString());
            editor.apply();
        }
        if (editable == tiePassword.getEditableText()) {
            tilPassword.setError(null);
            tilPassword.setErrorEnabled(false);
            editor.putString("Password", Objects.requireNonNull(tiePassword.getText()).toString());
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        if (VendorDetailsActivity.activityState) {
            VendorDetailsActivity.activity.finish();
            finish();
        } else {
            finish();
        }
    }
}
