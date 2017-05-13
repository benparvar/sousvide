package com.benparvar.sousvide.business;

import android.content.Context;
import android.util.Log;

import com.benparvar.sousvide.R;

import static com.benparvar.sousvide.infrastructure.Constants.ErrorCode.NO_PAIRED_DEVICES;

/**
 * Created by alans on 07/04/2017.
 */

public abstract class BaseBusiness {
    private final String TAG = "BaseBusiness";
    protected Context mContext;

    private BaseBusiness() {
    }

    public BaseBusiness(Context mContext) {
        this.mContext = mContext;
    }

    protected void showError(int code) {
        String message = "";
        switch (code) {
            case NO_PAIRED_DEVICES: message = mContext.getText(R.string.no_paired_devices).toString();
                break;
        }

        Log.d(TAG, message);
    }
}
