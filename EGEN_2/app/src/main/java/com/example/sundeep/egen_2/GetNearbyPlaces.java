package com.example.sundeep.egen_2;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetNearbyPlaces extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    InputStream is;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    String data;


    @Override
    protected String doInBackground(Object... params) {

        mMap=(GoogleMap)params[0];
        url=(String)params[1];


        try
        {
            URL myurl= new URL(url);
            HttpsURLConnection httpsURLConnection=(HttpsURLConnection)myurl.openConnection();
            httpsURLConnection.connect();
            is=httpsURLConnection.getInputStream();
            bufferedReader=new BufferedReader(new InputStreamReader(is));

            String line="";
            stringBuilder=new StringBuilder();

            while ((line=bufferedReader.readLine())!= null)
            {

                stringBuilder.append(line);
            }

            data=stringBuilder.toString();

            Log.d("",data);



        }catch (Exception e)
        {

        }





        return data;
    }

    @Override
    protected void onPostExecute(String s) {


        try
        {
            JSONObject parentObject =new JSONObject(s);
            JSONArray resultArray=parentObject.getJSONArray("results");


            for (int i=0;i<resultArray.length();i++)
            {
                JSONObject jsonObject=resultArray.getJSONObject(i);
                JSONObject locationobj=jsonObject.getJSONObject("geometry").getJSONObject("location");

                String latitude=locationobj.getString("lat");
                String longitude=locationobj.getString("lng");

                JSONObject nameObject=resultArray.getJSONObject(i);

                String name_hospital=nameObject.getString("name");
                String vicinity=nameObject.getString("vicinity");


                LatLng latLng=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.title(name_hospital+", "+vicinity);
               // markerOptions.title(vicinity);
                markerOptions.position(latLng);

                mMap.addMarker(markerOptions);



            }


        }
        catch (Exception e)
        {

        }

    }
}
