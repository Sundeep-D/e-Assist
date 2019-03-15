package com.example.sundeep.egen_2;

import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;


public class Register2 extends AppCompatActivity {

    CardView root_cardview;
    TextView register_tv,Medical_details_tv,Do_you_have_sinus_infection_TV,Do_you_have_Diabetes_Mellitus_TV,emergency_contacts_TV,why_medical_details_TV,why_emergency_contacts_TV;
    TextView add_contact_1_name_TV,add_contact_2_name_TV,add_contact_1_number_TV,add_contact_2_number_TV;
    ImageView add_contact_1_IV,add_contact_2_IV;
    Typeface exo_font;
    MaterialSpinner Blood_group_spinner,BMI_spinner,sinus_spinner,diabetes_spinner;
    String UID,BLOOD_GROUP,BMI,SINUS="no",DIABETES="no",CONTACT_NAME_1,CONATCT_NAME_2,CONTACT_NUMBER_1,CONTACT_NUMEBR_2,ADDRESS,LOCALITY,POSTAL_CODE,DISTRICT,STATE,COUNTRY,LATITUDE,LONGITUDE,MAILID,AGE,PHONE,GENDER,NAME;

    RelativeLayout register_2_root_layout;
    DatabaseReference mDatabaseReference;
    ArrayList<String> user_details=new ArrayList<String>();
    String[] user_details_splitted;
    SQLiteDatabase db;
    Cursor c;


    //for picking contact

    private final static int CONTACT_PICKER = 1;
    String contact_clicked;
    Cursor cur;
    Button verify;
    Boolean blood_group_selected=false,bmi_selected=false,contact1_selected=false,contact2_selected=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(650);
        }

        //for getting data from before screen

        getDataFromBeforeScreen();




        register_2_root_layout=findViewById(R.id.regiter_2_root_layout);

        root_cardview=findViewById(R.id.root_cardview);

        register_tv=findViewById(R.id.register_textview);
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/bold.TTF");
        register_tv.setTypeface(custom_font2);

        Medical_details_tv=findViewById(R.id.Medical_details_TV);
        exo_font = Typeface.createFromAsset(getAssets(),  "fonts/Exo2-Medium.ttf");
        Medical_details_tv.setTypeface(exo_font);
        why_medical_details_TV=findViewById(R.id.why_medical_details);
        why_medical_details_TV.setTypeface(exo_font);
        why_emergency_contacts_TV=findViewById(R.id.why_emergency_contact_details);
        why_emergency_contacts_TV.setTypeface(exo_font);
        emergency_contacts_TV=findViewById(R.id.Emergency_contacts_TV);
        emergency_contacts_TV.setTypeface(exo_font);
        add_contact_1_name_TV=findViewById(R.id.add_contact1_name_TV);
        add_contact_1_name_TV.setTypeface(exo_font);
        add_contact_2_name_TV=findViewById(R.id.add_contact2_name_TV);
        add_contact_2_name_TV.setTypeface(exo_font);
        add_contact_1_number_TV=findViewById(R.id.add_contact1_number_TV);
        add_contact_1_number_TV.setTypeface(exo_font);
        add_contact_2_number_TV=findViewById(R.id.add_contact2_number_TV);
        add_contact_2_number_TV.setTypeface(exo_font);
        Blood_group_spinner=findViewById(R.id.Blood_group_spinner);
        BMI_spinner=findViewById(R.id.BMI_spinner);
        sinus_spinner=findViewById(R.id.sinus_spinner);
        diabetes_spinner=findViewById(R.id.Diabetes_spinner);

        Blood_group_spinner.setTypeface(exo_font);
        BMI_spinner.setTypeface(exo_font);
        sinus_spinner.setTypeface(exo_font);
        diabetes_spinner.setTypeface(exo_font);

        add_contact_1_IV=findViewById(R.id.add_contact1_IV);
        add_contact_2_IV=findViewById(R.id.add_contact2_IV);

        Do_you_have_sinus_infection_TV=findViewById(R.id.Do_you_have_Sinus_Infection_TV);
        Do_you_have_sinus_infection_TV.setTypeface(exo_font);
        Do_you_have_Diabetes_Mellitus_TV=findViewById(R.id.Do_you_have_Diabetes_TV);
        Do_you_have_Diabetes_Mellitus_TV.setTypeface(exo_font);
        verify=findViewById(R.id.verify_button);


        //************  *******************************

        Blood_group_spinner.setItems("Your Blood Group", "O-positive", "O-negative", "A-positive", "A-negative", "B-positive", "B-negative", "AB-positive", "AB-negative", "I dont know", "Not preferred to say");
        Blood_group_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if(item.equals("Your Blood Group"))
                {
                    Snackbar.make(view, "Providing blood group details will help you in emergency circumstances.", Snackbar.LENGTH_LONG).show();
                    BLOOD_GROUP="Not preferred to say";
                    blood_group_selected=true;
                }
                else
                {
                    BLOOD_GROUP=item;
                    blood_group_selected=true;
                }
            }
        });
        Blood_group_spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Providing blood group details will help you in emergency circumstances.", Snackbar.LENGTH_LONG).show();
            }
        });

        BMI_spinner.setItems("Your BMI Category", "below 18.5", "between 18.5 and 24.9", "between 25 and 29.9", "between 30 and 39.9", "I dont know", "Not preferred to say");
        BMI_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if(item.equals("Your BMI Category"))
                {
                    Snackbar.make(view, "Providing BMI details will help you in emergency circumstances.", Snackbar.LENGTH_LONG).show();
                    BMI="Not preferred to say";
                    bmi_selected=true;

                }else
                {
                    if(item.equals("below 18.5"))
                    {
                        Snackbar.make(view, "Oops! You're in the underweight range", Snackbar.LENGTH_LONG).show();


                    }else if(item.equals("between 18.5 and 24.9"))
                    {
                        Snackbar.make(view, "You're in the healthy weight range", Snackbar.LENGTH_LONG).show();


                    }else if(item.equals("between 25 and 29.9"))
                    {
                        Snackbar.make(view, "You're in the overweight range", Snackbar.LENGTH_LONG).show();


                    }else if(item.equals("between 30 and 39.9"))
                    {
                        Snackbar.make(view, " You're in the obese range", Snackbar.LENGTH_LONG).show();


                    }
                }

                if(item.equals("Your BMI Category"))
                {

                    BMI="Not preferred to say";
                    bmi_selected=true;

                }else
                {
                    BMI=item;
                    bmi_selected=true;

                }

            }
        });

        sinus_spinner.setItems("No", "Yes! But rarely", "Yes! Viral sinusitis", "Yes! Bacterial sinusitis", "Yes! Allergic sinusitis", "Not preferred to say");
        sinus_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

               SINUS=item;
            }
        });

        sinus_spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
               SINUS="No";
            }
        });

        diabetes_spinner.setItems("No", "Yes", "Not preferred to say");
        diabetes_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                DIABETES=item;
            }
        });

        diabetes_spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                DIABETES="No";
            }
        });


        //******************* GETTING UID***************************************************

        db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);

        try
        {
            db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);

            String a="1";

            c = db.rawQuery("SELECT * FROM auth WHERE sno='" + a + "'", null);
            if (c.moveToFirst()) {
                UID = c.getString(1);

            }

        }
        catch (Exception e)

        {

            Snackbar snackbar = Snackbar
                    .make(register_2_root_layout, "Database corrupted", Snackbar.LENGTH_LONG);
            snackbar.show();
        }





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






    }//****************************************** O N C R E A T E *************************************************

    private void update_details_in_Edit_text() {


        if(user_details_splitted!=null)
        {
            if(!user_details_splitted[15].equals(" "))
            {
                blood_group_selected=true;
                Blood_group_spinner.setText(user_details_splitted[15]);
                BLOOD_GROUP=user_details_splitted[15];
            }

            if(!user_details_splitted[16].equals(" "))
            {
                BMI_spinner.setText(user_details_splitted[16]);
                bmi_selected=true;
                BMI=user_details_splitted[16];
            }

            if(!user_details_splitted[17].equals(" "))
            {
                sinus_spinner.setText(user_details_splitted[17]);
                SINUS=user_details_splitted[17];
            }

            if(!user_details_splitted[18].equals(" "))
            {
                diabetes_spinner.setText(user_details_splitted[18]);
                DIABETES=user_details_splitted[18];
            }

            if(!user_details_splitted[19].equals(" "))
            {

                add_contact_1_name_TV.setText(user_details_splitted[19]);
                add_contact_2_IV.setVisibility(View.INVISIBLE);
                contact1_selected=true;
                verify.setText("UPDATE");
            }
            if(!user_details_splitted[20].equals(" "))
            add_contact_2_name_TV.setText(user_details_splitted[20]);
            if(!user_details_splitted[21].equals(" "))
            {
                add_contact_1_number_TV.setText(user_details_splitted[21]);
                add_contact_1_IV.setVisibility(View.INVISIBLE);
                contact2_selected=true;
            }

            if(!user_details_splitted[22].equals(" "))
            add_contact_2_number_TV.setText(user_details_splitted[22]);

            CONTACT_NAME_1=user_details_splitted[19];
            CONATCT_NAME_2=user_details_splitted[20];
            CONTACT_NUMBER_1=user_details_splitted[21];
            CONTACT_NUMEBR_2=user_details_splitted[22];




        }


        }

    private void getDataFromBeforeScreen() {

        try
        {
            NAME= getIntent().getExtras().getString("NAME");
            MAILID= getIntent().getExtras().getString("MAILID");
            PHONE= getIntent().getExtras().getString("PHONE");
            AGE= getIntent().getExtras().getString("AGE");
            GENDER= getIntent().getExtras().getString("GENDER");
            ADDRESS= getIntent().getExtras().getString("ADDRESS");
            LOCALITY= getIntent().getExtras().getString("LOCALITY");
            DISTRICT= getIntent().getExtras().getString("DISTRICT");
            STATE= getIntent().getExtras().getString("STATE");
            COUNTRY= getIntent().getExtras().getString("COUNTRY");
            POSTAL_CODE= getIntent().getExtras().getString("PINCODE");
            LATITUDE= getIntent().getExtras().getString("LATITUDE");
            LONGITUDE= getIntent().getExtras().getString("LONGITUDE");
           // Toast.makeText(getApplicationContext(),""+NAME+MAILID+PHONE+AGE+GENDER+ADDRESS+LOCALITY+DISTRICT+STATE+COUNTRY+POSTAL_CODE+LATITUDE+LONGITUDE,Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Registration Failed",Toast.LENGTH_LONG).show();
        }



    }


    public void pickContact1(View v)
    {
        Intent contactPickerIntent =
                new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER);
        add_contact_1_IV.setVisibility(View.INVISIBLE);
        contact_clicked="1";
    }

    public void pickContact2(View v)
    {
        Intent contactPickerIntent =
                new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER);
        add_contact_2_IV.setVisibility(View.INVISIBLE);
        contact_clicked="2";
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be using multiple startActivityForReslut
            switch (requestCode) {
                case CONTACT_PICKER:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }
    private void contactPicked(Intent data) {
        ContentResolver cr = getContentResolver();
        try {
            Uri uri = data.getData();
            //Query the content uri
            cur = cr.query(uri, null, null, null, null);
            cur.moveToFirst();
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            if(contact_clicked.equals("1"))
            {
                add_contact_1_name_TV.setText(name);
                CONTACT_NAME_1=name.replaceAll("_", "-");
            }
            else
            {
                add_contact_2_name_TV.setText(name);
                CONATCT_NAME_2=name.replaceAll("_", "-");
            }


            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                    new String[]{id}, null);
            while (pCur.moveToNext()) {
                String phone = pCur.getString(
                        pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if(contact_clicked.equals("1"))
                {
                  add_contact_1_number_TV.setText(phone.trim());
                    CONTACT_NUMBER_1=phone.trim();
                  if(phone.length()<10)
                  {
                      Snackbar snackbar = Snackbar.make(register_2_root_layout, CONTACT_NAME_1+" contact seems like invalid", Snackbar.LENGTH_LONG);
                      snackbar.show();
                  }
                  contact1_selected=true;
                }
                else
                {
                    add_contact_2_number_TV.setText(phone.trim());
                    CONTACT_NUMEBR_2=phone.trim();
                    if(phone.length()<10)
                    {
                         Snackbar snackbar = Snackbar.make(register_2_root_layout, CONATCT_NAME_2+" contact seems like invalid", Snackbar.LENGTH_LONG);
                         snackbar.show();
                    }
                    contact2_selected=true;
                }

            }
            pCur.close();
            // column index of the email
            Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    new String[]{id}, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(
                        emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
               // add_contact_1_number_TV.setText(email);
            }
            emailCur.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Provide permission to read contacts",Toast.LENGTH_LONG).show();
        }
    }


    public void take_me_to_registration_page_3(View view) {

        if(blood_group_selected && bmi_selected && contact1_selected && contact2_selected)
        {
            verify.setText("VERIFYING");




            delay();
        }
        else
        {
            Snackbar snackbar = Snackbar.make(register_2_root_layout, "Kindly provide all details", Snackbar.LENGTH_LONG);
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
                    Intent MainscreenIntent=new Intent(Register2.this,Register3.class);
                    MainscreenIntent.putExtra("NAME", NAME);
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
                    MainscreenIntent.putExtra("BLOODGROUP", BLOOD_GROUP);
                    MainscreenIntent.putExtra("BMI", BMI);
                    MainscreenIntent.putExtra("DIABETES", DIABETES);
                    MainscreenIntent.putExtra("SINUS", SINUS);
                    MainscreenIntent.putExtra("CONTACTNAME1", CONTACT_NAME_1);
                    MainscreenIntent.putExtra("CONTACTNAME2", CONATCT_NAME_2);
                    MainscreenIntent.putExtra("CONTACTNUMBER1", CONTACT_NUMBER_1);
                    MainscreenIntent.putExtra("CONTACTNUMBER2", CONTACT_NUMEBR_2);
                    startActivity(MainscreenIntent);
                }


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


                    Intent MainscreenIntent = new Intent(getApplicationContext(), Register3.class);

                    MainscreenIntent.putExtra("NAME", NAME);
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
                    MainscreenIntent.putExtra("BLOODGROUP", BLOOD_GROUP);
                    MainscreenIntent.putExtra("BMI", BMI);
                    MainscreenIntent.putExtra("DIABETES", DIABETES);
                    MainscreenIntent.putExtra("SINUS", SINUS);
                    MainscreenIntent.putExtra("CONTACTNAME1", CONTACT_NAME_1);
                    MainscreenIntent.putExtra("CONTACTNAME2", CONATCT_NAME_2);
                    MainscreenIntent.putExtra("CONTACTNUMBER1", CONTACT_NUMBER_1);
                    MainscreenIntent.putExtra("CONTACTNUMBER2", CONTACT_NUMEBR_2);

                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(root_cardview, "cardview_transition");
                    //pairs[1]=new Pair<View, String>(user_circle_image,"userimage_transition");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Register2.this, pairs);
                    startActivity(MainscreenIntent, options.toBundle());

                }

            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        // Here you want to show the user a dialog box
        new AlertDialog.Builder(this)
                .setTitle("Register later")
                .setMessage("Are you sure you want to quit registration?")
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
}
