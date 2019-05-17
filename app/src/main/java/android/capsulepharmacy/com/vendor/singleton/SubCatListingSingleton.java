package android.capsulepharmacy.com.vendor.singleton;

import android.capsulepharmacy.com.vendor.modal.ServiceAtModal;
import android.capsulepharmacy.com.vendor.modal.SubListingModal;

import java.util.ArrayList;

public class SubCatListingSingleton {
    private static SubCatListingSingleton mInstance;
    private ArrayList<SubListingModal> list = null;

    public static SubCatListingSingleton getInstance() {
        if (mInstance == null)
            mInstance = new SubCatListingSingleton();

        return mInstance;
    }

    private SubCatListingSingleton() {
        list = new ArrayList<SubListingModal>();
    }

    // retrieve array from anywhere
    public ArrayList<SubListingModal> getArray() {
        return this.list;
    }

    //Add element to array
    public void addToArray(SubListingModal value) {
        list.add(value);
    }

    public void clearArrayList(){
        list.clear();
    }
}
