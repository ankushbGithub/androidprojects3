package android.capsulepharmacy.com.vendor.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.listener.BankDetailsCardClick;
import android.capsulepharmacy.com.vendor.modal.BankDetails;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

public class BankDetailsAdapter extends RecyclerView.Adapter<BankDetailsAdapter.ItemRowHolder> {
    private ArrayList<BankDetails> bankDetailsArrayList;
    private Context mContext;
    private boolean onBind;
    private BankDetailsCardClick bankDetailsCardClick;

    public BankDetailsAdapter(Context context, ArrayList<BankDetails> bankDetailsArrayList,BankDetailsCardClick bankDetailsCardClick) {
        this.bankDetailsArrayList = bankDetailsArrayList;
        this.mContext = context;
        this.bankDetailsCardClick = bankDetailsCardClick;

    }

    @Override
    public BankDetailsAdapter.ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bank_details_item, null);
        BankDetailsAdapter.ItemRowHolder mh = new BankDetailsAdapter.ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final BankDetailsAdapter.ItemRowHolder itemRowHolder, final int i) {
        onBind = true;
        BankDetails bankDetails = bankDetailsArrayList.get(i);

        itemRowHolder.rbSelect.setChecked(bankDetails.isSelected());
        itemRowHolder.tvHolderName.setText(bankDetails.getAccountHolderName());
        itemRowHolder.tvAccNum.setText("Account Number : "+bankDetails.getAccountNumber());
        itemRowHolder.tvBranchName.setText("Branch Name : "+bankDetails.getBranchName());
        itemRowHolder.tvIFSCcODE.setText("IFSC Code : "+bankDetails.getIFSCCode());

        itemRowHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankDetailsCardClick.onBankCardClick(itemRowHolder.getAdapterPosition());
            }
        });
        itemRowHolder.rbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!onBind) {
                    for (BankDetails bankDetails1 : bankDetailsArrayList) {
                        bankDetails1.setSelected(false);
                    }
                    bankDetails.setSelected(true);
                    bankDetailsArrayList.set(itemRowHolder.getAdapterPosition(), bankDetails);
                    notifyDataSetChanged();
                }
            }
        });
        onBind = false;

    }

    @Override
    public int getItemCount() {
        return (null != bankDetailsArrayList ? bankDetailsArrayList.size() : 0);
    }

    /**
     * The type Item row holder.
     */
    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView tvHolderName, tvAccNum, tvBranchName, tvIFSCcODE;
        private RadioButton rbSelect;
        private View item;

        /**
         * Instantiates a new Item row holder.
         *
         * @param view the view
         */
        public ItemRowHolder(View view) {
            super(view);
            item = itemView;
            this.tvHolderName = (TextView) view.findViewById(R.id.tvHolderName);
            this.tvAccNum = (TextView) view.findViewById(R.id.tvAccNum);
            this.tvBranchName = (TextView) view.findViewById(R.id.tvBranchName);
            this.tvIFSCcODE = (TextView) view.findViewById(R.id.tvIFSCcODE);
            this.rbSelect = (RadioButton) view.findViewById(R.id.rbSelect);


        }

    }
}

