package com.example.cortex.lokasi;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private static final int MY_PERMISSIONS_REQUEST = 99;    // int bebas max 1 byte
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    TextView mLatText;
    TextView mLongText;

    LocationRequest mLocationRequest;

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        // 10 detik sekali meminta lokasi 10000ms = 10 detik
        mLocationRequest.setInterval(10000);
        // tapi tidak boleh lebih cepat dari 5 detik
        mLocationRequest.setFastestInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // tambahan
        mLatText = (TextView) findViewById(R.id.tvLat);
        mLongText = (TextView) findViewById(R.id.tvLong);
        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // cek permision
        // mulai Android 6 (API 23), permission dilakukan secara dinamik (tidak saat instalasi saja)
        // untuk jenis permision tertentu, termasuk lokasi

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            // tampilkan dialog minta ijin
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //permission diberikan, mulai ambil lokasi
                buildGoogleApiClient();

            } else {

                //permssion tidak diberikan, tampilkan pesan
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Tidak mendapat ijin, tidak dapat mengambil lokasi");
                ad.show();
            }
            return;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setMessage("Update Lokasi");
        ad.show();

        mLatText.setText("Lat : " +String.valueOf(location.getLatitude()));
        mLongText.setText("Long : " +String.valueOf(location.getLongitude()));
    }
}
