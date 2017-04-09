package com.benparvar.sousvide.manager;

import android.content.Context;
import android.util.Log;

/**
 * Created by alans on 09/04/2017.
 */
public abstract class BaseManager {
    private final String TAG = "BaseManager";
    protected Context mContext;

    private BaseManager() {
    }

    public BaseManager(Context mContext) {
        this.mContext = mContext;
    }

    public void showToast(String message) {
        Log.d(TAG, message);
    }
}
