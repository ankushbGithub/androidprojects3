package android.capsulepharmacy.com.vendor.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.modal.SelectedSubCatList;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;

public class SubCatSelectedAdapter extends RecyclerView.Adapter<SubCatSelectedAdapter.ItemRowHolder> {
    private Context mContext;
    private ArrayList<SelectedSubCatList> selectedSubCatLists;
    private boolean onBind;

    public SubCatSelectedAdapter(Context mContext, ArrayList<SelectedSubCatList> selectedSubCatLists) {
        this.mContext = mContext;
        this.selectedSubCatLists = selectedSubCatLists;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.selected_sub_cat_item, null);
        return new ItemRowHolder(v, new MyCustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(ItemRowHolder holder, int position) {
        this.onBind = true;
        SelectedSubCatList selectedSubCatList = selectedSubCatLists.get(position);
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition(), holder);

        holder.tvSubCatName.setText(selectedSubCatList.SubCategoryName);
        if (selectedSubCatList.SubCategoryPrice!=0){
            holder.etPrice.setText(selectedSubCatList.SubCategoryPrice+"");
        }else {
            holder.etPrice.setText(0+"");
        }

        if (Utility.validateString(selectedSubCatList.SubCategoryDescription)&& !selectedSubCatList.SubCategoryDescription.equalsIgnoreCase("null")){
            holder.etDescription.setText(selectedSubCatList.SubCategoryDescription);
        }else {
            holder.etDescription.setText("");
        }

        this.onBind = false;

    }

    @Override
    public int getItemCount() {
        return selectedSubCatLists.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private EditText etPrice,etDescription;
        private TextView tvSubCatName;
        MyCustomEditTextListener myCustomEditTextListener;

        public ItemRowHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            tvSubCatName = itemView.findViewById(R.id.tvSubCatName);
            etPrice = itemView.findViewById(R.id.etPrice);
            etDescription = itemView.findViewById(R.id.etDescription);

            this.myCustomEditTextListener = myCustomEditTextListener;
            this.etPrice.addTextChangedListener(myCustomEditTextListener);
            this.etDescription.addTextChangedListener(myCustomEditTextListener);
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {

        private int position;
        private ItemRowHolder holder;

        public void updatePosition(int position, ItemRowHolder holder) {
            this.position = position;
            this.holder = holder;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            try {
                if (holder.etPrice != null) {
                    if (holder.etPrice.getText().hashCode() == charSequence.hashCode()) {
                        if (Utility.validateString(charSequence.toString())) {
                            selectedSubCatLists.get(position).SubCategoryPrice = Integer.parseInt(charSequence.toString());
                        }else {
                            selectedSubCatLists.get(position).SubCategoryPrice = 0;

                        }
                    }
                }
                if (holder.etDescription!=null){
                    if (holder.etDescription.getText().hashCode() == charSequence.hashCode()) {
                        if (Utility.validateString(charSequence.toString())) {
                            selectedSubCatLists.get(position).SubCategoryDescription = charSequence.toString();
                        }else {
                            selectedSubCatLists.get(position).SubCategoryDescription = "";

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
}
