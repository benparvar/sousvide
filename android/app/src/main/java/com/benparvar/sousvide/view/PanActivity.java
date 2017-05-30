package com.benparvar.sousvide.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.benparvar.sousvide.R;
import com.benparvar.sousvide.model.InputTO;
import com.benparvar.sousvide.presenter.PanPresenter;
import com.crashlytics.android.Crashlytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

import static com.benparvar.sousvide.infrastructure.Contants.TimTemperatureer.MAX_TARGET_TEMPERATURE;
import static com.benparvar.sousvide.infrastructure.Contants.TimTemperatureer.MIN_TARGET_TEMPERATURE;
import static com.benparvar.sousvide.infrastructure.Contants.Timer.MAX_TARGET_TIMER_IN_SECONDS;
import static com.benparvar.sousvide.infrastructure.Contants.Timer.MIN_TARGET_TIMER_IN_SECONDS;

public class PanActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final String TAG = "PanActivity";

    @BindView(R.id.current_temperature_tvw)
    TextView tvwCurrentTemperature;

    @BindView(R.id.target_temperature_tvw)
    TextView tvwTargetTemperature;

    @BindView(R.id.current_timer_tvw)
    TextView tvwCurrentTimer;

    @BindView(R.id.target_timer_tvw)
    TextView tvwTargetTimer;

    @BindView(R.id.device_spn)
    Spinner spnDevice;

    @BindView(R.id.loading_pgb)
    ProgressBar mProgressBar;

    @BindView(R.id.pan_btn)
    ImageButton mButtonPan;

    private PanPresenter mPanPresenter;

    //
    private String deviceAddress;
    private Double targetTemperature = 0.0;
    private Long targetTimer = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_pan);
        ButterKnife.bind(this);

        mPanPresenter = new PanPresenter(this);

        this.updateBtnPanStatus(R.color.panDisconnected, Boolean.TRUE);

        mPanPresenter.configureBluetooh();
        mPanPresenter.configureSpinnerDevices();
    }

    @OnClick(R.id.pan_btn)
    void onClickBtnPan(View view) {
        showProgressBar(Boolean.TRUE);

        InputTO mInputTO = new InputTO(deviceAddress,
                Double.valueOf(this.tvwTargetTemperature.getText().toString()),
                mPanPresenter.stringHourToSecond(this.tvwTargetTimer.getText().toString()));

        mPanPresenter.onClickBtnPan(mInputTO);

        showProgressBar(Boolean.FALSE);
    }

    @OnClick(R.id.temperature_img)
    void onClickTargetTemperature(View view) {
        setTargetTemperature();
    }

    @OnClick(R.id.timer_img)
    void onClickTargetTimer(View view) {
        setTargetTimer();
    }

    private void setTargetTemperature() {
        final EditText inputText = new EditText(this);
        inputText.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.set_temperature_target)
                .setTitle(R.string.temperature)
                .setView(inputText)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Double doubleValue = 0.0;
                        try {
                            doubleValue = Double.valueOf(inputText.getText().toString());
                        } catch (NumberFormatException e) {
                            Log.e(TAG, e.getMessage());
                        }
  
                        if (doubleValue > MAX_TARGET_TEMPERATURE ||
                                doubleValue < MIN_TARGET_TEMPERATURE) {

                            Toast.makeText(getApplicationContext(), "Invalid temperature",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            targetTemperature = doubleValue;
                            mPanPresenter.setTargetTemperature(targetTemperature);
                            tvwTargetTemperature.setText(mPanPresenter.
                                    doubleToTemperature(targetTemperature));
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setTargetTimer() {
        final EditText inputText = new EditText(this);
        inputText.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.set_timer_target)
                .setTitle(R.string.timer)
                .setView(inputText)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Long longValue = 0L;
                        try {
                            longValue = mPanPresenter.minuteToSecond(Long.valueOf(inputText.
                                    getText().toString()));
                        } catch (NumberFormatException e) {
                            Log.e(TAG, e.getMessage());
                        }


                        if (longValue > MAX_TARGET_TIMER_IN_SECONDS ||
                                longValue < MIN_TARGET_TIMER_IN_SECONDS) {
                            Toast.makeText(getApplicationContext(), "Invalid timer",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            targetTimer = longValue;
                            mPanPresenter.setTargetTimer(targetTimer);
                            tvwTargetTimer.setText(mPanPresenter.
                                    secondToStringHour(targetTimer));
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setSpnDeviceAdapter(SpinnerAdapter adapter) {
        spnDevice.setAdapter(adapter);
        spnDevice.setOnItemSelectedListener(this);
    }

    public void showProgressBar(Boolean show) {
        if (Boolean.TRUE == show) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemSelected");
        deviceAddress = adapterView.getItemAtPosition(i).toString();
        Log.d(TAG, "deviceAddress: " + deviceAddress);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(TAG, "onNothingSelected");
        deviceAddress = "";
        Log.d(TAG, "deviceAddress: " + deviceAddress);
    }

    public void setCurrentTimer(Long timer) {
        tvwCurrentTimer.setText(mPanPresenter.secondToStringHour(timer));
    }

    public void setCurrentTemperature(Double temperature) {
        tvwCurrentTemperature.setText(mPanPresenter.doubleToTemperature(temperature));
    }

    public void setTargetTemperature(Double temperature) {
        Log.d(TAG, "setTargetTemperature: " + temperature);
        tvwTargetTemperature.setText(mPanPresenter.doubleToTemperature(temperature));
    }

    public void updateBtnPanStatus(int color, Boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mButtonPan.setBackgroundColor(getResources().getColor(color, this.getTheme()));
        } else {
            mButtonPan.setBackgroundColor(getResources().getColor(color));
        }
        mButtonPan.setEnabled(enabled);
    }

    public void setTargetTimer(Long timer) {
        Log.d(TAG, "setTargetTimer: " + timer);
        tvwTargetTimer.setText(mPanPresenter.secondToStringHour(timer));
    }
}
