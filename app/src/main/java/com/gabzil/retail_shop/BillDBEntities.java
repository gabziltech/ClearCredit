package com.gabzil.retail_shop;

/**
 * Created by Yogesh on 01-Jun-16.
 */
public class BillDBEntities {
    //private variables
    int BillID;
    int ShopID;
    String ShopName;
    int CustomerID;
    String CustomerName;
    int UserID;
    Double BillAmount;
    Double PendingAmount;
    String Remark;
    Boolean IsPaid;
    Double PaidAmount;
    String ImagePath;
    String ImageUrl;
    String MobileNo;
    String NoOfItems;
    String EntryDate;
    String UserDate;

    public String getUserDate() {
        return UserDate;
    }

    public void setUserDate(String userDate) {
        UserDate = userDate;
    }



    // Empty constructor
    public BillDBEntities(){

    }

    // constructor
    public BillDBEntities(int BillID,int ShopID, String ShopName, int CustomerID, String CustomerName, int UserID, Double BillAmount, Double PendingAmount, String Remark
            , Boolean IsPaid, Double PaidAmount, String ImagePath, String ImageUrl, String MobileNo, String NoOfItems,String UserDate){
        this.BillID = BillID;
        this.ShopID = ShopID;
        this.ShopName = ShopName;
        this.CustomerID = CustomerID;
        this.CustomerName = CustomerName;
        this.UserID = UserID;
        this.BillAmount = BillAmount;
        this.PendingAmount = PendingAmount;
        this.Remark = Remark;
        this.IsPaid = IsPaid;
        this.PaidAmount = PaidAmount;
        this.ImagePath = ImagePath;
        this.ImageUrl = ImageUrl;
        this.MobileNo = MobileNo;
        this.NoOfItems = NoOfItems;
        this.UserDate= UserDate;
    }

    public int getBillID() {
        return BillID;
    }

    public void setBillID(int billID) {
        BillID = billID;
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

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public Double getBillAmount() {
        return BillAmount;
    }

    public void setBillAmount(Double billAmount) {
        BillAmount = billAmount;
    }

    public Double getPendingAmount() {
        return PendingAmount;
    }

    public void setPendingAmount(Double pendingAmount) {
        PendingAmount = pendingAmount;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public Boolean getIsPaid() {
        return IsPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        IsPaid = isPaid;
    }

    public Double getPaidAmount() {
        return PaidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        PaidAmount = paidAmount;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getNoOfItems() {
        return NoOfItems;
    }

    public void setNoOfItems(String noOfItems) {
        NoOfItems = noOfItems;
    }

    public String getEntryDate() {
        return EntryDate;
    }

    public void setEntryDate(String entryDate) {
        EntryDate = entryDate;
    }

}