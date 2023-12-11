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
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;


public class GeoCamera extends AppCompatActivity implements View.OnClickListener {

    //Permissions
    private static final String[] DESIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "GeoCamera";

    //Camera Setup
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private Button cameraBackToMain;
    private Button shutterButton;
    private String currentPhotoPath;
    private Button switchLens;

    /**
     * Variables for showing camera preview.
     */
    Camera camera;
    Preview preview;
    CameraSelector cameraSelector;
    ProcessCameraProvider cameraProvider;

    /**
     * Variables for saving photo
     */
    ContentValues contentValues;
    ImageCapture.OutputFileOptions outputFileOptions;





    // Storing the key and its value as the data fetched from edittext

     private int numPics = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        /**
         * Buttons
         */
        shutterButton = (Button) findViewById(R.id.buttonCameraBackToMain);
        shutterButton.setOnClickListener(this);

        cameraBackToMain = (Button) findViewById(R.id.buttonTakePhotoShutter);
        cameraBackToMain.setOnClickListener(this);

        switchLens = (Button) findViewById(R.id.buttonSwitchLens);
        switchLens.setOnClickListener(this);


        /**
         * Camera Setup
         */
        previewView = findViewById(R.id.preview_view);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        checkPermissions();
        checkPermissions();


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonTakePhotoShutter) {
            //take photo shutter
            Log.d(TAG, "onClick: Take Photo");
            takePhoto();
        } else if (v.getId() == R.id.buttonCameraBackToMain) {
            //camera backToMainIntent
            Log.d(TAG, "onClick: Want to go to main");
            Intent intent = new Intent(this, PublicActivity.class);
            startActivity(intent);
            finish();
        } else if (v.getId() == R.id.buttonSwitchLens) {
            switchLens();
        }
    }


    ImageCapture.OnImageSavedCallback imageSavedCallback = new ImageCapture.OnImageSavedCallback() {
        @Override
        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

            Log.d(TAG, "onImageSaved: File Saved: " + outputFileResults.getSavedUri());
            Log.d(TAG, "onImageSaved: that but string: " + outputFileResults.getSavedUri().toString());

//            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(outputFileResults.getSavedUri()));
//            Log.d(TAG, "onImageSaved: bitmap: " + bitmap.getHeight() + " "+bitmap.getWidth());
//
            //Shuttle user to the Make Post Screen
            Intent toMakePost = new Intent(GeoCamera.this, MakePostActivity.class);
            toMakePost.putExtra(MakePostActivity.KEY_PHOTO_URI, outputFileResults.getSavedUri().toString());
            startActivity(toMakePost);

            String[] files = GeoCamera.this.fileList();
            Log.d(TAG, "onImageSaved: files" + Arrays.toString(files));
        }

        @Override
        public void onError(@NonNull ImageCaptureException exception) {
            Log.d(TAG, "onError: Error in saving image. Try again" + exception);
        }
    };

    private void takePhoto() {

        /**
         * Save the photo to a local file.
         */
        File myFile2 = new File(this.getFilesDir(), "gCache_img");
        Log.d(TAG, "takePhoto: file saved to: " + myFile2.getAbsolutePath());
        String filename = "myfile2";
        String fileContents = "Hello world!";
        try (FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        outputFileOptions = new ImageCapture.OutputFileOptions.Builder(myFile2).build();





//        /**
//         * Save the photo to gallery.
//         */
//        contentValues = new ContentValues();
//        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "gCache_img_");
//        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
//
////        Save the photo to gallery.
//        outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
//                getContentResolver(),
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues).build();




        ProcessCameraProvider cameraProvider = null;
        try {
            cameraProvider = cameraProviderFuture.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        ImageCapture imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();
        Camera capCam = cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture);

        /**
         * Save image to file
         */
        imageCapture.takePicture(outputFileOptions,
                ContextCompat.getMainExecutor(this),
                imageSavedCallback);



    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, DESIRED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED) {
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
                cameraProvider = cameraProviderFuture.get();
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
        preview = new Preview.Builder().build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());


        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
        Log.d(TAG, "bindPreview: Binding Complete");

    }



    @SuppressLint("RestrictedApi")
    private void switchLens() {

        cameraProvider.unbindAll();


        preview = new Preview.Builder().build();

        if(cameraSelector.getLensFacing() == CameraSelector.LENS_FACING_FRONT) {
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();
        } else {
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build();
        }



        ImageCapture imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        Camera capCam = cameraProvider.bindToLifecycle(this, cameraSelector, preview);


    }


}