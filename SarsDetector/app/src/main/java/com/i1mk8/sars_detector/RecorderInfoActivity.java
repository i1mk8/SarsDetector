package com.i1mk8.sars_detector;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class RecorderInfoActivity extends AppCompatActivity {
    public Intent recorderActivityIntent;
    public TextView timerTextView;

    private boolean destroy = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_recorder_info);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.dark));

        final TextView titleTextView = findViewById(R.id.titleRecorderInfo);
        final TextView howToTextView = findViewById(R.id.howToTextRecorderInfo);
        final ImageView howToImageView = findViewById(R.id.howToIconRecorderInfo);
        final TextView tipTextView = findViewById(R.id.tipRecorderInfo);
        timerTextView = findViewById(R.id.timerRecorderInfo);
        final Button startTestButton = findViewById(R.id.startButtonRecorderInfo);

        Intent intent = getIntent();
        int state = intent.getIntExtra("state", 1);

        if (state == 1) {
            titleTextView.setText("Измерение 1");
            howToTextView.setText("Приложите телефон к груди и глубоко дышите, пока не услышите звуковой сигнал об окончании записи");
            howToImageView.setImageResource(R.drawable.detect_how_to_1);
            tipTextView.setText(Html.fromHtml("Пожалуйста , находитесь в тишине.<br>Тест займет примерно <font color=#4FC1E9>10</font> секунд"));
            startTestButton.setText("Начать тест 1");
        } else {
            titleTextView.setText("Измерение 2");
            howToTextView.setText("Держите телефон на расстоянии примерно 5 см от лица, микрофон должен быть направлен ко рту. По окончании записи вы услышите звуковой сигнал");
            howToImageView.setImageResource(R.drawable.detect_how_to_2);
            tipTextView.setText(Html.fromHtml("Пожалуйста, устраните посторонние звуки.<br>Тест займет примерно <font color=#4FC1E9>20</font> секунд"));
            startTestButton.setText("Начать тест 2");
        }

        startTestButton.setOnClickListener(view -> {
            destroy = false;
            recorderActivityIntent = new Intent(getBaseContext(), RecorderActivity.class);
            TimerThread timerThread = new TimerThread(RecorderInfoActivity.this);

            if (state == 1) {
                recorderActivityIntent.putExtra("state", 1);
            } else {
                Double[][] chartData = (Double[][]) intent.getSerializableExtra("chartData");
                boolean isCough = intent.getBooleanExtra("isCough", false);

                recorderActivityIntent.putExtra("chartData", chartData);
                recorderActivityIntent.putExtra("isCough", isCough);
                recorderActivityIntent.putExtra("state", 2);
            }

            timerThread.execute(timerTextView);
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
