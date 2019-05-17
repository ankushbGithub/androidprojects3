package android.capsulepharmacy.com.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.activity.LoginActivity;
import android.capsulepharmacy.com.activity.UploadPrescriptionActivity;
import android.capsulepharmacy.com.adapter.HomeAdapter;
import android.capsulepharmacy.com.modal.HomeModal;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.AutoscrollViewPager;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AdminHomeFragment extends Fragment {
    private String[] titles = {"My Orders", "Offers", "Terms & Conditions", "About Us"};
    private int[] images = {R.drawable.my_order,  R.drawable.offer, R.drawable.terms_app, R.drawable.about_app};
    private ArrayList<HomeModal> homeModals = new ArrayList<>();
    private ArrayList<String> imagesDeals = new ArrayList<>();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private AutoscrollViewPager mViewPager;
    private ImagesAdapter adapter;
    private RecyclerView recyclerviewCategory;
    private HomeAdapter homeAdapter;
    private Context mContext;
    private LinearLayout llCall;
    private RelativeLayout llUpload;

    public static AdminHomeFragment newInstance(String param1, String param2) {
        AdminHomeFragment fragment = new AdminHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();
        if (Utility.isConnected())
        getDashboard();
        else {
            Utility.showToast("Please check your internet connectivity");
        }
        init(v);
        setRecycleItems(v);
        setItems();



        return v;
    }

    private void setRecycleItems(View v) {
        recyclerviewCategory.setHasFixedSize(true);
        recyclerviewCategory.setLayoutManager(new GridLayoutManager(mContext, 2));


        homeAdapter = new HomeAdapter(mContext, homeModals);
        recyclerviewCategory.setAdapter(null);
        recyclerviewCategory.setAdapter(homeAdapter);
    }

    private void setItems() {
        for (int i = 0; i < titles.length; i++) {
            HomeModal homeModal = new HomeModal();
            homeModal.setTitle(titles[i]);
            homeModal.setImage(images[i]);
            homeModals.add(homeModal);

        }
        homeAdapter.notifyDataSetChanged();
    }



    private void init(View view) {
        llUpload = view.findViewById(R.id.llUpload);
        llUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(getActivity());
            }
        });
        llCall = view.findViewById(R.id.llCall);
        llCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPermissionGranted()){
                    call_action();
                }
            }
        });
        recyclerviewCategory = view.findViewById(R.id.recyclerviewCategory);
        mViewPager = (AutoscrollViewPager) view.findViewById(R.id.viewpager);
        //  mViewPager.setPadding(40, 0, 40, 0);


        adapter = new ImagesAdapter(getChildFragmentManager());
        // Set an Adapter on the ViewPager
        mViewPager.setAdapter(adapter);
        mViewPager.setPadding(50, 0, 50, 0);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(0);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.startAutoScroll();
        mViewPager.setInterval(4000);
        mViewPager.setCycle(true);
        mViewPager.setStopScrollWhenTouch(true);
        // Set a PageTransformer
        //   mViewPager.setPageTransformer(false, new IntroPageTransformer());

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);
    }

    private void call_action() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "9460460580"));
        startActivity(intent);
    }

    public class ImagesAdapter extends FragmentStatePagerAdapter {

        private ImagesAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CardDealsImages().newInstance(position, imagesDeals.get(position));
                default:
                    return new CardDealsImages().newInstance(position, imagesDeals.get(position));
            }
        }

        @Override
        public int getCount() {
            return imagesDeals.size();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                   call_action();
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }

    private void alertDialog(){
        CharSequence options[] = new CharSequence[] {"By Camera or Gallery", "By Whatsapp"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Upload Prescription by");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    Intent intent = new Intent(getActivity(), UploadPrescriptionActivity.class);
                    startActivity(intent);
                }else {
                    Uri uri = Uri.parse("smsto:" + "9460460580");
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.setPackage("com.whatsapp");
                    startActivity(Intent.createChooser(i, ""));
                }
                // the user clicked on options[which]
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //the user clicked on Cancel
            }
        });
        builder.show();
    }

    private void getDashboard() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        OkHttpClient okHttpClient = APIClient.getHttpClient();

        Request request = APIClient.getRequest(getActivity(), CapsuleAPI.WEBSERVICE_GET_DASHBOARD);

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
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
                    getActivity().runOnUiThread(new Runnable() {
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

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imagesDeals.clear();
                                        JSONObject object=jsonObject.optJSONObject("response");
                                        JSONArray array=object.optJSONArray("banners");
                                        for (int i=0;i<array.length();i++){
                                            JSONObject object1=array.optJSONObject(i);
                                            imagesDeals.add(object1.optString("image"));
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else if (response.code() == AppConstants.BAD_REQUEST) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_bad_request), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.INTERNAL_SERVER_ERROR) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.URL_NOT_FOUND) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_url_not_found), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.UNAUTHORIZE_ACCESS) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, getResources().getString(R.string.error_unautorize_access), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (response.code() == AppConstants.CONNECTION_OUT) {
                            getActivity().runOnUiThread(new Runnable() {
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

    public void showDialog(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_upload_prescription);

        LinearLayout llCamera = (LinearLayout) dialog.findViewById(R.id.llCamera);
        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Prefs.getStringPrefs("id").equalsIgnoreCase("")) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    ((Activity)mContext).startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), UploadPrescriptionActivity.class);
                    startActivity(intent);
                }
            }
        });
        LinearLayout llWhatsapp = (LinearLayout) dialog.findViewById(R.id.llWhatsapp);
        llWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("smsto:" + "9014460460");
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(i, ""));
            }
        });


        dialog.show();

    }

}
