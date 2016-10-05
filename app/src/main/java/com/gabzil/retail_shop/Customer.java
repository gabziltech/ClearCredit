package com.gabzil.retail_shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Customer extends Fragment implements TaskGettingBillComplete,TaskBillComplete,TaskPaymentComplete,
        TaskEditComplete,TaskDeleteComplete,OnDeleteTaskCompleted,OnGettingBills,TaskDialComplete,OnGettingSyncData {
    ListView list;
    TextView msg;
    private DataHelp dh;
    private MyOpenHelper db;
    int id,editcust;
    List<ShopDBEntities> shop;
    List<String> listItems=new ArrayList<String>();
    String SearchStr;
    AllCustomersAdaptor sublist2;
    CustomerDBEntities MainCust = new CustomerDBEntities();
    CustomerDBEntities MainCust1 = new CustomerDBEntities();
    ArrayList<ContactDBEntities> contactnos;
    boolean flag;

    public Customer() {
        // Required empty public constructor
    }

    public Customer(boolean flag) {
        this.flag = flag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_customer, container, false);
        getActivity().setTitle("Customer Details");
        setHasOptionsMenu(true);
        DeclareCustomerVariables(v);
        GetAllCustomerFromLocal();
        if (flag){
            shop = db.getAllShops();
            SyncData(shop.get(0).getShopID());
        }

        return v;
    }

    public boolean mScanning;
    public void SyncData(int shopid) {
        try {
            ArrayList<BillDBEntities> bills =  new ArrayList<BillDBEntities>(db.getAllBillInfo());
            ArrayList<PaymentDBEntities> payments =  new ArrayList<PaymentDBEntities>(db.getAllPaymentInfo());
            final SyncShopInfo p=new SyncShopInfo(getActivity(), this);
            mScanning = true;
            getActivity().invalidateOptionsMenu();
            p.execute(shopid, bills, payments);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (p.getStatus() == AsyncTask.Status.RUNNING) {
                        mScanning = false;
                        getActivity().invalidateOptionsMenu();
                        p.cancel(true);
                        Toast.makeText(getActivity(), "Network Problem, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000 * 60 * 5);
            getActivity().invalidateOptionsMenu();
        }
        catch (Exception e){
            e.getMessage();
        }
    }

    public Button Search,Cancel,AddCustmer;
    AutoCompleteTextView SearchText;
    TextView Name;
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (!flag) {
            menu.findItem(R.id.search_item).setVisible(true);
            MenuItem item1 = menu.findItem(R.id.search_item);
            MenuItemCompat.setActionView(item1, R.layout.update);
            LinearLayout notificationCount1 = (LinearLayout) MenuItemCompat.getActionView(item1);
            Name = (TextView) notificationCount1.findViewById(R.id.name);
            Search = (Button) notificationCount1.findViewById(R.id.search);
            Cancel = (Button) notificationCount1.findViewById(R.id.cancel);
            AddCustmer = (Button) notificationCount1.findViewById(R.id.addcustomer);
            SearchText = (AutoCompleteTextView) notificationCount1.findViewById(R.id.search_text);

            final KeyListener originalKeyListener = SearchText.getKeyListener();
            SearchText.setKeyListener(null);
            Search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Name.setVisibility(View.GONE);
                    SearchText.setVisibility(View.VISIBLE);
                    Cancel.setVisibility(View.VISIBLE);
                    Search.setVisibility(View.GONE);

                    SearchText.setKeyListener(originalKeyListener);
                    SearchText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(SearchText, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            SearchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // If it loses focus...
                    if (!hasFocus) {
                        // Hide soft keyboard.
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(SearchText.getWindowToken(), 0);
                        // Make it non-editable again.
                        SearchText.setKeyListener(null);
                    }
                }
            });

            SearchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                    // TODO Auto-generated method stub
                    try {
                        list.setAdapter(null);
                        listItems.clear();
                        SearchStr = arg0.toString();
                        Search(db.getAllContactsBySearch(SearchStr));
                    } catch (Exception ex) {
                        ex.getMessage();
                    }
                }
            });

            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Name.setVisibility(View.VISIBLE);
                    SearchText.setText("");
                    SearchText.setVisibility(View.GONE);
                    Cancel.setVisibility(View.GONE);
                    Search.setVisibility(View.VISIBLE);
                    GetAllCustomerFromLocal();
                }
            });

            AddCustmer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shop = db.getAllShops();
                    if (shop.size() == 0) {
                        OpenShopScreen();
                    }
                    else {
                        MainCust = MainCust1;
                        MainCust.setCity("");
                        AddCust(true, MainCust);
                    }
                }
            });
        }
        else {
            if (!mScanning) {
                menu.findItem(R.id.action_refresh).setActionView(null);
            } else {
                menu.findItem(R.id.action_refresh).setActionView(
                        R.layout.actionbar_indeterminate_progress);
            }
        }

        super.onPrepareOptionsMenu(menu);
    }

    public void AddCust(boolean set,CustomerDBEntities customer){
        Fragment fragment = new AddCustomer(set,customer);
        getFragmentManager().beginTransaction()
                .replace(((ViewGroup) getView().getParent()).getId(), fragment)
                .addToBackStack("" + fragment.getClass().getSimpleName())
                .commit();
    }

    public void Search(List<CustomerDBEntities> mContactList){
        if (mContactList.size() > 0) {
            sublist2 = new AllCustomersAdaptor(getActivity(), R.layout.customlist, (ArrayList<CustomerDBEntities>) mContactList,this,this,this,this,this,this);
            list.setVisibility(View.VISIBLE);
            msg.setVisibility(View.GONE);
            list.setAdapter(sublist2);
        }
        else {
            msg.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
            int unicode = 0x1F61E;
            String emoji = getEmijoByUnicode(unicode);
            String text = "No result";
            msg.setText(text + emoji);
        }
    }

    public void DeclareCustomerVariables(View v){
        dh = new DataHelp(getActivity());
        db = new MyOpenHelper(getActivity());

        list = (ListView) v.findViewById(R.id.custlist);
        msg = (TextView) v.findViewById(R.id.msg);
    }

    AllCustomerListAdaptor subAdap1;
    ArrayList<CustomerDBEntities> customers ;
    public void GetAllCustomerFromLocal() {
        customers =  new ArrayList<CustomerDBEntities>(db.getAllCustomerInfo());
        if (customers.size() > 0) {
            list.setVisibility(View.VISIBLE);
            msg.setVisibility(View.GONE);
            subAdap1 = new AllCustomerListAdaptor(getActivity(), R.layout.customlist, customers,this,this,this,this,this,this);
            list.setAdapter(subAdap1);
            list.setTextFilterEnabled(true);
        }
        else {
            list.setVisibility(View.GONE);
            msg.setVisibility(View.VISIBLE);
            int unicode = 0x1F61E;
            String emoji = getEmijoByUnicode(unicode);
            String text = "Currently no customers";
            msg.setText(text + emoji);
        }
    }

    public static String getEmijoByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    @Override
    public void OnBillButton(CustomerDBEntities data) {
        Fragment fragment = new Bill(data);
        getFragmentManager().beginTransaction()
                .replace(((ViewGroup) getView().getParent()).getId(), fragment)
                .addToBackStack(""+fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void OnPaymentButton(CustomerDBEntities data) {
        Fragment fragment = new Payment(data);
        getFragmentManager().beginTransaction()
                .replace(((ViewGroup) getView().getParent()).getId(), fragment)
                .addToBackStack("" + fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void OnEditButton(CustomerDBEntities data) {
        editcust = 1;
        MainCust = data;
        contactnos =  new ArrayList<ContactDBEntities>(db.getAllContacts(MainCust.getCustomerID()));
        MainCust.setAllContact(contactnos);
        AddCust(false, MainCust);
    }

    @Override
    public void OnDeleteButton(CustomerDBEntities data) {
        shop = db.getAllShops();
        if (shop.size() > 0) {
            MainCust = data;
            MainCust.setShopID(shop.get(0).getShopID());
            MainCust.setShopName(shop.get(0).getShopName());
            List<ContactDBEntities> Contact = db.getAllContacts(MainCust.getCustomerID());
            MainCust.setAllContact((ArrayList<ContactDBEntities>) Contact);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Alert");
            alertDialogBuilder.setIcon(R.drawable.alert_icon);
            alertDialogBuilder.setMessage("Do You Want To Delete This Customer???");

            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DeleteCustomerData(MainCust);
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    private void DeleteCustomerData(CustomerDBEntities custdelete)
    {
        MainCust.setIsActive("false");
        final DeleteCustomerInfo p=new DeleteCustomerInfo(getActivity(), this);
        p.execute(custdelete);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (p.getStatus() == AsyncTask.Status.RUNNING) {
                    p.cancel(true);
                    p.mProgress.dismiss();
                    Toast.makeText(getActivity(), "Network Problem, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000 * 30);
    }

    @Override
    public void OnDeleteTaskCompleted(String results) {
        int custid = Integer.parseInt(results);
        if (custid > 0)
        {
            if (dh.CustomerSbmt(MainCust))
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setIcon(R.mipmap.ic_done);
                alertDialogBuilder.setMessage("Customer Successfully Deleted");

                alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GetAllCustomerFromLocal();
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }
        else {
            Toast.makeText(getActivity(),"There is some problem, Customer is not Deleted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnGettingBill(CustomerDBEntities data) {
        final GetBills p=new GetBills(getActivity(), this);
        p.execute(data.getShopID(),data.getCustomerID());
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (p.getStatus() == AsyncTask.Status.RUNNING) {
                    p.cancel(true);
                    p.mProgress.dismiss();
                    Toast.makeText(getActivity(), "Network Problem, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000 * 30);
    }

    ArrayList<BillDBEntities> subList;
    @Override
    public void OnGettingBills(String results) {
        if (results != "null" && results.length() > 0) {
            try {
                Gson gson = new Gson();
                JSONArray objSub =  new JSONArray(results);
                subList = new ArrayList<BillDBEntities>();
                if(objSub.length() > 0)
                {
                    for (int i = 0; i < objSub.length(); i++) {
                        String subIDInfo = objSub.getJSONObject(i).toString();
                        //create java object from the JSON object
                        BillDBEntities cat = gson.fromJson(subIDInfo, BillDBEntities.class);
                        subList.add(cat);
                    }
                }
                Fragment fragment = new CustomerBillList(subList);
                getFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), fragment)
                        .addToBackStack("" +fragment.getClass().getSimpleName())
                        .commit();
            }
            catch (Exception e){
                e.getMessage();
            }
        }
        else {
            Toast.makeText(getActivity(),"Some problem occured, Bills not getting",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnDialButton(CustomerDBEntities data) {
        try {
            ArrayList<ContactDBEntities> contactnos = new ArrayList<ContactDBEntities>(db.getAllContacts(data.getCustomerID()));
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" +contactnos.get(0).getMobileNo()));
            getActivity().startActivity(intent);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void OpenShopScreen(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setIcon(R.mipmap.ic_alert);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setMessage("No Shop Added, Firstly Add The Shop");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try{
                    Fragment fragment = new ShopDetails();
                    getFragmentManager().popBackStack();
                    getFragmentManager().beginTransaction()
                            .replace(((ViewGroup)getView().getParent()).getId(),fragment)
                            .addToBackStack(null)
                            .commit();
                }
                catch (Exception e){
                    e.getMessage();
                }
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void OnGettingSyncData(String results) {
        mScanning = true;
        getActivity().invalidateOptionsMenu();
        try {
            if (results != "null" && results.length() > 0) {
                JSONObject obj = new JSONObject(results);
                String subIDInfo = obj.toString();
                Gson gson = new Gson();
                ShopDBEntities shop = gson.fromJson(subIDInfo, ShopDBEntities.class);
                dh.ShopSbmt(shop);
                try {
                    JSONArray objSub = obj.getJSONArray("AllCustomer");
                    for (int i = 0; i < objSub.length(); i++) {
                        String subIDInfo1 = objSub.getJSONObject(i).toString();
                        CustomerDBEntities customer = gson.fromJson(subIDInfo1, CustomerDBEntities.class);
                        dh.CustomerSbmt(customer);
                    }
                    SetAlert(getActivity());
                }
                catch (Exception e){
                    e.getMessage();
                }
            } else {
                Toast.makeText(getActivity(), "Some problem occured, Sync is not done", Toast.LENGTH_SHORT).show();
                mScanning = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getActivity().invalidateOptionsMenu();
    }

    public void SetAlert(final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setIcon(R.mipmap.ic_done);
        alertDialogBuilder.setMessage("Sync Successfully Done");

        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FragmentManager fragmentManager = getFragmentManager();
                getFragmentManager().popBackStack();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new Customer())
                        .addToBackStack(null)
                        .commit();

                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}