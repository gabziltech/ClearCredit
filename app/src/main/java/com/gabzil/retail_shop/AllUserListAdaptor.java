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
public class AllUserListAdaptor extends ArrayAdapter<UserDBEntities> {
    private ArrayList<UserDBEntities> SubsList;
    private TaskEditUserComplete mCallback;
    private TaskDeleteUserComplete mCallback1;


    public AllUserListAdaptor(Activity activity, int userlist, ArrayList<UserDBEntities> subList, TaskEditUserComplete listner
            , TaskDeleteUserComplete listner1) {
        super(activity, userlist, subList);
        this.SubsList = new ArrayList<UserDBEntities>();
        this.SubsList.addAll(subList);
        this.mCallback = listner;
        this.mCallback1 = listner1;
    }

    private class ViewHolder {
        TextView username;
        TextView mobileno;
        Button edit_user,delete_user;
        UserDBEntities data;
    }

    ViewHolder holder = null;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.userlist, null);

            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.mobileno = (TextView) convertView.findViewById(R.id.mobileno);
            holder.edit_user = (Button) convertView.findViewById(R.id.edit_user);
            holder.delete_user = (Button) convertView.findViewById(R.id.delete_user);
            final ViewHolder finalHolder = holder;

            holder.edit_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.OnEditUser(finalHolder.data,position);
                }
            });
            holder.delete_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback1.OnDeleteUser(finalHolder.data,position);
                }
            });

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.data = SubsList.get(position);
        holder.username.setText(TextClean(holder.data.getUserName()) + "/" + TextClean(holder.data.getUserType()));
        holder.mobileno.setText(TextClean(holder.data.getMobileNo()));

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