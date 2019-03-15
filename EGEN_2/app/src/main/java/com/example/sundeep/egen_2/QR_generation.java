package com.example.sundeep.egen_2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;


public class QR_generation extends AppCompatActivity {
    ImageView QR_generation_image_view;
    File newfile;
    Bitmap bitmap;
    TextView life_saver_QR_TV,register_successfully_TV,whatsnext_TV,back_to_dashboard_TV;
    AVLoadingIndicatorView avi;
    static String folderPath;
    SQLiteDatabase db,dummy;
    Cursor c;
    public String UID,PHOTO_URL,BLOOD_GROUP,BMI,SINUS,DIABETES,CONTACT_NAME_1,CONATCT_NAME_2,CONTACT_NUMBER_1,CONTACT_NUMEBR_2,ADDRESS,LOCALITY,POSTAL_CODE,DISTRICT,STATE,COUNTRY,LATITUDE,LONGITUDE,MAILID,AGE,PHONE,GENDER,NAME,AADHAAR_NUMBER,LICENSE_NUMBER,ORGANIZATIONAL_ID;

    Typeface exo_font;

    //fpr mail

                                                                                                                                                                String myEmailString="egen0412@gmail.com", passString="8122279858s", sendToEmailString="sundeep0412@gmail.com", subjectString="Registration Successful", textString="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generation);

        QR_generation_image_view=findViewById(R.id.qr_generation_image_view);
        life_saver_QR_TV=findViewById(R.id.life_saver_QR_textview);
        avi=findViewById(R.id.qr_load_progress);
        avi.show();
        delay_for_qr_load();

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/bold.TTF");
        life_saver_QR_TV.setTypeface(custom_font2);
        exo_font = Typeface.createFromAsset(getAssets(),  "fonts/Exo2-Medium.ttf");
        register_successfully_TV=findViewById(R.id.register_successfully_TV);
        register_successfully_TV.setTypeface(exo_font);
        whatsnext_TV=findViewById(R.id.whatsnext_TV);
        whatsnext_TV.setTypeface(exo_font);
        back_to_dashboard_TV=findViewById(R.id.back_to_dashboard_TV);
        back_to_dashboard_TV.setTypeface(exo_font);

        whatsnext_TV.setPaintFlags(whatsnext_TV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        back_to_dashboard_TV.setPaintFlags(back_to_dashboard_TV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        get_details_from_local_DB();
        update_REG_STATUS();


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(UID, BarcodeFormat.QR_CODE,350,350);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            QR_generation_image_view.setImageBitmap(bitmap);

            try {
                newfile = savebitmap(bitmap);


            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Error writing developer info",Toast.LENGTH_SHORT).show();

            }


        } catch (WriterException e) {
            e.printStackTrace();
        }



        sendToEmailString=MAILID;


        final SendEmailTask sendEmailTask = new SendEmailTask();
        sendEmailTask.execute();

    }

    private void get_details_from_local_DB() {

        int sno1=1;

        db = openOrCreateDatabase("USER_DETAILS", Context.MODE_PRIVATE, null);


        c = db.rawQuery("SELECT * FROM user WHERE sno1='" + sno1 + "'", null);
        if (c.moveToFirst()) {
             UID = c.getString(1);
           PHOTO_URL = c.getString(2);
             NAME = c.getString(3);
             MAILID = c.getString(4);
             PHONE = c.getString(5);
             AGE = c.getString(6);
             GENDER = c.getString(7);
             ADDRESS = c.getString(8);
             LOCALITY = c.getString(9);
             DISTRICT = c.getString(10);
             STATE = c.getString(11);
             COUNTRY = c.getString(12);
             POSTAL_CODE = c.getString(13);
             LATITUDE = c.getString(14);
             LONGITUDE = c.getString(15);
             BLOOD_GROUP = c.getString(16);
             BMI = c.getString(17);
             SINUS = c.getString(18);
             DIABETES = c.getString(19);
             CONTACT_NAME_1 = c.getString(20);
             CONATCT_NAME_2 = c.getString(21);
             CONTACT_NUMBER_1 = c.getString(22);
             CONTACT_NUMEBR_2 = c.getString(23);
             AADHAAR_NUMBER = c.getString(24);
             LICENSE_NUMBER = c.getString(25);
             ORGANIZATIONAL_ID = c.getString(26);

             textString="\n\nHi "+NAME+","
                     +"\n\nYour registration was successfully done."
                     +"\n\nKindly verify your details"
                     +"\n\n\nIdentification details"
                     +"\n\nID: "+UID
                     +"\nName: "+NAME
                     +"\nMail: "+MAILID
                     +"\nPhone number: "+PHONE
                     +"\nAge: "+AGE
                     +"\nGender: "+GENDER
                     +"\nAddress: "+ADDRESS
                     +"\nLocality: "+LOCALITY
                     +"\nDistrict: "+DISTRICT
                     +"\nState: "+STATE
                     +"\nCountry: "+COUNTRY
                     +"\nZip code: "+POSTAL_CODE
                     +"\nBlood Group: "+BLOOD_GROUP
                     +"\nBody Mass Index: "+BMI
                     +"\nSuffering from sinus: "+SINUS
                     +"\nSuffering from diabetes: "+DIABETES
                     +"\n\nEmergency contacts"
                     +"\n\nContact 1: "+CONTACT_NAME_1+" "+CONTACT_NUMBER_1
                     +"\nContact 2: "+CONATCT_NAME_2+" "+CONTACT_NUMEBR_2
                     +"\n\n Unique Identification"
                     +"\n\nAadhaar Number: "+AADHAAR_NUMBER
                     +"\nDriving Licence: "+LICENSE_NUMBER
                     +"\nOrganisation ID: "+ORGANIZATIONAL_ID;

        }


        }

    public static File savebitmap(Bitmap bmp) throws IOException {

        //Create folder !exist
        folderPath = Environment.getExternalStorageDirectory() + "/EGEN/Life Saver QR";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            File wallpaperDirectory = new File(folderPath);
            wallpaperDirectory.mkdirs();
        }


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(folderPath, "QR.jpg");

        if(f!=null)
        {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        }

        //writing the developer  information

        try {


                File file= new File(folderPath, "Developer info.txt");
                FileWriter filewriter = new FileWriter(file);
                BufferedWriter out = new BufferedWriter(filewriter);
                out.write("Designed and developed by\nSundeep D\n\nFor more information\nwww.sundeepdayalan.com");
                out.close();


        } catch (IOException e) {

        }

        return f;
    }

    private void delay_for_qr_load() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                QR_generation_image_view.setVisibility(View.VISIBLE);
                avi.hide();

            }
        }, 2000);
    }

    public void sendmail(View view) {


    }

    public void whats_next_onclick(View view) {
        Toast.makeText(getApplicationContext(),"Yet to design",Toast.LENGTH_LONG).show();
    }

    public void back_to_dashboard_onclick(View view) {
        Intent intent=new Intent(QR_generation.this,Main_screen.class);
        startActivity(intent);
    }


    class  SendEmailTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("Email sending", "sending start");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                GmailSender sender = new GmailSender(myEmailString, passString);

                //new
                sender.addAttachment(Environment.getExternalStorageDirectory() + "/EGEN/Life Saver QR/sundssdeepQR.jpg");
                //new



                //subject, body, sender, to
                sender.sendMail(subjectString,
                        textString,
                        myEmailString,
                        sendToEmailString);

                Log.i("Email sending", "send");
            } catch (Exception e) {
                Log.i("Email sending", "cannot send");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    private void update_REG_STATUS() {

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

    @Override
    public void onBackPressed() {

                        Intent MainscreenIntent=new Intent(getApplicationContext(),Main_screen.class);
                        startActivity(MainscreenIntent);


    }   //confirmation for going back to main page without registering

}


