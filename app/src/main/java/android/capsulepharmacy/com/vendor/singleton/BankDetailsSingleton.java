package android.capsulepharmacy.com.vendor.singleton;

import android.capsulepharmacy.com.vendor.modal.BankDetails;

import java.util.ArrayList;

public class BankDetailsSingleton {
    private static BankDetailsSingleton mInstance;
    private ArrayList<BankDetails> list = null;

    public static BankDetailsSingleton getInstance() {
        if (mInstance == null)
            mInstance = new BankDetailsSingleton();

        return mInstance;
    }

    private BankDetailsSingleton() {
        list = new ArrayList<BankDetails>();
    }

    // retrieve array from anywhere
    public ArrayList<BankDetails> getArray() {
        return this.list;
    }

    //Add element to array
    public void addToArray(BankDetails value) {
        list.add(value);
    }

    public void clearArrayList(){
        list.clear();
    }
}
