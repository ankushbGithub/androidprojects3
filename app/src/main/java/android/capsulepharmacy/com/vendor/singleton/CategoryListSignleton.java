package android.capsulepharmacy.com.vendor.singleton;

import android.capsulepharmacy.com.vendor.modal.CategoryListModal;

import java.util.ArrayList;

public class CategoryListSignleton {
    private static CategoryListSignleton mInstance;
    private ArrayList<CategoryListModal> list = null;

    public static CategoryListSignleton getInstance() {
        if (mInstance == null)
            mInstance = new CategoryListSignleton();

        return mInstance;
    }

    private CategoryListSignleton() {
        list = new ArrayList<CategoryListModal>();
    }

    // retrieve array from anywhere
    public ArrayList<CategoryListModal> getArray() {
        return this.list;
    }

    //Add element to array
    public void addToArray(CategoryListModal value) {
        list.add(value);
    }

    public void clearArrayList(){
        list.clear();
    }
}
