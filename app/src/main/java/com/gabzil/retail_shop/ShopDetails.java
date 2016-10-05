package com.gabzil.retail_shop;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ShopDetails extends Fragment implements OnTaskCompleted, TaskEditUserComplete, TaskDeleteUserComplete, OnDeleteUserCompleted {
    EditText shopnameET, shopaddET, usernameET, usermobileET, pincodeET;
    Button shopsave, adduser, btnDismiss, btnSave;
    ListView list;
    TextView msg;
    private DataHelp dh;
    private MyOpenHelper db;
    List<ShopDBEntities> shop;
    Spinner city, userSpinner;
    ArrayAdapter<String> dataAdapter, UserAdapter;
    List<String> categories, USers;
    int shopedit = 0, edituser, position1;
    ShopDBEntities MainShop = new ShopDBEntities();
    UserDBEntities MainUser = new UserDBEntities();
    UserDBEntities MainUser1 = new UserDBEntities();
    PopupWindow popupWindow;
    boolean flag;

    public ShopDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_shop, container, false);
        getActivity().setTitle("Shop Details");
        setHasOptionsMenu(true);
        DeclareShopVariables(v);
        dh = new DataHelp(getActivity());
        db = new MyOpenHelper(getActivity());

        shop = db.getAllShops();
        if (shop.size() > 0) {
            setShopInfo();
        } else {
            list.setVisibility(View.GONE);
            msg.setVisibility(View.VISIBLE);
            int unicode = 0x1F61E;
            String emoji = getEmijoByUnicode(unicode);
            String text = "Currently no users";
            msg.setText(text + emoji);
            SaveButton();
        }

        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainUser = MainUser1;
                edituser = 0;
                ShowUserPopUp(MainUser, edituser);
            }
        });
        return v;
    }

    public void ShowUserPopUp(UserDBEntities user, final int edituser) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_layout, null);
        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.update();

        usernameET = (EditText) popupView.findViewById(R.id.username);
        usermobileET = (EditText) popupView.findViewById(R.id.usermobileno);
        userSpinner = (Spinner) popupView.findViewById(R.id.user);
        btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
        btnSave = (Button) popupView.findViewById(R.id.save);

        if (edituser == 1) {
            selectedUserEntity = user;
            usernameET.setText(user.getUserName());
            usermobileET.setText(user.getMobileNo());
            SetUserType();
            for (int j = 0; j <= 2; j++) {
                if (user.getUserType().equals(USers.get(j)))
                    userSpinner.setSelection(j);
                userSpinner.getSelectedItem().toString().trim();
            }
        } else {
            SetUserType();
            MainUser.setUserType(USers.get(0));
        }
        btnSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edituser == 1)
                    SaveButton();
                SaveUser(edituser);
            }
        });

        btnDismiss.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(adduser, Gravity.CENTER_HORIZONTAL, 5, 25);
    }

    private void SetUserType() {
        USers = new ArrayList<String>();
        USers.add("--- Select ---");
        USers.add("Owner");
        USers.add("Staff");
        UserAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, USers) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        userSpinner.setAdapter(UserAdapter);
    }

    public void DeclareShopVariables(View v) {
        shopsave = (Button) v.findViewById(R.id.saveshopbtn);
        shopnameET = (EditText) v.findViewById(R.id.shopname);
        shopaddET = (EditText) v.findViewById(R.id.shopaddress);
        city = (Spinner) v.findViewById(R.id.city);
        pincodeET = (EditText) v.findViewById(R.id.pincode);
        list = (ListView) v.findViewById(R.id.userlist);
        msg = (TextView) v.findViewById(R.id.msg);
        adduser = (Button) v.findViewById(R.id.adduserbtn);

        categories = new ArrayList<String>();
        categories.add("--- Select ---");
        categories.add("Pune");
        categories.add("Mumbai");
        categories.add("Nagpur");
        categories.add("Latur");
        dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categories) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        city.setAdapter(dataAdapter);
        MainShop.setCity(categories.get(0));
    }

    public void setShopInfo() {
        try {
            adduser.setVisibility(View.INVISIBLE);
            shop = db.getAllShops();
            MainShop = shop.get(0);
            user = new ArrayList<UserDBEntities>(db.getAllUsers(MainShop.getShopID()));
            MainShop.setAllUser(user);
            shopnameET.setText(MainShop.getShopName());
            shopaddET.setText(MainShop.getAddress());
            for (int j = 1; j <= 4; j++) {
                if (MainShop.getCity().equals(categories.get(j)))
                    city.setSelection(j);
                city.getSelectedItem().toString().trim();
            }
            pincodeET.setText(MainShop.getPincode());
            EnableEditText(false);
            getAllUserFromLocal(MainShop.getShopID());
            shopsave.setBackgroundResource(R.mipmap.ic_edit);
            shopsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserDBEntities Users = db.getByUserID(shop.get(0).getUserID());
                    if (Users.getUserType().equals("Owner")) {
                        EnableEditText(true);
                        shopedit = 1;
                        adduser.setVisibility(View.VISIBLE);
                        SaveButton();
                    } else {
                        Toast.makeText(getActivity(), "You are not owner, so you can't change the sgop information", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void SaveButton() {
        shopsave.setBackgroundResource(R.drawable.save);
        shopsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setShopInformation();
                if (!IsValidation()) {
                    SaveShopData();
                }
            }
        });
    }

    UserDBEntities selectedUserEntity;
    private void SaveUser(int edit) {
        UserDBEntities user = new UserDBEntities();
        if (!IsValidation1()) {
            user.setUserName(usernameET.getText().toString().trim());
            user.setMobileNo(usermobileET.getText().toString().trim());
            user.setUserType(userSpinner.getSelectedItem().toString().trim());
            user.setIsActive(true);
            if (MainShop.getAllUser() != null && MainShop.getAllUser().size() > 0) {
                if (edit == 0) {
                    MainShop.getAllUser().add(user);
                } else {
                    user.setUserID(selectedUserEntity.getUserID());
                    MainShop.getAllUser().remove(position1);
                    MainShop.getAllUser().add(position1, user);
                }
            } else {
                ArrayList<UserDBEntities> list1 = new ArrayList<UserDBEntities>();
                list1.add(user);
                MainShop.setAllUser(list1);
            }
            setToList();
            popupWindow.dismiss();
        }
    }

    public void setShopInformation() {
        MainShop.setShopName(shopnameET.getText().toString().trim());
        MainShop.setAddress(shopaddET.getText().toString().trim());
        MainShop.setCity(city.getSelectedItem().toString().trim());
        MainShop.setPincode(pincodeET.getText().toString().trim());
    }

    public void EnableEditText(boolean set) {
        if (set == false) {
            shopnameET.setEnabled(false);
            shopaddET.setEnabled(false);
            city.setEnabled(false);
            pincodeET.setEnabled(false);
        } else {
            shopnameET.setEnabled(true);
            shopaddET.setEnabled(true);
            city.setEnabled(true);
            pincodeET.setEnabled(true);
        }
    }

    public void setToList() {
        if (MainShop.getAllUser().size() > 0) {
            msg.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            userAdap1 = new AllUserListAdaptor(getActivity(), R.layout.userlist, MainShop.getAllUser(), this, this);
            list.setAdapter(userAdap1);
            list.setTextFilterEnabled(true);
        } else {
            msg.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }
    }

    private boolean IsValidation() {
        boolean error = false;
        String strmsg = "Please Enter ";

        if ((MainShop.getAllUser() == null) || (MainShop.getAllUser().size() == 0)) {
            strmsg += "All Shop Details and Atleast One User";
            error = true;
        } else {
            if (MainShop.getShopName().trim().length() == 0) {
                strmsg += "Shop Name";
                error = true;
            }
            if (shopaddET.getText().toString().trim().length() == 0) {
                strmsg += ", Shop Address";
                error = true;
            }
            if (city.getSelectedItem().toString().equals("--- Select ---")) {
                strmsg += ", City";
                error = true;
            }
            if (pincodeET.getText().toString().trim().length() == 0) {
                strmsg += ", Shop Pincode";
                error = true;
            }
            ArrayList<UserDBEntities> user1 = MainShop.getAllUser();
            int count = 0;
            for (int i = 0; i < user1.size(); i++) {
                if (user1.get(i).getUserType().equals("Owner")) {
                    count++;
                }
            }
            if (count == 0) {
                strmsg += ", Atleast one Owner";
                error = true;
            }
        }

        if (error == true) {
            String replacedString = strmsg.replace("Please Enter ,", "Please Enter ");
            ShowAlert(replacedString);
        }
        return error;
    }

    private boolean IsValidation1() {
        boolean error = false;
        String strmsg = "Please Enter ";

        if (usernameET.getText().toString().trim().length() == 0) {
            strmsg += "User Name";
            error = true;
        }
        if (usermobileET.getText().toString().trim().length() == 0) {
            strmsg += ", User Mobile No";
            error = true;
        } else if ((usermobileET.getText().toString().trim().length() < 10) || (usermobileET.getText().toString().trim().length() > 13)) {
            strmsg += ", User Mobile No of only 10 characters";
            error = true;
        }
        if (userSpinner.getSelectedItem().toString().equals("--- Select ---")) {
            strmsg += ", User Type";
            error = true;
        }

        if (error == true) {
            String replacedString = strmsg.replace("Please Enter ,", "Please Enter ");
            ShowAlert(replacedString);
        }
        return error;
    }

    AllUserListAdaptor userAdap1;
    ArrayList<UserDBEntities> user;

    public void getAllUserFromLocal(int shopid) {
        user = new ArrayList<UserDBEntities>(db.getAllUsers(shopid));
        if (user.size() > 0) {
            list.setVisibility(View.VISIBLE);
            msg.setVisibility(View.GONE);
            userAdap1 = new AllUserListAdaptor(getActivity(), R.layout.userlist, user, this, this);
            list.setAdapter(userAdap1);
            list.setTextFilterEnabled(true);
        } else {
            list.setVisibility(View.GONE);
            msg.setVisibility(View.VISIBLE);
            int unicode = 0x1F61E;
            String emoji = getEmijoByUnicode(unicode);
            String text = "Currently no users";
            msg.setText(text + emoji);
        }
    }

    private void SaveShopData() {
        final SaveShopInfo p = new SaveShopInfo(getActivity(), this);
        p.execute(MainShop);
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
    public void OnTaskCompleted(String results) {
        if (results != "null" && results.length() > 0) {
            Gson gson = new Gson();
            ShopDBEntities shop = gson.fromJson(results, ShopDBEntities.class);
            ArrayList<UserDBEntities> user1 = shop.getAllUser();
            for (int i = 0; i < user1.size(); i++) {
                if (user1.get(i).getUserType().equals("Owner")) {
                    shop.setUserID(user1.get(i).getUserID());
                    break;
                }
            }
            if (dh.ShopSbmt(shop) == true) {
                if (shopedit == 1 || edituser == 1) {
                    SetAlert("Shop Successfully Edited");
                } else {
                    SetAlert("Shop Successfully Added");
                }
            }
        } else {
            Toast.makeText(getActivity(), "Some problem occured, Shop is not added", Toast.LENGTH_SHORT).show();
        }
    }

    public String getEmijoByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    @Override
    public void OnEditUser(UserDBEntities data, final int position) {
        position1 = position;
        edituser = 1;
        ShowUserPopUp(data, edituser);
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

    public void SetAlert(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setIcon(R.mipmap.ic_done);
        alertDialogBuilder.setMessage(msg);

        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Fragment fragment = new Customer();
                getFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), fragment)
                        .addToBackStack("" + fragment.getClass().getSimpleName())
                        .commit();
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void OnDeleteUser(final UserDBEntities data, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setIcon(R.drawable.alert_icon);
        alertDialogBuilder.setMessage("Do You Want To Delete This User???");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (data.getUserID() == 0) {
                    MainShop.getAllUser().remove(position);
                    setToList();
                } else {
                    DeleteUserData(data);
                }
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

    private void DeleteUserData(UserDBEntities user) {
        ArrayList<UserDBEntities> list1 = new ArrayList<UserDBEntities>();
        list1.add(user);
        MainShop.setAllUser(list1);
        final DeleteUser p = new DeleteUser(getActivity(), this);
        p.execute(MainShop);
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
    public void OnDeleteUserCompleted(String results) {
        if (results != "null" && results.length() > 0) {
            try {
                int reply = Integer.parseInt(results);
                if (reply == 1) {
                    if (dh.UserDelete(MainShop)) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("Alert");
                        alertDialogBuilder.setIcon(R.mipmap.ic_done);
                        alertDialogBuilder.setMessage("User Successfully Deleted");

                        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setShopInfo();
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Some problem occured, Customer is not Deleted", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            Toast.makeText(getActivity(), "Some problem occured, Customer is not Deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
