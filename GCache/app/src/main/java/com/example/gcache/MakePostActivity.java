package com.example.gcache;

import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MakePostActivity extends AppCompatActivity {

    private static final String TAG = "MakePostActivity";

    public static final String KEY_PHOTO_URI = "key_photo_uri";
    ImageView photoImageView;
    TextView filePathTextView;
    TextView distanceTextView;
    private FirebaseUser user;

    private FirebaseFirestore mFirestore;
    private DocumentReference mUserRef;
    private ListenerRegistration mUserRegistration;
    private FusedLocationProviderClient fusedLocationClient;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    setPicToThis(uri);
                }
                else {
                    Log.d("PhotoPicker", "No media selected");
                }

            });
    private void setPicToThis(Uri uri) {

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            photoImageView.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post);

        photoImageView = findViewById(R.id.makePostActivity_imageView_photo);
        filePathTextView = findViewById(R.id.makePostActivity_textView_filePath);
        distanceTextView = findViewById(R.id.makePostActivity_textView_distance);

        String photoUriString = getIntent().getExtras().getString(KEY_PHOTO_URI);
        if (photoUriString == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_PHOTO_URI);
        }

        displayPhoto(photoUriString);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the user
        mUserRef = mFirestore.collection("users").document(user.getUid());
        mUserRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, retrieve the value of "field1"
                        GeoPoint homeCoords = documentSnapshot.getGeoPoint("home");
                    }
                })
                .addOnFailureListener(e -> {
                });

//        Location lastLocation = getLocation();
//        double distance = calculateDistance(lastLocation, homeCoords);
//        distanceTextView.setText(distance + "Miles From Home");
    }

    private void displayPhoto(String photoUriString) {

//        filePathTextView.setText(photoUriString);

        Log.d(TAG, "onCreate: filePath: " + photoUriString);
        if(photoUriString != null) {
            Uri photoUri = Uri.parse(photoUriString);
            Glide.with(photoImageView.getContext())
                    .load(photoUri)
                    .into(photoImageView);
        }
    }

    public Location getLocation() {
        final Location[] lastLocation = new Location[1];
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                }
                                fusedLocationClient.getLastLocation()
                                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                // Got last known location. In some rare situations this can be null.
                                                if (location != null) {
                                                    lastLocation[0] = location;
                                                }
                                            }
                                        });
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                }
                                fusedLocationClient.getLastLocation()
                                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                // Got last known location. In some rare situations this can be null.
                                                if (location != null) {
                                                    lastLocation[0] = location;
                                                }
                                            }
                                        });
                            } else {
                                // No location access granted.
                            }
                        }
                );

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
        return lastLocation[0];
    }
    private double calculateDistance(Location location, GeoPoint homeCoords) {

        Geocoder geocoder = new Geocoder(MakePostActivity.this);
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), homeCoords.getLatitude(), homeCoords.getLongitude(), results);
        double distanceInMeters = results[0];

        return (distanceInMeters / 1609.344);
    }

    private void openMediaPicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    public void onPhotoClicked(View view) {
        openMediaPicker();
    }

    public void onAlbumClicked(View view) {
        Intent toAlbum = new Intent(this, AlbumActivity.class);
        startActivity(toAlbum);
    }
    public void onPublicClicked(View view) {
        Intent toPublic = new Intent(this, PublicActivity.class);
        startActivity(toPublic);
    }
    public void onMapsClicked(View v) {
        Intent toMaps = new Intent(this, MapsActivity.class);
        Log.d(TAG, "onMapsClicked: goToMaps");
        startActivity(toMaps);
    }
    public void onAccountClicked(View view) {
        Intent toAccount = new Intent(this, AccountActivity.class);
        startActivity(toAccount);
    }
}