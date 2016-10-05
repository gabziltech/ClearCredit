package com.gabzil.retail_shop;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MyOpenHelper extends SQLiteOpenHelper
{
	SQLiteDatabase db;

	private static final String DATABASE_NAME="RetailerDataBase";
	private static final int DATABASE_VERSION= 1;
	public static final String TABLE_NAME="CustomerDetails";
	public static final String TABLE_NAME1="ShopsDetails";
	public static final String TABLE_NAME2="UsersDetails";
	public static final String TABLE_NAME3="ContactDetails";
	public static final String TABLE_NAME4="BillDetails";
	public static final String TABLE_NAME5="PaymentDetails";
	public static final String TABLE_NAME6="DateDetails";

    private static final String DB_COLUMN_1_NAME = "CustName";

	public MyOpenHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE CustomerDetails (CustomerTableID Integer PRIMARY KEY AUTOINCREMENT,ShopID Integer,ShopName TEXT," +
				"CustomerID Integer,CustName TEXT,Address TEXT,Building TEXT,Area TEXT,City TEXT,Amount TEXT,CreditDays TEXT," +
				"OutStanding TEXT,IsActive TEXT,CustEntryDate TEXT)");

		db.execSQL("CREATE TABLE ShopsDetails (ShopTableID Integer PRIMARY KEY AUTOINCREMENT,ShopID Integer,ShopName TEXT,Address TEXT,City TEXT,Pincode TEXT,UserID Integer)");

		db.execSQL("CREATE TABLE UsersDetails (UserTableID Integer PRIMARY KEY AUTOINCREMENT,UserID Integer,UserName TEXT,MobileNo TEXT,UserType TEXT,ShopID Integer)");

		db.execSQL("CREATE TABLE ContactDetails (ContactTableID Integer PRIMARY KEY AUTOINCREMENT,ContactID Integer,ShopID Integer,CustomerID Integer,ContactNo TEXT)");

		db.execSQL("CREATE TABLE BillDetails (BillID Integer PRIMARY KEY AUTOINCREMENT,ShopID Integer,ShopName TEXT,CustomerID Integer,CustomerName TEXT,UserID Integer," +
				"BillAmount REAL,PendingAmount REAL,Remark TEXT,IsPaid TEXT,PaidAmount REAL,BillPhoto TEXT,MobileNo TEXT,NoOfItems TEXT,EntryDate TEXT,UserDate TEXT)");

		db.execSQL("CREATE TABLE PaymentDetails (PaymentID Integer PRIMARY KEY AUTOINCREMENT,ShopID Integer,ShopName TEXT,CustomerID Integer,CustomerName TEXT,MobileNo TEXT," +
				"PayAmount REAL,PaymentMode TEXT,PaymentDate TEXT,EntryDate TEXT,UserDate TEXT)");

		db.execSQL("CREATE TABLE DateDetails (ID Integer PRIMARY KEY AUTOINCREMENT,SyncDate TEXT)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion)
	{
		// TODO Auto-generated method stub

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME3);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME4);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME5);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME6);
		onCreate(db);
		System.out.println("On Upgrade Call");
	}

	public List<CustomerDBEntities> getAllCustomerInfo() {
		List<CustomerDBEntities> contactList = new ArrayList<CustomerDBEntities>();
		try {
			// Select All Query
			String str = String.valueOf(true);
			String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE IsActive LIKE '%true%'";

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					CustomerDBEntities contact = new CustomerDBEntities();
					contact.setShopID(Integer.parseInt(cursor.getString(1)));
					contact.setCustomerID(Integer.parseInt(cursor.getString(3)));
					contact.setCustomerName(cursor.getString(4));
					contact.setAddress(cursor.getString(5));
					contact.setBuilding(cursor.getString(6));
					contact.setArea(cursor.getString(7));
					contact.setCity(cursor.getString(8));
					contact.setAmount(cursor.getString(9));
					contact.setCreditDays(cursor.getString(10));
					contact.setOutStanding(cursor.getString(11));
					contact.setIsActive(cursor.getString(12));
					contact.setCustEntryDate(cursor.getString(13));
					// Adding contact to list
					contactList.add(contact);
				} while (cursor.moveToNext());
			}
		}
		catch (Exception e){
			e.getMessage();
		}

		// return contact list
		return contactList;
	}

	public List<PaymentDBEntities> getAllPaymentInfo() {
		List<PaymentDBEntities> paymentList = new ArrayList<PaymentDBEntities>();
		try {
			String selectQuery = "SELECT  * FROM " + TABLE_NAME5;

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					PaymentDBEntities payment = new PaymentDBEntities();
					payment.setPaymentID(Integer.parseInt(cursor.getString(0)));
					payment.setShopID(Integer.parseInt(cursor.getString(1)));
					payment.setShopName(cursor.getString(2));
					payment.setCustomerID(Integer.parseInt(cursor.getString(3)));
					payment.setCustomerName(cursor.getString(4));
					payment.setMobileNo(cursor.getString(5));
					payment.setPayAmount(Double.valueOf(cursor.getString(6)));
					payment.setPaymentMode(cursor.getString(7));
					payment.setPaymentDate(cursor.getString(8));
					payment.setUserDate(cursor.getString(10));
					// Adding contact to list
					paymentList.add(payment);
				} while (cursor.moveToNext());
			}
		}
		catch (Exception e){
			e.getMessage();
		}

		return paymentList;
	}

	public List<BillDBEntities> getAllBillInfo() {
		List<BillDBEntities> billList = new ArrayList<BillDBEntities>();
		try {
			String selectQuery = "SELECT * FROM " + TABLE_NAME4;

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					BillDBEntities bill = new BillDBEntities();
					bill.setBillID(Integer.parseInt(cursor.getString(0)));
					bill.setShopID(Integer.parseInt(cursor.getString(1)));
					bill.setShopName(cursor.getString(2));
					bill.setCustomerID(Integer.parseInt(cursor.getString(3)));
					bill.setCustomerName(cursor.getString(4));
					bill.setUserID(Integer.parseInt(cursor.getString(5)));
					bill.setBillAmount(Double.valueOf(cursor.getString(6)));
					bill.setPendingAmount(Double.valueOf(cursor.getString(7)));
					bill.setRemark(cursor.getString(8));
					bill.setIsPaid(Boolean.valueOf(cursor.getString(9)));
					bill.setPaidAmount(Double.valueOf(cursor.getString(10)));
					bill.setImagePath(cursor.getString(11));
					bill.setMobileNo(cursor.getString(12));
					bill.setNoOfItems(cursor.getString(13));
					bill.setUserDate(cursor.getString(15));
					// Adding contact to list
					billList.add(bill);
				} while (cursor.moveToNext());
			}
		}
		catch (Exception e){
			e.getMessage();
		}

		// return contact list
		return billList;
	}

	public List<CustomerDBEntities> getCustomerInfo(int CustID) {
		List<CustomerDBEntities> contactList = new ArrayList<CustomerDBEntities>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME + " where CustomerID = "+ CustID;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				CustomerDBEntities contact = new CustomerDBEntities();
				contact.setCustomerID(Integer.parseInt(cursor.getString(3)));
				contact.setCustomerName(cursor.getString(4));
				contact.setAddress(cursor.getString(5));
				contact.setBuilding(cursor.getString(6));
				contact.setArea(cursor.getString(7));
				contact.setCity(cursor.getString(8));
				contact.setAmount(cursor.getString(9));
				contact.setCreditDays(cursor.getString(10));
//				contact.setCell1(cursor.getString(11));
				// Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToNext());
		}

		// return contact list
		return contactList;
	}

	public CustomerDBEntities getByCustomerID(int CustomerID) {

		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME + " where IsActive LIKE '%true%' AND CustomerID= "+ CustomerID;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		CustomerDBEntities customer = null;
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				customer = new CustomerDBEntities();
				customer.setShopID(Integer.parseInt(cursor.getString(1)));
				customer.setShopName(cursor.getString(2));
				customer.setCustomerID(Integer.parseInt(cursor.getString(3)));
				customer.setCustomerName(cursor.getString(4));
				customer.setAddress(cursor.getString(5));
				customer.setBuilding(cursor.getString(6));
				customer.setArea(cursor.getString(7));
				customer.setCity(cursor.getString(8));
				customer.setAmount(cursor.getString(9));
				customer.setCreditDays(cursor.getString(10));
				customer.setOutStanding(cursor.getString(11));
				customer.setIsActive(cursor.getString(12));
				// Adding contact to list
			} while (cursor.moveToNext());
		}

		return customer;
	}

	public String[] getAllCustomers()
	{
		String selectQuery ="SELECT " +DB_COLUMN_1_NAME+ " FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if(cursor.getCount() >0)
		{
			String[] str = new String[cursor.getCount()];
			int i = 0;

			while (cursor.moveToNext())
			{
				str[i] = cursor.getString(cursor.getColumnIndex(DB_COLUMN_1_NAME));
				i++;
			}
			return str;
		}
		else
		{
			return new String[] {};
		}
	}

	public List<CustomerDBEntities> getAllContactsBySearch(String searchStr) {
		List<CustomerDBEntities> customerList = new ArrayList<CustomerDBEntities>();

		String selectQuery = " SELECT  *  FROM CustomerDetails "+
				" where IsActive LIKE '%true%' AND CustName LIKE '%" + searchStr +"%'" +
				" union all " +
				" SELECT  *  FROM CustomerDetails "+
				" where IsActive LIKE '%true%' AND Address LIKE '%" + searchStr +"%'" +
				" union all " +
				" SELECT  *  FROM CustomerDetails "+
				" where IsActive LIKE '%true%' AND Building LIKE '%" + searchStr +"%'" +
				" union all " +
				" SELECT  *  FROM CustomerDetails "+
				" where IsActive LIKE '%true%' AND Area LIKE '%" + searchStr +"%'" ;

		SQLiteDatabase db = this.getWritableDatabase();
		try
		{
			Cursor cursor = db.rawQuery(selectQuery, null);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					CustomerDBEntities customer = new CustomerDBEntities();
					customer.setCustomerID(Integer.parseInt(cursor.getString(3)));
					customer.setCustomerName(cursor.getString(4));
					customer.setAddress(cursor.getString(5));
					customer.setBuilding(cursor.getString(6));
					customer.setArea(cursor.getString(7));
					customer.setCity(cursor.getString(8));
					customer.setAmount(cursor.getString(9));
					customer.setCreditDays(cursor.getString(10));
					customer.setOutStanding(cursor.getString(11));
					// Adding contact to list
					customerList.add(customer);
				} while (cursor.moveToNext());
			}

			// Remove duplicate
			int count = customerList.size();

			for (int i = 0; i < count; i++)
			{
				for (int j = i + 1; j < count; j++)
				{
					if (customerList.get(i).getCustomerName().equals(customerList.get(j).getCustomerName()))
					{
						customerList.remove(j--);
						count--;
					}
				}
			}
		}
		catch(Exception e)
		{
			String exstr = e.getMessage();
			String a1 = "";
		}
		// return contact list
		return customerList;
	}

	public List<CustomerDBEntities> getAllContactBySearch(String searchStr) {
		List<CustomerDBEntities> contactList = new ArrayList<CustomerDBEntities>();

		String selectQuery = " SELECT  (CustName || ' ' || Address || ' ' || Building || ' ' || Area) as Abc, CustName, CustomerID  FROM CustomerDetails "+
				" where IsActive LIKE '%true%' AND CustName LIKE '%" + searchStr +"%'" +
				" union all " +
				" SELECT  (CustName || ' ' || Address || ' ' || Building || ' ' || Area) as Abc, CustName, CustomerID  FROM CustomerDetails "+
				" where IsActive LIKE '%true%' AND Address LIKE '%" + searchStr +"%'" +
				" union all " +
				" SELECT  (CustName || ' ' || Address || ' ' || Building || ' ' || Area) as Abc, CustName, CustomerID  FROM CustomerDetails "+
				" where IsActive LIKE '%true%' AND Building LIKE '%" + searchStr +"%'" +
				" union all " +
				" SELECT  (CustName || ' ' || Address || ' ' || Building || ' ' || Area) as Abc, CustName, CustomerID  FROM CustomerDetails "+
				" where IsActive LIKE '%true%' AND Area LIKE '%" + searchStr +"%'" ;

		SQLiteDatabase db = this.getWritableDatabase();
		try
		{
			Cursor cursor = db.rawQuery(selectQuery, null);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					CustomerDBEntities contact = new CustomerDBEntities();
					contact.setAddress(cursor.getString(0));
					contact.setCustomerName(cursor.getString(1));
					contact.setCustomerID(Integer.parseInt(cursor.getString(2)));
					// Adding contact to list
					contactList.add(contact);
				} while (cursor.moveToNext());
			}

			// Remove duplicate
			int count = contactList.size();

			for (int i = 0; i < count; i++)
			{
				for (int j = i + 1; j < count; j++)
				{
					if (contactList.get(i).getCustomerName().equals(contactList.get(j).getCustomerName()))
					{
						contactList.remove(j--);
						count--;
					}
				}
			}

		}
		catch(Exception e)
		{
			String exstr = e.getMessage();
			String a1 = "";
		}
		// return contact list
		return contactList;
	}

	public List<CustomerEntities> getAllContact1BySearch(String searchStr) {
		List<CustomerEntities> contactList = new ArrayList<CustomerEntities>();

		String selectQuery = " SELECT  CustomerID, Cell1  FROM CustomerDetails "+
				" where CustName LIKE '%" + searchStr +"%'";

		SQLiteDatabase db = this.getWritableDatabase();
		try
		{
			Cursor cursor = db.rawQuery(selectQuery, null);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					CustomerEntities contact = new CustomerEntities();
					contact.setCustomerID(Integer.parseInt(cursor.getString(0)));
					contact.setCell1(cursor.getString(1));
					// Adding contact to list
					contactList.add(contact);
				} while (cursor.moveToNext());
			}

			// Remove duplicate
			int count = contactList.size();
			for (int i = 0; i < count; i++)
			{
				for (int j = i + 1; j < count; j++)
				{
					if (contactList.get(i).getCustomerName().equals(contactList.get(j).getCustomerName()))
					{
						contactList.remove(j--);
						count--;
					}
				}
			}
		}
		catch(Exception e)
		{
			String exstr = e.getMessage();
			String a1 = "";
		}
		// return contact list
		return contactList;
	}

	public List<ShopDBEntities> getAllShops() {
		try{
			List<ShopDBEntities> shopList = new ArrayList<ShopDBEntities>();
			// Select All Query
			String selectQuery = "SELECT  * FROM " + TABLE_NAME1;

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					ShopDBEntities contact = new ShopDBEntities();
					contact.setShopID(Integer.parseInt(cursor.getString(1)));
					contact.setShopName(cursor.getString(2));
					contact.setAddress(cursor.getString(3));
					contact.setCity(cursor.getString(4));
					contact.setPincode(cursor.getString(5));
					contact.setUserID(Integer.parseInt(cursor.getString(6)));
					// Adding contact to list
					shopList.add(contact);
				} while (cursor.moveToNext());
			}

			// return contact list
			return shopList;
		}
		catch (Exception e){
			return null;
		}
	}

	public ShopDBEntities getShop() {
		ShopDBEntities contact = new ShopDBEntities();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME1;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor != null)
			cursor.moveToFirst();

		contact.setShopID(Integer.parseInt(cursor.getString(1)));
		contact.setShopName(cursor.getString(2));
		contact.setAddress(cursor.getString(3));
		contact.setCity(cursor.getString(4));
		contact.setPincode(cursor.getString(5));
		contact.setUserID(Integer.parseInt(cursor.getString(6)));

		return contact;
	}

	public List<UserDBEntities> getAllUsers() {
		List<UserDBEntities> userList = new ArrayList<UserDBEntities>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME2;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				UserDBEntities contact = new UserDBEntities();
				contact.setUserID(Integer.parseInt(cursor.getString(1)));
				contact.setUserName(cursor.getString(2));
				contact.setMobileNo(cursor.getString(3));
				contact.setUserType(cursor.getString(4));
				contact.setShopID(Integer.parseInt(cursor.getString(5)));
				// Adding contact to list
				userList.add(contact);
			} while (cursor.moveToNext());
		}

		// return contact list
		return userList;
	}

	public UserDBEntities getByUserID(int UserID) {

		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME2 + " where UserID= "+ UserID;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		UserDBEntities contact = null;
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				contact = new UserDBEntities();
				contact.setUserID(Integer.parseInt(cursor.getString(1)));
				contact.setUserName(cursor.getString(2));
				contact.setMobileNo(cursor.getString(3));
				contact.setUserType(cursor.getString(4));
				contact.setShopID(Integer.parseInt(cursor.getString(5)));
				// Adding contact to list
			} while (cursor.moveToNext());
		}

		// return contact list
		return contact;
	}

	public List<UserDBEntities> getAllUsers(int shopid) {
		List<UserDBEntities> userList = new ArrayList<UserDBEntities>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME2 + " WHERE ShopID =" +shopid;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				UserDBEntities contact = new UserDBEntities();
				contact.setUserID(Integer.parseInt(cursor.getString(1)));
				contact.setUserName(cursor.getString(2));
				contact.setMobileNo(cursor.getString(3));
				contact.setUserType(cursor.getString(4));
				contact.setShopID(Integer.parseInt(cursor.getString(5)));

				userList.add(contact);
			} while (cursor.moveToNext());
		}
		return userList;
	}

	public List<ContactDBEntities> getAllContacts(int custid) {
		List<ContactDBEntities> contactlist = new ArrayList<ContactDBEntities>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME3 + " WHERE CustomerID =" +custid;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				ContactDBEntities contact = new ContactDBEntities();
				contact.setContactID(Integer.parseInt(cursor.getString(1)));
				contact.setMobileNo(cursor.getString(4));

				contactlist.add(contact);
			} while (cursor.moveToNext());
		}
		return contactlist;
	}

	public ContactDBEntities getByContact(int ContactID) {

		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME3 + " where ContactID= "+ ContactID;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		ContactDBEntities contact = null;
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				contact = new ContactDBEntities();
				contact.setContactID(Integer.parseInt(cursor.getString(1)));
				contact.setMobileNo(cursor.getString(4));
				// Adding contact to list
			} while (cursor.moveToNext());
		}

		return contact;
	}

	public List<ContactDBEntities> getAllContacts() {
		try{
			List<ContactDBEntities> contactList = new ArrayList<ContactDBEntities>();
			// Select All Query
			String selectQuery = "SELECT  * FROM " + TABLE_NAME3;

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					ContactDBEntities contact = new ContactDBEntities();
					contact.setContactID(Integer.parseInt(cursor.getString(0)));
					contact.setMobileNo(cursor.getString(3));
//					contact.setShopName(cursor.getString(2));
//					contact.setAddress(cursor.getString(3));
//					contact.setCity(cursor.getString(4));
//					contact.setPincode(cursor.getString(5));
					// Adding contact to list
					contactList.add(contact);
				} while (cursor.moveToNext());
			}

			// return contact list
			return contactList;
		}
		catch (Exception e){
			return null;
		}
	}

	public List<DateEntities> getSyncDate() {
		try{
			List<DateEntities> contactList = new ArrayList<DateEntities>();
			String selectQuery = "SELECT  * FROM " + TABLE_NAME6;

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					DateEntities contact = new DateEntities();
					contact.setSyncID(Integer.parseInt(cursor.getString(0)));
					contact.setSyncDate(cursor.getString(1));
					contactList.add(contact);
				} while (cursor.moveToNext());
			}

			return contactList;
		}
		catch (Exception e){
			return null;
		}
	}
}
