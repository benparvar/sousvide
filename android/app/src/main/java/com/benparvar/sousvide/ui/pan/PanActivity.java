package com.benparvar.sousvide.ui.pan;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.benparvar.sousvide.R;
import com.benparvar.sousvide.entity.Pan;
import com.benparvar.sousvide.infrastructure.OperationListener;
import com.benparvar.sousvide.manager.PanManager;

import java.util.ArrayList;
import java.util.List;

public class PanActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final String TAG = "PanActivity";
    private AppCompatSeekBar skbTemperature;
    private AppCompatSeekBar skbTimer;
    private AppCompatImageButton btnPan;
    private Spinner spnDevice;
    private PanManager mPanManager;
    private Pan mPan;
    private ProgressBar mProgressBar;

    ArrayAdapter mDevicesAdapter;
    List<BluetoothDevice> mBluetoothDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan);


        mPanManager = new PanManager(PanActivity.this);
        mPanManager.hasBluetoohAdapter();
        mPanManager.activateBluetooth();

        // We need a pan instance
        mPan = mPanManager.getPanInstance();

        // Bind
        skbTemperature = (AppCompatSeekBar) findViewById(R.id.temperature_skb);
        skbTimer = (AppCompatSeekBar) findViewById(R.id.timer_skb);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        btnPan = (AppCompatImageButton) findViewById(R.id.pan_btn);

        spnDevice = (Spinner) findViewById(R.id.device_spn);
        mDevicesAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, this.mBluetoothDevices);
        mDevicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDevice.setAdapter(mDevicesAdapter);

        spnDevice.setOnItemSelectedListener(this);

        // Listener
        btnPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar(Boolean.TRUE);

                getPairedDevices();
                mPanManager.onClick(mPan, new OperationListener<Pan>() {
                    @Override
                    public void onSuccess(Pan result) {
                        showProgressBar(Boolean.FALSE);
                        Log.d(TAG, result.toString());
                        super.onSuccess(result);
                    }

                    @Override
                    public void onError(int error) {
                        showProgressBar(Boolean.FALSE);
                        Log.e(TAG, String.valueOf(error));
                        super.onError(error);
                    }

                    @Override
                    public void onCancel() {
                        showProgressBar(Boolean.FALSE);
                        super.onCancel();
                    }

                    @Override
                    public void onProgressUpdate(int progress) {
                        super.onProgressUpdate(progress);
                    }
                });
            }
        });

        getPairedDevices();
    }

    private void getPairedDevices() {
        this.mBluetoothDevices.clear();
        this.mBluetoothDevices.addAll(mPanManager.getPairedDevices());
        mDevicesAdapter.notifyDataSetChanged();
    }

    private void showProgressBar(Boolean show) {
        if (Boolean.TRUE == show) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult resultCode: " + resultCode);

        if (resultCode != Activity.RESULT_OK) {
            mPanManager.showToast(getString(R.string.disabled_bluetooth_adapter));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemSelected");
        mPan.setAddress(adapterView.getItemAtPosition(i).toString());
        Log.d(TAG, mPan.toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(TAG, "onNothingSelected");
        mPan = mPanManager.getNewPanInstance();
    }
}
