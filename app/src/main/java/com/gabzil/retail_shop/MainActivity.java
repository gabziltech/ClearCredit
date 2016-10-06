package com.gabzil.retail_shop;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends ActionBarActivity implements OnGettingSyncData {
    private String[] mNavigationDrawerItemTitles;
    ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    int position;
    MyOpenHelper db;
    List<ShopDBEntities> shop;
    public boolean mScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dh = new DataHelp(getApplicationContext());
        try {
            db = new MyOpenHelper(this);
            mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) findViewById(R.id.left_drawer);

            //Set the array if increasing the menu
            ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[7];
            drawerItem[0] = new ObjectDrawerItem(R.mipmap.ic_shop1, "Shop Details");
            drawerItem[1] = new ObjectDrawerItem(R.mipmap.ic_bill1, "Customer");
            drawerItem[2] = new ObjectDrawerItem(R.mipmap.ic_customer1, "Bill");
            drawerItem[3] = new ObjectDrawerItem(R.mipmap.ic_payment1, "Payment");
            drawerItem[4] = new ObjectDrawerItem(R.mipmap.ic_bill1, "Bill Outstanding");
            drawerItem[5] = new ObjectDrawerItem(R.mipmap.ic_history1, "History");
            drawerItem[6] = new ObjectDrawerItem(R.mipmap.ic_contact1, "Terms & Conditions");

            DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItem);
            mDrawerList.setAdapter(adapter);

            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

            mTitle = mDrawerTitle = getTitle();
            android.support.v7.widget.Toolbar toolbar = new android.support.v7.widget.Toolbar(getApplicationContext());
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_home);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF5722")));

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,
                    mDrawerLayout,
                    toolbar,
                    R.string.drawer_open,
                    R.string.drawer_close
            ) {
                /**
                 * Called when a drawer has settled in a completely closed state.
                 */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    getSupportActionBar().setTitle(mNavigationDrawerItemTitles[position]);
                }

                /**
                 * Called when a drawer has settled in a completely open state.
                 */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getSupportActionBar().setTitle(mDrawerTitle);
                }
            };

            mDrawerLayout.setDrawerListener(mDrawerToggle);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            if (savedInstanceState == null) {
                selectItem(0);
            }
        } catch (Exception Ex) {
            Log.println(3, "Err", Ex.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.search_item:
                item.setVisible(false);
                return true;

            case R.id.cancel:
                item.setVisible(false);
                return true;

            case R.id.save:
                item.setVisible(false);
                return true;

            case R.id.action_sync:
                item.setVisible(true);
                shop = db.getAllShops();
                if (shop.size() > 0) {
                    ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected() == true) {
                        List<DateEntities> DateData = db.getSyncDate();
                        String Datestr = DateData.get(0).getSyncDate();
                        Date1 = Datestr;
                        SyncData(shop.get(0).getShopID());
                    } else {
                        OpenInternetSetting();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Shop is not present", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_user:
                shop = db.getAllShops();
                if (shop.size() == 0)
                    ShowPopUp();
                else
                    Toast.makeText(getApplicationContext(), "You are already using shop", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    PopupWindow popupWindow;
    EditText code;
    Button btnDismiss, btnSave;
    public void ShowPopUp() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_enter_code, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.update();

        code = (EditText) popupView.findViewById(R.id.code);
        btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
        btnSave = (Button) popupView.findViewById(R.id.save);

        btnSave.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                String c=code.getText().toString().trim();
                if(c.length()==0){
                    Toast.makeText(getApplicationContext(), "Please enter code", Toast.LENGTH_SHORT).show();
                } else if(c.length()<4) {
                    Toast.makeText(getApplicationContext(), "Please enter valid code", Toast.LENGTH_SHORT).show();
                }else{
                    SyncNewUser(Integer.parseInt(c));
                    popupWindow.dismiss();
                }
            }
        });

        btnDismiss.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL, 5, 25);
    }

    public void SyncData(int shopid) {
        try {
            ArrayList<BillDBEntities> bills = new ArrayList<BillDBEntities>(db.getAllBillInfo());
            ArrayList<PaymentDBEntities> payments = new ArrayList<PaymentDBEntities>(db.getAllPaymentInfo());
            final SyncShopInfo p = new SyncShopInfo(getApplicationContext(), this);
            mScanning = true;
            invalidateOptionsMenu();
            p.execute(shopid, bills, payments);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (p.getStatus() == AsyncTask.Status.RUNNING) {
                        mScanning = false;
                        invalidateOptionsMenu();
                        p.cancel(true);
                        Toast.makeText(getApplicationContext(), "Network Problem, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000 * 60 * 5);
            invalidateOptionsMenu();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void SyncNewUser(int code) {
        final SyncNewUser p = new SyncNewUser(getApplicationContext(), this);
        mScanning = true;
        invalidateOptionsMenu();
        p.execute(code);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (p.getStatus() == AsyncTask.Status.RUNNING) {
                    mScanning = false;
                    invalidateOptionsMenu();
                    p.cancel(true);
                    Toast.makeText(getApplicationContext(), "Network Problem, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000 * 30);
        invalidateOptionsMenu();
    }

    DataHelp dh;
    @Override
    public void OnGettingSyncData(String results) {
        mScanning = false;
        invalidateOptionsMenu();
        try {
            if (!results.equals("null") & results.length() > 0) {
                JSONObject obj = new JSONObject(results);
                String subIDInfo = obj.toString();
                Gson gson = new Gson();
                ShopDBEntities shop = gson.fromJson(subIDInfo, ShopDBEntities.class);
                dh.ShopSbmt(shop);
                try {
                    JSONArray objSub = obj.getJSONArray("AllCustomer");
                    for (int i = 0; i < objSub.length(); i++) {
                        String subIDInfo1 = objSub.getJSONObject(i).toString();
                        CustomerDBEntities customer = gson.fromJson(subIDInfo1, CustomerDBEntities.class);
                        dh.CustomerSbmt(customer);
                    }
                    SetAlert("Sync Successfully Done");
                } catch (Exception e) {
                    e.getMessage();
                }
            }else if(results.equals("null")){
                Toast.makeText(getApplicationContext(), "Invalid code", Toast.LENGTH_SHORT).show();
            }else {
                dh.UpdateDate(Date1, 1);
                Toast.makeText(getApplicationContext(), "Some problem occured, Sync is not done", Toast.LENGTH_SHORT).show();
                mScanning = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        invalidateOptionsMenu();
    }

    public void SetAlert(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setIcon(R.drawable.alert_icon);
        alertDialogBuilder.setMessage(msg);

        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (!mScanning) {
            menu.findItem(R.id.action_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.action_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    String Date1;

    private void selectItem(int position) {
        this.position = position;
        Fragment fragment = null;

        switch (position) {
            case 0:
                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                String str = format.format(new Date());
                List<DateEntities> DateData = db.getSyncDate();
                if (DateData.size() == 0) {
                    dh.UpdateDate(str, 0);
                } else {
                    String Datestr = DateData.get(0).getSyncDate();
                    Date1 = Datestr;
                    if (!str.equals(Datestr)) {
                        shop = db.getAllShops();
                        if (shop.size() > 0) {
                            ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected() == true) {
                                dh.UpdateDate(str, 1);
                                SyncData(shop.get(0).getShopID());
                            } else {
                                OpenInternetSetting();
                            }
                        }
                    }
                }
                fragment = new ShopDetails();
                break;
            case 1:
                fragment = new Customer();
                break;
            case 2:
                fragment = new Bill();
                break;
            case 3:
                fragment = new Payment();
                break;
            case 4:
                fragment = new BillOutstanding();
                break;
            case 5:
                fragment = new History();
                break;
            case 6:
                fragment = new Contact();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
                    .addToBackStack("" + fragment.getClass().getSimpleName())
                    .commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            getSupportActionBar().setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    boolean second,flag = false;
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START))
            mDrawerLayout.closeDrawer(Gravity.START);
        else {
            int count = getFragmentManager().getBackStackEntryCount();
            String name = getFragmentManager().getBackStackEntryAt(count-1).getName();
            String name1;
            try {
                while (name == null || count > 2) {
                    try {
                        name1 = getFragmentManager().getBackStackEntryAt(count-2).getName();
                        if (name == null) {
                            getFragmentManager().popBackStack();
                            count = count - 1;
                        } else if (name.equals(name1)) {
                            getFragmentManager().popBackStack();
                            count = count - 1;
                        } else
                            count = count - 1;
                        name = getFragmentManager().getBackStackEntryAt(count-1).getName();
                    }
                    catch (Exception e) {
                        e.getMessage();
                    }
                }
                Button Cancel = (Button) findViewById(R.id.cancel);
                if (Cancel.getVisibility() == View.VISIBLE) {
                    TextView Name = (TextView) findViewById(R.id.name);
                    Button Search = (Button) findViewById(R.id.search);
                    AutoCompleteTextView SearchText = (AutoCompleteTextView) findViewById(R.id.search_text);

                    Cancel.setVisibility(View.GONE);
                    Name.setVisibility(View.VISIBLE);
                    SearchText.setText("");
                    SearchText.setVisibility(View.GONE);
                    Search.setVisibility(View.VISIBLE);

                    Fragment fragment = new Customer();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .commit();

                    flag = true;
                }
            } catch (Exception e) {
                e.getMessage();
            }
            if (count == 1) {
                if (!second) {
                    Toast.makeText(getApplication(), "Press back again to close application", Toast.LENGTH_SHORT).show();
                    second = true;
                } else {
                    second = false;
                    this.finish();
                }
            } else {
                if (flag == false) {
                    if (count != 1)
                        getFragmentManager().popBackStack();
                }
                else
                    flag = false;
            }
        }
    }

    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void OpenInternetSetting() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Network settings");
        alertDialog.setMessage("Network is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}