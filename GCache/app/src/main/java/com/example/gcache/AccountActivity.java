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
    }

    @Override
    public void onStart() {
        super.onStart();

        mUserRegistration = mUserRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mUserRegistration != null) {
            mUserRegistration.remove();
            mUserRegistration = null;
        }
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "user:onEvent", e);
            return;
        }

        onUserLoaded(snapshot.toObject(User.class));
    }

    private void onUserLoaded(User user) {

        // Photo image
        Glide.with(profilePicImageView.getContext())
                .load(user.getProfilePic())
                .into(profilePicImageView);

        pointsTextView.setText("Points:\n" + user.getPoints());
        displayNameEditText.setText(user.getDisplayName());
    }

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
        // TODO: Save the location city string to firebase
        String homeCity = String.valueOf(homeLocationEditText.getText());
        Log.d(TAG, "onSaveHomeClicked: homeCity: " + homeCity);
//        Intent confirmCity = new Intent(this, MapsActivity.class).putExtra("cityName", homeCity);
//        startActivity(confirmCity);
        changeHomeCity(mUserRef, displayNameEditText.getText().toString())



    }

    private Task<Void> changeHomeCity(final DocumentReference userRef, final String newHomeCity) {
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {
                User user = transaction.get(userRef)
                        .toObject(User.class);
                // Set new restaurant info
                GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                user.setHome();
                // Commit to Firestore
                transaction.set(userRef, user);
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