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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class MakePostActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MakePostActivity";
    ImageView imageCapture;
    TextView filePathTextView;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    setPicToThis(uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }

            });
    private void setPicToThis(Uri uri) {

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageCapture.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post);

        imageCapture = (ImageView) findViewById(R.id.imageCaptureView);



//        testing();

        demo();

    }

    private void demo() {
        imageCapture.setOnClickListener(this);
    }

    private void testing() {
        filePathTextView = (TextView) findViewById(R.id.filePathTextView);


        Intent getHere = getIntent();
        String filePath = getHere.getStringExtra("filePathToImage");
        Log.d(TAG, "onCreate: filePath: " + filePath);
        filePathTextView.setText(filePath);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        if(filePath != null) {
            imageCapture.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.imageCaptureView) {
            openMediaPicker();
        }
    }

    private void openMediaPicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }
}