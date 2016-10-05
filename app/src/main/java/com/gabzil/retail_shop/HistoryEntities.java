package com.gabzil.retail_shop;

/**
 * Created by Yogesh on 01-Jun-16.
 */
public class HistoryEntities {
    //private variables
    int ShopID;
    int CustomerID;
    String CustomerName;
    String Type;
    String FromDate;
    String ToDate;

    // Empty constructor
    public HistoryEntities(){

    }

    // constructor
    public HistoryEntities(int ShopID, int CustomerID, String CustomerName, String Type, String FromDate, String ToDate){
        this.ShopID = ShopID;
        this.CustomerID = CustomerID;
        this.CustomerName = CustomerName;
        this.Type = Type;
        this.FromDate = FromDate;
        this.ToDate = ToDate;
    }

    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getFromDate() {
        return FromDate;
    }

    public void setFromDate(String fromDate) {
        FromDate = fromDate;
    }

    public String getToDate() {
        return ToDate;
    }

    public void setToDate(String toDate) {
        ToDate = toDate;
    }
}