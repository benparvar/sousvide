package com.benparvar.sousvide.business;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

/**
 * Created by alans on 06/04/2017.
 */

public class BluetoothBusiness extends BaseBusiness {
    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothBusiness(Context context) {
        super(context);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public String getAddress() {
       return mBluetoothAdapter.getAddress();
    }

    public Boolean hasAdapter () {
        return mBluetoothAdapter.getDefaultAdapter() != null ? Boolean.TRUE : Boolean.FALSE;
    }

    public void activate() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //mContext.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
