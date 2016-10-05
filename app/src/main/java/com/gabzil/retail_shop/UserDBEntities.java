package com.gabzil.retail_shop;

/**
 * Created by Yogesh on 01-Jun-16.
 */
public class UserDBEntities {

    //private variables
    int UserID;
    String UserName;
    String MobileNo;
    String UserType;
    int ShopID;
    boolean IsActive;

    // Empty constructor
    public UserDBEntities(){

    }
    // constructor
    public UserDBEntities(int UserID, String UserName, String MobileNo, int ShopID, boolean IsActive){
        this.UserID = UserID;
        this.UserName = UserName;
        this.MobileNo = MobileNo;
        this.ShopID = ShopID;
        this.IsActive = IsActive;
    }

    // constructor
    public UserDBEntities(String UserName, String MobileNo, int ShopID, boolean IsActive){
        this.UserName = UserName;
        this.MobileNo = MobileNo;
        this.ShopID = ShopID;
        this.IsActive = IsActive;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public int getShopID() {
        return ShopID;
    }

    public void setShopID(int shopID) {
        ShopID = shopID;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setIsActive(boolean isActive) {
        IsActive = isActive;
    }

}