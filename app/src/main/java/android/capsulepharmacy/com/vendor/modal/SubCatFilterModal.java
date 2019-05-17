package android.capsulepharmacy.com.vendor.modal;

import android.os.Parcel;
import android.os.Parcelable;

public class SubCatFilterModal implements Parcelable {
    public String title;
    public String code;
    public boolean isSelected;


    public SubCatFilterModal() {


    }


    protected SubCatFilterModal(Parcel in) {
        title = in.readString();
        code = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(code);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubCatFilterModal> CREATOR = new Creator<SubCatFilterModal>() {
        @Override
        public SubCatFilterModal createFromParcel(Parcel in) {
            return new SubCatFilterModal(in);
        }

        @Override
        public SubCatFilterModal[] newArray(int size) {
            return new SubCatFilterModal[size];
        }
    };


}


