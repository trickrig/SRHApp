package de.srh.srha.communication;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

/**
 * Created by fettpet on 17.12.15.
 */
// http://stackoverflow.com/questions/18357641/is-it-possible-to-run-two-asynctask-in-same-time
public class AsyncTaskPool {
    public static <P, T extends AsyncTask<P, ?, ?>> void execute(T task) {
        execute(task, (P[]) null);
    }

    @SuppressLint("NewApi")
    public static <P, T extends AsyncTask<P, ?, ?>> void execute(T task, P... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }
}