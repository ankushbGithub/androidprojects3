package android.capsulepharmacy.com.Category.adapter;

import android.capsulepharmacy.com.Category.modal.SubCatListingModal;
import android.capsulepharmacy.com.R;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SubCategoryListAdapter extends RecyclerView.Adapter<SubCategoryListAdapter.ItemViewholder> {
    private Context mContext;
    private ArrayList<SubCatListingModal> subCatListingModals;
    public ThreeDots threeDots;

    public interface ThreeDots {
        void onThreeDotCLicked(View v, int position);
    }

    private CardOnClick cardOnClick;

    public interface CardOnClick {
        void onCardClick(int position);
    }

    public SubCategoryListAdapter(Context mContext, ArrayList<SubCatListingModal> subCatListingModals, SubCategoryListAdapter.ThreeDots threeDots, SubCategoryListAdapter.CardOnClick cardOnClick) {
        this.mContext = mContext;
        this.subCatListingModals = subCatListingModals;
        this.threeDots = threeDots;
        this.cardOnClick = cardOnClick;
    }

    @Override
    public SubCategoryListAdapter.ItemViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_cat_list_items, parent, false);
        return new SubCategoryListAdapter.ItemViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(SubCategoryListAdapter.ItemViewholder holder, int position) {
        SubCatListingModal subCatListingModal = subCatListingModals.get(position);
        holder.tvSubCatName.setText(subCatListingModal.Name);
        holder.imgThreeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threeDots.onThreeDotCLicked(v, holder.getAdapterPosition());
            }
        });
        holder.cardViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardOnClick.onCardClick(holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return subCatListingModals.size();
    }

    public class ItemViewholder extends RecyclerView.ViewHolder {
        private ImageView imgThreeDots;
        private TextView tvSubCatName;
        private CardView cardViewProduct;

        public ItemViewholder(View itemView) {
            super(itemView);
            imgThreeDots = itemView.findViewById(R.id.imgThreeDots);
            tvSubCatName = itemView.findViewById(R.id.tvSubCatName);
            cardViewProduct = itemView.findViewById(R.id.cardViewProduct);
        }
    }
}



