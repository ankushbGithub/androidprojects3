package android.capsulepharmacy.com.fragment;

import android.annotation.SuppressLint;

import android.capsulepharmacy.com.R;
import android.capsulepharmacy.com.utility.RoundedTransformation;
import android.capsulepharmacy.com.utility.Utility;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;


/**
 * Created by ankush.bansal on 05-04-2018.
 */

@SuppressLint("ValidFragment")
public  class CardDealsImages extends Fragment {

    private int position;
    private String strImage;

    // newInstance constructor for creating fragment with arguments
    public CardDealsImages newInstance(int position, String s) {
        CardDealsImages fragmentFirst = new CardDealsImages();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("image", s);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_images, container, false);

        RoundedTransformation image = (RoundedTransformation) view.findViewById(R.id.image);

        position = getArguments().getInt("position");
        strImage = getArguments().getString("image");
        if (Utility.validateURL(strImage))
        Picasso.get().load(strImage).fit().into(image);




            return view;

    }


}








