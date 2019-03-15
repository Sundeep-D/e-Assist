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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cdflynn.android.library.checkview.CheckView;

public class Register3 extends AppCompatActivity {

    SurfaceView surface_view_for_aadhaar_scan,surface_view_for_license_scan,surface_view_for_organisationID;
    TextView retrieved_aadhaar_number_TV,retrieved_license_number_TV,retrieved_organisationID_TV,register_tv;
    TextView please_fit_AADHAAR_instruction_TV,please_fit_license_instruction_TV,Please_fit_organisationID_instruction_TV,unique_identification_TV,license_unique_identification_TV,organisationID_Unique_identification_TV,aadhaar_number_heading_TV,license_number_heading_TV,organisationID_number_heading_TV;
    EditText aadhaar_number_ED,license_number_ED,organisationID_ED;
    CameraSource cameraSource,cameraSource_for_license,cameraSource_for_organisationID;
    final int RequestCameraPrmissionID = 1001;
    ArrayList<String> arr=new ArrayList<String>();
    String aadhaar_engine_retrivel_string,license_engine_retrivel_string,organisationID_engine_retrivel_string;

    Typeface exo_font;
    RelativeLayout relative_layout_aadhaar,relative_layout_license,relative_layout_organisationID;
    LinearLayout linear_layout_aadhaar,linear_layout_license,linear_layout_organisationID;
    CardView aadhaar_root_card_view;
    Boolean aadhaar_retrieved=false,user_updating=false;
    CheckView verified_aadhaar,verified_license,verified_organisationID;
    AlertDialog.Builder alertDialogBuilder;
    boolean opened_organization_scan_engine=false;
    SQLiteDatabase db,dummy;
    Cursor c;
    Button finish_reg;
    DatabaseReference mDatabaseReference;
    ArrayList<String> user_details=new ArrayList<String>();
    String[] user_details_splitted;

    String UID,PHOTO_URL,BLOOD_GROUP,BMI,SINUS,DIABETES,CONTACT_NAME_1,CONATCT_NAME_2,CONTACT_NUMBER_1,CONTACT_NUMEBR_2,ADDRESS,LOCALITY,POSTAL_CODE,DISTRICT,STATE,COUNTRY,LATITUDE,LONGITUDE,MAILID,AGE,PHONE,GENDER,NAME,AADHAAR_NUMBER,LICENSE_NUMBER,ORGANIZATIONAL_ID;


    private ProgressDialog mprogress;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case RequestCameraPrmissionID:
            {
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        return;
                }
                try {
                    cameraSource.start(surface_view_for_aadhaar_scan.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        exo_font = Typeface.createFromAsset(getAssets(),  "fonts/Exo2-Medium.ttf");

        finish_reg=findViewById(R.id.verify_button);

        surface_view_for_aadhaar_scan=findViewById(R.id.surface_view_of_adhaar);
        surface_view_for_license_scan=findViewById(R.id.surface_view_of_license);
        surface_view_for_organisationID=findViewById(R.id.surface_view_of_organisationID);

        relative_layout_aadhaar=findViewById(R.id.relative_layout_of_adhaar);
        relative_layout_license=findViewById(R.id.relative_layout_of_license);
        relative_layout_organisationID=findViewById(R.id.relative_layout_of_organisationID);
        linear_layout_aadhaar=findViewById(R.id.linear_layout_of_aadhaar);
        linear_layout_license=findViewById(R.id.linear_layout_of_license);
        linear_layout_organisationID=findViewById(R.id.linear_layout_of_organisationID);


        please_fit_AADHAAR_instruction_TV=findViewById(R.id.please_fit_instruction_AADHAAR);
        please_fit_license_instruction_TV=findViewById(R.id.please_fit_instruction_LICENSE);
        Please_fit_organisationID_instruction_TV=findViewById(R.id.please_fit_instruction_organisationID);
        please_fit_AADHAAR_instruction_TV.setTypeface(exo_font);
        please_fit_license_instruction_TV.setTypeface(exo_font);
        Please_fit_organisationID_instruction_TV.setTypeface(exo_font);
        retrieved_aadhaar_number_TV=findViewById(R.id.retrieved_adhar_number_TV);
        retrieved_license_number_TV=findViewById(R.id.retrieved_license_number_TV);
        retrieved_organisationID_TV=findViewById(R.id.retrieved_organisationID_number_TV);
        retrieved_aadhaar_number_TV.setTypeface(exo_font);
        retrieved_license_number_TV.setTypeface(exo_font);
        retrieved_organisationID_TV.setTypeface(exo_font);
        unique_identification_TV=findViewById(R.id.unique_identification_TV);
        license_unique_identification_TV=findViewById(R.id.license_unique_identification_TV);
        organisationID_Unique_identification_TV=findViewById(R.id.organisationID_unique_identification_TV);
        license_unique_identification_TV.setTypeface(exo_font);
        unique_identification_TV.setTypeface(exo_font);
        organisationID_Unique_identification_TV.setTypeface(exo_font);
        aadhaar_number_heading_TV=findViewById(R.id.aadhaar_heading_TV);
        license_number_heading_TV=findViewById(R.id.license_heading_TV);
        organisationID_number_heading_TV=findViewById(R.id.organisationID_heading_TV);
        license_number_heading_TV.setTypeface(exo_font);
        aadhaar_number_heading_TV.setTypeface(exo_font);
        organisationID_number_heading_TV.setTypeface(exo_font);
        aadhaar_number_ED=findViewById(R.id.aadhaar_number_ED);
        license_number_ED=findViewById(R.id.license_number_ED);
        organisationID_ED=findViewById(R.id.organisationID_number_ED);
        aadhaar_number_ED.setTypeface(exo_font);
        license_number_ED.setTypeface(exo_font);
        organisationID_ED.setTypeface(exo_font);
        aadhaar_number_ED.setHint("Enter manually");
        license_number_ED.setHint("Enter manually");
        organisationID_ED.setHint("Enter manually");
        verified_aadhaar=findViewById(R.id.verified_animation_aadhaar);
        verified_license=findViewById(R.id.verified_animation_license);
        verified_organisationID=findViewById(R.id.verified_animation_organisationID);

        alertDialogBuilder = new AlertDialog.Builder(this);
        register_tv=findViewById(R.id.register_textview);
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/bold.TTF");
        register_tv.setTypeface(custom_font2);

        TextRecognizer textRecognizer_for_license = new TextRecognizer.Builder(getApplicationContext()).build();    //for license

        if (!textRecognizer_for_license.isOperational()) {

        } else {
            cameraSource_for_license = new CameraSource.Builder(getApplicationContext(), textRecognizer_for_license)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedFps(2.0f)
                    .setRequestedPreviewSize(1200, 1024)
                    .setAutoFocusEnabled(true)
                    .build();
            surface_view_for_license_scan.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(Register3.this, new String[]
                                    {
                                            Manifest.permission.CAMERA

                                    }, RequestCameraPrmissionID);
                            return;
                        }

                        cameraSource_for_license.start(surface_view_for_license_scan.getHolder());
                    }
                    catch (Exception e)
                    {

                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                    cameraSource_for_license.stop();
                }
            });

            textRecognizer_for_license.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() !=0)
                    {
                        retrieved_license_number_TV.post(new Runnable() {
                            @Override
                            public void run() {

                                StringBuilder strBuilder = new StringBuilder();
                                for (int i = 0; i < items.size(); ++i)
                                {
                                    TextBlock item=items.valueAt(i);
                                    strBuilder.append(item.getValue());
                                    //strBuilder.append("\n");
                                    //arr.add(item.getValue());

                                }
                                show_retrieved_number_license(strBuilder.toString());
                            }
                        });
                    }
                }
            });


        }

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();                // for AADHAAR

        if (!textRecognizer.isOperational()) {

        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedFps(2.0f)
                    .setRequestedPreviewSize(1200, 1024)
                    .setAutoFocusEnabled(true)
                    .build();
            surface_view_for_aadhaar_scan.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(Register3.this, new String[]
                                    {
                                            Manifest.permission.CAMERA

                                    }, RequestCameraPrmissionID);
                            return;
                        }

                        cameraSource.start(surface_view_for_aadhaar_scan.getHolder());
                    }
                    catch (Exception e)
                    {

                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() !=0)
                    {
                        retrieved_aadhaar_number_TV.post(new Runnable() {
                            @Override
                            public void run() {

                                StringBuilder strBuilder = new StringBuilder();
                                for (int i = 0; i < items.size(); ++i)
                                {
                                    TextBlock item=items.valueAt(i);
                                    strBuilder.append(item.getValue());
                                    //strBuilder.append("\n");
                                    arr.add(item.getValue());

                                }
                                show_retrieved_number_aadhaar(strBuilder.toString());
                            }
                        });
                    }
                }
            });


        }

        mprogress=new ProgressDialog(this);



        aadhaar_number_ED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(aadhaar_number_ED.getText().toString().trim().length()==12)
                {
                    delay_for_aadhaar_validation();
                    mprogress.setMessage("Validating your Aadhaar Number");
                    mprogress.show();
                    keyboard_hide();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        license_number_ED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(license_number_ED.getText().toString().trim().length()==15)
                {
                    delay_for_license_validation();
                    mprogress.setMessage("Validating your License Number");
                    mprogress.show();
                    keyboard_hide();


                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        getDataFromBeforeScreen();


        db = openOrCreateDatabase("USER_DETAILS", Context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS user(sno1 VARCHAR,UID VARCHAR,PHOTOURL VARCHAR,NAME VARCHAR,MAILID VARCHAR,PHONE VARCHAR,AGE VARCHAR,GENDER VARCHAR,ADDRESS VARCHAR,LOCALITY VARCHAR,DISTRICT VARCHAR,STATE VARCHAR,COUNTRY VARCHAR,POSTAL_CODE VARCHAR,LATITUDE VARCHAR,LONGITUDE VARCHAR,BLOOD_GROUP VARCHAR,BMI VARCHAR,SINUS VARCHAR,DIABETES VARCHAR,CONTACT_NAME_1 VARCHAR,CONATCT_NAME_2 VARCHAR,CONTACT_NUMBER_1 VARCHAR,CONTACT_NUMEBR_2 VARCHAR,AADHAAR_NUMBER VARCHAR,LICENSE_NUMBER VARCHAR,ORGANIZATIONAL_ID VARCHAR);");




        try
        {
            dummy = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);

            String a="1";

            c = dummy.rawQuery("SELECT * FROM auth WHERE sno='" + a + "'", null);
            if (c.moveToFirst()) {
                UID = c.getString(1);
                PHOTO_URL=c.getString(4);
            }


        }
        catch (Exception e)

        {
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





    }//*****************************************      ON CREATE   *********************************************************

    private void update_details_in_Edit_text() {


        if(!user_details_splitted[26].equals("main-reg-notdone"))
        {
            user_updating=true;           //temp  chane to true
            finish_reg.setText("UPDATE AND BACK TO DASHBOARD");





            if(!user_details_splitted[23].equals("Not specified"))
            {
               // user_updating=false;           //temp  chane to true
                aadhaar_number_ED.setText(user_details_splitted[23]);
                aadhaar_number_ED.setHint(user_details_splitted[23]);
                finish_reg.setText("UPDATE AND BACK TO DASHBOARD");
            }
            if(!user_details_splitted[24].equals("Not specified"))
            {
              //  user_updating=false;           //temp  chane to true
                license_number_ED.setText(user_details_splitted[24]);
                license_number_ED.setHint(user_details_splitted[24]);
                finish_reg.setText("UPDATE AND BACK TO DASHBOARD");
            }
            if(!user_details_splitted[25].equals("Not specified"))
            {
               // user_updating=false;           //temp  chane to true
                organisationID_ED.setText(user_details_splitted[25]);
                organisationID_ED.setHint(user_details_splitted[25]);
                finish_reg.setText("UPDATE AND BACK TO DASHBOARD");
            }
        }




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


    private void show_retrieved_number_license(String num) {

        int length=slowLength(num.replaceAll("\\s+",""));
        //retrieved_license_number_TV.setText(num);
        String n = ".*[0-9].*";
        String a = ".*[A-Z].*";
        String b = "[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}";

        //Toast.makeText(getApplicationContext(),""+slowLength(num.replaceAll("\\s+","")),Toast.LENGTH_SHORT).show();
//
        if(num.matches(n) && num.matches(a)  && length==15)
        {
            license_engine_retrivel_string=num.replaceAll("\\s+","");
            retrieved_license_number_TV.setText(num);
           // delay();

        }


    }

    private void show_retrieved_number_aadhaar(String num) {


        int length=slowLength(num.replaceAll("\\s+",""));

        //Toast.makeText(getApplicationContext(),""+slowLength(num.replaceAll("\\s+","")),Toast.LENGTH_SHORT).show();

        if(length==12)
        {
                aadhaar_engine_retrivel_string=num.replaceAll("\\s+","");
                retrieved_aadhaar_number_TV.setText(num);
                delay();

        }




    }

    private void delay() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                linear_layout_aadhaar.setVisibility(View.VISIBLE);
                relative_layout_aadhaar.setVisibility(View.INVISIBLE);
                surface_view_for_aadhaar_scan.setVisibility(View.INVISIBLE);
                aadhaar_number_ED.setText(aadhaar_engine_retrivel_string.substring(0,12));
                //retrieved_aadhaar_number_TV.setText("XXXXXXXXXXXX");


            }
        }, 2000);
    }

    private void delay_for_aadhaar_validation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                if(isValidName(aadhaar_number_ED.getText().toString().trim()))
                {
                    mprogress.dismiss();
                    verified_aadhaar.check();
                    verified_aadhaar.setVisibility(View.VISIBLE);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Invalid Aadhaar Number",Toast.LENGTH_LONG).show();
                    mprogress.dismiss();
                    verified_aadhaar.setVisibility(View.INVISIBLE);




                }

            }
        }, 2000);
    }



    private void delay_for_license_validation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                String n = ".*[0-9].*";
                String a = ".*[A-Z].*";

                if(license_number_ED.getText().toString().matches(n) && license_number_ED.getText().toString().matches(a)  && license_number_ED.getText().toString().trim().length()==15)
                {

                    mprogress.dismiss();
                    verified_license.setVisibility(View.VISIBLE);
                    verified_license.check();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Invalid License Number",Toast.LENGTH_LONG).show();
                    mprogress.dismiss();
                    verified_license.setVisibility(View.INVISIBLE);
                }

            }
        }, 2000);
    }



    public int slowLength(String myString) {
        int i = 0;
        try {
            while (true) {
                myString.charAt(i);
                i++;
            }
        } catch (IndexOutOfBoundsException e) {
            return i;
        }
    }   //to find length programatically


    public void open_aadhaar_scan_engine(View view) {


        alertDialogBuilder.setTitle("Mode of scan");
        alertDialogBuilder.setMessage("\n\nChoose any one mode to scan the 12 digit number\n\n\n");
        alertDialogBuilder.setNeutralButton("Scan QR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                Intent intent=new Intent(Register3.this,QR_scan.class);
                startActivityForResult(intent,100);

            }
        });
        alertDialogBuilder.setPositiveButton("Scan 12 digit number", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                open_aadhaar_OCR_engine();

            }
        });

        alertDialogBuilder.show();

    }

    private void open_aadhaar_OCR_engine() {

        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion<22)
        {
            Toast.makeText(getApplicationContext(),"Your phone is incapatible for this feature.\nRunning SDK version below 22.",Toast.LENGTH_LONG).show();
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            linear_layout_aadhaar.setVisibility(View.INVISIBLE);
            relative_layout_aadhaar.setVisibility(View.VISIBLE);
            surface_view_for_aadhaar_scan.setVisibility(View.VISIBLE);
            linear_layout_license.setVisibility(View.VISIBLE);
            relative_layout_license.setVisibility(View.INVISIBLE);
            surface_view_for_license_scan.setVisibility(View.INVISIBLE);
            cameraSource_for_license.stop();
        }

    }

    public void open_license_scan_engine(View view) {


        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion<22)
        {
            Toast.makeText(getApplicationContext(),"Your phone is incapatible for this feature.\nRunning SDK version below 22.",Toast.LENGTH_LONG).show();
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            linear_layout_license.setVisibility(View.INVISIBLE);
            relative_layout_license.setVisibility(View.VISIBLE);
            surface_view_for_license_scan.setVisibility(View.VISIBLE);
            linear_layout_aadhaar.setVisibility(View.VISIBLE);
            relative_layout_aadhaar.setVisibility(View.INVISIBLE);
            surface_view_for_aadhaar_scan.setVisibility(View.INVISIBLE);
            cameraSource.stop();
        }




    }

    public void close_aadhaar_scan_engine(View view) {
        linear_layout_aadhaar.setVisibility(View.VISIBLE);
        relative_layout_aadhaar.setVisibility(View.INVISIBLE);
        surface_view_for_aadhaar_scan.setVisibility(View.INVISIBLE);

        aadhaar_number_ED.setText(null);
        aadhaar_number_ED.setHint("Unable to fetch");
    }

    public void close_license_scan_engine(View view) {
        linear_layout_license.setVisibility(View.VISIBLE);
        relative_layout_license.setVisibility(View.INVISIBLE);
        surface_view_for_license_scan.setVisibility(View.INVISIBLE);

        license_number_ED.setText(null);
        license_number_ED.setHint("Unable to fetch");
    }

    public static boolean isValidName(String name)
    {
        Pattern aadhaarPattern = Pattern.compile("^[2-9]{1}[0-9]{11}$");
        Matcher matcher = aadhaarPattern.matcher(name);
        return matcher.find();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100 && resultCode == RESULT_OK)
        {
            if(data!=null)
            {
                Barcode barcode=data.getParcelableExtra("barcode");
                String XML_AADHAAR=barcode.displayValue;

                if(XML_AADHAAR.contains("uid") &&  XML_AADHAAR.contains("xml"))
                {
                    try{
                        if(XML_AADHAAR.contains("uid"))
                        {
                            int firstIndex = XML_AADHAAR.indexOf("uid");
                            aadhaar_number_ED.setText(XML_AADHAAR.substring(firstIndex+5,firstIndex+17));
                            shakeItBaby();
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
                    if(opened_organization_scan_engine)
                    {
                        organisationID_ED.setText(XML_AADHAAR.trim());
                        shakeItBaby();
                        opened_organization_scan_engine=false;
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Invalid QR",Toast.LENGTH_LONG).show();
                    }

                }



            }
        }
    }

    public void  keyboard_hide()
    {
        try
        {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        catch (Exception e)
        {

        }

    }

    public void open_organisationID_scan_engine(View view) {

        opened_organization_scan_engine = true;
        Intent intent=new Intent(Register3.this,QR_scan.class);
        startActivityForResult(intent,100);
    }



    private void shakeItBaby() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
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
            BLOOD_GROUP= getIntent().getExtras().getString("BLOODGROUP");
            BMI= getIntent().getExtras().getString("BMI");
            SINUS= getIntent().getExtras().getString("SINUS");
            DIABETES= getIntent().getExtras().getString("DIABETES");
            CONTACT_NAME_1= getIntent().getExtras().getString("CONTACTNAME1");
            CONATCT_NAME_2= getIntent().getExtras().getString("CONTACTNAME2");
            CONTACT_NUMBER_1= getIntent().getExtras().getString("CONTACTNUMBER1");
            CONTACT_NUMEBR_2= getIntent().getExtras().getString("CONTACTNUMBER2");

            // Toast.makeText(getApplicationContext(),""+NAME+MAILID+PHONE+AGE+GENDER+ADDRESS+LOCALITY+DISTRICT+STATE+COUNTRY+POSTAL_CODE+LATITUDE+LONGITUDE,Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Registration Failed",Toast.LENGTH_LONG).show();
        }



    }

    public void finish_registration(View view) {

        if(TextUtils.isEmpty(aadhaar_number_ED.getText().toString().trim()))
        {
            AADHAAR_NUMBER="Not specified";
        }
        else
        {
            AADHAAR_NUMBER=aadhaar_number_ED.getText().toString().trim().toLowerCase().replaceAll("_", "-");
        }

        if(TextUtils.isEmpty(license_number_ED.getText().toString().trim()))
        {
            LICENSE_NUMBER="Not specified";
        }
        else
        {
            LICENSE_NUMBER=license_number_ED.getText().toString().trim().replaceAll("_", "-");
        }

        if(TextUtils.isEmpty(organisationID_ED.getText().toString().trim()))
        {
            ORGANIZATIONAL_ID="Not specified";
        }
        else
        {
            ORGANIZATIONAL_ID=organisationID_ED.getText().toString().trim().replaceAll("_", "-");
        }

        store_user_data_in_local_DB();
        update_details_in_cloud_DB();



        if(user_updating)
        {
            Intent intro_screen_intent=new Intent(Register3.this,Main_screen.class);
            startActivity(intro_screen_intent);
        }
        else
        {
            intent_to_qr_generation();
        }




    }

   private void update_details_in_cloud_DB()
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("EGEN_USER_DETAILS");
        databaseReference.child(UID).setValue(UID+"_"+PHOTO_URL.replaceAll("_", "----------")+"_"+NAME+"_"+MAILID.replaceAll("_", "----------")+"_"+AGE+"_"+PHONE+"_"+GENDER+"_"+ADDRESS+"_"+LOCALITY+"_"+DISTRICT+"_"+STATE+"_"+COUNTRY+"_"+POSTAL_CODE+"_"+LATITUDE+"_"+LONGITUDE+"_"+BLOOD_GROUP+"_"+BMI+"_"+SINUS+"_"+DIABETES+"_"+CONTACT_NAME_1+"_"+CONATCT_NAME_2+"_"+CONTACT_NUMBER_1+"_"+CONTACT_NUMEBR_2+"_"+AADHAAR_NUMBER+"_"+LICENSE_NUMBER+"_"+ORGANIZATIONAL_ID+"_"+"main-reg-done");

    }



    private void intent_to_qr_generation() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion<22)
        {
            Intent intro_screen_intent=new Intent(Register3.this,QR_generation.class);
            startActivity(intro_screen_intent);
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


            Intent MainscreenIntent = new Intent(getApplicationContext(), QR_generation.class);

            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(finish_reg, "button_transition");
            //pairs[1] = new Pair<View, String>(user_circle_image, "userimage_transition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Register3.this, pairs);
            startActivity(MainscreenIntent, options.toBundle());
        }

    }

    private void store_user_data_in_local_DB() {
        try {
            int sno1=1;


            db.execSQL("DROP TABLE user");

            db.execSQL("CREATE TABLE IF NOT EXISTS user(sno1 VARCHAR,UID VARCHAR,PHOTOURL VARCHAR,NAME VARCHAR,MAILID VARCHAR,PHONE VARCHAR,AGE VARCHAR,GENDER VARCHAR,ADDRESS VARCHAR,LOCALITY VARCHAR,DISTRICT VARCHAR,STATE VARCHAR,COUNTRY VARCHAR,POSTAL_CODE VARCHAR,LATITUDE VARCHAR,LONGITUDE VARCHAR,BLOOD_GROUP VARCHAR,BMI VARCHAR,SINUS VARCHAR,DIABETES VARCHAR,CONTACT_NAME_1 VARCHAR,CONATCT_NAME_2 VARCHAR,CONTACT_NUMBER_1 VARCHAR,CONTACT_NUMEBR_2 VARCHAR,AADHAAR_NUMBER VARCHAR,LICENSE_NUMBER VARCHAR,ORGANIZATIONAL_ID VARCHAR);");


            db.execSQL("INSERT INTO user VALUES('" + sno1 + "','" + UID + "','" + PHOTO_URL + "','" + NAME + "','" + MAILID + "','" + PHONE + "','" + AGE + "','" + GENDER + "','" + ADDRESS + "','" + LOCALITY + "','" + DISTRICT + "','" + STATE + "','" + COUNTRY + "','" + POSTAL_CODE + "','" + LATITUDE + "','" + LONGITUDE + "','" + BLOOD_GROUP + "','" + BMI + "','" + SINUS + "','" + DIABETES + "','" + CONTACT_NAME_1 + "','" + CONATCT_NAME_2 + "','" + CONTACT_NUMBER_1 + "','" + CONTACT_NUMEBR_2 + "','" + AADHAAR_NUMBER + "','" + LICENSE_NUMBER + "','" + ORGANIZATIONAL_ID + "');");

            c = db.rawQuery("SELECT * FROM user WHERE sno1='" + sno1 + "'", null);
            if (c.moveToFirst()) {
                String a=c.getString(1);
                String b=c.getString(2);
                String cc=c.getString(3);
                String d=c.getString(4);
                String e=c.getString(5);
                String f=c.getString(6);
                String g=c.getString(7);
                String h=c.getString(8);
                String i=c.getString(9);
                String j=c.getString(10);
                String k=c.getString(11);
                String l=c.getString(12);
                String m=c.getString(13);
                String n=c.getString(14);
                String o=c.getString(15);
                String p=c.getString(16);
                String q=c.getString(17);
                String r=c.getString(18);
                String s=c.getString(19);
                String t=c.getString(20);
                String u=c.getString(21);
                String v=c.getString(22);
                String w=c.getString(23);
                String x=c.getString(24);
                String y=c.getString(25);
                String z=c.getString(26);


//                Toast.makeText(getApplicationContext(),
//                        a+"\n"+b+"\n"+cc+"\n"+d+"\n"+e+"\n"+f+"\n"+g+"\n"+h+"\n"+i+"\n"+j+"\n"+k+"\n"+l+"\n"+m+"\n"+n+"\n"+o+"\n"+p+"\n"+q+"\n"+r+"\n"+s+"\n"+t+"\n"+u+"\n"+v+"\n"+w+"\n"+x+"\n"+y+"\n"+z+"\n",
//                        Toast.LENGTH_LONG).show();

            }


//
//            Toast.makeText(getApplicationContext(),
//                    "SUCCESS",
//                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),
                    "Server down !\nTry again later."+e,
                    Toast.LENGTH_LONG).show();
        }
    }
}
