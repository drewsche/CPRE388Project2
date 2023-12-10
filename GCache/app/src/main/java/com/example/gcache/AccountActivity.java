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

public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";

    private FirebaseUser user;

    private EditText displayNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        user = FirebaseAuth.getInstance().getCurrentUser();
        displayNameEditText = findViewById(R.id.accountActivity_editText_displayName);
        displayNameEditText.setText(user.getDisplayName());
    }

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

    public void onSignOutClicked(View view) {
        signOut();
        Intent toPublic = new Intent(this, PublicActivity.class);
        startActivity(toPublic);
    }

    //After signing out, takes you back to login page
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}