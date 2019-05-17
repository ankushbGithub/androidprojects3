package android.capsulepharmacy.com.adapter;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.modal.InstructionModal;
import android.content.Context;
import android.graphics.Color;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.SingleItemRowHolder> {
    private ArrayList<InstructionModal> ChapterModalArrayList;
    private Context mContext;

    /**
     * Instantiates a new Product main section items adapter.
     *
     * @param context                   the context
     * @param ChapterModalArrayList the product item modal array list
     */
    public InstructionsAdapter(Context context, ArrayList<InstructionModal> ChapterModalArrayList) {
        this.mContext = context;
        this.ChapterModalArrayList = ChapterModalArrayList;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.instruction_layout, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int i) {
        final InstructionModal singleItem = ChapterModalArrayList.get(i);


        holder.textViewProductName.setText(singleItem.getTitle());

        holder.textViewProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != ChapterModalArrayList ? ChapterModalArrayList.size() : 0);
    }

    /**
     * The type Single item row holder.
     */
    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        /**
         * The Text view product name.
         */
        protected TextView textViewProductName;
        public View view1;

        /**
         * The Image view product.
         */



        /**
         * Instantiates a new Single item row holder.
         *
         * @param view the view
         */
        public SingleItemRowHolder(View view) {
            super(view);
            view1=itemView;
            this.textViewProductName = (TextView) view.findViewById(R.id.textViewProductName);





        }

    }
}
