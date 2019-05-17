package android.capsulepharmacy.com.adapter;

import android.capsulepharmacy.com.activity.OrderDetailActivity;
import android.capsulepharmacy.com.listener.MyListener;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.AppConstants;
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

        final String sectionName = homeModals.get(i).getOrderId();
        itemRowHolder.itemTitle.setText("John Doe");
    /*    itemRowHolder.tvCreated.setText("Created on - "+ Utility.getFormattedDates(homeModals.get(i).getCreatedDate(), AppConstants.utc_format,AppConstants.format2));
        if (Utility.validateString(homeModals.get(i).getConfirmDate()) && !homeModals.get(i).getConfirmDate().equalsIgnoreCase("1970-01-01T00:00:00.000Z"))
            itemRowHolder.tvConfirmDate.setText("Confirmed on - "+Utility.getFormattedDates(homeModals.get(i).getConfirmDate(), AppConstants.utc_format,AppConstants.format2));
        if (Utility.validateString(homeModals.get(i).getDeliverDate()) && !homeModals.get(i).getConfirmDate().equalsIgnoreCase("1970-01-01T00:00:00.000Z"))
            itemRowHolder.tvDeliverDate.setText("Delivery on - "+Utility.getFormattedDates(homeModals.get(i).getDeliverDate(), AppConstants.utc_format,AppConstants.format2));
        int status=homeModals.get(i).getStatus();

        String strStatus = null;
        if (status==1){
            strStatus="Pending";
        }else if (status==-1){
            strStatus="Cancelled";
        }else if (status==2){
            strStatus="Confirmed";
        }else if (status==3){
            strStatus="Delivered";
        }else if (status==4){
            strStatus="Dispatched";
        }*/

      //  itemRowHolder.tvStatus.setText(strStatus+"");


        itemRowHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i=new Intent(mContext, OrderDetailActivity.class);
               i.putExtra("orderId",homeModals.get(itemRowHolder.getAdapterPosition()).getId());
                mContext.startActivity(i);
            }
        });
        itemRowHolder.llReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myListener.onListen(i);
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
        protected TextView itemTitle,tvCreated,tvConfirmDate,tvDeliverDate,tvStatus;
        private ImageView imageView;
        private LinearLayout llReorder;

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
            this.tvConfirmDate=view.findViewById(R.id.tvConfirmDate);
            this.tvDeliverDate=view.findViewById(R.id.tvdeliverDate);
            this.imageView=(ImageView) view.findViewById(R.id.image);
            llReorder=(LinearLayout)view.findViewById(R.id.llReorder);
            tvStatus=view.findViewById(R.id.tvStatus);




        }

    }
}
