package com.example.gcache;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button buttonCameraActivity;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonCameraActivity = (Button)findViewById(R.id.buttonCameraActivity);

    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonCameraActivity) {
            Log.d(TAG, "onClick: called");
            Intent intent = new Intent(this, Camera.class);
            startActivity(intent);
            finish();
        }
    }
}