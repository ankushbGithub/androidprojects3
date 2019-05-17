package android.capsulepharmacy.com.vendor.vendorBase;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.vendor.modal.ServiceFilterModal;
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

public class ServiceBottomDialog extends BottomSheetDialogFragment {
    public static final String TAG = ServiceBottomDialog.class.getSimpleName();
    private RecyclerView recyclerView;
    private CustomFilterAdapter adapter;
    private List<ServiceFilterModal> list;
    private TextView btnFilter;
    private TextView tvTitle;
    private OnFilterSelectionListener listener;
    ServiceFilterModal filterModel = null;

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
        adapter = new ServiceBottomDialog.CustomFilterAdapter(list);
        recyclerView.setAdapter(adapter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {

                    if (filterModel == null) {
                        for (ServiceFilterModal filterModel1 : list) {
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


    public static ServiceBottomDialog.Builder with(FragmentManager fm) {
        ServiceBottomDialog.Builder builder = new ServiceBottomDialog.Builder(fm);
        return builder;
    }

    public static class Builder {
        private final Bundle arguments;
        private ServiceBottomDialog.OnFilterSelectionListener listener;
        private FragmentManager fragmentManager;

        private Builder(FragmentManager fm) {
            fragmentManager = fm;
            arguments = new Bundle();
        }

        public ServiceBottomDialog.Builder setFilterHeader(String filterHeader) {
            arguments.putString("filterHeader", filterHeader);
            return this;
        }

        public ServiceBottomDialog.Builder setFilterList(List<ServiceFilterModal> filterList) {
            arguments.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) filterList);
            return this;
        }

        public ServiceBottomDialog.Builder setApplyButtonName(String applyButtonName) {
            arguments.putString("applyButtonName", applyButtonName);
            return this;
        }

        public ServiceBottomDialog.Builder setListener(ServiceBottomDialog.OnFilterSelectionListener listener) {
            this.listener = listener;
            return this;
        }


        public ServiceBottomDialog build() {
            ServiceBottomDialog fragment = new ServiceBottomDialog();
            fragment.setArguments(arguments);
            fragment.show(fragmentManager, "MyBottomSheetDialogFragment");
            fragment.setListener(listener);
            return fragment;
        }
    }



    public void setListener(ServiceBottomDialog.OnFilterSelectionListener listener) {
        this.listener = listener;
    }

    private class CustomFilterAdapter extends RecyclerView.Adapter<ServiceBottomDialog.CustomFilterAdapter.MyViewHolder> {
        private List<ServiceFilterModal> list;

        public CustomFilterAdapter(List<ServiceFilterModal> list) {
            this.list = list;
        }

        @Override
        public ServiceBottomDialog.CustomFilterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_filter_item, parent,false);
            return new ServiceBottomDialog.CustomFilterAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ServiceBottomDialog.CustomFilterAdapter.MyViewHolder holder, int position) {
            final ServiceFilterModal model = list.get(position);
            holder.radio.setChecked(model.isSelected);
            holder.radio.setText("" + model.title);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (ServiceFilterModal filterModel : list) {
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
        void onFilterSelected(List<ServiceFilterModal> list, ServiceFilterModal filterModel);

    }

}


