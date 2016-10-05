package com.gabzil.retail_shop;

/**
 * Created by Yogesh on 01-Jun-16.
 */
public class PaymentDBEntities {
    int PaymentID;
    int ShopID;
    String ShopName;
    int CustomerID;
    String CustomerName;
    String MobileNo;
    Double PayAmount;
    String PaymentMode;
    String PaymentDate;
    String EntryDate;
    String UserDate;

    public String getUserDate() {
        return UserDate;
    }

    public void setUserDate(String userDate) {
        UserDate = userDate;
    }



    // Empty constructor
    public PaymentDBEntities(){

    }
    // constructor
    public PaymentDBEntities(int PaymentID, int ShopID, String ShopName, int CustomerID, String CustomerName, String MobileNo,
                             Double PayAmount, String PaymentMode, String PaymentDate, String UserDate){
        this.PaymentID = PaymentID;
        this.ShopID = ShopID;
        this.ShopName = ShopName;
        this.CustomerID = CustomerID;
        this.CustomerName = CustomerName;
        this.MobileNo = MobileNo;
        this.PayAmount = PayAmount;
        this.PaymentMode = PaymentMode;
        this.PaymentDate = PaymentDate;
        this.UserDate = UserDate;
    }

    public int getPaymentID() {
        return PaymentID;
    }

    public void setPaymentID(int paymentID) {
        PaymentID = paymentID;
    }

    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public Double getPayAmount() {
        return PayAmount;
    }

    public void setPayAmount(Double payAmount) {
        PayAmount = payAmount;
    }

    public String getPaymentMode() {
        return PaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        PaymentMode = paymentMode;
    }

    public String getPaymentDate() {
        return PaymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        PaymentDate = paymentDate;
    }

    public String getEntryDate() {
        return EntryDate;
    }

    public void setEntryDate(String entryDate) {
        EntryDate = entryDate;
    }
}