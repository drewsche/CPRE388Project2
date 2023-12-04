package com.example.gcache;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PublicActivity extends AppCompatActivity {

    private static final String TAG = "PublicActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public);

        checkCurrentUser();
    }

    public void checkCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
//            Toast.makeText(MainActivity.this, "<!!DEVELOPER TOOL!!>" +
////                    " UID:\n" + user.getUid(),
////                    " Display Name:\n" + user.getDisplayName(),
//                    " E-mail:\n" + user.getEmail(),
//                    Toast.LENGTH_LONG).show();
        } else {
            // No user is signed in
            Intent toLogin = new Intent(this, LoginActivity.class);
            startActivity(toLogin);
        }
    }

    public void onCameraClicked(View view) {
            Log.d(TAG, "onClick: called");
            Intent toCamera = new Intent(this, GeoCamera.class);
            startActivity(toCamera);
    }

    public void onAccountClicked(View view) {
        Intent toAccount = new Intent(this, AccountActivity.class);
        startActivity(toAccount);
    }

    public void onMapsClicked(View v) {
        Intent toMaps = new Intent(this, MapsActivity.class);
        Log.d(TAG, "onMapsClicked: goToMaps");
        startActivity(toMaps);
    }
}