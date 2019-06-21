package android.capsulepharmacy.com.vendor.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.vendor.modal.GalleryModal;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class VendorGalleryAdapter extends RecyclerView.Adapter<VendorGalleryAdapter.ItemViewholder> {
    private Context mContext;
    private ArrayList<GalleryModal> galleryModals;
    public ThreeDots threeDots;
    public interface ThreeDots {
        void onThreeDotCLicked(View v, int position);
    }

    public DeleteImage deleteImage;
    public interface DeleteImage{
        void onDeleteImage(View v,int position);
    }


    public VendorGalleryAdapter(Context mContext, ArrayList<GalleryModal> galleryModals, ThreeDots threeDots,DeleteImage deleteImage) {
        this.mContext = mContext;
        this.galleryModals = galleryModals;
        this.threeDots = threeDots;
        this.deleteImage = deleteImage;
    }

    @Override
    public VendorGalleryAdapter.ItemViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_items, parent, false);
        return new VendorGalleryAdapter.ItemViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(VendorGalleryAdapter.ItemViewholder holder, int position) {
        GalleryModal galleryModal = galleryModals.get(position);
        Picasso.get().load(galleryModal.ImageUrl.trim()).placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).fit().into(holder.ivGallery);

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage.onDeleteImage(v,holder.getAdapterPosition());
            }
        });
//        holder.imgThreeDots.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                threeDots.onThreeDotCLicked(v, holder.getAdapterPosition());
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return galleryModals.size();
    }

    public class ItemViewholder extends RecyclerView.ViewHolder {
        private ImageView ivGallery,ivDelete;

        public ItemViewholder(View itemView) {
            super(itemView);
            ivGallery = itemView.findViewById(R.id.ivGallery);
            ivDelete = itemView.findViewById(R.id.ivDelete);

        }
    }
}


