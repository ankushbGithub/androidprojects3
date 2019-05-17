package android.capsulepharmacy.com.vendor.vendorBase;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.base.CustomFilterDialog;
import android.capsulepharmacy.com.vendor.modal.SubCatFilterModal;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SubCatBottomDialog extends BottomSheetDialogFragment {
    public static final String TAG = CustomFilterDialog.class.getSimpleName();
    private RecyclerView recyclerView;
    private SubCatBottomDialog.CustomFilterAdapter adapter;
    private List<SubCatFilterModal> list;
    private TextView btnFilter;
    private TextView tvTitle;
    private SubCatBottomDialog.OnFilterSelectionListener listener;
    SubCatFilterModal filterModel = null;

    private String applyButtonName;
    private String title;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = getArguments().getParcelableArrayList("list");
        applyButtonName = getArguments().getString("applyButtonName", "Apply");
        title = getArguments().getString("filterHeader", "Drilldown By");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_filter_dialog, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnFilter = view.findViewById(R.id.btnFilter);
        btnFilter.setText(applyButtonName);

        tvTitle = view.findViewById(R.id.textViewHeader);
        tvTitle.setText(title);

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new SubCatBottomDialog.CustomFilterAdapter(list);
        recyclerView.setAdapter(adapter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {

                    if (filterModel == null) {
                        for (SubCatFilterModal filterModel1 : list) {
                            if (filterModel1.isSelected) {
                                filterModel = filterModel1;
                            }
                        }
                        if (filterModel == null) {
                            Toast.makeText(getActivity(), "Select Item First!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    listener.onFilterSelected(list, filterModel);
                    dismiss();
                }
            }
        });
    }


    public static SubCatBottomDialog.Builder with(FragmentManager fm) {
        SubCatBottomDialog.Builder builder = new SubCatBottomDialog.Builder(fm);
        return builder;
    }

    public static class Builder {
        private final Bundle arguments;
        private SubCatBottomDialog.OnFilterSelectionListener listener;
        private FragmentManager fragmentManager;

        private Builder(FragmentManager fm) {
            fragmentManager = fm;
            arguments = new Bundle();
        }

        public SubCatBottomDialog.Builder setFilterHeader(String filterHeader) {
            arguments.putString("filterHeader", filterHeader);
            return this;
        }

        public SubCatBottomDialog.Builder setFilterList(List<SubCatFilterModal> filterList) {
            arguments.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) filterList);
            return this;
        }

        public SubCatBottomDialog.Builder setApplyButtonName(String applyButtonName) {
            arguments.putString("applyButtonName", applyButtonName);
            return this;
        }

        public SubCatBottomDialog.Builder setListener(SubCatBottomDialog.OnFilterSelectionListener listener) {
            this.listener = listener;
            return this;
        }


        public SubCatBottomDialog build() {
            SubCatBottomDialog fragment = new SubCatBottomDialog();
            fragment.setArguments(arguments);
            fragment.show(fragmentManager, "MyBottomSheetDialogFragment");
            fragment.setListener(listener);
            return fragment;
        }
    }



    public void setListener(SubCatBottomDialog.OnFilterSelectionListener listener) {
        this.listener = listener;
    }

    private class CustomFilterAdapter extends RecyclerView.Adapter<SubCatBottomDialog.CustomFilterAdapter.MyViewHolder> {
        private List<SubCatFilterModal> list;

        public CustomFilterAdapter(List<SubCatFilterModal> list) {
            this.list = list;
        }

        @Override
        public SubCatBottomDialog.CustomFilterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_filter_item, parent,false);
            return new SubCatBottomDialog.CustomFilterAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SubCatBottomDialog.CustomFilterAdapter.MyViewHolder holder, int position) {
            final SubCatFilterModal model = list.get(position);
            holder.radio.setChecked(model.isSelected);
            holder.radio.setText("" + model.title);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (SubCatFilterModal filterModel : list) {
                        filterModel.isSelected = false;
                    }
                    model.isSelected = true;
                    filterModel = model;
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public RadioButton radio;

            public MyViewHolder(View itemView) {
                super(itemView);
                radio = itemView.findViewById(R.id.radio);
            }
        }
    }

    public interface OnFilterSelectionListener {
        void onFilterSelected(List<SubCatFilterModal> list, SubCatFilterModal filterModel);

    }

}


