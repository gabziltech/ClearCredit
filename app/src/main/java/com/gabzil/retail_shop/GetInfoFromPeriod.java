package com.gabzil.retail_shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Yogesh on 10/27/2015.
 */
public class GetInfoFromPeriod extends AsyncTask<Object, String, String> {
    private Context mContext;
    public ProgressDialog mProgress;
    private OnGettingInfoFromPeriod mCallback;

    public GetInfoFromPeriod(Context context, OnGettingInfoFromPeriod listner) {
        this.mContext = context;
        this.mCallback = listner;
    }

    @Override
    public void onPreExecute() {
        mProgress = CreateProgress();
        mProgress.show();
    }

    @Override
    protected String doInBackground(Object[] params) {
        if (!isCancelled()) {
            return getServerInfo((HistoryEntities) params[0]);
        } else
            return null;
    }

    @Override
    protected void onPostExecute(String message) {
        try {
            mProgress.dismiss();
            mCallback.OnGettingBillsFromPeriod(message);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private ProgressDialog CreateProgress() {
        mProgress = new ProgressDialog(mContext);
        try {
            String msg = "Connecting... wait";
            String title = "Connecting to Internet";
            SpannableString ss1 = new SpannableString(title);
            ss1.setSpan(new RelativeSizeSpan(2f), 0, ss1.length(), 0);
            ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, ss1.length(), 0);

            SpannableString ss2 = new SpannableString(msg);
            ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
            ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);

            mProgress.setTitle(null);
            mProgress.setMessage(ss2);
            mProgress.setCanceledOnTouchOutside(false);

            return mProgress;
        } catch (Exception Ex) {
            return mProgress;
        }
    }

    private String getServerInfo(HistoryEntities history) {
        Gson gson = new Gson();
        String HistoryString = gson.toJson(history);
        String reply1 = "";
        URI uri = null;
        try {
            uri = new URI("http://gabretailprod.cloudapp.net:8080/HistoryDetails/retail/WebService/HistoryDetails");

            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Pigeon");
            conn.setChunkedStreamingMode(0);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.connect();
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.write(HistoryString.toString().getBytes());
            out.flush();
            int code = conn.getResponseCode();
            String message = conn.getResponseMessage();
            InputStream in1 = conn.getInputStream();
            StringBuffer sb = new StringBuffer();
            try {
                int chr;
                while ((chr = in1.read()) != -1) {
                    sb.append((char) chr);
                }
                reply1 = sb.toString();
            } finally {
                in1.close();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mProgress.dismiss();
        return reply1;
    }
}
