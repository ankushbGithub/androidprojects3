package android.capsulepharmacy.com.vendor.modal;

import android.os.Parcel;
import android.os.Parcelable;

public class ServiceFilterModal implements Parcelable {
    public String title;
    public String code;
    public boolean isSelected;


    public ServiceFilterModal() {


    }


    protected ServiceFilterModal(Parcel in) {
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

    public static final Creator<ServiceFilterModal> CREATOR = new Creator<ServiceFilterModal>() {
        @Override
        public ServiceFilterModal createFromParcel(Parcel in) {
            return new ServiceFilterModal(in);
        }

        @Override
        public ServiceFilterModal[] newArray(int size) {
            return new ServiceFilterModal[size];
        }
    };


}

