package com.gabzil.retail_shop;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yogesh on 2/29/2016.
 */
public class AllSearchAdaptor extends ArrayAdapter<CustomerDBEntities> {
    private List<CustomerDBEntities> SubsList;

    public AllSearchAdaptor(Activity activity, int customlist, List<CustomerDBEntities> subList) {
        super(activity, customlist, subList);
        this.SubsList = subList;
    }

    private class ViewHolder {
        TextView customer;

        CustomerDBEntities data;
    }

    ViewHolder holder;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.customerlist, null);

            holder = new ViewHolder();
            holder.customer = (TextView) convertView.findViewById(R.id.customer);
            final ViewHolder finalHolder = holder;

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.data = SubsList.get(position);
        holder.customer.setText(TextClean(holder.data.getCustomerName()) + ", " + TextClean(holder.data.getAddress())
                + ", " + TextClean(holder.data.getBuilding()) + ", " + TextClean(holder.data.getArea()));
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