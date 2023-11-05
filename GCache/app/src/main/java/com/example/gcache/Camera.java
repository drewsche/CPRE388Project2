package com.example.gcache;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class Camera extends AppCompatActivity {

    private static final String[] DESIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private ImageView imageView;

    private PreviewView previewView;
    private ImageButton shutterButton;
    ActivityCompat activityCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.preview_view);
        shutterButton = findViewById(R.id.buttonShutterTakePhoto);
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
     }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        androidx.camera.core.Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }
}