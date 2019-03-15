package com.example.sundeep.egen_2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Intro extends AppCompatActivity {

    TextView connect_with_social_textView,connect_with_social_layout;
    public static final int RequestSignInCode = 7;
    public FirebaseAuth firebaseAuth;
    public GoogleApiClient googleApiClient;
    SignInButton googleButton;
    DatabaseReference mDatabaseReference;
    FirebaseUser firebaseUser;
    RelativeLayout intro_root_layout;
    ProgressDialog progress;
    SQLiteDatabase db;
    Cursor c;
    String REG_STATUS="google_auth_done",MAIN_REG="not_done";
    String sno="1";
    ArrayList<String> existing_users_arr=new ArrayList<String>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;
    View view;
    String uid="UBmwlcr5p6Utw3Mz9cLXQFdZCrp2",UID="UBmwlcr5p6Utw3Mz9cLXQFdZCrp2",name="D.Sundeep",uname="D.Sundeep",url="https://lh3.googleusercontent.com/-CTVDnWiIGNw/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3rdGtBhET-eC6eMTCMnVl9vh56cGkw/mo/photo.jpg?sz=46",email="sundeep0412@gmail.com";

    KenBurnsView moving_image_view;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);  // for fullscreen activity
        setContentView(R.layout.activity_intro);

        intro_root_layout=findViewById(R.id.intro_root_layout);    //for snack bar root layout

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(500);             //for shared animation transition time
        }

        progress = new ProgressDialog(this);

        moving_image_view=findViewById(R.id.moving_IV);

        db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);  //opening the database

        //************************** CALLIOGRAPHY **************************************************************************

        //connect_with_social_textView = (TextView)findViewById(R.id.connect_with_social_textView);
        //connect_with_social_layout = (TextView)findViewById(R.id.connect_with_social_layout);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Century Gothic.ttf");
        //connect_with_social_textView.setTypeface(custom_font);


        //************************** GOOGLE SIGN**************************************************************************

        firebaseAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(Intro.this)
                .enableAutoManage(Intro.this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } )
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

     //************************** GOOGLE SIGN BUTTON**************************************************************************

        googleButton=(SignInButton)findViewById(R.id.sign_in_button);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(am_i_connected())
               {
                   Intent AuthIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);

                   startActivityForResult(AuthIntent, RequestSignInCode);

                   googleApiClient.connect();
//                  if( googleApiClient.isConnected())
//                  {
//
//                      Snackbar snackbar = Snackbar
//                              .make(intro_root_layout, "Connected", Snackbar.LENGTH_LONG);
//                      snackbar.show();
//
//                  }

                   //Toast.makeText(Intro.this,"clicked",Toast.LENGTH_LONG).show();
                   progress.setMessage("This may take a while...");
                   progress.show();
               }
               else
               {

                   Snackbar snackbar = Snackbar
                           .make(intro_root_layout, "No network Connectivity", Snackbar.LENGTH_LONG);
                   snackbar.show();
               }

            }
        });

        mDatabaseReference= FirebaseDatabase.getInstance().getReference("EGEN_USERS");
        mDatabaseReference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    String usrs=child.getValue(String.class);
                    existing_users_arr.add(usrs);
                   // Toast.makeText(Intro.this,usrs,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        startLocationUpdates();

        moving_image_view.restart();






    }//************************ O N   C R E A T E  ****************************************************************************



    public boolean am_i_connected()
    {
        ConnectivityManager con=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=con.getActiveNetworkInfo();

        return info!=null&&info.isConnected();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestSignInCode){

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);


           // data_from_google();// temp setup SSO not working

            if (googleSignInResult.isSuccess()){

                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

                FirebaseUserAuth(googleSignInAccount);

//                Snackbar snackbar = Snackbar
//                        .make(intro_root_layout, "called", Snackbar.LENGTH_LONG);
//                snackbar.show();


           }

        }
    }

    public void FirebaseUserAuth(GoogleSignInAccount googleSignInAccount) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(Intro.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> AuthResultTask) {

                        if (AuthResultTask.isSuccessful()){

                             firebaseUser = firebaseAuth.getCurrentUser();

                            try
                            {
                                data_from_google();
                            }
                            catch (Exception e)
                            {
                               // Toast.makeText(Intro.this,"Server down.Please try again later",Toast.LENGTH_LONG).show();
                                Snackbar snackbar = Snackbar
                                        .make(intro_root_layout, "Server down.Please try again later", Snackbar.LENGTH_LONG);
                                progress.dismiss();
                            }

                        }else {
                            //Toast.makeText(Intro.this,"",Toast.LENGTH_LONG).show();
                            Snackbar snackbar = Snackbar
                                    .make(intro_root_layout, "No internet connectivity", Snackbar.LENGTH_LONG);


                            progress.dismiss();
                        }
                    }
                });
    }

    private void data_from_google() {

//        if(existing_users_arr.contains(firebaseUser.getEmail()))
//        {
//            Snackbar snackbar = Snackbar
//                    .make(intro_root_layout, "Already exists", Snackbar.LENGTH_LONG);
//            snackbar.show();
//            progress.dismiss();
//        }
//        else
//        {
            try
            {
                 uid=firebaseUser.getUid().toString();                    //temp setup    SSO not worlking
                email= firebaseUser.getEmail();
                 uname= firebaseUser.getDisplayName();
               url= firebaseUser.getPhotoUrl().toString();
                String concat=uid+"_"+email+"_"+uname+"_"+url;
                UID=firebaseUser.getUid();

                mDatabaseReference=FirebaseDatabase.getInstance().getReference("EGEN_USERS").child("R_USERS");
                mDatabaseReference.setValue(email);

                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("EGEN_USER_DETAILS");
                    databaseReference.child(UID).setValue(UID+"_"+url.replaceAll("_", "----------")+"_"+uname+"_"+email.replaceAll("_", "----------")+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+" "+"_"+"main-reg-notdone");






                intent();

                add_to_existing_user();

                progress.dismiss();
            }
            catch (Exception e)
            {
                 Toast.makeText(Intro.this,""+e,Toast.LENGTH_LONG).show();
//                Snackbar snackbar = Snackbar
//                        .make(intro_root_layout, ""+e, Snackbar.LENGTH_LONG);
//                snackbar.show();
                progress.dismiss();

            }

       // }



    }

    private void add_to_existing_user() {



        String user_name= firebaseUser.getDisplayName().toString();                      //temp setup
        String user_photo_url= firebaseUser.getPhotoUrl().toString();

        Cursor c = db.rawQuery("SELECT * FROM auth", null);

        db.execSQL("INSERT INTO auth VALUES('" + sno + "','" + UID + "','" + REG_STATUS + "','" +user_name + "','" + user_photo_url + "','" + MAIN_REG + "');");

    }   //inserting values into table

    private void intent() {

        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
//        if(sdkVersion<22)
//        {
//            Intent intro_screen_intent=new Intent(Intro.this,Welcome.class);
//            startActivity(intro_screen_intent);
//        }
//
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//
//            Intent intro_screen_intent = new Intent(Intro.this, Welcome.class);
//
//            Pair[] pairs = new Pair[1];
//            pairs[0] = new Pair<View, String>(googleButton, "dashboard_transition");
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Intro.this, pairs);
//            startActivity(intro_screen_intent, options.toBundle());
//        }


        Intent intro_screen_intent=new Intent(Intro.this,Welcome.class);   //temp setup
        startActivity(intro_screen_intent);
    }


    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.CAMERA, READ_EXTERNAL_STORAGE,CALL_PHONE,WRITE_EXTERNAL_STORAGE,ACCESS_FINE_LOCATION, VIBRATE, READ_CONTACTS},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


}