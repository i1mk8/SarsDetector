package com.i1mk8.sars_detector;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;

import java.io.File;
import java.io.IOException;

public class Recorder {
    private static MediaRecorder recorder;
    private static boolean isRunning = false;

    public static void setup(Context context) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                File outputFile = File.createTempFile("breathing", ".3gp", context.getCacheDir());
                recorder.setOutputFile(outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            recorder.setOutputFile("/dev/null");
        }

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double getAmplitude() {
        return recorder.getMaxAmplitude();
    }

    public static void start() {
        if (!isRunning) {
            recorder.start();
            isRunning = true;
        }
    }

    public static void quit() {
        if (recorder != null) {
            if (isRunning) {
                recorder.stop();
            }
            recorder.release();
        }
    }
}
