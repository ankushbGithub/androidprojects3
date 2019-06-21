package android.capsulepharmacy.com.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.activity.BookVendor;
import android.capsulepharmacy.com.activity.CategoryFilterActivity;
import android.capsulepharmacy.com.activity.CategoryFilterActivity2Step;
import android.capsulepharmacy.com.activity.LoginActivity;
import android.capsulepharmacy.com.modal.HomeModal;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemRowHolder> {
    private ArrayList<HomeModal> homeModals;
    private Context mContext;

    /**
     * Instantiates a new Product main section adapter.
     *
     * @param context    the context
     * @param HomeModals the product section modals
     */
    public HomeAdapter(Context context, ArrayList<HomeModal> HomeModals) {
        this.homeModals = HomeModals;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_items, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder itemRowHolder, int i) {
        final String sectionName = homeModals.get(i).getTitle();
        itemRowHolder.itemTitle.setText(sectionName);
        if (Utility.validateURL(homeModals.get(i).getImage()))
            Picasso.get().load(homeModals.get(i).getImage()).fit().into(itemRowHolder.imageView);

        itemRowHolder.cvMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Prefs.getStringPrefs(AppConstants.USER_ROLE).equalsIgnoreCase("customer")) {
                    Log.e("Home Adapter clicked", "yes");
                    if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                        Intent i = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(i);

//                        if ( sectionName.equalsIgnoreCase("Photographer")
//                                || sectionName.equalsIgnoreCase("Flower Decoration")
//                                || sectionName.equalsIgnoreCase("Balloon Decoration")
//                                || sectionName.equalsIgnoreCase("Invitations Card")
//                                || sectionName.equalsIgnoreCase("DJs")
//                                || sectionName.equalsIgnoreCase("Ghodiwala")
//                                || sectionName.equalsIgnoreCase("Dhool")
//                                || sectionName.equalsIgnoreCase("Band")
//                                || sectionName.equalsIgnoreCase("Persist")
//                                || sectionName.equalsIgnoreCase("Pandit Ji")
//                                || sectionName.equalsIgnoreCase("Tailoring")
//                                || sectionName.equalsIgnoreCase("Persist/Pandit Ji")
//                                || sectionName.equalsIgnoreCase("Band/Dhool/Ghodiwala")){
//                             i = new Intent(mContext, CategoryFilterActivity2Step.class);
//                        }
//
//                        i.putExtra("categoryId", homeModals.get(itemRowHolder.getAdapterPosition()).getId());
//                        i.putExtra("categoryName", homeModals.get(itemRowHolder.getAdapterPosition()).getTitle());
                    } else {
                        Intent i = new Intent(mContext, CategoryFilterActivity.class);
                        if ( sectionName.equalsIgnoreCase("Photographer")
                                || sectionName.equalsIgnoreCase("Flower Decoration")
                                || sectionName.equalsIgnoreCase("Balloon Decoration")
                                || sectionName.equalsIgnoreCase("Invitations Card")
                                || sectionName.equalsIgnoreCase("DJs")
                                || sectionName.equalsIgnoreCase("Ghodiwala")
                                || sectionName.equalsIgnoreCase("Dhool")
                                || sectionName.equalsIgnoreCase("Band")
                                || sectionName.equalsIgnoreCase("Persist")
                                || sectionName.equalsIgnoreCase("Pandit Ji")
                                || sectionName.contains("Pandit")
                                || sectionName.equalsIgnoreCase("Tailoring")
                                || sectionName.equalsIgnoreCase("Persist/Pandit Ji")
                                || sectionName.equalsIgnoreCase("Band/Dhool/Ghodiwala")
                                || sectionName.equalsIgnoreCase("Flower Decorators")
                                || sectionName.equalsIgnoreCase("Follower Decorators")
                                || sectionName.equalsIgnoreCase("Balloon Decorators")
                                || sectionName.equalsIgnoreCase("Invitation Cards")
                                || sectionName.equalsIgnoreCase("Band/Dhol/Ghodiwala")
                        ){
                            i = new Intent(mContext, CategoryFilterActivity2Step.class);
                        }
                        i.putExtra("categoryId", homeModals.get(itemRowHolder.getAdapterPosition()).getId());
                        i.putExtra("categoryName", homeModals.get(itemRowHolder.getAdapterPosition()).getTitle());
                        mContext.startActivity(i);
                    }

                } else {
                    if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                        Intent i = new Intent(mContext, LoginActivity.class);
                        /*if ( sectionName.equalsIgnoreCase("Photographer")
                                || sectionName.equalsIgnoreCase("Flower Decoration")
                                || sectionName.equalsIgnoreCase("Balloon Decoration")
                                || sectionName.equalsIgnoreCase("Invitations Card")
                                || sectionName.equalsIgnoreCase("DJs")
                                || sectionName.equalsIgnoreCase("Ghodiwala")
                                || sectionName.equalsIgnoreCase("Dhool")
                                || sectionName.equalsIgnoreCase("Band")
                                || sectionName.equalsIgnoreCase("Persist")
                                || sectionName.equalsIgnoreCase("Pandit Ji")
                                || sectionName.equalsIgnoreCase("Tailoring")
                                || sectionName.equalsIgnoreCase("Persist/Pandit Ji")
                                || sectionName.equalsIgnoreCase("Band/Dhool/Ghodiwala")){
                            i = new Intent(mContext, CategoryFilterActivity2Step.class);
                        }
                        i.putExtra("categoryId", homeModals.get(itemRowHolder.getAdapterPosition()).getId());
                        i.putExtra("categoryName", homeModals.get(itemRowHolder.getAdapterPosition()).getTitle());*/
                        mContext.startActivity(i);
                    }
                }


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
        private CardView cvMain;

        /**
         * Instantiates a new Item row holder.
         *
         * @param view the view
         */
        public ItemRowHolder(View view) {
            super(view);
            item = itemView;
            this.itemTitle = (TextView) view.findViewById(R.id.textViewHeader);
            this.imageView = (ImageView) view.findViewById(R.id.image);
            this.cvMain = (CardView) view.findViewById(R.id.cvMain);


        }

    }
}
