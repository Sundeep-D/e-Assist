package com.example.sundeep.egen_2;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.wang.avi.AVLoadingIndicatorView;

public class Main_screen extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{

    TextView dashboard_tv,username_tv;
    RelativeLayout mainscreen_root_layout;
    CircleImageView user_circle_image;
    SQLiteDatabase db;
    Cursor c;
    String UID,U_NAME,REG_STATUS="google_auth_done",U_PHOTO_URL;
    AlertDialog.Builder dialog;
     AlertDialog show;
    AlertDialog show_alert;
    DatabaseReference mDatabaseReference;
    ArrayList<String> user_details=new ArrayList<String>();
    ArrayList<String> global_user_details=new ArrayList<String>();
    String[] user_details_splitted;
    private String MAIN_REG_STATUS;
    AVLoadingIndicatorView avi;
    Boolean MAIN_REG_STATUS_BOOLEAN=false;
    Typeface exo_font;



    private NetworkStateReceiver networkStateReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        dashboard_tv=findViewById(R.id.dashboard_textview);
        username_tv=findViewById(R.id.username_textview);
        mainscreen_root_layout=findViewById(R.id.mainscreen_root_layout);
        user_circle_image=findViewById(R.id.user_image_circleImage);

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Century Gothic.ttf");
        username_tv.setTypeface(custom_font1);
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/bold.TTF");
        dashboard_tv.setTypeface(custom_font2);
        exo_font = Typeface.createFromAsset(getAssets(),  "fonts/Exo2-Medium.ttf");

         dialog=new AlertDialog.Builder(this);

         avi=findViewById(R.id.user_image_load_progress);

         avi.show();  //starts the loading progress of user image load


        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));


        //***********************  FETCH UID FROM LOCAL DB***********************************************

        try
        {
            db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);

            String aa="1";

            c = db.rawQuery("SELECT * FROM auth WHERE sno='" + aa + "'", null);
            if (c.moveToFirst()) {
                UID = c.getString(1);
//                U_NAME=c.getString(3);
//                U_PHOTO_URL=c.getString(4);
            }

        }
        catch (Exception e)

        {
            Snackbar snackbar = Snackbar
                    .make(mainscreen_root_layout, "Database Corrupted", Snackbar.LENGTH_LONG);
            snackbar.show();

        }

        //***********************  FETCH UID FROM LOCAL DB***********************************************



        decide_table_auth_or_user();


        //*************************DATA FROM CLOUD ***********************************************


        mDatabaseReference= FirebaseDatabase.getInstance().getReference("EGEN_USER_DETAILS");
        mDatabaseReference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    String usrs=child.getValue(String.class);


                    try
                    {

                        //global_user_details.clear();
                        global_user_details.add(usrs);
                        //Toast.makeText(getApplicationContext(),""+global_user_details.size(),Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Could'nt fetch Global User Details",Toast.LENGTH_LONG).show();
                    }


                    try {
                        if(usrs.contains(UID))
                        {
                            user_details.clear();
                            user_details.add(usrs);
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Database 1 "+e,Toast.LENGTH_LONG).show();
                    }


                    if(user_details.size()!=0)

                    {

                          try
                         {

                            user_details_splitted=user_details.get(0).split("_");
                         }
                        catch (Exception e)
                          {
                        Toast.makeText(getApplicationContext(),"Database 2 "+e,Toast.LENGTH_LONG).show();
                         }

                    }

                }

                sync();
                check_MAIN_REG_status();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //*************************DATA FROM CLOUD ***********************************************



    }//*************************************** ON CREATE **********************************************************

    private void load_user_image() {

        Picasso.get().load(U_PHOTO_URL.replaceAll("----------", "_")).fit().centerCrop().into(user_circle_image, new Callback() {
            @Override
            public void onSuccess() {
                avi.hide();
            }

            @Override
            public void onError(Exception e) {

            }

        });
    }


    private void sync()
    {

        if(MAIN_REG_STATUS_BOOLEAN)
        {
            if(user_details_splitted!=null)
            {

                try
                {
                    U_NAME=user_details_splitted[2];
                    U_PHOTO_URL=user_details_splitted[1];
                    username_tv.setText(U_NAME);

                    load_user_image();

                    int sno1=1;

                    db = openOrCreateDatabase("USER_DETAILS", Context.MODE_PRIVATE, null);

                    db.execSQL("DROP TABLE user");

                    db.execSQL("CREATE TABLE IF NOT EXISTS user(sno1 VARCHAR,UID VARCHAR,PHOTOURL VARCHAR,NAME VARCHAR,MAILID VARCHAR,PHONE VARCHAR,AGE VARCHAR,GENDER VARCHAR,ADDRESS VARCHAR,LOCALITY VARCHAR,DISTRICT VARCHAR,STATE VARCHAR,COUNTRY VARCHAR,POSTAL_CODE VARCHAR,LATITUDE VARCHAR,LONGITUDE VARCHAR,BLOOD_GROUP VARCHAR,BMI VARCHAR,SINUS VARCHAR,DIABETES VARCHAR,CONTACT_NAME_1 VARCHAR,CONATCT_NAME_2 VARCHAR,CONTACT_NUMBER_1 VARCHAR,CONTACT_NUMEBR_2 VARCHAR,AADHAAR_NUMBER VARCHAR,LICENSE_NUMBER VARCHAR,ORGANIZATIONAL_ID VARCHAR);");


                    db.execSQL("INSERT INTO user VALUES('" + sno1 + "','" + UID + "','" + user_details_splitted[1] + "','" + user_details_splitted[2] + "','" + user_details_splitted[3] + "','" + user_details_splitted[4] + "','" + user_details_splitted[5] + "','" + user_details_splitted[6] + "','" + user_details_splitted[7] + "','" + user_details_splitted[8] + "','" + user_details_splitted[9] + "','" + user_details_splitted[10] + "','" + user_details_splitted[11] + "','" + user_details_splitted[12] + "','" + user_details_splitted[13] + "','" + user_details_splitted[14] + "','" + user_details_splitted[15] + "','" + user_details_splitted[16] + "','" + user_details_splitted[17] + "','" + user_details_splitted[18] + "','" + user_details_splitted[19] + "','" + user_details_splitted[20] + "','" + user_details_splitted[21] + "','" + user_details_splitted[22] + "','" + user_details_splitted[23] + "','" + user_details_splitted[24] + "','" + user_details_splitted[25] + "');");

                    decide_table_auth_or_user();





                }
                catch (Exception e)
                {

                }


            }

        }
    }

    public void decide_table_auth_or_user()
    {


        db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);

                int a=1;
        c = db.rawQuery("SELECT * FROM auth WHERE sno='" + a + "'", null);
        if (c.moveToFirst()) {
            MAIN_REG_STATUS=c.getString(5);
            //Toast.makeText(getApplicationContext(),""+c.getString(5),Toast.LENGTH_LONG).show();

            if(MAIN_REG_STATUS.equals("not_done"))
            {




                try
                {
                    db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);

                    String aa="1";

                    c = db.rawQuery("SELECT * FROM auth WHERE sno='" + aa + "'", null);
                    if (c.moveToFirst()) {
                        UID = c.getString(1);
                        U_NAME=c.getString(3);
                        U_PHOTO_URL=c.getString(4);
                    }

                    username_tv.setText(U_NAME);
                    //Toast.makeText(getApplicationContext(),U_PHOTO_URL,Toast.LENGTH_LONG).show();


                    load_user_image();



                }
                catch (Exception e)

                {
                    Snackbar snackbar = Snackbar
                            .make(mainscreen_root_layout, "Database Corrupted", Snackbar.LENGTH_LONG);
                    snackbar.show();

                }



            }
            else
            {
                int sno1=1;


                db = openOrCreateDatabase("USER_DETAILS", Context.MODE_PRIVATE, null);

                c = db.rawQuery("SELECT * FROM user WHERE sno1='" + sno1 + "'", null);
                if (c.moveToFirst()) {

                    UID=c.getString(1);
                    U_PHOTO_URL = c.getString(2);
                    U_NAME=c.getString(3);

                    username_tv.setText(U_NAME);

                    load_user_image();


                }

            }

        }
    }

    private void check_MAIN_REG_status() {


        if(user_details_splitted!=null)
        {
            if(user_details_splitted[26].equals("main-reg-done"))
            {

                MAIN_REG_STATUS_BOOLEAN=true;

                db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);
                String status="done";

                Cursor c = db.rawQuery("SELECT * FROM auth", null);
                try {
                    String TABLE_NAME="auth",ColumnName="main_reg",rowID="1",ColumnSno="sno";
                    String updateSql=" UPDATE " + TABLE_NAME + " SET " + ColumnName + " = '" + status + "' WHERE " + ColumnSno + " = "+ rowID;
                    db.execSQL(updateSql);
                }
                catch (Exception e) {

                }



            }
            else
            {
                delay();

            }
        }





    }

    private void alert_MainRegPage() {

        LayoutInflater inflater=LayoutInflater.from(this);
        View alert_mainReg=inflater.inflate(R.layout.main_registration_alert,null);

        final TextView alert_heading=alert_mainReg.findViewById(R.id.alert_heading);
        final TextView alert_desc=alert_mainReg.findViewById(R.id.alert_desc);
        final TextView alert_why_i_have_to_register=alert_mainReg.findViewById(R.id.alert_why_reg);
        final TextView alert_remind_me_next_time=alert_mainReg.findViewById(R.id.alert_later);
        final Button alert_take_me_to_registration_page=alert_mainReg.findViewById(R.id.alert_take_me_to_registration_page);

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Century Gothic.ttf");
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/bold.TTF");
        alert_remind_me_next_time.setTypeface(custom_font1);
        alert_why_i_have_to_register.setTypeface(custom_font1);
        alert_desc.setTypeface(custom_font1);
        alert_take_me_to_registration_page.setTypeface(custom_font1);
        alert_heading.setTypeface(custom_font2);

        dialog.setView(alert_mainReg);
        show_alert = dialog.show();
    }

    public void userProfile(View view) {


        //dialog.setTitle("User Profile");
        LayoutInflater inflater=LayoutInflater.from(this);
        View login_layout=inflater.inflate(R.layout.user_profile,null);

        final CircleImageView user_image_circle_inflate_layout=login_layout.findViewById(R.id.user_image_circleImage_inflatelayout);
        final TextView user_name_inflate_layout=login_layout.findViewById(R.id.user_name_inflatelayout);
        final TextView user_UID_inflate_layout=login_layout.findViewById(R.id.user_UID_inflatelayout);
        final TextView logout_textview_inflate_layout=login_layout.findViewById(R.id.logout_tv);
        final TextView dismiss_textview_inflate_layout=login_layout.findViewById(R.id.dissmiss_tv);

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Century Gothic.ttf");
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/bold.TTF");
        user_UID_inflate_layout.setTypeface(custom_font1);
        dismiss_textview_inflate_layout.setTypeface(custom_font2);
        logout_textview_inflate_layout.setTypeface(custom_font2);
        user_name_inflate_layout.setTypeface(custom_font2);

        user_name_inflate_layout.setText(U_NAME);
        Picasso.get().load(U_PHOTO_URL.replaceAll("----------", "_")).into(user_image_circle_inflate_layout);
        user_UID_inflate_layout.setText(UID);


        dialog.setView(login_layout);
       // dialog.show();
         show = dialog.show();
    }

    public void dismiss_inflate(View view) {


        show.dismiss();

    }

    public void logout(View view) {
        db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);

        String sno="1";
        c = db.rawQuery("SELECT * FROM auth WHERE sno='" + sno + "'", null);
        if (c.moveToFirst()) {
            db.execSQL("DELETE FROM auth WHERE sno='" + sno + "'");

            Intent nxt=new Intent(getApplicationContext(),Intro.class);
            startActivity(nxt);
        }
    }

    public void why_i_have_to_register_weblink(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.sundeepdayalan.com"));
        startActivity(browserIntent);
    }

    private void delay() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent MainscreenIntent=new Intent(getApplicationContext(),Main_screen.class);
//                startActivity(MainscreenIntent);
                alert_MainRegPage();
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        new AlertDialog.Builder(this)
                .setTitle("Exiting the App")
                .setMessage("Are you sure?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        finish();
                        System.exit(0);
                        dialog.dismiss();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // The user is not sure, so you can exit or just stay
                dialog.dismiss();
            }
        }).show();

    }   //confirmation for closing the app on back press

    public void dismiss(View view) {
        show_alert.dismiss();

      //  Intent MainscreenIntent=new Intent(getApplicationContext(),Register.class);

//        Pair[] pairs=new Pair[2];
//        // pairs[0]=new Pair<View, String>(welcome_linear_layout,"welcome_transition");
//        pairs[0]=new Pair<View, String>(dashboard_tv,"dashboard_transition");
//        pairs[1]=new Pair<View, String>(username_tv,"username_transition");
//       // pairs[2]=new Pair<View, String>(user_circle_image,"userimage_transition");
//        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(Main_screen.this,pairs);
//        startActivity(MainscreenIntent,options.toBundle());
       // startActivity(MainscreenIntent);



    }   // for alert dismiss

    public void take_me_to_registration_page(View view) {

        show_alert.dismiss();

        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion<22)
        {
            Intent intro_screen_intent=new Intent(Main_screen.this,Register.class);
            startActivity(intro_screen_intent);
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


            Intent MainscreenIntent = new Intent(getApplicationContext(), Register.class);

            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(dashboard_tv, "dashboard_transition");
            pairs[1] = new Pair<View, String>(user_circle_image, "userimage_transition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Main_screen.this, pairs);
            startActivity(MainscreenIntent, options.toBundle());
        }
    }

    public void go_to_RegisterScreen(View view) {

        String release = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;
            if(sdkVersion<22)
            {
                Intent intro_screen_intent=new Intent(Main_screen.this,Register.class);
                startActivity(intro_screen_intent);
            }


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


            Intent MainscreenIntent = new Intent(getApplicationContext(), Register.class);

            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(dashboard_tv, "dashboard_transition");
            pairs[1] = new Pair<View, String>(user_circle_image, "userimage_transition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Main_screen.this, pairs);
            startActivity(MainscreenIntent, options.toBundle());
        }
    }

    public void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void networkAvailable() {
//        Snackbar snackbar = Snackbar
//                .make(mainscreen_root_layout, "Back online", Snackbar.LENGTH_LONG);
//        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.custom_green));
//        snackbar.getView().setMinimumHeight(5);
//        TextView mainTextView = (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
//            mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        else
//            mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
//
//        mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
//        mainTextView.setTypeface(exo_font);
//        snackbar.show();
        avi.show();

        load_user_image();
    }

    @Override
    public void networkUnavailable() {
        Snackbar snackbar = Snackbar
                .make(mainscreen_root_layout, "No Connectivity", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(Color.DKGRAY);
        snackbar.getView().setMinimumHeight(5);

        TextView mainTextView = (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mainTextView.setTypeface(exo_font);

        snackbar.show();

        avi.hide();

    }

    public void Scan_Qr_at_emergency_situations(View view) {

        Intent intent=new Intent(Main_screen.this,QR_scan.class);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100 && resultCode == RESULT_OK)
        {
            if(data!=null)
            {
                Barcode barcode=data.getParcelableExtra("barcode");
                String QR_DATA=barcode.displayValue;
                String PRIMARY_KEY=" ";



                if(QR_DATA.contains("uid") &&  QR_DATA.contains("xml"))
                {
                    try{
                        if(QR_DATA.contains("uid"))
                        {
                            int firstIndex = QR_DATA.indexOf("uid");
                            PRIMARY_KEY=QR_DATA.substring(firstIndex+5,firstIndex+17);

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Invalid QR",Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Invalid QR",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    PRIMARY_KEY=QR_DATA;
                }

                Boolean DATA_FOUND=false;
                String USER_DATA_TO_BE_SENT=" ";


                for (int i=0;i<global_user_details.size();i++)
                {
                    if(global_user_details.get(i).contains(PRIMARY_KEY))
                    {
                        USER_DATA_TO_BE_SENT=global_user_details.get(i);
                        DATA_FOUND=true;
                    }

                }

                if (DATA_FOUND)
                {
                   // Toast.makeText(getApplicationContext(),""+USER_DATA_TO_BE_SENT,Toast.LENGTH_LONG).show();

                    Intent MainscreenIntent=new Intent(Main_screen.this,Show_User_Details.class);
                    MainscreenIntent.putExtra("USER_ENTIRE_DETAILS", USER_DATA_TO_BE_SENT);
                    startActivity(MainscreenIntent);
                }
                else
                {
                    Snackbar snackbar = Snackbar
                            .make(mainscreen_root_layout, "Not a Registered User", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(Color.DKGRAY);
                    snackbar.getView().setMinimumHeight(5);
                    snackbar.show();
                }






            }
        }
    }
}
