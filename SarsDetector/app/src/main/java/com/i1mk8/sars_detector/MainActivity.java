package com.i1mk8.sars_detector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;


public class MainActivity extends AppCompatActivity  {
    private boolean destroy = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) { 
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.dark));

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(view -> {
            Intent recorderInfoActivityIntent = new Intent(getBaseContext(), RecorderInfoActivity.class);
            recorderInfoActivityIntent.putExtra("state", 1);
            startActivity(recorderInfoActivityIntent);
            destroy = false;
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (destroy) {
            Recorder.quit();
        }
    }
}