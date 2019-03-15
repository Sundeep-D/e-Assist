package com.example.sundeep.egen_2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Nearby_Doctors extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    TextView cords;
    LatLng latLngCurrent;
    Double lat,Long;
    CameraUpdate update;
    SQLiteDatabase db;
    Cursor c;
    Marker markerName;
    Marker currentMarker = null,oldMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby__doctors);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try
        {
            db = openOrCreateDatabase("USER_DETAILS", Context.MODE_PRIVATE, null);

            int sno1=1;
            c = db.rawQuery("SELECT * FROM user WHERE sno1='" + sno1 + "'", null);
            if (c.moveToFirst()) {

                lat=Double.valueOf(c.getString(14));
                Long = Double.valueOf(c.getString(15));

            }



        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
        }


        cords=findViewById(R.id.cords);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        client.connect();

        LatLng latLngold=new LatLng(lat,Long);
        update=CameraUpdateFactory.newLatLngZoom(latLngold,15);
        oldMarker = mMap.addMarker(new MarkerOptions().position(latLngold).title("S U N D E E P").icon(BitmapDescriptorFactory.fromResource(R.drawable.sick)));
        mMap.animateCamera(update);

    }


    @Override
    public void onLocationChanged(Location location) {


        if(location== null)
        {
            Toast.makeText(getApplicationContext(),"No location",Toast.LENGTH_SHORT).show();

        }
        else
        {
           // Toast.makeText(getApplicationContext(),"Moving",Toast.LENGTH_SHORT).show();

            //mMap.clear();


          latLngCurrent=new LatLng(location.getLatitude(),location.getLongitude());

             update=CameraUpdateFactory.newLatLngZoom(latLngCurrent,15);


            lat=location.getLatitude();
            Long=location.getLongitude();

            oldMarker.remove();


            if (currentMarker!=null) {
                currentMarker.remove();
                currentMarker=null;
            }

            if (currentMarker==null) {
                currentMarker = mMap.addMarker(new MarkerOptions().position(latLngCurrent).title("S U N D E E P").icon(BitmapDescriptorFactory.fromResource(R.drawable.signal)));

                //mMap.animateCamera(update);
            }

            cords.setText(location.getLatitude()+"  -  "+location.getLongitude());
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        request = new LocationRequest().create();
        request.setInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void findNearBy(View view) {

        Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();
     nearby();




    }

    public void nearby()
    {
       // Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();
        StringBuilder stringBuilder=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("&location="+lat+","+Long);
        stringBuilder.append("&radius="+10000);
        stringBuilder.append("&keyword="+"hospital,emergency");
        stringBuilder.append("&key="+getResources().getString(R.string.Gmap_API_KRY));

        String url=stringBuilder.toString();

        Object dataTransfer[]=new Object[2];
        dataTransfer[0]=mMap;
        dataTransfer[1]=url;

        GetNearbyPlaces getNearbyPlaces=new GetNearbyPlaces();

        // Toast.makeText(getApplicationContext(),,Toast.LENGTH_LONG).show();
        getNearbyPlaces.execute(dataTransfer);





    }
}
