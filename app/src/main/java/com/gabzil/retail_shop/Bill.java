package com.gabzil.retail_shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bill extends Fragment implements OnSMSTaskCompleted {
    Button capturebtn, deletebtn;
    Uri imageUri = null;
    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    Activity CameraActivity = null;
    public static ImageView showImg = null;
    private MyOpenHelper db;
    PhotoEntity photodata = new PhotoEntity();
    AutoCompleteTextView customerET;
    ListView custlist;
    EditText amountET, remarkET, itemsET;
    String SearchStr;
    List<ShopDBEntities> shop;
    List<CustomerDBEntities> countries;
    CustomerDBEntities data, searchcust;
    boolean flag = false;
    BillDBEntities MainBill = new BillDBEntities();

    public Bill() {
        // Required empty public constructor
    }

    public Bill(CustomerDBEntities data) {
        this.data = data;
        this.flag = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bill, container, false);
        getActivity().setTitle("Bill");
        setHasOptionsMenu(true);
        DeclareVariables(v);
        ArrayList<BillDBEntities> bills =  new ArrayList<BillDBEntities>(db.getAllBillInfo());

        capturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (size == 1) {
                    Toast.makeText(getActivity(), "Only one image can capture!!!", Toast.LENGTH_SHORT).show();
                } else {
                    getCameraIntent();
                }
            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Decision");
                alertDialogBuilder.setIcon(R.drawable.alert_icon);
                alertDialogBuilder.setMessage("Do You Want To Delete???");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showImg.setImageBitmap(null);
                        deletebtn.setVisibility(View.GONE);
                        size = size - 1;
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });

        SetCustName();
        return v;
    }

    public void DeclareVariables(View v) {
        customerET = (AutoCompleteTextView) v.findViewById(R.id.customer);
        custlist = (ListView) v.findViewById(R.id.custlist);
        amountET = (EditText) v.findViewById(R.id.amount);
        itemsET = (EditText) v.findViewById(R.id.items);
        remarkET = (EditText) v.findViewById(R.id.remark);
        showImg = (ImageView) v.findViewById(R.id.showImg1);
        deletebtn = (Button) v.findViewById(R.id.deletebtn);
        capturebtn = (Button) v.findViewById(R.id.photo);
        db = new MyOpenHelper(getActivity());
        shop = db.getAllShops();
        if (shop.size() == 0) {
            OpenShopScreen(getActivity());
        } else {
            MainBill.setShopID(shop.get(0).getShopID());
            MainBill.setShopName(shop.get(0).getShopName());
            MainBill.setUserID(shop.get(0).getUserID());
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.cancel).setVisible(true);
        menu.findItem(R.id.save).setVisible(true);
        menu.findItem(R.id.action_refresh).setVisible(false);

        MenuItem cancel = menu.findItem(R.id.cancel);
        cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Fragment fragment = new Customer();
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), fragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
        });

        final MenuItem save = menu.findItem(R.id.save);
        save.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                setBillInformation();
                if (!ValidateData()) {
                    SaveDataLocally();
                }
                return true;
            }
        });

        super.onPrepareOptionsMenu(menu);
    }

    public void setBillInformation() {
        try {
            MainBill.setBillAmount(Double.valueOf(amountET.getText().toString().trim()));
            MainBill.setPendingAmount(MainBill.getBillAmount());
            MainBill.setNoOfItems(itemsET.getText().toString().trim());
            MainBill.setRemark(remarkET.getText().toString().trim());
            MainBill.setIsPaid(false);
            MainBill.setPaidAmount(Double.valueOf(0));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    AllSearchAdaptor subList;

    public void SetCustName() {
        if (flag) {
            customerET.setEnabled(false);
            customerET.setTypeface(null, Typeface.BOLD_ITALIC);
            customerET.setText(data.getCustomerName());
            MainBill.setCustomerID(data.getCustomerID());
            MainBill.setCustomerName(data.getCustomerName());
        } else {
            customerET.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                    // TODO Auto-generated method stub
                    try {
                        SearchStr = customerET.getText().toString();
                        countries = db.getAllContactsBySearch(SearchStr);
                        subList = new AllSearchAdaptor(getActivity(), R.layout.customerlist, countries);
                        if (countries.size() > 0 && SearchStr.length() != 0)
                            custlist.setVisibility(View.VISIBLE);
                        else
                            custlist.setVisibility(View.GONE);
                        custlist.setAdapter(subList);
                        custlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                searchcust = countries.get(i);
                                customerET.setText(searchcust.getCustomerName());
                                MainBill.setCustomerID(searchcust.getCustomerID());
                                MainBill.setCustomerName(searchcust.getCustomerName());
                                custlist.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception ex) {
                        String exstr = ex.getMessage();
                    }
                }
            });
        }
    }

    private boolean ValidateData() {
        boolean error = false;
        String strmsg = "Please Enter ";

        if (customerET.getText().toString().trim().length() == 0) {
            strmsg += "Customer Name";
            error = true;
        }
        if (amountET.getText().toString().trim().length() == 0) {
            strmsg += ", Amount";
            error = true;
        }
        if (itemsET.getText().toString().trim().length() == 0) {
            strmsg += ", No of items";
            error = true;
        }

        if (error == true) {
            String replacedString = strmsg.replace("Please Enter ,", "Please Enter ");
            ShowAlert(replacedString);
        }
        return error;
    }

    public void ShowAlert(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(msg);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void OpenShopScreen(Activity act) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
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

    private void SaveDataLocally() {
        if (MainBill.getCustomerID() > 0) {
            List<ContactDBEntities> Contact = db.getAllContacts(MainBill.getCustomerID());
            MainBill.setMobileNo(Contact.get(0).getMobileNo());
            MainBill.setImagePath(photodata.getPath());
            final SaveBill p = new SaveBill();
            p.execute(MainBill);
        } else {
            Toast.makeText(getActivity(), "Customer not exist", Toast.LENGTH_SHORT).show();
        }
    }

    ProgressDialog pDialog;
    private class SaveBill extends AsyncTask<Object, String, Boolean> {
        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                pDialog = new ProgressDialog(getActivity());
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setMessage("Bill Saving, Please Wait...");
                pDialog.show();
            } catch (Exception e) {
                e.getMessage();
            }
        }

        protected Boolean doInBackground(Object[] params) {
            boolean reply1 = false;
            try {
                DataHelp dh = new DataHelp(getActivity());
                reply1 = dh.BillSbmt((BillDBEntities) params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return reply1;
        }

        protected void onPostExecute(Boolean reply) {
            pDialog.dismiss();
            DataHelp dh = new DataHelp(getActivity());
            if (reply) {
                data = db.getByCustomerID(MainBill.getCustomerID());
                Double outstanding = Double.parseDouble(data.getOutStanding());
                outstanding = outstanding + MainBill.getBillAmount();
                data.setOutStanding(String.valueOf(outstanding));
                ArrayList<ContactDBEntities> contactnos = new ArrayList<ContactDBEntities>(db.getAllContacts(data.getCustomerID()));
                data.setAllContact(contactnos);
                if (dh.CustomerSbmt(data)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Alert");
                    alertDialogBuilder.setIcon(R.mipmap.ic_done);
                    alertDialogBuilder.setMessage("Bill Successfully Added");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ConnectivityManager ConnectionManager=(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
                            if(networkInfo != null && networkInfo.isConnected()==true ) {
                                CallSmsService();
                            }
                            else {
                                Toast.makeText(getActivity(), "Internet connection is not available, so message is not send to the customer", Toast.LENGTH_SHORT).show();
                            }
                            Fragment fragment = new Customer();
                            getFragmentManager().popBackStack("Bill", getFragmentManager().POP_BACK_STACK_INCLUSIVE);
                            getFragmentManager().beginTransaction()
                                    .replace(((ViewGroup) getView().getParent()).getId(), fragment)
                                    .addToBackStack(null)
                                    .commit();
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                } else {
                    Toast.makeText(getActivity(), "Some problem occures, Bill not saved", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Bill not saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void CallSmsService() {
        final SendBillSMS p=new SendBillSMS(getActivity(), this);
        p.execute(MainBill);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (p.getStatus() == AsyncTask.Status.RUNNING) {
                    // My AsyncTask is currently doing work in doInBackground()
                    p.cancel(true);
                    Toast.makeText(getActivity(), "Network Problem, SMS may not send", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000 * 30);
    }

    @Override
    public void OnSMSTaskCompleted(String results) {
    }

    private void getCameraIntent() {
        /*************************** Camera Intent Start ************************/
        // Define the file-name to save photo taken by Camera activity
        String fileName = "Camera_Example.jpg";

        // Create parameters for Intent with filename

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.TITLE, fileName);

        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");

        // imageUri is the current activity attribute, define and save it for later usage
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        /**** EXTERNAL_CONTENT_URI : style URI for the "primary" external storage volume. ****/
        // Standard Intent action that can be sent to have the camera
        // application capture an image and return it.

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

        /*************************** Camera Intent End ************************/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == getActivity().RESULT_OK) {

                /*********** Load Captured Image And Data Start ****************/

                String imageId = convertImageUriToFile(imageUri, CameraActivity);
                //  Create and excecute AsyncTask to load capture image
                new LoadImagesFromSDCard().execute("" + imageId);
                /*********** Load Captured Image And Data End ****************/
            } else if (resultCode == getActivity().RESULT_CANCELED) {

                Toast.makeText(getActivity(), " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getActivity(), " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /************
     * Convert Image Uri path to physical path
     **************/
    int size;
    public String convertImageUriToFile(Uri imageUri, Activity activity) {

        Cursor cursor = null;
        int imageID = 0;

        try {
            /*********** Which columns values want to get *******/
            String[] proj = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            cursor = getActivity().getContentResolver().query(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );

            //  Get Query Data
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            //int orientation_ColumnIndex = cursor.
            //getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

            size = cursor.getCount();

            /*******  If size is 0, there are no images on the SD Card. *****/

            if (size == 0) {
//                imageDetails.setText("No Image");
            } else {

                int thumbID = 0;
                if (cursor.moveToFirst()) {

                    /**************** Captured image details ************/

                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    imageID = cursor.getInt(columnIndex);

                    thumbID = cursor.getInt(columnIndexThumb);

                    String Path = cursor.getString(file_ColumnIndex);

                    photodata.setPath(Path);
                    //String orientation =  cursor.getString(orientation_ColumnIndex);

                    String CapturedImageDetails = " CapturedImageDetails : \n\n"
                            + " ImageID :" + imageID + "\n"
                            + " ThumbID :" + thumbID + "\n"
                            + " Path :" + Path + "\n";
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )

        return "" + imageID;
    }

    // Class with extends AsyncTask class
    public class LoadImagesFromSDCard extends AsyncTask<String, Void, Void> {

        private ProgressDialog Dialog = new ProgressDialog(getActivity());

        Bitmap mBitmap;

        protected void onPreExecute() {
            /****** NOTE: You can call UI Element here. *****/

            // Progress Dialog
            Dialog.setMessage(" Loading image from Sdcard..");
            Dialog.show();
        }


        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {

            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;

            try {

                /**  Uri.withAppendedPath Method Description
                 * Parameters
                 *    baseUri  Uri to append path segment to
                 *    pathSegment  encoded path segment to append
                 * Returns
                 *    a new Uri based on baseUri with the given segment appended to the path
                 */

                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);

                /**************  Decode an input stream into a bitmap. *********/
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));

                if (bitmap != null) {

                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/

                    newBitmap = Bitmap.createScaledBitmap(bitmap, 170, 170, true);

                    bitmap.recycle();

                    if (newBitmap != null) {

                        mBitmap = newBitmap;
                    }
                }
            } catch (IOException e) {
                // Error fetching image, try to recover

                /********* Cancel execution of this task. **********/
                cancel(true);
            }
            return null;
        }

        protected void onPostExecute(Void unused) {

            // NOTE: You can call UI Element here.
            // Close progress dialog
            Dialog.dismiss();
            if (mBitmap != null) {
                showImg.setImageBitmap(mBitmap);
                deletebtn.setVisibility(View.VISIBLE);
            }
        }
    }
}