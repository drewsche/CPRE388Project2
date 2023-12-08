package com.example.gcache;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class MakePostActivity extends AppCompatActivity {

    private static final String TAG = "MakePostActivity";
    ImageView imageCapture;
    TextView filePathTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post);

        imageCapture = (ImageView) findViewById(R.id.imageCaptureView);
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
}