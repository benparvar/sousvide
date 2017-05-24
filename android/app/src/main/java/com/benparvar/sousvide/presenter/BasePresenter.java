package com.benparvar.sousvide.presenter;

import android.app.Activity;
import android.util.Log;

import com.benparvar.sousvide.R;

import static com.benparvar.sousvide.infrastructure.Contants.ErrorCode.NO_BLUETOOTH_ADAPTER;
import static com.benparvar.sousvide.infrastructure.Contants.ErrorCode.NO_PAIRED_DEVICES;

/**
 * Created by alans on 19/05/17.
 */

public abstract class BasePresenter {
    private final String TAG = "BasePresenter";
    protected Activity mActivity;

    private BasePresenter() {
    }

    public BasePresenter(Activity activity) {
        this.mActivity = activity;
    }

    protected void showError(int code) {
        String message = "";
        switch (code) {
            case NO_PAIRED_DEVICES: message = mActivity.getString(R.string.no_paired_devices);
                break;
            case NO_BLUETOOTH_ADAPTER: message = mActivity.getString(R.string.no_bluetooth_adapter);
                break;
        }

        Log.d(TAG, message);
    }
}
