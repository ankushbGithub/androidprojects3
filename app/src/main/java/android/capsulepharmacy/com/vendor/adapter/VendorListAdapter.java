package android.capsulepharmacy.com.vendor.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.vendor.modal.VendorListModal;
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

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.ItemViewholder> {
    private Context mContext;
    private ArrayList<VendorListModal> vendorListModals;
    public ThreeDots threeDots;

    public interface ThreeDots {
        void onThreeDotCLicked(View v, int position);
    }
    private VendorListAdapter.CardOnClick cardOnClick;
    public interface CardOnClick {
        void onCardClick(int position);
    }


    public VendorListAdapter(Context mContext, ArrayList<VendorListModal> vendorListModals, ThreeDots threeDots, CardOnClick cardOnClick) {
        this.mContext = mContext;
        this.vendorListModals = vendorListModals;
        this.threeDots = threeDots;
        this.cardOnClick = cardOnClick;
    }

    @Override
    public VendorListAdapter.ItemViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_list_items, parent, false);
        return new VendorListAdapter.ItemViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(VendorListAdapter.ItemViewholder holder, int position) {
        VendorListModal vendorListModal = vendorListModals.get(position);
        holder.tvUserName.setText(vendorListModal.Name);
        holder.tvMob.setText(vendorListModal.MobileNo);
        holder.tvGstin.setText(vendorListModal.GSTIN);

        Picasso.get().load(vendorListModal.PhotoUrl.trim()).placeholder(R.drawable.user).error(R.drawable.user).fit().into(holder.imgPic);

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
        return vendorListModals.size();
    }

    public class ItemViewholder extends RecyclerView.ViewHolder {
        private ImageView imgThreeDots;
        private TextView tvUserName, tvMob, tvGstin;
        private CircleImageView imgPic;
        private CardView cardViewProduct;

        public ItemViewholder(View itemView) {
            super(itemView);
            imgThreeDots = itemView.findViewById(R.id.imgThreeDots);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMob = itemView.findViewById(R.id.tvMob);
            tvGstin = itemView.findViewById(R.id.tvGstin);
            imgPic = itemView.findViewById(R.id.imgPic);
            cardViewProduct = itemView.findViewById(R.id.cardViewProduct);
        }
    }
}

