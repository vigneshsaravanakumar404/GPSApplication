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
    TextView textView, addressView, distanceTo;
    LocationManager locationManager;
    LocationListener locationListener;
    double tempLat, tempLong, distanceTravelled;
    Location tempLocation;
    boolean first = true;

    // Suppressing Dumb Stuff Android Studios Does
    @SuppressLint({"MissingPermission", "ServiceCast", "MissingInflatedId", "ServiceCast"})
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Variables
        textView = findViewById(R.id.textView);
        addressView = findViewById(R.id.addressView);
        distanceTo = findViewById(R.id.distanceTo);

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


        // Useless Code
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                textView.setText("Latitude: " + latitude + " Longitude: " + longitude);
                Log.d("TAG", "Latitude: " + latitude + "\nLongitude: " + longitude);


                // Display Address
                addressView.setText("Address: " + getAddy(latitude, longitude));


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




        // Useful Code
        locationListener = new LocationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Get latitude and longitude and display it
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Storing last location
                if (first){
                    first = false;
                    tempLat = latitude;
                    tempLong = longitude;
                    distanceTravelled = 0;
                }
                else{
                    Location tempLocation = new Location("New York City");
                    tempLocation.setLatitude(40.7128);
                    tempLocation.setLongitude(-74.0060);
                    distanceTravelled = tempLocation.distanceTo(location);
                    //TODO: Formula to convert delta lat, long to distance and fix this
                }


                Log.d("TAG", "Latitude: " + latitude + "\nLongitude: " + longitude);

                /*
                 Display to Phone
                 Toast toast = Toast.makeText(getApplicationContext(), "LOCATION CHANGED " + "Latitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_SHORT);
                toast.show();
                */

                textView.setText("Latitude: " + latitude + " Longitude: " + longitude);


                // Display Address to phone
                Location newYorkCity = new Location("New York City");
                newYorkCity.setLatitude(40.7128);
                newYorkCity.setLongitude(-74.0060);

                addressView.setText(getAddy(latitude, longitude));


                // Calculating Distance to my house
                distanceTo.setText("Distance to New York City: " + location.distanceTo(newYorkCity));


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




    public String getAddy(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            return address + ", " + city + ", " + state + ", " + country + ", " + postalCode + ", " + knownName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}


// To Do List
    // Mandatory
    //TODO: Step 9
    //TODO: check against rubric
    //TODO: make it work first time
    //TODO: Code if permission is denied

    // Optional
    //TODO: clean up code
    //TODO: Make display neatly
    //TODO: Add bonus factor
