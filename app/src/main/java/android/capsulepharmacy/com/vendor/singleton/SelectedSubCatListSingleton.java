package android.capsulepharmacy.com.vendor.singleton;

import android.capsulepharmacy.com.vendor.modal.SelectedSubCatList;
import android.capsulepharmacy.com.vendor.modal.SubListingModal;

import java.util.ArrayList;

public class SelectedSubCatListSingleton {
    private static SelectedSubCatListSingleton mInstance;
    private ArrayList<SelectedSubCatList> list = null;

    public static SelectedSubCatListSingleton getInstance() {
        if (mInstance == null)
            mInstance = new SelectedSubCatListSingleton();

        return mInstance;
    }

    private SelectedSubCatListSingleton() {
        list = new ArrayList<SelectedSubCatList>();
    }

    // retrieve array from anywhere
    public ArrayList<SelectedSubCatList> getArray() {
        return this.list;
    }

    //Add element to array
    public void addToArray(SelectedSubCatList value) {
        list.add(value);
    }

    public void clearArrayList(){
        list.clear();
    }
}

