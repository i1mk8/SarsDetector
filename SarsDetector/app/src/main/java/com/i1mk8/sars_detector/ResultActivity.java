package com.i1mk8.sars_detector;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResultActivity extends AppCompatActivity {
    private final int SIGN_IN_RC = 100;
    private boolean destroy = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_result);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.dark));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = getIntent();
        int result = intent.getIntExtra("result", 0);

        TextView resultTextView = findViewById(R.id.resultText);
        TextView resultTipView = findViewById(R.id.resultTip);
        RelativeLayout resultTextBackground = findViewById(R.id.resultBackground);

        String resultText;
        String resultTip;
        int color;

        Button sendEmailButton = findViewById(R.id.sendEmailButton);

        if (result == 0) {
            resultText = "Программа не смогла распознать Ваше дыхание";
            resultTip = "Программа не смогла распознать Ваше дыхание. Возможно, Вы дышали не в микрофон.";
            sendEmailButton.setVisibility(View.INVISIBLE);
            color = R.color.yellow;
        } else if (result == 1) {
            resultText = "Возможно, у вас нет признаков ОРВИ";
            resultTip = "Программа посчитала, что вы здоровы. ЭТОТ ВЕРДИКТ НЕ ЯВЛЯЕТСЯ ТОЧНЫМ ДИАГНОЗОМ.";
            color = R.color.green;
        } else {
            resultText = "Возможно, у вас есть признаки ОРВИ";
            resultTip = "Программа посчитала, что вы больны. ЭТОТ ВЕРДИКТ НЕ ЯВЛЯЕТСЯ ТОЧНЫМ ДИАГНОЗОМ.";
            color = R.color.red;
        }
        resultTextView.setText(resultText);
        resultTipView.setText(resultTip);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resultTextBackground.setBackgroundTintList(getColorStateList(color));
        }

        sendEmailButton.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, SIGN_IN_RC);
        });

        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(view -> {
            Intent recorderActivityIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(recorderActivityIntent);
            destroy = false;
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_RC) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Double[][] chartData = (Double[][]) getIntent().getSerializableExtra("chartData");
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/html");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Результаты сканирования " + account.getGivenName() + " " + account.getFamilyName());

                File appDirectory = new File(Environment.getExternalStorageDirectory(), "Documents");
                File htmlFile = new File(appDirectory, "breathing.html");
                if (!htmlFile.exists()) {
                    try {
                        htmlFile.createNewFile();
                    } catch (IOException e) {
                        Toast.makeText(this, "Ошибка сохранения графика!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        return;
                    }
                }
                try {
                    FileOutputStream writer = new FileOutputStream(htmlFile);
                    writer.write(EmailTemplate.getTemplate(chartData).getBytes(StandardCharsets.UTF_8));
                    writer.close();
                } catch (IOException e) {
                    Toast.makeText(this, "Ошибка сохранения графика!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                }

                Uri htmlUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", htmlFile);
                emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                emailIntent.putExtra(Intent.EXTRA_STREAM, htmlUri);
                startActivity(Intent.createChooser(emailIntent, "Выберите приложение:"));

                } catch (ApiException e) {
                    Toast.makeText(this, "Ошибка входа в аккаунт!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (destroy) {
            Recorder.quit();
        }
    }
}
