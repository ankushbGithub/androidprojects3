package android.capsulepharmacy.com.adapter;

import android.capsulepharmacy.com.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


/**
 * The type Spinner drop down adapter.
 */
public class SpinnerDropDownAdapter extends BaseAdapter implements
        SpinnerAdapter {
    /**
     * The Context.
     */
    Context context;
    /**
     * The Name.
     */
    String[] name;
    /**
     * The Is black.
     */
    boolean isBlack=false;
    /**
     * The Color bg.
     */
    int colorBG;
    /**
     * The Str color.
     */
    String  strColor;

    /**
     * Instantiates a new Spinner drop down adapter.
     *
     * @param ctx  the ctx
     * @param name the name
     */
    public SpinnerDropDownAdapter(Context ctx, String[] name ) {
        context = ctx;
        this.name = name;
    }

    /**
     * Set color.
     *
     * @param isBlack the is black
     */
    public void setColor(boolean isBlack){
        this.isBlack = isBlack;
    }

    /**
     * Set color bg.
     *
     * @param colorBG the color bg
     * @param color   the color
     */
    public void setColorBG(int colorBG, String color){
        this.colorBG = colorBG;
        this.strColor = color;
    }
//
//    String[] name = { " One", " Two", " Three", " Four", " Five", " Six",
//            " Seven", " Eight" };
//    String[] value = { " 1", " 2", " 3", " 4", " 5", " 6", " 7", " 8" };

    @Override
    public int getCount() {
        return name.length;
    }

    @Override
    public String getItem(int pos) {
        // TODO Auto-generated method stub
        return name[pos];
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        TextView text1 = view.findViewById(R.id.text1);
        text1.setText(name[position]);
        if(strColor!=null){
            text1.setBackgroundColor(context.getResources().getColor(colorBG));
        }
        if (!isBlack)
            text1.setTextColor(context.getResources().getColor(R.color.white));
        else
            text1.setTextColor(context.getResources().getColor(R.color.accent_color));
        parent.setPadding(0, 0, 0, 0);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        TextView text1 = view.findViewById(R.id.text1);
        text1.setText(name[position]);
        text1.setTextColor(context.getResources().getColor(R.color.accent_color));
        return view;
    }
}