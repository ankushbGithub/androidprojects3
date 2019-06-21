package android.capsulepharmacy.com.modal;

import java.io.Serializable;

/**
 * Created on 11-08-2018.
 */

public class MyOrderModal  implements Serializable {


    /**
     * CustomerId : 1
     * CustomerName : Nikki
     * VendorId : 1
     * VendorName : Niteesh Kumar
     * BookingNumber : B000000001
     * BookingDate : 19-03-2019
     * ServiceAtId : 0
     * CategoryId : 1
     * CategoryName : Tour & Travels
     * SubCategoryId : 1
     * SubCategoryName : Car
     * ServiceAt :
     * PayLaterAmt : 0
     * BookingAmt : 500
     * Status : 0
     */

    private int CustomerId;
    private String CustomerName;
    private int VendorId;
    private String VendorName;
    private String BookingNumber;
    private String BookingDate;
    private int ServiceAtId;
    private int CategoryId;
    private String CategoryName;
    private int SubCategoryId;
    private String SubCategoryName;
    private String ServiceAt;
    private double PayLaterAmt;
    private double BookingAmt;
    private int Status;
    private double rating;
    public double Rating;
    private double Price;
    private int Members;
    private int UsedPoints;
    private double PointsPerValue;
    private int PaymentGatewayStatusCode;
    private String PaymentGatewayStatusDescription;
    private String PaymentGatewayRemarks;

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int CustomerId) {
        this.CustomerId = CustomerId;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String CustomerName) {
        this.CustomerName = CustomerName;
    }

    public int getVendorId() {
        return VendorId;
    }

    public void setVendorId(int VendorId) {
        this.VendorId = VendorId;
    }

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String VendorName) {
        this.VendorName = VendorName;
    }

    public String getBookingNumber() {
        return BookingNumber;
    }

    public void setBookingNumber(String BookingNumber) {
        this.BookingNumber = BookingNumber;
    }

    public String getBookingDate() {
        return BookingDate;
    }

    public void setBookingDate(String BookingDate) {
        this.BookingDate = BookingDate;
    }

    public int getServiceAtId() {
        return ServiceAtId;
    }

    public void setServiceAtId(int ServiceAtId) {
        this.ServiceAtId = ServiceAtId;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int CategoryId) {
        this.CategoryId = CategoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String CategoryName) {
        this.CategoryName = CategoryName;
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

    public String getServiceAt() {
        return ServiceAt;
    }

    public void setServiceAt(String ServiceAt) {
        this.ServiceAt = ServiceAt;
    }

    public double getPayLaterAmt() {
        return PayLaterAmt;
    }

    public void setPayLaterAmt(int PayLaterAmt) {
        this.PayLaterAmt = PayLaterAmt;
    }

    public double getBookingAmt() {
        return BookingAmt;
    }

    public void setBookingAmt(int BookingAmt) {
        this.BookingAmt = BookingAmt;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public void setPayLaterAmt(double payLaterAmt) {
        PayLaterAmt = payLaterAmt;
    }

    public void setBookingAmt(double bookingAmt) {
        BookingAmt = bookingAmt;
    }

    public int getMembers() {
        return Members;
    }

    public void setMembers(int members) {
        Members = members;
    }

    public int getUsedPoints() {
        return UsedPoints;
    }

    public void setUsedPoints(int usedPoints) {
        UsedPoints = usedPoints;
    }

    public double getPointsPerValue() {
        return PointsPerValue;
    }

    public void setPointsPerValue(double pointsPerValue) {
        PointsPerValue = pointsPerValue;
    }

    public int getPaymentGatewayStatusCode() {
        return PaymentGatewayStatusCode;
    }

    public void setPaymentGatewayStatusCode(int paymentGatewayStatusCode) {
        PaymentGatewayStatusCode = paymentGatewayStatusCode;
    }

    public String getPaymentGatewayStatusDescription() {
        return PaymentGatewayStatusDescription;
    }

    public void setPaymentGatewayStatusDescription(String paymentGatewayStatusDescription) {
        PaymentGatewayStatusDescription = paymentGatewayStatusDescription;
    }

    public String getPaymentGatewayRemarks() {
        return PaymentGatewayRemarks;
    }

    public void setPaymentGatewayRemarks(String paymentGatewayRemarks) {
        PaymentGatewayRemarks = paymentGatewayRemarks;
    }
}
