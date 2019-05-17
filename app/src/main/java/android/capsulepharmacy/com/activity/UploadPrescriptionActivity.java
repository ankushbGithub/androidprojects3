package android.capsulepharmacy.com.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.adapter.InstructionsAdapter;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.listener.AttachmentListener;
import android.capsulepharmacy.com.modal.AttachFileModel;
import android.capsulepharmacy.com.modal.InstructionModal;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.activity.VendorDetailsActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.capsulepharmacy.com.PharmacyApplication.attachFileModels;


/**
 * The type Edit expandable po details activity.
 */
public class UploadPrescriptionActivity extends BaseActivity implements AttachmentListener {
    String[] numbers={"Don't crop out any part of the image","Avoid blurred image.","Include details of doctor and patient + clinic visit date.","Medicines will be dispensed as per prescription."};


    ArrayList<InstructionModal> instructionModals = new ArrayList<>();
    private RecyclerView recycler_viewAttachment,recyclerViewItems;
    AttachFileAdapter attachmentsPOListAdapter;
    Context context;
    private RelativeLayout llGallery,llCamera;
    private InstructionsAdapter itemListDataAdapter;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final String TEMP_PHOTO_FILE_NAME = "capsulepharmacy.jpg";
    private String sendimagefile = "";
    private Uri imageUri;
    private File mFileTemp;
    private TextView tvAddAttachClick;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_prescription);
        context=UploadPrescriptionActivity.this;
        setHeader();
        attachFileModels.clear();
        inializeViews();

        setInstructions();
        getData();
        getPicture();

    }

    private void getPicture() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
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

    private void inializeViews(){


        tvAddAttachClick=findViewById(R.id.tvAddAttachClick);
        tvAddAttachClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attachFileModels.size()<5)
                openNeedHelpBottomSheet();
                else {
                    Utility.showToast("max 4 attachments allowed");
                }
            }
        });
        llCamera=findViewById(R.id.llCamera);
        llGallery = findViewById(R.id.llGallery);

        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        llGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   onAttachFileClicked();
            }
        });
        Button btnProceed=findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attachFileModels.size()>0) {
                    Intent i = new Intent(UploadPrescriptionActivity.this, VendorDetailsActivity.class);
                    startActivityForResult(i,900);
                }else {
                    Utility.showToast("Please add atleast one prescription");
                }
            }
        });

    }
    private void getData(){
        for (int i=0;i<numbers.length;i++){
            InstructionModal questionNumberModal=new InstructionModal();
            questionNumberModal.setTitle(numbers[i]);

            instructionModals.add(questionNumberModal);
        }
        itemListDataAdapter.notifyDataSetChanged();
    }





    @Override
    protected void onResume() {
        super.onResume();

    }

    private String createJson() {
        JSONObject jsonObject = new JSONObject();
        JSONArray poDetails = new JSONArray();

        try {




            JSONArray jsonArrayAttachments = new JSONArray();
          /*  for (int j = 0; j < poAttachments.size(); j++) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("grnAttachmentName", grnAttachments.get(j).getGrnAttachmentName());
                jsonObject1.put("grnAttachmentUrl", grnAttachments.get(j).getGrnAttachmentUrl());
                jsonObject1.put("grnAttachmentType", grnAttachments.get(j).getGrnAttachmentType());
                jsonArrayAttachments.put(jsonObject1);
            }
*/
            //attach new

            for (int i = 0; i < attachFileModels.size(); i++) {
                AttachFileModel fileModel = attachFileModels.get(i);
                Uri returnUri = fileModel.uri;
                Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                String fileName = returnCursor.getString(nameIndex);
                String fileSize = Long.toString(returnCursor.getLong(sizeIndex));
                String mimeType = getContentResolver().getType(returnUri);
                Log.i("Type", mimeType);


                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("pOAttachmentName", fileName);
                jsonObject1.put("pOAttachmentUrl", getBase64StringNew(returnUri, Integer.parseInt(fileSize)));
                jsonObject1.put("pOAttachmentType",  mimeType);
                jsonArrayAttachments.put(jsonObject1);


            }
            //attach end











            jsonObject.put("poAttachments", jsonArrayAttachments);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();

        //  new RealmController().saveGRNDetails(jsonObject.toString());

    }

    private void setAttahcments(){


        recycler_viewAttachment=findViewById(R.id.recycler_viewAttachment);
        recycler_viewAttachment.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(UploadPrescriptionActivity.this,LinearLayoutManager.HORIZONTAL,false);
        recycler_viewAttachment.setLayoutManager(mLayoutManager);
        attachmentsPOListAdapter = new AttachFileAdapter(attachFileModels);
        recycler_viewAttachment.setAdapter(attachmentsPOListAdapter);
    }
    private void setInstructions(){


        recyclerViewItems=findViewById(R.id.recycler_viewAttachment);
        recyclerViewItems.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(UploadPrescriptionActivity.this);
        recyclerViewItems.setLayoutManager(mLayoutManager);
        itemListDataAdapter = new InstructionsAdapter(mContext,instructionModals);
        recyclerViewItems.setAdapter(itemListDataAdapter);
    }








   /* *//**
     * Gets po details.
     *//*
    public void getPODetails() {
        final ProgressDialog progressDialog=new ProgressDialog(UploadPrescriptionActivity.this);
        JSONObject jsonObject1=new JSONObject();

        try {
            jsonObject1.put("empCode",Prefs.getStringPrefs(Constants.employeeCode));
            jsonObject1.put("businessPlaceId",businessPlaceId);
            jsonObject1.put("poNumber","na");
            jsonObject1.put("isGRN",false);
            jsonObject1.put("isGRNOrQC","na");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialog.show();
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        RequestBody requestBody = RequestBody.create(IPOSAPI.JSON, jsonObject1.toString());
        String url = IPOSAPI.WEB_SERVICE_GetPODETAILS;

        final Request request = APIClient.getPostRequest(this, url, requestBody);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                progressDialog.dismiss();
                //  dismissProgress();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // dismissProgress();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
                try {
                    if (response != null && response.isSuccessful()) {

                        final String responseData = response.body().string();
                        if (responseData != null) {
                            JSONObject jsonObject=new JSONObject(responseData);

                            // saveResponseLocalCreateOrder(jsonObject,requestId);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getExpandableData(responseData);
                                }
                            });


                        }


                    } else if (response.code() == Constants.BAD_REQUEST) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (response.code() == Constants.INTERNAL_SERVER_ERROR) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, getResources().getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (response.code() == Constants.URL_NOT_FOUND) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, getResources().getString(R.string.error_url_not_found), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (response.code() == Constants.UNAUTHORIZE_ACCESS) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, getResources().getString(R.string.error_unautorize_access), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (response.code() == Constants.CONNECTION_OUT) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, getResources().getString(R.string.error_connection_timed_out), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();



                }
            }
        });
    }*/


    public void openNeedHelpBottomSheet() {

        View view = getLayoutInflater().inflate(R.layout.dialog_pick_image, null);

        LinearLayout lin_done = (LinearLayout) view.findViewById(R.id.lin_done);
        LinearLayout order_status_rl = (LinearLayout) view.findViewById(R.id.gallery);
        LinearLayout need_issue = (LinearLayout) view.findViewById(R.id.camera);


        final Dialog mBottomSheetDialog = new Dialog(UploadPrescriptionActivity.this,
                R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        order_status_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAttachFileClicked();
                mBottomSheetDialog.dismiss();
            }
        });
        need_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takePicture();
                mBottomSheetDialog.dismiss();
            }
        });


        lin_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
         /*   Bitmap bitmap = null;

            Bitmap bitmapMask = BitmapFactory.decodeResource(getApplication()
                    .getResources(), R.drawable.user);
            Bitmap selectedBitmap = null;
            try {
                ParcelFileDescriptor parcelFileDescriptor =
                        getContentResolver().openFileDescriptor(Crop.getOutput(result), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                selectedBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);


                parcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // selectedBitmap = BitmapFactory.decodeFile(ConstantFunction
            // .getDefaultImagePath());
            if (selectedBitmap != null) {
                bitmap = Bitmap.createScaledBitmap(selectedBitmap, 500,
                        500, false);
                sendimagefile = Utility.BitMapToString(bitmap);

                // Set The Bitmap Data To ImageView
               // iv_imageViewProfile.setImageBitmap(Utility.masking(bitmap, bitmapMask));

                //  iv_imageViewProfile.setImageURI(Crop.getOutput(result));
            }*/
            setResultPicture();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void setResultPicture(){
        Uri uri = imageUri;
     /*   @SuppressLint("Recycle") Cursor returnCursor =
                getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();*/

        String fileName = mFileTemp.getName();
        long fileSize =mFileTemp.length();
        ContentResolver cR = mContext.getContentResolver();

        String mimeType = cR.getType(uri);
       // Log.i("Type", mimeType);
      //  Log.i("fileSize", fileSize+"");
        long twoMb = 1024 * 1024 * 2;

        if (!Utility.validateString(mimeType)){
            mimeType="image/jpg";
        }
     //   if(fileSize <= twoMb) {
            AttachFileModel fileModel = new AttachFileModel();
            fileModel.file=new File(uri.getPath());
            fileModel.fileName = fileName;
            fileModel.mimeType = mimeType;
            fileModel.uri = uri;

            attachFileModels.add(fileModel);
            updateSize();
          //  String FilePath = data.getData().getPath();
        /*}else {
            Toast.makeText(getApplicationContext(), "Oops! File Size must be less than 2 MB", Toast.LENGTH_SHORT).show();
        }*/

    }
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "fname_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }






    private static final int PICKFILE_RESULT_CODE = 101;

    @Override
    public void onAttachmentClicked(int position) {

    }

    /**
     * On attach file clicked.
     */
    public void onAttachFileClicked() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "fname_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PICKFILE_RESULT_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==600){

        }else if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
            //handleCrop(resultCode, data);
          //  beginCrop(imageUri);
            setResultPicture();
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }else if(requestCode==900){
            finish();
        } else
            onActivityResultAttachment(requestCode,resultCode,data);


    }
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    /**
     * On activity result attachment.
     *
     * @param requestCode the request code
     * @param resultCode  the result code
     * @param data        the data
     */
    public void onActivityResultAttachment(int requestCode, int resultCode, Intent data) {

        try {
            switch (requestCode) {
                case PICKFILE_RESULT_CODE:
                    if (resultCode == RESULT_OK) {
                        Uri uri = data.getData();
                        assert uri != null;
                        String[] projection = { MediaStore.Images.Media.DATA };
                     //   Cursor cursor = managedQuery(uri, projection, null, null, null);
                      //  int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                        @SuppressLint("Recycle") Cursor returnCursor =
                                getContentResolver().query(uri, null, null, null, null);
                        assert returnCursor != null;
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();


                        String fileName = returnCursor.getString(nameIndex);
                        long fileSize = returnCursor.getLong(sizeIndex);
                        String mimeType = getContentResolver().getType(uri);
                        Log.i("Type", mimeType);
                        Log.i("fileSize", fileSize+"");
                        long twoMb = 1024 * 1024 * 2;
                    //    getPath(data.getData());

                      //  if(fileSize <= twoMb) {
                            AttachFileModel fileModel = new AttachFileModel();
                            fileModel.file=new File(getPath(data.getData()));
                            fileModel.fileName = fileName;
                            fileModel.mimeType = mimeType;
                            fileModel.uri = uri;

                            attachFileModels.add(fileModel);
                            updateSize();
                            String FilePath = data.getData().getPath();
                      /*  }else {
                            Toast.makeText(getApplicationContext(), "Oops! File Size must be less than 2 MB", Toast.LENGTH_SHORT).show();
                        }*/

                    }
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSize() {
        int attachFileSize = attachFileModels.size();
        int attachVoiceSize = 0;
        int totalSize = attachFileSize;
        // textViewAttachmentSize.setText("(" + totalSize + ")");
        //    Toast.makeText(getActivity(), "attachFileModels" + attachFileModels.size(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < attachFileModels.size(); i++) {
            Log.v("attachFileModels", "attachFileModels" + attachFileModels.get(i));
        }
        if (attachFileSize > 0) {
            setAttahcments();

        }
    }




    private class AttachVH extends RecyclerView.ViewHolder {
        /**
         * The Text view.
         */
        public TextView textView;
        /**
         * The Btn clear.
         */
        public View btnClear;
        private ImageView image;

        /**
         * Instantiates a new Attach vh.
         *
         * @param itemView the item view
         */
        public AttachVH(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            btnClear = itemView.findViewById(R.id.btnClear);
            btnClear.setVisibility(View.VISIBLE);
            image=itemView.findViewById(R.id.image);
        }
    }

    private class AttachFileAdapter extends RecyclerView.Adapter<AttachVH> {
        private List<AttachFileModel> spendRequestAttachment;

        /**
         * Instantiates a new Attach file adapter.
         *
         * @param spendRequestAttachment the spend request attachment
         */
        public AttachFileAdapter(List<AttachFileModel> spendRequestAttachment) {
            this.spendRequestAttachment = spendRequestAttachment;
        }

        @NonNull
        @Override
        public AttachVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_attachfile_item, parent, false);
            return new AttachVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AttachVH holder, final int position) {
            final AttachFileModel fileModel = spendRequestAttachment.get(position);
            final String fileName = fileModel.fileName;
            holder.image.setImageURI(fileModel.uri);

            // String name = fileName.substring(fileName.lastIndexOf("/"));
            //SpannableString content = new SpannableString("" + name);
            //content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            // textView.setText(content);
            //holder.textView.setText(name);
            holder.textView.setText(fileName);
            Log.v("path", "---------------------" + fileName);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Uri path = Uri.parse(attachment);
                    String type = attachment;

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(attachment));*/
                    // intent.setDataAndType(spendRequestAttachment.get(position),"*/*");
                    //   intent.setDataAndType(path, type);
                    //intent.addFlag(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    //Toast.makeText(getActivity(), "In Progress!", Toast.LENGTH_SHORT).show();
                 /*   startActivity(intent);*/
                    final Intent shareIntent = new Intent(Intent.ACTION_VIEW);
                    //   shareIntent.setType("*/*");
                    //  shareIntent.setDataAndType(Uri.parse(fileModel.uri.toString()), "image/*");
                    shareIntent.setDataAndType(Uri.parse(fileModel.uri.toString()),fileModel.mimeType);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //final File photoFile = new File(getFilesDir(), "foo.jpg");

                    startActivity(Intent.createChooser(shareIntent, "View file using"));
                }
            });
            holder.btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spendRequestAttachment.remove(fileModel);
                    notifyDataSetChanged();

                    int attachFileSize = attachFileModels.size();
                    // textViewAttachmentSize.setText("(" + attachFileSize + ")");
                }
            });
        }

        @Override
        public int getItemCount() {
            return spendRequestAttachment.size();
        }
    }
    private String getBase64StringNew(Uri uri, int filelength) {
        String imageStr = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

           /* InputStream finput = new FileInputStream(file);
            byte[] imageBytes = new byte[(int)file.length()];
            finput.read(imageBytes, 0, imageBytes.length);
            finput.close();
            String imageStr = Base64.encodeBase64String(imageBytes);*/

            //InputStream finput = new FileInputStream(file);
            byte[] byteFileArray = new byte[filelength];
            inputStream.read(byteFileArray, 0, byteFileArray.length);
            inputStream.close();
            imageStr = android.util.Base64.encodeToString(byteFileArray, android.util.Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageStr;
    }

    private void confirmationDialog(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(UploadPrescriptionActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(UploadPrescriptionActivity.this);
        }
        builder.setTitle("Alert")
                .setMessage("Are you sure you want to exit the screen?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onBackPressed() {
        //    super.onBackPressed();
        finish();
    }
}
