package org.kwansystems.sensinator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SensorLogFragment extends Fragment implements SensorEventListener {
    private SensorManager mSensorManager;
    private TextView txtAcc[]=new TextView[3];
    private TextView txtBfld[]=new TextView[3];
    private TextView txtGyro[]=new TextView[3];
    private TextView txtQuat[]=new TextView[4];
    private TextView txtPres[]=new TextView[1];
    private ToggleButton btnShow;
    Sensor acc,bfld,gyro,quat,pres;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor_log, container, false);
        btnShow=(ToggleButton)rootView.findViewById(R.id.btnShow);
        txtAcc[0]=(TextView)rootView.findViewById(R.id.txtAccX);
        txtAcc[1]=(TextView)rootView.findViewById(R.id.txtAccY);
        txtAcc[2]=(TextView)rootView.findViewById(R.id.txtAccZ);
        txtBfld[0]=(TextView)rootView.findViewById(R.id.txtBfldX);
        txtBfld[1]=(TextView)rootView.findViewById(R.id.txtBfldY);
        txtBfld[2]=(TextView)rootView.findViewById(R.id.txtBfldZ);
        txtGyro[0]=(TextView)rootView.findViewById(R.id.txtGyroX);
        txtGyro[1]=(TextView)rootView.findViewById(R.id.txtGyroY);
        txtGyro[2]=(TextView)rootView.findViewById(R.id.txtGyroZ);
        txtQuat[0]=(TextView)rootView.findViewById(R.id.txtQuatX);
        txtQuat[1]=(TextView)rootView.findViewById(R.id.txtQuatY);
        txtQuat[2]=(TextView)rootView.findViewById(R.id.txtQuatZ);
        txtQuat[3]=(TextView)rootView.findViewById(R.id.txtQuatW);
        txtPres[0]=(TextView)rootView.findViewById(R.id.txtPresX);
        btnShow.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton btnGPS, boolean isChecked) {
                if (isChecked) {
                    mSensorManager.registerListener(SensorLogFragment.this, acc, SensorManager.SENSOR_DELAY_NORMAL);
                    mSensorManager.registerListener(SensorLogFragment.this, bfld, SensorManager.SENSOR_DELAY_NORMAL);
                    mSensorManager.registerListener(SensorLogFragment.this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
                    mSensorManager.registerListener(SensorLogFragment.this, quat, SensorManager.SENSOR_DELAY_NORMAL);
                    mSensorManager.registerListener(SensorLogFragment.this, pres, SensorManager.SENSOR_DELAY_NORMAL);
                } else {
                    mSensorManager.unregisterListener(SensorLogFragment.this);
                }
            }
        });
        mSensorManager = (SensorManager) (getActivity()).getSystemService(Context.SENSOR_SERVICE);
        acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        bfld = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        quat = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        pres = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        return rootView;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onSensorChanged(SensorEvent arg0) {
        TextView[] txtSens=null;
        int l;
        if(arg0.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            txtSens=txtAcc;
        } else if(arg0.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) {
            txtSens=txtBfld;
        } else if(arg0.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            txtSens=txtGyro;
        } else if(arg0.sensor.getType()==Sensor.TYPE_ORIENTATION) {
            txtSens=txtQuat;
        } else if(arg0.sensor.getType()==Sensor.TYPE_PRESSURE) {
            txtSens=txtPres;
        }
        for(int i=0;i<arg0.values.length && i<txtSens.length;i++) txtSens[i].setText(String.format("%f",arg0.values[i]));
    }
}