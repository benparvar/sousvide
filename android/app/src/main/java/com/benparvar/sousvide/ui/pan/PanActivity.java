package com.benparvar.sousvide.ui.pan;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Spinner;

import com.benparvar.sousvide.R;
import com.benparvar.sousvide.business.BluetoothBusiness;
import com.benparvar.sousvide.manager.PanManager;

public class PanActivity extends AppCompatActivity {
    private final String TAG = "PanActivity";
    private AppCompatSeekBar skbTemperature;
    private AppCompatSeekBar skbTimer;
    private AppCompatImageButton btnPan;
    private Spinner spnDevice;
    private PanManager mPanManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan);

        mPanManager = new PanManager(PanActivity.this);

        mPanManager.hasBluetoohAdapter();
        mPanManager.activateBluetooth();

        // Bind
        skbTemperature = (AppCompatSeekBar) findViewById(R.id.temperature_skb);
        skbTimer = (AppCompatSeekBar) findViewById(R.id.timer_skb);
        btnPan = (AppCompatImageButton) findViewById(R.id.pan_btn);
        spnDevice = (Spinner) findViewById(R.id.device_spn);

        // Listener
        btnPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult resultCode: " + resultCode);

        if (resultCode != Activity.RESULT_OK) {
            mPanManager.showToast(getString(R.string.disabled_bluetooth_adapter));
        }
    }
}
