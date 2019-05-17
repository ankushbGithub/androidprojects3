package android.capsulepharmacy.com.modal;

import android.os.Parcel;
import android.os.Parcelable;

public class FilterModel implements Parcelable {
    public String title;
    public String code;
    public boolean isSelected;


    public FilterModel() {


    }


    protected FilterModel(Parcel in) {
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

    public static final Creator<FilterModel> CREATOR = new Creator<FilterModel>() {
        @Override
        public FilterModel createFromParcel(Parcel in) {
            return new FilterModel(in);
        }

        @Override
        public FilterModel[] newArray(int size) {
            return new FilterModel[size];
        }
    };


}
