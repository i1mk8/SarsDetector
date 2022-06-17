package com.i1mk8.sars_detector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SplashScreenActivity extends AppCompatActivity {

    private boolean destroy = true;
    private final int REQUEST_CODE = 1;
    private final String [] PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private void verifyPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE);
        Toast.makeText(this, "Предоставьте разрешения!", Toast.LENGTH_LONG).show();
    }

    private void payload() {
        Recorder.setup(getBaseContext());

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
        destroy = false;
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(SplashScreenActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            payload();
        } else {
            verifyPermissions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (destroy) {
            Recorder.quit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                payload();
            } else {
                verifyPermissions();
            }
        }
    }
}
