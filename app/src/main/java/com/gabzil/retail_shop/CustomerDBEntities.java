package com.gabzil.retail_shop;

import java.util.ArrayList;

/**
 * Created by Yogesh on 01-Jun-16.
 */
public class CustomerDBEntities {

    int ShopID;
    String ShopName;
    int CustomerID;
    String CustomerName;
    String Address;
    String Building;
    String Area;
    String City;
    String Amount;
    String OutStanding;
    String CreditDays;
    String IsActive;
    ArrayList<ContactDBEntities> AllContact;
    String CustEntryDate;

    // Empty constructor
    public CustomerDBEntities(){

    }

    // constructor
    public CustomerDBEntities(int shopid,String shopname,int CustomerID, String CustomerName, String Address, String Building, String Area
            , String City,String Amount,String OutStanding,String CreditDays,String IsActive,ArrayList<ContactDBEntities> Contacts
            ,String CustEntryDate){
        this.ShopID = ShopID;
        this.ShopName = ShopName;
        this.CustomerID = CustomerID;
        this.CustomerName = CustomerName;
        this.Address = Address;
        this.Building = Building;
        this.Area = Area;
        this.City = City;
        this.Amount = Amount;
        this.OutStanding = OutStanding;
        this.CreditDays = CreditDays;
        this.IsActive = IsActive;
        this.AllContact = Contacts;
        this.CustEntryDate = CustEntryDate;
    }

    // constructor
    public CustomerDBEntities(int shopid,String shopname,String CustomerName, String Address, String Building, String Area
            , String City,String Amount,String OutStanding,String CreditDays,String IsActive,ArrayList<ContactDBEntities> Contacts
            ,String CustEntryDate){
        this.ShopID = ShopID;
        this.ShopName = ShopName;
        this.CustomerName = CustomerName;
        this.Address = Address;
        this.Building = Building;
        this.Area = Area;
        this.City = City;
        this.Amount = Amount;
        this.OutStanding = OutStanding;
        this.CreditDays = CreditDays;
        this.IsActive = IsActive;
        this.AllContact = Contacts;
        this.CustEntryDate = CustEntryDate;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getBuilding() {
        return Building;
    }

    public void setBuilding(String building) {
        Building = building;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getOutStanding() {
        return OutStanding;
    }

    public void setOutStanding(String outStanding) {
        OutStanding = outStanding;
    }

    public String getCreditDays() {
        return CreditDays;
    }

    public void setCreditDays(String creditDays) {
        CreditDays = creditDays;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    public ArrayList<ContactDBEntities> getAllContact() {
        return AllContact;
    }

    public void setAllContact(ArrayList<ContactDBEntities> allContact) {
        AllContact = allContact;
    }

    public String getCustEntryDate() {
        return CustEntryDate;
    }

    public void setCustEntryDate(String custEntryDate) {
        CustEntryDate = custEntryDate;
    }
}