package com.benparvar.sousvide.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.benparvar.sousvide.R;
import com.benparvar.sousvide.model.InputTO;
import com.benparvar.sousvide.model.Pan;
import com.benparvar.sousvide.view.PanActivity;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.benparvar.sousvide.infrastructure.Contants.Bluetooth.END_LINE;
import static com.benparvar.sousvide.infrastructure.Contants.Bluetooth.LINE_FEED;
import static com.benparvar.sousvide.infrastructure.Contants.Bluetooth.REQUEST_ENABLE_BT;
import static com.benparvar.sousvide.infrastructure.Contants.ErrorCode.INVALID_TEMPERATURE;
import static com.benparvar.sousvide.infrastructure.Contants.ErrorCode.NO_BLUETOOTH_ADAPTER;
import static com.benparvar.sousvide.infrastructure.Contants.ErrorCode.NO_PAIRED_DEVICES;
import static com.benparvar.sousvide.infrastructure.Contants.PanCommand.HEADER;
import static com.benparvar.sousvide.infrastructure.Contants.PanCommand.SEPARATOR;
import static com.benparvar.sousvide.infrastructure.Contants.PanCommand.STATUS;
import static com.benparvar.sousvide.infrastructure.Contants.PanCommand.VERB;
import static com.benparvar.sousvide.infrastructure.Contants.PanErrorCode.INVALID_TEMPERATURE_TARGET;
import static com.benparvar.sousvide.infrastructure.Contants.PanStatus.STS_COOK_FINISHED;
import static com.benparvar.sousvide.infrastructure.Contants.PanStatus.STS_COOK_IN_PROGRESS;
import static com.benparvar.sousvide.infrastructure.Contants.PanStatus.STS_OFF;
import static com.benparvar.sousvide.infrastructure.Contants.PanStatus.STS_READY;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_COOK_FINISHED;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_COOK_IN_PROGRESS;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_CURRENT_TEMPERATURE;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_CURRENT_TIMER;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_OFF;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_ON;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_READY;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_TEMPERATURE;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_TEMPERATURE_TARGET;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_TIMER;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_TIMER_TARGET;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PAN_VERSION;
import static com.benparvar.sousvide.infrastructure.Contants.PanVerb.PID_VALUE;

/**
 * Created by alans on 19/05/17.
 */

public class PanPresenter extends BasePresenter {
    private final String TAG = "PanPresenter";
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
    private PanActivity mActivity;

    private ArrayAdapter mDevicesAdapter;
    private List<BluetoothDevice> mBluetoothDevices = new ArrayList<>();

    private Pan mPan;

    public PanPresenter(PanActivity activity) {
        super(activity);
        mActivity = activity;
        mPan = new Pan();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void onClickBtnPan(InputTO inputTO) {
        Log.d(TAG, inputTO.toString());

        if (mPan.getStatus().isEmpty()) {
            this.updatePairedDevices();

            if (null != inputTO.getAddress() && !inputTO.getAddress().isEmpty()) {
                try {
                    this.openBluetoothDeviceConnection(inputTO.getAddress());
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } else if (mPan.getStatus().equals(STS_OFF)) {
            this.setTargetTemperature(inputTO.getTargetTemperature());
            this.setTargetTimer(inputTO.getTargetTimer());
        } else if (mPan.getStatus().equals(STS_READY)) {
            this.cookOn();
        } else if (mPan.getStatus().equals(STS_COOK_IN_PROGRESS)) {
            this.cookOff();
        } else if (mPan.getStatus().equals(STS_COOK_FINISHED)) {
            this.cookOff();
        }
    }

    public void configureBluetooh() {
        if (!this.hasBluetoohAdapter()) {
            showError(NO_BLUETOOTH_ADAPTER);
        } else {
            this.activateBluetooth();
        }
    }

    public Long minuteToSecond(Long sc) {
        return sc * 60;
    }

    public String secondToStringHour(Long ms) {
        String value = "00:00";
        try {
            value = String.format("%02d:%02d", ms / 3600, (ms / 60) % 60);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return value;
    }

    public String doubleToTemperature(Double tm) {
        String value = "00.00";
        try {
            value = String.format("%02.2f", tm).replace(",", ".");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return value;
    }

    public Long stringHourToSecond(String hour) {
        String[] hourSlited = hour.split(":");
        Long result = Long.valueOf(hourSlited[0]) * 60 * 60;
        result += Long.valueOf(hourSlited[1]) * 60;
//        Long result = 0L;
//        result += 0L;

        return result;
    }

    public void configureSpinnerDevices() {
        mDevicesAdapter = new ArrayAdapter(mActivity, R.layout.support_simple_spinner_dropdown_item,
                this.mBluetoothDevices);
        mDevicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mActivity.setSpnDeviceAdapter(mDevicesAdapter);
    }

    public void updatePairedDevices() {
        this.mBluetoothDevices.clear();
        List<BluetoothDevice> devices = this.getPairedBluetoothDevices();

        if (CollectionUtils.isEmpty(devices)) {
            mActivity.updateStatus(getString(R.string.panDisconnected), Boolean.TRUE);
        } else {
            mActivity.updateStatus(getString(R.string.panConnected), Boolean.TRUE);
        }

        this.mBluetoothDevices.addAll(devices);
        mDevicesAdapter.notifyDataSetChanged();
    }

    private void onReceiveData(String data) {
        Log.d(TAG, data);
        Boolean error = Boolean.FALSE;
        String string = "";
        int index = 0;
        List<String> preParsedString = Arrays.asList(data.replace(END_LINE, "")
                .replace(LINE_FEED, "")
                .split(SEPARATOR));
        Log.d(TAG, preParsedString.toString());

        // PAN
        string = preParsedString.get(index++);
        if (string.equals(HEADER)) {
            // S
            string = preParsedString.get(index++);
            if (string.equals(STATUS)) {
                // 000
                string = preParsedString.get(index++);
                switch (string) {
                    case PAN_OFF:
                        //PAN OFF -> "PAN:S:000"
                        mPan.setStatus(STS_OFF);
                        mActivity.updateStatus(getString(R.string.panOff), Boolean.TRUE);
                        break;
                    case PAN_ON:
                        //PAN ON -> "PAN:S:001"
                        mPan.setStatus(STS_READY);
                        mActivity.updateStatus(getString(R.string.panOn), Boolean.TRUE);
                        break;
                    case PAN_TIMER:
                        // Nothing...
                        break;
                    case PAN_TEMPERATURE:
                        // Nothing...
                        break;
                    case PAN_TIMER_TARGET:
                        //PAN TIMER -> "PAN:S:004:00000"
                        mActivity.setTargetTimer(this.strToTimer(preParsedString.get(index++)));
                        break;
                    case PAN_TEMPERATURE_TARGET:
                        //PAN TEMPERATURE -> "PAN:S:005:00000"
                        mActivity.setTargetTemperature(this.strToTemperature(preParsedString.get(index++)));
                        break;
                    case PAN_CURRENT_TIMER:
                        //PAN CURRENT TIMER -> "PAN:S:006:0000:0000"
                        mPan.setTimer(this.strToTimer(preParsedString.get(index++)));
                        mActivity.setTargetTimer(this.strToTimer(preParsedString.get(index++)));
                        break;
                    case PAN_CURRENT_TEMPERATURE:
                        //PAN CURRENT TEMPERATURE -> "PAN:S:007:0000:0000"
                        mPan.setTemperature(this.strToTemperature(preParsedString.get(index++)));
                        mActivity.setTargetTemperature(this.strToTemperature(preParsedString.get(index++)));
                        break;
                    case PAN_READY:
                        //PAN READY -> "PAN:S:008"
                        mPan.setStatus(STS_READY);
                        mActivity.updateStatus(getString(R.string.panReady), Boolean.TRUE);
                        break;
                    case PAN_COOK_IN_PROGRESS:
                        //PAN COOKI IN PROGRESS -> "PAN:S:009"
                        mPan.setStatus(STS_COOK_IN_PROGRESS);
                        mActivity.updateStatus(getString(R.string.panCooking), Boolean.TRUE);
                        break;
                    case PAN_COOK_FINISHED:
                        //PAN COOKI FINISHED -> "PAN:S:010"
                        mPan.setStatus(STS_COOK_FINISHED);
                        mActivity.updateStatus(getString(R.string.panCooked), Boolean.TRUE);
                        break;
                    case PID_VALUE:
                        //PAN PID -> "PAN:S:011:00000"
                        Log.v(TAG, "PID value: " + preParsedString.get(index++));
                        break;
                    case PAN_VERSION:
                        //PAN FIRMWARE VERSION -> "PAN:S:012:00000000"
                        Log.v(TAG, "Firmware version: " + preParsedString.get(index++));
                        break;
                    case INVALID_TEMPERATURE_TARGET:
                        showError(INVALID_TEMPERATURE);
                        break;
                }
            } else {
                error = Boolean.TRUE;
            }
        } else {
            error = Boolean.TRUE;
        }

        // Error
        if (Boolean.TRUE.equals(error)) {
            Log.e(TAG, "Unable to parse: " + string);
        }

        // Update UI
        Log.d(TAG, mPan.toString());

        mActivity.setCurrentTemperature(mPan.getTemperature());
        mActivity.setCurrentTimer(mPan.getTimer());
    }

    private Double strToTemperature(String value) {
        Double result = 0.0;
        try {
            result = Double.valueOf(value) / 100;
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    private Long strToTimer(String value) {
        Long result = 0L;
        try {
            result = Long.valueOf(value);
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    /**
     * Heater temperature in degrees Celsius (minimum is 30.00 -> 3000 and maximum is 60.00 -> 6000)
     *
     * @param targetTemperature
     */
    public void setTargetTemperature(Double targetTemperature) {
        // PAN:V:003:4000 3000 -> 4000
        String data = HEADER + SEPARATOR + VERB + SEPARATOR + PAN_TEMPERATURE + SEPARATOR + targetTemperature.
                toString().replace(".", "0");
        this.sendToBluetoothDevice(data);
    }

    /**
     * Heater timer target in minutes (minimum is 0.50 -> 050 and maximum is 1440.00 -> 144000)
     *
     * @param targetTimer
     */
    public void setTargetTimer(Long targetTimer) {
        // PAN:V:002:600
        String data = HEADER + SEPARATOR + VERB + SEPARATOR + PAN_TIMER + SEPARATOR + targetTimer.toString().
                replace(".", "");
        this.sendToBluetoothDevice(data);
    }

    public void cookOn() {
        // PAN:V:001
        String data = HEADER + SEPARATOR + VERB + SEPARATOR + PAN_ON;
        this.sendToBluetoothDevice(data);
    }

    public void cookOff() {
        // PAN:V:000
        String data = HEADER + SEPARATOR + VERB + SEPARATOR + PAN_OFF;
        this.sendToBluetoothDevice(data);
    }

    // INI BLUETOOTH

    private void activateBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private String getBluetoohAddress() {
        return mBluetoothAdapter.getAddress();
    }

    private Boolean hasBluetoohAdapter() {
        return mBluetoothAdapter.getDefaultAdapter() != null ? Boolean.TRUE : Boolean.FALSE;
    }

    private List<BluetoothDevice> getPairedBluetoothDevices() {
        List<BluetoothDevice> result = new ArrayList<>();

        try {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
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
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public BluetoothDevice getBluetoothDeviceByAddress(String address) {
        List<BluetoothDevice> pairedDevices = getPairedBluetoothDevices();
        BluetoothDevice result = null;

        for (BluetoothDevice device : pairedDevices) {
            if (device.getAddress().equals(address)) {
                result = device;
            }
        }

        return result;
    }

    public void openBluetoothDeviceConnection(String address)
            throws IOException {

        // I changed the device
        if (!address.equals(mDeviceAddress)) {
            isListening = Boolean.FALSE;
            mDeviceAddress = address;
        }

        BluetoothDevice aDevice = getBluetoothDeviceByAddress(address);
        if (isListening.equals(Boolean.FALSE)) {
            if (null == mSocket) {
                mSocket = aDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUID_SERIAL_PORT_PROFILE));
                mSocket.connect();
            }

            mmOutputStream = mSocket.getOutputStream();
            mmInputStream = mSocket.getInputStream();

            initializeBluetoothListener();
            isListening = Boolean.TRUE;
        }

    }

    public void closeBluetoothDeviceConnection() {
        stopWorker = true;
        try {
            mmOutputStream.close();
            mmInputStream.close();
            mSocket.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void initializeBluetoothListener() {
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
                                    final String data = new String(encodedBytes, "US-ASCII").replace("�", ""); //
                                    // Removing the character �
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            //Log.d(TAG, data);
                                            onReceiveData(data);
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

    public void sendToBluetoothDevice(String data) {
        Log.d(TAG, data);
        data.concat(END_LINE);
        try {
            mmOutputStream.write(data.getBytes());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    // END BLUETOOTH
}
