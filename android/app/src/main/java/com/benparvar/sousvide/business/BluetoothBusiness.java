package com.benparvar.sousvide.business;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.benparvar.sousvide.ui.pan.PanActivity;

import java.util.Set;

import static com.benparvar.sousvide.infrastructure.Constants.Bluetooth.REQUEST_ENABLE_BT;

/**
 * Created by alans on 06/04/2017.
 */

public class BluetoothBusiness extends BaseBusiness {
    private final String TAG = "BluetoothBusiness";
    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothBusiness(Context context) {
        super(context);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public String getAddress() {
        return mBluetoothAdapter.getAddress();
    }

    public Boolean hasAdapter() {
        return mBluetoothAdapter.getDefaultAdapter() != null ? Boolean.TRUE : Boolean.FALSE;
    }

    public void activate() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            if (mContext instanceof Activity) {
                ((Activity) mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Log.e(TAG, "mContext should be an instanceof Activity.");
            }

        }
    }

    public Set<BluetoothDevice> getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.d(TAG, deviceHardwareAddress);
            }
        }

        return pairedDevices;
    }
}
