package com.samsung.sdktest;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.sensorextension.Ssensor;
import com.samsung.android.sdk.sensorextension.SsensorEvent;
import com.samsung.android.sdk.sensorextension.SsensorEventListener;
import com.samsung.android.sdk.sensorextension.SsensorExtension;
import com.samsung.android.sdk.sensorextension.SsensorManager;

public class SsensorActivity extends Activity {

    Ssensor ir = null;
    Ssensor red = null;

    //private final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    ToggleButton btn_start = null;
    TextView tIR = null;
    TextView tRED = null;

    SSListener mSSListener = null;

    SsensorManager mSSensorManager = null;
    SsensorExtension mSsensorExtension = null;
    Activity mContext;

    @TargetApi(23) @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = (ToggleButton) findViewById(R.id.btn_start);
        tIR = (TextView) findViewById(R.id.tIR);
        tRED = (TextView) findViewById(R.id.tRED);

        mSSListener = new SSListener();
        if (btn_start != null) {
            btn_start.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    btn_start.setSelected(!btn_start.isSelected());

                    try {
                        if (!btn_start.isSelected()) {
                            // HRM OFF
                            btn_start.setText(btn_start.getTextOff());
                            if (mSSensorManager != null) {
                                if(ir != null)
                                    mSSensorManager.unregisterListener(mSSListener,ir);
                                if(red != null)
                                    mSSensorManager.unregisterListener(mSSListener,red);
                            }
                        } else {
                            mSsensorExtension = new SsensorExtension();
                            try {
                                mSsensorExtension.initialize(mContext);
                                mSSensorManager = new SsensorManager(mContext, mSsensorExtension);
                                ir = mSSensorManager.getDefaultSensor(Ssensor.TYPE_HRM_LED_IR);
                                red = mSSensorManager.getDefaultSensor(Ssensor.TYPE_HRM_LED_RED);
                            } catch (SsdkUnsupportedException e) {
                                Toast.makeText(mContext, e.getMessage(),Toast.LENGTH_LONG).show();
                                mContext.finish();
                            } catch (IllegalArgumentException e) {
                                Toast.makeText(mContext, e.getMessage(),Toast.LENGTH_SHORT).show();
                                mContext.finish();
                            } catch (SecurityException e) {
                                Toast.makeText(mContext, e.getMessage(),Toast.LENGTH_SHORT).show();
                                mContext.finish();
                            }
                            // HRM ON
                            btn_start.setText(btn_start.getTextOn());
                            if (mSSensorManager != null) {
                                if(ir != null)
                                    mSSensorManager.registerListener(mSSListener, ir, SensorManager.SENSOR_DELAY_NORMAL);
                                if(red != null)
                                    mSSensorManager.registerListener(mSSListener, red, SensorManager.SENSOR_DELAY_NORMAL);
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        ErrorToast(e);
                    }
                }
            });
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (shouldShowRequestPermissionRationale(Manifest.permission.BODY_SENSORS)) {
                        // Explain to the user why we need to read the contacts
                    }

                    requestPermissions(new String[] { Manifest.permission.BODY_SENSORS }, 101);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant

                    return;
                }
            }
        }
    }

    public void ErrorToast(IllegalArgumentException e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            if (ir != null) {
                mSSensorManager.unregisterListener(mSSListener, ir);
                tIR.setText("");
            }
            if (red != null) {
                mSSensorManager.unregisterListener(mSSListener, red);
                tRED.setText("");
            }
            btn_start.setSelected(false);
            btn_start.setText(btn_start.getTextOff());
        } catch (IllegalArgumentException e) {
            this.finish();
        } catch (IllegalStateException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    private class SSListener implements SsensorEventListener {
        @Override
        public void OnSensorChanged(SsensorEvent event) {
            // TODO Auto-generated method stub

            Ssensor s = event.sensor;
            StringBuffer sb = new StringBuffer();

            switch (event.sensor.getType())
            {
                case Ssensor.TYPE_HRM_LED_IR:
                    sb.append("==== Sensor Information ====\n")
                            .append("Name : " + s.getName() + "\n")
                            .append("Vendor : " + s.getVendor() + "\n")
                            .append("Type : " + s.getType() + "\n")
                            .append("SDK Version : "
                                    + mSsensorExtension.getVersionName() + "\n")
                            .append("MaxRange : " + s.getMaxRange() + "\n")
                            .append("MinDelay : " + s.getMinDelay() + "\n")
                            .append("Resolution : " + s.getResolution() + "\n")
                            .append("FifoMaxEventCount : " + s.getFifoMaxEventCount()
                                    + "\n").append("Power : " + s.getPower() + "\n")
                            .append("----------------------------\n")
                            .append("Accuracy : " + event.accuracy + "\n")
                            .append("IR RAW DATA : " + event.values[0] + "\n");

                    tIR.setText(sb.toString());
                    break;
                case Ssensor.TYPE_HRM_LED_RED:
                    sb.append("==== Sensor Information ====\n")
                            .append("Name : " + s.getName() + "\n")
                            .append("Vendor : " + s.getVendor() + "\n")
                            .append("Type : " + s.getType() + "\n")
                            .append("SDK Version : "
                                    + mSsensorExtension.getVersionName() + "\n")
                            .append("MaxRange : " + s.getMaxRange() + "\n")
                            .append("MinDelay : " + s.getMinDelay() + "\n")
                            .append("Resolution : " + s.getResolution() + "\n")
                            .append("FifoMaxEventCount : " + s.getFifoMaxEventCount()
                                    + "\n")
                            .append("Power : " + s.getPower() + "\n")
                            .append("----------------------------\n")
                            .append("Accuracy : " + event.accuracy + "\n")
                            .append("RED LED RAW DATA : " + event.values[0] + "\n");
                    tRED.setText(sb.toString());
                break;
            }
        }

        @Override
        public void OnAccuracyChanged(Ssensor ssensor, int i) {

        }
    }
}