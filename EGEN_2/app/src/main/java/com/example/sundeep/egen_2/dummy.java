package com.example.sundeep.egen_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.model.Dash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class dummy extends AppCompatActivity {
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);

        mDatabaseReference= FirebaseDatabase.getInstance().getReference("EGEN_USERS");
        mDatabaseReference.child("sfds").setValue("skjfhskjfskf");
    }
}
