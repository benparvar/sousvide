package com.benparvar.sousvide.business;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.benparvar.sousvide.infrastructure.Constants;
import com.benparvar.sousvide.ui.pan.PanActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.benparvar.sousvide.infrastructure.Constants.Bluetooth.END_LINE;
import static com.benparvar.sousvide.infrastructure.Constants.Bluetooth.REQUEST_ENABLE_BT;
import static com.benparvar.sousvide.infrastructure.Constants.ErrorCode.NO_PAIRED_DEVICES;

/**
 * Created by alans on 06/04/2017.
 */

public class BluetoothBusiness extends BaseBusiness {
    private final String TAG = "BluetoothBusiness";
    private BluetoothAdapter mBluetoothAdapter;
    private static final String UUID_SERIAL_PORT_PROFILE = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothSocket mSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private volatile boolean stopWorker;
    private byte[] readBuffer;
    private int readBufferPosition;
    private Thread workerThread;
    private Boolean isListening = Boolean.FALSE;
    private String mDeviceAddress;

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

    public void openDeviceConnection(String address)
            throws IOException {

        // I changed the device
        if (!address.equals(mDeviceAddress)) {
            isListening = Boolean.FALSE;
            mDeviceAddress = address;
        }

        BluetoothDevice aDevice = getDeviceByAddress(address);
        if (isListening.equals(Boolean.FALSE)) {
            if (null == mSocket) {
                mSocket = aDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUID_SERIAL_PORT_PROFILE));
                mSocket.connect();
            }

            mmOutputStream = mSocket.getOutputStream();
            mmInputStream = mSocket.getInputStream();

            initializeListener();
            isListening = Boolean.TRUE;
        }

    }

    public void closeDeviceConnection() {
        stopWorker = true;
        try {
            mmOutputStream.close();
            mmInputStream.close();
            mSocket.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void initializeListener() {
        final Handler handler = new Handler(Looper.getMainLooper());
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII").replace("�", ""); // Removing the character �
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            Log.d(TAG, data);
                                            //myLabel.setText(data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    public String readFromDevice() throws IOException {
        String data = null;
        // TODO

        return data;
    }


    public void sendToDevice(String data) {
        data.concat(END_LINE);
        try {
            mmOutputStream.write(data.getBytes());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
