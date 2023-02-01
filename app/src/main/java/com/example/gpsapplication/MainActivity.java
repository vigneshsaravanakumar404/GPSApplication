package com.example.gpsapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Variables
    TextView textView, addressView;

    // Suppressing Dumb Stuff Android Studios Does
    @SuppressLint({"MissingPermission", "ServiceCast", "MissingInflatedId", "ServiceCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Variables
        textView = findViewById(R.id.textView);
        addressView = findViewById(R.id.addressView);

        // Request Location Permissions
        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            Boolean fineLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
            //Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
            if (fineLocationGranted != null && fineLocationGranted) {
                Toast.makeText(this, "Thanks for permissions!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Grant Permissions in Settings to Use App", Toast.LENGTH_LONG).show();
            }
        });
        locationPermissionRequest.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});


        // Creating Location Listener and display Location as it is updated
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();



                // Display Location to Phone
                textView.setText(latitude + " " + longitude);
                Log.d("TAG", "Latitude: " + latitude + "\nLongitude: " + longitude);
                Toast toast = Toast.makeText(getApplicationContext(), "Latitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_LONG);
                toast.show();


                // Display Address
                addressView.setText((CharSequence) getAddy((LocationListener) getApplicationContext(), latitude,longitude));



            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        });
        LocationListener locationListener = new LocationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Get latitude and longitude and display it
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("TAG","Latitude: " + latitude + "\nLongitude: " + longitude);

                // Display to Phone
                Toast toast = Toast.makeText(getApplicationContext(), "LOCATION CHANGED " + "Latitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_LONG);
                textView.setText(latitude + " " + longitude);
                toast.show();

                // Display Address to phone
                addressView.setText((CharSequence) getAddy(this, latitude,longitude));

            }
            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);



    }


    public Address getAddy(LocationListener context, double latitude, double longitude){

        //TODO: FIX  ERRORS WITH RETRIEVING ADDRESS

        Geocoder geocoder = new Geocoder((Context) context, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            return addresses.get(0);
        } else {
            return null;
        }
    }
}


//TODO: Steps 6 - 9