package com.gabzil.retail_shop;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Payment extends Fragment implements OnSMSTaskCompleted {
    TextView date;
    Spinner mode;
    ListView custlist;
    private MyOpenHelper db;
    CustomerDBEntities data, searchcust;
    boolean flag = false;
    CalendarView calendarView;
    AutoCompleteTextView customer;
    EditText amountET;
    List<CustomerDBEntities> countries;
    String SearchStr;
    int shopid;
    List<ShopDBEntities> shop;
    Double amount;
    Calendar myCalendar;
    PaymentDBEntities MainPayment = new PaymentDBEntities();

    public Payment() {
        // Required empty public constructor
    }

    public Payment(CustomerDBEntities data) {
        this.data = data;
        this.flag = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment, container, false);
        getActivity().setTitle("Payment");
        setHasOptionsMenu(true);
        DeclareVariables(v);
//        ArrayList<PaymentDBEntities> payments =  new ArrayList<PaymentDBEntities>(db.getAllPaymentInfo());

        SimpleDateFormat myFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        date.setText(currentDateTimeString.substring(0, 11));
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDate();
            }
        });

        SetCustName();
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
                setPaymentInformation();
                if (!ValidateData()) {
                    SavePaymentLocally();
                }
                return true;
            }
        });

        super.onPrepareOptionsMenu(menu);
    }

    public void SetDate() {
        new DatePickerDialog(getActivity(), date1, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        date.setText(df.format(myCalendar.getTime()));
    }

    public void DeclareVariables(View v) {
        customer = (AutoCompleteTextView) v.findViewById(R.id.customer);
        custlist = (ListView) v.findViewById(R.id.custlist);
        amountET = (EditText) v.findViewById(R.id.amount);
        mode = (Spinner) v.findViewById(R.id.payment_mode);
        date = (TextView) v.findViewById(R.id.date);
        calendarView = (CalendarView) v.findViewById(R.id.calendarView);
        myCalendar = Calendar.getInstance();

        List<String> categories = new ArrayList<String>();
        categories.add("Cash");
        categories.add("Credit Card");
        categories.add("Return");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categories);
        mode.setAdapter(dataAdapter);

        db = new MyOpenHelper(getActivity());
        shop = db.getAllShops();
        if (shop.size() == 0) {
            OpenShopScreen();
        } else {
            MainPayment.setShopID(shop.get(0).getShopID());
            MainPayment.setShopName(shop.get(0).getShopName());
        }
    }

    AllSearchAdaptor subList;
    public void SetCustName() {
        if (flag) {
            customer.setEnabled(false);
            customer.setTypeface(null, Typeface.BOLD_ITALIC);
            customer.setText(data.getCustomerName());
            MainPayment.setCustomerID(data.getCustomerID());
            MainPayment.setCustomerName(data.getCustomerName());
        } else {
            customer.addTextChangedListener(new TextWatcher() {
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
                        SearchStr = customer.getText().toString();
                        countries = db.getAllContactsBySearch(SearchStr);
                        subList = new AllSearchAdaptor(getActivity(), R.layout.customerlist, countries);
                        if (countries.size() > 0 && SearchStr.length() != 0)
                            custlist.setVisibility(View.VISIBLE);
                        else
                            custlist.setVisibility(View.GONE);
                        custlist.setAdapter(subList);
                        custlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                searchcust = countries.get(i);
                                customer.setText(searchcust.getCustomerName());
                                MainPayment.setCustomerID(searchcust.getCustomerID());
                                MainPayment.setCustomerName(searchcust.getCustomerName());
                                custlist.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception ex) {
                        String exstr = ex.getMessage();
                    }
                }
            });
        }

    }

    public void setPaymentInformation() {
        try {
            MainPayment.setPayAmount(Double.valueOf(amountET.getText().toString().trim()));
            MainPayment.setPaymentMode(mode.getSelectedItem().toString().trim());
            MainPayment.setUserDate(date.getText().toString().trim());
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void SavePaymentLocally() {
        if (MainPayment.getCustomerID() > 0) {
            List<ContactDBEntities> Contact = db.getAllContacts(MainPayment.getCustomerID());
            MainPayment.setMobileNo(Contact.get(0).getMobileNo());
            final SavePayment p = new SavePayment();
            p.execute(MainPayment);
        } else {
            Toast.makeText(getActivity(), "Customer Not Exist", Toast.LENGTH_SHORT).show();
        }
    }

    ProgressDialog pDialog;
    private class SavePayment extends AsyncTask<Object, String, Boolean> {
        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                pDialog = new ProgressDialog(getActivity());
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setMessage("Payment Saving, Please Wait...");
                pDialog.show();
            } catch (Exception e) {
                e.getMessage();
            }
        }

        protected Boolean doInBackground(Object[] params) {
            boolean reply1 = false;
            try {
                DataHelp dh = new DataHelp(getActivity());
                reply1 = dh.PaymentSbmt((PaymentDBEntities) params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return reply1;
        }

        protected void onPostExecute(Boolean reply) {
            pDialog.dismiss();
            DataHelp dh = new DataHelp(getActivity());
            if (reply) {
                data = db.getByCustomerID(MainPayment.getCustomerID());
                Double outstanding = Double.parseDouble(data.getOutStanding());
                outstanding = outstanding - MainPayment.getPayAmount();
                data.setOutStanding(String.valueOf(outstanding));
                ArrayList<ContactDBEntities> contactnos = new ArrayList<ContactDBEntities>(db.getAllContacts(data.getCustomerID()));
                data.setAllContact(contactnos);
                if (dh.CustomerSbmt(data)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Alert");
                    alertDialogBuilder.setIcon(R.mipmap.ic_done);
                    alertDialogBuilder.setMessage("Payment Successfully Done");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ConnectivityManager ConnectionManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected() == true) {
                                CallSmsService();
                            } else {
                                Toast.makeText(getActivity(), "Internet connection is not available, so message is not send to the customer", Toast.LENGTH_SHORT).show();
                            }
                            Fragment fragment = new Customer();
                            getFragmentManager().popBackStack("Payment", getFragmentManager().POP_BACK_STACK_INCLUSIVE);
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
                } else {
                    Toast.makeText(getActivity(), "There is some problem, Bill is not Saved", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Payment not saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void CallSmsService() {
        final SendPaymentSMS p=new SendPaymentSMS(getActivity(), this);
        p.execute(MainPayment);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (p.getStatus() == AsyncTask.Status.RUNNING) {
                    p.cancel(true);
                    Toast.makeText(getActivity(), "Network Problem, SMS may not send", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000 * 30);
    }

    @Override
    public void OnSMSTaskCompleted(String results) {
    }

    private boolean ValidateData() {
        boolean error = false;
        String strmsg = "Please Enter ";

        if (customer.getText().toString().trim().length() == 0) {
            strmsg += "Customer Name";
            error = true;
        }
        if (amountET.getText().toString().trim().length() == 0) {
            strmsg += ", Amount";
            error = true;
        }
        if (mode.getSelectedItem().toString().equals("--- Select ---")) {
            strmsg += ", Payment Mode";
            error = true;
        }

        if (error == true) {
            String replacedString = strmsg.replace("Please Enter ,", "Please Enter ");
            ShowAlert(replacedString);
        }
        return error;
    }

    public void ShowAlert(String msg) {
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

    public void OpenShopScreen() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setIcon(R.mipmap.ic_alert);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setMessage("No Shop Added, Firstly Add The Shop");

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    Fragment fragment = new ShopDetails();
                    getFragmentManager().popBackStack();
                    getFragmentManager().beginTransaction()
                            .replace(((ViewGroup) getView().getParent()).getId(), fragment)
                            .addToBackStack(null)
                            .commit();
                } catch (Exception e) {
                    e.getMessage();
                }
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
