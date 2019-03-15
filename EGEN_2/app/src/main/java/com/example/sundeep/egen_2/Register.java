package com.example.sundeep.egen_2;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Pattern;

import cdflynn.android.library.checkview.CheckView;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class Register extends AppCompatActivity {

    TextView register_tv,identification_details_tv;
    static final int GALLERY_INTENT=1;
    RelativeLayout Register_root_layout;
    private ProgressDialog mprogress;
    StorageReference storage_reference;
    String user_profile_picture_url;
    CircleImageView user_profile_picture_Cirecle_image_view;
    SQLiteDatabase db;
    Cursor c;
    String UID,REG_STATUS="accepted_terms_and_conditions",U_NAME;
    PulsatorLayout pulsator;
    Uri download_url;
    CheckView verified_name,verified_mail,verified_age,verified_gender,verified_phone,verified_address,verified_locality,verified_district,verified_state,verified_country,verified_pincode;
    String[] arrOfAdd;
    String ADDRESS,LOCALITY,POSTAL_CODE,DISTRICT,STATE,COUNTRY,LATITUDE,LONGITUDE,MAILID,AGE,PHONE,GENDER;
    EditText NameED,MailED,AgeED,PhoneED,AddressED,LocalityED,DistrictED,StateED,CountryED,PincodeED;
    Typeface exo_font;
    Boolean name_clicked=false,mail_clicked=false,age_clicked=false,phone_clicked=false,address_clicked=false,locality_clicked=false,district_clicked=false,state_clicked=false,country_clicked=false,pincode_clicked=false,ED_UPDATED=false;
    Boolean selected_gender=false;
    ImageView male_imageview,female_imageview;
    CardView root_cardview;
    Button verify_button;
    DatabaseReference mDatabaseReference;
    ArrayList<String> user_details=new ArrayList<String>();
    String[] user_details_splitted;





    //for getting location

    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    private LocationAddressResultReceiver addressResultReceiver;


    private TextView currentAddTv;

    private Location currentLocation;

    private LocationCallback locationCallback;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_tv=findViewById(R.id.register_textview);
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/bold.TTF");
        register_tv.setTypeface(custom_font2);

        identification_details_tv=findViewById(R.id.identification_details_TV);
        exo_font = Typeface.createFromAsset(getAssets(),  "fonts/Exo2-Medium.ttf");
        identification_details_tv.setTypeface(exo_font);

        verify_button=findViewById(R.id.alert_take_me_to_registration_page_2);
        verify_button.setTypeface(exo_font);


        pulsator = (PulsatorLayout) findViewById(R.id.pulsator);


        Register_root_layout=findViewById(R.id.Register_rootLayout_Relative);

        mprogress=new ProgressDialog(this);

        storage_reference= FirebaseStorage.getInstance().getReference();

        user_profile_picture_Cirecle_image_view=findViewById(R.id.user_image_circleImage);

        root_cardview=findViewById(R.id.root_cardview);

        verified_name=findViewById(R.id.verified_animation_name);
        verified_mail=findViewById(R.id.verified_animation_mail);
        verified_age=findViewById(R.id.verified_animation_age);
        verified_phone=findViewById(R.id.verified_animation_phone);
        verified_address=findViewById(R.id.verified_animation_address);
        verified_locality=findViewById(R.id.verified_animation_locality);
        verified_district=findViewById(R.id.verified_animation_district);
        verified_state=findViewById(R.id.verified_animation_state);
        verified_country=findViewById(R.id.verified_animation_country);
        verified_pincode=findViewById(R.id.verified_animation_pincode);
        verified_gender=findViewById(R.id.verified_animation_gender);


        db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);

        try
        {
            db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);

            String a="1";

            c = db.rawQuery("SELECT * FROM auth WHERE sno='" + a + "'", null);
            if (c.moveToFirst()) {
                UID = c.getString(1);
                //U_NAME=c.getString(3);
                user_profile_picture_url=c.getString(4);

            }

//           load_user_image();

        }
        catch (Exception e)

        {

            Snackbar snackbar = Snackbar
                    .make(Register_root_layout, "Database corrupted", Snackbar.LENGTH_LONG);
            snackbar.show();
        }



        NameED=findViewById(R.id.name_ED);
        MailED=findViewById(R.id.mail_ED);
        AgeED=findViewById(R.id.age_ED);
        PhoneED=findViewById(R.id.phone_ED);
        AddressED=findViewById(R.id.address_ED);
        LocalityED=findViewById(R.id.locality_ED);
        DistrictED=findViewById(R.id.district_ED);
        StateED=findViewById(R.id.state_ED);
        CountryED=findViewById(R.id.Country_ED);
        PincodeED=findViewById(R.id.pincode_ED);

        custom_font_for_Edittexts();

        male_imageview=findViewById(R.id.male_img_view);
        female_imageview=findViewById(R.id.female_img_view);

        NameED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_edit_text_details();
            }
        });



        //for locaiton********************************* -----------X------------------------------------

        addressResultReceiver = new LocationAddressResultReceiver(new Handler());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                getAddress();
            }
        };


        startLocationUpdates();

        //for location********************************* -----------X------------------------------------




        //*************************DATA FROM CLOUD ***********************************************


        mDatabaseReference= FirebaseDatabase.getInstance().getReference("EGEN_USER_DETAILS");
        mDatabaseReference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    String usrs=child.getValue(String.class);

                    try {
                        if(usrs.contains(UID))
                        {
                            user_details.clear();
                            user_details.add(usrs);
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Server down. Cannot fetch details from Cloud Database 1",Toast.LENGTH_SHORT).show();
                    }


                    if(user_details.size()!=0)

                    {


                        try {

                            user_details_splitted = user_details.get(0).split("_");


                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Server down. Cannot fetch details from Cloud Database 2", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                update_details_in_Edit_text();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //*************************DATA FROM CLOUD ***********************************************




    }//********                 O N   C R E A T E                  ********************************************************************************************


//    public void decide_table_auth_or_user()
//    {
//
//
//        db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);
//
//        int a=1;
//        String MAIN_REG_STATUS;
//
//        c = db.rawQuery("SELECT * FROM auth WHERE sno='" + a + "'", null);
//        if (c.moveToFirst()) {
//            MAIN_REG_STATUS=c.getString(5);
//            //Toast.makeText(getApplicationContext(),""+c.getString(5),Toast.LENGTH_LONG).show();
//
//            if(MAIN_REG_STATUS.equals("not_done"))
//            {
//
//
//
//
//                try
//                {
//                    db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);
//
//                    String aa="1";
//
//                    c = db.rawQuery("SELECT * FROM auth WHERE sno='" + aa + "'", null);
//                    if (c.moveToFirst()) {
//                        UID = c.getString(1);
//                        U_NAME=c.getString(3);
//                        U_PHOTO_URL=c.getString(4);
//                    }
//
//                    username_tv.setText(U_NAME);
//                    //Toast.makeText(getApplicationContext(),U_PHOTO_URL,Toast.LENGTH_LONG).show();
//
//
//                    load_user_image();
//
//
//
//                }
//                catch (Exception e)
//
//                {
//                    Snackbar snackbar = Snackbar
//                            .make(Register_root_layout, "Database Corrupted", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//
//                }
//
//
//
//            }
//            else
//            {
//                int sno1=1;
//
//
//                db = openOrCreateDatabase("USER_DETAILS", Context.MODE_PRIVATE, null);
//
//                c = db.rawQuery("SELECT * FROM user WHERE sno1='" + sno1 + "'", null);
//                if (c.moveToFirst()) {
//
//                    UID=c.getString(1);
//                     = c.getString(2);
//                    U_NAME=c.getString(3);
//
//                    username_tv.setText(U_NAME);
//
//                    load_user_image();
//
//
//                }
//
//            }
//
//        }
//    }
//



    private void load_user_image() {

        Picasso.get().load(user_profile_picture_url.replaceAll("----------", "_")).fit().centerCrop().into(user_profile_picture_Cirecle_image_view, new Callback() {
            @Override
            public void onSuccess() {
                pulsator.start();
            }

            @Override
            public void onError(Exception e) {

            }

        });
    }

    private void update_details_in_Edit_text() {

        if(user_details_splitted!=null)
        {



        user_profile_picture_url=user_details_splitted[1];

        NameED.setText(user_details_splitted[2]);
        if(!user_details_splitted[3].equals(" "))
        MailED.setText(user_details_splitted[3]);
        if(!user_details_splitted[4].equals(" "))
        AgeED.setText(user_details_splitted[4]);
        if(!user_details_splitted[5].equals(" "))
        {
            verify_button.setText("UPDATE");
            PhoneED.setText(user_details_splitted[5]);
        }


        //gender
        if(user_details_splitted[6].equals("Male"))
        {
            GENDER="Male";
            selected_gender=true;
            male_imageview.setBackgroundColor(getResources().getColor(R.color.light_color));
            female_imageview.setBackgroundColor(getResources().getColor(R.color.WHITE));
        }
        else if(user_details_splitted[6].equals("Female"))
        {
            GENDER="Female";
            selected_gender=true;
            female_imageview.setBackgroundColor(getResources().getColor(R.color.light_color));
            male_imageview.setBackgroundColor(getResources().getColor(R.color.WHITE));
        }

        if(!user_details_splitted[7].equals(" "))
        AddressED.setText(user_details_splitted[7]);
        if(!user_details_splitted[8].equals(" "))
        LocalityED.setText(user_details_splitted[8]);
        if(!user_details_splitted[9].equals(" "))
        DistrictED.setText(user_details_splitted[9]);
        if(!user_details_splitted[10].equals(" "))
        StateED.setText(user_details_splitted[10]);
        if(!user_details_splitted[11].equals(" "))
        CountryED.setText(user_details_splitted[11]);
        if(!user_details_splitted[12].equals(" "))
        PincodeED.setText(user_details_splitted[12]);

        load_user_image();

        }

    }

    private void update_edit_text_details() {
        NameED.setText(U_NAME);
        AddressED.setText(ADDRESS);
        LocalityED.setText(LOCALITY);
        DistrictED.setText(DISTRICT);
        StateED.setText(STATE);
        CountryED.setText(COUNTRY);
        PincodeED.setText(POSTAL_CODE);
    }

    private void custom_font_for_Edittexts() {
        NameED.setTypeface(exo_font);
        MailED.setTypeface(exo_font);
        AgeED.setTypeface(exo_font);
        PhoneED.setTypeface(exo_font);
        AddressED.setTypeface(exo_font);
        PhoneED.setTypeface(exo_font);
        AddressED.setTypeface(exo_font);
        LocalityED.setTypeface(exo_font);
        DistrictED.setTypeface(exo_font);
        StateED.setTypeface(exo_font);
        CountryED.setTypeface(exo_font);
        PincodeED.setTypeface(exo_font);
    }


    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        new AlertDialog.Builder(this)
                .setTitle("Register later")
                .setMessage("Are you sure you want to exit registration?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                                        Intent MainscreenIntent=new Intent(getApplicationContext(),Main_screen.class);
                                        startActivity(MainscreenIntent);
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // The user is not sure, so you can exit or just stay
                dialog.dismiss();
            }
        }).show();

    }   //confirmation for going back to main page without registering


    public void upload_user_image(View view) {

        try
        {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"),GALLERY_INTENT);

        }
        catch (Exception e)
        {
            Snackbar snackbar = Snackbar
                    .make(Register_root_layout, "Unable to open gallery", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_INTENT)
        {
            mprogress.setMessage("Uploading...");
            mprogress.show();
            final Uri uri=data.getData();
            StorageReference file_path=storage_reference.child("USERS PROFILE PICTURES").child(uri.getLastPathSegment());


            file_path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pulsator.stop();


                    download_url=taskSnapshot.getDownloadUrl();
                    user_profile_picture_url=download_url.toString();
//                    Picasso.get().load(download_url).fit().centerCrop().into(user_profile_picture_Cirecle_image_view, new com.squareup.picasso.Callback() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                    });

                    Picasso.get()
                            .load(download_url).fit().centerCrop()
                            .into(user_profile_picture_Cirecle_image_view, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mprogress.dismiss();
                                    update_edit_text_details();
                                }

                                @Override
                                public void onError(Exception e) {

                                }

                            });


                    update_URL_in_local_database();



                }
            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
//                    Snackbar snackbar = Snackbar
//                            .make(Register_root_layout, ""+e, Snackbar.LENGTH_LONG);
//                    snackbar.show();
                    mprogress.dismiss();
                }
            });






        }
    }

    private void update_URL_in_local_database() {
        Cursor c = db.rawQuery("SELECT * FROM auth", null);
        try {
            String TABLE_NAME="auth",ColumnName="user_photo_url",rowID="1",ColumnSno="sno";
            String updateSql=" UPDATE " + TABLE_NAME + " SET " + ColumnName + " = '" + download_url.toString() + "' WHERE " + ColumnSno + " = "+ rowID;
            db.execSQL(updateSql);
        }
        catch (Exception e) {
            Snackbar snackbar = Snackbar
                    .make(Register_root_layout, "Ram usage does'nt supported", Snackbar.LENGTH_LONG);
            snackbar.show();
        }




    }



    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getAddress() {

        if (!Geocoder.isPresent()) {
//            Toast.makeText(Register.this,
//                    "Can't find current address, ",
//                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GetAddressIntentService.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", currentLocation);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(this, "Location permission not granted, " +
                                    "restart the app if you want the feature",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }




    public void name_ED_onclick(View view) {

        Toast.makeText(Register.this,
                "No Internet Connectivity" ,
                Toast.LENGTH_SHORT).show();

    }      //       NOT USED    ************************

    public void Mail_ED_onclick(View view) {


    }

    public void age_ED_onclick(View view) {



    }

    public void pincode_ED_onclick(View view) {



    }

    public void country_ED_onclick(View view) {


    }

    public void state_ED_onclick(View view) {


    }

    public void district_ED_onclick(View view) {


    }

    public void locality_ED_onclick(View view) {


    }

    public void address_ED_onclick(View view) {


    }

    public void phone_ED_onclick(View view) {

    }            //       NOT USED ***********************





    public void take_me_to_registration_page_2(View view) {

        try {

            int User_age_in_int=0;


            if(!TextUtils.isEmpty(AgeED.getText().toString().trim()))
            {
                User_age_in_int= Integer.parseInt(AgeED.getText().toString());
            }




            if(TextUtils.isEmpty(NameED.getText().toString())   && NameED.getText().toString().trim().equals("null"))
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "Please enter your name!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }else if(TextUtils.isEmpty(MailED.getText().toString())   && MailED.getText().toString().trim().equals("null"))
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "Please enter your Mail ID!", Snackbar.LENGTH_LONG);
                snackbar.show();

            }else if(TextUtils.isEmpty(AgeED.getText().toString())    && AgeED.getText().toString().trim().equals("null"))
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "Please provide your age!", Snackbar.LENGTH_LONG);
                snackbar.show();

            }else if(!selected_gender)
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "Please provide gender details!", Snackbar.LENGTH_LONG);
                snackbar.show();

            }else if(TextUtils.isEmpty(LocalityED.getText().toString())    && LocalityED.getText().toString().trim().equals("null"))
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "Please provide your Locality!", Snackbar.LENGTH_LONG);
                snackbar.show();

            }else if(TextUtils.isEmpty(DistrictED.getText().toString())  && DistrictED.getText().toString().trim().equals("null") && DistrictED.getText().toString().trim().replaceAll(" ","").equals(""))
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "Please provide your District/Area!", Snackbar.LENGTH_LONG);
                snackbar.show();

            }else if(TextUtils.isEmpty(StateED.getText().toString())    && StateED.getText().toString().trim().equals("null"))
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "Please provide your State!", Snackbar.LENGTH_LONG);
                snackbar.show();

            }else if(TextUtils.isEmpty(CountryED.getText().toString())    && CountryED.getText().toString().trim().equals("null"))
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "Please provide your Country!", Snackbar.LENGTH_LONG);
                snackbar.show();

            }else if(TextUtils.isEmpty(PincodeED.getText().toString())    && PincodeED.getText().toString().trim().equals("null"))
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "Please provide your Postal code/ZIP code!", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
            else if(TextUtils.isEmpty(PhoneED.getText().toString())    && PhoneED.getText().toString().trim().equals("null"))
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "Please provide your phone number!", Snackbar.LENGTH_LONG);
                snackbar.show();

            }else if(PhoneED.getText().toString().length()<10 )
            {
                if(COUNTRY.equals("India") )
                {
                    Snackbar snackbar = Snackbar
                            .make(Register_root_layout, "Please provide valid phone number!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }


            }else if(!(User_age_in_int<=123))
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "There is no record of lifespan of "+User_age_in_int+" years.\nPlease provide valid Age!", Snackbar.LENGTH_LONG);
                snackbar.show();

            }else if(!mailIsValid(MailED.getText().toString()))
            {
                Snackbar snackbar = Snackbar
                        .make(Register_root_layout, "Please provide valid Mail address!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            else
            {
                verified_name.check();
                verified_mail.check();
                verified_age.check();
                verified_phone.check();
                verified_address.check();
                verified_locality.check();
                verified_district.check();
                verified_state.check();
                verified_country.check();
                verified_pincode.check();
                //verified_gender.check();

                U_NAME=NameED.getText().toString().replaceAll("_", "-");
                MAILID=MailED.getText().toString();
                AGE=AgeED.getText().toString();
                PHONE=PhoneED.getText().toString();
                ADDRESS=AddressED.getText().toString();
                LOCALITY=LocalityED.getText().toString();
                DISTRICT=DistrictED.getText().toString();
                STATE=StateED.getText().toString();
                COUNTRY=CountryED.getText().toString();
                POSTAL_CODE=PincodeED.getText().toString();

                verify_button.setText("Verifying");

                try {
                    View keyview = this.getCurrentFocus();
                    if (keyview != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);    //to close keyboard
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                catch (Exception e)
                {

                }





                delay();   //wait till verification animation and intent to Register2 page



            }
        }
        catch (Exception e)
        {
            Snackbar snackbar = Snackbar
                    .make(Register_root_layout, "Invalid Age!" , Snackbar.LENGTH_LONG);
            snackbar.show();
        }



    }

    private void delay() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                String release = Build.VERSION.RELEASE;
                int sdkVersion = Build.VERSION.SDK_INT;
                if(sdkVersion<22)
                {
                    Intent MainscreenIntent=new Intent(Register.this,Register2.class);
                    MainscreenIntent.putExtra("NAME", U_NAME);
                    MainscreenIntent.putExtra("MAILID", MAILID);
                    MainscreenIntent.putExtra("PHONE", PHONE);
                    MainscreenIntent.putExtra("AGE", AGE);
                    MainscreenIntent.putExtra("GENDER", GENDER);
                    MainscreenIntent.putExtra("ADDRESS", ADDRESS);
                    MainscreenIntent.putExtra("LOCALITY", LOCALITY);
                    MainscreenIntent.putExtra("DISTRICT", DISTRICT);
                    MainscreenIntent.putExtra("STATE", STATE);
                    MainscreenIntent.putExtra("COUNTRY", COUNTRY);
                    MainscreenIntent.putExtra("PINCODE", POSTAL_CODE);
                    MainscreenIntent.putExtra("LATITUDE", LATITUDE);
                    MainscreenIntent.putExtra("LONGITUDE", LONGITUDE);
                    startActivity(MainscreenIntent);
                }


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


                    Intent MainscreenIntent = new Intent(getApplicationContext(), Register2.class);

                    MainscreenIntent.putExtra("NAME", U_NAME);
                    MainscreenIntent.putExtra("MAILID", MAILID);
                    MainscreenIntent.putExtra("PHONE", PHONE);
                    MainscreenIntent.putExtra("AGE", AGE);
                    MainscreenIntent.putExtra("GENDER", GENDER);
                    MainscreenIntent.putExtra("ADDRESS", ADDRESS);
                    MainscreenIntent.putExtra("LOCALITY", LOCALITY);
                    MainscreenIntent.putExtra("DISTRICT", DISTRICT);
                    MainscreenIntent.putExtra("STATE", STATE);
                    MainscreenIntent.putExtra("COUNTRY", COUNTRY);
                    MainscreenIntent.putExtra("PINCODE", POSTAL_CODE);
                    MainscreenIntent.putExtra("LATITUDE", LATITUDE);
                    MainscreenIntent.putExtra("LONGITUDE", LONGITUDE);

                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(root_cardview, "cardview_transition");
                    //pairs[1]=new Pair<View, String>(user_circle_image,"userimage_transition");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Register.this, pairs);
                    startActivity(MainscreenIntent, options.toBundle());

                }


            }
        }, 2000);
    }

    public static boolean mailIsValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public void update_user_as_male(View view) {
        GENDER="Male";
        selected_gender=true;
        male_imageview.setBackgroundColor(getResources().getColor(R.color.light_color));
        female_imageview.setBackgroundColor(getResources().getColor(R.color.WHITE));
    }

    public void update_user_as_female(View view) {
        GENDER="Female";
        selected_gender=true;
        female_imageview.setBackgroundColor(getResources().getColor(R.color.light_color));
        male_imageview.setBackgroundColor(getResources().getColor(R.color.WHITE));
    }

    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == 0) {
                Log.d("Address", "Location null retrying");
                getAddress();
            }

            if (resultCode == 1) {
//                Toast.makeText(Register.this,
//                        "Address not found, " ,
//                        Toast.LENGTH_SHORT).show();
            }

            String currentAdd = resultData.getString("address_result");

            showResults(currentAdd);
        }
    }

    private void showResults(String currentAdd){


        String str =currentAdd;
         arrOfAdd = str.split("_");

        Update_Address_in_String();
    }

    private void Update_Address_in_String() {



        try
        {
            ADDRESS=arrOfAdd[0].toString().replaceAll("null","");
            LOCALITY=arrOfAdd[1].toString().replaceAll("null","");
            DISTRICT=arrOfAdd[2].toString().replaceAll("null","");
            STATE= arrOfAdd[3].toString().replaceAll("null","");
            COUNTRY=arrOfAdd[4].toString().replaceAll("null","");
            POSTAL_CODE=arrOfAdd[5].toString().replaceAll("null","");
            LATITUDE=arrOfAdd[6].toString().replaceAll("null","");
            LONGITUDE=arrOfAdd[7].toString().replaceAll("null","");


            if(ADDRESS!=null && LOCALITY!= null && DISTRICT!=null && STATE!=null && COUNTRY!=null && POSTAL_CODE!=null)
            {
                if(ED_UPDATED==false)
                {
                    //NameED.setText(U_NAME);
                    AddressED.setText(ADDRESS);
                    LocalityED.setText(LOCALITY);
                    DistrictED.setText(DISTRICT);
                    StateED.setText(STATE);
                    CountryED.setText(COUNTRY);
                    PincodeED.setText(POSTAL_CODE);

                    ED_UPDATED=true;
                }
            }


        }
        catch (Exception e)
        {
                            Toast.makeText(Register.this,
                        "No Internet Connectivity" ,
                        Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}
