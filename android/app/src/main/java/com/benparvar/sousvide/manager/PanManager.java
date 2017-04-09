package com.benparvar.sousvide.manager;

import android.content.Context;

import com.benparvar.sousvide.R;
import com.benparvar.sousvide.business.BluetoothBusiness;

/**
 * Created by alans on 06/04/2017.
 */

public class PanManager extends BaseManager {
    private BluetoothBusiness mBluetoothBusiness;

    public PanManager(Context context) {
        super(context);
        this.mBluetoothBusiness = new BluetoothBusiness(context);
    }

    public void activateBluetooth() {
        mBluetoothBusiness.activate();
    }

    public void hasBluetoohAdapter() {
        if (mBluetoothBusiness.hasAdapter() == Boolean.FALSE) {
            showToast(mContext.getString(R.string.has_no_bluetooth_adapter));
        }
    }
}
