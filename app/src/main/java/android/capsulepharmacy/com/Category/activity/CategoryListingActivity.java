package android.capsulepharmacy.com.Category.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.capsulepharmacy.com.APIClient;
import android.capsulepharmacy.com.CapsuleAPI;
import android.capsulepharmacy.com.Category.adapter.CategoryListingAdapter;
import android.capsulepharmacy.com.Category.modal.CategoryListingModal;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.DeleteDialog;
import android.capsulepharmacy.com.utility.NetUtil;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CategoryListingActivity extends AppCompatActivity implements CategoryListingAdapter.CardOnClick, CategoryListingAdapter.ThreeDots, DeleteDialog.InDelete {
    private Toolbar toolbar;
    private Context mContext;
    private String TAG = CategoryListingActivity.class.getSimpleName();
    private RecyclerView rvCatList;
    private SwipeRefreshLayout mySwipeRefresh;
    private FloatingActionButton fab;
    private ArrayList<CategoryListingModal> categoryListingModals = new ArrayList<>();
    private ArrayList<CategoryListingModal> arrSearlist = new ArrayList<>();

    private CategoryListingAdapter categoryListingAdapter;
    private DeleteDialog deleteDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_list_act);
        mContext = CategoryListingActivity.this;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyInit();
    }

    private void applyInit() {
        if (NetUtil.isNetworkAvailable(mContext)) {
            getCategoryList();

        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void disableSwipeToRefresh() {
        if (mySwipeRefresh.isRefreshing()) {
            mySwipeRefresh.setRefreshing(false);
        }
    }

    private void getCategoryList() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_CAT_LIST;
        Log.e(TAG, "Get Request**" + url);

        final Request request = APIClient.getCatRequest(mContext, url);
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

    }

    private void setServerData(String responseData) {
        categoryListingModals.clear();
        arrSearlist.clear();
        try {
            JSONArray cjsonArray = new JSONArray(responseData);
            for (int i = 0; i < cjsonArray.length(); i++) {
                JSONObject jsonObject1 = cjsonArray.optJSONObject(i);
                CategoryListingModal categoryListingModal = new CategoryListingModal();
                categoryListingModal.Id = jsonObject1.optInt("Id");
                categoryListingModal.Name = jsonObject1.optString("Name");
                categoryListingModal.ImagePath = jsonObject1.optString("ImagePath");
                categoryListingModal.ImageUri = jsonObject1.optString("ImageUri");
                categoryListingModals.add(categoryListingModal);
                arrSearlist.add(categoryListingModal);
            }
            Collections.sort(arrSearlist, (p1, p2) -> p1.Name.compareToIgnoreCase(p2.Name));
            rvCatList.setHasFixedSize(true);
            rvCatList.setLayoutManager(new LinearLayoutManager(mContext));
            categoryListingAdapter = new CategoryListingAdapter(mContext, arrSearlist, this, this);
            rvCatList.setAdapter(categoryListingAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        rvCatList = findViewById(R.id.rvCatList);
        mySwipeRefresh = findViewById(R.id.mySwipeRefresh);
        fab = findViewById(R.id.fab);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle(getResources().getString(R.string.toolbar_title_cat_list));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        mySwipeRefresh.setOnRefreshListener(
                () -> {
                    if (NetUtil.isNetworkAvailable(mContext)) {
                        getCategoryList();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, CategoryCreateActivity.class);
                i.putExtra("isEdit", false);
                startActivity(i);
            }
        });

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

    @Override
    public void onCardClick(int position) {
        CategoryListingModal categoryListingModal = arrSearlist.get(position);
        Intent i = new Intent(mContext, SubCategoryListingActivity.class);
        i.putExtra("catId", categoryListingModal.Id);
        startActivity(i);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.vendor_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        ((EditText) searchView.findViewById(R.id.search_src_text)).setTextColor(getResources().getColor(R.color.white));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    searchView.setIconified(true);
                }
                filterData(query, categoryListingModals);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    searchView.setIconified(true);
                }
                filterData(newText, categoryListingModals);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filterData(String newText, ArrayList<CategoryListingModal> responseList) {
        if (responseList != null) {
            newText = newText.toLowerCase(Locale.getDefault());
            arrSearlist.clear();
            if (newText.length() == 0) {
                arrSearlist.addAll(responseList);
            } else {
                for (CategoryListingModal categoryListingModal : responseList) {
                    if (categoryListingModal.Name != null) {

                        if (categoryListingModal.Name.toLowerCase().contains(newText) ||
                                categoryListingModal.Name.toUpperCase().contains(newText)) {
                            arrSearlist.add(categoryListingModal);
                        }
                    }
                }
            }
        }
        Collections.sort(arrSearlist, (p1, p2) -> p1.Name.compareToIgnoreCase(p2.Name));
        rvCatList.setHasFixedSize(true);
        rvCatList.setLayoutManager(new LinearLayoutManager(mContext));
        categoryListingAdapter = new CategoryListingAdapter(mContext, arrSearlist, this, this);
        rvCatList.setAdapter(categoryListingAdapter);


    }

    @Override
    public void onThreeDotCLicked(View v, int position) {
        CategoryListingModal categoryListingModal = arrSearlist.get(position);
        showPopUpMenu(v, categoryListingModal.Id, categoryListingModal.Name, categoryListingModal.ImageUri, categoryListingModal.ImagePath);
    }

    private void showPopUpMenu(View view, int id, String name, String ImageUri, String ImagePath) {
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
            popup.getMenuInflater().inflate(R.menu.cat_three_dots_options, popup.getMenu());
            popup.getMenu().getItem(0).setIcon(R.drawable.icon_edit);
            popup.getMenu().getItem(0).setTitle("Edit");

            popup.getMenu().getItem(1).setIcon(R.drawable.ic_action_sub_category);
            popup.getMenu().getItem(1).setTitle("Create Sub Category");

            popup.getMenu().getItem(2).setIcon(R.drawable.icon_delete);
            popup.getMenu().getItem(2).setTitle("Delete");

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem menu_item) {
                    switch (menu_item.getItemId()) {
                        case R.id.nav_edit:
                            //handle the click here
                            onCatEditClicked(id, name, ImageUri, ImagePath);
                            break;
                        case R.id.nav_subCat:
                            onCreateSubCat(id);
                            break;
                        case R.id.nav_deleted:
                            onCatDeleteClicked(id, name);
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

    private void onCatDeleteClicked(int id, String name) {
        showDeleteConfirmation(id, name);

    }

    private void showDeleteConfirmation(int id, String name) {
        deleteDialog = new DeleteDialog(CategoryListingActivity.this, this, name, id, "Are you sure you want to delete " + name + " ?");
        deleteDialog.setCancelable(false);
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.show();

    }

    private void onCatEditClicked(int id, String name, String imageUri, String imagePath) {
        Intent i = new Intent(mContext, CategoryCreateActivity.class);
        i.putExtra("id", id);
        i.putExtra("name", name);
        i.putExtra("imageUri", imageUri);
        i.putExtra("imagePath", imagePath);
        i.putExtra("isEdit", true);
        startActivity(i);
    }

    private void onCreateSubCat(int catId) {
        Intent i = new Intent(mContext, CreateSubCategoryActivity.class);
        i.putExtra("catId", catId);
        i.putExtra("id", 0);
        i.putExtra("isEdit", false);
        i.putExtra("isFromCat", true);
        startActivity(i);
    }

    @Override
    public void onInDelete(String firstName, int id) {
        if (NetUtil.isNetworkAvailable(mContext)) {
            deleteCat(id);
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.no_internet_connection_warning_server_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCat(int id) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
        progressDialog.show();

        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = CapsuleAPI.WEB_SERVICE_CAT_DELETE + id;
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
                        runOnUiThread(() ->
                                getCategoryList());

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
