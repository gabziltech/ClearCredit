package com.gabzil.retail_shop;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONStringer;

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
public class SyncNewUser extends AsyncTask<Object,String, String>
{
    private OnGettingSyncData mCallback;

    public SyncNewUser(Context context, OnGettingSyncData listner)
    {
        this.mCallback = listner;
    }

    @Override
    public void onPreExecute() { }

    @Override
    protected String doInBackground(Object[] params)
    {
        if (!isCancelled()){
            Integer Code = (Integer) params[0];
            return getServerInfo(Code);
        }
        else
            return null;
    }

    @Override
    protected void onPostExecute(String message)
    {
        mCallback.OnGettingSyncData(message);
    }

    private String getServerInfo(int Code)
    {
        String reply1="";
        URI uri = null;
        try {
            uri = new URI("http://gabretailprod.cloudapp.net:8080/ShopDetails/retail/WebService/Code");
            JSONStringer info = new JSONStringer()
                    .object()
                    .key("ShopInfo")
                    .object()
                    .key("Code").value(Code)
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
        catch(Exception ex) {
            ex.printStackTrace();
        }
//        reply1 = "{\"ShopID\":10,\"ShopName\":\"Balaji stores\",\"Address\":\"navi peth\",\"City\":\"Pune\",\"Pincode\":\"411030\",\"UserID\":32,\"EntryDate\":\"Aug 8, 2016 4:45:30 AM\",\"AllUser\":[{\"ShopID\":10,\"UserID\":32,\"UserName\":\"govind\",\"UserType\":\"Staff\",\"MobileNo\":\"8600733169\",\"IsActive\":false},{\"ShopID\":10,\"UserID\":33,\"UserName\":\"pravin\",\"UserType\":\"Owner\",\"MobileNo\":\"8986858283\",\"IsActive\":false},{\"ShopID\":10,\"UserID\":34,\"UserName\":\"pankaj\",\"UserType\":\"Owner\",\"MobileNo\":\"8986858382\",\"IsActive\":false},{\"ShopID\":10,\"UserID\":35,\"UserName\":\"yogesh\",\"UserType\":\"Owner\",\"MobileNo\":\"8983787343\",\"IsActive\":true}],\"AllCustomer\":[{\"ShopID\":10,\"CustomerID\":27,\"ShopName\":\"Balaji stores\",\"CustomerName\":\"A\",\"IsActive\":true,\"Address\":\"22\",\"Building\":\"sai\",\"Area\":\"kp\",\"City\":\"Nagpur\",\"Amount\":\"2580\",\"CreditDays\":\"\",\"OutStanding\":\"730.0\",\"EntryDate\":\"Aug 8, 2016 6:52:42 AM\",\"AllContact\":[{\"ShopID\":10,\"CustomerID\":27,\"ContactID\":27,\"MobileNo\":\"8600733169\"}]},{\"ShopID\":10,\"CustomerID\":28,\"ShopName\":\"Balaji stores\",\"CustomerName\":\"B\",\"IsActive\":true,\"Address\":\"23\",\"Building\":\"venkatesh\",\"Area\":\"kp\",\"City\":\"Pune\",\"Amount\":\"2580\",\"CreditDays\":\"\",\"OutStanding\":\"-50.0\",\"EntryDate\":\"Aug 8, 2016 7:07:24 AM\",\"AllContact\":[{\"ShopID\":10,\"CustomerID\":28,\"ContactID\":28,\"MobileNo\":\"8685848787\"}]},{\"ShopID\":10,\"CustomerID\":29,\"ShopName\":\"Balaji stores\",\"CustomerName\":\"raju\",\"IsActive\":true,\"Address\":\"12\",\"Building\":\"venkat\",\"Area\":\"kp\",\"City\":\"Pune\",\"Amount\":\"2589\",\"CreditDays\":\"\",\"OutStanding\":\"0.0\",\"EntryDate\":\"Aug 8, 2016 6:15:52 AM\",\"AllContact\":[{\"ShopID\":10,\"CustomerID\":29,\"ContactID\":29,\"MobileNo\":\"8600799797\"}]},{\"ShopID\":10,\"CustomerID\":31,\"ShopName\":\"Balaji stores\",\"CustomerName\":\"d\",\"IsActive\":true,\"Address\":\"23\",\"Building\":\"venkatesh Apartment,behind petrol pump navi peth\",\"Area\":\"kp\",\"City\":\"Pune\",\"Amount\":\"2580\",\"CreditDays\":\"\",\"OutStanding\":\"0.0\",\"EntryDate\":\"Aug 8, 2016 7:06:59 AM\",\"AllContact\":[{\"ShopID\":10,\"CustomerID\":31,\"ContactID\":31,\"MobileNo\":\"8689858382\"}]}]}";
        return reply1;
    }
}
