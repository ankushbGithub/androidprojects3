package android.capsulepharmacy.com.vendor.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.listener.AddressCardClick;
import android.capsulepharmacy.com.vendor.modal.AddressModal;
import android.capsulepharmacy.com.utility.Utility;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;


public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.ItemRowHolder>{
    private ArrayList<AddressModal> homeModals;
    private Context mContext;
    private boolean onBind;
    private AddressCardClick addressCardClick;


    /**
     * Instantiates a new Product main section adapter.
     *
     * @param context              the context
     * @param AddressModals the product section modals
     */
    public DeliveryAddressAdapter(Context context, ArrayList<AddressModal> AddressModals, AddressCardClick addressCardClick) {
        this.homeModals = AddressModals;
        this.mContext = context;
        this.addressCardClick = addressCardClick;

    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.address_item, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder itemRowHolder, final int i) {
        onBind = true;
        AddressModal addressModal = homeModals.get(i);

        final String sectionName = addressModal.getName();

        itemRowHolder.rbSelect.setChecked(homeModals.get(i).isSelected());
        itemRowHolder.itemTitle.setText(sectionName);
        if (Utility.validateString(homeModals.get(i).getAlternateMobileNo()))
        itemRowHolder.mobile.setText(homeModals.get(i).getMobileNo()+", "+homeModals.get(i).getAlternateMobileNo());
        else
            itemRowHolder.mobile.setText(homeModals.get(i).getMobileNo());

        if (Utility.validateString(homeModals.get(i).getLandmark())){
            itemRowHolder.address.setText(homeModals.get(i).getFlatNoOrBuildingName()+", "+homeModals.get(i).getAreaOrStreet()+", "+homeModals.get(i).getLandmark()+", "+homeModals.get(i).getCity()+", "+homeModals.get(i).getState()+", "+homeModals.get(i).getPostcode());
        }else {
            itemRowHolder.address.setText(homeModals.get(i).getFlatNoOrBuildingName()+", "+homeModals.get(i).getAreaOrStreet()+", "+homeModals.get(i).getCity()+", "+homeModals.get(i).getState()+", "+homeModals.get(i).getPostcode());
        }

        itemRowHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressCardClick.onAddressCardClick(itemRowHolder.getAdapterPosition());
            }
        });
        itemRowHolder.rbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!onBind) {
                    for (AddressModal addressModal1 : homeModals) {
                        addressModal1.setSelected(false);
                    }
                    addressModal.setSelected(true);
                    homeModals.set(itemRowHolder.getAdapterPosition(), addressModal);
                    notifyDataSetChanged();
                }
            }
        });
        onBind = false;

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
        protected TextView itemTitle,mobile,address;
        private RadioButton rbSelect;
        /**
         * Instantiates a new Item row holder.
         *
         * @param view the view
         */
        public ItemRowHolder(View view) {
            super(view);
            item=itemView;
            this.itemTitle = (TextView) view.findViewById(R.id.textViewHeader);
            this.mobile=(TextView)view.findViewById(R.id.mobile);
            this.address=(TextView)view.findViewById(R.id.address);
            this.rbSelect= (RadioButton) view.findViewById(R.id.rbSelect);



        }

    }
}
