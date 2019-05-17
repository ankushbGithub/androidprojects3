package android.capsulepharmacy.com.activity;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.base.CustomFilterDialog;
import android.capsulepharmacy.com.modal.FilterModel;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class Support extends BaseActivity {
    private Context mContext;
    private Toolbar toolbar;
    private EditText etName,etEmail,etMobile,etMessage,etDepartment;
    private ArrayList<FilterModel> filterModels = new ArrayList<>();
    private AppCompatButton btnNext;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        mContext = Support.this;
        findViewId();
        applyInit();
    }

    private void applyInit() {
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
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

        etDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDepartment();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,ChatActivity.class);
                startActivity(i);
            }
        });
    }
    private void filterData(String[] arr) {
        filterModels.clear();
        for (int i = 0; i < arr.length; i++) {
            FilterModel filterModel = new FilterModel();
            filterModel.title = arr[i];
            if (i == 0)
                filterModel.isSelected = true;
            filterModels.add(filterModel);
        }
    }
    private void showDepartment(){
        String[] categoryList = {"IT", "Admin", "Finance"};
        String[] categoryArray = new String[categoryList.length];

        System.arraycopy(categoryList, 0, categoryArray, 0, categoryList.length);
        filterData(categoryArray);

        CustomFilterDialog.with(getSupportFragmentManager()).setFilterList(filterModels)
                .setFilterHeader("Department")
                .setApplyButtonName("Done")
                .setListener(new CustomFilterDialog.OnFilterSelectionListener() {
                    @Override
                    public void onFilterSelected(List<FilterModel> list, FilterModel filterModel) {
                        etDepartment.setText(filterModel.title);

                    }
                }).build();
    }
    private void findViewId() {
        toolbar = findViewById(R.id.toolbar);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etMessage = findViewById(R.id.etMessage);
        etDepartment = findViewById(R.id.etDepartment);
        btnNext = findViewById(R.id.btnNext);
    }
}
