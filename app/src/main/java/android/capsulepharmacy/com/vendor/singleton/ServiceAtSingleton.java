package android.capsulepharmacy.com.vendor.singleton;

import android.capsulepharmacy.com.vendor.modal.ServiceAtModal;

import java.util.ArrayList;

public class ServiceAtSingleton {
    private static ServiceAtSingleton mInstance;
    private ArrayList<ServiceAtModal> list = null;

    public static ServiceAtSingleton getInstance() {
        if (mInstance == null)
            mInstance = new ServiceAtSingleton();

        return mInstance;
    }

    private ServiceAtSingleton() {
        list = new ArrayList<ServiceAtModal>();
    }

    // retrieve array from anywhere
    public ArrayList<ServiceAtModal> getArray() {
        return this.list;
    }

    //Add element to array
    public void addToArray(ServiceAtModal value) {
        list.add(value);
    }

    public void clearArrayList(){
        list.clear();
    }
}
