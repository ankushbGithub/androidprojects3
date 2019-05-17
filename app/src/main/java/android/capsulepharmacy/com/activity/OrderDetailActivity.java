package android.capsulepharmacy.com.activity;

import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.modal.AttachFileModel;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.capsulepharmacy.com.PharmacyApplication.attachFileModels;


public class OrderDetailActivity extends AppCompatActivity {
    private Context mContext;
    private String orderId;
    private TextView tvName,tvMobile,textViewHeader,tvConfirmDate,tvReturnDate,tvdeliverDate,tvAddress,tvDispatchDate,tvRemarks;
    private RecyclerView recycler_viewAttachment;
    private AttachFileAdapter attachmentsListAdapter;
    private ImageView imgInvoice;
    private String imgeInvoice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        setHeader();
        mContext=OrderDetailActivity.this;
        findIds();
        Intent i=getIntent();
        if (i!=null){
            orderId=i.getStringExtra("orderId");
        }

        setAttahcments();
        if (Utility.isConnected())
            getOrders(orderId);
        else {
            Utility.showToast("Please check your internet connectivity");
        }


    }

    private void findIds() {
        imgInvoice=findViewById(R.id.imgInvoice);
        tvConfirmDate=findViewById(R.id.tvConfirmDate);
        tvdeliverDate=findViewById(R.id.tvdeliverDate);
        tvReturnDate=findViewById(R.id.tvReturnDate);
        tvAddress=findViewById(R.id.tvAddress);
        tvName=findViewById(R.id.tvName);
        tvMobile=findViewById(R.id.tvMobile);
        textViewHeader=findViewById(R.id.textViewHeader);
        tvDispatchDate=findViewById(R.id.tvDispatchDate);
        tvRemarks=findViewById(R.id.tvRemarks);

        imgInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(imgeInvoice));
                startActivity(i);
            }
        });
    }

    private void setAttahcments(){


        recycler_viewAttachment=findViewById(R.id.recycler_viewAttachment);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(OrderDetailActivity.this,LinearLayoutManager.HORIZONTAL,false);
        recycler_viewAttachment.setLayoutManager(mLayoutManager);
        attachmentsListAdapter = new AttachFileAdapter(attachFileModels);
        recycler_viewAttachment.setAdapter(attachmentsListAdapter);
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

    private void getOrders(String orderId) {
        final ProgressDialog progressDialog = new ProgressDialog(OrderDetailActivity.this);
        progressDialog.show();
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_BASE_URL+"order/"+ orderId;
        Request request = APIClient.getRequest(this, url);

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                        }
                    });
                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();

                        if (responseData != null) {

                            try {
                                final JSONObject jsonObject = new JSONObject(responseData);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        JSONObject object1=jsonObject.optJSONObject("response");
                                        if (Utility.validateString(object1.optString("confirm_date")) && !object1.optString("confirm_date").equalsIgnoreCase("1970-01-01T00:00:00.000Z"))
                                            tvConfirmDate.setText("Confirm on - "+Utility.getFormattedDates(object1.optString("confirm_date"), AppConstants.utc_format,AppConstants.format2));
                                        if (Utility.validateString(object1.optString("delivery_date"))&& !object1.optString("delivery_date").equalsIgnoreCase("1970-01-01T00:00:00.000Z"))
                                            tvdeliverDate.setText("Deliver on - "+Utility.getFormattedDates(object1.optString("delivery_date"), AppConstants.utc_format,AppConstants.format2));
                                        if (Utility.validateString(object1.optString("dispatch_date"))&& !object1.optString("dispatch_date").equalsIgnoreCase("1970-01-01T00:00:00.000Z"))
                                            tvDispatchDate.setText("Dispatch on - "+Utility.getFormattedDates(object1.optString("dispatch_date"), AppConstants.utc_format,AppConstants.format2));
                                        if (Utility.validateString(object1.optString("return_date"))&& !object1.optString("return_date").equalsIgnoreCase("1970-01-01T00:00:00.000Z"))
                                            tvReturnDate.setText("Return policy - "+Utility.getFormattedDates(object1.optString("return_date"), AppConstants.utc_format,AppConstants.format2));


                                        tvRemarks.setText("Remarks - "+object1.optString("remark"));

                                        tvName.setText("Name - "+object1.optJSONObject("address").optString("name"));
                                        tvMobile.setText("Mobile - "+object1.optJSONObject("address").optString("mobile")+", "+object1.optJSONObject("address").optString("alternate_mob"));
                                        tvAddress.setText("Delivery Address - "+object1.optJSONObject("address").optString("flatno")+", "+object1.optJSONObject("address").optString("area")+", "+object1.optJSONObject("address").optString("landmark")+", "+object1.optJSONObject("address").optString("city")+", "+object1.optJSONObject("address").optString("state")+", "+object1.optJSONObject("address").optString("country")+", "+object1.optJSONObject("address").optString("pincode"));
                                        textViewHeader.setText(object1.optString("order_id"));
                                        if (Utility.validateString(object1.optString("invoice_image"))) {
                                            imgeInvoice="https://s3.ap-south-1.amazonaws.com/image.orders/" + object1.optString("invoice_image");
                                            Picasso.get().load("https://s3.ap-south-1.amazonaws.com/image.orders/" + object1.optString("invoice_image")).fit().into(imgInvoice);
                                        }

                                        attachFileModels.clear();
                                        JSONArray array=object1.optJSONArray("images");
                                        for (int i=0;i<array.length();i++){
                                            AttachFileModel attachFileModel=new AttachFileModel();
                                          //  JSONObject object2=array.optJSONObject(i);
                                            attachFileModel.fileUrl=array.opt(i).toString();
                                          //  attachFileModel.fileName=object2.optString("");

                                            attachFileModels.add(attachFileModel);
                                        }
                                        attachmentsListAdapter.notifyDataSetChanged();

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else if (response.code() == AppConstants.BAD_REQUEST) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.INTERNAL_SERVER_ERROR) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.URL_NOT_FOUND) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_url_not_found), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.UNAUTHORIZE_ACCESS) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_unautorize_access), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.CONNECTION_OUT) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_connection_timed_out), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

    private  class AttachFileAdapter extends RecyclerView.Adapter<AttachVH> {
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
          //  final String fileName = fileModel.fileName;

            if (Utility.validateString(fileModel.fileUrl))
            Picasso.get().load("https://s3.ap-south-1.amazonaws.com/image.orders/"+fileModel.fileUrl.replaceAll("images.projectmynt.co.in","image.orders")).resize(263,263).into(holder.image);
            Picasso.get().setLoggingEnabled(true);
            // String name = fileName.substring(fileName.lastIndexOf("/"));
            //SpannableString content = new SpannableString("" + name);
            //content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            // textView.setText(content);
            //holder.textView.setText(name);
        //    holder.textView.setText(fileName);

            holder.btnClear.setVisibility(View.GONE);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://s3.ap-south-1.amazonaws.com/image.orders/"+fileModel.fileUrl.replaceAll("images.projectmynt.co.in","image.orders")));
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return spendRequestAttachment.size();
        }
    }
}
