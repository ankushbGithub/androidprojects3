package android.capsulepharmacy.com.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.activity.OrderConfirmationActivity;
import android.capsulepharmacy.com.activity.OrderDetailActivity;
import android.capsulepharmacy.com.listener.MyListener;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.ItemRowHolder>{
    private ArrayList<MyOrderModal> homeModals;
    private Context mContext;
    private MyListener myListener;

    /**
     * Instantiates a new Product main section adapter.
     *
     * @param context              the context
     * @param MyOrderModals the product section modals
     */
    public MyHistoryAdapter(Context context, ArrayList<MyOrderModal> MyOrderModals, MyListener myListener) {
        this.homeModals = MyOrderModals;
        this.myListener=myListener;
        this.mContext = context;

    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.myhistory_items, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder itemRowHolder, final int i) {

        String role=Prefs.getStringPrefs(AppConstants.USER_ROLE);
        MyOrderModal myOrderModal=homeModals.get(i);
        final String sectionName = homeModals.get(i).getBookingNumber();
        if (role.equalsIgnoreCase("customer")){
            itemRowHolder.itemTitle.setText(myOrderModal.getVendorName()+" | "+myOrderModal.getBookingNumber());
        }else if (role.equalsIgnoreCase("vendor")){
            itemRowHolder.itemTitle.setText(myOrderModal.getCustomerName()+" | "+myOrderModal.getBookingNumber());
        }else{
            itemRowHolder.itemTitle.setText(myOrderModal.getCustomerName()+" | "+myOrderModal.getVendorName()+" | "+myOrderModal.getBookingNumber());
        }



        itemRowHolder.tvCreated.setText(Utility.getFormattedDates(homeModals.get(i).getBookingDate(), AppConstants.format7,AppConstants.format2));
       int status=homeModals.get(i).getStatus();

        String strStatus = null;
        if (status==0 || status==1){
            strStatus="Pending";
        }else if (status==3){
            strStatus="Cancelled";
        }else if (status==2){
            strStatus="Confirmed";
        }

        itemRowHolder.tvStatus.setText(strStatus+"");
        itemRowHolder.tvCategory.setText(myOrderModal.getCategoryName()+"");
        itemRowHolder.tvlocationAt.setText(myOrderModal.getServiceAt()+"");
        itemRowHolder.tvAmt.setText("Paid Rs. "+myOrderModal.getBookingAmt()+"");



        itemRowHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*  Intent i=new Intent(mContext, OrderDetailActivity.class);
               i.putExtra("orderId",homeModals.get(itemRowHolder.getAdapterPosition()).getBookingNumber());
                mContext.startActivity(i);*/
            }
        });

        if (role.equalsIgnoreCase("vendor")){
            if (status==0||status==1) {
                itemRowHolder.llReorder.setVisibility(View.GONE);
                itemRowHolder.llDeny.setVisibility(View.GONE);
                itemRowHolder.tvGreenTitle.setText("Accept");
            }else {
                if (myOrderModal.Rating<1) {
                    itemRowHolder.llReorder.setVisibility(View.VISIBLE);
                    itemRowHolder.llDeny.setVisibility(View.INVISIBLE);
                    itemRowHolder.tvGreenTitle.setText("Add Rating");
                }else {
                    itemRowHolder.llReorder.setVisibility(View.GONE);
                    itemRowHolder.llDeny.setVisibility(View.GONE);
                }
            }
            itemRowHolder.tvRedTitle.setText("Deny");
            if (status==3){
                itemRowHolder.llReorder.setVisibility(View.GONE);
                itemRowHolder.llDeny.setVisibility(View.GONE);
            }
        }else if (role.equalsIgnoreCase("admin")){
            if (status==0||status==1) {
                itemRowHolder.llReorder.setVisibility(View.VISIBLE);
                itemRowHolder.llDeny.setVisibility(View.VISIBLE);
                itemRowHolder.tvGreenTitle.setText("Accept");
            }else {
                itemRowHolder.llReorder.setVisibility(View.GONE);
                itemRowHolder.llDeny.setVisibility(View.GONE);
                itemRowHolder.tvGreenTitle.setText("Add Rating");
            }
            itemRowHolder.tvRedTitle.setText("Deny");
            if (status==3){
                itemRowHolder.llReorder.setVisibility(View.GONE);
                itemRowHolder.llDeny.setVisibility(View.GONE);
            }
        }else {
            if (status==0||status==1) {
                itemRowHolder.llReorder.setVisibility(View.INVISIBLE);
                itemRowHolder.llDeny.setVisibility(View.VISIBLE);
                itemRowHolder.tvRedTitle.setText("Cancel");
            }else {
                if (myOrderModal.Rating<1) {
                    itemRowHolder.llReorder.setVisibility(View.VISIBLE);
                    itemRowHolder.llDeny.setVisibility(View.INVISIBLE);
                    itemRowHolder.tvGreenTitle.setText("Add Rating");
                }else {
                    itemRowHolder.llReorder.setVisibility(View.GONE);
                    itemRowHolder.llDeny.setVisibility(View.GONE);
                }
            }
            if (status==3){
                itemRowHolder.llReorder.setVisibility(View.GONE);
                itemRowHolder.llDeny.setVisibility(View.GONE);
            }
        }
        itemRowHolder.llReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==0||status==1) {
                    myListener.onListen(itemRowHolder.getAdapterPosition(), "accept");
                }else{
                    myListener.onListen(itemRowHolder.getAdapterPosition(), "rating");
                }
            }
        });
        itemRowHolder.llDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myListener.onListen(itemRowHolder.getAdapterPosition(),"deny");
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
        protected TextView itemTitle,tvCreated,tvCategory,tvlocationAt,tvStatus,tvAmt,tvRedTitle,tvGreenTitle;
        private ImageView imageView;
        private LinearLayout llReorder,llDeny;

        /**
         * Instantiates a new Item row holder.
         *
         * @param view the view
         */
        public ItemRowHolder(View view) {
            super(view);
            item=itemView;
            this.itemTitle = (TextView) view.findViewById(R.id.textViewHeader);
            this.tvCreated=view.findViewById(R.id.tvCreated);
            this.tvCategory=view.findViewById(R.id.tvCategory);
            this.tvlocationAt=view.findViewById(R.id.tvlocationAt);
            this.imageView=(ImageView) view.findViewById(R.id.image);
            llReorder=(LinearLayout)view.findViewById(R.id.llReorder);
            llDeny=(LinearLayout)view.findViewById(R.id.llDeny);
            tvStatus=view.findViewById(R.id.tvStatus);
            tvAmt=view.findViewById(R.id.tvAmt);
            tvRedTitle=view.findViewById(R.id.tvRedTitle);
            tvGreenTitle=view.findViewById(R.id.tvGreenTitle);




        }

    }
}
