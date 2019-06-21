package android.capsulepharmacy.com.modal;

/**
 * Created on 26-05-2019.
 */
public class BookSubCategory {
    /**
     * Id : 4
     * VendorId : 10043
     * SubCategoryId : 1
     * SubCategoryName : Cars
     * SubCategoryDescription : jsjsjswjej
     * SubCategoryPrice : 144.0
     */

    private int Id;
    private int VendorId;
    private int SubCategoryId;
    private String SubCategoryName;
    private String SubCategoryDescription;
    private String BookingNo;
    private double SubCategoryPrice;
    private int Qty;
    private double total;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getVendorId() {
        return VendorId;
    }

    public void setVendorId(int VendorId) {
        this.VendorId = VendorId;
    }

    public int getSubCategoryId() {
        return SubCategoryId;
    }

    public void setSubCategoryId(int SubCategoryId) {
        this.SubCategoryId = SubCategoryId;
    }

    public String getSubCategoryName() {
        return SubCategoryName;
    }

    public void setSubCategoryName(String SubCategoryName) {
        this.SubCategoryName = SubCategoryName;
    }

    public String getSubCategoryDescription() {
        return SubCategoryDescription;
    }

    public void setSubCategoryDescription(String SubCategoryDescription) {
        this.SubCategoryDescription = SubCategoryDescription;
    }

    public double getSubCategoryPrice() {
        return SubCategoryPrice;
    }

    public void setSubCategoryPrice(double SubCategoryPrice) {
        this.SubCategoryPrice = SubCategoryPrice;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }

    public String getBookingNo() {
        return BookingNo;
    }

    public void setBookingNo(String bookingNo) {
        BookingNo = bookingNo;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
