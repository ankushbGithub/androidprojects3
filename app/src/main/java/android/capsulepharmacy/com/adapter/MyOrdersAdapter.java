package android.capsulepharmacy.com.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.listener.MyListener;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ItemRowHolder>{
    private ArrayList<MyOrderModal> homeModals;
    private Context mContext;
    private MyListener myListener;

    /**
     * Instantiates a new Product main section adapter.
     *
     * @param context              the context
     * @param MyOrderModals the product section modals
     */
    public MyOrdersAdapter(Context context, ArrayList<MyOrderModal> MyOrderModals, MyListener myListener) {
        this.homeModals = MyOrderModals;
        this.myListener=myListener;
        this.mContext = context;

    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.admin_order_items, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder itemRowHolder, final int i) {

        MyOrderModal myOrderModal=homeModals.get(i);
        final String sectionName = homeModals.get(i).getBookingNumber();
        itemRowHolder.itemTitle.setText(myOrderModal.getCustomerName()+" | "+myOrderModal.getBookingNumber());


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

        if (Prefs.getStringPrefs(AppConstants.USER_ROLE).equalsIgnoreCase("vendor")){
            itemRowHolder.llReorder.setVisibility(View.VISIBLE);
            itemRowHolder.llDeny.setVisibility(View.VISIBLE);
        }else {
            itemRowHolder.llReorder.setVisibility(View.GONE);
            itemRowHolder.llDeny.setVisibility(View.GONE);
        }
        itemRowHolder.llReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myListener.onListen(itemRowHolder.getAdapterPosition(),"accept");
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
        protected TextView itemTitle,tvCreated,tvCategory,tvlocationAt,tvStatus,tvAmt;
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




        }

    }
}
