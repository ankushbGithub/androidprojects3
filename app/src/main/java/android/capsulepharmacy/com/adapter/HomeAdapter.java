package android.capsulepharmacy.com.adapter;

import android.app.Activity;
import android.capsulepharmacy.com.activity.AboutActivity;
import android.capsulepharmacy.com.activity.AdminMyOrders;
import android.capsulepharmacy.com.activity.LoginActivity;
import android.capsulepharmacy.com.activity.MyOrders;
import android.capsulepharmacy.com.activity.OffersActivity;
import android.capsulepharmacy.com.activity.ReorderActivty;
import android.capsulepharmacy.com.activity.TermsActivity;
import android.capsulepharmacy.com.activity.TrackOrderActivity;
import android.capsulepharmacy.com.modal.HomeModal;
import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Prefs;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemRowHolder>{
    private ArrayList<HomeModal> homeModals;
    private Context mContext;

    /**
     * Instantiates a new Product main section adapter.
     *
     * @param context              the context
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
    public void onBindViewHolder(final ItemRowHolder itemRowHolder,  int i) {

        final String sectionName = homeModals.get(i).getTitle();
        itemRowHolder.itemTitle.setText(sectionName);
        itemRowHolder.imageView.setBackgroundResource(homeModals.get(i).getImage());

        itemRowHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=null;
                int position=itemRowHolder.getAdapterPosition();
                if (!Prefs.getStringPrefs("type").equalsIgnoreCase("admin")) {
                    if (position == 0) {
                        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                            i = new Intent(mContext, LoginActivity.class);

                        } else {
                            i = new Intent(mContext, MyOrders.class);
                        }

                    } else if (position == 1) {
                        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                            i = new Intent(mContext, LoginActivity.class);

                        } else {
                            i = new Intent(mContext, MyOrders.class);
                        }
                    } else if (position == 2) {
                        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                            i = new Intent(mContext, LoginActivity.class);

                        } else {
                            i = new Intent(mContext, MyOrders.class);
                        }
                    } else if (position == 3) {
                        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                            i = new Intent(mContext, LoginActivity.class);

                        } else {
                            i = new Intent(mContext, MyOrders.class);
                        }
                    } else if (position == 4) {
                        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                            i = new Intent(mContext, LoginActivity.class);

                        } else {
                            i = new Intent(mContext, MyOrders.class);
                        }
                    } else if (position == 5) {
                        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                            i = new Intent(mContext, LoginActivity.class);

                        } else {
                            i = new Intent(mContext, MyOrders.class);
                        }
                    }
                    mContext.startActivity(i);
                }else {
                    if (position == 0) {
                        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                             i = new Intent(mContext, LoginActivity.class);


                        } else {
                            i = new Intent(mContext, AdminMyOrders.class);
                        }

                    } else if (position == 1) {
                        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                            i = new Intent(mContext, LoginActivity.class);

                        } else {
                            i = new Intent(mContext, MyOrders.class);
                        }
                    } else if (position == 2) {
                        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                            i = new Intent(mContext, LoginActivity.class);

                        } else {
                            i = new Intent(mContext, MyOrders.class);
                        }
                    } else if (position == 3) {
                        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                            i = new Intent(mContext, LoginActivity.class);

                        } else {
                            i = new Intent(mContext, MyOrders.class);
                        }
                    }
                    mContext.startActivity(i);
                }
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
