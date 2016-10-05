package com.gabzil.retail_shop;

import android.content.Context;
import android.os.AsyncTask;

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
public class SendBillSMS extends AsyncTask<Object, String, String> {
    private OnSMSTaskCompleted mCallback;

    public SendBillSMS(Context context, OnSMSTaskCompleted listner) {
        this.mCallback = listner;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    protected String doInBackground(Object[] params) {
        if (!isCancelled()) {
            BillDBEntities bill = (BillDBEntities) params[0];
            return getServerInfo(bill);
        } else
            return null;
    }

    @Override
    protected void onPostExecute(String message) {
        try {
            mCallback.OnSMSTaskCompleted(message);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private String getServerInfo(BillDBEntities bill) {
        Gson gson = new Gson();
        String BillString = gson.toJson(bill);
        String reply1="";
        URI uri = null;
        try {
            uri = new URI("http://gabretailprod.cloudapp.net:8080/BillDetails/retail/WebService/BillSMS");

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
            out.write(BillString.getBytes());
            out.flush();
            int code = conn.getResponseCode();
            String message = conn.getResponseMessage();
            InputStream in1 = conn.getInputStream();
            StringBuffer sb = new StringBuffer();
            try
            {
                int chr;
                while ((chr = in1.read()) != -1)
                {
                    sb.append((char) chr);
                }
                reply1 = sb.toString();
            }
            finally { in1.close(); }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
//        mProgress.dismiss();
        return reply1;
    }
}
