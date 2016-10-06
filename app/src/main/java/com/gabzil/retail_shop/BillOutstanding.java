package com.gabzil.retail_shop;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillOutstanding extends Fragment implements OnGettingOutStanding {
    private MyOpenHelper db;
    ListView list;
    TextView TotalOut, Extraout, DailyOut, WeekOut, FortNightOut, MonthOut;
    int shopid;
    ArrayList<BarEntry> entries;
    BarChart chart;
    LinearLayout barlayout;
    BarDataSet dataset;
    BarData data;
    ArrayList<String> labels;
    LimitLine ll;

    public BillOutstanding() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_billoutstanding, container, false);
        getActivity().setTitle("Bill Outstanding");
        barlayout = (LinearLayout) v.findViewById(R.id.myview1);

        entries = new ArrayList<BarEntry>();
        labels = new ArrayList<String>();
        labels.add("Extra");
        labels.add("Today");
        labels.add("8d");
        labels.add("15d");
        labels.add("30d");
        labels.add("Total");

        db = new MyOpenHelper(getActivity());
        try {
            List<ShopDBEntities> shop = db.getAllShops();
            shopid = shop.get(0).getShopID();
            DateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
            String currentDateTimeString = format.format(new Date());
            if(currentDateTimeString.contains("."))
                currentDateTimeString=currentDateTimeString.replace(".","");
            GetShopOutStanding(shopid, currentDateTimeString);
        } catch (Exception e) {
            OpenShopScreen();
        }
        DeclareVariables(v);
        return v;
    }

    private void DeclareVariables(View v) {
        TotalOut = (TextView) v.findViewById(R.id.totalout);
        Extraout = (TextView) v.findViewById(R.id.extraout);
        DailyOut = (TextView) v.findViewById(R.id.dailyout);
        WeekOut = (TextView) v.findViewById(R.id.weekout);
        FortNightOut = (TextView) v.findViewById(R.id.fortnightout);
        MonthOut = (TextView) v.findViewById(R.id.monthout);
    }

    GetOutStanding p;
    public void GetShopOutStanding(int shopid, String date) {
        p = new GetOutStanding(getActivity(), this);
        p.execute(String.valueOf(shopid), date);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (p.getStatus() == AsyncTask.Status.RUNNING) {
                    p.cancel(true);
                    p.mProgress.dismiss();
                    Toast.makeText(getActivity(), "Network Problem, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000 * 30);
    }

    @Override
    public void OnGettingOutStanding(String results) {
        try {
            if (results != "null" && results.length() > 0) {
                JSONObject obj = new JSONObject(results);
                String subIDInfo = obj.toString();
                Gson gson = new Gson();
                OutStandingEntity outstandings = gson.fromJson(subIDInfo, OutStandingEntity.class);

                TotalOut.setText("Rs " + String.format("%.2f", outstandings.getTotalOut()));
                Extraout.setText("Rs " + String.format("%.2f", outstandings.getExtraout()));
                DailyOut.setText("Rs " + String.format("%.2f", outstandings.getDailyOut()));
                WeekOut.setText("Rs " + String.format("%.2f", outstandings.getWeekOut()));
                FortNightOut.setText("Rs " + String.format("%.2f", outstandings.getDoubleWeekOut()));
                MonthOut.setText("Rs " + String.format("%.2f", outstandings.getMonthOut()));

                entries.add(new BarEntry(new BigDecimal(outstandings.getExtraout()).floatValue(), 0));
                entries.add(new BarEntry(new BigDecimal(outstandings.getDailyOut()).floatValue(), 1));
                entries.add(new BarEntry(new BigDecimal(outstandings.getWeekOut()).floatValue(), 2));
                entries.add(new BarEntry(new BigDecimal(outstandings.getDoubleWeekOut()).floatValue(), 3));
                entries.add(new BarEntry(new BigDecimal(outstandings.getMonthOut()).floatValue(), 4));
                entries.add(new BarEntry(new BigDecimal(outstandings.getTotalOut()).floatValue(), 5));
                dataset = new BarDataSet(entries, "Outstanding of Shop");
                dataset.setColors(ColorTemplate.COLORFUL_COLORS);

                BarChart chart = new BarChart(getActivity());
                barlayout.addView(chart);
                data = new BarData(labels, dataset);
                chart.setData(data);
                chart.setDoubleTapToZoomEnabled(false);
                chart.setPinchZoom(false);
                chart.setScaleEnabled(false);
                chart.setHighlightEnabled(false);
                chart.getLegend().setEnabled(false);
                chart.setDescription("Amount in rupees");
                chart.setDrawGridBackground(false);
                chart.animateY(2000);
                if (outstandings.getTotalOut() >= 10000) {
                    ll = new LimitLine(10000f, "10000 Outstanding");
                    ll.setLineColor(Color.RED);
                    ll.setLineWidth(1f);
                    ll.setTextColor(Color.BLACK);
                    ll.setTextSize(12f);
                    YAxis yAxis = chart.getAxisLeft();
                    yAxis.addLimitLine(ll);
                }
                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextSize(10f);
                xAxis.setSpaceBetweenLabels(2);
                YAxis yAxisRight = chart.getAxisRight();
                yAxisRight.setEnabled(false);
                TextView xAxisName = new TextView(getActivity());
                xAxisName.setText("Date");
            } else {
                Toast.makeText(getActivity(), "Some problem occured, OutStanding is not found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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