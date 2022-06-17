package com.i1mk8.sars_detector;

import android.os.AsyncTask;
import android.text.Html;

import java.lang.ref.WeakReference;

public class TimerThread extends AsyncTask<Object, Integer, Object> {
    private final WeakReference<RecorderInfoActivity> activityWeakReference;

    public TimerThread(RecorderInfoActivity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        RecorderInfoActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return null;
        }

        for (int i = 5; i > 0; i--) {
            try {
                publishProgress(i);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... integers) {
        super.onProgressUpdate(integers);

        RecorderInfoActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        activity.timerTextView.setText(Html.fromHtml("До начала осталось <font color=#4FC1E9>" + integers[0] + "</font>"));
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);

        RecorderInfoActivity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        activity.startActivity(activity.recorderActivityIntent);
    }
}
