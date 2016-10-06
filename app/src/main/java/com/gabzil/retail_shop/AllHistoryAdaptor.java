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
 * Created by javed on 8/11/2016.
 */
public class AllHistoryAdaptor extends ArrayAdapter<HistoryResultEntity> {
    private ArrayList<HistoryResultEntity> SubsList;
    private TaskImageHistoryComplete mCallback;

    public AllHistoryAdaptor(Activity activity, int historylist, ArrayList<HistoryResultEntity> subList,TaskImageHistoryComplete listner) {
        super(activity, historylist, subList);
        this.SubsList = new ArrayList<HistoryResultEntity>();
        this.SubsList.addAll(subList);
        this.mCallback=listner;
    }

    private class ViewHolder {
        LinearLayout paid,balance,billtype,paymenttype;
        TextView date,date2,billamount,paidamount,balamount,items,description,paymentamount,paymentmode,billname,payname,date3;
        Button bill_image;
        HistoryResultEntity data;
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
                holder.paymenttype = (LinearLayout) convertView.findViewById(R.id.paymenttype);
                holder.paid = (LinearLayout) convertView.findViewById(R.id.paid);
                holder.balance = (LinearLayout) convertView.findViewById(R.id.balance);
                holder.billname = (TextView) convertView.findViewById(R.id.billname);
                holder.payname = (TextView) convertView.findViewById(R.id.payname);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.date2 = (TextView) convertView.findViewById(R.id.date2);
                holder.date3 = (TextView) convertView.findViewById(R.id.date3);
                holder.billamount = (TextView) convertView.findViewById(R.id.billamount);
                holder.paidamount = (TextView) convertView.findViewById(R.id.paidamount);
                holder.balamount = (TextView) convertView.findViewById(R.id.balanceamount);
                holder.items = (TextView) convertView.findViewById(R.id.items);
              //  holder.description = (TextView) convertView.findViewById(R.id.description);
                holder.bill_image = (Button) convertView.findViewById(R.id.bill_image);

                holder.paymentamount = (TextView) convertView.findViewById(R.id.paymentamount);
                holder.paymentmode = (TextView) convertView.findViewById(R.id.paymentmode);
            final ViewHolder finalHolder = holder;
            try {
                holder.bill_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (finalHolder.data.getImageUrl() == "")
                            Toast.makeText(getContext(), "Image is not present", Toast.LENGTH_SHORT).show();
                        else
                            mCallback.OnImageButtonHistory(finalHolder.data, finalHolder.bill_image);
                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.data = SubsList.get(position);

        if(holder.data.getIsBill()) {
            holder.paymenttype.setVisibility(View.GONE);
            holder.billtype.setVisibility(View.VISIBLE);
            holder.billname.setText(holder.data.getCustomerName());
            holder.billamount.setText("Rs " + String.format("%.2f", holder.data.getBillAmount()));
            holder.paidamount.setText("Rs " + String.format("%.2f", holder.data.getPaidAmount()));
            holder.balamount.setText("Rs " + String.format("%.2f", holder.data.getPendingAmount()));
            if (holder.data.getPendingAmount() == 0.0 || holder.data.getPendingAmount().toString().contains("E")) {
                holder.paid.setVisibility(View.INVISIBLE);
                holder.balance.setVisibility(View.GONE);
            }
            else {
                holder.paid.setVisibility(View.VISIBLE);
                holder.balance.setVisibility(View.VISIBLE);
            }
            holder.items.setText(holder.data.getNoOfItems());
           // holder.description.setText(holder.data.getRemark());

            String date = holder.data.getUserDate();
            holder.date.setText(TextClean(date.substring(0, 12)));
        }
        else {
            holder.billtype.setVisibility(View.GONE);
            holder.paymenttype.setVisibility(View.VISIBLE);
            holder.payname.setText(holder.data.getCustomerName());
            holder.paymentmode.setText(String.valueOf(holder.data.getPaymentMode()));
            holder.paymentamount.setText("Rs " + String.format("%.2f", holder.data.getPayAmount()));
            holder.paymentmode.setText(String.valueOf(holder.data.getPaymentMode()));

            String date = holder.data.getUserDate();
            holder.date2.setText(TextClean(date.substring(0, 12)));
        }
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
