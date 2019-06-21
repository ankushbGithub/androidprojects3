package android.capsulepharmacy.com.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.modal.MyOrderModal;
import android.capsulepharmacy.com.utility.RoundedTransformation;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ItemRowHolder>{
    private ArrayList<MyOrderModal> homeModals;
    private Context mContext;

    /**
     * Instantiates a new Product main section adapter.
     *
     * @param context              the context
     * @param MyOrderModals the product section modals
     */
    public OffersAdapter(Context context, ArrayList<MyOrderModal> MyOrderModals) {
        this.homeModals = MyOrderModals;
        this.mContext = context;

    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_item, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, final int i) {

     /*   final String sectionName = homeModals.get(i).getTitle();
        itemRowHolder.itemTitle.setText(sectionName);
        if (Utility.validateURL(homeModals.get(i).getImage()))
            Picasso.get().load(homeModals.get(i).getImage()).fit().into(itemRowHolder.imageView);
*/
        itemRowHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent i=new Intent(mContext, ActivityContentTab.class);
                mContext.startActivity(i);*/
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
        private RoundedTransformation imageView;

        /**
         * Instantiates a new Item row holder.
         *
         * @param view the view
         */
        public ItemRowHolder(View view) {
            super(view);
            item=itemView;
            this.itemTitle = (TextView) view.findViewById(R.id.text);
            this.imageView=(RoundedTransformation) view.findViewById(R.id.image);




        }

    }
}
