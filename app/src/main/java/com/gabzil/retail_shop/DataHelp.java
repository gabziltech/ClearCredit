package com.gabzil.retail_shop;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataHelp {
    SQLiteDatabase db;
    Context context;
    private MyOpenHelper db1;
    List<ShopDBEntities> shop1;
    List<CustomerDBEntities> customer1;
    List<UserDBEntities> Users;
    SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
    public DataHelp(Context con) {
        this.context = con;
        SQLiteOpenHelper myHelper = new MyOpenHelper(this.context);
        this.db = myHelper.getWritableDatabase();
        this.db1 = new MyOpenHelper(this.context);
    }

    public boolean PaymentSbmt(PaymentDBEntities payment) {
        try {
            ContentValues conV = new ContentValues();
            conV.put("ShopID", payment.getShopID());
            conV.put("ShopName", payment.getShopName());
            conV.put("CustomerID", payment.getCustomerID());
            conV.put("CustomerName", payment.getCustomerName());
            conV.put("MobileNo", payment.getMobileNo());
            conV.put("PayAmount", payment.getPayAmount());
            conV.put("PaymentMode", payment.getPaymentMode());
            conV.put("PaymentDate", payment.getPaymentDate());
            String paydate= format.format(new Date());
            if(paydate.contains("."))
                paydate=paydate.replace(".", "");
            conV.put("UserDate", paydate);
            db.insert(MyOpenHelper.TABLE_NAME5, null, conV);
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
    }

    public boolean BillSbmt(BillDBEntities bill) {
        try {
            ContentValues conV = new ContentValues();
            conV.put("ShopID", bill.getShopID());
            conV.put("ShopName", bill.getShopName());
            conV.put("CustomerID", bill.getCustomerID());
            conV.put("CustomerName", bill.getCustomerName());
            conV.put("UserID", bill.getUserID());
            conV.put("BillAmount", bill.getBillAmount());
            conV.put("PendingAmount", bill.getPendingAmount());
            conV.put("Remark", bill.getRemark());
            conV.put("IsPaid", String.valueOf(bill.getIsPaid()));
            conV.put("PaidAmount", bill.getPaidAmount());
            conV.put("BillPhoto", bill.getImagePath());
            conV.put("MobileNo", bill.getMobileNo());
            conV.put("NoOfItems", bill.getNoOfItems());
            String billdate= format.format(new Date());
            if(billdate.contains("."))
                billdate = billdate.replace(".", "");
            conV.put("UserDate", billdate);
            db.insert(MyOpenHelper.TABLE_NAME4, null, conV);
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
    }

    public void DeleteBills(int ID) {
        try {
            ContentValues conV = new ContentValues();
            String where = "BillID=" + ID;
            db.delete(MyOpenHelper.TABLE_NAME4, where, null);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void DeletePayments(int ID) {
        try {
            ContentValues conV = new ContentValues();
            String where = "PaymentID=" + ID;
            db.delete(MyOpenHelper.TABLE_NAME5, where, null);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public boolean CustomerSbmt(CustomerDBEntities customer) {
        try {
            ContentValues conV = new ContentValues();
            conV.put("ShopID", customer.getShopID());
            conV.put("ShopName", customer.getShopName());
            conV.put("CustomerID", customer.getCustomerID());
            conV.put("CustName", customer.getCustomerName());
            conV.put("Address", customer.getAddress());
            conV.put("Building", customer.getBuilding());
            conV.put("Area", customer.getArea());
            conV.put("City", customer.getCity());
            conV.put("Amount", customer.getAmount());
            conV.put("CreditDays", customer.getCreditDays());
            conV.put("IsActive", customer.getIsActive());

            CustomerDBEntities Customer = db1.getByCustomerID(customer.getCustomerID());
            if (Customer == null) {
                if (customer.getOutStanding() == null)
                    conV.put("OutStanding", 0.0);
                else
                    conV.put("OutStanding", customer.getOutStanding());

                SimpleDateFormat format = new SimpleDateFormat("dd M yyyy");
                String currentDateTimeString = format.format(new Date());
                conV.put("CustEntryDate", currentDateTimeString);
                db.insert(MyOpenHelper.TABLE_NAME, null, conV);
            } else {
                if (customer.getOutStanding() == null)
                    if (Customer.getOutStanding() != null)
                        conV.put("OutStanding", Customer.getOutStanding());
                    else
                        conV.put("OutStanding", 0.0);
                else
                    conV.put("OutStanding", customer.getOutStanding());

                if (!(Customer.getCreditDays().equals(customer.getCreditDays()))) {
                    SimpleDateFormat format = new SimpleDateFormat("dd M yyyy");
                    String currentDateTimeString = format.format(new Date());
                    conV.put("CustEntryDate", currentDateTimeString);
                }
                String where = "CustomerID=" + customer.getCustomerID();
                db.update(MyOpenHelper.TABLE_NAME, conV, where, null);
            }
            return (putContact(customer));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean putContact(CustomerDBEntities customer) {
        ArrayList<ContactDBEntities> contact = customer.getAllContact();
        try {
            for (int i = 0; i < contact.size(); i++) {
                ContentValues conV = new ContentValues();
                conV.put("ShopID", customer.getShopID());
                conV.put("CustomerID", customer.getCustomerID());
                conV.put("ContactID", contact.get(i).getContactID());
                conV.put("ContactNo", contact.get(i).getMobileNo());

                ContactDBEntities Contact = db1.getByContact(contact.get(i).ContactID);
                if (Contact == null) {
                    db.insert(MyOpenHelper.TABLE_NAME3, null, conV);
                } else {
                    String where = "ContactID=" + contact.get(i).getContactID();
                    db.update(MyOpenHelper.TABLE_NAME3, conV, where, null);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean ShopSbmt(ShopDBEntities shop) {
        try {
            ContentValues conV = new ContentValues();
            conV.put("ShopID", shop.getShopID());
            conV.put("ShopName", shop.getShopName());
            conV.put("Address", shop.getAddress());
            conV.put("City", shop.getCity());
            conV.put("Pincode", shop.getPincode());
            conV.put("UserID", shop.getUserID());

            shop1 = db1.getAllShops();
            if (shop1.size() <= 0) {
                db.insert(MyOpenHelper.TABLE_NAME1, null, conV);
            } else {
                String where = "ShopID=" + shop.getShopID();
                db.update(MyOpenHelper.TABLE_NAME1, conV, where, null);
            }
            UserSbmt(shop);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean UserSbmt(ShopDBEntities shop) {
        ArrayList<UserDBEntities> users = shop.getAllUser();
        try {
            for (int i = 0; i < users.size(); i++) {
                ContentValues conV = new ContentValues();
                conV.put("UserID", users.get(i).getUserID());
                conV.put("UserName", users.get(i).getUserName());
                conV.put("MobileNo", users.get(i).getMobileNo());
                conV.put("UserType", users.get(i).getUserType());
                conV.put("ShopID", shop.getShopID());
                UserDBEntities Users1 = db1.getByUserID(users.get(i).getUserID());
                if (Users1 == null) {
                    db.insert(MyOpenHelper.TABLE_NAME2, null, conV);
                } else {
                    String where = " UserID = " + users.get(i).getUserID();
                    db.update(MyOpenHelper.TABLE_NAME2, conV, where, null);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean CustomerDelete(int custid) {
        try {
            String where = "CustomerID=" + custid;
            db.delete(MyOpenHelper.TABLE_NAME, where, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean UserDelete(ShopDBEntities shop) {
        ArrayList<UserDBEntities> user = shop.getAllUser();
        try {
            String where = "UserID=" + user.get(0).getUserID();
            db.delete(MyOpenHelper.TABLE_NAME2, where, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean ContactDelete(CustomerDBEntities customer) {
        ArrayList<ContactDBEntities> contact = customer.getAllContact();
        try {
            String where = "ContactID=" + contact.get(0).getContactID();
            db.delete(MyOpenHelper.TABLE_NAME3, where, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void UpdateDate(String syncDate,int id){
        ContentValues conV = new ContentValues();
        conV.put("SyncDate", syncDate);
        if (id == 0)
            db.insert(MyOpenHelper.TABLE_NAME6, null, conV);
        else {
            String where = "Id = " +id;
            db.update(MyOpenHelper.TABLE_NAME6, conV, where, null);
        }
    }

    public void dbClose(SQLiteDatabase db) {
        db.close();
    }
}
