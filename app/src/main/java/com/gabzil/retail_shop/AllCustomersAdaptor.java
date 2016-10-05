package com.gabzil.retail_shop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Yogesh on 2/29/2016.
 */
public class AllCustomersAdaptor extends ArrayAdapter<CustomerDBEntities> {
    private ArrayList<CustomerDBEntities> SubsList;
    private TaskEditComplete mCallback;
    private TaskDeleteComplete mCallback1;
    private TaskBillComplete mCallback2;
    private TaskPaymentComplete mCallback3;
    private TaskGettingBillComplete mCallback4;
    private TaskDialComplete mCallback5;
    private MyOpenHelper db = new MyOpenHelper(getContext());

    public AllCustomersAdaptor(Activity activity, int customlist, ArrayList<CustomerDBEntities> subList,TaskEditComplete listner,
                               TaskDeleteComplete listner1,TaskBillComplete listner2,TaskPaymentComplete listner3,
                               TaskGettingBillComplete listner4,TaskDialComplete listner5) {
        super(activity, customlist, subList);
        this.SubsList = new ArrayList<CustomerDBEntities>();
        this.SubsList.addAll(subList);
        this.mCallback= listner;
        this.mCallback1= listner1;
        this.mCallback2= listner2;
        this.mCallback3= listner3;
        this.mCallback4=listner4;
        this.mCallback5=listner5;
    }

    private class ViewHolder {
        TextView custname,address,outstanding,status,days,cell;
        Button editbtn,deletebtn,billbtn,paymentbtn;
        LinearLayout linear;
        CustomerDBEntities data;
    }

    ViewHolder holder;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.customlist, null);

            holder = new ViewHolder();
            holder.custname = (TextView) convertView.findViewById(R.id.custname);
            holder.address = (TextView) convertView.findViewById(R.id.custaddr);
            holder.outstanding = (TextView) convertView.findViewById(R.id.amount);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.days = (TextView) convertView.findViewById(R.id.days);
            holder.cell = (TextView) convertView.findViewById(R.id.cell);
            holder.linear = (LinearLayout) convertView.findViewById(R.id.linear);
            holder.editbtn = (Button) convertView.findViewById(R.id.edit_customer);
            holder.deletebtn = (Button) convertView.findViewById(R.id.delete_customer);
            holder.billbtn = (Button) convertView.findViewById(R.id.bill_customer);
            holder.paymentbtn = (Button) convertView.findViewById(R.id.payment_customer);
            final ViewHolder finalHolder = holder;

            holder.editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.OnEditButton(finalHolder.data);
                }
            });
            holder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Double.parseDouble(finalHolder.data.getOutStanding()) == 0.0)
                        mCallback1.OnDeleteButton(finalHolder.data);
                    else
                        Toast.makeText(getContext(), "This customer have remaining outstanding, so can't delete this customer", Toast.LENGTH_SHORT).show();
                }
            });
            holder.billbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback2.OnBillButton(finalHolder.data);
                }
            });
            holder.paymentbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback3.OnPaymentButton(finalHolder.data);
                }
            });
            holder.linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback4.OnGettingBill(finalHolder.data);
                }
            });
            holder.cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback5.OnDialButton(finalHolder.data);
                }
            });

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.data = SubsList.get(position);
        holder.custname.setText(TextClean(holder.data.getCustomerName()));
        holder.address.setText(holder.data.getAddress() + ", " + holder.data.getBuilding() + ", " + holder.data.getArea()
                + ", " + holder.data.getCity());
        try {
            if (Double.valueOf(holder.data.getOutStanding()) < 0) {
                holder.outstanding.setText("Rs. " +String.format("%.2f", Double.parseDouble(holder.data.getOutStanding())).replace("-", "") + "/-");
                holder.status.setText("(Cr)");
            } else {
                holder.outstanding.setText( "Rs. " +String.format("%.2f",Double.parseDouble(holder.data.getOutStanding())) + "/-");
                holder.status.setText("(Dr)");
            }
            if ((Double.valueOf(holder.data.getAmount())) > Double.valueOf(holder.data.getOutStanding())) {
                holder.outstanding.setTextColor((Color.parseColor("#008000")));
            } else {
                holder.outstanding.setTextColor((Color.parseColor("#FF0000")));
            }
        } catch (Exception e) {
            e.getMessage();
        }

        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        int diffInDays = 0;
        String inputString1 = myFormat.format(new Date());
        String inputString2 = holder.data.getCustEntryDate();

        try {
            Date date1 = myFormat.parse(inputString1);
            Date date2 = myFormat.parse(inputString2);
            final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
            diffInDays = (int) ((date1.getTime() - date2.getTime())/ DAY_IN_MILLIS );
        } catch (Exception e) {
            e.printStackTrace();
        }

        int remainingdays = Integer.parseInt(holder.data.getCreditDays()) - diffInDays;
        holder.days.setText(remainingdays + " Day's left");

        ArrayList<ContactDBEntities> contactnos =  new ArrayList<ContactDBEntities>(db.getAllContacts(holder.data.getCustomerID()));
        holder.cell.setText("+91 "+contactnos.get(0).getMobileNo());
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