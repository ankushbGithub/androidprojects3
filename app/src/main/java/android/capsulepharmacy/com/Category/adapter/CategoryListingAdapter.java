package android.capsulepharmacy.com.Category.adapter;

import android.capsulepharmacy.com.Category.modal.CategoryListingModal;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryListingAdapter extends RecyclerView.Adapter<CategoryListingAdapter.ItemViewholder> {
    private Context mContext;
    private ArrayList<CategoryListingModal> categoryListingModals;
    public ThreeDots threeDots;

    public interface ThreeDots {
        void onThreeDotCLicked(View v, int position);
    }

    private CardOnClick cardOnClick;

    public interface CardOnClick {
        void onCardClick(int position);
    }

    public CategoryListingAdapter(Context mContext, ArrayList<CategoryListingModal> categoryListingModals, ThreeDots threeDots, CardOnClick cardOnClick) {
        this.mContext = mContext;
        this.categoryListingModals = categoryListingModals;
        this.threeDots = threeDots;
        this.cardOnClick = cardOnClick;
    }

    @Override
    public CategoryListingAdapter.ItemViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_list_items, parent, false);
        return new CategoryListingAdapter.ItemViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryListingAdapter.ItemViewholder holder, int position) {
        CategoryListingModal categoryListingModal = categoryListingModals.get(position);
        holder.tvCatName.setText(categoryListingModal.Name);
        if (Utility.validateString(categoryListingModal.ImageUri))
        Picasso.get().load(categoryListingModal.ImageUri.trim()).placeholder(R.drawable.cat).error(R.drawable.cat).fit().into(holder.imgPic);

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
        return categoryListingModals.size();
    }

    public class ItemViewholder extends RecyclerView.ViewHolder {
        private ImageView imgThreeDots;
        private TextView tvCatName;
        private CircleImageView imgPic;
        private CardView cardViewProduct;

        public ItemViewholder(View itemView) {
            super(itemView);
            imgThreeDots = itemView.findViewById(R.id.imgThreeDots);
            tvCatName = itemView.findViewById(R.id.tvCatName);
            imgPic = itemView.findViewById(R.id.imgPic);
            cardViewProduct = itemView.findViewById(R.id.cardViewProduct);
        }
    }
}


