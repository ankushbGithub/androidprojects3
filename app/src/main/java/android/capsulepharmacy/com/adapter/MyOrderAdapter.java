package android.capsulepharmacy.com.adapter;

import android.capsulepharmacy.com.activity.BookVendor;
import android.capsulepharmacy.com.activity.OrderDetailActivity;
import android.capsulepharmacy.com.activity.OrderScreenActivity;
import android.capsulepharmacy.com.listener.MyListener;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Utility;
import android.capsulepharmacy.com.vendor.activity.VendorViewActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;


public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ItemRowHolder>{
    private ArrayList<MyOrderModal> homeModals;
    private Context mContext;
    private MyListener myListener;

    /**
     * Instantiates a new Product main section adapter.
     *
     * @param context              the context
     * @param MyOrderModals the product section modals
     */
    public MyOrderAdapter(Context context, ArrayList<MyOrderModal> MyOrderModals,MyListener myListener) {
        this.homeModals = MyOrderModals;
        this.myListener=myListener;
        this.mContext = context;

    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.myorders_items, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder itemRowHolder, final int i) {

        MyOrderModal myOrderModal=homeModals.get(i);
        final String sectionName = homeModals.get(i).getBookingNumber();
        itemRowHolder.itemTitle.setText(myOrderModal.getVendorName());
        itemRowHolder.tvCategory.setText(myOrderModal.getCategoryName());
        itemRowHolder.tvlocationAt.setText(myOrderModal.getServiceAt());
        itemRowHolder.tvService.setText(myOrderModal.getSubCategoryName());
        itemRowHolder.tvPrice.setText("Rs. "+myOrderModal.getPrice());
        itemRowHolder.ratingBar.setRating((float) myOrderModal.Rating);



        itemRowHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, VendorViewActivity.class);
                i.putExtra("model",myOrderModal);
                mContext.startActivity(i);
              /* Intent i=new Intent(mContext, OrderDetailActivity.class);
               i.putExtra("orderId",homeModals.get(itemRowHolder.getAdapterPosition()).getBookingNumber());
                mContext.startActivity(i);*/
            }
        });
        itemRowHolder.llReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myListener.onListen(i,"book");
            }
        });
        itemRowHolder.llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(mContext, VendorViewActivity.class);
                i.putExtra("model",myOrderModal);
                mContext.startActivity(i);
            }
        });




//      Glide.with(mContext)
//                .load(feedItem.getImageURL())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .centerCrop()
//                .error(R.drawable.bg)
//                .into(feedListRowHolder.thumbView);
    }

    @Override
    public int getItemCount() {
        return (null != homeModals ? homeModals.size() : 0);
    }

    /**
     * The type Item row holder.
     */
    public class ItemRowHolder extends RecyclerView.ViewHolder {

        private final View item;
        /**
         * The Item title.
         */
        protected TextView itemTitle,tvCategory,tvService,tvlocationAt,tvPrice;

        private LinearLayout llReorder,llProfile;
        private RatingBar ratingBar;

        /**
         * Instantiates a new Item row holder.
         *
         * @param view the view
         */
        public ItemRowHolder(View view) {
            super(view);
            item=itemView;
            this.itemTitle = (TextView) view.findViewById(R.id.textViewHeader);
            this.tvCategory=view.findViewById(R.id.tvCategory);
            this.tvService=view.findViewById(R.id.tvService);
            this.tvlocationAt=view.findViewById(R.id.tvlocationAt);

            llProfile=(LinearLayout)view.findViewById(R.id.llProfile);
            llReorder=(LinearLayout)view.findViewById(R.id.llReorder);
            tvPrice=view.findViewById(R.id.tvPrice);
            ratingBar=view.findViewById(R.id.ratingBar);




        }

    }
}
