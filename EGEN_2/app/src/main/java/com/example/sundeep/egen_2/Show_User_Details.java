package com.example.sundeep.egen_2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Show_User_Details extends AppCompatActivity {

    TextView emergency_head_TV,user_name_TV,user_address_TV,call_1_TV,call_2_TV;
    Typeface exo_font;
    String USER_ENTIRE_DATA;
    String[] user_details_arr;
    ImageView user_image_IV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_show__user__details);

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/bold.TTF");
        exo_font = Typeface.createFromAsset(getAssets(),  "fonts/Exo2-Medium.ttf");


        emergency_head_TV=findViewById(R.id.emergency_heading);
        user_name_TV=findViewById(R.id.user_name_TV);
        user_address_TV=findViewById(R.id.user_address_TV);
        call_1_TV=findViewById(R.id.call_TV_1);
        call_2_TV=findViewById(R.id.call_TV_2);
        user_image_IV=findViewById(R.id.user_image_IV);

        emergency_head_TV.setTypeface(exo_font);
        user_name_TV.setTypeface(exo_font);
        user_address_TV.setTypeface(exo_font);
        call_1_TV.setTypeface(exo_font);
        call_2_TV.setTypeface(exo_font);

        USER_ENTIRE_DATA= getIntent().getExtras().getString("USER_ENTIRE_DETAILS");
        user_details_arr=USER_ENTIRE_DATA.split("_");

       // Toast.makeText(getApplicationContext(),""+user_details_arr[0],Toast.LENGTH_LONG).show();

        Picasso.get().load(user_details_arr[1].replaceAll("----------", "_")).fit().centerCrop().into(user_image_IV, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {

            }

        });

        user_name_TV.setText(user_details_arr[2]);

        String add=user_details_arr[8]+"\n"+user_details_arr[9]+"\n"+user_details_arr[10];

        user_address_TV.setText(add);







    }

    public void call_emergency_contact_1(View view) {


        String contact_1=user_details_arr[21];

        if(contact_1.contains("+"))
        {
            contact_1=contact_1.substring(3).replaceAll(" ","");
        }
      //  Toast.makeText(getApplicationContext(),contact_1,Toast.LENGTH_LONG).show();

        try {
            Intent my_callIntent = new Intent(Intent.ACTION_CALL);
            my_callIntent.setData(Uri.parse("tel:"+contact_1));
            startActivity(my_callIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Error in your phone call"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public void call_emergency_contact_2(View view) {


        String contact_2=user_details_arr[22];

        if(contact_2.contains("+"))
        {
            contact_2=contact_2.substring(3).replaceAll(" ","");
        }

       // Toast.makeText(getApplicationContext(),contact_2,Toast.LENGTH_LONG).show();


        try {
            Intent my_callIntent = new Intent(Intent.ACTION_CALL);
            my_callIntent.setData(Uri.parse("tel:"+contact_2));
            //here the word 'tel' is important for making a call...
            startActivity(my_callIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Error in your phone call"+e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
}
