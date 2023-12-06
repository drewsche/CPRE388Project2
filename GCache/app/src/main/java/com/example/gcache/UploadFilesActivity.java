package com.example.gcache;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class UploadFilesActivity extends AppCompatActivity implements View.OnClickListener{

    Button buttonUploadFiles;
    Button buttonFilesBackToMain;
    private static final String TAG = "UploadFilesActivity";

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }

            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_files);

        /**
         * Buttons
         */
        buttonUploadFiles = (Button) findViewById(R.id.buttonUploadFiles);
        buttonUploadFiles.setOnClickListener(this);
        buttonFilesBackToMain = (Button) findViewById(R.id.buttonFilesToMain);
        buttonFilesBackToMain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: called");
        if (v.getId() == R.id.buttonUploadFiles) {
            Log.d(TAG, "onClick: upload a file");
            uploadAFile();
        } else if(v.getId() == R.id.buttonFilesToMain) {
            Log.d(TAG, "onClick: Files Go Back to Main");
            Intent intent = new Intent(this, PublicActivity.class);
            startActivity(intent);
        }
    }

    private void uploadAFile() {
        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

    }





}