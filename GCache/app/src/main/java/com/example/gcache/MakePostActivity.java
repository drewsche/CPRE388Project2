package com.example.gcache;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class MakePostActivity extends AppCompatActivity {

    private static final String TAG = "MakePostActivity";

    public static final String KEY_PHOTO_URI = "key_photo_uri";
    ImageView photoImageView;
    TextView filePathTextView;

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

        String photoUriString = getIntent().getExtras().getString(KEY_PHOTO_URI);
        if (photoUriString == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_PHOTO_URI);
        }

        displayPhoto(photoUriString);

    }

    private void displayPhoto(String photoUriString) {

//        filePathTextView.setText(photoUriString);

        Log.d(TAG, "onCreate: filePath: " + photoUriString);
        if(photoUriString != null) {
            Uri photoUri = Uri.parse(photoUriString);
            Glide.with(this)
                    .load(photoUri)
                    .into(photoImageView);
        }
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