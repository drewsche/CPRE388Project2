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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * This class represents the login page for the user, and is the first screen
 * the user is taken to when opening the app.
 *
 * Here the user is able to sign up with a new account by inputting
 * an email and password. If the user already has an account, they
 * will instead sign in.
 *
 * After this page, the user is taken to the main public page.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private EditText emailEditText;
    private EditText passwordEditText;

    /**
     * Sets login page and its variables, to include edit texts for email and password
     * @param savedInstanceState Saved instance of login page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.loginActivity_editText_email);
        passwordEditText = findViewById(R.id.loginActivity_editText_password);
    }

    /**
     * Checks if the user is signed into their account or created a new account
     * Updates the UI accordingly
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent toPublic = new Intent(this, PublicActivity.class);
            startActivity(toPublic);
        }
    }

    /**
     * Allows the user to input their email and password to sign in
     * @param view Sign in view
     */
    public void onSignInClicked(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        signIn(email, password);
    }

    /**
     * Allows the user to input an email and password to sign up
     * @param view Sign up view
     */
    public void onSignUpClicked(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        signUp(email, password);
    }

    /**
     * Check sign in parameters to include email and password.
     * If successful, UI is updated with signed-in user's info
     * If the sign in fails, error message is displayed to user
     * @param email String provided by user, which is a valid email
     * @param password String provided by user, which is a valid password
     */
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent reload = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(reload);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Checks sign up parameters,to include user email and password
     * If sign up is successful, UI is updated with the user's information and adds the user to Firebase
     * If failed, error message displayed to user
     * @param email string, which is user's valid email
     * @param password string, which is user's valid password
     */
    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.getUid())
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
                            Intent reload = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(reload);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}