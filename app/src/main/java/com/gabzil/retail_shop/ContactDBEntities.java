package com.gabzil.retail_shop;

/**
 * Created by Yogesh on 01-Jun-16.
 */
public class ContactDBEntities {

    int ContactID;
    String MobileNo;

    // Empty constructor
    public ContactDBEntities(){

    }

    public ContactDBEntities(int ContactID, String MobileNo){
        this.ContactID = ContactID;
        this.MobileNo = MobileNo;
    }

    public ContactDBEntities(String MobileNo){
        this.MobileNo = MobileNo;
    }

    public int getContactID() {
        return ContactID;
    }

    public void setContactID(int contactID) {
        ContactID = contactID;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }
}