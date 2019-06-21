package android.capsulepharmacy.com.vendor.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.listener.MyListener;
import android.capsulepharmacy.com.modal.BookSubCategory;
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

public class SubCatBookSelectedAdapter extends RecyclerView.Adapter<SubCatBookSelectedAdapter.ItemRowHolder> {
    private Context mContext;
    private ArrayList<BookSubCategory> selectedSubCatLists;
    private boolean onBind;
    private MyListener myListener;

    public SubCatBookSelectedAdapter(Context mContext, ArrayList<BookSubCategory> selectedSubCatLists,MyListener myListener) {
        this.mContext = mContext;
        this.selectedSubCatLists = selectedSubCatLists;
        this.myListener=myListener;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.book_sub_cat_item, null);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemRowHolder holder, int position) {
        this.onBind = true;
        BookSubCategory selectedSubCatList = selectedSubCatLists.get(position);


        holder.tvSubCatName.setText(selectedSubCatList.getSubCategoryName());

            holder.etPrice.setText("Rs. "+selectedSubCatList.getSubCategoryPrice()+"/item");
            holder.etTotal.setText("Rs. "+(selectedSubCatList.getQty()*selectedSubCatList.getSubCategoryPrice()));


        if (Utility.validateString(selectedSubCatList.getSubCategoryDescription())&& !selectedSubCatList.getSubCategoryDescription().equalsIgnoreCase("null")){
            holder.tvDescription.setText(selectedSubCatList.getSubCategoryDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        }else {
            holder.tvDescription.setVisibility(View.GONE);
            holder.tvDescription.setText("");
        }

        holder.etQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (Utility.validateString(holder.etQty.getText().toString())){
                    selectedSubCatList.setQty(Integer.parseInt(holder.etQty.getText().toString()));
                    selectedSubCatList.setTotal(Integer.parseInt(holder.etQty.getText().toString())*selectedSubCatList.getSubCategoryPrice());
                    holder.etTotal.setText("Rs. "+(selectedSubCatList.getQty()*selectedSubCatList.getSubCategoryPrice()));

                }else {
                    selectedSubCatList.setQty(0);
                    selectedSubCatList.setTotal(0);
                    holder.etTotal.setText("Rs. "+(selectedSubCatList.getQty()*selectedSubCatList.getSubCategoryPrice()));

                }
                myListener.onListen(position,"");

            }
        });

        this.onBind = false;

    }

    @Override
    public int getItemCount() {
        return selectedSubCatLists.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private EditText etPrice,etQty,etTotal;
        private TextView tvSubCatName,tvDescription;


        public ItemRowHolder(View itemView) {
            super(itemView);
            tvSubCatName = itemView.findViewById(R.id.tvSubCatName);
            etPrice = itemView.findViewById(R.id.etPrice);
            etQty = itemView.findViewById(R.id.etQty);
            etTotal = itemView.findViewById(R.id.etTotal);
            tvDescription = itemView.findViewById(R.id.tvDescription);




        }
    }

}
