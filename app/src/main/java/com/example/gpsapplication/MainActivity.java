package com.example.gpsapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final DecimalFormat dfZeroPlaces = new DecimalFormat("0");
    private static final DecimalFormat dfTwoPlaces = new DecimalFormat("0.00");
    private static final DecimalFormat dfEightPlaces = new DecimalFormat("0.00000000");

    double distanceTravelled;
    int currentPlaceIndex, maxIndex = -1;

    LocationManager locationManager;
    LocationListener locationListener;
    TextView longitudes, latitudes, addressView, distanceTo, distanceTraveledView, longestAddress, longestTime;
    Location oldLocation;
    ArrayList<Place> places = new ArrayList<>();


    // Suppressing Dumb Stuff Android Studios Does
    @SuppressLint({"MissingPermission", "ServiceCast", "MissingInflatedId", "ServiceCast", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Variables
        longitudes = findViewById(R.id.longitude);
        latitudes = findViewById(R.id.latitude);
        addressView = findViewById(R.id.addressView);
        distanceTo = findViewById(R.id.distanceToView);
        distanceTraveledView = findViewById(R.id.distanceTraveledView);
        longestAddress = findViewById(R.id.longest);
        longestTime = findViewById(R.id.longestTime);

        // If permissions are granted then run app
        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
            if (fineLocationGranted != null && fineLocationGranted) {
                // Useless Code
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }


                locationListener = new LocationListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        // Get latitude and longitude and display it
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        String address = getAddy(latitude, longitude);

                        // Calculations
                        boolean isNewAddress = true;
                        if (oldLocation == null) {
                            distanceTravelled = 0;
                            currentPlaceIndex = 0;
                            places.add(new Place(address));
                            places.get(0).setStartTime();
                        } else {
                            distanceTravelled += location.distanceTo(oldLocation);
                            for (int i = 0; i < places.size(); i++) {
                                if (places.get(i).getAddress().equals(address)) {
                                    currentPlaceIndex = i;
                                    isNewAddress = false;
                                    break;
                                }
                            }
                        }
                        if (isNewAddress) {
                            places.add(new Place(address));
                            currentPlaceIndex = places.size() - 1;
                        }
                        places.get(currentPlaceIndex).updateTime();

                        oldLocation = location;
                        distanceTraveledView.setText("Distance Travelled: " + dfTwoPlaces.format(distanceTravelled / 1609.0) + " Miles");

                        latitudes.setText("Latitude: " + dfEightPlaces.format(latitude));
                        longitudes.setText(" Longitude: " + dfEightPlaces.format(longitude));

                        // Display Address to phone
                        Location newYorkCity = new Location("New York City");
                        newYorkCity.setLatitude(40.7128);
                        newYorkCity.setLongitude(-74.0060);

                        addressView.setText(getAddy(latitude, longitude));
                        Log.d("TAG123", getAddy(latitude, longitude));

                        // Calculating Distance to my house
                        distanceTo.setText("Distance to New York City: " + location.distanceTo(newYorkCity));

                        // Favorite place Code
                        maxIndex = 0;
                        for (int i = 0; i < places.size(); i++) {
                            if (places.get(i).getTimeSpent() > places.get(maxIndex).getTimeSpent()) {
                                maxIndex = i;
                            }
                        }
                        longestAddress.setText("Location: " + places.get(maxIndex).getAddress());
                        longestTime.setText("Time Spent: " + dfZeroPlaces.format(places.get(maxIndex).getTimeSpent() / 15000.0) + " Seconds");
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


            } else {
                Toast.makeText(this, "Grant Permissions in Settings to Use App", Toast.LENGTH_LONG).show();
                latitudes.setText("NO PERMISSION");
                longitudes.setText("NO PERMISSION");
                addressView.setText("NO PERMISSION");
                distanceTo.setText("NO PERMISSION");
                distanceTraveledView.setText("NO PERMISSION");

            }
        });
        locationPermissionRequest.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});


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
// TODO: Make app look good
// TODO: Shorten address displayed
// TODO: Create background thread to update time spent

// Optional
// TODO: Add bonus factor
