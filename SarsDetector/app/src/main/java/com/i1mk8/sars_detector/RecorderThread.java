package com.i1mk8.sars_detector;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class RecorderThread extends AsyncTask<Integer, String, Double[][]> {
    private final WeakReference<RecorderActivity> activityWeakReference;
    private int state;
    private boolean isCough = false;

    private String title = "Измерение";
    private int titleCounter = 0;

    public RecorderThread(RecorderActivity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected Double[][] doInBackground(Integer... lengths) {
        RecorderActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return null;
        }

        state = lengths[1];
        Double[][] recordResult = new Double[lengths[0]][2];
        double counter = 0;

        speak(activity, R.raw.recorder_start);
        Recorder.start();
        Recorder.getAmplitude();


        if (state == 1) {
            for (int i = 0; i < lengths[0]; i++) {
                if (isCancelled()) {
                    return null;
                }
                counter += 0.1;
                double amplitude = Recorder.getAmplitude();

                recordResult[i][0] = counter;
                recordResult[i][1] = amplitude;
                if (amplitude > 5000) {
                    isCough = true;
                }

                titleAnimation();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (int i = 0; i < lengths[0]; i++, counter += 0.1) {
                if (isCancelled()) {
                    return null;
                }

                recordResult[i][0] = counter;
                recordResult[i][1] = Recorder.getAmplitude();

                titleAnimation();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        speak(activity.getBaseContext(), R.raw.beep);
        return recordResult;
    }

    @Override
    protected void onProgressUpdate(String... titles) {
        super.onProgressUpdate(titles);

        RecorderActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        activity.recorderAnimationText.setText(titles[0]);
    }

    @Override
    protected void onPostExecute(Double[][] result) {
        super.onPostExecute(result);

        RecorderActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        Intent intent;
        if (state == 1) {
            intent = new Intent(activity.getBaseContext(), RecorderInfoActivity.class);
            intent.putExtra("state", 2);
            intent.putExtra("isCough", isCough);
            intent.putExtra("chartData", result);
        } else {
            intent = new Intent(activity.getBaseContext(), ResultActivity.class);
            intent.putExtra("chartData", activity.chartData);
            if (activity.isCough) {
                intent.putExtra("result", 2);
            } else {
                RecorderParser recorderParser = new RecorderParser();
                intent.putExtra("result", recorderParser.parseRecorderResult(result));
            }
        }
        speak(activity, R.raw.recorder_finish);
        activity.startActivity(intent);
        activity.destroy = false;
        activity.finish();
    }

    private void speak(Context context, int id) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, id);
        mediaPlayer.start();

        while (mediaPlayer.isPlaying()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mediaPlayer.stop();
    }

    private void titleAnimation() {
        titleCounter++;
        if (titleCounter == 3 || titleCounter == 6 || titleCounter == 9) {
            title += ".";
            publishProgress(title);
        } else if (titleCounter == 12) {
            titleCounter = 0;
            title = "Измерение";
            publishProgress(title);
        }
    }
}
