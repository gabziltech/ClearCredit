package com.gabzil.retail_shop;

import java.util.ArrayList;

/**
 * Created by Yogesh on 01-Jun-16.
 */
public class ShopDBEntities {

    //private variables
    int ShopID;
    String ShopName;
    String Address;
    String City;
    String Pincode;
    int UserID;
    ArrayList<UserDBEntities> AllUser;

    // Empty constructor
    public ShopDBEntities(){

    }

    // constructor
    public ShopDBEntities(int ShopID, String ShopName, String Address, String City, String Pincode,int UserID
            ,ArrayList<UserDBEntities> Users){
        this.ShopID = ShopID;
        this.ShopName = ShopName;
        this.Address = Address;
        this.City = City;
        this.Pincode = Pincode;
        this.UserID = UserID;
        this.AllUser = Users;
    }

    // constructor
    public ShopDBEntities(String ShopName, String Address, String City, String Pincode,int UserID
            ,ArrayList<UserDBEntities> Users){
        this.ShopName = ShopName;
        this.Address = Address;
        this.City = City;
        this.Pincode = Pincode;
        this.UserID = UserID;
        this.AllUser = Users;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getPincode() {
        return Pincode;
    }

    public void setPincode(String pincode) {
        Pincode = pincode;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public ArrayList<UserDBEntities> getAllUser() {
        return AllUser;
    }

    public void setAllUser(ArrayList<UserDBEntities> allUser) {
        AllUser = allUser;
    }
}