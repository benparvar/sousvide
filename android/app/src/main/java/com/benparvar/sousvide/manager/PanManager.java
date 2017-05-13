package com.benparvar.sousvide.manager;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.benparvar.sousvide.R;
import com.benparvar.sousvide.business.BluetoothBusiness;
import com.benparvar.sousvide.business.PanBusiness;
import com.benparvar.sousvide.entity.Pan;
import com.benparvar.sousvide.entity.Status;
import com.benparvar.sousvide.infrastructure.Constants;
import com.benparvar.sousvide.infrastructure.OperationListener;
import com.benparvar.sousvide.infrastructure.OperationResult;

import java.util.List;
import java.util.Set;

import static com.benparvar.sousvide.R.string.no_paired_devices;
import static com.benparvar.sousvide.infrastructure.Constants.ErrorCode.NO_ERROR;

/**
 * Created by alans on 06/04/2017.
 */

public class PanManager extends BaseManager {
    private final String TAG = "PanManager";
    private BluetoothBusiness mBluetoothBusiness;
    private PanBusiness mPanBusiness;
    private Pan mPam;

    public PanManager(Context context) {
        super(context);
        // Business
        this.mBluetoothBusiness = new BluetoothBusiness(context);
        this.mPanBusiness = new PanBusiness(context);
        // Others
        this.mPam = this.mPanBusiness.getPanInstance();
    }

    public Pan getPanInstance() {
        return this.mPam;
    }

    public Pan getNewPanInstance() {
        return mPanBusiness.getNewPanInstance();
    }

    public void activateBluetooth() {
        mBluetoothBusiness.activate();
    }

    public void hasBluetoohAdapter() {
        if (mBluetoothBusiness.hasAdapter() == Boolean.FALSE) {
            showToast(mContext.getString(R.string.has_no_bluetooth_adapter));
        }
    }

    public List<BluetoothDevice> getPairedDevices() {
        return mBluetoothBusiness.getPairedDevices();
    }

    public void onClick(final Pan pan, final OperationListener<Pan> listener) {
        AsyncTask<Void, Integer, OperationResult<Pan>> task = new AsyncTask<Void, Integer, OperationResult<Pan>>() {
            @Override
            protected OperationResult<Pan> doInBackground(Void... params) {
                return mPanBusiness.onClick(pan);
            }

            @Override
            protected void onPostExecute(OperationResult<Pan> operationResult) {
                removeFromTaskList(this);
                if (listener != null) {
                    int error = operationResult.getError();
                    if (error != NO_ERROR) {
                        showToast(error);
                        listener.onError(error);
                    } else {
                        listener.onSuccess(operationResult.getResult());
                    }
                }
            }

            @Override
            protected void onCancelled() {
                removeFromTaskList(this);
                if (listener != null) {
                    listener.onCancel();
                }
            }
        };

        // Task execution
        addToTaskList(task);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
