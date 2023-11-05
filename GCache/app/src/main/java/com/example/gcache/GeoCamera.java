package com.example.gcache;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;


import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


public class GeoCamera extends AppCompatActivity {

    private static final String[] DESIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private Queue imageQ;

    private ImageView imageView;

    private PreviewView previewView;
    private TextView MLKitOverlay;

    private Executor executor; //TODO make sure this is what I am supposed to do.
    //I think it would be something. Check with
    // the Threading lab


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.preview_view);

        /**
         * The TextView holding the data from ML
         */
        /**
         * Check for Permissions
         */
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        checkPermissions();



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




}