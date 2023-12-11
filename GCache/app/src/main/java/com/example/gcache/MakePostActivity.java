package com.example.gcache;

import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gcache.model.Post;
import com.example.gcache.model.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.Transaction;

public class MakePostActivity extends AppCompatActivity {

    private static final String TAG = "MakePostActivity";

    public static final String KEY_PHOTO_URI = "key_photo_uri";
    ImageView photoImageView;
    TextView filePathTextView;
    TextView pointTotalTextView;
    TextView metPersonTextView;
    TextView locationNameTextView;
    TextView distanceTextView;
    TextView distancePointsTextView;
    private FirebaseUser fBUser;

    private FirebaseFirestore mFirestore;
    private DocumentReference mUserRef;
    private ListenerRegistration mUserRegistration;
    private FusedLocationProviderClient fusedLocationClient;
    private GeoPoint homeCoords;
    private Location lastLocation;
    private int pointTotal;
    private double distance;

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
        pointTotalTextView = findViewById(R.id.makePostActivity_textView_pointTotal);
        metPersonTextView = findViewById(R.id.makePostActivity_textView_metPerson);
        locationNameTextView = findViewById(R.id.makePostActivity_textView_locationName);
        distanceTextView = findViewById(R.id.makePostActivity_textView_distance);
        distancePointsTextView = findViewById(R.id.makePostActivity_textView_distancePoints);

        getLocation();

        String photoUriString = getIntent().getExtras().getString(KEY_PHOTO_URI);
        if (photoUriString == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_PHOTO_URI);
        }

        displayPhoto(photoUriString);

        fBUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the user
        mUserRef = mFirestore.collection("users").document(fBUser.getUid());
        mUserRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, retrieve the value of "field1"
                        homeCoords = documentSnapshot.getGeoPoint("home");
                    }

                    // Move the distance calculation logic inside the callback
                    if (lastLocation != null) {
                        distance = calculateDistance(lastLocation, homeCoords);
                        distance *= 10000;
                        distance = Math.round(distance);
                        distance /= 10000;
                        distanceTextView.setText(distance + " Miles From Home");
                        int distancePoints = (int) ((Math.ceil(distance)) * 5);
                        distancePointsTextView.setText("+" + distancePoints + " Points");
                        pointTotal += distancePoints;
                        updatePointTotalTextView();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure if needed
                });
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

    public void getLocation() {
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
                                                    lastLocation = location;
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
                                                    lastLocation = location;
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
    }
    private double calculateDistance(Location location, GeoPoint homeCoords) {

        Geocoder geocoder = new Geocoder(MakePostActivity.this);
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), homeCoords.getLatitude(), homeCoords.getLongitude(), results);
        double distanceInMeters = results[0];

        return (distanceInMeters / 1609.344);
    }

    private void updatePointTotalTextView() {
        pointTotalTextView.setText("Point Total:\n" + pointTotal);
    }

    private void openMediaPicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private Task<Void> addPost(String visibility) {
        final DocumentReference postRef = mFirestore.collection("posts").document();
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {
                User user = transaction.get(mUserRef)
                        .toObject(User.class);
                int newPoints = user.getPoints() + pointTotal;
                user.setPoints(newPoints);
                Post post = new Post();
                post.setDateTime(Timestamp.now());
                post.setDistance(distance);
                GeoPoint lastLocationCoords = new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
                post.setLocationCoords(lastLocationCoords);
                post.setLocationName(locationNameTextView.getText().toString());
                post.setMetPerson(metPersonTextView.getText().toString());
                post.setPhoto("https://raw.githubusercontent.com/julien-gargot/images-placeholder/master/placeholder-portrait.png");
                post.setPoints(pointTotal);
                post.setPoster(user.getDisplayName());
//                    post.setPosterUID(mUserRef.toString());
                post.setPosterUID(fBUser.getUid());
                post.setVisibility(visibility);
                transaction.set(mUserRef, user);
                transaction.set(postRef, post);
                return null;
            }
        });
    }

    public void onPhotoClicked(View view) {
        openMediaPicker();
    }

    public void onPostPrivatelyClicked(View view) {
        if((metPersonTextView.getText().toString().isEmpty()) || (locationNameTextView.getText().toString().isEmpty())) {
            Toast.makeText(MakePostActivity.this, "All text fields must be filled",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            addPost("Private");
            onAlbumClicked(view);
        }
    }
    public void onPostPubliclyClicked(View view) {
        if((metPersonTextView.getText().toString().isEmpty()) || (locationNameTextView.getText().toString().isEmpty())) {
            Toast.makeText(MakePostActivity.this, "All text fields must be filled",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            addPost("Public");
            onPublicClicked(view);
        }
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