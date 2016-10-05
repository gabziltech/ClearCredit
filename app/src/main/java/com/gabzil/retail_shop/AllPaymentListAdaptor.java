package com.gabzil.retail_shop;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yogesh on 2/29/2016.
 */
public class AllPaymentListAdaptor extends ArrayAdapter<PaymentDBEntities> {
    private ArrayList<PaymentDBEntities> SubsList;

    public AllPaymentListAdaptor(Activity activity, int paymentlist, ArrayList<PaymentDBEntities> subList) {
        super(activity, paymentlist, subList);
        this.SubsList = new ArrayList<PaymentDBEntities>();
        this.SubsList.addAll(subList);
    }

    private class ViewHolder {
        TextView date,paymentamount,paymentmode;
        PaymentDBEntities data;
        LinearLayout billtype,paymenttype;
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
            holder.date = (TextView) convertView.findViewById(R.id.date2);
            holder.paymentamount = (TextView) convertView.findViewById(R.id.paymentamount);
            holder.paymentmode = (TextView) convertView.findViewById(R.id.paymentmode);
            holder.billtype.setVisibility(View.GONE);
            holder.paymenttype.setVisibility(View.VISIBLE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.data = SubsList.get(position);
        String date = holder.data.getUserDate();
        holder.date.setText(TextClean(date.substring(0, 12)));
        holder.paymentamount.setText("Rs " +String.format("%.2f", holder.data.getPayAmount()));
        holder.paymentmode.setText(String.valueOf(holder.data.getPaymentMode()));

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