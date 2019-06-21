package android.capsulepharmacy.com.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.activity.OrderDetailActivity;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class ReOrderAdapter extends RecyclerView.Adapter<ReOrderAdapter.ItemRowHolder>{
    private ArrayList<MyOrderModal> homeModals;
    private Context mContext;

    /**
     * Instantiates a new Product main section adapter.
     *
     * @param context              the context
     * @param MyOrderModals the product section modals
     */
    public ReOrderAdapter(Context context, ArrayList<MyOrderModal> MyOrderModals) {
        this.homeModals = MyOrderModals;
        this.mContext = context;

    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reorder_items, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, final int i) {

       // final String sectionName = homeModals.get(i).getTitle();
      //  itemRowHolder.itemTitle.setText(sectionName);


        itemRowHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext, OrderDetailActivity.class);
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
        protected TextView itemTitle;
        private ImageView imageView;

        /**
         * Instantiates a new Item row holder.
         *
         * @param view the view
         */
        public ItemRowHolder(View view) {
            super(view);
            item=itemView;
            this.itemTitle = (TextView) view.findViewById(R.id.textViewHeader);
            this.imageView=(ImageView) view.findViewById(R.id.image);




        }

    }
}
