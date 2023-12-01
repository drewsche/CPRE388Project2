package com.example.gcache;

import static android.os.Environment.DIRECTORY_PICTURES;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.common.util.concurrent.ListenableFuture;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


public class GeoCamera extends AppCompatActivity implements View.OnClickListener{

    private static final String[] DESIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private Queue imageQ;

    private ImageView imageView;

    private PreviewView previewView;

    private Button cameraBackToMain;

    private Button shutterButton;

    private File photosDir;
    private String currentPhotoPath;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        /**
         * Buttons
         */
        shutterButton = (Button) findViewById(R.id.buttonCameraBackToMain);
        shutterButton.setOnClickListener(this);

        cameraBackToMain = (Button)findViewById(R.id.buttonTakePhotoShutter);
        cameraBackToMain.setOnClickListener(this);


        /**
         * Camera Setup
         */
        previewView = findViewById(R.id.preview_view);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        checkPermissions();
        checkPermissions();

        /**
         * Write Photo to file
         */
        photosDir = getExternalFilesDir(DIRECTORY_PICTURES);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonTakePhotoShutter) {
            //take photo shutter
            Log.d(TAG, "onClick: Take Photo");
            takePhoto();
        } else if(v.getId() == R.id.buttonCameraBackToMain) {
            //camera backToMainIntent
            Log.d(TAG, "onClick: Want to go to main");
            Intent intent = new Intent(this, PublicActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void takePhoto() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "NEW_IMAGE");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues).build();
        ImageCapture.OnImageSavedCallback imageSavedCallback = new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Log.d(TAG, "onImageSaved: File Saved: " + outputFileResults.getSavedUri());
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.d(TAG, "onError: Error in saving image. Try again" + exception);
            }
        };

        ProcessCameraProvider cameraProvider = null;
        try {
            cameraProvider = cameraProviderFuture.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();



        ImageCapture imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();
//        Do this step like it is below.
        Camera capCam = cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture);


        imageCapture.takePicture(outputFileOptions,
                ContextCompat.getMainExecutor(this),
                imageSavedCallback);

    }

    private void checkPermissions() {
            if (ActivityCompat.checkSelfPermission(this, DESIRED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ) {
                /**
                 * I don't have permissions so request Permissions
                 */
                Log.d(TAG, "checkPermissions: Don't have permissions.");
                ActivityCompat.requestPermissions(this, DESIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
            } else {
                /**
                 * Do Camera Things
                 */
                Log.d(TAG, "checkPermissions: Starting Camera Setup!");
                setupCamera();

            }

    }

    private void setupCamera() {

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
        Log.d(TAG, "setupCamera: Camera Setup Complete");



    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void bindPreview(ProcessCameraProvider cameraProvider) {

        /**
         * Build the 3 objects needed as arguments to cameraProvider.bindToLifecycle
         */
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());


        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
        Log.d(TAG, "bindPreview: Binding Complete");

    }





    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "dispatchTakePictureIntent: Error creating file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }



}