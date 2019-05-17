package android.capsulepharmacy.com.vendor.modal;

/**
 * Created on 15-08-2018.
 */

public class AddressModal {
    private int Id;
    private int VendorId;
    private String City;
    private String AreaOrStreet;
    private String FlatNoOrBuildingName;
    private String Postcode;
    private String State;
    private String Landmark;
    private String Name;
    private String MobileNo;
    private String AlternateMobileNo;
    private boolean isSelected;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getVendorId() {
        return VendorId;
    }

    public void setVendorId(int vendorId) {
        VendorId = vendorId;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getAreaOrStreet() {
        return AreaOrStreet;
    }

    public void setAreaOrStreet(String areaOrStreet) {
        AreaOrStreet = areaOrStreet;
    }

    public String getFlatNoOrBuildingName() {
        return FlatNoOrBuildingName;
    }

    public void setFlatNoOrBuildingName(String flatNoOrBuildingName) {
        FlatNoOrBuildingName = flatNoOrBuildingName;
    }

    public String getPostcode() {
        return Postcode;
    }

    public void setPostcode(String postcode) {
        Postcode = postcode;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getLandmark() {
        return Landmark;
    }

    public void setLandmark(String landmark) {
        Landmark = landmark;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getAlternateMobileNo() {
        return AlternateMobileNo;
    }

    public void setAlternateMobileNo(String alternateMobileNo) {
        AlternateMobileNo = alternateMobileNo;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
