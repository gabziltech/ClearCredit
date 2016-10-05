package com.gabzil.retail_shop;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class History extends Fragment implements OnGettingInfoFromPeriod, TaskImageComplete, TaskImageHistoryComplete {
    EditText customername;
    Spinner type;
    TextView fromdate, todate, message;
    Button search;
    ListView list, custlist;
    Calendar myCalendar;
    List<ShopDBEntities> shop;
    HistoryEntities MainHistory = new HistoryEntities();
    String SearchStr;
    List<CustomerDBEntities> customers;
    private MyOpenHelper db;
    AllSearchAdaptor subList;
    int flag;
    CustomerDBEntities searchcust;
    SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
    SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
    String first, second, third, fourth;

    public History() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        getActivity().setTitle("History");
        DeclareVariables(v);
        list.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        int unicode = 0x1F61E;
        String emoji = getEmijoByUnicode(unicode);
        String text = "Please search for records...";
        message.setText(text + emoji);

        SetCustomerName();
        Date dtStartDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dtStartDate);
        c.add(Calendar.DATE, -3);  // number of days to add

        String fromDateTimeString = format.format(c.getTime());
        first = fromDateTimeString.substring(0, 12);
        second = " 00:00:00 AM";

//        Character c1 = second.charAt(9);
//        String[] current = second.split(" ");
//        if (c1.equals('a') || c1.equals('A'))
//            second = second.replace(current[1],"AM");
//        else
//            second = second.replace(current[1],"PM");
//
        fromdate.setText(String.valueOf(first));
        fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 1;
                SetDate();
            }
        });

        String currentDateTimeString = format.format(new Date());
        third = currentDateTimeString.substring(0, 12);
        fourth = currentDateTimeString.substring(13, 24);

        Character c2 = fourth.charAt(9);
        String[] current1 = fourth.split(" ");
        if (c2.equals('a') || c2.equals('A'))
            fourth = fourth.replace(current1[1],"AM");
        else
            fourth = fourth.replace(current1[1],"PM");

        todate.setText(String.valueOf(third));
        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 0;
                SetDate();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSearchInformation();
                if (!IsValidation()) {
                    GetInfoFromPeriod();
                }
            }
        });

        return v;
    }

    public void setSearchInformation() {
        if (customername.getText().toString().trim().length() == 0) {
            MainHistory.setCustomerID(0);
            MainHistory.setCustomerName(null);
        }
        MainHistory.setFromDate(fromdate.getText().toString().trim() + " " + second);
        MainHistory.setToDate(todate.getText().toString().trim() + " " + fourth);
        MainHistory.setType(type.getSelectedItem().toString().trim());
    }

    private boolean IsValidation() {
        boolean error = false;
        String strmsg = "Please Enter ";

        if (customername.getText().toString().trim().length() > 0) {
            if (MainHistory.getCustomerID() == 0) {
                strmsg += ", Register Customer Name";
                error = true;
            }
        }
        if (type.getSelectedItem().toString().trim() == "--- Select ---") {
            strmsg += ", Search For";
            error = true;
        }
        if (fromdate.getText().toString().trim().length() == 0) {
            strmsg += " From Date";
            error = true;
        }
        if (todate.getText().toString().trim().length() == 0) {
            strmsg += ", To Date";
            error = true;
        }

        if (error == true) {
            String replacedString = strmsg.replace("Please Enter ,", "Please Enter ");
            ShowAlert(replacedString);
        }
        return error;
    }

    public String getEmijoByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public void GetInfoFromPeriod() {
        final GetInfoFromPeriod p = new GetInfoFromPeriod(getActivity(), this);
        p.execute(MainHistory);
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

    public void DeclareVariables(View v) {
        customername = (EditText) v.findViewById(R.id.name);
        custlist = (ListView) v.findViewById(R.id.custlist);
        type = (Spinner) v.findViewById(R.id.type);
        fromdate = (TextView) v.findViewById(R.id.fromdate);
        todate = (TextView) v.findViewById(R.id.todate);
        search = (Button) v.findViewById(R.id.searchbtn);
        list = (ListView) v.findViewById(R.id.list);
        message = (TextView) v.findViewById(R.id.msg);
        myCalendar = Calendar.getInstance();
        db = new MyOpenHelper(getActivity());

        List<String> categories = new ArrayList<String>();
        categories.add("All");
        categories.add("Payment");
        categories.add("Bill");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categories);
        type.setAdapter(dataAdapter);

        shop = db.getAllShops();
        if (shop.size() == 0) {
            OpenShopScreen();
        } else {
            MainHistory.setShopID(shop.get(0).getShopID());
        }
    }

    public void SetCustomerName() {
        customername.addTextChangedListener(new TextWatcher() {
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
                    SearchStr = customername.getText().toString();
                    customers = db.getAllContactsBySearch(SearchStr);
                    subList = new AllSearchAdaptor(getActivity(), R.layout.customerlist, customers);
                    if (customers.size() > 0 && SearchStr.length() != 0)
                        custlist.setVisibility(View.VISIBLE);
                    else {
                        custlist.setVisibility(View.GONE);
                        MainHistory.setCustomerID(0);
                    }
                    custlist.setAdapter(subList);
                    custlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            searchcust = customers.get(i);
                            customername.setText(searchcust.getCustomerName());
                            MainHistory.setCustomerID(searchcust.getCustomerID());
                            MainHistory.setCustomerName(searchcust.getCustomerName());
                            custlist.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception ex) {
                    String exstr = ex.getMessage();
                }
            }
        });
    }

    public void SetDate() {
        new DatePickerDialog(getActivity(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

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
        if (flag == 1)
            fromdate.setText(format2.format(myCalendar.getTime()));
        else
            todate.setText(format2.format(myCalendar.getTime()));
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

    ArrayList<BillDBEntities> billList;
    ArrayList<PaymentDBEntities> paymentList;
    ArrayList<HistoryResultEntity> historylist;

    @Override
    public void OnGettingBillsFromPeriod(String results) {
        if (results != "null" && results.length() > 0) {
            try {
                Gson gson = new Gson();
                JSONArray objSub = new JSONArray(results);

                if (MainHistory.getType() == "Payment") {
                    paymentList = new ArrayList<PaymentDBEntities>();
                    if (objSub.length() > 0) {
                        for (int i = 0; i < objSub.length(); i++) {
                            String subIDInfo = objSub.getJSONObject(i).toString();
                            //create java object from the JSON object
                            PaymentDBEntities cat = gson.fromJson(subIDInfo, PaymentDBEntities.class);
                            paymentList.add(cat);
                        }
                        SetPaymentToList(paymentList);
                    } else {
                        list.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                        int unicode = 0x1F61E;
                        String emoji = getEmijoByUnicode(unicode);
                        String text = "There are no records...";
                        message.setText(text + emoji);
                    }
                } else if (MainHistory.getType() == "Bill") {
                    billList = new ArrayList<BillDBEntities>();
                    if (objSub.length() > 0) {
                        for (int i = 0; i < objSub.length(); i++) {
                            String subIDInfo = objSub.getJSONObject(i).toString();
                            //create java object from the JSON object
                            BillDBEntities cat = gson.fromJson(subIDInfo, BillDBEntities.class);
                            billList.add(cat);
                        }
                        SetBillsToList(billList);
                    } else {
                        list.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                        int unicode = 0x1F61E;
                        String emoji = getEmijoByUnicode(unicode);
                        String text = "Currently no records...";
                        message.setText(text + emoji);
                    }
                } else if (MainHistory.getType() == "All") {
                    historylist = new ArrayList<HistoryResultEntity>();
                    if (objSub.length() > 0) {
                        for (int i = 0; i < objSub.length(); i++) {
                            String subIDInfo = objSub.getJSONObject(i).toString();
                            //create java object from the JSON object
                            HistoryResultEntity cat = gson.fromJson(subIDInfo, HistoryResultEntity.class);
                            historylist.add(cat);
                        }
                        SetHistoryToList(historylist);
                    } else {
                        list.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                        int unicode = 0x1F61E;
                        String emoji = getEmijoByUnicode(unicode);
                        String text = "Currently no records...";
                        message.setText(text + emoji);
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            Toast.makeText(getActivity(), "Some problem occured, history not getting", Toast.LENGTH_SHORT).show();
        }
    }

    AllPaymentListAdaptor subAdap;
    public void SetPaymentToList(ArrayList<PaymentDBEntities> payment) {
        if (payment.size() > 0) {
            message.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            subAdap = new AllPaymentListAdaptor(getActivity(), R.layout.historyresult1, payment);
            list.setAdapter(subAdap);
        }
    }

    AllBillListAdaptor subAdap1;
    public void SetBillsToList(ArrayList<BillDBEntities> bill) {
        if (bill.size() > 0) {
            message.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            subAdap1 = new AllBillListAdaptor(getActivity(), R.layout.historyresult1, bill, this);
            list.setAdapter(subAdap1);
        }
    }

    AllHistoryAdaptor subAdap2;
    public void SetHistoryToList(ArrayList<HistoryResultEntity> history) {
        if (history.size() > 0) {
            message.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            subAdap2 = new AllHistoryAdaptor(getActivity(), R.layout.historyresult1, history, this);
            list.setAdapter(subAdap2);
        }
    }

    Button btn1;
    ProgressDialog pDialog;
    Bitmap bitmap;
    @Override
    public void OnImageButton(BillDBEntities data, Button btn) {
        try {
            btn1 = btn;
            final LoadImage p = new LoadImage();
            p.execute(data.getImageUrl());
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (p.getStatus() == AsyncTask.Status.RUNNING) {
                        p.cancel(true);
                        pDialog.dismiss();
                        Toast.makeText(getActivity(), "Network Problem, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000 * 30);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void OnImageButtonHistory(HistoryResultEntity data, Button btn) {
        try {
            btn1 = btn;
            final LoadImage p = new LoadImage();
            p.execute(data.getImageUrl());
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (p.getStatus() == AsyncTask.Status.RUNNING) {
                        p.cancel(true);
                        pDialog.dismiss();
                        Toast.makeText(getActivity(), "Network Problem, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000 * 30);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                pDialog = new ProgressDialog(getActivity());
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setMessage("Loading Image ....");
                pDialog.show();
            } catch (Exception e) {
                e.getMessage();
            }
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            pDialog.dismiss();
            if (image != null) {
                PopUp(image);
            } else {
                Toast.makeText(getActivity(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void PopUp(Bitmap image) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.showimage, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.update();

        ImageView img = (ImageView) popupView.findViewById(R.id.billimage);
        img.setImageBitmap(image);
        Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
        btnDismiss.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(btn1, Gravity.CENTER_HORIZONTAL, 5, 25);
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
