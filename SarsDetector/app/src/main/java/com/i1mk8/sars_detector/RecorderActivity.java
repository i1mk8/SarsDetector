package com.i1mk8.sars_detector;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class RecorderActivity extends AppCompatActivity {
    public boolean destroy = true;
    private RecorderThread recorderThread;

    public TextView recorderAnimationText;
    public Double[][] chartData;
    public boolean isCough;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_recorder);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.dark));

        Intent intent = getIntent();
        int state = intent.getIntExtra("state", 1);
        recorderAnimationText = findViewById(R.id.detectTitle);
        recorderThread = new RecorderThread(this);

        if (state == 1) {
            recorderThread.execute(100, 1);
        } else {
            chartData = (Double[][]) intent.getSerializableExtra("chartData");
            isCough = intent.getBooleanExtra("isCough", false);
            recorderThread.execute(200, 2);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (destroy) {
            if (recorderThread != null) {
                recorderThread.cancel(true);
            }
            Intent mainActivityIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(mainActivityIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (destroy) {
            Recorder.quit();
        }
    }
}
