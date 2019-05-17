package android.capsulepharmacy.com.base;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.modal.FilterModel;
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

public class CustomFilterDialog extends BottomSheetDialogFragment {
    public static final String TAG = CustomFilterDialog.class.getSimpleName();
    private RecyclerView recyclerView;
    private CustomFilterAdapter adapter;
    private List<FilterModel> list;
    private TextView btnFilter;
    private TextView tvTitle;
    private OnFilterSelectionListener listener;
    FilterModel filterModel = null;

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
        adapter = new CustomFilterAdapter(list);
        recyclerView.setAdapter(adapter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {

                    if (filterModel == null) {
                        for (FilterModel filterModel1 : list) {
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

/*

    public static CustomFilterDialog showDialog(FragmentManager fm, boolean isCancelEnable, List<FilterModel> list, OnFilterSelectionListener listener) {
        CustomFilterDialog myDialog = new CustomFilterDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putBoolean("isCancelEnable", isCancelEnable);
        args.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) list);
        myDialog.setArguments(args);
        myDialog.show(fm, "MyBottomSheetDialogFragment");
        myDialog.setListener(listener);
        return myDialog;
    }
*/

    public static Builder with(FragmentManager fm) {
        Builder builder = new Builder(fm);
        return builder;
    }

    public static class Builder {
        private final Bundle arguments;
        private OnFilterSelectionListener listener;
        private FragmentManager fragmentManager;

        private Builder(FragmentManager fm) {
            fragmentManager = fm;
            arguments = new Bundle();
        }

        public Builder setFilterHeader(String filterHeader) {
            arguments.putString("filterHeader", filterHeader);
            return this;
        }

        public Builder setFilterList(List<FilterModel> filterList) {
            arguments.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) filterList);
            return this;
        }

        public Builder setApplyButtonName(String applyButtonName) {
            arguments.putString("applyButtonName", applyButtonName);
            return this;
        }

        public Builder setListener(OnFilterSelectionListener listener) {
            this.listener = listener;
            return this;
        }


        public CustomFilterDialog build() {
            CustomFilterDialog fragment = new CustomFilterDialog();
            fragment.setArguments(arguments);
            fragment.show(fragmentManager, "MyBottomSheetDialogFragment");
            fragment.setListener(listener);
            return fragment;
        }
    }



    public void setListener(OnFilterSelectionListener listener) {
        this.listener = listener;
    }

    private class CustomFilterAdapter extends RecyclerView.Adapter<CustomFilterAdapter.MyViewHolder> {
        private List<FilterModel> list;

        public CustomFilterAdapter(List<FilterModel> list) {
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_filter_item, parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final FilterModel model = list.get(position);
            holder.radio.setChecked(model.isSelected);
            holder.radio.setText("" + model.title);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (FilterModel filterModel : list) {
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
        void onFilterSelected(List<FilterModel> list, FilterModel filterModel);

    }

}

