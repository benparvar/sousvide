package com.benparvar.sousvide.business;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.benparvar.sousvide.ui.pan.PanActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.benparvar.sousvide.infrastructure.Constants.Bluetooth.REQUEST_ENABLE_BT;
import static com.benparvar.sousvide.infrastructure.Constants.ErrorCode.NO_PAIRED_DEVICES;

/**
 * Created by alans on 06/04/2017.
 */

public class BluetoothBusiness extends BaseBusiness {
    private final String TAG = "BluetoothBusiness";
    private BluetoothAdapter mBluetoothAdapter;
    private static final String UUID_SERIAL_PORT_PROFILE = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothSocket mSocket = null;
    private BufferedReader mBufferedReader = null;

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

    public List<BluetoothDevice> getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        List<BluetoothDevice> result = new ArrayList<>();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                result.add(device);
                Log.d(TAG, device.getName().concat(" - ").concat(device.getAddress()));
                Log.d(TAG, device.toString());
            }
        } else {
            showError(NO_PAIRED_DEVICES);
        }

        return result;
    }

    public BluetoothDevice getDeviceByAddress(String address) {
        List<BluetoothDevice> pairedDevices = getPairedDevices();
        BluetoothDevice result = null;

        for (BluetoothDevice device : pairedDevices) {
            if (device.getAddress().equals(address)) {
                result = device;
            }
        }

        return result;
    }

    public Boolean openDeviceConnection(String address)
            throws IOException {
        BluetoothDevice aDevice = getDeviceByAddress(address);

        if (null == mSocket) {
            mSocket = aDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUID_SERIAL_PORT_PROFILE));
        }
//        if (null == mSocket) {
//            mSocket = aDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUID_SERIAL_PORT_PROFILE));
//        }

        if (!mSocket.isConnected()) {
            mSocket.connect();
        }

        return mSocket.isConnected();
    }

    public String readFromDevice() throws IOException {
        InputStream aStream = null;
        InputStreamReader aReader = null;
        String panStatus = null;
        try {
            aStream = mSocket.getInputStream();
            aReader = new InputStreamReader(aStream);
            mBufferedReader = new BufferedReader(aReader);

            Log.d(TAG, "INI");
//            while ((panStatus = mBufferedReader.readLine()) != null) {
//                // TODO Modify the Pan Status
//                Log.d(TAG, panStatus);
//            }
            panStatus = mBufferedReader.readLine();
            Log.d(TAG, panStatus);
            Log.d(TAG, "END");
        } catch (IOException e) {
            Log.e(TAG, "Could not connect to device", e);
            throw e;
        } finally {
            mSocket.close();
        }

        return panStatus;
    }
}
