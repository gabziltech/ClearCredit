package com.gabzil.retail_shop;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AddCustomer extends Fragment implements OnCustomerTaskCompleted,TaskEditContactComplete,TaskDeleteContactComplete,
        OnDeleteContactCompleted {
    Button addcontact,btnDismiss,btnSave;
    EditText custmrnameET,custaddET,buildingET,areaET,amountET,mobileET,creditdaysET;
    Spinner cityET;
    ListView contactlist;
    TextView msg;
    private DataHelp dh;
    private MyOpenHelper db;
    List<ShopDBEntities> shop;
    CustomerDBEntities MainCust = new CustomerDBEntities();
    PopupWindow popupWindow;
    boolean set;
    CustomerDBEntities customer;
    ContactDBEntities MainContact = new ContactDBEntities();
    ContactDBEntities MainContact1 = new ContactDBEntities();
    int editcontact,position1;
    ContactDBEntities selectedContactEntity;
    AllContactListAdaptor contactAdap;

    public AddCustomer() {
        // Required empty public constructor
    }

    public AddCustomer(boolean set,CustomerDBEntities customer) {
        this.set = set;
        this.customer = customer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_customer, container, false);
        getActivity().setTitle("Add Customer");
        setHasOptionsMenu(true);
        DeclareCustomerVariables(v);

        addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainContact = MainContact1;
                editcontact = 0;
                ShowContactPopUp(MainContact, editcontact);
            }
        });

        AddCustBtn(customer);
        return v;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.cancel).setVisible(true);
        menu.findItem(R.id.save).setVisible(true);
        menu.findItem(R.id.action_refresh).setVisible(false);

        MenuItem cancel = menu.findItem(R.id.cancel);
        cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Fragment fragment = new Customer();
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), fragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
        });

        final MenuItem save = menu.findItem(R.id.save);
        save.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                setCustInformation();
                if (!IsValidation()) {
                    SaveCustomerData();
                }
                return true;
            }
        });

        super.onPrepareOptionsMenu(menu);
    }

    public void ShowContactPopUp(ContactDBEntities contact, final int editcontact){
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_add_mobile, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.update();

        mobileET = (EditText) popupView.findViewById(R.id.cell2);
        btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
        btnSave = (Button) popupView.findViewById(R.id.save);

        if(editcontact == 1){
            selectedContactEntity = contact;
            mobileET.setText(contact.getMobileNo());
        }

        btnSave.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                SaveContactNo(editcontact);
            }
        });

        btnDismiss.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(addcontact, Gravity.CENTER_HORIZONTAL, 5, 25);
    }

    public void AddCustBtn(CustomerDBEntities customer)
    {
        shop = db.getAllShops();
        if (shop.size() == 0) {
            Toast.makeText(getActivity(),"No Shop Added, Firstly Add The Shop",Toast.LENGTH_SHORT).show();
        }
        else{
            MainCust = customer;
            MainCust.setShopID(shop.get(0).getShopID());
            MainCust.setShopName(shop.get(0).getShopName());
            custmrnameET.setText(MainCust.getCustomerName());
            custaddET.setText(MainCust.getAddress());
            buildingET.setText(MainCust.getBuilding());
            areaET.setText(MainCust.getArea());
            Resources res = getResources();
            String[] planets = res.getStringArray(R.array.city_arrays);
            for (int j = 1; j <= 4; j++) {
                if ((MainCust.getCity().equals(planets[j])))
                    cityET.setSelection(j);
                cityET.getSelectedItem().toString().trim();
            }
            amountET.setText(MainCust.getAmount());
            creditdaysET.setText(MainCust.getCreditDays());
            if (MainCust.getAllContact() != null){
                msg.setVisibility(View.GONE);
                contactlist.setVisibility(View.VISIBLE);
                contactAdap = new AllContactListAdaptor(getActivity(), R.layout.contactlist,MainCust.getAllContact(),this,this);
                contactlist.setAdapter(contactAdap);
                contactlist.setTextFilterEnabled(true);
            }
        }
    }

    public void SaveContactNo(int edit){
        ContactDBEntities contact = new ContactDBEntities();
        if (!IsValidation1()) {
            contact.setMobileNo(mobileET.getText().toString().trim());
            if(MainCust.getAllContact() !=null && MainCust.getAllContact().size() >0)
            {
                if (edit == 0){
                    MainCust.getAllContact().add(contact);
                }
                else {
                    contact.setContactID(selectedContactEntity.getContactID());
                    MainCust.getAllContact().remove(position1);
                    MainCust.getAllContact().add(position1, contact);
                }
            }
            else {
                ArrayList<ContactDBEntities> list1 = new ArrayList<ContactDBEntities>();
                list1.add(contact);
                MainCust.setAllContact(list1);
            }
            setToList();
            popupWindow.dismiss();
        }
    }

    public void DeclareCustomerVariables(View v){
        dh = new DataHelp(getActivity());
        db = new MyOpenHelper(getActivity());

        custmrnameET = (EditText) v.findViewById(R.id.custmrname);
        custaddET = (EditText) v.findViewById(R.id.cstmraddress);
        buildingET = (EditText) v.findViewById(R.id.building);
        areaET = (EditText) v.findViewById(R.id.area);
        cityET = (Spinner) v.findViewById(R.id.city);
        amountET = (EditText) v.findViewById(R.id.amount);
        creditdaysET = (EditText) v.findViewById(R.id.days);
        addcontact = (Button) v.findViewById(R.id.addcontactbtn);
        contactlist = (ListView) v.findViewById(R.id.contactlist);
        msg = (TextView) v.findViewById(R.id.msg);
    }

    public void setCustInformation(){
        MainCust.setCustomerName(custmrnameET.getText().toString().trim());
        MainCust.setAddress(custaddET.getText().toString().trim());
        MainCust.setBuilding(buildingET.getText().toString().trim());
        MainCust.setArea(areaET.getText().toString().trim());
        MainCust.setCity(cityET.getSelectedItem().toString().trim());
        MainCust.setAmount(amountET.getText().toString().trim());
        MainCust.setCreditDays(creditdaysET.getText().toString().trim());
    }

    private boolean IsValidation()
    {
        boolean error=false;
        String strmsg= "Please Enter ";

        if((MainCust.getAllContact() == null) || (MainCust.getAllContact().size() == 0))
        {
            strmsg += "All Customer Details and Atleast One Contact";
            error = true;
        }
        else{
            if (MainCust.getCustomerName().trim().length() == 0) {
                strmsg += "Customer Name";
                error = true;
            }
            if (MainCust.getAddress().trim().length() == 0) {
                strmsg += ", Flat No";
                error = true;
            }
            if (MainCust.getBuilding().trim().length() == 0) {
                strmsg += ", Building Name";
                error = true;
            }
            if (MainCust.getArea().trim().length() == 0) {
                strmsg += ", Area";
                error = true;
            }
            if(cityET.getSelectedItem().toString().equals("Select"))
            {
                strmsg += ", City";
                error = true;
            }
            if (MainCust.getAmount().trim().length() == 0) {
                strmsg += ", Credit Amount";
                error = true;
            }
            if (MainCust.getCreditDays().trim().length() == 0) {
                strmsg += ", Credit Days";
                error = true;
            }
        }

        if (error == true) {
            String replacedString = strmsg.replace("Please Enter ,", "Please Enter ");
            ShowAlert(replacedString);
        }
        return error;
    }

    private boolean IsValidation1() {
        boolean error = false;
        String strmsg = "Please Enter ";

        if (mobileET.getText().toString().trim().length() == 0) {
            strmsg += ", Mobile No";
            error = true;
        } else if ((mobileET.getText().toString().trim().length() < 10) || (mobileET.getText().toString().trim().length() > 13)) {
            strmsg += ", Mobile No of only 10 characters";
            error = true;
        }

        if (error == true) {
            String replacedString = strmsg.replace("Please Enter ,", "Please Enter ");
            ShowAlert(replacedString);
        }
        return error;
    }

    public void setToList()
    {
        if (MainCust.getAllContact().size() > 0){
            msg.setVisibility(View.GONE);
            contactlist.setVisibility(View.VISIBLE);
            contactAdap = new AllContactListAdaptor(getActivity(), R.layout.contactlist,MainCust.getAllContact(),this,this);
            contactlist.setAdapter(contactAdap);
            contactlist.setTextFilterEnabled(true);
        }
        else {
            msg.setVisibility(View.VISIBLE);
            contactlist.setVisibility(View.GONE);
        }
    }

    private void SaveCustomerData()
    {
        MainCust.setIsActive("true");
        final SaveCustomerInfo p=new SaveCustomerInfo(getActivity(), this);
        p.execute(MainCust);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (p.getStatus() == AsyncTask.Status.RUNNING) {
                    // My AsyncTask is currently doing work in doInBackground()
                    p.cancel(true);
                    p.mProgress.dismiss();
                    Toast.makeText(getActivity(), "Network Problem, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000 * 30);
    }

    @Override
    public void OnCustomerTaskCompleted(String results) {
        if (results != "null" && results.length() > 0){
            Gson gson = new Gson();
            CustomerDBEntities customer = gson.fromJson(results, CustomerDBEntities.class);
            if (dh.CustomerSbmt(customer)){

                if (set == false){
                    SetAlert("Customer Successfully Edited");
                }
                else {
                    SetAlert("Customer Successfully Added");
                }
            }
        }
        else {
            Toast.makeText(getActivity(),"Some problem occured, Customer is not added",Toast.LENGTH_SHORT).show();
        }
    }

    public void ShowAlert(String msg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(msg);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void SetAlert(String msg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setIcon(R.mipmap.ic_done);
        alertDialogBuilder.setMessage(msg);

        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Fragment fragment = new Customer();
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), fragment)
                        .addToBackStack(null)
                        .commit();
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void OnEditContact(ContactDBEntities data, int position) {
        position1 = position;
        editcontact = 1;
        ShowContactPopUp(data, editcontact);
    }

    @Override
    public void OnDeleteContact(final ContactDBEntities data, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setIcon(R.drawable.alert_icon);
        alertDialogBuilder.setMessage("Do You Want To Delete This Contact???");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (data.getContactID() == 0) {
                    MainCust.getAllContact().remove(position);
                    setToList();
                } else {
                    DeleteContactData(data);
                }
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

    private void DeleteContactData(ContactDBEntities contact)
    {
        ArrayList<ContactDBEntities> list1 = new ArrayList<ContactDBEntities>();
        list1.add(contact);
        MainCust.setAllContact(list1);
        final DeleteContact p = new DeleteContact(getActivity(), this);
        p.execute(MainCust);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (p.getStatus() == AsyncTask.Status.RUNNING) {
                    // My AsyncTask is currently doing work in doInBackground()
                    p.cancel(true);
                    p.mProgress.dismiss();
                    Toast.makeText(getActivity(), "Network Problem, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000 * 30);
    }

    @Override
    public void OnDeleteContactCompleted(String results) {
        int reply = Integer.parseInt(results);
        if (reply == 1)
        {
            if (dh.ContactDelete(MainCust))
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setIcon(R.drawable.alert_icon);
                alertDialogBuilder.setMessage("Contact Successfully Deleted");
                alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddCustBtn(MainCust);
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }
        else {
            Toast.makeText(getActivity(),"Some problem occured, Contact is not Deleted",Toast.LENGTH_SHORT).show();
        }
    }
}
