package android.capsulepharmacy.com.vendor.singleton;

import android.capsulepharmacy.com.vendor.modal.AddressModal;

import java.util.ArrayList;

public class AddressSingleton {
    private static AddressSingleton mInstance;
    private ArrayList<AddressModal> list = null;

    public static AddressSingleton getInstance() {
        if (mInstance == null)
            mInstance = new AddressSingleton();

        return mInstance;
    }

    private AddressSingleton() {
        list = new ArrayList<AddressModal>();
    }

    // retrieve array from anywhere
    public ArrayList<AddressModal> getArray() {
        return this.list;
    }

    //Add element to array
    public void addToArray(AddressModal value) {
        list.add(value);
    }

    public void clearArrayList(){
        list.clear();
    }
}
