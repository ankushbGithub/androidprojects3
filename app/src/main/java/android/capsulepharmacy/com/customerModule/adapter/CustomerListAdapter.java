package android.capsulepharmacy.com.customerModule.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.customerModule.modal.CustomerModal;
import android.capsulepharmacy.com.utility.Constants;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.ItemViewholder> {
    private Context mContext;
    private ArrayList<CustomerModal> customerModals;
    public ThreeDots threeDots;
    public interface ThreeDots {
        void onThreeDotCLicked(View v, int position);
    }
    private CardOnClick cardOnClick;
    public interface CardOnClick {
        void onCardClick(int position);
    }


    public CustomerListAdapter(Context mContext, ArrayList<CustomerModal> customerModals, ThreeDots threeDots,CardOnClick cardOnClick) {
        this.mContext = mContext;
        this.customerModals = customerModals;
        this.threeDots = threeDots;
        this.cardOnClick = cardOnClick;
    }

    @Override
    public CustomerListAdapter.ItemViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list_items, parent, false);
        return new CustomerListAdapter.ItemViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomerListAdapter.ItemViewholder holder, int position) {
        CustomerModal customerModal = customerModals.get(position);
        holder.tvUserName.setText(customerModal.FirstName + " " + customerModal.LastName);
        holder.tvMob.setText(customerModal.MobileNo);
        holder.tvEmail.setText(customerModal.Email);

        if (TextUtils.isEmpty(customerModal.DOB)) {
            holder.tvDOB.setText("NA");
        } else {
            if (Utility.isValidDate(customerModal.DOB)) {
                String bDate = Utility.getFormattedDates(customerModal.DOB, Constants.format6, Constants.format2);
                holder.tvDOB.setText(bDate);
            } else {
                String bDate = Utility.getFormattedDates(customerModal.DOB, Constants.formatDate, Constants.format2);
                holder.tvDOB.setText(bDate);
            }

        }
        Picasso.get().load(customerModal.PhotoUrl.trim()).placeholder(R.drawable.user).error(R.drawable.user).fit().into(holder.imgPic);

//        Glide.with(mContext)
//                .load(customerModal.PhotoUrl.trim())
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .placeholder(R.drawable.user)
//                .fitCenter()
//                .error(R.drawable.user)
//                .fitCenter()
//                .into(holder.imgPic);

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
        return customerModals.size();
    }

    public class ItemViewholder extends RecyclerView.ViewHolder {
        private ImageView imgThreeDots;
        private TextView tvUserName, tvMob, tvEmail, tvDOB;
        private CircleImageView imgPic;
        private CardView cardViewProduct;
        public ItemViewholder(View itemView) {
            super(itemView);
            imgThreeDots = itemView.findViewById(R.id.imgThreeDots);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMob = itemView.findViewById(R.id.tvMob);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvDOB = itemView.findViewById(R.id.tvDOB);
            imgPic = itemView.findViewById(R.id.imgPic);
            cardViewProduct = itemView.findViewById(R.id.cardViewProduct);
        }
    }
}
