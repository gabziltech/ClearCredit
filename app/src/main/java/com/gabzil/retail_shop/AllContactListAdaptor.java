package com.gabzil.retail_shop;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yogesh on 2/29/2016.
 */
public class AllContactListAdaptor extends ArrayAdapter<ContactDBEntities> {
    private ArrayList<ContactDBEntities> SubsList;
    private TaskEditContactComplete mCallback;
    private TaskDeleteContactComplete mCallback1;

    public AllContactListAdaptor(Activity activity, int contactlist, ArrayList<ContactDBEntities> subList, TaskEditContactComplete listner
            , TaskDeleteContactComplete listner1) {
        super(activity, contactlist, subList);
        this.SubsList = new ArrayList<ContactDBEntities>();
        this.SubsList.addAll(subList);
        this.mCallback = listner;
        this.mCallback1 = listner1;
    }

    private class ViewHolder {
        TextView contactNo;
        Button edit_contact,delete_contact;
        ContactDBEntities data;
    }

    ViewHolder holder = null;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.contactlist, null);

            holder = new ViewHolder();
            holder.contactNo = (TextView) convertView.findViewById(R.id.contactno);
            holder.edit_contact = (Button) convertView.findViewById(R.id.edit_contact);
            holder.delete_contact = (Button) convertView.findViewById(R.id.delete_contact);
            final ViewHolder finalHolder = holder;

            holder.edit_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.OnEditContact(finalHolder.data,position);
                }
            });
            holder.delete_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback1.OnDeleteContact(finalHolder.data,position);
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.data = SubsList.get(position);
        holder.contactNo.setText(TextClean(holder.data.getMobileNo()));

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