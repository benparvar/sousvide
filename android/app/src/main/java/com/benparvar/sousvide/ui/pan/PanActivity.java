package com.benparvar.sousvide.ui.pan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.DragEvent;
import android.view.View;
import android.widget.Spinner;

import com.benparvar.sousvide.R;

public class PanActivity extends AppCompatActivity {
    private AppCompatSeekBar skbTemperature;
    private AppCompatSeekBar skbTimer;
    private AppCompatImageButton btnPan;
    private Spinner spnDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan);

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
}
