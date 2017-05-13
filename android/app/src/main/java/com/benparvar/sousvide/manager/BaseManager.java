package com.benparvar.sousvide.manager;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.benparvar.sousvide.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by alans on 09/04/2017.
 */
public abstract class BaseManager {
    private final String TAG = "BaseManager";
    protected final Context mContext;
    protected final List<AsyncTask<?, ?, ?>> mTaskList;
    private Toast mToast;

    protected BaseManager(Context mContext) {
        super();
        this.mContext = mContext;
        mTaskList = new ArrayList<>();
    }

    public void cancelOperations() {
        for (AsyncTask<?, ?, ?> task : mTaskList) {
            task.cancel(false);
        }
    }

    protected void addToTaskList(AsyncTask<?, ?, ?> task) {
        mTaskList.add(task);
    }

    protected void removeFromTaskList(AsyncTask<?, ?, ?> task) {
        mTaskList.remove(task);
    }

    public void showToast(String message) {
        Log.d(TAG, message);

        try {
            mToast.cancel();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showToast(int error) {
        String message = "";
        switch (error) {
            case -1 :
                message = mContext.getString(R.string.no_error);
                break;
            case 0 :
                message = mContext.getString(R.string.unknown_error);
                break;
            case 1 :
                message = mContext.getString(R.string.no_paired_devices);
                break;
            case 2 :
                message = mContext.getString(R.string.yes_paired_devices);
                break;

            default:
                message = "";
                break;
        }

        if (!message.isEmpty())
            showToast(message);
    }
}
