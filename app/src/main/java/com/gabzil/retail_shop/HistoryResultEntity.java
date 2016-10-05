package com.gabzil.retail_shop;

import java.util.Date;

/**
 * Created by javed on 8/10/2016.
 */
public class HistoryResultEntity {

    private String Type;
    private int BillID;
    private int PaymentID;
    private int CustomerID;
    private int ShopID;
    private int UserID;
    private String ShopName;
    private String CustomerName;
    private String MobileNo;
    private String NoOfItems;
    private String Remark;
    private boolean IsPaid;
    private boolean IsBill;
    private Double BillAmount;
    private Double PendingAmount;
    private Double PaidAmount;
    private String EntryDate;
    private String ImageUrl;
    private String PaymentMode;
    private Double PayAmount;
    private Double ExtraAmount;
    private Date FromDate;
    private Date ToDate;
    private String UserDate;

    public String getUserDate() {
        return UserDate;
    }

    public void setUserDate(String userDate) {
        UserDate = userDate;
    }


    //private boolean IsBill;
    public int getBillID() {
        return BillID;
    }

    public void setBillID(int billID) {
        BillID = billID;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
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

    public String getNoOfItems() {
        return NoOfItems;
    }

    public void setNoOfItems(String noOfItems) {
        NoOfItems = noOfItems;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public boolean getIsPaid() {
        return IsPaid;
    }

    public void setIsPaid(boolean isPaid) {
        IsPaid = isPaid;
    }

    public Double getBillAmount() {
        return BillAmount;
    }

    public void setBillAmount(Double billAmount) {
        BillAmount = billAmount;
    }

    public Double getPaidAmount() {
        return PaidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        PaidAmount = paidAmount;
    }

    public String getEntryDate() {
        return EntryDate;
    }

    public void setEntryDate(String entryDate) {
        EntryDate = entryDate;
    }

    public boolean getIsBill() {
        return IsBill;
    }

    public void setIsBill(boolean isBill) {
        IsBill = isBill;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public Date getFromDate() {
        return FromDate;
    }

    public void setFromDate(Date fromDate) {
        FromDate = fromDate;
    }

    public Date getToDate() {
        return ToDate;
    }

    public void setToDate(Date toDate) {
        ToDate = toDate;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getPaymentMode() {
        return PaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        PaymentMode = paymentMode;
    }

    public Double getPayAmount() {
        return PayAmount;
    }

    public void setPayAmount(Double payAmount) {
        PayAmount = payAmount;
    }

    public int getPaymentID() {
        return PaymentID;
    }

    public void setPaymentID(int paymentID) {
        PaymentID = paymentID;
    }

    public Double getPendingAmount() {
        return PendingAmount;
    }

    public void setPendingAmount(Double pendingAmount) {
        PendingAmount = pendingAmount;
    }

    public Double getExtraAmount() {
        return ExtraAmount;
    }


    public void setExtraAmount(Double extraAmount) {
        ExtraAmount = extraAmount;
    }
}
