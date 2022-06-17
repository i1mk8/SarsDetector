package com.i1mk8.sars_detector;

import android.util.Log;

import java.util.ArrayList;

public class RecorderParser {
    // Обработка данных

    public int parseRecorderResult(Double[][] recorderResult) {
        /*
        0 - Не удалось определить дыхание
        1 - Здоров
        2 - Болен
        */

        /*
        Категории состояния:
        0 - пустота
        1 - пустота во вдохе
        2 - вдох
        */
        int count = 0;
        int state = 0;

        double minVolume = 0;
        for (Double[] item : recorderResult) {
            minVolume += item[1];
        }

        minVolume /= recorderResult.length;
        if (minVolume < 3000) {
            return 0;
        }

        ArrayList<Double[]> parsedRecordResult = new ArrayList<>();
        Double[] lastVolume = new Double[2];
        int emptyCounter = 0;

        for (Double[] item : recorderResult) {
            if (item[1] > minVolume) {
                if (state == 0) {
                    parsedRecordResult.add(item);
                    state = 2;
                } else if (state == 1) {
                    state = 2;
                    emptyCounter = 0;
                }
                lastVolume = item;
            }

            else if (item[1] <= minVolume) {
                if (state != 0) {
                    if (state == 2) {
                        state = 1;
                    }
                    emptyCounter++;

                    if (emptyCounter >= 10) {
                        parsedRecordResult.add(lastVolume);
                        emptyCounter = 0;
                        state = 0;
                    }
                }
            }
        }

        boolean state2 = true;
        for (int i = 0; i < parsedRecordResult.size() - 1; i++) {
            state2 = !state2;
            if (state2) {
                Log.d("RECORDER_PARSER", String.valueOf(parsedRecordResult.get(i + 1)[0] - parsedRecordResult.get(i)[0]));
                if (parsedRecordResult.get(i + 1)[0] - parsedRecordResult.get(i)[0] <= 1) {
                    Log.d("RECORDER_PARSER", "length");
                    return 2;
                }
                count++;
            }
        }

        if (count == 0) {
            Log.d("RECORDER_PARSER", "count 0");
            return 0;
        } else if (count <= 7) {
            Log.d("RECORDER_PARSER", "count > 7");
            return 1;
        } else {
            Log.d("RECORDER_PARSER", "normal");
            return 2;
        }
    }
}
