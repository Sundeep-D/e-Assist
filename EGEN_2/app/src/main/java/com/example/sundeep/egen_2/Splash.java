package com.example.sundeep.egen_2;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;
    ImageView logo;
    TextView version;
    Intent intro_screen_intent;
    SQLiteDatabase db;
    Cursor c;
    String UID,REG_STATUS;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        //****************************** CALLIOGRAPHY  *****************************************************

        logo=(ImageView) findViewById(R.id.logo);
        version = (TextView)findViewById(R.id.version);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Century Gothic.ttf");
        version.setTypeface(custom_font);

        //****************************** SPLASH TIMER  *****************************************************
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              //  intro_screen_intent=new Intent(Splash.this,Intro.class);

               // startActivity(intro_screen_intent);
                check_existing_user();

                finish();
            }
        },SPLASH_TIME_OUT);


            String release = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;
            version.setText("Version 1.1\nRunning on SDK"+sdkVersion);
          // Toast.makeText(getApplicationContext(),""+sdkVersion,Toast.LENGTH_LONG).show();





    }  //ONCREATE  *****************************************************


    private void check_existing_user() {

        try {
            db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS auth(sno VARCHAR,uid VARCHAR,reg_status VARCHAR,user_name VARCHAR,user_photo_url VARCHAR,main_reg VARCHAR);");
            c = db.rawQuery("SELECT * FROM auth", null);
            if (c.getCount() == 0) {

                Intent nxt=new Intent(Splash.this,Intro.class);
                startActivity(nxt);
                return;
            }
            else
            {
                String a="1";


                c = db.rawQuery("SELECT * FROM auth WHERE sno='" + a + "'", null);
                if (c.moveToFirst()) {
                    UID=c.getString(1);
                    REG_STATUS=c.getString(2);
                    if(REG_STATUS.equals("google_auth_done"))
                    {
                        intent_to_welcome();   //if google sign done straighly goes to welcome screen (sundeep)
                    }
                    else if(REG_STATUS.equals("accepted_terms_and_conditions"))   //if terms and conditions accepted then  straightly goes to dashboard screen (Main_screen)
                    {
                        String release = Build.VERSION.RELEASE;
                        int sdkVersion = Build.VERSION.SDK_INT;
                        if(sdkVersion<22)
                        {
                            Intent intro_screen_intent=new Intent(Splash.this,Main_screen.class);
                            startActivity(intro_screen_intent);
                        }


                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {


                            Intent MainscreenIntent = new Intent(getApplicationContext(), Main_screen.class);
                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(logo, "dashboard_transition");
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Splash.this, pairs);
                            startActivity(MainscreenIntent, options.toBundle());

                        }
                    }
                }
            }


        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),
                    "Database Failure",
                    Toast.LENGTH_SHORT).show();
        }
    }   //table creation

    private void intent_to_welcome() {

        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion<22)
        {
           Intent intro_screen_intent=new Intent(Splash.this,Welcome.class);
           startActivity(intro_screen_intent);
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {

            Intent intro_screen_intent=new Intent(Splash.this,Welcome.class);

        Pair[] pairs=new Pair[1];
        pairs[0]=new Pair<View, String>(logo,"dashboard_transition");
        ActivityOptions options= null;
        options = ActivityOptions.makeSceneTransitionAnimation(Splash.this,pairs);
        startActivity(intro_screen_intent,options.toBundle());

        }
    }




}
