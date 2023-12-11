package com.example.gcache;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gcache.model.Post;
import com.example.gcache.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Transaction;

/**
 * This class represents the account page for the user.
 * Allows them to update their displayed user name, total points
 * and update their home location via coordinates.
 * The user is able to sign out, that takes them back to login page.
 */
public class AccountActivity extends AppCompatActivity implements
        EventListener<DocumentSnapshot> {

    private static final String TAG = "AccountActivity";
    private ImageView profilePicImageView;
    private EditText displayNameEditText;
    private EditText homeLocationEditText;
    private TextView pointsTextView;
    private FirebaseUser user;

    private FirebaseFirestore mFirestore;
    private DocumentReference mUserRef;
    private ListenerRegistration mUserRegistration;

    private double latitude = 0;
    private double longitude = 0;

    /**
     * Sets account page and variables, to include edit texts and text views.
     * Also updates the point display.
     * @param savedInstanceState State of account page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        profilePicImageView = findViewById(R.id.accountActivity_imageView_profilePic);
        pointsTextView = findViewById(R.id.accountActivity_textView_points);
        displayNameEditText = findViewById(R.id.accountActivity_editText_displayName);
        homeLocationEditText = findViewById(R.id.accountActivity_editText_homeLocation);
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the user
        mUserRef = mFirestore.collection("users").document(user.getUid());

//        //Update totalPoints TextView with initial total points
//        updatePointsDisplay();

        String lat = getIntent().getStringExtra("lat");
        String lng = getIntent().getStringExtra("lng");
        String coords = lat + "," + lng;

        if(lat != null && lng != null) {
            homeLocationEditText.setText(coords);
        }



    }

    /**
     * Updates page on start and adds snapshot listener
     */
    @Override
    public void onStart() {
        super.onStart();

        mUserRegistration = mUserRef.addSnapshotListener(this);
    }

    /**
     * Updates page on stop and removes user registration
     */
    @Override
    public void onStop() {
        super.onStop();

        if (mUserRegistration != null) {
            mUserRegistration.remove();
            mUserRegistration = null;
        }
    }

    /**
     * Adds document snapshot on event
     * @param snapshot The value of the event. {@code null} if there was an error.
     * @param e The error if there was error. {@code null} otherwise.
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "user:onEvent", e);
            return;
        }

        onUserLoaded(snapshot.toObject(User.class));
    }

    /**
     * When user is loaded, sets profile pic, total points view and displayed name
     * @param user the user with parameters such as profile pic, name, and points
     */
    private void onUserLoaded(User user) {

        // Photo image
        Glide.with(profilePicImageView.getContext())
                .load(user.getProfilePic())
                .into(profilePicImageView);

        pointsTextView.setText("Points:\n" + user.getPoints());
        displayNameEditText.setText(user.getDisplayName());
    }

    /**
     * Task that changes display name
     * @param userRef value of reference to user
     * @param newDisplayName string value of new display name for user
     * @return null value
     */
    private Task<Void> changeDisplayName(final DocumentReference userRef,
                                 final String newDisplayName) {
        // In a transaction, update displayName
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {
                User user = transaction.get(userRef)
                        .toObject(User.class);
                // Set new restaurant info
                user.setDisplayName(newDisplayName);
                // Commit to Firestore
                transaction.set(userRef, user);
                return null;
            }
        });
    }

//    public void onDisplay(Rating rating) {
//
//    }


//    /**
//     * Updates totalPoints TextView with the user's current sum of points
//     */
//    public void updatePointsDisplay() {
//        totalPointsTextView.setText("Points:\n" + String.valueOf(totalPoints));
//    }

    /**
     * Allows the user to edit and update their user name
     * @param view The view that shows user name
     */
    public void onSaveDisplayNameClicked(View view) {
        // In a transaction, add the new rating and update the aggregate totals
        changeDisplayName(mUserRef, displayNameEditText.getText().toString())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Display Name Changed");
                        Toast.makeText(AccountActivity.this, "New Display Name Saved!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add rating failed", e);

                        // Show failure message
                        Toast.makeText(AccountActivity.this, "Failed to change Display Name",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Allows the user to edit and save their home location
     * @param view The view that shows user home location
     */
    public void onSaveHomeClicked(View view) {
        String lat = getIntent().getStringExtra("lat");
        String lng = getIntent().getStringExtra("lng");
        String coords = lat + "," + lng;

        if(lat != null && lng != null) {
//            homeLocationEditText.setText(coords);
            //Do firebase transaction
            GeoPoint geoPoint = new GeoPoint(Double.valueOf(lat), Double.valueOf(lng));
            Log.d(TAG, "onSaveHomeClicked: called Lat: " + lat + "Lng: " + lng);
            changeHomeCity(mUserRef, geoPoint);
        } else {
            //Fail Toast
        }

    }

    /**
     * Moves user to map to pick a location
     * @param view associated with this activity
     */
    public void onUpdateHomeClicked(View view) {
        // TODO: Save the location city string to firebase
        String homeCity = String.valueOf(homeLocationEditText.getText());
        Log.d(TAG, "onSaveHomeClicked: homeCity: " + homeCity);
        Intent confirmCity = new Intent(this, MapsActivity.class).putExtra("cityName", homeCity);
        startActivity(confirmCity);

    }

    /**
     * Upload the new coordinates to firebase
     * @param userRef user logged in.
     * @param newHomeCity new coordinates to be pushed to firebase
     * @return null if working properly. else error
     */

    private Task<Void> changeHomeCity(final DocumentReference userRef, final GeoPoint newHomeCity) {
        Log.d(TAG, "changeHomeCity: called");
        Log.d(TAG, "changeHomeCity: Lat" + newHomeCity.getLatitude() + "Lng: "+ newHomeCity.getLongitude());
        Log.d(TAG, "changeHomeCity: userRef" + userRef.getId());
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {
                User user = transaction.get(userRef)
                        .toObject(User.class);

                // Commit to Firestore
                transaction.set(userRef, newHomeCity);
                return null;
            }
        });


    }

    /**
     * Takes the user to album page
     * @param view the album view
     */
    public void onAlbumClicked(View view) {
        Intent toAlbum = new Intent(this, AlbumActivity.class);
        startActivity(toAlbum);
    }

    /**
     * Takes user to the public (main) page
     * @param view the public view
     */
    public void onPublicClicked(View view) {
        Intent toPublic = new Intent(this, PublicActivity.class);
        startActivity(toPublic);
    }

    /**
     * Takes user to maps page
     * @param v the map view
     */
    public void onMapsClicked(View v) {
        Intent toMaps = new Intent(this, MapsActivity.class);
        Log.d(TAG, "onMapsClicked: goToMaps");
        startActivity(toMaps);
    }

    /**
     * Signs the user out of their account.
     * Takes user back to login page
     * @param view the sign out view
     */
    public void onSignOutClicked(View view) {
        signOut();
        Intent toPublic = new Intent(this, PublicActivity.class);
        startActivity(toPublic);
    }

    /**
     * Sign out signal to firebase
     */
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}