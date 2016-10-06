package com.gabzil.retail_shop;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Yogesh on 2/29/2016.
 */
public class AllBillListAdaptor extends ArrayAdapter<BillDBEntities> {
    private ArrayList<BillDBEntities> SubsList;
    private TaskImageComplete mCallback;
    private boolean flag;

    public AllBillListAdaptor(Activity activity, int billlist, ArrayList<BillDBEntities> subList,TaskImageComplete listner,boolean flag) {
        super(activity, billlist, subList);
        this.SubsList = new ArrayList<BillDBEntities>();
        this.SubsList.addAll(subList);
        this.mCallback=listner;
        this.flag=flag;
    }

    private class ViewHolder {
        LinearLayout paid,balance,billtype,paymenttype,billnamelay;
        TextView date,billamount,paidamount,balamount,items,description,billname,date3;
        Button bill_image;
        BillDBEntities data;
    }

    ViewHolder holder;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.historyresult1, null);

            holder = new ViewHolder();
            holder.billtype = (LinearLayout) convertView.findViewById(R.id.billtype);
            holder.billnamelay = (LinearLayout) convertView.findViewById(R.id.billnamelay);
            holder.paymenttype = (LinearLayout) convertView.findViewById(R.id.paymenttype);
            holder.paid = (LinearLayout) convertView.findViewById(R.id.paid);
            holder.date3 = (TextView) convertView.findViewById(R.id.date3);
            holder.balance = (LinearLayout) convertView.findViewById(R.id.balance);
            holder.billname = (TextView) convertView.findViewById(R.id.billname);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.billamount = (TextView) convertView.findViewById(R.id.billamount);
            holder.paidamount = (TextView) convertView.findViewById(R.id.paidamount);
            holder.balamount = (TextView) convertView.findViewById(R.id.balanceamount);
            holder.items = (TextView) convertView.findViewById(R.id.items);
           // holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.bill_image = (Button) convertView.findViewById(R.id.bill_image);
            holder.billtype.setVisibility(View.VISIBLE);
            holder.paymenttype.setVisibility(View.GONE);
            final ViewHolder finalHolder = holder;
            try {
                holder.bill_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (finalHolder.data.getImageUrl() == "")
                            Toast.makeText(getContext(), "Image is not present", Toast.LENGTH_SHORT).show();
                        else
                            mCallback.OnImageButton(finalHolder.data,finalHolder.bill_image);
                    }
                });
            }
            catch (Exception e){
                e.getMessage();
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.data = SubsList.get(position);
        String date = holder.data.getUserDate();
        holder.date.setText(TextClean(date.substring(0, 12)));
        holder.billname.setText(holder.data.getCustomerName());
        holder.date3.setText(TextClean(date.substring(0, 12)));
        if (flag) {
            holder.billnamelay.setVisibility(View.VISIBLE);
            holder.date3.setVisibility(View.GONE);
        }else{
            holder.billnamelay.setVisibility(View.GONE);
            holder.date3.setVisibility(View.VISIBLE);
        }

        holder.billamount.setText("Rs " +String.format("%.2f", holder.data.getBillAmount()));
        holder.paidamount.setText("Rs " +String.format("%.2f", holder.data.getPaidAmount()));
        holder.balamount.setText("Rs " +String.format("%.2f", holder.data.getPendingAmount()));
        if(holder.data.getPendingAmount() == 0.0 || holder.data.getPendingAmount().toString().contains("E")){
            holder.paid.setVisibility(View.INVISIBLE);
            holder.balance.setVisibility(View.GONE);
        }else{
            holder.paid.setVisibility(View.VISIBLE);
            holder.balance.setVisibility(View.VISIBLE);
        }
        holder.items.setText(holder.data.getNoOfItems());
       // holder.description.setText(holder.data.getRemark());

        return convertView;
    }

    public static String TextClean(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }
}