package com.gabzil.retail_shop;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class CustomerBillList extends Fragment implements TaskImageComplete {
    ListView BillList;
    TextView message;
    ArrayList<BillDBEntities> bills;
    AllBillListAdaptor subAdap;
    Bitmap bitmap;
    ProgressDialog pDialog;
    Button btn1;
    ArrayList<BillDBEntities> subList;

    public CustomerBillList() {
        // Required empty public constructor
    }

    public CustomerBillList(ArrayList<BillDBEntities> bills) {
        this.bills = bills;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customer_bill_list, container, false);
        getActivity().setTitle("Bill List");
        DeclareVariables(v);
        SetBillsToList(bills);

        return v;
    }

    public void SetBillsToList(ArrayList<BillDBEntities> bill) {
        if (bill.size() > 0){
            boolean flag=false;
            subAdap = new AllBillListAdaptor(getActivity(), R.layout.historyresult, bill,this,flag);
            BillList.setAdapter(subAdap);
        }
        else {
            BillList.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            int unicode = 0x1F61E;
            String emoji = Customer.getEmijoByUnicode(unicode);
            String text = "Currently no Bills...";
            message.setText(text + emoji);
        }
    }

    public void DeclareVariables(View v) {
        BillList = (ListView) v.findViewById(R.id.billlist);
        message = (TextView) v.findViewById(R.id.msg);
    }

    @Override
    public void OnImageButton(BillDBEntities data,Button btn) {
        try {
            btn1 = btn;
            final LoadImage p=new LoadImage();
            p.execute(data.getImageUrl());
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (p.getStatus() == AsyncTask.Status.RUNNING) {
                        // My AsyncTask is currently doing work in doInBackground()
                        p.cancel(true);
                        pDialog.dismiss();
                        Toast.makeText(getActivity(), "Network Problem, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000 * 30);
        }
        catch (Exception e){
            e.getMessage();
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Loading Image ....");
                pDialog.show();
            }
            catch (Exception e) {
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
            if(image != null){
                PopUp(image);
            }else{
                Toast.makeText(getActivity(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void PopUp(Bitmap image){
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

}
