package com.gabzil.retail_shop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Yogesh on 10/27/2015.
 */
public class SyncShopInfo extends AsyncTask<Object,String, String>
{
    private OnGettingSyncData mCallback;
    Context mContext;
    MyOpenHelper db;
    DataHelp dh;

    public SyncShopInfo(Context context, OnGettingSyncData listner) {
        try{
            this.mContext = context;
            this.mCallback = listner;
        }
        catch (Exception e){
            e.getMessage();
        }
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    protected String doInBackground(Object[] params) {
            if (!isCancelled()){
                Integer ShopID = (Integer) params[0];
                ArrayList<BillDBEntities> bills = (ArrayList<BillDBEntities>) params[1];
                ArrayList<PaymentDBEntities> payments = (ArrayList<PaymentDBEntities>) params[2];
                return getServerInfo(ShopID,bills,payments);
            }
            else
                return null;
    }

    @Override
    protected void onPostExecute(String message) {
        mCallback.OnGettingSyncData(message);
    }

    private String getServerInfo(int ShopID, ArrayList<BillDBEntities> bills, ArrayList<PaymentDBEntities> payments) {
        String reply1 = "";
        URI uri = null;
        Gson gson = new Gson();
        db = new MyOpenHelper(mContext);
        dh = new DataHelp(mContext);

        if (SaveImages(bills)) {
            String BillString = gson.toJson(bills);
            if (SaveBills(BillString) != "") {
                ArrayList<BillDBEntities> bill =  new ArrayList<BillDBEntities>(db.getAllBillInfo());
                for (int i=0; i<bill.size(); i++){
                    dh.DeleteBills(bills.get(i).getBillID());
                }
                String PaymentString = gson.toJson(payments);
                if (SavePayments(PaymentString) != "") {
                    ArrayList<PaymentDBEntities> payment =  new ArrayList<PaymentDBEntities>(db.getAllPaymentInfo());
                    for (int i=0; i<payment.size(); i++){
                        dh.DeletePayments(payment.get(i).getPaymentID());
                    }
                    try {
                        uri = new URI("http://gabretailprod.cloudapp.net:8080/ShopDetails/retail/WebService/Sync");
                        JSONStringer info = new JSONStringer()
                                .object()
                                .key("ShopInfo")
                                .object()
                                .key("ShopID").value(ShopID)
                                .endObject()
                                .endObject();

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
                        out.write(info.toString().getBytes());
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
                } else {
                    return "";
                }
            } else {
                return "";
            }
        } else {
            return "";
        }
        return reply1;
    }

    public boolean SaveImages(ArrayList<BillDBEntities> bills){
        for(int i=0; i<bills.size(); i++) {
            if (bills.get(i).getImagePath() == null)  {
                bills.get(i).setImageUrl("");
            }
            else {
                File imgFile = new File(bills.get(i).getImagePath());
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                String Imageurl = SendImageToServer(bitmap);
                if (Imageurl != "") {
                    bills.get(i).setImageUrl(Imageurl);
                }
                else {
                    return false;
                }
            }
        }
        return true;
    }

    private String SendImageToServer(Bitmap data2)
    {
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            data2.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] data = bao.toByteArray();

            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            final String URL1 = "http://gabretailprod.cloudapp.net:8080/BillDetails/retail/WebService/Image";
            HttpPost httpPost = new HttpPost(URL1);
            httpPost.setEntity(new ByteArrayEntity(data));

            HttpResponse response = httpClient.execute(httpPost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();
            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            System.out.println("Response: " + s);
            return String.valueOf(s).replace("\"","");
        } catch (Exception e) {
            return "";
        }
    }

    public String SaveBills(String Bills) {
        String reply1 = "";
        URI uri = null;

        try {
            uri = new URI("http://gabretailprod.cloudapp.net:8080/BillDetails/retail/WebService/InsertBill");
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
            out.write(Bills.getBytes());
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
        return reply1;
    }

    public String SavePayments(String Payments) {
        String reply1 = "";
        URI uri = null;

        try {
            uri = new URI("http://gabretailprod.cloudapp.net:8080/PaymentDetails/retail/WebService/Payment");
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
            out.write(Payments.toString().getBytes());
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
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return reply1;
    }
}
