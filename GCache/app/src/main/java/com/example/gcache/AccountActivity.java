package com.example.gcache;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * This class represents the account page for the user.
 * Allows them to update their displayed user name, total points
 * and update their home location via coordinates.
 * The user is able to sign out, that takes them back to login page.
 */
public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";
    private FirebaseUser user;
    private EditText displayNameEditText;
    private EditText homeLocationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        user = FirebaseAuth.getInstance().getCurrentUser();
        displayNameEditText = findViewById(R.id.accountActivity_editText_displayName);
        displayNameEditText.setText(user.getDisplayName());
        homeLocationEditText = findViewById(R.id.accountActivity_editText_homeLocation);
    }

    /**
     * Allows the user to edit and update their user name
     * @param view The view that shows user name
     */
    public void onSaveDisplayNameClicked(View view) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayNameEditText.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
        Toast.makeText(AccountActivity.this, "New Display Name Saved!",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Allows the user to edit and save their home location
     * @param view The view that shows user home location
     */
    public void onSaveHomeClicked(View view) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(homeLocationEditText.getText().toString())
                .build();
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