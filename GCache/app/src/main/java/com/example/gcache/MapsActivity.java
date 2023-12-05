package com.example.gcache;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.gcache.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static String[] DESIRED_PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    Geocoder geocoder;
    Geocoder.GeocodeListener geocodeListener;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private Location curLocation;
    private String homeString;
    private Location homeLocation;

    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getCurrentLocation();
    }
    private void getCurrentLocation() {
        /**
         * Check if I have permission to access COARSE/FINE Location
         */

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            /**
             * I don't have permissions so request Permissions
             */
            ActivityCompat.requestPermissions(this, DESIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
            getCurrentLocation();
        } else {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LatLng curLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(curLatLng).title("CurrentLocation"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(curLatLng));
                                curLocation = location;
                            }
                        }

                    });
        }
    }
    private void distFromHome() {
        int distFromHome = 0;
        geocodeListener = new Geocoder.GeocodeListener() {
            @Override
            public void onGeocode(@NonNull List<Address> list) {
                Address a = list.get(0);
                Log.d(TAG, "Location of " + homeString);
                homeLocation = new Location("Destination");
                homeLocation.setLatitude(a.getLatitude());
                homeLocation.setLongitude(a.getLongitude());


                Log.d(TAG, "onGeocode: Lat: " +
                        homeLocation.getLatitude() + " LNG: " + homeLocation.getLongitude());

                /**
                 * Display the distance to my goal.
                 */
                displayDistance(curLocation, homeLocation);




            }
            private void displayDistance(Location l1, Location l2) {
                float distanceMeters;
                float distanceKm;
                float[] results = new float[1];
                Location.distanceBetween(curLocation.getLatitude(), curLocation.getLongitude(), homeLocation.getLatitude(), homeLocation.getLongitude(), results);
                distanceMeters = results[0];
                distanceKm = distanceMeters/1000;
                Log.d(TAG, "displayDistance: Distance between: " + distanceKm + " meters");

            }
        };
        /**
         * Implememnt their listener
         */
        geocoder.getFromLocationName(homeString,1, geocodeListener);}


}