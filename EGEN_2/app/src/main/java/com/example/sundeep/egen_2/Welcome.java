package com.example.sundeep.egen_2;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.morphingbutton.MorphingButton;
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
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class Welcome extends AppCompatActivity {

    TextView welcome,username,switch_account,terms_and_conditions,conditions;
    SQLiteDatabase db;
    Cursor c;
    String UID,REG_STATUS="accepted_terms_and_conditions",U_NAME;
    AlertDialog.Builder alert;
    public static final int RequestSignInCode = 7;
    public FirebaseAuth firebaseAuth;
    public GoogleApiClient googleApiClient;
    SignInButton googleButton;
    DatabaseReference mDatabaseReference;
    FirebaseUser firebaseUser;
    ArrayList<String> user_details_arrayList=new ArrayList<String >();
    RelativeLayout welcome_root_layout;
    LinearLayout welcome_linear_layout;
    ProgressDialog progress;
    MorphingButton accept_and_continue;

    private int mMorphCounter1 = 1;
    private int mMorphCounter2 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //************************** CALLIOGRAPHY**************************************************************************

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(600);
        }

        welcome=(TextView)findViewById(R.id.welcome);
        username=(TextView)findViewById(R.id.username);
        switch_account=(TextView)findViewById(R.id.switch_account);
        welcome_root_layout=(RelativeLayout)findViewById(R.id.welcome_root_layout);
        welcome_linear_layout=(LinearLayout)findViewById(R.id.welcome_Linear_layout);
        terms_and_conditions=(TextView)findViewById(R.id.term_conditions) ;
        conditions=(TextView)findViewById(R.id.conditions);
        accept_and_continue= (MorphingButton) findViewById(R.id.accept_and_continue);


        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Century Gothic.ttf");
        username.setTypeface(custom_font1);
        switch_account.setTypeface(custom_font1);
        terms_and_conditions.setTypeface(custom_font1);
        Typeface roboto = Typeface.createFromAsset(getAssets(),  "fonts/Roboto-Light.ttf");
        conditions.setTypeface(roboto);
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/bold.TTF");
        welcome.setTypeface(custom_font2);

        progress = new ProgressDialog(this);
        //************************** GOOGLE SIGN**************************************************************************

        firebaseAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(Welcome.this)
                .enableAutoManage(Welcome.this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } )
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        //**********************************************DATA RETRIEVAL**************************************************

        db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);

        String a="1";

        c = db.rawQuery("SELECT * FROM auth WHERE sno='" + a + "'", null);
        if (c.moveToFirst()) {
            UID = c.getString(1);
            U_NAME=c.getString(3);
        }
        username.setText(U_NAME);


//        progress.setMessage("Initializing..");
//        progress.show();
//
//        mDatabaseReference= FirebaseDatabase.getInstance().getReference(UID).child("USER_LOGIN_DETAILS");
//        mDatabaseReference.addValueEventListener(new ValueEventListener(){
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot child : dataSnapshot.getChildren())
//                {
//                    String usrs=child.getValue(String.class);
//                    user_details_arrayList.add(usrs);
//                }
//                username.setText(user_details_arrayList.get(1));
//                progress.dismiss();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });





    }//*********************ON CREATE**********************************************************************************

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestSignInCode){

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (googleSignInResult.isSuccess()){

                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

                FirebaseUserAuth(googleSignInAccount);
            }

        }
    }

    public void FirebaseUserAuth(GoogleSignInAccount googleSignInAccount) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(Welcome.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> AuthResultTask) {

                        if (AuthResultTask.isSuccessful()){

                            firebaseUser = firebaseAuth.getCurrentUser();


                        }else {

                        }
                    }
                });
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


    public void switch_account(View view) {
        db = openOrCreateDatabase("AUTH_STATUS", Context.MODE_PRIVATE, null);

        String sno="1";
        c = db.rawQuery("SELECT * FROM auth WHERE sno='" + sno + "'", null);
        if (c.moveToFirst()) {
            db.execSQL("DELETE FROM auth WHERE sno='" + sno + "'");

            Intent nxt=new Intent(Welcome.this,Intro.class);
            startActivity(nxt);
        }


        try
        {

            firebaseUser = firebaseAuth.getCurrentUser();
            firebaseUser.delete();
            //mDatabaseReference= FirebaseDatabase.getInstance().getReference(UID);
           // mDatabaseReference.removeValue();
            Intent intro_screen_intent=new Intent(Welcome.this,Intro.class);
            startActivity(intro_screen_intent);
        }
        catch (Exception e)
        {
            Snackbar snackbar = Snackbar
                    .make(welcome_root_layout, "Server down.Please try again later", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    private void onMorphButton1Clicked(final MorphingButton btnMorph) {
        if (mMorphCounter1 == 0) {
            mMorphCounter1++;
            morphToSuccess(btnMorph);
        } else if (mMorphCounter1 == 1) {
            mMorphCounter1 = 0;
            morphToSuccess(btnMorph);
        }
    }

    private void morphToSuccess(final MorphingButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius(50)
                .width(400)
                .height(150)
                .color(R.color.subcolor)
                .colorPressed(R.color.morph_pressed)
                .text("Success")
                .icon(R.drawable.success);
        btnMorph.morph(circle);


        delay();   //waiting for button animation to finish
        update_REG_STATUS();  //updating the REG_STATUS for Splash support
    }

    private void update_REG_STATUS() {

        Cursor c = db.rawQuery("SELECT * FROM auth", null);
        try {
            String TABLE_NAME="auth",ColumnName="reg_status",rowID="1",ColumnSno="sno";
            String updateSql=" UPDATE " + TABLE_NAME + " SET " + ColumnName + " = '" + REG_STATUS + "' WHERE " + ColumnSno + " = "+ rowID;
            db.execSQL(updateSql);
        }
        catch (Exception e) {
            Snackbar snackbar = Snackbar
                    .make(welcome_root_layout, "Ram usage does'nt supported", Snackbar.LENGTH_LONG);
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
                    Intent intro_screen_intent=new Intent(Welcome.this,Main_screen.class);
                    startActivity(intro_screen_intent);
                }


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                    Intent MainscreenIntent = new Intent(getApplicationContext(), Main_screen.class);

                    Pair[] pairs = new Pair[2];
                    // pairs[0]=new Pair<View, String>(welcome_linear_layout,"welcome_transition");
                    pairs[0] = new Pair<View, String>(welcome, "dashboard_transition");
                    pairs[1] = new Pair<View, String>(username, "username_transition");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Welcome.this, pairs);
                    startActivity(MainscreenIntent, options.toBundle());
                }
            }
        }, 1000);
    }

    public void accept_and_continue(View view) {
        onMorphButton1Clicked(accept_and_continue);
    }
}
