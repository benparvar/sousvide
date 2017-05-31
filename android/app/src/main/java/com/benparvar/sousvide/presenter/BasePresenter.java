package com.benparvar.sousvide.presenter;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.benparvar.sousvide.R;

import static com.benparvar.sousvide.infrastructure.Contants.ErrorCode.INVALID_TEMPERATURE;
import static com.benparvar.sousvide.infrastructure.Contants.ErrorCode.NO_BLUETOOTH_ADAPTER;
import static com.benparvar.sousvide.infrastructure.Contants.ErrorCode.NO_PAIRED_DEVICES;
import static com.benparvar.sousvide.infrastructure.Contants.ErrorCode.OFF;
import static com.benparvar.sousvide.infrastructure.Contants.ErrorCode.ON;
import static com.benparvar.sousvide.infrastructure.Contants.ErrorCode.READY;

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
            case INVALID_TEMPERATURE: message = mActivity.getString(R.string.invalid_temperature);
                break;
            case OFF:  message = mActivity.getString(R.string.pan_off);
                break;
            case ON:  message = mActivity.getString(R.string.pan_on);
                break;
            case READY:  message = mActivity.getString(R.string.pan_ready);
                break;
        }

        Log.d(TAG, message);
        Toast.makeText(this.mActivity, message, Toast.LENGTH_LONG);
    }

    protected String getString(int resId) {
        return mActivity.getString(resId);
    }
}
