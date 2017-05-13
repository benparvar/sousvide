package com.benparvar.sousvide.business;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import com.benparvar.sousvide.entity.Pan;
import com.benparvar.sousvide.entity.Temperature;
import com.benparvar.sousvide.entity.Timer;
import com.benparvar.sousvide.infrastructure.OperationResult;

import java.io.IOException;

import static com.benparvar.sousvide.R.string.no_paired_devices;
import static com.benparvar.sousvide.infrastructure.Constants.ErrorCode.NO_PAIRED_DEVICES;
import static com.benparvar.sousvide.infrastructure.Constants.ErrorCode.UNKNOWN;
import static com.benparvar.sousvide.infrastructure.Constants.ErrorCode.YES_PAIRED_DEVICES;

/**
 * Created by alans on 06/04/2017.
 */

public class PanBusiness extends BaseBusiness {
    private final String TAG = "PanBusiness";
    private Pan mPam;
    private BluetoothBusiness mBluetoothBusiness;

    public PanBusiness(Context context) {
        super(context);
        this.mBluetoothBusiness = new BluetoothBusiness(context);
        this.mPam = new Pan();
    }

    public Pan getPanInstance() {
        return this.mPam;
    }

    public Pan getNewPanInstance() {
        this.mPam = new Pan();
        return this.mPam;
    }

    public Pan cook(Double temperature, Long timer) {
        // Get Address

        // Set Temperature

        // Set Timer

        // Cook on

//        PAN:V:000
//        PAN:V:003:4000
//        PAN:V:002:600
//        PAN:V:001

        return this.mPam;
    }

    public Pan readStatus() {
        return this.mPam;
    }

    private Pan setTemperature() {
        return this.mPam;
    }

    private Pan setTimer() {
        return this.mPam;
    }


    public OperationResult<Pan> onClick(Pan pan) {
        int error = UNKNOWN;
        OperationResult<Pan> result = new OperationResult<>();

        // I have a paired device
        if (null == mPam.getAddress() || mPam.getAddress().isEmpty()) {
            error = NO_PAIRED_DEVICES;
        } else {
            error = YES_PAIRED_DEVICES;
        }
        Log.d(TAG, "onClick error: " + error);
        result.setError(error);

        // I Have Paired devices
        if (error == YES_PAIRED_DEVICES) {
            try {
                if (mBluetoothBusiness.openDeviceConnection(mPam.getAddress())) {
                    //  Read
                    String panStatus = mBluetoothBusiness.readFromDevice();
                    Log.d(TAG, panStatus);
                    // TODO - Parse PanStatus



                    // Write
                    // TODO - Write on Pan
                }
            } catch (IOException e) {
               Log.e(TAG, e.getMessage());
            }
        }

        Log.d(TAG, "onClick return: " + result);
        return result;
    }

}
